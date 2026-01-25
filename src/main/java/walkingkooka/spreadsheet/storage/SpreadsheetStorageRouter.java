/*
 * Copyright 2025 Miroslav Pokorny (github.com/mP1)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package walkingkooka.spreadsheet.storage;

import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContextFactory;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.storage.Storage;
import walkingkooka.storage.StorageName;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;
import walkingkooka.storage.StorageValueInfo;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * A {@link Storage} that routes requests based on the following {@link StoragePath}.
 * <pre>
 * /spreadsheet/{@link SpreadsheetId}
 * /spreadsheet/1 // metadata
 *
 * /cell/SpreadsheetExpressionReference // uses {@link EnvironmentValueName}
 * /cell/A1
 *
 * /spreadsheet/{@link SpreadsheetId}/cell/SpreadsheetExpressionReference
 * /spreadsheet/1/cell/A1
 *
 * /label/{@link SpreadsheetLabelName}
 * /label/Label123
 *
 * /spreadsheet/{@link SpreadsheetId}/label/{@link SpreadsheetLabelName}
 * /spreadsheet/1/label/Label123
 * </pre>
 * This {@link Storage} is intended to be used within a terminal session, where the user is reading/writing files
 * some with and some without a spreadsheet-id within the path. Those without the {@link SpreadsheetEnvironmentContextFactory#SPREADSHEET_ID},
 * will use the {@link EnvironmentValueName} when forming the final full path.
 */
final class SpreadsheetStorageRouter extends SpreadsheetStorage {

    static SpreadsheetStorageRouter with(final Storage<SpreadsheetStorageContext> cells,
                                         final Storage<SpreadsheetStorageContext> labels,
                                         final Storage<SpreadsheetStorageContext> metadatas,
                                         final Storage<SpreadsheetStorageContext> other) {
        return new SpreadsheetStorageRouter(
            Objects.requireNonNull(cells, "cells"),
            Objects.requireNonNull(labels, "labels"),
            Objects.requireNonNull(metadatas, "metadatas"),
            Objects.requireNonNull(other, "other")
        );
    }

    private SpreadsheetStorageRouter(final Storage<SpreadsheetStorageContext> cells,
                                     final Storage<SpreadsheetStorageContext> labels,
                                     final Storage<SpreadsheetStorageContext> metadatas,
                                     final Storage<SpreadsheetStorageContext> other) {
        super();

        this.cells = cells.setPrefix(CELL);
        this.labels = labels.setPrefix(LABEL);
        this.metadatas = metadatas.setPrefix(SPREADSHEET);

        this.other = other;
    }

    // SpreadsheetStorage.......................................................................................

    @Override
    Optional<StorageValue> loadNonNull(final StoragePath path,
                                       final SpreadsheetStorageContext context) {
        return this.route(
            path,
            context,
            (s, c) -> s.load(
                path,
                c
            )
        );
    }

    @Override
    StorageValue saveNonNull(final StorageValue value,
                             final SpreadsheetStorageContext context) {
        return this.route(
            value.path(),
            context,
            (s, c) -> s.save(
                value,
                c
            )
        );
    }

    @Override
    void deleteNonNull(final StoragePath path,
                       final SpreadsheetStorageContext context) {
        this.route(
            path,
            context,
            (s, c) -> {
                s.delete(
                    path,
                    c
                );
                return null;
            }
        );
    }

    @Override
    List<StorageValueInfo> listNonNull(final StoragePath path,
                                       final int offset,
                                       final int count,
                                       final SpreadsheetStorageContext context) {
        return this.route(
            path,
            context,
            (s, c) -> s.list(
                path,
                offset,
                count,
                c
            )
        );
    }

    /**
     * If the path is a /cell or /column or /row then add the {@link SpreadsheetEnvironmentContextFactory#SPREADSHEET_ID}.
     */
    private <T> T route(final StoragePath path,
                        final SpreadsheetStorageContext context,
                        final BiFunction<Storage<SpreadsheetStorageContext>, SpreadsheetStorageContext, T> execute) {
        Storage<SpreadsheetStorageContext> storage;
        final SpreadsheetStorageContext executeContext;

        final List<StorageName> names = path.namesList();
        final int nameCount = names.size();

        switch (nameCount) {
            case 0:
            case 1:
                storage = this.other;
                executeContext = context;
                break;
            default:
                // /spreadsheet/1
                // /spreadsheet/1/cell/A1
                // /spreadsheet/1/label/Label123
                // /cell/A1
                // /label/Label123
                final StorageName storageName1 = names.get(1);
                switch (storageName1.value()) {
                    case SPREADSHEET_STRING:
                        switch (nameCount) {
                            case 2:
                            case 3:
                                // /spreadsheet/1
                                storage = this.metadatas;
                                executeContext = context;
                                break;
                            default:
                                // /spreadsheet/1/cell
                                // /spreadsheet/1/label

                                switch (names.get(3).value()) {
                                    case CELL_STRING:
                                        storage = this.cells;
                                        break;
                                    case LABEL_STRING:
                                        storage = this.labels;
                                        break;
                                    default:
                                        storage = this.other;
                                        break;
                                }

                                final StorageName spreadsheetIdStorageName = names.get(2);

                                storage = storage.setPrefix(
                                    StoragePath.ROOT.append(storageName1)
                                        .append(
                                            spreadsheetIdStorageName
                                        )
                                );

                                // clone saves "restoring" original SpreadsheetId
                                executeContext = context.cloneEnvironment();
                                executeContext.setSpreadsheetId(
                                    Optional.of(
                                        SpreadsheetId.parse(
                                            spreadsheetIdStorageName.value()
                                        )
                                    )
                                );

                                break;
                        }
                        break;
                    case CELL_STRING:
                        storage = this.cells;
                        executeContext = context;
                        break;
                    case LABEL_STRING:
                        storage = this.labels;
                        executeContext = context;
                        break;
                    default:
                        storage = this.other;
                        executeContext = context;
                        break;
                }

                break;
        }

        return execute.apply(
            storage,
            executeContext
        );
    }

    private final static String SPREADSHEET_STRING = "spreadsheet";

    private final static StoragePath SPREADSHEET = StoragePath.ROOT.append(
        StorageName.with(SPREADSHEET_STRING)
    );

    private final static String CELL_STRING = "cell";

    private final static StoragePath CELL = StoragePath.ROOT.append(
        StorageName.with(CELL_STRING)
    );

    private final static String LABEL_STRING = "label";

    private final static StoragePath LABEL = StoragePath.ROOT.append(
        StorageName.with(LABEL_STRING)
    );

    private final Storage<SpreadsheetStorageContext> cells;

    private final Storage<SpreadsheetStorageContext> labels;

    private final Storage<SpreadsheetStorageContext> metadatas;

    /**
     * This storage will provide storage for paths that dont match the cells, labels or metadata.
     */
    private final Storage<SpreadsheetStorageContext> other;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.cells + ", " + this.labels + ", " + this.metadatas + ", /* " + this.other;
    }
}

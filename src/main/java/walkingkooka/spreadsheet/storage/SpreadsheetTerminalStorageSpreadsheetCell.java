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

import walkingkooka.collect.list.ImmutableList;
import walkingkooka.collect.set.Sets;
import walkingkooka.net.header.MediaType;
import walkingkooka.spreadsheet.engine.SpreadsheetDeltaProperties;
import walkingkooka.spreadsheet.engine.collection.SpreadsheetCellSet;
import walkingkooka.spreadsheet.net.SpreadsheetMediaTypes;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.storage.Storage;
import walkingkooka.storage.StorageName;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValue;
import walkingkooka.storage.StorageValueInfo;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * A {@link Storage} that maps cells to a {@link Storage}, for the current spreadsheet.
 * <pre>
 * /cell/SpreadsheetExpressionReference
 * </pre>
 * for the {@link StorageValue}.
 */
final class SpreadsheetTerminalStorageSpreadsheetCell extends SpreadsheetTerminalStorage {

    /**
     * Singleton
     */
    final static SpreadsheetTerminalStorageSpreadsheetCell INSTANCE = new SpreadsheetTerminalStorageSpreadsheetCell();

    private final static MediaType MEDIA_TYPE = SpreadsheetMediaTypes.MEMORY_CELL;

    private SpreadsheetTerminalStorageSpreadsheetCell() {
        super();
    }

    @Override
    Optional<StorageValue> loadNonNull(final StoragePath path,
                                       final SpreadsheetStorageContext context) {
        StorageValue value = null;

        final List<StorageName> names = path.namesList();

        final SpreadsheetExpressionReference cellOrLabels;

        // SLASH A1 compute-if-necessary
        switch (names.size()) {
            case 2:
            case 3:
                cellOrLabels = parseExpressionReference(
                    names.get(1)
                );
                break;
            default:
                cellOrLabels = null;
                break;
        }

        if (null != cellOrLabels) {
            final Set<SpreadsheetCell> cells = context.loadCells(cellOrLabels);
            if (false == cells.isEmpty()) {
                value = StorageValue.with(
                    path,
                    Optional.of(cells)
                ).setContentType(MEDIA_TYPE);
            }
        }

        return Optional.ofNullable(value);
    }

    /**
     * Select only cells to appear in the response.
     */
    private final static Set<SpreadsheetDeltaProperties> CELLS_ONLY = Sets.of(SpreadsheetDeltaProperties.CELLS);

    @Override
    StorageValue saveNonNull(final StorageValue value,
                             final SpreadsheetStorageContext context) {
        switch (value.path()
            .namesList()
            .size()) {
            case 0:
            case 1:
                break;
            default:
                throw new IllegalArgumentException("Invalid path, must not contain selection");
        }

        final SpreadsheetCellSet cells = context.convertOrFail(
            value.value()
                .orElse(SpreadsheetCellSet.EMPTY),
            SpreadsheetCellSet.class
        );

        return value.setValue(
            Optional.of(
                context.saveCells(cells)
            )
        ).setContentType(MEDIA_TYPE);
    }

    /**
     * Deletes the given cells. Note if the path contains additional components a {@link IllegalArgumentException}
     * will be thrown.
     */
    @Override
    void deleteNonNull(final StoragePath path,
                       final SpreadsheetStorageContext context) {
        final List<StorageName> names = path.namesList();
        switch (names.size()) {
            case 0:
            case 1:
                throw new IllegalArgumentException("Missing selection");
            case 2:
                context.deleteCells(
                    parseExpressionReference(
                        names.get(1)
                    )
                );
                break;
            default:
                throw new IllegalArgumentException("Invalid path");
        }
    }

    @Override
    List<StorageValueInfo> listNonNull(final StoragePath path,
                                       final int offset,
                                       final int count,
                                       final SpreadsheetStorageContext context) {
        final List<StorageName> names = path.namesList();

        final SpreadsheetExpressionReference cellOrLabels;

        switch (names.size()) {
            case 0:
            case 1:
                cellOrLabels = SpreadsheetSelection.ALL_CELLS;
                break;
            case 2:
                cellOrLabels = parseExpressionReference(
                    names.get(1)
                );
                break;
            default:
                throw new IllegalArgumentException("Invalid path after selection");
        }

        return context.loadCells(cellOrLabels)
            .stream()
            .map(
                (SpreadsheetCell c) -> StorageValueInfo.with(
                    StoragePath.ROOT.append(
                        StorageName.with(c.reference().text())
                    ),
                    context.createdAuditInfo()
                )
            ).collect(ImmutableList.collector());
    }

    private static SpreadsheetExpressionReference parseExpressionReference(final StorageName name) {
        return SpreadsheetSelection.parseExpressionReference(
            name.value()
        );
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return SpreadsheetCellStore.class.getSimpleName();
    }
}

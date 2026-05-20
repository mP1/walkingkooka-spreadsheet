/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
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

import walkingkooka.collect.list.Lists;
import walkingkooka.storage.Storage;
import walkingkooka.storage.StorageContext;
import walkingkooka.storage.StorageDelegator;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.StorageValueInfo;

import java.util.List;

/**
 * A {@link Storage} that decorates {@link Storage#list(StoragePath, int, int, StorageContext)}, inserting entries
 * for the mounted paths.
 */
final class SpreadsheetStorageRouterRootStorage implements Storage<SpreadsheetStorageContext>,
    StorageDelegator<SpreadsheetStorageContext> {

    static SpreadsheetStorageRouterRootStorage with(final Storage<SpreadsheetStorageContext> storage,
                                                    final SpreadsheetStorageRouter router) {
        return new SpreadsheetStorageRouterRootStorage(
            storage,
            router
        );
    }

    private SpreadsheetStorageRouterRootStorage(final Storage<SpreadsheetStorageContext> storage,
                                                final SpreadsheetStorageRouter router) {
        super();

        this.storage = storage;

        this.router = router;
    }

    @Override
    public List<StorageValueInfo> list(final StoragePath parent,
                                       final int offset,
                                       final int count,
                                       final SpreadsheetStorageContext context) {
        final Storage<SpreadsheetStorageContext> storage = this.storage;
        List<StorageValueInfo> list;

        if (StoragePath.ROOT.equals(parent)) {
            list = Lists.array();

            Loop: //
            while (list.size() < count) {
                switch (list.size() + offset) {
                    // /cells
                    case 0:
                        list.add(
                            this.addMountEntry(
                                SpreadsheetStorageRouter.CELL,
                                context
                            )
                        );
                        break;
                    // /environment
                    case 1:
                        list.add(
                            this.addMountEntry(
                                SpreadsheetStorageRouter.ENVIRONMENT,
                                context
                            )
                        );
                        break;
                    // /forms
                    case 2:
                        list.add(
                            this.addMountEntry(
                                SpreadsheetStorageRouter.FORM,
                                context
                            )
                        );
                        break;
                    // /labels
                    case 3:
                        list.add(
                            this.addMountEntry(
                                SpreadsheetStorageRouter.LABEL,
                                context
                            )
                        );
                        break;
                    // /spreadsheet
                    case 4:
                        list.add(
                            this.addMountEntry(
                                SpreadsheetStorageRouter.SPREADSHEET,
                                context
                            )
                        );
                        break;
                    default:
                        final int storageOffset = offset + list.size() - 5;
                        if(storageOffset >= 0) {
                            list.addAll(
                                storage.list(
                                    parent,
                                    storageOffset,
                                    count - list.size(),
                                    context
                                )
                            );
                        }
                        break Loop;
                }
            }

            return Lists.immutable(list);
        } else {
            list = storage.list(
                parent,
                offset,
                count,
                context
            );
        }

        return list;
    }

    private final SpreadsheetStorageRouter router;

    private StorageValueInfo addMountEntry(final StoragePath path,
                                           final SpreadsheetStorageContext context) {
        return StorageValueInfo.with(
            path,
            context.createdAuditInfo()
        );
    }

//    private StorageValueInfo addMount(final Storage<SpreadsheetStorageContext> mount,
//                                      final StoragePath path,
//                                      final SpreadsheetStorageContext context) {
//        final List<StorageValueInfo> infos = mount.list(
//            path,
//            0,
//            1,
//            context
//        );
//
//        StorageValueInfo info;
//
//        if (infos.size() == 1) {
//            info = infos.get(0)
//                .setPath(path);
//        } else {
//            info = StorageValueInfo.with(
//                path,
//                context.createdAuditInfo()
//            );
//        }
//
//        return info;
//    }

    // StorageDelegator.................................................................................................

    @Override
    public Storage<SpreadsheetStorageContext> storage() {
        return this.storage;
    }

    private final Storage<SpreadsheetStorageContext> storage;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.storage.toString();
    }
}

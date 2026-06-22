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

package walkingkooka.spreadsheet.meta;

import walkingkooka.Context;
import walkingkooka.store.StoreWatcher;

import java.util.List;

/**
 * A context that provides CRUD operations for {@link SpreadsheetMetadata}.
 */
public interface SpreadsheetMetadataContext extends Context,
    SpreadsheetMetadataCreator,
    SpreadsheetMetadataLoader {

    /**
     * Updates the given {@link SpreadsheetMetadata}
     */
    SpreadsheetMetadata saveMetadata(final SpreadsheetMetadata metadata);

    /**
     * Deletes an existing {@link SpreadsheetMetadata}
     */
    void deleteMetadata(final SpreadsheetId id);

    /**
     * Finds all {@link SpreadsheetMetadata} with names that match name.
     */
    List<SpreadsheetMetadata> findMetadataBySpreadsheetName(final String name,
                                                            final int offset,
                                                            final int count);

    Runnable addMetadataWatcher(final StoreWatcher<SpreadsheetMetadata> watcher);

    Runnable addMetadataWatcherOnce(final StoreWatcher<SpreadsheetMetadata> watcher);
}

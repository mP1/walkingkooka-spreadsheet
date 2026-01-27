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
import walkingkooka.net.email.EmailAddress;
import walkingkooka.store.MissingStoreException;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * A context that provides CRUD operations for {@link SpreadsheetMetadata}.
 */
public interface SpreadsheetMetadataContext extends Context {

    /**
     * Creates a new {@link SpreadsheetMetadata}
     */
    SpreadsheetMetadata createMetadata(final EmailAddress user,
                                       final Optional<Locale> locale);

    /**
     * Loads the {@link SpreadsheetMetadata} for the given {@link SpreadsheetId}
     */
    Optional<SpreadsheetMetadata> loadMetadata(final SpreadsheetId id);

    /**
     * Attempts to load the {@link SpreadsheetMetadata} with the given {@link SpreadsheetId}, throwing
     * {@link MissingSpreadsheetException} which is a sub-class of {@link MissingStoreException} if not found.
     */
    default SpreadsheetMetadata loadMetadataOrFail(final SpreadsheetId id) {
        return this.loadMetadata(id)
            .orElseThrow(id::missingSpreadsheetException);
    }

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
}

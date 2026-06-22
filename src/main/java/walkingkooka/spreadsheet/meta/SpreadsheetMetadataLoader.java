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

import walkingkooka.store.MissingStoreException;

import java.util.Optional;

/**
 * Provides functionality to load a {@link SpreadsheetMetadata} given a {@link SpreadsheetId}.
 */
public interface SpreadsheetMetadataLoader {

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
}

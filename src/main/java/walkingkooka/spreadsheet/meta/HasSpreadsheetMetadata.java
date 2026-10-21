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

import java.util.Optional;

/**
 * Interface that provides a getter that returns a {@link SpreadsheetMetadata}
 */
public interface HasSpreadsheetMetadata {

    Optional<SpreadsheetMetadata> NO_METADATA = Optional.empty();

    /**
     * Gets the {@link SpreadsheetMetadata}
     */
    Optional<SpreadsheetMetadata> spreadsheetMetadata();


    default SpreadsheetMetadata spreadsheetMetadataOrFail() {
        return this.spreadsheetMetadata()
            .orElseThrow(() -> new IllegalStateException("Missing spreadsheet metadata"));
    }

    /**
     * Saves or replaces the current {@link SpreadsheetMetadata} with a new value which may or may not have a different
     * {@link SpreadsheetId}.
     */
    void setSpreadsheetMetadata(final Optional<SpreadsheetMetadata> metadata);
}

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

import java.util.Objects;
import java.util.Optional;

/**
 * A {@link HasSpreadsheetMetadata} that is read only.
 */
final class HasSpreadsheetMetadataReadOnly implements HasSpreadsheetMetadata {

    static HasSpreadsheetMetadataReadOnly with(final HasSpreadsheetMetadata hasSpreadsheetMetadata) {
        return new HasSpreadsheetMetadataReadOnly(
            Objects.requireNonNull(hasSpreadsheetMetadata, "hasSpreadsheetMetadata")
        );
    }

    private HasSpreadsheetMetadataReadOnly(final HasSpreadsheetMetadata hasSpreadsheetMetadata) {
        super();

        this.hasSpreadsheetMetadata = hasSpreadsheetMetadata;
    }

    @Override
    public Optional<SpreadsheetMetadata> spreadsheetMetadata() {
        return this.hasSpreadsheetMetadata.spreadsheetMetadata();
    }

    private final HasSpreadsheetMetadata hasSpreadsheetMetadata;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.hasSpreadsheetMetadata.toString();
    }
}

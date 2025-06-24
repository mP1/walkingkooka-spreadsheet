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


import walkingkooka.locale.LocaleContext;
import walkingkooka.spreadsheet.SpreadsheetName;

import java.util.Optional;

final class SpreadsheetMetadataPropertyNameSpreadsheetName extends SpreadsheetMetadataPropertyName<SpreadsheetName> {

    /**
     * Singleton
     */
    static SpreadsheetMetadataPropertyNameSpreadsheetName instance() {
        return new SpreadsheetMetadataPropertyNameSpreadsheetName();
    }

    /**
     * Private constructor use singleton.
     */
    private SpreadsheetMetadataPropertyNameSpreadsheetName() {
        super("spreadsheetName");
    }

    @Override
    SpreadsheetName checkValueNonNull(final Object value) {
        return this.checkValueType(value,
                v -> v instanceof SpreadsheetName);
    }

    @Override
    String expected() {
        return SpreadsheetName.class.getSimpleName();
    }

    @Override
    void accept(final SpreadsheetName value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitSpreadsheetName(value);
    }

    @Override
    Optional<SpreadsheetName> extractLocaleAwareValue(final LocaleContext context) {
        return Optional.empty();
    }

    @Override
    public Class<SpreadsheetName> type() {
        return SpreadsheetName.class;
    }

    // parseUrlFragmentSaveValue........................................................................................

    @Override
    SpreadsheetName parseUrlFragmentSaveValueNonNull(final String value) {
        return SpreadsheetName.with(value);
    }
}

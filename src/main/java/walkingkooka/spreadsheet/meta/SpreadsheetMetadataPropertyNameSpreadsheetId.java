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


import walkingkooka.spreadsheet.SpreadsheetId;

import java.util.Locale;
import java.util.Optional;

final class SpreadsheetMetadataPropertyNameSpreadsheetId extends SpreadsheetMetadataPropertyName<SpreadsheetId> {

    /**
     * Singleton
     */
    static SpreadsheetMetadataPropertyNameSpreadsheetId instance() {
        return new SpreadsheetMetadataPropertyNameSpreadsheetId();
    }

    /**
     * Private constructor use singleton.
     */
    private SpreadsheetMetadataPropertyNameSpreadsheetId() {
        super("spreadsheet-id");
    }

    @Override
    SpreadsheetId checkValue0(final Object value) {
        return this.checkValueType(value,
                v -> v instanceof SpreadsheetId);
    }

    @Override
    String expected() {
        return SpreadsheetId.class.getSimpleName();
    }

    @Override
    void accept(final SpreadsheetId value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitSpreadsheetId(value);
    }

    @Override
    Optional<SpreadsheetId> extractLocaleAwareValue(final Locale locale) {
        return Optional.empty();
    }

    @Override
    Class<SpreadsheetId> type() {
        return SpreadsheetId.class;
    }

    @Override
    String compareToName() {
        return ""; // ensure id always appears first.
    }

    // parseUrlFragmentSaveValue........................................................................................

    @Override
    public boolean isParseUrlFragmentSaveValueSupported() {
        return false;
    }

    @Override
    public SpreadsheetId parseUrlFragmentSaveValue0(final String value) {
        return this.failParseUrlFragmentSaveValueUnsupported();
    }
}

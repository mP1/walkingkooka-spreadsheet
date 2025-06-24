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
import walkingkooka.spreadsheet.SpreadsheetId;

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
        super("spreadsheetId");
    }

    @Override
    SpreadsheetId checkValueNonNull(final Object value) {
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
    Optional<SpreadsheetId> extractLocaleAwareValue(final LocaleContext context) {
        return Optional.empty();
    }

    @Override
    public Class<SpreadsheetId> type() {
        return SpreadsheetId.class;
    }

    // parseUrlFragmentSaveValue........................................................................................

    @Override
    SpreadsheetId parseUrlFragmentSaveValueNonNull(final String value) {
        return this.failParseUrlFragmentSaveValueUnsupported();
    }
}

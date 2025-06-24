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

import java.util.Optional;

final class SpreadsheetMetadataPropertyNameDateTimeOffset extends SpreadsheetMetadataPropertyName<Long> {

    /**
     * Singleton
     */
    static SpreadsheetMetadataPropertyNameDateTimeOffset instance() {
        return new SpreadsheetMetadataPropertyNameDateTimeOffset();
    }

    /**
     * Private constructor use singleton.
     */
    private SpreadsheetMetadataPropertyNameDateTimeOffset() {
        super();
    }

    @Override
    Long checkValueNonNull(final Object value) {
        return this.checkValueType(value,
                v -> v instanceof Long);
    }

    @Override
    String expected() {
        return "DateTime offset";
    }

    @Override
    void accept(final Long value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitDateTimeOffset(value);
    }

    @Override
    Optional<Long> extractLocaleAwareValue(final LocaleContext context) {
        return Optional.empty(); // Unrelated to Locales
    }

    @Override
    public Class<Long> type() {
        return Long.class;
    }

    // parseUrlFragmentSaveValue........................................................................................

    @Override
    Long parseUrlFragmentSaveValueNonNull(final String value) {
        return Long.parseLong(value);
    }
}

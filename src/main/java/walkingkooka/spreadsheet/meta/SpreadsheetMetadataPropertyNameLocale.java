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

import java.util.Locale;
import java.util.Optional;

final class SpreadsheetMetadataPropertyNameLocale extends SpreadsheetMetadataPropertyName<Locale> {

    /**
     * Singleton
     */
    static SpreadsheetMetadataPropertyNameLocale instance() {
        return new SpreadsheetMetadataPropertyNameLocale();
    }

    /**
     * Private constructor use singleton.
     */
    private SpreadsheetMetadataPropertyNameLocale() {
        super();
    }

    @Override
    Locale checkValue0(final Object value) {
        return this.checkValueType(value,
                v -> v instanceof Locale);
    }

    @Override
    String expected() {
        return "Locale";
    }

    @Override
    void accept(final Locale value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitLocale(value);
    }

    @Override
    Optional<Locale> extractLocaleAwareValue(final Locale locale) {
        return Optional.empty();
    }

    @Override
    Class<Locale> type() {
        return Locale.class;
    }

    @Override
    String compareToName() {
        return this.value();
    }

    // parseUrlFragmentSaveValue........................................................................................

    @Override
    public Locale parseUrlFragmentSaveValue0(final String value) {
        return Locale.forLanguageTag(value);
    }
}

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

import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Optional;

abstract class SpreadsheetMetadataPropertyNameString extends SpreadsheetMetadataPropertyName<String> {

    /**
     * Package private to limit sub classing.
     */
    SpreadsheetMetadataPropertyNameString(final String name) {
        super(name);
    }

    @Override
    final String checkValue0(final Object value) {
        final String stringValue = this.checkValueType(value,
                v -> v instanceof String);
        if (stringValue.isEmpty()) {
            throw new SpreadsheetMetadataPropertyValueException("Invalid value", this, stringValue);
        }
        return stringValue;
    }

    @Override
    final String expected() {
        return String.class.getSimpleName();
    }

    @Override
    final Class<String> type() {
        return String.class;
    }

    @Override
    final Optional<String> extractLocaleAwareValue(final Locale locale) {
        return Optional.of(this.extractLocaleValueString(DecimalFormatSymbols.getInstance(locale)));
    }

    abstract String extractLocaleValueString(final DecimalFormatSymbols symbols);

    @Override
    final String compareToName() {
        return this.value();
    }

    // parseUrlFragmentSaveValue........................................................................................

    @Override
    public final boolean isParseUrlFragmentSaveValueSupported() {
        return true;
    }

    @Override
    public final String parseUrlFragmentSaveValue0(final String value) {
        return value;
    }
}

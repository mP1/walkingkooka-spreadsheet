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

abstract class SpreadsheetMetadataPropertyNameInteger extends SpreadsheetMetadataPropertyName<Integer> {

    /**
     * Package private to limit subclassing.
     */
    SpreadsheetMetadataPropertyNameInteger(final String name) {
        super(name);
    }

    final Integer checkValueTypeInteger(final Object value) {
        return this.checkValueType(value, v -> v instanceof Integer);
    }

    @Override final String expected() {
        return "int";
    }

    @Override final Optional<Integer> extractLocaleAwareValue(final LocaleContext context) {
        return Optional.empty(); // Dont think width/precison/twoYearDigit are locale aware.
    }

    @Override
    public final Class<Integer> type() {
        return Integer.class;
    }

    // parseUrlFragmentSaveValue........................................................................................

    @Override final Integer parseUrlFragmentSaveValueNonNull(final String value) {
        return Integer.parseInt(value);
    }
}

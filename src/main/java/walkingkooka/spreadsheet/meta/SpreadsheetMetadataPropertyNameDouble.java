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

abstract class SpreadsheetMetadataPropertyNameDouble extends SpreadsheetMetadataPropertyName<Double> {

    /**
     * Package private to limit sub classing.
     */
    SpreadsheetMetadataPropertyNameDouble() {
        super();
    }

    @Override
    final String expected() {
        return "double";
    }

    @Override
    final Optional<Double> extractLocaleValue(final Locale locale) {
        return Optional.empty();
    }

    @Override
    final Class<Double> type() {
        return Double.class;
    }

    @Override
    String compareToName() {
        return this.value();
    }

    // parseUrlFragmentSaveValue........................................................................................

    @Override
    public final boolean isParseUrlFragmentSaveValueSupported() {
        return true;
    }

    @Override
    public final Double parseUrlFragmentSaveValue0(final String value) {
        return Double.parseDouble(value);
    }
}

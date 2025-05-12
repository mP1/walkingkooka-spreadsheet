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

import walkingkooka.math.DecimalNumberSymbols;

import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Optional;

final class SpreadsheetMetadataPropertyNameDecimalNumberSymbols extends SpreadsheetMetadataPropertyName<DecimalNumberSymbols> {

    /**
     * Singleton
     */
    static SpreadsheetMetadataPropertyNameDecimalNumberSymbols instance() {
        return new SpreadsheetMetadataPropertyNameDecimalNumberSymbols();
    }

    /**
     * Private constructor use singleton.
     */
    private SpreadsheetMetadataPropertyNameDecimalNumberSymbols() {
        super();
    }

    @Override
    DecimalNumberSymbols checkValueNonNull(final Object value) {
        return this.checkValueType(value,
                v -> v instanceof DecimalNumberSymbols);
    }

    @Override
    String expected() {
        return DecimalNumberSymbols.class.getSimpleName();
    }

    @Override
    void accept(final DecimalNumberSymbols value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitDecimalNumberSymbols(value);
    }

    @Override
    Optional<DecimalNumberSymbols> extractLocaleAwareValue(final Locale locale) {
        return Optional.of(
                DecimalNumberSymbols.fromDecimalFormatSymbols(
                        '+',
                        new DecimalFormatSymbols(locale)
                )
        );
    }

    @Override
    public Class<DecimalNumberSymbols> type() {
        return DecimalNumberSymbols.class;
    }

    // parseUrlFragmentSaveValue........................................................................................

    @Override
    DecimalNumberSymbols parseUrlFragmentSaveValueNonNull(final String value) {
        return DecimalNumberSymbols.parse(value);
    }
}

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

package walkingkooka.spreadsheet.convert;

import walkingkooka.Either;
import walkingkooka.convert.Converter;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

/**
 * A {@link Converter} that converts {@link SpreadsheetSelection} to {@link String}.
 */
final class SpreadsheetConverterSpreadsheetSelectionToString extends SpreadsheetConverter {

    /**
     * Singleton
     */
    final static SpreadsheetConverterSpreadsheetSelectionToString INSTANCE = new SpreadsheetConverterSpreadsheetSelectionToString();

    /**
     * Private ctor use singleton.
     */
    private SpreadsheetConverterSpreadsheetSelectionToString() {
    }

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type,
                              final SpreadsheetConverterContext context) {
        return value instanceof SpreadsheetSelection && String.class == type;
    }

    @Override
    <T> Either<T, String> convert0(final Object value,
                                   final Class<T> type,
                                   final SpreadsheetConverterContext context) {
        return this.convertSpreadsheetSelectionToString(
                (SpreadsheetSelection) value,
                type
        );
    }

    public <T> Either<T, String> convertSpreadsheetSelectionToString(final SpreadsheetSelection selection,
                                                                     final Class<T> type) {
        return this.successfulConversion(
                selection.text(),
                type
        );
    }

    @Override
    public String toString() {
        return "Selection to String";
    }
}

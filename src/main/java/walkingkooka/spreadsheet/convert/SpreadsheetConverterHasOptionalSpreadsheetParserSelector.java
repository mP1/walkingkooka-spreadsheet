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
import walkingkooka.spreadsheet.parser.HasOptionalSpreadsheetParserSelector;
import walkingkooka.spreadsheet.parser.SpreadsheetParserSelector;

final class SpreadsheetConverterHasOptionalSpreadsheetParserSelector extends SpreadsheetConverter {

    /**
     * Singleton
     */
    final static SpreadsheetConverterHasOptionalSpreadsheetParserSelector INSTANCE = new SpreadsheetConverterHasOptionalSpreadsheetParserSelector();

    private SpreadsheetConverterHasOptionalSpreadsheetParserSelector() {
        super();
    }

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type,
                              final SpreadsheetConverterContext context) {
        return value instanceof HasOptionalSpreadsheetParserSelector &&
            type == SpreadsheetParserSelector.class;
    }

    @Override
    public <T> Either<T, String> doConvert(final Object value,
                                           final Class<T> type,
                                           final SpreadsheetConverterContext context) {
        return this.successfulConversion(
            ((HasOptionalSpreadsheetParserSelector)value)
            .parserSelector()
                .orElse(null),
            type
        );
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return HasOptionalSpreadsheetParserSelector.class.getSimpleName();
    }
}

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
import walkingkooka.spreadsheet.format.SpreadsheetFormatters;
import walkingkooka.tree.expression.ExpressionNumber;

/**
 * A {@link Converter} that handles converting {@link Number} to {@link String} using {@link SpreadsheetFormatters#general()}.
 */
final class SpreadsheetConverterNumberToText extends SpreadsheetConverter {

    final static SpreadsheetConverterNumberToText INSTANCE = new SpreadsheetConverterNumberToText();

    private SpreadsheetConverterNumberToText() {
        super();
    }

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type,
                              final SpreadsheetConverterContext context) {
        return ExpressionNumber.is(value) &&
            type == String.class;
    }

    @Override
    public <T> Either<T, String> doConvert(final Object value,
                                           final Class<T> type,
                                           final SpreadsheetConverterContext context) {
        return GENERAL_FORMATTER.convert(
            value,
            type,
            context
        );
    }

    private final static Converter<SpreadsheetConverterContext> GENERAL_FORMATTER = SpreadsheetFormatters.general()
        .converter();

    // Object...........................................................................................................

    @Override
    public String toString() {
        return "number to text";
    }
}

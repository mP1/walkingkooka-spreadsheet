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
import walkingkooka.convert.ConverterContext;
import walkingkooka.tree.expression.ExpressionNumberConverterContext;

/**
 * Pipes the result of converting {@link Boolean} to {@link String} and then to the text formatter.
 */
final class SpreadsheetConverterBooleanString implements Converter<ExpressionNumberConverterContext> {

    static SpreadsheetConverterBooleanString with(final Converter<ExpressionNumberConverterContext> booleanString,
                                                  final Converter<ExpressionNumberConverterContext> textFormatter) {
        return new SpreadsheetConverterBooleanString(booleanString.cast(ConverterContext.class),
                textFormatter.cast(ConverterContext.class));
    }

    private SpreadsheetConverterBooleanString(final Converter<ConverterContext> booleanString,
                                              final Converter<ConverterContext> textFormatter) {
        super();
        this.booleanString = booleanString;
        this.textFormatter = textFormatter;
    }

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type,
                              final ExpressionNumberConverterContext context) {
        return this.booleanString.canConvert(value, type, context);
    }

    @Override
    public <T> Either<T, String> convert(final Object value,
                                         final Class<T> type,
                                         final ExpressionNumberConverterContext context) {
        Either<T, String> result = this.booleanString.convert(value, type, context);
        if (result.isLeft()) {
            result = this.textFormatter.convert(result.leftValue(), type, context);
        }
        return result;
    }

    private final Converter<ConverterContext> booleanString;
    private final Converter<ConverterContext> textFormatter;

    @Override
    public String toString() {
        return this.booleanString + "->" + this.textFormatter;
    }
}

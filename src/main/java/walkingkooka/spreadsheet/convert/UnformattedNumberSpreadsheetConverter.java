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

import walkingkooka.Cast;
import walkingkooka.Either;
import walkingkooka.convert.Converter;
import walkingkooka.tree.expression.ExpressionNumberConverterContext;

/**
 * A {@link Converter} that only supports converting values to {@link String}. Numbers are formatted to {@link String}
 * using a simple pattern, ignoring any number format pattern.
 */
final class UnformattedNumberSpreadsheetConverter<C extends ExpressionNumberConverterContext> implements Converter<C> {

    static <C extends ExpressionNumberConverterContext> UnformattedNumberSpreadsheetConverter instance() {
        return Cast.to(INSTANCE);
    }

    private final static UnformattedNumberSpreadsheetConverter<?> INSTANCE = new UnformattedNumberSpreadsheetConverter<>();

    private UnformattedNumberSpreadsheetConverter() {
        super();
    }

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type,
                              final C context) {
        return String.class == type;
    }

    @Override
    public <T> Either<T, String> convert(final Object value,
                                         final Class<T> type,
                                         final C context) {
        return this.canConvert(value, type, context) ?
                this.successfulConversion(
                        UnformattedNumberSpreadsheetConverterSpreadsheetValueVisitor.convertToString(
                                value,
                                context
                        ),
                        type
                ) :
                this.failConversion(value, type);
    }

    public String toString() {
        return this.getClass().getSimpleName();
    }
}

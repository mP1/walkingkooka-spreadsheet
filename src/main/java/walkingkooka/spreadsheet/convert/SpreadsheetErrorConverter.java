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
import walkingkooka.convert.Converters;
import walkingkooka.math.Maths;
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.SpreadsheetErrorConversionException;
import walkingkooka.spreadsheet.SpreadsheetErrorKind;
import walkingkooka.tree.expression.ExpressionNumber;

/**
 * A {@link Converter} that can convert {@link SpreadsheetError} to a {@link String} value.
 * This basically returns the {@link SpreadsheetErrorKind#text()}, giving text like <code>#ERROR</code>.
 * All other types ill throw a {@link SpreadsheetErrorConversionException}.
 */
final class SpreadsheetErrorConverter implements Converter<SpreadsheetConverterContext> {

    /**
     * Singleton
     */
    final static SpreadsheetErrorConverter INSTANCE = new SpreadsheetErrorConverter();

    /**
     * Private ctor use singleton.
     */
    private SpreadsheetErrorConverter() {
    }

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type,
                              final SpreadsheetConverterContext context) {
        return value instanceof SpreadsheetError;
    }

    @Override
    public <T> Either<T, String> convert(final Object value,
                                         final Class<T> type,
                                         final SpreadsheetConverterContext context) {
        return this.canConvert(value, type, context) ?
                this.convertSpreadsheetError(
                        (SpreadsheetError) value,
                        type,
                        context
                ) :
                this.failConversion(value, type);
    }

    private <T> Either<T, String> convertSpreadsheetError(final SpreadsheetError error,
                                                          final Class<T> type,
                                                          final SpreadsheetConverterContext context) {
        Either<T, String> converted = null;

        if (String.class == type) {
            converted = this.successfulConversion(
                    error.kind()
                            .text(),
                    type
            );
        } else {
            if (error.isMissingCell()) {
                if (Maths.isNumberClass(type)) {
                    converted = NUMBER_TO_NUMBER.convert(
                            0,
                            type,
                            context
                    );
                } else {
                    if (ExpressionNumber.isClass(type)) { // also matches Number so must do Maths.isNumberClass first
                        converted = this.successfulConversion(
                                context.expressionNumberKind()
                                        .zero(),
                                type
                        );
                    }
                }
            }

            if (null == converted) {
                throw new SpreadsheetErrorConversionException(error);
            }
        }

        return converted;
    }

    private final static Converter<SpreadsheetConverterContext> NUMBER_TO_NUMBER = Converters.numberNumber();

    @Override
    public String toString() {
        return SpreadsheetError.class.getSimpleName();
    }
}

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
import walkingkooka.spreadsheet.SpreadsheetStrings;
import walkingkooka.tree.expression.ExpressionNumber;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A {@link Converter} that handles converting {@link String}, {@link LocalDate}, {@link LocalDateTime} and {@link Number}
 * to {@link Boolean}.
 */
final class SpreadsheetConverterToBoolean extends SpreadsheetConverter {

    /**
     * Singleton
     */
    final static SpreadsheetConverterToBoolean INSTANCE = new SpreadsheetConverterToBoolean();

    private SpreadsheetConverterToBoolean() {
        super();
    }

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type,
                              final SpreadsheetConverterContext context) {
        return (
            value instanceof Boolean ||
                value instanceof LocalDate ||
                value instanceof LocalDateTime ||
                value instanceof CharSequence ||
                ExpressionNumber.is(value)
        ) &&
            Boolean.class == type;
    }

    @Override
    public <T> Either<T, String> doConvert(final Object value,
                                           final Class<T> type,
                                           final SpreadsheetConverterContext context) {
        Either<T, String> result = null;

        if (value instanceof Boolean && Boolean.class == type) {
            result = this.successfulConversion(
                value,
                type
            );
        } else {
            // handle "TRUE" and "FALSE"
            if (value instanceof CharSequence) {
                if (SpreadsheetStrings.CASE_SENSITIVITY.equals("TRUE", (CharSequence) value)) {
                    result = this.successfulConversion(
                        Boolean.TRUE,
                        type
                    );
                } else {
                    if (SpreadsheetStrings.CASE_SENSITIVITY.equals("FALSE", (CharSequence) value)) {
                        result = this.successfulConversion(
                            Boolean.FALSE,
                            type
                        );
                    }
                }
            }
        }

        if (null == result) {
            final Number numberOrNull = context.convert(
                value,
                Number.class
            ).orElseLeft(null);

            if (null != numberOrNull) {
                result = this.successfulConversion(
                    0 == numberOrNull.byteValue() ?
                        Boolean.FALSE :
                        Boolean.TRUE,
                    type
                );
            }
        }

        if (null == result) {
            result = this.failConversion(
                value,
                type
            );
        }

        return result;
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return "to boolean";
    }
}

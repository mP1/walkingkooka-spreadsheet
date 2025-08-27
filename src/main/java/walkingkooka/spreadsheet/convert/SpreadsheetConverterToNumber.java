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
import walkingkooka.tree.expression.ExpressionNumber;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A {@link walkingkooka.convert.Converter} that handles converting {@link Boolean}, {@link LocalDate}, {@link LocalDateTime},
 * {@link String} to a {@link Number}.
 * Note that converting {@link Number} to another {@link Number} type is not supported by this {@link Converter}.
 */
final class SpreadsheetConverterToNumber extends SpreadsheetConverter {

    /**
     * Gets an instance of {@link SpreadsheetConverterToNumber}.
     * Note the boolean flag is used to control whether {@link walkingkooka.math.DecimalNumberContext} symbols are ignored.
     * The default text to number behaviour requires the decimal point etc from the context to be ignored and "defaults"
     * used instead, so formulas will always only accept "." as the decimal point.
     * The function numberValue however must be able to pass custom decimal-point and group-separator.
     */
    static SpreadsheetConverterToNumber with(final boolean ignoreDecimalNumberContextSymbols) {
        return ignoreDecimalNumberContextSymbols ?
            TRUE :
            FALSE;
    }

    private final static SpreadsheetConverterToNumber TRUE = new SpreadsheetConverterToNumber(true);

    private final static SpreadsheetConverterToNumber FALSE = new SpreadsheetConverterToNumber(false);

    private SpreadsheetConverterToNumber(final boolean ignoreDecimalNumberContextSymbols) {
        super();
        this.ignoreDecimalNumberContextSymbols = ignoreDecimalNumberContextSymbols;
    }

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type,
                              final SpreadsheetConverterContext context) {
        return (value instanceof Boolean ||
            value instanceof LocalDate ||
            value instanceof LocalDateTime ||
            value instanceof CharSequence) &&
            (Number.class == type || ExpressionNumber.isClass(type));
    }

    @Override
    public <T> Either<T, String> doConvert(final Object value,
                                           final Class<T> type,
                                           final SpreadsheetConverterContext context) {
        final Converter<SpreadsheetConverterContext> converter = SpreadsheetConverterToNumberSpreadsheetValueVisitor.converter(
            value,
            this.ignoreDecimalNumberContextSymbols
        );
        if(null == converter) {
            throw new IllegalArgumentException("Converter missing for " + value);
        }
        return converter.convert(
            value,
            type,
            context
        );
    }

    private final boolean ignoreDecimalNumberContextSymbols;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof SpreadsheetConverterToNumber &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetConverterToNumber other) {
        return this.ignoreDecimalNumberContextSymbols == other.ignoreDecimalNumberContextSymbols;
    }

    @Override
    public String toString() {
        return "to number";
    }
}

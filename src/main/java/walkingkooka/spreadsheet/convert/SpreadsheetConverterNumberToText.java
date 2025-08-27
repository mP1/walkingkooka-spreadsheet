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
import walkingkooka.spreadsheet.format.SpreadsheetFormatters;
import walkingkooka.tree.expression.ExpressionNumber;

/**
 * A {@link Converter} that handles converting {@link Number} to {@link String} using {@link SpreadsheetFormatters#general()}.
 */
final class SpreadsheetConverterNumberToText extends SpreadsheetConverter {

    /**
     * Gets an instance of {@link SpreadsheetConverterNumberToText}.
     * Note the boolean flag is used to control whether {@link walkingkooka.math.DecimalNumberContext} symbols are ignored.
     * The default text to number behaviour requires the decimal point etc from the context to be ignored and "defaults"
     * used instead, so formulas will always only accept "." as the decimal point.
     * The function numberValue however must be able to pass custom decimal-point and group-separator.
     */
    static SpreadsheetConverterNumberToText with(final boolean ignoreDecimalNumberContextSymbols) {
        return ignoreDecimalNumberContextSymbols ?
            TRUE :
            FALSE;
    }

    private final static SpreadsheetConverterNumberToText TRUE = new SpreadsheetConverterNumberToText(true);

    private final static SpreadsheetConverterNumberToText FALSE = new SpreadsheetConverterNumberToText(false);

    private SpreadsheetConverterNumberToText(final boolean ignoreDecimalNumberContextSymbols) {
        super();
        this.ignoreDecimalNumberContextSymbols = ignoreDecimalNumberContextSymbols;
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
            this.ignoreDecimalNumberContextSymbols ?
                SpreadsheetConverterNumberToTextSpreadsheetConverterContext.with(context) :
                context
        );
    }

    private final boolean ignoreDecimalNumberContextSymbols;

    private final static Converter<SpreadsheetConverterContext> GENERAL_FORMATTER = SpreadsheetFormatters.general()
        .converter();

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof SpreadsheetConverterNumberToText &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetConverterNumberToText other) {
        return this.ignoreDecimalNumberContextSymbols == other.ignoreDecimalNumberContextSymbols;
    }

    @Override
    public String toString() {
        return "number to text";
    }
}

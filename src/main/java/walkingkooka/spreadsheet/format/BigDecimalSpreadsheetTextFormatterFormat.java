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

package walkingkooka.spreadsheet.format;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Handles preparing the integer, fraction and possibly exponent digits that will eventually appear in the formatted text.
 */
enum BigDecimalSpreadsheetTextFormatterFormat {

    /**
     * integer and fraction number.
     */
    NORMAL {
        @Override
        BigDecimalSpreadsheetTextFormatterComponentContext context(final BigDecimal value,
                                                                   final BigDecimalSpreadsheetTextFormatterThousandsSeparator thousandsSeparator,
                                                                   final BigDecimalSpreadsheetTextFormatter formatter,
                                                                   final SpreadsheetTextFormatContext context) {
            final BigDecimal rounded = value.scaleByPowerOfTen(formatter.decimalPlacesShift)
                    .setScale(formatter.fractionDigitSymbolCount, RoundingMode.HALF_UP);

            final int valueSign = rounded.signum();
            String integerDigits = "";
            String fractionDigits = "";

            if (0 != valueSign) {
                final String digits = rounded
                        .unscaledValue()
                        .abs()
                        .toString();
                final int integerDigitCount = Math.min(rounded.precision() - rounded.scale(), digits.length());
                integerDigits = digits.substring(0, integerDigitCount);
                fractionDigits = digits.substring(integerDigitCount);
            }

            return BigDecimalSpreadsheetTextFormatterComponentContext.with(
                    BigDecimalSpreadsheetTextFormatterDigits.integer(BigDecimalSpreadsheetTextFormatterMinusSign.fromSignum(valueSign), integerDigits, thousandsSeparator),
                    BigDecimalSpreadsheetTextFormatterDigits.fraction(fractionDigits),
                    NO_EXPONENT,
                    formatter,
                    context);
        }
    },
    /**
     * formatted number includes an exponent and desired number of decimal places.
     */
    SCENTIFIC {
        @Override
        BigDecimalSpreadsheetTextFormatterComponentContext context(final BigDecimal value,
                                                                   final BigDecimalSpreadsheetTextFormatterThousandsSeparator thousandsSeparator,
                                                                   final BigDecimalSpreadsheetTextFormatter formatter,
                                                                   final SpreadsheetTextFormatContext context) {

            final int integerDigitSymbolCount = formatter.integerDigitSymbolCount;
            final int fractionDigitSymbolCount = formatter.fractionDigitSymbolCount;

            final BigDecimal rounded = value.abs()
                    .setScale((integerDigitSymbolCount + fractionDigitSymbolCount) - (value.precision() - value.scale()),
                            RoundingMode.HALF_UP)
                    .stripTrailingZeros();

            final String digits = rounded.unscaledValue()
                    .abs()
                    .toString();
            final int digitCount = digits.length();
            final int integerDigitCount = Math.min(integerDigitSymbolCount, digitCount);
            final int fractionDigitCount = Math.max(digitCount - integerDigitCount, fractionDigitSymbolCount);

            final int exponent = rounded.precision() - rounded.scale() - integerDigitCount;

            return BigDecimalSpreadsheetTextFormatterComponentContext.with(
                    BigDecimalSpreadsheetTextFormatterDigits.integer(BigDecimalSpreadsheetTextFormatterMinusSign.fromSignum(value.signum()), digits.substring(0, integerDigitCount), thousandsSeparator),
                    BigDecimalSpreadsheetTextFormatterDigits.fraction(digits.substring(integerDigitCount, Math.min(integerDigitCount + fractionDigitCount, digitCount))),
                    BigDecimalSpreadsheetTextFormatterDigits.exponent(BigDecimalSpreadsheetTextFormatterMinusSign.fromSignum(exponent), String.valueOf(Math.abs(exponent))),
                    formatter,
                    context);
        }
    };

    /**
     * Creates a new {@link BigDecimalSpreadsheetTextFormatterComponentContext} which will accompany the current
     * format request. Note context cannot be recycled as they contain state.
     */
    abstract BigDecimalSpreadsheetTextFormatterComponentContext context(
            final BigDecimal value,
            final BigDecimalSpreadsheetTextFormatterThousandsSeparator thousandsSeparator,
            final BigDecimalSpreadsheetTextFormatter formatter,
            final SpreadsheetTextFormatContext context);

    private final static BigDecimalSpreadsheetTextFormatterDigits NO_EXPONENT = BigDecimalSpreadsheetTextFormatterDigits.exponent(BigDecimalSpreadsheetTextFormatterMinusSign.NOT_REQUIRED, "");
}

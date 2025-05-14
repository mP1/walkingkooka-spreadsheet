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

import walkingkooka.text.CharSequences;

import java.math.BigDecimal;

/**
 * Handles preparing the integer, fraction and possibly exponent digits that will eventually appear in the formatted text.
 */
enum SpreadsheetPatternSpreadsheetFormatterNumberNormalOrScientific {

    /**
     * integer and fraction number.
     */
    NORMAL {
        @Override
        SpreadsheetPatternSpreadsheetFormatterNumberContext context(final BigDecimal value,
                                                                    final SpreadsheetPatternSpreadsheetFormatterNumber formatter,
                                                                    final SpreadsheetFormatterContext context) {
            final BigDecimal rounded = value.scaleByPowerOfTen(formatter.decimalPlacesShift)
                    .setScale(
                            formatter.fractionDigitSymbolCount,
                            context.mathContext()
                                    .getRoundingMode()
                    );

            final int valueSignum = rounded.signum();
            String integerDigits = "";
            String fractionDigits = "";

            if (0 != valueSignum) {
                final String digits = rounded
                        .unscaledValue()
                        .abs()
                        .toString();
                final int integerDigitCount = Math.min(
                        rounded.precision() - rounded.scale(),
                        digits.length()
                );
                integerDigits = integerDigitCount > 0 ?
                        digits.substring(0, integerDigitCount) :
                        "";
                fractionDigits = integerDigitCount >= 0 ?
                        digits.substring(integerDigitCount) :
                        CharSequences.repeating('0', -integerDigitCount) +
                                digits;
            }

            return SpreadsheetPatternSpreadsheetFormatterNumberContext.with(
                    formatter.currency, // when true formatting will use the monetaryDecimalSeparator rather than decimalSeparator
                    SpreadsheetPatternSpreadsheetFormatterNumberDigits.integer(
                            SpreadsheetPatternSpreadsheetFormatterNumberMinusSign.fromSignum(valueSignum),
                            integerDigits,
                            formatter.groupSeparator
                    ),
                    SpreadsheetPatternSpreadsheetFormatterNumberDigits.fraction(fractionDigits),
                    NO_EXPONENT,
                    formatter,
                    context
            );
        }
    },
    /**
     * formatted number includes an exponent and desired number of decimal places.
     */
    SCENTIFIC {
        @Override
        SpreadsheetPatternSpreadsheetFormatterNumberContext context(final BigDecimal value,
                                                                    final SpreadsheetPatternSpreadsheetFormatterNumber formatter,
                                                                    final SpreadsheetFormatterContext context) {

            final int integerDigitSymbolCount = formatter.integerDigitSymbolCount;
            final int fractionDigitSymbolCount = formatter.fractionDigitSymbolCount;

            final BigDecimal rounded = value.abs()
                    .setScale(
                            (integerDigitSymbolCount + fractionDigitSymbolCount) - (value.precision() - value.scale()),
                            context.mathContext().getRoundingMode()
                    )
                    .stripTrailingZeros();

            final String digits = rounded.unscaledValue()
                    .abs()
                    .toString();
            final int digitCount = digits.length();
            final int integerDigitCount = Math.min(
                    integerDigitSymbolCount,
                    digitCount
            );
            final int fractionDigitCount = Math.max(
                    digitCount - integerDigitCount,
                    fractionDigitSymbolCount
            );

            final int exponent = rounded.precision() - rounded.scale() - integerDigitCount;

            return SpreadsheetPatternSpreadsheetFormatterNumberContext.with(
                    formatter.currency,
                    SpreadsheetPatternSpreadsheetFormatterNumberDigits.integer(
                            SpreadsheetPatternSpreadsheetFormatterNumberMinusSign.fromSignum(
                                    value.signum()
                            ),
                            digits.substring(
                                    0,
                                    integerDigitCount),
                            formatter.groupSeparator
                    ),
                    SpreadsheetPatternSpreadsheetFormatterNumberDigits.fraction(
                            digits.substring(
                                    integerDigitCount,
                                    Math.min(
                                            integerDigitCount + fractionDigitCount,
                                            digitCount
                                    )
                            )
                    ),
                    SpreadsheetPatternSpreadsheetFormatterNumberDigits.exponent(
                            SpreadsheetPatternSpreadsheetFormatterNumberMinusSign.fromSignum(exponent),
                            String.valueOf(
                                    Math.abs(exponent)
                            )
                    ),
                    formatter,
                    context);
        }
    };

    /**
     * Creates a new {@link SpreadsheetPatternSpreadsheetFormatterNumberContext} which will accompany the current
     * format request. Note context cannot be recycled as they contain state.
     */
    abstract SpreadsheetPatternSpreadsheetFormatterNumberContext context(final BigDecimal value,
                                                                         final SpreadsheetPatternSpreadsheetFormatterNumber formatter,
                                                                         final SpreadsheetFormatterContext context);

    private final static SpreadsheetPatternSpreadsheetFormatterNumberDigits NO_EXPONENT = SpreadsheetPatternSpreadsheetFormatterNumberDigits.exponent(SpreadsheetPatternSpreadsheetFormatterNumberMinusSign.NOT_REQUIRED, "");
}

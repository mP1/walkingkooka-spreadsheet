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

import walkingkooka.collect.map.Maps;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.expression.ExpressionNumber;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link SpreadsheetPatternSpreadsheetFormatter} that converts any given value to a {@link ExpressionNumber} and then proceeds to format.
 * Formatting as a scientific number is controlled by {@link SpreadsheetFormatterContext#generalFormatNumberDigitCount}.
 */
final class SpreadsheetPatternSpreadsheetFormatterGeneral implements SpreadsheetPatternSpreadsheetFormatter {

    /**
     * Singleton
     */
    final static SpreadsheetPatternSpreadsheetFormatterGeneral INSTANCE = new SpreadsheetPatternSpreadsheetFormatterGeneral();

    /**
     * Private ctor use singleton
     */
    private SpreadsheetPatternSpreadsheetFormatterGeneral() {
        super();
    }

    @Override
    public Optional<SpreadsheetText> formatSpreadsheetText(final Object value,
                                                           final SpreadsheetFormatterContext context) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(context, "context");

        return this.canFormat(
                value,
                context
        ) ? this.formatNumber(
                context.convertOrFail(
                        value,
                        ExpressionNumber.class
                ),
                context
        ) :
                SpreadsheetFormatter.EMPTY;
    }

    private boolean canFormat(final Object value,
                              final SpreadsheetFormatterContext context) {
        return context.canConvert(
                value,
                ExpressionNumber.class
        );
    }

    private Optional<SpreadsheetText> formatNumber(final ExpressionNumber number,
                                                   final SpreadsheetFormatterContext context) {
        return this.shouldScientificFormat(
                number,
                context
        ) ?
                this.scientificFormatter(context)
                        .formatSpreadsheetText(number, context) :
                this.nonScientificFormatter(context)
                        .formatSpreadsheetText(number, context)
                        .map(t -> removeTrailingDecimalPlaceIfNecessary(t, context));
    }

    /**
     * Tests if the {@link ExpressionNumber} should be scientific format, or does it have more than 12 digits ignoring
     * the sign.
     * <br>
     * The digit count should probably be a method on SpreadsheetFormatterContext#shouldScientificFormat
     */
    private boolean shouldScientificFormat(final ExpressionNumber number,
                                           final SpreadsheetFormatterContext context) {
        return number.isBigDecimal() ?
                this.shouldScientificFormatBigDecimal(
                        number.bigDecimal(),
                        context
                ) :
                this.shouldScientificFormatDouble(
                        number.doubleValue(),
                        context
                );
    }

    private boolean shouldScientificFormatBigDecimal(final BigDecimal number,
                                                     final SpreadsheetFormatterContext context) {
        final int digitCount = context.generalFormatNumberDigitCount();
        final Map<Integer, BigDecimal> map = this.generalNumberFormatDigitCountToMaxBigDouble;

        BigDecimal value = map.get(digitCount);
        if (null == value) {
            value = BigDecimal.valueOf(1, -digitCount); // eg: 1E12

            map.put(
                    digitCount,
                    value
            );
        }

        return number.abs()
                .compareTo(value) >= 0;
    }

    private final Map<Integer, BigDecimal> generalNumberFormatDigitCountToMaxBigDouble = Maps.concurrent();

    private boolean shouldScientificFormatDouble(final double number,
                                                 final SpreadsheetFormatterContext context) {
        final int digitCount = context.generalFormatNumberDigitCount();
        final Map<Integer, Double> map = this.generalNumberFormatDigitCountToMaxDouble;
        Double value = map.get(digitCount);
        if (null == value) {
            value = Double.parseDouble("1E" + digitCount); // eg: 1E12

            map.put(
                    digitCount,
                    value
            );
        }

        return Math.abs(number) >= value;
    }

    private final Map<Integer, Double> generalNumberFormatDigitCountToMaxDouble = Maps.concurrent();

    /**
     * The number format pattern for large numbers that should be formatted in scientific format.
     */
    private SpreadsheetPatternSpreadsheetFormatter scientificFormatter(final SpreadsheetFormatterContext context) {
        final int digitCount = context.generalFormatNumberDigitCount();

        final Map<Integer, SpreadsheetPatternSpreadsheetFormatter> map = generalNumberFormatDigitCountToScientificFormatter;

        SpreadsheetPatternSpreadsheetFormatter formatter = map.get(digitCount);
        if (null == formatter) {
            formatter = (SpreadsheetPatternSpreadsheetFormatter)
                    SpreadsheetPattern.parseNumberFormatPattern(
                            "0." +
                                    CharSequences.repeating('#', digitCount) +
                                    "E+0"
                    ).formatter();
            map.put(
                    digitCount,
                    formatter
            );
        }

        return formatter;
    }

    private final Map<Integer, SpreadsheetPatternSpreadsheetFormatter> generalNumberFormatDigitCountToScientificFormatter = Maps.concurrent();


    private SpreadsheetPatternSpreadsheetFormatter nonScientificFormatter(final SpreadsheetFormatterContext context) {
        final int digitCount = context.generalFormatNumberDigitCount();

        final Map<Integer, SpreadsheetPatternSpreadsheetFormatter> map = this.generalNumberFormatDigitCountToNonScientificFormatter;
        SpreadsheetPatternSpreadsheetFormatter formatter = map.get(digitCount);
        if (null == formatter) {
            formatter = (SpreadsheetPatternSpreadsheetFormatter)
                    SpreadsheetPattern.parseNumberFormatPattern(
                            "0." +
                                    CharSequences.repeating('#', digitCount)
                    ).formatter();
            map.put(
                    digitCount,
                    formatter
            );
        }

        return formatter;
    }

    private final Map<Integer, SpreadsheetPatternSpreadsheetFormatter> generalNumberFormatDigitCountToNonScientificFormatter = Maps.concurrent();

    /**
     * Removes any trailing decimal place, so formatting whole numbers do not have a decimal place.
     */
    private SpreadsheetText removeTrailingDecimalPlaceIfNecessary(final SpreadsheetText spreadsheetText,
                                                                  final SpreadsheetFormatterContext context) {
        final String text = spreadsheetText.text();
        final int last = text.length() - 1;
        return spreadsheetText.setText(
                text.charAt(last) == context.decimalSeparator() ?
                        text.substring(0, last) :
                        text
        );
    }

    @Override
    public Optional<List<SpreadsheetFormatterSelectorTextComponent>> textComponents(final SpreadsheetFormatterContext context) {
        Objects.requireNonNull(context, "context");
        throw new UnsupportedOperationException();
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return "General";
    }
}

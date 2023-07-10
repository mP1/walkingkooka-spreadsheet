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

import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.tree.expression.ExpressionNumber;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link SpreadsheetFormatter} that converts any given value to a {@link ExpressionNumber} and ten proceeds to format.
 * Note numbers with more than 12 digits are formatted as a scientific number.
 */
final class GeneralSpreadsheetFormatter implements SpreadsheetFormatter {

    /**
     * Singleton
     */
    final static GeneralSpreadsheetFormatter INSTANCE = new GeneralSpreadsheetFormatter();

    /**
     * Private ctor use singleton
     */
    private GeneralSpreadsheetFormatter() {
        super();
    }

    @Override
    public boolean canFormat(final Object value,
                             final SpreadsheetFormatterContext context) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(context, "context");

        return context.canConvert(
                value,
                ExpressionNumber.class
        );
    }

    @Override
    public Optional<SpreadsheetText> format(final Object value,
                                            final SpreadsheetFormatterContext context) {
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

    private Optional<SpreadsheetText> formatNumber(final ExpressionNumber number,
                                                   final SpreadsheetFormatterContext context) {
        return this.shouldScientificFormat(number) ?
                SCIENTIFIC_FORMAT.format(number, context) :
                NON_SCIENTIFIC_FORMAT.format(number, context)
                        .map(t -> removeTrailingDecimalPlaceIfNecessary(t, context));
    }

    /**
     * Tests if the {@link ExpressionNumber} should be scientific format, or does it have more than 12 digits ignoring
     * the sign.
     * <br>
     * The digit count should probably be a method on SpreadsheetFormatterContext#shouldScientificFormat
     */
    private boolean shouldScientificFormat(final ExpressionNumber number) {
        return number.isBigDecimal() ?
                this.shouldScientificFormatBigDecimal(number.bigDecimal()) :
                this.shouldScientificFormatDouble(number.doubleValue());
    }

    private boolean shouldScientificFormatBigDecimal(final BigDecimal number) {
        return number.abs().compareTo(SCIENTIFIC_BIG_DECIMAL) >= 0;
    }

    private final static BigDecimal SCIENTIFIC_BIG_DECIMAL = new BigDecimal("1E12");

    private boolean shouldScientificFormatDouble(final double number) {
        return Math.abs(number) >= SCIENTIFIC_DOUBLE;
    }

    private final static double SCIENTIFIC_DOUBLE = 1E12;

    /**
     * The number format pattern for large numbers that should be formatted in scientific format.
     */
    private final static SpreadsheetFormatter SCIENTIFIC_FORMAT = SpreadsheetPattern.parseNumberFormatPattern("0.###########E+0")
            .formatter();

    /**
     * The number format pattern used to format non scientific numbers..
     */
    private final static SpreadsheetFormatter NON_SCIENTIFIC_FORMAT = SpreadsheetPattern.parseNumberFormatPattern("0.#")
            .formatter();

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
    public String toString() {
        return "General";
    }
}

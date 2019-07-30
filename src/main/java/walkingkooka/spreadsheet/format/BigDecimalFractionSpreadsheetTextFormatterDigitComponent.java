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

/**
 * Represents a placeholder for a digit.
 */
final class BigDecimalFractionSpreadsheetTextFormatterDigitComponent extends BigDecimalFractionSpreadsheetTextFormatterComponent {

    /**
     * Factory that creates a {@link BigDecimalFractionSpreadsheetTextFormatterDigitComponent}.
     */
    static BigDecimalFractionSpreadsheetTextFormatterDigitComponent with(final int position,
                                                                         final BigDecimalFractionSpreadsheetTextFormatterZero zero) {
        return new BigDecimalFractionSpreadsheetTextFormatterDigitComponent(position, zero);
    }

    /**
     * Private ctor use factory
     */
    private BigDecimalFractionSpreadsheetTextFormatterDigitComponent(final int position,
                                                                     final BigDecimalFractionSpreadsheetTextFormatterZero zero) {
        super();

        this.position = position;
        this.zero = zero;
    }

    @Override
    void append(final BigDecimalFractionSpreadsheetTextFormatterComponentContext context) {
        context.appendDigit(this.position, this.zero);
    }

    private final int position;

    private final BigDecimalFractionSpreadsheetTextFormatterZero zero;

    @Override
    public final String toString() {
        return this.zero.pattern();
    }
}

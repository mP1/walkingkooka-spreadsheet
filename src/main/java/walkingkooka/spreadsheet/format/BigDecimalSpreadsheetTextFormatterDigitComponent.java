/*
 * Copyright 2018 Miroslav Pokorny (github.com/mP1)
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
 *
 */

package walkingkooka.spreadsheet.format;

/**
 * Represents a placeholder for a digit.
 */
final class BigDecimalSpreadsheetTextFormatterDigitComponent extends BigDecimalSpreadsheetTextFormatterComponent {

    /**
     * Factory that creates a {@link BigDecimalSpreadsheetTextFormatterDigitComponent}.
     */
    static BigDecimalSpreadsheetTextFormatterDigitComponent with(final int position,
                                                                 final BigDecimalSpreadsheetTextFormatterZero zero) {
        return new BigDecimalSpreadsheetTextFormatterDigitComponent(position, zero);
    }

    /**
     * Package private to limit subclassing.
     */
    BigDecimalSpreadsheetTextFormatterDigitComponent(final int position,
                                                     final BigDecimalSpreadsheetTextFormatterZero zero) {
        super();

        this.position = position;
        this.zero = zero;
    }

    @Override
    void append(final BigDecimalSpreadsheetTextFormatterComponentContext context) {
        context.appendDigit(this.position, this.zero);
    }

    private final int position;

    private final BigDecimalSpreadsheetTextFormatterZero zero;

    @Override
    public final String toString() {
        return this.zero.pattern();
    }
}

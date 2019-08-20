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

import walkingkooka.ToStringBuilder;

/**
 * Handles formatting of both the numerator.
 */
final class FractionSpreadsheetFormatterDigitsDenominator extends FractionSpreadsheetFormatterDigits {

    /**
     * {@see FractionSpreadsheetFormatterDigits}
     */
    static FractionSpreadsheetFormatterDigitsDenominator with(final String text) {
        return new FractionSpreadsheetFormatterDigitsDenominator(text);
    }

    /**
     * Private to use factory.
     */
    private FractionSpreadsheetFormatterDigitsDenominator(final String text) {
        super(text);
    }

    @Override
    final void sign(final FractionSpreadsheetFormatterContext context) {
    }

    @Override
    public void buildToString(final ToStringBuilder builder) {
        builder.value(this.text);
    }
}

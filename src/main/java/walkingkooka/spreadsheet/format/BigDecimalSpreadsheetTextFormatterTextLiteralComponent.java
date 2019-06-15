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
 * Appends a text literal into the formatted text.
 */
final class BigDecimalSpreadsheetTextFormatterTextLiteralComponent extends BigDecimalSpreadsheetTextFormatterComponent {

    /**
     * Creates a new text literal.
     */
    static BigDecimalSpreadsheetTextFormatterTextLiteralComponent with(final String text) {
        return new BigDecimalSpreadsheetTextFormatterTextLiteralComponent(text);
    }

    /**
     * Private ctor use factory
     */
    private BigDecimalSpreadsheetTextFormatterTextLiteralComponent(final String text) {
        super();
        this.text = text;
    }

    @Override
    void append(final BigDecimalSpreadsheetTextFormatterComponentContext context) {
        context.appendText(this.text);
    }

    private final String text;

    @Override
    public String toString() {
        return this.text;
    }
}

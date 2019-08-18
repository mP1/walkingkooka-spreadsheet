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

import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.cursor.TextCursor;

/**
 * A {@link SpreadsheetNumberParsePatternsComponent} that matches an exponent and switches parsing to exponent parsing mode.
 */
final class SpreadsheetNumberParsePatternsComponentExponent extends SpreadsheetNumberParsePatternsComponent2 {

    /**
     * Singleton
     */
    final static SpreadsheetNumberParsePatternsComponentExponent INSTANCE = new SpreadsheetNumberParsePatternsComponentExponent();

    /**
     * Private ctor use singleton.
     */
    private SpreadsheetNumberParsePatternsComponentExponent() {
        super();
    }

    @Override
    void parse(final TextCursor cursor,
               final SpreadsheetNumberParsePatternsContext context) {
        do {
            if (cursor.isEmpty()) {
                break;
            }

            // E
            if (0 != CaseSensitivity.INSENSITIVE.compare(cursor.at(), context.context.exponentSymbol())) {
                break;
            }

            cursor.next();
            boolean negativeExponent = false;

            if (false == cursor.isEmpty()) {
                final char sign = cursor.at();

                // E+
                if (context.context.positiveSign() == sign) {
                    cursor.next();
                    negativeExponent = false;
                    context.nextComponent(cursor);
                } else {
                    // E-
                    if (context.context.negativeSign() == sign) {
                        cursor.next();
                        negativeExponent = true;
                        context.nextComponent(cursor);
                    }
                }
            }
            context.mode = SpreadsheetNumberParsePatternsMode.EXPONENT;
            context.negativeExponent = negativeExponent;
            context.nextComponent(cursor);
        } while (false);
    }

    @Override
    public String toString() {
        return "E";
    }
}

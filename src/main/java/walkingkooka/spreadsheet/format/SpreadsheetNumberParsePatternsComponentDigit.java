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

import walkingkooka.text.cursor.TextCursor;

/**
 * Base class for both digit kinds, that tries to match the minimum but not more then the maximum.
 */
abstract class SpreadsheetNumberParsePatternsComponentDigit extends SpreadsheetNumberParsePatternsComponent {

    /**
     * Package private to limit sub classing.
     */
    SpreadsheetNumberParsePatternsComponentDigit(final int max) {
        super();
        this.max = max;
    }

    @Override
    final void parse(final TextCursor cursor,
                     final SpreadsheetNumberParsePatternsContext context) {
        final char groupingSeparator = context.context.groupingSeparator();
        final int max = this.max;
        int count = 0;

        for (; ; ) {
            if (cursor.isEmpty()) {
                break;
            }

            // grouping separators are skipped when digits are expected.
            final char c = cursor.at();
            if(groupingSeparator == c) {
                cursor.next();
                continue;
            }

            // special case positive/negative sign if in INTEGER mode.
            if (SpreadsheetNumberParsePatternsMode.INTEGER == context.mode && null == context.negativeMantissa) {
                if (context.context.negativeSign() == c) {
                    cursor.next();
                    context.negativeMantissa = true;
                    continue;
                }

                if (context.context.positiveSign() == c) {
                    cursor.next();
                    context.negativeMantissa = false;
                    continue;
                }
            }

            // $c might be a digit or space
            if (false == this.handle(c, context)) {
               break;
            }
            cursor.next();
            count++;
            if (count == max) {
                break;
            }
        }

        context.nextComponent(cursor);
    }

    /**
     * Called for each character found.
     */
    abstract boolean handle(final char c,
                            final SpreadsheetNumberParsePatternsContext context);

    final boolean handleDigit(final char c,
                              final SpreadsheetNumberParsePatternsContext context) {
        final int digit = Character.digit(c, 10);
        final boolean hasDigitValue = -1 != digit;
        if (hasDigitValue) {
            context.mode.onDigit(digit, context);
        }
        return hasDigitValue;
    }

    private final int max;

    /**
     * All digit components are optional.
     */
    @Override
    final boolean isRequired() {
        return false;
    }
}

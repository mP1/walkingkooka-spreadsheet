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

package walkingkooka.spreadsheet.format.pattern;

import walkingkooka.math.DecimalNumberContext;
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
                     final SpreadsheetNumberParsePatternsRequest request) {
        final DecimalNumberContext context = request.context;
        final char groupingSeparator = context.groupingSeparator();
        final int max = this.max;
        int count = 0;

        for (; ; ) {
            if (cursor.isEmpty()) {
                break;
            }

            // grouping separators are skipped when digits are expected.
            final char c = cursor.at();
            if (groupingSeparator == c) {
                cursor.next();
                continue;
            }

            // special case positive/negative sign if in INTEGER mode.
            if (SpreadsheetNumberParsePatternsMode.INTEGER == request.mode && null == request.negativeMantissa) {
                if (context.negativeSign() == c) {
                    cursor.next();
                    request.negativeMantissa = true;
                    continue;
                }

                if (context.positiveSign() == c) {
                    cursor.next();
                    request.negativeMantissa = false;
                    continue;
                }
            }

            // $c might be a digit or space
            if (false == this.handle(c, request)) {
                break;
            }
            cursor.next();
            count++;
            if (count == max) {
                break;
            }
        }

        request.nextComponent(cursor);
    }

    /**
     * Called for each character found.
     */
    abstract boolean handle(final char c,
                            final SpreadsheetNumberParsePatternsRequest request);

    final boolean handleDigit(final char c,
                              final SpreadsheetNumberParsePatternsRequest request) {
        final int digit = Character.digit(c, 10);
        final boolean hasDigitValue = -1 != digit;
        if (hasDigitValue) {
            request.mode.onDigit(digit, request);
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

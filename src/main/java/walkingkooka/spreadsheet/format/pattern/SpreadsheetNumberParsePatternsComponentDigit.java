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
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.text.cursor.TextCursor;

/**
 * Base class for both digit kinds, that tries to match the minimum but not more then the maximum.
 */
abstract class SpreadsheetNumberParsePatternsComponentDigit extends SpreadsheetNumberParsePatternsComponent {

    /**
     * Package private to limit sub classing.
     */
    SpreadsheetNumberParsePatternsComponentDigit(final SpreadsheetNumberParsePatternsComponentDigitMode mode,
                                                 final int max) {
        super();
        this.mode = mode;
        this.max = max;
    }

    @Override
    final boolean parse(final TextCursor cursor,
                     final SpreadsheetNumberParsePatternsRequest request) {
        boolean completed;

        if(cursor.isEmpty()) {
            completed = request.nextComponent(cursor);
        } else {
            request.digitMode.tryParseSign(cursor, request);
            completed = this.parseDigits(cursor, request);
        }
        return completed;
    }

    /**
     * Attempts to parse the requested number of digits.
     */
    private boolean parseDigits(final TextCursor cursor,
                             final SpreadsheetNumberParsePatternsRequest request) {
        final DecimalNumberContext context = request.context;
        final SpreadsheetNumberParsePatternsMode mode = request.mode;
        final int max = this.max;
        int count = 0;

        for (; ; ) {
            if (cursor.isEmpty()) {
                break;
            }

            // grouping separators are skipped when digits are expected.
            final char c = cursor.at();
            if (mode.isGroupSeparator(c, context)) {
                request.addNumberIfNecessary();

                final String groupingText = Character.toString(c);
                request.add(SpreadsheetParserToken.groupingSeparatorSymbol(groupingText, groupingText));

                cursor.next();
                continue;
            }

            if(this.shouldHandleWhitespace() && Character.isWhitespace(c)) {
                request.addNumberIfNecessary();

                final String whitespaceText = Character.toString(c);
                request.add(SpreadsheetParserToken.whitespace(whitespaceText, whitespaceText));

                cursor.next();
                continue;
            }

            // $c might be a digit or space
            final int digit = Character.digit(c, 10);
            if (-1 == digit) {
                break;
            }

            request.digits.append(c);

            cursor.next();
            count++;
            if (count == max) {
                break;
            }
        }

        if(request.addNumberIfNecessary()) {
            request.setDigitMode(request.digitMode.next());
        }
        return request.nextComponent(cursor);
    }

    final SpreadsheetNumberParsePatternsComponentDigitMode mode;

    private boolean shouldHandleWhitespace() {
        return this instanceof SpreadsheetNumberParsePatternsComponentDigitSpace;
    }

    private final int max;
}

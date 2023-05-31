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
abstract class SpreadsheetNumberParsePatternComponentDigit extends SpreadsheetNumberParsePatternComponent {

    /**
     * Package private to limit sub classing.
     */
    SpreadsheetNumberParsePatternComponentDigit(final SpreadsheetNumberParsePatternComponentDigitMode mode,
                                                final int max) {
        super();
        this.mode = mode;
        this.max = max;
    }

    @Override
    final boolean isExpressionCompatible() {
        return true;
    }

    @Override
    final boolean parse(final TextCursor cursor,
                        final SpreadsheetNumberParsePatternRequest request) {
        if (false == cursor.isEmpty()) {
            request.digitMode.tryParseSign(cursor, request);
            this.parseDigits(cursor, request);
        }
        return request.nextComponent(cursor);
    }

    /**
     * Attempts to parse the requested number of digits.
     */
    private void parseDigits(final TextCursor cursor,
                             final SpreadsheetNumberParsePatternRequest request) {
        final DecimalNumberContext context = request.context;
        final SpreadsheetNumberParsePatternMode mode = request.mode;
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

            if (this.shouldHandleWhitespace() && Character.isWhitespace(c)) {
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

        if (request.addNumberIfNecessary()) {
            request.setDigitMode(request.digitMode.next());
        }
    }

    final SpreadsheetNumberParsePatternComponentDigitMode mode;

    private boolean shouldHandleWhitespace() {
        return this instanceof SpreadsheetNumberParsePatternComponentDigitSpace;
    }

    private final int max;
}

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
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.text.cursor.TextCursor;

/**
 * Base class for both digit kinds, that tries to match the minimum but not more than the maximum.
 */
abstract class SpreadsheetNumberParsePatternComponentDigit extends SpreadsheetNumberParsePatternComponent {

    /**
     * Package private to limit subclassing.
     */
    SpreadsheetNumberParsePatternComponentDigit(final SpreadsheetNumberParsePatternComponentDigitMode mode,
                                                final int max) {
        super();
        this.mode = mode;
        this.max = max;
    }

    @Override //
    final boolean isExpressionCompatible() {
        return true;
    }

    @Override //
    final boolean parse(final TextCursor cursor,
                        final SpreadsheetNumberParsePatternRequest request) {
        if (cursor.isNotEmpty()) {
            final SpreadsheetNumberParsePatternComponentDigitMode digitMode = request.digitMode;
            digitMode.tryParseSign(cursor, request);

            this.parseDigits(cursor, request);

            if (request.addNumberIfNecessary()) {
                request.setDigitMode(
                    digitMode.next()
                );
            }
        }
        return request.nextComponent(cursor);
    }

    /**
     * Attempts to parse the requested number of digits.
     */
    private void parseDigits(final TextCursor cursor,
                             final SpreadsheetNumberParsePatternRequest request) {
        final DecimalNumberContext context = request.context;
        final SpreadsheetNumberParsePatternComponentDigitMode digitMode = request.digitMode;
        final SpreadsheetNumberParsePatternMode mode = request.mode;
        final int max = this.max;
        int count = 0;

        while (cursor.isNotEmpty()) {
            final char c = cursor.at();
            final int digit = context.digit(c);
            if (-1 != digit) {
                request.digits.append(c);

                cursor.next();
                count++;
                if (count < max) {
                    continue;
                }
            }

            if (digitMode.isInteger() && mode.isGroupSeparator(c, context)) {
                request.addNumberIfNecessary();

                final String groupingText = Character.toString(c);
                request.add(
                    SpreadsheetFormulaParserToken.groupSeparatorSymbol(
                        groupingText,
                        groupingText
                    )
                );

                cursor.next();
                continue;
            }

            if (this.shouldHandleWhitespace() && Character.isWhitespace(c)) {
                request.addNumberIfNecessary();

                final String whitespaceText = Character.toString(c);
                request.add(
                    SpreadsheetFormulaParserToken.whitespace(
                        whitespaceText,
                        whitespaceText
                    )
                );

                cursor.next();
                continue;
            }

            // bad char!
            break;
        }
    }

    final SpreadsheetNumberParsePatternComponentDigitMode mode;

    private boolean shouldHandleWhitespace() {
        return this instanceof SpreadsheetNumberParsePatternComponentDigitSpace;
    }

    private final int max;
}

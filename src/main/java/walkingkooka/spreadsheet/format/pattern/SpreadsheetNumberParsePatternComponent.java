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

import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorSavePoint;

import java.util.function.BiFunction;

/**
 * An individual component with a larger pattern.
 * Each component parses one or more characters and possibly contributes values to the {@link Number}.
 */
abstract class SpreadsheetNumberParsePatternComponent {

    /**
     * {@see SpreadsheetNumberParsePatternComponentCurrency}
     */
    @SuppressWarnings("SameReturnValue")
    static SpreadsheetNumberParsePatternComponent currency() {
        return SpreadsheetNumberParsePatternComponentCurrency.INSTANCE;
    }

    /**
     * {@see SpreadsheetNumberParsePatternComponentDecimalSeparator}
     */
    @SuppressWarnings("SameReturnValue")
    static SpreadsheetNumberParsePatternComponent decimalSeparator() {
        return SpreadsheetNumberParsePatternComponentDecimalSeparator.INSTANCE;
    }

    /**
     * {@see SpreadsheetNumberParsePatternComponentDigitDigit}
     */
    static SpreadsheetNumberParsePatternComponent digit(final SpreadsheetNumberParsePatternComponentDigitMode mode,
                                                        final int max) {
        return SpreadsheetNumberParsePatternComponentDigitDigit.with(mode, max);
    }

    /**
     * {@see SpreadsheetNumberParsePatternComponentDigitSpace}
     */
    static SpreadsheetNumberParsePatternComponent digitSpace(final SpreadsheetNumberParsePatternComponentDigitMode mode,
                                                             final int max) {
        return SpreadsheetNumberParsePatternComponentDigitSpace.with(mode, max);
    }

    /**
     * {@see SpreadsheetNumberParsePatternComponentDigitZero}
     */
    static SpreadsheetNumberParsePatternComponent digitZero(final SpreadsheetNumberParsePatternComponentDigitMode mode,
                                                            final int max) {
        return SpreadsheetNumberParsePatternComponentDigitZero.with(mode, max);
    }

    /**
     * {@see SpreadsheetNumberParsePatternComponentExponent}
     */
    @SuppressWarnings("SameReturnValue")
    static SpreadsheetNumberParsePatternComponent exponent() {
        return SpreadsheetNumberParsePatternComponentExponent.INSTANCE;
    }

    /**
     * {@see SpreadsheetNumberParsePatternComponentGroupSeparator}
     */
    static SpreadsheetNumberParsePatternComponent groupSeparator() {
        return SpreadsheetNumberParsePatternComponentGroupSeparator.INSTANCE;
    }

    /**
     * {@see SpreadsheetNumberParsePatternComponentPercent}
     */
    @SuppressWarnings("SameReturnValue")
    static SpreadsheetNumberParsePatternComponent percentage() {
        return SpreadsheetNumberParsePatternComponentPercent.INSTANCE;
    }

    /**
     * {@see SpreadsheetNumberParsePatternComponentTextLiteral}
     */
    static SpreadsheetNumberParsePatternComponent textLiteral(final String text) {
        return SpreadsheetNumberParsePatternComponentTextLiteral.with(text);
    }

    /**
     * {@see SpreadsheetNumberParsePatternComponentWhitespace}
     */
    @SuppressWarnings("SameReturnValue")
    static SpreadsheetNumberParsePatternComponent whitespace(final int length) {
        return SpreadsheetNumberParsePatternComponentWhitespace.with(length);
    }

    /**
     * Package private to limit subclassing.
     */
    SpreadsheetNumberParsePatternComponent() {
        super();
    }

    /**
     * Called by {@link SpreadsheetNumberParsePatternSpreadsheetFormatParserTokenVisitor}.
     * Most digits only consume a single character, while the last will be greedy.
     */
    abstract SpreadsheetNumberParsePatternComponent lastDigit(final SpreadsheetNumberParsePatternComponentDigitMode mode);

    // used within Streams as a method reference
    final boolean isNotExpressionCompatible() {
        return this instanceof SpreadsheetNumberParsePatternComponentGroupSeparator ||
            this instanceof SpreadsheetNumberParsePatternComponentTextLiteral ||
            this instanceof SpreadsheetNumberParsePatternComponentWhitespace;
    }

    /**
     * Some tokens (group separator) are not valid within an expression but are valid as a number literal.
     */
    final boolean isExpressionCompatible() {
        return false == this.isNotExpressionCompatible();
    }

    /**
     * Each component in turn is asked to consume and possibly update the number value in the context.
     * A return value of false indicates matching was completed.
     */
    abstract boolean parse(final TextCursor cursor,
                           final SpreadsheetNumberParsePatternRequest request);

    /**
     * Advances the cursor attempting to match the given token in full.
     */
    final boolean parseToken(final TextCursor cursor,
                             final String token,
                             final CaseSensitivity caseSensitivity,
                             final BiFunction<String, String, SpreadsheetFormulaParserToken> factory,
                             final SpreadsheetNumberParsePatternComponentDigitMode mode,
                             final SpreadsheetNumberParsePatternRequest request) {
        final int length = token.length();
        final TextCursorSavePoint save = cursor.save();

        int i = 0;
        boolean completed = false;

        for (; ; ) {
            if (cursor.isEmpty()) {
                save.restore();
                break;
            }
            // failed!
            final char c = cursor.at();
            if (false == caseSensitivity.isEqual(c, token.charAt(i))) {
                save.restore();
                break;
            }
            cursor.next();
            i++;
            if (i == length) {
                final String text = save.textBetween()
                    .toString();
                request.add(
                    factory.apply(
                        text,
                        text
                    )
                );

                // not all components update the digitMode
                if (null != mode) {
                    request.setDigitMode(mode);
                }

                completed = request.nextComponent(cursor);
                break;
            }
        }

        return completed;
    }

    @Override
    public abstract String toString();
}

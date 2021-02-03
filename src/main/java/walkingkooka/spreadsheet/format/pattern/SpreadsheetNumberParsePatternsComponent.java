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

import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorSavePoint;

import java.util.function.BiFunction;

/**
 * An individual component with a larger pattern. Each component parses one or more characters and possibly contributes values to the {@link Number}.
 */
abstract class SpreadsheetNumberParsePatternsComponent {

    /**
     * {@see SpreadsheetNumberParsePatternsComponentCurrency}
     */
    @SuppressWarnings("SameReturnValue")
    static SpreadsheetNumberParsePatternsComponent currency() {
        return SpreadsheetNumberParsePatternsComponentCurrency.INSTANCE;
    }

    /**
     * {@see SpreadsheetNumberParsePatternsComponentDecimalSeparator}
     */
    @SuppressWarnings("SameReturnValue")
    static SpreadsheetNumberParsePatternsComponent decimalSeparator() {
        return SpreadsheetNumberParsePatternsComponentDecimalSeparator.INSTANCE;
    }

    /**
     * {@see SpreadsheetNumberParsePatternsComponentDigitDigit}
     */
    static SpreadsheetNumberParsePatternsComponent digit(final SpreadsheetNumberParsePatternsComponentDigitMode mode,
                                                         final int max) {
        return SpreadsheetNumberParsePatternsComponentDigitDigit.with(mode, max);
    }

    /**
     * {@see SpreadsheetNumberParsePatternsComponentDigitSpace}
     */
    static SpreadsheetNumberParsePatternsComponent digitSpace(final SpreadsheetNumberParsePatternsComponentDigitMode mode,
                                                              final int max) {
        return SpreadsheetNumberParsePatternsComponentDigitSpace.with(mode, max);
    }

    /**
     * {@see SpreadsheetNumberParsePatternsComponentDigitZero}
     */
    static SpreadsheetNumberParsePatternsComponent digitZero(final SpreadsheetNumberParsePatternsComponentDigitMode mode,
                                                             final int max) {
        return SpreadsheetNumberParsePatternsComponentDigitZero.with(mode, max);
    }

    /**
     * {@see SpreadsheetNumberParsePatternsComponentExponent}
     */
    @SuppressWarnings("SameReturnValue")
    static SpreadsheetNumberParsePatternsComponent exponent() {
        return SpreadsheetNumberParsePatternsComponentExponent.INSTANCE;
    }

    /**
     * {@see SpreadsheetNumberParsePatternsComponentPercent}
     */
    @SuppressWarnings("SameReturnValue")
    static SpreadsheetNumberParsePatternsComponent percentage() {
        return SpreadsheetNumberParsePatternsComponentPercent.INSTANCE;
    }

    /**
     * {@see SpreadsheetNumberParsePatternsComponentTextLiteral}
     */
    static SpreadsheetNumberParsePatternsComponent textLiteral(final String text) {
        return SpreadsheetNumberParsePatternsComponentTextLiteral.with(text);
    }

    /**
     * {@see SpreadsheetNumberParsePatternsComponentThousandsSeparator}
     */
    static SpreadsheetNumberParsePatternsComponent thousands() {
        return SpreadsheetNumberParsePatternsComponentThousandsSeparator.INSTANCE;
    }

    /**
     * {@see SpreadsheetNumberParsePatternsComponentWhitespace}
     */
    @SuppressWarnings("SameReturnValue")
    static SpreadsheetNumberParsePatternsComponent whitespace() {
        return SpreadsheetNumberParsePatternsComponentCurrency.INSTANCE;
    }

    /**
     * Package private to limit sub classing.
     */
    SpreadsheetNumberParsePatternsComponent() {
        super();
    }

    /**
     * Called by {@link SpreadsheetNumberParsePatternsSpreadsheetFormatParserTokenVisitor}.
     * Most digits only consume a single character, while the last will be greedy.
     */
    abstract SpreadsheetNumberParsePatternsComponent lastDigit(final SpreadsheetNumberParsePatternsComponentDigitMode mode);

    // used within Streams as a method reference
    final boolean isNotExpressionCompatible() {
        return !this.isExpressionCompatible();
    }

    /**
     * Some components (grouping separator) are not valid within an expression but are valid as a number literal.
     */
    abstract boolean isExpressionCompatible();

    /**
     * Each component in turn is asked to consume and possibly update the number value in the context.
     * A return value of false indicates matching was completed.
     */
    abstract boolean parse(final TextCursor cursor,
                           final SpreadsheetNumberParsePatternsRequest request);

    /**
     * Advances the cursor attempting to match the given token in full.
     */
    final boolean parseToken(final TextCursor cursor,
                          final String token,
                          final CaseSensitivity caseSensitivity,
                          final BiFunction<String, String, SpreadsheetParserToken> factory,
                          final SpreadsheetNumberParsePatternsComponentDigitMode mode,
                          final SpreadsheetNumberParsePatternsRequest request) {
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
            if (!caseSensitivity.isEqual(c, token.charAt(i))) {
                save.restore();
                break;
            }
            cursor.next();
            i++;
            if (i == length) {
                final String text = save.textBetween().toString();
                request.add(factory.apply(text, text));

                if(null != mode) {
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

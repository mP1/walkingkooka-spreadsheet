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
 * An individual component with a larger pattern. Each component parses one or more characters and possibly contributes values to the {@link Number}.
 */
abstract class SpreadsheetNumberParsePatternsComponent {

    /**
     * {@see SpreadsheetNumberParsePatternsComponentCurrency}
     */
    static SpreadsheetNumberParsePatternsComponent currency() {
        return SpreadsheetNumberParsePatternsComponentCurrency.INSTANCE;
    }

    /**
     * {@see SpreadsheetNumberParsePatternsComponentDecimalSeparator}
     */
    static SpreadsheetNumberParsePatternsComponent decimalSeparator() {
        return SpreadsheetNumberParsePatternsComponentDecimalSeparator.INSTANCE;
    }

    /**
     * {@see SpreadsheetNumberParsePatternsComponentDigitDigit}
     */
    static SpreadsheetNumberParsePatternsComponent digit(final int max) {
        return SpreadsheetNumberParsePatternsComponentDigitDigit.with(max);
    }

    /**
     * {@see SpreadsheetNumberParsePatternsComponentDigitSpace}
     */
    static SpreadsheetNumberParsePatternsComponent digitSpace(final int max) {
        return SpreadsheetNumberParsePatternsComponentDigitSpace.with(max);
    }

    /**
     * {@see SpreadsheetNumberParsePatternsComponentDigitZero}
     */
    static SpreadsheetNumberParsePatternsComponent digitZero(final int max) {
        return SpreadsheetNumberParsePatternsComponentDigitZero.with(max);
    }

    /**
     * {@see SpreadsheetNumberParsePatternsComponentExponent}
     */
    static SpreadsheetNumberParsePatternsComponent exponent() {
        return SpreadsheetNumberParsePatternsComponentExponent.INSTANCE;
    }

    /**
     * {@see SpreadsheetNumberParsePatternsComponentPercent}
     */
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
     * {@see SpreadsheetNumberParsePatternsComponentWhitespace}
     */
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
     * Called by {@link SpreadsheetNumberParsePatternsSpreadsheetFormatParserTokenVisitor}
     */
    abstract SpreadsheetNumberParsePatternsComponent lastDecimal();

    /**
     * Each component in turn is asked to consume and possibly update the number value in the context.
     */
    abstract void parse(final TextCursor cursor, final SpreadsheetNumberParsePatternsContext context);

    /**
     * Some components must be matched in order, while the {@link SpreadsheetNumberParsePatternsComponentDigit} are optional.
     */
    abstract boolean isRequired();

    /**
     * Advances the cursor attempting to match the given token in full.
     */
    final void parseToken(final TextCursor cursor,
                          final String token,
                          final SpreadsheetNumberParsePatternsContext context) {
        final int length = token.length();

        int i = 0;
        for (; ; ) {
            if (cursor.isEmpty()) {
                break;
            }
            // failed!
            if (cursor.at() != token.charAt(i)) {
                break;
            }
            cursor.next();
            i++;
            if (i == length) {
                context.nextComponent(cursor);
                break;
            }
        }
    }

    @Override
    public abstract String toString();
}

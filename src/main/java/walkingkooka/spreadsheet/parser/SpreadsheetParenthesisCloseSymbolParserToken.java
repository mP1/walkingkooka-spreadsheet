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
package walkingkooka.spreadsheet.parser;

/**
 * Represents a close / right parens symbol token.
 */
public final class SpreadsheetParenthesisCloseSymbolParserToken extends SpreadsheetNonBinaryOperandSymbolParserToken {

    static SpreadsheetParenthesisCloseSymbolParserToken with(final String value, final String text) {
        checkValueAndText(value, text);

        return new SpreadsheetParenthesisCloseSymbolParserToken(value, text);
    }

    private SpreadsheetParenthesisCloseSymbolParserToken(final String value, final String text) {
        super(value, text);
    }

    // isXXX............................................................................................................

    @Override
    public boolean isFunctionParameterSeparatorSymbol() {
        return false;
    }

    @Override
    public boolean isParenthesisCloseSymbol() {
        return true;
    }

    @Override
    public boolean isParenthesisOpenSymbol() {
        return false;
    }

    @Override
    public boolean isPercentSymbol() {
        return false;
    }

    @Override
    public boolean isWhitespace() {
        return false;
    }

    // SpreadsheetParserTokenVisitor....................................................................................

    @Override
    void accept(final SpreadsheetParserTokenVisitor visitor) {
        visitor.visit(this);
    }

    // Object...........................................................................................................

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof SpreadsheetParenthesisCloseSymbolParserToken;
    }
}

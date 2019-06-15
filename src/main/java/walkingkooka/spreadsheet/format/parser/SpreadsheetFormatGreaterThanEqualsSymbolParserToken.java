/*
 * Copyright 2018 Miroslav Pokorny (github.com/mP1)
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
 *
 */
package walkingkooka.spreadsheet.format.parser;

/**
 * Represents a greater than equals symbol token.
 */
public final class SpreadsheetFormatGreaterThanEqualsSymbolParserToken extends SpreadsheetFormatSymbolParserToken {

    static SpreadsheetFormatGreaterThanEqualsSymbolParserToken with(final String value, final String text) {
        checkValueAndText(value, text);

        return new SpreadsheetFormatGreaterThanEqualsSymbolParserToken(value, text);
    }

    private SpreadsheetFormatGreaterThanEqualsSymbolParserToken(final String value, final String text) {
        super(value, text);
    }

    // is...............................................................................................................

    @Override
    public boolean isBracketCloseSymbol() {
        return false;
    }

    @Override
    public boolean isBracketOpenSymbol() {
        return false;
    }

    @Override
    public boolean isColorLiteralSymbol() {
        return false;
    }

    @Override
    public boolean isEqualsSymbol() {
        return false;
    }

    @Override
    public boolean isExponentSymbol() {
        return false;
    }

    @Override
    public boolean isFractionSymbol() {
        return false;
    }

    @Override
    public boolean isGeneralSymbol() {
        return false;
    }

    @Override
    public boolean isGreaterThanSymbol() {
        return false;
    }

    @Override
    public boolean isGreaterThanEqualsSymbol() {
        return true;
    }

    @Override
    public boolean isLessThanSymbol() {
        return false;
    }

    @Override
    public boolean isLessThanEqualsSymbol() {
        return false;
    }

    @Override
    public boolean isNotEqualsSymbol() {
        return false;
    }

    @Override
    public boolean isPercentSymbol() {
        return false;
    }

    @Override
    public boolean isSeparatorSymbol() {
        return false;
    }

    @Override
    public boolean isWhitespace() {
        return false;
    }

    // SpreadsheetFormatParserTokenVisitor..............................................................................

    @Override
    public void accept(final SpreadsheetFormatParserTokenVisitor visitor) {
        visitor.visit(this);
    }

    // Object...........................................................................................................

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof SpreadsheetFormatGreaterThanEqualsSymbolParserToken;
    }

}

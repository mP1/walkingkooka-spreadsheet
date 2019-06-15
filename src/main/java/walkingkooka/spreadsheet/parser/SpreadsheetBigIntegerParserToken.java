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

import walkingkooka.tree.search.SearchNode;

import java.math.BigInteger;

/**
 * Holds a single {@link BigInteger} number.
 */
public final class SpreadsheetBigIntegerParserToken extends SpreadsheetNumericParserToken<BigInteger> {

    static SpreadsheetBigIntegerParserToken with(final BigInteger value, final String text) {
        checkValueAndText(value, text);

        return new SpreadsheetBigIntegerParserToken(value, text);
    }

    private SpreadsheetBigIntegerParserToken(final BigInteger value, final String text) {
        super(value, text);
    }

    @Override
    public boolean isBigDecimal() {
        return false;
    }

    @Override
    public boolean isBigInteger() {
        return true;
    }

    @Override
    public boolean isDouble() {
        return false;
    }

    @Override
    public boolean isLong() {
        return false;
    }

    @Override
    public boolean isSymbol() {
        return false;
    }

    @Override
    public boolean isText() {
        return false;
    }

    // SpreadsheetParserTokenVisitor....................................................................................

    @Override
    public void accept(final SpreadsheetParserTokenVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof SpreadsheetBigIntegerParserToken;
    }

    // HasSearchNode ...............................................................................................

    @Override
    public SearchNode toSearchNode() {
        return SearchNode.bigInteger(this.text(), this.value());
    }
}

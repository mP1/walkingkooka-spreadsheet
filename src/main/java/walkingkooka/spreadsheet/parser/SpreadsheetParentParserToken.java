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

import walkingkooka.text.cursor.parser.ParentParserToken;
import walkingkooka.text.cursor.parser.ParserToken;

import java.util.List;

/**
 * Base class for a token that contain another child token, with the class knowing the cardinality.
 */
abstract class SpreadsheetParentParserToken<T extends SpreadsheetParentParserToken> extends SpreadsheetParserToken
        implements ParentParserToken<T> {

    SpreadsheetParentParserToken(final List<ParserToken> value, final String text) {
        super(text);
        this.value = value;
    }

    @Override
    public final List<ParserToken> value() {
        return this.value;
    }

    final List<ParserToken> value;

    // isXXX............................................................................................................

    @Override
    public final boolean isBetweenSymbol() {
        return false;
    }

    @Override
    public final boolean isBigDecimal() {
        return false;
    }

    @Override
    public final boolean isBigInteger() {
        return false;
    }

    @Override
    public final boolean isColumnReference() {
        return false;
    }

    @Override
    public final boolean isDivideSymbol() {
        return false;
    }

    @Override
    public final boolean isDouble() {
        return false;
    }

    @Override
    public final boolean isEqualsSymbol() {
        return false;
    }

    @Override
    public final boolean isFunctionName() {
        return false;
    }

    @Override
    public final boolean isFunctionParameterSeparatorSymbol() {
        return false;
    }

    @Override
    public final boolean isGreaterThanSymbol() {
        return false;
    }

    @Override
    public final boolean isGreaterThanEqualsSymbol() {
        return false;
    }

    @Override
    public final boolean isLabelName() {
        return false;
    }

    @Override
    public final boolean isLessThanSymbol() {
        return false;
    }

    @Override
    public final boolean isLessThanEqualsSymbol() {
        return false;
    }

    @Override
    public final boolean isLocalDate() {
        return false;
    }

    @Override
    public final boolean isLocalDateTime() {
        return false;
    }

    @Override
    public final boolean isLocalTime() {
        return false;
    }

    @Override
    public final boolean isLong() {
        return false;
    }

    @Override
    public final boolean isMinusSymbol() {
        return false;
    }

    @Override
    public final boolean isMultiplySymbol() {
        return false;
    }

    @Override
    public final boolean isNotEqualsSymbol() {
        return false;
    }

    @Override
    public final boolean isParenthesisCloseSymbol() {
        return false;
    }

    @Override
    public final boolean isParenthesisOpenSymbol() {
        return false;
    }

    @Override
    public final boolean isPercentSymbol() {
        return false;
    }

    @Override
    public final boolean isPlusSymbol() {
        return false;
    }

    @Override
    public final boolean isPowerSymbol() {
        return false;
    }

    @Override
    public final boolean isRowReference() {
        return false;
    }

    @Override
    public final boolean isSymbol() {
        return false;
    }

    @Override
    public final boolean isText() {
        return false;
    }

    @Override
    public final boolean isWhitespace() {
        return false;
    }

    @Override
    final int operatorPriority() {
        return IGNORED;
    }

    @Override
    final SpreadsheetParserToken binaryOperand(final List<ParserToken> tokens, final String text) {
        throw new UnsupportedOperationException();
    }

    // SpreadsheetParserTokenVisitor....................................................................................

    final void acceptValues(final SpreadsheetParserTokenVisitor visitor) {
        for (ParserToken token : this.value()) {
            visitor.accept(token);
        }
    }
}

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

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberExpression;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetParentParserTokenTestCase<T extends SpreadsheetParentParserToken<T>> extends SpreadsheetParserTokenTestCase<T> {

    final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.DEFAULT;

    final static String NUMBER1 = "1";
    final static String NUMBER2 = "22";

    final static String WHITESPACE = "   ";

    SpreadsheetParentParserTokenTestCase() {
        super();
    }

    @Test
    public final void testWithNullTokensFails() {
        assertThrows(NullPointerException.class, () -> this.createToken(this.text(), Cast.<List<ParserToken>>to(null)));
    }

    @Test
    public final void testWithEmptyTokensFails() {
        assertThrows(IllegalArgumentException.class, () -> this.createToken(this.text(), Lists.empty()));
    }

    @Test
    public final void testWithCopiesTokens() {
        final List<ParserToken> tokens = this.tokens();
        final String text = this.text();
        final T token = this.createToken(text, tokens);
        this.textAndCheck(token, text);
        assertEquals(tokens, token.value(), "tokens");
        assertEquals(tokens, token.value(), "tokens not copied");
    }

    abstract T createToken(final String text, final List<ParserToken> tokens);

    final public T createToken(final String text) {
        return this.createToken(text, this.tokens());
    }

    final T createToken(final String text, final ParserToken... tokens) {
        return this.createToken(text, Lists.of(tokens));
    }

    abstract List<ParserToken> tokens();

    final void checkValue(final SpreadsheetParentParserToken<?> token, final ParserToken... value) {
        this.checkValue(token, Lists.of(value));
    }

    final void checkValue(final SpreadsheetParentParserToken<?> token, final List<ParserToken> value) {
        assertEquals(value, token.value(), "value");
    }

    final SpreadsheetMinusSymbolParserToken minusSymbol() {
        return SpreadsheetParserToken.minusSymbol("-", "-");
    }

    final SpreadsheetExpressionNumberParserToken number1() {
        return this.number(NUMBER1);
    }

    final SpreadsheetExpressionNumberParserToken number2() {
        return this.number(NUMBER2);
    }

    final SpreadsheetExpressionNumberParserToken number(final String value) {
        return SpreadsheetParserToken.expressionNumber(expressionNumber(value), value);
    }

    final SpreadsheetPercentSymbolParserToken percentSymbol() {
        return SpreadsheetParserToken.percentSymbol("%", "%");
    }

    final SpreadsheetWhitespaceParserToken whitespace() {
        return SpreadsheetParserToken.whitespace(WHITESPACE, WHITESPACE);
    }

    final SpreadsheetParenthesisOpenSymbolParserToken openParenthesisSymbol() {
        return SpreadsheetParserToken.parenthesisOpenSymbol("(", "(");
    }

    final SpreadsheetParenthesisCloseSymbolParserToken closeParenthesisSymbol() {
        return SpreadsheetParserToken.parenthesisCloseSymbol(")", ")");
    }

    final ExpressionNumber expressionNumber(final String value) {
        return this.expressionNumber(new BigDecimal(value));
    }

    final ExpressionNumber expressionNumber(final Number value) {
        return EXPRESSION_NUMBER_KIND.create(value);
    }

    final ExpressionNumberExpression expressionNumberExpression(final Number value) {
        return Expression.expressionNumber(this.expressionNumber(value));
    }
}

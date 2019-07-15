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
import walkingkooka.spreadsheet.SpreadsheetExpressionReference;
import walkingkooka.text.cursor.parser.ParserToken;

import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetParentParserTokenTestCase<T extends SpreadsheetParentParserToken<T>> extends SpreadsheetParserTokenTestCase<T> {

    final static String NUMBER1 = "1";
    final static String NUMBER2 = "22";

    final static String TEXT1 = "text-1";
    final static String TEXT2 = "text-2";

    final static String WHITESPACE = "   ";

    SpreadsheetParentParserTokenTestCase() {
        super();
    }

    @Test
    public final void testWithNullTokensFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createToken(this.text(), Cast.<List<ParserToken>>to(null));
        });
    }

    @Test
    public final void testWithEmptyTokensFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            this.createToken(this.text(), Lists.empty());
        });
    }

    @Test
    public final void testWithCopiesTokens() {
        final List<ParserToken> tokens = this.tokens();
        final String text = this.text();
        final T token = this.createToken(text, tokens);
        this.checkText(token, text);
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

    final void checkValue(final SpreadsheetParserToken token, final ParserToken... value) {
        this.checkValue(token, Lists.of(value));
    }

    final void checkValue(final SpreadsheetParserToken token, final List<ParserToken> value) {
        this.checkValue(SpreadsheetParentParserToken.class.cast(token), value);
    }

    final void checkValue(final SpreadsheetParentParserToken<?> token, final ParserToken... value) {
        this.checkValue(token, Lists.of(value));
    }

    final void checkValue(final SpreadsheetParentParserToken<?> token, final List<ParserToken> value) {
        assertEquals(value, token.value(), "value");
    }


    final SpreadsheetLabelNameParserToken label1() {
        return this.label("label1");
    }

    final SpreadsheetLabelNameParserToken label2() {
        return this.label("label2");
    }

    final SpreadsheetLabelNameParserToken label(final String text) {
        return SpreadsheetParserToken.labelName(SpreadsheetExpressionReference.labelName(text), text);
    }

    final SpreadsheetMinusSymbolParserToken minusSymbol() {
        return SpreadsheetParserToken.minusSymbol("-", "-");
    }

    final SpreadsheetBigIntegerParserToken number1() {
        return this.number(1);
    }

    final SpreadsheetBigIntegerParserToken number2() {
        return this.number(2);
    }

    final SpreadsheetBigIntegerParserToken number(final int value) {
        return SpreadsheetParserToken.bigInteger(BigInteger.valueOf(value), String.valueOf(value));
    }

    final SpreadsheetPercentSymbolParserToken percentSymbol() {
        return SpreadsheetParserToken.percentSymbol("%", "%");
    }

    final SpreadsheetWhitespaceParserToken whitespace() {
        return SpreadsheetParserToken.whitespace(WHITESPACE, WHITESPACE);
    }

    final SpreadsheetWhitespaceParserToken whitespace(final String text) {
        return SpreadsheetParserToken.whitespace(text, text);
    }

    final SpreadsheetTextParserToken text1() {
        return text(TEXT1);
    }

    final SpreadsheetTextParserToken text2() {
        return text(TEXT2);
    }

    final SpreadsheetTextParserToken text(final String text) {
        return SpreadsheetParserToken.text(text, '"' + text + '"');
    }

    final SpreadsheetParenthesisOpenSymbolParserToken openParenthesisSymbol() {
        return SpreadsheetParserToken.parenthesisOpenSymbol("(", "(");
    }

    final SpreadsheetParenthesisCloseSymbolParserToken closeParenthesisSymbol() {
        return SpreadsheetParserToken.parenthesisCloseSymbol(")", ")");
    }
}

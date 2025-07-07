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

package walkingkooka.spreadsheet.formula.parser;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class TextSpreadsheetFormulaParserTokenTest extends ValueSpreadsheetFormulaParserTokenTestCase<TextSpreadsheetFormulaParserToken> {

    @Test
    public void testWithZeroTokensFails() {
        this.createToken(ParentSpreadsheetFormulaParserTokenTestCase.DOUBLE_QUOTE + ParentSpreadsheetFormulaParserTokenTestCase.TEXT + ParentSpreadsheetFormulaParserTokenTestCase.DOUBLE_QUOTE);
    }

    @Test
    public void testWithExtraTextLiteralFails() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> TextSpreadsheetFormulaParserToken.with(
                Lists.of(
                    textLiteral(),
                    textLiteral()
                ),
                textLiteral().text() + textLiteral().text()
            )
        );

        this.checkEquals(
            "Extra text literal in \"abc123abc123\"",
            thrown.getMessage()
        );
    }

    @Test
    public void testWithApostropheMissingTextLiteral() {
        final String textValue = "";

        this.textValueAndCheck(
            this.createToken(
                ParentSpreadsheetFormulaParserTokenTestCase.APOSTROPHE + textValue,
                this.apostropheSymbol()
            ),
            textValue
        );
    }

    @Test
    public void testWithApostrophe() {
        final String text = ParentSpreadsheetFormulaParserTokenTestCase.APOSTROPHE + ParentSpreadsheetFormulaParserTokenTestCase.TEXT;

        final ApostropheSymbolSpreadsheetFormulaParserToken apostrophe = apostropheSymbol();
        final SpreadsheetFormulaParserToken textLiteral = this.textLiteral();

        final TextSpreadsheetFormulaParserToken token = this.createToken(text, apostrophe, textLiteral);
        this.textAndCheck(token, text);
        this.checkValue(token, apostrophe, textLiteral);

        this.textValueAndCheck(
            token,
            ParentSpreadsheetFormulaParserTokenTestCase.TEXT
        );
    }

    @Test
    public void testWithDoubleQuote() {
        final String text = ParentSpreadsheetFormulaParserTokenTestCase.DOUBLE_QUOTE + ParentSpreadsheetFormulaParserTokenTestCase.TEXT + ParentSpreadsheetFormulaParserTokenTestCase.DOUBLE_QUOTE;

        final DoubleQuoteSymbolSpreadsheetFormulaParserToken open = doubleQuoteSymbol();
        final SpreadsheetFormulaParserToken textLiteral = this.textLiteral();
        final DoubleQuoteSymbolSpreadsheetFormulaParserToken close = doubleQuoteSymbol();

        final TextSpreadsheetFormulaParserToken token = this.createToken(text, open, textLiteral, close);
        this.textAndCheck(token, text);
        this.checkValue(token, open, textLiteral, close);

        this.textValueAndCheck(
            token,
            ParentSpreadsheetFormulaParserTokenTestCase.TEXT
        );
    }

    @Test
    public void testWithEmptyDoubleQuote() {
        final String text = ParentSpreadsheetFormulaParserTokenTestCase.DOUBLE_QUOTE + ParentSpreadsheetFormulaParserTokenTestCase.DOUBLE_QUOTE;

        final DoubleQuoteSymbolSpreadsheetFormulaParserToken open = doubleQuoteSymbol();
        final DoubleQuoteSymbolSpreadsheetFormulaParserToken close = doubleQuoteSymbol();

        final TextSpreadsheetFormulaParserToken token = this.createToken(
            text,
            open,
            close
        );
        this.textAndCheck(token, text);
        this.checkValue(
            token,
            open,
            close
        );

        this.textValueAndCheck(
            token,
            ""
        );
    }

    private void textValueAndCheck(final TextSpreadsheetFormulaParserToken token,
                                   final String textValue) {
        this.checkEquals(
            textValue,
            token.textValue()
        );
    }

    @Test
    public void testToExpressionApostrophe() {
        this.toExpressionAndCheck(
            TextSpreadsheetFormulaParserToken.with(
                Lists.of(
                    apostropheSymbol(),
                    textLiteral()
                ),
                ParentSpreadsheetFormulaParserTokenTestCase.APOSTROPHE + ParentSpreadsheetFormulaParserTokenTestCase.TEXT
            ),
            Expression.value(ParentSpreadsheetFormulaParserTokenTestCase.TEXT)
        );
    }

    @Test
    public void testToExpressionDoubleQuoted() {
        this.toExpressionAndCheck(
            TextSpreadsheetFormulaParserToken.with(
                Lists.of(
                    doubleQuoteSymbol(),
                    textLiteral(),
                    doubleQuoteSymbol()
                ),
                ParentSpreadsheetFormulaParserTokenTestCase.DOUBLE_QUOTE + ParentSpreadsheetFormulaParserTokenTestCase.TEXT + ParentSpreadsheetFormulaParserTokenTestCase.DOUBLE_QUOTE
            ),
            Expression.value(ParentSpreadsheetFormulaParserTokenTestCase.TEXT)
        );
    }

    @Override
    TextSpreadsheetFormulaParserToken createToken(final String text, final List<ParserToken> tokens) {
        return SpreadsheetFormulaParserToken.text(tokens, text);
    }

    @Override
    public String text() {
        return ParentSpreadsheetFormulaParserTokenTestCase.DOUBLE_QUOTE + ParentSpreadsheetFormulaParserTokenTestCase.TEXT + ParentSpreadsheetFormulaParserTokenTestCase.DOUBLE_QUOTE;
    }

    @Override
    List<ParserToken> tokens() {
        return Lists.of(
            doubleQuoteSymbol(),
            textLiteral(),
            doubleQuoteSymbol()
        );
    }

    @Override
    public TextSpreadsheetFormulaParserToken createDifferentToken() {
        final String different = "different456";
        return this.createToken(different,
            doubleQuoteSymbol(),
            SpreadsheetFormulaParserToken.textLiteral(different, different),
            doubleQuoteSymbol()
        );
    }

    @Override
    public Class<TextSpreadsheetFormulaParserToken> type() {
        return TextSpreadsheetFormulaParserToken.class;
    }

    @Override
    public TextSpreadsheetFormulaParserToken unmarshall(final JsonNode from,
                                                        final JsonNodeUnmarshallContext context) {
        return SpreadsheetFormulaParserToken.unmarshallText(from, context);
    }
}

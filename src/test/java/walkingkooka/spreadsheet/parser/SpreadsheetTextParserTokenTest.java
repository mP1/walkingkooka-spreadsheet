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
import walkingkooka.collect.list.Lists;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;

public final class SpreadsheetTextParserTokenTest extends SpreadsheetValueParserTokenTestCase<SpreadsheetTextParserToken> {

    @Test
    public void testWithZeroTokensFails() {
        this.createToken(DOUBLE_QUOTE + TEXT + DOUBLE_QUOTE);
    }

    @Test
    public void testWithApostrophe() {
        final String text = APOSTROPHE + TEXT;

        final SpreadsheetApostropheSymbolParserToken apostrophe = apostropheSymbol();
        final SpreadsheetParserToken textLiteral = this.textLiteral();

        final SpreadsheetTextParserToken token = this.createToken(text, apostrophe, textLiteral);
        this.textAndCheck(token, text);
        this.checkValue(token, apostrophe, textLiteral);
    }

    @Test
    public void testWithDoubleQuote() {
        final String text = DOUBLE_QUOTE + TEXT + DOUBLE_QUOTE;

        final SpreadsheetDoubleQuoteSymbolParserToken open = doubleQuoteSymbol();
        final SpreadsheetParserToken textLiteral = this.textLiteral();
        final SpreadsheetDoubleQuoteSymbolParserToken close = doubleQuoteSymbol();

        final SpreadsheetTextParserToken token = this.createToken(text, open, textLiteral, close);
        this.textAndCheck(token, text);
        this.checkValue(token, open, textLiteral, close);
    }

    @Test
    public void testToExpressionApostrophe() {
        this.toExpressionAndCheck(
                SpreadsheetTextParserToken.with(
                        Lists.of(
                                apostropheSymbol(),
                                textLiteral()
                        ),
                        APOSTROPHE + TEXT
                ),
                Expression.value(TEXT)
        );
    }

    @Test
    public void testToExpressionDoubleQuoted() {
        this.toExpressionAndCheck(
                SpreadsheetTextParserToken.with(
                        Lists.of(
                                doubleQuoteSymbol(),
                                textLiteral(),
                                doubleQuoteSymbol()
                        ),
                        DOUBLE_QUOTE + TEXT + DOUBLE_QUOTE
                ),
                Expression.value(TEXT)
        );
    }

    @Override
    SpreadsheetTextParserToken createToken(final String text, final List<ParserToken> tokens) {
        return SpreadsheetParserToken.text(tokens, text);
    }

    @Override
    public String text() {
        return DOUBLE_QUOTE + TEXT + DOUBLE_QUOTE;
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
    public SpreadsheetTextParserToken createDifferentToken() {
        final String different = "different456";
        return this.createToken(different,
                doubleQuoteSymbol(),
                SpreadsheetParserToken.textLiteral(different, different),
                doubleQuoteSymbol()
        );
    }

    @Override
    public Class<SpreadsheetTextParserToken> type() {
        return SpreadsheetTextParserToken.class;
    }

    @Override
    public SpreadsheetTextParserToken unmarshall(final JsonNode from,
                                                 final JsonNodeUnmarshallContext context) {
        return SpreadsheetParserToken.unmarshallText(from, context);
    }
}

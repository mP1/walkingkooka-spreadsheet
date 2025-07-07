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

package walkingkooka.spreadsheet.formula;

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.text.cursor.parser.ParserTesting2;

public final class SpreadsheetDoubleQuotesParserTest implements ParserTesting2<SpreadsheetDoubleQuotesParser, SpreadsheetParserContext>,
    ToStringTesting<SpreadsheetDoubleQuotesParser> {

    @Test
    public void testParseNotDoubleQuote() {
        this.parseFailAndCheck("not a double-quoted string/text");
    }

    @Test
    public void testParseUnclosed() {
        this.parseFailAndCheck("\"");
    }

    @Test
    public void testParseUnclosed2() {
        this.parseFailAndCheck("\"a");
    }

    @Test
    public void testParseUnclosed3() {
        this.parseFailAndCheck("\"abc");
    }

    @Test
    public void testParseEmptyDoubleQuotedText() {
        this.parseAndCheck2("");
    }

    @Test
    public void testParseSingleCharacter() {
        this.parseAndCheck2("a");
    }

    @Test
    public void testParseSingleCharacter2() {
        this.parseAndCheck2("b");
    }

    @Test
    public void testParseSeveralCharacters() {
        this.parseAndCheck2("abc");
    }

    @Test
    public void testParseDoubleQuote() {
        this.parseAndCheck2("\"\"");
    }

    @Test
    public void testParseIncludesDoubleQuote() {
        this.parseAndCheck2("abc\"\"123");
    }

    @Test
    public void testParseIncludesDoubleQuote2() {
        this.parseAndCheck2("abc\"\"123\"\"xyz");
    }

    @Test
    public void testParseIncludesDoubleQuote3() {
        this.parseAndCheck2("abc\"\"\"\"xyz");
    }

    private void parseAndCheck2(final String content) {
        this.parseAndCheck3(content, "");
        this.parseAndCheck3(content, "*");
    }

    private void parseAndCheck3(final String content,
                                final String after) {
        final String quotes = "" + SpreadsheetDoubleQuotesParser.DOUBLE_QUOTE;
        final String withQuotes = quotes + content + quotes;

        this.parseAndCheck(
            withQuotes + after,
            SpreadsheetFormulaParserToken.text(
                Lists.of(
                    SpreadsheetDoubleQuotesParser.DOUBLE_QUOTE_TOKEN,
                    SpreadsheetFormulaParserToken.textLiteral(
                        content.replace(quotes + quotes, quotes),
                        content
                    ),
                    SpreadsheetDoubleQuotesParser.DOUBLE_QUOTE_TOKEN
                ),
                withQuotes
            ),
            withQuotes,
            after
        );
    }

    @Test
    public void testMinCount() {
        this.minCountAndCheck(
            1
        );
    }

    @Test
    public void testMaxCount() {
        this.maxCountAndCheck(
            1
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createParser(), "Text");
    }

    @Override
    public SpreadsheetDoubleQuotesParser createParser() {
        return SpreadsheetDoubleQuotesParser.INSTANCE;
    }

    @Override
    public SpreadsheetParserContext createContext() {
        return SpreadsheetParserContexts.fake();
    }

    @Override
    public Class<SpreadsheetDoubleQuotesParser> type() {
        return SpreadsheetDoubleQuotesParser.class;
    }
}

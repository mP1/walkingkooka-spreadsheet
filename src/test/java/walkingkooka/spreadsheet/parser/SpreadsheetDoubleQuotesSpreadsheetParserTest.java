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
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;

public final class SpreadsheetDoubleQuotesSpreadsheetParserTest implements SpreadsheetParserTesting2<SpreadsheetDoubleQuotesSpreadsheetParser>,
        ToStringTesting<SpreadsheetDoubleQuotesSpreadsheetParser> {

    @Test
    public void testNotDoubleQuote() {
        this.parseFailAndCheck("not a double quoted string/text");
    }

    @Test
    public void testUnclosed() {
        this.parseFailAndCheck("\"");
    }

    @Test
    public void testUnclosed2() {
        this.parseFailAndCheck("\"a");
    }

    @Test
    public void testUnclosed3() {
        this.parseFailAndCheck("\"abc");
    }

    @Test
    public void testEmptyDoubleQuotedText() {
        this.parseAndCheck2("");
    }

    @Test
    public void testSingleCharacter() {
        this.parseAndCheck2("a");
    }

    @Test
    public void testSingleCharacter2() {
        this.parseAndCheck2("b");
    }

    @Test
    public void testSeveralCharacters() {
        this.parseAndCheck2("abc");
    }

    @Test
    public void testDoubleQuote() {
        this.parseAndCheck2("\"\"");
    }

    @Test
    public void testIncludesDoubleQuote() {
        this.parseAndCheck2("abc\"\"123");
    }

    @Test
    public void testIncludesDoubleQuote2() {
        this.parseAndCheck2("abc\"\"123\"\"xyz");
    }

    @Test
    public void testIncludesDoubleQuote3() {
        this.parseAndCheck2("abc\"\"\"\"xyz");
    }

    private void parseAndCheck2(final String content) {
        this.parseAndCheck3(content, "");
        this.parseAndCheck3(content, "*");
    }

    private void parseAndCheck3(final String content,
                                final String after) {
        final String quotes = "" + SpreadsheetDoubleQuotesSpreadsheetParser.DOUBLE_QUOTE;
        final String withQuotes = quotes + content + quotes;

        this.parseAndCheck(
                withQuotes + after,
                SpreadsheetParserToken.text(
                        Lists.of(
                                SpreadsheetDoubleQuotesSpreadsheetParser.DOUBLE_QUOTE_TOKEN,
                                SpreadsheetParserToken.textLiteral(
                                        content.replace(quotes + quotes, quotes),
                                        content
                                ),
                                SpreadsheetDoubleQuotesSpreadsheetParser.DOUBLE_QUOTE_TOKEN
                        ),
                        withQuotes
                ),
                withQuotes,
                after
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createParser(), "Text");
    }

    @Override
    public SpreadsheetDoubleQuotesSpreadsheetParser createParser() {
        return SpreadsheetDoubleQuotesSpreadsheetParser.INSTANCE;
    }

    @Override
    public SpreadsheetParserContext createContext() {
        return SpreadsheetParserContexts.fake();
    }

    @Override
    public Class<SpreadsheetDoubleQuotesSpreadsheetParser> type() {
        return SpreadsheetDoubleQuotesSpreadsheetParser.class;
    }
}

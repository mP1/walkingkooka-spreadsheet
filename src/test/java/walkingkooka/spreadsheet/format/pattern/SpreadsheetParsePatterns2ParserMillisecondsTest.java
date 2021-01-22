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

package walkingkooka.spreadsheet.format.pattern;

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.parser.FakeSpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetMillisecondParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;

public final class SpreadsheetParsePatterns2ParserMillisecondsTest extends SpreadsheetParsePatterns2ParserTestCase<SpreadsheetParsePatterns2ParserMilliseconds> {

    @Test
    public void testParseFails() {
        this.parseFailAndCheck("!");
    }

    @Test
    public void testParseFailsDecimalPoint() {
        this.parseFailAndCheck(".");
    }

    @Test
    public void testParseFailsNonDigit() {
        this.parseFailAndCheck("A");
    }

    @Test
    public void testParse0() {
        this.parseAndCheck2(
                "0",
                0
        );
    }

    @Test
    public void testParse1() {
        this.parseAndCheck2(
                "1",
                100 * 1000
        );
    }

    @Test
    public void testParse12() {
        this.parseAndCheck2(
                "12",
                120 * 1000
        );
    }

    @Test
    public void testParse123() {
        this.parseAndCheck2(
                "123",
                123 * 1000
        );
    }

    @Test
    public void testParse1234() {
        this.parseAndCheck2(
                "1234",
                1234 * 100
        );
    }

    @Test
    public void testParse12345() {
        this.parseAndCheck2(
                "12345",
                12345 * 10
        );
    }
    @Test
    public void testParse123456() {
        this.parseAndCheck2(
                "123456",
                123456 * 1
        );
    }

    @Test
    public void testParse1234567() {
        this.parseAndCheck2(
                "1234567",
                123457
        );
    }

    @Test
    public void testParse0000000() {
        this.parseAndCheck2(
                "0000000",
                0
        );
    }

    @Test
    public void testParse0000001() {
        this.parseAndCheck2(
                "0000001",
                0
        );
    }

    @Test
    public void testParse0000005() {
        this.parseAndCheck2(
                "0000005",
                1 // round up
        );
    }

    private void parseAndCheck2(final String text,
                               final int value) {
        final SpreadsheetMillisecondParserToken token = SpreadsheetParserToken.millisecond(value, text);

        this.parseAndCheck(
                text,
                token,
                text,
                ""
        );

        final String after = "!";
        this.parseAndCheck(
                text + after,
                token,
                text,
                after
        );
    }

    private final static String PATTERN = "abc-pattern-ignored-not-checked-or-used-in-anyway";

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createParser(), PATTERN);
    }

    @Override
    public SpreadsheetParsePatterns2ParserMilliseconds createParser() {
        return SpreadsheetParsePatterns2ParserMilliseconds.with(PATTERN);
    }

    @Override
    public SpreadsheetParserContext createContext() {
        return SpreadsheetParserContexts.fake();
    }

    @Override
    public Class<SpreadsheetParsePatterns2ParserMilliseconds> type() {
        return SpreadsheetParsePatterns2ParserMilliseconds.class;
    }

    @Override
    public String typeNameSuffix() {
        return "Milliseconds";
    }
}

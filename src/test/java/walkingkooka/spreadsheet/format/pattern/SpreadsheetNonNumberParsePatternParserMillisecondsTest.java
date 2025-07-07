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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.spreadsheet.formula.parser.MillisecondSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.parser.FakeSpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;

public final class SpreadsheetNonNumberParsePatternParserMillisecondsTest extends SpreadsheetNonNumberParsePatternParserTestCase<SpreadsheetNonNumberParsePatternParserMilliseconds>
    implements HashCodeEqualsDefinedTesting2<SpreadsheetNonNumberParsePatternParserMilliseconds> {

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
    public void testParse100000000() {
        this.parseAndCheck2(
            "1",
            100_000_000
        );
    }

    @Test
    public void testParse120000000() {
        this.parseAndCheck2(
            "12",
            120_000_000
        );
    }

    @Test
    public void testParse123000000() {
        this.parseAndCheck2(
            "123",
            123_000_000
        );
    }

    @Test
    public void testParse123400000() {
        this.parseAndCheck2(
            "1234",
            123_400_000
        );
    }

    @Test
    public void testParse123450000() {
        this.parseAndCheck2(
            "12345",
            123_450_000
        );
    }

    @Test
    public void testParse123456000() {
        this.parseAndCheck2(
            "123456",
            123_456_000
        );
    }

    @Test
    public void testParse1234567055RoundLastDigit() {
        this.parseAndCheck2(
            "1234567055",
            123_456_706
        );
    }

    @Test
    public void testParse000000000() {
        this.parseAndCheck2(
            "000000000",
            0
        );
    }

    @Test
    public void testParse0000000001() {
        this.parseAndCheck2(
            "0000000001",
            0
        );
    }

    @Test
    public void testParse0000000005() {
        this.parseAndCheck2(
            "0000000005",
            1 // round up
        );
    }

    private void parseAndCheck2(final String text,
                                final int value) {
        final MillisecondSpreadsheetFormulaParserToken token = SpreadsheetFormulaParserToken.millisecond(value, text);

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
    public void testParseWithArabicDecimalNumberContext() {
        final String text = arabicDigits(123);

        this.parseAndCheck(
            SpreadsheetNonNumberParsePatternParserMilliseconds.with(PATTERN),
            this.createContext(ARABIC_ZERO_DIGIT),
            text,
            SpreadsheetFormulaParserToken.millisecond(123_000_000, text),
            text,
            "" // textAfter
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createParser(), PATTERN);
    }

    @Override
    public SpreadsheetNonNumberParsePatternParserMilliseconds createParser() {
        return SpreadsheetNonNumberParsePatternParserMilliseconds.with(PATTERN);
    }

    @Override
    public SpreadsheetParserContext createContext() {
        return this.createContext('0');
    }

    private SpreadsheetParserContext createContext(final char zeroDigit) {
        return new FakeSpreadsheetParserContext() {
            @Override
            public char zeroDigit() {
                return zeroDigit;
            }
        };
    }

    // hashcode/equals..................................................................................................

    @Test
    public void testEqualsDifferentPattern() {
        this.checkNotEquals(
            SpreadsheetNonNumberParsePatternParserMilliseconds.with("sssss")
        );
    }

    @Override
    public SpreadsheetNonNumberParsePatternParserMilliseconds createObject() {
        return SpreadsheetNonNumberParsePatternParserMilliseconds.with("sss");
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetNonNumberParsePatternParserMilliseconds> type() {
        return SpreadsheetNonNumberParsePatternParserMilliseconds.class;
    }

    @Override
    public String typeNameSuffix() {
        return "Milliseconds";
    }
}

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
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.parser.FakeSpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;

public final class SpreadsheetNonNumberParsePatternParserDecimalSeparatorTest extends SpreadsheetNonNumberParsePatternParserTestCase<SpreadsheetNonNumberParsePatternParserDecimalSeparator> {

    @Test
    public void testParseFails() {
        this.parseFailAndCheck("!");
    }

    @Test
    public void testParseFailsDecimalPoint() {
        this.parseFailAndCheck(".");
    }

    @Test
    public void testParse() {
        this.parseAndCheck(
            "d",
            SpreadsheetFormulaParserToken.decimalSeparatorSymbol("d", "d"),
            "d"
        );
    }

    @Test
    public void testParseSingleCharacterOnly() {
        final String after = "123";
        this.parseAndCheck(
            "d" + after,
            SpreadsheetFormulaParserToken.decimalSeparatorSymbol("d", "d"),
            "d",
            after
        );
    }

    @Test
    public void testParseDifferentContextDecimalSeparator() {
        final char c = '%';
        final SpreadsheetParserContext context = this.createContext(c);

        final String s = "" + c;
        this.parseAndCheck(
            this.createParser(),
            context,
            s,
            SpreadsheetFormulaParserToken.decimalSeparatorSymbol(s, s),
            s
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createParser(), ".");
    }

    @Override
    public SpreadsheetNonNumberParsePatternParserDecimalSeparator createParser() {
        return SpreadsheetNonNumberParsePatternParserDecimalSeparator.decimalSeparator();
    }

    @Override
    public SpreadsheetParserContext createContext() {
        return this.createContext('d');
    }

    private SpreadsheetParserContext createContext(final char c) {
        return new FakeSpreadsheetParserContext() {
            @Override
            public char decimalSeparator() {
                return c;
            }
        };
    }

    @Override
    public Class<SpreadsheetNonNumberParsePatternParserDecimalSeparator> type() {
        return SpreadsheetNonNumberParsePatternParserDecimalSeparator.class;
    }

    @Override
    public String typeNameSuffix() {
        return "DecimalSeparator";
    }
}

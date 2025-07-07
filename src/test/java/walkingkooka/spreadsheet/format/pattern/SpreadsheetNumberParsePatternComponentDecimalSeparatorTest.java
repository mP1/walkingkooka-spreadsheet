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

public final class SpreadsheetNumberParsePatternComponentDecimalSeparatorTest extends SpreadsheetNumberParsePatternComponentTestCase2<SpreadsheetNumberParsePatternComponentDecimalSeparator> {

    @Test
    public void testDecimal() {
        this.checkEquals(DECIMAL, 'd');

        this.parseAndCheck2(
            DECIMAL,
            ""
        );
    }

    @Test
    public void testDecimalCharacter() {
        this.parseAndCheck2(
            DECIMAL,
            "!"
        );
    }

    @Test
    public void testDecimalTwice() {
        this.parseAndCheck2(
            DECIMAL,
            DECIMAL + ""
        );
    }

    @Test
    public void testDecimalTwiceCharacter() {
        this.parseAndCheck2(
            DECIMAL,
            DECIMAL + "!"
        );
    }

    void parseAndCheck2(final char c,
                        final String textAfter) {
        final String text = "" + c;
        this.parseAndCheck2(
            text,
            textAfter,
            SpreadsheetNumberParsePatternComponentDigitMode.DECIMAL_FIRST,
            NEXT_CALLED,
            SpreadsheetFormulaParserToken.decimalSeparatorSymbol(text, text));
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createComponent(), ".");
    }

    @Override
    SpreadsheetNumberParsePatternComponentDecimalSeparator createComponent() {
        return SpreadsheetNumberParsePatternComponentDecimalSeparator.INSTANCE;
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetNumberParsePatternComponentDecimalSeparator> type() {
        return SpreadsheetNumberParsePatternComponentDecimalSeparator.class;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNameSuffix() {
        return "DecimalSeparator";
    }
}

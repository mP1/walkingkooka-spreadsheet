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

public final class SpreadsheetNumberParsePatternComponentExponentTest extends SpreadsheetNumberParsePatternComponentTestCase2<SpreadsheetNumberParsePatternComponentExponent> {

    @Test
    public void testIncomplete() {
        this.parseFails(EXPONENT.substring(0, 1));
    }

    @Test
    public void testIncomplete2() {
        this.parseFails(EXPONENT.charAt(0) + "!");
    }

    @Test
    public void testIncomplete3() {
        this.parseFails(EXPONENT.substring(0, 2));
    }

    @Test
    public void testIncomplete4() {
        this.parseFails(EXPONENT.substring(0, 2) + "!");
    }

    @Test
    public void testToken1() {
        this.checkEquals(EXPONENT.toUpperCase(), EXPONENT);

        this.parseAndCheck2(
            EXPONENT,
            ""
        );
    }

    @Test
    public void testToken2() {
        this.checkEquals(EXPONENT.toUpperCase(), EXPONENT);

        this.parseAndCheck2(
            EXPONENT,
            "!"
        );
    }

    @Test
    public void testToken3() {
        this.checkNotEquals(EXPONENT.toLowerCase(), EXPONENT);

        this.parseAndCheck2(
            EXPONENT.toLowerCase(),
            ""
        );
    }

    @Test
    public void testToken4() {
        this.checkNotEquals(EXPONENT.toLowerCase(), EXPONENT);

        this.parseAndCheck2(
            EXPONENT.toLowerCase(),
            "!"
        );
    }

    @Test
    public void testCaseUnimportant() {
        this.parseAndCheck2(
            EXPONENT.toLowerCase().charAt(0) + EXPONENT.toUpperCase().substring(1),
            ""
        );
    }

    void parseAndCheck2(final String text,
                        final String textAfter) {
        this.parseAndCheck2(
            text,
            textAfter,
            NEXT_CALLED,
            SpreadsheetFormulaParserToken.exponentSymbol(text, text)
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createComponent(), "E");
    }

    @Override
    SpreadsheetNumberParsePatternComponentExponent createComponent() {
        return SpreadsheetNumberParsePatternComponentExponent.INSTANCE;
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetNumberParsePatternComponentExponent> type() {
        return SpreadsheetNumberParsePatternComponentExponent.class;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNameSuffix() {
        return "Exponent";
    }
}

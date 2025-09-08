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

public final class SpreadsheetNumberParsePatternComponentPercentTest extends SpreadsheetNumberParsePatternComponentTestCase2<SpreadsheetNumberParsePatternComponentPercent> {

    @Test
    public void testToken1() {
        this.parseAndCheck2(
            PERCENT,
            ""
        );
    }

    @Test
    public void testToken2() {
        this.parseAndCheck2(
            PERCENT,
            "!"
        );
    }

    @Test
    public void testTokenTwice() {
        this.parseAndCheck2(
            PERCENT,
            PERCENT + "!"
        );
    }

    void parseAndCheck2(final char c,
                        final String textAfter) {
        final String textString = "" + c;
        this.parseAndCheck2(
            textString,
            textAfter,
            NEXT_CALLED,
            SpreadsheetFormulaParserToken.percentSymbol(textString, textString)
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createComponent(),
            "%"
        );
    }

    @Override
    SpreadsheetNumberParsePatternComponentPercent createComponent() {
        return SpreadsheetNumberParsePatternComponentPercent.INSTANCE;
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetNumberParsePatternComponentPercent> type() {
        return SpreadsheetNumberParsePatternComponentPercent.class;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNameSuffix() {
        return "Percent";
    }
}

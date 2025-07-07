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

public final class SpreadsheetNumberParsePatternComponentTextLiteralTest extends SpreadsheetNumberParsePatternComponentTestCase2<SpreadsheetNumberParsePatternComponentTextLiteral> {

    private final static String TOKEN = "ghi";

    @Test
    public void testIncompleteFails() {
        this.parseFails(
            "gh"
        );
    }

    @Test
    public void testEqualsDifferentCaseFails() {
        this.parseFails(
            TOKEN.toUpperCase()
        );
    }

    @Test
    public void testMatchingCase() {
        final String text = "ghi";
        this.parseAndCheck2(
            text,
            TOKEN,
            NEXT_CALLED,
            SpreadsheetFormulaParserToken.textLiteral(
                text,
                text
            )
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createComponent(), TOKEN);
    }

    @Override
    SpreadsheetNumberParsePatternComponentTextLiteral createComponent() {
        return SpreadsheetNumberParsePatternComponentTextLiteral.with(TOKEN);
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetNumberParsePatternComponentTextLiteral> type() {
        return SpreadsheetNumberParsePatternComponentTextLiteral.class;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNameSuffix() {
        return "TextLiteral";
    }
}

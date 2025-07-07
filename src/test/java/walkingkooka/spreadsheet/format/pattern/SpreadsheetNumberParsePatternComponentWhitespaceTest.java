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

public final class SpreadsheetNumberParsePatternComponentWhitespaceTest extends SpreadsheetNumberParsePatternComponentTestCase2<SpreadsheetNumberParsePatternComponentWhitespace> {

    @Test
    public void testNonWhitespaceFails() {
        this.parseFails("failed!");
    }

    @Test
    public void testInsufficientSpaceFails() {
        this.parseFails(
            SpreadsheetNumberParsePatternComponentWhitespace.with(3),
            " a"
        );
    }

    @Test
    public void testSpace() {
        final String text = " ";
        this.parseAndCheck2(
            text,
            "A",
            NEXT_CALLED,
            SpreadsheetFormulaParserToken.whitespace(
                text,
                text
            )
        );
    }

    @Test
    public void testSpaceSpace() {
        final String text = "  ";

        this.parseAndCheck2(
            SpreadsheetNumberParsePatternComponentWhitespace.with(2),
            text, // text
            "A", // textAfter
            this.createRequest(true),
            NEXT_CALLED,
            SpreadsheetFormulaParserToken.whitespace(
                text,
                text
            )
        );
    }

    @Test
    public void testTab() {
        final String text = "\t";

        this.parseAndCheck2(
            text,
            "A",
            NEXT_CALLED,
            SpreadsheetFormulaParserToken.whitespace(
                text,
                text
            )
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createComponent(),
            " "
        );
    }

    @Test
    public void testToString3() {
        this.toStringAndCheck(
            SpreadsheetNumberParsePatternComponentWhitespace.with(3),
            "   "
        );
    }

    @Override
    SpreadsheetNumberParsePatternComponentWhitespace createComponent() {
        return SpreadsheetNumberParsePatternComponentWhitespace.with(1);
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetNumberParsePatternComponentWhitespace> type() {
        return SpreadsheetNumberParsePatternComponentWhitespace.class;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNameSuffix() {
        return "Whitespace";
    }
}

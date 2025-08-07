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
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.spreadsheet.parser.SpreadsheetParserTesting2;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

public final class SpreadsheetLabelNameSpreadsheetParserTest implements SpreadsheetParserTesting2<SpreadsheetLabelNameSpreadsheetParser>,
    ClassTesting2<SpreadsheetLabelNameSpreadsheetParser>,
    ToStringTesting<SpreadsheetLabelNameSpreadsheetParser> {

    @Test
    public void testParseWrongFirstCharFail() {
        this.parseFailAndCheck("1");
    }

    @Test
    public void testParseColumnOnly() {
        this.parseAndCheck2("A");
    }

    @Test
    public void testParseColumnAndRowFail() {
        this.parseFailAndCheck("A1");
    }

    @Test
    public void testParseColumnAndRowFail2() {
        this.parseFailAndCheck("AA11");
    }

    @Test
    public void testParseAbsoluteColumnAndRowFail() {
        this.parseFailAndCheck("$A1");
    }

    @Test
    public void testParseColumnAndAbsoluteRowFail() {
        this.parseFailAndCheck("A$1");
    }

    @Test
    public void testParseMaxColumn() {
        // A1 column+row
        this.parseAndCheck2(
            "A" + SpreadsheetSelection.MAX_ROW + 1
        );
    }

    @Test
    public void testParseMaxRow() {
        // A1 column+row
        this.parseAndCheck2("XFE1");
    }

    @Test
    public void testParseLabel() {
        this.parseAndCheck2("Hello");
    }

    @Test
    public void testParseLabel2() {
        this.parseAndCheck2("Hello", "...");
    }

    @Test
    public void testParseLabel3() {
        this.parseAndCheck2("Hello_");
    }

    @Test
    public void testParseLabel4() {
        this.parseAndCheck2("Hello_", "...");
    }

    @Test
    public void testParseLabel5() {
        this.parseAndCheck2("Hello123");
    }

    @Test
    public void testParseLabel6() {
        this.parseAndCheck2("Hello123", "...");
    }

    private void parseAndCheck2(final String text) {
        this.parseAndCheck2(text, "");
    }

    private void parseAndCheck2(final String text,
                                final String textAfter) {
        this.parseAndCheck(
            text + textAfter,
            SpreadsheetFormulaParserToken.label(
                SpreadsheetSelection.labelName(text),
                text
            ),
            text,
            textAfter
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

    @Override
    public SpreadsheetLabelNameSpreadsheetParser createParser() {
        return SpreadsheetLabelNameSpreadsheetParser.INSTANCE;
    }

    @Override
    public SpreadsheetParserContext createContext() {
        return SpreadsheetParserContexts.fake();
    }

    // tokens...........................................................................................................

    @Test
    public void testTokens() {
        this.tokensAndCheck(
            this.createContext()
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            SpreadsheetLabelNameSpreadsheetParser.INSTANCE,
            "LABEL"
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetLabelNameSpreadsheetParser> type() {
        return SpreadsheetLabelNameSpreadsheetParser.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}

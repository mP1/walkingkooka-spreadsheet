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
import walkingkooka.InvalidCharacterException;
import walkingkooka.spreadsheet.formula.parser.ColumnSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;

public final class SpreadsheetColumnReferenceSpreadsheetParserTest extends SpreadsheetColumnOrRowReferenceSpreadsheetParserTestCase<SpreadsheetColumnReferenceSpreadsheetParser> {

    private final static int A_VALUE = 1;
    private final static String A_TEXT = "A";

    private final static int AD_VALUE = 26 + 4;
    private final static String AD_TEXT = "AD";

    private final static String TEXT_AFTER = "...";

    private final static String INVALID = "XFE";

    @Test
    public void testParseInvalidFails() {
        this.parseFailAndCheck("123");
    }

    @Test
    public void testParseDollarFails() {
        this.parseFailAndCheck("$");
    }

    @Test
    public void testParseDollarRowFails() {
        final String text = "$123";

        this.parseThrows(
            text,
            new InvalidCharacterException(
                text,
                1
            ).getMessage()
        );
    }

    @Test
    public void testParseRelativeReference() {
        this.parseAndCheck2(A_TEXT, SpreadsheetReferenceKind.RELATIVE, A_VALUE);
    }

    @Test
    public void testParseRelativeReference1() {
        this.parseAndCheck2(A_TEXT, SpreadsheetReferenceKind.RELATIVE, A_VALUE, TEXT_AFTER);
    }

    @Test
    public void testParseRelativeReference2() {
        this.parseAndCheck2(AD_TEXT, SpreadsheetReferenceKind.RELATIVE, AD_VALUE);
    }

    @Test
    public void testParseRelativeReference3() {
        this.parseAndCheck2(AD_TEXT, SpreadsheetReferenceKind.RELATIVE, AD_VALUE, TEXT_AFTER);
    }

    @Test
    public void testParseRelativeReferenceLowerCase() {
        this.parseAndCheck2(AD_TEXT.toLowerCase(), SpreadsheetReferenceKind.RELATIVE, AD_VALUE, TEXT_AFTER);
    }

    @Test
    public void testParseAbsoluteReference() {
        this.parseAndCheck2("$" + AD_TEXT, SpreadsheetReferenceKind.ABSOLUTE, AD_VALUE);
    }

    @Test
    public void testParseAbsoluteReference1() {
        this.parseAndCheck2("$" + AD_TEXT, SpreadsheetReferenceKind.ABSOLUTE, AD_VALUE, TEXT_AFTER);
    }

    @Test
    public void testParseAbsoluteReference2() {
        this.parseAndCheck2("$" + AD_TEXT, SpreadsheetReferenceKind.ABSOLUTE, AD_VALUE);
    }

    @Test
    public void testParseAbsoluteReference3() {
        this.parseAndCheck2("$" + AD_TEXT, SpreadsheetReferenceKind.ABSOLUTE, AD_VALUE, TEXT_AFTER);
    }

    @Test
    public void testParseAbsoluteReferenceLowerCase() {
        this.parseAndCheck2("$" + AD_TEXT.toLowerCase(), SpreadsheetReferenceKind.ABSOLUTE, AD_VALUE, TEXT_AFTER);
    }

    @Test
    public void testParseRange() {
        this.parseAndCheck2("A", SpreadsheetReferenceKind.RELATIVE, A_VALUE, ":B");
    }

    @Test
    public void testParseMax() {
        this.parseAndCheck(
            "XFD",
            ColumnSpreadsheetFormulaParserToken.column(SpreadsheetReferenceKind.RELATIVE.lastColumn(), "XFD"),
            "XFD"
        );
    }

    @Test
    public void testParseRelativeReferenceInvalid() {
        this.parseThrows(
            "" + INVALID,
            "Invalid column \"XFE\" not between \"A\" and \"XFD\""
        );
    }

    @Test
    public void testParseAbsoluteReferenceInvalid() {
        this.parseThrows(
            "$" + INVALID,
            "Invalid column \"$XFE\" not between \"A\" and \"XFD\""
        );
    }

    private void parseAndCheck2(final String text, final SpreadsheetReferenceKind referenceKind, final int column) {
        this.parseAndCheck2(text, referenceKind, column, "");
    }

    private void parseAndCheck2(final String text, final SpreadsheetReferenceKind referenceKind, final int column, final String textAfter) {
        this.parseAndCheck(text + textAfter, this.token(referenceKind, column, text), text, textAfter);
    }

    private ColumnSpreadsheetFormulaParserToken token(final SpreadsheetReferenceKind referenceKind, final int column, final String text) {
        return SpreadsheetFormulaParserToken.column(referenceKind.column(column), text);
    }

    @Override
    public SpreadsheetColumnReferenceSpreadsheetParser createParser() {
        return SpreadsheetColumnReferenceSpreadsheetParser.INSTANCE;
    }

    @Override
    public Class<SpreadsheetColumnReferenceSpreadsheetParser> type() {
        return SpreadsheetColumnReferenceSpreadsheetParser.class;
    }
}

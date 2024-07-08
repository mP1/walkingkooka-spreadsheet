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

package walkingkooka.spreadsheet.parser;

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;

public final class SpreadsheetColumnReferenceParserTest extends SpreadsheetColumnOrRowReferenceParserTestCase<SpreadsheetColumnReferenceParser> {

    private final static int A_VALUE = 0;
    private final static String A_TEXT = "A";

    private final static int AD_VALUE = 26 + 3;
    private final static String AD_TEXT = "AD";

    private final static String TEXT_AFTER = "...";

    private final static String INVALID = "XFE";

    @Test
    public void testInvalidFails() {
        this.parseFailAndCheck("123");
    }

    @Test
    public void testDollarFails() {
        this.parseFailAndCheck("$");
    }

    @Test
    public void testDollarFails2() {
        this.parseFailAndCheck("$123");
    }

    @Test
    public void testRelativeReference() {
        this.parseAndCheck2(A_TEXT, SpreadsheetReferenceKind.RELATIVE, A_VALUE);
    }

    @Test
    public void testRelativeReference1() {
        this.parseAndCheck2(A_TEXT, SpreadsheetReferenceKind.RELATIVE, A_VALUE, TEXT_AFTER);
    }

    @Test
    public void testRelativeReference2() {
        this.parseAndCheck2(AD_TEXT, SpreadsheetReferenceKind.RELATIVE, AD_VALUE);
    }

    @Test
    public void testRelativeReference3() {
        this.parseAndCheck2(AD_TEXT, SpreadsheetReferenceKind.RELATIVE, AD_VALUE, TEXT_AFTER);
    }

    @Test
    public void testRelativeReferenceLowerCase() {
        this.parseAndCheck2(AD_TEXT.toLowerCase(), SpreadsheetReferenceKind.RELATIVE, AD_VALUE, TEXT_AFTER);
    }

    @Test
    public void testAbsoluteReference() {
        this.parseAndCheck2("$" + AD_TEXT, SpreadsheetReferenceKind.ABSOLUTE, AD_VALUE);
    }

    @Test
    public void testAbsoluteReference1() {
        this.parseAndCheck2("$" + AD_TEXT, SpreadsheetReferenceKind.ABSOLUTE, AD_VALUE, TEXT_AFTER);
    }

    @Test
    public void testAbsoluteReference2() {
        this.parseAndCheck2("$" + AD_TEXT, SpreadsheetReferenceKind.ABSOLUTE, AD_VALUE);
    }

    @Test
    public void testAbsoluteReference3() {
        this.parseAndCheck2("$" + AD_TEXT, SpreadsheetReferenceKind.ABSOLUTE, AD_VALUE, TEXT_AFTER);
    }

    @Test
    public void testAbsoluteReferenceLowerCase() {
        this.parseAndCheck2("$" + AD_TEXT.toLowerCase(), SpreadsheetReferenceKind.ABSOLUTE, AD_VALUE, TEXT_AFTER);
    }

    @Test
    public void testRange() {
        this.parseAndCheck2("A", SpreadsheetReferenceKind.RELATIVE, A_VALUE, ":B");
    }

    @Test
    public void testMax() {
        this.parseAndCheck(
                "XFD",
                SpreadsheetColumnReferenceParserToken.columnReference(SpreadsheetReferenceKind.RELATIVE.lastColumn(), "XFD"),
                "XFD"
        );
    }

    @Test
    public void testRelativeReferenceInvalid() {
        this.parseThrows("" + INVALID, "Invalid column value 16384 expected between 0 and 16384");
    }

    @Test
    public void testAbsoluteReferenceInvalid() {
        this.parseThrows("$" + INVALID, "Invalid column value 16384 expected between 0 and 16384");
    }

    private void parseAndCheck2(final String text, final SpreadsheetReferenceKind referenceKind, final int column) {
        this.parseAndCheck2(text, referenceKind, column, "");
    }

    private void parseAndCheck2(final String text, final SpreadsheetReferenceKind referenceKind, final int column, final String textAfter) {
        this.parseAndCheck(text + textAfter, this.token(referenceKind, column, text), text, textAfter);
    }

    private SpreadsheetColumnReferenceParserToken token(final SpreadsheetReferenceKind referenceKind, final int column, final String text) {
        return SpreadsheetParserToken.columnReference(referenceKind.column(column), text);
    }

    @Override
    public SpreadsheetColumnReferenceParser createParser() {
        return SpreadsheetColumnReferenceParser.INSTANCE;
    }

    @Override
    public Class<SpreadsheetColumnReferenceParser> type() {
        return SpreadsheetColumnReferenceParser.class;
    }
}

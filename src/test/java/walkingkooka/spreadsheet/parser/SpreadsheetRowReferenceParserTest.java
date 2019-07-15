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
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;

public final class SpreadsheetRowReferenceParserTest extends SpreadsheetParserTestCase<SpreadsheetRowReferenceParser, SpreadsheetRowReferenceParserToken> {

    private final static String ROW_TEXT = "1";
    private final static int ROW_VALUE = 0;

    private final static String ROW2_TEXT = "123";
    private final static int ROW2_VALUE = 122;

    @Test
    public void testInvalidFails() {
        this.parseFailAndCheck("ABC");
    }

    @Test
    public void testDollarFails() {
        this.parseFailAndCheck("$");
    }

    @Test
    public void testDollarFails2() {
        this.parseFailAndCheck("$ABC");
    }

    @Test
    public void testRelativeReference() {
        this.parseAndCheck2(ROW_TEXT, SpreadsheetReferenceKind.RELATIVE, ROW_VALUE);
    }

    @Test
    public void testRelativeReference1() {
        this.parseAndCheck2(ROW_TEXT, SpreadsheetReferenceKind.RELATIVE, ROW_VALUE, "...");
    }

    @Test
    public void testRelativeReference2() {
        this.parseAndCheck2(ROW2_TEXT, SpreadsheetReferenceKind.RELATIVE, ROW2_VALUE);
    }

    @Test
    public void testRelativeReference3() {
        this.parseAndCheck2(ROW2_TEXT, SpreadsheetReferenceKind.RELATIVE, ROW2_VALUE, "...");
    }

    @Test
    public void testAbsoluteReference() {
        this.parseAndCheck2("$1", SpreadsheetReferenceKind.ABSOLUTE, ROW_VALUE);
    }

    @Test
    public void testAbsoluteReference1() {
        this.parseAndCheck2("$1", SpreadsheetReferenceKind.ABSOLUTE, ROW_VALUE, "...");
    }

    @Test
    public void testAbsoluteReference2() {
        this.parseAndCheck2("$" + ROW2_TEXT, SpreadsheetReferenceKind.ABSOLUTE, ROW2_VALUE);
    }

    @Test
    public void testAbsoluteReference3() {
        this.parseAndCheck2("$" + ROW2_TEXT, SpreadsheetReferenceKind.ABSOLUTE, ROW2_VALUE, "...");
    }

    @Test
    public void testRelativeReferenceInvalid() {
        final int value = SpreadsheetRowReference.MAX + 1;
        this.parseThrows("" + value, "Invalid column value 1048576 expected between 0 and 1048576");
    }

    @Test
    public void testAbsoluteReferenceInvalid() {
        final int value = SpreadsheetRowReference.MAX + 1;
        this.parseThrows("$" + value, "Invalid column value 1048576 expected between 0 and 1048576");
    }

    private void parseAndCheck2(final String text, final SpreadsheetReferenceKind referenceKind, final int row) {
        this.parseAndCheck2(text, referenceKind, row, "");
    }

    private void parseAndCheck2(final String text, final SpreadsheetReferenceKind referenceKind, final int row, final String textAfter) {
        this.parseAndCheck(text + textAfter, this.token(referenceKind, row, text), text, textAfter);
    }

    private SpreadsheetRowReferenceParserToken token(final SpreadsheetReferenceKind referenceKind, final int row, final String text) {
        return SpreadsheetParserToken.rowReference(referenceKind.row(row), text);
    }

    @Override
    public SpreadsheetRowReferenceParser createParser() {
        return SpreadsheetRowReferenceParser.INSTANCE;
    }

    @Override
    public Class<SpreadsheetRowReferenceParser> type() {
        return SpreadsheetRowReferenceParser.class;
    }
}

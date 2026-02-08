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
import walkingkooka.spreadsheet.formula.parser.RowSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

public final class SpreadsheetRowReferenceSpreadsheetParserTest extends SpreadsheetColumnOrRowReferenceSpreadsheetParserTestCase<SpreadsheetRowReferenceSpreadsheetParser> {

    private final static String ROW_TEXT = "1";
    private final static int ROW_VALUE = 1;

    private final static String ROW2_TEXT = "123";
    private final static int ROW2_VALUE = 123;

    @Test
    public void testParseInvalidFails() {
        this.parseFailAndCheck("ABC");
    }

    @Test
    public void testParseDollarFails() {
        this.parseFailAndCheck("$");
    }

    @Test
    public void testParseDollarColumnFails() {
        final String text = "$ABC";

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
        this.parseAndCheck2(
            ROW_TEXT,
            SpreadsheetReferenceKind.RELATIVE,
            ROW_VALUE
        );
    }

    @Test
    public void testParseRelativeReference1() {
        this.parseAndCheck2(
            ROW_TEXT,
            SpreadsheetReferenceKind.RELATIVE,
            ROW_VALUE,
            "..."
        );
    }

    @Test
    public void testParseRelativeReference2() {
        this.parseAndCheck2(
            ROW2_TEXT,
            SpreadsheetReferenceKind.RELATIVE,
            ROW2_VALUE
        );
    }

    @Test
    public void testParseRelativeReference3() {
        this.parseAndCheck2(
            ROW2_TEXT,
            SpreadsheetReferenceKind.RELATIVE,
            ROW2_VALUE,
            "..."
        );
    }

    @Test
    public void testParseAbsoluteReference() {
        this.parseAndCheck2(
            "$1",
            SpreadsheetReferenceKind.ABSOLUTE,
            ROW_VALUE
        );
    }

    @Test
    public void testParseAbsoluteReference1() {
        this.parseAndCheck2(
            "$1",
            SpreadsheetReferenceKind.ABSOLUTE,
            ROW_VALUE,
            "..."
        );
    }

    @Test
    public void testParseAbsoluteReference2() {
        this.parseAndCheck2(
            "$" + ROW2_TEXT,
            SpreadsheetReferenceKind.ABSOLUTE,
            ROW2_VALUE
        );
    }

    @Test
    public void testParseAbsoluteReference3() {
        this.parseAndCheck2(
            "$" + ROW2_TEXT,
            SpreadsheetReferenceKind.ABSOLUTE,
            ROW2_VALUE,
            "..."
        );
    }

    @Test
    public void testParseRelativeReferenceInvalid() {
        final int value = SpreadsheetSelection.MAX_ROW + 1;
        this.parseThrows(
            "" + value,
            "Invalid row=1048577 not between 1 and 1048576"
        );
    }

    @Test
    public void testParseAbsoluteReferenceInvalid() {
        final int value = SpreadsheetSelection.MAX_ROW + 1;
        this.parseThrows(
            "$" + value,
            "Invalid row=1048577 not between 1 and 1048576"
        );
    }

    @Test
    public void testParseRange() {
        this.parseAndCheck2(
            "2",
            SpreadsheetReferenceKind.RELATIVE,
            2,
            ":34"
        );
    }

    @Test
    public void testParseMaxValue() {
        this.parseAndCheck2(
            "1048576",
            SpreadsheetReferenceKind.RELATIVE,
            SpreadsheetSelection.MAX_ROW
        );
    }

    private void parseAndCheck2(final String text,
                                final SpreadsheetReferenceKind referenceKind,
                                final int row) {
        this.parseAndCheck2(
            text,
            referenceKind,
            row,
            ""
        );
    }

    private void parseAndCheck2(final String text,
                                final SpreadsheetReferenceKind referenceKind,
                                final int row,
                                final String textAfter) {
        this.parseAndCheck(text + textAfter,
            this.token(referenceKind, row, text),
            text,
            textAfter
        );
    }

    private RowSpreadsheetFormulaParserToken token(final SpreadsheetReferenceKind referenceKind,
                                                   final int row,
                                                   final String text) {
        return SpreadsheetFormulaParserToken.row(
            referenceKind.row(row),
            text
        );
    }

    @Override
    public SpreadsheetRowReferenceSpreadsheetParser createParser() {
        return SpreadsheetRowReferenceSpreadsheetParser.INSTANCE;
    }

    @Override
    public Class<SpreadsheetRowReferenceSpreadsheetParser> type() {
        return SpreadsheetRowReferenceSpreadsheetParser.class;
    }
}

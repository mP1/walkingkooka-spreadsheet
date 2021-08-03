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

package walkingkooka.spreadsheet.reference;

import org.junit.jupiter.api.Test;
import walkingkooka.InvalidCharacterException;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.CharSequences;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetSelectionTest implements ClassTesting2<SpreadsheetSelection>,
        ParseStringTesting<SpreadsheetExpressionReference> {

    // isCellReferenceText..............................................................................................

    @Test
    public void testIsCellReferenceTextNullFails() {
        assertThrows(NullPointerException.class, () -> SpreadsheetExpressionReference.isCellReferenceText(null));
    }

    @Test
    public void testIsCellReferenceTextEmptyFalse() {
        this.isCellReferenceTextAndCheck("", false);
    }

    @Test
    public void testIsCellReferenceTextInvalidCharacterFalse() {
        this.isCellReferenceTextAndCheck("!", false);
    }

    @Test
    public void testIsCellReferenceTextAbsolutePrefixOnlyFalse() {
        this.isCellReferenceTextAndCheck("$", false);
    }

    @Test
    public void testIsCellReferenceTextAbsoluteColumnOnlyFalse() {
        this.isCellReferenceTextAndCheck("$A", false);
    }

    @Test
    public void testIsCellReferenceTextColumnOnlyFalse() {
        this.isCellReferenceTextAndCheck("A", false);
    }

    @Test
    public void testIsCellReferenceTextAbsoluteRowOnlyFalse() {
        this.isCellReferenceTextAndCheck("$9", false);
    }

    @Test
    public void testIsCellReferenceTextRowOnlyFalse() {
        this.isCellReferenceTextAndCheck("9", false);
    }

    @Test
    public void testIsCellReferenceTextRowOnlyFalse2() {
        this.isCellReferenceTextAndCheck("98", false);
    }

    @Test
    public void testIsCellReferenceTextWithCellReferenceUppercase() {
        this.isCellReferenceTextAndCheck("A1", true);
    }

    @Test
    public void testIsCellReferenceTextWithCellReferenceUppercaseAbsolute() {
        this.isCellReferenceTextAndCheck("$A1", true);
    }

    @Test
    public void testIsCellReferenceTextWithCellReferenceLowercase() {
        this.isCellReferenceTextAndCheck("a1", true);
    }

    @Test
    public void testIsCellReferenceTextWithCellReferenceLowercaseExtra() {
        this.isCellReferenceTextAndCheck("a1!", false);
    }

    @Test
    public void testIsCellReferenceTextWithCellReferenceLowercaseAbsolute() {
        this.isCellReferenceTextAndCheck("$a1", true);
    }

    @Test
    public void testIsCellReferenceTextAbsoluteColumnAbsoluteRow() {
        this.isCellReferenceTextAndCheck("$a$1", true);
    }

    @Test
    public void testIsCellReferenceTextAbsoluteColumnAbsoluteRowExtra() {
        this.isCellReferenceTextAndCheck("$a$1!", false);
    }

    @Test
    public void testIsCellReferenceTextLastColumn() {
        this.isCellReferenceTextAndCheck("XFD1", true);
    }

    @Test
    public void testIsCellReferenceTextLastColumnPlus1() {
        this.isCellReferenceTextAndCheck("XFE1", false);
    }

    @Test
    public void testIsCellReferenceTextLastRow() {
        this.isCellReferenceTextAndCheck("A1048576", true);
    }

    @Test
    public void testIsCellReferenceTextLastRowPlus1() {
        this.isCellReferenceTextAndCheck("A1048577", false);
    }

    @Test
    public void testIsCellReferenceTextWithLabel() {
        this.isCellReferenceTextAndCheck("LABEL123", false);
    }

    private void isCellReferenceTextAndCheck(final String text, final boolean expected) {
        assertEquals(expected,
                SpreadsheetExpressionReference.isCellReferenceText(text),
                () -> "isCellReferenceText " + CharSequences.quoteAndEscape(text));
        if (expected) {
            SpreadsheetExpressionReference.parseExpressionReference(text);
        }
    }

    // parseCell.......................................................................................................

    @Test
    public void testParseCell() {
        assertEquals(SpreadsheetExpressionReference.parseCell("A1"),
                SpreadsheetExpressionReference.cell(SpreadsheetSelection.parseColumn("A"),
                        SpreadsheetSelection.parseRow("1")));
    }

    // parseColumn......................................................................................................

    @Test
    public void testParseColumnWithRangeFails() {
        assertThrows(IllegalArgumentException.class, () -> SpreadsheetSelection.parseColumn("B:C"));
    }

    @Test
    public void testParseColumn() {
        assertEquals(
                SpreadsheetSelection.parseColumn("B"),
                SpreadsheetReferenceKind.RELATIVE.column(1)
        );
    }

    // parseColumnReference...............................................................................................

    @Test
    public void testParseColumnReference() {
        assertEquals(
                SpreadsheetSelection.parseColumn("B")
                        .spreadsheetColumnRange(SpreadsheetSelection.parseColumn("D")),
                SpreadsheetExpressionReference.parseColumnRange("B:D")
        );
    }

    @Test
    public void testParseColumnReferenceSingleton() {
        assertEquals(
                SpreadsheetSelection.parseColumn("B")
                        .toSpreadsheetColumnReferenceRange(),
                SpreadsheetExpressionReference.parseColumnRange("B")
        );
    }
    
    // ParseString...............................................................................................

    @Test
    public void testParseStringCellReferenceUpperCaseRelativeRelative() {
        final String reference = "A2";
        this.parseStringAndCheck(
                reference,
                SpreadsheetReferenceKind.RELATIVE.column(0).setRow(SpreadsheetReferenceKind.RELATIVE.row(1))
        );
    }

    @Test
    public void testParseStringCellReferenceUpperCaseRelativeAbsolute() {
        final String reference = "C$4";
        this.parseStringAndCheck(
                reference,
                SpreadsheetReferenceKind.RELATIVE.column(2).setRow(SpreadsheetReferenceKind.ABSOLUTE.row(3))
        );
    }

    @Test
    public void testParseStringCellReferenceUpperCaseAbsoluteRelative() {
        final String reference = "$E6";
        this.parseStringAndCheck(
                reference,
                SpreadsheetReferenceKind.ABSOLUTE.column(4).setRow(SpreadsheetReferenceKind.RELATIVE.row(5))
        );
    }

    @Test
    public void testParseStringCellReferenceUpperCaseAbsoluteAbsolute() {
        final String reference = "$G$8";
        this.parseStringAndCheck(
                reference,
                SpreadsheetReferenceKind.ABSOLUTE.column(6).setRow(SpreadsheetReferenceKind.ABSOLUTE.row(7))
        );
    }

    @Test
    public void testParseStringCellReferenceLowercaseRelativeRelative() {
        final String reference = "i10";
        this.parseStringAndCheck(
                reference,
                SpreadsheetReferenceKind.RELATIVE.column(8).setRow(SpreadsheetReferenceKind.RELATIVE.row(9))
        );
    }

    @Test
    public void testParseStringCellReferenceLowercaseAbsolute() {
        final String reference = "$k12";
        this.parseStringAndCheck(
                reference,
                SpreadsheetReferenceKind.ABSOLUTE.column(10).setRow(SpreadsheetReferenceKind.RELATIVE.row(11))
        );
    }

    @Test
    public void testParseStringLabel() {
        final String label = "label123";
        this.parseStringAndCheck(label, SpreadsheetExpressionReference.labelName(label));
    }

    @Test
    public void testParseStringRange() {
        final String range = "A2:B2";
        this.parseStringAndCheck(range, SpreadsheetExpressionReference.parseCellRange(range));
    }


    // parseCellOrLabelName....................................................................................

    @Test
    public void testParseCellOrLabelNameNullFails() {
        parseCellOrLabelNameFails(
                null,
                NullPointerException.class
        );
    }

    @Test
    public void testParseCellOrLabelNameEmptyFails() {
        parseCellOrLabelNameFails(
                "",
                IllegalArgumentException.class
        );
    }

    @Test
    public void testParseCellOrLabelNameRangeFails() {
        parseCellOrLabelNameFails(
                SpreadsheetExpressionReference.parseCellRange("A1:B2").toString(),
                InvalidCharacterException.class
        );
    }

    private void parseCellOrLabelNameFails(final String text,
                                           final Class<? extends RuntimeException> thrown) {
        assertThrows(thrown, () -> SpreadsheetCellReferenceOrLabelName.parseCellOrLabelName(text));
    }

    @Test
    public void testParseCellOrLabelNameCell() {
        final String text = "A1";
        this.parseCellOrLabelNameAndCheck(text, SpreadsheetCellReference.parseCell(text));
    }

    @Test
    public void testParseCellOrLabelNameLabel() {
        final String text = "Label123";
        this.parseCellOrLabelNameAndCheck(text, SpreadsheetCellReference.labelName(text));
    }

    private void parseCellOrLabelNameAndCheck(final String text,
                                              final SpreadsheetCellReferenceOrLabelName expected) {
        final SpreadsheetCellReferenceOrLabelName parsed = SpreadsheetCellReferenceOrLabelName.parseCellOrLabelName(text);
        assertEquals(
                expected,
                parsed,
                () -> "Parsing of " + CharSequences.quoteAndEscape(text) + " failed"
        );
    }

    // parseRow.........................................................................................................

    @Test
    public void testParseRowWithRangeFails() {
        assertThrows(IllegalArgumentException.class, () -> SpreadsheetSelection.parseRow("12:3"));
    }

    @Test
    public void testParseRow() {
        assertEquals(
                SpreadsheetReferenceKind.RELATIVE.row(2 - 1),
                SpreadsheetSelection.parseRow("2")
        );
    }

    @Test
    public void testParseRow2() {
        assertEquals(
                SpreadsheetReferenceKind.RELATIVE.row(23 - 1),
                SpreadsheetSelection.parseRow("23")
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetSelection> type() {
        return SpreadsheetSelection.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // ParseStringTesting...............................................................................................

    @Override
    public SpreadsheetExpressionReference parseString(final String text) {
        return SpreadsheetExpressionReference.parseExpressionReference(text);
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(Class<? extends RuntimeException> throwing) {
        return throwing;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException expected) {
        return expected;
    }
}

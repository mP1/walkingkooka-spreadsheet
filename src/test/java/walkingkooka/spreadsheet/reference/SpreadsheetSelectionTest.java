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
import walkingkooka.collect.set.Sets;
import walkingkooka.net.UrlFragment;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.CharSequences;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetSelectionTest implements ClassTesting2<SpreadsheetSelection>,
        ParseStringTesting<SpreadsheetExpressionReference> {

    @Test
    public void testAllCells() {
        this.checkEquals(
                "A1:XFD1048576",
                SpreadsheetSelection.ALL_CELLS.toString()
        );
    }

    @Test
    public void testAllColumns() {
        this.checkEquals(
                "A:XFD",
                SpreadsheetSelection.ALL_COLUMNS.toString()
        );
    }

    @Test
    public void testAllRows() {
        this.checkEquals(
                "1:1048576",
                SpreadsheetSelection.ALL_ROWS.toString()
        );
    }

    // isCellText........................................................................................................

    @Test
    public void testIsCellTextNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetExpressionReference.isCellText(null)
        );
    }

    @Test
    public void testIsCellTextEmptyFalse() {
        this.isCellTextAndCheck("", false);
    }

    @Test
    public void testIsCellTextInvalidCharacterFalse() {
        this.isCellTextAndCheck("!", false);
    }

    @Test
    public void testIsCellTextAbsolutePrefixOnlyFalse() {
        this.isCellTextAndCheck("$", false);
    }

    @Test
    public void testIsCellTextAbsoluteColumnOnlyFalse() {
        this.isCellTextAndCheck("$A", false);
    }

    @Test
    public void testIsCellTextColumnOnlyFalse() {
        this.isCellTextAndCheck("A", false);
    }

    @Test
    public void testIsCellTextAbsoluteRowOnlyFalse() {
        this.isCellTextAndCheck("$9", false);
    }

    @Test
    public void testIsCellTextRowOnlyFalse() {
        this.isCellTextAndCheck("9", false);
    }

    @Test
    public void testIsCellTextRowOnlyFalse2() {
        this.isCellTextAndCheck("98", false);
    }

    @Test
    public void testIsCellTextWithCellReferenceUppercase() {
        this.isCellTextAndCheck("A1", true);
    }

    @Test
    public void testIsCellTextWithCellReferenceUppercaseAbsolute() {
        this.isCellTextAndCheck("$A1", true);
    }

    @Test
    public void testIsCellTextWithCellReferenceLowercase() {
        this.isCellTextAndCheck("a1", true);
    }

    @Test
    public void testIsCellTextWithCellReferenceLowercaseExtra() {
        this.isCellTextAndCheck("a1!", false);
    }

    @Test
    public void testIsCellTextWithCellReferenceLowercaseAbsolute() {
        this.isCellTextAndCheck("$a1", true);
    }

    @Test
    public void testIsCellTextAbsoluteColumnAbsoluteRow() {
        this.isCellTextAndCheck("$a$1", true);
    }

    @Test
    public void testIsCellTextAbsoluteColumnAbsoluteRowExtra() {
        this.isCellTextAndCheck("$a$1!", false);
    }

    @Test
    public void testIsCellTextLastColumn() {
        this.isCellTextAndCheck("XFD1", true);
    }

    @Test
    public void testIsCellTextLastColumnPlus1() {
        this.isCellTextAndCheck("XFE1", false);
    }

    @Test
    public void testIsCellTextLastRow() {
        this.isCellTextAndCheck("A1048576", true);
    }

    @Test
    public void testIsCellTextLastRowPlus1() {
        this.isCellTextAndCheck("A1048577", false);
    }

    @Test
    public void testIsCellTextWithLabel() {
        this.isCellTextAndCheck("LABEL123", false);
    }

    private void isCellTextAndCheck(final String text, final boolean expected) {
        this.checkEquals(
                expected,
                SpreadsheetExpressionReference.isCellText(text),
                () -> "isCellText " + CharSequences.quoteAndEscape(text)
        );
        if (expected) {
            SpreadsheetSelection.parseCell(text);
        }
    }

    // isLabelText......................................................................................................

    @Test
    public void testIsLabelTextNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetSelection.isLabelText(null)
        );
    }

    @Test
    public void testIsLabelTextEmpty() {
        this.isLabelTextAndCheck(
                "",
                false
        );
    }

    @Test
    public void testIsLabelTextWithLabel() {
        this.isLabelTextAndCheck(
                "Label123",
                true
        );
    }

    @Test
    public void testIsLabelTextWithCell() {
        this.isLabelTextAndCheck(
                "A1",
                false
        );
    }

    @Test
    public void testIsLabelTextWithCellRange() {
        this.isLabelTextAndCheck(
                "A1:B2",
                false
        );
    }

    private void isLabelTextAndCheck(final String text,
                                     final boolean expected) {
        this.checkEquals(
                expected,
                SpreadsheetSelection.isLabelText(text),
                () -> "isLabelText(" + CharSequences.quoteAndEscape(text) + ")"
        );
    }

    // parseCell.......................................................................................................

    @Test
    public void testParseCell() {
        this.checkEquals(SpreadsheetSelection.parseCell("A1"),
                SpreadsheetSelection.cell(SpreadsheetSelection.parseColumn("A"),
                        SpreadsheetSelection.parseRow("1")));
    }

    // parseCellRangeOrLabel............................................................................................

    @Test
    public void testParseCellRangeOrLabelWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetSelection.parseCellRangeOrLabel(null)
        );
    }

    @Test
    public void testParseCellRangeOrLabelWithEmptyFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetSelection.parseCellRangeOrLabel("")
        );
    }

    @Test
    public void testParseCellRangeOrLabelWithCell() {
        this.parseCellRangeOrLabelCheck(
                "a1",
                SpreadsheetSelection::parseCellRange
        );
    }

    @Test
    public void testParseCellRangeOrLabelWithCellRange() {
        this.parseCellRangeOrLabelCheck(
                "B2:C3",
                SpreadsheetSelection::parseCellRange
        );
    }

    @Test
    public void testParseCellRangeOrLabelWithLabel() {
        this.parseCellRangeOrLabelCheck(
                "Label123",
                SpreadsheetSelection::labelName
        );
    }

    private void parseCellRangeOrLabelCheck(final String text,
                                            final Function<String, SpreadsheetExpressionReference> expected) {
        this.checkEquals(
                expected.apply(text),
                SpreadsheetSelection.parseCellRangeOrLabel(text),
                () -> "parseCellRangeOrLabel " + CharSequences.quoteAndEscape(text)
        );
    }

    // parseColumn......................................................................................................

    @Test
    public void testParseColumnWithRangeFails() {
        assertThrows(IllegalArgumentException.class, () -> SpreadsheetSelection.parseColumn("B:C"));
    }

    @Test
    public void testParseColumn() {
        this.checkEquals(
                SpreadsheetSelection.parseColumn("B"),
                SpreadsheetReferenceKind.RELATIVE.column(1)
        );
    }

    // parseColumnReference...............................................................................................

    @Test
    public void testParseColumnReference() {
        this.checkEquals(
                SpreadsheetSelection.parseColumn("B")
                        .columnRange(SpreadsheetSelection.parseColumn("D")),
                SpreadsheetSelection.parseColumnRange("B:D")
        );
    }

    @Test
    public void testParseColumnReferenceSingleton() {
        this.checkEquals(
                SpreadsheetSelection.parseColumn("B")
                        .toColumnRange(),
                SpreadsheetSelection.parseColumnRange("B")
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
        this.parseStringAndCheck(label, SpreadsheetSelection.labelName(label));
    }

    @Test
    public void testParseStringRange() {
        final String range = "A2:B2";
        this.parseStringAndCheck(range, SpreadsheetSelection.parseCellRange(range));
    }


    // parseCellOrLabel....................................................................................

    @Test
    public void testParseCellOrLabelNullFails() {
        parseCellOrLabelFails(
                null,
                NullPointerException.class
        );
    }

    @Test
    public void testParseCellOrLabelEmptyFails() {
        parseCellOrLabelFails(
                "",
                IllegalArgumentException.class
        );
    }

    @Test
    public void testParseCellOrLabelRangeFails() {
        parseCellOrLabelFails(
                SpreadsheetSelection.parseCellRange("A1:B2").toString(),
                InvalidCharacterException.class
        );
    }

    private void parseCellOrLabelFails(final String text,
                                       final Class<? extends RuntimeException> thrown) {
        assertThrows(thrown, () -> SpreadsheetSelection.parseCellOrLabel(text));
    }

    @Test
    public void testParseCellOrLabelCell() {
        final String text = "A1";
        this.parseCellOrLabelAndCheck(text, SpreadsheetSelection.parseCell(text));
    }

    @Test
    public void testParseCellOrLabelLabel() {
        final String text = "Label123";
        this.parseCellOrLabelAndCheck(text, SpreadsheetCellReference.labelName(text));
    }

    private void parseCellOrLabelAndCheck(final String text,
                                          final SpreadsheetExpressionReference expected) {
        final SpreadsheetExpressionReference parsed = SpreadsheetSelection.parseCellOrLabel(text);

        this.checkEquals(
                expected,
                parsed,
                () -> "Parsing of " + CharSequences.quoteAndEscape(text) + " failed"
        );
    }

    // parseCellOrCellRange.............................................................................................

    @Test
    public void testParseCellOrCellRangeNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetSelection.parseCellOrCellRange(null)
        );
    }

    @Test
    public void testParseCellOrCellRangeEmptyFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetSelection.parseCellOrCellRange("")
        );
    }

    @Test
    public void testParseCellOrCellRangeInvalidCellFails() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetSelection.parseCellOrCellRange("A@@@")
        );
        this.checkEquals(
                "Invalid character 'A' at (1,1) \"A@@@\" expected cell",
                thrown.getMessage(),
                "message"
        );
    }

    @Test
    public void testParseCellOrCellRangeInvalidCellRangeFails() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetSelection.parseCellOrCellRange("A1:")
        );
        this.checkEquals(
                "Empty upper range in \"A1:\"",
                thrown.getMessage(),
                "message"
        );
    }

    @Test
    public void testParseCellOrCellRangeWithCell() {
        this.parseCellOrCellRangeAndCheck(
                "A1",
                SpreadsheetSelection.parseCell("A1")
        );
    }

    @Test
    public void testParseCellOrCellRangeWithCellRange() {
        this.parseCellOrCellRangeAndCheck(
                "B2:C3",
                SpreadsheetSelection.parseCellOrCellRange("B2:C3")
        );
    }

    @Test
    public void testParseCellOrCellRangeWithCellRangeSame() {
        this.parseCellOrCellRangeAndCheck(
                "D4:D4",
                SpreadsheetSelection.parseCellOrCellRange("D4:D4")
        );
    }

    private void parseCellOrCellRangeAndCheck(final String text,
                                              final SpreadsheetCellReferenceOrRange expected) {
        this.checkEquals(
                expected,
                SpreadsheetSelection.parseCellOrCellRange(text),
                () -> "parseCellOrCellRange(" + CharSequences.quoteAndEscape(text) + ")"
        );
    }

    // parseRow.........................................................................................................

    @Test
    public void testParseRowWithRangeFails() {
        assertThrows(IllegalArgumentException.class, () -> SpreadsheetSelection.parseRow("12:3"));
    }

    @Test
    public void testParseRow() {
        this.checkEquals(
                SpreadsheetReferenceKind.RELATIVE.row(2 - 1),
                SpreadsheetSelection.parseRow("2")
        );
    }

    @Test
    public void testParseRow2() {
        this.checkEquals(
                SpreadsheetReferenceKind.RELATIVE.row(23 - 1),
                SpreadsheetSelection.parseRow("23")
        );
    }

    // textLabel.......................................................................................................

    @Test
    public void testTextLabelCell() {
        this.textLabelAndCheck(
                SpreadsheetSelection.parseCell("B2"),
                "Cell"
        );
    }

    @Test
    public void testTextLabelCellRange() {
        this.textLabelAndCheck(
                SpreadsheetSelection.parseCellRange("C3:D4"),
                "Cell Range"
        );
    }

    @Test
    public void testTextLabelColumn() {
        this.textLabelAndCheck(
                SpreadsheetSelection.parseColumn("C"),
                "Column"
        );
    }

    @Test
    public void testTextLabelColumnRange() {
        this.textLabelAndCheck(
                SpreadsheetSelection.parseColumnRange("C:D"),
                "Column Range"
        );
    }

    @Test
    public void testTextLabelSpreadsheetLabelName() {
        this.textLabelAndCheck(
                SpreadsheetSelection.labelName("Label123"),
                "Label"
        );
    }

    @Test
    public void testTextLabelRow() {
        this.textLabelAndCheck(
                SpreadsheetSelection.parseRow("2"),
                "Row"
        );
    }

    @Test
    public void testTextLabelRowRange() {
        this.textLabelAndCheck(
                SpreadsheetSelection.parseRowRange("3:4"),
                "Row Range"
        );
    }

    private void textLabelAndCheck(final SpreadsheetSelection selection,
                                   final String textLabel) {
        this.checkEquals(
                textLabel,
                selection.textLabel(),
                () -> "textLabel of " + selection
        );
    }

    // parseWindow......................................................................................................

    @Test
    public void testParseWindowNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetSelection.parseWindow(null)
        );
    }

    @Test
    public void testParseWindowEmpty() {
        this.parseWindowAndCheck(
                "",
                Sets.empty()
        );
    }

    @Test
    public void testParseWindowOneCell() {
        this.parseWindowAndCheck(
                "C1",
                SpreadsheetSelection.parseCellRange("C1")
        );
    }

    @Test
    public void testParseWindowMany() {
        this.parseWindowAndCheck(
                "A1,B2:C3",
                "A1",
                "B2:C3"
        );
    }

    private void parseWindowAndCheck(final String text,
                                     final String... windows) {
        this.parseWindowAndCheck(
                text,
                Arrays.stream(windows)
                        .map(SpreadsheetSelection::parseCellRange)
                        .collect(Collectors.toCollection(Sets::ordered))
        );
    }

    private void parseWindowAndCheck(final String text,
                                     final SpreadsheetCellRange... window) {
        this.parseWindowAndCheck(
                text,
                Sets.of(window)
        );
    }

    private void parseWindowAndCheck(final String text,
                                     final Set<SpreadsheetCellRange> window) {
        this.checkEquals(
                window,
                SpreadsheetSelection.parseWindow(text),
                () -> "parse " + CharSequences.quoteAndEscape(text)
        );
    }

    // deletedText.......................................................................................................

    @Test
    public void testDeletedTextCell() {
        this.checkEquals(
                "Cell deleted: Z99",
                SpreadsheetSelection.parseCell("Z99").deleteText()
        );
    }

    @Test
    public void testDeletedTextColumn() {
        this.checkEquals(
                "Column deleted: Z",
                SpreadsheetSelection.parseColumn("Z").deleteText()
        );
    }

    // notFound.........................................................................................................

    @Test
    public void testNotFound() {
        this.checkEquals(
                "Cell not found: Z99",
                SpreadsheetSelection.parseCell("Z99").notFound()
        );
    }

    // HasUrlFragment...................................................................................................

    @Test
    public void testHasUrlFragmentCell() {
        this.urlFragmentAndCheck(
                SpreadsheetSelection.parseCell("A1"),
                "/cell/A1"
        );
    }

    @Test
    public void testHasUrlFragmentCellRange() {
        this.urlFragmentAndCheck(
                SpreadsheetSelection.parseCellOrCellRange("B2:C3"),
                "/cell/B2:C3"
        );
    }

    @Test
    public void testHasUrlFragmentColumn() {
        this.urlFragmentAndCheck(
                SpreadsheetSelection.parseColumn("D"),
                "/column/D"
        );
    }

    @Test
    public void testHasUrlFragmentColumnRange() {
        this.urlFragmentAndCheck(
                SpreadsheetSelection.parseColumnRange("E:F"),
                "/column/E:F"
        );
    }

    private void urlFragmentAndCheck(final SpreadsheetSelection selection,
                                     final String expected) {
        this.checkEquals(
                UrlFragment.with(expected),
                selection.urlFragment(),
                () -> selection + " urlFragment"
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
        return SpreadsheetSelection.parseExpressionReference(text);
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

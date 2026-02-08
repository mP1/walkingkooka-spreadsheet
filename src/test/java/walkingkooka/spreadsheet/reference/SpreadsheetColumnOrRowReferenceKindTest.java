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
import walkingkooka.collect.list.Lists;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.CharSequences;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetColumnOrRowReferenceKindTest implements ClassTesting2<SpreadsheetColumnOrRowReferenceKind>,
    ParseStringTesting<SpreadsheetSelection> {

    // firstAbsolute....................................................................................................

    @Test
    public void testFirstAbsoluteColumn() {
        this.firstAbsoluteAndCheck(
            SpreadsheetColumnOrRowReferenceKind.COLUMN,
            SpreadsheetSelection.parseColumn("$A")
        );
    }

    @Test
    public void testFirstAbsoluteRow() {
        this.firstAbsoluteAndCheck(
            SpreadsheetColumnOrRowReferenceKind.ROW,
            SpreadsheetSelection.parseRow("$1")
        );
    }

    private void firstAbsoluteAndCheck(final SpreadsheetColumnOrRowReferenceKind kind,
                                       final SpreadsheetColumnOrRowReferenceOrRange expected) {
        this.checkEquals(
            expected,
            kind.firstAbsolute()
        );
    }

    // firstRelative....................................................................................................

    @Test
    public void testFirstRelativeColumn() {
        this.firstRelativeAndCheck(
            SpreadsheetColumnOrRowReferenceKind.COLUMN,
            SpreadsheetSelection.parseColumn("A")
        );
    }

    @Test
    public void testFirstRelativeRow() {
        this.firstRelativeAndCheck(
            SpreadsheetColumnOrRowReferenceKind.ROW,
            SpreadsheetSelection.parseRow("1")
        );
    }

    private void firstRelativeAndCheck(final SpreadsheetColumnOrRowReferenceKind kind,
                                       final SpreadsheetColumnOrRowReferenceOrRange expected) {
        this.checkEquals(
            expected,
            kind.firstRelative()
        );
    }

    // lastAbsolute....................................................................................................

    @Test
    public void testLastAbsoluteColumn() {
        this.lastAbsoluteAndCheck(
            SpreadsheetColumnOrRowReferenceKind.COLUMN,
            SpreadsheetSelection.parseColumn("$XFD")
        );
    }

    @Test
    public void testLastAbsoluteRow() {
        this.lastAbsoluteAndCheck(
            SpreadsheetColumnOrRowReferenceKind.ROW,
            SpreadsheetSelection.parseRow("$1048576")
        );
    }

    private void lastAbsoluteAndCheck(final SpreadsheetColumnOrRowReferenceKind kind,
                                      final SpreadsheetColumnOrRowReferenceOrRange expected) {
        this.checkEquals(
            expected,
            kind.lastAbsolute()
        );
    }

    // lastRelative....................................................................................................

    @Test
    public void testLastRelativeColumn() {
        this.lastRelativeAndCheck(
            SpreadsheetColumnOrRowReferenceKind.COLUMN,
            SpreadsheetSelection.parseColumn("XFD")
        );
    }

    @Test
    public void testLastRelativeRow() {
        this.lastRelativeAndCheck(
            SpreadsheetColumnOrRowReferenceKind.ROW,
            SpreadsheetSelection.parseRow("1048576")
        );
    }

    private void lastRelativeAndCheck(final SpreadsheetColumnOrRowReferenceKind kind,
                                      final SpreadsheetColumnOrRowReferenceOrRange expected) {
        this.checkEquals(
            expected,
            kind.lastRelative()
        );
    }

    // value............................................................................................................

    @Test
    public void testValueColumnRelative() {
        this.valueAndCheck(
            SpreadsheetColumnOrRowReferenceKind.COLUMN,
            SpreadsheetSelection.parseColumn("B"),
            2
        );
    }

    @Test
    public void testValueRowRelative() {
        this.valueAndCheck(
            SpreadsheetColumnOrRowReferenceKind.ROW,
            SpreadsheetSelection.parseRow("3"),
            3
        );
    }

    @Test
    public void testValueRowAbsolute() {
        this.valueAndCheck(
            SpreadsheetColumnOrRowReferenceKind.ROW,
            SpreadsheetSelection.parseRow("$4"),
            4
        );
    }

    private void valueAndCheck(final SpreadsheetColumnOrRowReferenceKind kind,
                               final SpreadsheetColumnOrRowReferenceOrRange reference,
                               final int expected) {
        this.checkEquals(
            expected,
            kind.value(reference),
            () -> kind + " value " + reference
        );
    }

    // setValue.........................................................................................................

    @Test
    public void testSetValueColumnRelative() {
        this.setValueAndCheck(
            SpreadsheetColumnOrRowReferenceKind.COLUMN,
            SpreadsheetReferenceKind.RELATIVE,
            2,
            SpreadsheetSelection.parseColumn("B")
        );
    }

    @Test
    public void testSetValueRowRelative() {
        this.setValueAndCheck(
            SpreadsheetColumnOrRowReferenceKind.ROW,
            SpreadsheetReferenceKind.RELATIVE,
            2,
            SpreadsheetSelection.parseRow("2")
        );
    }

    @Test
    public void testSetValueRowAbsolute() {
        this.setValueAndCheck(
            SpreadsheetColumnOrRowReferenceKind.ROW,
            SpreadsheetReferenceKind.ABSOLUTE,
            3,
            SpreadsheetSelection.parseRow("$3")
        );
    }

    private void setValueAndCheck(final SpreadsheetColumnOrRowReferenceKind kind,
                                  final SpreadsheetReferenceKind referenceKind,
                                  final int value,
                                  final SpreadsheetColumnOrRowReferenceOrRange expected) {
        this.checkEquals(
            expected,
            kind.setValue(
                referenceKind,
                value
            ),
            () -> kind + " setValue " + referenceKind + ", " + value
        );
    }

    // parse............................................................................................................

    @Test
    public void testParseColumnOrRowCellFails() {
        this.parseStringFails(
            "A1",
            IllegalArgumentException.class
        );
    }

    @Test
    public void testParseColumnOrRowCellRangeFails() {
        this.parseStringFails(
            "B2:C3",
            IllegalArgumentException.class
        );
    }

    @Test
    public void testParseColumnOrRowColumn() {
        this.parseStringAndCheck(
            "C",
            SpreadsheetSelection.parseColumn("C")
        );
    }

    @Test
    public void testParseColumnOrRowColumnRangeFails() {
        this.parseStringFails(
            "B:C",
            IllegalArgumentException.class
        );
    }

    @Test
    public void testParseColumnOrRowLabelFails() {
        this.parseStringFails(
            "Label123",
            IllegalArgumentException.class
        );
    }

    @Test
    public void testParseColumnOrRowRow() {
        this.parseStringAndCheck(
            "4",
            SpreadsheetSelection.parseRow("4")
        );
    }

    @Test
    public void testParseColumnOrRowRowRangeFails() {
        this.parseStringFails(
            "2:3",
            IllegalArgumentException.class
        );
    }

    // parse...........................................................................................................

    @Test
    public void testParseColumnWithCellFails() {
        this.parseFails(
            SpreadsheetColumnOrRowReferenceKind.COLUMN,
            "A1",
            "Invalid character '1' at 1"
        );
    }

    @Test
    public void testParseColumnWithColumn() {
        this.parseAndCheck(
            SpreadsheetColumnOrRowReferenceKind.COLUMN,
            "A",
            SpreadsheetSelection.parseColumn("A")
        );
    }

    @Test
    public void testParseRowWithColumnRangeFails() {
        this.parseFails(
            SpreadsheetColumnOrRowReferenceKind.COLUMN,
            "B:C",
            "Invalid character ':' at 1"
        );
    }

    @Test
    public void testParseColumnWithLabelFails() {
        this.parseFails(
            SpreadsheetColumnOrRowReferenceKind.COLUMN,
            "Label123",
            "Invalid column \"Label\" not between \"A\" and \"XFD\""
        );
    }

    @Test
    public void testParseColumnWithRowFails() {
        this.parseFails(
            SpreadsheetColumnOrRowReferenceKind.COLUMN,
            "321",
            "Invalid character '3' at 0"
        );
    }

    @Test
    public void testParseRowWithCellFails() {
        this.parseFails(
            SpreadsheetColumnOrRowReferenceKind.ROW,
            "A1",
            "Invalid character 'A' at 0"
        );
    }

    @Test
    public void testParseRowWithColumnFails() {
        this.parseFails(
            SpreadsheetColumnOrRowReferenceKind.ROW,
            "A",
            "Invalid character 'A'"
        );
    }

    @Test
    public void testParseRowWithLabelFails() {
        this.parseFails(
            SpreadsheetColumnOrRowReferenceKind.ROW,
            "Label123",
            "Invalid character 'L' at 0"
        );
    }

    @Test
    public void testParseRowWithRow() {
        this.parseAndCheck(
            SpreadsheetColumnOrRowReferenceKind.ROW,
            "12",
            SpreadsheetSelection.parseRow("12")
        );
    }

    @Test
    public void testParseRowWithRowRangeFails() {
        this.parseFails(
            SpreadsheetColumnOrRowReferenceKind.ROW,
            "1:2",
            "Invalid character ':' at 1"
        );
    }

    private void parseFails(final SpreadsheetColumnOrRowReferenceKind kind,
                            final String text,
                            final String expected) {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> kind.parse(text)
        );
        this.checkEquals(
            expected,
            thrown.getMessage()
        );
    }


    private void parseAndCheck(final SpreadsheetColumnOrRowReferenceKind kind,
                               final String text,
                               final SpreadsheetSelection expected) {
        this.checkEquals(
            expected,
            kind.parse(text),
            () -> kind + ".parse " + CharSequences.quoteAndEscape(text)
        );
    }

    // flip.............................................................................................................

    @Test
    public void testFlipColumn() {
        this.flipAndCheck(
            SpreadsheetColumnOrRowReferenceKind.COLUMN,
            SpreadsheetColumnOrRowReferenceKind.ROW
        );
    }

    @Test
    public void testFlipRow() {
        this.flipAndCheck(
            SpreadsheetColumnOrRowReferenceKind.ROW,
            SpreadsheetColumnOrRowReferenceKind.COLUMN
        );
    }

    private void flipAndCheck(final SpreadsheetColumnOrRowReferenceKind kind,
                              final SpreadsheetColumnOrRowReferenceKind expected) {
        this.checkEquals(
            expected,
            kind.flip(),
            () -> kind + " flip"
        );
    }

    // columnOrRow......................................................................................................

    @Test
    public void testColumnOrRowWithColumnAndNullSelectionFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetColumnOrRowReferenceKind.COLUMN.columnOrRow(null)
        );
    }

    @Test
    public void testColumnOrRowWithRowAndNullSelectionFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetColumnOrRowReferenceKind.ROW.columnOrRow(null)
        );
    }

    @Test
    public void testColumnOrRowWithColumn() {
        this.columnOrRowAndCheck(
            SpreadsheetColumnOrRowReferenceKind.COLUMN,
            SpreadsheetSelection.A1,
            SpreadsheetSelection.A1.column()
        );
    }

    @Test
    public void testColumnOrRowWithRow() {
        this.columnOrRowAndCheck(
            SpreadsheetColumnOrRowReferenceKind.ROW,
            SpreadsheetSelection.A1,
            SpreadsheetSelection.A1.row()
        );
    }

    @Test
    public void testColumnOrRowWithRow2() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseCell("B2");

        this.columnOrRowAndCheck(
            SpreadsheetColumnOrRowReferenceKind.ROW,
            cell,
            cell.row()
        );
    }

    private void columnOrRowAndCheck(final SpreadsheetColumnOrRowReferenceKind kind,
                                     final SpreadsheetSelection selection,
                                     final SpreadsheetColumnOrRowReferenceOrRange expected) {
        this.checkEquals(
            expected,
            kind.columnOrRow(selection),
            () -> kind + " columnOrRow from " + selection
        );
    }

    // columnOrRowRange.................................................................................................

    @Test
    public void testColumnOrRowRangeWithColumnAndNullSelectionFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetColumnOrRowReferenceKind.COLUMN.columnOrRowRange(null)
        );
    }

    @Test
    public void testColumnOrRowRangeWithRowAndNullSelectionFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetColumnOrRowReferenceKind.ROW.columnOrRowRange(null)
        );
    }

    @Test
    public void testColumnOrRowRangeWithColumn() {
        this.columnOrRowRangeAndCheck(
            SpreadsheetColumnOrRowReferenceKind.COLUMN,
            SpreadsheetSelection.parseCellRange("A1:B2"),
            SpreadsheetSelection.parseColumnRange("A:B")
        );
    }

    @Test
    public void testColumnOrRowRangeWithRow() {
        this.columnOrRowRangeAndCheck(
            SpreadsheetColumnOrRowReferenceKind.ROW,
            SpreadsheetSelection.parseCellRange("B2:C3"),
            SpreadsheetSelection.parseRowRange("2:3")
        );
    }

    @Test
    public void testColumnOrRowRangeWithRow2() {
        this.columnOrRowRangeAndCheck(
            SpreadsheetColumnOrRowReferenceKind.ROW,
            SpreadsheetSelection.parseCellRange("D4:D5"),
            SpreadsheetSelection.parseRowRange("4:5")
        );
    }

    private void columnOrRowRangeAndCheck(final SpreadsheetColumnOrRowReferenceKind kind,
                                          final SpreadsheetSelection selection,
                                          final SpreadsheetColumnOrRowReferenceOrRange expected) {
        this.checkEquals(
            expected,
            kind.columnOrRowRange(selection),
            () -> kind + " columnOrRowRange from " + selection
        );
    }

    @Test
    public void testColumnOrRowRangeColumnIteration() {
        this.columnOrRowRangeIterateAndCheck(
            SpreadsheetColumnOrRowReferenceKind.COLUMN,
            SpreadsheetSelection.parseCellRange("A1:B2"),
            SpreadsheetSelection.parseColumn("A"),
            SpreadsheetSelection.parseColumn("B")
        );
    }

    @Test
    public void testColumnOrRowRangeRowIteration() {
        this.columnOrRowRangeIterateAndCheck(
            SpreadsheetColumnOrRowReferenceKind.ROW,
            SpreadsheetSelection.parseCellRange("A1:B2"),
            SpreadsheetSelection.parseRow("1"),
            SpreadsheetSelection.parseRow("2")
        );
    }

    private void columnOrRowRangeIterateAndCheck(final SpreadsheetColumnOrRowReferenceKind kind,
                                                 final SpreadsheetSelection selection,
                                                 final SpreadsheetColumnOrRowReferenceOrRange... expected) {
        this.columnOrRowRangeIterateAndCheck(
            kind,
            selection,
            Lists.of(expected)
        );
    }

    private void columnOrRowRangeIterateAndCheck(final SpreadsheetColumnOrRowReferenceKind kind,
                                                 final SpreadsheetSelection selection,
                                                 final List<SpreadsheetColumnOrRowReferenceOrRange> expected) {
        final List<SpreadsheetSelection> actual = Lists.array();
        for (final SpreadsheetColumnOrRowReferenceOrRange columnOrRowReference : (Iterable<SpreadsheetColumnOrRowReferenceOrRange>) kind.columnOrRowRange(selection)) {
            actual.add(columnOrRowReference);
        }

        this.checkEquals(
            expected,
            actual
        );
    }

    // length...........................................................................................................

    @Test
    public void testLengthColumnWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetColumnOrRowReferenceKind
                .COLUMN.length(null)
        );
    }

    @Test
    public void testLengthRowWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetColumnOrRowReferenceKind.ROW
                .length(null)
        );
    }

    @Test
    public void testLengthColumnWithColumn() {
        this.lengthAndCheck(
            SpreadsheetColumnOrRowReferenceKind.COLUMN,
            SpreadsheetSelection.parseColumn("A"),
            1
        );
    }

    @Test
    public void testLengthRowWithColumn() {
        this.lengthAndCheck(
            SpreadsheetColumnOrRowReferenceKind.ROW,
            SpreadsheetSelection.parseColumn("A"),
            1048576
        );
    }

    @Test
    public void testLengthColumnWithRow() {
        this.lengthAndCheck(
            SpreadsheetColumnOrRowReferenceKind.COLUMN,
            SpreadsheetSelection.parseRow("2"),
            16384
        );
    }

    @Test
    public void testLengthRowWithRow() {
        this.lengthAndCheck(
            SpreadsheetColumnOrRowReferenceKind.ROW,
            SpreadsheetSelection.parseRow("3"),
            1
        );
    }

    @Test
    public void testLengthColumnWithCellRange() {
        this.lengthAndCheck(
            SpreadsheetColumnOrRowReferenceKind.COLUMN,
            SpreadsheetSelection.parseCellRange("A1:C5"),
            3
        );
    }

    @Test
    public void testLengthRowWithCellRange() {
        this.lengthAndCheck(
            SpreadsheetColumnOrRowReferenceKind.ROW,
            SpreadsheetSelection.parseCellRange("A1:C5"),
            5
        );
    }

    private void lengthAndCheck(final SpreadsheetColumnOrRowReferenceKind kind,
                                final SpreadsheetSelection selection,
                                final int expected) {
        this.checkEquals(
            expected,
            kind.length(selection),
            () -> kind + " length " + selection
        );
    }

    // ClassTesting2....................................................................................................

    @Override
    public Class<SpreadsheetColumnOrRowReferenceKind> type() {
        return SpreadsheetColumnOrRowReferenceKind.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // parseColumnOrRow.................................................................................................

    @Override
    public SpreadsheetSelection parseString(final String text) {
        return SpreadsheetColumnOrRowReferenceKind.parseColumnOrRow(text);
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> type) {
        return type;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException cause) {
        return cause;
    }
}

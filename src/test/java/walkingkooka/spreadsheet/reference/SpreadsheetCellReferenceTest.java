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
import walkingkooka.collect.Range;
import walkingkooka.net.http.server.hateos.HateosResourceTesting;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.visit.Visiting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetCellReferenceTest extends SpreadsheetCellReferenceOrLabelNameTestCase<SpreadsheetCellReference>
        implements HateosResourceTesting<SpreadsheetCellReference> {

    private final static int COLUMN = 123;
    private final static int ROW = 456;

    @Test
    public void testWithNullColumnFails() {
        assertThrows(NullPointerException.class, () -> SpreadsheetCellReference.with(null, row()));
    }

    @Test
    public void testWithNullRowFails() {
        assertThrows(NullPointerException.class, () -> SpreadsheetCellReference.with(column(), null));
    }

    @Test
    public void testWith() {
        final SpreadsheetColumnReference column = this.column();
        final SpreadsheetRowReference row = this.row();
        final SpreadsheetCellReference cell = SpreadsheetCellReference.with(column, row);
        this.checkColumn(cell, column);
        this.checkRow(cell, row);
    }

    // setColumn..................................................................................................

    @Test
    public void testSetColumnNullFails() {
        assertThrows(NullPointerException.class, () -> this.createSelection().setColumn(null));
    }

    @Test
    public void testSetColumnSame() {
        final SpreadsheetCellReference cell = this.createSelection();
        assertSame(cell, cell.setColumn(this.column(COLUMN)));
    }

    @Test
    public void testSetColumnDifferent() {
        final SpreadsheetCellReference cell = this.createSelection();
        final SpreadsheetColumnReference differentColumn = this.column(99);
        final SpreadsheetCellReference different = cell.setColumn(differentColumn);
        this.checkRow(different, this.row());
        this.checkColumn(different, differentColumn);
    }

    // setRow..................................................................................................

    @Test
    public void testSetRowNullFails() {
        assertThrows(NullPointerException.class, () -> this.createSelection().setRow(null));
    }

    @Test
    public void testSetRowSame() {
        final SpreadsheetCellReference cell = this.createSelection();
        assertSame(cell, cell.setRow(this.row(ROW)));
    }

    @Test
    public void testSetRowDifferent() {
        final SpreadsheetCellReference cell = this.createSelection();
        final SpreadsheetRowReference differentRow = this.row(99);
        final SpreadsheetCellReference different = cell.setRow(differentRow);
        this.checkColumn(different, this.column());
        this.checkRow(different, differentRow);
    }

    // Predicate........................................................................................................

    @Test
    public void testTestDifferentColumnFalse() {
        final SpreadsheetCellReference selection = this.createSelection();
        this.testFalse(selection.setColumn(selection.column().add(1)));
    }

    @Test
    public void testTestDifferentRowFalse() {
        final SpreadsheetCellReference selection = this.createSelection();
        this.testFalse(selection.setRow(selection.row().add(1)));
    }

    @Test
    public void testTestDifferentColumnKindTrue() {
        final SpreadsheetCellReference selection = this.createSelection();
        this.testFalse(selection.setRow(selection.row().add(1)));
    }

    @Test
    public void testTestDifferentRowKindTrue() {
        final SpreadsheetCellReference selection = this.createSelection();
        final SpreadsheetRowReference row = selection.row();
        final SpreadsheetReferenceKind kind = row.referenceKind();

        this.testTrue(selection.setRow(row.setReferenceKind(kind.flip())));
    }

    // toRelative........................................................................................................

    @Test
    public void testToRelativeAlreadyAbsolute() {
        this.toRelativeAndCheck0(SpreadsheetCellReference.parseCell("$B$2"));
    }

    @Test
    public void testToRelativeRelative() {
        this.toRelativeAndCheck0(SpreadsheetCellReference.parseCell("B2"));
    }

    @Test
    public void testToRelativeMixed() {
        this.toRelativeAndCheck0(SpreadsheetCellReference.parseCell("$B2"));
    }

    @Test
    public void testToRelativeMixed2() {
        this.toRelativeAndCheck0(SpreadsheetCellReference.parseCell("B$2"));
    }

    private void toRelativeAndCheck0(final SpreadsheetCellReference reference) {
        this.toRelativeOrAbsoluteAndCheck(reference,
                reference.toRelative(),
                SpreadsheetReferenceKind.RELATIVE);
    }

    // toAbsolute.......................................................................................................

    @Test
    public void testToAbsoluteAlreadyAbsolute() {
        this.toAbsoluteAndCheck(SpreadsheetCellReference.parseCell("$B$2"));
    }

    @Test
    public void testToAbsoluteRelative() {
        this.toAbsoluteAndCheck(SpreadsheetCellReference.parseCell("B2"));
    }

    @Test
    public void testToAbsoluteMixed() {
        this.toAbsoluteAndCheck(SpreadsheetCellReference.parseCell("$B2"));
    }

    @Test
    public void testToAbsoluteMixed2() {
        this.toAbsoluteAndCheck(SpreadsheetCellReference.parseCell("B$2"));
    }

    private void toAbsoluteAndCheck(final SpreadsheetCellReference reference) {
        this.toRelativeOrAbsoluteAndCheck(reference,
                reference.toAbsolute(),
                SpreadsheetReferenceKind.ABSOLUTE);
    }

    private void toRelativeOrAbsoluteAndCheck(final SpreadsheetCellReference reference,
                                              final SpreadsheetCellReference to,
                                              final SpreadsheetReferenceKind kind) {
        final SpreadsheetColumnReference column = reference.column();
        final SpreadsheetRowReference row = reference.row();

        this.checkColumn(to, column.setReferenceKind(kind));
        this.checkRow(to, row.setReferenceKind(kind));

        this.checkColumn(reference, column);
        this.checkRow(reference, row);
    }

    // addColumn .......................................................................................................

    @Test
    public void testAddColumnZero() {
        final SpreadsheetCellReference cell = this.createSelection();
        assertSame(cell, cell.addColumn(0));
    }

    @Test
    public void testAddColumnNonZero() {
        final SpreadsheetCellReference cell = this.createSelection();
        final int delta = 10;

        final SpreadsheetCellReference different = cell.addColumn(delta);
        this.checkColumn(different, this.column().setValue(COLUMN + delta));
        this.checkRow(different, this.row());
    }

    // addColumnSaturated................................................................................................

    @Test
    public void testAddColumnSaturationUnderflows() {
        final SpreadsheetCellReference cell = SpreadsheetCellReference.parseCell("B2");
        assertEquals(
                SpreadsheetCellReference.parseCell("A2"),
                cell.addColumnSaturated(-3)
        );
    }

    @Test
    public void testAddColumnSaturationOverflows() {
        final SpreadsheetRowReference row = SpreadsheetRowReference.parseRow("2");
        final SpreadsheetCellReference cell = SpreadsheetReferenceKind.RELATIVE.column(SpreadsheetColumnReference.MAX_VALUE - 2)
                .setRow(row);
        assertEquals(
                SpreadsheetReferenceKind.RELATIVE.lastColumn()
                        .setRow(row),
                cell.addColumnSaturated(+3)
        );
    }

    @Test
    public void testAddColumnSaturation() {
        final SpreadsheetCellReference cell = SpreadsheetCellReference.parseCell("B2");
        assertEquals(
                SpreadsheetCellReference.parseCell("D2"),
                cell.addColumnSaturated(+2)
        );
    }

    // addRow .............................................................................................

    @Test
    public void testAddRowZero() {
        final SpreadsheetCellReference cell = this.createSelection();
        assertSame(cell, cell.addRow(0));
    }

    @Test
    public void testAddRowNonZero() {
        final SpreadsheetCellReference cell = this.createSelection();
        final int delta = 10;

        final SpreadsheetCellReference different = cell.addRow(delta);
        this.checkRow(different, this.row().setValue(ROW + delta));
        this.checkColumn(different, this.column());
    }

    // addRowSaturated................................................................................................

    @Test
    public void testAddRowSaturationUnderflows() {
        final SpreadsheetCellReference cell = SpreadsheetCellReference.parseCell("B3");
        assertEquals(
                SpreadsheetCellReference.parseCell("B1"),
                cell.addRowSaturated(-3)
        );
    }

    @Test
    public void testAddRowSaturationOverflows() {
        final SpreadsheetColumnReference column = SpreadsheetRowReference.parseColumn("B");
        final SpreadsheetCellReference cell = SpreadsheetReferenceKind.RELATIVE.row(SpreadsheetRowReference.MAX_VALUE - 2)
                .setColumn(column);
        assertEquals(
                column.setRow(SpreadsheetReferenceKind.RELATIVE.lastRow()),
                cell.addRowSaturated(+3)
        );
    }

    @Test
    public void testAddRowSaturation() {
        final SpreadsheetCellReference cell = SpreadsheetCellReference.parseCell("B2");
        assertEquals(
                SpreadsheetCellReference.parseCell("B4"),
                cell.addRowSaturated(+2)
        );
    }

    // add .............................................................................................

    @Test
    public void testAddColumnRowColumnZeroAndRowZero() {
        final SpreadsheetCellReference cell = this.createSelection();
        assertSame(cell, cell.add(0, 0));
    }

    @Test
    public void testAddColumnRowColumnNonZeroAndRowNonZero() {
        final SpreadsheetCellReference cell = this.createSelection();
        final int column = 10;
        final int row = 100;

        final SpreadsheetCellReference different = cell.add(column, row);
        this.checkColumn(different, this.column().setValue(COLUMN + column));
        this.checkRow(different, this.row().setValue(ROW + row));
    }

    @Test
    public void testAddColumnRowColumnNonZero() {
        final SpreadsheetCellReference cell = this.createSelection();
        final int column = 10;

        final SpreadsheetCellReference different = cell.add(column, 0);
        this.checkColumn(different, this.column().setValue(COLUMN + column));
        this.checkRow(different, this.row());
    }

    @Test
    public void testAddColumnRowRowNonZero() {
        final SpreadsheetCellReference cell = this.createSelection();
        final int row = 100;

        final SpreadsheetCellReference different = cell.add(0, row);
        this.checkColumn(different, this.column());
        this.checkRow(different, this.row().setValue(ROW + row));
    }

    @Test
    public void testSameColumnSameRowDifferentReferenceKinds() {
        this.compareToAndCheckEquals(
                this.cell(SpreadsheetReferenceKind.ABSOLUTE, COLUMN, SpreadsheetReferenceKind.ABSOLUTE, ROW),
                this.cell(SpreadsheetReferenceKind.RELATIVE, COLUMN, SpreadsheetReferenceKind.RELATIVE, ROW));
    }

    @Test
    public void testSameColumnDifferentRow() {
        this.compareToAndCheckLess(
                this.cell(SpreadsheetReferenceKind.ABSOLUTE, COLUMN, SpreadsheetReferenceKind.ABSOLUTE, ROW),
                this.cell(COLUMN, ROW + 10));
    }

    @Test
    public void testSameColumnDifferentReferenceKindDifferentRow() {
        this.compareToAndCheckLess(
                this.cell(SpreadsheetReferenceKind.ABSOLUTE, COLUMN, SpreadsheetReferenceKind.ABSOLUTE, ROW),
                this.cell(SpreadsheetReferenceKind.RELATIVE, COLUMN, SpreadsheetReferenceKind.ABSOLUTE, ROW + 10));
    }

    @Test
    public void testDifferentColumnSameRow() {
        this.compareToAndCheckLess(this.cell(COLUMN + 10, ROW));
    }

    @Test
    public void testDifferentColumnDifferentReferenceKindDifferentRow() {
        this.compareToAndCheckLess(this.cell(SpreadsheetReferenceKind.RELATIVE, COLUMN + 10, SpreadsheetReferenceKind.ABSOLUTE, ROW));
    }

    private SpreadsheetCellReference cell(final int column, final int row) {
        return this.cell(SpreadsheetReferenceKind.ABSOLUTE, column, SpreadsheetReferenceKind.ABSOLUTE, row);
    }

    private SpreadsheetCellReference cell(final SpreadsheetReferenceKind columnKind,
                                          final int column,
                                          final SpreadsheetReferenceKind rowKind,
                                          final int row) {
        return columnKind.column(column).setRow(rowKind.row(row));
    }

    // range.............................................................................................................

    @Test
    public void testRangeNullFails() {
        assertThrows(NullPointerException.class, () -> this.cell(1, 1).spreadsheetCellRange(null));
    }

    @Test
    public void testRangeOne() {
        final SpreadsheetCellReference lower = this.cell(1, 1);
        final SpreadsheetCellRange range = lower.spreadsheetCellRange(lower);
        this.checkEquals(Range.singleton(lower), range.range());
    }

    @Test
    public void testRangeLeftTopRightBottom() {
        final int left = 1;
        final int top = 2;
        final int right = 3;
        final int bottom = 4;

        this.rangeAndCheck(this.cell(left, top),
                this.cell(right, bottom),
                left, top,
                right, bottom);
    }

    @Test
    public void testRangeLeftBottomRightTop() {
        final int left = 1;
        final int top = 2;
        final int right = 3;
        final int bottom = 4;

        this.rangeAndCheck(this.cell(left, bottom),
                this.cell(right, top),
                left, top,
                right, bottom);
    }

    @Test
    public void testRangeRightTopLeftBottom() {
        final int left = 1;
        final int top = 2;
        final int right = 3;
        final int bottom = 4;

        this.rangeAndCheck(this.cell(right, top),
                this.cell(left, bottom),
                left, top,
                right, bottom);
    }

    private void rangeAndCheck(final SpreadsheetCellReference cell,
                               final SpreadsheetCellReference other,
                               final int left,
                               final int top,
                               final int right,
                               final int bottom) {
        final Range<SpreadsheetCellReference> expected = Range.greaterThanEquals(this.cell(left, top)).and(Range.lessThanEquals(this.cell(right, bottom)));

        final Range<SpreadsheetCellReference> range = cell.range(other);
        assertEquals(expected,
                range,
                () -> cell + " range " + other);


        assertEquals(SpreadsheetCellRange.with(expected),
                cell.spreadsheetCellRange(other),
                () -> cell + " spreadsheetCellRange " + other);
    }

    // toSpreadsheetCellRange...........................................................................................

    @Test
    public void testToSpreadsheetCellRangeAbsolute() {
        this.toSpreadsheetCellRangeAndCheck(
                SpreadsheetCellReference.parseCell("$B$2"),
                SpreadsheetCellRange.parseCellRange("$B$2")
        );
    }

    @Test
    public void testToSpreadsheetCellRangeRelative() {
        final String text = "C3";

        this.toSpreadsheetCellRangeAndCheck(
                SpreadsheetCellReference.parseCell(text),
                SpreadsheetCellRange.parseCellRange(text)
        );
    }

    private void toSpreadsheetCellRangeAndCheck(final SpreadsheetCellReference reference,
                                                final SpreadsheetCellRange range) {
        assertEquals(
                range,
                reference.toSpreadsheetCellRange(),
                () -> reference + " toSpreadsheetCellRange()"
        );
    }

    // testCellRange.....................................................................................................

    @Test
    public void testTestCellRangeBeforeAbove() {
        this.testCellRangeCheck2(
                "B3",
                "C3:E5",
                false
        );
    }

    @Test
    public void testTestCellRangeAbove() {
        this.testCellRangeCheck2(
                "D2",
                "C3:E5",
                false
        );
    }

    @Test
    public void testTestCellRangeAfterAbove() {
        this.testCellRangeCheck2(
                "E2",
                "C3:E5",
                false
        );
    }

    @Test
    public void testTestCellRangeTopLeft() {
        this.testCellRangeCheck2(
                "C3",
                "C3:E5",
                true
        );
    }

    @Test
    public void testTestCellRangeTopCenter() {
        this.testCellRangeCheck2(
                "D3",
                "C3:E5",
                true
        );
    }

    @Test
    public void testTestCellRangeTopRight() {
        this.testCellRangeCheck2(
                "E3",
                "C3:E5",
                true
        );
    }

    @Test
    public void testTestCellRangeMiddleLeft() {
        this.testCellRangeCheck2(
                "C4",
                "C3:E5",
                true
        );
    }

    @Test
    public void testTestCellRangeMiddleCenter() {
        this.testCellRangeCheck2(
                "D4",
                "C3:E5",
                true
        );
    }

    @Test
    public void testTestCellRangeMiddleRight() {
        this.testCellRangeCheck2(
                "E4",
                "C3:E5",
                true
        );
    }

    @Test
    public void testTestCellRangeBottomLeft() {
        this.testCellRangeCheck2(
                "D5",
                "C3:E5",
                true
        );
    }

    @Test
    public void testTestCellRangeBottomCenter() {
        this.testCellRangeCheck2(
                "D5",
                "C3:E5",
                true
        );
    }

    @Test
    public void testTestCellRangeBottomRight() {
        this.testCellRangeCheck2(
                "E5",
                "C3:E5",
                true
        );
    }

    @Test
    public void testTestCellRangeBeforeBelow() {
        this.testCellRangeCheck2(
                "B6",
                "C3:E5",
                false
        );
    }

    @Test
    public void testTestCellRangeBelow() {
        this.testCellRangeCheck2(
                "D6",
                "C3:E5",
                false
        );
    }

    @Test
    public void testTestCellRangeAfterBelow() {
        this.testCellRangeCheck2(
                "E6",
                "C3:E5",
                false
        );
    }

    @Test
    public void testTestCellRangeBeforeMiddle() {
        this.testCellRangeCheck2(
                "A4",
                "C3:E5",
                false
        );
    }

    @Test
    public void testTestCellRangeAfterMiddle() {
        this.testCellRangeCheck2(
                "F4",
                "C3:E5",
                false
        );
    }

    private void testCellRangeCheck2(final String cell,
                                     final String range,
                                     final boolean expected) {
        this.testCellRangeAndCheck(
                SpreadsheetCellReference.parseCell(cell),
                SpreadsheetCellRange.parseCellRange(range),
                expected
        );
    }

    // ParseStringTesting...............................................................................................

    @Test
    public void testParseInvalidCellReferenceFails() {
        this.parseStringFails("Invalid",
                IllegalArgumentException.class);
    }

    @Test
    public void testParseInvalidCellReferenceLastColumnPlus1Fails() {
        this.parseStringFails("XFE2",
                IllegalArgumentException.class);
    }

    @Test
    public void testParseInvalidCellReferenceLastRowPlus1Fails() {
        this.parseStringFails("B1048577",
                IllegalArgumentException.class);
    }

    @Test
    public void testParseCellReferenceRangeFails() {
        this.parseStringFails("A1:B2",
                new IllegalArgumentException("Unrecognized character ':' at (3,1) \"A1:B2\" expected (SpreadsheetColumnReference, SpreadsheetRowReference)")
        );
    }

    @Test
    public void testParseCellReferenceRelative() {
        this.parseStringAndCheck("A98",
                SpreadsheetColumnOrRowReference.column(0, SpreadsheetReferenceKind.RELATIVE)
                        .setRow(SpreadsheetColumnOrRowReference.row(97, SpreadsheetReferenceKind.RELATIVE)));
    }

    @Test
    public void testParseCellReferenceAbsolute() {
        this.parseStringAndCheck("$A$98",
                SpreadsheetColumnOrRowReference.column(0, SpreadsheetReferenceKind.ABSOLUTE)
                        .setRow(SpreadsheetColumnOrRowReference.row(97, SpreadsheetReferenceKind.ABSOLUTE)));
    }

    @Test
    public void testParseCellReferenceLastColumn() {
        this.parseStringAndCheck("XFD2",
                SpreadsheetReferenceKind.RELATIVE.lastColumn()
                        .setRow(SpreadsheetReferenceKind.RELATIVE.row(1))
        );
    }

    @Test
    public void testParseCellReferenceLastRow() {
        this.parseStringAndCheck("B1048576",
                SpreadsheetReferenceKind.RELATIVE.column(1)
                        .setRow(SpreadsheetReferenceKind.RELATIVE.lastRow())
        );
    }

    // JsonNodeMarshallingTesting.......................................................................................

    @Test
    public void testJsonNodeUnmarshallString() {
        this.unmarshallAndCheck(JsonNode.string("$A$1"),
                SpreadsheetSelection.parseCell("$A$1"));
    }

    // SpreadsheetSelectionVisitor.......................................................................................

    @Test
    public void testSpreadsheetSelectionVisitorAccept() {
        final StringBuilder b = new StringBuilder();
        final SpreadsheetCellReference selection = this.createSelection();

        new FakeSpreadsheetSelectionVisitor() {
            @Override
            protected Visiting startVisit(final SpreadsheetSelection s) {
                assertSame(selection, s);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final SpreadsheetSelection s) {
                assertSame(selection, s);
                b.append("2");
            }

            @Override
            protected void visit(final SpreadsheetCellReference s) {
                assertSame(selection, s);
                b.append("3");
            }
        }.accept(selection);
        assertEquals("132", b.toString());
    }

    // SpreadsheetExpressionReferenceVisitor.............................................................................

    @Test
    public void testSpreadsheetExpressionReferenceVisitorAccept() {
        final StringBuilder b = new StringBuilder();
        final SpreadsheetCellReference reference = this.createSelection();

        new FakeSpreadsheetExpressionReferenceVisitor() {
            @Override
            protected Visiting startVisit(final ExpressionReference r) {
                assertSame(reference, r);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final ExpressionReference r) {
                assertSame(reference, r);
                b.append("2");
            }

            @Override
            protected Visiting startVisit(final SpreadsheetExpressionReference r) {
                assertSame(reference, r);
                b.append("3");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final SpreadsheetExpressionReference r) {
                assertSame(reference, r);
                b.append("4");
            }

            @Override
            protected void visit(final SpreadsheetCellReference r) {
                assertSame(reference, r);
                b.append("5");
            }
        }.accept(reference);
        assertEquals("13542", b.toString());
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
                SpreadsheetSelection.parseCell("A12"),
                "cell A12" + EOL
        );
    }

    // equalsIgnoreReferenceKind........................................................................................

    @Test
    public void testEqualsIgnoreReferenceKindDifferentValuesFalse() {
        this.equalsIgnoreReferenceKindAndCheck("$A1",
                "$B2",
                false);
    }

    @Test
    public void testEqualsIgnoreReferenceKindDifferentReferenceKindSameValues() {
        this.equalsIgnoreReferenceKindAndCheck("$C3",
                "C3",
                true);
    }

    @Test
    public void testEqualsIgnoreReferenceKindSameReferenceKindDifferentValues() {
        this.equalsIgnoreReferenceKindAndCheck("$C3",
                "$C4",
                false);
    }

    private void equalsIgnoreReferenceKindAndCheck(final String reference1,
                                                   final String reference2,
                                                   final boolean expected) {
        this.equalsIgnoreReferenceKindAndCheck(SpreadsheetCellReference.parseCell(reference1),
                SpreadsheetCellReference.parseCell(reference2),
                expected);
    }

    // compare..........................................................................................................

    @Test
    public void testArraySort() {
        final SpreadsheetCellReference a1 = SpreadsheetSelection.parseCell("A1");
        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("$B2");
        final SpreadsheetCellReference c3 = SpreadsheetSelection.parseCell("c$3");
        final SpreadsheetCellReference d4 = SpreadsheetSelection.parseCell("$D$4");

        this.compareToArraySortAndCheck(c3, a1, d4, b2,
                a1, b2, c3, d4);
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createSelection(), "$DT$457");
    }

    @Override
    SpreadsheetCellReference createSelection() {
        return SpreadsheetCellReference.with(column(), row());
    }

    private SpreadsheetColumnReference column() {
        return this.column(COLUMN);
    }

    private SpreadsheetColumnReference column(final int value) {
        return SpreadsheetColumnOrRowReference.column(value, SpreadsheetReferenceKind.ABSOLUTE);
    }

    private SpreadsheetRowReference row() {
        return this.row(ROW);
    }

    private SpreadsheetRowReference row(final int value) {
        return SpreadsheetColumnOrRowReference.row(value, SpreadsheetReferenceKind.ABSOLUTE);
    }

    private void checkColumn(final SpreadsheetCellReference cell, final SpreadsheetColumnReference column) {
        assertEquals(column, cell.column(), "column");
    }

    private void checkRow(final SpreadsheetCellReference cell, final SpreadsheetRowReference row) {
        assertEquals(row, cell.row(), "row");
    }

    @Override
    public SpreadsheetCellReference createComparable() {
        return this.createSelection();
    }

    @Override
    public boolean compareAndEqualsMatch() {
        return false;
    }

    @Override
    public Class<SpreadsheetCellReference> type() {
        return SpreadsheetCellReference.class;
    }

    // HatoesResourceTesting............................................................................................

    @Override
    public SpreadsheetCellReference createHateosResource() {
        return this.createSelection();
    }

    // ParseStringTesting.........................................................................................

    @Override
    public SpreadsheetCellReference parseString(final String text) {
        return SpreadsheetSelection.parseCell(text);
    }

    // JsonNodeMarshallingTesting...........................................................................................

    @Override
    public SpreadsheetCellReference unmarshall(final JsonNode from,
                                               final JsonNodeUnmarshallContext context) {
        return SpreadsheetCellReference.unmarshallCellReference(from, context);
    }
}

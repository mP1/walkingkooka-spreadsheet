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
import walkingkooka.predicate.Predicates;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStore;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStores;
import walkingkooka.spreadsheet.store.SpreadsheetRowStore;
import walkingkooka.spreadsheet.store.SpreadsheetRowStores;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.visit.Visiting;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetCellReferenceTest extends SpreadsheetCellReferenceOrLabelNameTestCase<SpreadsheetCellReference>
        implements HateosResourceTesting<SpreadsheetCellReference> {

    @Test
    public void testA1Constant() {
        final SpreadsheetCellReference a1 = SpreadsheetCellReference.A1;
        this.checkColumn(a1, SpreadsheetReferenceKind.RELATIVE.firstColumn());
        this.checkRow(a1, SpreadsheetReferenceKind.RELATIVE.firstRow());
    }

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

    // isAll..........................................................................................................

    @Test
    public void testIsAll() {
        this.isAllAndCheck("A1", false);
    }

    // isFirst..........................................................................................................

    @Test
    public void testIsFirstA1() {
        this.isFirstAndCheck("A1", true);
    }

    @Test
    public void testIsFirstA1Absolute() {
        this.isFirstAndCheck("$A$1", true);
    }

    @Test
    public void testIsFirstA2() {
        this.isFirstAndCheck("A2", false);
    }

    // isLast..........................................................................................................

    @Test
    public void testIsLastA1() {
        this.isLastAndCheck("A1", false);
    }

    @Test
    public void testIsLastA1Absolute() {
        this.isLastAndCheck("$A$1", false);
    }

    @Test
    public void testIsLast() {
        this.isLastAndCheck(
                SpreadsheetReferenceKind.RELATIVE.lastColumn().setRow(SpreadsheetReferenceKind.RELATIVE.lastRow()),
                true
        );
    }

    // SetFormula.......................................................................................................

    @Test
    public void testSetFormula() {
        final SpreadsheetFormula formula = SpreadsheetFormula.EMPTY.setText("=1");
        final SpreadsheetCellReference reference = SpreadsheetSelection.parseCell("B2");

        final SpreadsheetCell cell = reference.setFormula(formula);

        this.checkEquals(
                reference,
                cell.reference(),
                "reference"
        );
        this.checkEquals(
                formula,
                cell.formula(),
                "formula"
        );
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
        this.checkEquals(
                SpreadsheetCellReference.parseCell("A2"),
                cell.addColumnSaturated(-3)
        );
    }

    @Test
    public void testAddColumnSaturationOverflows() {
        final SpreadsheetRowReference row = SpreadsheetRowReference.parseRow("2");
        final SpreadsheetCellReference cell = SpreadsheetReferenceKind.RELATIVE.column(SpreadsheetColumnReference.MAX_VALUE - 2)
                .setRow(row);
        this.checkEquals(
                SpreadsheetReferenceKind.RELATIVE.lastColumn()
                        .setRow(row),
                cell.addColumnSaturated(+3)
        );
    }

    @Test
    public void testAddColumnSaturation() {
        final SpreadsheetCellReference cell = SpreadsheetCellReference.parseCell("B2");
        this.checkEquals(
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
        this.checkEquals(
                SpreadsheetCellReference.parseCell("B1"),
                cell.addRowSaturated(-3)
        );
    }

    @Test
    public void testAddRowSaturationOverflows() {
        final SpreadsheetColumnReference column = SpreadsheetRowReference.parseColumn("B");
        final SpreadsheetCellReference cell = SpreadsheetReferenceKind.RELATIVE.row(SpreadsheetRowReference.MAX_VALUE - 2)
                .setColumn(column);
        this.checkEquals(
                column.setRow(SpreadsheetReferenceKind.RELATIVE.lastRow()),
                cell.addRowSaturated(+3)
        );
    }

    @Test
    public void testAddRowSaturation() {
        final SpreadsheetCellReference cell = SpreadsheetCellReference.parseCell("B2");
        this.checkEquals(
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

    // cellRange........................................................................................................

    @Test
    public void testCellRangeNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.cell(1, 1)
                        .cellRange((SpreadsheetCellReference) null)
        );
    }

    @Test
    public void testCellRangeOne() {
        final SpreadsheetCellReference lower = this.cell(1, 1);
        final SpreadsheetCellRange range = lower.cellRange(lower);

        this.checkEquals(
                Range.singleton(lower),
                range.range()
        );
    }

    @Test
    public void testCellRangeLeftTopRightBottom() {
        final int left = 1;
        final int top = 2;
        final int right = 3;
        final int bottom = 4;

        this.cellRangeAndCheck(
                this.cell(left, top),
                this.cell(right, bottom),
                left, top,
                right, bottom
        );
    }

    @Test
    public void testCellRangeLeftBottomRightTop() {
        final int left = 1;
        final int top = 2;
        final int right = 3;
        final int bottom = 4;

        this.cellRangeAndCheck(
                this.cell(left, bottom),
                this.cell(right, top),
                left, top,
                right, bottom
        );
    }

    @Test
    public void testCellRangeRightTopLeftBottom() {
        final int left = 1;
        final int top = 2;
        final int right = 3;
        final int bottom = 4;

        this.cellRangeAndCheck(
                this.cell(right, top),
                this.cell(left, bottom),
                left, top,
                right, bottom
        );
    }

    private void cellRangeAndCheck(final SpreadsheetCellReference cell,
                                   final SpreadsheetCellReference other,
                                   final int left,
                                   final int top,
                                   final int right,
                                   final int bottom) {
        final Range<SpreadsheetCellReference> expected = Range.greaterThanEquals(
                this.cell(left, top)
        ).and(
                Range.lessThanEquals(
                        this.cell(right, bottom)
                )
        );

        final Range<SpreadsheetCellReference> range = cell.range(other);
        this.checkEquals(
                expected,
                range,
                () -> cell + " range " + other
        );


        this.checkEquals(
                SpreadsheetCellRange.with(expected),
                cell.cellRange(other),
                () -> cell + " cellRange " + other
        );
    }

    // cellRange.......................................................................................................

    @Test
    public void testCellRangeAbsolute() {
        this.cellRangeAndCheck(
                SpreadsheetCellReference.parseCell("$B$2"),
                SpreadsheetCellRange.parseCellRange("$B$2")
        );
    }

    @Test
    public void testCellRangeRelative() {
        final String text = "C3";

        this.cellRangeAndCheck(
                SpreadsheetCellReference.parseCell(text),
                SpreadsheetCellRange.parseCellRange(text)
        );
    }

    private void cellRangeAndCheck(final SpreadsheetCellReference reference,
                                   final SpreadsheetCellRange range) {
        this.checkEquals(
                range,
                reference.cellRange(),
                () -> reference + " cellRange()"
        );
    }

    // testCellRange.....................................................................................................

    @Test
    public void testTestCellRangeBeforeAbove() {
        this.testCellRangeAndCheck(
                "B3",
                "C3:E5",
                false
        );
    }

    @Test
    public void testTestCellRangeAbove() {
        this.testCellRangeAndCheck(
                "D2",
                "C3:E5",
                false
        );
    }

    @Test
    public void testTestCellRangeAfterAbove() {
        this.testCellRangeAndCheck(
                "E2",
                "C3:E5",
                false
        );
    }

    @Test
    public void testTestCellRangeTopLeft() {
        this.testCellRangeAndCheck(
                "C3",
                "C3:E5",
                true
        );
    }

    @Test
    public void testTestCellRangeTopCenter() {
        this.testCellRangeAndCheck(
                "D3",
                "C3:E5",
                true
        );
    }

    @Test
    public void testTestCellRangeTopRight() {
        this.testCellRangeAndCheck(
                "E3",
                "C3:E5",
                true
        );
    }

    @Test
    public void testTestCellRangeMiddleLeft() {
        this.testCellRangeAndCheck(
                "C4",
                "C3:E5",
                true
        );
    }

    @Test
    public void testTestCellRangeMiddleCenter() {
        this.testCellRangeAndCheck(
                "D4",
                "C3:E5",
                true
        );
    }

    @Test
    public void testTestCellRangeMiddleRight() {
        this.testCellRangeAndCheck(
                "E4",
                "C3:E5",
                true
        );
    }

    @Test
    public void testTestCellRangeBottomLeft() {
        this.testCellRangeAndCheck(
                "D5",
                "C3:E5",
                true
        );
    }

    @Test
    public void testTestCellRangeBottomCenter() {
        this.testCellRangeAndCheck(
                "D5",
                "C3:E5",
                true
        );
    }

    @Test
    public void testTestCellRangeBottomRight() {
        this.testCellRangeAndCheck(
                "E5",
                "C3:E5",
                true
        );
    }

    @Test
    public void testTestCellRangeBeforeBelow() {
        this.testCellRangeAndCheck(
                "B6",
                "C3:E5",
                false
        );
    }

    @Test
    public void testTestCellRangeBelow() {
        this.testCellRangeAndCheck(
                "D6",
                "C3:E5",
                false
        );
    }

    @Test
    public void testTestCellRangeAfterBelow() {
        this.testCellRangeAndCheck(
                "E6",
                "C3:E5",
                false
        );
    }

    @Test
    public void testTestCellRangeBeforeMiddle() {
        this.testCellRangeAndCheck(
                "A4",
                "C3:E5",
                false
        );
    }

    @Test
    public void testTestCellRangeAfterMiddle() {
        this.testCellRangeAndCheck(
                "F4",
                "C3:E5",
                false
        );
    }

    // testColumn.......................................................................................................

    @Test
    public void testTestColumnBefore() {
        this.testColumnAndCheck(
                "C3",
                "B",
                false
        );
    }

    @Test
    public void testTestColumnAfter() {
        this.testColumnAndCheck(
                "C3",
                "D",
                false
        );
    }

    @Test
    public void testTestColumn() {
        this.testColumnAndCheck(
                "C3",
                "C",
                true
        );
    }

    // testRow.......................................................................................................

    @Test
    public void testTestRowAbove() {
        this.testRowAndCheck(
                "C3",
                "2",
                false
        );
    }

    @Test
    public void testTestRowBelow() {
        this.testRowAndCheck(
                "C3",
                "4",
                false
        );
    }

    @Test
    public void testTestRow() {
        this.testRowAndCheck(
                "C3",
                "3",
                true
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

    // SpreadsheetExpressionReference...................................................................................

    @Test
    public void testToCell() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseCell("Z99");
        assertSame(cell, cell.toCell());
    }

    // isHidden.........................................................................................................

    @Test
    public void testIsHiddenColumnHidden() {
        this.isHiddenAndCheck(
                "A1",
                Predicates.is(SpreadsheetSelection.parseColumn("A")),
                Predicates.never(),
                true
        );
    }

    @Test
    public void testIsHiddenRowHidden() {
        this.isHiddenAndCheck(
                "A1",
                Predicates.never(),
                Predicates.is(SpreadsheetSelection.parseRow("1")),
                true
        );
    }

    @Test
    public void testIsHiddenNeitherColumnOrRowNotHidden() {
        this.isHiddenAndCheck(
                "A1",
                Predicates.never(),
                Predicates.never(),
                false
        );
    }

    // navigate.........................................................................................................

    @Test
    public void testLeft() {
        this.leftAndCheck(
                "B2",
                "A2"
        );
    }

    @Test
    public void testLeftFirstColumn() {
        this.leftAndCheck(
                "A2",
                "A2"
        );
    }

    @Test
    public void testLeftLastColumn() {
        final SpreadsheetColumnReference column = SpreadsheetReferenceKind.RELATIVE.lastColumn();

        this.leftAndCheck(
                column + "1",
                column.add(-1) + "1"
        );
    }

    @Test
    public void testLeftSkipsHidden() {
        final SpreadsheetColumnStore store = SpreadsheetColumnStores.treeMap();
        store.save(SpreadsheetSelection.parseColumn("C").column().setHidden(true));

        this.leftAndCheck(
                "D1",
                store,
                "B1"
        );
    }

    @Test
    public void testLeftHiddenRow() {
        final SpreadsheetRowStore store = SpreadsheetRowStores.treeMap();
        store.save(SpreadsheetSelection.parseRow("1").row().setHidden(true));

        this.leftAndCheck(
                "D1",
                store
        );
    }
    
    @Test
    public void testUp() {
        this.upAndCheck(
                "B2",
                "B1"
        );
    }

    @Test
    public void testUpFirstRow() {
        this.upAndCheck(
                "B1",
                "B1"
        );
    }

    @Test
    public void testUpLastRow() {
        final SpreadsheetRowReference row = SpreadsheetReferenceKind.RELATIVE.lastRow();

        this.upAndCheck(
                "B" + row,
                "B" + row.add(-1)
        );
    }

    @Test
    public void testUpSkipsHidden() {
        final SpreadsheetRowStore store = SpreadsheetRowStores.treeMap();
        store.save(SpreadsheetSelection.parseRow("3").row().setHidden(true));

        this.upAndCheck(
                "B4",
                store,
                "B2"
        );
    }

    @Test
    public void testUpHiddenColumn() {
        final SpreadsheetColumnStore store = SpreadsheetColumnStores.treeMap();
        store.save(SpreadsheetSelection.parseColumn("B").column().setHidden(true));

        this.upAndCheck(
                "B2",
                store
        );
    }
    
    @Test
    public void testRight() {
        this.rightAndCheck(
                "B2",
                "C2"
        );
    }

    @Test
    public void testRightFirstColumn() {
        this.rightAndCheck(
                "A2",
                "B2"
        );
    }

    @Test
    public void testRightLastColumn() {
        final SpreadsheetColumnReference column = SpreadsheetReferenceKind.RELATIVE.lastColumn();

        this.rightAndCheck(
                column + "1",
                column + "1"
        );
    }

    @Test
    public void testRightSkipsHidden() {
        final SpreadsheetColumnStore store = SpreadsheetColumnStores.treeMap();
        store.save(SpreadsheetSelection.parseColumn("C").column().setHidden(true));

        this.rightAndCheck(
                "B1",
                store,
                "D1"
        );
    }

    @Test
    public void testRightHiddenRow() {
        final SpreadsheetRowStore store = SpreadsheetRowStores.treeMap();
        store.save(SpreadsheetSelection.parseRow("1").row().setHidden(true));

        this.rightAndCheck(
                "D1",
                store
        );
    }

    @Test
    public void testDown() {
        this.downAndCheck(
                "B2",
                "B3"
        );
    }

    @Test
    public void testDownFirstRow() {
        this.downAndCheck(
                "B1",
                "B2"
        );
    }

    @Test
    public void testDownLastRow() {
        final SpreadsheetRowReference row = SpreadsheetReferenceKind.RELATIVE.lastRow();

        this.downAndCheck(
                "B" + row,
                "B" + row
        );
    }

    @Test
    public void testDownSkipsHidden() {
        final SpreadsheetRowStore store = SpreadsheetRowStores.treeMap();
        store.save(SpreadsheetSelection.parseRow("3").row().setHidden(true));

        this.downAndCheck(
                "B2",
                store,
                "B4"
        );
    }

    @Test
    public void testDownHiddenColumn() {
        final SpreadsheetColumnStore store = SpreadsheetColumnStores.treeMap();
        store.save(SpreadsheetSelection.parseColumn("B").column().setHidden(true));

        this.downAndCheck(
                "B2",
                store
        );
    }

    // extendRange......................................................................................................

    @Test
    public void testExtendRange() {
        this.extendRangeAndCheck(
                "B2",
                "C3",
                "B2:C3"
        );
    }

    @Test
    public void testExtendRange2() {
        this.extendRangeAndCheck(
                "C3",
                "B2",
                "B2:C3"
        );
    }

    @Test
    public void testExtendRangeSame() {
        this.extendRangeAndCheck(
                "A1",
                "A1"
        );
    }

    @Test
    public void testExtendRangeSame2() {
        this.extendRangeAndCheck(
                "B2",
                "B2"
        );
    }

    @Override
    SpreadsheetCellRange parseRange(final String range) {
        return SpreadsheetSelection.parseCellRange(range);
    }

    // extendXXXX.......................................................................................................

    @Test
    public void testExtendLeft() {
        this.extendLeftAndCheck(
                "C3",
                SpreadsheetViewportSelectionAnchor.NONE,
                "B3:C3",
                SpreadsheetViewportSelectionAnchor.BOTTOM_RIGHT
        );
    }

    @Test
    public void testExtendLeftFirstColumn() {
        final String cell = "A1";

        this.extendLeftAndCheck(
                cell,
                cell
        );
    }

    @Test
    public void testExtendLeftFirstColumn2() {
        final String cell =  "A2";

        this.extendLeftAndCheck(
               cell,
                cell
        );
    }

    @Test
    public void testExtendUp() {
        this.extendUpAndCheck(
                "C3",
                SpreadsheetViewportSelectionAnchor.NONE,
                "C2:C3",
                SpreadsheetViewportSelectionAnchor.BOTTOM_RIGHT
        );
    }

    @Test
    public void testExtendUpFirstRow() {
        final String cell =  "A1";

        this.extendUpAndCheck(
               cell,
                cell
        );
    }

    @Test
    public void testExtendUpFirstRow2() {
        final String cell = "B1";

        this.extendUpAndCheck(
                cell,
                cell
        );
    }

    @Test
    public void testExtendRight() {
        this.extendRightAndCheck(
                "C3",
                SpreadsheetViewportSelectionAnchor.NONE,
                "C3:D3",
                SpreadsheetViewportSelectionAnchor.TOP_LEFT
        );
    }

    @Test
    public void testExtendRightLastColumn() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseRow("1")
                .setColumn(
                        SpreadsheetReferenceKind.RELATIVE.lastColumn()
                );

        this.extendRightAndCheck(
                cell,
                cell
        );
    }

    @Test
    public void testExtendRightLastColumn2() {
        final SpreadsheetCellReference cell =  SpreadsheetSelection.parseRow("2")
                .setColumn(
                        SpreadsheetReferenceKind.RELATIVE.lastColumn()
                );

        this.extendRightAndCheck(
               cell,
                cell
        );
    }

    @Test
    public void testExtendDown() {
        this.extendDownAndCheck(
                "B2",
                SpreadsheetViewportSelectionAnchor.NONE,
                "B2:B3",
                SpreadsheetViewportSelectionAnchor.TOP_LEFT
        );
    }

    @Test
    public void testExtendDownLastRow() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseColumn("A")
                .setRow(
                        SpreadsheetReferenceKind.RELATIVE.lastRow()
                );

        this.extendDownAndCheck(
                cell,
                cell
        );
    }

    @Test
    public void testExtendDownLastRow2() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseColumn("B")
                .setRow(
                        SpreadsheetReferenceKind.RELATIVE.lastRow()
                );

        this.extendDownAndCheck(
                cell,
                cell
        );
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
        this.checkEquals("132", b.toString());
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
        this.checkEquals("13542", b.toString());
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
    public void testEqualsIgnoreReferenceKindDifferentKind() {
        this.equalsIgnoreReferenceKindAndCheck(
                "A1",
                "$A$1",
                true
        );
    }

    @Test
    public void testEqualsIgnoreReferenceKindDifferent() {
        this.equalsIgnoreReferenceKindAndCheck(
                "A1",
                "B2",
                false
        );
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
        this.checkEquals(column, cell.column(), "column");
    }

    private void checkRow(final SpreadsheetCellReference cell, final SpreadsheetRowReference row) {
        this.checkEquals(row, cell.row(), "row");
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

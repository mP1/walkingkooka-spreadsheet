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
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetViewportAnchorTest implements ClassTesting<SpreadsheetViewportAnchor>,
        ParseStringTesting<SpreadsheetViewportAnchor> {

    // constants........................................................................................................

    @Test
    public void testCellRangeConstant() {
        this.checkEquals(
                SpreadsheetViewportAnchor.valueOf(
                        SpreadsheetViewportAnchor.ROW_RANGE + "_" + SpreadsheetViewportAnchor.COLUMN_RANGE
                ),
                SpreadsheetViewportAnchor.CELL_RANGE
        );
    }

    // opposite.........................................................................................................

    @Test
    public void testOppositeNone() {
        this.oppositeAndCheck(
                SpreadsheetViewportAnchor.NONE,
                SpreadsheetViewportAnchor.NONE
        );
    }

    @Test
    public void testOppositeLeft() {
        this.oppositeAndCheck(
                SpreadsheetViewportAnchor.LEFT,
                SpreadsheetViewportAnchor.RIGHT
        );
    }

    @Test
    public void testOppositeTop() {
        this.oppositeAndCheck(
                SpreadsheetViewportAnchor.TOP,
                SpreadsheetViewportAnchor.BOTTOM
        );
    }

    @Test
    public void testOppositeTopLeft() {
        this.oppositeAndCheck(
                SpreadsheetViewportAnchor.TOP_LEFT,
                SpreadsheetViewportAnchor.BOTTOM_RIGHT
        );
    }

    @Test
    public void testOppositeTopRight() {
        this.oppositeAndCheck(
                SpreadsheetViewportAnchor.TOP_RIGHT,
                SpreadsheetViewportAnchor.BOTTOM_LEFT
        );
    }

    private void oppositeAndCheck(final SpreadsheetViewportAnchor anchor,
                                  final SpreadsheetViewportAnchor expected) {
        oppositeAndCheck2(
                anchor,
                expected
        );
        oppositeAndCheck2(
                expected,
                anchor
        );
    }

    private void oppositeAndCheck2(final SpreadsheetViewportAnchor anchor,
                                   final SpreadsheetViewportAnchor expected) {
        assertSame(
                expected,
                anchor.opposite()
        );
    }

    // kebabText........................................................................................................

    @Test
    public void testKebabTextLeft() {
        this.kebabTextAndCheck(
                SpreadsheetViewportAnchor.LEFT,
                "left"
        );
    }

    @Test
    public void testKebabTextTopRight() {
        this.kebabTextAndCheck(
                SpreadsheetViewportAnchor.TOP_RIGHT,
                "top-right"
        );
    }

    private void kebabTextAndCheck(final SpreadsheetViewportAnchor anchor,
                                   final String expected) {
        this.checkEquals(
                expected,
                anchor.kebabText(),
                () -> anchor + " kebabText"
        );
    }

    // setLeft........................................................................................................

    @Test
    public void testSetLeft_None() {
        this.setLeftAndCheck(
                SpreadsheetViewportAnchor.NONE
        );
    }

    @Test
    public void testSetLeft_Left() {
        this.setLeftAndCheck(
                SpreadsheetViewportAnchor.LEFT
        );
    }

    @Test
    public void testSetLeft_Right() {
        this.setLeftAndCheck(
                SpreadsheetViewportAnchor.RIGHT,
                SpreadsheetViewportAnchor.LEFT
        );
    }

    @Test
    public void testSetLeft_LEFTLeft() {
        this.setLeftAndCheck(
                SpreadsheetViewportAnchor.TOP_LEFT
        );
    }

    @Test
    public void testSetLeft_LEFTRight() {
        this.setLeftAndCheck(
                SpreadsheetViewportAnchor.TOP_RIGHT,
                SpreadsheetViewportAnchor.TOP_LEFT
        );
    }

    @Test
    public void testSetLeft_BottomLeft() {
        this.setLeftAndCheck(
                SpreadsheetViewportAnchor.BOTTOM_LEFT,
                SpreadsheetViewportAnchor.BOTTOM_LEFT
        );
    }

    @Test
    public void testSetLeft_BottomRight() {
        this.setLeftAndCheck(
                SpreadsheetViewportAnchor.BOTTOM_RIGHT,
                SpreadsheetViewportAnchor.BOTTOM_LEFT
        );
    }

    private void setLeftAndCheck(final SpreadsheetViewportAnchor anchor) {
        this.setLeftAndCheck(
                anchor,
                anchor
        );
    }

    private void setLeftAndCheck(final SpreadsheetViewportAnchor anchor,
                                 final SpreadsheetViewportAnchor expected) {
        this.checkEquals(
                expected,
                anchor.setLeft(),
                () -> anchor + " setLeft"
        );
    }

    // setRight........................................................................................................

    @Test
    public void testSetRight_None() {
        this.setRightAndCheck(
                SpreadsheetViewportAnchor.NONE
        );
    }

    @Test
    public void testSetRight_Left() {
        this.setRightAndCheck(
                SpreadsheetViewportAnchor.RIGHT
        );
    }

    @Test
    public void testSetRight_Right() {
        this.setRightAndCheck(
                SpreadsheetViewportAnchor.LEFT,
                SpreadsheetViewportAnchor.RIGHT
        );
    }

    @Test
    public void testSetRight_LEFTLeft() {
        this.setRightAndCheck(
                SpreadsheetViewportAnchor.TOP_RIGHT
        );
    }

    @Test
    public void testSetRight_LEFTRight() {
        this.setRightAndCheck(
                SpreadsheetViewportAnchor.TOP_LEFT,
                SpreadsheetViewportAnchor.TOP_RIGHT
        );
    }

    @Test
    public void testSetRight_BottomLeft() {
        this.setRightAndCheck(
                SpreadsheetViewportAnchor.BOTTOM_RIGHT,
                SpreadsheetViewportAnchor.BOTTOM_RIGHT
        );
    }

    @Test
    public void testSetRight_BottomRight() {
        this.setRightAndCheck(
                SpreadsheetViewportAnchor.BOTTOM_LEFT,
                SpreadsheetViewportAnchor.BOTTOM_RIGHT
        );
    }

    private void setRightAndCheck(final SpreadsheetViewportAnchor anchor) {
        this.setRightAndCheck(
                anchor,
                anchor
        );
    }

    private void setRightAndCheck(final SpreadsheetViewportAnchor anchor,
                                  final SpreadsheetViewportAnchor expected) {
        this.checkEquals(
                expected,
                anchor.setRight(),
                () -> anchor + " setRight"
        );
    }


    // setTop........................................................................................................

    @Test
    public void testSetTop_None() {
        this.setTopAndCheck(
                SpreadsheetViewportAnchor.NONE
        );
    }

    @Test
    public void testSetTop_Top() {
        this.setTopAndCheck(
                SpreadsheetViewportAnchor.TOP
        );
    }

    @Test
    public void testSetTop_Bottom() {
        this.setTopAndCheck(
                SpreadsheetViewportAnchor.BOTTOM,
                SpreadsheetViewportAnchor.TOP
        );
    }

    @Test
    public void testSetTop_TopLeft() {
        this.setTopAndCheck(
                SpreadsheetViewportAnchor.TOP_LEFT
        );
    }

    @Test
    public void testSetTop_TopRight() {
        this.setTopAndCheck(
                SpreadsheetViewportAnchor.TOP_RIGHT
        );
    }

    @Test
    public void testSetTop_BottomLeft() {
        this.setTopAndCheck(
                SpreadsheetViewportAnchor.BOTTOM_LEFT,
                SpreadsheetViewportAnchor.TOP_LEFT
        );
    }

    @Test
    public void testSetTop_BottomRight() {
        this.setTopAndCheck(
                SpreadsheetViewportAnchor.BOTTOM_RIGHT,
                SpreadsheetViewportAnchor.TOP_RIGHT
        );
    }

    private void setTopAndCheck(final SpreadsheetViewportAnchor anchor) {
        this.setTopAndCheck(
                anchor,
                anchor
        );
    }

    private void setTopAndCheck(final SpreadsheetViewportAnchor anchor,
                                final SpreadsheetViewportAnchor expected) {
        this.checkEquals(
                expected,
                anchor.setTop(),
                () -> anchor + " setTop"
        );
    }

    // setBottom........................................................................................................

    @Test
    public void testSetBottom_None() {
        this.setBottomAndCheck(
                SpreadsheetViewportAnchor.NONE
        );
    }

    @Test
    public void testSetBottom_Left() {
        this.setBottomAndCheck(
                SpreadsheetViewportAnchor.BOTTOM
        );
    }

    @Test
    public void testSetBottom_Bottom() {
        this.setBottomAndCheck(
                SpreadsheetViewportAnchor.TOP,
                SpreadsheetViewportAnchor.BOTTOM
        );
    }

    @Test
    public void testSetBottom_TopLeft() {
        this.setBottomAndCheck(
                SpreadsheetViewportAnchor.TOP_LEFT,
                SpreadsheetViewportAnchor.BOTTOM_LEFT
        );
    }

    @Test
    public void testSetBottom_TopRight() {
        this.setBottomAndCheck(
                SpreadsheetViewportAnchor.TOP_RIGHT,
                SpreadsheetViewportAnchor.BOTTOM_RIGHT
        );
    }

    @Test
    public void testSetBottom_BottomLeft() {
        this.setBottomAndCheck(
                SpreadsheetViewportAnchor.BOTTOM_LEFT
        );
    }

    @Test
    public void testSetBottom_BottomBottomRight() {
        this.setBottomAndCheck(
                SpreadsheetViewportAnchor.BOTTOM_RIGHT
        );
    }

    private void setBottomAndCheck(final SpreadsheetViewportAnchor anchor) {
        this.setBottomAndCheck(
                anchor,
                anchor
        );
    }

    private void setBottomAndCheck(final SpreadsheetViewportAnchor anchor,
                                   final SpreadsheetViewportAnchor expected) {
        this.checkEquals(
                expected,
                anchor.setBottom(),
                () -> anchor + " setBottom"
        );
    }

    // selection................................................................................................

    @Test
    public void testSelectionCell() {
        this.selectionAndCheck(
                SpreadsheetSelection.A1,
                SpreadsheetViewportAnchor.CELL
        );
    }

    @Test
    public void testSelectionColumn() {
        this.selectionAndCheck(
                SpreadsheetSelection.parseColumn("B"),
                SpreadsheetViewportAnchor.CELL
        );
    }

    @Test
    public void testSelectionRow() {
        this.selectionAndCheck(
                SpreadsheetSelection.parseRow("3"),
                SpreadsheetViewportAnchor.CELL
        );
    }

    @Test
    public void testSelectionCellRangeTopLeft() {
        this.selectionAndCheck(
                SpreadsheetSelection.parseCellRange("A1:B2"),
                SpreadsheetViewportAnchor.TOP_LEFT,
                SpreadsheetSelection.A1
        );
    }

    // A1 B1
    // A2 B2
    @Test
    public void testSelectionCellRangeTopRight() {
        this.selectionAndCheck(
                SpreadsheetSelection.parseCellRange("A1:B2"),
                SpreadsheetViewportAnchor.TOP_RIGHT,
                SpreadsheetSelection.parseCell("B1")
        );
    }

    @Test
    public void testSelectionCellRangeBottomLeft() {
        this.selectionAndCheck(
                SpreadsheetSelection.parseCellRange("A1:B2"),
                SpreadsheetViewportAnchor.BOTTOM_LEFT,
                SpreadsheetSelection.parseCell("A2")
        );
    }

    // A1 B1
    // A2 B2
    @Test
    public void testSelectionCellRangeBottomRight() {
        this.selectionAndCheck(
                SpreadsheetSelection.parseCellRange("A1:B2"),
                SpreadsheetViewportAnchor.BOTTOM_RIGHT,
                SpreadsheetSelection.parseCell("B2")
        );
    }

    @Test
    public void testSelectionColumnRangeLeft() {
        this.selectionAndCheck(
                SpreadsheetSelection.parseColumnRange("A:B"),
                SpreadsheetViewportAnchor.LEFT,
                SpreadsheetSelection.parseColumn("A")
        );
    }

    @Test
    public void testSelectionColumnRangeRight() {
        this.selectionAndCheck(
                SpreadsheetSelection.parseColumnRange("C:D"),
                SpreadsheetViewportAnchor.RIGHT,
                SpreadsheetSelection.parseColumn("D")
        );
    }

    @Test
    public void testSelectionRowRangeTop() {
        this.selectionAndCheck(
                SpreadsheetSelection.parseRowRange("1:2"),
                SpreadsheetViewportAnchor.TOP,
                SpreadsheetSelection.parseRow("1")
        );
    }

    @Test
    public void testSelectionRowRangeBottom() {
        this.selectionAndCheck(
                SpreadsheetSelection.parseRowRange("3:4"),
                SpreadsheetViewportAnchor.BOTTOM,
                SpreadsheetSelection.parseRow("4")
        );
    }

    private void selectionAndCheck(final SpreadsheetSelection selection,
                                   final SpreadsheetViewportAnchor anchor) {
        this.selectionAndCheck(
                selection,
                anchor,
                selection
        );
    }

    private void selectionAndCheck(final SpreadsheetSelection selection,
                                   final SpreadsheetViewportAnchor anchor,
                                   final SpreadsheetSelection expected) {
        this.checkEquals(
                expected,
                anchor.selection(selection),
                () -> anchor + " selection " + selection
        );
    }

    @Test
    public void testSelectionLabelFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetViewportAnchor.NONE.selection(
                        SpreadsheetSelection.labelName("Label123")
                )
        );
    }

    // cell................................................................................,,...........................

    @Test
    public void testCellTopLeft() {
        this.cellAndCheck(
                "B2:D4",
                SpreadsheetViewportAnchor.TOP_LEFT,
                "B2"
        );
    }

    @Test
    public void testCellTopRight() {
        this.cellAndCheck(
                "B2:D4",
                SpreadsheetViewportAnchor.TOP_RIGHT,
                "D2"
        );
    }

    @Test
    public void testCellBottomLeft() {
        this.cellAndCheck(
                "B2:D4",
                SpreadsheetViewportAnchor.BOTTOM_LEFT,
                "B4"
        );
    }

    private void cellAndCheck(final String range,
                                   final SpreadsheetViewportAnchor anchor,
                                   final String cell) {
        final SpreadsheetCellRangeReference parsed = SpreadsheetSelection.parseCellRange(range);
        final SpreadsheetCellReference fixed = SpreadsheetSelection.parseCell(cell);

        this.checkEquals(
                fixed,
                anchor.cell(parsed),
                () -> anchor + " cell " + range
        );

        this.checkNotEquals(
                fixed,
                anchor.opposite().cell(parsed),
                () -> anchor + " opposite cell " + range
        );
    }

    // column...........................................................................................................

    @Test
    public void testColumnLeft() {
        this.columnAndCheck(
                "B:D",
                SpreadsheetViewportAnchor.LEFT,
                "B"
        );
    }

    @Test
    public void testColumnRight() {
        this.columnAndCheck(
                "B:D",
                SpreadsheetViewportAnchor.RIGHT,
                "D"
        );
    }

    private void columnAndCheck(final String range,
                                     final SpreadsheetViewportAnchor anchor,
                                     final String column) {
        final SpreadsheetColumnRangeReference parsed = SpreadsheetSelection.parseColumnRange(range);
        final SpreadsheetColumnReference fixed = SpreadsheetSelection.parseColumn(column);

        this.checkEquals(
                fixed,
                anchor.column(parsed),
                () -> anchor + " column " + range
        );

        this.checkNotEquals(
                fixed,
                anchor.opposite()
                        .column(parsed),
                () -> anchor + " opposite column " + range
        );
    }

    // row..............................................................................................................

    @Test
    public void testRowTop() {
        this.rowAndCheck(
                "2:4",
                SpreadsheetViewportAnchor.TOP,
                "2"
        );
    }

    @Test
    public void testRowBottom() {
        this.rowAndCheck(
                "2:4",
                SpreadsheetViewportAnchor.BOTTOM,
                "4"
        );
    }

    private void rowAndCheck(final String range,
                             final SpreadsheetViewportAnchor anchor,
                             final String row) {
        final SpreadsheetRowRangeReference parsed = SpreadsheetSelection.parseRowRange(range);
        final SpreadsheetRowReference fixed = SpreadsheetSelection.parseRow(row);

        this.checkEquals(
                fixed,
                anchor.row(parsed),
                () -> anchor + " row " + range
        );

        this.checkNotEquals(
                fixed,
                anchor.opposite()
                        .row(parsed),
                () -> anchor + " opposite row " + range
        );
    }

    // parse.............................................................................................................
    
    @Test
    public void testParseUnknownFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetViewportAnchor.parse("!")
        );
    }

    @Test
    public void testParseDifferentCaseFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetViewportAnchor.parse("TOP-LEFT")
        );
    }

    @Test
    public void testParseLeft() {
        this.parseStringAndCheck(
                "left",
                SpreadsheetViewportAnchor.LEFT
        );
    }

    @Test
    public void testParseTopLeft() {
        this.parseStringAndCheck(
                "top-left",
                SpreadsheetViewportAnchor.TOP_LEFT
        );
    }

    // ParseStringTesting...............................................................................................

    @Override
    public SpreadsheetViewportAnchor parseString(final String text) {
        return SpreadsheetViewportAnchor.parse(text);
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> type) {
        return type;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException thrown) {
        return thrown;
    }

    // toColumnRangeAnchor..............................................................................................

    @Test
    public void testToColumnRangeNoneFails() {
        this.toColumnRangeAnchorFails(SpreadsheetViewportAnchor.NONE);
    }

    @Test
    public void testToColumnRangeTopFails() {
        this.toColumnRangeAnchorFails(SpreadsheetViewportAnchor.TOP);
    }

    @Test
    public void testToColumnRangeBottomFails() {
        this.toColumnRangeAnchorFails(SpreadsheetViewportAnchor.BOTTOM);
    }

    private void toColumnRangeAnchorFails(final SpreadsheetViewportAnchor anchor) {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> anchor.toColumnRangeAnchor()
        );
        this.checkEquals(
                "Cannot convert " + anchor + " to a column range compatible anchor",
                thrown.getMessage()
        );
    }

    @Test
    public void testToColumnRangeAnchorLeft() {
        this.toColumnRangeAnchorAndCheck(
                SpreadsheetViewportAnchor.LEFT,
                SpreadsheetViewportAnchor.LEFT
        );
    }

    @Test
    public void testToColumnRangeAnchorTopLeft() {
        this.toColumnRangeAnchorAndCheck(
                SpreadsheetViewportAnchor.TOP_LEFT,
                SpreadsheetViewportAnchor.LEFT
        );
    }

    @Test
    public void testToColumnRangeAnchorRight() {
        this.toColumnRangeAnchorAndCheck(
                SpreadsheetViewportAnchor.RIGHT,
                SpreadsheetViewportAnchor.RIGHT
        );
    }

    @Test
    public void testToColumnRangeAnchorTopRight() {
        this.toColumnRangeAnchorAndCheck(
                SpreadsheetViewportAnchor.TOP_RIGHT,
                SpreadsheetViewportAnchor.RIGHT
        );
    }

    private void toColumnRangeAnchorAndCheck(final SpreadsheetViewportAnchor anchor,
                                             final SpreadsheetViewportAnchor expected) {
        assertSame(
                expected,
                anchor.toColumnRangeAnchor(),
                () -> anchor.toString()
        );
    }

    // toRowRangeAnchor..............................................................................................

    @Test
    public void testToRowRangeNoneFails() {
        this.toRowRangeAnchorFails(SpreadsheetViewportAnchor.NONE);
    }

    @Test
    public void testToRowRangeLeftFails() {
        this.toRowRangeAnchorFails(SpreadsheetViewportAnchor.LEFT);
    }

    @Test
    public void testToRowRangeRightFails() {
        this.toRowRangeAnchorFails(SpreadsheetViewportAnchor.RIGHT);
    }

    private void toRowRangeAnchorFails(final SpreadsheetViewportAnchor anchor) {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> anchor.toRowRangeAnchor()
        );
        this.checkEquals(
                "Cannot convert " + anchor + " to a row range compatible anchor",
                thrown.getMessage()
        );
    }

    @Test
    public void testToRowRangeAnchorTop() {
        this.toRowRangeAnchorAndCheck(
                SpreadsheetViewportAnchor.TOP,
                SpreadsheetViewportAnchor.TOP
        );
    }

    @Test
    public void testToRowRangeAnchorTopLeft() {
        this.toRowRangeAnchorAndCheck(
                SpreadsheetViewportAnchor.TOP_LEFT,
                SpreadsheetViewportAnchor.TOP
        );
    }

    @Test
    public void testToRowRangeAnchorTopRight() {
        this.toRowRangeAnchorAndCheck(
                SpreadsheetViewportAnchor.TOP_RIGHT,
                SpreadsheetViewportAnchor.TOP
        );
    }

    @Test
    public void testToRowRangeAnchorBottom() {
        this.toRowRangeAnchorAndCheck(
                SpreadsheetViewportAnchor.BOTTOM,
                SpreadsheetViewportAnchor.BOTTOM
        );
    }

    @Test
    public void testToRowRangeAnchorBottomLeft() {
        this.toRowRangeAnchorAndCheck(
                SpreadsheetViewportAnchor.BOTTOM_LEFT,
                SpreadsheetViewportAnchor.BOTTOM
        );
    }

    @Test
    public void testToRowRangeAnchorBottomRight() {
        this.toRowRangeAnchorAndCheck(
                SpreadsheetViewportAnchor.BOTTOM_RIGHT,
                SpreadsheetViewportAnchor.BOTTOM
        );
    }

    private void toRowRangeAnchorAndCheck(final SpreadsheetViewportAnchor anchor,
                                          final SpreadsheetViewportAnchor expected) {
        assertSame(
                expected,
                anchor.toRowRangeAnchor(),
                () -> anchor.toString()
        );
    }

    // ClassTesting....................................................................................................

    @Override
    public Class<SpreadsheetViewportAnchor> type() {
        return SpreadsheetViewportAnchor.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}

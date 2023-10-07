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

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetViewportSelectionAnchorTest implements ClassTesting<SpreadsheetViewportSelectionAnchor>,
        ParseStringTesting<SpreadsheetViewportSelectionAnchor> {

    // constants........................................................................................................

    @Test
    public void testCellRangeConstant() {
        this.checkEquals(
                SpreadsheetViewportSelectionAnchor.valueOf(
                        SpreadsheetViewportSelectionAnchor.ROW_RANGE + "_" + SpreadsheetViewportSelectionAnchor.COLUMN_RANGE
                ),
                SpreadsheetViewportSelectionAnchor.CELL_RANGE
        );
    }

    // kebabText........................................................................................................

    @Test
    public void testKebabTextLeft() {
        this.kebabTextAndCheck(
                SpreadsheetViewportSelectionAnchor.LEFT,
                "left"
        );
    }

    @Test
    public void testKebabTextTopRight() {
        this.kebabTextAndCheck(
                SpreadsheetViewportSelectionAnchor.TOP_RIGHT,
                "top-right"
        );
    }

    private void kebabTextAndCheck(final SpreadsheetViewportSelectionAnchor anchor,
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
                SpreadsheetViewportSelectionAnchor.NONE
        );
    }

    @Test
    public void testSetLeft_Left() {
        this.setLeftAndCheck(
                SpreadsheetViewportSelectionAnchor.LEFT
        );
    }

    @Test
    public void testSetLeft_Right() {
        this.setLeftAndCheck(
                SpreadsheetViewportSelectionAnchor.RIGHT,
                SpreadsheetViewportSelectionAnchor.LEFT
        );
    }

    @Test
    public void testSetLeft_LEFTLeft() {
        this.setLeftAndCheck(
                SpreadsheetViewportSelectionAnchor.TOP_LEFT
        );
    }

    @Test
    public void testSetLeft_LEFTRight() {
        this.setLeftAndCheck(
                SpreadsheetViewportSelectionAnchor.TOP_RIGHT,
                SpreadsheetViewportSelectionAnchor.TOP_LEFT
        );
    }

    @Test
    public void testSetLeft_BottomLeft() {
        this.setLeftAndCheck(
                SpreadsheetViewportSelectionAnchor.BOTTOM_LEFT,
                SpreadsheetViewportSelectionAnchor.BOTTOM_LEFT
        );
    }

    @Test
    public void testSetLeft_BottomRight() {
        this.setLeftAndCheck(
                SpreadsheetViewportSelectionAnchor.BOTTOM_RIGHT,
                SpreadsheetViewportSelectionAnchor.BOTTOM_LEFT
        );
    }

    private void setLeftAndCheck(final SpreadsheetViewportSelectionAnchor anchor) {
        this.setLeftAndCheck(
                anchor,
                anchor
        );
    }

    private void setLeftAndCheck(final SpreadsheetViewportSelectionAnchor anchor,
                                 final SpreadsheetViewportSelectionAnchor expected) {
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
                SpreadsheetViewportSelectionAnchor.NONE
        );
    }

    @Test
    public void testSetRight_Left() {
        this.setRightAndCheck(
                SpreadsheetViewportSelectionAnchor.RIGHT
        );
    }

    @Test
    public void testSetRight_Right() {
        this.setRightAndCheck(
                SpreadsheetViewportSelectionAnchor.LEFT,
                SpreadsheetViewportSelectionAnchor.RIGHT
        );
    }

    @Test
    public void testSetRight_LEFTLeft() {
        this.setRightAndCheck(
                SpreadsheetViewportSelectionAnchor.TOP_RIGHT
        );
    }

    @Test
    public void testSetRight_LEFTRight() {
        this.setRightAndCheck(
                SpreadsheetViewportSelectionAnchor.TOP_LEFT,
                SpreadsheetViewportSelectionAnchor.TOP_RIGHT
        );
    }

    @Test
    public void testSetRight_BottomLeft() {
        this.setRightAndCheck(
                SpreadsheetViewportSelectionAnchor.BOTTOM_RIGHT,
                SpreadsheetViewportSelectionAnchor.BOTTOM_RIGHT
        );
    }

    @Test
    public void testSetRight_BottomRight() {
        this.setRightAndCheck(
                SpreadsheetViewportSelectionAnchor.BOTTOM_LEFT,
                SpreadsheetViewportSelectionAnchor.BOTTOM_RIGHT
        );
    }

    private void setRightAndCheck(final SpreadsheetViewportSelectionAnchor anchor) {
        this.setRightAndCheck(
                anchor,
                anchor
        );
    }

    private void setRightAndCheck(final SpreadsheetViewportSelectionAnchor anchor,
                                  final SpreadsheetViewportSelectionAnchor expected) {
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
                SpreadsheetViewportSelectionAnchor.NONE
        );
    }

    @Test
    public void testSetTop_Top() {
        this.setTopAndCheck(
                SpreadsheetViewportSelectionAnchor.TOP
        );
    }

    @Test
    public void testSetTop_Bottom() {
        this.setTopAndCheck(
                SpreadsheetViewportSelectionAnchor.BOTTOM,
                SpreadsheetViewportSelectionAnchor.TOP
        );
    }

    @Test
    public void testSetTop_TopLeft() {
        this.setTopAndCheck(
                SpreadsheetViewportSelectionAnchor.TOP_LEFT
        );
    }

    @Test
    public void testSetTop_TopRight() {
        this.setTopAndCheck(
                SpreadsheetViewportSelectionAnchor.TOP_RIGHT
        );
    }

    @Test
    public void testSetTop_BottomLeft() {
        this.setTopAndCheck(
                SpreadsheetViewportSelectionAnchor.BOTTOM_LEFT,
                SpreadsheetViewportSelectionAnchor.TOP_LEFT
        );
    }

    @Test
    public void testSetTop_BottomRight() {
        this.setTopAndCheck(
                SpreadsheetViewportSelectionAnchor.BOTTOM_RIGHT,
                SpreadsheetViewportSelectionAnchor.TOP_RIGHT
        );
    }

    private void setTopAndCheck(final SpreadsheetViewportSelectionAnchor anchor) {
        this.setTopAndCheck(
                anchor,
                anchor
        );
    }

    private void setTopAndCheck(final SpreadsheetViewportSelectionAnchor anchor,
                                final SpreadsheetViewportSelectionAnchor expected) {
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
                SpreadsheetViewportSelectionAnchor.NONE
        );
    }

    @Test
    public void testSetBottom_Left() {
        this.setBottomAndCheck(
                SpreadsheetViewportSelectionAnchor.BOTTOM
        );
    }

    @Test
    public void testSetBottom_Bottom() {
        this.setBottomAndCheck(
                SpreadsheetViewportSelectionAnchor.TOP,
                SpreadsheetViewportSelectionAnchor.BOTTOM
        );
    }

    @Test
    public void testSetBottom_TopLeft() {
        this.setBottomAndCheck(
                SpreadsheetViewportSelectionAnchor.TOP_LEFT,
                SpreadsheetViewportSelectionAnchor.BOTTOM_LEFT
        );
    }

    @Test
    public void testSetBottom_TopRight() {
        this.setBottomAndCheck(
                SpreadsheetViewportSelectionAnchor.TOP_RIGHT,
                SpreadsheetViewportSelectionAnchor.BOTTOM_RIGHT
        );
    }

    @Test
    public void testSetBottom_BottomLeft() {
        this.setBottomAndCheck(
                SpreadsheetViewportSelectionAnchor.BOTTOM_LEFT
        );
    }

    @Test
    public void testSetBottom_BottomBottomRight() {
        this.setBottomAndCheck(
                SpreadsheetViewportSelectionAnchor.BOTTOM_RIGHT
        );
    }

    private void setBottomAndCheck(final SpreadsheetViewportSelectionAnchor anchor) {
        this.setBottomAndCheck(
                anchor,
                anchor
        );
    }

    private void setBottomAndCheck(final SpreadsheetViewportSelectionAnchor anchor,
                                   final SpreadsheetViewportSelectionAnchor expected) {
        this.checkEquals(
                expected,
                anchor.setBottom(),
                () -> anchor + " setBottom"
        );
    }


    // cell...........................................................................................................

    // B2 C2 D2
    // B3 C3 D3
    // B4 C4 D4

    @Test
    public void testCellTopLeft() {
        this.cellAndCheck(
                "B2:D4",
                SpreadsheetViewportSelectionAnchor.TOP_LEFT,
                "D4"
        );
    }

    @Test
    public void testCellBottomLeft() {
        this.cellAndCheck(
                "B2:D4",
                SpreadsheetViewportSelectionAnchor.BOTTOM_LEFT,
                "D2"
        );
    }

    @Test
    public void testCellBottomRight() {
        this.cellAndCheck(
                "B2:D4",
                SpreadsheetViewportSelectionAnchor.BOTTOM_RIGHT,
                "B2"
        );
    }

    private void cellAndCheck(final String range,
                              final SpreadsheetViewportSelectionAnchor anchor,
                              final String cell) {
        this.checkEquals(
                SpreadsheetSelection.parseCell(cell),
                anchor.cell(SpreadsheetSelection.parseCellRange(range)),
                () -> anchor + " cell " + range
        );
    }

    // fixedCell..........................................................................,,...........................

    @Test
    public void testFixedCellTopLeft() {
        this.fixedCellAndCheck(
                "B2:D4",
                SpreadsheetViewportSelectionAnchor.TOP_LEFT,
                "B2"
        );
    }

    @Test
    public void testFixedCellTopRight() {
        this.fixedCellAndCheck(
                "B2:D4",
                SpreadsheetViewportSelectionAnchor.TOP_RIGHT,
                "D2"
        );
    }

    @Test
    public void testFixedCellBottomLeft() {
        this.fixedCellAndCheck(
                "B2:D4",
                SpreadsheetViewportSelectionAnchor.BOTTOM_LEFT,
                "B4"
        );
    }

    private void fixedCellAndCheck(final String range,
                                   final SpreadsheetViewportSelectionAnchor anchor,
                                   final String cell) {
        final SpreadsheetCellRange parsed = SpreadsheetSelection.parseCellRange(range);
        final SpreadsheetCellReference fixed = SpreadsheetSelection.parseCell(cell);

        this.checkEquals(
                fixed,
                anchor.fixedCell(parsed),
                () -> anchor + " fixedCell " + range
        );

        this.checkNotEquals(
                fixed,
                anchor.cell(parsed),
                () -> anchor + " cell " + range
        );
    }

    // column...........................................................................................................

    @Test
    public void testColumnLeft() {
        this.columnAndCheck(
                "B:D",
                SpreadsheetViewportSelectionAnchor.LEFT,
                "D"
        );
    }

    @Test
    public void testColumnRight() {
        this.columnAndCheck(
                "B:D",
                SpreadsheetViewportSelectionAnchor.RIGHT,
                "B"
        );
    }

    private void columnAndCheck(final String range,
                                final SpreadsheetViewportSelectionAnchor anchor,
                                final String column) {
        this.checkEquals(
                SpreadsheetSelection.parseColumn(column),
                anchor.column(SpreadsheetSelection.parseColumnRange(range)),
                () -> anchor + " column " + range
        );
    }

    // fixedColumn.....................................................................................................

    @Test
    public void testFixedColumnLeft() {
        this.fixedColumnAndCheck(
                "B:D",
                SpreadsheetViewportSelectionAnchor.LEFT,
                "B"
        );
    }

    @Test
    public void testFixedColumnRight() {
        this.fixedColumnAndCheck(
                "B:D",
                SpreadsheetViewportSelectionAnchor.RIGHT,
                "D"
        );
    }

    private void fixedColumnAndCheck(final String range,
                                     final SpreadsheetViewportSelectionAnchor anchor,
                                     final String column) {
        final SpreadsheetColumnReferenceRange parsed = SpreadsheetSelection.parseColumnRange(range);
        final SpreadsheetColumnReference fixed = SpreadsheetSelection.parseColumn(column);

        this.checkEquals(
                fixed,
                anchor.fixedColumn(parsed),
                () -> anchor + " fixedColumn " + range
        );

        this.checkNotEquals(
                fixed,
                anchor.column(parsed),
                () -> anchor + " column " + range
        );
    }

    // row...........................................................................................................

    @Test
    public void testRowTop() {
        this.rowAndCheck(
                "2:4",
                SpreadsheetViewportSelectionAnchor.TOP,
                "4"
        );
    }

    @Test
    public void testRowBottom() {
        this.rowAndCheck(
                "2:4",
                SpreadsheetViewportSelectionAnchor.BOTTOM,
                "2"
        );
    }

    private void rowAndCheck(final String range,
                             final SpreadsheetViewportSelectionAnchor anchor,
                             final String row) {
        this.checkEquals(
                SpreadsheetSelection.parseRow(row),
                anchor.row(SpreadsheetSelection.parseRowRange(range)),
                () -> anchor + " row " + range
        );
    }

    // fixedRow.....................................................................................................

    @Test
    public void testFixedRowTop() {
        this.fixedRowAndCheck(
                "2:4",
                SpreadsheetViewportSelectionAnchor.TOP,
                "2"
        );
    }

    @Test
    public void testFixedRowBottom() {
        this.fixedRowAndCheck(
                "2:4",
                SpreadsheetViewportSelectionAnchor.BOTTOM,
                "4"
        );
    }

    private void fixedRowAndCheck(final String range,
                                  final SpreadsheetViewportSelectionAnchor anchor,
                                  final String row) {
        final SpreadsheetRowReferenceRange parsed = SpreadsheetSelection.parseRowRange(range);
        final SpreadsheetRowReference fixed = SpreadsheetSelection.parseRow(row);

        this.checkEquals(
                fixed,
                anchor.fixedRow(parsed),
                () -> anchor + " fixedRow " + range
        );

        this.checkNotEquals(
                fixed,
                anchor.row(parsed),
                () -> anchor + " row " + range
        );
    }

    // parse.............................................................................................................
    
    @Test
    public void testParseUnknownFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetViewportSelectionAnchor.parse("!")
        );
    }

    @Test
    public void testParseDifferentCaseFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetViewportSelectionAnchor.parse("TOP-LEFT")
        );
    }

    @Test
    public void testParseLeft() {
        this.parseStringAndCheck(
                "left",
                SpreadsheetViewportSelectionAnchor.LEFT
        );
    }

    @Test
    public void testParseTopLeft() {
        this.parseStringAndCheck(
                "top-left",
                SpreadsheetViewportSelectionAnchor.TOP_LEFT
        );
    }

    // ParseStringTesting...............................................................................................

    @Override
    public SpreadsheetViewportSelectionAnchor parseString(final String text) {
        return SpreadsheetViewportSelectionAnchor.parse(text);
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> type) {
        return type;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException thrown) {
        return thrown;
    }

    // ClassTesting....................................................................................................

    @Override
    public Class<SpreadsheetViewportSelectionAnchor> type() {
        return SpreadsheetViewportSelectionAnchor.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}

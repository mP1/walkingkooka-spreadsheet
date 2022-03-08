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

public final class SpreadsheetViewportSelectionAnchorTest implements ClassTesting<SpreadsheetViewportSelectionAnchor> {

    @Test
    public void testCellRangeConstant() {
        this.checkEquals(
                SpreadsheetViewportSelectionAnchor.valueOf(
                        SpreadsheetViewportSelectionAnchor.ROW_RANGE + "_" + SpreadsheetViewportSelectionAnchor.COLUMN_RANGE
                ),
                SpreadsheetViewportSelectionAnchor.CELL_RANGE
        );
    }

    // B2 C2 D2
    // B3 C3 D3
    // B4 C4 D4

    // cell...........................................................................................................

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

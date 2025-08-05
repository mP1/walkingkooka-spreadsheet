

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

package walkingkooka.spreadsheet.viewport;

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.Optional;

public final class SpreadsheetViewportNavigationSelectionExtendColumnTest extends SpreadsheetViewportNavigationSelectionExtendTestCase<SpreadsheetViewportNavigationSelectionExtendColumn, SpreadsheetColumnReference> {

    // HasText..........................................................................................................

    @Test
    public void testHasText() {
        this.textAndCheck(
            SpreadsheetViewportNavigationSelectionExtendColumn.with(SpreadsheetSelection.parseColumn("ABC")),
            "extend column ABC"
        );
    }

    // update............................................................................................................

    @Test
    public void testUpdateColumnNoPreviousSelection() {
        final SpreadsheetColumnReference column = SpreadsheetSelection.parseColumn("C");

        this.updateAndCheck(
            this.createSpreadsheetViewportNavigation(column),
            Optional.of(
                column.setDefaultAnchor()
            )
        );
    }

    @Test
    public void testUpdateColumnPreviousCell() {
        final SpreadsheetColumnReference column = SpreadsheetSelection.parseColumn("C");

        this.updateAndCheck(
            this.createSpreadsheetViewportNavigation(column),
            Optional.of(
                SpreadsheetSelection.parseCell("B2")
                    .setDefaultAnchor()
            ),
            Optional.of(
                column.setDefaultAnchor()
            )
        );
    }

    // column...........................................................................................................

    @Test
    public void testUpdateColumnPreviousSameColumn() {
        final SpreadsheetColumnReference column = SpreadsheetSelection.parseColumn("C");

        this.updateAndCheck(
            this.createSpreadsheetViewportNavigation(column),
            Optional.of(
                column.setDefaultAnchor()
            ),
            Optional.of(
                column.setDefaultAnchor()
            )
        );
    }

    @Test
    public void testUpdateColumnPreviousColumnBefore() {
        final SpreadsheetColumnReference column = SpreadsheetSelection.parseColumn("D");

        this.updateAndCheck(
            this.createSpreadsheetViewportNavigation(column),
            Optional.of(
                SpreadsheetSelection.parseColumn("C")
                    .setDefaultAnchor()
            ),
            Optional.of(
                SpreadsheetSelection.parseColumnRange("C:D")
                    .setAnchor(SpreadsheetViewportAnchor.LEFT)
            )
        );
    }

    @Test
    public void testUpdateColumnPreviousColumnAfter() {
        this.updateAndCheck(
            this.createSpreadsheetViewportNavigation(SpreadsheetSelection.parseColumn("B")),
            Optional.of(
                SpreadsheetSelection.parseColumn("C")
                    .setDefaultAnchor()
            ),
            Optional.of(
                SpreadsheetSelection.parseColumnRange("B:C")
                    .setAnchor(SpreadsheetViewportAnchor.RIGHT)
            )
        );
    }

    // column-range 1....................................................................................................

    @Test
    public void testUpdateColumnPreviousSameColumnRange() {
        final SpreadsheetColumnReference column = SpreadsheetSelection.parseColumn("C");

        this.updateAndCheck(
            this.createSpreadsheetViewportNavigation(column),
            Optional.of(
                column.toColumnRange()
                    .setDefaultAnchor()
            ),
            Optional.of(
                column.toColumnRange()
                    .setDefaultAnchor()
            )
        );
    }

    @Test
    public void testUpdateColumnPreviousColumnRangeBefore() {
        final SpreadsheetColumnReference column = SpreadsheetSelection.parseColumn("D");

        this.updateAndCheck(
            this.createSpreadsheetViewportNavigation(column),
            Optional.of(
                SpreadsheetSelection.parseColumnRange("C")
                    .setDefaultAnchor()
            ),
            Optional.of(
                SpreadsheetSelection.parseColumnRange("C:D")
                    .setAnchor(SpreadsheetViewportAnchor.LEFT)
            )
        );
    }

    @Test
    public void testUpdateColumnPreviousColumnRangeAfter() {
        this.updateAndCheck(
            this.createSpreadsheetViewportNavigation(SpreadsheetSelection.parseColumn("B")),
            Optional.of(
                SpreadsheetSelection.parseColumnRange("C")
                    .setDefaultAnchor()
            ),
            Optional.of(
                SpreadsheetSelection.parseColumnRange("B:C")
                    .setAnchor(SpreadsheetViewportAnchor.RIGHT)
            )
        );
    }

    // column-range > 1.................................................................................................

    @Test
    public void testUpdateColumnPreviousColumnRangeLeftBefore() {
        this.updateColumnRangeAndCheck(
            "E",
            "C",
            SpreadsheetViewportAnchor.LEFT,
            "C:E",
            SpreadsheetViewportAnchor.LEFT
        );
    }

    @Test
    public void testUpdateColumnPreviousColumnRangeLeftBefore2() {
        this.updateColumnRangeAndCheck(
            "D",
            "C:D",
            SpreadsheetViewportAnchor.LEFT,
            "C:D",
            SpreadsheetViewportAnchor.LEFT
        );
    }

    @Test
    public void testUpdateColumnPreviousColumnRangeLeftBefore3() {
        this.updateColumnRangeAndCheck(
            "E",
            "C:D",
            SpreadsheetViewportAnchor.LEFT,
            "C:E",
            SpreadsheetViewportAnchor.LEFT
        );
    }

    @Test
    public void testUpdateColumnPreviousColumnRangeRightBefore() {
        this.updateColumnRangeAndCheck(
            "E",
            "C:D",
            SpreadsheetViewportAnchor.RIGHT,
            "D:E",
            SpreadsheetViewportAnchor.LEFT
        );
    }

    @Test
    public void testUpdateColumnPreviousColumnRangeRightBefore2() {
        this.updateColumnRangeAndCheck(
            "D",
            "C:D",
            SpreadsheetViewportAnchor.RIGHT,
            SpreadsheetSelection.parseColumn("D")
                .setDefaultAnchor()
        );
    }

    @Test
    public void testUpdateColumnPreviousColumnRangeRightBefore3() {
        this.updateColumnRangeAndCheck(
            "E",
            "C:D",
            SpreadsheetViewportAnchor.RIGHT,
            "D:E",
            SpreadsheetViewportAnchor.LEFT
        );
    }

    @Test
    public void testUpdateColumnPreviousColumnRangeLeft() {
        this.updateColumnRangeAndCheck(
            "C",
            "C:D",
            SpreadsheetViewportAnchor.LEFT,
            SpreadsheetSelection.parseColumn("C")
                .setDefaultAnchor()
        );
    }

    @Test
    public void testUpdateColumnPreviousColumnRangeRight() {
        this.updateColumnRangeAndCheck(
            "C",
            "C:D",
            SpreadsheetViewportAnchor.RIGHT,
            "C:D",
            SpreadsheetViewportAnchor.RIGHT
        );
    }

    @Test
    public void testUpdateColumnPreviousColumnRangeLeftInside2() {
        this.updateColumnRangeAndCheck(
            "D",
            "C:D",
            SpreadsheetViewportAnchor.LEFT,
            "C:D",
            SpreadsheetViewportAnchor.LEFT
        );
    }

    @Test
    public void testUpdateColumnPreviousColumnRangeRightInside2() {
        this.updateColumnRangeAndCheck(
            "D",
            "C:D",
            SpreadsheetViewportAnchor.RIGHT,
            SpreadsheetSelection.parseColumn("D")
                .setDefaultAnchor()
        );
    }

    @Test
    public void testUpdateColumnPreviousColumnRangeLeftInside3() {
        this.updateColumnRangeAndCheck(
            "D",
            "C:E",
            SpreadsheetViewportAnchor.LEFT,
            "C:D",
            SpreadsheetViewportAnchor.LEFT
        );
    }

    @Test
    public void testUpdateColumnPreviousColumnRangeRightInside3() {
        this.updateColumnRangeAndCheck(
            "D",
            "C:E",
            SpreadsheetViewportAnchor.RIGHT,
            "D:E",
            SpreadsheetViewportAnchor.RIGHT
        );
    }

    @Test
    public void testUpdateColumnPreviousColumnRangeLeftInside4() {
        this.updateColumnRangeAndCheck(
            "E",
            "C:F",
            SpreadsheetViewportAnchor.LEFT,
            "C:E",
            SpreadsheetViewportAnchor.LEFT
        );
    }

    @Test
    public void testUpdateColumnPreviousColumnRangeRightInside4() {
        this.updateColumnRangeAndCheck(
            "E",
            "C:F",
            SpreadsheetViewportAnchor.RIGHT,
            "E:F",
            SpreadsheetViewportAnchor.RIGHT
        );
    }

    @Test
    public void testUpdateColumnPreviousColumnRangeLeftAfter() {
        this.updateColumnRangeAndCheck(
            "B",
            "C",
            SpreadsheetViewportAnchor.LEFT,
            "B:C",
            SpreadsheetViewportAnchor.RIGHT
        );
    }

    @Test
    public void testUpdateColumnPreviousColumnRangeLeftAfter2() {
        this.updateColumnRangeAndCheck(
            "C",
            "C:D",
            SpreadsheetViewportAnchor.LEFT,
            SpreadsheetSelection.parseColumn("C")
                .setDefaultAnchor()
        );
    }

    @Test
    public void testUpdateColumnPreviousColumnRangeLeftAfter3() {
        this.updateColumnRangeAndCheck(
            "B",
            "C:D",
            SpreadsheetViewportAnchor.LEFT,
            "B:C",
            SpreadsheetViewportAnchor.RIGHT
        );
    }

    @Test
    public void testUpdateColumnPreviousColumnRangeRightAfter() {
        this.updateColumnRangeAndCheck(
            "B",
            "C:D",
            SpreadsheetViewportAnchor.RIGHT,
            "B:D",
            SpreadsheetViewportAnchor.RIGHT
        );
    }

    @Test
    public void testUpdateColumnPreviousColumnRangeRightAfter2() {
        this.updateColumnRangeAndCheck(
            "C",
            "C:D",
            SpreadsheetViewportAnchor.RIGHT,
            "C:D",
            SpreadsheetViewportAnchor.RIGHT
        );
    }

    @Test
    public void testUpdateColumnPreviousColumnRangeRightAfter3() {
        this.updateColumnRangeAndCheck(
            "B",
            "C:D",
            SpreadsheetViewportAnchor.RIGHT,
            "B:D",
            SpreadsheetViewportAnchor.RIGHT
        );
    }

    private void updateColumnRangeAndCheck(final String column,
                                           final String columnRange,
                                           final SpreadsheetViewportAnchor columnRangeAnchor,
                                           final String expectedColumnRange,
                                           final SpreadsheetViewportAnchor expectedAnchor) {
        this.updateColumnRangeAndCheck(
            column,
            columnRange,
            columnRangeAnchor,
            SpreadsheetSelection.parseColumnRange(expectedColumnRange)
                .setAnchor(expectedAnchor)
        );
    }

    private void updateColumnRangeAndCheck(final String column,
                                           final String columnRange,
                                           final SpreadsheetViewportAnchor columnRangeAnchor,
                                           final AnchoredSpreadsheetSelection expected) {
        this.updateAndCheck(
            this.createSpreadsheetViewportNavigation(
                SpreadsheetSelection.parseColumn(column)
            ),
            Optional.of(
                SpreadsheetSelection.parseColumnRange(columnRange)
                    .setAnchor(columnRangeAnchor)
            ),
            Optional.of(expected)
        );
    }

    // row..............................................................................................................

    @Test
    public void testUpdateColumnPreviousRow() {
        final SpreadsheetColumnReference column = SpreadsheetSelection.parseColumn("C");

        this.updateAndCheck(
            this.createSpreadsheetViewportNavigation(column),
            Optional.of(
                SpreadsheetSelection.parseRow("2")
                    .setDefaultAnchor()
            ),
            Optional.of(
                column.setDefaultAnchor()
            )
        );
    }

    @Test
    public void testUpdateColumnMovesHome() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseCell("Z99");

        this.updateAndCheck(
            this.createSpreadsheetViewportNavigation(cell.toColumn()),
            viewportRectangle(HOME)
                .viewport(),
            viewportRectangle(cell.setRow(HOME.row()))
                .viewport()
        );
    }

    @Override
    SpreadsheetViewportNavigationSelectionExtendColumn createSpreadsheetViewportNavigation(final SpreadsheetColumnReference selection) {
        return SpreadsheetViewportNavigationSelectionExtendColumn.with(selection);
    }

    @Override
    SpreadsheetColumnReference selection() {
        return SpreadsheetSelection.A1.column();
    }

    @Override
    SpreadsheetColumnReference differentSelection() {
        return SpreadsheetSelection.parseColumn("B");
    }

    @Override
    public Class<SpreadsheetViewportNavigationSelectionExtendColumn> type() {
        return SpreadsheetViewportNavigationSelectionExtendColumn.class;
    }
}

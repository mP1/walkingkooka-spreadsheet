
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
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.Optional;

public final class SpreadsheetViewportNavigationSelectionExtendRowTest extends SpreadsheetViewportNavigationSelectionExtendTestCase<SpreadsheetViewportNavigationSelectionExtendRow, SpreadsheetRowReference> {

    // HasText..........................................................................................................

    @Test
    public void testHasText() {
        this.textAndCheck(
            SpreadsheetViewportNavigationSelectionExtendRow.with(SpreadsheetSelection.parseRow("123")),
            "extend row 123"
        );
    }

    // update............................................................................................................

    @Test
    public void testUpdateRowNoPreviousSelection() {
        final SpreadsheetRowReference row = SpreadsheetSelection.parseRow("3");

        this.updateAndCheck(
            this.createSpreadsheetViewportNavigation(row),
            Optional.of(
                row.setDefaultAnchor()
            )
        );
    }

    @Test
    public void testUpdateRowPreviousCell() {
        final SpreadsheetRowReference row = SpreadsheetSelection.parseRow("3");

        this.updateAndCheck(
            this.createSpreadsheetViewportNavigation(row),
            Optional.of(
                SpreadsheetSelection.parseCell("B2")
                    .setDefaultAnchor()
            ),
            Optional.of(
                row.setDefaultAnchor()
            )
        );
    }

    @Test
    public void testUpdateRowPreviousColumn() {
        final SpreadsheetRowReference column = SpreadsheetSelection.parseRow("3");

        this.updateAndCheck(
            this.createSpreadsheetViewportNavigation(column),
            Optional.of(
                SpreadsheetSelection.parseColumn("B")
                    .setDefaultAnchor()
            ),
            Optional.of(
                column.setDefaultAnchor()
            )
        );
    }

    // row..............................................................................................................

    // row...........................................................................................................

    @Test
    public void testUpdateRowPreviousSameRow() {
        final SpreadsheetRowReference row = SpreadsheetSelection.parseRow("3");

        this.updateAndCheck(
            this.createSpreadsheetViewportNavigation(row),
            Optional.of(
                row.setDefaultAnchor()
            ),
            Optional.of(
                row.setDefaultAnchor()
            )
        );
    }

    @Test
    public void testUpdateRowPreviousRowBefore() {
        final SpreadsheetRowReference row = SpreadsheetSelection.parseRow("4");

        this.updateAndCheck(
            this.createSpreadsheetViewportNavigation(row),
            Optional.of(
                SpreadsheetSelection.parseRow("3")
                    .setDefaultAnchor()
            ),
            Optional.of(
                SpreadsheetSelection.parseRowRange("3:4")
                    .setAnchor(SpreadsheetViewportAnchor.TOP)
            )
        );
    }

    @Test
    public void testUpdateRowPreviousRowAfter() {
        this.updateAndCheck(
            this.createSpreadsheetViewportNavigation(SpreadsheetSelection.parseRow("2")),
            Optional.of(
                SpreadsheetSelection.parseRow("3")
                    .setDefaultAnchor()
            ),
            Optional.of(
                SpreadsheetSelection.parseRowRange("2:3")
                    .setAnchor(SpreadsheetViewportAnchor.BOTTOM)
            )
        );
    }

    // row-range 1....................................................................................................

    @Test
    public void testUpdateRowPreviousSameRowRange() {
        final SpreadsheetRowReference row = SpreadsheetSelection.parseRow("3");

        this.updateAndCheck(
            this.createSpreadsheetViewportNavigation(row),
            Optional.of(
                row.toRowRange()
                    .setDefaultAnchor()
            ),
            Optional.of(
                row.toRowRange()
                    .setDefaultAnchor()
            )
        );
    }

    @Test
    public void testUpdateRowPreviousRowRangeBefore() {
        final SpreadsheetRowReference row = SpreadsheetSelection.parseRow("4");

        this.updateAndCheck(
            this.createSpreadsheetViewportNavigation(row),
            Optional.of(
                SpreadsheetSelection.parseRowRange("3")
                    .setDefaultAnchor()
            ),
            Optional.of(
                SpreadsheetSelection.parseRowRange("3:4")
                    .setAnchor(SpreadsheetViewportAnchor.TOP)
            )
        );
    }

    @Test
    public void testUpdateRowPreviousRowRangeAfter() {
        this.updateAndCheck(
            this.createSpreadsheetViewportNavigation(SpreadsheetSelection.parseRow("2")),
            Optional.of(
                SpreadsheetSelection.parseRowRange("3")
                    .setDefaultAnchor()
            ),
            Optional.of(
                SpreadsheetSelection.parseRowRange("2:3")
                    .setAnchor(SpreadsheetViewportAnchor.BOTTOM)
            )
        );
    }

    // row-range > 1.................................................................................................

    @Test
    public void testUpdateRowPreviousRowRangeTopBefore() {
        this.updateRowRangeAndCheck(
            "5",
            "3",
            SpreadsheetViewportAnchor.TOP,
            "3:5",
            SpreadsheetViewportAnchor.TOP
        );
    }

    @Test
    public void testUpdateRowPreviousRowRangeTopBefore2() {
        this.updateRowRangeAndCheck(
            "4",
            "3:4",
            SpreadsheetViewportAnchor.TOP,
            "3:4",
            SpreadsheetViewportAnchor.TOP
        );
    }

    @Test
    public void testUpdateRowPreviousRowRangeTopBefore3() {
        this.updateRowRangeAndCheck(
            "5",
            "3:4",
            SpreadsheetViewportAnchor.TOP,
            "3:5",
            SpreadsheetViewportAnchor.TOP
        );
    }

    @Test
    public void testUpdateRowPreviousRowRangeBottomBefore() {
        this.updateRowRangeAndCheck(
            "5",
            "3:4",
            SpreadsheetViewportAnchor.BOTTOM,
            "4:5",
            SpreadsheetViewportAnchor.TOP
        );
    }

    @Test
    public void testUpdateRowPreviousRowRangeBottomBefore2() {
        this.updateRowRangeAndCheck(
            "4",
            "3:4",
            SpreadsheetViewportAnchor.BOTTOM,
            SpreadsheetSelection.parseRow("4")
                .setDefaultAnchor()
        );
    }

    @Test
    public void testUpdateRowPreviousRowRangeBottomBefore3() {
        this.updateRowRangeAndCheck(
            "5",
            "3:4",
            SpreadsheetViewportAnchor.BOTTOM,
            "4:5",
            SpreadsheetViewportAnchor.TOP
        );
    }

    @Test
    public void testUpdateRowPreviousRowRangeTop() {
        this.updateRowRangeAndCheck(
            "3",
            "3:4",
            SpreadsheetViewportAnchor.TOP,
            SpreadsheetSelection.parseRow("3")
                .setDefaultAnchor()
        );
    }

    @Test
    public void testUpdateRowPreviousRowRangeBottom() {
        this.updateRowRangeAndCheck(
            "3",
            "3:4",
            SpreadsheetViewportAnchor.BOTTOM,
            "3:4",
            SpreadsheetViewportAnchor.BOTTOM
        );
    }

    @Test
    public void testUpdateRowPreviousRowRangeTopInside2() {
        this.updateRowRangeAndCheck(
            "4",
            "3:4",
            SpreadsheetViewportAnchor.TOP,
            "3:4",
            SpreadsheetViewportAnchor.TOP
        );
    }

    @Test
    public void testUpdateRowPreviousRowRangeBottomInside2() {
        this.updateRowRangeAndCheck(
            "4",
            "3:4",
            SpreadsheetViewportAnchor.BOTTOM,
            SpreadsheetSelection.parseRow("4")
                .setDefaultAnchor()
        );
    }

    @Test
    public void testUpdateRowPreviousRowRangeTopInside3() {
        this.updateRowRangeAndCheck(
            "4",
            "3:5",
            SpreadsheetViewportAnchor.TOP,
            "3:4",
            SpreadsheetViewportAnchor.TOP
        );
    }

    @Test
    public void testUpdateRowPreviousRowRangeBottomInside3() {
        this.updateRowRangeAndCheck(
            "4",
            "3:5",
            SpreadsheetViewportAnchor.BOTTOM,
            "4:5",
            SpreadsheetViewportAnchor.BOTTOM
        );
    }

    @Test
    public void testUpdateRowPreviousRowRangeTopInside4() {
        this.updateRowRangeAndCheck(
            "5",
            "3:6",
            SpreadsheetViewportAnchor.TOP,
            "3:5",
            SpreadsheetViewportAnchor.TOP
        );
    }

    @Test
    public void testUpdateRowPreviousRowRangeBottomInside4() {
        this.updateRowRangeAndCheck(
            "5",
            "3:6",
            SpreadsheetViewportAnchor.BOTTOM,
            "5:6",
            SpreadsheetViewportAnchor.BOTTOM
        );
    }

    @Test
    public void testUpdateRowPreviousRowRangeTopAfter() {
        this.updateRowRangeAndCheck(
            "2",
            "3",
            SpreadsheetViewportAnchor.TOP,
            "2:3",
            SpreadsheetViewportAnchor.BOTTOM
        );
    }

    @Test
    public void testUpdateRowPreviousRowRangeTopAfter2() {
        this.updateRowRangeAndCheck(
            "3",
            "3:4",
            SpreadsheetViewportAnchor.TOP,
            SpreadsheetSelection.parseRow("3")
                .setDefaultAnchor()
        );
    }

    @Test
    public void testUpdateRowPreviousRowRangeTopAfter3() {
        this.updateRowRangeAndCheck(
            "2",
            "3:4",
            SpreadsheetViewportAnchor.TOP,
            "2:3",
            SpreadsheetViewportAnchor.BOTTOM
        );
    }

    @Test
    public void testUpdateRowPreviousRowRangeBottomAfter() {
        this.updateRowRangeAndCheck(
            "2",
            "3:4",
            SpreadsheetViewportAnchor.BOTTOM,
            "2:4",
            SpreadsheetViewportAnchor.BOTTOM
        );
    }

    @Test
    public void testUpdateRowPreviousRowRangeBottomAfter2() {
        this.updateRowRangeAndCheck(
            "3",
            "3:4",
            SpreadsheetViewportAnchor.BOTTOM,
            "3:4",
            SpreadsheetViewportAnchor.BOTTOM
        );
    }

    @Test
    public void testUpdateRowPreviousRowRangeBottomAfter3() {
        this.updateRowRangeAndCheck(
            "2",
            "3:4",
            SpreadsheetViewportAnchor.BOTTOM,
            "2:4",
            SpreadsheetViewportAnchor.BOTTOM
        );
    }

    private void updateRowRangeAndCheck(final String row,
                                        final String rowRange,
                                        final SpreadsheetViewportAnchor rowRangeAnchor,
                                        final String expectedRowRange,
                                        final SpreadsheetViewportAnchor expectedAnchor) {
        this.updateRowRangeAndCheck(
            row,
            rowRange,
            rowRangeAnchor,
            SpreadsheetSelection.parseRowRange(expectedRowRange)
                .setAnchor(expectedAnchor)
        );
    }

    private void updateRowRangeAndCheck(final String row,
                                        final String rowRange,
                                        final SpreadsheetViewportAnchor rowRangeAnchor,
                                        final AnchoredSpreadsheetSelection expected) {
        this.updateAndCheck(
            this.createSpreadsheetViewportNavigation(
                SpreadsheetSelection.parseRow(row)
            ),
            Optional.of(
                SpreadsheetSelection.parseRowRange(rowRange)
                    .setAnchor(rowRangeAnchor)
            ),
            Optional.of(expected)
        );
    }

    @Test
    public void testUpdateRowMovesHome() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseCell("Z99");

        this.updateAndCheck(
            this.createSpreadsheetViewportNavigation(cell.toRow()),
            viewportRectangle(HOME)
                .viewport(),
            viewportRectangle(cell.setColumn(HOME.column()))
                .viewport()
        );
    }

    @Override
    SpreadsheetViewportNavigationSelectionExtendRow createSpreadsheetViewportNavigation(final SpreadsheetRowReference selection) {
        return SpreadsheetViewportNavigationSelectionExtendRow.with(selection);
    }

    @Override
    SpreadsheetRowReference selection() {
        return SpreadsheetSelection.A1.row();
    }

    @Override
    SpreadsheetRowReference differentSelection() {
        return SpreadsheetSelection.parseRow("2");
    }

    @Override
    public Class<SpreadsheetViewportNavigationSelectionExtendRow> type() {
        return SpreadsheetViewportNavigationSelectionExtendRow.class;
    }
}

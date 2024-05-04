
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

import java.util.Optional;

public final class SpreadsheetViewportNavigationSelectionExtendCellTest extends SpreadsheetViewportNavigationSelectionExtendTestCase<SpreadsheetViewportNavigationSelectionExtendCell, SpreadsheetCellReference> {

    @Test
    public void testUpdateCellNoSelection() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseCell("C3");

        this.updateAndCheck(
                this.createSpreadsheetViewportNavigation(cell),
                Optional.empty(), // no selection
                Optional.of(
                        cell.setDefaultAnchor()
                ) // new selection
        );
    }

    // cell.............................................................................................................

    @Test
    public void testUpdateCellWhenCellSame() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseCell("C3");

        this.updateAndCheck(
                this.createSpreadsheetViewportNavigation(cell),
                cell.setDefaultAnchor(),
                cell.setDefaultAnchor()
        );
    }

    @Test
    public void testUpdateCellWhenCellTopLeft() {
        this.updateCellAndCheck(
                "D4",
                "C3:D4",
                SpreadsheetViewportAnchor.TOP_LEFT
        );
    }

    @Test
    public void testUpdateCellWhenCellTopRight() {
        this.updateCellAndCheck(
                "B4",
                "B3:C4",
                SpreadsheetViewportAnchor.TOP_RIGHT
        );
    }

    @Test
    public void testUpdateCellWhenCellBottomLeft() {
        this.updateCellAndCheck(
                "D2",
                "C2:D3",
                SpreadsheetViewportAnchor.BOTTOM_LEFT
        );
    }

    @Test
    public void testUpdateCellWhenCellBottomRight() {
        this.updateCellAndCheck(
                "B2",
                "B2:C3",
                SpreadsheetViewportAnchor.BOTTOM_RIGHT
        );
    }

    private void updateCellAndCheck(final String cell,
                                    final String expectedCellRange,
                                    final SpreadsheetViewportAnchor expectedAnchor) {
        this.updateAndCheck(
                this.createSpreadsheetViewportNavigation(cell),
                SpreadsheetSelection.parseCell("C3")
                        .setDefaultAnchor(),
                SpreadsheetSelection.parseCellRange(expectedCellRange)
                        .setAnchor(expectedAnchor)
        );
    }

    // cell-range.......................................................................................................

    @Test
    public void testUpdateCellWhenCellRangeSame() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseCell("C3");

        this.updateAndCheck(
                this.createSpreadsheetViewportNavigation(cell),
                cell.setDefaultAnchor(),
                cell.setDefaultAnchor()
        );
    }

    // cell-range TOP_LEFT..............................................................................................

    @Test
    public void testUpdateCellWhenCellRangeTopLeft() {
        this.updateCellRangeAndCheck(
                "D4", // cell
                SpreadsheetViewportAnchor.TOP_LEFT, // initial anchor
                SpreadsheetViewportAnchor.TOP_LEFT
        );
    }

    @Test
    public void testUpdateCellWhenCellRangeTopLeft2() {
        this.updateCellRangeAndCheck(
                "D4", // cell
                SpreadsheetViewportAnchor.TOP_RIGHT, // initial anchor
                SpreadsheetViewportAnchor.TOP_LEFT
        );
    }

    @Test
    public void testUpdateCellWhenCellRangeTopLeft3() {
        this.updateCellRangeAndCheck(
                "E5", // cell
                SpreadsheetViewportAnchor.TOP_LEFT, // initial anchor
                "B2:E5",
                SpreadsheetViewportAnchor.TOP_LEFT
        );
    }

    @Test
    public void testUpdateCellWhenCellRangeTopLeft4() {
        this.updateCellRangeAndCheck(
                "E5", // cell
                SpreadsheetViewportAnchor.TOP_RIGHT, // initial anchor
                "B2:E5",
                SpreadsheetViewportAnchor.TOP_LEFT
        );
    }

    // cell-range TOP_RIGHT.............................................................................................

    @Test
    public void testUpdateCellWhenCellRangeTopRight() {
        this.updateCellRangeAndCheck(
                "B4", // cell
                SpreadsheetViewportAnchor.TOP_LEFT, // initial anchor
                SpreadsheetViewportAnchor.TOP_RIGHT
        );
    }

    @Test
    public void testUpdateCellWhenCellRangeTopRight2() {
        this.updateCellRangeAndCheck(
                "B4", // cell
                SpreadsheetViewportAnchor.TOP_RIGHT, // initial anchor
                SpreadsheetViewportAnchor.TOP_RIGHT
        );
    }

    @Test
    public void testUpdateCellWhenCellRangeTopRight3() {
        this.updateCellRangeAndCheck(
                "A5", // cell
                SpreadsheetViewportAnchor.TOP_LEFT, // initial anchor
                "A2:D5",
                SpreadsheetViewportAnchor.TOP_RIGHT
        );
    }

    @Test
    public void testUpdateCellWhenCellRangeTopRight4() {
        this.updateCellRangeAndCheck(
                "A5", // cell
                SpreadsheetViewportAnchor.TOP_RIGHT, // initial anchor
                "A2:D5",
                SpreadsheetViewportAnchor.TOP_RIGHT
        );
    }

    // cell-range BOTTOM_LEFT...........................................................................................

    @Test
    public void testUpdateCellWhenCellRangeBottomLeft() {
        this.updateCellRangeAndCheck(
                "D2", // cell
                SpreadsheetViewportAnchor.TOP_LEFT, // initial anchor
                SpreadsheetViewportAnchor.BOTTOM_LEFT
        );
    }

    @Test
    public void testUpdateCellWhenCellRangeBottomLeft2() {
        this.updateCellRangeAndCheck(
                "D2", // cell
                SpreadsheetViewportAnchor.TOP_RIGHT, // initial anchor
                SpreadsheetViewportAnchor.BOTTOM_LEFT
        );
    }

    @Test
    public void testUpdateCellWhenCellRangeBottomLeft3() {
        this.updateCellRangeAndCheck(
                "E1", // cell
                SpreadsheetViewportAnchor.TOP_LEFT, // initial anchor
                "B1:E4",
                SpreadsheetViewportAnchor.BOTTOM_LEFT
        );
    }

    @Test
    public void testUpdateCellWhenCellRangeBottomLeft4() {
        this.updateCellRangeAndCheck(
                "E1", // cell
                SpreadsheetViewportAnchor.TOP_RIGHT, // initial anchor
                "B1:E4",
                SpreadsheetViewportAnchor.BOTTOM_LEFT
        );
    }

    // cell-range BOTTOM_RIGHT..........................................................................................

    @Test
    public void testUpdateCellWhenCellRangeBottomRight() {
        this.updateCellRangeAndCheck(
                "B2", // cell
                SpreadsheetViewportAnchor.TOP_LEFT, // initial anchor
                SpreadsheetViewportAnchor.BOTTOM_RIGHT
        );
    }

    @Test
    public void testUpdateCellWhenCellRangeBottomRight2() {
        this.updateCellRangeAndCheck(
                "B2", // cell
                SpreadsheetViewportAnchor.TOP_RIGHT, // initial anchor
                SpreadsheetViewportAnchor.BOTTOM_RIGHT
        );
    }

    @Test
    public void testUpdateCellWhenCellRangeBottomRight3() {
        this.updateCellRangeAndCheck(
                "A1", // cell
                SpreadsheetViewportAnchor.TOP_LEFT, // initial anchor
                "A1:D4",
                SpreadsheetViewportAnchor.BOTTOM_RIGHT
        );
    }

    @Test
    public void testUpdateCellWhenCellRangeBottomRight4() {
        this.updateCellRangeAndCheck(
                "A1", // cell
                SpreadsheetViewportAnchor.TOP_RIGHT, // initial anchor
                "A1:D4",
                SpreadsheetViewportAnchor.BOTTOM_RIGHT
        );
    }

    @Test
    public void testUpdateCellWhenCellRangeBottomRight5() {
        this.updateCellRangeAndCheck(
                "A1", // cell
                SpreadsheetViewportAnchor.BOTTOM_LEFT, // initial anchor
                "A1:D4",
                SpreadsheetViewportAnchor.BOTTOM_RIGHT
        );
    }

    @Test
    public void testUpdateCellWhenCellRangeBottomRight6() {
        this.updateCellRangeAndCheck(
                "A1", // cell
                SpreadsheetViewportAnchor.BOTTOM_RIGHT, // initial anchor
                "A1:D4",
                SpreadsheetViewportAnchor.BOTTOM_RIGHT
        );
    }

    private void updateCellRangeAndCheck(final String cell,
                                         final SpreadsheetViewportAnchor initialAnchor,
                                         final SpreadsheetViewportAnchor expectedAnchor) {
        this.updateCellRangeAndCheck(
                cell,
                initialAnchor,
                "B2:D4", // expected cell-range
                expectedAnchor
        );
    }

    private void updateCellRangeAndCheck(final String cell,
                                         final SpreadsheetViewportAnchor initialAnchor,
                                         final String expectedCellRange,
                                         final SpreadsheetViewportAnchor expectedAnchor) {
        this.updateAndCheck(
                this.createSpreadsheetViewportNavigation(cell),
                SpreadsheetSelection.parseCellRange("B2:D4")
                        .setAnchor(initialAnchor),
                SpreadsheetSelection.parseCellRange(expectedCellRange)
                        .setAnchor(expectedAnchor)
        );
    }

    // column...........................................................................................................

    @Test
    public void testUpdateCellWhenColumn() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseCell("C3");

        this.updateAndCheck(
                this.createSpreadsheetViewportNavigation(cell),
                SpreadsheetSelection.parseColumn("A")
                        .setDefaultAnchor(),
                cell.setDefaultAnchor() // no selection
        );
    }

    @Test
    public void testUpdateCellWhenRow() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseCell("C3");

        this.updateAndCheck(
                this.createSpreadsheetViewportNavigation(cell),
                SpreadsheetSelection.parseRow("1")
                        .setDefaultAnchor(),
                cell.setDefaultAnchor() // no selection
        );
    }

    @Test
    public void testUpdateCellMovesHome() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseCell("Z99");

        this.updateAndCheck(
                this.createSpreadsheetViewportNavigation(cell),
                viewportRectangle(HOME)
                        .viewport(),
                viewportRectangle(cell)
                        .viewport()
        );
    }

    private SpreadsheetViewportNavigationSelectionExtendCell createSpreadsheetViewportNavigation(final String selection) {
        return this.createSpreadsheetViewportNavigation(
                SpreadsheetSelection.parseCell(selection)
        );
    }

    @Override
    SpreadsheetViewportNavigationSelectionExtendCell createSpreadsheetViewportNavigation(final SpreadsheetCellReference selection) {
        return SpreadsheetViewportNavigationSelectionExtendCell.with(selection);
    }

    @Override
    SpreadsheetCellReference selection() {
        return SpreadsheetSelection.A1;
    }

    @Override
    SpreadsheetCellReference differentSelection() {
        return SpreadsheetSelection.parseCell("B2");
    }

    @Override
    public Class<SpreadsheetViewportNavigationSelectionExtendCell> type() {
        return SpreadsheetViewportNavigationSelectionExtendCell.class;
    }
}

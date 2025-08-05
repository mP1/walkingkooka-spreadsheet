
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
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.Optional;

public final class SpreadsheetViewportNavigationSelectionExtendCellTest extends SpreadsheetViewportNavigationSelectionExtendTestCase<SpreadsheetViewportNavigationSelectionExtendCell, SpreadsheetCellReference> {

    // HasText..........................................................................................................

    @Test
    public void testHasText() {
        this.textAndCheck(
            SpreadsheetViewportNavigationSelectionExtendCell.with(SpreadsheetSelection.parseCell("ABC123")),
            "extend cell ABC123"
        );
    }

    // update............................................................................................................

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

    // different cell...................................................................................................

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

    private final static String OUTSIDE_TOP_LEFT = "A1";

    private final static String OUTSIDE_TOP_RIGHT = "E1";

    private final static String OUTSIDE_BOTTOM_RIGHT = "E5";

    private final static String OUTSIDE_BOTTOM_LEFT = "A5";


    private final static String INSIDE_TOP_LEFT = "B2";

    private final static String INSIDE_TOP_RIGHT = "D2";

    private final static String INSIDE_BOTTOM_RIGHT = "D4";

    private final static String INSIDE_BOTTOM_LEFT = "B4";

    private final static String CENTER = "C3";

    // cell-range TOP_LEFT..............................................................................................

    @Test
    public void testUpdateCellWhenCellRangeTopLeftTopLeftOutside() {
        this.updateCellRangeAndCheck(
            OUTSIDE_TOP_LEFT, // cell
            SpreadsheetViewportAnchor.TOP_LEFT, // initial anchor
            "A1:B2",
            SpreadsheetViewportAnchor.BOTTOM_RIGHT
        );
    }

    @Test
    public void testUpdateCellWhenCellRangeTopLeftTopRightOutside() {
        this.updateCellRangeAndCheck(
            OUTSIDE_TOP_RIGHT, // cell
            SpreadsheetViewportAnchor.TOP_LEFT, // initial anchor
            "B1:E2",
            SpreadsheetViewportAnchor.BOTTOM_LEFT
        );
    }

    @Test
    public void testUpdateCellWhenCellRangeTopLeftBottomRightOutside() {
        this.updateCellRangeAndCheck(
            OUTSIDE_BOTTOM_RIGHT, // cell
            SpreadsheetViewportAnchor.TOP_LEFT, // initial anchor
            "B2:E5",
            SpreadsheetViewportAnchor.TOP_LEFT
        );
    }

    @Test
    public void testUpdateCellWhenCellRangeTopLeftBottomLeftOutside() {
        this.updateCellRangeAndCheck(
            OUTSIDE_BOTTOM_LEFT, // cell
            SpreadsheetViewportAnchor.TOP_LEFT, // initial anchor
            "A2:B5",
            SpreadsheetViewportAnchor.TOP_RIGHT
        );
    }

    @Test
    public void testUpdateCellWhenCellRangeTopLeftTopLeftInside() {
        this.updateCellRangeAndCheck(
            INSIDE_TOP_LEFT, // cell
            SpreadsheetViewportAnchor.TOP_LEFT, // initial anchor
            SpreadsheetSelection.parseCell(INSIDE_TOP_LEFT)
                .setDefaultAnchor()
        );
    }

    @Test
    public void testUpdateCellWhenCellRangeTopLeftTopRightInside() {
        this.updateCellRangeAndCheck(
            INSIDE_TOP_RIGHT, // cell
            SpreadsheetViewportAnchor.TOP_LEFT, // initial anchor
            "B2:D2",
            SpreadsheetViewportAnchor.TOP_LEFT
        );
    }

    @Test
    public void testUpdateCellWhenCellRangeTopLeftBottomRightInside() {
        this.updateCellRangeAndCheck(
            INSIDE_BOTTOM_RIGHT, // cell
            SpreadsheetViewportAnchor.TOP_LEFT, // initial anchor
            "B2:D4",
            SpreadsheetViewportAnchor.TOP_LEFT
        );
    }

    @Test
    public void testUpdateCellWhenCellRangeTopLeftBottomLeftInside() {
        this.updateCellRangeAndCheck(
            INSIDE_BOTTOM_LEFT, // cell
            SpreadsheetViewportAnchor.TOP_LEFT, // initial anchor
            "B2:B4",
            SpreadsheetViewportAnchor.TOP_LEFT
        );
    }

    @Test
    public void testUpdateCellWhenCellRangeTopLeftCenter() {
        this.updateCellRangeAndCheck(
            CENTER, // cell
            SpreadsheetViewportAnchor.TOP_LEFT, // initial anchor
            "B2:C3",
            SpreadsheetViewportAnchor.TOP_LEFT
        );
    }

    // cell-range TOP_RIGHT.............................................................................................

    @Test
    public void testUpdateCellWhenCellRangeTopRightTopLeftOutside() {
        this.updateCellRangeAndCheck(
            OUTSIDE_TOP_LEFT, // cell
            SpreadsheetViewportAnchor.TOP_RIGHT, // initial anchor
            "A1:D2",
            SpreadsheetViewportAnchor.BOTTOM_RIGHT
        );
    }

    @Test
    public void testUpdateCellWhenCellRangeTopRightTopRightOutside() {
        this.updateCellRangeAndCheck(
            OUTSIDE_TOP_RIGHT, // cell
            SpreadsheetViewportAnchor.TOP_RIGHT, // initial anchor
            "D1:E2",
            SpreadsheetViewportAnchor.BOTTOM_LEFT
        );
    }

    @Test
    public void testUpdateCellWhenCellRangeTopRightBottomRightOutside() {
        this.updateCellRangeAndCheck(
            OUTSIDE_BOTTOM_RIGHT, // cell
            SpreadsheetViewportAnchor.TOP_RIGHT, // initial anchor
            "D2:E5",
            SpreadsheetViewportAnchor.TOP_LEFT
        );
    }

    @Test
    public void testUpdateCellWhenCellRangeTopRightBottomLeftOutside() {
        this.updateCellRangeAndCheck(
            OUTSIDE_BOTTOM_LEFT, // cell
            SpreadsheetViewportAnchor.TOP_RIGHT, // initial anchor
            "A2:D5",
            SpreadsheetViewportAnchor.TOP_RIGHT
        );
    }

    @Test
    public void testUpdateCellWhenCellRangeTopRightTopLeftInside() {
        this.updateCellRangeAndCheck(
            INSIDE_TOP_LEFT, // cell
            SpreadsheetViewportAnchor.TOP_RIGHT, // initial anchor
            SpreadsheetSelection.parseCellRange("B2:D2")
                .setAnchor(SpreadsheetViewportAnchor.TOP_RIGHT)
        );
    }

    @Test
    public void testUpdateCellWhenCellRangeTopRightTopRightInside() {
        this.updateCellRangeAndCheck(
            INSIDE_TOP_RIGHT, // cell
            SpreadsheetViewportAnchor.TOP_RIGHT, // initial anchor
            SpreadsheetSelection.parseCell(INSIDE_TOP_RIGHT)
                .setDefaultAnchor()
        );
    }

    @Test
    public void testUpdateCellWhenCellRangeTopRightBottomRightInside() {
        this.updateCellRangeAndCheck(
            INSIDE_BOTTOM_RIGHT, // cell
            SpreadsheetViewportAnchor.TOP_RIGHT, // initial anchor
            "D2:D4",
            SpreadsheetViewportAnchor.TOP_LEFT
        );
    }

    @Test
    public void testUpdateCellWhenCellRangeTopRightBottomLeftInside() {
        this.updateCellRangeAndCheck(
            INSIDE_BOTTOM_LEFT, // cell
            SpreadsheetViewportAnchor.TOP_RIGHT, // initial anchor
            "B2:D4",
            SpreadsheetViewportAnchor.TOP_RIGHT
        );
    }

    @Test
    public void testUpdateCellWhenCellRangeTopRightCenter() {
        this.updateCellRangeAndCheck(
            CENTER, // cell
            SpreadsheetViewportAnchor.TOP_RIGHT, // initial anchor
            "C2:D3",
            SpreadsheetViewportAnchor.TOP_RIGHT
        );
    }

    // cell-range BOTTOM_RIGHT..........................................................................................

    @Test
    public void testUpdateCellWhenCellRangeBottomRightTopLeftOutside() {
        this.updateCellRangeAndCheck(
            OUTSIDE_TOP_LEFT, // cell
            SpreadsheetViewportAnchor.BOTTOM_RIGHT, // initial anchor
            "A1:D4",
            SpreadsheetViewportAnchor.BOTTOM_RIGHT
        );
    }

    @Test
    public void testUpdateCellWhenCellRangeBottomRightTopRightOutside() {
        this.updateCellRangeAndCheck(
            OUTSIDE_TOP_RIGHT, // cell
            SpreadsheetViewportAnchor.BOTTOM_RIGHT, // initial anchor
            "D1:E4",
            SpreadsheetViewportAnchor.BOTTOM_LEFT
        );
    }

    @Test
    public void testUpdateCellWhenCellRangeBottomRightBottomRightOutside() {
        this.updateCellRangeAndCheck(
            OUTSIDE_BOTTOM_RIGHT, // cell
            SpreadsheetViewportAnchor.BOTTOM_RIGHT, // initial anchor
            "D4:E5",
            SpreadsheetViewportAnchor.TOP_LEFT
        );
    }

    @Test
    public void testUpdateCellWhenCellRangeBottomRightBottomLeftOutside() {
        this.updateCellRangeAndCheck(
            OUTSIDE_BOTTOM_LEFT, // cell
            SpreadsheetViewportAnchor.BOTTOM_RIGHT, // initial anchor
            "A4:D5",
            SpreadsheetViewportAnchor.TOP_RIGHT
        );
    }

    @Test
    public void testUpdateCellWhenCellRangeBottomRightTopLeftInside() {
        this.updateCellRangeAndCheck(
            INSIDE_TOP_LEFT, // cell
            SpreadsheetViewportAnchor.BOTTOM_RIGHT, // initial anchor
            SpreadsheetSelection.parseCellRange("B2:D4")
                .setAnchor(SpreadsheetViewportAnchor.BOTTOM_RIGHT)
        );
    }

    @Test
    public void testUpdateCellWhenCellRangeBottomRightTopRightInside() {
        this.updateCellRangeAndCheck(
            INSIDE_TOP_RIGHT, // cell
            SpreadsheetViewportAnchor.BOTTOM_RIGHT, // initial anchor
            "D2:D4",
            SpreadsheetViewportAnchor.BOTTOM_LEFT
        );
    }

    @Test
    public void testUpdateCellWhenCellRangeBottomRightBottomRightInside() {
        this.updateCellRangeAndCheck(
            INSIDE_BOTTOM_RIGHT, // cell
            SpreadsheetViewportAnchor.BOTTOM_RIGHT, // initial anchor
            SpreadsheetSelection.parseCell(INSIDE_BOTTOM_RIGHT)
                .setDefaultAnchor()
        );
    }

    @Test
    public void testUpdateCellWhenCellRangeBottomRightBottomLeftInside() {
        this.updateCellRangeAndCheck(
            INSIDE_BOTTOM_LEFT, // cell
            SpreadsheetViewportAnchor.BOTTOM_RIGHT, // initial anchor
            "B4:D4",
            SpreadsheetViewportAnchor.TOP_RIGHT
        );
    }

    @Test
    public void testUpdateCellWhenCellRangeBottomRightCenter() {
        this.updateCellRangeAndCheck(
            CENTER, // cell
            SpreadsheetViewportAnchor.BOTTOM_RIGHT, // initial anchor
            "C3:D4",
            SpreadsheetViewportAnchor.BOTTOM_RIGHT
        );
    }

    // cell-range BOTTOM_LEFT..........................................................................................

    @Test
    public void testUpdateCellWhenCellRangeBottomLeftTopLeftOutside() {
        this.updateCellRangeAndCheck(
            OUTSIDE_TOP_LEFT, // cell
            SpreadsheetViewportAnchor.BOTTOM_LEFT, // initial anchor
            "A1:B4",
            SpreadsheetViewportAnchor.BOTTOM_RIGHT
        );
    }

    @Test
    public void testUpdateCellWhenCellRangeBottomLeftTopRightOutside() {
        this.updateCellRangeAndCheck(
            OUTSIDE_TOP_RIGHT, // cell
            SpreadsheetViewportAnchor.BOTTOM_LEFT, // initial anchor
            "B1:E4",
            SpreadsheetViewportAnchor.BOTTOM_LEFT
        );
    }

    @Test
    public void testUpdateCellWhenCellRangeBottomLeftBottomRightOutside() {
        this.updateCellRangeAndCheck(
            OUTSIDE_BOTTOM_RIGHT, // cell
            SpreadsheetViewportAnchor.BOTTOM_LEFT, // initial anchor
            "B4:E5",
            SpreadsheetViewportAnchor.TOP_LEFT
        );
    }

    @Test
    public void testUpdateCellWhenCellRangeBottomLeftBottomLeftOutside() {
        this.updateCellRangeAndCheck(
            OUTSIDE_BOTTOM_LEFT, // cell
            SpreadsheetViewportAnchor.BOTTOM_LEFT, // initial anchor
            "A4:B5",
            SpreadsheetViewportAnchor.TOP_RIGHT
        );
    }

    @Test
    public void testUpdateCellWhenCellRangeBottomLeftTopLeftInside() {
        this.updateCellRangeAndCheck(
            INSIDE_TOP_LEFT, // cell
            SpreadsheetViewportAnchor.BOTTOM_LEFT, // initial anchor
            "B2:B4",
            SpreadsheetViewportAnchor.BOTTOM_LEFT
        );
    }

    @Test
    public void testUpdateCellWhenCellRangeBottomLeftTopRightInside() {
        this.updateCellRangeAndCheck(
            INSIDE_TOP_RIGHT, // cell
            SpreadsheetViewportAnchor.BOTTOM_LEFT, // initial anchor
            "B2:D4",
            SpreadsheetViewportAnchor.BOTTOM_LEFT
        );
    }

    @Test
    public void testUpdateCellWhenCellRangeBottomLeftBottomRightInside() {
        this.updateCellRangeAndCheck(
            INSIDE_BOTTOM_RIGHT, // cell
            SpreadsheetViewportAnchor.BOTTOM_LEFT, // initial anchor
            "B4:D4",
            SpreadsheetViewportAnchor.TOP_LEFT
        );
    }

    @Test
    public void testUpdateCellWhenCellRangeBottomLeftBottomLeftInside() {
        this.updateCellRangeAndCheck(
            INSIDE_BOTTOM_LEFT, // cell
            SpreadsheetViewportAnchor.BOTTOM_LEFT, // initial anchor
            SpreadsheetSelection.parseCell(INSIDE_BOTTOM_LEFT)
                .setDefaultAnchor()
        );
    }

    @Test
    public void testUpdateCellWhenCellRangeBottomLeftCenter() {
        this.updateCellRangeAndCheck(
            CENTER, // cell
            SpreadsheetViewportAnchor.BOTTOM_LEFT, // initial anchor
            "B3:C4",
            SpreadsheetViewportAnchor.BOTTOM_LEFT
        );
    }

    // updateCellRange helpers..........................................................................................

    private void updateCellRangeAndCheck(final String cell,
                                         final SpreadsheetViewportAnchor initialAnchor,
                                         final String expectedCellRange,
                                         final SpreadsheetViewportAnchor expectedAnchor) {
        this.updateCellRangeAndCheck(
            cell,
            initialAnchor,
            SpreadsheetSelection.parseCellRange(expectedCellRange)
                .setAnchor(expectedAnchor)
        );
    }

    private void updateCellRangeAndCheck(final String cell,
                                         final SpreadsheetViewportAnchor initialAnchor,
                                         final AnchoredSpreadsheetSelection expected) {
        this.updateAndCheck(
            this.createSpreadsheetViewportNavigation(cell),
            SpreadsheetSelection.parseCellRange("B2:D4")
                .setAnchor(initialAnchor),
            expected
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

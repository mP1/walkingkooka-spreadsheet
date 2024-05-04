
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
    public void testUpdateCellWhenDifferentCellTopLeft() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseCell("C3");

        this.updateAndCheck(
                this.createSpreadsheetViewportNavigation(cell),
                SpreadsheetSelection.A1.setDefaultAnchor(),
                SpreadsheetSelection.parseCellRange("A1:C3")
                        .setAnchor(SpreadsheetViewportAnchor.TOP_LEFT)
        );
    }

    @Test
    public void testUpdateCellWhenDifferentCellTopRight() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseCell("C3");

        this.updateAndCheck(
                this.createSpreadsheetViewportNavigation(cell),
                SpreadsheetSelection.parseCell("E1")
                        .setDefaultAnchor(),
                SpreadsheetSelection.parseCellRange("C1:E3")
                        .setAnchor(SpreadsheetViewportAnchor.TOP_RIGHT)
        );
    }

    @Test
    public void testUpdateCellWhenDifferentCellBottomLeft() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseCell("C3");

        this.updateAndCheck(
                this.createSpreadsheetViewportNavigation(cell),
                SpreadsheetSelection.parseCell("A5")
                        .setDefaultAnchor(),
                SpreadsheetSelection.parseCellRange("A3:C5")
                        .setAnchor(SpreadsheetViewportAnchor.BOTTOM_LEFT)
        );
    }

    @Test
    public void testUpdateCellWhenDifferentCellBottomRight() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseCell("C3");

        this.updateAndCheck(
                this.createSpreadsheetViewportNavigation(cell),
                SpreadsheetSelection.parseCell("E5")
                        .setDefaultAnchor(),
                SpreadsheetSelection.parseCellRange("C3:E5")
                        .setAnchor(SpreadsheetViewportAnchor.BOTTOM_RIGHT)
        );
    }

    @Test
    public void testUpdateCellWhenCellRangeSame() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseCell("C3");

        this.updateAndCheck(
                this.createSpreadsheetViewportNavigation(cell),
                cell.setDefaultAnchor(),
                cell.setDefaultAnchor()
        );
    }

    @Test
    public void testUpdateCellWhenDifferentCellRangeTopLeft() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseCell("C3");

        this.updateAndCheck(
                this.createSpreadsheetViewportNavigation(cell),
                SpreadsheetSelection.A1.setDefaultAnchor(),
                SpreadsheetSelection.parseCellRange("A1:C3")
                        .setAnchor(SpreadsheetViewportAnchor.TOP_LEFT)
        );
    }

    @Test
    public void testUpdateCellWhenDifferentCellRangeTopRight() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseCell("C3");

        this.updateAndCheck(
                this.createSpreadsheetViewportNavigation(cell),
                SpreadsheetSelection.parseCellRange("E1")
                        .setDefaultAnchor(),
                SpreadsheetSelection.parseCellRange("C1:E3")
                        .setAnchor(SpreadsheetViewportAnchor.TOP_RIGHT)
        );
    }

    @Test
    public void testUpdateCellWhenDifferentCellRangeBottomLeft() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseCell("C3");

        this.updateAndCheck(
                this.createSpreadsheetViewportNavigation(cell),
                SpreadsheetSelection.parseCellRange("A5")
                        .setDefaultAnchor(),
                SpreadsheetSelection.parseCellRange("A3:C5")
                        .setAnchor(SpreadsheetViewportAnchor.BOTTOM_LEFT)
        );
    }

    @Test
    public void testUpdateCellWhenDifferentCellRangeBottomRight() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseCell("C3");

        this.updateAndCheck(
                this.createSpreadsheetViewportNavigation(cell),
                SpreadsheetSelection.parseCellRange("E5")
                        .setDefaultAnchor(),
                SpreadsheetSelection.parseCellRange("C3:E5")
                        .setAnchor(SpreadsheetViewportAnchor.BOTTOM_RIGHT)
        );
    }

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

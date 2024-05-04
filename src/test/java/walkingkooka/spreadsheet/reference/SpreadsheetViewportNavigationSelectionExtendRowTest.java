
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

public final class SpreadsheetViewportNavigationSelectionExtendRowTest extends SpreadsheetViewportNavigationSelectionExtendTestCase<SpreadsheetViewportNavigationSelectionExtendRow, SpreadsheetRowReference> {

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

    @Test
    public void testUpdateRowPreviousRowRangeTopBefore() {
        this.updateAndCheck(
                this.createSpreadsheetViewportNavigation(
                        SpreadsheetSelection.parseRow("5")
                ),
                Optional.of(
                        SpreadsheetSelection.parseRowRange("3")
                                .setAnchor(SpreadsheetViewportAnchor.TOP)
                ),
                Optional.of(
                        SpreadsheetSelection.parseRowRange("3:5")
                                .setAnchor(SpreadsheetViewportAnchor.TOP)
                )
        );
    }

    @Test
    public void testUpdateRowPreviousRowRangeTopBefore2() {
        this.updateAndCheck(
                this.createSpreadsheetViewportNavigation(
                        SpreadsheetSelection.parseRow("4")
                ),
                Optional.of(
                        SpreadsheetSelection.parseRowRange("3:4")
                                .setAnchor(SpreadsheetViewportAnchor.TOP)
                ),
                Optional.of(
                        SpreadsheetSelection.parseRowRange("3:4")
                                .setAnchor(SpreadsheetViewportAnchor.TOP)
                )
        );
    }

    @Test
    public void testUpdateRowPreviousRowRangeTopBefore3() {
        this.updateAndCheck(
                this.createSpreadsheetViewportNavigation(
                        SpreadsheetSelection.parseRow("5")
                ),
                Optional.of(
                        SpreadsheetSelection.parseRowRange("3:4")
                                .setAnchor(SpreadsheetViewportAnchor.TOP)
                ),
                Optional.of(
                        SpreadsheetSelection.parseRowRange("3:5")
                                .setAnchor(SpreadsheetViewportAnchor.TOP)
                )
        );
    }

    @Test
    public void testUpdateRowPreviousRowRangeBottomBefore() {
        this.updateAndCheck(
                this.createSpreadsheetViewportNavigation(
                        SpreadsheetSelection.parseRow("5")
                ),
                Optional.of(
                        SpreadsheetSelection.parseRowRange("3:4")
                                .setAnchor(SpreadsheetViewportAnchor.BOTTOM)
                ),
                Optional.of(
                        SpreadsheetSelection.parseRowRange("3:5")
                                .setAnchor(SpreadsheetViewportAnchor.TOP)
                )
        );
    }

    @Test
    public void testUpdateRowPreviousRowRangeBottomBefore2() {
        this.updateAndCheck(
                this.createSpreadsheetViewportNavigation(
                        SpreadsheetSelection.parseRow("4")
                ),
                Optional.of(
                        SpreadsheetSelection.parseRowRange("3:4")
                                .setAnchor(SpreadsheetViewportAnchor.BOTTOM)
                ),
                Optional.of(
                        SpreadsheetSelection.parseRowRange("3:4")
                                .setAnchor(SpreadsheetViewportAnchor.TOP)
                )
        );
    }

    @Test
    public void testUpdateRowPreviousRowRangeBottomBefore3() {
        this.updateAndCheck(
                this.createSpreadsheetViewportNavigation(
                        SpreadsheetSelection.parseRow("5")
                ),
                Optional.of(
                        SpreadsheetSelection.parseRowRange("3:4")
                                .setAnchor(SpreadsheetViewportAnchor.BOTTOM)
                ),
                Optional.of(
                        SpreadsheetSelection.parseRowRange("3:5")
                                .setAnchor(SpreadsheetViewportAnchor.TOP)
                )
        );
    }

    @Test
    public void testUpdateRowPreviousRowRangeTopAfter() {
        this.updateAndCheck(
                this.createSpreadsheetViewportNavigation(
                        SpreadsheetSelection.parseRow("2")
                ),
                Optional.of(
                        SpreadsheetSelection.parseRowRange("3")
                                .setAnchor(SpreadsheetViewportAnchor.TOP)
                ),
                Optional.of(
                        SpreadsheetSelection.parseRowRange("2:3")
                                .setAnchor(SpreadsheetViewportAnchor.BOTTOM)
                )
        );
    }

    @Test
    public void testUpdateRowPreviousRowRangeTopAfter2() {
        this.updateAndCheck(
                this.createSpreadsheetViewportNavigation(
                        SpreadsheetSelection.parseRow("3")
                ),
                Optional.of(
                        SpreadsheetSelection.parseRowRange("3:4")
                                .setAnchor(SpreadsheetViewportAnchor.TOP)
                ),
                Optional.of(
                        SpreadsheetSelection.parseRowRange("3:4")
                                .setAnchor(SpreadsheetViewportAnchor.BOTTOM)
                )
        );
    }

    @Test
    public void testUpdateRowPreviousRowRangeTopAfter3() {
        this.updateAndCheck(
                this.createSpreadsheetViewportNavigation(
                        SpreadsheetSelection.parseRow("2")
                ),
                Optional.of(
                        SpreadsheetSelection.parseRowRange("3:4")
                                .setAnchor(SpreadsheetViewportAnchor.TOP)
                ),
                Optional.of(
                        SpreadsheetSelection.parseRowRange("2:4")
                                .setAnchor(SpreadsheetViewportAnchor.BOTTOM)
                )
        );
    }

    @Test
    public void testUpdateRowPreviousRowRangeBottomAfter() {
        this.updateAndCheck(
                this.createSpreadsheetViewportNavigation(
                        SpreadsheetSelection.parseRow("2")
                ),
                Optional.of(
                        SpreadsheetSelection.parseRowRange("3:4")
                                .setAnchor(SpreadsheetViewportAnchor.BOTTOM)
                ),
                Optional.of(
                        SpreadsheetSelection.parseRowRange("2:4")
                                .setAnchor(SpreadsheetViewportAnchor.BOTTOM)
                )
        );
    }

    @Test
    public void testUpdateRowPreviousRowRangeBottomAfter2() {
        this.updateAndCheck(
                this.createSpreadsheetViewportNavigation(
                        SpreadsheetSelection.parseRow("3")
                ),
                Optional.of(
                        SpreadsheetSelection.parseRowRange("3:4")
                                .setAnchor(SpreadsheetViewportAnchor.BOTTOM)
                ),
                Optional.of(
                        SpreadsheetSelection.parseRowRange("3:4")
                                .setAnchor(SpreadsheetViewportAnchor.BOTTOM)
                )
        );
    }

    @Test
    public void testUpdateRowPreviousRowRangeBottomAfter3() {
        this.updateAndCheck(
                this.createSpreadsheetViewportNavigation(
                        SpreadsheetSelection.parseRow("2")
                ),
                Optional.of(
                        SpreadsheetSelection.parseRowRange("3:4")
                                .setAnchor(SpreadsheetViewportAnchor.BOTTOM)
                ),
                Optional.of(
                        SpreadsheetSelection.parseRowRange("2:4")
                                .setAnchor(SpreadsheetViewportAnchor.BOTTOM)
                )
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

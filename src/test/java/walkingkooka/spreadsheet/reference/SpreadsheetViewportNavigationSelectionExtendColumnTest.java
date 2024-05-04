

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

public final class SpreadsheetViewportNavigationSelectionExtendColumnTest extends SpreadsheetViewportNavigationSelectionExtendTestCase<SpreadsheetViewportNavigationSelectionExtendColumn, SpreadsheetColumnReference> {

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
    public void testUpdateColumnPreviousDifferentColumnLeft() {
        this.updateAndCheck(
                this.createSpreadsheetViewportNavigation(SpreadsheetSelection.parseColumn("C")),
                Optional.of(
                        SpreadsheetSelection.parseColumn("B")
                                .setDefaultAnchor()
                ),
                Optional.of(
                        SpreadsheetSelection.parseColumnRange("B:C")
                                .setAnchor(SpreadsheetViewportAnchor.LEFT)
                )
        );
    }

    @Test
    public void testUpdateColumnPreviousDifferentColumnRight() {
        final SpreadsheetColumnReference column = SpreadsheetSelection.parseColumn("C");

        this.updateAndCheck(
                this.createSpreadsheetViewportNavigation(column),
                Optional.of(
                        SpreadsheetSelection.parseColumn("D")
                                .setDefaultAnchor()
                ),
                Optional.of(
                        SpreadsheetSelection.parseColumnRange("C:D")
                                .setAnchor(SpreadsheetViewportAnchor.RIGHT)
                )
        );
    }

    @Test
    public void testUpdateColumnPreviousColumnRangeLeft() {
        this.updateAndCheck(
                this.createSpreadsheetViewportNavigation(
                        SpreadsheetSelection.parseColumn("C")
                ),
                Optional.of(
                        SpreadsheetSelection.parseColumnRange("B")
                                .setAnchor(SpreadsheetViewportAnchor.LEFT)
                ),
                Optional.of(
                        SpreadsheetSelection.parseColumnRange("B:C")
                                .setAnchor(SpreadsheetViewportAnchor.LEFT)
                )
        );
    }

    @Test
    public void testUpdateColumnPreviousColumnRangeLeft2() {
        this.updateAndCheck(
                this.createSpreadsheetViewportNavigation(
                        SpreadsheetSelection.parseColumn("C")
                ),
                Optional.of(
                        SpreadsheetSelection.parseColumnRange("B:C")
                                .setAnchor(SpreadsheetViewportAnchor.LEFT)
                ),
                Optional.of(
                        SpreadsheetSelection.parseColumnRange("B:C")
                                .setAnchor(SpreadsheetViewportAnchor.LEFT)
                )
        );
    }

    @Test
    public void testUpdateColumnPreviousColumnRangeRight() {
        this.updateAndCheck(
                this.createSpreadsheetViewportNavigation(
                        SpreadsheetSelection.parseColumn("C")
                ),
                Optional.of(
                        SpreadsheetSelection.parseColumnRange("D")
                                .setAnchor(SpreadsheetViewportAnchor.RIGHT)
                ),
                Optional.of(
                        SpreadsheetSelection.parseColumnRange("C:D")
                                .setAnchor(SpreadsheetViewportAnchor.RIGHT)
                )
        );
    }

    @Test
    public void testUpdateColumnPreviousColumnRangeRight2() {
        this.updateAndCheck(
                this.createSpreadsheetViewportNavigation(
                        SpreadsheetSelection.parseColumn("C")
                ),
                Optional.of(
                        SpreadsheetSelection.parseColumnRange("A:D")
                                .setAnchor(SpreadsheetViewportAnchor.RIGHT)
                ),
                Optional.of(
                        SpreadsheetSelection.parseColumnRange("C:D")
                                .setAnchor(SpreadsheetViewportAnchor.RIGHT)
                )
        );
    }

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

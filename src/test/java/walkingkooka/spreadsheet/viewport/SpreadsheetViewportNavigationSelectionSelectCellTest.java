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

public final class SpreadsheetViewportNavigationSelectionSelectCellTest extends SpreadsheetViewportNavigationSelectionSelectTestCase<SpreadsheetViewportNavigationSelectionSelectCell, SpreadsheetCellReference> {

    // HasText..........................................................................................................

    @Test
    public void testHasText() {
        this.textAndCheck(
            SpreadsheetViewportNavigationSelectionSelectCell.with(SpreadsheetSelection.parseCell("ABC123")),
            "select cell ABC123"
        );
    }

    // update...........................................................................................................

    @Test
    public void testUpdateCell() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseCell("C3");

        this.updateAndCheck(
            this.createSpreadsheetViewportNavigation(cell),
            SpreadsheetSelection.A1.setDefaultAnchor(),
            cell.setDefaultAnchor()
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
    SpreadsheetViewportNavigationSelectionSelectCell createSpreadsheetViewportNavigation(final SpreadsheetCellReference selection) {
        return SpreadsheetViewportNavigationSelectionSelectCell.with(selection);
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
    public Class<SpreadsheetViewportNavigationSelectionSelectCell> type() {
        return SpreadsheetViewportNavigationSelectionSelectCell.class;
    }
}

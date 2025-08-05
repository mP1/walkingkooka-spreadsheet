
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

public final class SpreadsheetViewportNavigationSelectionSelectColumnTest extends SpreadsheetViewportNavigationSelectionSelectTestCase<SpreadsheetViewportNavigationSelectionSelectColumn, SpreadsheetColumnReference> {

    // HasText..........................................................................................................

    @Test
    public void testHasText() {
        this.textAndCheck(
            SpreadsheetViewportNavigationSelectionSelectColumn.with(SpreadsheetSelection.parseColumn("ABC")),
            "select column ABC"
        );
    }

    // update...........................................................................................................

    @Test
    public void testUpdateColumn() {
        final SpreadsheetColumnReference column = SpreadsheetSelection.parseColumn("C");

        this.updateAndCheck(
            this.createSpreadsheetViewportNavigation(column),
            SpreadsheetSelection.A1.setDefaultAnchor(),
            column.setDefaultAnchor()
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
    SpreadsheetViewportNavigationSelectionSelectColumn createSpreadsheetViewportNavigation(final SpreadsheetColumnReference selection) {
        return SpreadsheetViewportNavigationSelectionSelectColumn.with(selection);
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
    public Class<SpreadsheetViewportNavigationSelectionSelectColumn> type() {
        return SpreadsheetViewportNavigationSelectionSelectColumn.class;
    }
}

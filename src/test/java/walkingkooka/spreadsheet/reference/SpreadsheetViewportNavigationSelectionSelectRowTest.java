
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

public final class SpreadsheetViewportNavigationSelectionSelectRowTest extends SpreadsheetViewportNavigationSelectionSelectTestCase<SpreadsheetViewportNavigationSelectionSelectRow, SpreadsheetRowReference> {

    // HasText..........................................................................................................

    @Test
    public void testHasText() {
        this.textAndCheck(
            SpreadsheetViewportNavigationSelectionSelectRow.with(SpreadsheetSelection.parseRow("456")),
            "select row 456"
        );
    }

    // update...........................................................................................................

    @Test
    public void testUpdateRow() {
        final SpreadsheetRowReference row = SpreadsheetSelection.parseRow("3");

        this.updateAndCheck(
            this.createSpreadsheetViewportNavigation(row),
            SpreadsheetSelection.A1.setDefaultAnchor(),
            row.setDefaultAnchor()
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
    SpreadsheetViewportNavigationSelectionSelectRow createSpreadsheetViewportNavigation(final SpreadsheetRowReference selection) {
        return SpreadsheetViewportNavigationSelectionSelectRow.with(selection);
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
    public Class<SpreadsheetViewportNavigationSelectionSelectRow> type() {
        return SpreadsheetViewportNavigationSelectionSelectRow.class;
    }
}

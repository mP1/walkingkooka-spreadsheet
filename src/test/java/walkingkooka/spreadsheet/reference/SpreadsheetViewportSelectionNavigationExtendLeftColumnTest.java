
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

public final class SpreadsheetViewportSelectionNavigationExtendLeftColumnTest extends SpreadsheetViewportSelectionNavigationTestCase2<SpreadsheetViewportSelectionNavigationExtendLeftColumn> {

    @Test
    public void testUpdateCell() {
        this.updateAndCheck(
                SpreadsheetViewportSelectionNavigation.extendLeftColumn(),
                SpreadsheetSelection.parseCell("C3"),
                SpreadsheetSelection.parseCellRange("B3:C3")
        );
    }

    @Test
    public void testUpdateColumn() {
        this.updateAndCheck(
                SpreadsheetViewportSelectionNavigation.extendLeftColumn(),
                SpreadsheetSelection.parseColumn("C"),
                SpreadsheetSelection.parseColumnRange("B:C")
        );
    }

    @Test
    public void testUpdateRow() {
        this.updateAndCheck(
                SpreadsheetViewportSelectionNavigation.extendLeftColumn(),
                SpreadsheetSelection.parseRow("3")
        );
    }
    
    @Override
    SpreadsheetViewportSelectionNavigationExtendLeftColumn createSpreadsheetViewportSelectionNavigation() {
        return SpreadsheetViewportSelectionNavigationExtendLeftColumn.INSTANCE;
    }

    @Override
    public Class<SpreadsheetViewportSelectionNavigationExtendLeftColumn> type() {
        return SpreadsheetViewportSelectionNavigationExtendLeftColumn.class;
    }
}

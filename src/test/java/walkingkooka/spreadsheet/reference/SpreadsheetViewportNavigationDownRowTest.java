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

public final class SpreadsheetViewportNavigationDownRowTest extends SpreadsheetViewportNavigationColumnOrRowTestCase<SpreadsheetViewportNavigationDownRow> {

    @Test
    public void testUpdateCell() {
        this.updateAndCheck(
            SpreadsheetSelection.parseCell("C3"),
            SpreadsheetSelection.parseCell("C4")
        );
    }

    @Test
    public void testUpdateColumn() {
        this.updateAndCheck(
            SpreadsheetSelection.parseColumn("C")
        );
    }

    @Test
    public void testUpdateRow() {
        this.updateAndCheck(
            SpreadsheetSelection.parseRow("2"),
            SpreadsheetSelection.parseRow("3")
        );
    }

    @Override
    SpreadsheetViewportNavigationDownRow createSpreadsheetViewportNavigation() {
        return SpreadsheetViewportNavigationDownRow.INSTANCE;
    }

    @Override
    public Class<SpreadsheetViewportNavigationDownRow> type() {
        return SpreadsheetViewportNavigationDownRow.class;
    }
}

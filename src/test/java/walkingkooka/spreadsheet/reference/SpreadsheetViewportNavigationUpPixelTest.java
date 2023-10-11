/*
 * Copydown 2019 Miroslav Pokorny (github.com/mP1)
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

public final class SpreadsheetViewportNavigationUpPixelTest extends SpreadsheetViewportNavigationTestCase2<SpreadsheetViewportNavigationUpPixel> {

    @Test
    public void testUpdateCell() {
        this.updateAndCheck(
                this.createSpreadsheetViewportSelectionNavigation(),
                SpreadsheetSelection.parseCell("E5"),
                SpreadsheetSelection.parseCell("E3")
        );
    }

    @Test
    public void testUpdateColumn() {
        this.updateAndCheck(
                this.createSpreadsheetViewportSelectionNavigation(),
                SpreadsheetSelection.parseColumn("E")
        );
    }

    @Test
    public void testUpdateRow() {
        this.updateAndCheck(
                this.createSpreadsheetViewportSelectionNavigation(),
                SpreadsheetSelection.parseRow("5"),
                SpreadsheetSelection.parseRow("3")
        );
    }
    @Override
    SpreadsheetViewportNavigationUpPixel createSpreadsheetViewportSelectionNavigation() {
        return SpreadsheetViewportNavigationUpPixel.with(2 * ROW_HEIGHT - 1);
    }

    @Override
    public Class<SpreadsheetViewportNavigationUpPixel> type() {
        return SpreadsheetViewportNavigationUpPixel.class;
    }
}

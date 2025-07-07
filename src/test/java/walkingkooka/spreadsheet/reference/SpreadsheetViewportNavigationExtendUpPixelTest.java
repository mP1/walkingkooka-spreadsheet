
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

public final class SpreadsheetViewportNavigationExtendUpPixelTest extends SpreadsheetViewportNavigationPixelTestCase<SpreadsheetViewportNavigationExtendUpPixel> {

    @Test
    public void testUpdateHome() {
        this.updateAndCheck(
            "E5",
            "E3"
        );
    }

    @Test
    public void testUpdateHomeSkipsHiddenRow() {
        this.updateAndCheck(
            "E5", // home
            "", // hidden columns
            "4", // hidden rows
            "E2" // expected
        );
    }

    @Test
    public void testUpdateCell() {
        this.updateAndCheck(
            SpreadsheetSelection.parseCell("E5")
                .viewportRectangle(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
                .viewport()
                .setAnchoredSelection(
                    Optional.of(
                        SpreadsheetSelection.parseCell("E5")
                            .setDefaultAnchor()
                    )
                ),
            SpreadsheetSelection.parseCell("E3")
                .viewportRectangle(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
                .viewport()
                .setAnchoredSelection(
                    Optional.of(
                        SpreadsheetSelection.parseCellRange("E3:E5")
                            .setAnchor(SpreadsheetViewportAnchor.BOTTOM_RIGHT)
                    )
                )
        );
    }

    @Test
    public void testUpdateColumn() {
        this.updateAndCheck(
            SpreadsheetSelection.parseCell("E5")
                .viewportRectangle(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
                .viewport()
                .setAnchoredSelection(
                    Optional.of(
                        SpreadsheetSelection.parseColumn("E")
                            .setDefaultAnchor()
                    )
                ),
            SpreadsheetSelection.parseCell("E3")
                .viewportRectangle(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
                .viewport()
                .setAnchoredSelection(
                    Optional.of(
                        SpreadsheetSelection.parseColumn("E")
                            .setDefaultAnchor()
                    )
                )
        );
    }

    @Test
    public void testUpdateRow() {
        this.updateAndCheck(
            SpreadsheetSelection.parseCell("E5")
                .viewportRectangle(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
                .viewport()
                .setAnchoredSelection(
                    Optional.of(
                        SpreadsheetSelection.parseRow("5")
                            .setDefaultAnchor()
                    )
                ),
            SpreadsheetSelection.parseCell("E3")
                .viewportRectangle(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
                .viewport()
                .setAnchoredSelection(
                    Optional.of(
                        SpreadsheetSelection.parseRowRange("3:5")
                            .setAnchor(SpreadsheetViewportAnchor.BOTTOM)
                    )
                )
        );
    }

    @Override
    SpreadsheetViewportNavigationExtendUpPixel createSpreadsheetViewportNavigation() {
        return SpreadsheetViewportNavigationExtendUpPixel.with(2 * ROW_HEIGHT - 1);
    }

    @Override
    public Class<SpreadsheetViewportNavigationExtendUpPixel> type() {
        return SpreadsheetViewportNavigationExtendUpPixel.class;
    }
}

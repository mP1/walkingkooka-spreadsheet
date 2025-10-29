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
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.Optional;

public final class SpreadsheetViewportNavigationPixelMoveLeftTest extends SpreadsheetViewportNavigationPixelTestCase<SpreadsheetViewportNavigationPixelMoveLeft> {

    @Test
    public void testUpdateHome() {
        this.updateAndCheck(
            "E5",
            "C5"
        );
    }

    @Test
    public void testUpdateHomeSkipsHiddenColumn() {
        this.updateAndCheck(
            "E5", // home
            "D", // hidden columns
            "", // hidden rows
            "B5" // expected
        );
    }

    @Test
    public void testUpdateCell() {
        final Optional<AnchoredSpreadsheetSelection> selection = Optional.of(
            SpreadsheetSelection.parseCell("E5")
                .setDefaultAnchor()
        );

        this.updateAndCheck(
            SpreadsheetSelection.parseCell("E5")
                .viewportRectangle(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
                .viewport()
                .setAnchoredSelection(selection),
            SpreadsheetSelection.parseCell("C5")
                .viewportRectangle(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
                .viewport()
                .setAnchoredSelection(selection)
        );
    }

    @Test
    public void testUpdateColumn() {
        final Optional<AnchoredSpreadsheetSelection> selection = Optional.of(
            SpreadsheetSelection.parseColumn("E")
                .setDefaultAnchor()
        );

        this.updateAndCheck(
            SpreadsheetSelection.parseCell("E5")
                .viewportRectangle(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
                .viewport()
                .setAnchoredSelection(selection),
            SpreadsheetSelection.parseCell("C5")
                .viewportRectangle(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
                .viewport()
                .setAnchoredSelection(selection)
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
            SpreadsheetSelection.parseCell("C5")
                .viewportRectangle(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
                .viewport()
                .setAnchoredSelection(
                    Optional.of(
                        SpreadsheetSelection.parseRow("5")
                            .setDefaultAnchor()
                    )
                )
        );
    }

    // isExtend.........................................................................................................

    @Test
    public void testIsExtend() {
        this.isExtendAndCheck(
            this.createSpreadsheetViewportNavigation(),
            false
        );
    }

    @Override
    SpreadsheetViewportNavigationPixelMoveLeft createSpreadsheetViewportNavigation() {
        return SpreadsheetViewportNavigationPixelMoveLeft.with(2 * COLUMN_WIDTH - 1);
    }

    @Override
    public Class<SpreadsheetViewportNavigationPixelMoveLeft> type() {
        return SpreadsheetViewportNavigationPixelMoveLeft.class;
    }
}


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

public final class SpreadsheetViewportNavigationScrollExtendRightTest extends SpreadsheetViewportNavigationScrollTestCase<SpreadsheetViewportNavigationScrollExtendRight> {

    @Test
    public void testUpdateHome() {
        this.updateAndCheck(
            "C5",
            "E5"
        );
    }

    @Test
    public void testUpdateHomeSkipsHiddenColumn() {
        this.updateAndCheck(
            "B5", // home
            "C", // hidden columns
            "", // hidden rows
            "E5" // expected
        );
    }

    @Test
    public void testUpdateCell() {
        this.updateAndCheck(
            SpreadsheetSelection.parseCell("C3")
                .viewportRectangle(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
                .viewport()
                .setAnchoredSelection(
                    Optional.of(
                        SpreadsheetSelection.parseCell("C3")
                            .setDefaultAnchor()
                    )
                ),
            SpreadsheetSelection.parseCell("E3")
                .viewportRectangle(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
                .viewport()
                .setAnchoredSelection(
                    Optional.of(
                        SpreadsheetSelection.parseCellRange("C3:E3")
                            .setAnchor(SpreadsheetViewportAnchor.TOP_LEFT)
                    )
                )
        );
    }

    @Test
    public void testUpdateColumn() {
        this.updateAndCheck(
            SpreadsheetSelection.parseCell("C3")
                .viewportRectangle(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
                .viewport()
                .setAnchoredSelection(
                    Optional.of(
                        SpreadsheetSelection.parseColumn("C")
                            .setDefaultAnchor()
                    )
                ),
            SpreadsheetSelection.parseCell("E3")
                .viewportRectangle(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
                .viewport()
                .setAnchoredSelection(
                    Optional.of(
                        SpreadsheetSelection.parseColumnRange("C:E")
                            .setAnchor(SpreadsheetViewportAnchor.LEFT)
                    )
                )
        );
    }

    @Test
    public void testUpdateRow() {
        this.updateAndCheck(
            SpreadsheetSelection.parseCell("C3")
                .viewportRectangle(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
                .viewport()
                .setAnchoredSelection(
                    Optional.of(
                        SpreadsheetSelection.parseRow("3")
                            .setDefaultAnchor()
                    )
                ),
            SpreadsheetSelection.parseCell("E3")
                .viewportRectangle(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
                .viewport()
                .setAnchoredSelection(
                    Optional.of(
                        SpreadsheetSelection.parseRow("3")
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
            true
        );
    }

    @Override
    SpreadsheetViewportNavigationScrollExtendRight createSpreadsheetViewportNavigation() {
        return SpreadsheetViewportNavigationScrollExtendRight.with(2 * COLUMN_WIDTH - 1);
    }

    @Override
    public Class<SpreadsheetViewportNavigationScrollExtendRight> type() {
        return SpreadsheetViewportNavigationScrollExtendRight.class;
    }
}

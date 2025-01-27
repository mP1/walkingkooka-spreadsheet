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

import java.util.Optional;

public final class SpreadsheetViewportNavigationUpPixelTest extends SpreadsheetViewportNavigationPixelTestCase<SpreadsheetViewportNavigationUpPixel> {

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
        final Optional<AnchoredSpreadsheetSelection> selection = Optional.of(
                SpreadsheetSelection.parseCell("E5")
                        .setDefaultAnchor()
        );
        this.updateAndCheck(
                SpreadsheetSelection.parseCell("E5")
                        .viewportRectangle(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
                        .viewport()
                        .setAnchoredSelection(selection),
                SpreadsheetSelection.parseCell("E3")
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
                        .setAnchoredSelection(
                                selection
                        ),
                SpreadsheetSelection.parseCell("E3")
                        .viewportRectangle(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
                        .viewport()
                        .setAnchoredSelection(
                                selection
                        )
        );
    }

    @Test
    public void testUpdateRow() {
        final Optional<AnchoredSpreadsheetSelection> selection = Optional.of(
                SpreadsheetSelection.parseRow("5")
                        .setDefaultAnchor()
        );
        this.updateAndCheck(
                SpreadsheetSelection.parseCell("E5")
                        .viewportRectangle(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
                        .viewport()
                        .setAnchoredSelection(
                                selection
                        ),
                SpreadsheetSelection.parseCell("E3")
                        .viewportRectangle(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
                        .viewport()
                        .setAnchoredSelection(selection)
        );
    }

    @Override
    SpreadsheetViewportNavigationUpPixel createSpreadsheetViewportNavigation() {
        return SpreadsheetViewportNavigationUpPixel.with(2 * ROW_HEIGHT - 1);
    }

    @Override
    public Class<SpreadsheetViewportNavigationUpPixel> type() {
        return SpreadsheetViewportNavigationUpPixel.class;
    }
}

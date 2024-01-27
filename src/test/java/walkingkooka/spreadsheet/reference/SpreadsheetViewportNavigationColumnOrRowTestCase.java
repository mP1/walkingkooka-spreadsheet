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

import java.util.Optional;

public abstract class SpreadsheetViewportNavigationColumnOrRowTestCase<T extends SpreadsheetViewportNavigationColumnOrRow> extends
        SpreadsheetViewportNavigationTestCase2<T>  {

    SpreadsheetViewportNavigationColumnOrRowTestCase() {
        super();
    }

    // update...........................................................................................................

    final static SpreadsheetCellReference HOME = SpreadsheetCellReference.A1;

    final void updateAndCheck(final SpreadsheetSelection selection) {
        this.updateAndCheck(
                selection,
                selection
        );
    }
    final void updateAndCheck(final SpreadsheetSelection selection,
                              final SpreadsheetSelection expected) {
        this.updateAndCheck(
                selection,
                expected.setDefaultAnchor()
        );
    }

    final void updateAndCheck(final SpreadsheetSelection selection,
                              final AnchoredSpreadsheetSelection expected) {
        this.updateAndCheck(
                selection.setDefaultAnchor(),
                expected
        );
    }

    final void updateAndCheck(final AnchoredSpreadsheetSelection selection,
                              final AnchoredSpreadsheetSelection expected) {
        this.updateAndCheck(
                this.createSpreadsheetViewportNavigation(),
                HOME.viewportRectangle(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
                        .viewport()
                        .setAnchoredSelection(
                                Optional.of(
                                        selection
                                )
                        ),
                HOME.viewportRectangle(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
                        .viewport()
                        .setAnchoredSelection(
                                Optional.of(
                                        expected
                                )
                        )
        );
    }
}

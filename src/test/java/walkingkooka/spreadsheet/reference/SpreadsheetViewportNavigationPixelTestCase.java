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

import walkingkooka.predicate.Predicates;

import java.util.function.Predicate;

public abstract class SpreadsheetViewportNavigationPixelTestCase<T extends SpreadsheetViewportNavigationPixel> extends
    SpreadsheetViewportNavigationTestCase2<T> {

    SpreadsheetViewportNavigationPixelTestCase() {
        super();
    }

    final void updateAndCheck(final String home) {
        this.updateAndCheck(
            SpreadsheetSelection.parseCell(home)
        );
    }

    final void updateAndCheck(final String home,
                              final String expected) {
        this.updateAndCheck(
            SpreadsheetSelection.parseCell(home),
            Predicates.never(), // hidden Columns
            Predicates.never(), // hidden rows
            SpreadsheetSelection.parseCell(expected)
        );
    }

    final void updateAndCheck(final String home,
                              final String hiddenColumns,
                              final String hiddenRows,
                              final String expected) {
        this.updateAndCheck(
            SpreadsheetSelection.parseCell(home),
            hiddenColumns,
            hiddenRows,
            SpreadsheetSelection.parseCell(expected)
        );
    }

    final void updateAndCheck(final SpreadsheetCellReference home) {
        this.updateAndCheck(
            home,
            home
        );
    }

    final void updateAndCheck(final SpreadsheetCellReference home,
                              final SpreadsheetCellReference expected) {
        this.updateAndCheck(
            home,
            Predicates.never(), // hidden columns
            Predicates.never(), // hidden rows
            expected
        );
    }

    final void updateAndCheck(final SpreadsheetCellReference home,
                              final String hiddenColumns,
                              final String hiddenRows,
                              final SpreadsheetCellReference expected) {
        this.updateAndCheck(
            home,
            this.hiddenColumns(hiddenColumns),
            this.hiddenRows(hiddenRows),
            expected
        );
    }

    final void updateAndCheck(final SpreadsheetCellReference home,
                              final Predicate<SpreadsheetColumnReference> hiddenColumns,
                              final Predicate<SpreadsheetRowReference> hiddenRows,
                              final SpreadsheetCellReference expected) {
        this.updateAndCheck(
            this.createSpreadsheetViewportNavigation(),
            home.viewportRectangle(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
                .viewport(),
            hiddenColumns,
            hiddenRows,
            expected.viewportRectangle(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
                .viewport()
        );
    }
}

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

import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;

import java.util.Optional;

/**
 * subclasses represent scrolling of the viewport in either the horizontal or vertical directions. These are measured
 * in pixels, which will be used to skip non hidden columns or rows.
 */
abstract class SpreadsheetViewportNavigationPixel extends SpreadsheetViewportNavigation {

    SpreadsheetViewportNavigationPixel(final int value) {
        super();

        if (value <= 0) {
            throw new IllegalArgumentException("Invalid pixel value " + value + " <= 0");
        }

        this.value = value;
    }

    final int value;

    /**
     * Pixel navigations do not have an opposite, a new method is available to try and sum adjacent {@link SpreadsheetViewportNavigation}.
     */
    @Override final boolean isOpposite(final SpreadsheetViewportNavigation other) {
        return false;
    }

    // update...........................................................................................................

    @Override
    final SpreadsheetViewport update0(final SpreadsheetViewport viewport,
                                      final SpreadsheetViewportNavigationContext context) {
        SpreadsheetViewport result = viewport;

        // move home
        final SpreadsheetViewportRectangle rectangle = viewport.rectangle();
        final SpreadsheetCellReference home = rectangle.home();
        final Optional<SpreadsheetCellReference> maybeMovedHome = this.updateHome(
            home,
            context
        );

        if (maybeMovedHome.isPresent()) {
            final SpreadsheetViewportRectangle movedRectangle = rectangle.setHome(
                maybeMovedHome.get()
                    .toCell()
            );

            result = result.setRectangle(movedRectangle);

            final Optional<AnchoredSpreadsheetSelection> maybeAnchoredSelection = viewport.anchoredSelection();
            if (maybeAnchoredSelection.isPresent()) {
                result = result.setAnchoredSelection(
                    updateViewportSelection(
                        maybeAnchoredSelection.get(),
                        rectangle,
                        context
                    )
                );
            }

        } else {
            // reset home
            result = result.setRectangle(
                rectangle.setHome(home)
            ).clearAnchoredSelection();
        }

        return result;
    }

    abstract Optional<AnchoredSpreadsheetSelection> updateViewportSelection(final AnchoredSpreadsheetSelection anchoredSelection,
                                                                            final SpreadsheetViewportRectangle rectangle,
                                                                            final SpreadsheetViewportNavigationContext context);

    // Object...........................................................................................................

    @Override
    final Object value() {
        return this.value;
    }
}

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

import walkingkooka.spreadsheet.SpreadsheetViewportRectangle;

import java.util.Optional;

/**
 * Sub-classes represent scrolling of the viewport in either the horizontal or vertical directions. These are measured
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
    @Override
    final boolean isOpposite(final SpreadsheetViewportNavigation other) {
        return false;
    }

    // update...........................................................................................................

    @Override
    SpreadsheetViewport update0(final SpreadsheetViewport viewport,
                                final SpreadsheetViewportNavigationContext context) {
        SpreadsheetViewport result = viewport;

        // move home
        final SpreadsheetViewportRectangle rectangle = viewport.rectangle();
        final SpreadsheetCellReference home = rectangle.home();
        final Optional<SpreadsheetSelection> maybeMovedHome = this.updateHome(
                home,
                SpreadsheetViewportAnchor.CELL,
                context
        );

        if (maybeMovedHome.isPresent()) {
            final SpreadsheetViewportRectangle movedRectangle = rectangle.setHome(
                    maybeMovedHome.get()
                            .toCell()
            );

            result = result.setRectangle(movedRectangle);

            final Optional<AnchoredSpreadsheetSelection> maybeSelection = viewport.selection();
            if (maybeSelection.isPresent()) {
                result = result.setSelection(
                        updateViewportSelection(
                                maybeSelection.get(),
                                rectangle,
                                context
                        )
                );
            }

        } else {
            // reset home
            result = result.setRectangle(
                    rectangle.setHome(home)
            ).setSelection(SpreadsheetViewport.NO_SELECTION);
        }

        return result;
    }

    abstract Optional<SpreadsheetSelection> updateHome(final SpreadsheetCellReference home,
                                                       final SpreadsheetViewportAnchor anchor,
                                                       final SpreadsheetViewportNavigationContext context);

    abstract Optional<AnchoredSpreadsheetSelection> updateViewportSelection(final AnchoredSpreadsheetSelection selection,
                                                                            final SpreadsheetViewportRectangle rectangle,
                                                                            final SpreadsheetViewportNavigationContext context);

    // Object...........................................................................................................

    @Override
    public final int hashCode() {
        return this.value;
    }

    @Override
    public final boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetViewportNavigationPixel &&
                        this.equals0((SpreadsheetViewportNavigationPixel) other);
    }

    private boolean equals0(final SpreadsheetViewportNavigationPixel other) {
        return this.getClass().equals(other.getClass()) && this.value == other.value;
    }
}

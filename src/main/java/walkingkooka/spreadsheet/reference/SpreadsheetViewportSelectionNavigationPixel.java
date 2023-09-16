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

/**
 * Sub-classes represent scrolling of the viewport in either the horizontal or vertical directions. These are measured
 * in pixels, which will be used to skip non hidden columns or rows.
 */
abstract class SpreadsheetViewportSelectionNavigationPixel extends SpreadsheetViewportSelectionNavigation {

    SpreadsheetViewportSelectionNavigationPixel(final int value) {
        super();

        if (value <= 0) {
            throw new IllegalArgumentException("Invalid pixel value " + value + " <= 0");
        }

        this.value = value;
    }

    final int value;

    /**
     * Pixel navigations do not have an opposite, a new method is available to try and sum adjacent {@link SpreadsheetViewportSelectionNavigation}.
     */
    @Override
    final boolean isOpposite(final SpreadsheetViewportSelectionNavigation other) {
        return false;
    }

    @Override
    public final Optional<SpreadsheetViewportSelection> update(final SpreadsheetSelection selection,
                                                               final SpreadsheetViewportSelectionAnchor anchor,
                                                               final SpreadsheetViewportSelectionNavigationContext context) {
        throw new UnsupportedOperationException();
    }

    // Object...........................................................................................................

    @Override
    public final int hashCode() {
        return this.value;
    }

    @Override
    public final boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetViewportSelectionNavigationPixel &&
                        this.equals0((SpreadsheetViewportSelectionNavigationPixel) other);
    }

    private boolean equals0(final SpreadsheetViewportSelectionNavigationPixel other) {
        return this.getClass().equals(other.getClass()) && this.value == other.value;
    }
}

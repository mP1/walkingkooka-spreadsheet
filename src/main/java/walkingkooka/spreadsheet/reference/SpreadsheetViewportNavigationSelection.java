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

import java.util.Objects;
import java.util.Optional;

/**
 * Base class for any navigation involving a cell, column or row, such as a MOUSE click to select.
 */
abstract class SpreadsheetViewportNavigationSelection<T extends SpreadsheetSelection> extends SpreadsheetViewportNavigation {

    SpreadsheetViewportNavigationSelection(final T selection) {
        super();
        Objects.requireNonNull(selection, "selection");
        if (selection.isLabelName()) {
            throw new IllegalArgumentException("Cannot select a label " + selection);
        }
        this.selection = selection;
    }

    /**
     * The selected {@link SpreadsheetSelection}.
     */
    final SpreadsheetSelection selection;

    @Override
    final SpreadsheetViewport update0(final SpreadsheetViewport viewport,
                                      final SpreadsheetViewportNavigationContext context) {
        return this.updateViewport(
                this.selection.setDefaultAnchor(),
                viewport,
                context
        );
    }

    @Override
    final Optional<AnchoredSpreadsheetSelection> updateSelection(final SpreadsheetSelection selection,
                                                                 final SpreadsheetViewportAnchor anchor,
                                                                 final SpreadsheetViewportNavigationContext context) {
        return Optional.of(
                this.selection.setDefaultAnchor()
        );
    }

    @Override
    public final String text() {
        return "select " + this.selection.textLabel().toLowerCase() + " " + this.selection;
    }

    /**
     * Select navigations have no opposite.
     */
    @Override
    final boolean isOpposite(final SpreadsheetViewportNavigation other) {
        return false;
    }

    // Object...........................................................................................................

    @Override
    public final int hashCode() {
        return this.selection.hashCode();
    }

    @Override
    public final boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetViewportNavigationSelection && this.equals0((SpreadsheetViewportNavigationSelection<?>) other);
    }

    private boolean equals0(final SpreadsheetViewportNavigationSelection<?> other) {
        return this.selection.equals(other.selection);
    }
}

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

import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.Objects;

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
    final T selection;

    @Override
    public final String text() {
        return this.textPrefix() + this.selection.textLabel().toLowerCase() + " " + this.selection;
    }

    abstract String textPrefix();

    final String SELECT_SPACE = "select ";
    final String EXTEND_SPACE = "extend ";

    /**
     * Select navigations have no opposite.
     */
    @Override final boolean isOpposite(final SpreadsheetViewportNavigation other) {
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

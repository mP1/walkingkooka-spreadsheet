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

/**
 * Holds a {@link SpreadsheetSelection} and {@link SpreadsheetViewportAnchor}.
 */
public final class AnchoredSpreadsheetSelection {

    public static AnchoredSpreadsheetSelection with(final SpreadsheetSelection selection,
                                                    final SpreadsheetViewportAnchor anchor) {
        Objects.requireNonNull(selection, "selection");
        selection.checkAnchor(anchor);

        return new AnchoredSpreadsheetSelection(
                selection,
                anchor
        );
    }

    private AnchoredSpreadsheetSelection(final SpreadsheetSelection selection,
                                         final SpreadsheetViewportAnchor anchor) {
        this.selection = selection;
        this.anchor = anchor;
    }

    public SpreadsheetSelection selection() {
        return this.selection;
    }

    private final SpreadsheetSelection selection;

    public SpreadsheetViewportAnchor anchor() {
        return this.anchor;
    }

    private final SpreadsheetViewportAnchor anchor;

    // Object..........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
                this.selection,
                this.anchor
        );
    }

    public boolean equals(final Object other) {
        return this == other ||
                other instanceof AnchoredSpreadsheetSelection && this.equals0((AnchoredSpreadsheetSelection) other);
    }

    private boolean equals0(final AnchoredSpreadsheetSelection other) {
        return this.selection.equals(other.selection) &&
                this.anchor == other.anchor;
    }

    @Override
    public String toString() {
        return this.selection + " " + this.anchor;
    }
}

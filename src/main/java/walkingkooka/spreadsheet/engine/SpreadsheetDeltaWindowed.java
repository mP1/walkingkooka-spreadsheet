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

package walkingkooka.spreadsheet.engine;

import walkingkooka.ToStringBuilder;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRange;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A {@link SpreadsheetDelta} with any window/filtering.
 */
final class SpreadsheetDeltaWindowed extends SpreadsheetDelta {

    /**
     * Factory that creates a new {@link SpreadsheetDeltaWindowed} without copying or filtering the cells.
     */
    static SpreadsheetDeltaWindowed withWindowed(final Set<SpreadsheetCell> cells,
                                                 final Map<SpreadsheetColumnReference, Double> maxColumnWidths,
                                                 final Map<SpreadsheetRowReference, Double> maxRowHeights,
                                                 final List<SpreadsheetRange> window) {
        return new SpreadsheetDeltaWindowed(cells,
                maxColumnWidths,
                maxRowHeights,
                window);
    }

    private SpreadsheetDeltaWindowed(final Set<SpreadsheetCell> cells,
                                     final Map<SpreadsheetColumnReference, Double> maxColumnWidths,
                                     final Map<SpreadsheetRowReference, Double> maxRowHeights,
                                     final List<SpreadsheetRange> window) {
        super(cells, maxColumnWidths, maxRowHeights);
        this.window = window;
    }

    @Override
    SpreadsheetDelta replaceCells(final Set<SpreadsheetCell> cells) {
        return new SpreadsheetDeltaWindowed(cells, this.maxColumnWidths, this.maxRowHeights, this.window);
    }

    @Override
    SpreadsheetDelta replaceMaxColumnWidths(final Map<SpreadsheetColumnReference, Double> maxColumnWidths) {
        return new SpreadsheetDeltaWindowed(this.cells, maxColumnWidths, this.maxRowHeights, this.window);
    }

    @Override
    SpreadsheetDelta replaceMaxRowHeights(final Map<SpreadsheetRowReference, Double> maxRowHeights) {
        return new SpreadsheetDeltaWindowed(this.cells, this.maxColumnWidths, maxRowHeights, this.window);
    }

    @Override
    public List<SpreadsheetRange> window() {
        return this.window;
    }

    private final List<SpreadsheetRange> window;

    @Override
    Set<SpreadsheetCell> copyCells(Set<SpreadsheetCell> cells) {
        return maybeFilterCells(cells, this.window);
    }

    @Override
    int hashWindow() {
        return this.window.hashCode();
    }

    @Override
    boolean canBeEquals(final Object other) {
        return other instanceof SpreadsheetDeltaWindowed;
    }

    @Override
    boolean equals1(final SpreadsheetDelta other) {
        return this.window.equals(other.window());
    }

    @Override
    void toStringWindow(final ToStringBuilder b) {
        b.append(' ');
        b.label("window")
                .labelSeparator(": ")
                .separator(" ")
                .value(this.window());
    }
}

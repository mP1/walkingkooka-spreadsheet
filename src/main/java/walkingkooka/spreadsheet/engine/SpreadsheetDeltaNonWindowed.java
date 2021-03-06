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
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRange;
import walkingkooka.text.printer.IndentingPrinter;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A {@link SpreadsheetDelta} without any window/filtering.
 */
final class SpreadsheetDeltaNonWindowed extends SpreadsheetDelta {

    /**
     * Factory that creates a new {@link SpreadsheetDeltaNonWindowed} without copying or filtering the cells.
     */
    static SpreadsheetDeltaNonWindowed withNonWindowed(final Set<SpreadsheetCell> cells,
                                                       final Map<SpreadsheetCellReference, Set<SpreadsheetLabelName>> cellToLabels,
                                                       final Map<SpreadsheetColumnReference, Double> maxColumnWidths,
                                                       final Map<SpreadsheetRowReference, Double> maxRowHeights) {
        return new SpreadsheetDeltaNonWindowed(
                cells,
                cellToLabels,
                maxColumnWidths,
                maxRowHeights
        );
    }

    private SpreadsheetDeltaNonWindowed(final Set<SpreadsheetCell> cells,
                                        final Map<SpreadsheetCellReference, Set<SpreadsheetLabelName>> cellToLabels,
                                        final Map<SpreadsheetColumnReference, Double> maxColumnWidths,
                                        final Map<SpreadsheetRowReference, Double> maxRowHeights) {
        super(cells,
                cellToLabels,
                maxColumnWidths,
                maxRowHeights
        );
    }

    @Override
    SpreadsheetDelta replaceCells(final Set<SpreadsheetCell> cells) {
        // cells have already been filtered by window
        return new SpreadsheetDeltaNonWindowed(
                cells,
                this.cellToLabels,
                this.maxColumnWidths,
                this.maxRowHeights
        );
    }

    @Override
    SpreadsheetDelta replaceCellToLabels(final Map<SpreadsheetCellReference, Set<SpreadsheetLabelName>> cellToLabels) {
        return new SpreadsheetDeltaNonWindowed(
                this.cells,
                cellToLabels,
                this.maxColumnWidths,
                this.maxRowHeights
        );
    }

    @Override
    SpreadsheetDelta replaceMaxColumnWidths(final Map<SpreadsheetColumnReference, Double> maxColumnWidths) {
        return new SpreadsheetDeltaNonWindowed(this.cells, this.cellToLabels, maxColumnWidths, this.maxRowHeights);
    }

    @Override
    SpreadsheetDelta replaceMaxRowHeights(final Map<SpreadsheetRowReference, Double> maxRowHeights) {
        return new SpreadsheetDeltaNonWindowed(this.cells, this.cellToLabels, this.maxColumnWidths, maxRowHeights);
    }

    /**
     * There is no window.
     */
    @Override
    public List<SpreadsheetRange> window() {
        return Lists.empty();
    }

    @Override
    Set<SpreadsheetCell> filterCells(final Set<SpreadsheetCell> cells) {
        return Sets.immutable(cells); // already empty
    }

    // TreePrintable.....................................................................................................

    @Override
    void printWindow(final IndentingPrinter printer) {
        // nop
    }

    // Object...........................................................................................................

    @Override
    int hashWindow() {
        return 0;
    }

    @Override
    boolean canBeEquals(final Object other) {
        return other instanceof SpreadsheetDeltaNonWindowed;
    }

    @Override
    boolean equals1(final SpreadsheetDelta other) {
        return true;
    }

    @Override
    void toStringWindow(final ToStringBuilder b) {
    }
}

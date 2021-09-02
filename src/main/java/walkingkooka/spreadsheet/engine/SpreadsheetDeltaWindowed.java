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
import walkingkooka.spreadsheet.reference.SpreadsheetCellRange;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
import walkingkooka.text.printer.IndentingPrinter;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * A {@link SpreadsheetDelta} with any window/filtering.
 */
final class SpreadsheetDeltaWindowed extends SpreadsheetDelta {

    /**
     * Factory that creates a new {@link SpreadsheetDeltaWindowed} without copying or filtering the cells.
     */
    static SpreadsheetDeltaWindowed withWindowed(final Set<SpreadsheetCell> cells,
                                                 final Set<SpreadsheetLabelMapping> labels,
                                                 final Set<SpreadsheetCellReference> deletedCells,
                                                 final Map<SpreadsheetColumnReference, Double> columnWidths,
                                                 final Map<SpreadsheetRowReference, Double> rowHeights,
                                                 final Optional<SpreadsheetCellRange> window) {
        return new SpreadsheetDeltaWindowed(
                cells,
                labels,
                deletedCells,
                columnWidths,
                rowHeights,
                window
        );
    }

    private SpreadsheetDeltaWindowed(final Set<SpreadsheetCell> cells,
                                     final Set<SpreadsheetLabelMapping> labels,
                                     final Set<SpreadsheetCellReference> deletedCells,
                                     final Map<SpreadsheetColumnReference, Double> columnWidths,
                                     final Map<SpreadsheetRowReference, Double> rowHeights,
                                     final Optional<SpreadsheetCellRange> window) {
        super(cells, labels, deletedCells, columnWidths, rowHeights);
        this.window = window;
    }

    @Override
    SpreadsheetDelta replaceCells(final Set<SpreadsheetCell> cells) {
        // cells have already been filtered by window
        return new SpreadsheetDeltaWindowed(
                cells,
                this.labels,
                this.deletedCells,
                this.columnWidths,
                this.rowHeights,
                this.window
        );
    }

    @Override
    SpreadsheetDelta replaceLabels(final Set<SpreadsheetLabelMapping> labels) {
        return new SpreadsheetDeltaWindowed(
                this.cells,
                labels,
                this.deletedCells,
                this.columnWidths,
                this.rowHeights,
                this.window
        );
    }

    @Override
    SpreadsheetDelta replaceDeletedCells(final Set<SpreadsheetCellReference> deletedCells) {
        return new SpreadsheetDeltaWindowed(
                this.cells,
                this.labels,
                deletedCells,
                this.columnWidths,
                this.rowHeights,
                this.window
        );
    }

    @Override
    SpreadsheetDelta replaceColumnWidths(final Map<SpreadsheetColumnReference, Double> columnWidths) {
        return new SpreadsheetDeltaWindowed(
                this.cells,
                this.labels,
                this.deletedCells,
                columnWidths,
                this.rowHeights,
                this.window
        );
    }

    @Override
    SpreadsheetDelta replaceRowHeights(final Map<SpreadsheetRowReference, Double> rowHeights) {
        return new SpreadsheetDeltaWindowed(
                this.cells,
                this.labels,
                this.deletedCells,
                this.columnWidths,
                rowHeights,
                this.window
        );
    }

    @Override
    public Optional<SpreadsheetCellRange> window() {
        return this.window;
    }

    private final Optional<SpreadsheetCellRange> window;

    @Override
    Set<SpreadsheetCell> filterCells(final Set<SpreadsheetCell> cells) {
        return filterCells(cells, this.window);
    }

    @Override
    Set<SpreadsheetCellReference> filterDeletedCells(final Set<SpreadsheetCellReference> deletedCells) {
        return filterDeletedCells(deletedCells, this.window);
    }

    @Override
    Map<SpreadsheetColumnReference, Double> filterColumnWidths(final Map<SpreadsheetColumnReference, Double> columnWidths) {
        return filterColumnWidths(columnWidths, this.window);
    }

    @Override
    Map<SpreadsheetRowReference, Double> filterRowHeights(final Map<SpreadsheetRowReference, Double> rowHeights) {
        return filterRowHeights(rowHeights, this.window);
    }

    // TreePrintable.....................................................................................................

    @Override
    void printWindow(final IndentingPrinter printer) {
        printer.println("window:");
        printer.indent();
        {
            final Optional<SpreadsheetCellRange> window = this.window();
            if (window.isPresent()) {
                printer.println(window.get().toString());
            }
        }

        printer.outdent();
    }

    // Object..........................................................................................................

    @Override
    void toStringWindow(final ToStringBuilder b) {
        b.append(' ');
        b.label("window")
                .labelSeparator(": ")
                .separator(" ")
                .value(this.window());
    }

    @Override
    public String treeToString(Indentation indentation, LineEnding eol) {
        return super.treeToString(indentation, eol);
    }
}

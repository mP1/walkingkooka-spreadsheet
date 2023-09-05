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
import walkingkooka.ToStringBuilderOption;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.spreadsheet.SpreadsheetRow;
import walkingkooka.spreadsheet.SpreadsheetViewportWindows;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetViewportSelection;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
import walkingkooka.text.printer.IndentingPrinter;

import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;

/**
 * A {@link SpreadsheetDelta} with any window/filtering.
 */
final class SpreadsheetDeltaWindowed extends SpreadsheetDelta {

    /**
     * Factory that creates a new {@link SpreadsheetDeltaWindowed} without copying or filtering the cells.
     */
    static SpreadsheetDeltaWindowed withWindowed(final Optional<SpreadsheetViewportSelection> viewportSelection,
                                                 final Set<SpreadsheetCell> cells,
                                                 final Set<SpreadsheetColumn> columns,
                                                 final Set<SpreadsheetLabelMapping> labels,
                                                 final Set<SpreadsheetRow> rows,
                                                 final Set<SpreadsheetCellReference> deletedCells,
                                                 final Set<SpreadsheetColumnReference> deletedColumns,
                                                 final Set<SpreadsheetRowReference> deletedRows,
                                                 final Map<SpreadsheetColumnReference, Double> columnWidths,
                                                 final Map<SpreadsheetRowReference, Double> rowHeights,
                                                 final OptionalInt maxColumn,
                                                 final OptionalInt maxRow,
                                                 final SpreadsheetViewportWindows window) {
        return new SpreadsheetDeltaWindowed(
                viewportSelection,
                cells,
                columns,
                labels,
                rows,
                deletedCells,
                deletedColumns,
                deletedRows,
                columnWidths,
                rowHeights,
                maxColumn,
                maxRow,
                window
        );
    }

    private SpreadsheetDeltaWindowed(final Optional<SpreadsheetViewportSelection> viewportSelection,
                                     final Set<SpreadsheetCell> cells,
                                     final Set<SpreadsheetColumn> columns,
                                     final Set<SpreadsheetLabelMapping> labels,
                                     final Set<SpreadsheetRow> rows,
                                     final Set<SpreadsheetCellReference> deletedCells,
                                     final Set<SpreadsheetColumnReference> deletedColumns,
                                     final Set<SpreadsheetRowReference> deletedRows,
                                     final Map<SpreadsheetColumnReference, Double> columnWidths,
                                     final Map<SpreadsheetRowReference, Double> rowHeights,
                                     final OptionalInt maxColumn,
                                     final OptionalInt maxRow,
                                     final SpreadsheetViewportWindows window) {
        super(
                viewportSelection,
                cells,
                columns,
                labels,
                rows,
                deletedCells,
                deletedColumns,
                deletedRows,
                columnWidths,
                rowHeights,
                maxColumn,
                maxRow
        );
        this.window = window;
    }

    @Override
    SpreadsheetDelta replaceViewportSelection(final Optional<SpreadsheetViewportSelection> viewportSelection) {
        return new SpreadsheetDeltaWindowed(
                viewportSelection,
                this.cells,
                this.columns,
                this.labels,
                this.rows,
                this.deletedCells,
                this.deletedColumns,
                this.deletedRows,
                this.columnWidths,
                this.rowHeights,
                this.maxColumn,
                this.maxRow,
                this.window
        );
    }

    @Override
    SpreadsheetDelta replaceCells(final Set<SpreadsheetCell> cells) {
        // cells have already been filtered by window
        return new SpreadsheetDeltaWindowed(
                this.viewportSelection,
                cells,
                this.columns,
                this.labels,
                this.rows,
                this.deletedCells,
                this.deletedColumns,
                this.deletedRows,
                this.columnWidths,
                this.rowHeights,
                this.maxColumn,
                this.maxRow,
                this.window
        );
    }

    @Override
    SpreadsheetDelta replaceColumns(final Set<SpreadsheetColumn> columns) {
        return new SpreadsheetDeltaWindowed(
                this.viewportSelection,
                filterCells(
                        this.cells,
                        columns,
                        null, // cells have already been filtered by hidden rows so SKIP
                        null // dont need to filter by window again.
                ),
                columns,
                this.labels,
                this.rows,
                this.deletedCells,
                this.deletedColumns,
                this.deletedRows,
                this.columnWidths,
                this.rowHeights,
                this.maxColumn,
                this.maxRow,
                this.window
        );
    }

    @Override
    SpreadsheetDelta replaceLabels(final Set<SpreadsheetLabelMapping> labels) {
        return new SpreadsheetDeltaWindowed(
                this.viewportSelection,
                this.cells,
                this.columns,
                labels,
                this.rows,
                this.deletedCells,
                this.deletedColumns,
                this.deletedRows,
                this.columnWidths,
                this.rowHeights,
                this.maxColumn,
                this.maxRow,
                this.window
        );
    }

    @Override
    SpreadsheetDelta replaceRows(final Set<SpreadsheetRow> rows) {
        return new SpreadsheetDeltaWindowed(
                this.viewportSelection,
                filterCells(
                        this.cells,
                        null, // cells have already been filtered by hidden columns so SKIP
                        rows,
                        null // dont want to filter cells by window again.
                ),
                this.columns,
                this.labels,
                rows,
                this.deletedCells,
                this.deletedColumns,
                this.deletedRows,
                this.columnWidths,
                this.rowHeights,
                this.maxColumn,
                this.maxRow,
                this.window
        );
    }

    @Override
    SpreadsheetDelta replaceDeletedCells(final Set<SpreadsheetCellReference> deletedCells) {
        return new SpreadsheetDeltaWindowed(
                this.viewportSelection,
                this.cells,
                this.columns,
                this.labels,
                this.rows,
                deletedCells,
                this.deletedColumns,
                this.deletedRows,
                this.columnWidths,
                this.rowHeights,
                this.maxColumn,
                this.maxRow,
                this.window
        );
    }

    @Override
    SpreadsheetDelta replaceDeletedColumns(final Set<SpreadsheetColumnReference> deletedColumns) {
        return new SpreadsheetDeltaWindowed(
                this.viewportSelection,
                this.cells,
                this.columns,
                this.labels,
                this.rows,
                this.deletedCells,
                deletedColumns,
                this.deletedRows,
                this.columnWidths,
                this.rowHeights,
                this.maxColumn,
                this.maxRow,
                this.window
        );
    }

    @Override
    SpreadsheetDelta replaceDeletedRows(final Set<SpreadsheetRowReference> deletedRows) {
        return new SpreadsheetDeltaWindowed(
                this.viewportSelection,
                this.cells,
                this.columns,
                this.labels,
                this.rows,
                this.deletedCells,
                this.deletedColumns,
                deletedRows,
                this.columnWidths,
                this.rowHeights,
                this.maxColumn,
                this.maxRow,
                this.window
        );
    }

    @Override
    SpreadsheetDelta replaceColumnWidths(final Map<SpreadsheetColumnReference, Double> columnWidths) {
        return new SpreadsheetDeltaWindowed(
                this.viewportSelection,
                this.cells,
                this.columns,
                this.labels,
                this.rows,
                this.deletedCells,
                this.deletedColumns,
                this.deletedRows,
                columnWidths,
                this.rowHeights,
                this.maxColumn,
                this.maxRow,
                this.window
        );
    }

    @Override
    SpreadsheetDelta replaceRowHeights(final Map<SpreadsheetRowReference, Double> rowHeights) {
        return new SpreadsheetDeltaWindowed(
                this.viewportSelection,
                this.cells,
                this.columns,
                this.labels,
                this.rows,
                this.deletedCells,
                this.deletedColumns,
                this.deletedRows,
                this.columnWidths,
                rowHeights,
                this.maxColumn,
                this.maxRow,
                this.window
        );
    }

    @Override
    SpreadsheetDelta replaceMaxColumn(final OptionalInt maxColumn) {
        return new SpreadsheetDeltaWindowed(
                this.viewportSelection,
                this.cells,
                this.columns,
                this.labels,
                this.rows,
                this.deletedCells,
                this.deletedColumns,
                this.deletedRows,
                this.columnWidths,
                this.rowHeights,
                maxColumn,
                this.maxRow,
                this.window
        );
    }

    @Override
    SpreadsheetDelta replaceMaxRow(final OptionalInt maxRow) {
        return new SpreadsheetDeltaWindowed(
                this.viewportSelection,
                this.cells,
                this.columns,
                this.labels,
                this.rows,
                this.deletedCells,
                this.deletedColumns,
                this.deletedRows,
                this.columnWidths,
                this.rowHeights,
                this.maxColumn,
                maxRow,
                this.window
        );
    }

    @Override
    public SpreadsheetViewportWindows window() {
        return this.window;
    }

    private final SpreadsheetViewportWindows window;

    @Override
    Set<SpreadsheetColumn> filterColumns(final Set<SpreadsheetColumn> columns) {
        return filterColumns(columns, this.window);
    }

    @Override
    Set<SpreadsheetRow> filterRows(final Set<SpreadsheetRow> rows) {
        return filterRows(rows, this.window);
    }

    @Override
    Set<SpreadsheetCellReference> filterDeletedCells(final Set<SpreadsheetCellReference> deletedCells) {
        return filterDeletedCells(deletedCells, this.window);
    }

    @Override
    Set<SpreadsheetColumnReference> filterDeletedColumns(final Set<SpreadsheetColumnReference> deletedColumns) {
        return filterDeletedColumns(deletedColumns, this.window);
    }

    @Override
    Set<SpreadsheetRowReference> filterDeletedRows(final Set<SpreadsheetRowReference> deletedRows) {
        return filterDeletedRows(deletedRows, this.window);
    }

    @Override
    Map<SpreadsheetColumnReference, Double> filterColumnWidths(final Map<SpreadsheetColumnReference, Double> columnWidths) {
        return filterColumnWidths0(columnWidths, this.window);
    }

    @Override
    Map<SpreadsheetRowReference, Double> filterRowHeights(final Map<SpreadsheetRowReference, Double> rowHeights) {
        return filterRowHeights0(rowHeights, this.window);
    }

    // TreePrintable.....................................................................................................

    @Override
    void printWindow(final IndentingPrinter printer) {
        printer.println("window:");
        printer.indent();
        {
            this.window()
                    .printTree(printer);
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
                .disable(ToStringBuilderOption.QUOTE)
                .value(this.window().toString());
    }

    @Override
    public String treeToString(Indentation indentation, LineEnding eol) {
        return super.treeToString(indentation, eol);
    }
}

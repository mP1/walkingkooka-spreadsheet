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
import walkingkooka.spreadsheet.reference.SpreadsheetCellReferenceSet;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReferenceSet;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameSet;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReferenceSet;
import walkingkooka.spreadsheet.reference.SpreadsheetViewport;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.validation.form.Form;

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
    static SpreadsheetDeltaWindowed withWindowed(final Optional<SpreadsheetViewport> viewport,
                                                 final Set<SpreadsheetCell> cells,
                                                 final Set<SpreadsheetColumn> columns,
                                                 final Set<Form<SpreadsheetExpressionReference>> forms,
                                                 final Set<SpreadsheetLabelMapping> labels,
                                                 final Set<SpreadsheetRow> rows,
                                                 final Map<SpreadsheetCellReference, Set<SpreadsheetExpressionReference>> references,
                                                 final SpreadsheetCellReferenceSet deletedCells,
                                                 final SpreadsheetColumnReferenceSet deletedColumns,
                                                 final SpreadsheetRowReferenceSet deletedRows,
                                                 final SpreadsheetLabelNameSet deletedLabels,
                                                 final SpreadsheetCellReferenceSet matchedCells,
                                                 final Map<SpreadsheetColumnReference, Double> columnWidths,
                                                 final Map<SpreadsheetRowReference, Double> rowHeights,
                                                 final OptionalInt columnCount,
                                                 final OptionalInt rowCount,
                                                 final SpreadsheetViewportWindows window) {
        return new SpreadsheetDeltaWindowed(
            viewport,
            SpreadsheetCellSet.with(cells),
            columns,
            forms,
            labels,
            rows,
            references,
            deletedCells,
            deletedColumns,
            deletedRows,
            deletedLabels,
            matchedCells,
            columnWidths,
            rowHeights,
            columnCount,
            rowCount,
            window
        );
    }

    private SpreadsheetDeltaWindowed(final Optional<SpreadsheetViewport> viewport,
                                     final SpreadsheetCellSet cells,
                                     final Set<SpreadsheetColumn> columns,
                                     final Set<Form<SpreadsheetExpressionReference>> forms,
                                     final Set<SpreadsheetLabelMapping> labels,
                                     final Set<SpreadsheetRow> rows,
                                     final Map<SpreadsheetCellReference, Set<SpreadsheetExpressionReference>> references,
                                     final SpreadsheetCellReferenceSet deletedCells,
                                     final SpreadsheetColumnReferenceSet deletedColumns,
                                     final SpreadsheetRowReferenceSet deletedRows,
                                     final SpreadsheetLabelNameSet deletedLabels,
                                     final SpreadsheetCellReferenceSet matchedCells,
                                     final Map<SpreadsheetColumnReference, Double> columnWidths,
                                     final Map<SpreadsheetRowReference, Double> rowHeights,
                                     final OptionalInt columnCount,
                                     final OptionalInt rowCount,
                                     final SpreadsheetViewportWindows window) {
        super(
            viewport,
            cells,
            columns,
            forms,
            labels,
            rows,
            references,
            deletedCells,
            deletedColumns,
            deletedRows,
            deletedLabels,
            matchedCells,
            columnWidths,
            rowHeights,
            columnCount,
            rowCount
        );
        this.window = window;
    }

    @Override
    SpreadsheetDelta replaceViewport(final Optional<SpreadsheetViewport> viewport) {
        return new SpreadsheetDeltaWindowed(
            viewport,
            this.cells,
            this.columns,
            this.forms,
            this.labels,
            this.rows,
            this.references,
            this.deletedCells,
            this.deletedColumns,
            this.deletedRows,
            this.deletedLabels,
            this.matchedCells,
            this.columnWidths,
            this.rowHeights,
            this.columnCount,
            this.rowCount,
            this.window
        );
    }

    @Override
    SpreadsheetDelta replaceCells(final SpreadsheetCellSet cells) {
        // cells have already been filtered by window
        return new SpreadsheetDeltaWindowed(
            this.viewport,
            cells,
            this.columns,
            this.forms,
            this.labels,
            this.rows,
            this.references,
            this.deletedCells,
            this.deletedColumns,
            this.deletedRows,
            this.deletedLabels,
            this.matchedCells,
            this.columnWidths,
            this.rowHeights,
            this.columnCount,
            this.rowCount,
            this.window
        );
    }

    @Override
    SpreadsheetDelta replaceColumns(final Set<SpreadsheetColumn> columns) {
        return new SpreadsheetDeltaWindowed(
            this.viewport,
            filterCellsByWindow(
                this.cells,
                columns,
                null, // cells have already been filtered by hidden rows so SKIP
                null // dont need to filter by window again.
            ),
            columns,
            this.forms,
            this.labels,
            this.rows,
            this.references,
            this.deletedCells,
            this.deletedColumns,
            this.deletedRows,
            this.deletedLabels,
            this.matchedCells,
            this.columnWidths,
            this.rowHeights,
            this.columnCount,
            this.rowCount,
            this.window
        );
    }

    @Override
    SpreadsheetDelta replaceForms(final Set<Form<SpreadsheetExpressionReference>> forms) {
        return new SpreadsheetDeltaWindowed(
            this.viewport,
            this.cells,
            this.columns,
            forms,
            this.labels,
            this.rows,
            this.references,
            this.deletedCells,
            this.deletedColumns,
            this.deletedRows,
            this.deletedLabels,
            this.matchedCells,
            this.columnWidths,
            this.rowHeights,
            this.columnCount,
            this.rowCount,
            this.window
        );
    }

    @Override
    SpreadsheetDelta replaceLabels(final Set<SpreadsheetLabelMapping> labels) {
        return new SpreadsheetDeltaWindowed(
            this.viewport,
            this.cells,
            this.columns,
            this.forms,
            labels,
            this.rows,
            this.references,
            this.deletedCells,
            this.deletedColumns,
            this.deletedRows,
            this.deletedLabels,
            this.matchedCells,
            this.columnWidths,
            this.rowHeights,
            this.columnCount,
            this.rowCount,
            this.window
        );
    }

    @Override
    SpreadsheetDelta replaceReferences(final Map<SpreadsheetCellReference, Set<SpreadsheetExpressionReference>> references) {
        return new SpreadsheetDeltaWindowed(
            this.viewport,
            this.cells,
            this.columns,
            this.forms,
            this.labels,
            this.rows,
            references,
            this.deletedCells,
            this.deletedColumns,
            this.deletedRows,
            this.deletedLabels,
            this.matchedCells,
            this.columnWidths,
            this.rowHeights,
            this.columnCount,
            this.rowCount,
            this.window
        );
    }

    @Override
    SpreadsheetDelta replaceRows(final Set<SpreadsheetRow> rows) {
        return new SpreadsheetDeltaWindowed(
            this.viewport,
            filterCellsByWindow(
                this.cells,
                null, // cells have already been filtered by hidden columns so SKIP
                rows,
                null // dont want to filter cells by window again.
            ),
            this.columns,
            this.forms,
            this.labels,
            rows,
            this.references,
            this.deletedCells,
            this.deletedColumns,
            this.deletedRows,
            this.deletedLabels,
            this.matchedCells,
            this.columnWidths,
            this.rowHeights,
            this.columnCount,
            this.rowCount,
            this.window
        );
    }

    @Override
    SpreadsheetDelta replaceDeletedCells(final SpreadsheetCellReferenceSet deletedCells) {
        return new SpreadsheetDeltaWindowed(
            this.viewport,
            this.cells,
            this.columns,
            this.forms,
            this.labels,
            this.rows,
            this.references,
            deletedCells,
            this.deletedColumns,
            this.deletedRows,
            this.deletedLabels,
            this.matchedCells,
            this.columnWidths,
            this.rowHeights,
            this.columnCount,
            this.rowCount,
            this.window
        );
    }

    @Override
    SpreadsheetDelta replaceDeletedColumns(final SpreadsheetColumnReferenceSet deletedColumns) {
        return new SpreadsheetDeltaWindowed(
            this.viewport,
            this.cells,
            this.columns,
            this.forms,
            this.labels,
            this.rows,
            this.references,
            this.deletedCells,
            deletedColumns,
            this.deletedRows,
            this.deletedLabels,
            this.matchedCells,
            this.columnWidths,
            this.rowHeights,
            this.columnCount,
            this.rowCount,
            this.window
        );
    }

    @Override
    SpreadsheetDelta replaceDeletedLabels(final SpreadsheetLabelNameSet deletedLabels) {
        return new SpreadsheetDeltaWindowed(
            this.viewport,
            this.cells,
            this.columns,
            this.forms,
            this.labels,
            this.rows,
            this.references,
            this.deletedCells,
            this.deletedColumns,
            this.deletedRows,
            deletedLabels,
            this.matchedCells,
            this.columnWidths,
            this.rowHeights,
            this.columnCount,
            this.rowCount,
            this.window
        );
    }

    @Override
    SpreadsheetDelta replaceDeletedRows(final SpreadsheetRowReferenceSet deletedRows) {
        return new SpreadsheetDeltaWindowed(
            this.viewport,
            this.cells,
            this.columns,
            this.forms,
            this.labels,
            this.rows,
            this.references,
            this.deletedCells,
            this.deletedColumns,
            deletedRows,
            this.deletedLabels,
            this.matchedCells,
            this.columnWidths,
            this.rowHeights,
            this.columnCount,
            this.rowCount,
            this.window
        );
    }

    @Override
    SpreadsheetDelta replaceMatchedCells(final SpreadsheetCellReferenceSet matchedCells) {
        return new SpreadsheetDeltaWindowed(
            this.viewport,
            this.cells,
            this.columns,
            this.forms,
            this.labels,
            this.rows,
            this.references,
            this.deletedCells,
            this.deletedColumns,
            this.deletedRows,
            this.deletedLabels,
            matchedCells,
            this.columnWidths,
            this.rowHeights,
            this.columnCount,
            this.rowCount,
            this.window
        );
    }

    @Override
    SpreadsheetDelta replaceColumnWidths(final Map<SpreadsheetColumnReference, Double> columnWidths) {
        return new SpreadsheetDeltaWindowed(
            this.viewport,
            this.cells,
            this.columns,
            this.forms,
            this.labels,
            this.rows,
            this.references,
            this.deletedCells,
            this.deletedColumns,
            this.deletedRows,
            this.deletedLabels,
            this.matchedCells,
            columnWidths,
            this.rowHeights,
            this.columnCount,
            this.rowCount,
            this.window
        );
    }

    @Override
    SpreadsheetDelta replaceRowHeights(final Map<SpreadsheetRowReference, Double> rowHeights) {
        return new SpreadsheetDeltaWindowed(
            this.viewport,
            this.cells,
            this.columns,
            this.forms,
            this.labels,
            this.rows,
            this.references,
            this.deletedCells,
            this.deletedColumns,
            this.deletedRows,
            this.deletedLabels,
            this.matchedCells,
            this.columnWidths,
            rowHeights,
            this.columnCount,
            this.rowCount,
            this.window
        );
    }

    @Override
    SpreadsheetDelta replaceColumnCount(final OptionalInt columnCount) {
        return new SpreadsheetDeltaWindowed(
            this.viewport,
            this.cells,
            this.columns,
            this.forms,
            this.labels,
            this.rows,
            this.references,
            this.deletedCells,
            this.deletedColumns,
            this.deletedRows,
            this.deletedLabels,
            this.matchedCells,
            this.columnWidths,
            this.rowHeights,
            columnCount,
            this.rowCount,
            this.window
        );
    }

    @Override
    SpreadsheetDelta replaceRowCount(final OptionalInt rowCount) {
        return new SpreadsheetDeltaWindowed(
            this.viewport,
            this.cells,
            this.columns,
            this.forms,
            this.labels,
            this.rows,
            this.references,
            this.deletedCells,
            this.deletedColumns,
            this.deletedRows,
            this.deletedLabels,
            this.matchedCells,
            this.columnWidths,
            this.rowHeights,
            this.columnCount,
            rowCount,
            this.window
        );
    }

    @Override
    public SpreadsheetViewportWindows window() {
        return this.window;
    }

    private final SpreadsheetViewportWindows window;

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
    public String treeToString(final Indentation indentation,
                               final LineEnding eol) {
        return super.treeToString(
            indentation,
            eol
        );
    }
}

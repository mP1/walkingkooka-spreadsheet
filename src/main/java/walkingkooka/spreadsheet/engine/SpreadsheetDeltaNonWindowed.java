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
import walkingkooka.collect.set.ImmutableSortedSet;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.predicate.Predicates;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.spreadsheet.SpreadsheetRow;
import walkingkooka.spreadsheet.SpreadsheetViewportWindows;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnOrRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetViewport;
import walkingkooka.text.printer.IndentingPrinter;

import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.TreeSet;

/**
 * A {@link SpreadsheetDelta} without any window/filtering.
 */
final class SpreadsheetDeltaNonWindowed extends SpreadsheetDelta {

    /**
     * Factory that creates a new {@link SpreadsheetDeltaNonWindowed} without copying or filtering the cells.
     */
    static SpreadsheetDeltaNonWindowed withNonWindowed(final Optional<SpreadsheetViewport> viewport,
                                                       final Set<SpreadsheetCell> cells,
                                                       final Set<SpreadsheetColumn> columns,
                                                       final Set<SpreadsheetLabelMapping> labels,
                                                       final Set<SpreadsheetRow> rows,
                                                       final Set<SpreadsheetCellReference> deletedCells,
                                                       final Set<SpreadsheetColumnReference> deletedColumns,
                                                       final Set<SpreadsheetRowReference> deletedRows,
                                                       final Set<SpreadsheetLabelName> deletedLabels,
                                                       final Set<SpreadsheetCellReference> matchedCells,
                                                       final Map<SpreadsheetColumnReference, Double> columnWidths,
                                                       final Map<SpreadsheetRowReference, Double> rowHeights,
                                                       final OptionalInt columnCount,
                                                       final OptionalInt rowCount) {
        return new SpreadsheetDeltaNonWindowed(
                viewport,
                cells,
                columns,
                labels,
                rows,
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
    }

    private SpreadsheetDeltaNonWindowed(final Optional<SpreadsheetViewport> viewport,
                                        final Set<SpreadsheetCell> cells,
                                        final Set<SpreadsheetColumn> columns,
                                        final Set<SpreadsheetLabelMapping> labels,
                                        final Set<SpreadsheetRow> rows,
                                        final Set<SpreadsheetCellReference> deletedCells,
                                        final Set<SpreadsheetColumnReference> deletedColumns,
                                        final Set<SpreadsheetRowReference> deletedRows,
                                        final Set<SpreadsheetLabelName> deletedLabels,
                                        final Set<SpreadsheetCellReference> matchedCells,
                                        final Map<SpreadsheetColumnReference, Double> columnWidths,
                                        final Map<SpreadsheetRowReference, Double> rowHeights,
                                        final OptionalInt columnCount,
                                        final OptionalInt rowCount) {
        super(
                viewport,
                cells,
                columns,
                labels,
                rows,
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
    }

    @Override
    SpreadsheetDelta replaceViewport(final Optional<SpreadsheetViewport> viewport) {
        return new SpreadsheetDeltaNonWindowed(
                viewport,
                this.cells,
                this.columns,
                this.labels,
                this.rows,
                this.deletedCells,
                this.deletedColumns,
                this.deletedRows,
                this.deletedLabels,
                this.matchedCells,
                this.columnWidths,
                this.rowHeights,
                this.columnCount,
                this.rowCount
        );
    }

    @Override
    SpreadsheetDelta replaceCells(final Set<SpreadsheetCell> cells) {
        // cells have already been filtered by window
        return new SpreadsheetDeltaNonWindowed(
                this.viewport,
                cells,
                this.columns,
                this.labels,
                this.rows,
                this.deletedCells,
                this.deletedColumns,
                this.deletedRows,
                this.deletedLabels,
                this.matchedCells,
                this.columnWidths,
                this.rowHeights,
                this.columnCount,
                this.rowCount
        );
    }

    @Override
    SpreadsheetDelta replaceColumns(final Set<SpreadsheetColumn> columns) {
        return new SpreadsheetDeltaNonWindowed(
                this.viewport,
                filterCellsByWindow(
                        this.cells,
                        columns,
                        null, // cells have already been filtered by hidden rows so SKIP
                        NO_WINDOW
                ),
                columns,
                this.labels,
                this.rows,
                this.deletedCells,
                this.deletedColumns,
                this.deletedRows,
                this.deletedLabels,
                this.matchedCells,
                this.columnWidths,
                this.rowHeights,
                this.columnCount,
                this.rowCount
        );
    }

    @Override
    SpreadsheetDelta replaceLabels(final Set<SpreadsheetLabelMapping> labels) {
        return new SpreadsheetDeltaNonWindowed(
                this.viewport,
                this.cells,
                this.columns,
                labels,
                this.rows,
                this.deletedCells,
                this.deletedColumns,
                this.deletedRows,
                this.deletedLabels,
                this.matchedCells,
                this.columnWidths,
                this.rowHeights,
                this.columnCount,
                this.rowCount
        );
    }

    @Override
    SpreadsheetDelta replaceRows(final Set<SpreadsheetRow> rows) {
        return new SpreadsheetDeltaNonWindowed(
                this.viewport,
                filterCellsByWindow(
                        this.cells,
                        null, // cells have already been filtered by hidden columns so SKIP
                        rows,
                        NO_WINDOW
                ),
                this.columns,
                this.labels,
                rows,
                this.deletedCells,
                this.deletedColumns,
                this.deletedRows,
                this.deletedLabels,
                this.matchedCells,
                this.columnWidths,
                this.rowHeights,
                this.columnCount,
                this.rowCount
        );
    }

    @Override
    SpreadsheetDelta replaceDeletedCells(final Set<SpreadsheetCellReference> deletedCells) {
        return new SpreadsheetDeltaNonWindowed(
                this.viewport,
                this.cells,
                this.columns,
                this.labels,
                this.rows,
                deletedCells,
                this.deletedColumns,
                this.deletedRows,
                this.deletedLabels,
                this.matchedCells,
                this.columnWidths,
                this.rowHeights,
                this.columnCount,
                this.rowCount
        );
    }

    @Override
    SpreadsheetDelta replaceDeletedColumns(final Set<SpreadsheetColumnReference> deletedColumns) {
        return new SpreadsheetDeltaNonWindowed(
                this.viewport,
                this.cells,
                this.columns,
                this.labels,
                this.rows,
                this.deletedCells,
                deletedColumns,
                this.deletedRows,
                this.deletedLabels,
                this.matchedCells,
                this.columnWidths,
                this.rowHeights,
                this.columnCount,
                this.rowCount
        );
    }

    @Override
    SpreadsheetDelta replaceDeletedRows(final Set<SpreadsheetRowReference> deletedRows) {
        return new SpreadsheetDeltaNonWindowed(
                this.viewport,
                this.cells,
                this.columns,
                this.labels,
                this.rows,
                this.deletedCells,
                this.deletedColumns,
                deletedRows,
                this.deletedLabels,
                this.matchedCells,
                this.columnWidths,
                this.rowHeights,
                this.columnCount,
                this.rowCount
        );
    }

    @Override
    SpreadsheetDelta replaceDeletedLabels(final Set<SpreadsheetLabelName> deletedLabels) {
        return new SpreadsheetDeltaNonWindowed(
                this.viewport,
                this.cells,
                this.columns,
                this.labels,
                this.rows,
                this.deletedCells,
                this.deletedColumns,
                this.deletedRows,
                deletedLabels,
                this.matchedCells,
                this.columnWidths,
                this.rowHeights,
                this.columnCount,
                this.rowCount
        );
    }

    @Override
    SpreadsheetDelta replaceMatchedCells(final Set<SpreadsheetCellReference> matchedCells) {
        return new SpreadsheetDeltaNonWindowed(
                this.viewport,
                this.cells,
                this.columns,
                this.labels,
                this.rows,
                this.deletedCells,
                this.deletedColumns,
                this.deletedRows,
                this.deletedLabels,
                matchedCells,
                this.columnWidths,
                this.rowHeights,
                this.columnCount,
                this.rowCount
        );
    }

    @Override
    SpreadsheetDelta replaceColumnWidths(final Map<SpreadsheetColumnReference, Double> columnWidths) {
        return new SpreadsheetDeltaNonWindowed(
                this.viewport,
                this.cells,
                this.columns,
                this.labels,
                this.rows,
                this.deletedCells,
                this.deletedColumns,
                this.deletedRows,
                this.deletedLabels,
                this.matchedCells,
                columnWidths,
                this.rowHeights,
                this.columnCount,
                this.rowCount
        );
    }

    @Override
    SpreadsheetDelta replaceRowHeights(final Map<SpreadsheetRowReference, Double> rowHeights) {
        return new SpreadsheetDeltaNonWindowed(
                this.viewport,
                this.cells,
                this.columns,
                this.labels,
                this.rows,
                this.deletedCells,
                this.deletedColumns,
                this.deletedRows,
                this.deletedLabels,
                this.matchedCells,
                this.columnWidths,
                rowHeights,
                this.columnCount,
                this.rowCount
        );
    }

    @Override
    SpreadsheetDelta replaceColumnCount(final OptionalInt columnCount) {
        return new SpreadsheetDeltaNonWindowed(
                this.viewport,
                this.cells,
                this.columns,
                this.labels,
                this.rows,
                this.deletedCells,
                this.deletedColumns,
                this.deletedRows,
                this.deletedLabels,
                this.matchedCells,
                this.columnWidths,
                this.rowHeights,
                columnCount,
                this.rowCount
        );
    }

    @Override
    SpreadsheetDelta replaceRowCount(final OptionalInt rowCount) {
        return new SpreadsheetDeltaNonWindowed(
                this.viewport,
                this.cells,
                this.columns,
                this.labels,
                this.rows,
                this.deletedCells,
                this.deletedColumns,
                this.deletedRows,
                this.deletedLabels,
                this.matchedCells,
                this.columnWidths,
                this.rowHeights,
                this.columnCount,
                rowCount
        );
    }

    /**
     * There are no window.
     */
    @Override
    public SpreadsheetViewportWindows window() {
        return NO_WINDOW;
    }

    @Override
    Set<SpreadsheetColumn> filterColumns(final Set<SpreadsheetColumn> columns) {
        return columns instanceof ImmutableSortedSet ?
                columns :
                SortedSets.immutable(
                        new TreeSet<>(columns)
                );
    }

    @Override
    Set<SpreadsheetRow> filterRows(final Set<SpreadsheetRow> rows) {
        return rows instanceof ImmutableSortedSet ?
                rows :
                SortedSets.immutable(
                        new TreeSet<>(rows)
                );
    }

    @Override
    Set<SpreadsheetCellReference> filterDeletedCells(final Set<SpreadsheetCellReference> deletedCells) {
        return filter(
                deletedCells,
                Predicates.always(),
                SpreadsheetCellReference::toRelative
        );
    }

    @Override
    Set<SpreadsheetColumnReference> filterDeletedColumns(final Set<SpreadsheetColumnReference> deletedColumns) {
        return filter(
                deletedColumns,
                Predicates.always(),
                SpreadsheetColumnReference::toRelative
        );
    }

    @Override
    Set<SpreadsheetRowReference> filterDeletedRows(final Set<SpreadsheetRowReference> deletedRows) {
        return filter(
                deletedRows,
                Predicates.always(),
                SpreadsheetRowReference::toRelative
        );
    }

    @Override
    Set<SpreadsheetCellReference> filterMatchedCells(final Set<SpreadsheetCellReference> matchedCells) {
        return filter(
                matchedCells,
                Predicates.always(),
                SpreadsheetCellReference::toRelative
        );
    }

    @Override
    Map<SpreadsheetColumnReference, Double> filterColumnWidths(final Map<SpreadsheetColumnReference, Double> columnWidths) {
        return filterMap(
                columnWidths
        );
    }

    @Override
    Map<SpreadsheetRowReference, Double> filterRowHeights(final Map<SpreadsheetRowReference, Double> rowHeights) {
        return filterMap(
                rowHeights
        );
    }

    private static <R extends SpreadsheetColumnOrRowReference> Map<R, Double> filterMap(final Map<R, Double> source) {
        return filterMap(
                source,
                Predicates.always()
        );
    }

    // TreePrintable.....................................................................................................

    @Override
    void printWindow(final IndentingPrinter printer) {
        // nop
    }

    // Object...........................................................................................................

    @Override
    void toStringWindow(final ToStringBuilder b) {
    }
}

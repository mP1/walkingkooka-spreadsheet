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

import walkingkooka.Cast;
import walkingkooka.ToStringBuilder;
import walkingkooka.ToStringBuilderOption;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.spreadsheet.SpreadsheetRow;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRange;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReferenceOrLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnOrRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.reference.SpreadsheetViewportSelection;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonObject;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.json.patch.Patchable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Captures changes following an operation. A window when non empty is applied to any given cells and label mappings returned as a filter.
 */
public abstract class SpreadsheetDelta implements Patchable<SpreadsheetDelta>,
        TreePrintable {

    public final static Optional<SpreadsheetViewportSelection> NO_SELECTION = Optional.empty();

    public final static Set<SpreadsheetCell> NO_CELLS = Sets.empty();
    public final static Set<SpreadsheetColumn> NO_COLUMNS = Sets.empty();
    public final static Set<SpreadsheetLabelMapping> NO_LABELS = Sets.empty();
    public final static Set<SpreadsheetRow> NO_ROWS = Sets.empty();

    public final static Set<SpreadsheetCellReference> NO_DELETED_CELLS = Sets.empty();
    public final static Set<SpreadsheetColumnReference> NO_DELETED_COLUMNS = Sets.empty();
    public final static Set<SpreadsheetRowReference> NO_DELETED_ROWS = Sets.empty();


    public final static Optional<SpreadsheetCellRange> NO_WINDOW = Optional.empty();
    public final static Map<SpreadsheetColumnReference, Double> NO_COLUMN_WIDTHS = Maps.empty();
    public final static Map<SpreadsheetRowReference, Double> NO_ROW_HEIGHTS = Maps.empty();

    /**
     * A {@link SpreadsheetDelta} with everything empty.
     */
    public final static SpreadsheetDelta EMPTY = SpreadsheetDeltaNonWindowed.withNonWindowed(
            NO_SELECTION,
            NO_CELLS,
            NO_COLUMNS,
            NO_LABELS,
            NO_ROWS,
            NO_DELETED_CELLS,
            NO_DELETED_COLUMNS,
            NO_DELETED_ROWS,
            NO_COLUMN_WIDTHS,
            NO_ROW_HEIGHTS
    );

    /**
     * Package private to limit sub classing.
     */
    SpreadsheetDelta(final Optional<SpreadsheetViewportSelection> selection,
                     final Set<SpreadsheetCell> cells,
                     final Set<SpreadsheetColumn> columns,
                     final Set<SpreadsheetLabelMapping> labels,
                     final Set<SpreadsheetRow> rows,
                     final Set<SpreadsheetCellReference> deletedCells,
                     final Set<SpreadsheetColumnReference> deletedColumns,
                     final Set<SpreadsheetRowReference> deletedRows,
                     final Map<SpreadsheetColumnReference, Double> columnWidths,
                     final Map<SpreadsheetRowReference, Double> rowHeights) {
        super();

        this.selection = selection;
        this.cells = cells;
        this.columns = columns;
        this.labels = labels;
        this.rows = rows;

        this.deletedCells = deletedCells;
        this.deletedColumns = deletedColumns;
        this.deletedRows = deletedRows;

        this.columnWidths = columnWidths;
        this.rowHeights = rowHeights;
    }

    // selection............................................................................................................

    public final Optional<SpreadsheetViewportSelection> selection() {
        return this.selection;
    }

    /**
     * Would be setter that returns a {@link SpreadsheetDelta} holding the given selection.
     */
    public final SpreadsheetDelta setSelection(final Optional<SpreadsheetViewportSelection> selection) {
        checkSelection(selection);

        return this.selection.equals(selection) ?
                this :
                this.replaceSelection(selection);
    }

    abstract SpreadsheetDelta replaceSelection(final Optional<SpreadsheetViewportSelection> selection);

    final Optional<SpreadsheetViewportSelection> selection;

    private static void checkSelection(final Optional<SpreadsheetViewportSelection> selection) {
        Objects.requireNonNull(selection, "selection");
    }

    // cells............................................................................................................

    public final Set<SpreadsheetCell> cells() {
        return this.cells;
    }

    /**
     * Would be setter that returns a {@link SpreadsheetDelta} holding the given cells after they are possibly filtered
     * using the {@link #window()}
     */
    public final SpreadsheetDelta setCells(final Set<SpreadsheetCell> cells) {
        checkCells(cells);

        final Set<SpreadsheetCell> copy = this.filterCells(cells);
        return this.cells.equals(copy) ?
                this :
                this.replaceCells(copy);
    }

    abstract SpreadsheetDelta replaceCells(final Set<SpreadsheetCell> cells);

    /**
     * Takes a copy of the cells, possibly filtering cells in hidden columns and rows, and cells if a window is present.
     * Note filtering of {@link #labels} will happen later.
     */
    private Set<SpreadsheetCell> filterCells(final Set<SpreadsheetCell> cells) {
        return filterCells(
                cells,
                this.columns,
                this.rows,
                this.window()
        );
    }

    final Set<SpreadsheetCell> cells;

    private static void checkCells(final Set<SpreadsheetCell> cells) {
        Objects.requireNonNull(cells, "cells");
    }

    /**
     * Finds a {@link SpreadsheetCell} matching the given {@link SpreadsheetCellReference}.
     */
    public Optional<SpreadsheetCell> cell(final SpreadsheetCellReference reference) {
        Objects.requireNonNull(reference, "reference");

        return this.cells()
                .stream()
                .filter(c -> c.reference().equalsIgnoreReferenceKind(reference))
                .findFirst();
    }

    // columns............................................................................................................

    public final Set<SpreadsheetColumn> columns() {
        return this.columns;
    }

    /**
     * Would be setter that returns a {@link SpreadsheetDelta} holding the given columns after they are possibly filtered
     * using the {@link #window()}
     */
    public final SpreadsheetDelta setColumns(final Set<SpreadsheetColumn> columns) {
        checkColumns(columns);

        final Set<SpreadsheetColumn> copy = this.filterColumns(columns);
        return this.columns.equals(copy) ?
                this :
                this.replaceColumns(copy);
    }

    abstract SpreadsheetDelta replaceColumns(final Set<SpreadsheetColumn> columns);

    /**
     * Takes a copy of the columns, possibly filtering out columns if a window is present. Note filtering of {@link #labels} will happen later.
     */
    abstract Set<SpreadsheetColumn> filterColumns(final Set<SpreadsheetColumn> columns);

    final Set<SpreadsheetColumn> columns;

    private static void checkColumns(final Set<SpreadsheetColumn> columns) {
        Objects.requireNonNull(columns, "columns");
    }

    // labels............................................................................................................

    public final Set<SpreadsheetLabelMapping> labels() {
        return this.labels;
    }

    /**
     * Would be setter that returns a {@link SpreadsheetDelta} holding the given labels after they are possibly filtered
     * using the {@link #window()}
     */
    public final SpreadsheetDelta setLabels(final Set<SpreadsheetLabelMapping> labels) {
        checkLabels(labels);

        final Set<SpreadsheetLabelMapping> copy = filterLabels(labels, this.window());
        return this.labels.equals(copy) ?
                this :
                this.replaceLabels(copy);
    }

    /**
     * Sub classes only need to call the right constructor, the map is already immutable and has been filtered by {#link #cells}
     */
    abstract SpreadsheetDelta replaceLabels(final Set<SpreadsheetLabelMapping> labels);

    final Set<SpreadsheetLabelMapping> labels;

    private static void checkLabels(final Set<SpreadsheetLabelMapping> labels) {
        Objects.requireNonNull(labels, "labels");
    }

    // rows............................................................................................................

    public final Set<SpreadsheetRow> rows() {
        return this.rows;
    }

    /**
     * Would be setter that returns a {@link SpreadsheetDelta} holding the given rows after they are possibly filtered
     * using the {@link #window()}
     */
    public final SpreadsheetDelta setRows(final Set<SpreadsheetRow> rows) {
        checkRows(rows);

        final Set<SpreadsheetRow> copy = this.filterRows(rows);
        return this.rows.equals(copy) ?
                this :
                this.replaceRows(copy);
    }

    abstract SpreadsheetDelta replaceRows(final Set<SpreadsheetRow> rows);

    /**
     * Takes a copy of the rows, possibly filtering out rows if a window is present. Note filtering of {@link #labels} will happen later.
     */
    abstract Set<SpreadsheetRow> filterRows(final Set<SpreadsheetRow> rows);

    final Set<SpreadsheetRow> rows;

    private static void checkRows(final Set<SpreadsheetRow> rows) {
        Objects.requireNonNull(rows, "rows");
    }

    // deletedCells............................................................................................................

    public final Set<SpreadsheetCellReference> deletedCells() {
        return this.deletedCells;
    }

    /**
     * Would be setter that returns a {@link SpreadsheetDelta} holding the given deletedCells after they are possibly filtered
     * using the {@link #window()}
     */
    public final SpreadsheetDelta setDeletedCells(final Set<SpreadsheetCellReference> deletedCells) {
        checkDeletedCells(deletedCells);

        final Set<SpreadsheetCellReference> copy = this.filterDeletedCells(deletedCells);
        return this.deletedCells.equals(copy) ?
                this :
                this.replaceDeletedCells(copy);
    }

    abstract SpreadsheetDelta replaceDeletedCells(final Set<SpreadsheetCellReference> deletedCells);

    /**
     * Takes a copy of the deleted cells, possibly filtering out deleted Cells if a window is present.
     */
    abstract Set<SpreadsheetCellReference> filterDeletedCells(final Set<SpreadsheetCellReference> deletedCells);

    final Set<SpreadsheetCellReference> deletedCells;

    private static void checkDeletedCells(final Set<SpreadsheetCellReference> deletedCells) {
        Objects.requireNonNull(deletedCells, "deletedCells");
    }

    // deletedColumns............................................................................................................

    public final Set<SpreadsheetColumnReference> deletedColumns() {
        return this.deletedColumns;
    }

    /**
     * Would be setter that returns a {@link SpreadsheetDelta} holding the given deletedColumns after they are possibly filtered
     * using the {@link #window()}
     */
    public final SpreadsheetDelta setDeletedColumns(final Set<SpreadsheetColumnReference> deletedColumns) {
        checkDeletedColumns(deletedColumns);

        final Set<SpreadsheetColumnReference> copy = this.filterDeletedColumns(deletedColumns);
        return this.deletedColumns.equals(copy) ?
                this :
                this.replaceDeletedColumns(copy);
    }

    abstract SpreadsheetDelta replaceDeletedColumns(final Set<SpreadsheetColumnReference> deletedColumns);

    /**
     * Takes a copy of the deleted columns, possibly filtering out deleted Columns if a window is present.
     */
    abstract Set<SpreadsheetColumnReference> filterDeletedColumns(final Set<SpreadsheetColumnReference> deletedColumns);

    final Set<SpreadsheetColumnReference> deletedColumns;

    private static void checkDeletedColumns(final Set<SpreadsheetColumnReference> deletedColumns) {
        Objects.requireNonNull(deletedColumns, "deletedColumns");
    }

    // deletedRows............................................................................................................

    public final Set<SpreadsheetRowReference> deletedRows() {
        return this.deletedRows;
    }

    /**
     * Would be setter that returns a {@link SpreadsheetDelta} holding the given deletedRows after they are possibly filtered
     * using the {@link #window()}
     */
    public final SpreadsheetDelta setDeletedRows(final Set<SpreadsheetRowReference> deletedRows) {
        checkDeletedRows(deletedRows);

        final Set<SpreadsheetRowReference> copy = this.filterDeletedRows(deletedRows);
        return this.deletedRows.equals(copy) ?
                this :
                this.replaceDeletedRows(copy);
    }

    abstract SpreadsheetDelta replaceDeletedRows(final Set<SpreadsheetRowReference> deletedRows);

    /**
     * Takes a copy of the deleted rows, possibly filtering out deleted Rows if a window is present.
     */
    abstract Set<SpreadsheetRowReference> filterDeletedRows(final Set<SpreadsheetRowReference> deletedRows);

    final Set<SpreadsheetRowReference> deletedRows;

    private static void checkDeletedRows(final Set<SpreadsheetRowReference> deletedRows) {
        Objects.requireNonNull(deletedRows, "deletedRows");
    }

    // columnWidths..................................................................................................

    /**
     * Returns a map of columns with the column width for each. The included columns should appear within one of the cells.
     * Note that keys will always be relative, absolute column are not present.
     */
    public final Map<SpreadsheetColumnReference, Double> columnWidths() {
        return this.columnWidths;
    }

    public final SpreadsheetDelta setColumnWidths(final Map<SpreadsheetColumnReference, Double> columnWidths) {
        Objects.requireNonNull(columnWidths, "columnWidths");

        final Map<SpreadsheetColumnReference, Double> copy = this.filterColumnWidths(columnWidths);
        return this.columnWidths.equals(copy) ?
                this :
                this.replaceColumnWidths(copy);

    }

    /**
     * Takes a copy of the columnWidths, possibly filtering out deleted Cells if a window is present.
     */
    abstract Map<SpreadsheetColumnReference, Double> filterColumnWidths(final Map<SpreadsheetColumnReference, Double> columnWidths);

    abstract SpreadsheetDelta replaceColumnWidths(final Map<SpreadsheetColumnReference, Double> columnWidths);

    final Map<SpreadsheetColumnReference, Double> columnWidths;

    // rowHeights..................................................................................................

    /**
     * Returns a map of rows to the row height for each. The included rows should appear within one of the cells.
     * Note that keys will always be relative, absolute rows are not present.
     */
    public final Map<SpreadsheetRowReference, Double> rowHeights() {
        return this.rowHeights;
    }

    public final SpreadsheetDelta setRowHeights(final Map<SpreadsheetRowReference, Double> rowHeights) {
        Objects.requireNonNull(rowHeights, "rowHeights");

        final Map<SpreadsheetRowReference, Double> copy = this.filterRowHeights(rowHeights);
        return this.rowHeights.equals(copy) ?
                this :
                this.replaceRowHeights(copy);
    }

    /**
     * Takes a copy of the rowHeights, possibly filtering out deleted Cells if a window is present.
     */
    abstract Map<SpreadsheetRowReference, Double> filterRowHeights(final Map<SpreadsheetRowReference, Double> rowHeights);

    abstract SpreadsheetDelta replaceRowHeights(final Map<SpreadsheetRowReference, Double> rowHeights);

    final Map<SpreadsheetRowReference, Double> rowHeights;

    // window............................................................................................................

    /**
     * Getter that returns the windows for this delta. Empty means no filtering was performed on the cells etc contained.
     */
    public abstract Optional<SpreadsheetCellRange> window();

    /**
     * Would be setter that if necessary returns a new {@link SpreadsheetDelta} which will also filter cells if necessary,
     * only if all {@link SpreadsheetCellRange} are all {@link SpreadsheetCellRange ranges}. Filtering is not possible if a
     * {@link SpreadsheetCellRange} is present because it is not possible to determine if a cell is within those
     * boundaries.
     */
    public final SpreadsheetDelta setWindow(final Optional<SpreadsheetCellRange> window) {
        Objects.requireNonNull(window, "window");

        return this.window().equals(window) ?
                this :
                this.setWindow0(window);
    }

    private SpreadsheetDelta setWindow0(final Optional<SpreadsheetCellRange> window) {
        final Optional<SpreadsheetViewportSelection> selection = this.selection;

        final Set<SpreadsheetCell> cells = this.cells;
        final Set<SpreadsheetColumn> columns = this.columns;
        final Set<SpreadsheetLabelMapping> labels = this.labels;
        final Set<SpreadsheetRow> rows = this.rows;

        final Set<SpreadsheetCellReference> deletedCells = this.deletedCells;
        final Set<SpreadsheetColumnReference> deletedColumns = this.deletedColumns;
        final Set<SpreadsheetRowReference> deletedRows = this.deletedRows;

        final Map<SpreadsheetColumnReference, Double> columnWidths = this.columnWidths;
        final Map<SpreadsheetRowReference, Double> rowHeights = this.rowHeights;

        final Set<SpreadsheetCell> filteredCells = filterCells(
                cells,
                columns,
                rows,
                window
        );

        final SpreadsheetDelta delta;
        if (window.isPresent()) {
            final SpreadsheetCellRange filter = window.get();

            delta = SpreadsheetDeltaWindowed.withWindowed(
                    selection,
                    filteredCells,
                    filterColumns0(columns, filter),
                    filterLabels0(labels, filter),
                    filterRows0(rows, filter),
                    filterDeletedCells0(deletedCells, filter),
                    filterDeletedColumns0(deletedColumns, filter),
                    filterDeletedRows0(deletedRows, filter),
                    filterColumnWidths0(columnWidths, filter),
                    filterRowHeights0(rowHeights, filter),
                    window
            );
        } else {
            delta = SpreadsheetDeltaNonWindowed.withNonWindowed(
                    selection,
                    filteredCells,
                    columns,
                    labels,
                    rows,
                    deletedCells,
                    deletedColumns,
                    deletedRows,
                    columnWidths,
                    rowHeights
            );
        }

        return delta;
    }

    /**
     * Filters the given cells there are several rules that could make this happen. Cells within hidden columns or rows,
     * or outside the window if present will be removed.
     */
    static Set<SpreadsheetCell> filterCells(final Set<SpreadsheetCell> cells,
                                            final Set<SpreadsheetColumn> columns,
                                            final Set<SpreadsheetRow> rows,
                                            final Optional<SpreadsheetCellRange> maybeWindow) {
        Predicate<SpreadsheetCell> predicate = null;

        final int columnCount = null != columns ? columns.size() : 0;
        final int rowCount = null != rows ? rows.size() : 0;

        if (columnCount + rowCount > 0) {
            // Any columns or rows within this Set are hidden and matches by a cell will be removed.
            final Set<SpreadsheetColumnOrRowReference> hidden = Sets.sorted(SpreadsheetColumnOrRowReference.COLUMN_OR_ROW_REFERENCE_KIND_IGNORED_COMPARATOR);

            if (columnCount > 0) {
                for (final SpreadsheetColumn column : columns) {
                    if (column.hidden()) {
                        hidden.add(column.reference());
                    }
                }
            }

            if (rowCount > 0) {
                for (final SpreadsheetRow row : rows) {
                    if (row.hidden()) {
                        hidden.add(row.reference());
                    }
                }
            }

            if (!hidden.isEmpty()) {
                predicate = (c) -> {
                    final SpreadsheetCellReference cellReference = c.reference();
                    return !(
                            hidden.contains(cellReference.column()) ||
                                    hidden.contains(cellReference.row())
                    );
                };
            }
        }


        if (null != maybeWindow && maybeWindow.isPresent()) {
            final SpreadsheetCellRange window = maybeWindow.get();
            final Predicate<SpreadsheetCell> windowPredicate = c -> window.test(c.reference());

            predicate = null != predicate ?
                    predicate.and(windowPredicate) :
                    windowPredicate;
        }

        return null != predicate ?
                filter(
                        cells,
                        predicate
                ) :
                copyAndImmutable(cells);
    }

    static <T> Set<T> copyAndImmutable(final Set<T> cells) {
        final Set<T> copy = Sets.sorted();
        copy.addAll(cells);
        return Sets.immutable(copy);
    }

    static Set<SpreadsheetColumn> filterColumns(final Set<SpreadsheetColumn> columns,
                                                final Optional<SpreadsheetCellRange> window) {
        return window.isPresent() ?
                filterColumns0(columns, window.get()) :
                columns;
    }

    private static Set<SpreadsheetColumn> filterColumns0(final Set<SpreadsheetColumn> columns,
                                                         final SpreadsheetCellRange window) {
        return filter(
                columns,
                c -> window.testColumn(c.reference())
        );
    }

    /**
     * Returns a {@link Set} removing any references that are not within the window if present.
     */
    static Set<SpreadsheetLabelMapping> filterLabels(final Set<SpreadsheetLabelMapping> labels,
                                                     final Optional<SpreadsheetCellRange> window) {
        return window.isPresent() ?
                filterLabels0(
                        labels,
                        window.get()
                ) :
                labels;
    }

    /**
     * Removes all {@link SpreadsheetLabelMapping} that belong to cells that outside the window.
     */
    private static Set<SpreadsheetLabelMapping> filterLabels0(final Set<SpreadsheetLabelMapping> labels,
                                                              final SpreadsheetCellRange window) {
        return filter(
                labels,
                m -> {
                    final SpreadsheetExpressionReference r = m.reference();
                    return r.isCellReference() && window.test((SpreadsheetCellReference) m.reference());
                }
        );
    }

    static Set<SpreadsheetRow> filterRows(final Set<SpreadsheetRow> rows,
                                          final Optional<SpreadsheetCellRange> window) {
        return window.isPresent() ?
                filterRows0(rows, window.get()) :
                rows;
    }

    private static Set<SpreadsheetRow> filterRows0(final Set<SpreadsheetRow> rows,
                                                   final SpreadsheetCellRange window) {
        return filter(
                rows,
                r -> window.testRow(r.reference())
        );
    }

    static Set<SpreadsheetCellReference> filterDeletedCells(final Set<SpreadsheetCellReference> deletedCells,
                                                            final Optional<SpreadsheetCellRange> window) {
        return window.isPresent() ?
                filterDeletedCells0(deletedCells, window.get()) :
                deletedCells;
    }

    private static Set<SpreadsheetCellReference> filterDeletedCells0(final Set<SpreadsheetCellReference> deletedCells,
                                                                     final SpreadsheetCellRange window) {
        return filter(
                deletedCells,
                window,
                SpreadsheetCellReference::toRelative
        );
    }

    static Set<SpreadsheetColumnReference> filterDeletedColumns(final Set<SpreadsheetColumnReference> deletedColumns,
                                                                final Optional<SpreadsheetCellRange> window) {
        return window.isPresent() ?
                filterDeletedColumns0(deletedColumns, window.get()) :
                deletedColumns;
    }

    private static Set<SpreadsheetColumnReference> filterDeletedColumns0(final Set<SpreadsheetColumnReference> deletedColumns,
                                                                         final SpreadsheetCellRange window) {
        return filter(
                deletedColumns,
                window::testColumn,
                SpreadsheetColumnReference::toRelative
        );
    }

    static Set<SpreadsheetRowReference> filterDeletedRows(final Set<SpreadsheetRowReference> deletedRows,
                                                          final Optional<SpreadsheetCellRange> window) {
        return window.isPresent() ?
                filterDeletedRows0(deletedRows, window.get()) :
                deletedRows;
    }

    private static Set<SpreadsheetRowReference> filterDeletedRows0(final Set<SpreadsheetRowReference> deletedRows,
                                                                   final SpreadsheetCellRange window) {
        return filter(
                deletedRows,
                window::testRow,
                SpreadsheetRowReference::toRelative
        );
    }

    static <T> Set<T> filter(final Set<T> values,
                             final Predicate<T> keep) {
        return filter(
                values,
                keep,
                Function.identity()
        );
    }

    static <T> Set<T> filter(final Set<T> values,
                             final Predicate<T> keep,
                             final Function<T, T> mapper) {
        return Sets.immutable(
                values.stream()
                        .filter(keep)
                        .map(mapper)
                        .collect(Collectors.toCollection(Sets::sorted))
        );
    }

    static Map<SpreadsheetColumnReference, Double> filterColumnWidths0(final Map<SpreadsheetColumnReference, Double> columnWidths,
                                                                       final SpreadsheetCellRange window) {
        return filterMap(
                columnWidths,
                window.columnReferenceRange()::testColumn
        );
    }

    static Map<SpreadsheetRowReference, Double> filterRowHeights0(final Map<SpreadsheetRowReference, Double> rowHeights,
                                                                  final SpreadsheetCellRange window) {
        return filterMap(
                rowHeights,
                window.rowReferenceRange()::testRow
        );
    }

    static <R extends SpreadsheetColumnOrRowReference> Map<R, Double> filterMap(final Map<R, Double> source,
                                                                                final Predicate<R> keep) {
        final Map<R, Double> filtered = Maps.ordered();

        for (final Map.Entry<R, Double> keyAndValue : source.entrySet()) {
            final R key = keyAndValue.getKey();
            if (keep.test(key)) {
                filtered.put(
                        (R) key.toRelative(),
                        keyAndValue.getValue()
                );
            }
        }

        return Maps.immutable(filtered);
    }

    // Patchable.......................................................................................................

    /**
     * Patches the given {@link SpreadsheetDelta}.
     * Note only some properties may be patched (selection, cell and window) others will throw an exception as invalid.
     * Attempts to patch an unknown cell will fail with an {@link IllegalArgumentException} being thrown.
     */
    @Override
    public SpreadsheetDelta patch(final JsonNode json,
                                  final JsonNodeUnmarshallContext context) {
        Objects.requireNonNull(json, "json");
        Objects.requireNonNull(context, "context");

        SpreadsheetDelta patched = this;

        Set<SpreadsheetCell> cells = this.cells();
        Optional<SpreadsheetCellRange> window = this.window();


        for (final JsonNode propertyAndValue : json.objectOrFail().children()) {
            final JsonPropertyName propertyName = propertyAndValue.name();
            switch (propertyName.value()) {
                case SELECTION_PROPERTY_STRING:
                    patched = patched.setSelection(
                            unmarshallSelection(propertyAndValue, context)
                    );
                    break;
                case CELLS_PROPERTY_STRING:
                    cells = patchCells(propertyAndValue, context);
                    break;
                case LABELS_PROPERTY_STRING:
                case DELETED_CELLS_PROPERTY_STRING:
                case COLUMN_WIDTHS_PROPERTY_STRING:
                case ROW_HEIGHTS_PROPERTY_STRING:
                    Patchable.invalidPropertyPresent(propertyName, propertyAndValue);
                    break;
                case WINDOW_PROPERTY_STRING:
                    window = unmarshallWindow(propertyAndValue, context);
                    break;
                default:
                    Patchable.unknownPropertyPresent(propertyName, propertyAndValue);
                    break;
            }
        }

        return patched.setCells(NO_CELLS)
                .setWindow(window)
                .setCells(cells)
                .setWindow(window);
    }

    private Set<SpreadsheetCell> patchCells(final JsonNode node,
                                            final JsonNodeUnmarshallContext context) {

        return node.isNull() ?
                NO_CELLS :
                this.patchCellsFromObject(node, context);
    }

    /**
     * Takes a json object of reference to cell and patches the existing cells in this {@link SpreadsheetDelta}.
     * If the patch is a new cell it is added, existing cells are patched.
     */
    private Set<SpreadsheetCell> patchCellsFromObject(final JsonNode node,
                                                      final JsonNodeUnmarshallContext context) {
        final Set<SpreadsheetCell> patched = Sets.ordered();

        for (final JsonNode child : node.objectOrFail().children()) {
            final JsonPropertyName propertyName = child.name();

            final SpreadsheetCellReference reference = SpreadsheetSelection.parseCell(
                    propertyName.value()
            );
            ;
            final SpreadsheetCell add;

            final Optional<SpreadsheetCell> old = this.cell(reference);
            if (old.isPresent()) {
                add = old.get()
                        .patch(child, context);
            } else {
                add = context.unmarshall(
                        JsonNode.object()
                                .set(propertyName, child),
                        SpreadsheetCell.class
                );
            }

            patched.add(add);
        }

        return patched;
    }

    // labels...........................................................................................................

    /**
     * Examines the JSON holding a {@link SpreadsheetDelta} traversing the cells property replacing labels with their
     * cell-reference equivalent. Note no attempt is made to validate other properties.
     */
    public static JsonObject resolveCellLabels(final JsonObject json,
                                               final Function<SpreadsheetLabelName, SpreadsheetCellReference> labelToCell) {
        Objects.requireNonNull(json, "json");
        Objects.requireNonNull(labelToCell, "labelToCell");

        JsonObject after = json;

        final Optional<JsonNode> maybeCells = json.get(CELLS_PROPERTY);
        if (maybeCells.isPresent()) {
            final JsonNode cells = maybeCells.get();
            if (!cells.isNull()) {
                after = after.set(
                        CELLS_PROPERTY,
                        resolveCellLabelsCell(
                                cells.objectOrFail(),
                                labelToCell
                        )
                );
            }
        }

        return after;
    }

    /**
     * Loops over the cells json replacing the key if it is a label with its cell equivalent. If the label is unknown this will fail.
     */
    private static JsonObject resolveCellLabelsCell(final JsonObject cells,
                                                    final Function<SpreadsheetLabelName, SpreadsheetCellReference> labelToCell) {

        final List<JsonNode> resolved = Lists.array();

        for (final Map.Entry<JsonPropertyName, JsonNode> child : cells.objectOrFail().asMap().entrySet()) {
            final SpreadsheetCellReferenceOrLabelName cellReferenceOrLabelName = SpreadsheetSelection.parseCellOrLabel(child.getKey().value());

            final JsonNode value = child.getValue();
            resolved.add(
                    cellReferenceOrLabelName.isLabelName() ?
                            value.setName(
                                    JsonPropertyName.with(
                                            labelToCell.apply(
                                                    (SpreadsheetLabelName) cellReferenceOrLabelName
                                            ).toString()
                                    )
                            ) :
                            value
            );
        }

        return cells.setChildren(resolved);
    }

    // TreePrintable.....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println("SpreadsheetDelta");
        printer.indent();
        {
            final Optional<SpreadsheetViewportSelection> selection = this.selection();
            if (selection.isPresent()) {
                printer.println("selection:");
                printer.indent();
                {
                    printer.println(selection.get().toString());
                }
                printer.outdent();
            }

            printTreeCollection("cells", this.cells(), printer);
            printTreeCollection("columns", this.columns(), printer);
            printTreeCollection("labels", this.labels(), printer);
            printTreeCollection("rows", this.rows(), printer);

            printTreeCollection2(
                    "deletedCells",
                    this.deletedCells(),
                    printer
            );

            printTreeCollection2(
                    "deletedColumns",
                    this.deletedColumns(),
                    printer
            );

            printTreeCollection2(
                    "deletedRows",
                    this.deletedRows(),
                    printer
            );

            this.printTreeMap(
                    "columnWidths",
                    this.columnWidths(),
                    printer
            );

            this.printTreeMap(
                    "rowHeights",
                    this.rowHeights(),
                    printer
            );

            this.printWindow(printer);
        }
        printer.outdent();
    }

    private <T extends TreePrintable> void printTreeCollection(final String label,
                                                               final Collection<T> collection,
                                                               final IndentingPrinter printer) {
        if (!collection.isEmpty()) {
            printer.println(label + ":");
            printer.indent();
            {
                for (final T element : collection) {
                    element.printTree(printer);
                }
            }
            printer.outdent();
        }
    }

    private <T extends TreePrintable> void printTreeCollection2(final String label,
                                                                final Collection<T> collection,
                                                                final IndentingPrinter printer) {
        if (!collection.isEmpty()) {
            printer.println(label + ":");
            printer.indent();
            {
                for (final T element : collection) {
                    printer.println(element.toString()); // only want reference string not type
                }
            }
            printer.outdent();
        }
    }

    private void printTreeMap(final String label,
                              final Map<? extends SpreadsheetColumnOrRowReference, Double> references,
                              final IndentingPrinter printer) {
        if (!references.isEmpty()) {
            printer.println(label + ":");
            printer.indent();
            {
                for (final Map.Entry<? extends SpreadsheetColumnOrRowReference, Double> referenceAndWidth : references.entrySet()) {
                    printer.println(referenceAndWidth.getKey() + ": " + referenceAndWidth.getValue());
                }
            }
            printer.outdent();
        }
    }

    abstract void printWindow(final IndentingPrinter printer);

    // JsonNodeContext..................................................................................................

    // @VisibleForTesting
    static SpreadsheetDelta unmarshall(final JsonNode node,
                                       final JsonNodeUnmarshallContext context) {
        SpreadsheetDelta unmarshalled = EMPTY;

        for (final JsonNode child : node.objectOrFail().children()) {
            final JsonPropertyName name = child.name();

            switch (name.value()) {
                case SELECTION_PROPERTY_STRING:
                    unmarshalled = unmarshalled.setSelection(
                            unmarshallSelection(
                                    child,
                                    context
                            )
                    );
                    break;
                case CELLS_PROPERTY_STRING:
                    unmarshalled = unmarshalled.setCells(
                            unmarshallCellsOrColumnsOrRows(
                                    child,
                                    SpreadsheetCell.class,
                                    context
                            )
                    );
                    break;
                case COLUMNS_PROPERTY_STRING:
                    unmarshalled = unmarshalled.setColumns(
                            unmarshallCellsOrColumnsOrRows(
                                    child,
                                    SpreadsheetColumn.class,
                                    context
                            )
                    );
                    break;
                case LABELS_PROPERTY_STRING:
                    unmarshalled = unmarshalled.setLabels(
                            context.unmarshallSet(
                                    child,
                                    SpreadsheetLabelMapping.class
                            )
                    );
                    break;
                case ROWS_PROPERTY_STRING:
                    unmarshalled = unmarshalled.setRows(
                            unmarshallCellsOrColumnsOrRows(
                                    child,
                                    SpreadsheetRow.class,
                                    context
                            )
                    );
                    break;
                case DELETED_CELLS_PROPERTY_STRING:
                    unmarshalled = unmarshalled.setDeletedCells(
                            context.unmarshallSet(
                                    child,
                                    SpreadsheetCellReference.class
                            )
                    );
                    break;
                case DELETED_COLUMNS_PROPERTY_STRING:
                    unmarshalled = unmarshalled.setDeletedColumns(
                            context.unmarshallSet(
                                    child,
                                    SpreadsheetColumnReference.class
                            )
                    );
                    break;
                case DELETED_ROWS_PROPERTY_STRING:
                    unmarshalled = unmarshalled.setDeletedRows(
                            context.unmarshallSet(
                                    child,
                                    SpreadsheetRowReference.class
                            )
                    );
                    break;

                case COLUMN_WIDTHS_PROPERTY_STRING:
                    unmarshalled = unmarshalled.setColumnWidths(
                            context.unmarshallMap(
                                    child,
                                    SpreadsheetColumnReference.class,
                                    Double.class
                            )
                    );
                    break;
                case ROW_HEIGHTS_PROPERTY_STRING:
                    unmarshalled = unmarshalled.setRowHeights(
                            context.unmarshallMap(
                                    child,
                                    SpreadsheetRowReference.class,
                                    Double.class
                            )
                    );
                    break;
                case WINDOW_PROPERTY_STRING:
                    unmarshalled = unmarshalled.setWindow(
                            unmarshallWindow(
                                    child,
                                    context
                            )
                    );
                    break;
                default:
                    JsonNodeUnmarshallContext.unknownPropertyPresent(name, node);
                    break;
            }
        }

        return unmarshalled;
    }

    private static Optional<SpreadsheetViewportSelection> unmarshallSelection(final JsonNode node,
                                                                              final JsonNodeUnmarshallContext context) {
        return Optional.ofNullable(
                context.unmarshall(node, SpreadsheetViewportSelection.class)
        );
    }

    private static <T> Set<T> unmarshallCellsOrColumnsOrRows(final JsonNode node,
                                                             final Class<T> type,
                                                             final JsonNodeUnmarshallContext context) {

        final Set<T> cells = Sets.ordered();

        for (final JsonNode child : node.objectOrFail().children()) {
            cells.add(
                    context.unmarshall(
                            JsonNode.object()
                                    .set(
                                            child.name(),
                                            child
                                    ),
                            type
                    )
            );
        }

        return cells;
    }

    private static Optional<SpreadsheetCellRange> unmarshallWindow(final JsonNode json,
                                                                   final JsonNodeUnmarshallContext context) {
        return Optional.ofNullable(
                context.unmarshall(json, SpreadsheetCellRange.class)
        );
    }

    /**
     * <pre>
     * {
     *   "selection": {
     *      "type": "spreadsheet-column",
     *      "value: "Z"
     *   },
     *   "cells": {
     *     "A1": {
     *       "formula": {
     *         "text": "1"
     *       }
     *     },
     *     "B2": {
     *       "formula": {
     *         "text": "2"
     *       }
     *     },
     *     "C3": {
     *       "formula": {
     *         "text": "3"
     *       }
     *     }
     *   },
     *   "labels": {
     *       "A1": "Label1,Label2"
     *   }
     *   "window": "A1:E5,F6:Z99"
     * }
     * </pre>
     */
    private JsonNode marshall(final JsonNodeMarshallContext context) {
        final List<JsonNode> children = Lists.array();

        final Optional<SpreadsheetViewportSelection> selection = this.selection;
        if (selection.isPresent()) {
            children.add(
                    context.marshall(selection.get())
                            .setName(SELECTION_PROPERTY)
            );
        }

        final Set<SpreadsheetCell> cells = this.cells;
        if (!cells.isEmpty()) {
            children.add(marshallCellOrColumnsOrRow(cells, context).setName(CELLS_PROPERTY));
        }

        final Set<SpreadsheetColumn> columns = this.columns;
        if (!columns.isEmpty()) {
            children.add(marshallCellOrColumnsOrRow(columns, context).setName(COLUMNS_PROPERTY));
        }

        final Set<SpreadsheetLabelMapping> labels = this.labels;
        if (!labels.isEmpty()) {
            children.add(
                    marshallLabels(labels, context)
                            .setName(LABELS_PROPERTY)
            );
        }

        final Set<SpreadsheetRow> rows = this.rows;
        if (!rows.isEmpty()) {
            children.add(marshallCellOrColumnsOrRow(rows, context).setName(ROWS_PROPERTY));
        }

        final Set<SpreadsheetCellReference> deletedCells = this.deletedCells;
        if (!deletedCells.isEmpty()) {
            children.add(context.marshallSet(deletedCells)
                    .setName(DELETED_CELLS_PROPERTY));
        }

        final Set<SpreadsheetColumnReference> deletedColumns = this.deletedColumns;
        if (!deletedColumns.isEmpty()) {
            children.add(context.marshallSet(deletedColumns)
                    .setName(DELETED_COLUMNS_PROPERTY));
        }

        final Set<SpreadsheetRowReference> deletedRows = this.deletedRows;
        if (!deletedRows.isEmpty()) {
            children.add(context.marshallSet(deletedRows)
                    .setName(DELETED_ROWS_PROPERTY));
        }

        final Map<SpreadsheetColumnReference, Double> columnWidths = this.columnWidths;
        if (!columnWidths.isEmpty()) {
            children.add(
                    context.marshallMap(columnWidths)
                            .setName(COLUMN_WIDTHS_PROPERTY)
            );
        }

        final Map<SpreadsheetRowReference, Double> rowsHeights = this.rowHeights;
        if (!rowsHeights.isEmpty()) {
            children.add(
                    context.marshallMap(rowsHeights)
                            .setName(ROW_HEIGHTS_PROPERTY)
            );
        }

        final Optional<SpreadsheetCellRange> window = this.window();
        if (window.isPresent()) {
            children.add(
                    context.marshall(window.get())
                            .setName(WINDOW_PROPERTY)
            );
        }

        return JsonNode.object().setChildren(children);
    }

    /**
     * Creates a JSON object with each cell one of the properties.
     */
    private static <T> JsonNode marshallCellOrColumnsOrRow(final Set<T> cellsOrColumnsOrRows,
                                                           final JsonNodeMarshallContext context) {
        final List<JsonNode> children = Lists.array();

        for (final T cell : cellsOrColumnsOrRows) {
            final JsonObject json = context.marshall(cell)
                    .objectOrFail();
            children.add(json.children().get(0));
        }

        return JsonNode.object()
                .setChildren(children);
    }

    /**
     * Creates a JSON array holding all the labels.
     */
    private static JsonNode marshallLabels(final Set<SpreadsheetLabelMapping> labels,
                                           final JsonNodeMarshallContext context) {
        return context.marshallSet(labels);
    }


    private final static String SELECTION_PROPERTY_STRING = "selection";
    private final static String CELLS_PROPERTY_STRING = "cells";
    private final static String COLUMNS_PROPERTY_STRING = "columns";
    private final static String LABELS_PROPERTY_STRING = "labels";
    private final static String ROWS_PROPERTY_STRING = "rows";

    private final static String DELETED_CELLS_PROPERTY_STRING = "deletedCells";
    private final static String DELETED_COLUMNS_PROPERTY_STRING = "deletedColumns";
    private final static String DELETED_ROWS_PROPERTY_STRING = "deletedRows";

    private final static String COLUMN_WIDTHS_PROPERTY_STRING = "columnWidths";
    private final static String ROW_HEIGHTS_PROPERTY_STRING = "rowHeights";
    private final static String WINDOW_PROPERTY_STRING = "window";

    // @VisibleForTesting
    final static JsonPropertyName SELECTION_PROPERTY = JsonPropertyName.with(SELECTION_PROPERTY_STRING);
    // @VisibleForTesting
    final static JsonPropertyName CELLS_PROPERTY = JsonPropertyName.with(CELLS_PROPERTY_STRING);
    // @VisibleForTesting
    final static JsonPropertyName COLUMNS_PROPERTY = JsonPropertyName.with(COLUMNS_PROPERTY_STRING);
    // @VisibleForTesting
    final static JsonPropertyName LABELS_PROPERTY = JsonPropertyName.with(LABELS_PROPERTY_STRING);
    // @VisibleForTesting
    final static JsonPropertyName ROWS_PROPERTY = JsonPropertyName.with(ROWS_PROPERTY_STRING);
    // @VisibleForTesting
    final static JsonPropertyName DELETED_CELLS_PROPERTY = JsonPropertyName.with(DELETED_CELLS_PROPERTY_STRING);
    // @VisibleForTesting
    final static JsonPropertyName DELETED_COLUMNS_PROPERTY = JsonPropertyName.with(DELETED_COLUMNS_PROPERTY_STRING);
    // @VisibleForTesting
    final static JsonPropertyName DELETED_ROWS_PROPERTY = JsonPropertyName.with(DELETED_ROWS_PROPERTY_STRING);
    // @VisibleForTesting
    final static JsonPropertyName COLUMN_WIDTHS_PROPERTY = JsonPropertyName.with(COLUMN_WIDTHS_PROPERTY_STRING);
    // @VisibleForTesting
    final static JsonPropertyName ROW_HEIGHTS_PROPERTY = JsonPropertyName.with(ROW_HEIGHTS_PROPERTY_STRING);
    // @VisibleForTesting
    final static JsonPropertyName WINDOW_PROPERTY = JsonPropertyName.with(WINDOW_PROPERTY_STRING);

    static {
        // force static initializers to run, preventing Json type name lookup failures.
        SpreadsheetCell.NO_FORMATTED_CELL.isPresent();

        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(SpreadsheetDelta.class),
                SpreadsheetDelta::unmarshall,
                SpreadsheetDelta::marshall,
                SpreadsheetDelta.class,
                SpreadsheetDeltaNonWindowed.class,
                SpreadsheetDeltaWindowed.class
        );
    }

    // equals...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
                this.selection,
                this.cells,
                this.columns,
                this.labels,
                this.rows,
                this.deletedCells,
                this.window().hashCode(),
                this.columnWidths,
                this.rowHeights
        );
    }

    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public final boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetDelta &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetDelta other) {
        return this.selection.equals(other.selection) &&
                this.cells.equals(other.cells) &&
                this.columns.equals(other.columns) &&
                this.labels.equals(other.labels) &&
                this.rows.equals(other.rows) &&
                this.deletedCells.equals(other.deletedCells) &&
                this.columnWidths.equals(other.columnWidths) &&
                this.rowHeights.equals(other.rowHeights) &&
                this.window().equals(other.window());
    }

    /**
     * Produces a {@link String} the cells, max columnWidths/rowHeights and window if present.
     */
    @Override
    public final String toString() {
        final ToStringBuilder b = ToStringBuilder.empty()
                .labelSeparator(": ")
                .separator(" ")
                .valueSeparator(", ")
                .enable(ToStringBuilderOption.QUOTE)
                .label("selection")
                .value(this.selection)
                .label("cells")
                .value(this.cells)
                .label("columns")
                .value(this.columns)
                .label("labels")
                .value(this.labels)
                .label("rows")
                .value(this.rows)
                .label("deletedCells")
                .value(this.deletedCells)
                .label("deletedColumns")
                .value(this.deletedColumns)
                .label("deletedRows")
                .value(this.deletedRows);

        final Map<SpreadsheetColumnReference, Double> columnWidths = this.columnWidths;
        final Map<SpreadsheetRowReference, Double> rowHeights = this.rowHeights;

        if (columnWidths.size() + rowHeights.size() > 0) {
            b.append(" max: ");

            b.labelSeparator("=");
            b.value(columnWidths);

            if (columnWidths.size() + rowHeights.size() > 1) {
                b.append(", ");
            }

            b.value(rowHeights);
        }

        this.toStringWindow(b);

        return b.build();
    }

    abstract void toStringWindow(final ToStringBuilder b);
}

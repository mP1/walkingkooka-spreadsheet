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
import walkingkooka.collect.set.ImmutableSortedSet;
import walkingkooka.collect.set.Sets;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.predicate.Predicates;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.spreadsheet.SpreadsheetRow;
import walkingkooka.spreadsheet.SpreadsheetViewportWindows;
import walkingkooka.spreadsheet.expression.SpreadsheetFunctionName;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetParsePattern;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.parser.SpreadsheetParserSelector;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReferenceOrRange;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnOrRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.reference.SpreadsheetViewport;
import walkingkooka.text.CharSequences;
import walkingkooka.text.CharacterConstant;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonObject;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.JsonString;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.json.patch.Patchable;
import walkingkooka.tree.text.TextStyle;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Captures changes following an operation on a spreadsheet, such as performing CRUD operations on a CELL or LABEL.
 * Note it is possible to select which {@link SpreadsheetDeltaProperties properties} to return for an operation.
 * Eg only return related {@link SpreadsheetCell} but not labels following a cell save/update ignoring. It is also
 * possible to include a window parameter and only select cells within the window, not cells outside will still be updated
 * but not returned, this is useful to update a viewport.
 */
public abstract class SpreadsheetDelta implements Patchable<SpreadsheetDelta>,
        TreePrintable {

    public final static Optional<SpreadsheetViewport> NO_VIEWPORT = Optional.empty();

    public final static Set<SpreadsheetCell> NO_CELLS = Sets.empty();
    public final static Set<SpreadsheetColumn> NO_COLUMNS = Sets.empty();
    public final static Set<SpreadsheetLabelMapping> NO_LABELS = Sets.empty();
    public final static Set<SpreadsheetRow> NO_ROWS = Sets.empty();

    public final static Map<SpreadsheetCellReference, Set<SpreadsheetExpressionReference>> NO_REFERENCES = Maps.empty();

    public final static Set<SpreadsheetCellReference> NO_DELETED_CELLS = Sets.empty();
    public final static Set<SpreadsheetColumnReference> NO_DELETED_COLUMNS = Sets.empty();
    public final static Set<SpreadsheetRowReference> NO_DELETED_ROWS = Sets.empty();
    public final static Set<SpreadsheetLabelName> NO_DELETED_LABELS = Sets.empty();

    public final static Set<SpreadsheetCellReference> NO_MATCHED_CELLS = Sets.empty();

    public final static Map<SpreadsheetColumnReference, Double> NO_COLUMN_WIDTHS = Maps.empty();
    public final static Map<SpreadsheetRowReference, Double> NO_ROW_HEIGHTS = Maps.empty();

    public final static OptionalInt NO_TOTAL_WIDTH = OptionalInt.empty();
    public final static OptionalInt NO_TOTAL_HEIGHT = OptionalInt.empty();

    public final static SpreadsheetViewportWindows NO_WINDOW = SpreadsheetViewportWindows.EMPTY;

    /**
     * A {@link SpreadsheetDelta} with everything empty.
     */
    public final static SpreadsheetDelta EMPTY = SpreadsheetDeltaNonWindowed.withNonWindowed(
            NO_VIEWPORT,
            NO_CELLS,
            NO_COLUMNS,
            NO_LABELS,
            NO_ROWS,
            NO_REFERENCES,
            NO_DELETED_CELLS,
            NO_DELETED_COLUMNS,
            NO_DELETED_ROWS,
            NO_DELETED_LABELS,
            NO_MATCHED_CELLS,
            NO_COLUMN_WIDTHS,
            NO_ROW_HEIGHTS,
            NO_TOTAL_WIDTH,
            NO_TOTAL_HEIGHT
    );

    /**
     * Package private to limit sub classing.
     */
    SpreadsheetDelta(final Optional<SpreadsheetViewport> viewport,
                     final Set<SpreadsheetCell> cells,
                     final Set<SpreadsheetColumn> columns,
                     final Set<SpreadsheetLabelMapping> labels,
                     final Set<SpreadsheetRow> rows,
                     final Map<SpreadsheetCellReference, Set<SpreadsheetExpressionReference>> references,
                     final Set<SpreadsheetCellReference> deletedCells,
                     final Set<SpreadsheetColumnReference> deletedColumns,
                     final Set<SpreadsheetRowReference> deletedRows,
                     final Set<SpreadsheetLabelName> deletedLabels,
                     final Set<SpreadsheetCellReference> matchedCells,
                     final Map<SpreadsheetColumnReference, Double> columnWidths,
                     final Map<SpreadsheetRowReference, Double> rowHeights,
                     final OptionalInt columnCount,
                     final OptionalInt rowCount) {
        super();

        this.viewport = viewport;
        this.cells = cells;
        this.columns = columns;
        this.labels = labels;
        this.rows = rows;

        this.references = references;

        this.deletedCells = deletedCells;
        this.deletedColumns = deletedColumns;
        this.deletedRows = deletedRows;
        this.deletedLabels = deletedLabels;

        this.matchedCells = matchedCells;

        this.columnWidths = columnWidths;
        this.rowHeights = rowHeights;

        this.columnCount = columnCount;
        this.rowCount = rowCount;
    }

    // viewport................................................................................................

    public final Optional<SpreadsheetViewport> viewport() {
        return this.viewport;
    }

    /**
     * Would be setter that returns a {@link SpreadsheetDelta} holding the given selection.
     */
    public final SpreadsheetDelta setViewport(final Optional<SpreadsheetViewport> viewport) {
        Objects.requireNonNull(viewport, "viewport");

        return this.viewport.equals(viewport) ?
                this :
                this.replaceViewport(viewport);
    }

    abstract SpreadsheetDelta replaceViewport(final Optional<SpreadsheetViewport> viewport);

    final Optional<SpreadsheetViewport> viewport;

    // cells............................................................................................................

    /**
     * Return interesting cells, such as cells referencing an update cell or loading all cells within a viewport (window).
     */
    public final Set<SpreadsheetCell> cells() {
        return this.cells;
    }

    final Set<SpreadsheetCell> cells;

    /**
     * Would be setter that returns a {@link SpreadsheetDelta} holding the given cells after they are possibly filtered
     * using the {@link #window()}
     */
    public final SpreadsheetDelta setCells(final Set<SpreadsheetCell> cells) {
        Objects.requireNonNull(cells, "cells");

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
        return filterCellsByWindow(
                cells,
                this.columns,
                this.rows,
                this.window()
        );
    }

    /**
     * Filters the given cells there are several rules that could make this happen. Cells within hidden columns or rows,
     * or outside the window if present will be removed.
     */
    static Set<SpreadsheetCell> filterCellsByWindow(final Set<SpreadsheetCell> cells,
                                                    final Set<SpreadsheetColumn> columns,
                                                    final Set<SpreadsheetRow> rows,
                                                    final SpreadsheetViewportWindows window) {
        Predicate<SpreadsheetCell> predicate = null;

        final int columnCount = null != columns ? columns.size() : 0;
        final int rowCount = null != rows ? rows.size() : 0;

        if (columnCount + rowCount > 0) {
            // Any columns or rows within this Set are hidden and matches by a cell will be removed.
            final Set<SpreadsheetColumnOrRowReference> hidden = SortedSets.tree(
                    SpreadsheetColumnOrRowReference.COLUMN_OR_ROW_REFERENCE_KIND_IGNORED_COMPARATOR
            );

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

            if (false == hidden.isEmpty()) {
                predicate = (c) -> {
                    final SpreadsheetCellReference cell = c.reference();
                    return false == (
                            hidden.contains(cell.column()) ||
                                    hidden.contains(cell.row())
                    );
                };
            }
        }


        if (null != window && window.isNotEmpty()) {
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
                cells instanceof ImmutableSortedSet ?
                        cells :
                        SortedSets.immutable(
                                new TreeSet<>(cells)
                        );
    }

    /**
     * Finds a {@link SpreadsheetCell} matching the given {@link SpreadsheetCellReference}.
     */
    public Optional<SpreadsheetCell> cell(final SpreadsheetCellReference cell) {
        Objects.requireNonNull(cell, "cell");

        return this.cells()
                .stream()
                .filter(c -> c.reference().equalsIgnoreReferenceKind(cell))
                .findFirst();
    }

    // columns..........................................................................................................

    /**
     * Returns columns (if selected) following an operation which are related to selected cells.
     */
    public final Set<SpreadsheetColumn> columns() {
        return this.columns;
    }

    final Set<SpreadsheetColumn> columns;

    /**
     * Would be setter that returns a {@link SpreadsheetDelta} holding the given columns after they are possibly filtered
     * using the {@link #window()}
     */
    public final SpreadsheetDelta setColumns(final Set<SpreadsheetColumn> columns) {
        Objects.requireNonNull(columns, "columns");

        final Set<SpreadsheetColumn> copy = filterColumns(
                columns,
                this.window()
        );
        return this.columns.equals(copy) ?
                this :
                this.replaceColumns(copy);
    }

    /**
     * Takes a copy of the columns, possibly filtering out columns if a window is present. Note filtering of {@link #labels} will happen later.
     */
    private static Set<SpreadsheetColumn> filterColumns(final Set<SpreadsheetColumn> columns,
                                                        final SpreadsheetViewportWindows window) {
        return filter(
                        columns,
                        (c) -> window.test(c.reference())
                );
    }

    abstract SpreadsheetDelta replaceColumns(final Set<SpreadsheetColumn> columns);

    /**
     * Finds a {@link SpreadsheetColumn} matching the given {@link SpreadsheetColumnReference}.
     */
    public Optional<SpreadsheetColumn> column(final SpreadsheetColumnReference column) {
        Objects.requireNonNull(column, "column");

        return this.columns()
                .stream()
                .filter(c -> c.reference().equalsIgnoreReferenceKind(column))
                .findFirst();
    }

    // labels...........................................................................................................

    /**
     * Returns labels (if selected) following an operation which are related to selected cells.
     */
    public final Set<SpreadsheetLabelMapping> labels() {
        return this.labels;
    }

    final Set<SpreadsheetLabelMapping> labels;

    /**
     * Would be setter that returns a {@link SpreadsheetDelta} holding the given labels after they are possibly filtered
     * using the {@link #window()}
     */
    public final SpreadsheetDelta setLabels(final Set<SpreadsheetLabelMapping> labels) {
        Objects.requireNonNull(labels, "labels");

        final Set<SpreadsheetLabelMapping> copy = filterLabels(
                labels,
                this.window()
        );
        return this.labels.equals(copy) ?
                this :
                this.replaceLabels(copy);
    }

    /**
     * Returns a {@link Set} removing any references that are not within the window.
     */
    private static Set<SpreadsheetLabelMapping> filterLabels(final Set<SpreadsheetLabelMapping> labels,
                                                             final SpreadsheetViewportWindows window) {
        return filter(
                labels,
                window.isEmpty() ?
                        Predicates.always() :
                        (final SpreadsheetLabelMapping m) -> {
                            // added type to lambda parameter to help GWTC
                            final SpreadsheetExpressionReference r = m.reference();

                            return r.isLabelName() ||
                                    window.cellRanges()
                                            .stream()
                                            .anyMatch(r::testCellRange);
                        }
        );
    }

    /**
     * sub-classes only need to call the right constructor, the map is already immutable and has been filtered by {#link #cells}
     */
    abstract SpreadsheetDelta replaceLabels(final Set<SpreadsheetLabelMapping> labels);

    // rows............................................................................................................

    /**
     * Returns rows (if selected) following an operation which are related to selected cells.
     */
    public final Set<SpreadsheetRow> rows() {
        return this.rows;
    }

    final Set<SpreadsheetRow> rows;

    /**
     * Would be setter that returns a {@link SpreadsheetDelta} holding the given rows after they are possibly filtered
     * using the {@link #window()}
     */
    public final SpreadsheetDelta setRows(final Set<SpreadsheetRow> rows) {
        Objects.requireNonNull(rows, "rows");

        final Set<SpreadsheetRow> copy = filterRows(
                rows,
                this.window()
        );
        return this.rows.equals(copy) ?
                this :
                this.replaceRows(copy);
    }

    private static Set<SpreadsheetRow> filterRows(final Set<SpreadsheetRow> rows,
                                                  final SpreadsheetViewportWindows window) {
        return filter(
                rows,
                r -> window.test(r.reference())
        );
    }

    abstract SpreadsheetDelta replaceRows(final Set<SpreadsheetRow> rows);

    /**
     * Finds a {@link SpreadsheetRow} matching the given {@link SpreadsheetRowReference}.
     */
    public Optional<SpreadsheetRow> row(final SpreadsheetRowReference row) {
        Objects.requireNonNull(row, "row");

        return this.rows()
                .stream()
                .filter(c -> c.reference().equalsIgnoreReferenceKind(row))
                .findFirst();
    }

    // references............................................................................................................

    /**
     * Returns references (if selected) following an operation which are related to selected cells.
     */
    public final Map<SpreadsheetCellReference, Set<SpreadsheetExpressionReference>> references() {
        return this.references;
    }

    final Map<SpreadsheetCellReference, Set<SpreadsheetExpressionReference>> references;

    /**
     * Would be setter that returns a {@link SpreadsheetDelta} holding the given references after they are possibly filtered
     * using the {@link #window()}
     */
    public final SpreadsheetDelta setReferences(final Map<SpreadsheetCellReference, Set<SpreadsheetExpressionReference>> references) {
        Objects.requireNonNull(references, "references");

        final Map<SpreadsheetCellReference, Set<SpreadsheetExpressionReference>> copy = filterReferences(
                references,
                this.window()
        );
        return this.references.equals(copy) ?
                this :
                this.replaceReferences(copy);
    }

    /**
     * Takes a copy of the references, possibly filtering out references if a window is present. Note filtering of {@link #labels} will happen later.
     */
    private static Map<SpreadsheetCellReference, Set<SpreadsheetExpressionReference>> filterReferences(final Map<SpreadsheetCellReference, Set<SpreadsheetExpressionReference>> references,
                                                                                                       final SpreadsheetViewportWindows window) {
        final Map<SpreadsheetCellReference, Set<SpreadsheetExpressionReference>> filtered = Maps.sorted();

        for (final Map.Entry<SpreadsheetCellReference, Set<SpreadsheetExpressionReference>> cellAndReferences : references.entrySet()) {
            final SpreadsheetCellReference cell = cellAndReferences.getKey();

            if (window.test(cell)) {
                final Set<SpreadsheetExpressionReference> cellReferences = cellAndReferences.getValue();

                SortedSet<SpreadsheetExpressionReference> spreadsheetExpressionReferences;

                if(cellReferences instanceof SortedSet) {
                    spreadsheetExpressionReferences = (SortedSet<SpreadsheetExpressionReference>) cellReferences;
                } else {
                    spreadsheetExpressionReferences = new TreeSet<>(SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR);
                    spreadsheetExpressionReferences.addAll(cellReferences);
                }

                filtered.put(
                        cell,
                        SortedSets.immutable(spreadsheetExpressionReferences)
                );
            }
        }

        return Maps.immutable(filtered);
    }

    abstract SpreadsheetDelta replaceReferences(final Map<SpreadsheetCellReference, Set<SpreadsheetExpressionReference>> references);

    /**
     * Returns the references for the given {@link SpreadsheetCellReference}.
     */
    public Optional<Set<SpreadsheetExpressionReference>> references(final SpreadsheetCellReference cell) {
        Objects.requireNonNull(cell, "cell");

        return Optional.ofNullable(
                this.references.get(cell)
        );
    }

    // deletedCells.....................................................................................................

    /**
     * Returns deleted cells (if selected) following an operation which are related to selected cells.
     */
    public final Set<SpreadsheetCellReference> deletedCells() {
        return this.deletedCells;
    }

    final Set<SpreadsheetCellReference> deletedCells;

    /**
     * Would be setter that returns a {@link SpreadsheetDelta} holding the given deletedCells after they are possibly filtered
     * using the {@link #window()}
     */
    public final SpreadsheetDelta setDeletedCells(final Set<SpreadsheetCellReference> deletedCells) {
        Objects.requireNonNull(deletedCells, "deletedCells");

        final Set<SpreadsheetCellReference> copy = filterDeletedCells(
                deletedCells,
                this.window()
        );
        return this.deletedCells.equals(copy) ?
                this :
                this.replaceDeletedCells(copy);
    }

    private static Set<SpreadsheetCellReference> filterDeletedCells(final Set<SpreadsheetCellReference> deletedCells,
                                                                    final SpreadsheetViewportWindows window) {
        return filter(
                deletedCells,
                window::test,
                SpreadsheetCellReference::toRelative
        );
    }

    abstract SpreadsheetDelta replaceDeletedCells(final Set<SpreadsheetCellReference> deletedCells);

    // deletedColumns...................................................................................................

    public final Set<SpreadsheetColumnReference> deletedColumns() {
        return this.deletedColumns;
    }

    final Set<SpreadsheetColumnReference> deletedColumns;

    /**
     * Would be setter that returns a {@link SpreadsheetDelta} holding the given deletedColumns after they are possibly filtered
     * using the {@link #window()}
     */
    public final SpreadsheetDelta setDeletedColumns(final Set<SpreadsheetColumnReference> deletedColumns) {
        Objects.requireNonNull(deletedColumns, "deletedColumns");

        final Set<SpreadsheetColumnReference> copy = filterDeletedColumns(
                deletedColumns,
                this.window()
        );
        return this.deletedColumns.equals(copy) ?
                this :
                this.replaceDeletedColumns(copy);
    }

    private static Set<SpreadsheetColumnReference> filterDeletedColumns(final Set<SpreadsheetColumnReference> deletedColumns,
                                                                        final SpreadsheetViewportWindows window) {
        return filter(
                deletedColumns,
                window::test,
                SpreadsheetColumnReference::toRelative
        );
    }

    abstract SpreadsheetDelta replaceDeletedColumns(final Set<SpreadsheetColumnReference> deletedColumns);

    // deletedRows......................................................................................................

    /**
     * Returns deleted rows (if selected) following an operation which are related to selected cells.
     */
    public final Set<SpreadsheetRowReference> deletedRows() {
        return this.deletedRows;
    }

    final Set<SpreadsheetRowReference> deletedRows;

    /**
     * Would be setter that returns a {@link SpreadsheetDelta} holding the given deletedRows after they are possibly filtered
     * using the {@link #window()}
     */
    public final SpreadsheetDelta setDeletedRows(final Set<SpreadsheetRowReference> deletedRows) {
        Objects.requireNonNull(deletedRows, "deletedRows");

        final Set<SpreadsheetRowReference> copy = filterDeletedRows(
                deletedRows,
                this.window()
        );
        return this.deletedRows.equals(copy) ?
                this :
                this.replaceDeletedRows(copy);
    }

    private static Set<SpreadsheetRowReference> filterDeletedRows(final Set<SpreadsheetRowReference> deletedRows,
                                                                  final SpreadsheetViewportWindows window) {
        return filter(
                deletedRows,
                window::test,
                SpreadsheetRowReference::toRelative
        );
    }

    abstract SpreadsheetDelta replaceDeletedRows(final Set<SpreadsheetRowReference> deletedRows);

    // deletedLabels....................................................................................................

    public final Set<SpreadsheetLabelName> deletedLabels() {
        return this.deletedLabels;
    }

    final Set<SpreadsheetLabelName> deletedLabels;

    /**
     * Would be setter that returns a {@link SpreadsheetDelta} holding the given deletedLabels after they are possibly filtered
     * using the {@link #window()}
     */
    public final SpreadsheetDelta setDeletedLabels(final Set<SpreadsheetLabelName> deletedLabels) {
        Objects.requireNonNull(deletedLabels, "deletedLabels");

        final Set<SpreadsheetLabelName> copy = this.filterDeletedLabels(deletedLabels);
        return this.deletedLabels.equals(copy) ?
                this :
                this.replaceDeletedLabels(copy);
    }

    /**
     * Takes a copy of the deleted labels, because no target is present actual filtering is NOT possible.
     */
    private Set<SpreadsheetLabelName> filterDeletedLabels(final Set<SpreadsheetLabelName> deletedLabels) {
        return SortedSets.immutable(
                deletedLabels instanceof SortedSet ?
                        (SortedSet<SpreadsheetLabelName>) deletedLabels :
                        new TreeSet<>(deletedLabels)
        );
    }

    abstract SpreadsheetDelta replaceDeletedLabels(final Set<SpreadsheetLabelName> deletedLabels);

    // matchedCells.....................................................................................................

    /**
     * Returns matched cells (if selected) for a given find parameters
     */
    public final Set<SpreadsheetCellReference> matchedCells() {
        return this.matchedCells;
    }

    final Set<SpreadsheetCellReference> matchedCells;

    /**
     * Would be setter that returns a {@link SpreadsheetDelta} holding the given matched cells after they are possibly filtered
     * using the {@link #window()}
     */
    public final SpreadsheetDelta setMatchedCells(final Set<SpreadsheetCellReference> matchedCells) {
        Objects.requireNonNull(matchedCells, "matchedCells");

        final Set<SpreadsheetCellReference> copy = filterMatchedCells(
                matchedCells,
                this.window()
        );
        return this.matchedCells.equals(copy) ?
                this :
                this.replaceMatchedCells(copy);
    }

    private static Set<SpreadsheetCellReference> filterMatchedCells(final Set<SpreadsheetCellReference> matchedCells,
                                                                    final SpreadsheetViewportWindows window) {
        return filter(
                matchedCells,
                window::test,
                SpreadsheetCellReference::toRelative
        );
    }

    abstract SpreadsheetDelta replaceMatchedCells(final Set<SpreadsheetCellReference> matchedCells);

    // columnWidths.....................................................................................................

    /**
     * Returns column widths (if selected) following an operation which are related to selected cells.
     */
    public final Map<SpreadsheetColumnReference, Double> columnWidths() {
        return this.columnWidths;
    }

    final Map<SpreadsheetColumnReference, Double> columnWidths;

    public final SpreadsheetDelta setColumnWidths(final Map<SpreadsheetColumnReference, Double> columnWidths) {
        Objects.requireNonNull(columnWidths, "columnWidths");

        final Map<SpreadsheetColumnReference, Double> copy = filterColumnWidths(
                columnWidths,
                this.window()
        );
        return this.columnWidths.equals(copy) ?
                this :
                this.replaceColumnWidths(copy);

    }

    private static Map<SpreadsheetColumnReference, Double> filterColumnWidths(final Map<SpreadsheetColumnReference, Double> columnWidths,
                                                                              final SpreadsheetViewportWindows window) {
        return filterMap(
                columnWidths,
                window::test
        );
    }

    abstract SpreadsheetDelta replaceColumnWidths(final Map<SpreadsheetColumnReference, Double> columnWidths);

    // rowHeights.......................................................................................................

    /**
     * Returns row heights (if selected) following an operation which are related to selected cells.
     */
    public final Map<SpreadsheetRowReference, Double> rowHeights() {
        return this.rowHeights;
    }

    final Map<SpreadsheetRowReference, Double> rowHeights;

    public final SpreadsheetDelta setRowHeights(final Map<SpreadsheetRowReference, Double> rowHeights) {
        Objects.requireNonNull(rowHeights, "rowHeights");

        final Map<SpreadsheetRowReference, Double> copy = filterRowHeights(
                rowHeights,
                this.window()
        );
        return this.rowHeights.equals(copy) ?
                this :
                this.replaceRowHeights(copy);
    }

    private static Map<SpreadsheetRowReference, Double> filterRowHeights(final Map<SpreadsheetRowReference, Double> rowHeights,
                                                                         final SpreadsheetViewportWindows window) {
        return filterMap(
                rowHeights,
                window::test
        );
    }

    abstract SpreadsheetDelta replaceRowHeights(final Map<SpreadsheetRowReference, Double> rowHeights);

    // columnCount......................................................................................................

    /**
     * Returns the total number or columns in this spreadsheet (if selected).
     */
    public final OptionalInt columnCount() {
        return this.columnCount;
    }

    public SpreadsheetDelta setColumnCount(final OptionalInt columnCount) {
        checkCount(columnCount, "columnCount");

        return this.columnCount.equals(columnCount) ?
                this :
                this.replaceColumnCount(columnCount);
    }

    abstract SpreadsheetDelta replaceColumnCount(final OptionalInt columnCount);

    final OptionalInt columnCount;

    // rowCount.........................................................................................................

    /**
     * Returns the total number of rows in this spreadsheet (if selected).
     */
    public final OptionalInt rowCount() {
        return this.rowCount;
    }

    final OptionalInt rowCount;

    public SpreadsheetDelta setRowCount(final OptionalInt rowCount) {
        checkCount(rowCount, "rowCount");

        return this.rowCount.equals(rowCount) ?
                this :
                this.replaceRowCount(rowCount);
    }

    abstract SpreadsheetDelta replaceRowCount(final OptionalInt rowCount);

    private OptionalInt checkCount(final OptionalInt value,
                                   final String label) {
        Objects.requireNonNull(value, label);

        if (value.isPresent()) {
            final int intValue = value.getAsInt();
            if (intValue < 0) {
                throw new IllegalArgumentException("Invalid " + label + " = " + intValue + " < 0");
            }
        }

        return value;
    }

    // window............................................................................................................

    /**
     * Getter that returns the windows for this delta. Empty means no filtering was performed on the cells etc contained.
     */
    public abstract SpreadsheetViewportWindows window();

    /**
     * Would be setter that if necessary returns a new {@link SpreadsheetDelta} which will also filter cells if necessary,
     * only if all {@link SpreadsheetCellRangeReference} are all {@link SpreadsheetCellRangeReference ranges}. Filtering is not possible if a
     * {@link SpreadsheetCellRangeReference} is present because it is not possible to determine if a cell is within those
     * boundaries.
     */
    public final SpreadsheetDelta setWindow(final SpreadsheetViewportWindows window) {
        return this.window().equals(window) ?
                this :
                this.setWindow0(window);
    }

    private SpreadsheetDelta setWindow0(final SpreadsheetViewportWindows window) {
        final Optional<SpreadsheetViewport> viewport = this.viewport;

        final Set<SpreadsheetCell> cells = this.cells;
        final Set<SpreadsheetColumn> columns = this.columns;
        final Set<SpreadsheetLabelMapping> labels = this.labels;
        final Set<SpreadsheetRow> rows = this.rows;

        final Map<SpreadsheetCellReference, Set<SpreadsheetExpressionReference>> references = this.references;

        final Set<SpreadsheetCellReference> deletedCells = this.deletedCells;
        final Set<SpreadsheetColumnReference> deletedColumns = this.deletedColumns;
        final Set<SpreadsheetRowReference> deletedRows = this.deletedRows;
        final Set<SpreadsheetLabelName> deletedLabels = this.deletedLabels;

        final Set<SpreadsheetCellReference> matchedCells = this.matchedCells;

        final Map<SpreadsheetColumnReference, Double> columnWidths = this.columnWidths;
        final Map<SpreadsheetRowReference, Double> rowHeights = this.rowHeights;

        final Set<SpreadsheetCell> filteredCells = filterCellsByWindow(
                cells,
                columns,
                rows,
                window
        );

        final OptionalInt columnCount = this.columnCount;
        final OptionalInt rowCount = this.rowCount;

        final SpreadsheetDelta delta;
        if (false == window.isEmpty()) {
            delta = SpreadsheetDeltaWindowed.withWindowed(
                    viewport,
                    filteredCells,
                    filterColumns(columns, window),
                    filterLabels(labels, window),
                    filterRows(rows, window),
                    filterReferences(references, window),
                    filterDeletedCells(deletedCells, window),
                    filterDeletedColumns(deletedColumns, window),
                    filterDeletedRows(deletedRows, window),
                    filterDeletedLabels(deletedLabels),
                    filterMatchedCells(matchedCells, window),
                    filterColumnWidths(columnWidths, window),
                    filterRowHeights(rowHeights, window),
                    columnCount,
                    rowCount,
                    window
            );
        } else {
            delta = SpreadsheetDeltaNonWindowed.withNonWindowed(
                    viewport,
                    filteredCells,
                    columns,
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
        }

        return delta;
    }

    // filter...........................................................................................................

    static <T> Set<T> filter(final Set<T> values,
                             final Predicate<T> keep) {
        return filter(
                values,
                keep,
                Function.identity()
        );
    }

    static <T> Set<T> filter(final Set<T> values,
                             final Predicate<T> windowTester,
                             final Function<T, T> mapper) {
        return values.stream()
                .filter(windowTester)
                .map(mapper)
                .collect(
                        ImmutableSortedSet.collector()
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

    // patch argument factories.........................................................................................

    /**
     * Creates a {@link JsonNode patch} which may be used to {@link #patchCells(SpreadsheetCellReferenceOrRange, JsonNode, JsonNodeUnmarshallContext)}.
     */
    public static JsonNode cellsPatch(final Set<SpreadsheetCell> cells,
                                      final JsonNodeMarshallContext context) {
        Objects.requireNonNull(cells, "cells");
        Objects.requireNonNull(context, "context");

        return JsonNode.object()
                .set(
                        SpreadsheetDelta.CELLS_PROPERTY,
                        marshallCellOrColumnsOrRow(
                                cells,
                                context
                        )
                );
    }

    /**
     * Creates a {@link JsonNode patch} which may be used to {@link #patchCells(SpreadsheetCellReferenceOrRange, JsonNode, JsonNodeUnmarshallContext)}.
     */
    public static JsonNode cellsFormulaPatch(final Map<SpreadsheetCellReference, SpreadsheetFormula> cellToFormulas,
                                             final JsonNodeMarshallContext context) {
        Objects.requireNonNull(cellToFormulas, "cellToFormulas");
        Objects.requireNonNull(context, "context");

        return cellsPatchFromMap(
                cellToFormulas,
                FORMULA_PROPERTY,
                context::marshall
        );
    }

    /**
     * Creates a {@link JsonNode patch} which may be used to {@link #patchCells(SpreadsheetCellReferenceOrRange, JsonNode, JsonNodeUnmarshallContext)}.
     */
    public static JsonNode cellsFormatterPatch(final Map<SpreadsheetCellReference, Optional<SpreadsheetFormatterSelector>> cellToFormatters,
                                               final JsonNodeMarshallContext context) {
        Objects.requireNonNull(cellToFormatters, "cellToFormatters");
        Objects.requireNonNull(context, "context");

        return SpreadsheetDelta.cellsPatchFromMap(
                cellToFormatters,
                FORMATTER_PROPERTY,
                (pattern) -> context.marshall(pattern.orElse(null))
        );
    }

    /**
     * Creates a {@link JsonNode patch} which may be used to {@link #patchCells(SpreadsheetCellReferenceOrRange, JsonNode, JsonNodeUnmarshallContext)}.
     */
    public static JsonNode cellsParserPatch(final Map<SpreadsheetCellReference, Optional<SpreadsheetParserSelector>> cellToParser,
                                            final JsonNodeMarshallContext context) {
        Objects.requireNonNull(cellToParser, "cellToParser");
        Objects.requireNonNull(context, "context");

        return SpreadsheetDelta.cellsPatchFromMap(
                cellToParser,
                PARSER_PROPERTY,
                (pattern) -> context.marshall(pattern.orElse(null))
        );
    }

    /**
     * Creates a {@link JsonNode patch} which may be used to {@link #patchCells(SpreadsheetCellReferenceOrRange, JsonNode, JsonNodeUnmarshallContext)}.
     */
    public static JsonNode cellsStylePatch(final Map<SpreadsheetCellReference, TextStyle> cellToStyles,
                                           final JsonNodeMarshallContext context) {
        Objects.requireNonNull(cellToStyles, "cellToStyles");
        Objects.requireNonNull(context, "context");

        return cellsPatchFromMap(
                cellToStyles,
                STYLE_PROPERTY,
                context::marshall
        );
    }

    private static <T> JsonNode cellsPatchFromMap(final Map<SpreadsheetCellReference, T> cellToValue,
                                                  final JsonPropertyName propertyName,
                                                  final Function<T, JsonNode> marshaller) {
        // {
        //  "cells": [
        //    {
        //      "formula": {
        //        "text": "=1"
        //      }
        //    },
        //    {
        //      "formula": {
        //        "text": "=22"
        //      }
        //    }
        //  ]
        // }

        return makePatch(
                SpreadsheetDelta.CELLS_PROPERTY,
                JsonNode.object()
                        .setChildren(
                                cellToValue.entrySet()
                                        .stream()
                                        .map(ctv -> JsonNode.object()
                                                .setChildren(
                                                        Lists.of(
                                                                marshaller.apply(
                                                                        ctv.getValue()
                                                                ).setName(propertyName)
                                                        )
                                                ).setName(
                                                        JsonPropertyName.with(ctv.getKey().toString())
                                                )
                                        ).collect(Collectors.toList())
                        )
        );
    }

    /**
     * Creates a {@link JsonNode} patch that may be used by {@link #patch(JsonNode, JsonNodeUnmarshallContext)}.
     */
    public static JsonNode formulaPatch(final SpreadsheetFormula formula,
                                        final JsonNodeMarshallContext context) {
        Objects.requireNonNull(formula, "formula");
        Objects.requireNonNull(context, "context");

        return makePatch(
                FORMULA_PROPERTY,
                context.marshall(formula)
        );
    }

    /**
     * Creates a {@link SpreadsheetFormatterSelector} which can then be used to as an argument to {@link #patchCells(SpreadsheetCellReferenceOrRange, JsonNode, JsonNodeUnmarshallContext).}
     */
    public static JsonNode formatterPatch(final Optional<SpreadsheetFormatterSelector> formatter,
                                          final JsonNodeMarshallContext context) {
        Objects.requireNonNull(formatter, "formatter");
        Objects.requireNonNull(context, "context");

        return makePatch(
                FORMATTER_PROPERTY,
                formatter.isPresent() ?
                        context.marshall(formatter.get()) :
                        JsonNode.nullNode()
        );
    }

    /**
     * Creates a {@link SpreadsheetParserSelector} which can then be used to as an argument to {@link #patchCells(SpreadsheetCellReferenceOrRange, JsonNode, JsonNodeUnmarshallContext).}
     */
    public static JsonNode parserPatch(final Optional<SpreadsheetParserSelector> parser,
                                       final JsonNodeMarshallContext context) {
        Objects.requireNonNull(parser, "parser");
        Objects.requireNonNull(context, "context");

        return makePatch(
                PARSER_PROPERTY,
                parser.isPresent() ?
                        context.marshall(parser.get()) :
                        JsonNode.nullNode()
        );
    }

    /**
     * Creates a {@link JsonObject} which can then be used to as an argument to {@link #patchStyle(SpreadsheetCellRangeReference, Set, JsonNode, JsonNodeUnmarshallContext)}.
     */
    public static JsonNode stylePatch(final JsonNode style) {
        Objects.requireNonNull(style, "style");

        return makePatch(
                STYLE_PROPERTY,
                style
        );
    }

    private static JsonNode makePatch(final JsonPropertyName propertyName,
                                      final JsonNode value) {
        return JsonNode.object()
                .set(
                        propertyName,
                        value
                );
    }

    // Patchable.......................................................................................................

    /**
     * Patches the given {@link SpreadsheetDelta}.
     * Note only some properties may be patched (viewport, cell and window) others will throw an exception as invalid.
     * Attempts to patch an unknown cell will fail with an {@link IllegalArgumentException} being thrown.
     */
    @Override
    public SpreadsheetDelta patch(final JsonNode json,
                                  final JsonNodeUnmarshallContext context) {
        return this.patchValidateAndApply(
                null,
                json,
                Predicates.always(),
                context
        );
    }

    /**
     * Patches the cells within this {@link SpreadsheetDelta} using the provided patch, which may be any of the following:
     * <ul>
     *     <li>{@link SpreadsheetFormatPattern}</li>
     *     <li>{@link SpreadsheetParsePattern}</li>
     *     <li>{@link TextStyle}</li>
     * </ul>
     * Any attempt to patch any other property will result in a {@link IllegalArgumentException} being thrown. It is not
     * possible to patch a single formula for given {@link SpreadsheetCell cells}.<br>
     * <br>
     * Cells to be patched
     * <pre>
     * {
     *   "cells": {
     *     "A1": {
     *       "formula": {
     *         "text": ""
     *       }
     *     }
     *   }
     * }
     * </pre>
     * Format pattern patch
     * <pre>
     * {
     *   "formatter": "text-format-pattern @\"patched\""
     *   }
     * }
     * </pre>
     * Parse pattern patch
     * <pre>
     * {
     *   "parser": {
     *     "type": "spreadsheet-number-parser",
     *     "value": "\"patched\""
     *   }
     * }
     * </pre>
     * Style patch, any style property may be replaced by including a key/value entry with null resulting in the property
     * being removed for all cells.
     * <pre>
     * {
     *   "style": {
     *     "color": "#123456"
     *   }
     * }
     * </pre>
     */
    public SpreadsheetDelta patchCells(final SpreadsheetCellReferenceOrRange cellOrCellRange,
                                       final JsonNode json,
                                       final JsonNodeUnmarshallContext context) {
        Objects.requireNonNull(cellOrCellRange, "cellOrCellRange");

        return this.patchValidateAndApply(
                cellOrCellRange, // technically only required by patchFormat & patchStyle
                json,
                PATCH_CELL_PROPERTIES_PREDICATE,
                context
        );
    }

    private final static Predicate<String> PATCH_CELL_PROPERTIES_PREDICATE = Predicates.setContains(
            Sets.of(
                    SpreadsheetDelta.CELLS_PROPERTY_STRING,
                    SpreadsheetDelta.FORMULA_PROPERTY_STRING,
                    SpreadsheetDelta.FORMATTER_PROPERTY_STRING,
                    SpreadsheetDelta.PARSER_PROPERTY_STRING,
                    SpreadsheetDelta.STYLE_PROPERTY_STRING
            )
    );

    // PatchColumns.....................................................................................................

    /**
     * Patches the given {@link SpreadsheetDelta} assuming only columns have been patched.
     * Note only some properties may be patched (viewport, column and window) others will throw an exception as invalid.
     * Attempts to patch an unknown column will fail with an {@link IllegalArgumentException} being thrown.
     * <pre>
     * {
     *   "columns": {
     *     "Z": {
     *       "hidden": false
     *     }
     *   }
     * }
     * </pre>
     */
    public SpreadsheetDelta patchColumns(final JsonNode json,
                                         final JsonNodeUnmarshallContext context) {
        return this.patchValidateAndApply(
                null, // dont care now will matter when patchColumns supports format&style
                json,
                Predicates.is(SpreadsheetDelta.COLUMNS_PROPERTY_STRING),
                context
        );
    }

    // PatchRows.......................................................................................................

    /**
     * Patches the given {@link SpreadsheetDelta} assuming only rows have been patched.
     * Note only some properties may be patched (viewport, row and window) others will throw an exception as invalid.
     * Attempts to patch an unknown row will fail with an {@link IllegalArgumentException} being thrown.
     * <pre>
     * {
     *   "rows": {
     *     "9": {
     *       "hidden": false
     *     }
     *   }
     * }
     * </pre>
     */
    public SpreadsheetDelta patchRows(final JsonNode json,
                                      final JsonNodeUnmarshallContext context) {
        return this.patchValidateAndApply(
                null, // dont care now will matter when patchRows supports format&style
                json,
                Predicates.is(SpreadsheetDelta.ROWS_PROPERTY_STRING),
                context
        );
    }

    // two pass patch, first validate valid properties are being patched
    // also want to verify not an invalid combo like cells and style, before actually patching.
    private SpreadsheetDelta patchValidateAndApply(final SpreadsheetSelection selection,
                                                   final JsonNode json,
                                                   final Predicate<String> patchableProperties,
                                                   final JsonNodeUnmarshallContext context) {
        Objects.requireNonNull(json, "json");
        Objects.requireNonNull(context, "context");

        patchValidate(
                selection,
                json,
                patchableProperties
        );

        return patchApply(
                selection,
                json,
                context
        );
    }

    private static void patchValidate(final SpreadsheetSelection selection,
                                      final JsonNode json,
                                      final Predicate<String> patchableProperties) {
        boolean cellsPatched = false;
        boolean formulaPatched = false;
        boolean formatterPatched = false;
        boolean parserPatched = false;
        boolean stylePatched = false;

        for (final JsonNode propertyAndValue : json.objectOrFail().children()) {

            final JsonPropertyName propertyName = propertyAndValue.name();
            final String propertyNameString = propertyName.value();
            boolean valid = patchableProperties.test(propertyNameString);

            switch (propertyNameString) {
                case VIEWPORT_SELECTION_PROPERTY_STRING:
                    valid = true;
                    break;
                case CELLS_PROPERTY_STRING:
                    if (valid) {
                        cellsPatched = true;
                    }
                    break;
                case COLUMNS_PROPERTY_STRING:
                    break;
                case FORMATTER_PROPERTY_STRING:
                    formatterPatched = true;
                    break;
                case FORMATTED_VALUE_PROPERTY_STRING:
                    break;
                case FORMULA_PROPERTY_STRING:
                    formulaPatched = true;
                    break;
                case PARSER_PROPERTY_STRING:
                    parserPatched = true;
                    break;
                case ROWS_PROPERTY_STRING:
                    break;
                case STYLE_PROPERTY_STRING:
                    stylePatched = true;
                    break;
                case LABELS_PROPERTY_STRING:
                case DELETED_CELLS_PROPERTY_STRING:
                case DELETED_COLUMNS_PROPERTY_STRING:
                case DELETED_ROWS_PROPERTY_STRING:
                case COLUMN_WIDTHS_PROPERTY_STRING:
                case ROW_HEIGHTS_PROPERTY_STRING:
                    valid = false;
                    break;
                case WINDOW_PROPERTY_STRING:
                    valid = true;
                    break;
                default:
                    Patchable.unknownPropertyPresent(propertyName, propertyAndValue);
                    break;
            }

            if (false == valid) {
                Patchable.invalidPropertyPresent(propertyName, propertyAndValue);
            }
        }

        if (cellsPatched && formatterPatched) {
            patchInvalidFail(CELLS_PROPERTY_STRING, FORMATTER_PROPERTY_STRING);
        }

        if (cellsPatched && parserPatched) {
            patchInvalidFail(CELLS_PROPERTY_STRING, PARSER_PROPERTY_STRING);
        }

        if (cellsPatched && stylePatched) {
            patchInvalidFail(CELLS_PROPERTY_STRING, STYLE_PROPERTY_STRING);
        }

        if (false == cellsPatched && stylePatched && null == selection) {
            throw new IllegalArgumentException(
                    "Patch includes " +
                            CharSequences.quote(STYLE_PROPERTY_STRING) +
                            " but is missing selection."
            );
        }

        if (formatterPatched && parserPatched) {
            patchInvalidFail(FORMATTER_PROPERTY_STRING, PARSER_PROPERTY_STRING);
        }

        if (formatterPatched && stylePatched) {
            patchInvalidFail(FORMATTER_PROPERTY_STRING, STYLE_PROPERTY_STRING);
        }

        if (parserPatched && stylePatched) {
            patchInvalidFail(PARSER_PROPERTY_STRING, STYLE_PROPERTY_STRING);
        }
    }

    @SuppressWarnings("lgtm[java/dereferenced-value-may-be-null]")
    private SpreadsheetDelta patchApply(final SpreadsheetSelection selection,
                                        final JsonNode json,
                                        final JsonNodeUnmarshallContext context) {
        SpreadsheetDelta patched = this;

        Set<SpreadsheetCell> cells = this.cells();
        Set<SpreadsheetColumn> columns = this.columns();
        Set<SpreadsheetRow> rows = this.rows();
        SpreadsheetViewportWindows window = this.window();

        for (final JsonNode propertyAndValue : json.objectOrFail().children()) {

            final JsonPropertyName propertyName = propertyAndValue.name();
            final String propertyNameString = propertyName.value();

            switch (propertyNameString) {
                case VIEWPORT_SELECTION_PROPERTY_STRING:
                    patched = patched.setViewport(
                            unmarshallViewport(propertyAndValue, context)
                    );
                    break;
                case CELLS_PROPERTY_STRING:
                    cells = patchApplyCells(
                            selection,
                            propertyAndValue,
                            context
                    );
                    break;
                case COLUMNS_PROPERTY_STRING:
                    columns = patchApplyColumns(
                            selection,
                            propertyAndValue,
                            context
                    );
                    break;
                case FORMATTER_PROPERTY_STRING:
                    cells = patchApplyFormatter(
                            selection.toCellRange(),
                            cells,
                            JsonNode.object()
                                    .set(FORMATTER_PROPERTY, propertyAndValue),
                            context
                    );
                    break;
                case FORMULA_PROPERTY_STRING:
                    cells = patchApplyCellFormula(
                            selection.toCellRange(),
                            cells,
                            JsonNode.object()
                                    .set(
                                            FORMULA_PROPERTY,
                                            propertyAndValue
                                    ),
                            context
                    );
                    break;
                case PARSER_PROPERTY_STRING:
                    cells = patchApplyParser(
                            selection.toCellRange(),
                            cells,
                            JsonNode.object()
                                    .set(PARSER_PROPERTY, propertyAndValue),
                            context
                    );
                    break;
                case ROWS_PROPERTY_STRING:
                    rows = patchApplyRows(
                            selection,
                            propertyAndValue,
                            context
                    );
                    break;
                case STYLE_PROPERTY_STRING:
                    cells = patchStyle(
                            selection.toCellRange(),
                            cells,
                            propertyAndValue,
                            context
                    );
                    break;
                case LABELS_PROPERTY_STRING:
                case DELETED_CELLS_PROPERTY_STRING:
                case DELETED_COLUMNS_PROPERTY_STRING:
                case DELETED_ROWS_PROPERTY_STRING:
                case COLUMN_WIDTHS_PROPERTY_STRING:
                case ROW_HEIGHTS_PROPERTY_STRING:
                    break;
                case WINDOW_PROPERTY_STRING:
                    window = unmarshallWindow(propertyAndValue);
                    break;
                default:
                    break;
            }
        }

        return patched.setCells(NO_CELLS)
                .setColumns(NO_COLUMNS)
                .setRows(NO_ROWS)
                .setWindow(window)
                .setColumns(columns)
                .setRows(rows)
                .setCells(cells)
                .setWindow(window);
    }

    private Set<SpreadsheetCell> patchApplyCells(final SpreadsheetSelection selection,
                                                 final JsonNode node,
                                                 final JsonNodeUnmarshallContext context) {

        return node.isNull() ?
                NO_CELLS :
                this.patchApplyCellsNonNull(
                        selection,
                        node,
                        context
                );
    }

    /**
     * Takes a json object of reference to cell and patches the existing cells in this {@link SpreadsheetDelta}.
     * If the patch is a new cell it is added, existing cells are patched.
     */
    private Set<SpreadsheetCell> patchApplyCellsNonNull(final SpreadsheetSelection selection,
                                                        final JsonNode node,
                                                        final JsonNodeUnmarshallContext context) {
        return patchApplySelectionToValue(
                selection,
                node,
                SpreadsheetSelection::parseCell,
                this::cell,
                SpreadsheetCell.class,
                context
        );
    }

    private Set<SpreadsheetColumn> patchApplyColumns(final SpreadsheetSelection selection,
                                                     final JsonNode node,
                                                     final JsonNodeUnmarshallContext context) {

        return node.isNull() ?
                NO_COLUMNS :
                this.patchApplyColumnsNonNull(
                        selection,
                        node,
                        context
                );
    }

    /**
     * Takes a json object of reference to column and patches the existing columns in this {@link SpreadsheetDelta}.
     * If the patch is a new column it is added, existing columns are patched.
     */
    private Set<SpreadsheetColumn> patchApplyColumnsNonNull(final SpreadsheetSelection selection,
                                                            final JsonNode node,
                                                            final JsonNodeUnmarshallContext context) {
        return patchApplySelectionToValue(
                selection,
                node,
                SpreadsheetSelection::parseColumn,
                this::column,
                SpreadsheetColumn.class,
                context
        );
    }

    /**
     * Traverses the cells, patching each with the provided {@link JsonNode formula}.
     * <pre>
     * {
     *   "formula": {
     *     "text": "=1+2"
     *   }
     * }
     * </pre>
     */
    private static Set<SpreadsheetCell> patchApplyCellFormula(final SpreadsheetCellRangeReference cellRange,
                                                              final Set<SpreadsheetCell> cells,
                                                              final JsonNode patch,
                                                              final JsonNodeUnmarshallContext context) {
        final SpreadsheetFormula formula = context.unmarshall(
                patch.objectOrFail()
                        .getOrFail(FORMULA_PROPERTY),
                SpreadsheetFormula.class
        );

        return patchAllCells(
                cellRange,
                cells,
                c -> c.setFormula(formula),
                r -> r.setFormula(formula)
        );
    }

    /**
     * Traverses the cells, patching each with the provided {@link JsonNode format}.
     * <pre>
     * {
     *   "formatter": "text-format-pattern @\"patched\""
     * }
     * </pre>
     */
    private static Set<SpreadsheetCell> patchApplyFormatter(final SpreadsheetCellRangeReference cellRange,
                                                            final Set<SpreadsheetCell> cells,
                                                            final JsonNode patch,
                                                            final JsonNodeUnmarshallContext context) {
        final Optional<SpreadsheetFormatterSelector> formatter = Optional.ofNullable(
                context.unmarshall(
                        patch.objectOrFail()
                                .getOrFail(FORMATTER_PROPERTY),
                        SpreadsheetFormatterSelector.class
                )
        );

        return patchAllCells(
                cellRange,
                cells,
                c -> c.setFormatter(formatter),
                r -> r.setFormula(SpreadsheetFormula.EMPTY)
                        .setFormatter(formatter)
        );
    }

    /**
     * Traverses the cells, patching each with the provided {@link JsonNode parser}.
     * <pre>
     * {
     *   "parser": "spreadsheet-number-parser patched"
     * }
     * </pre>
     */
    private static Set<SpreadsheetCell> patchApplyParser(final SpreadsheetCellRangeReference cellRange,
                                                         final Set<SpreadsheetCell> cells,
                                                         final JsonNode patch,
                                                         final JsonNodeUnmarshallContext context) {
        final Optional<SpreadsheetParserSelector> parserSelector = Optional.ofNullable(
                context.unmarshall(
                        patch.objectOrFail()
                                .getOrFail(PARSER_PROPERTY),
                        SpreadsheetParserSelector.class
                )
        );

        return patchAllCells(
                cellRange,
                cells,
                c -> c.setParser(parserSelector),
                r -> r.setFormula(SpreadsheetFormula.EMPTY)
                        .setParser(parserSelector)
        );
    }

    private Set<SpreadsheetRow> patchApplyRows(final SpreadsheetSelection selection,
                                               final JsonNode node,
                                               final JsonNodeUnmarshallContext context) {

        return node.isNull() ?
                NO_ROWS :
                this.patchApplyRowsNonNull(
                        selection,
                        node,
                        context
                );
    }

    /**
     * Takes a json object of reference to row and patches the existing rows in this {@link SpreadsheetDelta}.
     * If the patch is a new row it is added, existing rows are patched.
     */
    private Set<SpreadsheetRow> patchApplyRowsNonNull(final SpreadsheetSelection selection,
                                                      final JsonNode node,
                                                      final JsonNodeUnmarshallContext context) {
        return patchApplySelectionToValue(
                selection,
                node,
                SpreadsheetSelection::parseRow,
                this::row,
                SpreadsheetRow.class,
                context
        );
    }

    private <S extends SpreadsheetSelection, V extends Patchable<V>> Set<V> patchApplySelectionToValue(final SpreadsheetSelection patchSelection,
                                                                                                       final JsonNode node,
                                                                                                       final Function<String, S> selectionParser,
                                                                                                       final Function<S, Optional<V>> selectionToValue,
                                                                                                       final Class<V> valueType,
                                                                                                       final JsonNodeUnmarshallContext context) {
        // collect either patch or invalid cells.
        Set<V> patched = null;
        Set<S> invalid = null;

        for (final JsonNode child : node.objectOrFail().children()) {
            final JsonPropertyName propertyName = child.name();

            final S selection = selectionParser.apply(
                    propertyName.value()
            );
            if (null != patchSelection && false == patchSelection.test(selection)) {
                if (null == invalid) {
                    invalid = Sets.ordered();
                }
                invalid.add(selection);
                patched = null; // no longer patching.
                continue;
            }
            // skip patching only interested in collecting invalid patch cells
            if (null != invalid) {
                continue;
            }

            final V add;

            final Optional<V> old = selectionToValue.apply(selection);
            if (old.isPresent()) {
                add = old.get()
                        .patch(child, context);
            } else {
                add = context.unmarshall(
                        JsonNode.object()
                                .set(propertyName, child),
                        valueType
                );
            }

            if (null == patched) {
                patched = Sets.ordered();
            }
            patched.add(add);
        }

        if (null != invalid) {
            throw new IllegalArgumentException(
                    "Patch includes cells " +
                            invalid.stream()
                                    .map(Objects::toString)
                                    .collect(Collectors.joining(", ")) +
                            " outside " +
                            patchSelection.toStringMaybeStar()
            );
        }

        // helps prevent NPE in patchCells
        return null == patched ?
                Sets.empty() :
                patched;
    }

    /**
     * Traverses the cells, patching each with the provided {@link JsonNode style}.
     * <pre>
     * {
     *   "style": {
     *     "color": "#123456"
     *   }
     * }
     * </pre>
     */
    private static Set<SpreadsheetCell> patchStyle(final SpreadsheetCellRangeReference cellRange,
                                                   final Set<SpreadsheetCell> cells,
                                                   final JsonNode patch,
                                                   final JsonNodeUnmarshallContext context) {
        final TextStyle style = TextStyle.EMPTY.patch(
                patch,
                context
        );

        return patchAllCells(
                cellRange,
                cells,
                c -> c.setStyle(
                        c.style()
                                .patch(
                                        patch,
                                        context
                                )
                ),
                r -> r.setFormula(SpreadsheetFormula.EMPTY)
                        .setStyle(style)
        );
    }

    private static Set<SpreadsheetCell> patchAllCells(final SpreadsheetCellRangeReference cellRange,
                                                      final Set<SpreadsheetCell> cells,
                                                      final Function<SpreadsheetCell, SpreadsheetCell> patcher,
                                                      final Function<SpreadsheetCellReference, SpreadsheetCell> creator) {
        final Set<SpreadsheetCell> patched = SortedSets.tree();
        final Set<SpreadsheetCellReference> patchedCellReferences = SortedSets.tree();

        for (final SpreadsheetCell cell : cells) {
            patched.add(
                    patcher.apply(cell)
            );

            patchedCellReferences.add(
                    cell.reference()
            );
        }

        if (patched.size() < cellRange.count()) {
            for (final SpreadsheetCellReference possible : cellRange) {
                if (cellRange.testCell(possible) && false == patchedCellReferences.contains(possible)) {
                    patched.add(
                            creator.apply(possible)
                    );
                }
            }
        }

        return patched;
    }

    // patching cells can only patch individual cells or a style for all selected cells but not both.
    private static void patchInvalidFail(final String first,
                                         final String second) {
        throw new IllegalArgumentException("Patch must not contain both " + CharSequences.quote(first) + " and " + CharSequences.quote(second));
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
            if (false == cells.isNull()) {
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
            final SpreadsheetExpressionReference cellOrLabelName = SpreadsheetSelection.parseCellOrLabel(
                    child.getKey()
                            .value()
            );

            final JsonNode value = child.getValue();
            resolved.add(
                    cellOrLabelName.isLabelName() ?
                            value.setName(
                                    JsonPropertyName.with(
                                            labelToCell.apply(
                                                    cellOrLabelName.toLabelName()
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
            final Optional<SpreadsheetViewport> viewport = this.viewport();
            if (viewport.isPresent()) {
                printer.println("viewport:");
                printer.indent();
                {
                    viewport.get()
                            .printTree(printer);
                }
                printer.outdent();
            }

            printTreeCollectionTreePrinter("cells", this.cells(), printer);
            printTreeCollectionTreePrinter("columns", this.columns(), printer);
            printTreeCollectionTreePrinter("labels", this.labels(), printer);
            printTreeCollectionTreePrinter("rows", this.rows(), printer);

            // references...............................................................................................
            {
                final Map<SpreadsheetCellReference, Set<SpreadsheetExpressionReference>> references = this.references();
                if (false == references.isEmpty()) {
                    printer.println("references:");
                    printer.indent();
                    {
                        for (final Map.Entry<SpreadsheetCellReference, Set<SpreadsheetExpressionReference>> cellAndReferences : references.entrySet()) {
                            this.printTreeCollectionCsv(
                                    cellAndReferences.getKey().toString(), // label
                                    cellAndReferences.getValue(), // collection
                                    printer
                            );
                        }
                    }
                    printer.outdent();
                }
            }

            printTreeCollectionCsv(
                    "deletedCells",
                    this.deletedCells(),
                    printer
            );

            printTreeCollectionCsv(
                    "deletedColumns",
                    this.deletedColumns(),
                    printer
            );

            printTreeCollectionCsv(
                    "deletedRows",
                    this.deletedRows(),
                    printer
            );

            printTreeCollectionCsv(
                    "deletedLabels",
                    this.deletedLabels(),
                    printer
            );

            printTreeCollectionCsv(
                    "matchedCells",
                    this.matchedCells(),
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

            this.printTreeOptionalInt(
                    "columnCount",
                    this.columnCount(),
                    printer
            );

            this.printTreeOptionalInt(
                    "rowCount",
                    this.rowCount(),
                    printer
            );

            this.printWindow(printer);
        }
        printer.outdent();
    }

    private <T extends TreePrintable> void printTreeCollectionTreePrinter(final String label,
                                                                          final Collection<T> collection,
                                                                          final IndentingPrinter printer) {
        this.printTreeLabelAndCollection(
                label,
                collection,
                this::printTreeCollection,
                printer
        );
    }

    private <T extends TreePrintable> void printTreeCollectionCsv(final String label,
                                                                  final Collection<T> collection,
                                                                  final IndentingPrinter printer) {
        this.printTreeLabelAndCollection(
                label,
                collection,
                this::printTreeCollectionCsv,
                printer
        );
    }

    private <T extends TreePrintable> void printTreeLabelAndCollection(final String label,
                                                                       final Collection<T> collection,
                                                                       final BiConsumer<Collection<T>, IndentingPrinter> printCollection,
                                                                       final IndentingPrinter printer) {
        if (false == collection.isEmpty()) {
            printer.println(label + ":");
            printer.indent();
            {
                printCollection.accept(collection, printer);
            }
            printer.outdent();
        }
    }

    private <T extends TreePrintable> void printTreeCollection(final Collection<T> collection,
                                                               final IndentingPrinter printer) {
        for (final T element : collection) {
            element.printTree(printer);
        }
    }

    private <T extends TreePrintable> void printTreeCollectionCsv(final Collection<T> collection,
                                                                  final IndentingPrinter printer) {
        printer.println(
                CharacterConstant.COMMA.toSeparatedString(
                        collection,
                        Object::toString
                )
        );
    }

    private void printTreeMap(final String label,
                              final Map<? extends SpreadsheetColumnOrRowReference, Double> columnOrRowAndDoubleValue,
                              final IndentingPrinter printer) {
        if (false == columnOrRowAndDoubleValue.isEmpty()) {
            printer.println(label + ":");
            printer.indent();
            {
                for (final Map.Entry<? extends SpreadsheetColumnOrRowReference, Double> referenceAndWidth : columnOrRowAndDoubleValue.entrySet()) {
                    printer.println(referenceAndWidth.getKey() + ": " + referenceAndWidth.getValue());
                }
            }
            printer.outdent();
        }
    }

    private void printTreeOptionalInt(final String label,
                                      final OptionalInt value,
                                      final IndentingPrinter printer) {
        if (value.isPresent()) {
            printer.println(label + ": " + value.getAsInt());
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
                case VIEWPORT_SELECTION_PROPERTY_STRING:
                    unmarshalled = unmarshalled.setViewport(
                            unmarshallViewport(
                                    child,
                                    context
                            )
                    );
                    break;
                case CELLS_PROPERTY_STRING:
                    unmarshalled = unmarshalled.setCells(
                            unmarshallCsv(
                                    child,
                                    SpreadsheetCell.class,
                                    context
                            )
                    );
                    break;
                case COLUMNS_PROPERTY_STRING:
                    unmarshalled = unmarshalled.setColumns(
                            unmarshallCsv(
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
                            unmarshallCsv(
                                    child,
                                    SpreadsheetRow.class,
                                    context
                            )
                    );
                    break;
                case REFERENCES_PROPERTY_STRING:
                    final Map<SpreadsheetCellReference, Set<SpreadsheetExpressionReference>> references = Maps.sorted();

                    for(final JsonNode reference : child.objectOrFail().children()) {
                        references.put(
                                SpreadsheetSelection.parseCell(
                                    reference.name()
                                            .toString()
                                ),
                                context.unmarshallSetWithType(reference)
                        );
                    }

                    unmarshalled = unmarshalled.setReferences(references);
                    break;
                case DELETED_CELLS_PROPERTY_STRING:
                    unmarshalled = unmarshalled.setDeletedCells(
                            unmarshallSelectionCsv(
                                    child,
                                    SpreadsheetSelection::parseCell
                            )
                    );
                    break;
                case DELETED_COLUMNS_PROPERTY_STRING:
                    unmarshalled = unmarshalled.setDeletedColumns(
                            unmarshallSelectionCsv(
                                    child,
                                    SpreadsheetSelection::parseColumn
                            )
                    );
                    break;
                case DELETED_ROWS_PROPERTY_STRING:
                    unmarshalled = unmarshalled.setDeletedRows(
                            unmarshallSelectionCsv(
                                    child,
                                    SpreadsheetSelection::parseRow
                            )
                    );
                    break;
                case DELETED_LABELS_PROPERTY_STRING:
                    unmarshalled = unmarshalled.setDeletedLabels(
                            unmarshallSelectionCsv(
                                    child,
                                    SpreadsheetSelection::labelName
                            )
                    );
                    break;
                case MATCHED_CELLS_PROPERTY_STRING:
                    unmarshalled = unmarshalled.setMatchedCells(
                            unmarshallSelectionCsv(
                                    child,
                                    SpreadsheetSelection::parseCell
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

                case COLUMN_COUNT_PROPERTY_STRING:
                    unmarshalled = unmarshalled.setColumnCount(
                            OptionalInt.of(
                                    context.unmarshall(
                                            child,
                                            Integer.class
                                    )
                            )
                    );
                    break;
                case ROW_COUNT_PROPERTY_STRING:
                    unmarshalled = unmarshalled.setRowCount(
                            OptionalInt.of(
                                    context.unmarshall(
                                            child,
                                            Integer.class
                                    )
                            )
                    );
                    break;

                case WINDOW_PROPERTY_STRING:
                    unmarshalled = unmarshalled.setWindow(
                            unmarshallWindow(
                                    child
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

    private static Optional<SpreadsheetViewport> unmarshallViewport(final JsonNode node,
                                                                    final JsonNodeUnmarshallContext context) {
        return Optional.ofNullable(
                context.unmarshall(node, SpreadsheetViewport.class)
        );
    }

    private static <S extends SpreadsheetSelection> Set<S> unmarshallSelectionCsv(final JsonNode csv,
                                                                                  final Function<String, S> parser) {
        return Arrays.stream(
                        csv.stringOrFail()
                                .split(CSV_COMMA.string())
                ).map(parser)
                .collect(Collectors.toCollection(Sets::ordered));
    }

    private static <T> Set<T> unmarshallCsv(final JsonNode node,
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

    private static SpreadsheetViewportWindows unmarshallWindow(final JsonNode json) {
        return json.isNull() ?
                NO_WINDOW :
                SpreadsheetViewportWindows.parse(json.stringOrFail());
    }

    static <S extends SpreadsheetSelection> String csv(final Collection<S> selections) {
        return CSV_COMMA.toSeparatedString(
                selections,
                SpreadsheetSelection::toString
        );
    }

    /**
     * <pre>
     * {
     *   "viewport": {
     *     "selection": {
     *        "type": "spreadsheet-column",
     *        "value: "Z"
     *     }
     *   }
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

        {
            final Optional<SpreadsheetViewport> viewport = this.viewport;
            if (viewport.isPresent()) {
                children.add(
                        context.marshall(viewport.get())
                                .setName(VIEWPORT_SELECTION_PROPERTY)
                );
            }
        }

        {
            final Set<SpreadsheetCell> cells = this.cells;
            if (false == cells.isEmpty()) {
                children.add(marshallCellOrColumnsOrRow(cells, context).setName(CELLS_PROPERTY));
            }
        }

        {
            final Set<SpreadsheetColumn> columns = this.columns;
            if (false == columns.isEmpty()) {
                children.add(marshallCellOrColumnsOrRow(columns, context).setName(COLUMNS_PROPERTY));
            }
        }

        {
            final Set<SpreadsheetLabelMapping> labels = this.labels;
            if (false == labels.isEmpty()) {
                children.add(
                        context.marshallCollection(labels)
                                .setName(LABELS_PROPERTY)
                );
            }
        }

        {
            final Set<SpreadsheetRow> rows = this.rows;
            if (false == rows.isEmpty()) {
                children.add(marshallCellOrColumnsOrRow(rows, context).setName(ROWS_PROPERTY));
            }
        }

        {
            // A1:
            //  [ B2, C3, D4 ]
            final Map<SpreadsheetCellReference, Set<SpreadsheetExpressionReference>> references = this.references;
            if (false == references.isEmpty()) {
                children.add(
                        JsonNode.object()
                                .setChildren(
                                        references.entrySet()
                                                .stream()
                                                .map(car ->
                                                        context.marshallCollectionWithType(car.getValue())
                                                                .setName(
                                                                        JsonPropertyName.with(
                                                                                car.getKey()
                                                                                        .toString()
                                                                        )
                                                                )
                                                ).collect(Collectors.toList())
                                ).setName(REFERENCES_PROPERTY)
                );
            }
        }

        {
            final Set<SpreadsheetCellReference> deletedCells = this.deletedCells;
            if (false == deletedCells.isEmpty()) {
                children.add(
                        marshallSelection(deletedCells, DELETED_CELLS_PROPERTY)
                );
            }
        }

        {
            final Set<SpreadsheetColumnReference> deletedColumns = this.deletedColumns;
            if (false == deletedColumns.isEmpty()) {
                children.add(
                        marshallSelection(deletedColumns, DELETED_COLUMNS_PROPERTY)
                );
            }
        }

        {
            final Set<SpreadsheetRowReference> deletedRows = this.deletedRows;
            if (false == deletedRows.isEmpty()) {
                children.add(
                        marshallSelection(deletedRows, DELETED_ROWS_PROPERTY)
                );
            }
        }

        {
            final Set<SpreadsheetLabelName> deletedLabels = this.deletedLabels;
            if (false == deletedLabels.isEmpty()) {
                children.add(
                        marshallSelection(deletedLabels, DELETED_LABELS_PROPERTY)
                );
            }
        }

        {
            final Set<SpreadsheetCellReference> matchedCells = this.matchedCells;
            if (false == matchedCells.isEmpty()) {
                children.add(
                        marshallSelection(matchedCells, MATCHED_CELLS_PROPERTY)
                );
            }
        }

        {
            final Map<SpreadsheetColumnReference, Double> columnWidths = this.columnWidths;
            if (false == columnWidths.isEmpty()) {
                children.add(
                        context.marshallMap(columnWidths)
                                .setName(COLUMN_WIDTHS_PROPERTY)
                );
            }
        }

        {
            final Map<SpreadsheetRowReference, Double> rowsHeights = this.rowHeights;
            if (false == rowsHeights.isEmpty()) {
                children.add(
                        context.marshallMap(rowsHeights)
                                .setName(ROW_HEIGHTS_PROPERTY)
                );
            }
        }

        {
            final OptionalInt columnCount = this.columnCount;
            if (columnCount.isPresent()) {
                children.add(
                        context.marshall(columnCount.getAsInt())
                                .setName(COLUMN_COUNT_PROPERTY)
                );
            }
        }

        {
            final OptionalInt rowCount = this.rowCount;
            if (rowCount.isPresent()) {
                children.add(
                        context.marshall(rowCount.getAsInt())
                                .setName(ROW_COUNT_PROPERTY)
                );
            }
        }

        {
            final SpreadsheetViewportWindows window = this.window();
            if (false == window.isEmpty()) {
                children.add(
                        context.marshall(window)
                                .setName(WINDOW_PROPERTY)
                );
            }
        }

        return JsonNode.object().setChildren(children);
    }

    private final static CharacterConstant CSV_COMMA = CharacterConstant.COMMA;

    /**
     * Accepts a {@link Collection} of any {@link SpreadsheetSelection} and returns a {@link JsonString} with the selections
     * as a CSV.
     */
    private static <S extends SpreadsheetSelection> JsonString marshallSelection(final Collection<S> selections,
                                                                                 final JsonPropertyName name) {
        return JsonNode.string(
                csv(selections)
        ).setName(name);
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

    private final static String VIEWPORT_SELECTION_PROPERTY_STRING = "viewport";
    private final static String CELLS_PROPERTY_STRING = "cells";
    private final static String COLUMNS_PROPERTY_STRING = "columns";
    private final static String FORMATTED_VALUE_PROPERTY_STRING = "formattedValue";
    private final static String FORMULA_PROPERTY_STRING = "formula";
    private final static String FORMATTER_PROPERTY_STRING = "formatter";
    private final static String LABELS_PROPERTY_STRING = "labels";
    private final static String PARSER_PROPERTY_STRING = "parser";
    private final static String REFERENCES_PROPERTY_STRING = "references";
    private final static String ROWS_PROPERTY_STRING = "rows";
    private final static String STYLE_PROPERTY_STRING = "style"; // only used by patchCells

    private final static String DELETED_CELLS_PROPERTY_STRING = "deletedCells";
    private final static String DELETED_COLUMNS_PROPERTY_STRING = "deletedColumns";
    private final static String DELETED_ROWS_PROPERTY_STRING = "deletedRows";

    private final static String DELETED_LABELS_PROPERTY_STRING = "deletedLabels";

    private final static String MATCHED_CELLS_PROPERTY_STRING = "matchedCells";

    private final static String COLUMN_WIDTHS_PROPERTY_STRING = "columnWidths";
    private final static String ROW_HEIGHTS_PROPERTY_STRING = "rowHeights";

    private final static String COLUMN_COUNT_PROPERTY_STRING = "columnCount";
    private final static String ROW_COUNT_PROPERTY_STRING = "rowCount";

    private final static String WINDOW_PROPERTY_STRING = "window";

    // @VisibleForTesting
    final static JsonPropertyName VIEWPORT_SELECTION_PROPERTY = JsonPropertyName.with(VIEWPORT_SELECTION_PROPERTY_STRING);
    // @VisibleForTesting
    final static JsonPropertyName CELLS_PROPERTY = JsonPropertyName.with(CELLS_PROPERTY_STRING);
    // @VisibleForTesting
    final static JsonPropertyName COLUMNS_PROPERTY = JsonPropertyName.with(COLUMNS_PROPERTY_STRING);

    // @VisibleForTesting
    final static JsonPropertyName FORMULA_PROPERTY = JsonPropertyName.with(FORMULA_PROPERTY_STRING);

    // @VisibleForTesting
    final static JsonPropertyName FORMATTER_PROPERTY = JsonPropertyName.with(FORMATTER_PROPERTY_STRING);
    // @VisibleForTesting
    final static JsonPropertyName LABELS_PROPERTY = JsonPropertyName.with(LABELS_PROPERTY_STRING);
    // @VisibleForTesting
    final static JsonPropertyName PARSER_PROPERTY = JsonPropertyName.with(PARSER_PROPERTY_STRING);
    // @VisibleForTesting
    final static JsonPropertyName ROWS_PROPERTY = JsonPropertyName.with(ROWS_PROPERTY_STRING);

    // @VisibleForTesting
    final static JsonPropertyName REFERENCES_PROPERTY = JsonPropertyName.with(REFERENCES_PROPERTY_STRING);

    // @VisibleForTesting
    final static JsonPropertyName DELETED_CELLS_PROPERTY = JsonPropertyName.with(DELETED_CELLS_PROPERTY_STRING);
    // @VisibleForTesting
    final static JsonPropertyName DELETED_COLUMNS_PROPERTY = JsonPropertyName.with(DELETED_COLUMNS_PROPERTY_STRING);
    // @VisibleForTesting
    final static JsonPropertyName DELETED_ROWS_PROPERTY = JsonPropertyName.with(DELETED_ROWS_PROPERTY_STRING);

    // @VisibleForTesting
    final static JsonPropertyName DELETED_LABELS_PROPERTY = JsonPropertyName.with(DELETED_LABELS_PROPERTY_STRING);

    // @VisibleForTesting
    final static JsonPropertyName MATCHED_CELLS_PROPERTY = JsonPropertyName.with(MATCHED_CELLS_PROPERTY_STRING);

    // @VisibleForTesting
    final static JsonPropertyName COLUMN_WIDTHS_PROPERTY = JsonPropertyName.with(COLUMN_WIDTHS_PROPERTY_STRING);
    // @VisibleForTesting
    final static JsonPropertyName ROW_HEIGHTS_PROPERTY = JsonPropertyName.with(ROW_HEIGHTS_PROPERTY_STRING);

    // @VisibleForTesting
    final static JsonPropertyName COLUMN_COUNT_PROPERTY = JsonPropertyName.with(COLUMN_COUNT_PROPERTY_STRING);
    // @VisibleForTesting
    final static JsonPropertyName ROW_COUNT_PROPERTY = JsonPropertyName.with(ROW_COUNT_PROPERTY_STRING);

    // @VisibleForTesting
    final static JsonPropertyName STYLE_PROPERTY = JsonPropertyName.with(STYLE_PROPERTY_STRING); // only used by patchCells
    // @VisibleForTesting
    final static JsonPropertyName WINDOW_PROPERTY = JsonPropertyName.with(WINDOW_PROPERTY_STRING);

    static {
        // force static initializers to run, preventing Json type name lookup failures.
        SpreadsheetViewport.SEPARATOR.toString();
        SpreadsheetViewportWindows.EMPTY.toString();
        SpreadsheetFunctionName.with("forceJsonRegister");

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
                this.viewport,
                this.cells,
                this.columns,
                this.labels,
                this.rows,
                this.deletedCells,
                this.deletedColumns,
                this.deletedRows,
                this.matchedCells,
                this.columnWidths,
                this.rowHeights,
                this.columnCount,
                this.rowCount,
                this.window().hashCode()
        );
    }

    @Override
    public final boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetDelta &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetDelta other) {
        return this.viewport.equals(other.viewport) &&
                this.cells.equals(other.cells) &&
                this.columns.equals(other.columns) &&
                this.labels.equals(other.labels) &&
                this.rows.equals(other.rows) &&
                this.references.equals(other.references) &&
                this.deletedCells.equals(other.deletedCells) &&
                this.deletedColumns.equals(other.deletedColumns) &&
                this.deletedRows.equals(other.deletedRows) &&
                this.deletedLabels.equals(other.deletedLabels) &&
                this.matchedCells.equals(other.matchedCells) &&
                this.columnWidths.equals(other.columnWidths) &&
                this.rowHeights.equals(other.rowHeights) &&
                this.columnCount.equals(other.columnCount) &&
                this.rowCount.equals(other.rowCount) &&
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
                .value(this.viewport)
                .label("cells")
                .value(this.cells)
                .label("columns")
                .value(this.columns)
                .label("labels")
                .value(this.labels)
                .label("rows")
                .value(this.rows)
                .label("references")
                .value(this.references)
                .label("deletedCells")
                .value(this.deletedCells)
                .label("deletedColumns")
                .value(this.deletedColumns)
                .label("deletedRows")
                .value(this.deletedRows)
                .label("deletedLabels")
                .value(this.deletedLabels)
                .label("matchedCells")
                .value(this.matchedCells);

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

        b.label("columnCount").value(this.columnCount);
        b.label("rowCount").value(this.rowCount);

        this.toStringWindow(b);

        return b.build();
    }

    abstract void toStringWindow(final ToStringBuilder b);
}

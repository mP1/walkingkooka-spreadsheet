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
import walkingkooka.spreadsheet.reference.SpreadsheetCellRange;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnOrRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonObject;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Captures changes following an operation. A window when non empty is applied to any given cells and label mappings returned as a filter.
 */
public abstract class SpreadsheetDelta implements TreePrintable {

    public final static Set<SpreadsheetCell> NO_CELLS = Sets.empty();
    public final static Set<SpreadsheetLabelMapping> NO_LABELS = Sets.empty();
    public final static Set<SpreadsheetCellReference> NO_DELETED_CELLS = Sets.empty();
    public final static Optional<SpreadsheetCellRange> NO_WINDOW = Optional.empty();
    public final static Map<SpreadsheetColumnReference, Double> NO_COLUMN_WIDTHS = Maps.empty();
    public final static Map<SpreadsheetRowReference, Double> NO_ROW_HEIGHTS = Maps.empty();

    /**
     * A {@link SpreadsheetDelta} with everything empty.
     */
    public final static SpreadsheetDelta EMPTY = SpreadsheetDeltaNonWindowed.withNonWindowed(
            NO_CELLS,
            NO_LABELS,
            NO_DELETED_CELLS,
            NO_COLUMN_WIDTHS,
            NO_ROW_HEIGHTS
    );

    /**
     * Package private to limit sub classing.
     */
    SpreadsheetDelta(final Set<SpreadsheetCell> cells,
                     final Set<SpreadsheetLabelMapping> labels,
                     final Set<SpreadsheetCellReference> deleteCells,
                     final Map<SpreadsheetColumnReference, Double> columnWidths,
                     final Map<SpreadsheetRowReference, Double> rowHeights) {
        super();

        this.cells = cells;
        this.labels = labels;
        this.deletedCells = deleteCells;
        this.columnWidths = columnWidths;
        this.rowHeights = rowHeights;
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
     * Takes a copy of the cells, possibly filtering out cells if a window is present. Note filtering of {@link #labels} will happen later.
     */
    abstract Set<SpreadsheetCell> filterCells(final Set<SpreadsheetCell> cells);

    final Set<SpreadsheetCell> cells;

    private static void checkCells(final Set<SpreadsheetCell> cells) {
        Objects.requireNonNull(cells, "cells");
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

    // columnWidths..................................................................................................

    /**
     * Returns a map of columns to max column width for each. The included columns should appear within one of the cells.
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
     * Returns a map of rows to max row height for each. The included rows should appear within one of the cells.
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
        final Set<SpreadsheetCell> cells = this.cells;
        final Set<SpreadsheetLabelMapping> labels = this.labels;
        final Set<SpreadsheetCellReference> deletedCells = this.deletedCells;
        final Map<SpreadsheetColumnReference, Double> columnWidths = this.columnWidths;
        final Map<SpreadsheetRowReference, Double> rowHeights = this.rowHeights;

        final SpreadsheetDelta delta;
        if (window.isPresent()) {
            final SpreadsheetCellRange filter = window.get();

            delta = SpreadsheetDeltaWindowed.withWindowed(
                    filterCells0(cells, filter),
                    filterLabels0(labels, filter),
                    filterDeletedCells0(deletedCells, filter),
                    filterColumnWidths0(columnWidths, filter),
                    filterRowHeights0(rowHeights, filter),
                    window);
        } else {
            delta = SpreadsheetDeltaNonWindowed.withNonWindowed(
                    cells,
                    labels,
                    deletedCells,
                    columnWidths,
                    rowHeights
            );
        }

        return delta;
    }

    static Set<SpreadsheetCell> filterCells(final Set<SpreadsheetCell> cells,
                                            final Optional<SpreadsheetCellRange> window) {
        return window.isPresent() ?
                filterCells0(cells, window.get()) :
                cells;
    }

    private static Set<SpreadsheetCell> filterCells0(final Set<SpreadsheetCell> cells,
                                                     final SpreadsheetCellRange window) {
        return Sets.immutable(
                cells.stream()
                        .filter(c -> window.test(c.reference()))
                        .collect(Collectors.toCollection(Sets::sorted))
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
        return Sets.immutable(
                labels.stream()
                        .filter(m -> {
                            final SpreadsheetExpressionReference r = m.reference();
                            return r.isCellReference() && window.test((SpreadsheetCellReference) m.reference());
                        })
                        .collect(Collectors.toCollection(Sets::ordered))
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
        return Sets.immutable(
                deletedCells.stream()
                        .filter(c -> window.test(c))
                        .map(SpreadsheetCellReference::toRelative)
                        .collect(Collectors.toCollection(Sets::sorted))
        );
    }

    static Map<SpreadsheetColumnReference, Double> filterColumnWidths(final Map<SpreadsheetColumnReference, Double> columnWidths,
                                                                      final Optional<SpreadsheetCellRange> window) {
        return window.isPresent() ?
                filterColumnWidths0(columnWidths, window.get()) :
                columnWidths;
    }

    private static Map<SpreadsheetColumnReference, Double> filterColumnWidths0(final Map<SpreadsheetColumnReference, Double> columnWidths,
                                                                               final SpreadsheetCellRange window) {
        final Map<SpreadsheetColumnReference, Double> filtered = Maps.ordered();

        for (final Map.Entry<SpreadsheetColumnReference, Double> columnAndWidth : columnWidths.entrySet()) {
            final SpreadsheetColumnReference column = columnAndWidth.getKey();
            if (window.columnReferenceRange().testColumn(column)) {
                filtered.put(column, columnAndWidth.getValue());
            }
        }

        return Maps.immutable(filtered);
    }

    static Map<SpreadsheetRowReference, Double> filterRowHeights(final Map<SpreadsheetRowReference, Double> rowHeights,
                                                                 final Optional<SpreadsheetCellRange> window) {
        return window.isPresent() ?
                filterRowHeights0(rowHeights, window.get()) :
                rowHeights;
    }

    private static Map<SpreadsheetRowReference, Double> filterRowHeights0(final Map<SpreadsheetRowReference, Double> rowHeights,
                                                                          final SpreadsheetCellRange window) {
        final Map<SpreadsheetRowReference, Double> filtered = Maps.ordered();

        for (final Map.Entry<SpreadsheetRowReference, Double> rowAndHeight : rowHeights.entrySet()) {
            final SpreadsheetRowReference row = rowAndHeight.getKey();
            if (window.rowReferenceRange().testRow(row)) {
                filtered.put(row, rowAndHeight.getValue());
            }
        }

        return Maps.immutable(filtered);
    }

    // TreePrintable.....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println("SpreadsheetDelta");
        printer.indent();
        {

            final Set<SpreadsheetCell> cells = this.cells();
            if (!cells.isEmpty()) {
                printer.println("cells:");
                printer.indent();
                {
                    for (final SpreadsheetCell cell : cells) {
                        cell.printTree(printer);
                    }
                }
                printer.outdent();
            }

            final Set<SpreadsheetLabelMapping> labels = this.labels();
            if (!labels.isEmpty()) {
                printer.println("labels:");
                printer.indent();
                {
                    for (final SpreadsheetLabelMapping mapping : labels) {
                        printer.println(mapping.label() + ": " + mapping.reference());
                    }
                }
                printer.outdent();
            }

            final Set<SpreadsheetCellReference> deletedCells = this.deletedCells();
            if (!deletedCells.isEmpty()) {
                printer.println("deletedCells:");
                printer.indent();
                {
                    for (final SpreadsheetCellReference deleted : deletedCells) {
                        printer.println(deleted.toString());
                    }
                }
                printer.outdent();
            }

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

    static SpreadsheetDelta unmarshall(final JsonNode node,
                                       final JsonNodeUnmarshallContext context) {
        Set<SpreadsheetCell> cells = Sets.empty();
        Set<SpreadsheetLabelMapping> labels = NO_LABELS;
        Set<SpreadsheetCellReference> deletedCells = Sets.empty();
        Map<SpreadsheetColumnReference, Double> columnWidths = NO_COLUMN_WIDTHS;
        Map<SpreadsheetRowReference, Double> maxRowsHeights = NO_ROW_HEIGHTS;
        Optional<SpreadsheetCellRange> window = NO_WINDOW;

        for (final JsonNode child : node.objectOrFail().children()) {
            final JsonPropertyName name = child.name();

            switch (name.value()) {
                case CELLS_PROPERTY_STRING:
                    cells = unmarshallCells(child, context);
                    break;
                case LABELS_PROPERTY_STRING:
                    labels = unmarshallLabels(child, context);
                    break;
                case DELETED_CELLS_PROPERTY_STRING:
                    deletedCells = context.unmarshallSet(child, SpreadsheetCellReference.class);
                    break;
                case COLUMN_WIDTHS_PROPERTY_STRING:
                    columnWidths = unmarshallMap(child, SpreadsheetColumnReference::parseColumn);
                    break;
                case MAX_ROW_HEIGHTS_PROPERTY_STRING:
                    maxRowsHeights = unmarshallMap(child, SpreadsheetRowReference::parseRow);
                    break;
                case WINDOW_PROPERTY_STRING:
                    window = Optional.of(SpreadsheetCellRange.parseCellRange(child.stringOrFail()));
                    break;
                default:
                    JsonNodeUnmarshallContext.unknownPropertyPresent(name, node);
            }
        }

        return EMPTY.setCells(cells)
                .setWindow(window)
                .setLabels(labels)
                .setDeletedCells(deletedCells)
                .setColumnWidths(columnWidths)
                .setRowHeights(maxRowsHeights);
    }

    private static Set<SpreadsheetCell> unmarshallCells(final JsonNode node,
                                                        final JsonNodeUnmarshallContext context) {

        final Set<SpreadsheetCell> cells = Sets.ordered();

        for (final Map.Entry<JsonPropertyName, JsonNode> child : node.objectOrFail().asMap().entrySet()) {
            cells.add(context.unmarshall(JsonNode.object()
                            .set(child.getKey(), child.getValue()),
                    SpreadsheetCell.class));
        }

        return cells;
    }

    private static Set<SpreadsheetLabelMapping> unmarshallLabels(final JsonNode node,
                                                                 final JsonNodeUnmarshallContext context) {
        return node.arrayOrFail()
                .children()
                .stream()
                .map(n -> context.unmarshall(n, SpreadsheetLabelMapping.class))
                .collect(Collectors.toCollection(Sets::ordered));
    }

    private static <R extends SpreadsheetColumnOrRowReference> Map<R, Double> unmarshallMap(final JsonNode object,
                                                                                            final Function<String, R> reference) {
        final Map<R, Double> max = Maps.ordered();

        for (final JsonNode entry : object.children()) {
            max.put(reference.apply(entry.name().value()), entry.numberOrFail().doubleValue());
        }

        return max;
    }
    /**
     * <pre>
     * {
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

        final Set<SpreadsheetCell> cells = this.cells;
        if (!cells.isEmpty()) {
            children.add(marshallCells(cells, context).setName(CELLS_PROPERTY));
        }

        final Set<SpreadsheetLabelMapping> labels = this.labels;
        if (!labels.isEmpty()) {
            children.add(marshallLabels(labels, context).setName(LABELS_PROPERTY));
        }

        final Set<SpreadsheetCellReference> deletedCells = this.deletedCells;
        if (!deletedCells.isEmpty()) {
            children.add(context.marshallSet(deletedCells)
                    .setName(DELETED_CELLS_PROPERTY));
        }

        final Map<SpreadsheetColumnReference, Double> columnWidths = this.columnWidths;
        if (!columnWidths.isEmpty()) {
            children.add(marshallMap(columnWidths,
                    (r) -> r.setReferenceKind(SpreadsheetReferenceKind.RELATIVE)).setName(COLUMN_WIDTHS_PROPERTY));
        }

        final Map<SpreadsheetRowReference, Double> maxRowsHeights = this.rowHeights;
        if (!maxRowsHeights.isEmpty()) {
            children.add(marshallMap(maxRowsHeights,
                    (r) -> r.setReferenceKind(SpreadsheetReferenceKind.RELATIVE)).setName(MAX_ROW_HEIGHTS_PROPERTY));
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
    private static JsonNode marshallCells(final Set<SpreadsheetCell> cells,
                                          final JsonNodeMarshallContext context) {
        final List<JsonNode> children = Lists.array();

        for (final SpreadsheetCell cell : cells) {
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

    /**
     * Creates a JSON object where the reference in string form is the key and the max width is the value.
     */
    private static <R extends SpreadsheetColumnOrRowReference> JsonNode marshallMap(final Map<R, Double> referenceToWidth,
                                                                                    final Function<R, R> withoutAbsolute) {
        final List<JsonNode> children = Lists.array();

        for (final Map.Entry<R, Double> referenceAndWidth : referenceToWidth.entrySet()) {
            children.add(JsonNode.number(referenceAndWidth.getValue())
                    .setName(JsonPropertyName.with(withoutAbsolute.apply(referenceAndWidth.getKey()).toString())));
        }

        return JsonNode.object()
                .setChildren(children);
    }

    /**
     * Constant used to separate individual ranges in the window list.
     */
    private final static String WINDOW_SEPARATOR = ",";

    private final static String CELLS_PROPERTY_STRING = "cells";
    private final static String LABELS_PROPERTY_STRING = "labels";
    private final static String DELETED_CELLS_PROPERTY_STRING = "deletedCells";
    private final static String COLUMN_WIDTHS_PROPERTY_STRING = "columnWidths";
    private final static String MAX_ROW_HEIGHTS_PROPERTY_STRING = "rowHeights";
    private final static String WINDOW_PROPERTY_STRING = "window";

    // @VisibleForTesting
    final static JsonPropertyName CELLS_PROPERTY = JsonPropertyName.with(CELLS_PROPERTY_STRING);
    // @VisibleForTesting
    final static JsonPropertyName LABELS_PROPERTY = JsonPropertyName.with(LABELS_PROPERTY_STRING);
    // @VisibleForTesting
    final static JsonPropertyName DELETED_CELLS_PROPERTY = JsonPropertyName.with(DELETED_CELLS_PROPERTY_STRING);
    // @VisibleForTesting
    final static JsonPropertyName COLUMN_WIDTHS_PROPERTY = JsonPropertyName.with(COLUMN_WIDTHS_PROPERTY_STRING);
    // @VisibleForTesting
    final static JsonPropertyName MAX_ROW_HEIGHTS_PROPERTY = JsonPropertyName.with(MAX_ROW_HEIGHTS_PROPERTY_STRING);
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
                this.cells,
                this.labels,
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
        return this.cells.equals(other.cells) &&
                this.labels.equals(other.labels) &&
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
                .label("cells")
                .value(this.cells)
                .label("labels")
                .value(this.labels)
                .label("deletedCells")
                .value(this.deletedCells);

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

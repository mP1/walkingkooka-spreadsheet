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
import walkingkooka.predicate.Predicates;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnOrRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRange;
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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Captures changes following an operation. A window when non empty is applied to any given cells as a filter.
 */
public abstract class SpreadsheetDelta implements TreePrintable {

    public final static Set<SpreadsheetCell> NO_CELLS = Sets.empty();
    public final static Map<SpreadsheetCellReference, Set<SpreadsheetLabelName>> NO_CELL_TO_LABELS = Maps.empty();
    public final static List<SpreadsheetRange> NO_WINDOW = Lists.empty();
    public final static Map<SpreadsheetColumnReference, Double> NO_MAX_COLUMN_WIDTHS = Maps.empty();
    public final static Map<SpreadsheetRowReference, Double> NO_MAX_ROW_HEIGHTS = Maps.empty();

    /**
     * Factory that creates a new {@link SpreadsheetDelta} with an id.
     */
    public static SpreadsheetDelta with(final Set<SpreadsheetCell> cells) {
        checkCells(cells);

        return SpreadsheetDeltaNonWindowed.withNonWindowed(Sets.immutable(cells),
                NO_CELL_TO_LABELS,
                NO_MAX_COLUMN_WIDTHS,
                NO_MAX_ROW_HEIGHTS);
    }

    /**
     * Package private to limit sub classing.
     */
    SpreadsheetDelta(final Set<SpreadsheetCell> cells,
                     final Map<SpreadsheetCellReference, Set<SpreadsheetLabelName>> cellToLabels,
                     final Map<SpreadsheetColumnReference, Double> maxColumnWidths,
                     final Map<SpreadsheetRowReference, Double> maxRowHeights) {
        super();

        this.cells = cells;
        this.cellToLabels = cellToLabels;
        this.maxColumnWidths = maxColumnWidths;
        this.maxRowHeights = maxRowHeights;
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
     * Takes a copy of the cells, possibly filtering out cells if a window is present. Note filtering of {@link #cellToLabels} will happen later.
     */
    abstract Set<SpreadsheetCell> filterCells(final Set<SpreadsheetCell> cells);

    final Set<SpreadsheetCell> cells;

    private static void checkCells(final Set<SpreadsheetCell> cells) {
        Objects.requireNonNull(cells, "cells");
    }

    // cellToLabels............................................................................................................

    public final Map<SpreadsheetCellReference, Set<SpreadsheetLabelName>> cellToLabels() {
        return this.cellToLabels;
    }

    /**
     * Would be setter that returns a {@link SpreadsheetDelta} holding the given cellToLabels after they are possibly filtered
     * using the {@link #window()}
     */
    public final SpreadsheetDelta setCellToLabels(final Map<SpreadsheetCellReference, Set<SpreadsheetLabelName>> cellToLabels) {
        checkCellToLabels(cellToLabels);

        final Map<SpreadsheetCellReference, Set<SpreadsheetLabelName>> copy = filterCellToLabels(cellToLabels, this.window());
        return this.cellToLabels.equals(copy) ?
                this :
                this.replaceCellToLabels(copy);
    }

    /**
     * Sub classes only need to call the right constructor, the map is already immutable and has been filtered by {#link #cells}
     */
    abstract SpreadsheetDelta replaceCellToLabels(final Map<SpreadsheetCellReference, Set<SpreadsheetLabelName>> cellToLabels);

    final Map<SpreadsheetCellReference, Set<SpreadsheetLabelName>> cellToLabels;

    private static void checkCellToLabels(final Map<SpreadsheetCellReference, Set<SpreadsheetLabelName>> cellToLabels) {
        Objects.requireNonNull(cellToLabels, "cellToLabels");
    }

    // maxColumnWidths..................................................................................................

    /**
     * Returns a map of columns to max column width for each. The included columns should appear within one of the cells.
     */
    public final Map<SpreadsheetColumnReference, Double> maxColumnWidths() {
        return this.maxColumnWidths;
    }

    public final SpreadsheetDelta setMaxColumnWidths(final Map<SpreadsheetColumnReference, Double> maxColumnWidths) {
        Objects.requireNonNull(maxColumnWidths, "maxColumnWidths");

        final Map<SpreadsheetColumnReference, Double> copy = Maps.immutable(maxColumnWidths);
        return this.maxColumnWidths.equals(copy) ?
                this :
                this.replaceMaxColumnWidths(copy);

    }

    abstract SpreadsheetDelta replaceMaxColumnWidths(final Map<SpreadsheetColumnReference, Double> maxColumnWidths);

    final Map<SpreadsheetColumnReference, Double> maxColumnWidths;

    // maxRowHeights..................................................................................................

    /**
     * Returns a map of rows to max row height for each. The included rows should appear within one of the cells.
     */
    public final Map<SpreadsheetRowReference, Double> maxRowHeights() {
        return this.maxRowHeights;
    }

    public final SpreadsheetDelta setMaxRowHeights(final Map<SpreadsheetRowReference, Double> maxRowHeights) {
        Objects.requireNonNull(maxRowHeights, "maxRowHeights");

        final Map<SpreadsheetRowReference, Double> copy = Maps.immutable(maxRowHeights);
        return this.maxRowHeights.equals(copy) ?
                this :
                this.replaceMaxRowHeights(copy);

    }

    abstract SpreadsheetDelta replaceMaxRowHeights(final Map<SpreadsheetRowReference, Double> maxRowHeights);

    final Map<SpreadsheetRowReference, Double> maxRowHeights;

    // window............................................................................................................

    /**
     * Getter that returns any windows for this delta. An empty list signifies, no filtering.
     */
    public abstract List<SpreadsheetRange> window();

    /**
     * Would be setter that if necessary returns a new {@link SpreadsheetDelta} which will also filter cells if necessary,
     * only if all {@link SpreadsheetRange} are all {@link SpreadsheetRange ranges}. Filtering is not possible if a
     * {@link SpreadsheetRange} is present because it is not possible to determine if a cell is within those
     * boundaries.
     */
    public final SpreadsheetDelta setWindow(final List<SpreadsheetRange> window) {
        Objects.requireNonNull(window, "window");

        final List<SpreadsheetRange> copy = Lists.immutable(window);
        return this.window().equals(copy) ?
                this :
                this.setWindow0(copy);
    }

    private SpreadsheetDelta setWindow0(final List<SpreadsheetRange> window) {
        final Set<SpreadsheetCell> cells = this.cells;
        final Map<SpreadsheetCellReference, Set<SpreadsheetLabelName>> cellToLabels = this.cellToLabels;
        final Map<SpreadsheetColumnReference, Double> maxColumnWidths = this.maxColumnWidths;
        final Map<SpreadsheetRowReference, Double> maxRowHeights = this.maxRowHeights;

        final Set<SpreadsheetCell> filteredCells = filterCells0(cells, window);
        final Map<SpreadsheetCellReference, Set<SpreadsheetLabelName>> filteredCellToLabels = filterCellToLabels(cellToLabels, this.window());

        return window.isEmpty() ?
                SpreadsheetDeltaNonWindowed.withNonWindowed(filteredCells, filteredCellToLabels, maxColumnWidths, maxRowHeights) :
                SpreadsheetDeltaWindowed.withWindowed(filteredCells, filteredCellToLabels, maxColumnWidths, maxRowHeights, window);
    }
    
    static Set<SpreadsheetCell> filterCells0(final Set<SpreadsheetCell> cells,
                                             final List<SpreadsheetRange> window) {
        return window.isEmpty() ?
                Sets.immutable(cells) :
                Sets.readOnly(filterCells1(cells, Cast.to(window)));
    }

    private static Set<SpreadsheetCell> filterCells1(final Set<SpreadsheetCell> cells,
                                                     final List<SpreadsheetRange> ranges) {
        return cells.stream()
                .filter(c -> {
                    return ranges.stream()
                            .anyMatch(r -> r.test(c.reference()));
                })
                .collect(Collectors.toCollection(Sets::sorted));
    }

    /**
     * Returns a {@link Map} removing any references that are not within {@link Set}.
     */
    static Map<SpreadsheetCellReference, Set<SpreadsheetLabelName>> filterCellToLabels(final Map<SpreadsheetCellReference, Set<SpreadsheetLabelName>> cellToLabels,
                                                                                       final List<SpreadsheetRange> window) {
        return cellToLabels.isEmpty() ?
                Maps.empty() :
                filterCellToLabels0(
                        cellToLabels,
                        window
                );
    }

    /**
     * Copies and filters the cells using the filter to decide which cell to label mappings are kept.
     * As a bonus, cells with no labels are no kept.
     */
    private static Map<SpreadsheetCellReference, Set<SpreadsheetLabelName>> filterCellToLabels0(final Map<SpreadsheetCellReference, Set<SpreadsheetLabelName>> cellToLabels,
                                                                                                final List<SpreadsheetRange> window) {

        final Predicate<SpreadsheetCellReference> filter = window.isEmpty() ?
                Predicates.always() :
                windowRangesPredicate(Cast.to(window));

        final Map<SpreadsheetCellReference, Set<SpreadsheetLabelName>> filtered = Maps.sorted();

        for (final Map.Entry<SpreadsheetCellReference, Set<SpreadsheetLabelName>> cellAndLabels : cellToLabels.entrySet()) {
            final Set<SpreadsheetLabelName> labels = Sets.immutable(cellAndLabels.getValue());
            if (!labels.isEmpty()) {
                final SpreadsheetCellReference reference = cellAndLabels.getKey();
                if (filter.test(reference)) {
                    filtered.put(reference, labels);
                }
            }
        }
        return Maps.immutable(filtered);
    }

    private static Predicate<SpreadsheetCellReference> windowRangesPredicate(final List<SpreadsheetRange> window) {
        return (r) -> window.stream()
                .anyMatch(rr -> rr.test(r));
    }

    // TreePrintable.....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println("SpreadsheetDelta");
        printer.indent();
        {

            printer.println("cells:");
            printer.indent();
            {
                for (final SpreadsheetCell cell : this.cells()) {
                    cell.printTree(printer);
                }
            }
            printer.outdent();

            final Map<SpreadsheetCellReference, Set<SpreadsheetLabelName>> cellToLabels = this.cellToLabels();
            if (!cellToLabels.isEmpty()) {
                printer.println("cellToLabels:");
                printer.indent();
                {
                    for (final Map.Entry<SpreadsheetCellReference, Set<SpreadsheetLabelName>> cellAndLabels : cellToLabels.entrySet()) {
                        printer.println(cellAndLabels.getKey() + ": " + cellAndLabels.getValue().stream().map(SpreadsheetLabelName::toString).collect(Collectors.joining(", ")));
                    }
                }
                printer.outdent();
            }

            this.printTreeMap(
                    "columnWidths",
                    this.maxColumnWidths(),
                    printer
            );

            this.printTreeMap(
                    "rowHeights",
                    this.maxRowHeights(),
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
        Map<SpreadsheetCellReference, Set<SpreadsheetLabelName>> cellToLabels = NO_CELL_TO_LABELS;
        Map<SpreadsheetColumnReference, Double> maxColumnWidths = NO_MAX_COLUMN_WIDTHS;
        Map<SpreadsheetRowReference, Double> maxRowsHeights = NO_MAX_ROW_HEIGHTS;
        List<SpreadsheetRange> window = NO_WINDOW;

        for (final JsonNode child : node.objectOrFail().children()) {
            final JsonPropertyName name = child.name();

            switch (name.value()) {
                case CELLS_PROPERTY_STRING:
                    cells = unmarshallCells(child, context);
                    break;
                case CELL_TO_LABELS_PROPERTY_STRING:
                    cellToLabels = unmarshallCellToLabels(child, context);
                    break;
                case MAX_COLUMN_WIDTHS_PROPERTY_STRING:
                    maxColumnWidths = unmarshallMap(child, SpreadsheetColumnReference::parseColumn);
                    break;
                case MAX_ROW_HEIGHTS_PROPERTY_STRING:
                    maxRowsHeights = unmarshallMap(child, SpreadsheetRowReference::parseRow);
                    break;
                case WINDOW_PROPERTY_STRING:
                    window = rangeJsonNodeUnmarshall(child.stringOrFail());
                    break;
                default:
                    JsonNodeUnmarshallContext.unknownPropertyPresent(name, node);
            }
        }

        return with(cells)
                .setWindow(window)
                .setCellToLabels(cellToLabels)
                .setMaxColumnWidths(maxColumnWidths)
                .setMaxRowHeights(maxRowsHeights);
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

    private static Map<SpreadsheetCellReference, Set<SpreadsheetLabelName>> unmarshallCellToLabels(final JsonNode node,
                                                        final JsonNodeUnmarshallContext context) {

        final Map<SpreadsheetCellReference, Set<SpreadsheetLabelName>> cells = Maps.sorted();

        for (final Map.Entry<JsonPropertyName, JsonNode> child : node.objectOrFail().asMap().entrySet()) {
            final JsonPropertyName cell = child.getKey();
            final String labels = child.getValue().stringOrFail();

            cells.put(
                    SpreadsheetCellReference.parseCellReference(cell.value()),
                    Arrays.stream(labels.split(",")).map(SpreadsheetLabelName::labelName).collect(Collectors.toSet())
            );
        }

        return cells;
    }

    private static <R extends SpreadsheetColumnOrRowReference> Map<R, Double> unmarshallMap(final JsonNode object,
                                                                                            final Function<String, R> reference) {
        final Map<R, Double> max = Maps.ordered();

        for (final JsonNode entry : object.children()) {
            max.put(reference.apply(entry.name().value()), entry.numberOrFail().doubleValue());
        }

        return max;
    }

    private static List<SpreadsheetRange> rangeJsonNodeUnmarshall(final String range) {
        return Arrays.stream(range.split(WINDOW_SEPARATOR))
                .map(SpreadsheetRange::parseRange)
                .collect(Collectors.toList());
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

        final Map<SpreadsheetCellReference, Set<SpreadsheetLabelName>> cellToLabels = this.cellToLabels;
        if (!cellToLabels.isEmpty()) {
            children.add(marshallCellToLabels(cellToLabels, context).setName(CELL_TO_LABELS_PROPERTY));
        }

        final Map<SpreadsheetColumnReference, Double> maxColumnWidths = this.maxColumnWidths;
        if (!maxColumnWidths.isEmpty()) {
            children.add(marshallMap(maxColumnWidths,
                    (r) -> r.setReferenceKind(SpreadsheetReferenceKind.RELATIVE)).setName(MAX_COLUMN_WIDTHS_PROPERTY));
        }

        final Map<SpreadsheetRowReference, Double> maxRowsHeights = this.maxRowHeights;
        if (!maxRowsHeights.isEmpty()) {
            children.add(marshallMap(maxRowsHeights,
                    (r) -> r.setReferenceKind(SpreadsheetReferenceKind.RELATIVE)).setName(MAX_ROW_HEIGHTS_PROPERTY));
        }

        final List<SpreadsheetRange> window = this.window();
        if (!window.isEmpty()) {
            children.add(JsonNode.string(window.stream()
                    .map(SpreadsheetRange::toString)
                    .collect(Collectors.joining(WINDOW_SEPARATOR)))
                    .setName(WINDOW_PROPERTY));
        }

        return JsonNode.object().setChildren(children);
    }

    /**
     * Creates a JSON object with each cell one of the properties.
     */
    private static JsonNode marshallCells(final Set<SpreadsheetCell> cells,
                                          final JsonNodeMarshallContext context) {
        final List<JsonNode> children = Lists.array();

        for(final SpreadsheetCell cell : cells) {
            final JsonObject json = context.marshall(cell)
                    .objectOrFail();
            children.add(json.children().get(0));
        }

        return JsonNode.object()
                .setChildren(children);
    }

    /**
     * Creates a JSON object with each cell reference is a property and the labels are combined into a single CSV.
     */
    private static JsonNode marshallCellToLabels(final Map<SpreadsheetCellReference, Set<SpreadsheetLabelName>> cellToLabels,
                                                 final JsonNodeMarshallContext context) {
        final List<JsonNode> children = Lists.array();

        for (final Map.Entry<SpreadsheetCellReference, Set<SpreadsheetLabelName>> cellAndLabel : cellToLabels.entrySet()) {
            final SpreadsheetCellReference cell = cellAndLabel.getKey();
            final Set<SpreadsheetLabelName> labels = cellAndLabel.getValue();
            final String labelsCsv = labels.stream()
                    .map(SpreadsheetLabelName::toString)
                    .collect(Collectors.joining(","));

            children.add(JsonNode.string(labelsCsv).setName(JsonPropertyName.with(cell.toString())));
        }

        return JsonNode.object()
                .setChildren(children);
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
    private final static String CELL_TO_LABELS_PROPERTY_STRING = "labels";
    private final static String MAX_COLUMN_WIDTHS_PROPERTY_STRING = "maxColumnWidths";
    private final static String MAX_ROW_HEIGHTS_PROPERTY_STRING = "maxRowHeights";
    private final static String WINDOW_PROPERTY_STRING = "window";

    // @VisibleForTesting
    final static JsonPropertyName CELLS_PROPERTY = JsonPropertyName.with(CELLS_PROPERTY_STRING);
    // @VisibleForTesting
    final static JsonPropertyName CELL_TO_LABELS_PROPERTY = JsonPropertyName.with(CELL_TO_LABELS_PROPERTY_STRING);
    // @VisibleForTesting
    final static JsonPropertyName MAX_COLUMN_WIDTHS_PROPERTY = JsonPropertyName.with(MAX_COLUMN_WIDTHS_PROPERTY_STRING);
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
        return Objects.hash(this.cells,
                this.hashWindow(),
                this.maxColumnWidths,
                this.maxRowHeights);
    }

    abstract int hashWindow();

    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public final boolean equals(final Object other) {
        return this == other ||
                this.canBeEquals(other) &&
                        this.equals0(Cast.to(other));
    }

    abstract boolean canBeEquals(final Object other);

    private boolean equals0(final SpreadsheetDelta other) {
        return this.cells.equals(other.cells) &&
                this.cellToLabels.equals(other.cellToLabels) &&
                this.maxColumnWidths.equals(other.maxColumnWidths) &&
                this.maxRowHeights.equals(other.maxRowHeights) &&
                this.equals1(other);
    }

    abstract boolean equals1(final SpreadsheetDelta other);

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
                .label("cellToLabels")
                .value(this.cellToLabels);

        final Map<SpreadsheetColumnReference, Double> maxColumnWidths = this.maxColumnWidths;
        final Map<SpreadsheetRowReference, Double> maxRowHeights = this.maxRowHeights;

        if (maxColumnWidths.size() + maxRowHeights.size() > 0) {
            b.append(" max: ");

            b.labelSeparator("=");
            b.value(maxColumnWidths);

            if(maxColumnWidths.size() + maxRowHeights.size() > 1) {
                b.append(", ");
            }

            b.value(maxRowHeights);
        }

        this.toStringWindow(b);

        return b.build();
    }

    abstract void toStringWindow(final ToStringBuilder b);
}

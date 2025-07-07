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

package walkingkooka.spreadsheet;

import walkingkooka.CanBeEmpty;
import walkingkooka.collect.set.Sets;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.CharacterConstant;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Captures one or more windows that define the cells within a viewport.
 */
public final class SpreadsheetViewportWindows implements CanBeEmpty,
    Predicate<SpreadsheetSelection>,
    TreePrintable {

    /**
     * A window query parameter and other string representations are {@link SpreadsheetCellReference} separated by a
     * comma.
     */
    public final static CharacterConstant SEPARATOR = CharacterConstant.with(',');

    /**
     * An empty {@link SpreadsheetViewportWindows}.
     */
    public final static SpreadsheetViewportWindows EMPTY = new SpreadsheetViewportWindows(Sets.empty());

    /**
     * Parses a window query parameter or other string representation into a {@link Set} or {@link SpreadsheetCellRangeReference}.
     * eg
     * <pre>
     * A1
     * B2,C3
     * D4:E5,F6,G7:HI
     * </pre>
     */
    public static SpreadsheetViewportWindows parse(final String windows) {
        Objects.requireNonNull(windows, "windows");

        return windows.isEmpty() ?
            EMPTY :
            new SpreadsheetViewportWindows(
                copy(
                    SEPARATOR.parse(
                        windows,
                        SpreadsheetSelection::parseCellRange
                    )
                )
            );
    }

    public static SpreadsheetViewportWindows with(final Set<SpreadsheetCellRangeReference> cellRanges) {
        Objects.requireNonNull(cellRanges, "cellRanges");

        final Set<SpreadsheetCellRangeReference> copy = copy(cellRanges);
        return copy.isEmpty() ?
            EMPTY :
            new SpreadsheetViewportWindows(copy);
    }

    /**
     * While taking a copy of the given {@link Collection} test if there are any overlapping {@link SpreadsheetCellRangeReference}.
     */
    private static Set<SpreadsheetCellRangeReference> copy(final Collection<SpreadsheetCellRangeReference> cellRanges) {
        return overlapCheckAndCreate(
            cellRanges.toArray(
                new SpreadsheetCellRangeReference[
                    cellRanges.size()
                    ]
            )
        );
    }

    /**
     * Assumes the ranges have been copied, and checks that there are no overlaps.
     */
    private static Set<SpreadsheetCellRangeReference> overlapCheckAndCreate(final SpreadsheetCellRangeReference[] cellRanges) {
        Arrays.sort(cellRanges);
        final int count = cellRanges.length;

        for (int i = 0; i < count; i++) {
            final SpreadsheetCellRangeReference first = cellRanges[i];

            for (int j = i + 1; j < count; j++) {
                final SpreadsheetCellRangeReference other = cellRanges[j];
                if (first.testCellRange(other)) {
                    throw new IllegalArgumentException("Window component cell-ranges overlap " + first + " and " + other);
                }
            }
        }

        return Sets.of(cellRanges);
    }

    private SpreadsheetViewportWindows(final Set<SpreadsheetCellRangeReference> cellRanges) {
        super();

        this.cellRanges = cellRanges;
    }

    public Set<SpreadsheetCellRangeReference> cellRanges() {
        return this.cellRanges;
    }

    private final Set<SpreadsheetCellRangeReference> cellRanges;

    /**
     * Returns true if there are no {@link SpreadsheetCellRangeReference ranges}
     */
    @Override
    public boolean isEmpty() {
        return this.cellRanges.isEmpty();
    }

    /**
     * Returns the last window, and because the {@link #cellRanges} is sorted this will be the bottom right window,
     * which is also often the window that maybe scrolled by the user in the UI.
     */
    public Optional<SpreadsheetCellRangeReference> last() {
        if (null == this.last) {
            SpreadsheetCellRangeReference last = null;

            for (final SpreadsheetCellRangeReference possible : this.cellRanges) {
                last = possible;
            }

            this.last = Optional.ofNullable(last);
        }

        return this.last;
    }

    private Optional<SpreadsheetCellRangeReference> last;

    /**
     * Returns the home cell if one is present.
     */
    public Optional<SpreadsheetCellReference> home() {
        return this.last()
            .map(SpreadsheetCellRangeReference::begin);
    }

    // bounds...........................................................................................................

    /**
     * Returns a {@link SpreadsheetCellRangeReference} or bounds that includes all the {@link SpreadsheetCellRangeReference ranges}.
     */
    public Optional<SpreadsheetCellRangeReference> bounds() {
        if (null == this.bounds) {
            this.bounds = this.isEmpty() ?
                Optional.empty() :
                this.boundsNotEmpty();
        }

        return this.bounds;
    }

    private Optional<SpreadsheetCellRangeReference> bounds;

    private Optional<SpreadsheetCellRangeReference> boundsNotEmpty() {
        SpreadsheetColumnReference left = SpreadsheetReferenceKind.RELATIVE.lastColumn();
        SpreadsheetRowReference top = SpreadsheetReferenceKind.RELATIVE.lastRow();

        SpreadsheetColumnReference right = SpreadsheetReferenceKind.RELATIVE.firstColumn();
        SpreadsheetRowReference bottom = SpreadsheetReferenceKind.RELATIVE.firstRow();

        for (final SpreadsheetCellRangeReference range : this.cellRanges()) {
            final SpreadsheetCellReference begin = range.begin();
            left = left.min(begin.column());
            top = top.min(begin.row());

            final SpreadsheetCellReference end = range.end();
            right = right.max(end.column());
            bottom = bottom.max(end.row());
        }

        return Optional.of(left.setRow(top)
            .cellRange(
                right.setRow(bottom)
            )
        );
    }

    // cells..........................................................................................................

    /**
     * Returns all the cells in this window.
     */
    public Set<SpreadsheetCellReference> cells() {
        if (null == this.cells) {
            this.cells = this.extract(Function.identity());
        }

        return this.cells;
    }

    private Set<SpreadsheetCellReference> cells;

    // columns..........................................................................................................

    /**
     * Returns all the columns in this window.
     */
    public Set<SpreadsheetColumnReference> columns() {
        if (null == this.columns) {
            this.columns = this.extract(SpreadsheetCellReference::column);
        }

        return this.columns;
    }

    private Set<SpreadsheetColumnReference> columns;

    // rows..........................................................................................................

    /**
     * Returns all the rows in this window.
     */
    public Set<SpreadsheetRowReference> rows() {
        if (null == this.rows) {
            this.rows = this.extract(SpreadsheetCellReference::row);
        }

        return this.rows;
    }

    private Set<SpreadsheetRowReference> rows;

    private <T extends SpreadsheetSelection> Set<T> extract(final Function<SpreadsheetCellReference, T> mapper) {
        final Set<T> selections = SortedSets.tree();

        for (final SpreadsheetCellRangeReference range : this.cellRanges()) {
            for (final SpreadsheetCellReference cell : range) {
                selections.add(
                    mapper.apply(cell)
                );
            }
        }

        return Sets.readOnly(selections);
    }

    // Predicate........................................................................................................

    /**
     * Return true if there are no cell-ranges OR the cell is matched by any cell-range.
     * A cell-range that overlaps the window with some cells inside and some outside will return true.
     */
    @Override
    public boolean test(final SpreadsheetSelection selection) {
        return null != selection &&
            this.testNonNull(selection);
    }

    private boolean testNonNull(final SpreadsheetSelection selection) {
        final Set<SpreadsheetCellRangeReference> cellRanges = this.cellRanges;
        return cellRanges.isEmpty() ||
            cellRanges.stream()
                .anyMatch(r -> r.test(selection));

    }

    // containsAll......................................................................................................

    /**
     * Returns true only if this window contains the entire given {@link SpreadsheetCellRangeReference}.
     */
    public boolean containsAll(final SpreadsheetCellRangeReference cells) {
        Objects.requireNonNull(cells, "cells");

        final Set<SpreadsheetCellRangeReference> cellRanges = this.cellRanges;
        return cellRanges.isEmpty() ||
            cellRanges.stream()
                .anyMatch(r -> r.containsAll(cells));
    }

    // count............................................................................................................

    /**
     * Returns the number of cells within this window. {@link #EMPTY} will return all cells.
     */
    public long count() {
        return this.isEmpty() ?
            SpreadsheetSelection.ALL_CELLS.count() :
            this.cellRanges().stream()
                .mapToLong(SpreadsheetSelection::count)
                .sum();
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.toString());
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.cellRanges.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other || other instanceof SpreadsheetViewportWindows && this.equals0((SpreadsheetViewportWindows) other);
    }

    private boolean equals0(final SpreadsheetViewportWindows other) {
        return this.cellRanges.equals(other.cellRanges);
    }

    @Override
    public String toString() {
        return SEPARATOR.toSeparatedString(
            this.cellRanges,
            SpreadsheetCellRangeReference::toStringMaybeStar
        );
    }

    // Json.............................................................................................................

    static SpreadsheetViewportWindows unmarshall(final JsonNode node,
                                                 final JsonNodeUnmarshallContext context) {
        return parse(node.stringOrFail());
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.string(this.toString());
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetViewportWindows.class),
            SpreadsheetViewportWindows::unmarshall,
            SpreadsheetViewportWindows::marshall,
            SpreadsheetViewportWindows.class
        );
    }
}

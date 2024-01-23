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

import walkingkooka.collect.iterable.Iterables;
import walkingkooka.collect.iterator.Iterators;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRange;
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
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Captures one or more windows that define the cells within a viewport.
 */
public final class SpreadsheetViewportWindows implements Iterable<SpreadsheetCellReference>,
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
     * Parses a window query parameter or other string representation into a {@link Set} or {@link SpreadsheetCellRange}.
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

    public static SpreadsheetViewportWindows with(final Set<SpreadsheetCellRange> cellRanges) {
        Objects.requireNonNull(cellRanges, "cellRanges");

        final Set<SpreadsheetCellRange> copy = copy(cellRanges);
        return copy.isEmpty() ?
                EMPTY :
                new SpreadsheetViewportWindows(copy);
    }

    /**
     * While taking a copy of the given {@link Collection} test if there are any overlapping {@link SpreadsheetCellRange}.
     */
    private static Set<SpreadsheetCellRange> copy(final Collection<SpreadsheetCellRange> cellRanges) {
        return overlapCheckAndCreate(
                cellRanges.toArray(
                        new SpreadsheetCellRange[
                                cellRanges.size()
                                ]
                )
        );
    }

    /**
     * Assumes the ranges have been copied, and checks that there are no overlaps.
     */
    private static Set<SpreadsheetCellRange> overlapCheckAndCreate(final SpreadsheetCellRange[] cellRanges) {
        Arrays.sort(cellRanges);
        final int count = cellRanges.length;

        for (int i = 0; i < count; i++) {
            final SpreadsheetCellRange first = cellRanges[i];

            for (int j = i + 1; j < count; j++) {
                final SpreadsheetCellRange other = cellRanges[j];
                if (first.testCellRange(other)) {
                    throw new IllegalArgumentException("Window component cell-ranges overlap " + first + " and " + other);
                }
            }
        }

        return Sets.of(cellRanges);
    }

    private SpreadsheetViewportWindows(final Set<SpreadsheetCellRange> cellRanges) {
        super();

        this.cellRanges = cellRanges;
    }

    public Set<SpreadsheetCellRange> cellRanges() {
        return this.cellRanges;
    }

    private final Set<SpreadsheetCellRange> cellRanges;

    /**
     * Returns true if there are no {@link SpreadsheetCellRange ranges}
     */
    public boolean isEmpty() {
        return this.cellRanges.isEmpty();
    }

    /**
     * Returns the last window, and because the {@link #cellRanges} is sorted this will be the bottom right window,
     * which is also often the window that maybe scrolled by the user in the UI.
     */
    public Optional<SpreadsheetCellRange> last() {
        if (null == this.last) {
            SpreadsheetCellRange last = null;

            for (final SpreadsheetCellRange possible : this.cellRanges) {
                last = possible;
            }

            this.last = Optional.ofNullable(last);
        }

        return this.last;
    }

    private Optional<SpreadsheetCellRange> last;

    /**
     * Returns the home cell if one is present.
     */
    public Optional<SpreadsheetCellReference> home() {
        return this.last()
                .map(SpreadsheetCellRange::begin);
    }

    // bounds...........................................................................................................

    /**
     * Returns a {@link SpreadsheetCellRange} or bounds that includes all the {@link SpreadsheetCellRange ranges}.
     */
    public Optional<SpreadsheetCellRange> bounds() {
        if (null == this.bounds) {
            this.bounds = this.isEmpty() ?
                    Optional.empty() :
                    this.boundsNotEmpty();
        }

        return this.bounds;
    }

    private Optional<SpreadsheetCellRange> bounds;

    private Optional<SpreadsheetCellRange> boundsNotEmpty() {
        SpreadsheetColumnReference left = SpreadsheetReferenceKind.RELATIVE.lastColumn();
        SpreadsheetRowReference top = SpreadsheetReferenceKind.RELATIVE.lastRow();

        SpreadsheetColumnReference right = SpreadsheetReferenceKind.RELATIVE.firstColumn();
        SpreadsheetRowReference bottom = SpreadsheetReferenceKind.RELATIVE.firstRow();

        for (final SpreadsheetCellRange range : this.cellRanges()) {
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

    // Iterable.........................................................................................................

    @Override
    public Iterator<SpreadsheetCellReference> iterator() {
        final Set<SpreadsheetCellRange> cellRanges = this.cellRanges;

        final Iterable<SpreadsheetCellReference>[] iterables = new Iterable[cellRanges.size()];
        cellRanges.toArray(iterables);

        return Iterables.chain(iterables).iterator();
    }

    /**
     * This {@link Iterator} returns all the {@link SpreadsheetCellReference} present within this {@link SpreadsheetViewportWindows}.
     */
    public Iterator<SpreadsheetCellReference> cells(final SpreadsheetSelection nonLabel) {
        Objects.requireNonNull(nonLabel, "nonLabel");

        return this.isEmpty() ?
                Iterators.empty() :
                this.cells0(nonLabel.toCellRange());
    }

    private Iterator<SpreadsheetCellReference> cells0(final SpreadsheetCellRange cells) {
        final SpreadsheetCellReference cellsBegin = cells.begin();
        final SpreadsheetCellReference cellsEnd = cells.end();

        final SpreadsheetCellRange bounds = this.bounds()
                .get();

        final SpreadsheetCellReference boundsBegin = bounds.begin();
        final SpreadsheetCellReference boundsEnd = bounds.end();

        final SpreadsheetColumnReference left = cellsBegin.column()
                .max(boundsBegin.column());

        final SpreadsheetRowReference top = cellsBegin.row()
                .max(boundsBegin.row());

        final SpreadsheetColumnReference right = cellsEnd.column()
                .min(boundsEnd.column());

        final SpreadsheetRowReference bottom = cellsEnd.row()
                .min(boundsEnd.row());

        final SpreadsheetCellRange selection = left.setRow(top)
                .cellRange(
                        right.setRow(bottom)
                );

        return Iterators.predicated(
                this.iterator(),
                selection::testCell
        );
    }

    // Predicate........................................................................................................

    /**
     * Return true if there are no cell-ranges OR the cell is matched by any cell-range.
     */
    @Override
    public boolean test(final SpreadsheetSelection selection) {
        Objects.requireNonNull(selection, "selection");

        final Set<SpreadsheetCellRange> cellRanges = this.cellRanges;
        return cellRanges.isEmpty() ||
                cellRanges.stream()
                        .anyMatch(r -> r.test(selection));

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
                SpreadsheetCellRange::toStringMaybeStar
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

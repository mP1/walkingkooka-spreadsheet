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
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRange;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.CharacterConstant;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Captures one or more windows that define the cells within a viewport.
 */
public final class SpreadsheetViewportWindows implements Iterable<SpreadsheetCellReference>,
        Predicate<SpreadsheetSelection> {

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
        return with(
                SpreadsheetSelection.parseWindow(windows)
        );
    }

    public static SpreadsheetViewportWindows with(final Set<SpreadsheetCellRange> cellRanges) {
        Objects.requireNonNull(cellRanges, "cellRanges");

        final Set<SpreadsheetCellRange> copy = Sets.immutable(cellRanges);
        return copy.isEmpty() ?
                EMPTY :
                new SpreadsheetViewportWindows(copy);
    }

    private SpreadsheetViewportWindows(final Set<SpreadsheetCellRange> cellRanges) {
        super();

        this.cellRanges = Sets.immutable(cellRanges);
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

    // Iterable.........................................................................................................

    @Override
    public Iterator<SpreadsheetCellReference> iterator() {
        final Set<SpreadsheetCellRange> cellRanges = this.cellRanges;

        final Iterable<SpreadsheetCellReference>[] iterables = new Iterable[cellRanges.size()];
        cellRanges.toArray(iterables);

        return Iterables.chain(iterables).iterator();
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
        return this.cellRanges.stream()
                .map(SpreadsheetCellRange::toString)
                .collect(Collectors.joining(SEPARATOR.string()));
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

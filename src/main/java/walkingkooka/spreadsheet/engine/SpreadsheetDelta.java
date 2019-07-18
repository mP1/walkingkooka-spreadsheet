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
import walkingkooka.collect.set.Sets;
import walkingkooka.compare.Range;
import walkingkooka.net.http.server.hateos.HasHateosLinkId;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.reference.SpreadsheetRange;
import walkingkooka.test.HashCodeEqualsDefined;
import walkingkooka.tree.json.HasJsonNode;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonNodeName;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Captures changes following an operation. A window when non empty is applied to any given cells as a filter.
 * The ID must be either an {@link Optional} or {@link Range} matching the ID limitations of {@link walkingkooka.net.http.server.hateos.HateosHandler}.
 */
public abstract class SpreadsheetDelta<I> implements Comparable<SpreadsheetDelta<I>>,
        HashCodeEqualsDefined,
        HateosResource<I> {

    public final static Set<SpreadsheetCell> NO_CELLS = Sets.empty();

    /**
     * Factory that creates a new {@link SpreadsheetDelta} with an id.
     */
    public static <I extends Comparable<I> & HasHateosLinkId> SpreadsheetDelta<Optional<I>> withId(final Optional<I> id,
                                                                                                   final Set<SpreadsheetCell> cells) {
        checkId(id);
        checkCells(cells);

        return SpreadsheetDeltaIdNonWindowed.with(id, cells);
    }

    /**
     * Factory that creates a new {@link SpreadsheetDelta} with an id.
     */
    public static <I extends Comparable<I> & HasHateosLinkId> SpreadsheetDelta<Range<I>> withRange(final Range<I> range,
                                                                                                   final Set<SpreadsheetCell> cells) {
        checkRange(range);
        checkCells(cells);

        return SpreadsheetDeltaRangeNonWindowed.with(range, cells);
    }

    static void checkId(final Optional<?> id) {
        Objects.requireNonNull(id, "id");
    }

    static void checkRange(final Range<?> range) {
        Objects.requireNonNull(range, "range");
    }

    /**
     * Package private to limit sub classing.
     */
    SpreadsheetDelta(final I id,
                     final Set<SpreadsheetCell> cells) {
        super();

        this.id = id;
        this.cells = cells;
    }

    // HateosResource...................................................................................................

    @Override
    public final I id() {
        return this.id;
    }

    final I id;

    /**
     * Would be setter that returns a {@link SpreadsheetDelta} with the given {@link Optional id} creating a new instance and sharing other properties.
     */
    public final <II extends Comparable<II> & HasHateosLinkId> SpreadsheetDelta<Optional<II>> setId(final Optional<II> id) {
        checkId(id);
        return this.setId0(id);
    }

    abstract <II extends Comparable<II> & HasHateosLinkId> SpreadsheetDelta<Optional<II>> setId0(final Optional<II> id);

    /**
     * Would be setter that returns a {@link SpreadsheetDelta} with the given {@link Range range} creating a new instance and sharing other properties.
     */
    public final <II extends Comparable<II> & HasHateosLinkId> SpreadsheetDelta<Range<II>> setRange(final Range<II> range) {
        checkRange(range);

        return this.setRange0(range);
    }

    abstract <II extends Comparable<II> & HasHateosLinkId> SpreadsheetDelta<Range<II>> setRange0(final Range<II> ids);

    /**
     * Unconditionally creates a new instance with the given {@link Optional id}.
     */
    abstract <II extends Comparable<II> & HasHateosLinkId> SpreadsheetDelta<Optional<II>> replaceId(final Optional<II> id);

    /**
     * Unconditionally creates a new instance with the given {@link Range range}.
     */
    abstract <II extends Comparable<II> & HasHateosLinkId> SpreadsheetDelta<Range<II>> replaceRange(final Range<II> range);

    // cells............................................................................................................

    public final Set<SpreadsheetCell> cells() {
        return this.cells;
    }

    /**
     * Would be setter that returns a {@link SpreadsheetDelta} holding the given cells after they are possibly filtered
     * using the {@link #window()}
     */
    public final SpreadsheetDelta<I> setCells(final Set<SpreadsheetCell> cells) {
        checkCells(cells);

        final Set<SpreadsheetCell> copy = this.copyCells(cells);
        return this.cells.equals(copy) ?
                this :
                this.replace(copy, this.window());
    }

    /**
     * Takes a copy of the cells, possibly filtering out cells if a window is present.
     */
    abstract Set<SpreadsheetCell> copyCells(final Set<SpreadsheetCell> cells);

    final Set<SpreadsheetCell> cells;

    static void checkCells(final Set<SpreadsheetCell> cells) {
        Objects.requireNonNull(cells, "cells");
    }

    // window............................................................................................................

    /**
     * Getter that returns any windows for this delta. An empty list signifies, no filtering.
     */
    public abstract List<SpreadsheetRange> window();

    /**
     * Would be setter that if necessary returns a new {@link SpreadsheetDelta} which will also filter cells if necessary.
     */
    public final SpreadsheetDelta<I> setWindow(final List<SpreadsheetRange> window) {
        Objects.requireNonNull(window, "window");

        final List<SpreadsheetRange> copy = Lists.immutable(window);

        return this.window().equals(copy) ?
                this :
                setWindow0(copy);
    }

    private SpreadsheetDelta<I> setWindow0(final List<SpreadsheetRange> window) {
        final Set<SpreadsheetCell> cells = this.cells;

        final Set<SpreadsheetCell> filtered = maybeFilterCells(cells, window);
        return this.window().equals(cells) ?
                this :
                this.replace(filtered, window);
    }

    // filter............................................................................................................

    /**
     * Factory that creates a new {@link SpreadsheetDelta}.
     */
    private SpreadsheetDelta<I> replace(final Set<SpreadsheetCell> cells,
                                        final List<SpreadsheetRange> window) {
        return window.isEmpty() ?
                this.replaceNonWindowed(cells) :
                this.replaceWindowed(cells, window);
    }

    abstract SpreadsheetDelta<I> replaceNonWindowed(final Set<SpreadsheetCell> cells);

    abstract SpreadsheetDelta<I> replaceWindowed(final Set<SpreadsheetCell> cells,
                                                 final List<SpreadsheetRange> window);

    /**
     * Filters the cells using the assumed window of {@link Range cell reference ranges}.
     */
    static Set<SpreadsheetCell> maybeFilterCells(final Set<SpreadsheetCell> cells,
                                                 final List<SpreadsheetRange> window) {
        return null == window || window.isEmpty() ?
                cells :
                cells.stream()
                        .filter(c -> window.stream().anyMatch(r -> r.test(c.reference())))
                        .collect(Collectors.toCollection(Sets::ordered));
    }

    // HasJsonNode.......................................................................................................

    /**
     * Factory that creates a {@link SpreadsheetDelta} from a {@link JsonNode}.
     */
    static SpreadsheetDelta<?> fromJsonNode(final JsonNode node) {
        Objects.requireNonNull(node, "node");

        HasHateosLinkId id = null;
        Range<?> range = null;
        Set<SpreadsheetCell> cells = Sets.empty();
        List<SpreadsheetRange> window = null;

        for (JsonNode child : node.objectOrFail().children()) {
            final JsonNodeName name = child.name();

            switch (name.value()) {
                case ID_PROPERTY_STRING:
                    id = child.fromJsonNodeWithType();
                    break;
                case RANGE_PROPERTY_STRING:
                    range = child.fromJsonNode(Range.class);
                    break;
                case CELLS_PROPERTY_STRING:
                    cells = child.fromJsonNodeSet(SpreadsheetCell.class);
                    break;
                case WINDOW_PROPERTY_STRING:
                    window = rangeFromJsonNode(child.stringValueOrFail());
                    break;
                default:
                    HasJsonNode.unknownPropertyPresent(name, node);
            }
        }

        final SpreadsheetDelta<?> delta = range != null ?
                SpreadsheetDeltaRangeNonWindowed.with(Cast.to(range), cells) :
                fromJsonNodeWithId(Cast.to(id), cells);
        return null != window ?
                delta.setWindow0(window) :
                delta;
    }

    private static List<SpreadsheetRange> rangeFromJsonNode(final String range) {
        return Arrays.stream(range.split(WINDOW_SEPARATOR))
                .map(SpreadsheetRange::parseRange)
                .collect(Collectors.toList());
    }

    private static <I extends Comparable<I> & HasHateosLinkId> SpreadsheetDeltaIdNonWindowed fromJsonNodeWithId(final I id,
                                                                                                                final Set<SpreadsheetCell> cells) {
        return SpreadsheetDeltaIdNonWindowed.with(Optional.ofNullable(id), cells);
    }

    @Override
    public final JsonNode toJsonNode() {
        final List<JsonNode> children = Lists.array();

        final JsonNode idOrRange = this.idOrRangeToJson();
        if (null != idOrRange) {
            children.add(idOrRange.setName(this.idOrRangeJsonPropertyName()));
        }

        final Set<SpreadsheetCell> cells = this.cells;
        if (!cells.isEmpty()) {
            children.add(HasJsonNode.toJsonNodeSet(cells).setName(CELLS_PROPERTY));
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

    abstract JsonNode idOrRangeToJson();

    abstract JsonNodeName idOrRangeJsonPropertyName();

    /**
     * Constant used to separate individual ranges in the window list.
     */
    final static String WINDOW_SEPARATOR = ",";

    private final static String ID_PROPERTY_STRING = "id";
    private final static String RANGE_PROPERTY_STRING = "range";
    private final static String CELLS_PROPERTY_STRING = "cells";
    private final static String WINDOW_PROPERTY_STRING = "window";

    final static JsonNodeName ID_PROPERTY = JsonNodeName.with(ID_PROPERTY_STRING);
    final static JsonNodeName RANGE_PROPERTY = JsonNodeName.with(RANGE_PROPERTY_STRING);
    final static JsonNodeName CELLS_PROPERTY = JsonNodeName.with(CELLS_PROPERTY_STRING);
    final static JsonNodeName WINDOW_PROPERTY = JsonNodeName.with(WINDOW_PROPERTY_STRING);

    static {
        HasJsonNode.register("spreadsheet-delta",
                SpreadsheetDelta::fromJsonNode,
                SpreadsheetDelta.class,
                SpreadsheetDeltaIdNonWindowed.class,
                SpreadsheetDeltaIdWindowed.class,
                SpreadsheetDeltaRangeNonWindowed.class,
                SpreadsheetDeltaRangeWindowed.class);
    }

    // equals...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.cells);
    }

    @Override
    public final boolean equals(final Object other) {
        return this == other ||
                this.canBeEquals(other) &&
                        this.equals0(Cast.to(other));
    }

    abstract boolean canBeEquals(final Object other);

    private boolean equals0(final SpreadsheetDelta other) {
        return this.id.equals(other.id) &&
                this.cells.equals(other.cells) &&
                this.equals1(other);
    }

    abstract boolean equals1(final SpreadsheetDelta other);

    /**
     * Produces a {@link String} that contains ID then cells: comma separated cells then window: comma separated window ranges.
     */
    @Override
    public final String toString() {
        return ToStringBuilder.empty()
                .labelSeparator(": ")
                .separator(" ")
                .valueSeparator(", ")
                .disable(ToStringBuilderOption.QUOTE)
                .value(this.toStringId())
                .enable(ToStringBuilderOption.QUOTE)
                .label("cells")
                .value(this.cells)
                .label("window").value(this.window())
                .build();
    }

    /**
     * Allows Range to format itself with a COLON rather than the default double dot...
     * <pre>
     * (1e:2f) cells: A1=1, B2=2, C3=3 window: A1:E5, F6:Z99
     * (1e..2f) cells: A1=1, B2=2, C3=3 window: A1:E5, F6:Z99
     * </pre>
     */
    abstract Object toStringId();

    // Comparable.......................................................................................................

    @Override
    public final int compareTo(final SpreadsheetDelta<I> other) {
        throw new UnsupportedOperationException();
    }
}

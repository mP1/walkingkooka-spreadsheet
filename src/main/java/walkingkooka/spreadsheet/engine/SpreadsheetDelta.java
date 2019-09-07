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
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.reference.SpreadsheetRange;
import walkingkooka.test.HashCodeEqualsDefined;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonNodeName;
import walkingkooka.tree.json.marshall.FromJsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.ToJsonNodeContext;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Captures changes following an operation. A window when non empty is applied to any given cells as a filter.
 */
public abstract class SpreadsheetDelta implements HashCodeEqualsDefined {

    public final static Set<SpreadsheetCell> NO_CELLS = Sets.empty();

    /**
     * Factory that creates a new {@link SpreadsheetDelta} with an id.
     */
    public static SpreadsheetDelta with(final Set<SpreadsheetCell> cells) {
        checkCells(cells);

        return SpreadsheetDeltaNonWindowed.withNonWindowed(cells);
    }

    /**
     * Package private to limit sub classing.
     */
    SpreadsheetDelta(final Set<SpreadsheetCell> cells) {
        super();

        this.cells = cells;
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

        final Set<SpreadsheetCell> copy = this.copyCells(cells);
        return this.cells.equals(copy) ?
                this :
                this.replace(copy);
    }

    abstract SpreadsheetDelta replace(final Set<SpreadsheetCell> cells);

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
    public final SpreadsheetDelta setWindow(final List<SpreadsheetRange> window) {
        Objects.requireNonNull(window, "window");

        final List<SpreadsheetRange> copy = Lists.immutable(window);
        return this.window().equals(copy) ?
                this :
                this.setWindow0(copy);
    }

    private SpreadsheetDelta setWindow0(final List<SpreadsheetRange> window) {
        final Set<SpreadsheetCell> cells = this.cells;

        final Set<SpreadsheetCell> filtered = maybeFilterCells(cells, window);
        return window.isEmpty() ?
                SpreadsheetDeltaNonWindowed.withNonWindowed(filtered) :
                SpreadsheetDeltaWindowed.withWindowed(filtered, window);
    }

    static Set<SpreadsheetCell> maybeFilterCells(final Set<SpreadsheetCell> cells,
                                                 final List<SpreadsheetRange> window) {
        return window.isEmpty() ?
                cells :
                cells.stream()
                        .filter(c -> window.stream().anyMatch(r -> r.test(c.reference())))
                        .collect(Collectors.toCollection(Sets::ordered));
    }

    // JsonNodeContext..................................................................................................

    static SpreadsheetDelta fromJsonNode(final JsonNode node,
                                         final FromJsonNodeContext context) {
        Set<SpreadsheetCell> cells = Sets.empty();
        List<SpreadsheetRange> window = null;

        for (JsonNode child : node.objectOrFail().children()) {
            final JsonNodeName name = child.name();

            switch (name.value()) {
                case CELLS_PROPERTY_STRING:
                    cells = context.fromJsonNodeSet(child, SpreadsheetCell.class);
                    break;
                case WINDOW_PROPERTY_STRING:
                    window = rangeFromJsonNode(child.stringValueOrFail());
                    break;
                default:
                    FromJsonNodeContext.unknownPropertyPresent(name, node);
            }
        }

        return null == window ?
                SpreadsheetDeltaNonWindowed.withNonWindowed(cells) :
                SpreadsheetDeltaWindowed.withWindowed(cells, window);
    }

    private static List<SpreadsheetRange> rangeFromJsonNode(final String range) {
        return Arrays.stream(range.split(WINDOW_SEPARATOR))
                .map(SpreadsheetRange::parseRange)
                .collect(Collectors.toList());
    }

    final JsonNode toJsonNode(final ToJsonNodeContext context) {
        final List<JsonNode> children = Lists.array();

        final Set<SpreadsheetCell> cells = this.cells;
        if (!cells.isEmpty()) {
            children.add(context.toJsonNodeSet(cells).setName(CELLS_PROPERTY));
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
     * Constant used to separate individual ranges in the window list.
     */
    final static String WINDOW_SEPARATOR = ",";

    private final static String CELLS_PROPERTY_STRING = "cells";
    private final static String WINDOW_PROPERTY_STRING = "window";

    final static JsonNodeName CELLS_PROPERTY = JsonNodeName.with(CELLS_PROPERTY_STRING);
    final static JsonNodeName WINDOW_PROPERTY = JsonNodeName.with(WINDOW_PROPERTY_STRING);

    static {
        JsonNodeContext.register("spreadsheet-delta",
                SpreadsheetDelta::fromJsonNode,
                SpreadsheetDelta::toJsonNode,
                SpreadsheetDelta.class,
                SpreadsheetDeltaNonWindowed.class,
                SpreadsheetDeltaWindowed.class);
    }

    // equals...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(this.cells);
    }

    @Override
    public final boolean equals(final Object other) {
        return this == other ||
                this.canBeEquals(other) &&
                        this.equals0(Cast.to(other));
    }

    abstract boolean canBeEquals(final Object other);

    private boolean equals0(final SpreadsheetDelta other) {
        return this.cells.equals(other.cells) &&
                this.equals1(other);
    }

    abstract boolean equals1(final SpreadsheetDelta other);

    /**
     * Produces a {@link String} the cells and window if present.
     */
    @Override
    public final String toString() {
        return ToStringBuilder.empty()
                .labelSeparator(": ")
                .separator(" ")
                .valueSeparator(", ")
                .enable(ToStringBuilderOption.QUOTE)
                .label("cells")
                .value(this.cells)
                .label("window").value(this.window())
                .build();
    }
}

package walkingkooka.spreadsheet;

import walkingkooka.Cast;
import walkingkooka.build.tostring.ToStringBuilder;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.compare.Range;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.test.HashCodeEqualsDefined;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.tree.json.HasJsonNode;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonNodeException;
import walkingkooka.tree.json.JsonNodeName;
import walkingkooka.tree.json.JsonObjectNode;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Captures changes following an operation.
 */
public abstract class SpreadsheetDelta implements HashCodeEqualsDefined, HateosResource<SpreadsheetId> {

    public final static Set<SpreadsheetCell> NO_CELLS = Sets.empty();

    /**
     * Factory that creates a new {@link SpreadsheetDelta}
     */
    public static SpreadsheetDelta with(final SpreadsheetId id,
                                        final Set<SpreadsheetCell> cells) {
        Objects.requireNonNull(id, "id");
        checkCells(cells);

        return nonWindowed(id, cells);
    }

    static SpreadsheetDelta filterCellsIfNecessaryAndCreate(final SpreadsheetId id,
                                                            final Set<SpreadsheetCell> cells,
                                                            final List<Range<SpreadsheetCellReference>> window) {
        return null == window || window.isEmpty() ?
                nonWindowed(id, cells) :
                windowed(id,
                        filterCells(cells, window),
                        Lists.readOnly(window));
    }

    /**
     * Factory that creates a {@link SpreadsheetDeltaNonWindowed}
     */
    static SpreadsheetDeltaNonWindowed nonWindowed(final SpreadsheetId id,
                                                   final Set<SpreadsheetCell> cells) {
        return SpreadsheetDeltaNonWindowed.with0(id, copyCells(cells));
    }

    /**
     * Factory that creates a {@link SpreadsheetDeltaWindowed}
     */
    static SpreadsheetDeltaWindowed windowed(final SpreadsheetId id,
                                             final Set<SpreadsheetCell> cells,
                                             final List<Range<SpreadsheetCellReference>> window) {
        return SpreadsheetDeltaWindowed.with0(id, cells, window);
    }

    SpreadsheetDelta(final SpreadsheetId id,
                     final Set<SpreadsheetCell> cells) {
        super();

        this.id = id;
        this.cells = cells;
    }

    @Override
    public SpreadsheetId id() {
        return this.id;
    }

    final SpreadsheetId id;

    // cells............................................................................................................

    public final Set<SpreadsheetCell> cells() {
        return this.cells;
    }

    /**
     * Would be setter that returns {@link SpreadsheetDelta}
     */
    public abstract SpreadsheetDelta setCells(final Set<SpreadsheetCell> cells);

    final Set<SpreadsheetCell> cells;

    static void checkCells(final Set<SpreadsheetCell> cells) {
        Objects.requireNonNull(cells, "cells");
    }

    /**
     * Makes a copy of the cells without any filtering.
     */
    static Set<SpreadsheetCell> copyCells(final Set<SpreadsheetCell> cells) {
        final Set<SpreadsheetCell> copy = Sets.ordered();
        copy.addAll(cells);
        return Sets.readOnly(cells);
    }

    /**
     * Filters the cells using the assumed window of {@link Range cell reference ranges}.
     */
    static Set<SpreadsheetCell> filterCells(final Set<SpreadsheetCell> cells,
                                            final List<Range<SpreadsheetCellReference>> window) {
        return cells.stream()
                .filter(c -> window.stream().anyMatch(r -> r.test(c.reference())))
                .collect(Collectors.toCollection(Sets::ordered));
    }

    // window............................................................................................................

    /**
     * Getter that returns any windows for this delta. An empty list signifies, no filtering.
     */
    public abstract List<Range<SpreadsheetCellReference>> window();

    /**
     * Would be setter that if necessary returns a new {@link SpreadsheetDelta} which will also filter cells if necessary.
     */
    public final SpreadsheetDelta setWindow(final List<Range<SpreadsheetCellReference>> window) {
        final List<Range<SpreadsheetCellReference>> copy = copyWindow(window);

        return this.window().equals(copy) ?
                this :
                filterCellsIfNecessaryAndCreate(this.id, this.cells, copy);
    }

    /**
     * Checks and makes a copy of the window ranges.
     */
    private static List<Range<SpreadsheetCellReference>> copyWindow(final List<Range<SpreadsheetCellReference>> window) {
        Objects.requireNonNull(window, "window");

        final List<Range<SpreadsheetCellReference>> copy = Lists.array();
        copy.addAll(window);

        return copy;
    }

    // HasJsonNode...................................................................................................

    /**
     * Factory that creates a {@link SpreadsheetDelta} from a {@link JsonNode}.
     */
    public static SpreadsheetDelta fromJsonNode(final JsonNode node) {
        Objects.requireNonNull(node, "node");

        SpreadsheetId id = null;
        Set<SpreadsheetCell> cells = null;
        List<Range<SpreadsheetCellReference>> window = null;

        try {
            for (JsonNode child : node.objectOrFail().children()) {
                final JsonNodeName name = child.name();
                switch (name.value()) {
                    case ID_PROPERTY_STRING:
                        id = SpreadsheetId.fromJsonNode(child);
                        break;
                    case CELLS_PROPERTY_STRING:
                        cells = child.fromJsonNodeSet(SpreadsheetCell.class);
                        break;
                    case WINDOW_PROPERTY_STRING:
                        window = windowFromJsonNode(child.stringValueOrFail());
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown property " + name + "=" + node);
                }
            }
        } catch (final JsonNodeException cause) {
            throw new IllegalArgumentException(cause.getMessage(), cause);
        }

        if (null == id) {
            HasJsonNode.requiredPropertyMissing(ID_PROPERTY, node);
        }
        if (null == cells) {
            HasJsonNode.requiredPropertyMissing(CELLS_PROPERTY, node);
        }

        return SpreadsheetDelta.filterCellsIfNecessaryAndCreate(id, cells, window);
    }

    private static List<Range<SpreadsheetCellReference>> windowFromJsonNode(final String range) {
        return Arrays.stream(range.split(WINDOW_SEPARATOR))
                .map(SpreadsheetCellReference::parseRange)
                .collect(Collectors.toList());
    }

    /**
     * Creates a {@link JsonObjectNode} with the id and cells.
     */
    final JsonObjectNode toJsonNodeIdAndCells() {
        return JsonNode.object()
                .set(ID_PROPERTY, this.id.toJsonNode())
                .set(CELLS_PROPERTY, HasJsonNode.toJsonNodeSet(this.cells));
    }

    final static String WINDOW_SEPARATOR = ",";

    private final static String ID_PROPERTY_STRING = "id";
    private final static String CELLS_PROPERTY_STRING = "cells";
    private final static String WINDOW_PROPERTY_STRING = "window";

    final static JsonNodeName ID_PROPERTY = JsonNodeName.with(ID_PROPERTY_STRING);
    final static JsonNodeName CELLS_PROPERTY = JsonNodeName.with(CELLS_PROPERTY_STRING);
    final static JsonNodeName WINDOW_PROPERTY = JsonNodeName.with(WINDOW_PROPERTY_STRING);

    static {
        HasJsonNode.register("spreadsheet-delta",
                SpreadsheetDelta::fromJsonNode,
                SpreadsheetDelta.class,
                SpreadsheetDeltaNonWindowed.class,
                SpreadsheetDeltaWindowed.class);
    }

    // equals...................................................................................................

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

    @Override
    public final String toString() {
        return ToStringBuilder.empty()
                .labelSeparator(": ")
                .separator(" ")
                .valueSeparator(",")
                .label("cells").value(this.cells)
                .label("window").value(this.window())
                .build();
    }
}

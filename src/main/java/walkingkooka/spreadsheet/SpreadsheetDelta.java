package walkingkooka.spreadsheet;

import walkingkooka.Cast;
import walkingkooka.collect.set.Sets;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.test.HashCodeEqualsDefined;
import walkingkooka.tree.json.HasJsonNode;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonNodeException;
import walkingkooka.tree.json.JsonNodeName;

import java.util.Objects;
import java.util.Set;

/**
 * Captures changes following an operation.
 */
public final class SpreadsheetDelta implements HashCodeEqualsDefined, HateosResource<SpreadsheetId> {

    public static SpreadsheetDelta with(final SpreadsheetId id, final Set<SpreadsheetCell> cells) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(cells, "cells");

        final Set<SpreadsheetCell> copy = Sets.ordered();
        copy.addAll(cells);
        return new SpreadsheetDelta(id, Sets.readOnly(cells));
    }

    private SpreadsheetDelta(final SpreadsheetId id, final Set<SpreadsheetCell> cells) {
        super();

        this.id = id;
        this.cells = cells;
    }

    @Override
    public SpreadsheetId id() {
        return this.id;
    }

    private final SpreadsheetId id;

    public Set<SpreadsheetCell> cells() {
        return this.cells;
    }

    private final Set<SpreadsheetCell> cells;

    // HasJsonNode...................................................................................................

    /**
     * Factory that creates a {@link SpreadsheetDelta} from a {@link JsonNode}.
     */
    public static SpreadsheetDelta fromJsonNode(final JsonNode node) {
        Objects.requireNonNull(node, "node");

        SpreadsheetId id = null;
        Set<SpreadsheetCell> cells = null;

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

        return new SpreadsheetDelta(id, cells);
    }

    @Override
    public JsonNode toJsonNode() {
        return JsonNode.object()
                .set(ID_PROPERTY, this.id.toJsonNode())
                .set(CELLS_PROPERTY, HasJsonNode.toJsonNodeSet(this.cells));
    }

    private final static String ID_PROPERTY_STRING = "id";
    private final static String CELLS_PROPERTY_STRING = "cells";

    final static JsonNodeName ID_PROPERTY = JsonNodeName.with(ID_PROPERTY_STRING);
    final static JsonNodeName CELLS_PROPERTY = JsonNodeName.with(CELLS_PROPERTY_STRING);

    static {
        HasJsonNode.register("spreadsheet-delta",
                SpreadsheetDelta::fromJsonNode,
                SpreadsheetDelta.class);
    }

    // equals...................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.cells);
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetDelta &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetDelta other) {
        return this.id.equals(other.id) &&
                this.cells.equals(other.cells);
    }

    @Override
    public String toString() {
        return this.cells.toString();
    }
}

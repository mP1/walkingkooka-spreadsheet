package walkingkooka.spreadsheet.security;

import walkingkooka.tree.json.JsonNode;

/**
 * The primary key for a {@link Group}.
 */
public final class GroupId extends IdentityId {

    public static GroupId fromJsonNode(final JsonNode node) {
        return with(JsonNode.fromJsonNodeLong(node));
    }

    static GroupId with(final long value) {
        return new GroupId(value);
    }

    private GroupId(final long value) {
        super(value);
    }

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof GroupId;
    }
}

package walkingkooka.spreadsheet.security;

import walkingkooka.tree.json.JsonNode;

/**
 * The primary key for a {@link User}.
 */
public final class UserId extends IdentityId {

    public static UserId fromJsonNode(final JsonNode node) {
        return with(JsonNode.fromJsonNodeLong(node));
    }

    static UserId with(final long value) {
        return new UserId(value);
    }

    private UserId(final long value) {
        super(value);
    }

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof UserId;
    }
}

package walkingkooka.spreadsheet.security;

import walkingkooka.Cast;
import walkingkooka.Value;
import walkingkooka.test.HashCodeEqualsDefined;
import walkingkooka.tree.json.HasJsonNode;
import walkingkooka.tree.json.JsonNode;

/**
 * Base class for all ids.
 */
public abstract class IdentityId implements Value<Long>,
        HashCodeEqualsDefined,
        HasJsonNode {

    /**
     * Package private to limit sub classing.
     *
     * @param value
     */
    IdentityId(final long value) {
        super();
        this.value = value;
    }

    @Override
    public final Long value() {
        return Long.valueOf(this.value);
    }

    private final long value;

    // HasJsonNode.................................................

    /**
     * Stores the id.
     */
    public final JsonNode toJsonNode() {
        return JsonNode.wrapLong(this.value);
    }

    // Object.................................................

    @Override
    public final int hashCode() {
        return Long.hashCode(this.value);
    }

    @Override
    public final boolean equals(final Object other) {
        return this == other ||
                this.canBeEqual(other) &&
                        this.equals0(Cast.to(other));
    }

    /**
     * Sub classes should do an instanceof test.
     */
    abstract boolean canBeEqual(final Object other);

    private boolean equals0(final IdentityId other) {
        return this.value == other.value;
    }

    @Override
    public final String toString() {
        return String.valueOf(this.value);
    }
}

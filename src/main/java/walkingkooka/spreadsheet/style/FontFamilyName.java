package walkingkooka.spreadsheet.style;

import walkingkooka.Cast;
import walkingkooka.naming.Name;
import walkingkooka.test.HashCodeEqualsDefined;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.json.HasJsonNode;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonNodeException;
import walkingkooka.tree.json.JsonStringNode;

import java.util.Objects;

/**
 * A font family name.
 */
public final class FontFamilyName implements Name,
        Comparable<FontFamilyName>,
        HashCodeEqualsDefined,
        HasJsonNode {

    public static FontFamilyName with(final String name) {
        CharSequences.failIfNullOrEmpty(name, "name");
        return new FontFamilyName(name);
    }

    private FontFamilyName(final String name) {
        this.name = name;
    }

    @Override
    public String value() {
        return this.name;
    }

    private final String name;

    // HasJsonNode ...................................................................................................

    /**
     * Factory that creates a {@link FontFamilyName} from a {@link JsonNode}.
     */
    public static FontFamilyName fromJsonNode(final JsonNode node) {
        Objects.requireNonNull(node, "node");

        try {
            return with(node.stringValueOrFail());
        } catch (final JsonNodeException cause) {
            throw new IllegalArgumentException(cause.getMessage(), cause);
        }
    }

    @Override
    public JsonNode toJsonNode() {
        return JsonNode.string(this.name);
    }

    // Object..................................................................................................

    public final int hashCode() {
        return CASE_SENSITITY.hash(this.name);
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof FontFamilyName &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final FontFamilyName other) {
        return this.compareTo(other) == 0;
    }

    @Override
    public String toString() {
        return this.name;
    }

    // Comparable ...................................................................................................

    @Override
    public int compareTo(final FontFamilyName other) {
        return CASE_SENSITITY.comparator().compare(this.name, other.name);
    }

    private final CaseSensitivity CASE_SENSITITY = CaseSensitivity.SENSITIVE;
}

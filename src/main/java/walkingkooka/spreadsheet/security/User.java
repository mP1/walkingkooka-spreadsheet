package walkingkooka.spreadsheet.security;

import walkingkooka.net.email.EmailAddress;
import walkingkooka.tree.json.HasJsonNode;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonNodeException;
import walkingkooka.tree.json.JsonNodeName;

import java.util.Objects;

/**
 * A user in the system.
 */
public final class User extends Identity<UserId>
        implements HasJsonNode {

    /**
     * Factory that creates a new {@link User}.
     */
    public static User with(final UserId id, final EmailAddress email) {
        checkId(id);
        Objects.requireNonNull(email, "email");

        return new User(id, email);
    }

    /**
     * Private ctor use factory.
     */
    private User(final UserId id, final EmailAddress email) {
        super(id);
        this.email = email;
    }

    public EmailAddress email() {
        return this.email;
    }

    private final EmailAddress email;

    // HasJsonNode..........................................................................................

    /**
     * Factory that creates a {@link User} from a {@link JsonNode}.
     */
    public static User fromJsonNode(final JsonNode node) {
        Objects.requireNonNull(node, "node");

        UserId id = null;
        EmailAddress email = null;

        try {
            for (JsonNode child : node.objectOrFail().children()) {
                final JsonNodeName name = child.name();
                switch (name.value()) {
                    case ID_PROPERTY_STRING:
                        id = UserId.fromJsonNode(child);
                        break;
                    case EMAIL_PROPERTY_STRING:
                        email = EmailAddress.fromJsonNode(child);
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
        if (null == email) {
            HasJsonNode.requiredPropertyMissing(EMAIL_PROPERTY, node);
        }

        return new User(id, email);
    }

    @Override
    public JsonNode toJsonNode() {
        return JsonNode.object()
                .set(ID_PROPERTY, this.id.toJsonNode())
                .set(EMAIL_PROPERTY, this.email.toJsonNode());
    }

    private final static String ID_PROPERTY_STRING = "id";
    private final static String EMAIL_PROPERTY_STRING = "email";

    final static JsonNodeName ID_PROPERTY = JsonNodeName.with(ID_PROPERTY_STRING);
    final static JsonNodeName EMAIL_PROPERTY = JsonNodeName.with(EMAIL_PROPERTY_STRING);

    static {
        HasJsonNode.register(User.class, User::fromJsonNode);
    }

    // Identity.................................................................................................

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof User;
    }

    @Override
    boolean equals1(final Identity<?> other) {
        return this.email.equals(User.class.cast(other).email);
    }

    @Override
    public String toString() {
        return this.email.toString();
    }
}

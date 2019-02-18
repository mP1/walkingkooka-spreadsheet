package walkingkooka.spreadsheet.security;

import org.junit.jupiter.api.Test;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.tree.json.HasJsonNodeTesting;
import walkingkooka.tree.json.JsonNode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class UserTest implements IdentityTesting<User, UserId>,
        HasJsonNodeTesting<User> {

    private final static EmailAddress EMAIL = EmailAddress.parse("user@example.com");

    @Test
    public void testWithNullEmailFails() {
        assertThrows(NullPointerException.class, () -> {
            User.with(UserId.with(1), null);
        });
    }

    @Test
    public void testWith() {
        final UserId id = UserId.with(1);
        final User user = User.with(id, EMAIL);
        assertEquals(id, user.value(), "id");
        assertEquals(EMAIL, user.email(), "email");
    }

    // HasJsonNodeTesting.................................................................................................

    @Test
    public void testFromJsonNodeBooleanFails() {
        this.fromJsonNodeFails(JsonNode.booleanNode(true));
    }

    @Test
    public void testFromJsonNodeNullFails() {
        this.fromJsonNodeFails(JsonNode.nullNode());
    }

    @Test
    public void testFromJsonNodeNumberFails() {
        this.fromJsonNodeFails(JsonNode.number(12));
    }

    @Test
    public void testFromJsonNodeArrayFails() {
        this.fromJsonNodeFails(JsonNode.array());
    }

    @Test
    public void testFromJsonNodeObjectFails() {
        this.fromJsonNodeFails(JsonNode.object());
    }

    @Test
    public void testFromJsonNode() {
        this.fromJsonNodeAndCheck(this.jsonNode(),
                User.with(this.createId(), EMAIL));
    }

    @Test
    public void testToJsonNode() {
        this.toJsonNodeAndCheck(this.createObject(), this.jsonNode());
    }

    private JsonNode jsonNode() {
        return JsonNode.object()
                .set(User.ID_PROPERTY, createId().toJsonNode())
                .set(User.EMAIL_PROPERTY, EMAIL.toJsonNode());
    }

    @Test
    public void testToJsonNodeRoundtripTwice() {
        this.toJsonNodeRoundTripTwiceAndCheck(this.createObject());
    }

    // ToStringTesting.................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(User.with(UserId.with(1), EMAIL), EMAIL.toString());
    }

    @Override
    public User createIdentity(final UserId id) {
        return User.with(id, EMAIL);
    }

    @Override
    public UserId createId() {
        return UserId.with(1);
    }

    @Override
    public Class<User> type() {
        return User.class;
    }

    // HasJsonNodeTesting..................................................................

    @Override
    public final User fromJsonNode(final JsonNode from) {
        return User.fromJsonNode(from);
    }
}

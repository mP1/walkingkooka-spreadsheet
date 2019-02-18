package walkingkooka.spreadsheet.security;

import org.junit.jupiter.api.Test;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.tree.json.HasJsonNodeTesting;
import walkingkooka.tree.json.JsonNode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class GroupTest implements IdentityTesting<Group, GroupId>,
        HasJsonNodeTesting<Group> {

    private final long ID_VALUE = 123;

    @Test
    public void testWithNullNameFails() {
        assertThrows(NullPointerException.class, () -> {
            Group.with(GroupId.with(1), null);
        });
    }

    @Test
    public void testWith() {
        final GroupId id = GroupId.with(1);
        final Group group = Group.with(id, name());
        assertEquals(id, group.value(), "id");
        assertEquals(name(), group.name(), "name");
    }

    @Test
    public void testDifferentName() {
        this.checkNotEquals(Group.with(GroupId.with(ID_VALUE), GroupName.with("different")));
    }

    @Test
    public void testUser() {
        this.checkNotEquals(User.with(UserId.with(ID_VALUE), EmailAddress.parse("user@example.com")));
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
                Group.with(this.createId(), this.name()));
    }

    @Test
    public void testToJsonNode() {
        this.toJsonNodeAndCheck(this.createObject(), this.jsonNode());
    }

    private JsonNode jsonNode() {
        return JsonNode.object()
                .set(Group.ID_PROPERTY, createId().toJsonNode())
                .set(Group.NAME_PROPERTY, this.name().toJsonNode());
    }

    @Test
    public void testToJsonNodeRoundtripTwice() {
        this.toJsonNodeRoundTripTwiceAndCheck(this.createObject());
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(Group.with(GroupId.with(ID_VALUE), name()), name().toString());
    }

    @Override
    public Group createIdentity(final GroupId id) {
        return Group.with(id, name());
    }

    @Override
    public GroupId createId() {
        return GroupId.with(1);
    }

    private GroupName name() {
        return GroupName.with("Group-123");
    }

    @Override
    public Class<Group> type() {
        return Group.class;
    }

    // HasJsonNodeTesting..................................................................

    @Override
    public final Group fromJsonNode(final JsonNode from) {
        return Group.fromJsonNode(from);
    }
}

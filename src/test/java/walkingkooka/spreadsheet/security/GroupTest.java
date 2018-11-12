package walkingkooka.spreadsheet.security;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public final class GroupTest extends IdentityTestCase<Group, GroupId> {

    @Test(expected = NullPointerException.class)
    public void testWithNullNameFails() {
        Group.with(GroupId.with(1), null);
    }

    @Test
    public void testWith() {
        final GroupId id = GroupId.with(1);
        final Group group = Group.with(id, name());
        assertEquals("id", id, group.value());
        assertEquals("name", name(), group.name());
    }

    @Test
    public void testToString() {
        assertEquals(name().toString(), Group.with(GroupId.with(1), name()).toString());
    }

    @Override
    Group createIdentity(final GroupId id) {
        return Group.with(id, name());
    }

    @Override
    GroupId createId() {
        return GroupId.with(1);
    }

    private GroupName name() {
        return GroupName.with("Group-123");
    }

    @Override
    protected Class<Group> type() {
        return Group.class;
    }
}

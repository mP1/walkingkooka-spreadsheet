package walkingkooka.spreadsheet.security;

import org.junit.Test;
import walkingkooka.net.email.EmailAddress;

import static org.junit.Assert.assertEquals;

public final class GroupTest extends IdentityTestCase<Group, GroupId> {

    private final long ID_VALUE = 123;

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
    public void testDifferentName() {
        this.checkNotEquals(Group.with(GroupId.with(ID_VALUE), GroupName.with("different")));
    }

    @Test
    public void testUser() {
        this.checkNotEquals(User.with(UserId.with(ID_VALUE), EmailAddress.with("user@example.com")));
    }

    @Test
    public void testToString() {
        assertEquals(name().toString(), Group.with(GroupId.with(ID_VALUE), name()).toString());
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

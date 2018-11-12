package walkingkooka.spreadsheet.security;

import org.junit.Test;
import walkingkooka.net.email.EmailAddress;

public final class GroupEqualityTest extends IdentityEqualityTestCase<Group, GroupId> {

    @Test
    public void testDifferentName() {
        this.checkNotEquals(Group.with(GroupId.with(ID_VALUE), GroupName.with("different")));
    }

    @Test
    public void testUser() {
        this.checkNotEquals(User.with(UserId.with(ID_VALUE), EmailAddress.with("user@example.com")));
    }

    @Override
    Group createObject(final GroupId id) {
        return Group.with(id, name());
    }

    @Override
    GroupId createId(final long value) {
        return GroupId.with(value);
    }

    private GroupName name() {
        return GroupName.with("Group-123");
    }
}

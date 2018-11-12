package walkingkooka.spreadsheet.security;

import org.junit.Test;

public final class GroupIdEqualityTest extends IdentityIdEqualityTestCase<GroupId> {

    @Test
    public void testUserId() {
        this.checkNotEquals(UserId.with(ID_VALUE));
    }

    @Override
    GroupId createObject(final long id) {
        return GroupId.with(id);
    }
}

package walkingkooka.spreadsheet.security;

import org.junit.Test;

public final class UserIdEqualityTest extends IdentityIdEqualityTestCase<UserId> {

    @Test
    public void testGroupId() {
        this.checkNotEquals(GroupId.with(ID_VALUE));
    }

    @Override
    UserId createObject(final long id) {
        return UserId.with(id);
    }
}

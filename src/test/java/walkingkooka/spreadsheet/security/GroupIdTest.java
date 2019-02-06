package walkingkooka.spreadsheet.security;

import org.junit.jupiter.api.Test;

public final class GroupIdTest extends IdentityIdTestCase<GroupId> {

    @Test
    public void testToString() {
        this.toStringAndCheck(GroupId.with(123).toString(), "123");
    }

    @Override
    public Class<GroupId> type() {
        return GroupId.class;
    }
}

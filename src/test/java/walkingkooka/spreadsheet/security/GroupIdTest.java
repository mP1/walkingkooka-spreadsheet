package walkingkooka.spreadsheet.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class GroupIdTest extends IdentityIdTestCase<GroupId> {

    @Test
    public void testToString() {
        assertEquals("123", GroupId.with(123).toString());
    }

    @Override
    protected Class<GroupId> type() {
        return GroupId.class;
    }
}

package walkingkooka.spreadsheet.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class UserIdTest extends IdentityIdTestCase<UserId> {

    @Test
    public void testToString() {
        assertEquals("123", UserId.with(123).toString());
    }

    @Override
    protected Class<UserId> type() {
        return UserId.class;
    }
}

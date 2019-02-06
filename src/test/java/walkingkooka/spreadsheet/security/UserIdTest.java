package walkingkooka.spreadsheet.security;

import org.junit.jupiter.api.Test;

public final class UserIdTest extends IdentityIdTestCase<UserId> {

    @Test
    public void testToString() {
        this.toStringAndCheck(UserId.with(123), "123");
    }

    @Override
    public Class<UserId> type() {
        return UserId.class;
    }
}

package walkingkooka.spreadsheet.security;

import org.junit.Test;
import walkingkooka.net.email.EmailAddress;

public final class UserEqualityTest extends IdentityEqualityTestCase<User, UserId> {

    private final static EmailAddress EMAIL = EmailAddress.with("user@example.com");

    @Test
    public void testDifferentEmail() {
        this.checkNotEquals(User.with(UserId.with(ID_VALUE), EmailAddress.with("different@example.com")));
    }

    @Override
    User createObject(final UserId id) {
        return User.with(id, EMAIL);
    }

    @Override
    UserId createId(final long value) {
        return UserId.with(value);
    }
}

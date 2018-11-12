package walkingkooka.spreadsheet.security;

import org.junit.Test;
import walkingkooka.net.email.EmailAddress;

import static org.junit.Assert.assertEquals;

public final class UserTest extends IdentityTestCase<User, UserId> {

    private final static EmailAddress EMAIL = EmailAddress.with("user@example.com");

    @Test(expected = NullPointerException.class)
    public void testWithNullEmailFails() {
        User.with(UserId.with(1), null);
    }

    @Test
    public void testWith() {
        final UserId id = UserId.with(1);
        final User user = User.with(id, EMAIL);
        assertEquals("id", id, user.value());
        assertEquals("email", EMAIL, user.email());
    }

    @Test
    public void testToString() {
        assertEquals(EMAIL.toString(), User.with(UserId.with(1), EMAIL).toString());
    }

    @Override
    User createIdentity(final UserId id) {
        return User.with(id, EMAIL);
    }

    @Override
    UserId createId() {
        return UserId.with(1);
    }

    @Override
    protected Class<User> type() {
        return User.class;
    }
}

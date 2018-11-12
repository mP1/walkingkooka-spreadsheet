package walkingkooka.spreadsheet.security;

import walkingkooka.net.email.EmailAddress;

import java.util.Objects;

/**
 * A user in the system.
 */
public final class User extends Identity<UserId> {

    /**
     * Factory that creates a new {@link User}.
     */
    public static User with(final UserId id, final EmailAddress email) {
        checkId(id);
        Objects.requireNonNull(email, "email");

        return new User(id, email);
    }

    /**
     * Private ctor use factory.
     */
    private User(final UserId id, final EmailAddress email) {
        super(id);
        this.email = email;
    }

    public EmailAddress email() {
        return this.email;
    }

    private final EmailAddress email;

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof User;
    }

    @Override
    boolean equals1(final Identity<?> other) {
        return this.email.equals(User.class.cast(other).email);
    }

    @Override
    public String toString() {
        return this.email.toString();
    }
}

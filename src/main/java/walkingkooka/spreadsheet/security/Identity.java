package walkingkooka.spreadsheet.security;

import walkingkooka.Cast;
import walkingkooka.Value;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.test.HashCodeEqualsDefined;

import java.util.Objects;

/**
 * Base class for all security related identifies
 */
public abstract class Identity<I extends IdentityId> implements Value<I>, HashCodeEqualsDefined {

    /**
     * Factory that creates a new {@link Group}.
     */
    public static Group group(final GroupId id, final GroupName name) {
        return Group.with(id, name);
    }

    /**
     * Factory that creates a new {@link User}.
     */
    public static User user(final UserId id, final EmailAddress email) {
        return User.with(id, email);
    }

    static void checkId(final IdentityId id) {
        Objects.requireNonNull(id, "id");
    }

    /**
     * Package private to limit sub classing.
     */
    Identity(final I id) {
        super();
        this.id = id;
    }

    @Override
    public final I value() {
        return this.id;
    }

    private final I id;

    // Object.................................................

    @Override
    public final int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public final boolean equals(final Object other) {
        return this == other ||
                this.canBeEqual(other) &&
                        this.equals0(Cast.to(other));
    }

    /**
     * Sub classes should do an instanceof test.
     */
    abstract boolean canBeEqual(final Object other);

    private final boolean equals0(final Identity<?> other) {
        return this.id.equals(other.id) &&
                this.equals1(other);
    }

    abstract boolean equals1(final Identity<?> other);
}

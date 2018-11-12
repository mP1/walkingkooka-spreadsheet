package walkingkooka.spreadsheet.security;

/**
 * The primary key for a {@link User}.
 */
public final class UserId extends IdentityId {

    static UserId with(final long value) {
        return new UserId(value);
    }

    private UserId(final long value) {
        super(value);
    }

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof UserId;
    }
}

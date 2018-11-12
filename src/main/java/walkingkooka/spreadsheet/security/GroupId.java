package walkingkooka.spreadsheet.security;

/**
 * The primary key for a {@link Group}.
 */
public final class GroupId extends IdentityId {

    static GroupId with(final long value) {
        return new GroupId(value);
    }

    private GroupId(final long value) {
        super(value);
    }

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof GroupId;
    }
}

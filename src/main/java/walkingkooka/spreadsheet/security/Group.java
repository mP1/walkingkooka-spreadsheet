package walkingkooka.spreadsheet.security;

import java.util.Objects;

/**
 * A group defined in the system.
 */
public final class Group extends Identity<GroupId> {

    /**
     * Factory that creates a new {@link Group}.
     */
    public static Group with(final GroupId id, final GroupName name) {
        checkId(id);
        Objects.requireNonNull(name, "name");

        return new Group(id, name);
    }

    /**
     * Private ctor use factory.
     */
    private Group(final GroupId id, final GroupName name) {
        super(id);
        this.name = name;
    }

    public GroupName name() {
        return this.name;
    }

    private final GroupName name;

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof Group;
    }

    @Override
    boolean equals1(final Identity<?> other) {
        return this.name.equals(Group.class.cast(other).name);
    }

    @Override
    public String toString() {
        return this.name.toString();
    }
}

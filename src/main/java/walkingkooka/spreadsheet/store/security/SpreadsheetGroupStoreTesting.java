package walkingkooka.spreadsheet.store.security;

import walkingkooka.spreadsheet.security.Group;
import walkingkooka.spreadsheet.security.GroupId;
import walkingkooka.spreadsheet.security.GroupName;
import walkingkooka.spreadsheet.security.UserId;
import walkingkooka.spreadsheet.store.StoreTesting;
import walkingkooka.test.TypeNameTesting;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public interface SpreadsheetGroupStoreTesting<S extends SpreadsheetGroupStore> extends StoreTesting<S, GroupId, Group>,
        TypeNameTesting<S> {

    default void loadUserGroupsAndCheck(final SpreadsheetGroupStore store,
                                        final UserId userId,
                                        final Set<Group> groups) {
        assertEquals(groups,
                store.loadUserGroups(userId),
                "store loadUserGroups " + userId + " incorrect result");
    }

    // StoreTesting...........................................................

    @Override
    default GroupId id() {
        return GroupId.with(1);
    }

    @Override
    default Group value() {
        return Group.with(this.id(), GroupName.with("Group1"));
    }

    // TypeNameTesting..................................................................

    @Override
    default String typeNameSuffix() {
        return SpreadsheetGroupStore.class.getSimpleName();
    }
}

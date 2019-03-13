package walkingkooka.spreadsheet.store.security;

import walkingkooka.spreadsheet.security.Group;
import walkingkooka.spreadsheet.security.GroupId;
import walkingkooka.spreadsheet.security.User;
import walkingkooka.spreadsheet.security.UserId;
import walkingkooka.spreadsheet.store.Store;

import java.util.Set;

/**
 * A {@link Store} that holds all {@link Group groups} for a spreadsheet.
 */
public interface SpreadsheetGroupStore extends Store<GroupId, Group> {

    /**
     * Adds a user to an existing group.
     */
    void addUser(final UserId userId, final GroupId groupId);

    /**
     * Adds a user from an existing group.
     */
    void removeUser(final UserId userId, final GroupId groupId);

    /**
     * Loads all the groups for the provided {@link User}
     */
    Set<Group> loadUserGroups(final UserId userId);
}

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
     * Loads all the groups for the provided {@link User}
     */
    public Set<Group> loadUserGroups(final UserId id);
}

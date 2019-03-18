package walkingkooka.spreadsheet.store.security;

import walkingkooka.spreadsheet.security.Group;
import walkingkooka.spreadsheet.security.GroupId;
import walkingkooka.spreadsheet.security.UserId;
import walkingkooka.spreadsheet.store.FakeStore;

import java.util.Set;

public class FakeSpreadsheetGroupStore extends FakeStore<GroupId, Group> implements SpreadsheetGroupStore {

    @Override
    public void addUser(final UserId userId, final GroupId groupId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeUser(final UserId userId, final GroupId groupId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Group> loadUserGroups(final UserId userId) {
        throw new UnsupportedOperationException();
    }
}

package walkingkooka.spreadsheet.store.security;

import walkingkooka.spreadsheet.security.Group;
import walkingkooka.spreadsheet.security.GroupId;
import walkingkooka.spreadsheet.security.UserId;
import walkingkooka.spreadsheet.store.FakeStore;

import java.util.Set;

public class FakeSpreadsheetGroupStore extends FakeStore<Group, GroupId> implements SpreadsheetGroupStore {

    @Override
    public Set<Group> loadUserGroups(final UserId id) {
        throw new UnsupportedOperationException();
    }
}

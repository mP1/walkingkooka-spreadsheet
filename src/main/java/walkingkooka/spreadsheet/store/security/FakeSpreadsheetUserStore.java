package walkingkooka.spreadsheet.store.security;

import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.security.User;
import walkingkooka.spreadsheet.security.UserId;
import walkingkooka.spreadsheet.store.FakeStore;

public class FakeSpreadsheetUserStore extends FakeStore<User, UserId> implements SpreadsheetUserStore {

    @Override
    public User loadWithEmail(final EmailAddress email) {
        throw new UnsupportedOperationException();
    }
}

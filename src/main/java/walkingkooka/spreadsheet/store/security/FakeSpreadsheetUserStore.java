package walkingkooka.spreadsheet.store.security;

import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.security.User;
import walkingkooka.spreadsheet.security.UserId;
import walkingkooka.spreadsheet.store.FakeStore;

import java.util.Optional;

public class FakeSpreadsheetUserStore extends FakeStore<UserId, User> implements SpreadsheetUserStore {

    @Override
    public Optional<User> loadWithEmail(final EmailAddress email) {
        throw new UnsupportedOperationException();
    }
}

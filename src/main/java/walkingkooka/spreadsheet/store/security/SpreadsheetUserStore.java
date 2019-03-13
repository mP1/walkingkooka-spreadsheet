package walkingkooka.spreadsheet.store.security;

import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.security.User;
import walkingkooka.spreadsheet.security.UserId;
import walkingkooka.spreadsheet.store.Store;

import java.util.Optional;

/**
 * A {@link Store} that holds all users.
 */
public interface SpreadsheetUserStore extends Store<UserId, User> {

    /**
     * Loads the user with the provided {@link EmailAddress}
     */
    Optional<User> loadWithEmail(final EmailAddress email);
}

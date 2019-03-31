package walkingkooka.spreadsheet.hateos;

import walkingkooka.compare.Range;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.spreadsheet.security.User;
import walkingkooka.spreadsheet.security.UserId;
import walkingkooka.spreadsheet.store.security.SpreadsheetUserStore;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A {@link HateosHandler} that performs a {@link SpreadsheetUserStore#loadWithEmail(EmailAddress)}
 */
final class SpreadsheetUserStoreLoadWithEmailHateosHandler extends SpreadsheetStoreHateosHandler<UserId, User, SpreadsheetUserStore>
        implements HateosHandler<EmailAddress, User, User> {

    static SpreadsheetUserStoreLoadWithEmailHateosHandler with(final SpreadsheetUserStore store) {
        check(store);
        return new SpreadsheetUserStoreLoadWithEmailHateosHandler(store);
    }

    private SpreadsheetUserStoreLoadWithEmailHateosHandler(final SpreadsheetUserStore store) {
        super(store);
    }

    @Override
    public Optional<User> handle(final EmailAddress email,
                                 final Optional<User> user,
                                 final Map<HttpRequestAttribute<?>, Object> parameters) {
        checkId(email);
        checkResourceEmpty(user);
        checkParameters(parameters);

        return this.store.loadWithEmail(email);
    }

    @Override
    public List<User> handleCollection(final Range<EmailAddress> email,
                                       final List<User> users,
                                       final Map<HttpRequestAttribute<?>, Object> parameters) {
        checkIds(email);
        checkResourcesEmpty(users);
        checkParameters(parameters);

        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return SpreadsheetUserStore.class.getSimpleName() + ".loadWithEmail";
    }
}

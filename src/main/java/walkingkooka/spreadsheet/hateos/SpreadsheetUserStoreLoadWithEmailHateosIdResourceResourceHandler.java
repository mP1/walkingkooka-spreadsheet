package walkingkooka.spreadsheet.hateos;

import walkingkooka.net.email.EmailAddress;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosIdResourceResourceHandler;
import walkingkooka.spreadsheet.security.User;
import walkingkooka.spreadsheet.security.UserId;
import walkingkooka.spreadsheet.store.security.SpreadsheetUserStore;

import java.util.Map;
import java.util.Optional;

/**
 * A {@link HateosIdResourceResourceHandler} that performs a {@link SpreadsheetUserStore#loadWithEmail(EmailAddress)}
 */
final class SpreadsheetUserStoreLoadWithEmailHateosIdResourceResourceHandler extends SpreadsheetStoreHateosHandler<UserId, User, SpreadsheetUserStore>
        implements HateosIdResourceResourceHandler<EmailAddress, User, User> {

    static SpreadsheetUserStoreLoadWithEmailHateosIdResourceResourceHandler with(final SpreadsheetUserStore store) {
        check(store);
        return new SpreadsheetUserStoreLoadWithEmailHateosIdResourceResourceHandler(store);
    }

    private SpreadsheetUserStoreLoadWithEmailHateosIdResourceResourceHandler(final SpreadsheetUserStore store) {
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
    public String toString() {
        return SpreadsheetUserStore.class.getSimpleName() + ".loadWithEmail";
    }
}

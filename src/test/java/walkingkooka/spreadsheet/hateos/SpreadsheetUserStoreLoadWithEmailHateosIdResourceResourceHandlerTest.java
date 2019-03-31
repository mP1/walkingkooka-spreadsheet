package walkingkooka.spreadsheet.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosIdResourceResourceHandlerTesting;
import walkingkooka.spreadsheet.security.User;
import walkingkooka.spreadsheet.security.UserId;
import walkingkooka.spreadsheet.store.security.FakeSpreadsheetUserStore;
import walkingkooka.spreadsheet.store.security.SpreadsheetUserStore;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetUserStoreLoadWithEmailHateosIdResourceResourceHandlerTest extends SpreadsheetStoreHateosHandlerTestCase<SpreadsheetUserStoreLoadWithEmailHateosIdResourceResourceHandler,
        UserId,
        User,
        SpreadsheetUserStore>
        implements HateosIdResourceResourceHandlerTesting<SpreadsheetUserStoreLoadWithEmailHateosIdResourceResourceHandler, EmailAddress, User, User> {

    @Test
    public void testLoadWithEmail() {
        this.handleAndCheck(this.id(), this.resource(), this.parameters(), this.loaded());
    }

    @Override
    SpreadsheetUserStoreLoadWithEmailHateosIdResourceResourceHandler createHandler(final SpreadsheetUserStore store) {
        return SpreadsheetUserStoreLoadWithEmailHateosIdResourceResourceHandler.with(store);
    }

    @Override
    SpreadsheetUserStore store() {
        return new FakeSpreadsheetUserStore() {

            @Override
            public Optional<User> loadWithEmail(final EmailAddress email) {
                assertEquals(SpreadsheetUserStoreLoadWithEmailHateosIdResourceResourceHandlerTest.this.id(), email, "email");
                return SpreadsheetUserStoreLoadWithEmailHateosIdResourceResourceHandlerTest.this.loaded();
            }
        };
    }

    @Override
    String toStringExpectation() {
        return SpreadsheetUserStore.class.getSimpleName() + ".loadWithEmail";
    }

    private Optional<User> loaded() {
        return Optional.of(User.with(UserId.with(1), this.emailAddress()));
    }

    private EmailAddress emailAddress() {
        return EmailAddress.parse("user@example.com");
    }

    @Override
    public Map<HttpRequestAttribute<?>, Object> parameters() {
        return Maps.empty();
    }

    @Override
    public EmailAddress id() {
        return EmailAddress.parse("user@example.com");
    }

    @Override
    public Optional<User> resource() {
        return Optional.empty();
    }

    @Override
    public Class<SpreadsheetUserStoreLoadWithEmailHateosIdResourceResourceHandler> type() {
        return SpreadsheetUserStoreLoadWithEmailHateosIdResourceResourceHandler.class;
    }

    // TypeNamingTesting..................................................................................

    @Override
    public final String typeNamePrefix() {
        return SpreadsheetUserStore.class.getSimpleName();
    }
}

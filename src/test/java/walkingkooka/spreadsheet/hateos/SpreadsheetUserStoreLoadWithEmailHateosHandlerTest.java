package walkingkooka.spreadsheet.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.compare.Range;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandlerTesting;
import walkingkooka.spreadsheet.security.User;
import walkingkooka.spreadsheet.security.UserId;
import walkingkooka.spreadsheet.store.security.FakeSpreadsheetUserStore;
import walkingkooka.spreadsheet.store.security.SpreadsheetUserStore;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetUserStoreLoadWithEmailHateosHandlerTest extends SpreadsheetStoreHateosHandlerTestCase2<SpreadsheetUserStoreLoadWithEmailHateosHandler,
        EmailAddress,
        User,
        SpreadsheetUserStore>
        implements HateosHandlerTesting<SpreadsheetUserStoreLoadWithEmailHateosHandler, EmailAddress, User> {

    @Test
    public void testLoadWithEmail() {
        this.handleAndCheck(this.id(), this.resource(), this.parameters(), this.loaded());
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createHandler(), "SpreadsheetUserStore.loadWithEmail");
    }

    @Override
    SpreadsheetUserStoreLoadWithEmailHateosHandler createHandler(final SpreadsheetUserStore store) {
        return SpreadsheetUserStoreLoadWithEmailHateosHandler.with(store);
    }

    @Override
    SpreadsheetUserStore store() {
        return new FakeSpreadsheetUserStore() {

            @Override
            public Optional<User> loadWithEmail(final EmailAddress email) {
                assertEquals(SpreadsheetUserStoreLoadWithEmailHateosHandlerTest.this.id(), email, "email");
                return SpreadsheetUserStoreLoadWithEmailHateosHandlerTest.this.loaded();
            }
        };
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
    public Range<EmailAddress> collection() {
        return Range.all();
    }

    @Override
    public List<User> resourceCollection() {
        return Lists.empty();
    }

    @Override
    public Class<SpreadsheetUserStoreLoadWithEmailHateosHandler> type() {
        return SpreadsheetUserStoreLoadWithEmailHateosHandler.class;
    }

    // TypeNameTesting.......................................................

    @Override
    public String typeNamePrefix() {
        return SpreadsheetUserStore.class.getSimpleName();
    }
}

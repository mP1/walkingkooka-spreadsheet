package walkingkooka.spreadsheet.store.security;

import org.junit.jupiter.api.Test;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.security.User;
import walkingkooka.spreadsheet.security.UserId;
import walkingkooka.test.ToStringTesting;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class TreeMapSpreadsheetUserStoreTest implements SpreadsheetUserStoreTesting<TreeMapSpreadsheetUserStore>,
        ToStringTesting<TreeMapSpreadsheetUserStore> {

    @Test
    public void testLoad1() {
        this.loadAndCheck(this.createStore(), this.user2().id(), this.user2());
    }

    @Test
    public void testLoad2() {
        this.loadAndCheck(this.createStore(), this.user3().id(), this.user3());
    }

    @Test
    public void testSave() {
        final TreeMapSpreadsheetUserStore store = this.createStore();

        final User saved = User.with(UserId.with(2), EmailAddress.parse("saved@example.com"));
        store.save(saved);

        this.loadAndCheck(store, saved.id(), saved);
    }

    @Test
    public void testSaveReplaces() {
        final TreeMapSpreadsheetUserStore store = this.createStore();

        final User replace = User.with(this.user3().id(), EmailAddress.parse("replaced@example.com"));
        store.save(replace);

        this.loadAndCheck(store, replace.id(), replace);
    }

    @Test
    public void testDelete() {
        final TreeMapSpreadsheetUserStore store = this.createStore();

        final User user1 = this.user1();
        store.delete(user1.id());

        this.loadFailCheck(store, user1.id());
    }

    @Test
    public void testCount() {
        this.countAndCheck(this.createStore(), 3);
    }

    @Test
    public void testCountAfterSave() {
        final TreeMapSpreadsheetUserStore store = this.createStore();

        store.save(User.with(UserId.with(999), EmailAddress.parse("saved@example.com")));

        this.countAndCheck(store, 3 + 1);
    }

    @Test
    public void testLoadWithEmail() {
        final TreeMapSpreadsheetUserStore store = this.createStore();

        assertEquals(Optional.of(this.user1()), store.loadWithEmail(this.user1().email()));
    }

    @Test
    public void testToString() {
        final TreeMapSpreadsheetUserStore store = createStore();
        this.toStringAndCheck(store, store.userIdToUser.toString());
    }

    // helpers........................................................................................

    private User user1() {
        return User.with(UserId.with(1), EmailAddress.parse("user1@example.com"));
    }

    private User user2() {
        return User.with(UserId.with(2), EmailAddress.parse("user2@example.com"));
    }

    private User user3() {
        return User.with(UserId.with(333), EmailAddress.parse("user3@example.com"));
    }

    // SpreadsheetUserStoreTesting............................................................................

    @Override
    public TreeMapSpreadsheetUserStore createStore() {
        final TreeMapSpreadsheetUserStore store = TreeMapSpreadsheetUserStore.with();

        store.userIdToUser.put(this.user1().id(), this.user1());
        store.userIdToUser.put(this.user2().id(), this.user2());
        store.userIdToUser.put(this.user3().id(), this.user3());

        return store;
    }

    // ClassTesting..........................................................................................

    @Override
    public Class<TreeMapSpreadsheetUserStore> type() {
        return TreeMapSpreadsheetUserStore.class;
    }
}

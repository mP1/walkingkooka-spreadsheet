/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package walkingkooka.spreadsheet.security.store;

import org.junit.jupiter.api.Test;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.security.User;
import walkingkooka.spreadsheet.security.UserId;

import java.util.Optional;
import java.util.TreeMap;

public final class TreeMapSpreadsheetUserStoreTest implements SpreadsheetUserStoreTesting<TreeMapSpreadsheetUserStore> {

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void testLoad1() {
        this.loadAndCheck(this.createNotEmptyStore(), this.user2().id().get(), this.user2());
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void testLoad2() {
        this.loadAndCheck(this.createNotEmptyStore(), this.user3().id().get(), this.user3());
    }

    @Test
    public void testSave() {
        final TreeMapSpreadsheetUserStore store = this.createNotEmptyStore();

        final Optional<UserId> id = Optional.of(UserId.with(2));
        final User saved = User.with(id, EmailAddress.parse("saved@example.com"));
        store.save(saved);

        this.loadAndCheck(store, id.get(), saved);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void testSaveReplaces() {
        final TreeMapSpreadsheetUserStore store = this.createNotEmptyStore();

        final Optional<UserId> id = this.userId3();
        final User replace = User.with(id, EmailAddress.parse("replaced@example.com"));
        store.save(replace);

        this.loadAndCheck(store, id.get(), replace);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void testDelete() {
        final TreeMapSpreadsheetUserStore store = this.createNotEmptyStore();

        final UserId id = this.userId1().get();
        store.delete(id);

        this.loadAndCheck(
            store,
            id
        );
    }

    @Test
    public void testCount() {
        this.countAndCheck(this.createNotEmptyStore(), 3);
    }

    @Test
    public void testCountAfterSave() {
        final TreeMapSpreadsheetUserStore store = this.createNotEmptyStore();

        store.save(User.with(Optional.of(UserId.with(999)), EmailAddress.parse("saved@example.com")));

        this.countAndCheck(store, 3 + 1);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void testIds() {
        final TreeMapSpreadsheetUserStore store = this.createStore();

        final User a = this.user1();
        final User b = this.user2();
        final User c = this.user3();

        store.save(a);
        store.save(b);
        store.save(c);

        this.idsAndCheck(store,
            0,
            3,
            a.id().get(), b.id().get(), c.id().get());
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void testIdsWindow() {
        final TreeMapSpreadsheetUserStore store = this.createStore();

        final User a = this.user1();
        final User b = this.user2();
        final User c = this.user3();
        final User d = this.user4();

        store.save(a);
        store.save(b);
        store.save(c);
        store.save(d);

        this.idsAndCheck(store,
            1,
            2,
            b.id().get(), c.id().get());
    }

    @Test
    public void testValues() {
        final TreeMapSpreadsheetUserStore store = this.createStore();

        final User a = this.user1();
        final User b = this.user2();
        final User c = this.user3();

        store.save(a);
        store.save(b);
        store.save(c);

        this.valuesAndCheck(
            store,
            0,
            3,
            a,
            b,
            c
        );
    }

    @Test
    public void testValuesWindow() {
        final TreeMapSpreadsheetUserStore store = this.createStore();

        final User a = this.user1();
        final User b = this.user2();
        final User c = this.user3();
        final User d = this.user4();

        store.save(a);
        store.save(b);
        store.save(c);
        store.save(d);

        this.valuesAndCheck(
            store,
            1,
            2,
            b,
            c
        );
    }

    @Test
    public void testLoadWithEmail() {
        final TreeMapSpreadsheetUserStore store = this.createNotEmptyStore();

        this.checkEquals(Optional.of(this.user1()), store.loadWithEmail(this.user1().email()));
    }

    @Test
    public void testToString() {
        final TreeMapSpreadsheetUserStore store = createNotEmptyStore();
        this.toStringAndCheck(store, store.store.toString());
    }

    // helpers........................................................................................

    private User user1() {
        return User.with(this.userId1(), EmailAddress.parse("user1@example.com"));
    }

    private User user2() {
        return User.with(this.userId2(), EmailAddress.parse("user2@example.com"));
    }

    private User user3() {
        return User.with(this.userId3(), EmailAddress.parse("user3@example.com"));
    }

    private User user4() {
        return User.with(this.userId4(), EmailAddress.parse("user4@example.com"));
    }

    private Optional<UserId> userId1() {
        return Optional.of(UserId.with(1));
    }

    private Optional<UserId> userId2() {
        return Optional.of(UserId.with(2));
    }

    private Optional<UserId> userId3() {
        return Optional.of(UserId.with(33));
    }

    private Optional<UserId> userId4() {
        return Optional.of(UserId.with(444));
    }

    // SpreadsheetUserStoreTesting............................................................................

    @Override
    public TreeMapSpreadsheetUserStore createStore() {
        return TreeMapSpreadsheetUserStore.with();
    }

    private TreeMapSpreadsheetUserStore createNotEmptyStore() {
        final TreeMapSpreadsheetUserStore store = this.createStore();

        store.store.save(this.user1());
        store.store.save(this.user2());
        store.store.save(this.user3());

        return store;
    }

    // ClassTesting..........................................................................................

    @Override
    public String typeNamePrefix() {
        return TreeMap.class.getSimpleName();
    }

    @Override
    public Class<TreeMapSpreadsheetUserStore> type() {
        return TreeMapSpreadsheetUserStore.class;
    }
}

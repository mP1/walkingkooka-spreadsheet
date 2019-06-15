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

package walkingkooka.spreadsheet.store.security;

import org.junit.jupiter.api.Test;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.security.User;
import walkingkooka.spreadsheet.security.UserId;

import java.util.Optional;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class TreeMapSpreadsheetUserStoreTest implements SpreadsheetUserStoreTesting<TreeMapSpreadsheetUserStore> {

    @Test
    public void testLoad1() {
        this.loadAndCheck(this.createNotEmptyStore(), this.user2().id(), this.user2());
    }

    @Test
    public void testLoad2() {
        this.loadAndCheck(this.createNotEmptyStore(), this.user3().id(), this.user3());
    }

    @Test
    public void testSave() {
        final TreeMapSpreadsheetUserStore store = this.createNotEmptyStore();

        final User saved = User.with(UserId.with(2), EmailAddress.parse("saved@example.com"));
        store.save(saved);

        this.loadAndCheck(store, saved.id(), saved);
    }

    @Test
    public void testSaveReplaces() {
        final TreeMapSpreadsheetUserStore store = this.createNotEmptyStore();

        final User replace = User.with(this.user3().id(), EmailAddress.parse("replaced@example.com"));
        store.save(replace);

        this.loadAndCheck(store, replace.id(), replace);
    }

    @Test
    public void testDelete() {
        final TreeMapSpreadsheetUserStore store = this.createNotEmptyStore();

        final User user1 = this.user1();
        store.delete(user1.id());

        this.loadFailCheck(store, user1.id());
    }

    @Test
    public void testCount() {
        this.countAndCheck(this.createNotEmptyStore(), 3);
    }

    @Test
    public void testCountAfterSave() {
        final TreeMapSpreadsheetUserStore store = this.createNotEmptyStore();

        store.save(User.with(UserId.with(999), EmailAddress.parse("saved@example.com")));

        this.countAndCheck(store, 3 + 1);
    }

    @Test
    public final void testIds() {
        final TreeMapSpreadsheetUserStore store = this.createStore();

        final User a = this.user1();
        final User b = this.user2();
        final User c = this.user3();

        store.save(a);
        store.save(b);
        store.save(c);

        this.idsAndCheck(store, 0, 3, a.id(), b.id(), c.id());
    }

    @Test
    public final void testIdsWindow() {
        final TreeMapSpreadsheetUserStore store = this.createStore();

        final User a = this.user1();
        final User b = this.user2();
        final User c = this.user3();
        final User d = this.user4();

        store.save(a);
        store.save(b);
        store.save(c);
        store.save(d);

        this.idsAndCheck(store, 1, 2, b.id(), c.id());
    }

    @Test
    public final void testValues() {
        final TreeMapSpreadsheetUserStore store = this.createStore();

        final User a = this.user1();
        final User b = this.user2();
        final User c = this.user3();

        store.save(a);
        store.save(b);
        store.save(c);

        this.valuesAndCheck(store, a.id(), 3, a, b, c);
    }

    @Test
    public final void testValuesWindow() {
        final TreeMapSpreadsheetUserStore store = this.createStore();

        final User a = this.user1();
        final User b = this.user2();
        final User c = this.user3();
        final User d = this.user4();

        store.save(a);
        store.save(b);
        store.save(c);
        store.save(d);

        this.valuesAndCheck(store, b.id(), 2, b, c);
    }

    @Test
    public void testLoadWithEmail() {
        final TreeMapSpreadsheetUserStore store = this.createNotEmptyStore();

        assertEquals(Optional.of(this.user1()), store.loadWithEmail(this.user1().email()));
    }

    @Test
    public void testToString() {
        final TreeMapSpreadsheetUserStore store = createNotEmptyStore();
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
        return User.with(UserId.with(333), EmailAddress.parse("user333@example.com"));
    }

    private User user4() {
        return User.with(UserId.with(444), EmailAddress.parse("user444@example.com"));
    }

    // SpreadsheetUserStoreTesting............................................................................

    @Override
    public TreeMapSpreadsheetUserStore createStore() {
        return TreeMapSpreadsheetUserStore.with();
    }

    private TreeMapSpreadsheetUserStore createNotEmptyStore() {
        final TreeMapSpreadsheetUserStore store = this.createStore();

        store.userIdToUser.put(this.user1().id(), this.user1());
        store.userIdToUser.put(this.user2().id(), this.user2());
        store.userIdToUser.put(this.user3().id(), this.user3());

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

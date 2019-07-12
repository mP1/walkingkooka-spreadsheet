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

package walkingkooka.spreadsheet.store;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.security.User;
import walkingkooka.spreadsheet.security.UserId;
import walkingkooka.test.TypeNameTesting;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class TreeMapSpreadsheetStoreTest implements SpreadsheetStoreTesting<TreeMapSpreadsheetStore<UserId, User>, UserId, User>,
        TypeNameTesting<TreeMapSpreadsheetStore<UserId, User>> {

    @Test
    public void testWithNullIdComparatorFails() {
        this.withFails(null, this::userFactory);
    }

    @Test
    public void testWithNullValueWithIdFactoryFails() {
        this.withFails(Comparator.naturalOrder(), null);
    }

    private void withFails(final Comparator<UserId> idComparator,
                           final BiFunction<Long, User, User> valueWithIdFactory) {
        assertThrows(NullPointerException.class, () -> {
            TreeMapSpreadsheetStore.with(idComparator, valueWithIdFactory);
        });
    }

    @Test
    public void testLoad1() {
        this.loadAndCheck(this.createNotEmptyStore(), this.user2().id().get(), this.user2());
    }

    @Test
    public void testLoad2() {
        this.loadAndCheck(this.createNotEmptyStore(), this.user3().id().get(), this.user3());
    }

    @Test
    public void testSaveWithId() {
        final TreeMapSpreadsheetStore<UserId, User> store = this.createNotEmptyStore();

        final User saved = User.with(Optional.of(UserId.with(2)), EmailAddress.parse("saved@example.com"));
        store.save(saved);

        this.loadAndCheck(store, saved.id().get(), saved);
    }

    @Test
    public void testSaveReplaces() {
        final TreeMapSpreadsheetStore<UserId, User> store = this.createNotEmptyStore();

        final User replace = User.with(this.user3().id(), EmailAddress.parse("replaced@example.com"));
        store.save(replace);

        this.loadAndCheck(store, replace.id().get(), replace);
    }

    @Test
    public void testSaveWithoutIdStoreEmpty() {
        final TreeMapSpreadsheetStore<UserId, User> store = this.createStore();
        this.countAndCheck(store, 0);

        final EmailAddress email = EmailAddress.parse("saved@example.com");

        final User saved = store.save(User.with(Optional.empty(), email));
        assertEquals(User.with(Optional.of(UserId.with(1)), email), saved, "id");

        this.loadAndCheck(store, saved.id().get(), saved);
        this.countAndCheck(store, 1);
    }

    @Test
    public void testSaveWithoutId() {
        final TreeMapSpreadsheetStore<UserId, User> store = this.createNotEmptyStore();
        this.countAndCheck(store, 3);

        final EmailAddress email = EmailAddress.parse("saved@example.com");

        final User saved = store.save(User.with(Optional.empty(), email));
        assertEquals(User.with(Optional.of(UserId.with(334)), email), saved, "id");

        this.loadAndCheck(store, saved.id().get(), saved);
        this.countAndCheck(store, 4);
    }

    @Test
    public void testSaveWithoutId2() {
        final TreeMapSpreadsheetStore<UserId, User> store = this.createNotEmptyStore();
        this.countAndCheck(store, 3);

        final EmailAddress email = EmailAddress.parse("saved1@example.com");

        final User saved1 = store.save(User.with(Optional.empty(), email));
        assertEquals(User.with(Optional.of(UserId.with(334)), email), saved1, "id");

        final EmailAddress email2 = EmailAddress.parse("saved2@example.com");

        final User saved2 = store.save(User.with(Optional.empty(), email2));
        assertEquals(User.with(Optional.of(UserId.with(335)), email2), saved2, "id");

        this.loadAndCheck(store, saved2.id().get(), saved2);

        this.countAndCheck(store, 5);
    }

    @Test
    public void testDelete() {
        final TreeMapSpreadsheetStore<UserId, User> store = this.createNotEmptyStore();

        final User user1 = this.user1();
        store.delete(user1.id().get());

        this.loadFailCheck(store, user1.id().get());
    }

    @Test
    public void testCount() {
        this.countAndCheck(this.createNotEmptyStore(), 3);
    }

    @Test
    public void testCountAfterSave() {
        final TreeMapSpreadsheetStore<UserId, User> store = this.createNotEmptyStore();

        store.save(User.with(Optional.of(UserId.with(999)), EmailAddress.parse("saved@example.com")));

        this.countAndCheck(store, 3 + 1);
    }

    @Test
    public final void testIds() {
        final TreeMapSpreadsheetStore<UserId, User> store = this.createStore();

        final User a = this.user1();
        final User b = this.user2();
        final User c = this.user3();

        store.save(a);
        store.save(b);
        store.save(c);

        this.idsAndCheck2(store, 0, 3, a.id(), b.id(), c.id());
    }

    @Test
    public final void testIdsWindow() {
        final TreeMapSpreadsheetStore<UserId, User> store = this.createStore();

        final User a = this.user1();
        final User b = this.user2();
        final User c = this.user3();
        final User d = this.user4();

        store.save(a);
        store.save(b);
        store.save(c);
        store.save(d);

        this.idsAndCheck2(store, 1, 2, b.id(), c.id());
    }

    @Test
    public final void testValues() {
        final TreeMapSpreadsheetStore<UserId, User> store = this.createStore();

        final User a = this.user1();
        final User b = this.user2();
        final User c = this.user3();

        store.save(a);
        store.save(b);
        store.save(c);

        this.valuesAndCheck(store, a.id().get(), 3, a, b, c);
    }

    @Test
    public final void testValuesWindow() {
        final TreeMapSpreadsheetStore<UserId, User> store = this.createStore();

        final User a = this.user1();
        final User b = this.user2();
        final User c = this.user3();
        final User d = this.user4();

        store.save(a);
        store.save(b);
        store.save(c);
        store.save(d);

        this.valuesAndCheck(store, b.id().get(), 2, b, c);
    }

    @Test
    public void testToString() {
        final TreeMapSpreadsheetStore<UserId, User> store = createNotEmptyStore();
        this.toStringAndCheck(store, store.idToValue.values().toString());
    }

    // helpers..........................................................................................................

    private User user1() {
        return this.user(1, "user1@example.com");
    }

    private User user2() {
        return this.user(2, "user2@example.com");
    }

    private User user3() {
        return this.user(333, "user3@example.com");
    }

    private User user4() {
        return this.user(444, "user4@example.com");
    }

    private User user(final long value, final String email) {
        return User.with(Optional.of(UserId.with(value)), EmailAddress.parse(email));
    }

    // SpreadsheetStoreTesting..........................................................................................

    @Override
    public TreeMapSpreadsheetStore<UserId, User> createStore() {
        return TreeMapSpreadsheetStore.with(Comparator.naturalOrder(), this::userFactory);
    }

    final User userFactory(final Long value, final User user) {
        return User.with(Optional.of(UserId.with(value)), user.email());
    }

    private TreeMapSpreadsheetStore<UserId, User> createNotEmptyStore() {
        final TreeMapSpreadsheetStore<UserId, User> store = this.createStore();

        Arrays.asList(user1(), user2(), user3())
                .stream()
                .forEach(u -> {
                    store.idToValue.put(u.id().get(), u);
                });

        return store;
    }

    @Override
    public UserId id() {
        return this.value().id().get();
    }

    @Override
    public User value() {
        return this.user1();
    }

    private void idsAndCheck2(final TreeMapSpreadsheetStore<UserId, User> store,
                              final int from,
                              final int to,
                              final Optional<UserId>... ids) {
        this.idsAndCheck(store,
                from,
                to,
                Arrays.asList(ids).stream().map(i -> i.get()).collect(Collectors.toSet()));
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<TreeMapSpreadsheetStore<UserId, User>> type() {
        return Cast.to(TreeMapSpreadsheetStore.class);
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNamePrefix() {
        return TreeMap.class.getSimpleName();
    }

    @Override
    public String typeNameSuffix() {
        return SpreadsheetStore.class.getSimpleName();
    }
}

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

import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.security.User;
import walkingkooka.spreadsheet.security.UserId;
import walkingkooka.spreadsheet.store.SpreadsheetStore;
import walkingkooka.spreadsheet.store.SpreadsheetStores;
import walkingkooka.spreadsheet.store.Watchers;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * A {@link SpreadsheetUserStore} backed by a {@link SpreadsheetStores#treeMap(BiFunction)}.
 */
final class TreeMapSpreadsheetUserStore implements SpreadsheetUserStore {

    static TreeMapSpreadsheetUserStore with() {
        return new TreeMapSpreadsheetUserStore();
    }

    private TreeMapSpreadsheetUserStore() {
        super();

        this.store = SpreadsheetStores.treeMap(TreeMapSpreadsheetUserStore::createUser);
    }

    private static User createUser(final Long value, final User user) {
        return user.setId(Optional.of(UserId.with(value)));
    }

    @Override
    public Optional<User> load(final UserId id) {
        return this.store.load(id);
    }

    @Override
    public User save(final User user) {
        return this.store.save(user);
    }

    @Override
    public Runnable addSaveWatcher(final Consumer<User> saved) {
        return this.store.addSaveWatcher(saved);
    }

    @Override
    public void delete(final UserId id) {
        this.store.delete(id);
    }

    @Override
    public Runnable addDeleteWatcher(final Consumer<UserId> deleted) {
        return this.store.addDeleteWatcher(deleted);
    }

    @Override
    public int count() {
        return this.store.count();
    }

    @Override
    public Set<UserId> ids(final int from,
                           final int count) {
        return this.store.ids(from, count);
    }

    @Override
    public List<User> values(final UserId from,
                             final int count) {
        return this.store.values(from, count);
    }

    @Override
    public Optional<User> loadWithEmail(final EmailAddress email) {
        return this.store.values(UserId.with(0), Integer.MAX_VALUE)
                .stream()
                .filter(u -> email.equals(u.email()))
                .findFirst();
    }

    final SpreadsheetStore<UserId, User> store;

    @Override
    public String toString() {
        return this.store.toString();
    }
}

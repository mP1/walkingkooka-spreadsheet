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
import walkingkooka.spreadsheet.store.Watchers;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * A {@link SpreadsheetUserStore} backed by a {@link java.util.TreeMap}.
 */
final class TreeMapSpreadsheetUserStore implements SpreadsheetUserStore {

    static TreeMapSpreadsheetUserStore with() {
        return new TreeMapSpreadsheetUserStore();
    }

    private TreeMapSpreadsheetUserStore() {
        super();
    }

    @Override
    public Optional<User> load(final UserId id) {
        Objects.requireNonNull(id, "id");

        return Optional.ofNullable(this.userIdToUser.get(id));
    }

    @Override
    public User save(final User user) {
        Objects.requireNonNull(user, "user");

        if (false == user.equals(this.userIdToUser.put(user.id(), user))) {
            this.saveWatchers.accept(user);
        }

        return user;
    }

    @Override
    public Runnable addSaveWatcher(final Consumer<User> saved) {
        return this.saveWatchers.addWatcher(saved);
    }

    private final Watchers<User> saveWatchers = Watchers.create();

    @Override
    public void delete(final UserId id) {
        Objects.requireNonNull(id, "id");

        if (null != this.userIdToUser.remove(id)) {
            this.deleteWatchers.accept(id);
        }
    }

    @Override
    public Runnable addDeleteWatcher(final Consumer<UserId> deleted) {
        return this.deleteWatchers.addWatcher(deleted);
    }

    private final Watchers<UserId> deleteWatchers = Watchers.create();

    @Override
    public int count() {
        return this.userIdToUser.size();
    }

    @Override
    public Set<UserId> ids(final int from,
                           final int count) {
        SpreadsheetStore.checkFromAndTo(from, count);

        return this.userIdToUser.keySet()
                .stream()
                .skip(from)
                .limit(count)
                .collect(Collectors.toCollection(Sets::ordered));
    }

    @Override
    public List<User> values(final UserId from,
                             final int count) {
        SpreadsheetStore.checkFromAndToIds(from, count);

        return this.userIdToUser.entrySet()
                .stream()
                .filter(e -> e.getKey().compareTo(from) >= 0)
                .map(e -> e.getValue())
                .limit(count)
                .collect(Collectors.toCollection(Lists::array));
    }

    @Override
    public Optional<User> loadWithEmail(EmailAddress email) {
        return this.userIdToUser.values()
                .stream()
                .filter(u -> email.equals(u.email()))
                .findFirst();
    }

    // VisibleForTesting
    final Map<UserId, User> userIdToUser = Maps.sorted();

    @Override
    public String toString() {
        return this.userIdToUser.toString();
    }
}

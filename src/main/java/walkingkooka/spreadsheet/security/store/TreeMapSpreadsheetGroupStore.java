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

import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.security.Group;
import walkingkooka.spreadsheet.security.GroupId;
import walkingkooka.spreadsheet.security.UserId;
import walkingkooka.store.Store;
import walkingkooka.store.Stores;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * A {@link SpreadsheetGroupStore} backed by a {@link java.util.TreeMap}.
 */
final class TreeMapSpreadsheetGroupStore implements SpreadsheetGroupStore {

    static TreeMapSpreadsheetGroupStore with() {
        return new TreeMapSpreadsheetGroupStore();
    }

    private TreeMapSpreadsheetGroupStore() {
        super();
        this.store = Stores.treeMap(Comparator.naturalOrder(), TreeMapSpreadsheetGroupStore::groupSetId);
    }

    private static Group groupSetId(final GroupId id,
                                    final Group group) {
        return group.setId(Optional.of(GroupId.with(null != id ? id.value() + 1 : 1)));
    }

    @Override
    public Optional<Group> load(final GroupId id) {
        return this.store.load(id);
    }

    @Override
    public Group save(final Group group) {
        return this.store.save(group);
    }

    @Override
    public Runnable addSaveWatcher(final Consumer<Group> saved) {
        return this.store.addSaveWatcher(saved);
    }

    @Override
    public void delete(final GroupId id) {
        this.groupIdToUserIds.remove(id);
        this.store.delete(id);
    }

    @Override
    public Runnable addDeleteWatcher(final Consumer<GroupId> deleted) {
        return this.store.addDeleteWatcher(deleted);
    }

    @Override
    public int count() {
        return this.store.count();
    }

    @Override
    public Set<GroupId> ids(final int offset,
                            final int count) {
        return this.store.ids(
            offset,
            count
        );
    }

    @Override
    public List<Group> values(final int offset,
                              final int count) {
        return this.store.values(
            offset,
            count
        );
    }

    @Override
    public List<Group> between(final GroupId from,
                               final GroupId to) {
        return this.store.between(
            from,
            to
        );
    }

    @Override
    public void addUser(final UserId userId, final GroupId groupId) {
        Objects.requireNonNull(userId, "userId");
        Objects.requireNonNull(groupId, "groupId");

        final Map<GroupId, Set<UserId>> groupIdToUserIds = this.groupIdToUserIds;
        Set<UserId> users = groupIdToUserIds.get(groupId);
        //noinspection Java8MapApi
        if (null == users) {
            users = Sets.ordered();
            groupIdToUserIds.put(groupId, users);
        }
        users.add(userId);
    }

    @Override
    public void removeUser(final UserId userId, final GroupId groupId) {
        Objects.requireNonNull(userId, "userId");
        Objects.requireNonNull(groupId, "groupId");

        final Map<GroupId, Set<UserId>> groupIdToUserIds = this.groupIdToUserIds;
        Set<UserId> users = groupIdToUserIds.get(groupId);
        if (null != users) {
            users.remove(userId);

            if (users.isEmpty()) {
                groupIdToUserIds.remove(groupId);
            }
        }
    }

    @Override
    public Set<Group> loadUserGroups(final UserId userId) {
        final Set<Group> groups = Sets.ordered();

        for (Map.Entry<GroupId, Set<UserId>> groupIdAndUsers : this.groupIdToUserIds.entrySet()) {
            final Set<UserId> users = groupIdAndUsers.getValue();
            if (users.contains(userId)) {
                groups.add(this.store.loadOrFail(groupIdAndUsers.getKey()));
            }
        }

        return groups;
    }

    // VisibleForTesting
    final Store<GroupId, Group> store;

    private final Map<GroupId, Set<UserId>> groupIdToUserIds = Maps.sorted();

    @Override
    public String toString() {
        return this.store.toString();
    }
}

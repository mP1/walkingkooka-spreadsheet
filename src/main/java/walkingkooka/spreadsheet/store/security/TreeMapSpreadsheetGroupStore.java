package walkingkooka.spreadsheet.store.security;

import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.security.Group;
import walkingkooka.spreadsheet.security.GroupId;
import walkingkooka.spreadsheet.security.UserId;
import walkingkooka.spreadsheet.store.Store;
import walkingkooka.spreadsheet.store.Watchers;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * A {@link SpreadsheetGroupStore} backed by a {@link java.util.TreeMap}.
 */
final class TreeMapSpreadsheetGroupStore implements SpreadsheetGroupStore {

    static TreeMapSpreadsheetGroupStore with() {
        return new TreeMapSpreadsheetGroupStore();
    }

    private TreeMapSpreadsheetGroupStore() {
        super();
    }

    @Override
    public Optional<Group> load(final GroupId id) {
        Objects.requireNonNull(id, "id");

        return Optional.ofNullable(this.groupIdToGroup.get(id));
    }

    @Override
    public Group save(final Group group) {
        Objects.requireNonNull(group, "group");

        if (false == group.equals(this.groupIdToGroup.put(group.id(), group))) {
            this.saveWatchers.accept(group);
        }

        return group;
    }

    public Runnable addSaveWatcher(final Consumer<Group> saved) {
        return this.saveWatchers.addWatcher(saved);
    }

    private final Watchers<Group> saveWatchers = Watchers.create();

    @Override
    public void delete(final GroupId id) {
        Objects.requireNonNull(id, "id");

        if(null!=this.groupIdToGroup.remove(id)) {
            this.groupIdToUserIds.remove(id);
            this.deleteWatchers.accept(id);
        }
    }

    @Override
    public Runnable addDeleteWatcher(final Consumer<GroupId> deleted) {
        return this.deleteWatchers.addWatcher(deleted);
    }

    private final Watchers<GroupId> deleteWatchers = Watchers.create();

    @Override
    public int count() {
        return this.groupIdToGroup.size();
    }

    @Override
    public Set<GroupId> ids(final int from,
                            final int count) {
        Store.checkFromAndTo(from, count);

        return this.groupIdToGroup.keySet()
                .stream()
                .skip(from)
                .limit(count)
                .collect(Collectors.toCollection(Sets::ordered));
    }

    @Override
    public List<Group> values(final GroupId from,
                              final int count) {
        Store.checkFromAndToIds(from, count);

        return this.groupIdToGroup.entrySet()
                .stream()
                .filter(e -> e.getKey().compareTo(from) >= 0)
                .map(e -> e.getValue())
                .limit(count)
                .collect(Collectors.toCollection(Lists::array));
    }

    @Override
    public void addUser(final UserId userId, final GroupId groupId) {
        Objects.requireNonNull(userId, "userId");
        Objects.requireNonNull(groupId, "groupId");

        final Map<GroupId, Set<UserId>> groupIdToUserIds = this.groupIdToUserIds;
        Set<UserId> users = groupIdToUserIds.get(groupId);
        if(null==users) {
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
        if(null!=users) {
            users.remove(userId);

            if(users.isEmpty()) {
                groupIdToUserIds.remove(groupId);
            }
        }
    }

    @Override
    public Set<Group> loadUserGroups(final UserId userId) {
        final Set<Group> groups = Sets.ordered();

        for(Map.Entry<GroupId, Set<UserId>> groupIdAndUsers : this.groupIdToUserIds.entrySet()) {
            final Set<UserId> users = groupIdAndUsers.getValue();
            if(users.contains(userId)) {
                groups.add(this.groupIdToGroup.get(groupIdAndUsers.getKey()));
            }
        }

        return groups;
    }

    // VisibleForTesting
    final Map<GroupId, Group> groupIdToGroup = Maps.sorted();

    final Map<GroupId, Set<UserId>> groupIdToUserIds = Maps.sorted();

    @Override
    public String toString() {
        return this.groupIdToGroup.values().toString();
    }
}

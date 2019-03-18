package walkingkooka.spreadsheet.store.security;

import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.security.Group;
import walkingkooka.spreadsheet.security.GroupId;
import walkingkooka.spreadsheet.security.UserId;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

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
        this.groupIdToGroup.put(group.id(), group);
        return group;
    }

    @Override
    public void delete(final GroupId id) {
        Objects.requireNonNull(id, "id");

        if(null!=this.groupIdToGroup.remove(id)) {
            this.groupIdToUserIds.remove(id);
        }
    }

    @Override
    public int count() {
        return this.groupIdToGroup.size();
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
        return this.groupIdToGroup.toString();
    }
}

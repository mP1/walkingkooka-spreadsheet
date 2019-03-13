package walkingkooka.spreadsheet.store.security;

import walkingkooka.collect.map.Maps;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.security.User;
import walkingkooka.spreadsheet.security.UserId;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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
        this.userIdToUser.put(user.id(), user);
        return user;
    }

    @Override
    public void delete(final UserId id) {
        Objects.requireNonNull(id, "id");

        this.userIdToUser.remove(id);
    }

    @Override
    public int count() {
        return this.userIdToUser.size();
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

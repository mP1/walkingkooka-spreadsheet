package walkingkooka.spreadsheet.store.security;

import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.security.User;
import walkingkooka.spreadsheet.security.UserId;
import walkingkooka.spreadsheet.store.StoreTesting;

public interface SpreadsheetUserStoreTesting<S extends SpreadsheetUserStore> extends StoreTesting<S, UserId, User> {

    // StoreTesting...........................................................

    @Override
    default UserId id() {
        return UserId.with(1);
    }

    @Override
    default User value() {
        return User.with(this.id(), EmailAddress.parse("user@example.com"));
    }
}

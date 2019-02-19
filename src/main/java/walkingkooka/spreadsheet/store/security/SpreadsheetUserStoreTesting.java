package walkingkooka.spreadsheet.store.security;

import walkingkooka.spreadsheet.security.User;
import walkingkooka.spreadsheet.security.UserId;
import walkingkooka.spreadsheet.store.StoreTesting;

public interface SpreadsheetUserStoreTesting<S extends SpreadsheetUserStore> extends StoreTesting<S, UserId, User> {
}

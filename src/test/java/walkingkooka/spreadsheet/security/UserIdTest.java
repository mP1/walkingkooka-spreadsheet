package walkingkooka.spreadsheet.security;

import org.junit.jupiter.api.Test;
import walkingkooka.tree.json.JsonNode;

public final class UserIdTest extends IdentityIdTestCase<UserId> {

    @Test
    public void testToString() {
        this.toStringAndCheck(UserId.with(123), "123");
    }

    @Override
    UserId createId(final long value) {
        return UserId.with(value);
    }

    @Override
    public Class<UserId> type() {
        return UserId.class;
    }

    // HasJsonNodeTesting..................................................................

    @Override
    public final UserId fromJsonNode(final JsonNode from) {
        return UserId.fromJsonNode(from);
    }
}

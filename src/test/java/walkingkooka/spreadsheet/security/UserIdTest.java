package walkingkooka.spreadsheet.security;

import org.junit.jupiter.api.Test;
import walkingkooka.compare.ComparableTesting;
import walkingkooka.compare.ComparatorTesting;
import walkingkooka.tree.json.JsonNode;

public final class UserIdTest extends IdentityIdTestCase<UserId> implements ComparableTesting<UserId> {

    @Test
    public void testCompareLess() {
        this.compareToAndCheckLess(UserId.with(999));
    }

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

    // ComparableTesting....................................................................

    @Override
    public UserId createComparable() {
        return UserId.with(99);
    }

    // HasJsonNodeTesting..................................................................

    @Override
    public final UserId fromJsonNode(final JsonNode from) {
        return UserId.fromJsonNode(from);
    }
}

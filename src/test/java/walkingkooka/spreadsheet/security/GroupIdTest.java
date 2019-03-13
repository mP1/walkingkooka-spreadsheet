package walkingkooka.spreadsheet.security;

import org.junit.jupiter.api.Test;
import walkingkooka.compare.ComparableTesting;
import walkingkooka.net.header.Link;
import walkingkooka.tree.json.JsonNode;

public final class GroupIdTest extends IdentityIdTestCase<GroupId> implements ComparableTesting<GroupId> {

    @Test
    public void testCompareLess() {
        this.compareToAndCheckLess(GroupId.with(999));
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(GroupId.with(123).toString(), "123");
    }

    @Override
    GroupId createId(final long value) {
        return GroupId.with(value);
    }

    @Override
    public Class<GroupId> type() {
        return GroupId.class;
    }

    // ComparableTesting....................................................................

    @Override
    public GroupId createComparable() {
        return GroupId.with(99);
    }

    // HasJsonNodeTesting..................................................................

    @Override
    public final GroupId fromJsonNode(final JsonNode from) {
        return GroupId.fromJsonNode(from);
    }
}

package walkingkooka.spreadsheet.security;

import org.junit.jupiter.api.Test;
import walkingkooka.net.header.Link;
import walkingkooka.tree.json.JsonNode;

public final class GroupIdTest extends IdentityIdTestCase<GroupId> {

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

    // HasJsonNodeTesting..................................................................

    @Override
    public final GroupId fromJsonNode(final JsonNode from) {
        return GroupId.fromJsonNode(from);
    }
}

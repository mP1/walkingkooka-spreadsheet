package walkingkooka.spreadsheet.security;

import org.junit.jupiter.api.Test;
import walkingkooka.test.ClassTesting2;
import walkingkooka.test.ToStringTesting;
import walkingkooka.tree.json.HasJsonNode;
import walkingkooka.tree.json.HasJsonNodeTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.type.MemberVisibility;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class IdentityIdTestCase<I extends IdentityId> implements ClassTesting2<I>,
        HasJsonNodeTesting<I>,
        ToStringTesting<I> {

    IdentityIdTestCase() {
        super();
    }

    @Test
    public final void testCreate() {
        final Long value = 123L;
        final I id = this.createId(value);
        assertEquals(value, id.value(), "value");
    }

    @Test
    public final void testToJson() {
        this.toJsonNodeAndCheck(this.createId(123L), HasJsonNode.toJsonNodeObject(123L));
    }

    @Test
    public final void testToJsonRoundtrip() {
        this.toJsonNodeRoundTripTwiceAndCheck(this.createId(123L));
    }

    @Override
    public final MemberVisibility typeVisibility() {
        return MemberVisibility.PUBLIC;
    }

    abstract I createId(long value);

    @Override
    public I createHasJsonNode() {
        return this.createId(1L);
    }
}

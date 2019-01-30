package walkingkooka.spreadsheet.style;

import org.junit.Test;
import walkingkooka.collect.set.Sets;
import walkingkooka.compare.ComparableTesting;
import walkingkooka.test.ClassTestCase;
import walkingkooka.test.ConstantsTesting;
import walkingkooka.test.SerializationTesting;
import walkingkooka.tree.json.HasJsonNodeTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.type.MemberVisibility;

import java.util.Set;

import static org.junit.Assert.assertEquals;

public final class FontSizeTest extends ClassTestCase<FontSize>
        implements ComparableTesting<FontSize>,
        ConstantsTesting<FontSize>,
        SerializationTesting<FontSize>,
        HasJsonNodeTesting<FontSize> {

    private final static int VALUE = 10;

    @Test(expected = IllegalArgumentException.class)
    public void testWithNegativeValueFails() {
        FontSize.with(-1);
    }

    @Test
    public void testWith() {
        final Integer value = 10;
        final FontSize size = FontSize.with(value);
        assertEquals("value", value, size.value());
    }

    // HasJsonNode......................................................................................

    @Test
    public void testToJsonNode() {
        this.toJsonNodeAndCheck(this.createComparable(), JsonNode.number(VALUE));
    }

    // Object...........................................................................................

    @Test
    public void testToString() {
        assertEquals("10", FontSize.with(10).toString());
    }

    @Override
    public Set<FontSize> intentionalDuplicateConstants() {
        return Sets.empty();
    }

    @Override
    public FontSize createComparable() {
        return FontSize.with(VALUE);
    }

    @Override
    public FontSize serializableInstance() {
        return this.createComparable();
    }

    @Override
    public boolean serializableInstanceIsSingleton() {
        return false;
    }

    @Override
    protected MemberVisibility typeVisibility() {
        return MemberVisibility.PUBLIC;
    }

    @Override
    public Class<FontSize> type() {
        return FontSize.class;
    }
}

package walkingkooka.spreadsheet.style;


import org.junit.Test;
import walkingkooka.test.SerializationTestCase;

public final class FontSizeSerializationTest extends SerializationTestCase<FontSize> {

    @Test
    public void testSingleton() throws Exception {
        this.serializeSingletonAndCheck(FontSize.with(10));
    }

    @Override
    protected FontSize create() {
        return FontSize.with(20);
    }

    @Override
    protected boolean isSingleton() {
        return true;
    }

    @Override
    protected Class<FontSize> type() {
        return FontSize.class;
    }
}

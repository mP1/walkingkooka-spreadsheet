package walkingkooka.spreadsheet.style;

import walkingkooka.test.ClassTestCase;
import walkingkooka.type.MemberVisibility;

public final class TextWrappingTest extends ClassTestCase<TextWrapping> {

    @Override
    public Class<TextWrapping> type() {
        return TextWrapping.class;
    }

    @Override
    protected MemberVisibility typeVisibility() {
        return MemberVisibility.PUBLIC;
    }
}

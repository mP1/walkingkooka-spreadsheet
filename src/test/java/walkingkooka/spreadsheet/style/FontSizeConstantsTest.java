package walkingkooka.spreadsheet.style;

import walkingkooka.collect.set.Sets;
import walkingkooka.test.ConstantsTestCase;

import java.util.Set;

public final class FontSizeConstantsTest extends ConstantsTestCase<FontSize> {

    @Override
    protected Class<FontSize> type() {
        return FontSize.class;
    }

    @Override
    protected Set<FontSize> intentionalDuplicates() {
        return Sets.empty();
    }
}

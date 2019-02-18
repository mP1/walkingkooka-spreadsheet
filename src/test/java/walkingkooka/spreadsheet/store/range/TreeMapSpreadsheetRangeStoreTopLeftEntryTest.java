package walkingkooka.spreadsheet.store.range;

import walkingkooka.test.ClassTesting2;
import walkingkooka.type.MemberVisibility;

import java.util.Map;

public final class TreeMapSpreadsheetRangeStoreTopLeftEntryTest extends TreeMapSpreadsheetRangeStoreTestCase<TreeMapSpreadsheetRangeStoreTopLeftEntry>
        implements ClassTesting2<TreeMapSpreadsheetRangeStoreTopLeftEntry> {

    @Override
    public MemberVisibility typeVisibility() {
        return MemberVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public Class<TreeMapSpreadsheetRangeStoreTopLeftEntry> type() {
        return TreeMapSpreadsheetRangeStoreTopLeftEntry.class;
    }

    @Override
    public String typeNameSuffix() {
        return Map.Entry.class.getSimpleName();
    }
}

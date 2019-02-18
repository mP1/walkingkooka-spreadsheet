package walkingkooka.spreadsheet.store.range;

import walkingkooka.test.ClassTesting2;
import walkingkooka.type.MemberVisibility;

import java.util.Map;

public final class TreeMapSpreadsheetRangeStoreBottomRightEntryTest extends TreeMapSpreadsheetRangeStoreTestCase<TreeMapSpreadsheetRangeStoreBottomRightEntry>
        implements ClassTesting2<TreeMapSpreadsheetRangeStoreBottomRightEntry> {

    @Override
    public MemberVisibility typeVisibility() {
        return MemberVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public Class<TreeMapSpreadsheetRangeStoreBottomRightEntry> type() {
        return TreeMapSpreadsheetRangeStoreBottomRightEntry.class;
    }

    @Override
    public String typeNameSuffix() {
        return Map.Entry.class.getSimpleName();
    }
}

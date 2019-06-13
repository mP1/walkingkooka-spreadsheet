package walkingkooka.spreadsheet.store.range;

import walkingkooka.test.ClassTesting2;
import walkingkooka.type.JavaVisibility;

import java.util.Map;

public final class TreeMapSpreadsheetRangeStoreBottomRightEntryTest extends TreeMapSpreadsheetRangeStoreTestCase<TreeMapSpreadsheetRangeStoreBottomRightEntry>
        implements ClassTesting2<TreeMapSpreadsheetRangeStoreBottomRightEntry> {

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
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

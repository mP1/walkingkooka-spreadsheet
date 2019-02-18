package walkingkooka.spreadsheet.store.range;

import walkingkooka.test.ClassTesting2;
import walkingkooka.type.MemberVisibility;

import java.util.Map;

public final class TreeMapSpreadsheetRangeStoreEntryTest extends TreeMapSpreadsheetRangeStoreTestCase<TreeMapSpreadsheetRangeStoreEntry>
        implements ClassTesting2<TreeMapSpreadsheetRangeStoreEntry> {

    @Override
    public MemberVisibility typeVisibility() {
        return MemberVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public Class<TreeMapSpreadsheetRangeStoreEntry> type() {
        return TreeMapSpreadsheetRangeStoreEntry.class;
    }

    @Override
    public String typeNameSuffix() {
        return Map.Entry.class.getSimpleName();
    }
}

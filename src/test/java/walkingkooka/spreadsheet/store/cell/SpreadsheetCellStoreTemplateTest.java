package walkingkooka.spreadsheet.store.cell;

import walkingkooka.test.ClassTesting2;
import walkingkooka.type.MemberVisibility;

public final class SpreadsheetCellStoreTemplateTest implements ClassTesting2<SpreadsheetCellStoreTemplate> {
    @Override
    public Class<SpreadsheetCellStoreTemplate> type() {
        return SpreadsheetCellStoreTemplate.class;
    }

    @Override
    public MemberVisibility typeVisibility() {
        return MemberVisibility.PACKAGE_PRIVATE;
    }
}

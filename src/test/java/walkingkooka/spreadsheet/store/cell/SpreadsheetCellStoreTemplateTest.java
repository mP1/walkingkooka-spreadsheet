package walkingkooka.spreadsheet.store.cell;

import walkingkooka.test.ClassTestCase;
import walkingkooka.type.MemberVisibility;

public final class SpreadsheetCellStoreTemplateTest extends ClassTestCase<SpreadsheetCellStoreTemplate> {
    @Override
    public Class<SpreadsheetCellStoreTemplate> type() {
        return SpreadsheetCellStoreTemplate.class;
    }

    @Override
    protected MemberVisibility typeVisibility() {
        return MemberVisibility.PACKAGE_PRIVATE;
    }
}

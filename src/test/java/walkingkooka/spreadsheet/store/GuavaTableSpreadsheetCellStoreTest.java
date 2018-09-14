package walkingkooka.spreadsheet.store;

import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.Optional;

public final class GuavaTableSpreadsheetCellStoreTest extends SpreadsheetCellStoreTestCase<GuavaTableSpreadsheetCellStore> {
    @Override
    GuavaTableSpreadsheetCellStore createSpreadsheetCellStore() {
        return GuavaTableSpreadsheetCellStore.create();
    }

    @Override
    protected Class<GuavaTableSpreadsheetCellStore> type() {
        return GuavaTableSpreadsheetCellStore.class;
    }
}

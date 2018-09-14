package walkingkooka.spreadsheet.store;

import org.junit.Test;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import static org.junit.Assert.assertSame;

public final class BasicSpreadsheetCellStoreTest extends SpreadsheetCellStoreTestCase<BasicSpreadsheetCellStore> {

    @Override
    BasicSpreadsheetCellStore createSpreadsheetCellStore() {
        return BasicSpreadsheetCellStore.create();
    }

    @Override
    protected Class<BasicSpreadsheetCellStore> type() {
        return BasicSpreadsheetCellStore.class;
    }
}

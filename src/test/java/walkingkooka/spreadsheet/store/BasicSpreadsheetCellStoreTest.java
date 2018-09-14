package walkingkooka.spreadsheet.store;

import org.junit.Test;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import static org.junit.Assert.assertSame;

public final class BasicSpreadsheetCellStoreTest extends SpreadsheetCellStoreTestCase<BasicSpreadsheetCellStore> {

    @Test
    public void testLoadUnknown() {
        this.loadFailCheck(REFERENCE);
    }

    @Test
    public void testSaveAndLoad() {
        final BasicSpreadsheetCellStore store = this.createSpreadsheetCellStore();

        final SpreadsheetCellReference reference = this.cellReference(1, 2);
        final SpreadsheetCell cell = SpreadsheetCell.with(reference, SpreadsheetFormula.with("1+2"));
        store.save(cell);

        assertSame(cell, this.loadOrFail(store, reference));
    }

    @Test
    public void testSaveDeleteLoad() {
        final BasicSpreadsheetCellStore store = this.createSpreadsheetCellStore();

        final SpreadsheetCellReference reference = this.cellReference(1, 2);
        final SpreadsheetCell cell = SpreadsheetCell.with(reference, SpreadsheetFormula.with("1+2"));
        store.save(cell);
        store.delete(reference);

        this.loadFailCheck(store, reference);
    }

    @Override
    BasicSpreadsheetCellStore createSpreadsheetCellStore() {
        return BasicSpreadsheetCellStore.create();
    }

    @Override
    protected Class<BasicSpreadsheetCellStore> type() {
        return BasicSpreadsheetCellStore.class;
    }
}

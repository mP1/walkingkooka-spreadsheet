package walkingkooka.spreadsheet.store.cell;

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.style.SpreadsheetCellStyle;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.TreeMap;

final class TreeMapSpreadsheetCellStoreTest extends SpreadsheetCellStoreTestCase<TreeMapSpreadsheetCellStore> {

    @Test
    public void testToString() {
        final TreeMapSpreadsheetCellStore store = this.createStore();
        store.save(SpreadsheetCell.with(SpreadsheetCellReference.parse("A1"),
                SpreadsheetFormula.with("1+2"),
                SpreadsheetCellStyle.EMPTY));

        this.toStringAndCheck(store, "[A1=1+2]");
    }

    @Override
    public TreeMapSpreadsheetCellStore createStore() {
        return TreeMapSpreadsheetCellStore.create();
    }

    @Override
    public Class<TreeMapSpreadsheetCellStore> type() {
        return TreeMapSpreadsheetCellStore.class;
    }

    // TypeNameTesting..................................................................

    @Override
    public String typeNamePrefix() {
        return TreeMap.class.getSimpleName();
    }
}

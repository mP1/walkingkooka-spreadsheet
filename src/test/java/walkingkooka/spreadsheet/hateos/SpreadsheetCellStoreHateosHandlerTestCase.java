package walkingkooka.spreadsheet.hateos;

import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.store.Store;
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStore;
import walkingkooka.spreadsheet.style.SpreadsheetCellStyle;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

public abstract class SpreadsheetCellStoreHateosHandlerTestCase<H extends SpreadsheetStoreHateosHandler<I, R, S>,
        I extends Comparable<I>,
        R extends HateosResource<I>,
        S extends Store<?, ?>>
        extends SpreadsheetStoreHateosHandlerTestCase<H, I, R, S> {

    SpreadsheetCellStoreHateosHandlerTestCase() {
        super();
    }

    final SpreadsheetCell cell1() {
        return SpreadsheetCell.with(SpreadsheetCellReference.parse("ZZ1"), SpreadsheetFormula.with("1+2"), SpreadsheetCellStyle.EMPTY);
    }

    final SpreadsheetCell cell2() {
        return SpreadsheetCell.with(SpreadsheetCellReference.parse("ZZ2"), SpreadsheetFormula.with("3+4"), SpreadsheetCellStyle.EMPTY);
    }

    // TypeNamingTesting..................................................................................

    @Override
    public final String typeNamePrefix() {
        return SpreadsheetCellStore.class.getSimpleName();
    }
}

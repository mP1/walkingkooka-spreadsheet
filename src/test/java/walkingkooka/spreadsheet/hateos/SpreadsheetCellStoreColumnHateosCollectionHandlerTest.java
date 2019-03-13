package walkingkooka.spreadsheet.hateos;

import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.store.cell.FakeSpreadsheetCellStore;
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStore;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetCellStoreColumnHateosCollectionHandlerTest extends SpreadsheetCellStoreHateosCollecionHandlerTestCase<SpreadsheetCellStoreColumnHateosCollectionHandler> {

    private final static int COLUMN = 123;

    @Override
    SpreadsheetCellStoreColumnHateosCollectionHandler createHandler(final SpreadsheetCellStore store) {
        return SpreadsheetCellStoreColumnHateosCollectionHandler.with(store);
    }

    @Override
    SpreadsheetCellStore store() {
        return new FakeSpreadsheetCellStore() {
            @Override
            public Set<SpreadsheetCell> column(final int column) {
                assertEquals(COLUMN, column, "column");

                return Sets.of(cell1(), cell2());
            }
        };
    }

    @Override
    public Integer id() {
        return COLUMN;
    }

    @Override
    String toStringExpectation() {
        return "SpreadsheetCellStore.column";
    }

    @Override
    public Class<SpreadsheetCellStoreColumnHateosCollectionHandler> type() {
        return SpreadsheetCellStoreColumnHateosCollectionHandler.class;
    }
}

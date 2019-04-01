package walkingkooka.spreadsheet.hateos;

import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetRow;
import walkingkooka.spreadsheet.store.cell.FakeSpreadsheetCellStore;
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStore;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetReferenceKind;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRowReference;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetCellStoreRowHateosCollectionHandlerTest extends SpreadsheetCellStoreHateosCollecionHandlerTestCase<SpreadsheetCellStoreRowHateosCollectionHandler,
        SpreadsheetRowReference,
        SpreadsheetRow> {

    private final static SpreadsheetRowReference ROW = SpreadsheetReferenceKind.ABSOLUTE.row(123);

    @Override
    SpreadsheetCellStoreRowHateosCollectionHandler createHandler(final SpreadsheetCellStore store) {
        return SpreadsheetCellStoreRowHateosCollectionHandler.with(store);
    }

    @Override
    SpreadsheetCellStore store() {
        return new FakeSpreadsheetCellStore() {
            @Override
            public Set<SpreadsheetCell> row(final SpreadsheetRowReference row) {
                assertEquals(ROW, row, "row");

                return Sets.of(cell1(), cell2());
            }
        };
    }

    @Override
    public SpreadsheetRowReference id() {
        return ROW;
    }

    @Override
    String toStringExpectation() {
        return "SpreadsheetCellStore.row";
    }

    @Override
    public Class<SpreadsheetCellStoreRowHateosCollectionHandler> type() {
        return SpreadsheetCellStoreRowHateosCollectionHandler.class;
    }
}

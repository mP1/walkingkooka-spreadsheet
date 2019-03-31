package walkingkooka.spreadsheet.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.net.http.server.hateos.HateosIdResourceCollectionResourceCollectionHandlerTesting;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetRow;
import walkingkooka.spreadsheet.store.cell.FakeSpreadsheetCellStore;
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStore;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetReferenceKind;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRowReference;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetCellStoreRowHateosIdResourceCollectionResourceCollectionHandlerTest extends SpreadsheetCellStoreHateosHandlerTestCase<SpreadsheetCellStoreRowHateosIdResourceCollectionResourceCollectionHandler,
        SpreadsheetRowReference,
        SpreadsheetRow,
        SpreadsheetCellStore>
        implements HateosIdResourceCollectionResourceCollectionHandlerTesting<SpreadsheetCellStoreRowHateosIdResourceCollectionResourceCollectionHandler,
                SpreadsheetRowReference,
                SpreadsheetRow,
                SpreadsheetCell> {

    private final static SpreadsheetRowReference ROW = SpreadsheetReferenceKind.ABSOLUTE.row(123);

    @Test
    public void testHandle() {
        this.handleAndCheck(this.id(),
                this.resourceCollection(),
                this.parameters(),
                Lists.of(cell1(), cell2()));
    }

    @Override
    SpreadsheetCellStoreRowHateosIdResourceCollectionResourceCollectionHandler createHandler(final SpreadsheetCellStore store) {
        return SpreadsheetCellStoreRowHateosIdResourceCollectionResourceCollectionHandler.with(store);
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
    public List<SpreadsheetRow> resourceCollection() {
        return Lists.empty();
    }

    @Override
    public Map<HttpRequestAttribute<?>, Object> parameters() {
        return HateosHandler.NO_PARAMETERS;
    }

    @Override
    String toStringExpectation() {
        return SpreadsheetCellStore.class.getSimpleName() + ".row";
    }

    @Override
    public Class<SpreadsheetCellStoreRowHateosIdResourceCollectionResourceCollectionHandler> type() {
        return SpreadsheetCellStoreRowHateosIdResourceCollectionResourceCollectionHandler.class;
    }
}

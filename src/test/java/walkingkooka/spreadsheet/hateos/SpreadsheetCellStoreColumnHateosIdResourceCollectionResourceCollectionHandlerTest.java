package walkingkooka.spreadsheet.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.net.http.server.hateos.HateosIdResourceCollectionResourceCollectionHandlerTesting;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.spreadsheet.store.cell.FakeSpreadsheetCellStore;
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStore;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetReferenceKind;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetCellStoreColumnHateosIdResourceCollectionResourceCollectionHandlerTest extends SpreadsheetCellStoreHateosHandlerTestCase<SpreadsheetCellStoreColumnHateosIdResourceCollectionResourceCollectionHandler,
        SpreadsheetColumnReference,
        SpreadsheetColumn,
        SpreadsheetCellStore>
        implements HateosIdResourceCollectionResourceCollectionHandlerTesting<SpreadsheetCellStoreColumnHateosIdResourceCollectionResourceCollectionHandler,
        SpreadsheetColumnReference,
        SpreadsheetColumn,
        SpreadsheetCell> {

    private final static SpreadsheetColumnReference COLUMN = SpreadsheetReferenceKind.ABSOLUTE.column(123);

    @Test
    public void testHandle() {
        this.handleAndCheck(this.id(),
                this.resourceCollection(),
                this.parameters(),
                Lists.of(cell1(), cell2()));
    }

    @Override
    SpreadsheetCellStoreColumnHateosIdResourceCollectionResourceCollectionHandler createHandler(final SpreadsheetCellStore store) {
        return SpreadsheetCellStoreColumnHateosIdResourceCollectionResourceCollectionHandler.with(store);
    }

    @Override
    SpreadsheetCellStore store() {
        return new FakeSpreadsheetCellStore() {
            @Override
            public Set<SpreadsheetCell> column(final SpreadsheetColumnReference column) {
                assertEquals(COLUMN, column, "column");

                return Sets.of(cell1(), cell2());
            }
        };
    }

    @Override
    String toStringExpectation() {
        return SpreadsheetCellStore.class.getSimpleName() + ".column";
    }

    @Override
    public SpreadsheetColumnReference id() {
        return COLUMN;
    }

    @Override
    public List<SpreadsheetColumn> resourceCollection() {
        return Lists.empty();
    }

    @Override
    public Map<HttpRequestAttribute<?>, Object> parameters() {
        return HateosHandler.NO_PARAMETERS;
    }

    @Override
    public Class<SpreadsheetCellStoreColumnHateosIdResourceCollectionResourceCollectionHandler> type() {
        return SpreadsheetCellStoreColumnHateosIdResourceCollectionResourceCollectionHandler.class;
    }
}

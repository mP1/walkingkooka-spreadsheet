package walkingkooka.spreadsheet.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.compare.Range;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandlerTesting;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.store.cell.FakeSpreadsheetCellStore;
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStore;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetStoreDeleteHateosHandlerTest extends SpreadsheetStoreHateosHandlerTestCase2<SpreadsheetStoreDeleteHateosHandler<SpreadsheetCellReference,
        SpreadsheetCell,
        SpreadsheetCellStore>,
        SpreadsheetCellReference,
        SpreadsheetCell,
        SpreadsheetCellStore>
        implements HateosHandlerTesting<SpreadsheetStoreDeleteHateosHandler<SpreadsheetCellReference,
        SpreadsheetCell,
        SpreadsheetCellStore>, SpreadsheetCellReference, SpreadsheetCell> {

    @Test
    public void testHandleDeletes() {
        this.handleAndCheck(this.id(), this.resource(), this.parameters(), Optional.empty());
        assertEquals(true, this.deleted);
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createHandler(), "Store.delete");
    }

    @Override
    SpreadsheetStoreDeleteHateosHandler<SpreadsheetCellReference,
            SpreadsheetCell,
            SpreadsheetCellStore> createHandler(final SpreadsheetCellStore store) {
        return SpreadsheetStoreDeleteHateosHandler.with(store);
    }

    @Override
    SpreadsheetCellStore store() {
        this.deleted=false;
        return new FakeSpreadsheetCellStore() {
            @Override
            public void delete(SpreadsheetCellReference id) {
                SpreadsheetStoreDeleteHateosHandlerTest.this.deleted =true;
            }
        };
    }

    private boolean deleted;

    @Override
    public Map<HttpRequestAttribute<?>, Object> parameters() {
        return Maps.empty();
    }

    @Override
    public SpreadsheetCellReference id() {
        return SpreadsheetCellReference.parse("B2");
    }

    @Override
    public Optional<SpreadsheetCell> resource() {
        return Optional.empty();
    }

    @Override
    public Range<SpreadsheetCellReference> collection() {
        return Range.all();
    }

    @Override
    public List<SpreadsheetCell> resourceCollection() {
        return Lists.empty();
    }

    @Override
    public Class<SpreadsheetStoreDeleteHateosHandler<SpreadsheetCellReference,
            SpreadsheetCell,
            SpreadsheetCellStore>> type() {
        return Cast.to(SpreadsheetStoreDeleteHateosHandler.class);
    }
}

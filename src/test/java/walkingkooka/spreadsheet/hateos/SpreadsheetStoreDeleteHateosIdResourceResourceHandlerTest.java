package walkingkooka.spreadsheet.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosIdResourceResourceHandlerTesting;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.store.cell.FakeSpreadsheetCellStore;
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStore;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetStoreDeleteHateosIdResourceResourceHandlerTest extends SpreadsheetStoreHateosHandlerTestCase<SpreadsheetStoreDeleteHateosIdResourceResourceHandler<SpreadsheetCellReference,
        SpreadsheetCell,
        SpreadsheetCellStore>,
        SpreadsheetCellReference,
        SpreadsheetCell,
        SpreadsheetCellStore>
        implements HateosIdResourceResourceHandlerTesting<SpreadsheetStoreDeleteHateosIdResourceResourceHandler<SpreadsheetCellReference,
                        SpreadsheetCell,
                        SpreadsheetCellStore>,
                SpreadsheetCellReference,
                SpreadsheetCell,
                SpreadsheetCell> {

    @Test
    public void testHandleDeletes() {
        this.handleAndCheck(this.id(), this.resource(), this.parameters(), Optional.empty());
        assertEquals(true, this.deleted);
    }

    @Override
    SpreadsheetStoreDeleteHateosIdResourceResourceHandler<SpreadsheetCellReference,
                SpreadsheetCell,
                SpreadsheetCellStore> createHandler(final SpreadsheetCellStore store) {
        return SpreadsheetStoreDeleteHateosIdResourceResourceHandler.with(store);
    }

    @Override
    SpreadsheetCellStore store() {
        this.deleted=false;
        return new FakeSpreadsheetCellStore() {
            @Override
            public void delete(SpreadsheetCellReference id) {
                SpreadsheetStoreDeleteHateosIdResourceResourceHandlerTest.this.deleted =true;
            }
        };
    }

    @Override
    String toStringExpectation() {
        return "Store.delete";
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
    public Class<SpreadsheetStoreDeleteHateosIdResourceResourceHandler<SpreadsheetCellReference,
                SpreadsheetCell,
                SpreadsheetCellStore>> type() {
        return Cast.to(SpreadsheetStoreDeleteHateosIdResourceResourceHandler.class);
    }

    // TypeNamingTesting..................................................................................

    @Override
    public final String typeNamePrefix() {
        return "SpreadsheetStore";
    }
}

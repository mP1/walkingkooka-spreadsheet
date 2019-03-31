package walkingkooka.spreadsheet.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosIdResourceResourceHandlerTesting;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.store.cell.FakeSpreadsheetCellStore;
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStore;
import walkingkooka.spreadsheet.style.SpreadsheetCellStyle;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetStoreLoadHateosIdResourceResourceHandlerTest extends SpreadsheetStoreHateosHandlerTestCase<SpreadsheetStoreLoadHateosIdResourceResourceHandler<SpreadsheetCellReference,
        SpreadsheetCell,
        SpreadsheetCellStore>,
        SpreadsheetCellReference,
        SpreadsheetCell,
        SpreadsheetCellStore>
        implements HateosIdResourceResourceHandlerTesting<SpreadsheetStoreLoadHateosIdResourceResourceHandler<SpreadsheetCellReference,
                                SpreadsheetCell,
                                SpreadsheetCellStore>,
                        SpreadsheetCellReference,
                        SpreadsheetCell,
                        SpreadsheetCell> {

    @Test
    public void testLoad() {
        this.handleAndCheck(this.id(), this.resource(), this.parameters(), this.loaded());
    }

    @Override
    SpreadsheetStoreLoadHateosIdResourceResourceHandler<SpreadsheetCellReference,
                SpreadsheetCell,
                SpreadsheetCellStore> createHandler(final SpreadsheetCellStore store) {
        return SpreadsheetStoreLoadHateosIdResourceResourceHandler.with(store);
    }

    @Override
    SpreadsheetCellStore store() {
        return new FakeSpreadsheetCellStore() {

            @Override
            public Optional<SpreadsheetCell> load(final SpreadsheetCellReference id) {
                assertEquals(SpreadsheetStoreLoadHateosIdResourceResourceHandlerTest.this.id(), id, "id");
                return SpreadsheetStoreLoadHateosIdResourceResourceHandlerTest.this.loaded();
            }
        };
    }

    @Override
    String toStringExpectation() {
        return "Store.load";
    }

    private Optional<SpreadsheetCell> loaded() {
        return Optional.of(SpreadsheetCell.with(this.id(),
                SpreadsheetFormula.with("1"),
                SpreadsheetCellStyle.EMPTY));
    }

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
    public Class<SpreadsheetStoreLoadHateosIdResourceResourceHandler<SpreadsheetCellReference,
                SpreadsheetCell,
                SpreadsheetCellStore>> type() {
        return Cast.to(SpreadsheetStoreLoadHateosIdResourceResourceHandler.class);
    }

    // TypeNamingTesting..................................................................................

    @Override
    public final String typeNamePrefix() {
        return "SpreadsheetStore";
    }
}

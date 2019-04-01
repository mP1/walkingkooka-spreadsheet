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
import walkingkooka.spreadsheet.style.SpreadsheetTextStyle;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetStoreSaveHateosIdResourceResourceHandlerTest extends SpreadsheetStoreHateosHandlerTestCase<SpreadsheetStoreSaveHateosIdResourceResourceHandler<SpreadsheetCellReference,
        SpreadsheetCell,
        SpreadsheetCellStore>,
        SpreadsheetCellReference,
        SpreadsheetCell,
        SpreadsheetCellStore>
        implements HateosIdResourceResourceHandlerTesting<SpreadsheetStoreSaveHateosIdResourceResourceHandler<SpreadsheetCellReference,
                                SpreadsheetCell,
                                SpreadsheetCellStore>,
                SpreadsheetCellReference,
                SpreadsheetCell,
                SpreadsheetCell> {

    @Test
    public void testSave() {
        this.handleAndCheck(this.id(),
                this.resource(),
                this.parameters(),
                Optional.of(this.saved()));
    }

    @Override
    SpreadsheetStoreSaveHateosIdResourceResourceHandler<SpreadsheetCellReference,
                SpreadsheetCell,
                SpreadsheetCellStore> createHandler(final SpreadsheetCellStore store) {
        return SpreadsheetStoreSaveHateosIdResourceResourceHandler.with(store);
    }

    @Override
    SpreadsheetCellStore store() {
        return new FakeSpreadsheetCellStore() {

            @Override
            public SpreadsheetCell save(final SpreadsheetCell save) {
                assertEquals(SpreadsheetStoreSaveHateosIdResourceResourceHandlerTest.this.saving(), save, "saved");
                return SpreadsheetStoreSaveHateosIdResourceResourceHandlerTest.this.saved();
            }
        };
    }

    @Override
    String toStringExpectation() {
        return "Store.save";
    }

    private SpreadsheetCell saved() {
        return SpreadsheetCell.with(this.id(),
                SpreadsheetFormula.with("999"),
                SpreadsheetCellStyle.with(SpreadsheetTextStyle.EMPTY.setBold(SpreadsheetTextStyle.ITALICS)));
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
        return Optional.of(this.saving());
    }

    private SpreadsheetCell saving() {
        return SpreadsheetCell.with(this.id(),
                SpreadsheetFormula.with("1"),
                SpreadsheetCellStyle.with(SpreadsheetTextStyle.EMPTY.setBold(SpreadsheetTextStyle.BOLD)));
    }

    @Override
    public Class<SpreadsheetStoreSaveHateosIdResourceResourceHandler<SpreadsheetCellReference,
                SpreadsheetCell,
                SpreadsheetCellStore>> type() {
        return Cast.to(SpreadsheetStoreSaveHateosIdResourceResourceHandler.class);
    }

    // TypeNamingTesting..................................................................................

    @Override
    public final String typeNamePrefix() {
        return "SpreadsheetStore";
    }
}

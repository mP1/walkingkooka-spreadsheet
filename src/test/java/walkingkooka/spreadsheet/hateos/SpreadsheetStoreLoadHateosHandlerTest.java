package walkingkooka.spreadsheet.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.compare.Range;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.store.cell.FakeSpreadsheetCellStore;
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStore;
import walkingkooka.spreadsheet.style.SpreadsheetCellStyle;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetStoreLoadHateosHandlerTest extends SpreadsheetStoreHateosHandlerTestCase2<SpreadsheetStoreLoadHateosHandler<SpreadsheetCellReference,
        SpreadsheetCell,
        SpreadsheetCellStore>,
        SpreadsheetCellReference,
        SpreadsheetCell,
        SpreadsheetCellStore> {

    @Test
    public void testLoad() {
        this.handleAndCheck(this.id(), this.resource(), this.parameters(), this.loaded());
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createHandler(), "Store.load");
    }

    @Override
    SpreadsheetStoreLoadHateosHandler<SpreadsheetCellReference,
            SpreadsheetCell,
            SpreadsheetCellStore> createHandler(final SpreadsheetCellStore store) {
        return SpreadsheetStoreLoadHateosHandler.with(store);
    }

    @Override
    SpreadsheetCellStore store() {
        return new FakeSpreadsheetCellStore() {

            @Override
            public Optional<SpreadsheetCell> load(final SpreadsheetCellReference id) {
                assertEquals(SpreadsheetStoreLoadHateosHandlerTest.this.id(), id, "id");
                return SpreadsheetStoreLoadHateosHandlerTest.this.loaded();
            }
        };
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
    public Range<SpreadsheetCellReference> collection() {
        return Range.all();
    }

    @Override
    public List<SpreadsheetCell> resourceCollection() {
        return Lists.empty();
    }

    @Override
    public Class<SpreadsheetStoreLoadHateosHandler<SpreadsheetCellReference,
            SpreadsheetCell,
            SpreadsheetCellStore>> type() {
        return Cast.to(SpreadsheetStoreLoadHateosHandler.class);
    }
}

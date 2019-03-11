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
import walkingkooka.spreadsheet.style.SpreadsheetTextStyle;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetStoreSaveHateosHandlerTest extends SpreadsheetStoreHateosHandlerTestCase2<SpreadsheetStoreSaveHateosHandler<SpreadsheetCellReference,
        SpreadsheetCell,
        SpreadsheetCellStore>,
        SpreadsheetCellReference,
        SpreadsheetCell,
        SpreadsheetCellStore> {

    @Test
    public void testSave() {
        this.handleAndCheck(this.id(),
                this.resource(),
                this.parameters(),
                Optional.of(this.saved()));
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createHandler(), "Store.save");
    }

    @Override
    SpreadsheetStoreSaveHateosHandler<SpreadsheetCellReference,
            SpreadsheetCell,
            SpreadsheetCellStore> createHandler(final SpreadsheetCellStore store) {
        return SpreadsheetStoreSaveHateosHandler.with(store);
    }

    @Override
    SpreadsheetCellStore store() {
        return new FakeSpreadsheetCellStore() {

            @Override
            public SpreadsheetCell save(final SpreadsheetCell save) {
                assertEquals(SpreadsheetStoreSaveHateosHandlerTest.this.saving(), save, "saved");
                return SpreadsheetStoreSaveHateosHandlerTest.this.saved();
            }
        };
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
    public Range<SpreadsheetCellReference> collection() {
        return Range.all();
    }

    @Override
    public List<SpreadsheetCell> resourceCollection() {
        return Lists.empty();
    }

    @Override
    public Class<SpreadsheetStoreSaveHateosHandler<SpreadsheetCellReference,
            SpreadsheetCell,
            SpreadsheetCellStore>> type() {
        return Cast.to(SpreadsheetStoreSaveHateosHandler.class);
    }
}

package walkingkooka.spreadsheet.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosCollectionHandler;
import walkingkooka.net.http.server.hateos.HateosCollectionHandlerTesting;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStore;
import walkingkooka.spreadsheet.style.SpreadsheetCellStyle;
import walkingkooka.test.ToStringTesting;
import walkingkooka.test.TypeNameTesting;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetCellStoreHateosCollecionHandlerTestCase<H extends HateosCollectionHandler<Integer, SpreadsheetCell, SpreadsheetCell>>
        extends SpreadsheetStoreHateosHandlerTestCase<H>
        implements HateosCollectionHandlerTesting<H, Integer, SpreadsheetCell, SpreadsheetCell>,
        ToStringTesting<H>,
        TypeNameTesting<H> {

    SpreadsheetCellStoreHateosCollecionHandlerTestCase() {
        super();
    }

    @Test
    public final void testWithNullStoreFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createHandler(null);
        });
    }

    @Test
    public final void testToString() {
        this.toStringAndCheck(this.createHandler(), this.toStringExpectation());
    }

    abstract String toStringExpectation();

    public final H createHandler() {
        return this.createHandler(this.store());
    }

    abstract H createHandler(final SpreadsheetCellStore store);

    abstract SpreadsheetCellStore store();


    final SpreadsheetCell cell1() {
        return SpreadsheetCell.with(SpreadsheetCellReference.parse("ZZ1"), SpreadsheetFormula.with("1+2"), SpreadsheetCellStyle.EMPTY);
    }

    final SpreadsheetCell cell2() {
        return SpreadsheetCell.with(SpreadsheetCellReference.parse("ZZ2"), SpreadsheetFormula.with("3+4"), SpreadsheetCellStyle.EMPTY);
    }

    @Override
    public final Map<HttpRequestAttribute<?>, Object> parameters() {
        return HateosCollectionHandler.NO_PARAMETERS;
    }


    @Override
    public final List<SpreadsheetCell> resourceCollection() {
        return Lists.empty();
    }

    // TypeNameTesting...................................................................

    @Override
    public final String typeNamePrefix() {
        return SpreadsheetCellStore.class.getSimpleName();
    }

    @Override
    public final String typeNameSuffix() {
        return HateosCollectionHandler.class.getSimpleName();
    }
}

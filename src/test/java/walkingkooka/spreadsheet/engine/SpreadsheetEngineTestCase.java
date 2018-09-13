package walkingkooka.spreadsheet.engine;

import org.junit.*;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.test.*;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetReferenceKind;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public abstract class SpreadsheetEngineTestCase<E extends SpreadsheetEngine> extends PackagePrivateClassTestCase<E> {

    @Test(expected = NullPointerException.class)
    public final void testLoadNullCellsFails() {
        this.createSpreadsheetEngine().load(null, SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testLoadEmptyCellsFails() {
        this.createSpreadsheetEngine().load(Sets.empty(), SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY);
    }

    @Test(expected = NullPointerException.class)
    public final void testLoadNullLoadingFails() {
        this.createSpreadsheetEngine().load(Sets.of(SpreadsheetCellReference.with(SpreadsheetReferenceKind.ABSOLUTE.column(1), SpreadsheetReferenceKind.ABSOLUTE.row(2))),
                null);
    }

    @Test(expected = NullPointerException.class)
    public final void testSetNullCellFails() {
        this.createSpreadsheetEngine().set(null);
    }

    abstract E createSpreadsheetEngine();

    final void loadAndCheck(final Set<SpreadsheetCellReference> references, final SpreadsheetEngineLoading loading, final Object...values) {
        this.loadAndCheck(this.createSpreadsheetEngine(), references, loading, values);
    }

    final void loadAndCheck(final SpreadsheetEngine engine,
                            final Set<SpreadsheetCellReference> references,
                            final SpreadsheetEngineLoading loading,
                            final Object...values) {
        final Set<SpreadsheetCell> cells = engine.load(references, loading);
        assertEquals("cell count returned doesnt match expected value count=" + cells, values.length, cells.size());
        assertEquals("values from returned cells=" + cells,
                Lists.of(values),
                cells.stream().map(this::valueOrError).collect(Collectors.toList()));
    }

    private Object valueOrError(final SpreadsheetCell cell) {
        return cell.value()
                .map(v -> v)
                .orElse(cell.error()
                        .map(e -> e.value())
                        .orElse("Value and Error absent (" + cell + ")"));
    }
}

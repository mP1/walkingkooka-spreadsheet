package walkingkooka.spreadsheet.engine;

import org.junit.*;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.test.*;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetReferenceKind;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

public abstract class SpreadsheetEngineTestCase<E extends SpreadsheetEngine> extends PackagePrivateClassTestCase<E> {

    @Test(expected = NullPointerException.class)
    public final void testLoadNullCellFails() {
        this.createSpreadsheetEngine().load(null, SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY);
    }

    @Test(expected = NullPointerException.class)
    public final void testLoadNullLoadingFails() {
        this.createSpreadsheetEngine().load(SpreadsheetCellReference.with(SpreadsheetReferenceKind.ABSOLUTE.column(1), SpreadsheetReferenceKind.ABSOLUTE.row(2)),
                null);
    }

    @Test(expected = NullPointerException.class)
    public final void testSetNullCellFails() {
        this.createSpreadsheetEngine().save(null);
    }

    abstract E createSpreadsheetEngine();

    final SpreadsheetCell loadOrFail(final SpreadsheetEngine engine,
                                     final SpreadsheetCellReference reference,
                                     final SpreadsheetEngineLoading loading) {
        final Optional<SpreadsheetCell> cell = engine.load(reference, loading);
        if(!cell.isPresent()) {
            fail("Loading " + reference + " should have succeeded");
        }
        return cell.get();
    }

    final void loadFailCheck(final SpreadsheetCellReference reference,
                             final SpreadsheetEngineLoading loading) {
        this.loadFailCheck(this.createSpreadsheetEngine(), reference, loading);
    }

    final void loadFailCheck(final SpreadsheetEngine engine,
                             final SpreadsheetCellReference reference,
                             final SpreadsheetEngineLoading loading) {
        final Optional<SpreadsheetCell> cell = engine.load(reference, loading);
        assertEquals("Expected reference " + reference + " to fail", Optional.empty(), cell);
    }

    final void loadAndCheckWithoutValueOrError(final SpreadsheetCellReference reference, final SpreadsheetEngineLoading loading) {
        this.loadAndCheckWithoutValueOrError(this.createSpreadsheetEngine(), reference, loading);
    }

    final void loadAndCheckWithoutValueOrError(final SpreadsheetEngine engine,
                                               final SpreadsheetCellReference reference,
                                               final SpreadsheetEngineLoading loading) {
        final SpreadsheetCell cell = this.loadOrFail(engine, reference, loading);
        assertEquals("values from returned cells=" + cell,
                null,
                this.valueOrError(cell, null));
    }

    final void loadAndCheck(final SpreadsheetCellReference reference, final SpreadsheetEngineLoading loading, final Object value) {
        this.loadAndCheck(this.createSpreadsheetEngine(), reference, loading, value);
    }

    final void loadAndCheck(final SpreadsheetEngine engine,
                            final SpreadsheetCellReference reference,
                            final SpreadsheetEngineLoading loading,
                            final Object value) {
        final SpreadsheetCell cell = this.loadOrFail(engine, reference, loading);
        assertEquals("values from returned cells=" + cell,
                value,
                this.valueOrError(cell, "Value and Error absent (" + cell + ")"));
    }

    private Object valueOrError(final SpreadsheetCell cell, final Object bothAbsent) {
        return cell.value()
                .map(v -> v)
                .orElse(cell.error()
                        .map(e -> (Object)e.value())
                        .orElse(bothAbsent));
    }
}

package walkingkooka.spreadsheet.engine;

import org.junit.Test;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.test.PackagePrivateClassTestCase;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetReferenceKind;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public abstract class SpreadsheetEngineTestCase<E extends SpreadsheetEngine> extends PackagePrivateClassTestCase<E> {

    final static SpreadsheetCellReference REFERENCE = SpreadsheetReferenceKind.ABSOLUTE.column(1).setRow(SpreadsheetReferenceKind.ABSOLUTE.row(2));
    final static SpreadsheetLabelName LABEL = SpreadsheetLabelName.with("label123");

    @Test(expected = NullPointerException.class)
    public final void testLoadCellNullCellFails() {
        this.createSpreadsheetEngine().loadCell(null, SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY);
    }

    @Test(expected = NullPointerException.class)
    public final void testLoadCellNullLoadingFails() {
        this.createSpreadsheetEngine().loadCell(REFERENCE,
                null);
    }

    abstract E createSpreadsheetEngine();

    final SpreadsheetCell loadCellOrFail(final SpreadsheetEngine engine,
                                         final SpreadsheetCellReference reference,
                                         final SpreadsheetEngineLoading loading) {
        final Optional<SpreadsheetCell> cell = engine.loadCell(reference, loading);
        if(!cell.isPresent()) {
            fail("Loading " + reference + " should have succeeded");
        }
        return cell.get();
    }

    final void loadCellFailCheck(final SpreadsheetCellReference reference,
                                 final SpreadsheetEngineLoading loading) {
        this.loadCellFailCheck(this.createSpreadsheetEngine(), reference, loading);
    }

    final void loadCellFailCheck(final SpreadsheetEngine engine,
                                 final SpreadsheetCellReference reference,
                                 final SpreadsheetEngineLoading loading) {
        final Optional<SpreadsheetCell> cell = engine.loadCell(reference, loading);
        assertEquals("Expected reference " + reference + " to fail", Optional.empty(), cell);
    }

    final void loadCellAndCheckWithoutValueOrError(final SpreadsheetCellReference reference, final SpreadsheetEngineLoading loading) {
        this.loadCellAndCheckWithoutValueOrError(this.createSpreadsheetEngine(), reference, loading);
    }

    final void loadCellAndCheckWithoutValueOrError(final SpreadsheetEngine engine,
                                                   final SpreadsheetCellReference reference,
                                                   final SpreadsheetEngineLoading loading) {
        final SpreadsheetCell cell = this.loadCellOrFail(engine, reference, loading);
        assertEquals("values from returned cells=" + cell,
                null,
                this.valueOrError(cell, null));
    }

    final void loadCellAndCheck(final SpreadsheetCellReference reference, final SpreadsheetEngineLoading loading, final Object value) {
        this.loadCellAndCheck(this.createSpreadsheetEngine(), reference, loading, value);
    }

    final void loadCellAndCheck(final SpreadsheetEngine engine,
                                final SpreadsheetCellReference reference,
                                final SpreadsheetEngineLoading loading,
                                final Object value) {
        final SpreadsheetCell cell = this.loadCellOrFail(engine, reference, loading);
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

    final void loadCellAndCheckError(final SpreadsheetCellReference reference, final SpreadsheetEngineLoading loading, final String errorContains) {
        this.loadCellAndCheck(this.createSpreadsheetEngine(), reference, loading, errorContains);
    }

    final void loadCellAndCheckError(final SpreadsheetEngine engine,
                                final SpreadsheetCellReference reference,
                                final SpreadsheetEngineLoading loading,
                                final String errorContains) {
        final SpreadsheetCell cell = this.loadCellOrFail(engine, reference, loading);

        final Optional<SpreadsheetError> error = cell.error();
        assertNotEquals("Expected error missing=" + cell, SpreadsheetCell.NO_ERROR, error);
        assertTrue("Error message " + error + " missing " + CharSequences.quoteAndEscape(errorContains), error.get().value().contains(errorContains));
    }
}

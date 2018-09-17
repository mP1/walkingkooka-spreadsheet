package walkingkooka.spreadsheet.engine;

import org.junit.Test;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.store.Store;
import walkingkooka.spreadsheet.store.label.SpreadsheetLabelStore;
import walkingkooka.test.PackagePrivateClassTestCase;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetReferenceKind;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRowReference;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public abstract class SpreadsheetEngineTestCase<E extends SpreadsheetEngine> extends PackagePrivateClassTestCase<E> {

    final static SpreadsheetColumnReference COLUMN = SpreadsheetReferenceKind.ABSOLUTE.column(1);
    final static SpreadsheetRowReference ROW = SpreadsheetReferenceKind.ABSOLUTE.row(2);
    final static SpreadsheetCellReference REFERENCE = COLUMN.setRow(ROW);
    final static SpreadsheetLabelName LABEL = SpreadsheetLabelName.with("LABEL123");

    @Test(expected = NullPointerException.class)
    public final void testLoadCellNullCellFails() {
        this.createSpreadsheetEngine().loadCell(null, SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY);
    }

    @Test(expected = NullPointerException.class)
    public final void testLoadCellNullLoadingFails() {
        this.createSpreadsheetEngine().loadCell(REFERENCE,
                null);
    }

    @Test(expected = NullPointerException.class)
    public final void testDeleteColumnsNullColumnFails() {
        this.createSpreadsheetEngine().deleteColumns(null, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testDeleteColumnsNegativeCountFails() {
        this.createSpreadsheetEngine().deleteColumns(COLUMN, -1);
    }

    @Test(expected = NullPointerException.class)
    public final void testDeleteRowsNullRowFails() {
        this.createSpreadsheetEngine().deleteRows(null, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testDeleteRowsNegativeCountFails() {
        this.createSpreadsheetEngine().deleteRows(ROW, -1);
    }

    @Test(expected = NullPointerException.class)
    public final void testInsertColumnsNullColumnFails() {
        this.createSpreadsheetEngine().insertColumns(null, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testInsertColumnsNegativeCountFails() {
        this.createSpreadsheetEngine().insertColumns(COLUMN, -1);
    }

    @Test(expected = NullPointerException.class)
    public final void testInsertRowsNullRowFails() {
        this.createSpreadsheetEngine().insertRows(null, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testInsertRowsNegativeCountFails() {
        this.createSpreadsheetEngine().insertRows(ROW, -1);
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
                                 final SpreadsheetCellReference reference) {
        this.loadCellFailCheck(engine, reference, SpreadsheetEngineLoading.SKIP_EVALUATE);
    }

    final void loadCellFailCheck(final SpreadsheetEngine engine,
                                 final SpreadsheetCellReference reference,
                                 final SpreadsheetEngineLoading loading) {
        final Optional<SpreadsheetCell> cell = engine.loadCell(reference, loading);
        assertEquals("Expected reference " + reference + " to fail", Optional.empty(), cell);
    }

    final void loadCellAndCheckWithoutValueOrError(final SpreadsheetEngine engine,
                                                   final SpreadsheetCellReference reference,
                                                   final SpreadsheetEngineLoading loading) {
        final SpreadsheetCell cell = this.loadCellOrFail(engine, reference, loading);
        assertEquals("values from returned cells=" + cell,
                null,
                this.valueOrError(cell, null));
    }

    final void loadCellAndCheckFormula(final SpreadsheetEngine engine,
                                     final SpreadsheetCellReference reference,
                                     final SpreadsheetEngineLoading loading,
                                     final String formula) {
        final SpreadsheetCell cell = this.loadCellOrFail(engine, reference, loading);
        this.checkFormula(cell, formula);
    }

    final void loadCellAndCheckFormulaAndValue(final SpreadsheetEngine engine,
                                       final SpreadsheetCellReference reference,
                                       final SpreadsheetEngineLoading loading,
                                       final String formula,
                                       final Object value) {
        final SpreadsheetCell cell = this.loadCellOrFail(engine, reference, loading);
        this.checkFormula(cell, formula);
        this.checkValueOrError(cell, value);
    }

    final void loadCellAndCheckValue(final SpreadsheetEngine engine,
                                     final SpreadsheetCellReference reference,
                                     final SpreadsheetEngineLoading loading,
                                     final Object value) {
        final SpreadsheetCell cell = this.loadCellOrFail(engine, reference, loading);
        this.checkValueOrError(cell, value);
    }

    private void checkFormula(final SpreadsheetCell cell, final String formula) {
        assertEquals("formula from returned cell=" + cell,
                SpreadsheetFormula.with(formula),
                cell.formula());
    }

    private void checkValueOrError(final SpreadsheetCell cell, final Object value) {
        assertEquals("values from returned cell=" + cell,
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

    final void loadCellAndCheckError(final SpreadsheetEngine engine,
                                final SpreadsheetCellReference reference,
                                final SpreadsheetEngineLoading loading,
                                final String errorContains) {
        final SpreadsheetCell cell = this.loadCellOrFail(engine, reference, loading);

        final Optional<SpreadsheetError> error = cell.error();
        assertNotEquals("Expected error missing=" + cell, SpreadsheetCell.NO_ERROR, error);
        assertTrue("Error message " + error + " missing " + CharSequences.quoteAndEscape(errorContains), error.get().value().contains(errorContains));
    }

    final void loadLabelAndCheck(final SpreadsheetLabelStore labelStore,
                                 final SpreadsheetLabelName label,
                                 final SpreadsheetCellReference reference) {
        assertEquals("label loaded", Optional.of(SpreadsheetLabelMapping.with(label, reference)), labelStore.load(label));
    }

    final void loadLabelFailCheck(final SpreadsheetLabelStore labelStore,
                                 final SpreadsheetLabelName label) {
        assertEquals("label loaded failed", Optional.empty(), labelStore.load(label));
    }

    final void countAndCheck(final Store<?, ?> store, final int count) {
        assertEquals("record count in " + store, count, store.count());
    }
}

package walkingkooka.spreadsheet.engine;

import org.junit.Test;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.store.Store;
import walkingkooka.spreadsheet.store.label.SpreadsheetLabelStore;
import walkingkooka.test.ClassTestCase;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetReferenceKind;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRowReference;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.type.MemberVisibility;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public abstract class SpreadsheetEngineTestCase<E extends SpreadsheetEngine> extends ClassTestCase<E> {

    final static SpreadsheetColumnReference COLUMN = SpreadsheetReferenceKind.ABSOLUTE.column(1);
    final static SpreadsheetRowReference ROW = SpreadsheetReferenceKind.ABSOLUTE.row(2);
    final static SpreadsheetCellReference REFERENCE = COLUMN.setRow(ROW);
    final static SpreadsheetLabelName LABEL = SpreadsheetLabelName.with("LABEL123");

    @Test(expected = NullPointerException.class)
    public final void testLoadCellNullCellFails() {
        this.createSpreadsheetEngine().loadCell(null, SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY, this.createContext());
    }

    @Test(expected = NullPointerException.class)
    public final void testLoadCellNullLoadingFails() {
        this.createSpreadsheetEngine().loadCell(REFERENCE,
                null,
                this.createContext());
    }

    @Test(expected = NullPointerException.class)
    public final void testLoadCellNullContextFails() {
        this.createSpreadsheetEngine().loadCell(REFERENCE,
                SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                null);
    }

    @Test(expected = NullPointerException.class)
    public final void testDeleteColumnsNullColumnFails() {
        this.createSpreadsheetEngine().deleteColumns(null, 1, this.createContext());
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testDeleteColumnsNegativeCountFails() {
        this.createSpreadsheetEngine().deleteColumns(COLUMN, -1, this.createContext());
    }

    @Test(expected = NullPointerException.class)
    public final void testDeleteColumnsNullContextFails() {
        this.createSpreadsheetEngine().deleteColumns(COLUMN, 1, null);
    }

    @Test(expected = NullPointerException.class)
    public final void testDeleteRowsNullRowFails() {
        this.createSpreadsheetEngine().deleteRows(null, 1, this.createContext());
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testDeleteRowsNegativeCountFails() {
        this.createSpreadsheetEngine().deleteRows(ROW, -1, this.createContext());
    }

    @Test(expected = NullPointerException.class)
    public final void testDeleteRowsNullContextFails() {
        this.createSpreadsheetEngine().deleteRows(ROW, 1, null);
    }

    @Test(expected = NullPointerException.class)
    public final void testInsertColumnsNullColumnFails() {
        this.createSpreadsheetEngine().insertColumns(null, 1, this.createContext());
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testInsertColumnsNegativeCountFails() {
        this.createSpreadsheetEngine().insertColumns(COLUMN, -1, this.createContext());
    }

    @Test(expected = NullPointerException.class)
    public final void testInsertColumnsNullContextFails() {
        this.createSpreadsheetEngine().insertColumns(COLUMN, 1, null);
    }

    @Test(expected = NullPointerException.class)
    public final void testInsertRowsNullRowFails() {
        this.createSpreadsheetEngine().insertRows(null, 1, this.createContext());
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testInsertRowsNegativeCountFails() {
        this.createSpreadsheetEngine().insertRows(ROW, -1, this.createContext());
    }

    @Test(expected = NullPointerException.class)
    public final void testInsertRowsNullContextFails() {
        this.createSpreadsheetEngine().insertRows(ROW, 1, null);
    }

    abstract protected E createSpreadsheetEngine();

    abstract protected SpreadsheetEngineContext createContext();

    protected final SpreadsheetCell loadCellOrFail(final SpreadsheetEngine engine,
                                                   final SpreadsheetCellReference reference,
                                                   final SpreadsheetEngineLoading loading,
                                                   final SpreadsheetEngineContext context) {
        final Optional<SpreadsheetCell> cell = engine.loadCell(reference, loading, context);
        if (!cell.isPresent()) {
            fail("Loading " + reference + " should have succeeded");
        }
        return cell.get();
    }

    protected final void loadCellFailCheck(final SpreadsheetCellReference reference,
                                           final SpreadsheetEngineLoading loading) {
        this.loadCellFailCheck(reference, loading, this.createContext());
    }

    protected final void loadCellFailCheck(final SpreadsheetCellReference reference,
                                           final SpreadsheetEngineLoading loading,
                                           final SpreadsheetEngineContext context) {
        this.loadCellFailCheck(this.createSpreadsheetEngine(), reference, loading, context);
    }

    protected final void loadCellFailCheck(final SpreadsheetEngine engine,
                                           final SpreadsheetCellReference reference,
                                           final SpreadsheetEngineContext context) {
        this.loadCellFailCheck(engine, reference, SpreadsheetEngineLoading.SKIP_EVALUATE, context);
    }

    protected final void loadCellFailCheck(final SpreadsheetEngine engine,
                                           final SpreadsheetCellReference reference,
                                           final SpreadsheetEngineLoading loading,
                                           final SpreadsheetEngineContext context) {
        final Optional<SpreadsheetCell> cell = engine.loadCell(reference, loading, context);
        assertEquals("Expected reference " + reference + " to fail", Optional.empty(), cell);
    }

    protected final SpreadsheetCell loadCellAndCheckWithoutValueOrError(final SpreadsheetEngine engine,
                                                                        final SpreadsheetCellReference reference,
                                                                        final SpreadsheetEngineLoading loading,
                                                                        final SpreadsheetEngineContext context) {
        final SpreadsheetCell cell = this.loadCellOrFail(engine, reference, loading, context);
        assertEquals("values from returned cells=" + cell,
                null,
                this.valueOrError(cell, null));
        return cell;
    }

    protected final SpreadsheetCell loadCellAndCheckFormula(final SpreadsheetEngine engine,
                                                            final SpreadsheetCellReference reference,
                                                            final SpreadsheetEngineLoading loading,
                                                            final SpreadsheetEngineContext context,
                                                            final String formula) {
        final SpreadsheetCell cell = this.loadCellOrFail(engine, reference, loading, context);
        this.checkFormula(cell, formula);
        return cell;
    }

    protected final SpreadsheetCell loadCellAndCheckFormulaAndValue(final SpreadsheetEngine engine,
                                                                    final SpreadsheetCellReference reference,
                                                                    final SpreadsheetEngineLoading loading,
                                                                    final SpreadsheetEngineContext context,
                                                                    final String formula,
                                                                    final Object value) {
        final SpreadsheetCell cell = this.loadCellAndCheckFormula(engine, reference, loading, context, formula);
        this.checkValueOrError(cell, value);
        return cell;
    }

    protected final SpreadsheetCell loadCellAndCheckValue(final SpreadsheetEngine engine,
                                                          final SpreadsheetCellReference reference,
                                                          final SpreadsheetEngineLoading loading,
                                                          final SpreadsheetEngineContext context,
                                                          final Object value) {
        final SpreadsheetCell cell = this.loadCellOrFail(engine, reference, loading, context);
        this.checkValueOrError(cell, value);
        return cell;
    }

    protected final SpreadsheetCell loadCellAndCheckFormatted(final SpreadsheetEngine engine,
                                                              final SpreadsheetCellReference reference,
                                                              final SpreadsheetEngineLoading loading,
                                                              final SpreadsheetEngineContext context,
                                                              final Object value,
                                                              final String text) {
        final SpreadsheetCell cell = this.loadCellAndCheckValue(engine, reference, loading, context, value);
        this.checkFormattedText(cell, text);
        return cell;
    }

    protected final void loadCellAndCheckError(final SpreadsheetEngine engine,
                                               final SpreadsheetCellReference reference,
                                               final SpreadsheetEngineLoading loading,
                                               final SpreadsheetEngineContext context,
                                               final String errorContains) {
        final SpreadsheetCell cell = this.loadCellOrFail(engine, reference, loading, context);

        final Optional<SpreadsheetError> error = cell.formula().error();
        assertNotEquals("Expected error missing=" + cell, SpreadsheetFormula.NO_ERROR, error);
        assertTrue("Error message " + error + " missing " + CharSequences.quoteAndEscape(errorContains), error.get().value().contains(errorContains));
    }

    protected final void loadLabelAndCheck(final SpreadsheetLabelStore labelStore,
                                           final SpreadsheetLabelName label,
                                           final ExpressionReference reference) {
        assertEquals("label loaded", Optional.of(SpreadsheetLabelMapping.with(label, reference)), labelStore.load(label));
    }

    protected final void loadLabelFailCheck(final SpreadsheetLabelStore labelStore,
                                            final SpreadsheetLabelName label) {
        assertEquals("label loaded failed", Optional.empty(), labelStore.load(label));
    }

    protected final void countAndCheck(final Store<?, ?> store, final int count) {
        assertEquals("record count in " + store, count, store.count());
    }

    private void checkFormula(final SpreadsheetCell cell, final String formula) {
        assertEquals("formula.text from returned cell=" + cell,
                formula,
                cell.formula().text());
    }

    private void checkValueOrError(final SpreadsheetCell cell, final Object value) {
        assertEquals("values from returned cell=" + cell,
                value,
                this.valueOrError(cell, "Value and Error absent (" + cell + ")"));
    }

    private Object valueOrError(final SpreadsheetCell cell, final Object bothAbsent) {
        final SpreadsheetFormula formula = cell.formula();
        return formula.value()
                .orElse(formula.error()
                        .map(e -> (Object) e.value())
                        .orElse(bothAbsent));
    }

    protected void checkFormattedText(final SpreadsheetCell cell, final String text) {
        assertNotEquals("formatted text absent", Optional.empty(), cell.formatted());
        assertEquals("formattedText", text, cell.formatted().get().text());
    }

    protected DecimalNumberContext decimalNumberContext() {
        return DecimalNumberContexts.basic("$", '.', 'E', ',', '-', '%', '+');
    }

    @Override
    protected final MemberVisibility typeVisibility() {
        return MemberVisibility.PACKAGE_PRIVATE;
    }
}

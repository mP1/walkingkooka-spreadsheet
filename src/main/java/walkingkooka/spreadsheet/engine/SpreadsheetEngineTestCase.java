package walkingkooka.spreadsheet.engine;

import org.junit.jupiter.api.Test;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public abstract class SpreadsheetEngineTestCase<E extends SpreadsheetEngine> extends ClassTestCase<E> {

    final static SpreadsheetColumnReference COLUMN = SpreadsheetReferenceKind.ABSOLUTE.column(1);
    final static SpreadsheetRowReference ROW = SpreadsheetReferenceKind.ABSOLUTE.row(2);
    final static SpreadsheetCellReference REFERENCE = COLUMN.setRow(ROW);
    final static SpreadsheetLabelName LABEL = SpreadsheetLabelName.with("LABEL123");

    @Test
    public final void testLoadCellNullCellFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().loadCell(null, SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY, this.createContext());
        });
    }

    @Test
    public final void testLoadCellNullLoadingFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().loadCell(REFERENCE,
                    null,
                    this.createContext());
        });
    }

    @Test
    public final void testLoadCellNullContextFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().loadCell(REFERENCE,
                    SpreadsheetEngineLoading.COMPUTE_IF_NECESSARY,
                    null);
        });
    }

    @Test
    public final void testDeleteColumnsNullColumnFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().deleteColumns(null, 1, this.createContext());
        });
    }

    @Test
    public final void testDeleteColumnsNegativeCountFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            this.createSpreadsheetEngine().deleteColumns(COLUMN, -1, this.createContext());
        });
    }

    @Test
    public final void testDeleteColumnsNullContextFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().deleteColumns(COLUMN, 1, null);
        });
    }

    @Test
    public final void testDeleteRowsNullRowFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().deleteRows(null, 1, this.createContext());
        });
    }

    @Test
    public final void testDeleteRowsNegativeCountFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            this.createSpreadsheetEngine().deleteRows(ROW, -1, this.createContext());
        });
    }

    @Test
    public final void testDeleteRowsNullContextFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().deleteRows(ROW, 1, null);
        });
    }

    @Test
    public final void testInsertColumnsNullColumnFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().insertColumns(null, 1, this.createContext());
        });
    }

    @Test
    public final void testInsertColumnsNegativeCountFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            this.createSpreadsheetEngine().insertColumns(COLUMN, -1, this.createContext());
        });
    }

    @Test
    public final void testInsertColumnsNullContextFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().insertColumns(COLUMN, 1, null);
        });
    }

    @Test
    public final void testInsertRowsNullRowFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().insertRows(null, 1, this.createContext());
        });
    }

    @Test
    public final void testInsertRowsNegativeCountFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            this.createSpreadsheetEngine().insertRows(ROW, -1, this.createContext());
        });
    }

    @Test
    public final void testInsertRowsNullContextFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().insertRows(ROW, 1, null);
        });
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
        assertEquals(Optional.empty(),
                cell,
                () -> "Expected reference " + reference + " to fail");
    }

    protected final SpreadsheetCell loadCellAndCheckWithoutValueOrError(final SpreadsheetEngine engine,
                                                                        final SpreadsheetCellReference reference,
                                                                        final SpreadsheetEngineLoading loading,
                                                                        final SpreadsheetEngineContext context) {
        final SpreadsheetCell cell = this.loadCellOrFail(engine, reference, loading, context);
        assertEquals(null,
                this.valueOrError(cell, null),
                ()-> "values from returned cells=" + cell);
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
        assertNotEquals(SpreadsheetFormula.NO_ERROR,
                error,
                ()-> "Expected error missing=" + cell);
        assertTrue(error.get().value().contains(errorContains),
                ()-> "Error message " + error + " missing " + CharSequences.quoteAndEscape(errorContains));
    }

    protected final void loadLabelAndCheck(final SpreadsheetLabelStore labelStore,
                                           final SpreadsheetLabelName label,
                                           final ExpressionReference reference) {
        assertEquals(Optional.of(SpreadsheetLabelMapping.with(label, reference)),
                labelStore.load(label),
                "label loaded");
    }

    protected final void loadLabelFailCheck(final SpreadsheetLabelStore labelStore,
                                            final SpreadsheetLabelName label) {
        assertEquals(Optional.empty(),
                labelStore.load(label),
                "label loaded failed");
    }

    protected final void countAndCheck(final Store<?, ?> store, final int count) {
        assertEquals(count,
                store.count(),
                "record count in " + store);
    }

    private void checkFormula(final SpreadsheetCell cell, final String formula) {
        assertEquals(formula,
                cell.formula().text(),
                ()-> "formula.text from returned cell=" + cell);
    }

    private void checkValueOrError(final SpreadsheetCell cell, final Object value) {
        assertEquals(value,
                this.valueOrError(cell, "Value and Error absent (" + cell + ")"),
                ()-> "values from returned cell=" + cell);
    }

    private Object valueOrError(final SpreadsheetCell cell, final Object bothAbsent) {
        final SpreadsheetFormula formula = cell.formula();
        return formula.value()
                .orElse(formula.error()
                        .map(e -> (Object) e.value())
                        .orElse(bothAbsent));
    }

    protected void checkFormattedText(final SpreadsheetCell cell, final String text) {
        assertNotEquals(Optional.empty(), cell.formatted(), "formatted text absent");
        assertEquals(text, cell.formatted().get().text(), "formattedText");
    }

    protected DecimalNumberContext decimalNumberContext() {
        return DecimalNumberContexts.basic("$", '.', 'E', ',', '-', '%', '+');
    }

    @Override
    protected final MemberVisibility typeVisibility() {
        return MemberVisibility.PACKAGE_PRIVATE;
    }
}

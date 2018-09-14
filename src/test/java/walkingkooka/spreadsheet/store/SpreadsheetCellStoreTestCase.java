package walkingkooka.spreadsheet.store;

import org.junit.Test;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.test.PackagePrivateClassTestCase;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetReferenceKind;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

public abstract class SpreadsheetCellStoreTestCase<S extends SpreadsheetCellStore> extends PackagePrivateClassTestCase<S> {

    final static SpreadsheetCellReference REFERENCE = SpreadsheetReferenceKind.ABSOLUTE.column(1).setRow(SpreadsheetReferenceKind.ABSOLUTE.row(2));
    final static SpreadsheetLabelName LABEL = SpreadsheetLabelName.with("label123");

    @Test(expected = NullPointerException.class)
    public final void testLoadNullCellFails() {
        this.createSpreadsheetCellStore().load(null);
    }

    @Test(expected = NullPointerException.class)
    public final void testSaveNullFails() {
        this.createSpreadsheetCellStore().save(null);
    }

    @Test(expected = NullPointerException.class)
    public final void testDeleteNullFails() {
        this.createSpreadsheetCellStore().delete(null);
    }

    @Test
    public final void testLoadUnknown() {
        this.loadFailCheck(REFERENCE);
    }

    @Test
    public final void testSaveAndLoad() {
        final S store = this.createSpreadsheetCellStore();

        final SpreadsheetCellReference reference = this.cellReference(1, 2);
        final SpreadsheetCell cell = SpreadsheetCell.with(reference, this.formula());
        store.save(cell);

        assertSame(cell, this.loadOrFail(store, reference));
    }

    @Test
    public final void testSaveDeleteLoad() {
        final S store = this.createSpreadsheetCellStore();

        final SpreadsheetCellReference reference = this.cellReference(1, 2);
        final SpreadsheetCell cell = SpreadsheetCell.with(reference, this.formula());
        store.save(cell);
        store.delete(reference);

        this.loadFailCheck(store, reference);
    }

    @Test
    public final void testRows() {
        final S store = this.createSpreadsheetCellStore();

        final SpreadsheetFormula formula = this.formula();

        store.save(SpreadsheetCell.with(this.cellReference(1, 2), formula));
        store.save(SpreadsheetCell.with(this.cellReference(1, 99), formula));
        store.save(SpreadsheetCell.with(this.cellReference(1, 5), formula));

        this.rowsAndCheck(store, 99);
    }

    @Test
    public final void testColumn() {
        final S store = this.createSpreadsheetCellStore();

        final SpreadsheetFormula formula = this.formula();

        store.save(SpreadsheetCell.with(this.cellReference(1, 1), formula));
        store.save(SpreadsheetCell.with(this.cellReference(99, 1), formula));
        store.save(SpreadsheetCell.with(this.cellReference(98, 2), formula));

        this.columnsAndCheck(store, 99);
    }

    abstract S createSpreadsheetCellStore();

    final SpreadsheetCell loadOrFail(final SpreadsheetCellStore store,
                                     final SpreadsheetCellReference reference) {
        final Optional<SpreadsheetCell> cell = store.load(reference);
        if(!cell.isPresent()) {
            fail("Loading " + reference + " should have succeeded");
        }
        return cell.get();
    }

    final void loadFailCheck(final SpreadsheetCellReference reference) {
        this.loadFailCheck(this.createSpreadsheetCellStore(), reference);
    }

    final void loadFailCheck(final SpreadsheetCellStore store,
                                 final SpreadsheetCellReference reference) {
        final Optional<SpreadsheetCell> cell = store.load(reference);
        assertEquals("Expected reference " + reference + " to fail", Optional.empty(), cell);
    }

    final SpreadsheetCellReference cellReference(final int column, final int row) {
        return SpreadsheetCellReference.with(SpreadsheetReferenceKind.ABSOLUTE.column(column),
                SpreadsheetReferenceKind.ABSOLUTE.row(row));
    }
    
    final void rowsAndCheck(final SpreadsheetCellStore store, final int row) {
        assertEquals("rows for store=" + store, row, store.rows());
    }

    final void columnsAndCheck(final SpreadsheetCellStore store, final int column) {
        assertEquals("columns for store=" + store, column, store.columns());
    }
    
    private SpreadsheetFormula formula() {
        return SpreadsheetFormula.with("1+2");
    }
}

package walkingkooka.spreadsheet.store;

import org.junit.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.test.PackagePrivateClassTestCase;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetReferenceKind;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

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
    public final void testColumns() {
        final S store = this.createSpreadsheetCellStore();

        final SpreadsheetFormula formula = this.formula();

        store.save(SpreadsheetCell.with(this.cellReference(1, 1), formula));
        store.save(SpreadsheetCell.with(this.cellReference(99, 1), formula));
        store.save(SpreadsheetCell.with(this.cellReference(98, 2), formula));

        this.columnsAndCheck(store, 99);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testRowInvalidRowFails() {
        this.createSpreadsheetCellStore().row(-1);
    }

    @Test
    public final void testRow() {
        final S store = this.createSpreadsheetCellStore();

        final SpreadsheetFormula formula = this.formula();

        final SpreadsheetCell a = SpreadsheetCell.with(this.cellReference(11, 1), formula);
        final SpreadsheetCell b = SpreadsheetCell.with(this.cellReference(22, 1), formula);
        final SpreadsheetCell c = SpreadsheetCell.with(this.cellReference(11, 2), formula);
        final SpreadsheetCell d = SpreadsheetCell.with(this.cellReference(22, 2), formula);

        store.save(a);
        store.save(b);
        store.save(c);
        store.save(d);

        checkEquals("row 1", store.row(1), a, b);
        checkEquals("row 2", store.row(2), c, d);
        checkEquals("row 99", store.row(99));
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testColumnInvalidColumnFails() {
        this.createSpreadsheetCellStore().column(-1);
    }

    @Test
    public final void testColumn() {
        final S store = this.createSpreadsheetCellStore();

        final SpreadsheetFormula formula = this.formula();

        final SpreadsheetCell a = SpreadsheetCell.with(this.cellReference(1, 11), formula);
        final SpreadsheetCell b = SpreadsheetCell.with(this.cellReference(1, 22), formula);
        final SpreadsheetCell c = SpreadsheetCell.with(this.cellReference(2, 11), formula);
        final SpreadsheetCell d = SpreadsheetCell.with(this.cellReference(2, 22), formula);

        store.save(a);
        store.save(b);
        store.save(c);
        store.save(d);

        checkEquals("column 1", store.column(1), a, b);
        checkEquals("column 2", store.column(2), c, d);
        checkEquals("column 99", store.column(99));
    }

    private void checkEquals(final String message, final Collection<SpreadsheetCell> cells, final SpreadsheetCell...expected) {
        final Set<SpreadsheetCell> actual = Sets.sorted();
        actual.addAll(cells);

        final Set<SpreadsheetCell> expectedSets = Sets.sorted();
        expectedSets.addAll(Lists.of(expected));

        assertEquals(message, expectedSets, actual);
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

package walkingkooka.spreadsheet.store.cell;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetCellFormat;
import walkingkooka.spreadsheet.SpreadsheetFormattedCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.store.StoreTesting;
import walkingkooka.spreadsheet.style.SpreadsheetCellStyle;
import walkingkooka.spreadsheet.style.SpreadsheetTextStyle;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetReferenceKind;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetCellStoreTestCase<S extends SpreadsheetCellStore> implements StoreTesting<S, SpreadsheetCellReference, SpreadsheetCell> {

    final static SpreadsheetCellReference REFERENCE = SpreadsheetReferenceKind.ABSOLUTE.column(1).setRow(SpreadsheetReferenceKind.ABSOLUTE.row(2));

    SpreadsheetCellStoreTestCase() {
        super();
    }

    @Test
    public final void testLoadUnknown() {
        this.loadFailCheck(REFERENCE);
    }

    @Test
    public final void testSaveAndLoad() {
        final S store = this.createStore();

        final SpreadsheetCellReference reference = this.cellReference(1, 2);
        final SpreadsheetCell cell = this.cell(reference);
        assertEquals(cell, store.save(cell), "incorrect key returned");

        assertSame(cell, store.loadOrFail(reference));
    }

    @Test
    public final void testSaveDeleteLoad() {
        final S store = this.createStore();

        final SpreadsheetCellReference reference = this.cellReference(1, 2);
        final SpreadsheetCell cell = this.cell(reference);
        store.save(cell);
        store.delete(reference);

        this.loadFailCheck(store, reference);
    }

    @Test
    public final void testCount() {
        final S store = this.createStore();

        store.save(this.cell(1, 2));
        store.save(this.cell(3, 4));

        this.countAndCheck(store, 2);
    }

    @Test
    public final void testCount2() {
        final S store = this.createStore();

        final SpreadsheetCellReference cell = this.cellReference(1, 2);
        store.save(this.cell(cell));
        store.save(this.cell(3, 4));
        store.delete(cell);

        this.countAndCheck(store, 2 - 1);
    }

    @Test
    public final void testRows() {
        final S store = this.createStore();

        store.save(this.cell(1, 2));
        store.save(this.cell(1, 99));
        store.save(this.cell(1, 5));

        this.rowsAndCheck(store, 99);
    }

    @Test
    public final void testColumns() {
        final S store = this.createStore();

        store.save(this.cell(1, 1));
        store.save(this.cell(99, 1));
        store.save(this.cell(98, 2));

        this.columnsAndCheck(store, 99);
    }

    @Test
    public final void testRowInvalidRowFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            this.createStore().row(-1);
        });
    }

    @Test
    public final void testRow() {
        final S store = this.createStore();

        final SpreadsheetCell a = this.cell(11, 1);
        final SpreadsheetCell b = this.cell(22, 1);
        final SpreadsheetCell c = this.cell(11, 2);
        final SpreadsheetCell d = this.cell(22, 2);

        store.save(a);
        store.save(b);
        store.save(c);
        store.save(d);

        checkEquals("row 1", store.row(1), a, b);
        checkEquals("row 2", store.row(2), c, d);
        checkEquals("row 99", store.row(99));
    }

    @Test
    public final void testColumnInvalidColumnFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            this.createStore().column(-1);
        });
    }

    @Test
    public final void testColumn() {
        final S store = this.createStore();

        final SpreadsheetCell a = this.cell(1, 11);
        final SpreadsheetCell b = this.cell(1, 22);
        final SpreadsheetCell c = this.cell(2, 11);
        final SpreadsheetCell d = this.cell(2, 22);

        store.save(a);
        store.save(b);
        store.save(c);
        store.save(d);

        checkEquals("column 1", store.column(1), a, b);
        checkEquals("column 2", store.column(2), c, d);
        checkEquals("column 99", store.column(99));
    }

    private void checkEquals(final String message, final Collection<SpreadsheetCell> cells, final SpreadsheetCell... expected) {
        final Set<SpreadsheetCell> actual = Sets.sorted();
        actual.addAll(cells);

        final Set<SpreadsheetCell> expectedSets = Sets.sorted();
        expectedSets.addAll(Lists.of(expected));

        assertEquals(expectedSets, actual, message);
    }

    private SpreadsheetCell cell(final int column, final int row) {
        return this.cell(this.cellReference(column, row));
    }

    private SpreadsheetCell cell(final SpreadsheetCellReference cellReference) {
        return SpreadsheetCell.with(cellReference, this.formula(), this.style())
                .setFormat(this.format())
                .setFormatted(this.formatted());
    }

    final SpreadsheetCellReference cellReference(final int column, final int row) {
        return SpreadsheetCellReference.with(SpreadsheetReferenceKind.ABSOLUTE.column(column),
                SpreadsheetReferenceKind.ABSOLUTE.row(row));
    }

    final void rowsAndCheck(final SpreadsheetCellStore store, final int row) {
        assertEquals(row, store.rows(), () -> "rows for store=" + store);
    }

    final void columnsAndCheck(final SpreadsheetCellStore store, final int column) {
        assertEquals(column, store.columns(), () -> "columns for store=" + store);
    }

    private SpreadsheetFormula formula() {
        return SpreadsheetFormula.with("1+2");
    }

    private SpreadsheetCellStyle style() {
        return SpreadsheetCellStyle.EMPTY.setText(SpreadsheetTextStyle.EMPTY.setBold(SpreadsheetTextStyle.BOLD));
    }

    private Optional<SpreadsheetCellFormat> format() {
        return SpreadsheetCell.NO_FORMAT;
    }

    private Optional<SpreadsheetFormattedCell> formatted() {
        return SpreadsheetCell.NO_FORMATTED_CELL;
    }
}

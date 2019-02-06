package walkingkooka.spreadsheet.store.range;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetRange;
import walkingkooka.spreadsheet.store.StoreTestCase;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetReferenceKind;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetRangeStoreTestCase<S extends SpreadsheetRangeStore<V>, V> extends StoreTestCase<S, SpreadsheetRange, List<V>> {

    /**
     * RANGE and RANGE1B share a common TOPLEFT.
     */
    private final static SpreadsheetCellReference TOPLEFT = cell(10, 20);
    private final static SpreadsheetCellReference CENTER = TOPLEFT.add(1, 1);
    private final static SpreadsheetCellReference BOTTOMRIGHT = CENTER.add(1, 1);
    private final static SpreadsheetRange RANGE = SpreadsheetRange.with(TOPLEFT, BOTTOMRIGHT);

    // tests.......................................................................................................

    @Test
    public final void testLoadUnknownFails() {
        this.loadFailCheck(RANGE);
    }

    @Test
    public final void testLoadCellNullCellReferenceFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createStore().loadCellReference(null);
        });
    }

    @Test
    public final void testLoadCellUnknownReference() {
        this.loadCellReferenceFails(RANGE.begin());
    }

    @Test
    public final void testSaveNullRangeFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createStore().saveValue(null, this.value());
        });
    }

    @Test
    public final void testSaveNullValueFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createStore().saveValue(RANGE, null);
        });
    }

    @Test
    public final void testReplaceNullRangeFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createStore().replaceValue(null, this.value(), this.value());
        });
    }

    @Test
    public final void testReplaceNullNewValueFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createStore().replaceValue(RANGE, null, this.value());
        });
    }

    @Test
    public final void testReplaceNullOldValueFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createStore().replaceValue(RANGE, this.value(), null);
        });
    }

    @Test
    public final void testDeleteNullRangeFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createStore().deleteValue(null, this.value());
        });
    }

    @Test
    public final void testDeleteNullValueFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createStore().deleteValue(RANGE, null);
        });
    }

    // helpers ............................................................

    protected static SpreadsheetCellReference cell(final int column, final int row) {
        return SpreadsheetReferenceKind.ABSOLUTE.column(column).setRow(SpreadsheetReferenceKind.ABSOLUTE.row(row));
    }

    protected void loadRangeFails(final SpreadsheetRangeStore<V> store, final SpreadsheetRange range) {
        assertEquals(Optional.empty(),
                store.load(range),
                () -> "load range " + range + " should have returned no values");
    }

    protected void loadRangeAndCheck(final SpreadsheetRangeStore<V> store, final SpreadsheetRange range, final V... expected) {
        final Optional<List<V>> values = store.load(range);
        assertNotEquals(Optional.empty(), values, () -> "load of " + range + " failed");
        assertEquals(Lists.of(expected), values.get(), () -> "load range " + range);
    }

    protected void loadCellReferenceFails(final SpreadsheetCellReference cell) {
        this.loadCellReferenceFails(this.createStore(), cell);
    }

    protected void loadCellReferenceFails(final SpreadsheetRangeStore<V> store, final SpreadsheetCellReference cell) {
        assertEquals(Sets.empty(),
                this.loadCellReference(store, cell),
                () -> "load cell " + cell + " should have returned no values");
    }

    protected void loadCellReferenceAndCheck(final SpreadsheetRangeStore<V> store, final SpreadsheetCellReference cell, final V... values) {
        assertEquals(Sets.of(values),
                this.loadCellReference(store, cell),
                () -> "load cell " + cell);
    }

    protected Set<V> loadCellReference(final SpreadsheetRangeStore<V> store, final SpreadsheetCellReference cell) {
        final Set<V> values = store.loadCellReference(cell);
        assertNotNull(values, "values");
        return values;
    }

    protected abstract V value();
}

package walkingkooka.spreadsheet.store.range;

import org.junit.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetRange;
import walkingkooka.spreadsheet.store.StoreTestCase;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetReferenceKind;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public abstract class SpreadsheetRangeStoreTestCase<S extends SpreadsheetRangeStore<V>, V> extends StoreTestCase<S, SpreadsheetRange, List<V>> {

    /**
     * RANGE and RANGE1B share a common TOPLEFT.
     */
    private final static SpreadsheetCellReference TOPLEFT = cell(10,20);
    private final static SpreadsheetCellReference CENTER = TOPLEFT.add(1,1);
    private final static SpreadsheetCellReference BOTTOMRIGHT = CENTER.add(1,1);
    private final static SpreadsheetRange RANGE = SpreadsheetRange.with(TOPLEFT, BOTTOMRIGHT);

    // tests.......................................................................................................

    @Test
    public final void testLoadUnknownFails() {
        this.loadFailCheck(RANGE);
    }

    @Test(expected = NullPointerException.class)
    public final void testLoadCellNullCellReferenceFails() {
        this.createStore().loadCellReference(null);
    }

    @Test
    public final void testLoadCellUnknownReference() {
        this.loadCellReferenceFails(RANGE.begin());
    }

    @Test(expected = NullPointerException.class)
    public final void testSaveNullRangeFails() {
        this.createStore().saveValue(null, this.value());
    }

    @Test(expected = NullPointerException.class)
    public final void testSaveNullValueFails() {
        this.createStore().saveValue(RANGE, null);
    }

    @Test(expected = NullPointerException.class)
    public final void testReplaceNullRangeFails() {
        this.createStore().replaceValue(null, this.value(), this.value());
    }

    @Test(expected = NullPointerException.class)
    public final void testReplaceNullNewValueFails() {
        this.createStore().replaceValue(RANGE, null, this.value());
    }

    @Test(expected = NullPointerException.class)
    public final void testReplaceNullOldValueFails() {
        this.createStore().replaceValue(RANGE, this.value(), null);
    }

    @Test(expected = NullPointerException.class)
    public final void testDeleteNullRangeFails() {
        this.createStore().deleteValue(null, this.value());
    }

    @Test(expected = NullPointerException.class)
    public final void testDeleteNullValueFails() {
        this.createStore().deleteValue(RANGE, null);
    }

    // helpers ............................................................

    protected static SpreadsheetCellReference cell(final int column, final int row) {
        return SpreadsheetReferenceKind.ABSOLUTE.column(column).setRow(SpreadsheetReferenceKind.ABSOLUTE.row(row));
    }

    protected void loadRangeFails(final SpreadsheetRangeStore<V> store, final SpreadsheetRange range) {
        assertEquals("load range " + range + " should have returned no values", Optional.empty(), store.load(range));
    }

    protected void loadRangeAndCheck(final SpreadsheetRangeStore<V> store, final SpreadsheetRange range, final V... expected) {
        final Optional<List<V>> values = store.load(range);
        assertNotEquals("load of " + range + " failed", Optional.empty(), values);
        assertEquals("load range " + range, Lists.of(expected), values.get());
    }

    protected void loadCellReferenceFails(final SpreadsheetCellReference cell) {
        this.loadCellReferenceFails(this.createStore(), cell);
    }

    protected void loadCellReferenceFails(final SpreadsheetRangeStore<V> store, final SpreadsheetCellReference cell) {
        assertEquals("load cell " + cell + " should have returned no values", Sets.empty(), this.loadCellReference(store, cell));
    }

    protected void loadCellReferenceAndCheck(final SpreadsheetRangeStore<V> store, final SpreadsheetCellReference cell, final V... values) {
        assertEquals("load cell " + cell, Sets.of(values), this.loadCellReference(store, cell));
    }

    protected Set<V> loadCellReference(final SpreadsheetRangeStore<V> store, final SpreadsheetCellReference cell) {
        final Set<V> values = store.loadCellReference(cell);
        assertNotNull("values", values);
        return values;
    }

    protected abstract V value();
}

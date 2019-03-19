package walkingkooka.spreadsheet.store.range;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetRange;
import walkingkooka.spreadsheet.store.StoreTesting;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetReferenceKind;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetRangeStoreTesting<S extends SpreadsheetRangeStore<V>, V> extends StoreTesting<S, SpreadsheetRange, List<V>> {

    /**
     * RANGE and RANGE1B share a common TOPLEFT.
     */
    SpreadsheetCellReference TOPLEFT = cell(10, 20);
    SpreadsheetCellReference CENTER = TOPLEFT.add(1, 1);
    SpreadsheetCellReference BOTTOMRIGHT = CENTER.add(1, 1);
    SpreadsheetRange RANGE = SpreadsheetRange.with(TOPLEFT, BOTTOMRIGHT);

    // tests.......................................................................................................

    @Test
    default void testLoadUnknownFails() {
        this.loadFailCheck(RANGE);
    }

    @Test
    default void testLoadCellReferenceNullCellReferenceFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createStore().loadCellReferenceValues(null);
        });
    }

    @Test
    default void testLoadCellReferenceUnknownReference() {
        this.loadCellReferenceValuesFails(RANGE.begin());
    }

    @Test
    default void testAddValueNullRangeFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createStore().addValue(null, this.value());
        });
    }

    @Test
    default void testAddValueNullValueFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createStore().addValue(RANGE, null);
        });
    }

    @Test
    default void testReplaceNullRangeFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createStore().replaceValue(null, this.value(), this.value());
        });
    }

    @Test
    default void testReplaceNullNewValueFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createStore().replaceValue(RANGE, null, this.value());
        });
    }

    @Test
    default void testReplaceNullOldValueFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createStore().replaceValue(RANGE, this.value(), null);
        });
    }

    @Test
    default void testRemoveValueNullRangeFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createStore().removeValue(null, this.value());
        });
    }

    @Test
    default void testRemoveValueNullValueFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createStore().removeValue(RANGE, null);
        });
    }

    // helpers ............................................................

    static SpreadsheetCellReference cell(final int column, final int row) {
        return SpreadsheetReferenceKind.ABSOLUTE.column(column).setRow(SpreadsheetReferenceKind.ABSOLUTE.row(row));
    }

    default void loadRangeFails(final SpreadsheetRangeStore<V> store, final SpreadsheetRange range) {
        assertEquals(Optional.empty(),
                store.load(range),
                () -> "load range " + range + " should have returned no values");
    }

    default void loadRangeAndCheck(final SpreadsheetRangeStore<V> store, final SpreadsheetRange range, final V... expected) {
        final Optional<List<V>> values = store.load(range);
        assertNotEquals(Optional.empty(), values, () -> "load of " + range + " failed");
        assertEquals(Lists.of(expected), values.get(), () -> "load range " + range);
    }

    // loadCellReferenceRanges.........................................................................................

    default void loadCellReferenceRangesFails(final SpreadsheetCellReference cell) {
        this.loadCellReferenceRangesFails(this.createStore(), cell);
    }

    default void loadCellReferenceRangesFails(final SpreadsheetRangeStore<V> store,
                                              final SpreadsheetCellReference cell) {
        assertEquals(Sets.empty(),
                this.loadCellReferenceRanges(store, cell),
                () -> "load cell " + cell + " should have returned no ranges");
    }

    default void loadCellReferenceRangesAndCheck(final SpreadsheetRangeStore<V> store,
                                                 final SpreadsheetCellReference cell,
                                                 final SpreadsheetRange... ranges) {
        assertEquals(Sets.of(ranges),
                this.loadCellReferenceRanges(store, cell),
                () -> "load cell reference ranges for " + cell);
    }

    default Set<SpreadsheetRange> loadCellReferenceRanges(final SpreadsheetRangeStore<V> store,
                                                          final SpreadsheetCellReference cell) {
        final Set<SpreadsheetRange> ranges = store.loadCellReferenceRanges(cell);
        assertNotNull(ranges, "ranges");
        return ranges;
    }
    
    // loadCellReferenceValues.........................................................................................

    default void loadCellReferenceValuesFails(final SpreadsheetCellReference cell) {
        this.loadCellReferenceValuesFails(this.createStore(), cell);
    }

    default void loadCellReferenceValuesFails(final SpreadsheetRangeStore<V> store,
                                              final SpreadsheetCellReference cell) {
        assertEquals(Sets.empty(),
                this.loadCellReferenceValues(store, cell),
                () -> "load cell " + cell + " should have returned no values");
    }

    default void loadCellReferenceValuesAndCheck(final SpreadsheetRangeStore<V> store,
                                                 final SpreadsheetCellReference cell,
                                                 final V... values) {
        assertEquals(Sets.of(values),
                this.loadCellReferenceValues(store, cell),
                () -> "load cell reference values for " + cell);
    }

    default Set<V> loadCellReferenceValues(final SpreadsheetRangeStore<V> store,
                                           final SpreadsheetCellReference cell) {
        final Set<V> values = store.loadCellReferenceValues(cell);
        assertNotNull(values, "values");
        return values;
    }

    V value();
}

/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package walkingkooka.spreadsheet.reference.store;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRange;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.spreadsheet.store.SpreadsheetStoreTesting;
import walkingkooka.test.TypeNameTesting;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetRangeStoreTesting<S extends SpreadsheetRangeStore<V>, V> extends SpreadsheetStoreTesting<S, SpreadsheetRange, List<V>>,
        TypeNameTesting<S> {

    /**
     * RANGE and RANGE1B share a common TOPLEFT.
     */
    SpreadsheetCellReference TOPLEFT = cell(10, 20);
    SpreadsheetCellReference CENTER = TOPLEFT.add(1, 1);
    SpreadsheetCellReference BOTTOMRIGHT = CENTER.add(1, 1);
    SpreadsheetRange RANGE = TOPLEFT.spreadsheetRange(BOTTOMRIGHT);

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

    @Override
    default void testAddSaveWatcherAndRemove() {
    }

    @Override
    default void testAddSaveWatcherAndSave() {
    }

    @Test
    default void testAddSaveWatcherFails() {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.createStore().addSaveWatcher((a) -> {
            });
        });
    }

    @Test
    default void testAddDeleteWatcherAndDelete() {
        final SpreadsheetRange range = this.id();
        final V value = this.valueValue();

        final S store = this.createStore();

        final List<SpreadsheetRange> fired = Lists.array();
        store.addDeleteWatcher((d) -> fired.add(d));

        store.addValue(range, value);

        store.delete(range);

        assertEquals(Lists.of(range), fired, "fired values");
    }

    @Test
    default void testAddValueNullRangeFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createStore().addValue(null, this.valueValue());
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
            this.createStore().replaceValue(null, this.valueValue(), this.valueValue());
        });
    }

    @Test
    default void testReplaceNullNewValueFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createStore().replaceValue(RANGE, null, this.valueValue());
        });
    }

    @Test
    default void testReplaceNullOldValueFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createStore().replaceValue(RANGE, this.valueValue(), null);
        });
    }

    @Test
    default void testRemoveValueNullRangeFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createStore().removeValue(null, this.valueValue());
        });
    }

    @Test
    default void testRemoveValueNullValueFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createStore().removeValue(RANGE, null);
        });
    }

    @Test
    default void testRangesNullValueFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createStore().rangesWithValue(null);
        });
    }

    @Test
    default void testRangesWithValue() {
        final S store = this.createStore();

        final SpreadsheetRange range = this.id();
        final V value = this.valueValue();
        this.rangesWithValuesAndCheck(store, value);

        store.addValue(range, value);

        this.rangesWithValuesAndCheck(store, value, range);
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

    default void rangesWithValuesAndCheck(final SpreadsheetRangeStore<V> store,
                                          final V value,
                                          final SpreadsheetRange... ranges) {
        assertEquals(Sets.of(ranges),
                this.rangesWithValues(store, value),
                () -> "ranges with values for " + value);
    }

    default Set<SpreadsheetRange> rangesWithValues(final SpreadsheetRangeStore<V> store,
                                                   final V value) {
        final Set<SpreadsheetRange> ranges = store.rangesWithValue(value);
        assertNotNull(ranges, "ranges");
        return ranges;
    }

    V valueValue();

    // SpreadsheetStoreTesting...........................................................

    @Override
    default SpreadsheetRange id() {
        return SpreadsheetExpressionReference.parseRange("A1:B2");
    }

    @Override
    default List<V> value() {
        return Lists.of(this.valueValue());
    }

    // TypeNameTesting..................................................................

    @Override
    default String typeNameSuffix() {
        return SpreadsheetRangeStore.class.getSimpleName();
    }
}

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

package walkingkooka.spreadsheet.store;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.reflect.TypeNameTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetCellRangeStoreTesting<S extends SpreadsheetCellRangeStore<V>, V> extends SpreadsheetStoreTesting<S, SpreadsheetCellRangeReference, List<V>>,
    TypeNameTesting<S> {

    /**
     * RANGE and RANGE1B share a common TOPLEFT.
     */
    SpreadsheetCellReference TOPLEFT = cell(10, 20);
    SpreadsheetCellReference CENTER = TOPLEFT.add(1, 1);
    SpreadsheetCellReference BOTTOMRIGHT = CENTER.add(1, 1);
    SpreadsheetCellRangeReference RANGE = TOPLEFT.cellRange(BOTTOMRIGHT);

    // tests.............................................................................................................

    @Test
    default void testLoadUnknownFails() {
        this.loadAndCheck(
            this.createStore(),
            RANGE
        );
    }

    @Override
    default void testAddSaveWatcherAndRemove() {
    }

    @Override
    default void testAddSaveWatcherAndSave() {
    }

    @Test
    default void testAddSaveWatcherFails() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> this.createStore()
                .addSaveWatcher((a) -> {
                    }
                )
        );
    }

    default void loadRangeFails(final SpreadsheetCellRangeStore<V> store,
                                final SpreadsheetCellRangeReference range) {
        this.checkEquals(
            Optional.empty(),
            store.load(range),
            () -> "load range " + range + " should have returned no values"
        );
    }

    @SuppressWarnings({"OptionalGetWithoutIsPresent", "unchecked"})
    default void loadRangeAndCheck(final SpreadsheetCellRangeStore<V> store,
                                   final SpreadsheetCellRangeReference range,
                                   final V... expected) {
        final Optional<List<V>> values = store.load(range);

        this.checkNotEquals(
            Optional.empty(),
            values,
            () -> "load of " + range + " failed"
        );
        this.checkEquals(
            Lists.of(expected),
            values.get(),
            () -> "load range " + range
        );
    }

    @Test
    @Override
    default void testAddDeleteWatcherAndDelete() {
        final SpreadsheetCellRangeReference range = this.id();
        final V value = this.valueValue();

        final S store = this.createStore();

        final List<SpreadsheetCellRangeReference> fired = Lists.array();
        store.addDeleteWatcher(fired::add);

        store.addValue(range, value);

        store.delete(range);

        this.checkEquals(
            Lists.of(range),
            fired,
            "fired values"
        );
    }

    // addValue.........................................................................................................

    @Test
    default void testAddValueNullRangeFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStore()
                .addValue(
                    null,
                    this.valueValue()
                )
        );
    }

    @Test
    default void testAddValueNullValueFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStore()
                .addValue(
                    RANGE,
                    null
                )
        );
    }

    // replaceValue.....................................................................................................

    @Test
    default void testReplaceValueNullRangeFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStore()
                .replaceValue(
                    null,
                    this.valueValue(),
                    this.valueValue()
                )
        );
    }

    @Test
    default void testReplaceValueNullNewValueFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStore()
                .replaceValue(
                    RANGE,
                    null,
                    this.valueValue()
                )
        );
    }

    @Test
    default void testReplaceValueNullOldValueFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStore()
                .replaceValue(
                    RANGE,
                    this.valueValue(),
                    null
                )
        );
    }

    @Test
    default void testRemoveValueNullRangeFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStore()
                .removeValue(
                    null,
                    this.valueValue()
                )
        );
    }

    @Test
    default void testRemoveValueNullValueFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStore()
                .removeValue(
                    RANGE,
                    null
                )
        );
    }

    // findCellRangesIncludingCell......................................................................................

    default void findCellRangesIncludingCellFails(final SpreadsheetCellReference cell) {
        this.findCellRangesIncludingCellFails(this.createStore(), cell);
    }

    default void findCellRangesIncludingCellFails(final SpreadsheetCellRangeStore<V> store,
                                                  final SpreadsheetCellReference cell) {
        this.checkEquals(Sets.empty(),
            this.findCellRangesIncludingCell(store, cell),
            () -> "load cell " + cell + " should have returned no ranges");
    }

    default void findCellRangesIncludingCellAndCheck(final SpreadsheetCellRangeStore<V> store,
                                                     final SpreadsheetCellReference cell,
                                                     final SpreadsheetCellRangeReference... ranges) {
        this.checkEquals(Sets.of(ranges),
            this.findCellRangesIncludingCell(store, cell),
            () -> "load cell reference ranges for " + cell);
    }

    default Set<SpreadsheetCellRangeReference> findCellRangesIncludingCell(final SpreadsheetCellRangeStore<V> store,
                                                                           final SpreadsheetCellReference cell) {
        final Set<SpreadsheetCellRangeReference> ranges = store.findCellRangesIncludingCell(cell);
        assertNotNull(ranges, "ranges");
        return ranges;
    }

    // findValuesWithCell...............................................................................................

    @Test
    default void testFindValuesWithCellWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStore()
                .findValuesWithCell(null)
        );
    }

    @Test
    default void testFindValuesWithCellUnknownReference() {
        this.findValuesWithCellFails(RANGE.begin());
    }

    default void findValuesWithCellFails(final SpreadsheetCellReference cell) {
        this.findValuesWithCellFails(
            this.createStore(),
            cell
        );
    }

    default void findValuesWithCellFails(final SpreadsheetCellRangeStore<V> store,
                                         final SpreadsheetCellReference cell) {
        this.checkEquals(
            Sets.empty(),
            this.findValuesWithCell(store, cell),
            () -> "load cell " + cell + " should have returned no values"
        );
    }

    @SuppressWarnings("unchecked")
    default void findValuesWithCellAndCheck(final SpreadsheetCellRangeStore<V> store,
                                            final SpreadsheetCellReference cell,
                                            final V... values) {
        this.checkEquals(
            Sets.of(values),
            this.findValuesWithCell(store, cell),
            () -> "load cell reference values for " + cell
        );
    }

    default Set<V> findValuesWithCell(final SpreadsheetCellRangeStore<V> store,
                                      final SpreadsheetCellReference cell) {
        final Set<V> values = store.findValuesWithCell(cell);
        this.checkNotEquals(
            null,
            values,
            "values"
        );
        return values;
    }

    // findCellRangesWithValue..........................................................................................

    @Test
    default void testFindCellRangesWithValueWithNullValueFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStore()
                .findCellRangesWithValue(null)
        );
    }

    @Test
    default void testFindCellRangesWithValue() {
        final S store = this.createStore();

        final SpreadsheetCellRangeReference range = this.id();
        final V value = this.valueValue();
        this.findCellRangesWithValueAndCheck(store, value);

        store.addValue(range, value);

        this.findCellRangesWithValueAndCheck(store, value, range);
    }

    default void findCellRangesWithValueAndCheck(final SpreadsheetCellRangeStore<V> store,
                                                 final V value,
                                                 final SpreadsheetCellRangeReference... ranges) {
        this.checkEquals(
            Sets.of(ranges),
            store.findCellRangesWithValue(value),
            () -> "ranges with values for " + value
        );
    }

    V valueValue();

    // StoreTesting.....................................................................................................

    @Override
    default SpreadsheetCellRangeReference id() {
        return SpreadsheetSelection.parseCellRange("A1:B2");
    }

    @Override
    default List<V> value() {
        return Lists.of(this.valueValue());
    }

    // TypeNameTesting..................................................................................................

    @Override
    default String typeNameSuffix() {
        return SpreadsheetCellRangeStore.class.getSimpleName();
    }

    // helpers .........................................................................................................

    static SpreadsheetCellReference cell(final int column,
                                         final int row) {
        return SpreadsheetReferenceKind.RELATIVE.column(column)
            .setRow(SpreadsheetReferenceKind.RELATIVE.row(row));
    }
}

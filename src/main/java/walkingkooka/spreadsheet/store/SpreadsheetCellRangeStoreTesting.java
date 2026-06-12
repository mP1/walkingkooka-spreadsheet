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
import walkingkooka.store.MultiValueStoreTesting;
import walkingkooka.store.MultiValueStoreWatcher;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetCellRangeStoreTesting<S extends SpreadsheetCellRangeStore> extends MultiValueStoreTesting<S, SpreadsheetCellRangeReference, SpreadsheetCellReference>,
    TypeNameTesting<S> {

    /**
     * RANGE and RANGE1B share a common TOPLEFT.
     */
    SpreadsheetCellReference TOPLEFT = cell(10, 20);
    SpreadsheetCellReference CENTER = TOPLEFT.add(1, 1);
    SpreadsheetCellReference BOTTOMRIGHT = CENTER.add(1, 1);
    SpreadsheetCellRangeReference RANGE = TOPLEFT.cellRange(BOTTOMRIGHT);

    // tests.............................................................................................................

    @Override
    default void testAddStoreWatcherAndSave() {
        throw new UnsupportedOperationException();
    }

    @Override
    default void testAddStoreWatcherAndSaveTwiceFiresOnce() {
        throw new UnsupportedOperationException();
    }

    @Test
    @Override
    default void testAddStoreWatcherAndDelete() {
        final SpreadsheetCellRangeReference range = this.id();
        final SpreadsheetCellReference value = this.value();

        final S store = this.createStore();

        final List<SpreadsheetCellReference> added = Lists.array();
        final List<SpreadsheetCellReference> removed = Lists.array();

        store.addStoreWatcher(
            new MultiValueStoreWatcher<>() {
                @Override
                public void onValueAdded(final SpreadsheetCellRangeReference id,
                                         final SpreadsheetCellReference value) {
                    added.add(value);
                }

                @Override
                public void onValueRemoved(final SpreadsheetCellRangeReference id,
                                           final SpreadsheetCellReference value) {
                    removed.add(value);
                }
            }
        );

        store.addValue(range, value);

        this.checkEquals(
            Lists.of(value),
            added,
            "MultiValueStoreWatcher.onValueAdded"
        );

        store.delete(range);

        this.checkEquals(
            Lists.of(value),
            removed,
            "MultiValueStoreWatcher.onValueRemoved"
        );
    }

    // addValue.........................................................................................................

    @Test
    default void testAddValueWithNullRangeFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStore()
                .addValue(
                    null,
                    this.value()
                )
        );
    }

    @Test
    default void testAddValueWithNullValueFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStore()
                .addValue(
                    RANGE,
                    null
                )
        );
    }

    @Test
    default void testRemoveValueWithNullRangeFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStore()
                .removeValue(
                    null,
                    this.value()
                )
        );
    }

    @Test
    default void testRemoveValueWithNullValueFails() {
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

    default void findCellRangesIncludingCellFails(final SpreadsheetCellRangeStore store,
                                                  final SpreadsheetCellReference cell) {
        this.checkEquals(Sets.empty(),
            this.findCellRangesIncludingCell(store, cell),
            () -> "load cell " + cell + " should have returned no ranges");
    }

    default void findCellRangesIncludingCellAndCheck(final SpreadsheetCellRangeStore store,
                                                     final SpreadsheetCellReference cell,
                                                     final SpreadsheetCellRangeReference... ranges) {
        this.checkEquals(Sets.of(ranges),
            this.findCellRangesIncludingCell(store, cell),
            () -> "load cell reference ranges for " + cell);
    }

    default Set<SpreadsheetCellRangeReference> findCellRangesIncludingCell(final SpreadsheetCellRangeStore store,
                                                                           final SpreadsheetCellReference cell) {
        final Set<SpreadsheetCellRangeReference> ranges = store.findCellRangesIncludingCell(cell);
        assertNotNull(ranges, "ranges");
        return ranges;
    }

    // StoreTesting.....................................................................................................

    @Override
    default SpreadsheetCellRangeReference id() {
        return SpreadsheetSelection.parseCellRange("A1:B2");
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

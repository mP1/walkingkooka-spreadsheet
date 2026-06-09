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
import walkingkooka.Cast;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.store.ReadOnlyStoreTesting;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ReadOnlySpreadsheetCellRangeStoreTest implements SpreadsheetCellRangeStoreTesting<ReadOnlySpreadsheetCellRangeStore>,
    ReadOnlyStoreTesting<ReadOnlySpreadsheetCellRangeStore, SpreadsheetCellRangeReference, List<SpreadsheetCellReference>>,
    HashCodeEqualsDefinedTesting2<ReadOnlySpreadsheetCellRangeStore> {

    private final static SpreadsheetCellRangeReference RANGE = SpreadsheetSelection.parseCellRange("a1:b2");
    private final static SpreadsheetCellReference VALUE = SpreadsheetSelection.A1;

    @Test
    public void testSaveAndLoadRange() {
        final SpreadsheetCellRangeStore store = SpreadsheetCellRangeStores.treeMap();

        store.addValue(RANGE, VALUE);

        this.countAndCheck(store, 1);

        this.loadRangeAndCheck(ReadOnlySpreadsheetCellRangeStore.with(store), RANGE, VALUE);
    }

    @Override
    public void testAddStoreWatcherAndDelete() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testAddValueFails() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> this.createStore()
                .addValue(RANGE, VALUE)
        );
    }

    @Test
    public void testRemoveValueFails() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> this.createStore()
                .removeValue(RANGE, VALUE)
        );
    }

    @Test
    @Override
    public void testFindCellRangesWithValue() {
        final SpreadsheetCellRangeStore store = SpreadsheetCellRangeStores.treeMap();
        store.addValue(RANGE, VALUE);

        this.findCellRangesWithValueAndCheck(
            ReadOnlySpreadsheetCellRangeStore.with(store),
            VALUE,
            RANGE
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(ReadOnlySpreadsheetCellRangeStore.with(new FakeSpreadsheetCellRangeStore() {
            @Override
            public String toString() {
                return "ABC";
            }
        }), "ABC");
    }

    @Override
    public SpreadsheetCellReference valueValue() {
        return SpreadsheetCellReference.A1;
    }

    @Override
    public ReadOnlySpreadsheetCellRangeStore createStore() {
        return ReadOnlySpreadsheetCellRangeStore.with(SpreadsheetCellRangeStores.treeMap());
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEquals2() {
        final SpreadsheetCellRangeStore store1 = SpreadsheetCellRangeStores.treeMap();
        final SpreadsheetCellRangeStore store2 = SpreadsheetCellRangeStores.treeMap();

        final SpreadsheetCellRangeReference range = SpreadsheetSelection.parseCellRange("A1:B2");
        final SpreadsheetCellReference value = SpreadsheetCellReference.A1;

        store1.addValue(
            range,
            value
        );

        store2.addValue(
            range,
            value
        );

        this.checkEquals(
            store1,
            store2
        );
    }

    @Test
    public void testEqualsDifferent() {
        final SpreadsheetCellRangeStore different = SpreadsheetCellRangeStores.treeMap();

        different.addValue(
            SpreadsheetSelection.parseCellRange("A1:B2"),
            SpreadsheetSelection.parseCell("Z9")
        );

        this.checkNotEquals(
            ReadOnlySpreadsheetCellRangeStore.with(different)
        );
    }

    @Override
    public ReadOnlySpreadsheetCellRangeStore createObject() {
        return this.createStore();
    }

    // class............................................................................................................

    @Override
    public Class<ReadOnlySpreadsheetCellRangeStore> type() {
        return Cast.to(ReadOnlySpreadsheetCellRangeStore.class);
    }
}

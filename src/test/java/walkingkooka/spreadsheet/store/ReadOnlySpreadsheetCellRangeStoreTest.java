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
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.store.ReadOnlyStoreTesting;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ReadOnlySpreadsheetCellRangeStoreTest implements SpreadsheetCellRangeStoreTesting<ReadOnlySpreadsheetCellRangeStore<String>, String>,
    ReadOnlyStoreTesting<ReadOnlySpreadsheetCellRangeStore<String>, SpreadsheetCellRangeReference, List<String>> {

    private final static SpreadsheetCellRangeReference RANGE = SpreadsheetSelection.parseCellRange("a1:b2");
    private final static String VALUE = "value";

    @Test
    public void testSaveAndLoadRange() {
        final SpreadsheetCellRangeStore<String> store = SpreadsheetCellRangeStores.treeMap();

        store.addValue(RANGE, VALUE);

        this.countAndCheck(store, 1);

        this.loadRangeAndCheck(ReadOnlySpreadsheetCellRangeStore.with(store), RANGE, VALUE);
    }

    @Override
    public void testAddSaveWatcherAndSave() {
    }

    @Override
    public void testAddSaveWatcherAndSaveTwiceFiresOnce() {
    }

    @Override
    public void testAddSaveWatcherAndRemove() {
    }

    @Override
    public void testAddDeleteWatcherAndDelete() {
    }

    @Override
    public void testAddDeleteWatcherAndRemove() {
    }

    @Test
    public void testAddValueFails() {
        assertThrows(UnsupportedOperationException.class, () -> this.createStore().addValue(RANGE, VALUE));
    }

    @Test
    public void testReplacealueFails() {
        assertThrows(UnsupportedOperationException.class, () -> this.createStore().replaceValue(RANGE, "old", "new"));
    }

    @Test
    public void testRemoveValueFails() {
        assertThrows(UnsupportedOperationException.class, () -> this.createStore().removeValue(RANGE, VALUE));
    }

    @Test
    @Override
    public void testFindCellRangesWithValue() {
        final SpreadsheetCellRangeStore<String> store = SpreadsheetCellRangeStores.treeMap();
        store.addValue(RANGE, VALUE);
        this.findCellRangesWithValueAndCheck(ReadOnlySpreadsheetCellRangeStore.with(store), VALUE, RANGE);
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(ReadOnlySpreadsheetCellRangeStore.with(new FakeSpreadsheetCellRangeStore<String>() {
            @Override
            public String toString() {
                return "ABC";
            }
        }), "ABC");
    }

    @Override
    public String valueValue() {
        return "hello";
    }

    @Override
    public ReadOnlySpreadsheetCellRangeStore<String> createStore() {
        return ReadOnlySpreadsheetCellRangeStore.with(SpreadsheetCellRangeStores.treeMap());
    }

    @Override
    public Class<ReadOnlySpreadsheetCellRangeStore<String>> type() {
        return Cast.to(ReadOnlySpreadsheetCellRangeStore.class);
    }
}

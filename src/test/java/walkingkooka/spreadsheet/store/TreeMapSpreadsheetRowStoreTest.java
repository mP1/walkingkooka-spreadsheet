

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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetRow;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.TreeMap;

final class TreeMapSpreadsheetRowStoreTest extends SpreadsheetRowStoreTestCase<TreeMapSpreadsheetRowStore> 
implements HashCodeEqualsDefinedTesting2<TreeMapSpreadsheetRowStore> {

    @Test
    public void testLoadRows() {
        final TreeMapSpreadsheetRowStore store = this.createStore();

        final SpreadsheetRow row1 = SpreadsheetRow.with(
            SpreadsheetSelection.parseRow("1")
        );
        store.save(row1);

        final SpreadsheetRow row2 = SpreadsheetRow.with(
            SpreadsheetSelection.parseRow("2")
        );
        store.save(row2);

        final SpreadsheetRow row3 = SpreadsheetRow.with(
            SpreadsheetSelection.parseRow("3")
        );
        store.save(row3);

        final SpreadsheetRow row4 = SpreadsheetRow.with(
            SpreadsheetSelection.parseRow("4")
        );

        store.save(row4);

        this.loadRowsAndCheck(
            store,
            SpreadsheetSelection.parseRowRange("2:3"),
            row2,
            row3
        );
    }

    @Test
    public void testSaveRows() {
        final TreeMapSpreadsheetRowStore store = this.createStore();

        final SpreadsheetRow row1 = SpreadsheetRow.with(
            SpreadsheetSelection.parseRow("1")
        );
        final SpreadsheetRow row2 = SpreadsheetRow.with(
            SpreadsheetSelection.parseRow("2")
        );

        store.saveRows(
            Sets.of(
                row1,
                row2
            )
        );

        this.loadRowsAndCheck(
            store,
            SpreadsheetSelection.parseRowRange("1:2"),
            row1,
            row2
        );
    }

    @Override
    public TreeMapSpreadsheetRowStore createStore() {
        return TreeMapSpreadsheetRowStore.create();
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEquals2() {
        final TreeMapSpreadsheetRowStore store1 = this.createStore();
        final TreeMapSpreadsheetRowStore store2 = this.createStore();

        final SpreadsheetRow row = SpreadsheetRow.with(
            SpreadsheetSelection.parseRow("1")
        );

        store1.save(row);
        store2.save(row);

        this.checkEquals(
            store1,
            store2
        );
    }

    @Test
    public void testEqualsDifferent() {
        final TreeMapSpreadsheetRowStore different = this.createStore();

        different.save(
            SpreadsheetRow.with(
                SpreadsheetSelection.parseRow("23")
            )
        );

        this.checkNotEquals(different);
    }

    @Override
    public TreeMapSpreadsheetRowStore createObject() {
        return this.createStore();
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        final TreeMapSpreadsheetRowStore store = this.createStore();
        store.save(
            SpreadsheetSelection.parseRow("2").row()
        );

        this.toStringAndCheck(store, "[2]");
    }

    // class............................................................................................................

    @Override
    public Class<TreeMapSpreadsheetRowStore> type() {
        return TreeMapSpreadsheetRowStore.class;
    }

    @Override
    public String typeNamePrefix() {
        return TreeMap.class.getSimpleName();
    }
}

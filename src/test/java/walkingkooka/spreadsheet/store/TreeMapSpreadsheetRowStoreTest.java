

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
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.TreeMap;

final class TreeMapSpreadsheetRowStoreTest extends SpreadsheetRowStoreTestCase<TreeMapSpreadsheetRowStore> {

    // upSkipHidden...................................................................................................

    @Test
    public void testUpSkipHidden() {
        this.upSkipHiddenAndCheck(
                this.createStore(),
                "2",
                "1"
        );
    }

    @Test
    public void testUpSkipHidden2() {
        final TreeMapSpreadsheetRowStore store = this.createStore();
        store.save(SpreadsheetSelection.parseRow("3").row());

        this.upSkipHiddenAndCheck(
                store,
                "4",
                "3"
        );
    }

    @Test
    public void testUpSkipHiddenSkips() {
        final TreeMapSpreadsheetRowStore store = this.createStore();
        store.save(SpreadsheetSelection.parseRow("3").row().setHidden(true));
        store.save(SpreadsheetSelection.parseRow("4").row().setHidden(true));

        this.upSkipHiddenAndCheck(
                store,
                "5",
                "2"
        );
    }

    @Test
    public void testUpSkipHiddenSkipsFirstRow() {
        final TreeMapSpreadsheetRowStore store = this.createStore();
        store.save(SpreadsheetSelection.parseRow("2").row().setHidden(true));
        store.save(SpreadsheetSelection.parseRow("3").row().setHidden(true));

        this.upSkipHiddenAndCheck(
                store,
                "4",
                "1"
        );
    }

    @Test
    public void testUpSkipHiddenAllUpHidden() {
        final TreeMapSpreadsheetRowStore store = this.createStore();
        store.save(SpreadsheetSelection.parseRow("1").row().setHidden(true));
        store.save(SpreadsheetSelection.parseRow("2").row().setHidden(true));

        this.upSkipHiddenAndCheck(
                store,
                "3"
        );
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

    @Override
    public TreeMapSpreadsheetRowStore createStore() {
        return TreeMapSpreadsheetRowStore.create();
    }

    @Override
    public Class<TreeMapSpreadsheetRowStore> type() {
        return TreeMapSpreadsheetRowStore.class;
    }

    // TypeNameTesting..................................................................

    @Override
    public String typeNamePrefix() {
        return TreeMap.class.getSimpleName();
    }
}

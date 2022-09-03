

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
import walkingkooka.spreadsheet.SpreadsheetRow;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.TreeMap;

final class TreeMapSpreadsheetRowStoreTest extends SpreadsheetRowStoreTestCase<TreeMapSpreadsheetRowStore> {

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

    // upSkipHidden...................................................................................................

    @Test
    public void testUpTopHidden() {
        final TreeMapSpreadsheetRowStore store = this.createStore();
        store.save(SpreadsheetSelection.parseRow("1").row().setHidden(true));

        this.upSkipHiddenAndCheck(
                store,
                "1"
        );
    }

    @Test
    public void testUpTopAllHidden() {
        final TreeMapSpreadsheetRowStore store = this.createStore();
        store.save(SpreadsheetSelection.parseRow("1").row().setHidden(true));
        store.save(SpreadsheetSelection.parseRow("2").row().setHidden(true));
        store.save(SpreadsheetSelection.parseRow("3").row().setHidden(true));

        this.upSkipHiddenAndCheck(
                store,
                "3"
        );
    }

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
                "3",
                "3"
        );
    }

    // downSkipHidden...................................................................................................

    @Test
    public void testDownSkipHiddenLastHidden() {
        final SpreadsheetRowReference last = SpreadsheetReferenceKind.RELATIVE.lastRow();

        final TreeMapSpreadsheetRowStore store = this.createStore();
        store.save(last.row().setHidden(true));

        this.downSkipHiddenAndCheck(
                store,
                last
        );
    }

    @Test
    public void testDownSkipHiddenLastAllHidden() {
        final SpreadsheetRowReference last = SpreadsheetReferenceKind.RELATIVE.lastRow();

        final TreeMapSpreadsheetRowStore store = this.createStore();
        store.save(last.row().setHidden(true));
        store.save(last.add(-1).row().setHidden(true));
        store.save(last.add(-2).row().setHidden(true));

        this.downSkipHiddenAndCheck(
                store,
                last.add(-2)
        );
    }

    @Test
    public void testDownSkipHidden() {
        this.downSkipHiddenAndCheck(
                this.createStore(),
                "2",
                "3"
        );
    }

    @Test
    public void testDownSkipHidden2() {
        final TreeMapSpreadsheetRowStore store = this.createStore();
        store.save(SpreadsheetSelection.parseRow("3").row().setHidden(true));

        this.downSkipHiddenAndCheck(
                store,
                "2",
                "4"
        );
    }

    @Test
    public void testDownSkipHiddenSkips() {
        final TreeMapSpreadsheetRowStore store = this.createStore();
        store.save(SpreadsheetSelection.parseRow("3").row().setHidden(true));
        store.save(SpreadsheetSelection.parseRow("4").row().setHidden(true));

        this.downSkipHiddenAndCheck(
                store,
                "2",
                "5"
        );
    }

    @Test
    public void testDownSkipHiddenSkipsLastRow() {
        final TreeMapSpreadsheetRowStore store = this.createStore();

        final SpreadsheetRowReference last = SpreadsheetReferenceKind.RELATIVE.lastRow();

        store.save(last.add(-2).row().setHidden(true));
        store.save(last.add(-1).row().setHidden(true));

        this.downSkipHiddenAndCheck(
                store,
                last.add(-3),
                last
        );
    }

    @Test
    public void testDownSkipHiddenAllDownHidden() {
        final TreeMapSpreadsheetRowStore store = this.createStore();

        final SpreadsheetRowReference last = SpreadsheetReferenceKind.RELATIVE.lastRow();

        store.save(last.add(-2).row());
        store.save(last.add(-1).row().setHidden(true));
        store.save(last.row().setHidden(true));

        this.downSkipHiddenAndCheck(
                store,
                last.add(-2),
                last.add(-2)
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

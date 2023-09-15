
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
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.TreeMap;

final class TreeMapSpreadsheetColumnStoreTest extends SpreadsheetColumnStoreTestCase<TreeMapSpreadsheetColumnStore> {

    @Test
    public void testLoadColumns() {
        final TreeMapSpreadsheetColumnStore store = this.createStore();

        final SpreadsheetColumn a = SpreadsheetColumn.with(
                SpreadsheetSelection.parseColumn("A")
        );
        store.save(a);

        final SpreadsheetColumn b = SpreadsheetColumn.with(
                SpreadsheetSelection.parseColumn("B")
        );
        store.save(b);

        final SpreadsheetColumn c = SpreadsheetColumn.with(
                SpreadsheetSelection.parseColumn("C")
        );
        store.save(c);

        final SpreadsheetColumn d = SpreadsheetColumn.with(
                SpreadsheetSelection.parseColumn("D")
        );

        store.save(d);

        this.loadColumnsAndCheck(
                store,
                SpreadsheetSelection.parseColumnRange("B:C"),
                b,
                c
        );
    }

    @Test
    public void testSaveColumns() {
        final TreeMapSpreadsheetColumnStore store = this.createStore();

        final SpreadsheetColumn a = SpreadsheetColumn.with(
                SpreadsheetSelection.parseColumn("A")
        );
        final SpreadsheetColumn b = SpreadsheetColumn.with(
                SpreadsheetSelection.parseColumn("B")
        );

        store.saveColumns(Sets.of(a, b));

        this.checkEquals(
                Sets.of(
                        a, b
                ),
                store.loadColumns(SpreadsheetSelection.parseColumnRange("A:B"))
        );
    }

    // leftColumnSkipHidden...................................................................................................

    @Test
    public void testLeftFirstColumnHidden() {
        final TreeMapSpreadsheetColumnStore store = this.createStore();
        store.save(SpreadsheetSelection.parseColumn("A").column().setHidden(true));

        this.leftColumnSkipHiddenAndCheck(
                store,
                "A"
        );
    }

    @Test
    public void testLeftAllColumnsHiddenIncludingGiven() {
        final TreeMapSpreadsheetColumnStore store = this.createStore();
        store.save(SpreadsheetSelection.parseColumn("A").column().setHidden(true));
        store.save(SpreadsheetSelection.parseColumn("B").column().setHidden(true));
        store.save(SpreadsheetSelection.parseColumn("C").column().setHidden(true));

        this.leftColumnSkipHiddenAndCheck(
                store,
                "C"
        );
    }

    @Test
    public void testLeftSkipHidden() {
        this.leftColumnSkipHiddenAndCheck(
                this.createStore(),
                "B",
                "A"
        );
    }

    @Test
    public void testLeftSkipHidden2() {
        final TreeMapSpreadsheetColumnStore store = this.createStore();
        store.save(SpreadsheetSelection.parseColumn("C").column());

        this.leftColumnSkipHiddenAndCheck(
                store,
                "D",
                "C"
        );
    }

    @Test
    public void testLeftSkipHiddenFirstColumn() {
        this.leftColumnSkipHiddenAndCheck(
                this.createStore(),
                "A",
                "A"
        );
    }

    @Test
    public void testLeftSkipHiddenSkips() {
        final TreeMapSpreadsheetColumnStore store = this.createStore();
        store.save(SpreadsheetSelection.parseColumn("C").column().setHidden(true));
        store.save(SpreadsheetSelection.parseColumn("D").column().setHidden(true));

        this.leftColumnSkipHiddenAndCheck(
                store,
                "E",
                "B"
        );
    }

    @Test
    public void testLeftSkipHiddenSkipsFirstColumn() {
        final TreeMapSpreadsheetColumnStore store = this.createStore();
        store.save(SpreadsheetSelection.parseColumn("B").column().setHidden(true));
        store.save(SpreadsheetSelection.parseColumn("C").column().setHidden(true));

        this.leftColumnSkipHiddenAndCheck(
                store,
                "D",
                "A"
        );
    }

    @Test
    public void testLeftSkipHiddenAllLeftHidden() {
        final TreeMapSpreadsheetColumnStore store = this.createStore();
        store.save(SpreadsheetSelection.parseColumn("A").column().setHidden(true));
        store.save(SpreadsheetSelection.parseColumn("B").column().setHidden(true));

        this.leftColumnSkipHiddenAndCheck(
                store,
                "C",
                "C"
        );
    }

    // rightSkipHidden...................................................................................................

    @Test
    public void testRightLastColumnHidden() {
        final SpreadsheetColumnReference last = SpreadsheetReferenceKind.RELATIVE.lastColumn();

        final TreeMapSpreadsheetColumnStore store = this.createStore();
        store.save(last.column().setHidden(true));

        this.rightSkipHiddenAndCheck(
                store,
                last
        );
    }

    @Test
    public void testRightAllColumnsHiddenIncludingGiven() {
        final TreeMapSpreadsheetColumnStore store = this.createStore();

        final SpreadsheetColumnReference last = SpreadsheetReferenceKind.RELATIVE.lastColumn();

        store.save(last.add(-2).column().setHidden(true));
        store.save(last.add(-1).column().setHidden(true));
        store.save(last.column().setHidden(true));

        this.rightSkipHiddenAndCheck(
                store,
                last.add(-2)
        );
    }

    @Test
    public void testRightSkipHidden() {
        this.rightSkipHiddenAndCheck(
                this.createStore(),
                "B",
                "C"
        );
    }

    @Test
    public void testRightSkipHidden2() {
        final TreeMapSpreadsheetColumnStore store = this.createStore();
        store.save(SpreadsheetSelection.parseColumn("C").column().setHidden(true));

        this.rightSkipHiddenAndCheck(
                store,
                "B",
                "D"
        );
    }

    @Test
    public void testRightSkipHiddenSkips() {
        final TreeMapSpreadsheetColumnStore store = this.createStore();
        store.save(SpreadsheetSelection.parseColumn("C").column().setHidden(true));
        store.save(SpreadsheetSelection.parseColumn("D").column().setHidden(true));

        this.rightSkipHiddenAndCheck(
                store,
                "B",
                "E"
        );
    }

    @Test
    public void testRightSkipHiddenSkipsLastColumn() {
        final TreeMapSpreadsheetColumnStore store = this.createStore();

        final SpreadsheetColumnReference last = SpreadsheetReferenceKind.RELATIVE.lastColumn();

        store.save(last.add(-2).column().setHidden(true));
        store.save(last.add(-1).column().setHidden(true));

        this.rightSkipHiddenAndCheck(
                store,
                last.add(-3),
                last
        );
    }

    @Test
    public void testRightSkipHiddenAllRightHidden() {
        final TreeMapSpreadsheetColumnStore store = this.createStore();

        final SpreadsheetColumnReference last = SpreadsheetReferenceKind.RELATIVE.lastColumn();

        store.save(last.add(-1).column().setHidden(true));
        store.save(last.column().setHidden(true));

        this.rightSkipHiddenAndCheck(
                store,
                last.add(-2),
                last.add(-2)
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        final TreeMapSpreadsheetColumnStore store = this.createStore();
        store.save(
                SpreadsheetSelection.parseColumn("B")
                        .column()
        );

        this.toStringAndCheck(store, "[B]");
    }

    @Override
    public TreeMapSpreadsheetColumnStore createStore() {
        return TreeMapSpreadsheetColumnStore.create();
    }

    @Override
    public Class<TreeMapSpreadsheetColumnStore> type() {
        return TreeMapSpreadsheetColumnStore.class;
    }

    // TypeNameTesting..................................................................

    @Override
    public String typeNamePrefix() {
        return TreeMap.class.getSimpleName();
    }
}

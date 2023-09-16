
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

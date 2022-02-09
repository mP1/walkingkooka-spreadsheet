
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
import walkingkooka.spreadsheet.reference.SpreadsheetColumn;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.TreeMap;

final class TreeMapSpreadsheetColumnStoreTest extends SpreadsheetColumnStoreTestCase<TreeMapSpreadsheetColumnStore> {

    // toString.........................................................................................................

    @Test
    public void testToString() {
        final TreeMapSpreadsheetColumnStore store = this.createStore();
        store.save(
                SpreadsheetColumn.with(
                        SpreadsheetSelection.parseColumn("B")
                )
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

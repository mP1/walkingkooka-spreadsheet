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

package walkingkooka.spreadsheet.store.cell;

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetCellReference;
import walkingkooka.spreadsheet.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.SpreadsheetFormula;

import java.util.TreeMap;

final class TreeMapSpreadsheetCellStoreTest extends SpreadsheetCellStoreTestCase<TreeMapSpreadsheetCellStore> {

    @Test
    public void testToString() {
        final TreeMapSpreadsheetCellStore store = this.createStore();
        store.save(SpreadsheetCell.with(SpreadsheetExpressionReference.parseCellReference("A1"), SpreadsheetFormula.with("1+2")));

        this.toStringAndCheck(store, "[A1=1+2]");
    }

    @Override
    public TreeMapSpreadsheetCellStore createStore() {
        return TreeMapSpreadsheetCellStore.create();
    }

    @Override
    public Class<TreeMapSpreadsheetCellStore> type() {
        return TreeMapSpreadsheetCellStore.class;
    }

    // TypeNameTesting..................................................................

    @Override
    public String typeNamePrefix() {
        return TreeMap.class.getSimpleName();
    }
}

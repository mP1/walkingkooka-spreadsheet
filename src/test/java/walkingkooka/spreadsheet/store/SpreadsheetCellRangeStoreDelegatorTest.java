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

import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.store.SpreadsheetCellRangeStoreDelegatorTest.TestSpreadsheetCellRangeStoreDelegator;

public final class SpreadsheetCellRangeStoreDelegatorTest implements SpreadsheetCellRangeStoreTesting<TestSpreadsheetCellRangeStoreDelegator> {

    @Override
    public void testTestNaming() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String typeNamePrefix() {
        return "";
    }

    @Override
    public TestSpreadsheetCellRangeStoreDelegator createStore() {
        return new TestSpreadsheetCellRangeStoreDelegator();
    }

    @Override
    public SpreadsheetCellReference value() {
        return SpreadsheetSelection.A1;
    }

    @Override
    public Class<TestSpreadsheetCellRangeStoreDelegator> type() {
        return TestSpreadsheetCellRangeStoreDelegator.class;
    }

    final static class TestSpreadsheetCellRangeStoreDelegator implements SpreadsheetCellRangeStoreDelegator {

        private TestSpreadsheetCellRangeStoreDelegator() {
            super();
        }

        @Override
        public SpreadsheetCellRangeStore spreadsheetCellRangeStore() {
            return this.store;
        }

        private final SpreadsheetCellRangeStore store = SpreadsheetCellRangeStores.treeMap();

        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }
    }
}

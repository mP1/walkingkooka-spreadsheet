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

package walkingkooka.spreadsheet.reference.store;

import walkingkooka.reflect.PublicStaticHelper;

/**
 * Contains many factory methods for a variety of {@link SpreadsheetCellRangeStore} implementations.
 */
public final class SpreadsheetCellRangeStores implements PublicStaticHelper {

    /**
     * {@see FakeSpreadsheetCellRangeStore}
     */
    public static <V> SpreadsheetCellRangeStore<V> fake() {
        return new FakeSpreadsheetCellRangeStore<>();
    }

    /**
     * {@see ReadOnlySpreadsheetCellRangeStore}
     */
    public static <V> SpreadsheetCellRangeStore<V> readOnly(final SpreadsheetCellRangeStore<V> store) {
        return ReadOnlySpreadsheetCellRangeStore.with(store);
    }

    /**
     * {@see TreeMapSpreadsheetCellRangeStore}
     */
    public static <V> SpreadsheetCellRangeStore<V> treeMap() {
        return TreeMapSpreadsheetCellRangeStore.create();
    }

    /**
     * Stop creation
     */
    private SpreadsheetCellRangeStores() {
        throw new UnsupportedOperationException();
    }
}

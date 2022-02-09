


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

import walkingkooka.reflect.PublicStaticHelper;

/**
 * Contains many factory methods for a variety of {@link SpreadsheetRowStore} implementations.
 */
public final class SpreadsheetRowStores implements PublicStaticHelper {

    /**
     * {@see FakeSpreadsheetRowStore}
     */
    public static SpreadsheetRowStore fake() {
        return new FakeSpreadsheetRowStore();
    }

    /**
     * {@see TreeMapSpreadsheetRowStore}
     */
    public static SpreadsheetRowStore treeMap() {
        return TreeMapSpreadsheetRowStore.create();
    }

    /**
     * Stop creation
     */
    private SpreadsheetRowStores() {
        throw new UnsupportedOperationException();
    }
}

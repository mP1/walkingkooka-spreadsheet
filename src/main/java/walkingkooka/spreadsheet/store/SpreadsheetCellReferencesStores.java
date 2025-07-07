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
 * Contains many factory methods for a variety of {@link SpreadsheetCellReferencesStore} implementations.
 */
public final class SpreadsheetCellReferencesStores implements PublicStaticHelper {

    /**
     * {@see FakeSpreadsheetCellReferencesStore}
     */
    public static SpreadsheetCellReferencesStore fake() {
        return new FakeSpreadsheetCellReferencesStore();
    }

    /**
     * {@see TreeMapSpreadsheetCellReferencesStore}
     */
    public static SpreadsheetCellReferencesStore treeMap() {
        return TreeMapSpreadsheetCellReferencesStore.empty();
    }

    /**
     * Stop creation
     */
    private SpreadsheetCellReferencesStores() {
        throw new UnsupportedOperationException();
    }
}

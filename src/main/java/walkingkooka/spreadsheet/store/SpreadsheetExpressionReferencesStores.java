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
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;

/**
 * Contains many factory methods for a variety of {@link SpreadsheetExpressionReferencesStore} implementations.
 */
public final class SpreadsheetExpressionReferencesStores implements PublicStaticHelper {

    /**
     * {@see FakeSpreadsheetExpressionReferencesStore}
     */
    public static <T extends SpreadsheetExpressionReference> SpreadsheetExpressionReferencesStore<T> fake() {
        return new FakeSpreadsheetExpressionReferencesStore<>();
    }

    /**
     * {@see ReadOnlySpreadsheetExpressionReferencesStore}
     */
    public static <T extends SpreadsheetExpressionReference> SpreadsheetExpressionReferencesStore<T> readOnly(final SpreadsheetExpressionReferencesStore<T> store) {
        return ReadOnlySpreadsheetExpressionReferencesStore.with(store);
    }

    /**
     * {@see TreeMapSpreadsheetExpressionReferencesStore}
     */
    public static <T extends SpreadsheetExpressionReference & Comparable<T>> SpreadsheetExpressionReferencesStore<T> treeMap() {
        return TreeMapSpreadsheetExpressionReferencesStore.create();
    }

    /**
     * Stop creation
     */
    private SpreadsheetExpressionReferencesStores() {
        throw new UnsupportedOperationException();
    }
}

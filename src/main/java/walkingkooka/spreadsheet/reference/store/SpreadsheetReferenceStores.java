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

import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.type.PublicStaticHelper;

/**
 * Contains many factory methods for a variety of {@link SpreadsheetReferenceStore} implementations.
 */
public final class SpreadsheetReferenceStores implements PublicStaticHelper {

    /**
     * {@see FakeSpreadsheetReferenceStore}
     */
    public static <T extends ExpressionReference & Comparable<T>> SpreadsheetReferenceStore<T> fake() {
        return new FakeSpreadsheetReferenceStore<>();
    }

    /**
     * {@see ReadOnlySpreadsheetReferenceStore}
     */
    public static <T extends ExpressionReference & Comparable<T>> SpreadsheetReferenceStore<T> readOnly(final SpreadsheetReferenceStore<T> store) {
        return ReadOnlySpreadsheetReferenceStore.with(store);
    }

    /**
     * {@see TreeMapSpreadsheetReferenceStore}
     */
    public static <T extends ExpressionReference & Comparable<T>> SpreadsheetReferenceStore<T> treeMap() {
        return TreeMapSpreadsheetReferenceStore.create();
    }

    /**
     * Stop creation
     */
    private SpreadsheetReferenceStores() {
        throw new UnsupportedOperationException();
    }
}

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
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceComparable;

/**
 * Contains many factory methods for a variety of {@link SpreadsheetExpressionReferenceStore} implementations.
 */
public final class SpreadsheetExpressionReferenceStores implements PublicStaticHelper {

    /**
     * {@see FakeSpreadsheetExpressionReferenceStore}
     */
    public static <T extends SpreadsheetExpressionReferenceComparable<T>> SpreadsheetExpressionReferenceStore<T> fake() {
        return new FakeSpreadsheetExpressionReferenceStore<>();
    }

    /**
     * {@see ReadOnlySpreadsheetExpressionReferenceStore}
     */
    public static <T extends SpreadsheetExpressionReferenceComparable<T>> SpreadsheetExpressionReferenceStore<T> readOnly(final SpreadsheetExpressionReferenceStore<T> store) {
        return ReadOnlySpreadsheetExpressionReferenceStore.with(store);
    }

    /**
     * {@see TreeMapSpreadsheetExpressionReferenceStore}
     */
    public static <T extends SpreadsheetExpressionReferenceComparable<T>> SpreadsheetExpressionReferenceStore<T> treeMap() {
        return TreeMapSpreadsheetExpressionReferenceStore.create();
    }

    /**
     * Stop creation
     */
    private SpreadsheetExpressionReferenceStores() {
        throw new UnsupportedOperationException();
    }
}

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

import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.store.MissingStoreException;
import walkingkooka.store.Store;

public interface SpreadsheetStore<K, V> extends Store<K, V> {

    /**
     * Used to report that a label was not found.
     */
    @Override
    default MissingStoreException notFound(final Object reference) {
        return reference instanceof SpreadsheetExpressionReference ?
                this.notFound((SpreadsheetExpressionReference) reference) :
                Store.super.notFound(reference);
    }

    /**
     * Used to report a {@link SpreadsheetExpressionReferenceStore} was not found.
     * The {@link SpreadsheetExpressionReferenceMissingStoreException} if caught should result in the cell holding a
     * <pre>#REF!</pre> error.
     */
    default MissingStoreException notFound(final SpreadsheetExpressionReference reference) {
        return new SpreadsheetExpressionReferenceMissingStoreException(reference);
    }
}

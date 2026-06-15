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
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;

import java.util.Objects;

/**
 * A read only wrapper around a {@link SpreadsheetExpressionReferencesStore}
 */
final class ReadOnlySpreadsheetExpressionReferencesStore<T extends SpreadsheetExpressionReference>
    implements SpreadsheetExpressionReferencesStoreDelegator<T> {

    static <T extends SpreadsheetExpressionReference> ReadOnlySpreadsheetExpressionReferencesStore<T> with(final SpreadsheetExpressionReferencesStore<T> store) {
        Objects.requireNonNull(store, "store");
        return new ReadOnlySpreadsheetExpressionReferencesStore<>(store);
    }

    private ReadOnlySpreadsheetExpressionReferencesStore(final SpreadsheetExpressionReferencesStore<T> store) {
        this.store = store;
    }

    @Override
    public void delete(final T reference) {
        Objects.requireNonNull(reference, "reference");

        throw new UnsupportedOperationException();
    }

    @Override
    public void addValue(final T reference,
                         final SpreadsheetCellReference cell) {
        Objects.requireNonNull(reference, "reference");
        Objects.requireNonNull(cell, "cell");

        throw new UnsupportedOperationException();
    }

    @Override
    public void removeValue(final T reference,
                            final SpreadsheetCellReference cell) {
        Objects.requireNonNull(reference, "reference");
        Objects.requireNonNull(cell, "cell");

        throw new UnsupportedOperationException();
    }

    @Override
    public void removeByValue(final SpreadsheetCellReference cell) {
        Objects.requireNonNull(cell, "cell");

        throw new UnsupportedOperationException();
    }

    // SpreadsheetExpressionReferencesStoreDelegator....................................................................

    @Override
    public SpreadsheetExpressionReferencesStore<T> spreadsheetExpressionReferencesStore() {
        return this.store;
    }

    private final SpreadsheetExpressionReferencesStore<T> store;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.store.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            (other instanceof ReadOnlySpreadsheetExpressionReferencesStore &&
                this.equals0((ReadOnlySpreadsheetExpressionReferencesStore<?>) other));
    }

    private boolean equals0(final ReadOnlySpreadsheetExpressionReferencesStore<?> other) {
        return this.store.equals(other.store);
    }

    @Override
    public String toString() {
        return this.store.toString();
    }
}

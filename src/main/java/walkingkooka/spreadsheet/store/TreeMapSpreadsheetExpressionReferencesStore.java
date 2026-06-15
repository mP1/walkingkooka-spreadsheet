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

import walkingkooka.Cast;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.store.MultiValueStore;
import walkingkooka.store.MultiValueStoreDelegator;
import walkingkooka.store.MultiValueStores;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * A {@link SpreadsheetExpressionReferencesStore} that uses a {@link Map} to store a cell or label to its many {@link SpreadsheetCellReference references}.
 */
// using a type parameter of T extends SpreadsheetExpressionReference & Comparable<T> causes a Transpiler ERROR.
// Error:TreeMapSpreadsheetExpressionReferencesStore.java:39: This class must implement the inherited abstract method SpreadsheetExpressionReference.equalsIgnoreReferenceK
final class TreeMapSpreadsheetExpressionReferencesStore<T extends SpreadsheetExpressionReference> implements SpreadsheetExpressionReferencesStore<T>,
    MultiValueStoreDelegator<T, SpreadsheetCellReference> {

    static <T extends SpreadsheetExpressionReference> TreeMapSpreadsheetExpressionReferencesStore<T> create() {
        return new TreeMapSpreadsheetExpressionReferencesStore<>();
    }

    private TreeMapSpreadsheetExpressionReferencesStore() {
        super();

        this.store = MultiValueStores.treeMap(
            Cast.to(SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR),
            EMPTY_VALUES_SET_SUPPLIER
        );
    }

    private final static Supplier<Set<SpreadsheetCellReference>> EMPTY_VALUES_SET_SUPPLIER = () -> SortedSets.tree(SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR);

    // addValue/removeValue make id and value relative before delegating...

    @Override
    public void addValue(final T id,
                         final SpreadsheetCellReference value) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(value, "value");

        this.multiValueStore()
            .addValue(
                (T) id.toRelative(),
                value.toRelative()
            );
    }

    @Override
    public void removeValue(final T id,
                            final SpreadsheetCellReference value) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(value, "value");

        this.multiValueStore()
            .removeValue(
                (T) id.toRelative(),
                value.toRelative()
            );
    }

    // MultiValueStoreDelegator.........................................................................................

    @Override
    public MultiValueStore<T, SpreadsheetCellReference> multiValueStore() {
        return this.store;
    }

    private final MultiValueStore<T, SpreadsheetCellReference> store;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.store.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            (other instanceof TreeMapSpreadsheetExpressionReferencesStore &&
                this.equals0((TreeMapSpreadsheetExpressionReferencesStore<?>) other));
    }

    private boolean equals0(final TreeMapSpreadsheetExpressionReferencesStore<?> other) {
       return this.store.equals(other.store);
    }

    @Override
    public String toString() {
        return this.store.toString();
    }
}

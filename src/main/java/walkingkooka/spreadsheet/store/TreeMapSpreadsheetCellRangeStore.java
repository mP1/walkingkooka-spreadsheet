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

import walkingkooka.collect.set.ImmutableSet;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.store.MultiValueStore;
import walkingkooka.store.MultiValueStoreDelegator;
import walkingkooka.store.MultiValueStores;

import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * A {@link SpreadsheetCellRangeStore} that wraps a {@link walkingkooka.store.TreeMapMultiValueStore}.
 */
final class TreeMapSpreadsheetCellRangeStore implements SpreadsheetCellRangeStore,
    MultiValueStoreDelegator<SpreadsheetCellRangeReference, SpreadsheetCellReference> {

    /**
     * Factory that creates a new {@link TreeMapSpreadsheetCellRangeStore}
     */
    static TreeMapSpreadsheetCellRangeStore create() {
        return new TreeMapSpreadsheetCellRangeStore();
    }

    /**
     * Private ctor.
     */
    private TreeMapSpreadsheetCellRangeStore() {
        super();

        this.store = MultiValueStores.treeMap(
            (SpreadsheetCellRangeReference left, SpreadsheetCellRangeReference right) -> left.compareTo(right),
            EMPTY_VALUE_SUPPLIER
        );
    }

    private final static Supplier<Set<SpreadsheetCellReference>> EMPTY_VALUE_SUPPLIER = () -> SortedSets.tree(SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR);

    @Override
    public void addValue(final SpreadsheetCellRangeReference id,
                         final SpreadsheetCellReference value) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(value, "value");

        MultiValueStoreDelegator.super.addValue(
            id.toRelative(),
            value.toRelative()
        );
    }

    @Override
    public Set<SpreadsheetCellRangeReference> findCellRangesIncludingCell(final SpreadsheetCellReference cell) {
        Objects.requireNonNull(cell, "cell");

        return this.store.ids(
            0,
            Integer.MAX_VALUE
        ).stream()
            .filter(
                (SpreadsheetCellRangeReference cellRange) -> cellRange.testCell(cell)
            ).collect(ImmutableSet.collector());
    }

    @Override
    public Set<SpreadsheetCellRangeReference> findCellRangesWithValue(final SpreadsheetCellReference value) {
        Objects.requireNonNull(value, "cell");

        return this.store.ids(
                0,
                Integer.MAX_VALUE
            ).stream()
            .filter(
                (SpreadsheetCellRangeReference cellRange) -> this.store.findValuesById(
                    cellRange,
                    0,
                    Integer.MAX_VALUE
                ).contains(value)
            ).collect(ImmutableSet.collector());
    }

    @Override
    public MultiValueStore<SpreadsheetCellRangeReference, SpreadsheetCellReference> multiValueStore() {
        return this.store;
    }

    private final MultiValueStore<SpreadsheetCellRangeReference, SpreadsheetCellReference> store;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.store.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            (other instanceof TreeMapSpreadsheetCellRangeStore &&
                this.equals0((TreeMapSpreadsheetCellRangeStore) other));
    }

    private boolean equals0(final TreeMapSpreadsheetCellRangeStore other) {
        return this.store.equals(other.store);
    }

    @Override
    public String toString() {
        return this.store.toString();
    }
}

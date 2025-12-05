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

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * A read only wrapper around a {@link SpreadsheetExpressionReferenceStore}
 */
final class ReadOnlySpreadsheetExpressionReferenceStore<T extends SpreadsheetExpressionReference>
    implements SpreadsheetExpressionReferenceStore<T> {

    static <T extends SpreadsheetExpressionReference> ReadOnlySpreadsheetExpressionReferenceStore<T> with(final SpreadsheetExpressionReferenceStore<T> store) {
        Objects.requireNonNull(store, "store");
        return new ReadOnlySpreadsheetExpressionReferenceStore<>(store);
    }

    private ReadOnlySpreadsheetExpressionReferenceStore(final SpreadsheetExpressionReferenceStore<T> store) {
        this.store = store;
    }

    @Override
    public Optional<Set<SpreadsheetCellReference>> load(final T id) {
        return this.store.load(id);
    }

    @Override
    public Set<SpreadsheetCellReference> save(final Set<SpreadsheetCellReference> cells) {
        Objects.requireNonNull(cells, "cells");
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(final T reference) {
        Objects.requireNonNull(reference, "reference");

        throw new UnsupportedOperationException();
    }

    @Override
    public Runnable addDeleteWatcher(final Consumer<T> deleted) {
        Objects.requireNonNull(deleted, "deleted");
        throw new UnsupportedOperationException();
    }

    @Override
    public int count() {
        return this.store.count();
    }

    @Override
    public Set<T> ids(final int offset,
                      final int count) {
        return this.store.ids(
            offset,
            count
        );
    }

    @Override
    public List<Set<SpreadsheetCellReference>> values(final int offset,
                                                      final int count) {
        return this.store.values(
            offset,
            count
        );
    }

    @Override
    public List<Set<SpreadsheetCellReference>> between(final T from,
                                                       final T to) {
        return this.store.between(
            from,
            to
        );
    }

    @Override
    public void saveCells(final T reference,
                          final Set<SpreadsheetCellReference> cells) {
        Objects.requireNonNull(reference, "reference");
        Objects.requireNonNull(cells, "cells");

        throw new UnsupportedOperationException();
    }

    @Override
    public void addCell(final ReferenceAndSpreadsheetCellReference<T> referenceAndCell) {
        checkReferenceAndSpreadsheetCell(referenceAndCell);

        throw new UnsupportedOperationException();
    }

    @Override
    public Runnable addAddCellWatcher(final Consumer<ReferenceAndSpreadsheetCellReference<T>> watcher) {
        Objects.requireNonNull(watcher, "watcher");

        throw new UnsupportedOperationException();
    }

    @Override
    public void removeCell(final ReferenceAndSpreadsheetCellReference<T> referenceAndCell) {
        checkReferenceAndSpreadsheetCell(referenceAndCell);

        throw new UnsupportedOperationException();
    }

    @Override
    public Runnable addRemoveCellWatcher(final Consumer<ReferenceAndSpreadsheetCellReference<T>> watcher) {
        Objects.requireNonNull(watcher, "watcher");

        throw new UnsupportedOperationException();
    }

    @Override
    public Set<SpreadsheetCellReference> findCellsWithReference(final T reference,
                                                                final int offset,
                                                                final int count) {
        return this.store.findCellsWithReference(
            reference,
            offset,
            count
        );
    }

    @Override
    public int countCellsWithReference(final T reference) {
        return this.store.countCellsWithReference(reference);
    }

    @Override
    public Set<T> findReferencesWithCell(final SpreadsheetCellReference cell,
                                         final int offset,
                                         final int count) {
        return this.store.findReferencesWithCell(
            cell,
            offset,
            count
        );
    }

    @Override
    public void removeReferencesWithCell(final SpreadsheetCellReference cell) {
        Objects.requireNonNull(cell, "cell");

        throw new UnsupportedOperationException();
    }

    private final SpreadsheetExpressionReferenceStore<T> store;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.store.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            (other instanceof ReadOnlySpreadsheetExpressionReferenceStore &&
                this.equals0((ReadOnlySpreadsheetExpressionReferenceStore<?>) other));
    }

    private boolean equals0(final ReadOnlySpreadsheetExpressionReferenceStore<?> other) {
        return this.store.equals(other.store);
    }

    @Override
    public String toString() {
        return this.store.toString();
    }

    private static void checkReferenceAndSpreadsheetCell(final ReferenceAndSpreadsheetCellReference<?> referenceAndCell) {
        Objects.requireNonNull(referenceAndCell, "referenceAndCell");
    }
}

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

package walkingkooka.spreadsheet.store.reference;

import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * A read only wrapper around a {@link SpreadsheetReferenceStore}
 */
final class ReadOnlySpreadsheetReferenceStore<T extends ExpressionReference & Comparable<T>> implements SpreadsheetReferenceStore<T> {

    static <T extends ExpressionReference & Comparable<T>> ReadOnlySpreadsheetReferenceStore<T> with(final SpreadsheetReferenceStore<T> store) {
        Objects.requireNonNull(store, "store");
        return new ReadOnlySpreadsheetReferenceStore<>(store);
    }

    private ReadOnlySpreadsheetReferenceStore(final SpreadsheetReferenceStore<T> store) {
        this.store = store;
    }

    @Override
    public Optional<Set<SpreadsheetCellReference>> load(final T id) {
        return this.store.load(id);
    }

    @Override
    public Set<SpreadsheetCellReference> save(final Set<SpreadsheetCellReference> references) {
        Objects.requireNonNull(references, "references");
        throw new UnsupportedOperationException();
    }

    @Override
    public Runnable addSaveWatcher(final Consumer<Set<SpreadsheetCellReference>> saved) {
        Objects.requireNonNull(saved, "saved");
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(final T id) {
        checkId(id);
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
    public Set<T> ids(final int from, final int count) {
        return this.store.ids(from, count);
    }

    @Override
    public List<Set<SpreadsheetCellReference>> values(final T from, final int count) {
        return this.store.values(from, count);
    }

    @Override
    public void saveReferences(final T id, final Set<SpreadsheetCellReference> referrers) {
        checkId(id);
        Objects.requireNonNull(referrers, "referrers");

        throw new UnsupportedOperationException();
    }

    @Override
    public void addReference(final TargetAndSpreadsheetCellReference<T> targetedReference) {
        checkTargetedReference(targetedReference);

        throw new UnsupportedOperationException();
    }

    @Override
    public Runnable addAddReferenceWatcher(final Consumer<TargetAndSpreadsheetCellReference<T>> watcher) {
        Objects.requireNonNull(watcher, "watcher");

        throw new UnsupportedOperationException();
    }

    @Override
    public void removeReference(final TargetAndSpreadsheetCellReference<T> targetedReference) {
        checkTargetedReference(targetedReference);

        throw new UnsupportedOperationException();
    }

    @Override
    public Runnable addRemoveReferenceWatcher(final Consumer<TargetAndSpreadsheetCellReference<T>> watcher) {
        Objects.requireNonNull(watcher, "watcher");

        throw new UnsupportedOperationException();
    }

    @Override
    public Set<T> loadReferred(final SpreadsheetCellReference reference) {
        return store.loadReferred(reference);
    }

    private SpreadsheetReferenceStore<T> store;

    @Override
    public String toString() {
        return this.store.toString();
    }

    private void checkId(final T id) {
        Objects.requireNonNull(id, "id");
    }

    private static void checkTargetedReference(final TargetAndSpreadsheetCellReference targetedReference) {
        Objects.requireNonNull(targetedReference, "targetedReference");
    }
}


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

import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.store.Store;
import walkingkooka.store.Stores;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;

/**
 * A {@link SpreadsheetColumnStore} that uses a {@link TreeMap}.
 */
final class TreeMapSpreadsheetColumnStore implements SpreadsheetColumnStore {

    /**
     * Factory that creates a new {@link TreeMapSpreadsheetColumnStore}
     */
    static TreeMapSpreadsheetColumnStore create() {
        return new TreeMapSpreadsheetColumnStore();
    }

    /**
     * Private ctor.
     */
    private TreeMapSpreadsheetColumnStore() {
        super();
        this.store = Stores.treeMap(Comparator.naturalOrder(), TreeMapSpreadsheetColumnStore::idSetter);
    }

    private static SpreadsheetColumn idSetter(final SpreadsheetColumnReference id,
                                              final SpreadsheetColumn SpreadsheetColumn) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetColumn> load(final SpreadsheetColumnReference id) {
        return this.store.load(id);
    }

    @Override
    public SpreadsheetColumn save(final SpreadsheetColumn SpreadsheetColumn) {
        return this.store.save(SpreadsheetColumn);
    }

    @Override
    public Runnable addSaveWatcher(final Consumer<SpreadsheetColumn> saved) {
        return this.store.addSaveWatcher(saved);
    }

    @Override
    public void delete(final SpreadsheetColumnReference id) {
        this.store.delete(id);
    }

    @Override
    public Runnable addDeleteWatcher(final Consumer<SpreadsheetColumnReference> deleted) {
        return this.store.addDeleteWatcher(deleted);
    }

    @Override
    public int count() {
        return this.store.count();
    }

    @Override
    public Set<SpreadsheetColumnReference> ids(final int from,
                                               final int count) {
        return this.store.ids(from, count);
    }

    @Override
    public List<SpreadsheetColumn> values(final SpreadsheetColumnReference from,
                                          final int count) {
        return this.store.values(from, count);
    }

    @Override
    public Optional<SpreadsheetColumnReference> leftSkipHidden(final SpreadsheetColumnReference reference) {
        SpreadsheetColumnReference left = reference;

        for (; ; ) {
            if (left.isFirst()) {
                left = this.isHidden(reference) ?
                        null :
                        reference;
                break;
            }

            left = left.addSaturated(-1);

            if (!this.isHidden(left)) {
                break;
            }
        }

        return Optional.ofNullable(left);
    }

    @Override
    public SpreadsheetColumnReference rightSkipHidden(final SpreadsheetColumnReference reference) {
        SpreadsheetColumnReference right = reference;

        for (; ; ) {
            if (right.isLast()) {
                right = reference;
                break;
            }

            right = right.addSaturated(+1);

            if (!this.isHidden(right)) {
                break;
            }
        }

        return right;
    }

    // VisibleForTesting
    private final Store<SpreadsheetColumnReference, SpreadsheetColumn> store;

    @Override
    public String toString() {
        return this.store.toString();
    }
}

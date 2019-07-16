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

import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.store.Store;
import walkingkooka.store.Stores;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

/**
 * A {@link SpreadsheetCellStore} that uses a {@link Map}.
 */
final class TreeMapSpreadsheetCellStore implements SpreadsheetCellStore {

    /**
     * Factory that creates a new {@link TreeMapSpreadsheetCellStore}
     */
    static TreeMapSpreadsheetCellStore create() {
        return new TreeMapSpreadsheetCellStore();
    }

    /**
     * Private ctor.
     */
    private TreeMapSpreadsheetCellStore() {
        super();
        this.store = Stores.treeMap(Comparator.naturalOrder(), TreeMapSpreadsheetCellStore::idSetter);
    }

    private static SpreadsheetCell idSetter(final SpreadsheetCellReference id, final SpreadsheetCell spreadsheetCell) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetCell> load(final SpreadsheetCellReference id) {
        return this.store.load(id);
    }

    @Override
    public SpreadsheetCell save(final SpreadsheetCell spreadsheetCell) {
        return this.store.save(spreadsheetCell);
    }

    @Override
    public Runnable addSaveWatcher(final Consumer<SpreadsheetCell> saved) {
        return this.store.addSaveWatcher(saved);
    }

    @Override
    public void delete(final SpreadsheetCellReference id) {
        this.store.delete(id);
    }

    @Override
    public Runnable addDeleteWatcher(final Consumer<SpreadsheetCellReference> deleted) {
        return this.store.addDeleteWatcher(deleted);
    }

    @Override
    public int count() {
        return this.store.count();
    }

    @Override
    public Set<SpreadsheetCellReference> ids(final int from,
                                             final int count) {
        return this.store.ids(from, count);
    }

    @Override
    public List<SpreadsheetCell> values(final SpreadsheetCellReference from,
                                        final int count) {
        return this.store.values(from, count);
    }

    @Override
    public int rows() {
        return this.max(c -> c.reference().row().value());
    }

    @Override
    public int columns() {
        return this.max(c -> c.reference().column().value());
    }

    private int max(final ToIntFunction<SpreadsheetCell> value) {
        return this.all()
                .stream()
                .mapToInt(value::applyAsInt)
                .max()
                .orElse(0);
    }

    @Override
    public final Set<SpreadsheetCell> row(final SpreadsheetRowReference row) {
        Objects.requireNonNull(row, "row");

        return this.filter(c -> row.compareTo(c.reference().row()) == 0);
    }

    @Override
    public final Set<SpreadsheetCell> column(final SpreadsheetColumnReference column) {
        Objects.requireNonNull(column, "column");

        return this.filter(c -> column.compareTo(c.reference().column()) == 0);
    }

    private Set<SpreadsheetCell> filter(final Predicate<SpreadsheetCell> filter) {
        return this.all()
                .stream()
                .filter(filter)
                .collect(Collectors.toCollection(Sets::sorted));
    }

    // VisibleForTesting
    final Store<SpreadsheetCellReference, SpreadsheetCell> store;

    @Override
    public String toString() {
        return this.store.toString();
    }
}

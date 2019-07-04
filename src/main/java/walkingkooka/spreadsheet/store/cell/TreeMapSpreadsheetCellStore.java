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

package walkingkooka.spreadsheet.store.cell;

import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetCellReference;
import walkingkooka.spreadsheet.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.SpreadsheetRowReference;
import walkingkooka.spreadsheet.store.SpreadsheetStore;
import walkingkooka.spreadsheet.store.Watchers;

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
    }

    @Override
    public final Optional<SpreadsheetCell> load(final SpreadsheetCellReference reference) {
        Objects.requireNonNull(reference, "references");

        return Optional.ofNullable(this.cells.get(reference));
    }

    /**
     * Accepts a potentially updated cell.
     */
    @Override
    public final SpreadsheetCell save(final SpreadsheetCell cell) {
        Objects.requireNonNull(cell, "cell");

        final SpreadsheetCellReference key = cell.reference();
        if (false == cell.equals(this.cells.put(key, cell))) {
            this.saveWatchers.accept(cell);
        }

        return cell;
    }

    @Override
    public Runnable addSaveWatcher(final Consumer<SpreadsheetCell> saved) {
        return this.saveWatchers.addWatcher(saved);
    }

    private final Watchers<SpreadsheetCell> saveWatchers = Watchers.create();

    /**
     * Deletes a single cell, ignoring invalid requests.
     */
    @Override
    public final void delete(final SpreadsheetCellReference reference) {
        Objects.requireNonNull(reference, "reference");

        if (null != this.cells.remove(reference)) {
            this.deleteWatchers.accept(reference);
        }
    }

    @Override
    public Runnable addDeleteWatcher(final Consumer<SpreadsheetCellReference> deleted) {
        return this.deleteWatchers.addWatcher(deleted);
    }

    private final Watchers<SpreadsheetCellReference> deleteWatchers = Watchers.create();


    @Override
    public int count() {
        return this.cells.size();
    }

    @Override
    public Set<SpreadsheetCellReference> ids(final int from,
                                             final int count) {
        SpreadsheetStore.checkFromAndTo(from, count);

        return this.cells.keySet()
                .stream()
                .skip(from)
                .limit(count)
                .collect(Collectors.toCollection(Sets::ordered));
    }

    @Override
    public List<SpreadsheetCell> values(final SpreadsheetCellReference from,
                                        final int count) {
        SpreadsheetStore.checkFromAndToIds(from, count);

        return this.cells.entrySet()
                .stream()
                .filter(e -> e.getKey().compareTo(from) >= 0)
                .map(e -> e.getValue())
                .limit(count)
                .collect(Collectors.toCollection(Lists::array));
    }

    @Override
    public int rows() {
        return this.max(c -> c.row().value());
    }

    @Override
    public int columns() {
        return this.max(c -> c.column().value());
    }

    private int max(final ToIntFunction<SpreadsheetCellReference> value) {
        return this.cells.keySet()
                .stream()
                .mapToInt(value)
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
        return this.cells.values()
                .stream()
                .filter(filter)
                .collect(Collectors.toCollection(Sets::sorted));
    }

    /**
     * All cells present in this spreadsheet
     */
    private final Map<SpreadsheetCellReference, SpreadsheetCell> cells = Maps.sorted();

    @Override
    public String toString() {
        return this.cells.values().toString();
    }
}

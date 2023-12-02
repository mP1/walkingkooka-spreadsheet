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
import walkingkooka.spreadsheet.SpreadsheetValueType;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRange;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.store.Store;
import walkingkooka.store.Stores;
import walkingkooka.tree.text.Length;
import walkingkooka.tree.text.TextStylePropertyName;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private static SpreadsheetCellRange checkCellRange(final SpreadsheetCellRange range) {
        return Objects.requireNonNull(range, "range");
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
    public void deleteCells(final SpreadsheetCellRange range) {
        checkCellRange(range);

        this.store.all()
                .stream()
                .filter(c -> range.testCell(c.reference()))
                .forEach(
                        c -> this.delete(c.reference())
                );
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
    public List<SpreadsheetCell> between(final SpreadsheetCellReference from,
                                         final SpreadsheetCellReference to) {
        final List<SpreadsheetCell> cells = this.store.between(
                from,
                to
        );

        final SpreadsheetCellRange window = from.cellRange(to);

        return cells.stream()
                .filter(c -> window.test(c.reference()))
                .collect(Collectors.toList());
    }

    @Override
    public int rowCount() {
        return this.count(c -> c.reference().row().value());
    }

    @Override
    public int columnCount() {
        return this.count(c -> c.reference().column().value());
    }

    private int count(final ToIntFunction<SpreadsheetCell> value) {
        return 1 +
                this.all()
                .stream()
                .mapToInt(value)
                .max()
                .orElse(-1);
    }

    @Override
    public Set<SpreadsheetCell> row(final SpreadsheetRowReference row) {
        Objects.requireNonNull(row, "row");

        return this.filter(c -> row.compareTo(c.reference().row()) == 0);
    }

    @Override
    public Set<SpreadsheetCell> column(final SpreadsheetColumnReference column) {
        Objects.requireNonNull(column, "column");

        return this.filter(c -> column.compareTo(c.reference().column()) == 0);
    }

    private Set<SpreadsheetCell> filter(final Predicate<SpreadsheetCell> filter) {
        return this.all()
                .stream()
                .filter(filter)
                .collect(Collectors.toCollection(Sets::sorted));
    }

    /**
     * Filters all cells with the given column and finds the max value.
     */
    @Override
    public double maxColumnWidth(final SpreadsheetColumnReference column) {
        return this.maxWidthOrHeight(
                this.column(column),
                TextStylePropertyName.WIDTH
        );
    }

    /**
     * Filters all cells with the given row and finds the max {@link TextStylePropertyName#HEIGHT} value.
     */
    @Override
    public double maxRowHeight(final SpreadsheetRowReference row) {
        return this.maxWidthOrHeight(
                this.row(row),
                TextStylePropertyName.HEIGHT
        );
    }

    private double maxWidthOrHeight(final Collection<SpreadsheetCell> cells,
                                    final TextStylePropertyName<Length<?>> widthOrHeight) {
        return cells.stream()
                .mapToDouble(c -> c.style()
                        .get(widthOrHeight)
                        .map(Length::pixelValue)
                        .orElse(0.0)
                ).max()
                .orElse(0.0);
    }

    @Override
    public Set<SpreadsheetCell> findCellsWithValueType(final SpreadsheetCellRange range,
                                                       final String valueTypeName,
                                                       final int max) {
        SpreadsheetCellStore.checkFindCellsWithValueType(
                range,
                valueTypeName,
                max
        );

        return this.valueTypeStream(
                        range,
                        valueTypeName
                ).limit(max)
                .collect(Collectors.toCollection(Sets::sorted));
    }

    @Override
    public int countCellsWithValueType(final SpreadsheetCellRange range,
                                       final String valueTypeName) {
        SpreadsheetCellStore.checkCountCellsWithValueType(
                range,
                valueTypeName
        );

        return (int) valueTypeStream(
                range,
                valueTypeName
        ).count();
    }

    /**
     * If the valueTypeName is {@link SpreadsheetValueType#ANY} this will match all cells with a value.
     */
    private Stream<SpreadsheetCell> valueTypeStream(final SpreadsheetCellRange range,
                                                    final String valueTypeName) {
        final Function<Object, Boolean> filter = SpreadsheetValueType.ANY.equals(valueTypeName) ?
                v -> Boolean.TRUE :
                v -> valueTypeName.equals(
                        SpreadsheetValueType.typeName(v.getClass())
                );

        return this.between(
                        range.begin(),
                        range.end()
                ).stream()
                .filter(
                        (cell) -> cell.formula()
                                .value()
                                .map(filter)
                                .orElse(false)
                );
    }

    // VisibleForTesting
    private final Store<SpreadsheetCellReference, SpreadsheetCell> store;

    @Override
    public String toString() {
        return this.store.toString();
    }
}

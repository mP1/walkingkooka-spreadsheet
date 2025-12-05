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

import walkingkooka.NeverError;
import walkingkooka.collect.set.Sets;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetValueType;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReferencePath;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.store.Store;
import walkingkooka.store.Stores;
import walkingkooka.tree.text.Length;
import walkingkooka.tree.text.TextStylePropertyName;
import walkingkooka.validation.ValueType;

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
    public SpreadsheetCell save(final SpreadsheetCell cell) {
        Objects.requireNonNull(cell, "cell");

        this.lrtd.addOrReplace(cell);
        this.rltd.addOrReplace(cell);

        this.lrbu.addOrReplace(cell);
        this.rlbu.addOrReplace(cell);

        this.tdlr.addOrReplace(cell);
        this.tdrl.addOrReplace(cell);

        this.bulr.addOrReplace(cell);
        this.burl.addOrReplace(cell);

        // must be last so any SaveWatchers that try and loadCellRange after the #maps like #lrtd have already saved $cell
        return this.store.save(cell);
    }

    @Override
    public Runnable addSaveWatcher(final Consumer<SpreadsheetCell> saved) {
        return this.store.addSaveWatcher(saved);
    }

    @Override
    public void delete(final SpreadsheetCellReference id) {
        Objects.requireNonNull(id, "id");

        this.lrtd.remove(id);
        this.rltd.remove(id);

        this.lrbu.remove(id);
        this.rlbu.remove(id);

        this.tdlr.remove(id);
        this.tdrl.remove(id);

        this.bulr.remove(id);
        this.burl.remove(id);

        // must be last so any DeleteWatchers that try and loadCellRange after the #maps like #lrtd have already deleted $id
        this.store.delete(id);
    }

    @Override
    public Set<SpreadsheetCell> loadCellRange(final SpreadsheetCellRangeReference range,
                                              final SpreadsheetCellRangeReferencePath path,
                                              final int offset,
                                              final int count) {
        Objects.requireNonNull(range, "range");
        Objects.requireNonNull(path, "path");
        Store.checkOffsetAndCount(
            offset,
            count
        );

        return 0 == count || offset >= range.count() ?
            Sets.empty() :
            this.loadCellsNonZeroMax(
                range,
                path,
                offset,
                count
            );
    }

    private Set<SpreadsheetCell> loadCellsNonZeroMax(final SpreadsheetCellRangeReference range,
                                                     final SpreadsheetCellRangeReferencePath path,
                                                     final int offset,
                                                     final int count) {
        final Set<SpreadsheetCell> loaded = SortedSets.tree(
            SpreadsheetCellReference.cellComparator(
                path.comparator()
            )
        );

        final TreeMapSpreadsheetCellStoreSortedList list;

        switch (path) {
            case LRTD:
                list = this.lrtd;
                break;
            case RLTD:
                list = this.rltd;
                break;
            case LRBU:
                list = this.lrbu;
                break;
            case RLBU:
                list = this.rlbu;
                break;
            case TDLR:
                list = this.tdlr;
                break;
            case TDRL:
                list = this.tdrl;
                break;
            case BULR:
                list = this.bulr;
                break;
            case BURL:
                list = this.burl;
                break;
            default:
                list = NeverError.unhandledEnum(
                    path,
                    SpreadsheetCellRangeReferencePath.values()
                );
                break;
        }

        final int height = path.height(range);
        final List<SpreadsheetCell> cells = list.cells;
        final Comparator<SpreadsheetCellReference> comparator = path.comparator();
        final int size = cells.size();

        SpreadsheetCellReference cellReference = path.first(range);

        int rows = 0;
        int i = 0;

        Exit:
        //
        for (; ; ) {
            final SpreadsheetCellReference firstColumn = cellReference;
            final SpreadsheetCellReference lastColumn = path.lastColumn(
                firstColumn,
                range
            );

            int index = list.indexOfOrNext(firstColumn);
            if (-1 == index) {
                break;
            }
            for (; ; ) {
                if (index == size) {
                    break Exit;
                }

                final SpreadsheetCell cell = cells.get(index);
                if (comparator.compare(cell.reference(), lastColumn) > 0) {
                    break;
                }
                if (i >= offset) {
                    if (loaded.size() >= count) {
                        break Exit;
                    }
                    loaded.add(cell);
                }
                i++;
                index++;
            }

            rows++;
            if (height == rows) {
                break;
            }

            cellReference = path.nextRow(
                cellReference,
                range
            );
        }

        return loaded;
    }

    @Override
    public void deleteCells(final SpreadsheetCellRangeReference range) {
        Objects.requireNonNull(range, "range");

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
    public Set<SpreadsheetCellReference> ids(final int offset,
                                             final int count) {
        return this.store.ids(
            offset,
            count
        );
    }

    @Override
    public List<SpreadsheetCell> values(final int offset,
                                        final int count) {
        return this.store.values(
            offset,
            count
        );
    }

    @Override
    public List<SpreadsheetCell> between(final SpreadsheetCellReference from,
                                         final SpreadsheetCellReference to) {
        final List<SpreadsheetCell> cells = this.store.between(
            from,
            to
        );

        final SpreadsheetCellRangeReference window = from.cellRange(to);

        return cells.stream()
            .filter(c -> window.test(c.reference()))
            .collect(Collectors.toList());
    }

    @Override
    public int rowCount() {
        return this.max(c -> c.reference().row().value());
    }

    @Override
    public int columnCount() {
        return this.max(c -> c.reference().column().value());
    }

    private int max(final ToIntFunction<SpreadsheetCell> value) {
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

        return this.filterAndCollect(c -> row.compareTo(c.reference().row()) == 0);
    }

    @Override
    public Set<SpreadsheetCell> column(final SpreadsheetColumnReference column) {
        Objects.requireNonNull(column, "column");

        return this.filterAndCollect(c -> column.compareTo(c.reference().column()) == 0);
    }

    private Set<SpreadsheetCell> filterAndCollect(final Predicate<SpreadsheetCell> filter) {
        return this.all()
            .stream()
            .filter(filter)
            .collect(Collectors.toCollection(() -> SortedSets.tree(SpreadsheetCell.REFERENCE_COMPARATOR)));
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

    /**
     * Slow but safe way to find the last column for the given row and then adds one.
     */
    @Override
    public Optional<SpreadsheetColumnReference> nextEmptyColumn(final SpreadsheetRowReference row) {
        Objects.requireNonNull(row, "row");

        SpreadsheetColumnReference next = null;
        final SpreadsheetColumnReference first = SpreadsheetReferenceKind.RELATIVE.firstColumn();
        final SpreadsheetColumnReference last = SpreadsheetReferenceKind.RELATIVE.lastColumn();

        for (final SpreadsheetCell spreadsheetCell : this.store.between(
            row.setColumn(first),
            row.setColumn(last))) {
            final SpreadsheetColumnReference possible = spreadsheetCell.reference()
                .column();
            if (null == next || possible.compareTo(next) > 0) {
                next = possible;
            }
        }

        return Optional.ofNullable(
            null == next ?
                first :
                next.equalsIgnoreReferenceKind(last) ?
                    null :
                    next.add(1)
        );
    }

    /**
     * Slow but safe way to find the last row for the given column and then adds one.
     */
    @Override
    public Optional<SpreadsheetRowReference> nextEmptyRow(final SpreadsheetColumnReference column) {
        Objects.requireNonNull(column, "column");

        SpreadsheetRowReference next = null;
        final SpreadsheetRowReference first = SpreadsheetReferenceKind.RELATIVE.firstRow();
        final SpreadsheetRowReference last = SpreadsheetReferenceKind.RELATIVE.lastRow();

        for (final SpreadsheetCell spreadsheetCell : this.store.between(
            column.setRow(first),
            column.setRow(last))) {
            final SpreadsheetRowReference possible = spreadsheetCell.reference()
                .row();
            if (null == next || possible.compareTo(next) > 0) {
                next = possible;
            }
        }

        return Optional.ofNullable(
            null == next ?
                first :
                next.equalsIgnoreReferenceKind(last) ?
                    null :
                    next.add(1)
        );
    }

    @Override
    public Set<SpreadsheetCell> findCellsWithValueType(final SpreadsheetCellRangeReference range,
                                                       final ValueType valueType,
                                                       final int max) {
        SpreadsheetCellStore.checkFindCellsWithValueType(
            range,
            valueType,
            max
        );

        return this.valueTypeStream(
                range,
                valueType
            ).limit(max)
            .collect(Collectors.toCollection(() -> SortedSets.tree(SpreadsheetCell.REFERENCE_COMPARATOR)));
    }

    @Override
    public int countCellsWithValueType(final SpreadsheetCellRangeReference range,
                                       final ValueType valueType) {
        SpreadsheetCellStore.checkCountCellsWithValueType(
            range,
            valueType
        );

        return (int) valueTypeStream(
            range,
            valueType
        ).count();
    }

    /**
     * If the {@link ValueType} is {@link SpreadsheetValueType#ANY} this will match all cells with a value.
     */
    private Stream<SpreadsheetCell> valueTypeStream(final SpreadsheetCellRangeReference range,
                                                    final ValueType valueType) {
        final Function<Object, Boolean> filter = SpreadsheetValueType.ANY.equals(valueType) ?
            v -> Boolean.TRUE :
            v -> valueType.equals(
                SpreadsheetValueType.toValueType(v.getClass())
                    .orElse(null)
            );

        return this.between(
                range.begin(),
                range.end()
            ).stream()
            .filter(
                (cell) -> cell.formula()
                    .errorOrValue()
                    .map(filter)
                    .orElse(false)
            );
    }

    // VisibleForTesting
    private final Store<SpreadsheetCellReference, SpreadsheetCell> store;

    private final TreeMapSpreadsheetCellStoreSortedList lrtd = TreeMapSpreadsheetCellStoreSortedList.with(
        SpreadsheetCellRangeReferencePath.LRTD
    );
    private final TreeMapSpreadsheetCellStoreSortedList rltd = TreeMapSpreadsheetCellStoreSortedList.with(
        SpreadsheetCellRangeReferencePath.RLTD
    );

    private final TreeMapSpreadsheetCellStoreSortedList lrbu = TreeMapSpreadsheetCellStoreSortedList.with(
        SpreadsheetCellRangeReferencePath.LRBU
    );

    private final TreeMapSpreadsheetCellStoreSortedList rlbu = TreeMapSpreadsheetCellStoreSortedList.with(
        SpreadsheetCellRangeReferencePath.RLBU
    );

    private final TreeMapSpreadsheetCellStoreSortedList tdlr = TreeMapSpreadsheetCellStoreSortedList.with(
        SpreadsheetCellRangeReferencePath.TDLR
    );

    private final TreeMapSpreadsheetCellStoreSortedList tdrl = TreeMapSpreadsheetCellStoreSortedList.with(
        SpreadsheetCellRangeReferencePath.TDRL
    );

    private final TreeMapSpreadsheetCellStoreSortedList bulr = TreeMapSpreadsheetCellStoreSortedList.with(
        SpreadsheetCellRangeReferencePath.BULR
    );

    private final TreeMapSpreadsheetCellStoreSortedList burl = TreeMapSpreadsheetCellStoreSortedList.with(
        SpreadsheetCellRangeReferencePath.BURL
    );

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.store.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            (other instanceof TreeMapSpreadsheetCellStore &&
                this.equals0((TreeMapSpreadsheetCellStore) other));
    }

    private boolean equals0(final TreeMapSpreadsheetCellStore other) {
        return this.store.equals(other.store);
    }

    @Override
    public String toString() {
        return this.store.toString();
    }
}

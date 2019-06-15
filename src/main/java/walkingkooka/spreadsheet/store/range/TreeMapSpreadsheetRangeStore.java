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

package walkingkooka.spreadsheet.store.range;

import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetCellReference;
import walkingkooka.spreadsheet.SpreadsheetRange;
import walkingkooka.spreadsheet.store.Store;
import walkingkooka.spreadsheet.store.Watchers;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A {@link SpreadsheetRangeStore} that uses two {@link TreeMap} to navigate and store range to value mappings.
 */
final class TreeMapSpreadsheetRangeStore<V> implements SpreadsheetRangeStore<V> {

    /**
     * Factory that creates a new {@link TreeMapSpreadsheetRangeStore}
     */
    static <V> TreeMapSpreadsheetRangeStore<V> create() {
        return new TreeMapSpreadsheetRangeStore();
    }

    /**
     * Private ctor.
     */
    private TreeMapSpreadsheetRangeStore() {
        super();
    }

    // load .....................................................................................................

    @Override
    public Optional<List<V>> load(final SpreadsheetRange range) {
        checkRange(range);

        final TreeMapSpreadsheetRangeStoreTopLeftEntry<V> value = this.topLeft.get(range.begin());
        return null != value ?
                value.load(range) :
                Optional.empty();
    }

    // loadCellReferenceRanges.............................................................................................

    @Override
    public Set<SpreadsheetRange> loadCellReferenceRanges(final SpreadsheetCellReference cell) {
        checkCell(cell);

        final Set<SpreadsheetRange> values = Sets.ordered();

        this.gatherTopLeft(cell, entry -> entry.loadCellReferenceRanges(cell, values));
        this.gatherBottomRight(cell, entry -> entry.loadCellReferenceRanges(cell, values));

        return Sets.readOnly(values);
    }

    // loadCellReferences .....................................................................................................

    @Override
    public Set<V> loadCellReferenceValues(final SpreadsheetCellReference cell) {
        checkCell(cell);

        final Set<V> values = Sets.ordered();

        this.gatherTopLeft(cell, entry -> entry.loadCellReferenceValues(cell, values));
        this.gatherBottomRight(cell, entry -> entry.loadCellReferenceValues(cell, values));

        return Sets.readOnly(values);
    }

    // loadCellReferenceRanges & loadCellReferences ................................................................................

    private void gatherTopLeft(final SpreadsheetCellReference cell,
                               final Consumer<TreeMapSpreadsheetRangeStoreTopLeftEntry<V>> values) {
        this.gather(this.topLeft,
                cell,
                cellReference -> cellReference.compareTo(cell) > 0,
                values);
    }

    private void gatherBottomRight(final SpreadsheetCellReference cell,
                                   final Consumer<TreeMapSpreadsheetRangeStoreBottomRightEntry<V>> values) {
        this.gather(this.bottomRight,
                cell,
                cellReference -> cellReference.compareTo(cell) < 0,
                values);
    }

    private <E extends TreeMapSpreadsheetRangeStoreEntry<V>> void gather(final NavigableMap<SpreadsheetCellReference, E> cellReferenceToEntry,
                                                                         final SpreadsheetCellReference cell,
                                                                         final Predicate<SpreadsheetCellReference> stop,
                                                                         final Consumer<E> values) {
        // loop over all entries until cell is after entry.
        for (Map.Entry<SpreadsheetCellReference, E> refAndValues : cellReferenceToEntry.tailMap(cell, true)
                .entrySet()) {
            if (stop.test(refAndValues.getKey())) {
                break;
            }
            values.accept(refAndValues.getValue());
        }
    }

    // addValue .....................................................................................................

    @Override
    public void addValue(final SpreadsheetRange range, final V value) {
        checkRange(range);
        checkValue(value);

        this.addTopLeft(range, value);
        this.addBottomRight(range, value);
        this.addValueToValueToRanges(range, value);
    }

    private void addTopLeft(final SpreadsheetRange range, final V value) {
        final SpreadsheetCellReference topLeft = range.begin();
        final TreeMapSpreadsheetRangeStoreEntry<V> values = this.topLeft.get(topLeft);
        if (null != values) {
            values.save(range, value);
        } else {
            this.topLeft.put(topLeft,
                    TreeMapSpreadsheetRangeStoreTopLeftEntry.with(range, value));
        }
    }

    private void addBottomRight(final SpreadsheetRange range, final V value) {
        final SpreadsheetCellReference bottomRight = range.end();
        final TreeMapSpreadsheetRangeStoreEntry<V> values = this.bottomRight.get(bottomRight);
        if (null != values) {
            values.save(range, value);
        } else {
            this.bottomRight.put(bottomRight,
                    TreeMapSpreadsheetRangeStoreBottomRightEntry.with(range, value));
        }
    }

    @Override
    public boolean replaceValue(final SpreadsheetRange range, final V newValue, final V oldValue) {
        checkRange(range);
        Objects.requireNonNull(newValue, "newValue");
        Objects.requireNonNull(oldValue, "oldValue");

        return !oldValue.equals(newValue) &&
                this.replace0(range, newValue, oldValue) &&
                this.replace1(range, newValue, oldValue) &&
                this.replace2(range, newValue, oldValue);
    }

    private boolean replace0(final SpreadsheetRange range, final V newValue, final V oldValue) {
        final Set<SpreadsheetRange> deleted = this.valueToRanges.remove(oldValue);

        final boolean replaced = null != deleted;
        if (replaced) {
            this.addValueToValueToRanges(range, newValue);
        }
        return replaced;
    }

    private boolean replace1(final SpreadsheetRange range, final V newValue, final V oldValue) {
        final SpreadsheetCellReference topLeft = range.begin();
        final TreeMapSpreadsheetRangeStoreEntry<V> values = this.topLeft.get(topLeft);
        return null != values &&
                values.replace(range, newValue, oldValue);
    }

    private boolean replace2(final SpreadsheetRange range, final V newValue, final V oldValue) {
        final SpreadsheetCellReference bottomRight = range.end();
        final TreeMapSpreadsheetRangeStoreEntry<V> values = this.bottomRight.get(bottomRight);
        return null != values &&
                values.replace(range, newValue, oldValue);
    }

    private void addValueToValueToRanges(final SpreadsheetRange range, final V value) {
        Set<SpreadsheetRange> updated = this.valueToRanges.get(value);
        if (null == updated) {
            updated = Sets.ordered();
            this.valueToRanges.put(value, updated);
        }
        updated.add(range);
    }

    // removeValue .....................................................................................................

    /**
     * Only removes the given value for the given range if it exists.
     */
    @Override
    public void removeValue(final SpreadsheetRange range, final V value) {
        checkRange(range);
        checkValue(value);

        this.removeTopLeftValue(range, value);
        this.removeBottomRightValue(range, value);
        this.valueToRanges.remove(value);
    }

    private void removeTopLeftValue(final SpreadsheetRange range, final V value) {
        final SpreadsheetCellReference topLeft = range.begin();
        TreeMapSpreadsheetRangeStoreEntry<V> topLeftValues = this.topLeft.get(topLeft);
        if (null != topLeftValues) {
            if (topLeftValues.delete(range, value)) {
                this.topLeft.remove(topLeft);
            }
        }
    }

    private void removeBottomRightValue(final SpreadsheetRange range, final V value) {
        final SpreadsheetCellReference bottomRight = range.begin();
        TreeMapSpreadsheetRangeStoreEntry<V> bottomRightValues = this.bottomRight.get(bottomRight);
        if (null != bottomRightValues) {
            if (bottomRightValues.delete(range, value)) {
                this.bottomRight.remove(bottomRight);
            }
        }
    }

    // delete .....................................................................................................

    /**
     * Only deletes values that match the given range exactly.
     */
    @Override
    public void delete(final SpreadsheetRange range) {
        checkRange(range);

        if (null != this.topLeft.remove(range.begin())) {
            this.bottomRight.remove(range.end());
            this.deleteRangeFromValueToRanges(range);
            this.deleteWatchers.accept(range);
        }
    }

    /**
     * Slowly visits and deletes all entries that contain a {@link SpreadsheetRange}.
     */
    private void deleteRangeFromValueToRanges(final SpreadsheetRange range) {
        for (Iterator<Map.Entry<V, Set<SpreadsheetRange>>> i = this.valueToRanges.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry<V, Set<SpreadsheetRange>> valueToRange = i.next();

            final Set<SpreadsheetRange> ranges = valueToRange.getValue();
            if (ranges.remove(range)) {
                if (ranges.isEmpty()) {
                    i.remove();
                }
            }
        }
    }

    @Override
    public Runnable addDeleteWatcher(final Consumer<SpreadsheetRange> deleted) {
        return this.deleteWatchers.addWatcher(deleted);
    }

    private final Watchers<SpreadsheetRange> deleteWatchers = Watchers.create();

    // count.........................................................................................................

    @Override
    public int count() {
        return this.topLeft.values()
                .stream()
                .mapToInt(e -> e.count())
                .sum();
    }

    @Override
    public Set<SpreadsheetRange> ids(final int from,
                                     final int count) {
        Store.checkFromAndTo(from, count);

        final Set<SpreadsheetRange> ids = Sets.ordered();
        int i = 0;

        Exit:
        for (TreeMapSpreadsheetRangeStoreTopLeftEntry<V> entry : this.topLeft.values()) {
            for (SpreadsheetRange range : entry.ranges()) {
                if (i >= from) {
                    ids.add(range);

                    if (ids.size() == count) {
                        break Exit;
                    }
                }
                i++;
            }
        }

        return Sets.readOnly(ids);
    }

    @Override
    public List<List<V>> values(final SpreadsheetRange from,
                                final int count) {
        Store.checkFromAndToIds(from, count);

        final List<List<V>> values = Lists.array();
        boolean copy = false;

        Exit:
//
        for (TreeMapSpreadsheetRangeStoreTopLeftEntry<V> entry : this.topLeft.values()) {
            for (SpreadsheetRange range : entry.ranges()) {
                copy = copy | range.equals(from); // doesnt find > from must be ==

                if (copy) {
                    if (values.size() == count) {
                        break Exit;
                    }

                    final List<V> v = Lists.array();
                    v.addAll(entry.secondaryCellReferenceToValues.get(range.end()));
                    values.add(v);
                }
            }
        }

        return Lists.readOnly(values);
    }

    @Override
    public Set<SpreadsheetRange> rangesWithValue(final V value) {
        checkValue(value);

        Set<SpreadsheetRange> ranges;

        final Set<SpreadsheetRange> current = this.valueToRanges.get(value);
        if (null != current) {
            ranges = Sets.ordered();
            ranges.addAll(current);
            Sets.readOnly(ranges);
        } else {
            ranges = Sets.empty();
        }

        return ranges;
    }

    /**
     * The top left cell is the key, with the value holding all ranges that share the same top/left cell.
     * To locate matching values, all entries will be filtered.
     */
    private final NavigableMap<SpreadsheetCellReference, TreeMapSpreadsheetRangeStoreTopLeftEntry<V>> topLeft = new TreeMap<>();

    private final NavigableMap<SpreadsheetCellReference, TreeMapSpreadsheetRangeStoreBottomRightEntry<V>> bottomRight = new TreeMap<>();

    /**
     * Tracks all values to ranges.
     */
    private final Map<V, Set<SpreadsheetRange>> valueToRanges = Maps.ordered();

    // toString.........................................................................................................

    @Override
    public String toString() {
        return this.topLeft.toString();
    }

    private static void checkCell(final SpreadsheetCellReference cell) {
        Objects.requireNonNull(cell, "cell");
    }

    private static void checkRange(final SpreadsheetRange range) {
        Objects.requireNonNull(range, "range");
    }

    private void checkValue(final V value) {
        Objects.requireNonNull(value, "value");
    }
}

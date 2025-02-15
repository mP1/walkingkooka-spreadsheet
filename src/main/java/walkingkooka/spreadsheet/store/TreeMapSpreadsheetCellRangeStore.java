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

import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.store.Store;
import walkingkooka.watch.Watchers;

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
 * A {@link SpreadsheetCellRangeStore} that uses two {@link TreeMap} to navigate and store range to value mappings.
 */
final class TreeMapSpreadsheetCellRangeStore<V> implements SpreadsheetCellRangeStore<V> {

    /**
     * Factory that creates a new {@link TreeMapSpreadsheetCellRangeStore}
     */
    static <V> TreeMapSpreadsheetCellRangeStore<V> create() {
        return new TreeMapSpreadsheetCellRangeStore<>();
    }

    /**
     * Private ctor.
     */
    private TreeMapSpreadsheetCellRangeStore() {
        super();
    }

    // load .....................................................................................................

    @Override
    public Optional<List<V>> load(final SpreadsheetCellRangeReference range) {
        checkRange(range);

        final TreeMapSpreadsheetCellRangeStoreTopLeftEntry<V> value = this.topLeft.get(range.begin());
        return null != value ?
                value.load(range) :
                Optional.empty();
    }

    // findCellRangesIncludingCell......................................................................................

    @Override
    public Set<SpreadsheetCellRangeReference> findCellRangesIncludingCell(final SpreadsheetCellReference cell) {
        checkCell(cell);

        final Set<SpreadsheetCellRangeReference> values = Sets.ordered();

        this.gatherTopLeft(
                cell,
                entry -> entry.loadCellRangeReferences(
                        cell,
                        values
                )
        );
        this.gatherBottomRight(
                cell,
                entry -> entry.loadCellRangeReferences(
                        cell,
                        values
                )
        );

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

    // findCellRangesIncludingCell & loadCellReferences ................................................................

    private void gatherTopLeft(final SpreadsheetCellReference cell,
                               final Consumer<TreeMapSpreadsheetCellRangeStoreTopLeftEntry<V>> values) {
        this.gather(this.topLeft,
                cell,
                cellReference -> cellReference.compareTo(cell) > 0,
                values);
    }

    private void gatherBottomRight(final SpreadsheetCellReference cell,
                                   final Consumer<TreeMapSpreadsheetCellRangeStoreBottomRightEntry<V>> values) {
        this.gather(this.bottomRight,
                cell,
                cellReference -> cellReference.compareTo(cell) < 0,
                values);
    }

    private <E extends TreeMapSpreadsheetCellRangeStoreEntry<V>> void gather(final NavigableMap<SpreadsheetCellReference, E> cellReferenceToEntry,
                                                                             final SpreadsheetCellReference cell,
                                                                             final Predicate<SpreadsheetCellReference> stop,
                                                                             final Consumer<E> values) {
        // loop over all entries until cell is after entry.
        for (final Map.Entry<SpreadsheetCellReference, E> refAndValues : cellReferenceToEntry.tailMap(cell, true)
                .entrySet()) {
            if (stop.test(refAndValues.getKey())) {
                break;
            }
            values.accept(refAndValues.getValue());
        }
    }

    // addValue .....................................................................................................

    @Override
    public void addValue(final SpreadsheetCellRangeReference range, final V value) {
        checkRange(range);
        checkValue(value);

        this.addValue0(range.toRelative(), value);
    }

    private void addValue0(final SpreadsheetCellRangeReference range, final V value) {
        this.addTopLeft(range, value);
        this.addBottomRight(range, value);
        this.addValueToValueToRanges(range, value);
    }

    private void addTopLeft(final SpreadsheetCellRangeReference range, final V value) {
        final SpreadsheetCellReference topLeft = range.begin();
        final TreeMapSpreadsheetCellRangeStoreEntry<V> values = this.topLeft.get(topLeft);
        if (null != values) {
            values.save(range, value);
        } else {
            this.topLeft.put(topLeft,
                    TreeMapSpreadsheetCellRangeStoreTopLeftEntry.with(range, value));
        }
    }

    private void addBottomRight(final SpreadsheetCellRangeReference range, final V value) {
        final SpreadsheetCellReference bottomRight = range.end();
        final TreeMapSpreadsheetCellRangeStoreEntry<V> values = this.bottomRight.get(bottomRight);
        if (null != values) {
            values.save(range, value);
        } else {
            this.bottomRight.put(bottomRight,
                    TreeMapSpreadsheetCellRangeStoreBottomRightEntry.with(range, value));
        }
    }

    @Override
    public boolean replaceValue(final SpreadsheetCellRangeReference range, final V newValue, final V oldValue) {
        checkRange(range);
        Objects.requireNonNull(newValue, "newValue");
        Objects.requireNonNull(oldValue, "oldValue");

        return !oldValue.equals(newValue) &&
                this.replace0(range, newValue, oldValue) &&
                this.replace1(range, newValue, oldValue) &&
                this.replace2(range, newValue, oldValue);
    }

    private boolean replace0(final SpreadsheetCellRangeReference range, final V newValue, final V oldValue) {
        final Set<SpreadsheetCellRangeReference> deleted = this.valueToRanges.remove(oldValue);

        final boolean replaced = null != deleted;
        if (replaced) {
            this.addValueToValueToRanges(range, newValue);
        }
        return replaced;
    }

    private boolean replace1(final SpreadsheetCellRangeReference range, final V newValue, final V oldValue) {
        final SpreadsheetCellReference topLeft = range.begin();
        final TreeMapSpreadsheetCellRangeStoreEntry<V> values = this.topLeft.get(topLeft);
        return null != values &&
                values.replace(range, newValue, oldValue);
    }

    private boolean replace2(final SpreadsheetCellRangeReference range, final V newValue, final V oldValue) {
        final SpreadsheetCellReference bottomRight = range.end();
        final TreeMapSpreadsheetCellRangeStoreEntry<V> values = this.bottomRight.get(bottomRight);
        return null != values &&
                values.replace(range, newValue, oldValue);
    }

    private void addValueToValueToRanges(final SpreadsheetCellRangeReference range, final V value) {
        Set<SpreadsheetCellRangeReference> updated = this.valueToRanges.get(value);
        //noinspection Java8MapApi
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
    public void removeValue(final SpreadsheetCellRangeReference range, final V value) {
        checkRange(range);
        checkValue(value);

        this.removeTopLeftValue(range, value);
        this.removeBottomRightValue(range, value);
        this.valueToRanges.remove(value);
    }

    private void removeTopLeftValue(final SpreadsheetCellRangeReference range, final V value) {
        final SpreadsheetCellReference topLeft = range.begin();
        TreeMapSpreadsheetCellRangeStoreEntry<V> topLeftValues = this.topLeft.get(topLeft);
        if (null != topLeftValues) {
            if (topLeftValues.delete(range, value)) {
                this.topLeft.remove(topLeft);
            }
        }
    }

    private void removeBottomRightValue(final SpreadsheetCellRangeReference range, final V value) {
        final SpreadsheetCellReference bottomRight = range.begin();
        TreeMapSpreadsheetCellRangeStoreEntry<V> bottomRightValues = this.bottomRight.get(bottomRight);
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
    public void delete(final SpreadsheetCellRangeReference range) {
        checkRange(range);

        if (null != this.topLeft.remove(range.begin())) {
            this.bottomRight.remove(range.end());
            this.deleteRangeFromValueToRanges(range);
            this.deleteWatchers.accept(range);
        }
    }

    /**
     * Slowly visits and deletes all entries that contain a {@link SpreadsheetCellRangeReference}.
     */
    private void deleteRangeFromValueToRanges(final SpreadsheetCellRangeReference range) {
        for (final Iterator<Map.Entry<V, Set<SpreadsheetCellRangeReference>>> i = this.valueToRanges.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry<V, Set<SpreadsheetCellRangeReference>> valueToRange = i.next();

            final Set<SpreadsheetCellRangeReference> ranges = valueToRange.getValue();
            if (ranges.remove(range)) {
                if (ranges.isEmpty()) {
                    i.remove();
                }
            }
        }
    }

    @Override
    public Runnable addDeleteWatcher(final Consumer<SpreadsheetCellRangeReference> deleted) {
        return this.deleteWatchers.add(deleted);
    }

    private final Watchers<SpreadsheetCellRangeReference> deleteWatchers = Watchers.create();

    // count.........................................................................................................

    @Override
    public int count() {
        return this.topLeft.values()
                .stream()
                .mapToInt(TreeMapSpreadsheetCellRangeStoreEntry::count)
                .sum();
    }

    @Override
    public Set<SpreadsheetCellRangeReference> ids(final int from,
                                                  final int count) {
        Store.checkFromAndCount(from, count);

        final Set<SpreadsheetCellRangeReference> ids = Sets.ordered();
        int i = 0;

        Exit:
        for (final TreeMapSpreadsheetCellRangeStoreTopLeftEntry<V> entry : this.topLeft.values()) {
            for (final SpreadsheetCellRangeReference range : entry.ranges()) {
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
    public List<List<V>> values(final int from,
                                final int count) {
        Store.checkFromAndCount(from, count);

        final List<List<V>> values = Lists.array();
        int i = 0;

        Exit:
//
        for (final TreeMapSpreadsheetCellRangeStoreTopLeftEntry<V> entry : this.topLeft.values()) {
            for (final SpreadsheetCellRangeReference range : entry.ranges()) {
                if (i >= from) {
                    final List<V> v = Lists.array();
                    v.addAll(entry.secondaryCellReferenceToValues.get(range.end()));
                    values.add(v);

                    if (i == count) {
                        break Exit;
                    }
                }

                i++;
            }
        }

        return Lists.readOnly(values);
    }

    @Override
    public List<List<V>> between(final SpreadsheetCellRangeReference from,
                                 final SpreadsheetCellRangeReference to) {
        Store.checkBetween(from, to);

        throw new UnsupportedOperationException();
    }

    @Override
    public Set<SpreadsheetCellRangeReference> rangesWithValue(final V value) {
        checkValue(value);

        Set<SpreadsheetCellRangeReference> ranges;

        final Set<SpreadsheetCellRangeReference> current = this.valueToRanges.get(value);
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
     * <ul>
     * <li>Only {@link SpreadsheetCellReference} that are relative are added</li>
     * </ul>
     */
    private final NavigableMap<SpreadsheetCellReference, TreeMapSpreadsheetCellRangeStoreTopLeftEntry<V>> topLeft = new TreeMap<>();

    private final NavigableMap<SpreadsheetCellReference, TreeMapSpreadsheetCellRangeStoreBottomRightEntry<V>> bottomRight = new TreeMap<>();

    /**
     * Tracks all values to ranges.
     */
    private final Map<V, Set<SpreadsheetCellRangeReference>> valueToRanges = Maps.ordered();

    // toString.........................................................................................................

    @Override
    public String toString() {
        return this.topLeft.toString();
    }

    private static void checkCell(final SpreadsheetCellReference cell) {
        Objects.requireNonNull(cell, "cell");
    }

    private static void checkRange(final SpreadsheetCellRangeReference range) {
        Objects.requireNonNull(range, "range");
    }

    private void checkValue(final V value) {
        Objects.requireNonNull(value, "value");
    }
}

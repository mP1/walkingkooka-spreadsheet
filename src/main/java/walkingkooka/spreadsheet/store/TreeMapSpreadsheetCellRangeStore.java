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
import walkingkooka.store.StoreWatcher;
import walkingkooka.store.StoreWatchers;

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
final class TreeMapSpreadsheetCellRangeStore implements SpreadsheetCellRangeStore {

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
    }

    // load ............................................................................................................

    @Override
    public Optional<List<SpreadsheetCellReference>> load(final SpreadsheetCellRangeReference range) {
        Objects.requireNonNull(range, "range");

        final TreeMapSpreadsheetCellRangeStoreTopLeftEntry value = this.topLeft.get(range.begin());
        return null != value ?
            value.load(range) :
            Optional.empty();
    }

    // findCellRangesIncludingCell......................................................................................

    @Override
    public Set<SpreadsheetCellRangeReference> findCellRangesIncludingCell(final SpreadsheetCellReference cell) {
        Objects.requireNonNull(cell, "cell");

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

    // findValuesWithCell...............................................................................................

    @Override
    public Set<SpreadsheetCellReference> findValuesWithCell(final SpreadsheetCellReference cell) {
        Objects.requireNonNull(cell, "cell");

        final Set<SpreadsheetCellReference> values = Sets.ordered();

        this.gatherTopLeft(
            cell,
            entry -> entry.loadCellReferenceValues(
                cell,
                values
            )
        );
        this.gatherBottomRight(
            cell,
            entry -> entry.loadCellReferenceValues(
                cell,
                values
            )
        );

        return Sets.readOnly(values);
    }

    // findCellRangesIncludingCell & findValuesWithCell ................................................................

    private void gatherTopLeft(final SpreadsheetCellReference cell,
                               final Consumer<TreeMapSpreadsheetCellRangeStoreTopLeftEntry> values) {
        this.gather(
            this.topLeft,
            cell,
            cellReference -> cellReference.compareTo(cell) > 0,
            values
        );
    }

    private void gatherBottomRight(final SpreadsheetCellReference cell,
                                   final Consumer<TreeMapSpreadsheetCellRangeStoreBottomRightEntry> values) {
        this.gather(
            this.bottomRight,
            cell,
            cellReference -> cellReference.compareTo(cell) < 0,
            values
        );
    }

    private <E extends TreeMapSpreadsheetCellRangeStoreEntry> void gather(final NavigableMap<SpreadsheetCellReference, E> cellReferenceToEntry,
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

    // addValue ........................................................................................................

    @Override
    public void addValue(final SpreadsheetCellRangeReference range,
                         final SpreadsheetCellReference value) {
        Objects.requireNonNull(range, "range");
        Objects.requireNonNull(value, "value");

        this.addValueNotNull(
            range.toRelative(),
            value
        );
    }

    private void addValueNotNull(final SpreadsheetCellRangeReference range,
                                 final SpreadsheetCellReference value) {
        this.addTopLeft(range, value);
        this.addBottomRight(range, value);
        this.addValueToValueToRanges(range, value);
    }

    private void addTopLeft(final SpreadsheetCellRangeReference range,
                            final SpreadsheetCellReference value) {
        final SpreadsheetCellReference topLeft = range.begin();
        final TreeMapSpreadsheetCellRangeStoreEntry values = this.topLeft.get(topLeft);
        if (null != values) {
            values.save(
                range,
                value
            );
        } else {
            this.topLeft.put(
                topLeft,
                TreeMapSpreadsheetCellRangeStoreTopLeftEntry.with(range, value)
            );
        }
    }

    private void addBottomRight(final SpreadsheetCellRangeReference range,
                                final SpreadsheetCellReference value) {
        final SpreadsheetCellReference bottomRight = range.end();
        final TreeMapSpreadsheetCellRangeStoreEntry values = this.bottomRight.get(bottomRight);
        if (null != values) {
            values.save(
                range,
                value
            );
        } else {
            this.bottomRight.put(
                bottomRight,
                TreeMapSpreadsheetCellRangeStoreBottomRightEntry.with(range, value)
            );
        }
    }

    private void addValueToValueToRanges(final SpreadsheetCellRangeReference range,
                                         final SpreadsheetCellReference value) {
        Set<SpreadsheetCellRangeReference> updated = this.valueToRanges.get(value);
        //noinspection Java8MapApi
        if (null == updated) {
            updated = Sets.ordered();
            this.valueToRanges.put(
                value,
                updated
            );
        }
        updated.add(range);
    }

    // removeValue .....................................................................................................

    /**
     * Only removes the given value for the given range if it exists.
     */
    @Override
    public void removeValue(final SpreadsheetCellRangeReference range,
                            final SpreadsheetCellReference value) {
        Objects.requireNonNull(range, "range");
        Objects.requireNonNull(value, "value");

        this.removeValueTopLeft(range, value);
        this.removeValueBottomRight(range, value);
        this.valueToRanges.remove(value);
    }

    private void removeValueTopLeft(final SpreadsheetCellRangeReference range,
                                    final SpreadsheetCellReference value) {
        final SpreadsheetCellReference topLeft = range.begin();
        TreeMapSpreadsheetCellRangeStoreEntry topLeftValues = this.topLeft.get(topLeft);
        if (null != topLeftValues) {
            if (topLeftValues.delete(range, value)) {
                this.topLeft.remove(topLeft);
            }
        }
    }

    private void removeValueBottomRight(final SpreadsheetCellRangeReference range,
                                        final SpreadsheetCellReference value) {
        final SpreadsheetCellReference bottomRight = range.begin();
        TreeMapSpreadsheetCellRangeStoreEntry bottomRightValues = this.bottomRight.get(bottomRight);
        if (null != bottomRightValues) {
            if (bottomRightValues.delete(range, value)) {
                this.bottomRight.remove(bottomRight);
            }
        }
    }

    // delete ..........................................................................................................

    /**
     * Only deletes values that match the given range exactly.
     */
    @Override
    public void delete(final SpreadsheetCellRangeReference range) {
        Objects.requireNonNull(range, "range");

        if (null != this.topLeft.remove(range.begin())) {
            this.bottomRight.remove(range.end());
            final List<SpreadsheetCellReference> deleted = this.deleteRangeFromValueToRanges(range);

            this.watcher.onValueChange(
                Optional.of(deleted),
                Optional.empty()
            );
        }
    }

    /**
     * Slowly visits and deletes all entries that contain a {@link SpreadsheetCellRangeReference}.
     */
    private List<SpreadsheetCellReference> deleteRangeFromValueToRanges(final SpreadsheetCellRangeReference range) {
        final List<SpreadsheetCellReference> deleted = Lists.array();

        for (final Iterator<Map.Entry<SpreadsheetCellReference, Set<SpreadsheetCellRangeReference>>> i = this.valueToRanges.entrySet().iterator(); i.hasNext(); ) {
            final Map.Entry<SpreadsheetCellReference, Set<SpreadsheetCellRangeReference>> valueToRange = i.next();

            deleted.add(
                valueToRange.getKey()
            );

            final Set<SpreadsheetCellRangeReference> ranges = valueToRange.getValue();
            if (ranges.remove(range)) {
                if (ranges.isEmpty()) {
                    i.remove();
                }
            }
        }

        return deleted;
    }

    // count............................................................................................................

    @Override
    public int count() {
        return this.topLeft.values()
            .stream()
            .mapToInt(TreeMapSpreadsheetCellRangeStoreEntry::count)
            .sum();
    }

    // ids..............................................................................................................

    @Override
    public Set<SpreadsheetCellRangeReference> ids(final int offset,
                                                  final int count) {
        Store.checkOffsetAndCount(offset, count);

        final Set<SpreadsheetCellRangeReference> ids = Sets.ordered();
        int i = 0;

        Exit:
        for (final TreeMapSpreadsheetCellRangeStoreTopLeftEntry entry : this.topLeft.values()) {
            for (final SpreadsheetCellRangeReference range : entry.ranges()) {
                if (i >= offset) {
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

    // values...........................................................................................................

    @Override
    public List<List<SpreadsheetCellReference>> values(final int offset,
                                                       final int count) {
        Store.checkOffsetAndCount(offset, count);

        final List<List<SpreadsheetCellReference>> values = Lists.array();
        int i = 0;

        Exit:
//
        for (final TreeMapSpreadsheetCellRangeStoreTopLeftEntry entry : this.topLeft.values()) {
            for (final SpreadsheetCellRangeReference range : entry.ranges()) {
                if (i >= offset) {
                    final List<SpreadsheetCellReference> v = Lists.array();
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

    // between..........................................................................................................

    @Override
    public List<List<SpreadsheetCellReference>> between(final SpreadsheetCellRangeReference from,
                                                        final SpreadsheetCellRangeReference to) {
        Store.checkBetween(from, to);

        throw new UnsupportedOperationException();
    }

    // findCellRangesWithValue..........................................................................................

    @Override
    public Set<SpreadsheetCellRangeReference> findCellRangesWithValue(final SpreadsheetCellReference value) {
        Objects.requireNonNull(value, "value");

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
    private final NavigableMap<SpreadsheetCellReference, TreeMapSpreadsheetCellRangeStoreTopLeftEntry> topLeft = new TreeMap<>();

    private final NavigableMap<SpreadsheetCellReference, TreeMapSpreadsheetCellRangeStoreBottomRightEntry> bottomRight = new TreeMap<>();

    /**
     * Tracks all values to ranges.
     */
    private final Map<SpreadsheetCellReference, Set<SpreadsheetCellRangeReference>> valueToRanges = Maps.ordered();

    @Override
    public Runnable addStoreWatcher(final StoreWatcher<List<SpreadsheetCellReference>> watcher) {
        return this.watcher.add(watcher);
    }

    private final StoreWatchers<List<SpreadsheetCellReference>> watcher = StoreWatchers.empty();

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.valueToRanges.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            (other instanceof TreeMapSpreadsheetCellRangeStore &&
                this.equals0((TreeMapSpreadsheetCellRangeStore) other));
    }

    private boolean equals0(final TreeMapSpreadsheetCellRangeStore other) {
        return this.valueToRanges.equals(other.valueToRanges);
    }

    @Override
    public String toString() {
        return this.topLeft.toString();
    }
}

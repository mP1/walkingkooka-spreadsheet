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

import walkingkooka.HasId;
import walkingkooka.Value;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * A {@link SpreadsheetStore} that shares a {@link TreeMap} and automatically allocates an ID if saving a value without an ID.
 * This store is intended to be decorated sharing the map.
 */
final class TreeMapSpreadsheetStore<K extends Comparable<K> & Value<Long>, V extends HasId<Optional<K>>> implements SpreadsheetStore<K, V> {

    /**
     * Factory that creates a new {@link TreeMapSpreadsheetStore}.
     */
    static <K extends Comparable<K> & Value<Long>, V extends HasId<Optional<K>>> TreeMapSpreadsheetStore<K, V> with(final BiFunction<Long, V, V> valueWithIdFactory) {
        Objects.requireNonNull(valueWithIdFactory, "valueWithIdFactory");

        return new TreeMapSpreadsheetStore<>(valueWithIdFactory);
    }

    /**
     * Private ctor
     */
    private TreeMapSpreadsheetStore(final BiFunction<Long, V, V> valueWithIdFactory) {
        super();
        this.idToValue = Maps.sorted();
        this.valueWithIdFactory = valueWithIdFactory;
    }

    @Override
    public Optional<V> load(final K id) {
        Objects.requireNonNull(id, "id");

        return Optional.ofNullable(this.idToValue.get(id));
    }

    @Override
    public V save(final V value) {
        Objects.requireNonNull(value, "value");

        final K id = value.id().orElse(null);
        return null != id ?
                this.update(id, value) :
                this.saveNew(value);
    }

    private V update(final K id, final V value) {
        if (false == value.equals(this.idToValue.put(id, value))) {
            this.saveWatchers.accept(value);
        }
        return value;
    }

    // no attempt to avoid clashes etc.
    private V saveNew(final V value) {
        final long max = this.idToValue.lastKey().value();
        final V valueWithId = this.valueWithIdFactory.apply(max + 1, value);
        this.idToValue.put(valueWithId.id().get(), valueWithId);
        this.saveWatchers.accept(valueWithId);
        return valueWithId;
    }

    /**
     * Accepts an ID and value combining the two into a new value.
     */
    private final BiFunction<Long, V, V> valueWithIdFactory;

    @Override
    public Runnable addSaveWatcher(final Consumer<V> saved) {
        return this.saveWatchers.addWatcher(saved);
    }

    private final Watchers<V> saveWatchers = Watchers.create();

    @Override
    public void delete(final K id) {
        Objects.requireNonNull(id, "id");

        if (null != this.idToValue.remove(id)) {
            this.deleteWatchers.accept(id);
        }
    }

    @Override
    public Runnable addDeleteWatcher(final Consumer<K> deleted) {
        return this.deleteWatchers.addWatcher(deleted);
    }

    private final Watchers<K> deleteWatchers = Watchers.create();

    @Override
    public int count() {
        return this.idToValue.size();
    }

    @Override
    public Set<K> ids(final int from,
                      final int count) {
        SpreadsheetStore.checkFromAndTo(from, count);

        return this.idToValue.keySet()
                .stream()
                .skip(from)
                .limit(count)
                .collect(Collectors.toCollection(Sets::ordered));
    }

    @Override
    public List<V> values(final K from,
                          final int count) {
        SpreadsheetStore.checkFromAndToIds(from, count);

        return this.idToValue.entrySet()
                .stream()
                .filter(e -> e.getKey().compareTo(from) >= 0)
                .map(e -> e.getValue())
                .limit(count)
                .collect(Collectors.toCollection(Lists::array));
    }

    /**
     * A {@link TreeMap} sorted by ID from lowest to highest.
     */
    // VisibleForTesting
    final SortedMap<K, V> idToValue;

    @Override
    public String toString() {
        return this.idToValue.values().toString();
    }
}

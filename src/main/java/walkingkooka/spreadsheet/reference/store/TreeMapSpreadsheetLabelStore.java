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

package walkingkooka.spreadsheet.reference.store;

import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.store.Store;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.watch.Watchers;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * A {@link SpreadsheetLabelStore} that uses a {@link Map}.
 */
final class TreeMapSpreadsheetLabelStore implements SpreadsheetLabelStore {

    /**
     * Factory that creates a new {@link TreeMapSpreadsheetLabelStore}
     */
    static TreeMapSpreadsheetLabelStore create() {
        return new TreeMapSpreadsheetLabelStore();
    }

    /**
     * Private ctor.
     */
    private TreeMapSpreadsheetLabelStore() {
        super();
    }

    @Override
    public Optional<SpreadsheetLabelMapping> load(final SpreadsheetLabelName label) {
        Objects.requireNonNull(label, "labels");
        return Optional.ofNullable(this.mappings.get(label));
    }

    @Override
    public SpreadsheetLabelMapping save(final SpreadsheetLabelMapping mapping) {
        Objects.requireNonNull(mapping, "mapping");

        final SpreadsheetLabelName key = mapping.label();
        if (false == mapping.equals(this.mappings.put(key, mapping))) {
            this.saveWatchers.accept(mapping);
        }

        return mapping;
    }

    @Override
    public Runnable addSaveWatcher(final Consumer<SpreadsheetLabelMapping> saved) {
        return this.saveWatchers.add(saved);
    }

    private final Watchers<SpreadsheetLabelMapping> saveWatchers = Watchers.create();

    @Override
    public void delete(final SpreadsheetLabelName label) {
        Objects.requireNonNull(label, "label");

        if (null != this.mappings.remove(label)) {
            this.deleteWatchers.accept(label);
        }
    }

    @Override
    public Runnable addDeleteWatcher(final Consumer<SpreadsheetLabelName> deleted) {
        return this.deleteWatchers.add(deleted);
    }

    private final Watchers<SpreadsheetLabelName> deleteWatchers = Watchers.create();

    @Override
    public int count() {
        return this.mappings.size();
    }

    @Override
    public Set<SpreadsheetLabelName> ids(final int from,
                                         final int count) {
        Store.checkFromAndTo(from, count);

        return this.mappings.keySet()
                .stream()
                .skip(from)
                .limit(count)
                .collect(Collectors.toCollection(Sets::ordered));
    }

    /**
     * Find the first mapping at or after the parse {@link SpreadsheetLabelName} and then gather the required count.
     */
    @Override
    public List<SpreadsheetLabelMapping> values(final SpreadsheetLabelName from,
                                                final int count) {
        Store.checkFromAndToIds(from, count);

        return this.mappings.tailMap(from)
                .values()
                .stream()
                .limit(count)
                .collect(Collectors.toCollection(Lists::array));
    }

    @Override
    public List<SpreadsheetLabelMapping> between(final SpreadsheetLabelName from,
                                                 final SpreadsheetLabelName to) {
        Store.checkBetween(from, to);

        final List<SpreadsheetLabelMapping> values = Lists.array();

        for (final Map.Entry<SpreadsheetLabelName, SpreadsheetLabelMapping> labelAndMapping : this.mappings.tailMap(from).entrySet()) {
            if (labelAndMapping.getKey().compareTo(to) > 0) {
                break;
            }

            values.add(labelAndMapping.getValue());
        }

        return Lists.readOnly(values);
    }

    @Override
    public Set<SpreadsheetLabelMapping> findSimilar(final String text,
                                                    final int count) {
        Objects.requireNonNull(text, "text");
        Store.checkCount(count);

        final Set<SpreadsheetLabelMapping> results;
        if (text.isEmpty() || 0 == count) {
            results = Sets.empty();
        } else {
            results = findSimilarNonEmpty(text, count);
        }

        return results;
    }

    private Set<SpreadsheetLabelMapping> findSimilarNonEmpty(final String text,
                                                             final int count) {
        Set<SpreadsheetLabelMapping> results;

        do {
            SpreadsheetLabelMapping mapping = null;
            try {
                mapping = this.mappings.get(SpreadsheetLabelName.labelName(text));
                if (null != mapping && 1 == count) {
                    results = Sets.of(mapping);
                    break;
                }
            } catch (final Exception notExact) {
                // ignore
            }

            results = Sets.ordered();
            if (null != mapping) {
                results.add(mapping);
            }

            this.mappings.values()
                    .stream()
                    .filter(l -> contains(text, l))
                    .limit(count - (null != mapping ? 1 : 0))
                    .forEach(results::add);
        } while (false);

        return results;
    }

    /**
     * Predicate that returns true if the {@link SpreadsheetLabelMapping} contains the given text value.
     * This assumes that a test has already been performed for exact matches.
     */
    private boolean contains(final String text,
                             final SpreadsheetLabelMapping possible) {
        final String value = possible.label()
                .value();
        return value.length() != text.length() && SpreadsheetLabelName.CASE_SENSITIVITY.contains(value, text);
    }

    @Override
    public Set<? super ExpressionReference> loadCellReferencesOrRanges(final SpreadsheetLabelName label) {
        Objects.requireNonNull(label, "label");

        return Sets.readOnly(TreeMapSpreadsheetLabelStoreReferencesSpreadsheetSelectionVisitor.gather(label, this.mappings));
    }

    @Override
    public Set<SpreadsheetLabelMapping> labels(final SpreadsheetExpressionReference selection) {
        Objects.requireNonNull(selection, "selection");

        return TreeMapSpreadsheetLabelStoreLabelsSpreadsheetSelectionVisitor.gather(
                this.mappings,
                selection
        );
    }

    /**
     * All mappings present in this spreadsheet
     */
    private final SortedMap<SpreadsheetLabelName, SpreadsheetLabelMapping> mappings = Maps.sorted();

    @Override
    public String toString() {
        return this.mappings.values().toString();
    }
}

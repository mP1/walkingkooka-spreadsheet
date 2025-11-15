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
import walkingkooka.collect.set.ImmutableSet;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReferenceOrRange;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelectionMaps;
import walkingkooka.store.Store;
import walkingkooka.text.CaseSensitivity;
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

        TreeMapSpreadsheetLabelStoreCycleSpreadsheetSelectionVisitor.cycleFreeTest(
            mapping,
            this
        );

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
    public Set<SpreadsheetLabelName> ids(final int offset,
                                         final int count) {
        Store.checkOffsetAndCount(offset, count);

        return this.mappings.keySet()
            .stream()
            .skip(offset)
            .limit(count)
            .collect(Collectors.toCollection(Sets::ordered));
    }

    /**
     * Find the first mapping at or after the parse {@link SpreadsheetLabelName} and then gather the required count.
     */
    @Override
    public List<SpreadsheetLabelMapping> values(final int offset,
                                                final int count) {
        Store.checkOffsetAndCount(offset, count);

        return this.mappings.values()
            .stream()
            .skip(offset)
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
    public Set<SpreadsheetLabelMapping> findLabelsByName(final String text,
                                                         final int offset,
                                                         final int count) {
        Objects.requireNonNull(text, "text");
        Store.checkOffsetAndCount(
            offset,
            count
        );


        return this.all()
            .stream()
            .filter(m -> text.isEmpty() ||
                CaseSensitivity.INSENSITIVE.contains(
                    m.label()
                        .value(),
                    text
                )
            ).skip(offset)
            .limit(count)
            .collect(ImmutableSet.collector());
    }

    @Override
    public Set<SpreadsheetCellReferenceOrRange> loadCellOrCellRanges(final SpreadsheetLabelName label) {
        Objects.requireNonNull(label, "label");

        return Sets.readOnly(
            TreeMapSpreadsheetLabelStoreReferencesSpreadsheetSelectionVisitor.gather(
                label,
                this.mappings
            )
        );
    }

    @Override
    public Set<SpreadsheetLabelMapping> findLabelsWithReference(final SpreadsheetExpressionReference reference,
                                                                final int offset,
                                                                final int count) {
        Objects.requireNonNull(reference, "reference");
        Store.checkOffsetAndCount(
            offset,
            count
        );

        return TreeMapSpreadsheetLabelStoreFindLabelsWithReferencesSpreadsheetSelectionVisitor.gather(
            this.mappings,
            reference,
            offset,
            count
        );
    }

    /**
     * All mappings present in this spreadsheet
     */
    private final SortedMap<SpreadsheetLabelName, SpreadsheetLabelMapping> mappings = SpreadsheetSelectionMaps.label();

    @Override
    public String toString() {
        return this.mappings.values().toString();
    }
}

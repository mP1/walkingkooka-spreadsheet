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

import walkingkooka.Cast;
import walkingkooka.ToStringBuilder;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.ImmutableSortedSet;
import walkingkooka.collect.set.Sets;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.store.Store;
import walkingkooka.watch.Watchers;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * A {@link SpreadsheetExpressionReferenceStore} that uses a {@link Map} to store a cell or label to its many {@link SpreadsheetCellReference references}.
 */
// using a type parameter of T extends SpreadsheetExpressionReference & Comparable<T> causes a Transpiler ERROR.
// Error:TreeMapSpreadsheetExpressionReferenceStore.java:39: This class must implement the inherited abstract method SpreadsheetExpressionReference.equalsIgnoreReferenceK
final class TreeMapSpreadsheetExpressionReferenceStore<T extends SpreadsheetExpressionReference> implements SpreadsheetExpressionReferenceStore<T> {

    static <T extends SpreadsheetExpressionReference> TreeMapSpreadsheetExpressionReferenceStore<T> create() {
        return new TreeMapSpreadsheetExpressionReferenceStore<>();
    }

    private TreeMapSpreadsheetExpressionReferenceStore() {
        super();

        final Comparator<SpreadsheetSelection> comparator = SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR;
        this.cellToReferences = Maps.sorted(comparator);
        this.referenceToCells = Maps.sorted(comparator);
    }

    @Override
    public Optional<Set<SpreadsheetCellReference>> load(final T reference) {
        Objects.requireNonNull(reference, "reference");

        // best to take defensive copy to prevent ConcurrentModificationException
        return Optional.ofNullable(
            this.referenceToCells.get(reference)
        ).map(SortedSets::immutable);
    }

    @Override
    public void delete(final T reference) {
        Objects.requireNonNull(reference, "reference");

        // where id=label remove label to cells, then remove cell to label.
        final Set<SpreadsheetCellReference> allCells = this.referenceToCells.get(reference);

        if (null != allCells) {
            for (final SpreadsheetCellReference cell : allCells) {
                final Set<T> references = this.cellToReferences.get(cell);
                if (null != references) {
                    if (references.remove(reference)) {
                        if (references.isEmpty()) {
                            this.cellToReferences.remove(cell);
                        }
                        this.removeCellWatchers.accept(
                            ReferenceAndSpreadsheetCellReference.with(
                                reference,
                                cell
                            )
                        );
                    }
                }
            }
        }
    }

    @Override
    public Runnable addDeleteWatcher(final Consumer<T> deleted) {
        return this.deleteWatchers.add(deleted);
    }

    private final Watchers<T> deleteWatchers = Watchers.create();

    @Override
    public int count() {
        return this.referenceToCells.size();
    }

    @Override
    public Set<T> ids(final int offset,
                      final int count) {
        Store.checkOffsetAndCount(offset, count);

        return this.referenceToCells.keySet()
            .stream()
            .skip(offset)
            .limit(count)
            .collect(Collectors.toCollection(Sets::ordered));
    }

    @Override
    public List<Set<SpreadsheetCellReference>> values(final int offset,
                                                      final int count) {
        Store.checkOffsetAndCount(offset, count);

        return this.referenceToCells.values()
            .stream()
            .skip(offset)
            .limit(count)
            .collect(Collectors.toCollection(Lists::array));
    }

    @Override
    public List<Set<SpreadsheetCellReference>> between(final T from,
                                                       final T to) {
        Store.checkBetween(from, to);

        return this.referenceToCells.entrySet()
            .stream()
            .filter(e -> castToComparable(
                    e.getKey()
                ).compareTo(from) >= 0 &&
                    castToComparable(
                        e.getKey()
                    ).compareTo(to) <= 0
            ).map(Map.Entry::getValue)
            .collect(Collectors.toCollection(Lists::array));
    }

    private Comparable<T> castToComparable(final T cellOrLabel) {
        return Cast.to(cellOrLabel);
    }

    @Override
    public void saveCells(final T reference,
                          final Set<SpreadsheetCellReference> cells) {
        Objects.requireNonNull(reference, "reference");
        Objects.requireNonNull(cells, "cells");

        this.saveCellsWithRelativeReference(
            (T) reference.toRelative(),
            cells
        );
    }

    private void saveCellsWithRelativeReference(final T reference,
                                                final Set<SpreadsheetCellReference> cells) {
        final Set<SpreadsheetCellReference> previous = this.referenceToCells.get(reference);
        if (null == previous) {
            cells.forEach(cell -> this.addCellNonNull(
                    ReferenceAndSpreadsheetCellReference.with(
                        reference,
                        cell.toRelative()
                    )
                )
            );
        } else {
            final Set<SpreadsheetCellReference> copy = Sets.ordered();
            copy.addAll(previous);

            cells.stream()
                .map(SpreadsheetCellReference::toRelative)
                .filter(cell -> false == copy.contains(cell))
                .forEach(cell -> this.addCellNonNull(
                        ReferenceAndSpreadsheetCellReference.with(
                            reference,
                            cell.toRelative()
                        )
                    )
                );

            copy.stream()
                .filter(cell -> false == cells.contains(cell))
                .forEach(cell -> this.removeCellNonNull(
                        ReferenceAndSpreadsheetCellReference.with(
                            reference,
                            cell.toRelative()
                        )
                    )
                );
        }
    }

    @Override
    public void addCell(final ReferenceAndSpreadsheetCellReference<T> referenceAndCell) {
        this.addCellNonNull(
            Objects.requireNonNull(referenceAndCell, "referenceAndCell")
                .setCell(referenceAndCell.cell().toRelative())
                .setReference((T) referenceAndCell.reference().toRelative())
        );
    }

    private void addCellNonNull(final ReferenceAndSpreadsheetCellReference<T> referenceAndCell) {
        final T reference = referenceAndCell.reference();
        final SpreadsheetCellReference cell = referenceAndCell.cell()
            .toRelative();

        SortedSet<SpreadsheetCellReference> allCells = this.referenceToCells.get(reference);
        //noinspection Java8MapApi
        if (null == allCells) {
            allCells = emptySortedSet();
            this.referenceToCells.put(reference, allCells);
        }
        allCells.add(cell);

        SortedSet<T> references = this.cellToReferences.get(cell);
        //noinspection Java8MapApi
        if (null == references) {
            references = emptySortedSet();
            this.cellToReferences.put(
                cell,
                references
            );
        }
        references.add(reference);

        this.addCellWatchers.accept(referenceAndCell);
    }

    private static <T extends SpreadsheetExpressionReference> SortedSet<T> emptySortedSet() {
        return SortedSets.tree(SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR);
    }

    @Override
    public Runnable addAddCellWatcher(final Consumer<ReferenceAndSpreadsheetCellReference<T>> watcher) {
        return this.addCellWatchers.add(watcher);
    }

    private final Watchers<ReferenceAndSpreadsheetCellReference<T>> addCellWatchers = Watchers.create();

    @Override
    public void removeCell(final ReferenceAndSpreadsheetCellReference<T> referenceAndCell) {
        this.removeCellNonNull(
            Objects.requireNonNull(referenceAndCell, "referenceAndCell")
        );
    }

    private void removeCellNonNull(final ReferenceAndSpreadsheetCellReference<T> referenceAndCell) {
        final T reference = referenceAndCell.reference();
        final SpreadsheetCellReference cell = referenceAndCell.cell();

        final Set<SpreadsheetCellReference> allCells = this.referenceToCells.get(reference);
        final boolean removed = null != allCells;
        if (removed) {
            allCells.remove(cell);
            if (allCells.isEmpty()) {
                this.referenceToCells.remove(reference);
                this.deleteWatchers.accept(reference);
            }
        }

        if (removed) {
            final Set<T> allReferences = this.cellToReferences.get(cell);
            if (null != allReferences) {
                if (allReferences.remove(reference)) {
                    if (allReferences.isEmpty()) {
                        this.cellToReferences.remove(cell);
                    }
                }
            }
            this.removeCellWatchers.accept(referenceAndCell);
        }
    }

    @Override
    public Runnable addRemoveCellWatcher(final Consumer<ReferenceAndSpreadsheetCellReference<T>> watcher) {
        return this.removeCellWatchers.add(watcher);
    }

    private final Watchers<ReferenceAndSpreadsheetCellReference<T>> removeCellWatchers = Watchers.create();

    @Override
    public Set<SpreadsheetCellReference> findCellsWithReference(final T reference,
                                                                final int offset,
                                                                final int count) {
        Objects.requireNonNull(reference, "reference");
        Store.checkOffsetAndCount(
            offset,
            count
        );

        final SortedSet<SpreadsheetCellReference> cells = this.referenceToCells.get(reference);
        return null != cells ?
            cells.stream()
                .skip(offset)
                .limit(count)
                .collect(ImmutableSortedSet.collector(null)) :
            SortedSets.empty();
    }

    @Override
    public int countCellsWithReference(final T reference) {
        Objects.requireNonNull(reference, "reference");

        final SortedSet<SpreadsheetCellReference> cells = this.referenceToCells.get(reference);
        return null != cells ?
            cells.size() :
            0;
    }

    @Override
    public Set<T> findReferencesWithCell(final SpreadsheetCellReference cell,
                                         final int offset,
                                         final int count) {
        Objects.requireNonNull(cell, "cell");
        Store.checkOffsetAndCount(
            offset,
            count
        );

        final SortedSet<T> references = this.cellToReferences.get(cell);
        return null != references ?
            references.stream()
                .skip(offset)
                .limit(count)
                .collect(ImmutableSortedSet.collector(null)) :
            SortedSets.empty();
    }

    @Override
    public void removeReferencesWithCell(final SpreadsheetCellReference cell) {
        Objects.requireNonNull(cell, "cell");

        for (final T reference : new TreeSet<>(this.referenceToCells.keySet())) {
            this.removeCellNonNull(
                ReferenceAndSpreadsheetCellReference.with(
                    reference,
                    cell
                )
            );
        }
    }

    // helpers..........................................................................................................

    /**
     * Something like labels and the cell references expressions containing the label.
     */
    // VisibleForTesting
    final Map<T, SortedSet<SpreadsheetCellReference>> referenceToCells;

    /**
     * The inverse of {@link #referenceToCells}
     */
    // VisibleForTesting
    final Map<SpreadsheetCellReference, SortedSet<T>> cellToReferences;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return ToStringBuilder.empty()
            .label("referenceToCells")
            .value(this.referenceToCells)
            .label("cellToReferences")
            .value(this.cellToReferences)
            .build();
    }
}

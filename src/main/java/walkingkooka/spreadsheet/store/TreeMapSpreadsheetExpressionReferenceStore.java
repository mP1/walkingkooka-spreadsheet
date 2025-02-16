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
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.store.Store;
import walkingkooka.watch.Watchers;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
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
    }

    @Override
    public Optional<Set<SpreadsheetCellReference>> load(final T reference) {
        Objects.requireNonNull(reference, "reference");

        // best to take defensive copy to prevent ConcurrentModificationException
        return Optional.ofNullable(
                this.referenceToCell.get(reference)
        ).map(SortedSets::immutable);
    }

    @Override
    public void delete(final T reference) {
        Objects.requireNonNull(reference, "reference");

        this.removeAllWithReferenceAndFireDeleteWatchers(reference);
    }

    /**
     * Delete the reference and all referrers to that reference.
     */
    private void removeAllWithReferenceAndFireDeleteWatchers(final T reference) {
        if (this.removeAllWithReference(reference)) {
            this.deleteWatchers.accept(reference);
        }
    }

    /**
     * Delete the cells for the given {@link SpreadsheetExpressionReference}
     */
    private boolean removeAllWithReference(final T reference) {
        // where id=label remove label to cells, then remove cell to label.
        final Set<SpreadsheetCellReference> allCells = this.referenceToCell.remove(reference);

        final boolean needsFire = null != allCells;
        if (needsFire) {
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
        return needsFire;
    }

    @Override
    public Runnable addDeleteWatcher(final Consumer<T> deleted) {
        return this.deleteWatchers.add(deleted);
    }

    private final Watchers<T> deleteWatchers = Watchers.create();

    @Override
    public int count() {
        return this.referenceToCell.size();
    }

    @Override
    public Set<T> ids(final int from,
                      final int count) {
        Store.checkFromAndCount(from, count);

        return this.referenceToCell.keySet()
                .stream()
                .skip(from)
                .limit(count)
                .collect(Collectors.toCollection(Sets::ordered));
    }

    @Override
    public List<Set<SpreadsheetCellReference>> values(final int from,
                                                      final int count) {
        Store.checkFromAndCount(from, count);

        return this.referenceToCell.values()
                .stream()
                .skip(from)
                .limit(count)
                .collect(Collectors.toCollection(Lists::array));
    }

    @Override
    public List<Set<SpreadsheetCellReference>> between(final T from,
                                                       final T to) {
        Store.checkBetween(from, to);

        return this.referenceToCell.entrySet()
                .stream()
                .filter(e -> comparable(e.getKey()).compareTo(from) >= 0 && comparable(e.getKey()).compareTo(to) <= 0)
                .map(Map.Entry::getValue)
                .collect(Collectors.toCollection(Lists::array));
    }

    private Comparable<T> comparable(final T cellOrLabel) {
        return Cast.to(cellOrLabel);
    }

    @Override
    public void saveCells(final T reference,
                          final Set<SpreadsheetCellReference> cells) {
        Objects.requireNonNull(reference, "reference");
        Objects.requireNonNull(cells, "cells");

        final Set<SpreadsheetCellReference> previous = this.referenceToCell.get(reference);
        if (null == previous) {
            cells.forEach(r -> this.addCell0(
                            ReferenceAndSpreadsheetCellReference.with(
                                    reference,
                                    r
                            )
                    )
            );
        } else {
            final Set<SpreadsheetCellReference> copy = Sets.ordered();
            copy.addAll(previous);

            cells.stream()
                    .map(SpreadsheetCellReference::toRelative)
                    .filter(r -> !copy.contains(r))
                    .forEach(r -> this.addCell0(
                                    ReferenceAndSpreadsheetCellReference.with(
                                            reference,
                                            r
                                    )
                            )
                    );

            copy.stream()
                    .filter(r -> !cells.contains(r))
                    .forEach(r -> this.removeCell0(
                                    ReferenceAndSpreadsheetCellReference.with(
                                            reference,
                                            r
                                    )
                            )
                    );
        }
    }

    @Override
    public void addCell(final ReferenceAndSpreadsheetCellReference<T> referenceAndCell) {
        this.addCell0(
                referenceAndSpreadsheetCellReferenceCheck(referenceAndCell)
        );
    }

    private void addCell0(final ReferenceAndSpreadsheetCellReference<T> referenceAndCell) {
        final T reference = referenceAndCell.reference();
        final SpreadsheetCellReference cell = referenceAndCell.cell()
                .toRelative();

        SortedSet<SpreadsheetCellReference> allCells = this.referenceToCell.get(reference);
        //noinspection Java8MapApi
        if (null == allCells) {
            allCells = SortedSets.tree();
            this.referenceToCell.put(reference, allCells);
        }
        allCells.add(cell);

        SortedSet<T> references = this.cellToReferences.get(cell);
        //noinspection Java8MapApi
        if (null == references) {
            references = SortedSets.tree();
            this.cellToReferences.put(
                    cell,
                    references
            );
        }
        references.add(reference);

        this.addCellWatchers.accept(referenceAndCell);
    }

    @Override
    public Runnable addAddCellWatcher(final Consumer<ReferenceAndSpreadsheetCellReference<T>> watcher) {
        return this.addCellWatchers.add(watcher);
    }

    private final Watchers<ReferenceAndSpreadsheetCellReference<T>> addCellWatchers = Watchers.create();

    @Override
    public void removeCell(final ReferenceAndSpreadsheetCellReference<T> referenceAndCell) {
        this.removeCell0(
                referenceAndSpreadsheetCellReferenceCheck(referenceAndCell)
        );
    }

    private void removeCell0(final ReferenceAndSpreadsheetCellReference<T> referenceAndCell) {
        final T reference = referenceAndCell.reference();
        final SpreadsheetCellReference cell = referenceAndCell.cell();

        final Set<SpreadsheetCellReference> allCells = this.referenceToCell.get(reference);
        final boolean removed = null != allCells;
        if (removed) {
            allCells.remove(cell);
            if (allCells.isEmpty()) {
                this.referenceToCell.remove(reference);
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
    public Set<T> findReferencesWithCell(final SpreadsheetCellReference cell) {
        Objects.requireNonNull(cell, "cell");

        final SortedSet<T> references = this.cellToReferences.get(cell);
        return null != references ?
                SortedSets.immutable(references) :
                SortedSets.empty();
    }

    // helpers..........................................................................................................

    private ReferenceAndSpreadsheetCellReference<T> referenceAndSpreadsheetCellReferenceCheck(final ReferenceAndSpreadsheetCellReference<T> referenceAndCell) {
        return Objects.requireNonNull(referenceAndCell, "referenceAndCell");
    }

    /**
     * Something like labels and the cell references expressions containing the label.
     */
    // VisibleForTesting
    final Map<T, SortedSet<SpreadsheetCellReference>> referenceToCell = Maps.sorted();

    /**
     * The inverse of {@link #referenceToCell}
     */
    // VisibleForTesting
    final Map<SpreadsheetCellReference, SortedSet<T>> cellToReferences = Maps.sorted();

    @Override
    public String toString() {
        return this.referenceToCell.toString();
    }
}

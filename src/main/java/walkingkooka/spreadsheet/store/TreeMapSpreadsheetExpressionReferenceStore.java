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
    public Optional<Set<SpreadsheetCellReference>> load(final T target) {
        Objects.requireNonNull(target, "target");

        // best to take defensive copy to prevent ConcurrentModificationException
        return Optional.ofNullable(
                this.targetToCell.get(target)
        ).map(SortedSets::immutable);
    }

    @Override
    public void delete(final T target) {
        Objects.requireNonNull(target, "target");

        this.removeAllWithTargetAndFireDeleteWatchers(target);
    }

    /**
     * Delete the reference and all referrers to that reference.
     */
    private void removeAllWithTargetAndFireDeleteWatchers(final T target) {
        if (this.removeAllWithTarget(target)) {
            this.deleteWatchers.accept(target);
        }
    }

    /**
     * Delete the reference and all referrers to that reference.
     */
    private boolean removeAllWithTarget(final T target) {
        // where id=label remove label to cells, then remove cell to label.
        final Set<SpreadsheetCellReference> allReferences = this.targetToCell.remove(target);

        final boolean needsFire = null != allReferences;
        if (needsFire) {
            for (final SpreadsheetCellReference reference : allReferences) {
                final Set<T> targets = this.cellToTargets.get(reference);
                if (null != targets) {
                    if (targets.remove(target)) {
                        if (targets.isEmpty()) {
                            this.cellToTargets.remove(reference);
                        }
                        this.removeCellWatchers.accept(TargetAndSpreadsheetCellReference.with(target, reference));
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
        return this.targetToCell.size();
    }

    @Override
    public Set<T> ids(final int from,
                      final int count) {
        Store.checkFromAndCount(from, count);

        return this.targetToCell.keySet()
                .stream()
                .skip(from)
                .limit(count)
                .collect(Collectors.toCollection(Sets::ordered));
    }

    @Override
    public List<Set<SpreadsheetCellReference>> values(final int from,
                                                      final int count) {
        Store.checkFromAndCount(from, count);

        return this.targetToCell.values()
                .stream()
                .skip(from)
                .limit(count)
                .collect(Collectors.toCollection(Lists::array));
    }

    @Override
    public List<Set<SpreadsheetCellReference>> between(final T from,
                                                       final T to) {
        Store.checkBetween(from, to);

        return this.targetToCell.entrySet()
                .stream()
                .filter(e -> comparable(e.getKey()).compareTo(from) >= 0 && comparable(e.getKey()).compareTo(to) <= 0)
                .map(Map.Entry::getValue)
                .collect(Collectors.toCollection(Lists::array));
    }

    private Comparable<T> comparable(final T cellOrLabel) {
        return Cast.to(cellOrLabel);
    }

    @Override
    public void saveCells(final T target,
                          final Set<SpreadsheetCellReference> cells) {
        Objects.requireNonNull(target, "target");
        Objects.requireNonNull(cells, "cells");

        final Set<SpreadsheetCellReference> previous = this.targetToCell.get(target);
        if (null == previous) {
            cells.forEach(r -> this.addCell0(
                            TargetAndSpreadsheetCellReference.with(
                                    target,
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
                                    TargetAndSpreadsheetCellReference.with(
                                            target,
                                            r
                                    )
                            )
                    );

            copy.stream()
                    .filter(r -> !cells.contains(r))
                    .forEach(r -> this.removeCell0(
                                    TargetAndSpreadsheetCellReference.with(
                                            target,
                                            r
                                    )
                            )
                    );
        }
    }

    @Override
    public void addCell(final TargetAndSpreadsheetCellReference<T> targetAndCell) {
        this.addCell0(
                checkTargetAndSpreadsheetCellReference(targetAndCell)
        );
    }

    private void addCell0(final TargetAndSpreadsheetCellReference<T> targetAndCell) {
        final T target = targetAndCell.target();
        final SpreadsheetCellReference cell = targetAndCell.reference()
                .toRelative();

        SortedSet<SpreadsheetCellReference> allCells = this.targetToCell.get(target);
        //noinspection Java8MapApi
        if (null == allCells) {
            allCells = SortedSets.tree();
            this.targetToCell.put(target, allCells);
        }
        allCells.add(cell);

        SortedSet<T> targets = this.cellToTargets.get(cell);
        //noinspection Java8MapApi
        if (null == targets) {
            targets = SortedSets.tree();
            this.cellToTargets.put(cell, targets);
        }
        targets.add(target);

        this.addCellWatchers.accept(targetAndCell);
    }

    @Override
    public Runnable addAddCellWatcher(final Consumer<TargetAndSpreadsheetCellReference<T>> watcher) {
        return this.addCellWatchers.add(watcher);
    }

    private final Watchers<TargetAndSpreadsheetCellReference<T>> addCellWatchers = Watchers.create();

    @Override
    public void removeCell(final TargetAndSpreadsheetCellReference<T> targetAndCell) {
        this.removeCell0(
                checkTargetAndSpreadsheetCellReference(targetAndCell)
        );
    }

    private void removeCell0(final TargetAndSpreadsheetCellReference<T> targetAndCell) {
        final T target = targetAndCell.target();
        final SpreadsheetCellReference cell = targetAndCell.reference();

        final Set<SpreadsheetCellReference> allCells = this.targetToCell.get(target);
        final boolean removed = null != allCells;
        if (removed) {
            allCells.remove(cell);
            if (allCells.isEmpty()) {
                this.targetToCell.remove(target);
                this.deleteWatchers.accept(target);
            }
        }

        if (removed) {
            final Set<T> allTargets = this.cellToTargets.get(cell);
            if (null != allTargets) {
                if (allTargets.remove(target)) {
                    if (allTargets.isEmpty()) {
                        this.cellToTargets.remove(cell);
                    }
                }
            }
            this.removeCellWatchers.accept(targetAndCell);
        }
    }

    @Override
    public Runnable addRemoveCellWatcher(final Consumer<TargetAndSpreadsheetCellReference<T>> watcher) {
        return this.removeCellWatchers.add(watcher);
    }

    private final Watchers<TargetAndSpreadsheetCellReference<T>> removeCellWatchers = Watchers.create();

    @Override
    public Set<T> loadTargets(final SpreadsheetCellReference cell) {
        Objects.requireNonNull(cell, "cell");

        final SortedSet<T> targets = this.cellToTargets.get(cell);
        return null != targets ?
                SortedSets.immutable(targets) :
                SortedSets.empty();
    }

    // helpers..........................................................................................................

    private TargetAndSpreadsheetCellReference<T> checkTargetAndSpreadsheetCellReference(final TargetAndSpreadsheetCellReference<T> targetAndCell) {
        return Objects.requireNonNull(targetAndCell, "targetAndCell");
    }

    /**
     * Something like labels and the cell references expressions containing the label.
     */
    // VisibleForTesting
    final Map<T, SortedSet<SpreadsheetCellReference>> targetToCell = Maps.sorted();

    /**
     * The inverse of {@link #targetToCell}
     */
    // VisibleForTesting
    final Map<SpreadsheetCellReference, SortedSet<T>> cellToTargets = Maps.sorted();

    @Override
    public String toString() {
        return this.targetToCell.toString();
    }
}

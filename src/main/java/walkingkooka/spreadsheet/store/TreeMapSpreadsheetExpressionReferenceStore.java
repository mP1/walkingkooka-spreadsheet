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

        return Optional.ofNullable(
                this.targetToReferences.get(target)
        ).map(Sets::readOnly);
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
        final Set<SpreadsheetCellReference> allReferences = this.targetToReferences.remove(target);

        final boolean needsFire = null != allReferences;
        if (needsFire) {
            for (final SpreadsheetCellReference reference : allReferences) {
                final Set<T> targets = this.referenceToTargets.get(reference);
                if (null != targets) {
                    if (targets.remove(target)) {
                        if (targets.isEmpty()) {
                            this.referenceToTargets.remove(reference);
                        }
                        this.removeReferenceWatchers.accept(TargetAndSpreadsheetCellReference.with(target, reference));
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
        return this.targetToReferences.size();
    }

    @Override
    public Set<T> ids(final int from,
                      final int count) {
        Store.checkFromAndCount(from, count);

        return this.targetToReferences.keySet()
                .stream()
                .skip(from)
                .limit(count)
                .collect(Collectors.toCollection(Sets::ordered));
    }

    @Override
    public List<Set<SpreadsheetCellReference>> values(final int from,
                                                      final int count) {
        Store.checkFromAndCount(from, count);

        return this.targetToReferences.values()
                .stream()
                .skip(from)
                .limit(count)
                .collect(Collectors.toCollection(Lists::array));
    }

    @Override
    public List<Set<SpreadsheetCellReference>> between(final T from,
                                                       final T to) {
        Store.checkBetween(from, to);

        return this.targetToReferences.entrySet()
                .stream()
                .filter(e -> comparable(e.getKey()).compareTo(from) >= 0 && comparable(e.getKey()).compareTo(to) <= 0)
                .map(Map.Entry::getValue)
                .collect(Collectors.toCollection(Lists::array));
    }

    private Comparable<T> comparable(final T cellOrLabel) {
        return Cast.to(cellOrLabel);
    }

    @Override
    public void saveReferences(final T target,
                               final Set<SpreadsheetCellReference> references) {
        Objects.requireNonNull(target, "target");
        Objects.requireNonNull(references, "references");

        final Set<SpreadsheetCellReference> previous = this.targetToReferences.get(target);
        if (null == previous) {
            references.forEach(r -> this.addReference0(
                            TargetAndSpreadsheetCellReference.with(
                                    target,
                                    r
                            )
                    )
            );
        } else {
            final Set<SpreadsheetCellReference> copy = Sets.ordered();
            copy.addAll(previous);

            references.stream()
                    .map(SpreadsheetCellReference::toRelative)
                    .filter(r -> !copy.contains(r))
                    .forEach(r -> this.addReference0(
                                    TargetAndSpreadsheetCellReference.with(
                                            target,
                                            r
                                    )
                            )
                    );

            copy.stream()
                    .filter(r -> !references.contains(r))
                    .forEach(r -> this.removeReference0(
                                    TargetAndSpreadsheetCellReference.with(
                                            target,
                                            r
                                    )
                            )
                    );
        }
    }

    @Override
    public void addReference(final TargetAndSpreadsheetCellReference<T> targetAndReference) {
        checkTargetAndReference(targetAndReference);

        this.addReference0(targetAndReference);
    }

    private void addReference0(final TargetAndSpreadsheetCellReference<T> targetAndReference) {
        final T target = targetAndReference.target();
        final SpreadsheetCellReference reference = targetAndReference.reference()
                .toRelative();

        SortedSet<SpreadsheetCellReference> allReferences = this.targetToReferences.get(target);
        //noinspection Java8MapApi
        if (null == allReferences) {
            allReferences = SortedSets.tree();
            this.targetToReferences.put(target, allReferences);
        }
        allReferences.add(reference);

        SortedSet<T> targets = this.referenceToTargets.get(reference);
        //noinspection Java8MapApi
        if (null == targets) {
            targets = SortedSets.tree();
            this.referenceToTargets.put(reference, targets);
        }
        targets.add(target);

        this.addReferenceWatchers.accept(targetAndReference);
    }

    @Override
    public Runnable addAddReferenceWatcher(final Consumer<TargetAndSpreadsheetCellReference<T>> watcher) {
        return this.addReferenceWatchers.add(watcher);
    }

    private final Watchers<TargetAndSpreadsheetCellReference<T>> addReferenceWatchers = Watchers.create();

    @Override
    public void removeReference(final TargetAndSpreadsheetCellReference<T> targetAndReference) {
        checkTargetAndReference(targetAndReference);

        this.removeReference0(targetAndReference);
    }

    private void removeReference0(final TargetAndSpreadsheetCellReference<T> targetAndReference) {
        final T target = targetAndReference.target();
        final SpreadsheetCellReference reference = targetAndReference.reference();

        final Set<SpreadsheetCellReference> allReferences = this.targetToReferences.get(target);
        final boolean removed = null != allReferences;
        if (removed) {
            allReferences.remove(reference);
            if (allReferences.isEmpty()) {
                this.targetToReferences.remove(target);
                this.deleteWatchers.accept(target);
            }
        }

        if (removed) {
            final Set<T> allTargets = this.referenceToTargets.get(reference);
            if (null != allTargets) {
                if (allTargets.remove(target)) {
                    if (allTargets.isEmpty()) {
                        this.referenceToTargets.remove(reference);
                    }
                }
            }
            this.removeReferenceWatchers.accept(targetAndReference);
        }
    }

    @Override
    public Runnable addRemoveReferenceWatcher(final Consumer<TargetAndSpreadsheetCellReference<T>> watcher) {
        return this.removeReferenceWatchers.add(watcher);
    }

    private final Watchers<TargetAndSpreadsheetCellReference<T>> removeReferenceWatchers = Watchers.create();

    @Override
    public Set<T> loadTargets(final SpreadsheetCellReference reference) {
        Objects.requireNonNull(reference, "reference");

        final SortedSet<T> targets = this.referenceToTargets.get(reference);
        return null != targets ?
                SortedSets.immutable(targets) :
                SortedSets.empty();
    }

    // helpers..........................................................................................................

    private static void checkTargetAndReference(final TargetAndSpreadsheetCellReference<?> targetAndReference) {
        Objects.requireNonNull(targetAndReference, "targetAndReference");
    }

    /**
     * Something like labels and the cell references expressions containing the label.
     */
    // VisibleForTesting
    final Map<T, SortedSet<SpreadsheetCellReference>> targetToReferences = Maps.sorted();

    /**
     * The inverse of {@link #targetToReferences}
     */
    // VisibleForTesting
    final Map<SpreadsheetCellReference, SortedSet<T>> referenceToTargets = Maps.sorted();

    @Override
    public String toString() {
        return this.targetToReferences.toString();
    }
}

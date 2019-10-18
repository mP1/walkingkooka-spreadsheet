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
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.store.Store;
import walkingkooka.store.Watchers;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * A {@link SpreadsheetReferenceStore} that uses a {@link Map} to store an entity to {@link SpreadsheetCellReference}
 */
final class TreeMapSpreadsheetReferenceStore<T extends ExpressionReference & Comparable<T>> implements SpreadsheetReferenceStore<T> {

    static <T extends ExpressionReference & Comparable<T>> TreeMapSpreadsheetReferenceStore<T> create() {
        return new TreeMapSpreadsheetReferenceStore<>();
    }

    private TreeMapSpreadsheetReferenceStore() {
        super();
    }

    @Override
    public Optional<Set<SpreadsheetCellReference>> load(final T id) {
        checkId(id);
        return Optional.ofNullable(this.targetToReferences.get(id)).map(Sets::readOnly);
    }

    @Override
    public Runnable addSaveWatcher(final Consumer<Set<SpreadsheetCellReference>> saved) {
        Objects.requireNonNull(saved, "saved");
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(final T id) {
        checkId(id);
        this.removeAllWithTargetAndFireDeleteWatchers(id);
    }

    /**
     * Delete the reference and all referrers to that reference.
     */
    private void removeAllWithTargetAndFireDeleteWatchers(final T id) {
        if (this.removeAllWithTarget(id)) {
            this.deleteWatchers.accept(id);
        }
    }

    /**
     * Delete the reference and all referrers to that reference.
     */
    private boolean removeAllWithTarget(final T id) {
        // where id=label remove label to cells, then remove cell to label.
        final Set<SpreadsheetCellReference> referrers = this.targetToReferences.remove(id);

        final boolean needsFire = null != referrers;
        if (needsFire) {
            for (final SpreadsheetCellReference referrer : referrers) {
                final Set<T> targets = this.referenceToTargets.get(referrer);
                if (null != targets) {
                    if (targets.remove(id)) {
                        if (targets.isEmpty()) {
                            this.referenceToTargets.remove(referrer);
                        }
                        this.removeReferenceWatchers.accept(TargetAndSpreadsheetCellReference.with(id, referrer));
                    }
                }
            }
        }
        return needsFire;
    }

    @Override
    public Runnable addDeleteWatcher(final Consumer<T> deleted) {
        return this.deleteWatchers.addWatcher(deleted);
    }

    private final Watchers<T> deleteWatchers = Watchers.create();

    @Override
    public int count() {
        return this.targetToReferences.size();
    }

    @Override
    public Set<T> ids(final int from,
                      final int count) {
        Store.checkFromAndTo(from, count);

        return this.targetToReferences.keySet()
                .stream()
                .skip(from)
                .limit(count)
                .collect(Collectors.toCollection(Sets::ordered));
    }

    @Override
    public List<Set<SpreadsheetCellReference>> values(final T from,
                                                      final int count) {
        Store.checkFromAndToIds(from, count);

        return this.targetToReferences.entrySet()
                .stream()
                .filter(e -> e.getKey().compareTo(from) >= 0)
                .map(Map.Entry::getValue)
                .limit(count)
                .collect(Collectors.toCollection(Lists::array));
    }

    @Override
    public void saveReferences(final T id, final Set<SpreadsheetCellReference> referrers) {
        checkId(id);
        Objects.requireNonNull(referrers, "referrers");

        final Set<SpreadsheetCellReference> previous = this.targetToReferences.get(id);
        if (null == previous) {
            referrers.forEach(r -> this.addReference0(TargetAndSpreadsheetCellReference.with(id, r)));
        } else {
            final Set<SpreadsheetCellReference> copy = Sets.ordered();
            copy.addAll(previous);

            referrers.stream()
                    .filter(r -> !copy.contains(r))
                    .forEach(r -> this.addReference0(TargetAndSpreadsheetCellReference.with(id, r)));

            copy.stream()
                    .filter(r -> !referrers.contains(r))
                    .forEach(r -> this.removeReference0(TargetAndSpreadsheetCellReference.with(id, r)));
        }
    }

    @Override
    public void addReference(final TargetAndSpreadsheetCellReference<T> targetAndReference) {
        checkReferrerAndReference(targetAndReference);

        this.addReference0(targetAndReference);
    }

    private void addReference0(final TargetAndSpreadsheetCellReference<T> targetAndReference) {
        final T id = targetAndReference.target();
        final SpreadsheetCellReference reference = targetAndReference.reference();

        Set<SpreadsheetCellReference> referrers = this.targetToReferences.get(id);
        //noinspection Java8MapApi
        if (null == referrers) {
            referrers = Sets.sorted();
            this.targetToReferences.put(id, referrers);
        }
        referrers.add(reference);

        Set<T> targets = this.referenceToTargets.get(reference);
        //noinspection Java8MapApi
        if (null == targets) {
            targets = Sets.sorted();
            this.referenceToTargets.put(reference, targets);
        }
        targets.add(id);

        this.addReferenceWatchers.accept(targetAndReference);
    }

    @Override
    public Runnable addAddReferenceWatcher(final Consumer<TargetAndSpreadsheetCellReference<T>> watcher) {
        return this.addReferenceWatchers.addWatcher(watcher);
    }

    private final Watchers<TargetAndSpreadsheetCellReference<T>> addReferenceWatchers = Watchers.create();

    @Override
    public void removeReference(final TargetAndSpreadsheetCellReference<T> targetAndReference) {
        checkReferrerAndReference(targetAndReference);

        this.removeReference0(targetAndReference);
    }

    private void removeReference0(final TargetAndSpreadsheetCellReference<T> targetAndReference) {
        final T id = targetAndReference.target();
        final SpreadsheetCellReference reference = targetAndReference.reference();

        final Set<SpreadsheetCellReference> referrers = this.targetToReferences.get(id);
        final boolean removed = null != referrers;
        if (removed) {
            referrers.remove(reference);
            if (referrers.isEmpty()) {
                this.targetToReferences.remove(id);
                this.deleteWatchers.accept(id);
            }
        }

        if (removed) {
            final Set<T> ids = this.referenceToTargets.get(reference);
            if (null != ids) {
                if (ids.remove(id)) {
                    if (ids.isEmpty()) {
                        this.referenceToTargets.remove(reference);
                    }
                }
            }
            this.removeReferenceWatchers.accept(targetAndReference);
        }
    }

    @Override
    public Runnable addRemoveReferenceWatcher(final Consumer<TargetAndSpreadsheetCellReference<T>> watcher) {
        return this.removeReferenceWatchers.addWatcher(watcher);
    }

    private final Watchers<TargetAndSpreadsheetCellReference<T>> removeReferenceWatchers = Watchers.create();

    @Override
    public Set<T> loadReferred(final SpreadsheetCellReference referrer) {
        checkReferrer(referrer);

        final Set<T> targets = this.referenceToTargets.get(referrer);
        return null != targets ?
                Sets.immutable(targets) :
                Sets.empty();
    }

    // helpers..........................................................................................

    private void checkId(final T id) {
        Objects.requireNonNull(id, "id");
    }

    private static void checkReferrer(final SpreadsheetCellReference referrer) {
        Objects.requireNonNull(referrer, "referrer");
    }

    private static void checkReferrerAndReference(final TargetAndSpreadsheetCellReference targetAndReference) {
        Objects.requireNonNull(targetAndReference, "targetAndReference");
    }

    /**
     * Something like labels and the cell references expressions containing the label.
     */
    // VisibleForTesting
    final Map<T, Set<SpreadsheetCellReference>> targetToReferences = Maps.sorted();

    /**
     * The inverse of {@link #targetToReferences}
     */
    // VisibleForTesting
    final Map<SpreadsheetCellReference, Set<T>> referenceToTargets = Maps.sorted(SpreadsheetCellReference.COMPARATOR);

    @Override
    public String toString() {
        return this.targetToReferences.toString();
    }
}

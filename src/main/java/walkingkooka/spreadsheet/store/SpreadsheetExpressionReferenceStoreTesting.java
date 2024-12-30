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

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.reflect.TypeNameTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.store.StoreTesting;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

@SuppressWarnings("unchecked")
public interface SpreadsheetExpressionReferenceStoreTesting<S extends SpreadsheetExpressionReferenceStore<T>,
        T extends SpreadsheetExpressionReference & Comparable<T>>
        extends StoreTesting<S, T, Set<SpreadsheetCellReference>>,
        TypeNameTesting<S> {

    @Test
    @Override
    default void testSaveNullFails() {
        assertThrows(UnsupportedOperationException.class, () -> this.createStore().save(Sets.of(SpreadsheetSelection.A1)));
    }

    @Test
    default void testAddSaveWatcherFails() {
        assertThrows(UnsupportedOperationException.class, () -> this.createStore().addSaveWatcher((a) -> {
        }));
    }

    @Override
    default void testAddSaveWatcherAndRemove() {
    }

    @Test
    default void testAddDeleteWatcherAndDelete2() {
        final Set<SpreadsheetCellReference> references = this.value();

        final S store = this.createStore();

        final List<T> fired = Lists.array();
        store.addDeleteWatcher(fired::add);

        final T id = this.id();

        references.forEach(v -> store.addReference(TargetAndSpreadsheetCellReference.with(id, v)));

        store.delete(id);

        this.checkEquals(Lists.of(id), fired, "fired values");
    }

    // addReference...........................................................................................

    @Test
    default void testAddReferenceNullFails() {
        assertThrows(NullPointerException.class, () -> this.createStore().addReference(null));
    }

    @Test
    default void testAddReferenceWithWatcher() {
        final S store = this.createStore();

        final T target = this.id();
        final SpreadsheetCellReference reference = SpreadsheetSelection.parseCell("Z99");

        final List<TargetAndSpreadsheetCellReference<T>> fired = Lists.array();
        store.addAddReferenceWatcher(fired::add);

        final TargetAndSpreadsheetCellReference<T> targeted = TargetAndSpreadsheetCellReference.with(target, reference);
        store.addReference(targeted);

        this.checkEquals(Lists.of(targeted), fired, "fired add reference events");
    }

    // removeReference...........................................................................................

    @Test
    default void testRemoveReferenceNullFails() {
        assertThrows(NullPointerException.class, () -> this.createStore().removeReference(null));
    }

    @Test
    default void testRemoveLastReferenceAddDeleteWatcher() {
        final Set<SpreadsheetCellReference> references = this.value();

        final S store = this.createStore();

        final List<T> fired = Lists.array();
        store.addDeleteWatcher(fired::add);

        final T id = this.id();

        references.forEach(v -> store.addReference(TargetAndSpreadsheetCellReference.with(id, v)));
        references.forEach(v -> store.removeReference(TargetAndSpreadsheetCellReference.with(id, v)));

        this.checkEquals(Lists.of(id), fired, "fired values");
    }

    @Test
    default void testRemoveReferenceWithWatcher() {
        final S store = this.createStore();

        final T target = this.id();
        final SpreadsheetCellReference reference = SpreadsheetSelection.parseCell("Z99");

        final List<TargetAndSpreadsheetCellReference<T>> fired = Lists.array();
        store.addAddReferenceWatcher(fired::add);

        final TargetAndSpreadsheetCellReference<T> targeted = TargetAndSpreadsheetCellReference.with(target, reference);
        store.addReference(targeted);
        store.removeReference(targeted);

        this.checkEquals(Lists.of(targeted), fired, "fired add reference events");
    }

    @Test
    default void testDeleteWithRemoveReferenceWatcher() {
        final S store = this.createStore();

        final T target = this.id();
        final SpreadsheetCellReference reference = SpreadsheetSelection.parseCell("Z99");

        store.saveReferences(target, Sets.of(reference));

        final List<TargetAndSpreadsheetCellReference<T>> fired = Lists.array();
        store.addRemoveReferenceWatcher(fired::add);

        store.delete(target);

        this.checkEquals(Lists.of(TargetAndSpreadsheetCellReference.with(target, reference)),
                fired,
                "fired remove reference events");
    }

    // saveReferences...........................................................................................

    @Test
    default void testSaveReferencesNullIdFails() {
        assertThrows(NullPointerException.class, () -> this.createStore().saveReferences(null, Sets.of(SpreadsheetSelection.A1)));
    }

    @Test
    default void testSaveReferencesNullTargetFails() {
        assertThrows(NullPointerException.class, () -> this.createStore().saveReferences(this.id(), null));
    }

    @Test
    default void testSaveReferences() {
        final S store = this.createStore();
        store.saveReferences(this.id(), this.value());
    }

    @Test
    default void testSaveReferencesDoesntFireDeleteWatchers() {
        final S store = this.createStore();
        store.addDeleteWatcher((v) -> {
            throw new UnsupportedOperationException();
        });
        store.saveReferences(this.id(), this.value());
    }

    @Test
    default void testSaveReferencesAddReferenceWatcher() {
        final S store = this.createStore();

        final T id = this.id();
        final Set<SpreadsheetCellReference> references = this.value();

        final List<TargetAndSpreadsheetCellReference<T>> fired = Lists.array();
        store.addAddReferenceWatcher(fired::add);
        store.saveReferences(id, references);

        this.checkEquals(references.stream()
                        .map(r -> TargetAndSpreadsheetCellReference.with(id, r))
                        .collect(Collectors.toList()),
                fired,
                "fired add reference");
    }

    @Test
    default void testSaveReferencesReplaceNoneAddReferenceWatcher() {
        final S store = this.createStore();

        final T id = this.id();
        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("B2");

        store.saveReferences(id, Sets.of(b2));

        store.addAddReferenceWatcher((e) -> {
            throw new UnsupportedOperationException();
        });
        store.addRemoveReferenceWatcher((e) -> {
            throw new UnsupportedOperationException();
        });
        store.saveReferences(id, Sets.of(b2));
    }

    @Test
    default void testSaveReferencesReplaceAddReferenceWatcher() {
        final S store = this.createStore();

        final T id = this.id();
        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("B2");

        store.saveReferences(id, Sets.of(b2));

        final List<TargetAndSpreadsheetCellReference<T>> addFired = Lists.array();
        store.addAddReferenceWatcher(addFired::add);

        final List<TargetAndSpreadsheetCellReference<T>> removeFired = Lists.array();
        store.addRemoveReferenceWatcher(removeFired::add);

        final SpreadsheetCellReference z9 = SpreadsheetSelection.parseCell("Z9");
        store.saveReferences(id, Sets.of(z9));

        this.checkEquals(Lists.of(TargetAndSpreadsheetCellReference.with(id, z9)),
                addFired,
                "fired add reference");
        this.checkEquals(Lists.of(TargetAndSpreadsheetCellReference.with(id, b2)),
                removeFired,
                "fired remove reference");
    }

    @Test
    default void testSaveReferencesReplaceAddReferenceWatcher2() {
        final S store = this.createStore();

        final T id = this.id();
        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("B2");
        final SpreadsheetCellReference c3 = SpreadsheetSelection.parseCell("C3");

        store.saveReferences(id, Sets.of(b2, c3));

        final List<TargetAndSpreadsheetCellReference<T>> addFired = Lists.array();
        store.addAddReferenceWatcher(addFired::add);

        final List<TargetAndSpreadsheetCellReference<T>> removeFired = Lists.array();
        store.addRemoveReferenceWatcher(removeFired::add);

        final SpreadsheetCellReference d4 = SpreadsheetSelection.parseCell("d4");
        store.saveReferences(id, Sets.of(c3, d4));

        this.checkEquals(Lists.of(TargetAndSpreadsheetCellReference.with(id, d4)),
                addFired,
                "fired add reference");
        this.checkEquals(Lists.of(TargetAndSpreadsheetCellReference.with(id, b2)),
                removeFired,
                "fired remove reference");
    }

    /**
     * The key
     */
    @Override
    T id();

    default void loadAndCheck(final S store, final T id, final SpreadsheetCellReference... references) {
        this.loadAndCheck(store, id, Sets.of(references));
    }

    @Override
    default void loadAndCheck(final S store, final T id, final Set<SpreadsheetCellReference> references) {
        if (references.isEmpty()) {
            this.loadFailCheck(store, id);
        } else {
            StoreTesting.super.loadAndCheck(store, id, references);

        }

        for (SpreadsheetCellReference reference : references) {
            final Set<T> referred = store.loadTargets(reference);
            if (!referred.contains(id)) {
                fail(store + " loadTargets " + reference + " didnt return id " + id + ", actual: " + referred);
            }
        }
    }

    // loadTargets.....................................................................................................

    @Test
    default void testLoadTargetsWithNullReferenceFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createStore().loadTargets(null)
        );
    }

    @SuppressWarnings("unchecked")
    default void loadTargetsAndCheck(final S store,
                                     final SpreadsheetCellReference reference,
                                     final T... targets) {
        this.loadTargetsAndCheck(
                store,
                reference,
                Sets.of(targets)
        );
    }

    default void loadTargetsAndCheck(final S store,
                                     final SpreadsheetCellReference reference,
                                     final Set<T> targets) {
        this.checkEquals(
                targets,
                store.loadTargets(reference),
                "loadTargets " + reference
        );

        for (T id : targets) {
            final Optional<Set<SpreadsheetCellReference>> references = store.load(id);
            if (false == references.isPresent() && references.get().contains(reference)) {
                fail(store + " load " + id + " didnt return reference " + reference + ", actual: " + references);
            }
        }
    }

    // TypeNameTesting...........................................................................................

    @Override
    default String typeNameSuffix() {
        return SpreadsheetExpressionReferenceStore.class.getSimpleName();
    }
}

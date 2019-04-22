package walkingkooka.spreadsheet.store.reference;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.store.StoreTesting;
import walkingkooka.test.TypeNameTesting;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public interface SpreadsheetReferenceStoreTesting<S extends SpreadsheetReferenceStore<T>, T extends ExpressionReference & Comparable<T>>
        extends StoreTesting<S, T, Set<SpreadsheetCellReference>>,
        TypeNameTesting<S> {

    @Test
    default void testSaveNullFails() {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.createStore().save(Sets.of(SpreadsheetCellReference.parse("A1")));
        });
    }

    @Test
    default void testAddSaveWatcherFails() {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.createStore().addSaveWatcher((a) -> {
            });
        });
    }

    @Override
    default void testAddSaveWatcherAndRemove() {
    }

    @Test
    default void testAddDeleteWatcherAndDelete2() {
        final Set<SpreadsheetCellReference> references = this.value();

        final S store = this.createStore();

        final List<T> fired = Lists.array();
        store.addDeleteWatcher((d) -> fired.add(d));

        final T id = this.id();

        references.forEach(v -> store.addReference(TargetAndSpreadsheetCellReference.with(id, v)));

        store.delete(id);

        assertEquals(Lists.of(id), fired, "fired values");
    }

    // addReference...........................................................................................

    @Test
    default void testAddReferenceNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createStore().addReference(null);
        });
    }

    @Test
    default void testAddReferenceWithWatcher() {
        final S store = this.createStore();

        final T target = this.id();
        final SpreadsheetCellReference reference = SpreadsheetCellReference.parse("Z99");

        final List<TargetAndSpreadsheetCellReference<T>> fired = Lists.array();
        store.addAddReferenceWatcher((e) -> fired.add(e));

        final TargetAndSpreadsheetCellReference<T> targeted = TargetAndSpreadsheetCellReference.with(target, reference);
        store.addReference(targeted);

        assertEquals(Lists.of(targeted), fired, "fired add reference events");
    }

    // removeReference...........................................................................................

    @Test
    default void testRemoveReferenceNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createStore().removeReference(null);
        });
    }

    @Test
    default void testRemoveLastReferenceAddDeleteWatcher() {
        final Set<SpreadsheetCellReference> references = this.value();

        final S store = this.createStore();

        final List<T> fired = Lists.array();
        store.addDeleteWatcher((d) -> fired.add(d));

        final T id = this.id();

        references.forEach(v -> store.addReference(TargetAndSpreadsheetCellReference.with(id, v)));
        references.forEach(v -> store.removeReference(TargetAndSpreadsheetCellReference.with(id, v)));

        assertEquals(Lists.of(id), fired, "fired values");
    }

    @Test
    default void testRemoveReferenceWithWatcher() {
        final S store = this.createStore();

        final T target = this.id();
        final SpreadsheetCellReference reference = SpreadsheetCellReference.parse("Z99");

        final List<TargetAndSpreadsheetCellReference<T>> fired = Lists.array();
        store.addAddReferenceWatcher((e) -> fired.add(e));

        final TargetAndSpreadsheetCellReference<T> targeted = TargetAndSpreadsheetCellReference.with(target, reference);
        store.addReference(targeted);
        store.removeReference(targeted);

        assertEquals(Lists.of(targeted), fired, "fired add reference events");
    }

    @Test
    default void testDeleteWithRemoveReferenceWatcher() {
        final S store = this.createStore();

        final T target = this.id();
        final SpreadsheetCellReference reference = SpreadsheetCellReference.parse("Z99");

        store.saveReferences(target, Sets.of(reference));

        final List<TargetAndSpreadsheetCellReference<T>> fired = Lists.array();
        store.addRemoveReferenceWatcher((e) -> fired.add(e));

        store.delete(target);

        assertEquals(Lists.of(TargetAndSpreadsheetCellReference.with(target, reference)),
                fired,
                "fired remove reference events");
    }

    // saveReferences...........................................................................................

    @Test
    default void testSaveReferencesNullIdFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createStore().saveReferences(null, Sets.of(SpreadsheetCellReference.parse("A1")));
        });
    }

    @Test
    default void testSaveReferencesNullTargetFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createStore().saveReferences(this.id(), null);
        });
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

        assertEquals(references.stream()
                        .map(r -> TargetAndSpreadsheetCellReference.with(id, r))
                        .collect(Collectors.toList()),
                fired,
                "fired add reference");
    }

    @Test
    default void testSaveReferencesReplaceNoneAddReferenceWatcher() {
        final S store = this.createStore();

        final T id = this.id();
        final SpreadsheetCellReference b2 = SpreadsheetCellReference.parse("B2");

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
        final SpreadsheetCellReference b2 = SpreadsheetCellReference.parse("B2");

        store.saveReferences(id, Sets.of(b2));

        final List<TargetAndSpreadsheetCellReference<T>> addFired = Lists.array();
        store.addAddReferenceWatcher(addFired::add);

        final List<TargetAndSpreadsheetCellReference<T>> removeFired = Lists.array();
        store.addRemoveReferenceWatcher(removeFired::add);

        final SpreadsheetCellReference z9 = SpreadsheetCellReference.parse("Z9");
        store.saveReferences(id, Sets.of(z9));

        assertEquals(Lists.of(TargetAndSpreadsheetCellReference.with(id, z9)),
                addFired,
                "fired add reference");
        assertEquals(Lists.of(TargetAndSpreadsheetCellReference.with(id, b2)),
                removeFired,
                "fired remove reference");
    }

    @Test
    default void testSaveReferencesReplaceAddReferenceWatcher2() {
        final S store = this.createStore();

        final T id = this.id();
        final SpreadsheetCellReference b2 = SpreadsheetCellReference.parse("B2");
        final SpreadsheetCellReference c3 = SpreadsheetCellReference.parse("C3");

        store.saveReferences(id, Sets.of(b2, c3));

        final List<TargetAndSpreadsheetCellReference<T>> addFired = Lists.array();
        store.addAddReferenceWatcher(addFired::add);

        final List<TargetAndSpreadsheetCellReference<T>> removeFired = Lists.array();
        store.addRemoveReferenceWatcher(removeFired::add);

        final SpreadsheetCellReference d4 = SpreadsheetCellReference.parse("d4");
        store.saveReferences(id, Sets.of(c3, d4));

        assertEquals(Lists.of(TargetAndSpreadsheetCellReference.with(id, d4)),
                addFired,
                "fired add reference");
        assertEquals(Lists.of(TargetAndSpreadsheetCellReference.with(id, b2)),
                removeFired,
                "fired remove reference");
    }

    // loadReferred...........................................................................................

    @Test
    default void testLoadReferredNullReferenceFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createStore().loadReferred(null);
        });
    }

    /**
     * The key
     */
    T id();

    default void loadAndCheck(final S store, final T id, final SpreadsheetCellReference... references) {
        this.loadAndCheck(store, id, Sets.of(references));
    }

    default void loadAndCheck(final S store, final T id, final Set<SpreadsheetCellReference> references) {
        if (references.isEmpty()) {
            this.loadFailCheck(store, id);
        } else {
            StoreTesting.super.loadAndCheck(store, id, references);

        }

        for (SpreadsheetCellReference reference : references) {
            final Set<T> referred = store.loadReferred(reference);
            if (!referred.contains(id)) {
                fail(store + " loadReferred " + reference + " didnt return id " + id + ", actual: " + referred);
            }
        }
    }

    default void loadReferredAndCheck(final S store, final SpreadsheetCellReference reference, final T... ids) {
        this.loadReferredAndCheck(store, reference, Sets.of(ids));
    }

    default void loadReferredAndCheck(final S store, final SpreadsheetCellReference reference, final Set<T> ids) {
        assertEquals(ids,
                store.loadReferred(reference),
                "loadReferred " + reference);

        for (T id : ids) {
            final Optional<Set<SpreadsheetCellReference>> references = store.load(id);
            if (!references.isPresent() && references.get().contains(reference)) {
                fail(store + " load " + id + " didnt return reference " + reference + ", actual: " + references);
            }
        }
    }

    // TypeNameTesting...........................................................................................

    @Override
    default String typeNameSuffix() {
        return SpreadsheetReferenceStore.class.getSimpleName();
    }
}

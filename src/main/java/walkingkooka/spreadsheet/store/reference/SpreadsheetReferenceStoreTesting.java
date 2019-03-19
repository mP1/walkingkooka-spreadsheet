package walkingkooka.spreadsheet.store.reference;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.store.StoreTesting;
import walkingkooka.test.TypeNameTesting;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public interface SpreadsheetReferenceStoreTesting<S extends SpreadsheetReferenceStore<T>, T extends ExpressionReference & Comparable<T>>
        extends StoreTesting<S, T, Set<SpreadsheetCellReference>>, TypeNameTesting<S> {

    @Test
    default void testSaveNullFails() {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.createStore().save(Sets.of(SpreadsheetCellReference.parse("A1")));
        });
    }

    // addReference...........................................................................................

    @Test
    default void testAddReferenceNullIdFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createStore().addReference(null, SpreadsheetCellReference.parse("A1"));
        });
    }

    @Test
    default void testAddReferenceNullTargetFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createStore().addReference(this.id(), null);
        });
    }

    // removeReference...........................................................................................

    @Test
    default void testRemoveReferenceNullIdFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createStore().removeReference(null, SpreadsheetCellReference.parse("A1"));
        });
    }

    @Test
    default void testRemoveReferenceNullTargetFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createStore().removeReference(this.id(), null);
        });
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
        if(references.isEmpty()) {
            fail("References is empty");
        }

        StoreTesting.super.loadAndCheck(store, id, references);

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

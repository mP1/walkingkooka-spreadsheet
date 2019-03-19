package walkingkooka.spreadsheet.store.reference;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.store.StoreTesting;
import walkingkooka.test.TypeNameTesting;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

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

    /**
     * The key
     */
    T id();

    // TypeNameTesting...........................................................................................

    @Override
    default String typeNameSuffix() {
        return SpreadsheetReferenceStore.class.getSimpleName();
    }
}

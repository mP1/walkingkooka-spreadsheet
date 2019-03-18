package walkingkooka.spreadsheet.store.cellreferences;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.store.StoreTesting;
import walkingkooka.test.TypeNameTesting;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetCellReferenceStoreTesting<S extends SpreadsheetCellReferenceStore>
        extends StoreTesting<S, SpreadsheetCellReference, Set<SpreadsheetCellReference>>, TypeNameTesting<S> {

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
            this.createStore().addReference(SpreadsheetCellReference.parse("A1"), null);
        });
    }

    @Test
    default void testAddReferenceIdEqualsTargetFails() {
        final SpreadsheetCellReference a1 = SpreadsheetCellReference.parse("A1");

        assertThrows(IllegalArgumentException.class, () -> {
            this.createStore().addReference(a1, a1);
        });
    }

    @Test
    default void testAddReferenceIdEqualsTargetFails2() {
        assertThrows(IllegalArgumentException.class, () -> {
            this.createStore().addReference(SpreadsheetCellReference.parse("A1"), SpreadsheetCellReference.parse("$A$1"));
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
            this.createStore().removeReference(SpreadsheetCellReference.parse("A1"), null);
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
            this.createStore().saveReferences(SpreadsheetCellReference.parse("A1"), null);
        });
    }

    @Test
    default void testSaveReferencesIncludesTargetFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            this.createStore().saveReferences(SpreadsheetCellReference.parse("A1"),
                    Sets.of(SpreadsheetCellReference.parse("A1"), SpreadsheetCellReference.parse("B1")));
        });
    }

    @Test
    default void testSaveReferencesIncludesTargetFails2() {
        assertThrows(IllegalArgumentException.class, () -> {
            this.createStore().saveReferences(SpreadsheetCellReference.parse("A1"),
                    Sets.of(SpreadsheetCellReference.parse("$A$1"), SpreadsheetCellReference.parse("B1")));
        });
    }

    // TypeNameTesting...........................................................................................

    @Override
    default String typeNameSuffix() {
        return SpreadsheetCellReferenceStore.class.getSimpleName();
    }
}

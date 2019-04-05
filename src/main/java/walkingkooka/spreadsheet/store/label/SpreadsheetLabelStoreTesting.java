package walkingkooka.spreadsheet.store.label;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.store.StoreTesting;
import walkingkooka.spreadsheet.store.security.SpreadsheetGroupStore;
import walkingkooka.test.TypeNameTesting;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetLabelStoreTesting<S extends SpreadsheetLabelStore> extends StoreTesting<S, SpreadsheetLabelName, SpreadsheetLabelMapping>,
        TypeNameTesting<S> {

    @Test
    default void testLoadCellReferencesOrRangesNullLabelFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createStore().loadCellReferencesOrRanges(null);
        });
    }

    @Test
    default void testLabelsNullSpreadsheetCellReferenceFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createStore().labels(null);
        });
    }

    @Test
    default void testLabelsWithUnknownCellReference() {
        final S store = this.createStore();
        this.labelsAndCheck(store, SpreadsheetCellReference.parse("Z99"));
    }

    @Test
    default void testLabelsWithCellReference() {
        final S store = this.createStore();

        final SpreadsheetLabelName label = SpreadsheetLabelName.with("LabelZ99");
        final SpreadsheetCellReference reference = SpreadsheetCellReference.parse("Z99");

        store.save(SpreadsheetLabelMapping.with(label, reference));
        store.save(SpreadsheetLabelMapping.with(SpreadsheetLabelName.with("DifferentLabel"), SpreadsheetCellReference.parse("A1")));

        this.labelsAndCheck(store, reference, label);
    }

    @Test
    default void testLabelsWithCellReference2() {
        final S store = this.createStore();

        final SpreadsheetLabelName label1 = SpreadsheetLabelName.with("LabelZ991");
        final SpreadsheetLabelName label2 = SpreadsheetLabelName.with("LabelZ992");
        final SpreadsheetLabelName label3 = SpreadsheetLabelName.with("LabelZ993");

        final SpreadsheetCellReference reference = SpreadsheetCellReference.parse("Z99");

        store.save(SpreadsheetLabelMapping.with(label1, reference));
        store.save(SpreadsheetLabelMapping.with(label2, reference));
        store.save(SpreadsheetLabelMapping.with(label3, reference));

        this.labelsAndCheck(store, reference, label1, label2, label3);
    }

    @Test
    default void testLabelsWithCellIndirectReference() {
        final S store = this.createStore();

        final SpreadsheetLabelName indirect = SpreadsheetLabelName.with("IndirectLabelZ99");
        final SpreadsheetLabelName label = SpreadsheetLabelName.with("LabelZ99");
        final SpreadsheetCellReference reference = SpreadsheetCellReference.parse("Z99");

        store.save(SpreadsheetLabelMapping.with(indirect, reference));
        store.save(SpreadsheetLabelMapping.with(label, indirect));

        this.labelsAndCheck(store, reference, indirect, label);
    }

    default void loadCellReferencesOrRangesAndCheck(final SpreadsheetLabelStore store,
                                                    final SpreadsheetLabelName label,
                                                    final Set<? super ExpressionReference> referencesOrRanges) {
        assertEquals(referencesOrRanges,
                store.loadCellReferencesOrRanges(label),
                ()-> "loadCellReferencesOrRanges for " + label);
    }

    default void labelsAndCheck(final SpreadsheetLabelStore store,
                                final SpreadsheetCellReference reference,
                                final SpreadsheetLabelName... labels) {
        this.labelsAndCheck(store, reference, Sets.of(labels));
    }

    default void labelsAndCheck(final SpreadsheetLabelStore store,
                                final SpreadsheetCellReference reference,
                                final Set<SpreadsheetLabelName> labels) {
        assertEquals(labels,
                store.labels(reference),
                () -> "labels for " + reference);
    }

    @Override
    default SpreadsheetLabelName id() {
        return SpreadsheetLabelName.with("abc123456789");
    }

    @Override
    default SpreadsheetLabelMapping value() {
        return SpreadsheetLabelMapping.with(this.id(), SpreadsheetCellReference.parse("A1"));
    }

    // TypeNameTesting..................................................................

    @Override
    default String typeNameSuffix() {
        return SpreadsheetLabelStore.class.getSimpleName();
    }
}

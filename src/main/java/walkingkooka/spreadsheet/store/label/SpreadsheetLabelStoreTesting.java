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

package walkingkooka.spreadsheet.store.label;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetCellReference;
import walkingkooka.spreadsheet.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.SpreadsheetLabelName;
import walkingkooka.spreadsheet.store.SpreadsheetStoreTesting;
import walkingkooka.test.TypeNameTesting;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetLabelStoreTesting<S extends SpreadsheetLabelStore> extends SpreadsheetStoreTesting<S, SpreadsheetLabelName, SpreadsheetLabelMapping>,
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
        this.labelsAndCheck(store, SpreadsheetExpressionReference.parseCellReference("Z99"));
    }

    @Test
    default void testLabelsWithCellReference() {
        final S store = this.createStore();

        final SpreadsheetLabelName label = SpreadsheetExpressionReference.labelName("LabelZ99");
        final SpreadsheetCellReference reference = SpreadsheetExpressionReference.parseCellReference("Z99");

        store.save(SpreadsheetLabelMapping.with(label, reference));
        store.save(SpreadsheetLabelMapping.with(SpreadsheetExpressionReference.labelName("DifferentLabel"), SpreadsheetExpressionReference.parseCellReference("A1")));

        this.labelsAndCheck(store, reference, label);
    }

    @Test
    default void testLabelsWithCellReference2() {
        final S store = this.createStore();

        final SpreadsheetLabelName label1 = SpreadsheetExpressionReference.labelName("LabelZ991");
        final SpreadsheetLabelName label2 = SpreadsheetExpressionReference.labelName("LabelZ992");
        final SpreadsheetLabelName label3 = SpreadsheetExpressionReference.labelName("LabelZ993");

        final SpreadsheetCellReference reference = SpreadsheetExpressionReference.parseCellReference("Z99");

        store.save(SpreadsheetLabelMapping.with(label1, reference));
        store.save(SpreadsheetLabelMapping.with(label2, reference));
        store.save(SpreadsheetLabelMapping.with(label3, reference));

        this.labelsAndCheck(store, reference, label1, label2, label3);
    }

    @Test
    default void testLabelsWithCellIndirectReference() {
        final S store = this.createStore();

        final SpreadsheetLabelName indirect = SpreadsheetExpressionReference.labelName("IndirectLabelZ99");
        final SpreadsheetLabelName label = SpreadsheetExpressionReference.labelName("LabelZ99");
        final SpreadsheetCellReference reference = SpreadsheetExpressionReference.parseCellReference("Z99");

        store.save(SpreadsheetLabelMapping.with(indirect, reference));
        store.save(SpreadsheetLabelMapping.with(label, indirect));

        this.labelsAndCheck(store, reference, indirect, label);
    }

    default void loadCellReferencesOrRangesAndCheck(final SpreadsheetLabelStore store,
                                                    final SpreadsheetLabelName label,
                                                    final Set<? super ExpressionReference> referencesOrRanges) {
        assertEquals(referencesOrRanges,
                store.loadCellReferencesOrRanges(label),
                () -> "loadCellReferencesOrRanges for " + label);
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
        return SpreadsheetExpressionReference.labelName("abc123456789");
    }

    @Override
    default SpreadsheetLabelMapping value() {
        return SpreadsheetLabelMapping.with(this.id(), SpreadsheetExpressionReference.parseCellReference("A1"));
    }

    // TypeNameTesting..................................................................

    @Override
    default String typeNameSuffix() {
        return SpreadsheetLabelStore.class.getSimpleName();
    }
}

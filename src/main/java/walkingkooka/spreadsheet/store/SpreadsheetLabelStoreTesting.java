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
import walkingkooka.collect.set.Sets;
import walkingkooka.reflect.TypeNameTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.store.MissingStoreException;
import walkingkooka.store.StoreTesting;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetLabelStoreTesting<S extends SpreadsheetLabelStore> extends StoreTesting<S, SpreadsheetLabelName, SpreadsheetLabelMapping>,
        TypeNameTesting<S> {

    // findSimilar......................................................................................................

    @Test
    default void testFindSimilarNullTextFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createStore()
                        .findSimilar(
                                null,
                                1
                        )
        );
    }

    @Test
    default void testFindSimilarInvalidCountFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> this.createStore()
                        .findSimilar(
                                "text",
                                -1
                        )
        );
    }

    @Test
    default void testFindSimilarEmptyText() {
        this.findSimilarAndCheck("", 1);
    }

    @Test
    default void testFindSimilarZeroCount() {
        this.findSimilarAndCheck("text", 0);
    }

    default void findSimilarAndCheck(final String text,
                                     final int count,
                                     final SpreadsheetLabelMapping... mappings) {
        this.findSimilarAndCheck(
                this.createStore(),
                text,
                count,
                mappings
        );
    }

    default void findSimilarAndCheck(final SpreadsheetLabelStore store,
                                     final String text,
                                     final int count,
                                     final SpreadsheetLabelMapping... mappings) {
        this.findSimilarAndCheck(
                store,
                text,
                count,
                Sets.of(mappings)
        );
    }

    default void findSimilarAndCheck(final SpreadsheetLabelStore store,
                                     final String text,
                                     final int count,
                                     final Set<SpreadsheetLabelMapping> mappings) {
        this.checkEquals(
                mappings,
                store.findSimilar(
                        text,
                        count
                ),
                () -> "findSimilar " + CharSequences.quoteAndEscape(text) + " count=" + count
        );
    }

    // loadCellOrRanges.................................................................................................

    @Test
    default void testLoadCellOrRangesNullLabelFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createStore()
                        .loadCellOrRanges(null)
        );
    }

    default void loadCellOrRangesAndCheck(final SpreadsheetLabelStore store,
                                          final SpreadsheetLabelName label,
                                          final Set<? super ExpressionReference> referencesOrRanges) {
        this.checkEquals(referencesOrRanges,
                store.loadCellOrRanges(label),
                () -> "loadCellOrRanges for " + label);
    }

    // labels...........................................................................................................

    @Test
    default void testLabelsNullSpreadsheetCellReferenceFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createStore()
                        .labels(null)
        );
    }

    @Test
    default void testLabelsWithUnknownCellReference() {
        final S store = this.createStore();
        this.labelsAndCheck(store, SpreadsheetSelection.parseCell("Z99"));
    }

    @Test
    default void testLabelsWithCellReference() {
        final S store = this.createStore();

        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("LabelZ99");
        final SpreadsheetCellReference reference = SpreadsheetSelection.parseCell("Z99");

        store.save(SpreadsheetLabelMapping.with(label, reference));
        store.save(SpreadsheetLabelMapping.with(SpreadsheetSelection.labelName("DifferentLabel"), SpreadsheetSelection.A1));

        this.labelsAndCheck(
                store,
                reference,
                label.setLabelMappingTarget(reference)
        );
    }

    @Test
    default void testLabelsWithCellReference2() {
        final S store = this.createStore();

        final SpreadsheetLabelName label1 = SpreadsheetSelection.labelName("LabelZ991");
        final SpreadsheetLabelName label2 = SpreadsheetSelection.labelName("LabelZ992");
        final SpreadsheetLabelName label3 = SpreadsheetSelection.labelName("LabelZ993");

        final SpreadsheetCellReference reference = SpreadsheetSelection.parseCell("Z99");

        final SpreadsheetLabelMapping mapping1 = store.save(SpreadsheetLabelMapping.with(label1, reference));
        final SpreadsheetLabelMapping mapping2 = store.save(SpreadsheetLabelMapping.with(label2, reference));
        final SpreadsheetLabelMapping mapping3 = store.save(SpreadsheetLabelMapping.with(label3, reference));

        this.labelsAndCheck(
                store,
                reference,
                mapping1,
                mapping2,
                mapping3
        );
    }

    @Test
    default void testLabelsWithCellIndirectReference() {
        final S store = this.createStore();

        final SpreadsheetLabelName indirect = SpreadsheetSelection.labelName("IndirectLabelZ99");
        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("LabelZ99");
        final SpreadsheetCellReference reference = SpreadsheetSelection.parseCell("Z99");

        final SpreadsheetLabelMapping mapping1 = store.save(SpreadsheetLabelMapping.with(indirect, reference));
        final SpreadsheetLabelMapping mapping2 = store.save(SpreadsheetLabelMapping.with(label, indirect));

        this.labelsAndCheck(
                store,
                reference,
                mapping1,
                mapping2
        );
    }

    default void labelsAndCheck(final SpreadsheetLabelStore store,
                                final SpreadsheetExpressionReference reference,
                                final SpreadsheetLabelMapping... labels) {
        this.labelsAndCheck(
                store,
                reference,
                Sets.of(labels)
        );
    }

    default void labelsAndCheck(final SpreadsheetLabelStore store,
                                final SpreadsheetExpressionReference reference,
                                final Set<SpreadsheetLabelMapping> labels) {
        this.checkEquals(
                labels,
                store.labels(reference),
                () -> "labels for " + reference
        );
    }

    // resolveLabel.....................................................................................................

    @Test
    default void testResolveLabelOrFail() {
        final S store = this.createStore();
        boolean tested = false;

        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            final SpreadsheetLabelName label = SpreadsheetSelection.labelName("Label" + i);
            if (!store.load(label).isPresent()) {
                assertThrows(
                        MissingStoreException.class,
                        () -> store.resolveLabelOrFail(label),
                        () -> "Unknown Label: " + label + " should have failed"
                );
                tested = true;
                break;
            }
        }

        this.checkEquals(true, tested, () -> "Unable to find an unknown label");
    }

    @Override
    default SpreadsheetLabelName id() {
        return SpreadsheetSelection.labelName("abc123456789");
    }

    @Override
    default SpreadsheetLabelMapping value() {
        return SpreadsheetLabelMapping.with(this.id(), SpreadsheetSelection.A1);
    }

    // TypeNameTesting..................................................................................................

    @Override
    default String typeNameSuffix() {
        return SpreadsheetLabelStore.class.getSimpleName();
    }
}

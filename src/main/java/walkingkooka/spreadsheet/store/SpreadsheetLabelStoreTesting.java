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

    // findLabelsByName......................................................................................................

    @Test
    default void testFindLabelsByNameNullTextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStore()
                .findLabelsByName(
                    null,
                    0,
                    1
                )
        );
    }

    @Test
    default void testFindLabelsByNameInvalidOffsetFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createStore()
                .findLabelsByName(
                    "text",
                    -1,
                    0
                )
        );
    }

    @Test
    default void testFindLabelsByNameInvalidCountFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createStore()
                .findLabelsByName(
                    "text",
                    0,
                    -1
                )
        );
    }

    @Test
    default void testFindLabelsByNameEmptyText() {
        this.findLabelsByNameAndCheck(
            "",
            0,
            1
        );
    }

    @Test
    default void testFindLabelsByNameZeroCount() {
        this.findLabelsByNameAndCheck(
            "text",
            0,
            0
        );
    }

    default void findLabelsByNameAndCheck(final String text,
                                          final int offset,
                                          final int count,
                                          final SpreadsheetLabelMapping... mappings) {
        this.findLabelsByNameAndCheck(
            this.createStore(),
            text,
            offset,
            count,
            mappings
        );
    }

    default void findLabelsByNameAndCheck(final SpreadsheetLabelStore store,
                                          final String text,
                                          final int offset,
                                          final int count,
                                          final SpreadsheetLabelMapping... mappings) {
        this.findLabelsByNameAndCheck(
            store,
            text,
            offset,
            count,
            Sets.of(mappings)
        );
    }

    default void findLabelsByNameAndCheck(final SpreadsheetLabelStore store,
                                          final String text,
                                          final int offset,
                                          final int count,
                                          final Set<SpreadsheetLabelMapping> mappings) {
        this.checkEquals(
            mappings,
            store.findLabelsByName(
                text,
                offset,
                count
            ),
            () -> "findLabelsByName " + CharSequences.quoteAndEscape(text) + " offset=" + offset + " count=" + count
        );
    }

    // loadCellOrRanges.................................................................................................

    @Test
    default void testLoadCellOrCellRangesNullLabelFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStore()
                .loadCellOrCellRanges(null)
        );
    }

    default void loadCellOrCellRangesAndCheck(final SpreadsheetLabelStore store,
                                              final SpreadsheetLabelName label,
                                              final Set<? super ExpressionReference> referencesOrRanges) {
        this.checkEquals(referencesOrRanges,
            store.loadCellOrCellRanges(label),
            () -> "loadCellOrCellRanges for " + label);
    }

    // labels...........................................................................................................

    @Test
    default void testFindLabelsWithReferenceWithNullReferenceFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStore()
                .findLabelsWithReference(
                    null,
                    0, // offset
                    0 // count
                )
        );
    }

    @Test
    default void testFindLabelsWithReferenceWithNegativeOffsetFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createStore()
                .findLabelsWithReference(
                    SpreadsheetSelection.A1,
                    -1, // offset
                    0 // count
                )
        );
    }

    @Test
    default void testFindLabelsWithReferenceWithNegativeCountFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createStore()
                .findLabelsWithReference(
                    SpreadsheetSelection.A1,
                    0, // offset
                    -1 // count
                )
        );
    }

    @Test
    default void testFindLabelsWithReferenceWithUnknownCell() {
        final S store = this.createStore();
        this.findLabelsWithReferenceAndCheck(
            store,
            SpreadsheetSelection.parseCell("Z99"),
            0,
            1
        );
    }

    @Test
    default void testFindLabelsWithReferenceWithCell() {
        final S store = this.createStore();

        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("LabelZ99");
        final SpreadsheetCellReference reference = SpreadsheetSelection.parseCell("Z99");

        store.save(SpreadsheetLabelMapping.with(label, reference));
        store.save(SpreadsheetLabelMapping.with(SpreadsheetSelection.labelName("DifferentLabel"), SpreadsheetSelection.A1));

        this.findLabelsWithReferenceAndCheck(
            store,
            reference,
            0, // offset
            1, // count
            label.setLabelMappingReference(reference)
        );
    }

    @Test
    default void testFindLabelsWithReferenceWithCell2() {
        final S store = this.createStore();

        final SpreadsheetLabelName label1 = SpreadsheetSelection.labelName("LabelZ991");
        final SpreadsheetLabelName label2 = SpreadsheetSelection.labelName("LabelZ992");
        final SpreadsheetLabelName label3 = SpreadsheetSelection.labelName("LabelZ993");

        final SpreadsheetCellReference reference = SpreadsheetSelection.parseCell("Z99");

        final SpreadsheetLabelMapping mapping1 = store.save(SpreadsheetLabelMapping.with(label1, reference));
        final SpreadsheetLabelMapping mapping2 = store.save(SpreadsheetLabelMapping.with(label2, reference));
        final SpreadsheetLabelMapping mapping3 = store.save(SpreadsheetLabelMapping.with(label3, reference));

        this.findLabelsWithReferenceAndCheck(
            store,
            reference,
            0, // offset
            4, // count
            mapping1,
            mapping2,
            mapping3
        );
    }

    @Test
    default void testFindLabelsWithReferenceWithCellIndirectReference() {
        final S store = this.createStore();

        final SpreadsheetLabelName indirect = SpreadsheetSelection.labelName("IndirectLabelZ99");
        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("LabelZ99");
        final SpreadsheetCellReference reference = SpreadsheetSelection.parseCell("Z99");

        final SpreadsheetLabelMapping mapping1 = store.save(SpreadsheetLabelMapping.with(indirect, reference));
        final SpreadsheetLabelMapping mapping2 = store.save(SpreadsheetLabelMapping.with(label, indirect));

        this.findLabelsWithReferenceAndCheck(
            store,
            reference,
            0, // offset
            3, // count
            mapping1,
            mapping2
        );
    }

    default void findLabelsWithReferenceAndCheck(final SpreadsheetLabelStore store,
                                                 final SpreadsheetExpressionReference reference,
                                                 final int offset,
                                                 final int count,
                                                 final SpreadsheetLabelMapping... labels) {
        this.findLabelsWithReferenceAndCheck(
            store,
            reference,
            offset,
            count,
            Sets.of(labels)
        );
    }

    default void findLabelsWithReferenceAndCheck(final SpreadsheetLabelStore store,
                                                 final SpreadsheetExpressionReference reference,
                                                 final int offset,
                                                 final int count,
                                                 final Set<SpreadsheetLabelMapping> labels) {
        this.checkEquals(
            labels,
            store.findLabelsWithReference(
                reference,
                offset,
                count
            ),
            () -> "findLabelsWithReferenceAndCheck for " + reference + " offset=" + offset + " count=" + count
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

        this.checkEquals(true, tested, () -> "Unable to find a unknown label");
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

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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.reflect.ThrowableTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class TreeMapSpreadsheetLabelStoreTest extends SpreadsheetLabelStoreTestCase<TreeMapSpreadsheetLabelStore>
    implements HashCodeEqualsDefinedTesting2<TreeMapSpreadsheetLabelStore>,
    ThrowableTesting {

    private final static SpreadsheetLabelName LABEL1 = SpreadsheetSelection.labelName("label1");

    private final static SpreadsheetLabelName LABEL2 = SpreadsheetSelection.labelName("label2");

    private final static SpreadsheetLabelName LABEL3 = SpreadsheetSelection.labelName("label3");

    private final static SpreadsheetLabelName LABEL4 = SpreadsheetSelection.labelName("label4");

    private final static SpreadsheetLabelName LABEL5 = SpreadsheetSelection.labelName("label5");

    private final static SpreadsheetCellReference A1 =SpreadsheetSelection.A1;

    private final static SpreadsheetCellReference A2 = SpreadsheetSelection.parseCell("A2");

    private final static SpreadsheetCellReference A3 = SpreadsheetSelection.parseCell("A3");

    private final static SpreadsheetCellReference B2 = SpreadsheetSelection.parseCell("B2");

    private final static SpreadsheetCellRangeReference A1A3 = SpreadsheetSelection.parseCellRange("A1:A3");

    // save.............................................................................................................

    @Test
    public void testSaveCycleFails() {
        final TreeMapSpreadsheetLabelStore store = this.createStore();


        final SpreadsheetLabelMapping mapping1 = LABEL1.setLabelMappingReference(LABEL2);
        final SpreadsheetLabelMapping mapping2 = LABEL2.setLabelMappingReference(LABEL1);

        store.save(mapping1);

        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> store.save(mapping2)
        );

        this.getMessageAndCheck(
            thrown,
            "Cycle detected for \"label2\" -> \"label1\" -> \"label2\""
        );

        this.checkEquals(
            Lists.of(
                mapping1
            ),
            store.all()
        );
    }

    @Test
    public void testSaveCycleFails2() {
        final TreeMapSpreadsheetLabelStore store = this.createStore();

        final SpreadsheetLabelName label1 = SpreadsheetSelection.labelName("Label111");
        final SpreadsheetLabelName label2 = SpreadsheetSelection.labelName("Label222");
        final SpreadsheetLabelName label3 = SpreadsheetSelection.labelName("Label333");

        final SpreadsheetLabelMapping mapping1 = label1.setLabelMappingReference(label2);
        final SpreadsheetLabelMapping mapping2 = label2.setLabelMappingReference(label3);
        final SpreadsheetLabelMapping mapping3 = label3.setLabelMappingReference(label1);

        store.save(mapping1);
        store.save(mapping2);

        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> store.save(mapping3)
        );

        this.getMessageAndCheck(
            thrown,
            "Cycle detected for \"Label333\" -> \"Label111\" -> \"Label222\" -> \"Label333\""
        );

        // mapping3 must not have been saved
        this.checkEquals(
            Lists.of(
                mapping1,
                mapping2
            ),
            store.all()
        );
    }

    // between..........................................................................................................

    @Test
    public void testBetweenAll() {
        final TreeMapSpreadsheetLabelStore store = this.createStore();

        store.save(LABEL1.setLabelMappingReference(A1));
        store.save(LABEL2.setLabelMappingReference(A2));
        store.save(LABEL3.setLabelMappingReference(A3));

        this.betweenAndCheck(
            store,
            LABEL1,
            LABEL3,
            LABEL1.setLabelMappingReference(A1),
            LABEL2.setLabelMappingReference(A2),
            LABEL3.setLabelMappingReference(A3)
        );
    }

    @Test
    public void testBetweenOne() {
        final TreeMapSpreadsheetLabelStore store = this.createStore();

        store.save(LABEL1.setLabelMappingReference(A1));
        store.save(LABEL2.setLabelMappingReference(A2));
        store.save(LABEL3.setLabelMappingReference(A3));

        this.betweenAndCheck(
            store,
            LABEL2,
            LABEL2,
            LABEL2.setLabelMappingReference(A2)
        );
    }

    // findLabelsWithReference..........................................................................................

    @Test
    public void testFindLabelsWithReferenceWhereNoneWithCell() {
        this.findLabelsWithReferenceWhereNoneAndCheck(
            SpreadsheetSelection.parseCellRange("a3")
        );
    }

    @Test
    public void testFindLabelsWithReferenceWhereNoneWithCellRange() {
        this.findLabelsWithReferenceWhereNoneAndCheck(
            SpreadsheetSelection.parseCellRange("a3:a4")
        );
    }

    private void findLabelsWithReferenceWhereNoneAndCheck(final SpreadsheetExpressionReference reference) {
        final TreeMapSpreadsheetLabelStore store = this.createStore();

        store.save(LABEL1.setLabelMappingReference(A1));
        store.save(LABEL2.setLabelMappingReference(A2));

        this.findLabelsWithReferenceAndCheck(
            store,
            reference,
            0, // offset
            1 // count
        );
    }

    @Test
    public void testFindLabelsWithReferenceWhereSomeWithCell() {
        this.findLabelsWithReferenceWhereSomeAndCheck(A2);
    }

    @Test
    public void testFindLabelsWithReferenceWhereSomeWithCellRange() {
        this.findLabelsWithReferenceWhereSomeAndCheck(
            SpreadsheetSelection.parseCellRange("a2:a3")
        );
    }

    private void findLabelsWithReferenceWhereSomeAndCheck(final SpreadsheetExpressionReference reference) {
        final TreeMapSpreadsheetLabelStore store = this.createStore();

        store.save(LABEL1.setLabelMappingReference(A1));
        final SpreadsheetLabelMapping mapping = store.save(LABEL2.setLabelMappingReference(A2));

        this.findLabelsWithReferenceAndCheck(
            store,
            reference,
            0, // offset
            2, // count
            mapping
        );
    }

    @Test
    public void testFindLabelsWithReferenceWhereSomeWithCellAndLabelToLabels() {
        this.findLabelsWithReferenceWhereSomeAndLabelsToLabelsCheck(A2);
    }

    @Test
    public void testFindLabelsWithReferenceWhereSomeWithCellRangeAndLabelToLabels() {
        this.findLabelsWithReferenceWhereSomeAndLabelsToLabelsCheck(
            SpreadsheetSelection.parseCellRange("a2:a3")
        );
    }

    private void findLabelsWithReferenceWhereSomeAndLabelsToLabelsCheck(final SpreadsheetExpressionReference reference) {
        final TreeMapSpreadsheetLabelStore store = this.createStore();

        store.save(
            LABEL1.setLabelMappingReference(A1)
        );
        final SpreadsheetLabelMapping mapping = store.save(
            LABEL2.setLabelMappingReference(A2)
        );
        final SpreadsheetLabelMapping mapping2 = store.save(
            LABEL3.setLabelMappingReference(LABEL2)
        );

        this.findLabelsWithReferenceAndCheck(
            store,
            reference,
            0, // offset
            3, // count
            mapping,
            mapping2
        );
    }

    @Test
    public void testFindLabelsWithReferenceWhereSomeWithLabel() {
        final TreeMapSpreadsheetLabelStore store = this.createStore();

        store.save(
            LABEL1.setLabelMappingReference(A1)
        );
        final SpreadsheetLabelMapping mapping = store.save(
            LABEL2.setLabelMappingReference(A2)
        );
        final SpreadsheetLabelMapping mapping2 = store.save(
            LABEL3.setLabelMappingReference(LABEL2)
        );

        // label3 -> label2 -> a2
        this.findLabelsWithReferenceAndCheck(
            store,
            LABEL3,
            0, // offset
            2, // count
            mapping,
            mapping2
        );
    }

    @Test
    public void testFindLabelsWithReferenceWhereSomeWithCell2() {
        this.findLabelsWithReferenceWhereSomeAndCheck(
            A2
        );
    }

    @Test
    public void testFindLabelsWithReferenceWhereSomeWithCellRange2() {
        this.findLabelsWithReferenceWhereSomeAndCheck2(
            SpreadsheetSelection.parseCellRange("b2:b3")
        );
    }

    private void findLabelsWithReferenceWhereSomeAndCheck2(final SpreadsheetExpressionReference reference) {
        final TreeMapSpreadsheetLabelStore store = this.createStore();
        
        final SpreadsheetCellRangeReference b2c3 = SpreadsheetSelection.parseCellRange("b2:c3");

        store.save(
            LABEL1.setLabelMappingReference(A1)
        );
        final SpreadsheetLabelMapping mapping = store.save(
            LABEL2.setLabelMappingReference(b2c3)
        );

        this.findLabelsWithReferenceAndCheck(
            store,
            reference,
            0, // offset
            2, // count
            mapping
        );
    }

    @Test
    public void testFindLabelsWithReferenceWhereSomeWithCellAndOffset() {
        this.findLabelsWithReferenceWhereSomeAndOffsetAndCheck(B2);
    }

    @Test
    public void testFindLabelsWithReferenceWhereSomeWithCellRangeAndOffset() {
        this.findLabelsWithReferenceWhereSomeAndOffsetAndCheck(
            SpreadsheetSelection.parseCellRange("b2:b3")
        );
    }

    private void findLabelsWithReferenceWhereSomeAndOffsetAndCheck(final SpreadsheetExpressionReference reference) {
        final TreeMapSpreadsheetLabelStore store = this.createStore();
        
        final SpreadsheetCellRangeReference b2c3 = SpreadsheetSelection.parseCellRange("b2:c3");
        final SpreadsheetCellRangeReference b2d4 = SpreadsheetSelection.parseCellRange("b2:d4");

        store.save(
            LABEL1.setLabelMappingReference(A1)
        );
        final SpreadsheetLabelMapping mapping1 = store.save(
            LABEL2.setLabelMappingReference(b2c3)
        );
        final SpreadsheetLabelMapping mapping2 = store.save(
            LABEL3.setLabelMappingReference(b2d4)
        );

        this.findLabelsWithReferenceAndCheck(
            store,
            reference,
            1, // offset
            2, // count
            mapping2
        );
    }

    @Test
    public void testFindLabelsWithReferenceWhereSomeWithCellAndCount() {
        this.findLabelsWithReferenceWhereSomeAndCountAndCheck(B2);
    }

    @Test
    public void testFindLabelsWithReferenceWhereSomeWithCellRangeAndCount() {
        this.findLabelsWithReferenceWhereSomeAndCountAndCheck(
            SpreadsheetSelection.parseCellRange("b2:b3")
        );
    }

    private void findLabelsWithReferenceWhereSomeAndCountAndCheck(final SpreadsheetExpressionReference reference) {
        final TreeMapSpreadsheetLabelStore store = this.createStore();
        
        final SpreadsheetCellRangeReference b2c3 = SpreadsheetSelection.parseCellRange("b2:c3");
        final SpreadsheetCellRangeReference b2d4 = SpreadsheetSelection.parseCellRange("b2:d4");

        store.save(
            LABEL1.setLabelMappingReference(A1)
        );
        final SpreadsheetLabelMapping mapping1 = store.save(
            LABEL2.setLabelMappingReference(b2c3)
        );
        final SpreadsheetLabelMapping mapping2 = store.save(
            LABEL3.setLabelMappingReference(b2d4)
        );

        this.findLabelsWithReferenceAndCheck(
            store,
            reference,
            0, // offset
            1, // count
            mapping1
        );
    }


    @Test
    public void testFindLabelsWithReferenceWhereSomeWithCellAndOffsetAndCount() {
        this.findLabelsWithReferenceWhereSomeAndOffsetAndCountAndCheck(B2);
    }

    @Test
    public void testFindLabelsWithReferenceWhereSomeWithCellRangeAndOffsetAndCount() {
        this.findLabelsWithReferenceWhereSomeAndOffsetAndCountAndCheck(
            SpreadsheetSelection.parseCellRange("b2:b3")
        );
    }

    private void findLabelsWithReferenceWhereSomeAndOffsetAndCountAndCheck(final SpreadsheetExpressionReference reference) {
        final TreeMapSpreadsheetLabelStore store = this.createStore();
        
        final SpreadsheetCellRangeReference b2c3 = SpreadsheetSelection.parseCellRange("b2:c3");
        final SpreadsheetCellRangeReference b2d4 = SpreadsheetSelection.parseCellRange("b2:d4");
        final SpreadsheetCellRangeReference b2e5 = SpreadsheetSelection.parseCellRange("b2:e5");
        final SpreadsheetCellRangeReference b2f6 = SpreadsheetSelection.parseCellRange("b2:f6");

        // not matched by reference
        store.save(
            LABEL1.setLabelMappingReference(A1)
        );

        // offset=0
        final SpreadsheetLabelMapping mapping1 = store.save(
            LABEL2.setLabelMappingReference(b2c3)
        );

        // offset=1
        final SpreadsheetLabelMapping mapping2 = store.save(
            LABEL3.setLabelMappingReference(b2d4)
        );

        // offset=2
        final SpreadsheetLabelMapping mapping3 = store.save(
            LABEL4.setLabelMappingReference(b2e5)
        );

        // offset=3
        final SpreadsheetLabelMapping mapping4 = store.save(
            LABEL5.setLabelMappingReference(b2f6)
        );

        this.findLabelsWithReferenceAndCheck(
            store,
            reference,
            1, // offset
            2, // count
            mapping2,
            mapping3 // skips mapping1, mapping4 after
        );
    }

    // findLabelsByName......................................................................................................

    @Test
    public void testFindLabelsByNameNone() {
        final TreeMapSpreadsheetLabelStore store = this.createStore();

        store.save(
            SpreadsheetLabelMapping.with(
                LABEL1,
                A1
            )
        );

        this.findLabelsByNameAndCheck(
            store,
            LABEL2.value(),
            0,
            2
        );
    }

    @Test
    public void testFindLabelsByNameOnly() {
        final TreeMapSpreadsheetLabelStore store = this.createStore();
        
        final SpreadsheetLabelMapping mapping = LABEL1.setLabelMappingReference(A1);

        store.save(mapping);

        this.findLabelsByNameAndCheck(
            store,
            LABEL1.value(),
            0,
            1,
            mapping
        );
    }

    @Test
    public void testFindLabelsByNameOnly2() {
        final TreeMapSpreadsheetLabelStore store = this.createStore();

        final SpreadsheetLabelMapping mapping = LABEL1.setLabelMappingReference(A1);

        store.save(mapping);

        this.findLabelsByNameAndCheck(
            store,
            LABEL1.value(),
            0, // offset
            2, // count
            mapping
        );
    }

    @Test
    public void testFindLabelsByNameOnly3() {
        final TreeMapSpreadsheetLabelStore store = this.createStore();

        final SpreadsheetLabelMapping mapping = LABEL1.setLabelMappingReference(A1);

        store.save(mapping);
        store.save(
            SpreadsheetLabelMapping.with(
                SpreadsheetLabelName.labelName("xyz"),
                A1
            )
        );

        this.findLabelsByNameAndCheck(
            store,
            LABEL1.value(),
            0, // offset
            2, // count
            mapping
        );
    }

    @Test
    public void testFindLabelsByNameWithEmpty() {
        final TreeMapSpreadsheetLabelStore store = this.createStore();

        final SpreadsheetLabelName label123 = SpreadsheetLabelName.labelName("Label123");
        final SpreadsheetLabelMapping mapping = label123.setLabelMappingReference(A1);

        store.save(mapping);

        this.findLabelsByNameAndCheck(
            store,
            "",
            0, // offset
            2, // count
            mapping
        );
    }

    @Test
    public void testFindLabelsByNameWithNoneSeveral() {
        final TreeMapSpreadsheetLabelStore store = this.createStore();
        
        final SpreadsheetLabelName label1 = SpreadsheetLabelName.labelName("Label123");
        final SpreadsheetLabelMapping mapping1 = label1.setLabelMappingReference(A1);
        store.save(mapping1);

        final SpreadsheetLabelName label2 = SpreadsheetLabelName.labelName("Label1234");
        final SpreadsheetLabelMapping mapping2 = label2.setLabelMappingReference(A1);
        store.save(mapping2);

        final SpreadsheetLabelName label3 = SpreadsheetLabelName.labelName("Label999");
        store.save(SpreadsheetLabelMapping.with(label3, A1));

        this.findLabelsByNameAndCheck(
            store,
            "",
            0, // offset
            2, // count
            mapping1,
            mapping2
        );
    }

    @Test
    public void testFindLabelsByNameWithPartial() {
        final TreeMapSpreadsheetLabelStore store = this.createStore();

        final SpreadsheetLabelName label = SpreadsheetLabelName.labelName("Label123");
        final SpreadsheetLabelMapping mapping = label.setLabelMappingReference(A1);

        store.save(mapping);

        this.findLabelsByNameAndCheck(
            store,
            "Label",
            0, // offset
            2, // count
            mapping
        );
    }

    @Test
    public void testFindLabelsByNameWithPrefixSeveral() {
        final TreeMapSpreadsheetLabelStore store = this.createStore();
        
        final SpreadsheetLabelName label1 = SpreadsheetLabelName.labelName("Label123");
        final SpreadsheetLabelMapping mapping1 = label1.setLabelMappingReference(A1);
        store.save(mapping1);

        final SpreadsheetLabelName label2 = SpreadsheetLabelName.labelName("Label1234");
        final SpreadsheetLabelMapping mapping2 = label2.setLabelMappingReference(A1);
        store.save(mapping2);

        final SpreadsheetLabelName label3 = SpreadsheetLabelName.labelName("Label999");
        store.save(SpreadsheetLabelMapping.with(label3, A1));

        this.findLabelsByNameAndCheck(
            store,
            "Label123",
            0, // offset
            2, // count
            mapping1,
            mapping2
        );
    }

    @Test
    public void testFindLabelsByNameWithPrefixSeveral2() {
        final TreeMapSpreadsheetLabelStore store = this.createStore();

        final SpreadsheetLabelName label1 = SpreadsheetLabelName.labelName("Label123");
        final SpreadsheetLabelMapping mapping1 = label1.setLabelMappingReference(A1);
        store.save(mapping1);

        final SpreadsheetLabelName label2 = SpreadsheetLabelName.labelName("Label1234");
        final SpreadsheetLabelMapping mapping2 = label2.setLabelMappingReference(A1);
        store.save(mapping2);

        final SpreadsheetLabelName label3 = SpreadsheetLabelName.labelName("Label999");
        store.save(SpreadsheetLabelMapping.with(label3, A1));

        this.findLabelsByNameAndCheck(
            store,
            "2",
            0, // offset
            2, // count
            mapping1,
            mapping2
        );
    }

    @Test
    public void testFindLabelsByNameWithOffset() {
        final TreeMapSpreadsheetLabelStore store = this.createStore();
        
        final SpreadsheetLabelName label1 = SpreadsheetLabelName.labelName("Label123");
        final SpreadsheetLabelMapping mapping1 = label1.setLabelMappingReference(A1);
        store.save(mapping1);

        final SpreadsheetLabelName label2 = SpreadsheetLabelName.labelName("Label1234");
        final SpreadsheetLabelMapping mapping2 = label2.setLabelMappingReference(A1);
        store.save(mapping2);

        final SpreadsheetLabelName label3 = SpreadsheetLabelName.labelName("Label999");
        store.save(SpreadsheetLabelMapping.with(label3, A1));

        this.findLabelsByNameAndCheck(
            store,
            "2",
            1, // offset
            2, // count
            // skipped by offset mapping1,
            mapping2
        );
    }

    // loadCellOrCellRanges.............................................................................................

    @Test
    public void testLoadCellOrCellRangesWithUnknownLabelNotFound() {
        final TreeMapSpreadsheetLabelStore store = this.createStore();
        store.save(
            SpreadsheetLabelMapping.with(
                LABEL1,
                A1
            )
        );

        this.loadCellOrCellRangesAndCheck(
            store,
            SpreadsheetSelection.labelName("unknown"),
            Sets.empty());
    }

    @Test
    public void testLoadCellOrCellRangesWithLabelToCell() {
        final TreeMapSpreadsheetLabelStore store = this.createStore();
        store.save(
            SpreadsheetLabelMapping.with(
                LABEL1,
                A1
            )
        );
        store.save(
            SpreadsheetLabelMapping.with(
                LABEL2,
                A2
            )
        );

        this.loadCellOrCellRangesAndCheck(
            store,
            LABEL1,
            Sets.of(A1)
        );
    }

    @Test
    public void testLoadCellOrCellRangesWithLabelToLabelToCell() {
        final TreeMapSpreadsheetLabelStore store = this.createStore();
        store.save(
            SpreadsheetLabelMapping.with(
                LABEL1,
                A1
            )
        );
        store.save(
            SpreadsheetLabelMapping.with(
                LABEL2,
                LABEL1
            )
        );

        this.loadCellOrCellRangesAndCheck(
            store,
            LABEL2,
            Sets.of(A1)
        );
    }

    @Test
    public void testLoadCellOrCellRangesWithLabelToLabelToLabelToCell() {
        final TreeMapSpreadsheetLabelStore store = this.createStore();
        store.save(
            SpreadsheetLabelMapping.with(
                LABEL1,
                A1
            )
        );
        store.save(
            SpreadsheetLabelMapping.with(
                LABEL2,
                LABEL1
            )
        );
        store.save(
            SpreadsheetLabelMapping.with(
                LABEL3,
                LABEL2
            )
        );

        this.loadCellOrCellRangesAndCheck(
            store,
            LABEL2,
            Sets.of(A1)
        );
    }

    @Test
    public void testLoadCellOrCellRangesWithLabelToCellRange() {
        final TreeMapSpreadsheetLabelStore store = this.createStore();
        store.save(
            SpreadsheetLabelMapping.with(
                LABEL1,
                A1A3
            )
        );
        store.save(
            SpreadsheetLabelMapping.with(
                LABEL2,
                A2
            )
        );

        this.loadCellOrCellRangesAndCheck(
            store,
            LABEL1,
            Sets.of(A1A3)
        );
    }

    @Override
    public TreeMapSpreadsheetLabelStore createStore() {
        return TreeMapSpreadsheetLabelStore.create();
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEquals2() {
        final TreeMapSpreadsheetLabelStore store1 = this.createStore();
        final TreeMapSpreadsheetLabelStore store2 = this.createStore();

        final SpreadsheetLabelMapping mapping = SpreadsheetLabelMapping.with(
            LABEL1,
            A1
        );

        store1.save(mapping);
        store2.save(mapping);

        this.checkEquals(
            store1,
            store2
        );
    }

    @Test
    public void testEqualsDifferent() {
        final TreeMapSpreadsheetLabelStore different = this.createStore();

        different.save(
            SpreadsheetLabelMapping.with(
                LABEL1,
                A1
            )
        );

        this.checkNotEquals(different);
    }

    @Override
    public TreeMapSpreadsheetLabelStore createObject() {
        return this.createStore();
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        final TreeMapSpreadsheetLabelStore store = this.createStore();
        store.save(
            SpreadsheetLabelMapping.with(
                LABEL1,
                A1A3
            )
        );
        store.save(
            SpreadsheetLabelMapping.with(
                LABEL2,
                A2
            )
        );

        this.toStringAndCheck(
            store,
            "[label1=A1:A3, label2=A2]"
        );
    }

    // class............................................................................................................

    @Override
    public Class<TreeMapSpreadsheetLabelStore> type() {
        return TreeMapSpreadsheetLabelStore.class;
    }

    @Override
    public String typeNamePrefix() {
        return TreeMap.class.getSimpleName();
    }
}

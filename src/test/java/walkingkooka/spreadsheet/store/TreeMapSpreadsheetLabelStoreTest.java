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
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class TreeMapSpreadsheetLabelStoreTest extends SpreadsheetLabelStoreTestCase<TreeMapSpreadsheetLabelStore> {

    // save.............................................................................................................

    @Test
    public void testSaveCycleFails() {
        final TreeMapSpreadsheetLabelStore store = this.createStore();

        final SpreadsheetLabelName label1 = SpreadsheetSelection.labelName("Label111");
        final SpreadsheetLabelName label2 = SpreadsheetSelection.labelName("Label222");

        final SpreadsheetLabelMapping mapping1 = label1.setLabelMappingReference(label2);
        final SpreadsheetLabelMapping mapping2 = label2.setLabelMappingReference(label1);

        store.save(mapping1);

        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> store.save(mapping2)
        );

        this.checkEquals(
            "Cycle detected for \"Label222\" -> \"Label111\" -> \"Label222\"",
            thrown.getMessage()
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

        this.checkEquals(
            "Cycle detected for \"Label333\" -> \"Label111\" -> \"Label222\" -> \"Label333\"",
            thrown.getMessage()
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

        final SpreadsheetLabelName label1 = this.label1();
        final SpreadsheetLabelName label2 = this.label2();
        final SpreadsheetLabelName label3 = this.label3();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference a2 = this.a2();
        final SpreadsheetCellReference a3 = SpreadsheetSelection.parseCell("A3");

        store.save(label1.setLabelMappingReference(a1));
        store.save(label2.setLabelMappingReference(a2));
        store.save(label3.setLabelMappingReference(a3));

        this.betweenAndCheck(
            store,
            label1,
            label3,
            label1.setLabelMappingReference(a1),
            label2.setLabelMappingReference(a2),
            label3.setLabelMappingReference(a3)
        );
    }

    @Test
    public void testBetweenOne() {
        final TreeMapSpreadsheetLabelStore store = this.createStore();

        final SpreadsheetLabelName label1 = this.label1();
        final SpreadsheetLabelName label2 = this.label2();
        final SpreadsheetLabelName label3 = this.label3();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference a2 = this.a2();
        final SpreadsheetCellReference a3 = SpreadsheetSelection.parseCell("A3");

        store.save(label1.setLabelMappingReference(a1));
        store.save(label2.setLabelMappingReference(a2));
        store.save(label3.setLabelMappingReference(a3));

        this.betweenAndCheck(
            store,
            label2,
            label2,
            label2.setLabelMappingReference(a2)
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

        final SpreadsheetLabelName label1 = this.label1();
        final SpreadsheetLabelName label2 = this.label2();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference a2 = this.a2();

        store.save(label1.setLabelMappingReference(a1));
        store.save(label2.setLabelMappingReference(a2));

        this.findLabelsWithReferenceAndCheck(
            store,
            reference,
            0, // offset
            1 // count
        );
    }

    @Test
    public void testFindLabelsWithReferenceWhereSomeWithCell() {
        this.findLabelsWithReferenceWhereSomeAndCheck(
            this.a2()
        );
    }

    @Test
    public void testFindLabelsWithReferenceWhereSomeWithCellRange() {
        this.findLabelsWithReferenceWhereSomeAndCheck(
            SpreadsheetSelection.parseCellRange("a2:a3")
        );
    }

    private void findLabelsWithReferenceWhereSomeAndCheck(final SpreadsheetExpressionReference reference) {
        final TreeMapSpreadsheetLabelStore store = this.createStore();

        final SpreadsheetLabelName label1 = this.label1();
        final SpreadsheetLabelName label2 = this.label2();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference a2 = this.a2();

        store.save(label1.setLabelMappingReference(a1));
        final SpreadsheetLabelMapping mapping = store.save(label2.setLabelMappingReference(a2));

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
        this.findLabelsWithReferenceWhereSomeAndLabelsToLabelsCheck(
            this.a2()
        );
    }

    @Test
    public void testFindLabelsWithReferenceWhereSomeWithCellRangeAndLabelToLabels() {
        this.findLabelsWithReferenceWhereSomeAndLabelsToLabelsCheck(
            SpreadsheetSelection.parseCellRange("a2:a3")
        );
    }

    private void findLabelsWithReferenceWhereSomeAndLabelsToLabelsCheck(final SpreadsheetExpressionReference reference) {
        final TreeMapSpreadsheetLabelStore store = this.createStore();

        final SpreadsheetLabelName label1 = this.label1();
        final SpreadsheetLabelName label2 = this.label2();
        final SpreadsheetLabelName label3 = this.label3();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference a2 = this.a2();

        store.save(
            label1.setLabelMappingReference(a1)
        );
        final SpreadsheetLabelMapping mapping = store.save(
            label2.setLabelMappingReference(a2)
        );
        final SpreadsheetLabelMapping mapping2 = store.save(
            label3.setLabelMappingReference(label2)
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

        final SpreadsheetLabelName label1 = this.label1();
        final SpreadsheetLabelName label2 = this.label2();
        final SpreadsheetLabelName label3 = this.label3();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellReference a2 = this.a2();

        store.save(
            label1.setLabelMappingReference(a1)
        );
        final SpreadsheetLabelMapping mapping = store.save(
            label2.setLabelMappingReference(a2)
        );
        final SpreadsheetLabelMapping mapping2 = store.save(
            label3.setLabelMappingReference(label2)
        );

        // label3 -> label2 -> a2
        this.findLabelsWithReferenceAndCheck(
            store,
            label3,
            0, // offset
            2, // count
            mapping,
            mapping2
        );
    }

    @Test
    public void testFindLabelsWithReferenceWhereSomeWithCell2() {
        this.findLabelsWithReferenceWhereSomeAndCheck(
            this.a2()
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

        final SpreadsheetLabelName label1 = this.label1();
        final SpreadsheetLabelName label2 = this.label2();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellRangeReference b2c3 = SpreadsheetSelection.parseCellRange("b2:c3");

        store.save(label1.setLabelMappingReference(a1));
        final SpreadsheetLabelMapping mapping = store.save(label2.setLabelMappingReference(b2c3));

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
        this.findLabelsWithReferenceWhereSomeAndOffsetAndCheck(
            this.b2()
        );
    }

    @Test
    public void testFindLabelsWithReferenceWhereSomeWithCellRangeAndOffset() {
        this.findLabelsWithReferenceWhereSomeAndOffsetAndCheck(
            SpreadsheetSelection.parseCellRange("b2:b3")
        );
    }

    private void findLabelsWithReferenceWhereSomeAndOffsetAndCheck(final SpreadsheetExpressionReference reference) {
        final TreeMapSpreadsheetLabelStore store = this.createStore();

        final SpreadsheetLabelName label1 = this.label1();
        final SpreadsheetLabelName label2 = this.label2();
        final SpreadsheetLabelName label3 = this.label3();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellRangeReference b2c3 = SpreadsheetSelection.parseCellRange("b2:c3");
        final SpreadsheetCellRangeReference b2d4 = SpreadsheetSelection.parseCellRange("b2:d4");

        store.save(
            label1.setLabelMappingReference(a1)
        );
        final SpreadsheetLabelMapping mapping1 = store.save(
            label2.setLabelMappingReference(b2c3)
        );
        final SpreadsheetLabelMapping mapping2 = store.save(
            label3.setLabelMappingReference(b2d4)
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
        this.findLabelsWithReferenceWhereSomeAndCountAndCheck(
            this.b2()
        );
    }

    @Test
    public void testFindLabelsWithReferenceWhereSomeWithCellRangeAndCount() {
        this.findLabelsWithReferenceWhereSomeAndCountAndCheck(
            SpreadsheetSelection.parseCellRange("b2:b3")
        );
    }

    private void findLabelsWithReferenceWhereSomeAndCountAndCheck(final SpreadsheetExpressionReference reference) {
        final TreeMapSpreadsheetLabelStore store = this.createStore();

        final SpreadsheetLabelName label1 = this.label1();
        final SpreadsheetLabelName label2 = this.label2();
        final SpreadsheetLabelName label3 = this.label3();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellRangeReference b2c3 = SpreadsheetSelection.parseCellRange("b2:c3");
        final SpreadsheetCellRangeReference b2d4 = SpreadsheetSelection.parseCellRange("b2:d4");

        store.save(
            label1.setLabelMappingReference(a1)
        );
        final SpreadsheetLabelMapping mapping1 = store.save(
            label2.setLabelMappingReference(b2c3)
        );
        final SpreadsheetLabelMapping mapping2 = store.save(
            label3.setLabelMappingReference(b2d4)
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
        this.findLabelsWithReferenceWhereSomeAndOffsetAndCountAndCheck(
            this.b2()
        );
    }

    @Test
    public void testFindLabelsWithReferenceWhereSomeWithCellRangeAndOffsetAndCount() {
        this.findLabelsWithReferenceWhereSomeAndOffsetAndCountAndCheck(
            SpreadsheetSelection.parseCellRange("b2:b3")
        );
    }

    private void findLabelsWithReferenceWhereSomeAndOffsetAndCountAndCheck(final SpreadsheetExpressionReference reference) {
        final TreeMapSpreadsheetLabelStore store = this.createStore();

        final SpreadsheetLabelName label1 = this.label1();
        final SpreadsheetLabelName label2 = this.label2();
        final SpreadsheetLabelName label3 = this.label3();
        final SpreadsheetLabelName label4 = this.label4();
        final SpreadsheetLabelName label5 = this.label5();

        final SpreadsheetCellReference a1 = this.a1();
        final SpreadsheetCellRangeReference b2c3 = SpreadsheetSelection.parseCellRange("b2:c3");
        final SpreadsheetCellRangeReference b2d4 = SpreadsheetSelection.parseCellRange("b2:d4");
        final SpreadsheetCellRangeReference b2e5 = SpreadsheetSelection.parseCellRange("b2:e5");
        final SpreadsheetCellRangeReference b2f6 = SpreadsheetSelection.parseCellRange("b2:f6");

        // not matched by reference
        store.save(
            label1.setLabelMappingReference(a1)
        );

        // offset=0
        final SpreadsheetLabelMapping mapping1 = store.save(
            label2.setLabelMappingReference(b2c3)
        );

        // offset=1
        final SpreadsheetLabelMapping mapping2 = store.save(
            label3.setLabelMappingReference(b2d4)
        );

        // offset=2
        final SpreadsheetLabelMapping mapping3 = store.save(
            label4.setLabelMappingReference(b2e5)
        );

        // offset=3
        final SpreadsheetLabelMapping mapping4 = store.save(
            label5.setLabelMappingReference(b2f6)
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
                this.label1(),
                this.a1()
            )
        );

        this.findLabelsByNameAndCheck(
            store,
            this.label2()
                .value(),
            0,
            2
        );
    }

    @Test
    public void testFindLabelsByNameOnly() {
        final TreeMapSpreadsheetLabelStore store = this.createStore();

        final SpreadsheetLabelName label = this.label1();
        final SpreadsheetCellReference cell = this.a1();
        final SpreadsheetLabelMapping mapping = label.setLabelMappingReference(cell);

        store.save(mapping);

        this.findLabelsByNameAndCheck(
            store,
            label.value(),
            0,
            1,
            mapping
        );
    }

    @Test
    public void testFindLabelsByNameOnly2() {
        final TreeMapSpreadsheetLabelStore store = this.createStore();

        final SpreadsheetLabelName label = this.label1();
        final SpreadsheetCellReference cell = this.a1();
        final SpreadsheetLabelMapping mapping = label.setLabelMappingReference(cell);

        store.save(mapping);

        this.findLabelsByNameAndCheck(
            store,
            label.value(),
            0, // offset
            2, // count
            mapping
        );
    }

    @Test
    public void testFindLabelsByNameOnly3() {
        final TreeMapSpreadsheetLabelStore store = this.createStore();

        final SpreadsheetLabelName label = this.label1();
        final SpreadsheetCellReference cell = this.a1();
        final SpreadsheetLabelMapping mapping = label.setLabelMappingReference(cell);

        store.save(mapping);
        store.save(SpreadsheetLabelMapping.with(SpreadsheetLabelName.labelName("xyz"), this.a1()));

        this.findLabelsByNameAndCheck(
            store,
            label.value(),
            0, // offset
            2, // count
            mapping
        );
    }

    @Test
    public void testFindLabelsByNamePartial() {
        final TreeMapSpreadsheetLabelStore store = this.createStore();

        final SpreadsheetLabelName label = SpreadsheetLabelName.labelName("Label123");
        final SpreadsheetCellReference cell = this.a1();
        final SpreadsheetLabelMapping mapping = label.setLabelMappingReference(cell);

        store.save(mapping);

        this.findLabelsByNameAndCheck(
            store,
            "123",
            0, // offset
            2, // count
            mapping
        );
    }

    @Test
    public void testFindLabelsByNameSeveral() {
        final TreeMapSpreadsheetLabelStore store = this.createStore();

        final SpreadsheetCellReference cell = this.a1();

        final SpreadsheetLabelName label1 = SpreadsheetLabelName.labelName("Label123");
        final SpreadsheetLabelMapping mapping1 = label1.setLabelMappingReference(cell);
        store.save(mapping1);

        final SpreadsheetLabelName label2 = SpreadsheetLabelName.labelName("Label1234");
        final SpreadsheetLabelMapping mapping2 = label2.setLabelMappingReference(cell);
        store.save(mapping2);

        final SpreadsheetLabelName label3 = SpreadsheetLabelName.labelName("Label999");
        store.save(SpreadsheetLabelMapping.with(label3, cell));

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
    public void testFindLabelsByNameSeveral2() {
        final TreeMapSpreadsheetLabelStore store = this.createStore();

        final SpreadsheetCellReference cell = this.a1();

        final SpreadsheetLabelName label1 = SpreadsheetLabelName.labelName("Label123");
        final SpreadsheetLabelMapping mapping1 = label1.setLabelMappingReference(cell);
        store.save(mapping1);

        final SpreadsheetLabelName label2 = SpreadsheetLabelName.labelName("Label1234");
        final SpreadsheetLabelMapping mapping2 = label2.setLabelMappingReference(cell);
        store.save(mapping2);

        final SpreadsheetLabelName label3 = SpreadsheetLabelName.labelName("Label999");
        store.save(SpreadsheetLabelMapping.with(label3, cell));

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

        final SpreadsheetCellReference cell = this.a1();

        final SpreadsheetLabelName label1 = SpreadsheetLabelName.labelName("Label123");
        final SpreadsheetLabelMapping mapping1 = label1.setLabelMappingReference(cell);
        store.save(mapping1);

        final SpreadsheetLabelName label2 = SpreadsheetLabelName.labelName("Label1234");
        final SpreadsheetLabelMapping mapping2 = label2.setLabelMappingReference(cell);
        store.save(mapping2);

        final SpreadsheetLabelName label3 = SpreadsheetLabelName.labelName("Label999");
        store.save(SpreadsheetLabelMapping.with(label3, cell));

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
                this.label1(),
                this.a1()
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
                this.label1(),
                this.a1()
            )
        );
        store.save(
            SpreadsheetLabelMapping.with(
                this.label2(),
                this.a2()
            )
        );

        this.loadCellOrCellRangesAndCheck(
            store,
            this.label1(),
            Sets.of(this.a1())
        );
    }

    @Test
    public void testLoadCellOrCellRangesWithLabelToLabelToCell() {
        final TreeMapSpreadsheetLabelStore store = this.createStore();
        store.save(
            SpreadsheetLabelMapping.with(
                this.label1(),
                this.a1()
            )
        );
        store.save(
            SpreadsheetLabelMapping.with(
                this.label2(),
                this.label1()
            )
        );

        this.loadCellOrCellRangesAndCheck(
            store,
            this.label2(),
            Sets.of(this.a1())
        );
    }

    @Test
    public void testLoadCellOrCellRangesWithLabelToLabelToLabelToCell() {
        final TreeMapSpreadsheetLabelStore store = this.createStore();
        store.save(
            SpreadsheetLabelMapping.with(
                this.label1(),
                this.a1()
            )
        );
        store.save(
            SpreadsheetLabelMapping.with(
                this.label2(),
                this.label1()
            )
        );
        store.save(
            SpreadsheetLabelMapping.with(
                this.label3(),
                this.label2()
            )
        );

        this.loadCellOrCellRangesAndCheck(
            store,
            this.label2(),
            Sets.of(this.a1())
        );
    }

    @Test
    public void testLoadCellOrCellRangesWithLabelToCellRange() {
        final TreeMapSpreadsheetLabelStore store = this.createStore();
        store.save(
            SpreadsheetLabelMapping.with(
                this.label1(),
                this.range1()
            )
        );
        store.save(
            SpreadsheetLabelMapping.with(
                this.label2(),
                this.a2()
            )
        );

        this.loadCellOrCellRangesAndCheck(
            store,
            this.label1(),
            Sets.of(this.range1())
        );
    }

    private SpreadsheetLabelName label1() {
        return SpreadsheetSelection.labelName("label1");
    }

    private SpreadsheetLabelName label2() {
        return SpreadsheetSelection.labelName("label2");
    }

    private SpreadsheetLabelName label3() {
        return SpreadsheetSelection.labelName("label3");
    }

    private SpreadsheetLabelName label4() {
        return SpreadsheetSelection.labelName("label4");
    }

    private SpreadsheetLabelName label5() {
        return SpreadsheetSelection.labelName("label5");
    }

    private SpreadsheetCellReference a1() {
        return SpreadsheetSelection.A1;
    }

    private SpreadsheetCellReference a2() {
        return SpreadsheetSelection.parseCell("A2");
    }

    private SpreadsheetCellReference b2() {
        return SpreadsheetSelection.parseCell("B2");
    }

    private SpreadsheetCellRangeReference range1() {
        return SpreadsheetSelection.parseCellRange("A1:A3");
    }

    @Override
    public TreeMapSpreadsheetLabelStore createStore() {
        return TreeMapSpreadsheetLabelStore.create();
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        final TreeMapSpreadsheetLabelStore store = this.createStore();
        store.save(
            SpreadsheetLabelMapping.with(
                this.label1(),
                this.range1()
            )
        );
        store.save(
            SpreadsheetLabelMapping.with(
                this.label2(),
                this.a2()
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

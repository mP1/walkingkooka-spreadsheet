
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
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.Set;
import java.util.TreeMap;

public final class TreeMapSpreadsheetLabelReferencesStoreTest implements SpreadsheetLabelReferencesStoreTesting<TreeMapSpreadsheetLabelReferencesStore> {

    // findCellsWithCellOrCellRange.....................................................................................

    @Test
    public void testFindLabelsWithCellOrCellRangeWithZeroCount() {
        final TreeMapSpreadsheetLabelReferencesStore store = TreeMapSpreadsheetLabelReferencesStore.empty();

        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("Label123");
        final SpreadsheetCellReference reference = SpreadsheetSelection.parseCell("B2");

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                label,
                reference
            )
        );

        this.findLabelsWithCellOrCellRangeAndCheck(
            store,
            reference,
            0,
            0
        );
    }

    @Test
    public void testFindLabelsWithCellOrCellRangeWhereCellWithCellReference() {
        final TreeMapSpreadsheetLabelReferencesStore store = TreeMapSpreadsheetLabelReferencesStore.empty();

        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("Label123");
        final SpreadsheetCellReference reference = SpreadsheetSelection.parseCell("B2");

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                label,
                reference
            )
        );

        this.findCellsWithReferenceAndCheck(
            store,
            label,
            0,
            2,
            reference
        );

        this.findLabelsWithCellOrCellRangeAndCheck(
            store,
            reference,
            0,
            2,
            label
        );
    }

    @Test
    public void testFindLabelsWithCellOrCellRangeWhereLabelsWithCellReferences() {
        final TreeMapSpreadsheetLabelReferencesStore store = TreeMapSpreadsheetLabelReferencesStore.empty();

        final SpreadsheetLabelName label1 = SpreadsheetSelection.labelName("Label111");
        final SpreadsheetCellReference reference = SpreadsheetSelection.parseCell("B2");

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                label1,
                reference
            )
        );

        final SpreadsheetLabelName label2 = SpreadsheetSelection.labelName("Label222");

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                label2,
                reference
            )
        );

        // ignored
        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                label1,
                reference
            )
        );

        this.findLabelsWithCellOrCellRangeAndCheck(
            store,
            reference,
            0,
            3,
            label1,
            label2
        );
    }

    @Test
    public void testFindLabelsWithCellOrCellRangeWhereLabelsWithReferencesWithOffset() {
        final TreeMapSpreadsheetLabelReferencesStore store = TreeMapSpreadsheetLabelReferencesStore.empty();

        final SpreadsheetLabelName label1 = SpreadsheetSelection.labelName("Label111");
        final SpreadsheetCellReference reference = SpreadsheetSelection.parseCell("B2");

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                label1,
                reference
            )
        );

        final SpreadsheetLabelName label2 = SpreadsheetSelection.labelName("Label222");

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                label2,
                reference
            )
        );

        // ignored
        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                label1,
                reference
            )
        );

        this.findLabelsWithCellOrCellRangeAndCheck(
            store,
            reference,
            1,
            3,
            //label1, skipped because offset=1
            label2
        );
    }

    @Test
    public void testFindLabelsWithCellOrCellRangeWhereLabelsWithReferencesWithCount() {
        final TreeMapSpreadsheetLabelReferencesStore store = TreeMapSpreadsheetLabelReferencesStore.empty();

        final SpreadsheetLabelName label1 = SpreadsheetSelection.labelName("Label111");
        final SpreadsheetCellReference reference = SpreadsheetSelection.parseCell("B2");

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                label1,
                reference
            )
        );

        final SpreadsheetLabelName label2 = SpreadsheetSelection.labelName("Label222");

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                label2,
                reference
            )
        );

        // ignored
        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                label1,
                reference
            )
        );

        this.findLabelsWithCellOrCellRangeAndCheck(
            store,
            reference,
            0,
            1,
            label1
            //label2 not included because of count=1
        );
    }

    @Test
    public void testFindLabelsWithCellOrCellRangeWhereCellWithCellRangeReference() {
        final TreeMapSpreadsheetLabelReferencesStore store = TreeMapSpreadsheetLabelReferencesStore.empty();

        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("Label123");
        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("B2");

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                label,
                b2
            )
        );

        final SpreadsheetCellReference c3 = SpreadsheetSelection.parseCell("C3");

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                label,
                c3
            )
        );

        this.findCellsWithReferenceAndCheck(
            store,
            label,
            0,
            3,
            b2,
            c3
        );

        this.findLabelsWithCellOrCellRangeAndCheck(
            store,
            SpreadsheetSelection.parseCellRange("B2:C3"),
            0,
            2,
            label
        );
    }

    @Test
    public void testFindLabelsWithCellOrCellRangeWhereCellWithCellRangeReferences() {
        final TreeMapSpreadsheetLabelReferencesStore store = TreeMapSpreadsheetLabelReferencesStore.empty();

        final SpreadsheetLabelName label1 = SpreadsheetSelection.labelName("Label111");
        final SpreadsheetLabelName label2 = SpreadsheetSelection.labelName("Label222");

        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("B2");

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                label1,
                b2
            )
        );

        final SpreadsheetCellReference c3 = SpreadsheetSelection.parseCell("C3");

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                label1,
                c3
            )
        );

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                label2,
                c3
            )
        );

        final SpreadsheetCellReference d4 = SpreadsheetSelection.parseCell("d4");

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                label2,
                d4
            )
        );

        this.findCellsWithReferenceAndCheck(
            store,
            label1,
            0,
            3,
            b2,
            c3
        );

        this.findCellsWithReferenceAndCheck(
            store,
            label2,
            0,
            3,
            c3,
            d4
        );

        this.findLabelsWithCellOrCellRangeAndCheck(
            store,
            SpreadsheetSelection.parseCellRange("B2:C3"),
            0,
            3,
            label1,
            label2
        );
    }

    @Test
    public void testFindLabelsWithCellOrCellRangeWhereCellWithCellRangeReferences2() {
        final TreeMapSpreadsheetLabelReferencesStore store = TreeMapSpreadsheetLabelReferencesStore.empty();

        final SpreadsheetLabelName label1 = SpreadsheetSelection.labelName("Label111");
        final SpreadsheetLabelName label2 = SpreadsheetSelection.labelName("Label222");
        final SpreadsheetLabelName label3 = SpreadsheetSelection.labelName("Label333");

        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("B2");

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                label1,
                b2
            )
        );

        final SpreadsheetCellReference c3 = SpreadsheetSelection.parseCell("C3");

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                label1,
                c3
            )
        );

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                label2,
                c3
            )
        );

        final SpreadsheetCellReference d4 = SpreadsheetSelection.parseCell("d4");

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                label2,
                d4
            )
        );

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                label3,
                d4
            )
        );

        this.findCellsWithReferenceAndCheck(
            store,
            label1,
            0,
            3,
            b2,
            c3
        );

        this.findCellsWithReferenceAndCheck(
            store,
            label2,
            0,
            3,
            c3,
            d4
        );

        this.findLabelsWithCellOrCellRangeAndCheck(
            store,
            SpreadsheetSelection.parseCellRange("B2:C3"),
            0,
            3,
            label1,
            label2
        );
    }

    @Test
    public void testFindLabelsWithCellOrCellRangeWhereCellWithCellRangeReferencesAndOffsetAndCount() {
        final TreeMapSpreadsheetLabelReferencesStore store = TreeMapSpreadsheetLabelReferencesStore.empty();

        final SpreadsheetLabelName label1 = SpreadsheetSelection.labelName("Label111");
        final SpreadsheetLabelName label2 = SpreadsheetSelection.labelName("Label222");
        final SpreadsheetLabelName label3 = SpreadsheetSelection.labelName("Label333");

        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("B2");

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                label1,
                b2
            )
        );

        final SpreadsheetCellReference c3 = SpreadsheetSelection.parseCell("C3");

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                label1,
                c3
            )
        );

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                label2,
                c3
            )
        );

        final SpreadsheetCellReference d4 = SpreadsheetSelection.parseCell("d4");

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                label2,
                d4
            )
        );

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                label3,
                d4
            )
        );

        this.findCellsWithReferenceAndCheck(
            store,
            label1,
            0,
            3,
            b2,
            c3
        );

        this.findCellsWithReferenceAndCheck(
            store,
            label2,
            0,
            3,
            c3,
            d4
        );

        this.findLabelsWithCellOrCellRangeAndCheck(
            store,
            SpreadsheetSelection.parseCellRange("B2:e5"),
            1, // skips label111
            1, // stops before label333
            label2
        );
    }

    // SpreadsheetEngine................................................................................................

    @Test
    public void testAddValuesAndRemoveReferencesWithCellAndCount() {
        final TreeMapSpreadsheetLabelReferencesStore store = TreeMapSpreadsheetLabelReferencesStore.empty();

        final SpreadsheetLabelName label1 = SpreadsheetSelection.labelName("Label111");
        final SpreadsheetLabelName label2 = SpreadsheetSelection.labelName("Label222");

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("B2");
        final SpreadsheetCellReference c3 = SpreadsheetSelection.parseCell("C3");

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(label1, a1)
        );

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(label1, b2)
        );

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(label2, b2)
        );

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(label2, c3)
        );

        this.findCellsWithReferenceAndCheck(
            store,
            label1,
            0, // offset
            3, // count
            a1,
            b2
        );

        store.delete(label1);

        this.countAndCheck(
            store,
            2
        );

        // TODO removeReferencesWithLabel and count
    }

    // SpreadsheetLabelReferencesStoreTesting............................................................................

    @Override
    public TreeMapSpreadsheetLabelReferencesStore createStore() {
        return TreeMapSpreadsheetLabelReferencesStore.empty();
    }

    @Override
    public SpreadsheetLabelName id() {
        return SpreadsheetSelection.labelName("Label123");
    }

    @Override
    public Set<SpreadsheetCellReference> value() {
        return Set.of(
            SpreadsheetSelection.parseCell("B2"),
            SpreadsheetSelection.parseCell("C3"),
            SpreadsheetSelection.parseCell("D4")
        );
    }

    // class............................................................................................................

    @Override
    public String typeNamePrefix() {
        return TreeMap.class.getSimpleName();
    }

    @Override
    public Class<TreeMapSpreadsheetLabelReferencesStore> type() {
        return TreeMapSpreadsheetLabelReferencesStore.class;
    }
}

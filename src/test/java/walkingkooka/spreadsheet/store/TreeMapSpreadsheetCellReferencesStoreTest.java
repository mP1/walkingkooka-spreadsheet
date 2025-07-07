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
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.Set;
import java.util.TreeMap;

public final class TreeMapSpreadsheetCellReferencesStoreTest implements SpreadsheetCellReferencesStoreTesting<TreeMapSpreadsheetCellReferencesStore> {

    // findCellsWithCellOrCellRange.....................................................................................

    @Test
    public void testFindCellsWithCellOrCellRangeWithZeroCount() {
        final TreeMapSpreadsheetCellReferencesStore store = TreeMapSpreadsheetCellReferencesStore.empty();

        final SpreadsheetCellReference cell = SpreadsheetSelection.A1;
        final SpreadsheetCellReference reference = SpreadsheetSelection.parseCell("B2");

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                reference,
                cell
            )
        );

        this.findCellsWithCellOrCellRangeAndCheck(
            store,
            cell,
            0,
            0
        );
    }

    @Test
    public void testFindCellsWithCellOrCellRangeWhereCellWithReference() {
        final TreeMapSpreadsheetCellReferencesStore store = TreeMapSpreadsheetCellReferencesStore.empty();

        final SpreadsheetCellReference cell = SpreadsheetSelection.A1;
        final SpreadsheetCellReference reference = SpreadsheetSelection.parseCell("B2");

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                reference,
                cell
            )
        );

        this.findCellsWithReferenceAndCheck(
            store,
            reference,
            0,
            2,
            cell
        );

        this.findCellsWithCellOrCellRangeAndCheck(
            store,
            reference,
            0,
            2,
            cell
        );
    }

    @Test
    public void testFindCellsWithCellOrCellRangeWhereCellWithReferences() {
        final TreeMapSpreadsheetCellReferencesStore store = TreeMapSpreadsheetCellReferencesStore.empty();

        final SpreadsheetCellReference cell = SpreadsheetSelection.A1;
        final SpreadsheetCellReference reference = SpreadsheetSelection.parseCell("B2");

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                reference,
                cell
            )
        );

        final SpreadsheetCellReference cell2 = SpreadsheetSelection.parseCell("C3");

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                reference,
                cell2
            )
        );

        // ignored
        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                cell,
                reference
            )
        );

        this.findCellsWithCellOrCellRangeAndCheck(
            store,
            reference,
            0,
            3,
            cell,
            cell2
        );
    }

    @Test
    public void testFindCellsWithCellOrCellRangeWhereCellWithReferencesWithOffset() {
        final TreeMapSpreadsheetCellReferencesStore store = TreeMapSpreadsheetCellReferencesStore.empty();

        final SpreadsheetCellReference cell = SpreadsheetSelection.A1;
        final SpreadsheetCellReference reference = SpreadsheetSelection.parseCell("Z9");

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                reference,
                cell
            )
        );

        final SpreadsheetCellReference cell2 = SpreadsheetSelection.parseCell("B2");

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                reference,
                cell2
            )
        );

        // ignored
        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                cell,
                reference
            )
        );

        this.findCellsWithCellOrCellRangeAndCheck(
            store,
            reference,
            1,
            3,
            // cell1 skipped by offset=1
            cell2
        );
    }

    @Test
    public void testFindCellsWithCellOrCellRangeWhereCellWithReferencesWithCellRange() {
        final TreeMapSpreadsheetCellReferencesStore store = TreeMapSpreadsheetCellReferencesStore.empty();

        final SpreadsheetCellReference cell1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference reference1 = SpreadsheetSelection.parseCell("F6");

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                reference1,
                cell1
            )
        );

        final SpreadsheetCellReference cell2 = SpreadsheetSelection.parseCell("b2");
        final SpreadsheetCellReference reference2 = SpreadsheetSelection.parseCell("G7");

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                reference2,
                cell2
            )
        );

        // ignored
        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                cell1,
                reference1
            )
        );

        this.findCellsWithCellOrCellRangeAndCheck(
            store,
            SpreadsheetSelection.parseCellRange("F6:G7"),
            0,
            3,
            cell1,
            cell2
        );
    }

    @Test
    public void testFindCellsWithCellOrCellRangeWhereCellWithReferencesWithCellRange2() {
        final TreeMapSpreadsheetCellReferencesStore store = TreeMapSpreadsheetCellReferencesStore.empty();

        final SpreadsheetCellReference cell1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference reference1 = SpreadsheetSelection.parseCell("F6");

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                reference1,
                cell1
            )
        );

        final SpreadsheetCellReference cell2 = SpreadsheetSelection.parseCell("b2");
        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                reference1,
                cell2
            )
        );

        final SpreadsheetCellReference reference2 = SpreadsheetSelection.parseCell("G7");
        final SpreadsheetCellReference cell3 = SpreadsheetSelection.parseCell("c3");

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                reference2,
                cell3
            )
        );

        final SpreadsheetCellReference cell4 = SpreadsheetSelection.parseCell("d4");
        final SpreadsheetCellReference reference3 = SpreadsheetSelection.parseCell("H8");

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                reference3,
                cell4
            )
        );

        // ignored
        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                cell1,
                reference1
            )
        );

        this.findCellsWithCellOrCellRangeAndCheck(
            store,
            SpreadsheetSelection.parseCellRange("F6:I9"),
            0,
            5,
            cell1,
            cell2,
            cell3,
            cell4
        );
    }

    @Test
    public void testFindCellsWithCellOrCellRangeWhereCellWithReferencesWithCellRangeAndOffsetAndCount() {
        final TreeMapSpreadsheetCellReferencesStore store = TreeMapSpreadsheetCellReferencesStore.empty();

        final SpreadsheetCellReference cell1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference reference1 = SpreadsheetSelection.parseCell("F6");

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                reference1,
                cell1
            )
        );

        final SpreadsheetCellReference cell2 = SpreadsheetSelection.parseCell("b2");
        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                reference1,
                cell2
            )
        );

        final SpreadsheetCellReference reference2 = SpreadsheetSelection.parseCell("G7");
        final SpreadsheetCellReference cell3 = SpreadsheetSelection.parseCell("c3");

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                reference2,
                cell3
            )
        );

        final SpreadsheetCellReference cell4 = SpreadsheetSelection.parseCell("d4");
        final SpreadsheetCellReference reference3 = SpreadsheetSelection.parseCell("H8");

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                reference3,
                cell4
            )
        );

        // ignored
        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(
                cell1,
                reference1
            )
        );

        this.findCellsWithCellOrCellRangeAndCheck(
            store,
            SpreadsheetSelection.parseCellRange("F6:I9"),
            1,
            2,
            //cell1, skipped by offset 1
            cell2,
            cell3
            //cell4 less than count
        );
    }

    // SpreadsheetEngine................................................................................................

    @Test
    public void testAddValuesAndRemoveReferencesWithCellAndCount() {
        final TreeMapSpreadsheetCellReferencesStore store = TreeMapSpreadsheetCellReferencesStore.empty();

        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;

        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("B2");
        final SpreadsheetCellReference c3 = SpreadsheetSelection.parseCell("C3");

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(b2, a1)
        );

        store.addCell(
            ReferenceAndSpreadsheetCellReference.with(c3, a1)
        );

        this.findReferencesWithCellAndCheck(
            store,
            a1,
            0, // offset
            3, // count
            b2, c3
        );

        store.delete(a1);

        this.countAndCheck(
            store,
            2
        );

        store.removeReferencesWithCell(a1);

        this.findReferencesWithCellAndCheck(
            store,
            a1,
            0, // offset
            3 // count
        );

        this.countAndCheck(
            store,
            0
        );
    }

    // SpreadsheetCellReferencesStoreTesting............................................................................

    @Override
    public TreeMapSpreadsheetCellReferencesStore createStore() {
        return TreeMapSpreadsheetCellReferencesStore.empty();
    }

    @Override
    public SpreadsheetCellReference id() {
        return SpreadsheetSelection.A1;
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
    public Class<TreeMapSpreadsheetCellReferencesStore> type() {
        return TreeMapSpreadsheetCellReferencesStore.class;
    }
}

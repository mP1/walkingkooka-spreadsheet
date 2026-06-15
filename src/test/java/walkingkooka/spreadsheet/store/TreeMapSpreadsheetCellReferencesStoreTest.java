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

import java.util.TreeMap;

public final class TreeMapSpreadsheetCellReferencesStoreTest implements SpreadsheetCellReferencesStoreTesting<TreeMapSpreadsheetCellReferencesStore> {

    final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
    final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("B2");
    final SpreadsheetCellReference c3 = SpreadsheetSelection.parseCell("c3");
    final SpreadsheetCellReference d4 = SpreadsheetSelection.parseCell("d4");
    final SpreadsheetCellReference f6 = SpreadsheetSelection.parseCell("F6");
    final SpreadsheetCellReference g7 = SpreadsheetSelection.parseCell("G7");
    final SpreadsheetCellReference h8 = SpreadsheetSelection.parseCell("H8");
    final SpreadsheetCellReference z9 = SpreadsheetSelection.parseCell("Z9");

    // findCellsWithCellOrCellRange.....................................................................................

    @Test
    public void testFindCellsWithCellOrCellRangeWithZeroCount() {
        final TreeMapSpreadsheetCellReferencesStore store = TreeMapSpreadsheetCellReferencesStore.empty();

        store.addValue(
            b2,
            a1
        );

        this.findCellsWithCellOrCellRangeAndCheck(
            store,
            a1,
            0,
            0
        );
    }

    @Test
    public void testFindCellsWithCellOrCellRangeWhereCellWithReference() {
        final TreeMapSpreadsheetCellReferencesStore store = TreeMapSpreadsheetCellReferencesStore.empty();

        store.addValue(
            b2,
            a1
        );

        this.findValuesByIdAndCheck2(
            store,
            b2,
            0,
            2,
            a1
        );

        this.findCellsWithCellOrCellRangeAndCheck(
            store,
            b2,
            0,
            2,
            a1
        );
    }

    @Test
    public void testFindCellsWithCellOrCellRangeWhereCellWithReferences() {
        final TreeMapSpreadsheetCellReferencesStore store = TreeMapSpreadsheetCellReferencesStore.empty();

        store.addValue(
            b2,
            a1
        );

        store.addValue(
            b2,
            c3
        );

        // ignored
        store.addValue(
            a1,
            b2
        );

        this.findCellsWithCellOrCellRangeAndCheck(
            store,
            b2,
            0,
            3,
            a1,
            c3
        );
    }

    @Test
    public void testFindCellsWithCellOrCellRangeWhereCellWithReferencesWithOffset() {
        final TreeMapSpreadsheetCellReferencesStore store = TreeMapSpreadsheetCellReferencesStore.empty();

        store.addValue(
            z9,
            a1
        );

        store.addValue(
            z9,
            c3
        );

        // ignored
        store.addValue(
            a1,
            z9
        );

        this.findCellsWithCellOrCellRangeAndCheck(
            store,
            z9,
            1,
            3,
            // cell1 skipped by offset=1
            c3
        );
    }

    @Test
    public void testFindCellsWithCellOrCellRangeWhereCellWithReferencesWithCellRange() {
        final TreeMapSpreadsheetCellReferencesStore store = TreeMapSpreadsheetCellReferencesStore.empty();

        store.addValue(
            f6,
            a1
        );

        store.addValue(
            g7,
            c3
        );

        // ignored
        store.addValue(
            a1,
            f6
        );

        this.findCellsWithCellOrCellRangeAndCheck(
            store,
            SpreadsheetSelection.parseCellRange("F6:G7"),
            0,
            3,
            a1,
            c3
        );
    }

    @Test
    public void testFindCellsWithCellOrCellRangeWhereCellWithReferencesWithCellRange2() {
        final TreeMapSpreadsheetCellReferencesStore store = TreeMapSpreadsheetCellReferencesStore.empty();

        store.addValue(
            f6,
            a1
        );

        store.addValue(
            f6,
            c3
        );


        store.addValue(
            g7,
            c3
        );

        store.addValue(
            h8,
            d4
        );

        // ignored
        store.addValue(
            a1,
            f6
        );

        this.findCellsWithCellOrCellRangeAndCheck(
            store,
            SpreadsheetSelection.parseCellRange("F6:I9"),
            0,
            5,
            a1,
            c3,
            c3,
            d4
        );
    }

    @Test
    public void testFindCellsWithCellOrCellRangeWhereCellWithReferencesWithCellRangeAndOffsetAndCount() {
        final TreeMapSpreadsheetCellReferencesStore store = TreeMapSpreadsheetCellReferencesStore.empty();

        store.addValue(
            f6,
            a1
        );

        store.addValue(
            f6,
            b2
        );

        store.addValue(
            g7,
            c3
        );

        store.addValue(
            h8,
            d4
        );

        // ignored
        store.addValue(
            a1,
            f6
        );

        this.findCellsWithCellOrCellRangeAndCheck(
            store,
            SpreadsheetSelection.parseCellRange("F6:I9"),
            1,
            2,
            //cell1, skipped by offset 1
            b2,
            c3
            //cell4 less than count
        );
    }

    // SpreadsheetEngine................................................................................................

    @Test
    public void testAddValuesAndRemoveByValueAndFindIdsByValueAndCount() {
        final TreeMapSpreadsheetCellReferencesStore store = TreeMapSpreadsheetCellReferencesStore.empty();

        store.addValue(
            b2,
            a1
        );

        store.addValue(
            c3,
            a1
        );

        this.findIdsByValueAndCheck(
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

        store.removeByValue(a1);

        this.findIdsByValueAndCheck(
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
    public SpreadsheetCellReference value() {
        return SpreadsheetSelection.parseCell("B2");
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

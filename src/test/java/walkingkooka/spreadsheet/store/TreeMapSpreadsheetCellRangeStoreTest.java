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
import walkingkooka.Cast;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertTrue;

public final class TreeMapSpreadsheetCellRangeStoreTest implements SpreadsheetCellRangeStoreTesting<TreeMapSpreadsheetCellRangeStore>,
    HashCodeEqualsDefinedTesting2<TreeMapSpreadsheetCellRangeStore>,
    ClassTesting<TreeMapSpreadsheetCellRangeStore> {

    /**
     * RANGE1A and RANGE1B share a common TOPLEFT.
     */
    private final static SpreadsheetCellReference TOPLEFT1 = SpreadsheetCellRangeStoreTesting.cell(10, 20);
    private final static SpreadsheetCellReference CENTER1 = TOPLEFT1.add(1, 1);
    private final static SpreadsheetCellReference BOTTOMRIGHT1 = CENTER1.add(1, 1);
    private final static SpreadsheetCellRangeReference RANGE1A = TOPLEFT1.cellRange(BOTTOMRIGHT1);
    private final static SpreadsheetCellRangeReference RANGE1B = TOPLEFT1.cellRange(BOTTOMRIGHT1.add(1, 1));
    private final static SpreadsheetCellRangeReference RANGE1C = CENTER1.cellRange(BOTTOMRIGHT1);

    private final static SpreadsheetCellRangeReference RANGE1AABSOLUTE = TOPLEFT1.toAbsolute()
        .cellRange(BOTTOMRIGHT1.toAbsolute());

    private final static SpreadsheetCellReference TOPLEFT2 = SpreadsheetCellRangeStoreTesting.cell(30, 40);
    private final static SpreadsheetCellReference CENTER2 = TOPLEFT2.add(1, 1);
    private final static SpreadsheetCellReference BOTTOMRIGHT2 = CENTER2.add(2, 2);
    private final static SpreadsheetCellRangeReference RANGE2A = TOPLEFT2.cellRange(BOTTOMRIGHT2);

    private final static SpreadsheetCellRangeReference RANGE2B = CENTER1.cellRange(BOTTOMRIGHT2);

    private final static SpreadsheetCellReference TOPLEFT3 = SpreadsheetCellRangeStoreTesting.cell(50, 60);
    private final static SpreadsheetCellReference CENTER3 = TOPLEFT3.add(1, 1);
    private final static SpreadsheetCellReference BOTTOMRIGHT3 = CENTER3.add(2, 2);
    private final static SpreadsheetCellRangeReference RANGE3 = TOPLEFT3.cellRange(BOTTOMRIGHT3);

    private final static SpreadsheetCellRangeReference RANGE4 = SpreadsheetCellRangeStoreTesting.cell(70, 70)
        .cellRange(SpreadsheetCellRangeStoreTesting.cell(80, 80));

    private final static SpreadsheetCellReference VALUE1 = SpreadsheetSelection.parseCell("Z1");
    private final static SpreadsheetCellReference VALUE2 = SpreadsheetSelection.parseCell("Z2");
    private final static SpreadsheetCellReference VALUE2B = SpreadsheetSelection.parseCell("Z22");
    private final static SpreadsheetCellReference VALUE3 = SpreadsheetSelection.parseCell("Z3");
    private final static SpreadsheetCellReference VALUE4 = SpreadsheetSelection.parseCell("Z4");
    private final static SpreadsheetCellReference VALUE5 = SpreadsheetSelection.parseCell("Z5");
    private final static SpreadsheetCellReference VALUE6 = SpreadsheetSelection.parseCell("Z6");

    // save and load range .............................................................................................

    @Test
    public void testSaveAndLoadRange() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1A, VALUE1);

        this.countAndCheck(store, 1);

        this.findValuesByIdAndCheck(
            store,
            RANGE1A,
            VALUE1
        );
    }

    @Test
    public void testSaveAndLoadRangeAbsolute() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1AABSOLUTE, VALUE1);

        this.countAndCheck(store, 1);

        this.findValuesByIdAndCheck(
            store,
            RANGE1A,
            VALUE1
        );
    }

    @Test
    public void testSaveAndLoadRangeAbsolute2() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1A, VALUE1);

        this.countAndCheck(store, 1);

        this.findValuesByIdAndCheck(
            store,
            RANGE1AABSOLUTE,
            VALUE1
        );
    }

    @Test
    public void testSaveAndLoadRangeAbsolute3() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1AABSOLUTE, VALUE1);

        this.countAndCheck(store, 1);

        this.findValuesByIdAndCheck(
            store,
            RANGE1AABSOLUTE,
            VALUE1
        );
    }

    @Test
    public void testSaveAndLoadRangeSameValue() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1A, VALUE1);

        this.countAndCheck(store, 1);

        this.findValuesByIdAndCheck(
            store,
            RANGE1A,
            VALUE1
        );
    }

    @Test
    public void testSaveAndLoadRangeWithMultipleValues() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1A, VALUE2);

        this.countAndCheck(store, 2);

        this.findValuesByIdAndCheck(
            store,
            RANGE1A,
            VALUE1, VALUE2
        );
    }

    @Test
    public void testSaveAndLoadRangeWithMultipleValuesSameValue() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1A, VALUE2);
        store.addValue(RANGE1A, VALUE2);

        this.countAndCheck(store, 2);

        this.findValuesByIdAndCheck(
            store,
            RANGE1A,
            VALUE1, VALUE2
        );
    }

    @Test
    public void testSaveAndLoadMultipleRanges() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE2A, VALUE2);
        store.addValue(RANGE3, VALUE3);

        this.countAndCheck(store, 3);

        this.findValuesByIdAndCheck(
            store,
            RANGE1A,
            VALUE1
        );
        this.findValuesByIdAndCheck(
            store,
            RANGE2A,
            VALUE2
        );
        this.findValuesByIdAndCheck(
            store,
            RANGE3,
            VALUE3
        );
    }

    @Test
    public void testSaveAndLoadMultipleRangesMultipleValues() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1A, VALUE2);
        store.addValue(RANGE2A, VALUE3);
        store.addValue(RANGE2A, VALUE4);

        this.countAndCheck(store, 4);

        this.findValuesByIdAndCheck(
            store,
            RANGE1A,
            VALUE1, VALUE2
        );
        this.findValuesByIdAndCheck(
            store,
            RANGE2A,
            VALUE3, VALUE4
        );
    }

    @Test
    public void testSaveAndLoadOverlappingRanges() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1B, VALUE2);

        this.countAndCheck(store, 2);

        this.findValuesByIdAndCheck(
            store,
            RANGE1A,
            VALUE1
        );
        this.findValuesByIdAndCheck(
            store,
            RANGE1B,
            VALUE2
        );
    }

    @Test
    public void testSaveAndLoadOverlappingRanges2() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1C, VALUE2);

        this.countAndCheck(store, 2);

        this.findValuesByIdAndCheck(
            store,
            RANGE1A,
            VALUE1
        );
        this.findValuesByIdAndCheck(
            store,
            RANGE1C,
            VALUE2
        );
    }

    @Test
    public void testSaveAndLoadOverlappingRanges3() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1C, VALUE2);

        this.countAndCheck(store, 2);

        this.findValuesByIdAndCheck(
            store,
            RANGE1A,
            VALUE1
        );
        this.findValuesByIdAndCheck(
            store,
            RANGE1C,
            VALUE2
        );
    }

    @Test
    public void testSaveAndLoadOverlappingRanges4() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE2A, VALUE1);
        store.addValue(RANGE2B, VALUE2);

        this.countAndCheck(store, 2);

        this.findValuesByIdAndCheck(
            store,
            RANGE2A,
            VALUE1
        );
        this.findValuesByIdAndCheck(
            store,
            RANGE2B,
            VALUE2
        );
    }

    // ids..............................................................................................................

    @Test
    public void testIds() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1A, VALUE1); // 0
        store.addValue(RANGE1B, VALUE2); // 1
        store.addValue(RANGE1B, VALUE2B);
        store.addValue(RANGE2A, VALUE3); // 2
        store.addValue(RANGE3, VALUE4); // 3

        this.idsAndCheck(
            store,
            0,
            4,
            RANGE1A, RANGE1B, RANGE2A, RANGE3
        );
    }

    @Test
    public void testIdsAbsoluteRange() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1AABSOLUTE, VALUE1); // 0
        store.addValue(RANGE1B, VALUE2); // 1
        store.addValue(RANGE1B, VALUE2B);
        store.addValue(RANGE2A, VALUE3); // 2
        store.addValue(RANGE3, VALUE4); // 3

        this.idsAndCheck(
            store,
            0,
            4,
            RANGE1A,
            RANGE1B,
            RANGE2A,
            RANGE3
        );
    }

    @Test
    public void testIdsAbsoluteRange2() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1A, VALUE1); // 0
        store.addValue(RANGE1B, VALUE2); // 1
        store.addValue(RANGE1B, VALUE2B);
        store.addValue(RANGE2A, VALUE3); // 2
        store.addValue(RANGE3, VALUE4); // 3

        this.idsAndCheck(
            store,
            0,
            4,
            RANGE1A, RANGE1B, RANGE2A, RANGE3
        );
    }

    @Test
    public void testIdsWindow() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1A, VALUE1); // 0
        store.addValue(RANGE1B, VALUE2); // 1
        store.addValue(RANGE1B, VALUE2B);
        store.addValue(RANGE2A, VALUE3); // 2
        store.addValue(RANGE2B, VALUE4); // 3
        store.addValue(RANGE3, VALUE5); // 4
        store.addValue(RANGE4, VALUE6); // 5

        this.idsAndCheck(
            store,
            1,
            4,
            RANGE1B, RANGE2A, RANGE2B, RANGE3
        );
    }

    // values...........................................................................................................

    @Test
    public void testValues() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1B, VALUE2);
        store.addValue(RANGE1B, VALUE2B);
        store.addValue(RANGE2A, VALUE3);

        //noinspection unchecked
        this.valuesAndCheck(
            store,
            0,
            5,
            VALUE1,
            VALUE2,
            VALUE2B,
            VALUE3
        );
    }

    @Test
    public void testValuesAbsoluteRange() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1AABSOLUTE, VALUE1);
        store.addValue(RANGE1B, VALUE2);
        store.addValue(RANGE1B, VALUE2B);
        store.addValue(RANGE2A, VALUE3);

        //noinspection unchecked
        this.valuesAndCheck(
            store,
            0,
            3,
            VALUE1,
            VALUE2,
            VALUE2B
        );
    }

    @Test
    public void testValuesAbsoluteRange2() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1AABSOLUTE, VALUE1);
        store.addValue(RANGE1B, VALUE2);
        store.addValue(RANGE1B, VALUE2B);
        store.addValue(RANGE2A, VALUE3);

        //noinspection unchecked
        this.valuesAndCheck(
            store,
            0,
            3,
            VALUE1,
            VALUE2,
            VALUE2B
        );
    }

    @Test
    public void testValuesWithOffsetAndCount() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1A, VALUE1); // 0
        store.addValue(RANGE1B, VALUE2); // 1
        store.addValue(RANGE1B, VALUE2B);
        store.addValue(RANGE2A, VALUE3); // 3
        store.addValue(RANGE2B, VALUE4); // 2

        //noinspection unchecked
        this.valuesAndCheck(
            store,
            1,
            2,
            VALUE2,
            VALUE2B
        );
    }

    // findCellRangesIncludingCell......................................................................................

    @Test
    public void testFindCellRangesIncludingCellWithBeginRange() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1A, VALUE1);

        this.findCellRangesIncludingCellAndCheck(
            store,
            RANGE1A.begin(),
            RANGE1A
        );
    }

    @Test
    public void testFindCellRangesIncludingCellWithBeginRangeAbsolute() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1A, VALUE1);

        this.findCellRangesIncludingCellAndCheck(
            store,
            RANGE1A.begin()
                .toAbsolute(),
            RANGE1A
        );
    }

    @Test
    public void testFindCellRangesIncludingCellWithBeginRangeAbsolute2() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1AABSOLUTE, VALUE1);

        this.findCellRangesIncludingCellAndCheck(
            store,
            RANGE1A.begin(),
            RANGE1A
        );
    }

    @Test
    public void testFindCellRangesIncludingCellWithBeginRangeAbsolute3() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1AABSOLUTE, VALUE1);

        this.findCellRangesIncludingCellAndCheck(
            store,
            RANGE1A.begin()
                .toAbsolute(),
            RANGE1A
        );
    }

    @Test
    public void testFindCellRangesIncludingCellWithMidRange() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1A, VALUE1);

        final SpreadsheetCellReference mid = RANGE1A.begin().add(1, 1);
        assertTrue(RANGE1A.begin().compareTo(mid) < 0, "RANGE1A.begin < mid");
        assertTrue(mid.compareTo(RANGE1A.end()) < 0, "mid< RANGE1A.end");

        this.findCellRangesIncludingCellAndCheck(
            store,
            mid,
            RANGE1A
        );
    }

    @Test
    public void testFindCellRangesIncludingCellWithMidRangeAbsolute() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1AABSOLUTE, VALUE1);

        final SpreadsheetCellReference mid = RANGE1A.begin().add(1, 1);
        assertTrue(RANGE1A.begin().compareTo(mid) < 0, "RANGE1A.begin < mid");
        assertTrue(mid.compareTo(RANGE1A.end()) < 0, "mid< RANGE1A.end");

        this.findCellRangesIncludingCellAndCheck(store, mid, RANGE1A);
    }

    @Test
    public void testFindCellRangesIncludingCellWithMidRangeAbsolute2() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1AABSOLUTE, VALUE1);

        final SpreadsheetCellReference mid = RANGE1AABSOLUTE.begin().add(1, 1);
        assertTrue(RANGE1A.begin().compareTo(mid) < 0, "RANGE1A.begin < mid");
        assertTrue(mid.compareTo(RANGE1A.end()) < 0, "mid< RANGE1A.end");

        this.findCellRangesIncludingCellAndCheck(
            store,
            mid,
            RANGE1A
        );
    }

    @Test
    public void testFindCellRangesIncludingCellWithEndRange() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1A, VALUE1);

        this.findCellRangesIncludingCellAndCheck(
            store,
            RANGE1A.end(),
            RANGE1A
        );
    }

    @Test
    public void testFindCellRangesIncludingCellWithEndRangeAbsolute() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1AABSOLUTE, VALUE1);

        this.findCellRangesIncludingCellAndCheck(
            store,
            RANGE1A.end(),
            RANGE1A
        );
    }

    @Test
    public void testFindCellRangesIncludingCellWithEndRangeAbsolute2() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1AABSOLUTE, VALUE1);

        this.findCellRangesIncludingCellAndCheck(
            store,
            RANGE1AABSOLUTE.end(),
            RANGE1A
        );
    }

    @Test
    public void testFindCellRangesIncludingCellWithEndRangeAbsolute3() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1A, VALUE1);

        this.findCellRangesIncludingCellAndCheck(
            store,
            RANGE1AABSOLUTE.end(),
            RANGE1A
        );
    }

    @Test
    public void testFindCellRangesIncludingCellWithBeginRange2() {
        this.checkNotEquals(
            RANGE1A.begin(),
            RANGE1C.begin(),
            "RANGE1A.begin() != RANGE1C.begin()"
        );
        this.checkNotEquals(
            RANGE1A.begin(),
            RANGE2A.begin(),
            "RANGE1A.begin() != RANGE2A.begin()"
        );

        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1C, VALUE2);
        store.addValue(RANGE2A, VALUE3);

        this.findCellRangesIncludingCellAndCheck(
            store,
            RANGE1A.begin(),
            RANGE1A
        );
    }

    @Test
    public void testFindCellRangesIncludingCellWithBeginRange3() {
        this.checkNotEquals(
            RANGE1A.begin(),
            RANGE1C.begin(),
            "RANGE1A.begin() != RANGE1C.begin()"
        );
        this.checkNotEquals(
            RANGE1A.begin(),
            RANGE2A.begin(),
            "RANGE1A.begin() != RANGE2A.begin()"
        );

        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1C, VALUE2);
        store.addValue(RANGE2A, VALUE3);

        this.findCellRangesIncludingCellAndCheck(store, RANGE1C.begin(), RANGE1A, RANGE1C);
    }

    @Test
    public void testFindCellRangesIncludingCellWithBeginRange4() {
        this.checkNotEquals(
            RANGE1A.begin(),
            RANGE1C.begin(),
            "RANGE1A.begin() != RANGE1C.begin()"
        );
        this.checkNotEquals(
            RANGE1A.begin(),
            RANGE2A.begin(),
            "RANGE1A.begin() != RANGE2A.begin()"
        );

        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1C, VALUE2);
        store.addValue(RANGE2A, VALUE3);

        this.findCellRangesIncludingCellAndCheck(store, RANGE2A.begin(), RANGE2A);
    }

    @Test
    public void testFindCellRangesIncludingCellWithEndRange2() {
        this.checkNotEquals(RANGE1A.end(), RANGE1B.end(), "RANGE1A.end() != RANGE1B.end()");
        this.checkNotEquals(RANGE1A.end(), RANGE2A.end(), "RANGE1A.end() != RANGE1B.end()");

        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1B, VALUE2);
        store.addValue(RANGE2A, VALUE3);

        this.findCellRangesIncludingCellAndCheck(store, RANGE1A.end(), RANGE1A, RANGE1B);
    }

    @Test
    public void testFindCellRangesIncludingCellWithEndRange3() {
        this.checkNotEquals(RANGE1A.end(), RANGE1B.end(), "RANGE1A.end() != RANGE1B.end()");
        this.checkNotEquals(RANGE1A.end(), RANGE2A.end(), "RANGE1A.end() != RANGE1B.end()");

        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1B, VALUE2);
        store.addValue(RANGE2A, VALUE3);

        this.findCellRangesIncludingCellAndCheck(
            store,
            RANGE1B.end(),
            RANGE1B
        );
    }

    @Test
    public void testFindCellRangesIncludingCellWithEndRange4() {
        this.checkNotEquals(
            RANGE1A.end(),
            RANGE1B.end(),
            "RANGE1A.end() != RANGE1B.end()"
        );
        this.checkNotEquals(
            RANGE1A.end(),
            RANGE2A.end(),
            "RANGE1A.end() != RANGE1B.end()"
        );

        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1B, VALUE2);
        store.addValue(RANGE2A, VALUE3);

        this.findCellRangesIncludingCellAndCheck(
            store,
            RANGE2A.end(),
            RANGE2A
        );
    }

    @Test
    public void testFindCellRangesIncludingCellWithMidRange2() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE2A, VALUE2);
        store.addValue(RANGE3, VALUE3);

        final SpreadsheetCellReference mid = RANGE1A.begin().add(1, 1);
        assertTrue(RANGE1A.begin().compareTo(mid) < 0, "RANGE1A.begin < mid");
        assertTrue(mid.compareTo(RANGE1A.end()) < 0, "mid< RANGE1A.end");

        this.findCellRangesIncludingCellAndCheck(store, mid, RANGE1A);
    }

    // delete range.....................................................................

    @Test
    public void testDeleteRange() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1A, VALUE1);

        store.delete(RANGE1A);

        this.countAndCheck(store, 0);
    }

    @Test
    public void testDeleteRangeAbsolute() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1AABSOLUTE, VALUE1);

        store.delete(RANGE1AABSOLUTE);

        this.countAndCheck(store, 0);
    }

    @Test
    public void testDeleteRangeAbsolute2() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1A, VALUE1);

        store.delete(RANGE1AABSOLUTE);

        this.countAndCheck(store, 0);
    }

    @Test
    public void testDeleteRange2() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1A, VALUE2);

        store.delete(RANGE1A);

        this.countAndCheck(store, 0);

        this.findValuesByIdAndCheck(
            store,
            RANGE1A
        );
    }

    @Test
    public void testDelete3() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1A, VALUE2);
        store.addValue(RANGE2A, VALUE3);

        store.delete(RANGE1A);

        this.countAndCheck(store, 1);

        this.findValuesByIdAndCheck(
            store,
            RANGE1A
        );
        this.findValuesByIdAndCheck(
            store,
            RANGE2A,
            VALUE3
        );
    }

    @Test
    public void testDelete4() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1A, VALUE2);
        store.addValue(RANGE2A, VALUE3);
        store.addValue(RANGE3, VALUE4);

        store.delete(RANGE1A);

        this.countAndCheck(store, 2);

        store.delete(RANGE2A);

        this.countAndCheck(store, 1);

        this.findValuesByIdAndCheck(store, RANGE1A);
        this.findValuesByIdAndCheck(store, RANGE2A);
        this.findValuesByIdAndCheck(store, RANGE3, VALUE4);
    }

    @Test
    public void testDelete4AbsoluteRange() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1AABSOLUTE, VALUE2);
        store.addValue(RANGE2A, VALUE3);
        store.addValue(RANGE3, VALUE4);

        store.delete(RANGE1A);

        this.countAndCheck(store, 2);

        store.delete(RANGE2A);

        this.countAndCheck(store, 1);

        this.findValuesByIdAndCheck(
            store,
            RANGE1AABSOLUTE
        );
        this.findValuesByIdAndCheck(
            store,
            RANGE2A
        );
        this.findValuesByIdAndCheck(
            store,
            RANGE3,
            VALUE4
        );
    }

    @Test
    public void testDelete4AbsoluteRange2() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1AABSOLUTE, VALUE2);
        store.addValue(RANGE2A, VALUE3);
        store.addValue(RANGE3, VALUE4);

        store.delete(RANGE1A);

        this.countAndCheck(
            store,
            2
        );

        store.delete(RANGE2A);

        this.countAndCheck(
            store,
            1
        );

        this.findValuesByIdAndCheck(
            store,
            RANGE1A
        );
        this.findValuesByIdAndCheck(
            store,
            RANGE2A
        );
        this.findValuesByIdAndCheck(
            store,
            RANGE3,
            VALUE4
        );
    }

    // removeValue......................................................................................................

    @Test
    public void testRemoveValueUnknownIgnored() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.removeValue(RANGE1A, VALUE3);

        this.countAndCheck(store, 1);

        this.findValuesByIdAndCheck(
            store,
            RANGE1A,
            VALUE1
        );
    }

    @Test
    public void testRemoveValue() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.removeValue(RANGE1A, VALUE1);

        this.countAndCheck(
            store,
            0
        );
    }

    @Test
    public void testRemoveValue2() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE2A, VALUE2);
        store.removeValue(RANGE1A, VALUE1);

        this.countAndCheck(store, 1); // VALUE2

        this.findValuesByIdAndCheck(
            store,
            RANGE1A
        );
        this.findValuesByIdAndCheck(
            store,
            RANGE2A,
            VALUE2
        );
    }

    @Test
    public void testRemoveValueIgnored2() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE2A, VALUE2);
        store.removeValue(RANGE1A, VALUE2);

        this.countAndCheck(store, 2); // VALUE2

        this.findValuesByIdAndCheck(
            store,
            RANGE1A,
            VALUE1
        );
        this.findValuesByIdAndCheck(
            store,
            RANGE2A,
            VALUE2
        );
    }

    @Test
    public void testRemoveValueRangeMultipleValues() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1A, VALUE2);

        store.removeValue(RANGE1A, VALUE1);

        this.countAndCheck(store, 1); // VALUE2

        this.findValuesByIdAndCheck(
            store,
            RANGE1A,
            VALUE2
        );
    }

    @Test
    public void testRemoveValueRangeMultipleValues2() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1A, VALUE2);
        store.addValue(RANGE2A, VALUE3);

        store.removeValue(RANGE1A, VALUE1);

        this.countAndCheck(store, 2); // VALUE2, VALUE3

        this.findValuesByIdAndCheck(
            store,
            RANGE1A,
            VALUE2
        );
        this.findValuesByIdAndCheck(
            store,
            RANGE2A,
            VALUE3
        );
    }

    @Test
    public void testRemoveValueDifferentRanges() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE2A, VALUE2);
        store.addValue(RANGE3, VALUE3);

        store.removeValue(RANGE1A, VALUE1);

        this.countAndCheck(store, 2); // VALUE2, VALUE3

        this.findValuesByIdAndCheck(
            store,
            RANGE1A
        );
        this.findValuesByIdAndCheck(
            store,
            RANGE2A,
            VALUE2
        );
        this.findValuesByIdAndCheck(
            store,
            RANGE3,
            VALUE3
        );
    }

    // testFindIdsByValue...............................................................................................

    @Test
    public void testFindIdsByValueUnknownValue() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        final SpreadsheetCellRangeReference range1 = SpreadsheetSelection.parseCellRange("A1:A1");
        store.addValue(
            range1,
            VALUE1
        );

        this.findIdsByValueAndCheck(
            store,
            SpreadsheetSelection.parseCell("Z99")
        );
    }

    @Test
    public void testFindIdsByValue2() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        final SpreadsheetCellRangeReference range1 = SpreadsheetSelection.parseCellRange("A1:A1");
        store.addValue(
            range1,
            VALUE1
        );

        final SpreadsheetCellRangeReference range2 = SpreadsheetSelection.parseCellRange("A2:A2");
        store.addValue(
            range2,
            VALUE2
        );

        this.findIdsByValueAndCheck(
            store,
            VALUE1,
            range1
        );
        this.findIdsByValueAndCheck(
            store,
            VALUE2,
            range2
        );
    }

    @Test
    public void testFindIdsByValueAddValueRemoveValue() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        final SpreadsheetCellRangeReference range1 = SpreadsheetSelection.parseCellRange("A1:A1");
        store.addValue(
            range1,
            VALUE1
        );
        store.removeValue(
            range1,
            VALUE1
        );

        this.findIdsByValueAndCheck(
            store,
            VALUE1
        );
    }

    @Test
    public void testFindIdsByValueAddValueRemoveValueAddValue() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        final SpreadsheetCellRangeReference range1 = SpreadsheetSelection.parseCellRange("A1:A1");
        store.addValue(
            range1,
            VALUE1
        );
        store.removeValue(
            range1,
            VALUE1
        );

        this.findIdsByValueAndCheck(
            store,
            VALUE1
        );

        store.addValue(
            range1,
            VALUE1
        );
        this.findIdsByValueAndCheck(
            store,
            VALUE1,
            range1
        );
    }

    @Test
    public void testFindIdsByValueAddValueRemoveValue2() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        final SpreadsheetCellRangeReference range1 = SpreadsheetSelection.parseCellRange("A1:A1");
        @SuppressWarnings("unused") final SpreadsheetCellRangeReference range2 = SpreadsheetSelection.parseCellRange("A2:A2");

        store.addValue(
            range1,
            VALUE1
        );
        store.removeValue(
            range1,
            VALUE1
        );

        this.findIdsByValueAndCheck(
            store,
            VALUE1
        );
    }

    @Test
    public void testFindIdsByValueAddValueManyRanges() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        final SpreadsheetCellRangeReference range1 = SpreadsheetSelection.parseCellRange("A1:A1");
        store.addValue(
            range1,
            VALUE1
        );

        final SpreadsheetCellRangeReference range2 = SpreadsheetSelection.parseCellRange("A2:A2");
        store.addValue(
            range2,
            VALUE1
        );

        this.findIdsByValueAndCheck(
            store,
            VALUE1,
            range1,
            range2
        );
        this.findIdsByValueAndCheck(
            store,
            SpreadsheetSelection.parseCell("Z99")
        );
    }

    @Test
    public void testFindIdsByValueAddValueManyRanges2() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        final SpreadsheetCellRangeReference range1 = SpreadsheetSelection.parseCellRange("A1:A1");
        store.addValue(
            range1,
            VALUE1
        );

        final SpreadsheetCellRangeReference range2 = SpreadsheetSelection.parseCellRange("A2:A2");
        store.addValue(
            range2,
            VALUE1
        );

        final String value2 = "value2";
        store.addValue(
            range2,
            VALUE2
        );

        this.findIdsByValueAndCheck(
            store,
            VALUE1,
            range1,
            range2
        );
        this.findIdsByValueAndCheck(
            store,
            VALUE2,
            range2
        );
        this.findIdsByValueAndCheck(
            store,
            SpreadsheetSelection.parseCell("Z9")
        );
    }

    @Test
    public void testFindIdsByValueAddValueRemoveValueManyRanges() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        final SpreadsheetCellRangeReference range1 = SpreadsheetSelection.parseCellRange("A1:A1");
        store.addValue(
            range1,
            VALUE1
        );

        final SpreadsheetCellRangeReference range2 = SpreadsheetSelection.parseCellRange("A2:A2");
        store.addValue(
            range2,
            VALUE2
        );

        store.addValue(
            range1,
            VALUE3
        );

        this.findIdsByValueAndCheck(
            store,
            VALUE1,
            range1
        );
        this.findIdsByValueAndCheck(
            store,
            VALUE2,
            range2
        );
        this.findIdsByValueAndCheck(
            store,
            VALUE3,
            range1
        );
    }


    @Override
    public TreeMapSpreadsheetCellRangeStore createStore() {
        return TreeMapSpreadsheetCellRangeStore.create();
    }

    @Override
    public SpreadsheetCellReference value() {
        return VALUE1;
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEquals2() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();
        final TreeMapSpreadsheetCellRangeStore different = this.createStore();

        final SpreadsheetCellRangeReference range = SpreadsheetSelection.parseCellRange("A1:B2");

        store.addValue(
            range,
            VALUE1
        );

        different.addValue(
            range,
            VALUE1
        );

        this.checkEquals(
            store,
            different
        );
    }

    @Test
    public void testEqualsDifferent() {
        final TreeMapSpreadsheetCellRangeStore different = this.createStore();

        different.addValue(
            SpreadsheetSelection.parseCellRange("A1:B2"),
            VALUE2B
        );

        this.checkNotEquals(different);
    }

    @Override
    public TreeMapSpreadsheetCellRangeStore createObject() {
        return this.createStore();
    }

    // ToString.........................................................................................................

    @Test
    public void testToString() {
        final TreeMapSpreadsheetCellRangeStore store = this.createStore();

        final SpreadsheetCellRangeReference range1 = SpreadsheetSelection.parseCellRange("A1:A1");
        store.addValue(
            range1,
            VALUE1
        );

        final SpreadsheetCellRangeReference range2 = SpreadsheetSelection.parseCellRange("A2:A2");
        store.addValue(
            range2,
            VALUE2
        );

        this.toStringAndCheck(
            store,
            "{A1=[Z1], A2=[Z2]}"
        );
    }

    // class............................................................................................................

    @Override
    public Class<TreeMapSpreadsheetCellRangeStore> type() {
        return Cast.to(TreeMapSpreadsheetCellRangeStore.class);
    }

    @Override
    public String typeNamePrefix() {
        return TreeMap.class.getSimpleName();
    }
}

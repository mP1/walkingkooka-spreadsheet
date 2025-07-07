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
import walkingkooka.collect.list.Lists;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import static org.junit.jupiter.api.Assertions.assertTrue;

public final class TreeMapSpreadsheetCellRangeStoreTest extends TreeMapSpreadsheetCellRangeStoreTestCase<TreeMapSpreadsheetCellRangeStore<String>>
    implements SpreadsheetCellRangeStoreTesting<TreeMapSpreadsheetCellRangeStore<String>, String> {

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

    private final static String VALUE1 = "value1";
    private final static String VALUE2 = "value2";
    private final static String VALUE2B = "value2!!!!";
    private final static String VALUE3 = "value3";
    private final static String VALUE4 = "value4";
    private final static String VALUE5 = "value5";
    private final static String VALUE6 = "value6";

    @Override
    public void testAddSaveWatcherAndSaveTwiceFiresOnce() {
    }

    // save and load range ...................................................................................................

    @Test
    public void testSaveAndLoadRange() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);

        this.countAndCheck(store, 1);

        this.loadRangeAndCheck(store, RANGE1A, VALUE1);
    }

    @Test
    public void testSaveAndLoadRangeAbsolute() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1AABSOLUTE, VALUE1);

        this.countAndCheck(store, 1);

        this.loadRangeAndCheck(store, RANGE1A, VALUE1);
    }

    @Test
    public void testSaveAndLoadRangeAbsolute2() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);

        this.countAndCheck(store, 1);

        this.loadRangeAndCheck(store, RANGE1AABSOLUTE, VALUE1);
    }

    @Test
    public void testSaveAndLoadRangeAbsolute3() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1AABSOLUTE, VALUE1);

        this.countAndCheck(store, 1);

        this.loadRangeAndCheck(store, RANGE1AABSOLUTE, VALUE1);
    }

    @Test
    public void testSaveAndLoadRangeSameValue() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1A, VALUE1);

        this.countAndCheck(store, 1);

        this.loadRangeAndCheck(store, RANGE1A, VALUE1);
    }

    @Test
    public void testSaveAndLoadRangeWithMultipleValues() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1A, VALUE2);

        this.countAndCheck(store, 2);

        this.loadRangeAndCheck(store, RANGE1A, VALUE1, VALUE2);
    }

    @Test
    public void testSaveAndLoadRangeWithMultipleValuesSameValue() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1A, VALUE2);
        store.addValue(RANGE1A, VALUE2);

        this.countAndCheck(store, 2);

        this.loadRangeAndCheck(store, RANGE1A, VALUE1, VALUE2);
    }

    @Test
    public void testSaveAndLoadMultipleRanges() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE2A, VALUE2);
        store.addValue(RANGE3, VALUE3);

        this.countAndCheck(store, 3);

        this.loadRangeAndCheck(store, RANGE1A, VALUE1);
        this.loadRangeAndCheck(store, RANGE2A, VALUE2);
        this.loadRangeAndCheck(store, RANGE3, VALUE3);
    }

    @Test
    public void testSaveAndLoadMultipleRangesMultipleValues() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1A, VALUE2);
        store.addValue(RANGE2A, VALUE3);
        store.addValue(RANGE2A, VALUE4);

        this.countAndCheck(store, 4);

        this.loadRangeAndCheck(store, RANGE1A, VALUE1, VALUE2);
        this.loadRangeAndCheck(store, RANGE2A, VALUE3, VALUE4);
    }

    @Test
    public void testSaveAndLoadOverlappingRanges() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1B, VALUE2);

        this.countAndCheck(store, 2);

        this.loadRangeAndCheck(store, RANGE1A, VALUE1);
        this.loadRangeAndCheck(store, RANGE1B, VALUE2);
    }

    @Test
    public void testSaveAndLoadOverlappingRanges2() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1C, VALUE2);

        this.countAndCheck(store, 2);

        this.loadRangeAndCheck(store, RANGE1A, VALUE1);
        this.loadRangeAndCheck(store, RANGE1C, VALUE2);
    }

    @Test
    public void testSaveAndLoadOverlappingRanges3() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1C, VALUE2);

        this.countAndCheck(store, 2);

        this.loadRangeAndCheck(store, RANGE1A, VALUE1);
        this.loadRangeAndCheck(store, RANGE1C, VALUE2);
    }

    @Test
    public void testSaveAndLoadOverlappingRanges4() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE2A, VALUE1);
        store.addValue(RANGE2B, VALUE2);

        this.countAndCheck(store, 2);

        this.loadRangeAndCheck(store, RANGE2A, VALUE1);
        this.loadRangeAndCheck(store, RANGE2B, VALUE2);
    }

    // ids....................................................................................

    @Test
    public void testIds() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1); // 0
        store.addValue(RANGE1B, VALUE2); // 1
        store.addValue(RANGE1B, VALUE2B);
        store.addValue(RANGE2A, VALUE3); // 2
        store.addValue(RANGE3, VALUE4); // 3

        this.idsAndCheck(store, 0, 4, RANGE1A, RANGE1B, RANGE2A, RANGE3);
    }

    @Test
    public void testIdsAbsoluteRange() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1AABSOLUTE, VALUE1); // 0
        store.addValue(RANGE1B, VALUE2); // 1
        store.addValue(RANGE1B, VALUE2B);
        store.addValue(RANGE2A, VALUE3); // 2
        store.addValue(RANGE3, VALUE4); // 3

        this.idsAndCheck(store, 0, 4, RANGE1A, RANGE1B, RANGE2A, RANGE3);
    }

    @Test
    public void testIdsAbsoluteRange2() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1); // 0
        store.addValue(RANGE1B, VALUE2); // 1
        store.addValue(RANGE1B, VALUE2B);
        store.addValue(RANGE2A, VALUE3); // 2
        store.addValue(RANGE3, VALUE4); // 3

        this.idsAndCheck(store, 0, 4, RANGE1A, RANGE1B, RANGE2A, RANGE3);
    }

    @Test
    public void testIdsWindow() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1); // 0
        store.addValue(RANGE1B, VALUE2); // 1
        store.addValue(RANGE1B, VALUE2B);
        store.addValue(RANGE2A, VALUE3); // 2
        store.addValue(RANGE2B, VALUE4); // 3
        store.addValue(RANGE3, VALUE5); // 4
        store.addValue(RANGE4, VALUE6); // 5

        this.idsAndCheck(store, 1, 4, RANGE1B, RANGE2A, RANGE2B, RANGE3);
    }

    // values....................................................................................

    @Test
    public void testValues() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1B, VALUE2);
        store.addValue(RANGE1B, VALUE2B);
        store.addValue(RANGE2A, VALUE3);

        //noinspection unchecked
        this.valuesAndCheck(
            store,
            0,
            3,
            Lists.of(VALUE1),
            Lists.of(VALUE2, VALUE2B),
            Lists.of(VALUE3)
        );
    }

    @Test
    public void testValuesAbsoluteRange() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1AABSOLUTE, VALUE1);
        store.addValue(RANGE1B, VALUE2);
        store.addValue(RANGE1B, VALUE2B);
        store.addValue(RANGE2A, VALUE3);

        //noinspection unchecked
        this.valuesAndCheck(
            store,
            0,
            3,
            Lists.of(VALUE1),
            Lists.of(VALUE2, VALUE2B),
            Lists.of(VALUE3)
        );
    }

    @Test
    public void testValuesAbsoluteRange2() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1AABSOLUTE, VALUE1);
        store.addValue(RANGE1B, VALUE2);
        store.addValue(RANGE1B, VALUE2B);
        store.addValue(RANGE2A, VALUE3);

        //noinspection unchecked
        this.valuesAndCheck(
            store,
            0,
            3,
            Lists.of(VALUE1),
            Lists.of(VALUE2, VALUE2B),
            Lists.of(VALUE3)
        );
    }

    @Test
    public void testValuesWindow() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

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
            Lists.of(VALUE2, VALUE2B),
            Lists.of(VALUE4)
        );
    }

    // findCellRangesIncludingCell......................................................................................

    @Test
    public void testFindCellRangesIncludingCellWithBeginRange() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);

        this.findCellRangesIncludingCellAndCheck(store, RANGE1A.begin(), RANGE1A);
    }

    @Test
    public void testFindCellRangesIncludingCellWithBeginRangeAbsolute() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);

        this.findCellRangesIncludingCellAndCheck(store, RANGE1A.begin().toAbsolute(), RANGE1A);
    }

    @Test
    public void testFindCellRangesIncludingCellWithBeginRangeAbsolute2() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1AABSOLUTE, VALUE1);

        this.findCellRangesIncludingCellAndCheck(store, RANGE1A.begin(), RANGE1A);
    }

    @Test
    public void testFindCellRangesIncludingCellWithBeginRangeAbsolute3() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1AABSOLUTE, VALUE1);

        this.findCellRangesIncludingCellAndCheck(store, RANGE1A.begin().toAbsolute(), RANGE1A);
    }

    @Test
    public void testFindCellRangesIncludingCellWithMidRange() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);

        final SpreadsheetCellReference mid = RANGE1A.begin().add(1, 1);
        assertTrue(RANGE1A.begin().compareTo(mid) < 0, "RANGE1A.begin < mid");
        assertTrue(mid.compareTo(RANGE1A.end()) < 0, "mid< RANGE1A.end");

        this.findCellRangesIncludingCellAndCheck(store, mid, RANGE1A);
    }

    @Test
    public void testFindCellRangesIncludingCellWithMidRangeAbsolute() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1AABSOLUTE, VALUE1);

        final SpreadsheetCellReference mid = RANGE1A.begin().add(1, 1);
        assertTrue(RANGE1A.begin().compareTo(mid) < 0, "RANGE1A.begin < mid");
        assertTrue(mid.compareTo(RANGE1A.end()) < 0, "mid< RANGE1A.end");

        this.findCellRangesIncludingCellAndCheck(store, mid, RANGE1A);
    }

    @Test
    public void testFindCellRangesIncludingCellWithMidRangeAbsolute2() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1AABSOLUTE, VALUE1);

        final SpreadsheetCellReference mid = RANGE1AABSOLUTE.begin().add(1, 1);
        assertTrue(RANGE1A.begin().compareTo(mid) < 0, "RANGE1A.begin < mid");
        assertTrue(mid.compareTo(RANGE1A.end()) < 0, "mid< RANGE1A.end");

        this.findCellRangesIncludingCellAndCheck(store, mid, RANGE1A);
    }

    @Test
    public void testFindCellRangesIncludingCellWithEndRange() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);

        this.findCellRangesIncludingCellAndCheck(store, RANGE1A.end(), RANGE1A);
    }

    @Test
    public void testFindCellRangesIncludingCellWithEndRangeAbsolute() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1AABSOLUTE, VALUE1);

        this.findCellRangesIncludingCellAndCheck(store, RANGE1A.end(), RANGE1A);
    }

    @Test
    public void testFindCellRangesIncludingCellWithEndRangeAbsolute2() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1AABSOLUTE, VALUE1);

        this.findCellRangesIncludingCellAndCheck(store, RANGE1AABSOLUTE.end(), RANGE1A);
    }

    @Test
    public void testFindCellRangesIncludingCellWithEndRangeAbsolute3() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);

        this.findCellRangesIncludingCellAndCheck(store, RANGE1AABSOLUTE.end(), RANGE1A);
    }

    @Test
    public void testFindCellRangesIncludingCellWithBeginRange2() {
        this.checkNotEquals(RANGE1A.begin(), RANGE1C.begin(), "RANGE1A.begin() != RANGE1C.begin()");
        this.checkNotEquals(RANGE1A.begin(), RANGE2A.begin(), "RANGE1A.begin() != RANGE2A.begin()");

        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1C, VALUE2);
        store.addValue(RANGE2A, VALUE3);

        this.findCellRangesIncludingCellAndCheck(store, RANGE1A.begin(), RANGE1A);
    }

    @Test
    public void testFindCellRangesIncludingCellWithBeginRange3() {
        this.checkNotEquals(RANGE1A.begin(), RANGE1C.begin(), "RANGE1A.begin() != RANGE1C.begin()");
        this.checkNotEquals(RANGE1A.begin(), RANGE2A.begin(), "RANGE1A.begin() != RANGE2A.begin()");

        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1C, VALUE2);
        store.addValue(RANGE2A, VALUE3);

        this.findCellRangesIncludingCellAndCheck(store, RANGE1C.begin(), RANGE1A, RANGE1C);
    }

    @Test
    public void testFindCellRangesIncludingCellWithBeginRange4() {
        this.checkNotEquals(RANGE1A.begin(), RANGE1C.begin(), "RANGE1A.begin() != RANGE1C.begin()");
        this.checkNotEquals(RANGE1A.begin(), RANGE2A.begin(), "RANGE1A.begin() != RANGE2A.begin()");

        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1C, VALUE2);
        store.addValue(RANGE2A, VALUE3);

        this.findCellRangesIncludingCellAndCheck(store, RANGE2A.begin(), RANGE2A);
    }

    @Test
    public void testFindCellRangesIncludingCellWithEndRange2() {
        this.checkNotEquals(RANGE1A.end(), RANGE1B.end(), "RANGE1A.end() != RANGE1B.end()");
        this.checkNotEquals(RANGE1A.end(), RANGE2A.end(), "RANGE1A.end() != RANGE1B.end()");

        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1B, VALUE2);
        store.addValue(RANGE2A, VALUE3);

        this.findCellRangesIncludingCellAndCheck(store, RANGE1A.end(), RANGE1A, RANGE1B);
    }

    @Test
    public void testFindCellRangesIncludingCellWithEndRange3() {
        this.checkNotEquals(RANGE1A.end(), RANGE1B.end(), "RANGE1A.end() != RANGE1B.end()");
        this.checkNotEquals(RANGE1A.end(), RANGE2A.end(), "RANGE1A.end() != RANGE1B.end()");

        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1B, VALUE2);
        store.addValue(RANGE2A, VALUE3);

        this.findCellRangesIncludingCellAndCheck(store, RANGE1B.end(), RANGE1B);
    }

    @Test
    public void testFindCellRangesIncludingCellWithEndRange4() {
        this.checkNotEquals(RANGE1A.end(), RANGE1B.end(), "RANGE1A.end() != RANGE1B.end()");
        this.checkNotEquals(RANGE1A.end(), RANGE2A.end(), "RANGE1A.end() != RANGE1B.end()");

        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1B, VALUE2);
        store.addValue(RANGE2A, VALUE3);

        this.findCellRangesIncludingCellAndCheck(store, RANGE2A.end(), RANGE2A);
    }

    @Test
    public void testFindCellRangesIncludingCellWithMidRange2() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE2A, VALUE2);
        store.addValue(RANGE3, VALUE3);

        final SpreadsheetCellReference mid = RANGE1A.begin().add(1, 1);
        assertTrue(RANGE1A.begin().compareTo(mid) < 0, "RANGE1A.begin < mid");
        assertTrue(mid.compareTo(RANGE1A.end()) < 0, "mid< RANGE1A.end");

        this.findCellRangesIncludingCellAndCheck(store, mid, RANGE1A);
    }

    // load cell reference ranges....................................................................................

    @Test
    public void testFindValuesWithCellBeginRange() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);

        this.findValuesWithCellAndCheck(store, RANGE1A.begin(), VALUE1);
    }

    @Test
    public void testFindValuesWithCellBeginRangeAbsolute() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1AABSOLUTE, VALUE1);

        this.findValuesWithCellAndCheck(store, RANGE1AABSOLUTE.begin(), VALUE1);
    }

    @Test
    public void testFindValuesWithCellBeginRangeAbsolute2() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1AABSOLUTE, VALUE1);

        this.findValuesWithCellAndCheck(store, RANGE1A.begin(), VALUE1);
    }

    @Test
    public void testFindValuesWithCellBeginRangeAbsolute3() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);

        this.findValuesWithCellAndCheck(store, RANGE1AABSOLUTE.begin(), VALUE1);
    }

    @Test
    public void testFindValuesWithCellMidRange() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);

        final SpreadsheetCellReference mid = RANGE1A.begin().add(1, 1);
        assertTrue(RANGE1A.begin().compareTo(mid) < 0, "RANGE1A.begin < mid");
        assertTrue(mid.compareTo(RANGE1A.end()) < 0, "mid< RANGE1A.end");

        this.findValuesWithCellAndCheck(store, mid, VALUE1);
    }

    @Test
    public void testFindValuesWithCellEndRange() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);

        this.findValuesWithCellAndCheck(store, RANGE1A.end(), VALUE1);
    }

    @Test
    public void testFindValuesWithCellBeginRange2() {
        this.checkNotEquals(RANGE1A.begin(), RANGE1C.begin(), "RANGE1A.begin() != RANGE1C.begin()");
        this.checkNotEquals(RANGE1A.begin(), RANGE2A.begin(), "RANGE1A.begin() != RANGE2A.begin()");

        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1C, VALUE2);
        store.addValue(RANGE2A, VALUE3);

        this.findValuesWithCellAndCheck(store, RANGE1A.begin(), VALUE1);
    }

    @Test
    public void testFindValuesWithCellBeginRange3() {
        this.checkNotEquals(RANGE1A.begin(), RANGE1C.begin(), "RANGE1A.begin() != RANGE1C.begin()");
        this.checkNotEquals(RANGE1A.begin(), RANGE2A.begin(), "RANGE1A.begin() != RANGE2A.begin()");

        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1C, VALUE2);
        store.addValue(RANGE2A, VALUE3);

        this.findValuesWithCellAndCheck(store, RANGE1C.begin(), VALUE1, VALUE2);
    }

    @Test
    public void testFindValuesWithCellBeginRange4() {
        this.checkNotEquals(RANGE1A.begin(), RANGE1C.begin(), "RANGE1A.begin() != RANGE1C.begin()");
        this.checkNotEquals(RANGE1A.begin(), RANGE2A.begin(), "RANGE1A.begin() != RANGE2A.begin()");

        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1C, VALUE2);
        store.addValue(RANGE2A, VALUE3);

        this.findValuesWithCellAndCheck(store, RANGE2A.begin(), VALUE3);
    }

    @Test
    public void testFindValuesWithCellEndRange2() {
        this.checkNotEquals(RANGE1A.end(), RANGE1B.end(), "RANGE1A.end() != RANGE1B.end()");
        this.checkNotEquals(RANGE1A.end(), RANGE2A.end(), "RANGE1A.end() != RANGE1B.end()");

        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1B, VALUE2);
        store.addValue(RANGE2A, VALUE3);

        this.findValuesWithCellAndCheck(store, RANGE1A.end(), VALUE1, VALUE2);
    }

    @Test
    public void testFindValuesWithCellEndRange3() {
        this.checkNotEquals(RANGE1A.end(), RANGE1B.end(), "RANGE1A.end() != RANGE1B.end()");
        this.checkNotEquals(RANGE1A.end(), RANGE2A.end(), "RANGE1A.end() != RANGE1B.end()");

        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1B, VALUE2);
        store.addValue(RANGE2A, VALUE3);

        this.findValuesWithCellAndCheck(store, RANGE1B.end(), VALUE2);
    }

    @Test
    public void testFindValuesWithCellEndRange4() {
        this.checkNotEquals(RANGE1A.end(), RANGE1B.end(), "RANGE1A.end() != RANGE1B.end()");
        this.checkNotEquals(RANGE1A.end(), RANGE2A.end(), "RANGE1A.end() != RANGE1B.end()");

        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1B, VALUE2);
        store.addValue(RANGE2A, VALUE3);

        this.findValuesWithCellAndCheck(store, RANGE2A.end(), VALUE3);
    }

    @Test
    public void testFindValuesWithCellMidRange2() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE2A, VALUE2);
        store.addValue(RANGE3, VALUE3);

        final SpreadsheetCellReference mid = RANGE1A.begin().add(1, 1);
        assertTrue(RANGE1A.begin().compareTo(mid) < 0, "RANGE1A.begin < mid");
        assertTrue(mid.compareTo(RANGE1A.end()) < 0, "mid< RANGE1A.end");

        this.findValuesWithCellAndCheck(store, mid, VALUE1);
    }

    // delete range.....................................................................

    @Test
    public void testDeleteRange() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);

        store.delete(RANGE1A);

        this.countAndCheck(store, 0);
    }

    @Test
    public void testDeleteRangeAbsolute() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1AABSOLUTE, VALUE1);

        store.delete(RANGE1AABSOLUTE);

        this.countAndCheck(store, 0);
    }

    @Test
    public void testDeleteRangeAbsolute2() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);

        store.delete(RANGE1AABSOLUTE);

        this.countAndCheck(store, 0);
    }

    @Test
    public void testDeleteRange2() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1A, VALUE2);

        store.delete(RANGE1A);

        this.countAndCheck(store, 0);

        this.loadRangeFails(store, RANGE1A);
    }

    @Test
    public void testDelete3() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1A, VALUE2);
        store.addValue(RANGE2A, VALUE3);

        store.delete(RANGE1A);

        this.countAndCheck(store, 1);

        this.loadRangeFails(store, RANGE1A);
        this.loadRangeAndCheck(store, RANGE2A, VALUE3);
    }

    @Test
    public void testDelete4() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1A, VALUE2);
        store.addValue(RANGE2A, VALUE3);
        store.addValue(RANGE3, VALUE4);

        store.delete(RANGE1A);

        this.countAndCheck(store, 2);

        store.delete(RANGE2A);

        this.countAndCheck(store, 1);

        this.loadRangeFails(store, RANGE1A);
        this.loadRangeFails(store, RANGE2A);
        this.loadRangeAndCheck(store, RANGE3, VALUE4);
    }

    @Test
    public void testDelete4AbsoluteRange() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1AABSOLUTE, VALUE2);
        store.addValue(RANGE2A, VALUE3);
        store.addValue(RANGE3, VALUE4);

        store.delete(RANGE1A);

        this.countAndCheck(store, 2);

        store.delete(RANGE2A);

        this.countAndCheck(store, 1);

        this.loadRangeFails(store, RANGE1AABSOLUTE);
        this.loadRangeFails(store, RANGE2A);
        this.loadRangeAndCheck(store, RANGE3, VALUE4);
    }

    @Test
    public void testDelete4AbsoluteRange2() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1AABSOLUTE, VALUE2);
        store.addValue(RANGE2A, VALUE3);
        store.addValue(RANGE3, VALUE4);

        store.delete(RANGE1A);

        this.countAndCheck(store, 2);

        store.delete(RANGE2A);

        this.countAndCheck(store, 1);

        this.loadRangeFails(store, RANGE1A);
        this.loadRangeFails(store, RANGE2A);
        this.loadRangeAndCheck(store, RANGE3, VALUE4);
    }

    // replace value ......................................................................................

    @Test
    public void testReplaceValueSame() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.replaceValue(RANGE1A, VALUE1, VALUE1);

        this.countAndCheck(store, 1);

        this.loadRangeAndCheck(store, RANGE1A, VALUE1);
    }

    @Test
    public void testReplaceValueInvalidOldValue() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.replaceValue(RANGE1A, VALUE3, VALUE2);

        this.countAndCheck(store, 1);

        this.loadRangeAndCheck(store, RANGE1A, VALUE1);
    }

    @Test
    public void testReplaceValue() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.replaceValue(RANGE1A, VALUE2, VALUE1);

        this.countAndCheck(store, 1);

        this.loadRangeAndCheck(store, RANGE1A, VALUE2);
    }

    @Test
    public void testReplaceValueAbsoluteRange() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1AABSOLUTE, VALUE1);
        store.replaceValue(RANGE1AABSOLUTE, VALUE2, VALUE1);

        this.countAndCheck(store, 1);

        this.loadRangeAndCheck(store, RANGE1AABSOLUTE, VALUE2);
    }

    @Test
    public void testReplaceValueAbsoluteRange2() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.replaceValue(RANGE1AABSOLUTE, VALUE2, VALUE1);

        this.countAndCheck(store, 1);

        this.loadRangeAndCheck(store, RANGE1A, VALUE2);
    }

    @Test
    public void testReplaceValueAbsoluteRange3() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1AABSOLUTE, VALUE1);
        store.replaceValue(RANGE1A, VALUE2, VALUE1);

        this.countAndCheck(store, 1);

        this.loadRangeAndCheck(store, RANGE1A, VALUE2);
    }

    @Test
    public void testReplaceValueRangeMultipleValues() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1A, VALUE2);
        store.replaceValue(RANGE1A, VALUE3, VALUE2);

        this.countAndCheck(store, 2);

        this.loadRangeAndCheck(store, RANGE1A, VALUE1, VALUE3);
    }

    @Test
    public void testReplaceValueRangeMultipleValues2() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1A, VALUE2);
        store.addValue(RANGE2A, VALUE3);

        store.replaceValue(RANGE1A, VALUE4, VALUE2);

        this.countAndCheck(store, 3);

        this.loadRangeAndCheck(store, RANGE1A, VALUE1, VALUE4);
        this.loadRangeAndCheck(store, RANGE2A, VALUE3);
    }

    @Test
    public void testReplaceValueMany() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1A, VALUE2);
        store.addValue(RANGE2A, VALUE3);
        store.addValue(RANGE2A, VALUE4);

        store.replaceValue(RANGE1A, VALUE5, VALUE2);
        store.replaceValue(RANGE2A, VALUE6, VALUE3);

        this.countAndCheck(store, 4);

        this.loadRangeAndCheck(store, RANGE1A, VALUE1, VALUE5);
        this.loadRangeAndCheck(store, RANGE2A, VALUE4, VALUE6);
    }

    // delete value ......................................................................................

    @Test
    public void testDeleteValueUnknownIgnored() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.removeValue(RANGE1A, VALUE3);

        this.countAndCheck(store, 1);

        this.loadRangeAndCheck(store, RANGE1A, VALUE1);
    }

    @Test
    public void testDeleteValue() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.removeValue(RANGE1A, VALUE1);

        this.countAndCheck(store, 0);
    }

    @Test
    public void testDeleteValue2() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE2A, VALUE2);
        store.removeValue(RANGE1A, VALUE1);

        this.countAndCheck(store, 1); // VALUE2

        this.loadRangeFails(store, RANGE1A);
        this.loadRangeAndCheck(store, RANGE2A, VALUE2);
    }

    @Test
    public void testDeleteValueIgnored2() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE2A, VALUE2);
        store.removeValue(RANGE1A, VALUE2);

        this.countAndCheck(store, 2); // VALUE2

        this.loadRangeAndCheck(store, RANGE1A, VALUE1);
        this.loadRangeAndCheck(store, RANGE2A, VALUE2);
    }

    @Test
    public void testDeleteValueRangeMultipleValues() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1A, VALUE2);

        store.removeValue(RANGE1A, VALUE1);

        this.countAndCheck(store, 1); // VALUE2

        this.loadRangeAndCheck(store, RANGE1A, VALUE2);
    }

    @Test
    public void testDeleteValueRangeMultipleValues2() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1A, VALUE2);
        store.addValue(RANGE2A, VALUE3);

        store.removeValue(RANGE1A, VALUE1);

        this.countAndCheck(store, 2); // VALUE2, VALUE3

        this.loadRangeAndCheck(store, RANGE1A, VALUE2);
        this.loadRangeAndCheck(store, RANGE2A, VALUE3);
    }

    @Test
    public void testDeleteValueDifferentRanges() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE2A, VALUE2);
        store.addValue(RANGE3, VALUE3);

        store.removeValue(RANGE1A, VALUE1);

        this.countAndCheck(store, 2); // VALUE2, VALUE3

        this.loadRangeFails(store, RANGE1A);
        this.loadRangeAndCheck(store, RANGE2A, VALUE2);
        this.loadRangeAndCheck(store, RANGE3, VALUE3);
    }

    // findCellRangesWithValues.........................................................................................

    @Test
    public void testFindCellRangesWithValueUnknownValue() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        final SpreadsheetCellRangeReference range1 = SpreadsheetSelection.parseCellRange("A1:A1");
        final String value1 = "value1";
        store.addValue(range1, value1);

        this.findCellRangesWithValueAndCheck(store, "unknown!");
    }

    @Test
    public void testFindCellRangesWithValue2() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        final SpreadsheetCellRangeReference range1 = SpreadsheetSelection.parseCellRange("A1:A1");
        final String value1 = "value1";
        store.addValue(range1, value1);

        final SpreadsheetCellRangeReference range2 = SpreadsheetSelection.parseCellRange("A2:A2");
        final String value2 = "value2";
        store.addValue(range2, value2);

        this.findCellRangesWithValueAndCheck(store, value1, range1);
        this.findCellRangesWithValueAndCheck(store, value2, range2);
    }

    @Test
    public void testFindCellRangesWithValueAddValueRemoveValue() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        final SpreadsheetCellRangeReference range1 = SpreadsheetSelection.parseCellRange("A1:A1");
        final String value1 = "value1";
        store.addValue(range1, value1);
        store.removeValue(range1, value1);

        this.findCellRangesWithValueAndCheck(store, value1);
    }

    @Test
    public void testFindCellRangesWithValueAddValueRemoveValueAddValue() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        final SpreadsheetCellRangeReference range1 = SpreadsheetSelection.parseCellRange("A1:A1");
        final String value1 = "value1";
        store.addValue(range1, value1);
        store.removeValue(range1, value1);

        this.findCellRangesWithValueAndCheck(store, value1);

        store.addValue(range1, value1);
        this.findCellRangesWithValueAndCheck(store, value1, range1);
    }

    @Test
    public void testFindCellRangesWithValueAddValueReplaceValue() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        final SpreadsheetCellRangeReference range1 = SpreadsheetSelection.parseCellRange("A1:A1");
        final String value1 = "value1";
        store.addValue(range1, value1);

        final String value2 = "value2";
        store.replaceValue(range1, value2, value1);

        this.findCellRangesWithValueAndCheck(store, value1);
        this.findCellRangesWithValueAndCheck(store, value2, range1);
    }

    @Test
    public void testFindCellRangesWithValueAddValueRemoveValue2() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        final SpreadsheetCellRangeReference range1 = SpreadsheetSelection.parseCellRange("A1:A1");
        @SuppressWarnings("unused") final SpreadsheetCellRangeReference range2 = SpreadsheetSelection.parseCellRange("A2:A2");

        final String value1 = "value1";
        store.addValue(range1, value1);
        store.removeValue(range1, value1);

        this.findCellRangesWithValueAndCheck(store, value1);
    }

    @Test
    public void testFindCellRangesWithValueAddValueManyRanges() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        final SpreadsheetCellRangeReference range1 = SpreadsheetSelection.parseCellRange("A1:A1");
        final String value1 = "value1";
        store.addValue(range1, value1);

        final SpreadsheetCellRangeReference range2 = SpreadsheetSelection.parseCellRange("A2:A2");
        store.addValue(range2, value1);

        this.findCellRangesWithValueAndCheck(store, value1, range1, range2);
        this.findCellRangesWithValueAndCheck(store, "???");
    }

    @Test
    public void testFindCellRangesWithValueAddValueManyRanges2() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        final SpreadsheetCellRangeReference range1 = SpreadsheetSelection.parseCellRange("A1:A1");
        final String value1 = "value1";
        store.addValue(range1, value1);

        final SpreadsheetCellRangeReference range2 = SpreadsheetSelection.parseCellRange("A2:A2");
        store.addValue(range2, value1);

        final String value2 = "value2";
        store.addValue(range2, value2);

        this.findCellRangesWithValueAndCheck(store, value1, range1, range2);
        this.findCellRangesWithValueAndCheck(store, value2, range2);
        this.findCellRangesWithValueAndCheck(store, "???");
    }

    @Test
    public void testFindCellRangesWithValueAddValueReplaceValueManyRanges() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        final SpreadsheetCellRangeReference range1 = SpreadsheetSelection.parseCellRange("A1:A1");
        final String value1 = "value1";
        store.addValue(range1, value1);

        final SpreadsheetCellRangeReference range2 = SpreadsheetSelection.parseCellRange("A2:A2");
        final String value2 = "value2";
        store.addValue(range2, value2);

        final String value3 = "value3";
        store.replaceValue(range1, value3, value1);

        this.findCellRangesWithValueAndCheck(store, value1); // was removed by replace(value3
        this.findCellRangesWithValueAndCheck(store, value2, range2);
        this.findCellRangesWithValueAndCheck(store, value3, range1);
    }

    @Test
    public void testFindCellRangesWithValueAddValueReplaceValueManyRanges2() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        final SpreadsheetCellRangeReference range1 = SpreadsheetSelection.parseCellRange("A1:A1");
        final String value1 = "value1";
        store.addValue(range1, value1);

        final SpreadsheetCellRangeReference range2 = SpreadsheetSelection.parseCellRange("A2:A2");
        final String value2 = "value2";
        store.addValue(range2, value2);

        final String value3 = "value3";
        store.addValue(range1, value3);

        final String value4 = "value4";
        store.replaceValue(range1, value4, value3);

        this.findCellRangesWithValueAndCheck(store, value1, range1);
        this.findCellRangesWithValueAndCheck(store, value2, range2);
        this.findCellRangesWithValueAndCheck(store, value3);
        this.findCellRangesWithValueAndCheck(store, value4, range1);
    }

    @Test
    public void testFindCellRangesWithValueAddValueRemoveValueManyRanges() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        final SpreadsheetCellRangeReference range1 = SpreadsheetSelection.parseCellRange("A1:A1");
        final String value1 = "value1";
        store.addValue(range1, value1);

        final SpreadsheetCellRangeReference range2 = SpreadsheetSelection.parseCellRange("A2:A2");
        final String value2 = "value2";
        store.addValue(range2, value2);

        final String value3 = "value3";
        store.addValue(range1, value3);

        this.findCellRangesWithValueAndCheck(store, value1, range1);
        this.findCellRangesWithValueAndCheck(store, value2, range2);
        this.findCellRangesWithValueAndCheck(store, value3, range1);
    }

    // ToStringTesting ...................................................................................................

    @Test
    public void testToString() {
        final TreeMapSpreadsheetCellRangeStore<String> store = this.createStore();

        final SpreadsheetCellRangeReference range1 = SpreadsheetSelection.parseCellRange("A1:A1");
        final String value1 = "value1";
        store.addValue(range1, value1);

        final SpreadsheetCellRangeReference range2 = SpreadsheetSelection.parseCellRange("A2:A2");
        final String value2 = "value2";
        store.addValue(range2, value2);

        this.toStringAndCheck(store, "{A1=A1={A1=[value1]}, A2=A2={A2=[value2]}}");
    }

    // helpers ...................................................................................................

    @Override
    public TreeMapSpreadsheetCellRangeStore<String> createStore() {
        return TreeMapSpreadsheetCellRangeStore.create();
    }

    @Override
    public Class<TreeMapSpreadsheetCellRangeStore<String>> type() {
        return Cast.to(TreeMapSpreadsheetCellRangeStore.class);
    }

    @Override
    public String valueValue() {
        return VALUE1;
    }

    @Override
    public String typeNameSuffix() {
        return "";
    }
}

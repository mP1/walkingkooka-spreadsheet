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

package walkingkooka.spreadsheet.store.range;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.spreadsheet.SpreadsheetCellReference;
import walkingkooka.spreadsheet.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.SpreadsheetRange;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class TreeMapSpreadsheetRangeStoreTest extends TreeMapSpreadsheetRangeStoreTestCase<TreeMapSpreadsheetRangeStore<String>>
        implements SpreadsheetRangeStoreTesting<TreeMapSpreadsheetRangeStore<String>, String> {

    /**
     * RANGE1A and RANGE1B share a common TOPLEFT.
     */
    private final static SpreadsheetCellReference TOPLEFT1 = SpreadsheetRangeStoreTesting.cell(10, 20);
    private final static SpreadsheetCellReference CENTER1 = TOPLEFT1.add(1, 1);
    private final static SpreadsheetCellReference BOTTOMRIGHT1 = CENTER1.add(1, 1);
    private final static SpreadsheetRange RANGE1A = TOPLEFT1.spreadsheetRange(BOTTOMRIGHT1);
    private final static SpreadsheetRange RANGE1B = TOPLEFT1.spreadsheetRange(BOTTOMRIGHT1.add(1, 1));
    private final static SpreadsheetRange RANGE1C = CENTER1.spreadsheetRange(BOTTOMRIGHT1);

    private final static SpreadsheetCellReference TOPLEFT2 = SpreadsheetRangeStoreTesting.cell(30, 40);
    private final static SpreadsheetCellReference CENTER2 = TOPLEFT2.add(1, 1);
    private final static SpreadsheetCellReference BOTTOMRIGHT2 = CENTER2.add(2, 2);
    private final static SpreadsheetRange RANGE2A = TOPLEFT2.spreadsheetRange(BOTTOMRIGHT2);

    private final static SpreadsheetRange RANGE2B = CENTER1.spreadsheetRange(BOTTOMRIGHT2);

    private final static SpreadsheetCellReference TOPLEFT3 = SpreadsheetRangeStoreTesting.cell(50, 60);
    private final static SpreadsheetCellReference CENTER3 = TOPLEFT3.add(1, 1);
    private final static SpreadsheetCellReference BOTTOMRIGHT3 = CENTER3.add(2, 2);
    private final static SpreadsheetRange RANGE3 = TOPLEFT3.spreadsheetRange(BOTTOMRIGHT3);

    private final static SpreadsheetRange RANGE4 = SpreadsheetRangeStoreTesting.cell(70, 70)
            .spreadsheetRange(SpreadsheetRangeStoreTesting.cell(80, 80));

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
        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);

        this.countAndCheck(store, 1);

        this.loadRangeAndCheck(store, RANGE1A, VALUE1);
    }

    @Test
    public void testSaveAndLoadRangeSameValue() {
        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1A, VALUE1);

        this.countAndCheck(store, 1);

        this.loadRangeAndCheck(store, RANGE1A, VALUE1);
    }

    @Test
    public void testSaveAndLoadRangeWithMultipleValues() {
        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1A, VALUE2);

        this.countAndCheck(store, 2);

        this.loadRangeAndCheck(store, RANGE1A, VALUE1, VALUE2);
    }

    @Test
    public void testSaveAndLoadRangeWithMultipleValuesSameValue() {
        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1A, VALUE2);
        store.addValue(RANGE1A, VALUE2);

        this.countAndCheck(store, 2);

        this.loadRangeAndCheck(store, RANGE1A, VALUE1, VALUE2);
    }

    @Test
    public void testSaveAndLoadMultipleRanges() {
        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

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
        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

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
        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1B, VALUE2);

        this.countAndCheck(store, 2);

        this.loadRangeAndCheck(store, RANGE1A, VALUE1);
        this.loadRangeAndCheck(store, RANGE1B, VALUE2);
    }

    @Test
    public void testSaveAndLoadOverlappingRanges2() {
        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1C, VALUE2);

        this.countAndCheck(store, 2);

        this.loadRangeAndCheck(store, RANGE1A, VALUE1);
        this.loadRangeAndCheck(store, RANGE1C, VALUE2);
    }

    @Test
    public void testSaveAndLoadOverlappingRanges3() {
        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1C, VALUE2);

        this.countAndCheck(store, 2);

        this.loadRangeAndCheck(store, RANGE1A, VALUE1);
        this.loadRangeAndCheck(store, RANGE1C, VALUE2);
    }

    @Test
    public void testSaveAndLoadOverlappingRanges4() {
        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        store.addValue(RANGE2A, VALUE1);
        store.addValue(RANGE2B, VALUE2);

        this.countAndCheck(store, 2);

        this.loadRangeAndCheck(store, RANGE2A, VALUE1);
        this.loadRangeAndCheck(store, RANGE2B, VALUE2);
    }

    // ids....................................................................................

    @Test
    public final void testIds() {
        final TreeMapSpreadsheetRangeStore store = this.createStore();

        store.addValue(RANGE1A, VALUE1); // 0
        store.addValue(RANGE1B, VALUE2); // 1
        store.addValue(RANGE1B, VALUE2B);
        store.addValue(RANGE2A, VALUE3); // 2
        store.addValue(RANGE3, VALUE4); // 3

        this.idsAndCheck(store, 0, 4, RANGE1A, RANGE1B, RANGE2A, RANGE3);
    }

    @Test
    public final void testIdsWindow() {
        final TreeMapSpreadsheetRangeStore store = this.createStore();

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
    public final void testValues() {
        final TreeMapSpreadsheetRangeStore store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1B, VALUE2);
        store.addValue(RANGE1B, VALUE2B);
        store.addValue(RANGE2A, VALUE3);

        this.valuesAndCheck(store, RANGE1A, 3, Lists.of(VALUE1), Lists.of(VALUE2, VALUE2B), Lists.of(VALUE3));
    }

    @Test
    public final void testValuesWindow() {
        final TreeMapSpreadsheetRangeStore store = this.createStore();

        store.addValue(RANGE1A, VALUE1); // 0
        store.addValue(RANGE1B, VALUE2); // 1
        store.addValue(RANGE1B, VALUE2B);
        store.addValue(RANGE2A, VALUE3); // 3
        store.addValue(RANGE2B, VALUE4); // 2

        this.valuesAndCheck(store, RANGE1B, 2, Lists.of(VALUE2, VALUE2B), Lists.of(VALUE4));
    }

    // load cell reference ranges....................................................................................

    @Test
    public void testLoadCellReferenceRangesBeginRange() {
        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);

        this.loadCellReferenceRangesAndCheck(store, RANGE1A.begin(), RANGE1A);
    }

    @Test
    public void testLoadCellReferenceRangesMidRange() {
        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);

        final SpreadsheetCellReference mid = RANGE1A.begin().add(1, 1);
        assertTrue(RANGE1A.begin().compareTo(mid) < 0, "RANGE1A.begin < mid");
        assertTrue(mid.compareTo(RANGE1A.end()) < 0, "mid< RANGE1A.end");

        this.loadCellReferenceRangesAndCheck(store, mid, RANGE1A);
    }

    @Test
    public void testLoadCellReferenceRangesEndRange() {
        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);

        this.loadCellReferenceRangesAndCheck(store, RANGE1A.end(), RANGE1A);
    }

    @Test
    public void testLoadCellReferenceRangesBeginRange2() {
        assertNotEquals(RANGE1A.begin(), RANGE1C.begin(), "RANGE1A.begin() != RANGE1C.begin()");
        assertNotEquals(RANGE1A.begin(), RANGE2A.begin(), "RANGE1A.begin() != RANGE2A.begin()");

        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1C, VALUE2);
        store.addValue(RANGE2A, VALUE3);

        this.loadCellReferenceRangesAndCheck(store, RANGE1A.begin(), RANGE1A);
    }

    @Test
    public void testLoadCellReferenceRangesBeginRange3() {
        assertNotEquals(RANGE1A.begin(), RANGE1C.begin(), "RANGE1A.begin() != RANGE1C.begin()");
        assertNotEquals(RANGE1A.begin(), RANGE2A.begin(), "RANGE1A.begin() != RANGE2A.begin()");

        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1C, VALUE2);
        store.addValue(RANGE2A, VALUE3);

        this.loadCellReferenceRangesAndCheck(store, RANGE1C.begin(), RANGE1A, RANGE1C);
    }

    @Test
    public void testLoadCellReferenceRangesBeginRange4() {
        assertNotEquals(RANGE1A.begin(), RANGE1C.begin(), "RANGE1A.begin() != RANGE1C.begin()");
        assertNotEquals(RANGE1A.begin(), RANGE2A.begin(), "RANGE1A.begin() != RANGE2A.begin()");

        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1C, VALUE2);
        store.addValue(RANGE2A, VALUE3);

        this.loadCellReferenceRangesAndCheck(store, RANGE2A.begin(), RANGE2A);
    }

    @Test
    public void testLoadCellReferenceRangesEndRange2() {
        assertNotEquals(RANGE1A.end(), RANGE1B.end(), "RANGE1A.end() != RANGE1B.end()");
        assertNotEquals(RANGE1A.end(), RANGE2A.end(), "RANGE1A.end() != RANGE1B.end()");

        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1B, VALUE2);
        store.addValue(RANGE2A, VALUE3);

        this.loadCellReferenceRangesAndCheck(store, RANGE1A.end(), RANGE1A, RANGE1B);
    }

    @Test
    public void testLoadCellReferenceRangesEndRange3() {
        assertNotEquals(RANGE1A.end(), RANGE1B.end(), "RANGE1A.end() != RANGE1B.end()");
        assertNotEquals(RANGE1A.end(), RANGE2A.end(), "RANGE1A.end() != RANGE1B.end()");

        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1B, VALUE2);
        store.addValue(RANGE2A, VALUE3);

        this.loadCellReferenceRangesAndCheck(store, RANGE1B.end(), RANGE1B);
    }

    @Test
    public void testLoadCellReferenceRangesEndRange4() {
        assertNotEquals(RANGE1A.end(), RANGE1B.end(), "RANGE1A.end() != RANGE1B.end()");
        assertNotEquals(RANGE1A.end(), RANGE2A.end(), "RANGE1A.end() != RANGE1B.end()");

        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1B, VALUE2);
        store.addValue(RANGE2A, VALUE3);

        this.loadCellReferenceRangesAndCheck(store, RANGE2A.end(), RANGE2A);
    }

    @Test
    public void testLoadCellReferenceRangesMidRange2() {
        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE2A, VALUE2);
        store.addValue(RANGE3, VALUE3);

        final SpreadsheetCellReference mid = RANGE1A.begin().add(1, 1);
        assertTrue(RANGE1A.begin().compareTo(mid) < 0, "RANGE1A.begin < mid");
        assertTrue(mid.compareTo(RANGE1A.end()) < 0, "mid< RANGE1A.end");

        this.loadCellReferenceRangesAndCheck(store, mid, RANGE1A);
    }

    // load cell reference ranges....................................................................................

    @Test
    public void testLoadCellReferenceValuesBeginRange() {
        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);

        this.loadCellReferenceValuesAndCheck(store, RANGE1A.begin(), VALUE1);
    }

    @Test
    public void testLoadCellReferenceValuesMidRange() {
        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);

        final SpreadsheetCellReference mid = RANGE1A.begin().add(1, 1);
        assertTrue(RANGE1A.begin().compareTo(mid) < 0, "RANGE1A.begin < mid");
        assertTrue(mid.compareTo(RANGE1A.end()) < 0, "mid< RANGE1A.end");

        this.loadCellReferenceValuesAndCheck(store, mid, VALUE1);
    }

    @Test
    public void testLoadCellReferenceValuesEndRange() {
        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);

        this.loadCellReferenceValuesAndCheck(store, RANGE1A.end(), VALUE1);
    }

    @Test
    public void testLoadCellReferenceValuesBeginRange2() {
        assertNotEquals(RANGE1A.begin(), RANGE1C.begin(), "RANGE1A.begin() != RANGE1C.begin()");
        assertNotEquals(RANGE1A.begin(), RANGE2A.begin(), "RANGE1A.begin() != RANGE2A.begin()");

        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1C, VALUE2);
        store.addValue(RANGE2A, VALUE3);

        this.loadCellReferenceValuesAndCheck(store, RANGE1A.begin(), VALUE1);
    }

    @Test
    public void testLoadCellReferenceValuesBeginRange3() {
        assertNotEquals(RANGE1A.begin(), RANGE1C.begin(), "RANGE1A.begin() != RANGE1C.begin()");
        assertNotEquals(RANGE1A.begin(), RANGE2A.begin(), "RANGE1A.begin() != RANGE2A.begin()");

        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1C, VALUE2);
        store.addValue(RANGE2A, VALUE3);

        this.loadCellReferenceValuesAndCheck(store, RANGE1C.begin(), VALUE1, VALUE2);
    }

    @Test
    public void testLoadCellReferenceValuesBeginRange4() {
        assertNotEquals(RANGE1A.begin(), RANGE1C.begin(), "RANGE1A.begin() != RANGE1C.begin()");
        assertNotEquals(RANGE1A.begin(), RANGE2A.begin(), "RANGE1A.begin() != RANGE2A.begin()");

        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1C, VALUE2);
        store.addValue(RANGE2A, VALUE3);

        this.loadCellReferenceValuesAndCheck(store, RANGE2A.begin(), VALUE3);
    }

    @Test
    public void testLoadCellReferenceValuesEndRange2() {
        assertNotEquals(RANGE1A.end(), RANGE1B.end(), "RANGE1A.end() != RANGE1B.end()");
        assertNotEquals(RANGE1A.end(), RANGE2A.end(), "RANGE1A.end() != RANGE1B.end()");

        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1B, VALUE2);
        store.addValue(RANGE2A, VALUE3);

        this.loadCellReferenceValuesAndCheck(store, RANGE1A.end(), VALUE1, VALUE2);
    }

    @Test
    public void testLoadCellReferenceValuesEndRange3() {
        assertNotEquals(RANGE1A.end(), RANGE1B.end(), "RANGE1A.end() != RANGE1B.end()");
        assertNotEquals(RANGE1A.end(), RANGE2A.end(), "RANGE1A.end() != RANGE1B.end()");

        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1B, VALUE2);
        store.addValue(RANGE2A, VALUE3);

        this.loadCellReferenceValuesAndCheck(store, RANGE1B.end(), VALUE2);
    }

    @Test
    public void testLoadCellReferenceValuesEndRange4() {
        assertNotEquals(RANGE1A.end(), RANGE1B.end(), "RANGE1A.end() != RANGE1B.end()");
        assertNotEquals(RANGE1A.end(), RANGE2A.end(), "RANGE1A.end() != RANGE1B.end()");

        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1B, VALUE2);
        store.addValue(RANGE2A, VALUE3);

        this.loadCellReferenceValuesAndCheck(store, RANGE2A.end(), VALUE3);
    }

    @Test
    public void testLoadCellReferenceValuesMidRange2() {
        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE2A, VALUE2);
        store.addValue(RANGE3, VALUE3);

        final SpreadsheetCellReference mid = RANGE1A.begin().add(1, 1);
        assertTrue(RANGE1A.begin().compareTo(mid) < 0, "RANGE1A.begin < mid");
        assertTrue(mid.compareTo(RANGE1A.end()) < 0, "mid< RANGE1A.end");

        this.loadCellReferenceValuesAndCheck(store, mid, VALUE1);
    }

    // delete range.....................................................................

    @Test
    public void testDeleteRange() {
        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);

        store.delete(RANGE1A);

        this.countAndCheck(store, 0);
    }

    @Test
    public void testDeleteRange2() {
        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1A, VALUE2);

        store.delete(RANGE1A);

        this.countAndCheck(store, 0);

        this.loadRangeFails(store, RANGE1A);
    }

    @Test
    public void testDelete3() {
        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

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
        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

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

    // replace value ......................................................................................

    @Test
    public void testReplaceValueSame() {
        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.replaceValue(RANGE1A, VALUE1, VALUE1);

        this.countAndCheck(store, 1);

        this.loadRangeAndCheck(store, RANGE1A, VALUE1);
    }

    @Test
    public void testReplaceValueInvalidOldValue() {
        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.replaceValue(RANGE1A, VALUE3, VALUE2);

        this.countAndCheck(store, 1);

        this.loadRangeAndCheck(store, RANGE1A, VALUE1);
    }

    @Test
    public void testReplaceValue() {
        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.replaceValue(RANGE1A, VALUE2, VALUE1);

        this.countAndCheck(store, 1);

        this.loadRangeAndCheck(store, RANGE1A, VALUE2);
    }

    @Test
    public void testReplaceValueRangeMultipleValues() {
        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1A, VALUE2);
        store.replaceValue(RANGE1A, VALUE3, VALUE2);

        this.countAndCheck(store, 2);

        this.loadRangeAndCheck(store, RANGE1A, VALUE1, VALUE3);
    }

    @Test
    public void testReplaceValueRangeMultipleValues2() {
        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

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
        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

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
        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.removeValue(RANGE1A, VALUE3);

        this.countAndCheck(store, 1);

        this.loadRangeAndCheck(store, RANGE1A, VALUE1);
    }

    @Test
    public void testDeleteValue() {
        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.removeValue(RANGE1A, VALUE1);

        this.countAndCheck(store, 0);
    }

    @Test
    public void testDeleteValue2() {
        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE2A, VALUE2);
        store.removeValue(RANGE1A, VALUE1);

        this.countAndCheck(store, 1); // VALUE2

        this.loadRangeFails(store, RANGE1A);
        this.loadRangeAndCheck(store, RANGE2A, VALUE2);
    }

    @Test
    public void testDeleteValueIgnored2() {
        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE2A, VALUE2);
        store.removeValue(RANGE1A, VALUE2);

        this.countAndCheck(store, 2); // VALUE2

        this.loadRangeAndCheck(store, RANGE1A, VALUE1);
        this.loadRangeAndCheck(store, RANGE2A, VALUE2);
    }

    @Test
    public void testDeleteValueRangeMultipleValues() {
        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE1A, VALUE2);

        store.removeValue(RANGE1A, VALUE1);

        this.countAndCheck(store, 1); // VALUE2

        this.loadRangeAndCheck(store, RANGE1A, VALUE2);
    }

    @Test
    public void testDeleteValueRangeMultipleValues2() {
        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

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
        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        store.addValue(RANGE1A, VALUE1);
        store.addValue(RANGE2A, VALUE2);
        store.addValue(RANGE3, VALUE3);

        store.removeValue(RANGE1A, VALUE1);

        this.countAndCheck(store, 2); // VALUE2, VALUE3

        this.loadRangeFails(store, RANGE1A);
        this.loadRangeAndCheck(store, RANGE2A, VALUE2);
        this.loadRangeAndCheck(store, RANGE3, VALUE3);
    }

    // rangesWithValue....................................................................................................

    @Test
    public void testRangesWithValueUnknownValue() {
        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        final SpreadsheetRange range1 = SpreadsheetExpressionReference.parseRange("A1:A1");
        final String value1 = "value1";
        store.addValue(range1, value1);

        this.rangesWithValuesAndCheck(store, "unknown!");
    }

    @Test
    public void testRangesWithValue2() {
        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        final SpreadsheetRange range1 = SpreadsheetExpressionReference.parseRange("A1:A1");
        final String value1 = "value1";
        store.addValue(range1, value1);

        final SpreadsheetRange range2 = SpreadsheetExpressionReference.parseRange("A2:A2");
        final String value2 = "value2";
        store.addValue(range2, value2);

        this.rangesWithValuesAndCheck(store, value1, range1);
        this.rangesWithValuesAndCheck(store, value2, range2);
    }

    @Test
    public void testRangesWithValueAddValueRemoveValue() {
        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        final SpreadsheetRange range1 = SpreadsheetExpressionReference.parseRange("A1:A1");
        final String value1 = "value1";
        store.addValue(range1, value1);
        store.removeValue(range1, value1);

        this.rangesWithValuesAndCheck(store, value1);
    }

    @Test
    public void testRangesWithValueAddValueRemoveValueAddValue() {
        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        final SpreadsheetRange range1 = SpreadsheetExpressionReference.parseRange("A1:A1");
        final String value1 = "value1";
        store.addValue(range1, value1);
        store.removeValue(range1, value1);

        this.rangesWithValuesAndCheck(store, value1);

        store.addValue(range1, value1);
        this.rangesWithValuesAndCheck(store, value1, range1);
    }

    @Test
    public void testRangesWithValueAddValueReplaceValue() {
        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        final SpreadsheetRange range1 = SpreadsheetExpressionReference.parseRange("A1:A1");
        final String value1 = "value1";
        store.addValue(range1, value1);

        final String value2 = "value2";
        store.replaceValue(range1, value2, value1);

        this.rangesWithValuesAndCheck(store, value1);
        this.rangesWithValuesAndCheck(store, value2, range1);
    }

    @Test
    public void testRangesWithValueAddValueRemoveValue2() {
        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        final SpreadsheetRange range1 = SpreadsheetExpressionReference.parseRange("A1:A1");
        final SpreadsheetRange range2 = SpreadsheetExpressionReference.parseRange("A2:A2");

        final String value1 = "value1";
        store.addValue(range1, value1);
        store.removeValue(range1, value1);

        this.rangesWithValuesAndCheck(store, value1);
    }

    @Test
    public void testRangesWithValueAddValueManyRanges() {
        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        final SpreadsheetRange range1 = SpreadsheetExpressionReference.parseRange("A1:A1");
        final String value1 = "value1";
        store.addValue(range1, value1);

        final SpreadsheetRange range2 = SpreadsheetExpressionReference.parseRange("A2:A2");
        store.addValue(range2, value1);

        this.rangesWithValuesAndCheck(store, value1, range1, range2);
        this.rangesWithValuesAndCheck(store, "???");
    }

    @Test
    public void testRangesWithValueAddValueManyRanges2() {
        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        final SpreadsheetRange range1 = SpreadsheetExpressionReference.parseRange("A1:A1");
        final String value1 = "value1";
        store.addValue(range1, value1);

        final SpreadsheetRange range2 = SpreadsheetExpressionReference.parseRange("A2:A2");
        store.addValue(range2, value1);

        final String value2 = "value2";
        store.addValue(range2, value2);

        this.rangesWithValuesAndCheck(store, value1, range1, range2);
        this.rangesWithValuesAndCheck(store, value2, range2);
        this.rangesWithValuesAndCheck(store, "???");
    }

    @Test
    public void testRangesWithValueAddValueReplaceValueManyRanges() {
        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        final SpreadsheetRange range1 = SpreadsheetExpressionReference.parseRange("A1:A1");
        final String value1 = "value1";
        store.addValue(range1, value1);

        final SpreadsheetRange range2 = SpreadsheetExpressionReference.parseRange("A2:A2");
        final String value2 = "value2";
        store.addValue(range2, value2);

        final String value3 = "value3";
        store.replaceValue(range1, value3, value1);

        this.rangesWithValuesAndCheck(store, value1); // was removed by replace(value3
        this.rangesWithValuesAndCheck(store, value2, range2);
        this.rangesWithValuesAndCheck(store, value3, range1);
    }

    @Test
    public void testRangesWithValueAddValueReplaceValueManyRanges2() {
        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        final SpreadsheetRange range1 = SpreadsheetExpressionReference.parseRange("A1:A1");
        final String value1 = "value1";
        store.addValue(range1, value1);

        final SpreadsheetRange range2 = SpreadsheetExpressionReference.parseRange("A2:A2");
        final String value2 = "value2";
        store.addValue(range2, value2);

        final String value3 = "value3";
        store.addValue(range1, value3);

        final String value4 = "value4";
        store.replaceValue(range1, value4, value3);

        this.rangesWithValuesAndCheck(store, value1, range1);
        this.rangesWithValuesAndCheck(store, value2, range2);
        this.rangesWithValuesAndCheck(store, value3);
        this.rangesWithValuesAndCheck(store, value4, range1);
    }

    @Test
    public void testRangesWithValueAddValueRemoveValueManyRanges() {
        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        final SpreadsheetRange range1 = SpreadsheetExpressionReference.parseRange("A1:A1");
        final String value1 = "value1";
        store.addValue(range1, value1);

        final SpreadsheetRange range2 = SpreadsheetExpressionReference.parseRange("A2:A2");
        final String value2 = "value2";
        store.addValue(range2, value2);

        final String value3 = "value3";
        store.addValue(range1, value3);

        this.rangesWithValuesAndCheck(store, value1, range1);
        this.rangesWithValuesAndCheck(store, value2, range2);
        this.rangesWithValuesAndCheck(store, value3, range1);
    }

    // ToStringTesting ...................................................................................................

    @Test
    public void testToString() {
        final TreeMapSpreadsheetRangeStore<String> store = this.createStore();

        final SpreadsheetRange range1 = SpreadsheetExpressionReference.parseRange("A1:A1");
        final String value1 = "value1";
        store.addValue(range1, value1);

        final SpreadsheetRange range2 = SpreadsheetExpressionReference.parseRange("A2:A2");
        final String value2 = "value2";
        store.addValue(range2, value2);

        this.toStringAndCheck(store, "{A1=A1={A1=[value1]}, A2=A2={A2=[value2]}}");
    }

    // helpers ...................................................................................................

    @Override
    public TreeMapSpreadsheetRangeStore<String> createStore() {
        return TreeMapSpreadsheetRangeStore.create();
    }

    @Override
    public Class<TreeMapSpreadsheetRangeStore<String>> type() {
        return Cast.to(TreeMapSpreadsheetRangeStore.class);
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

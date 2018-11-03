package walkingkooka.spreadsheet.store.range;

import org.junit.Test;
import walkingkooka.Cast;
import walkingkooka.spreadsheet.SpreadsheetRange;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public final class BasicSpreadsheetRangeStoreTest extends SpreadsheetRangeStoreTestCase<BasicSpreadsheetRangeStore<String>, String> {

    /**
     * RANGE1A and RANGE1B share a common TOPLEFT.
     */
    private final static SpreadsheetCellReference TOPLEFT1 = cell(10,20);
    private final static SpreadsheetCellReference CENTER1 = TOPLEFT1.add(1,1);
    private final static SpreadsheetCellReference BOTTOMRIGHT1 = CENTER1.add(1,1);
    private final static SpreadsheetRange RANGE1A = SpreadsheetRange.with(TOPLEFT1, BOTTOMRIGHT1);
    private final static SpreadsheetRange RANGE1B = SpreadsheetRange.with(TOPLEFT1, BOTTOMRIGHT1.add(1, 1));
    private final static SpreadsheetRange RANGE1C = SpreadsheetRange.with(CENTER1, BOTTOMRIGHT1);

    private final static SpreadsheetCellReference TOPLEFT2 = cell(30, 40);
    private final static SpreadsheetCellReference CENTER2 = TOPLEFT2.add(1,1);
    private final static SpreadsheetCellReference BOTTOMRIGHT2 = CENTER2.add(2,2);
    private final static SpreadsheetRange RANGE2A = SpreadsheetRange.with(TOPLEFT2, BOTTOMRIGHT2);

    private final static SpreadsheetRange RANGE2B = SpreadsheetRange.with(CENTER1, BOTTOMRIGHT2);

    private final static SpreadsheetCellReference TOPLEFT3 = cell(50, 60);
    private final static SpreadsheetCellReference CENTER3 = TOPLEFT3.add(1,1);
    private final static SpreadsheetCellReference BOTTOMRIGHT3 = CENTER3.add(2,2);
    private final static SpreadsheetRange RANGE3 = SpreadsheetRange.with(TOPLEFT3, BOTTOMRIGHT3);
    
    private final static String VALUE1 = "value1";
    private final static String VALUE2 = "value2";
    private final static String VALUE3 = "value3";
    private final static String VALUE4 = "value4";
    private final static String VALUE5 = "value5";
    private final static String VALUE6 = "value6";

    // save and load range ...................................................................................................

    @Test
    public void testSaveAndLoadRange() {
        final BasicSpreadsheetRangeStore<String> store = this.createStore();

        store.saveValue(RANGE1A, VALUE1);

        this.countAndCheck(store, 1);

        this.loadRangeAndCheck(store, RANGE1A, VALUE1);
    }

    @Test
    public void testSaveAndLoadRangeSameValue() {
        final BasicSpreadsheetRangeStore<String> store = this.createStore();

        store.saveValue(RANGE1A, VALUE1);
        store.saveValue(RANGE1A, VALUE1);

        this.countAndCheck(store, 1);

        this.loadRangeAndCheck(store, RANGE1A, VALUE1);
    }

    @Test
    public void testSaveAndLoadRangeWithMultipleValues() {
        final BasicSpreadsheetRangeStore<String> store = this.createStore();

        store.saveValue(RANGE1A, VALUE1);
        store.saveValue(RANGE1A, VALUE2);

        this.countAndCheck(store, 2);

        this.loadRangeAndCheck(store, RANGE1A, VALUE1, VALUE2);
    }

    @Test
    public void testSaveAndLoadRangeWithMultipleValuesSameValue() {
        final BasicSpreadsheetRangeStore<String> store = this.createStore();

        store.saveValue(RANGE1A, VALUE1);
        store.saveValue(RANGE1A, VALUE2);
        store.saveValue(RANGE1A, VALUE2);

        this.countAndCheck(store, 2);

        this.loadRangeAndCheck(store, RANGE1A, VALUE1, VALUE2);
    }

    @Test
    public void testSaveAndLoadMultipleRanges() {
        final BasicSpreadsheetRangeStore<String> store = this.createStore();

        store.saveValue(RANGE1A, VALUE1);
        store.saveValue(RANGE2A, VALUE2);
        store.saveValue(RANGE3, VALUE3);

        this.countAndCheck(store, 3);

        this.loadRangeAndCheck(store, RANGE1A, VALUE1);
        this.loadRangeAndCheck(store, RANGE2A, VALUE2);
        this.loadRangeAndCheck(store, RANGE3, VALUE3);
    }

    @Test
    public void testSaveAndLoadMultipleRangesMultipleValues() {
        final BasicSpreadsheetRangeStore<String> store = this.createStore();

        store.saveValue(RANGE1A, VALUE1);
        store.saveValue(RANGE1A, VALUE2);
        store.saveValue(RANGE2A, VALUE3);
        store.saveValue(RANGE2A, VALUE4);

        this.countAndCheck(store, 4);

        this.loadRangeAndCheck(store, RANGE1A, VALUE1, VALUE2);
        this.loadRangeAndCheck(store, RANGE2A, VALUE3, VALUE4);
    }

    @Test
    public void testSaveAndLoadOverlappingRanges() {
        final BasicSpreadsheetRangeStore<String> store = this.createStore();

        store.saveValue(RANGE1A, VALUE1);
        store.saveValue(RANGE1B, VALUE2);

        this.countAndCheck(store, 2);

        this.loadRangeAndCheck(store, RANGE1A, VALUE1);
        this.loadRangeAndCheck(store, RANGE1B, VALUE2);
    }

    @Test
    public void testSaveAndLoadOverlappingRanges2() {
        final BasicSpreadsheetRangeStore<String> store = this.createStore();

        store.saveValue(RANGE1A, VALUE1);
        store.saveValue(RANGE1C, VALUE2);

        this.countAndCheck(store, 2);

        this.loadRangeAndCheck(store, RANGE1A, VALUE1);
        this.loadRangeAndCheck(store, RANGE1C, VALUE2);
    }

    @Test
    public void testSaveAndLoadOverlappingRanges3() {
        final BasicSpreadsheetRangeStore<String> store = this.createStore();

        store.saveValue(RANGE1A, VALUE1);
        store.saveValue(RANGE1C, VALUE2);

        this.countAndCheck(store, 2);

        this.loadRangeAndCheck(store, RANGE1A, VALUE1);
        this.loadRangeAndCheck(store, RANGE1C, VALUE2);
    }

    @Test
    public void testSaveAndLoadOverlappingRanges4() {
        final BasicSpreadsheetRangeStore<String> store = this.createStore();

        store.saveValue(RANGE2A, VALUE1);
        store.saveValue(RANGE2B, VALUE2);

        this.countAndCheck(store, 2);

        this.loadRangeAndCheck(store, RANGE2A, VALUE1);
        this.loadRangeAndCheck(store, RANGE2B, VALUE2);
    }

    // load cell.....................................................................................................
    
    @Test
    public void testLoadCellBeginRange() {
        final BasicSpreadsheetRangeStore<String> store = this.createStore();

        store.saveValue(RANGE1A, VALUE1);

        this.loadCellReferenceAndCheck(store, RANGE1A.begin(), VALUE1);
    }

    @Test
    public void testLoadCellMidRange() {
        final BasicSpreadsheetRangeStore<String> store = this.createStore();

        store.saveValue(RANGE1A, VALUE1);

        final SpreadsheetCellReference mid = RANGE1A.begin().add(1, 1);
        assertTrue("RANGE1A.begin < mid", RANGE1A.begin().compareTo(mid) < 0);
        assertTrue("mid< RANGE1A.end", mid.compareTo(RANGE1A.end()) < 0);

        this.loadCellReferenceAndCheck(store, mid, VALUE1);
    }

    @Test
    public void testLoadCellEndRange() {
        final BasicSpreadsheetRangeStore<String> store = this.createStore();

        store.saveValue(RANGE1A, VALUE1);

        this.loadCellReferenceAndCheck(store, RANGE1A.end(), VALUE1);
    }

    @Test
    public void testLoadCellBeginRange2() {
        assertNotEquals("RANGE1A.begin() != RANGE1C.begin()", RANGE1A.begin(), RANGE1C.begin());
        assertNotEquals("RANGE1A.begin() != RANGE2A.begin()", RANGE1A.begin(), RANGE2A.begin());

        final BasicSpreadsheetRangeStore<String> store = this.createStore();

        store.saveValue(RANGE1A, VALUE1);
        store.saveValue(RANGE1C, VALUE2);
        store.saveValue(RANGE2A, VALUE3);

        this.loadCellReferenceAndCheck(store, RANGE1A.begin(), VALUE1);
    }

    @Test
    public void testLoadCellBeginRange3() {
        assertNotEquals("RANGE1A.begin() != RANGE1C.begin()", RANGE1A.begin(), RANGE1C.begin());
        assertNotEquals("RANGE1A.begin() != RANGE2A.begin()", RANGE1A.begin(), RANGE2A.begin());

        final BasicSpreadsheetRangeStore<String> store = this.createStore();

        store.saveValue(RANGE1A, VALUE1);
        store.saveValue(RANGE1C, VALUE2);
        store.saveValue(RANGE2A, VALUE3);

        this.loadCellReferenceAndCheck(store, RANGE1C.begin(), VALUE1, VALUE2);
    }

    @Test
    public void testLoadCellBeginRange4() {
        assertNotEquals("RANGE1A.begin() != RANGE1C.begin()", RANGE1A.begin(), RANGE1C.begin());
        assertNotEquals("RANGE1A.begin() != RANGE2A.begin()", RANGE1A.begin(), RANGE2A.begin());

        final BasicSpreadsheetRangeStore<String> store = this.createStore();

        store.saveValue(RANGE1A, VALUE1);
        store.saveValue(RANGE1C, VALUE2);
        store.saveValue(RANGE2A, VALUE3);

        this.loadCellReferenceAndCheck(store, RANGE2A.begin(), VALUE3);
    }

    @Test
    public void testLoadCellEndRange2() {
        assertNotEquals("RANGE1A.end() != RANGE1B.end()", RANGE1A.end(), RANGE1B.end());
        assertNotEquals("RANGE1A.end() != RANGE1B.end()", RANGE1A.end(), RANGE2A.end());

        final BasicSpreadsheetRangeStore<String> store = this.createStore();

        store.saveValue(RANGE1A, VALUE1);
        store.saveValue(RANGE1B, VALUE2);
        store.saveValue(RANGE2A, VALUE3);

        this.loadCellReferenceAndCheck(store, RANGE1A.end(), VALUE1, VALUE2);
    }

    @Test
    public void testLoadCellEndRange3() {
        assertNotEquals("RANGE1A.end() != RANGE1B.end()", RANGE1A.end(), RANGE1B.end());
        assertNotEquals("RANGE1A.end() != RANGE1B.end()", RANGE1A.end(), RANGE2A.end());

        final BasicSpreadsheetRangeStore<String> store = this.createStore();

        store.saveValue(RANGE1A, VALUE1);
        store.saveValue(RANGE1B, VALUE2);
        store.saveValue(RANGE2A, VALUE3);

        this.loadCellReferenceAndCheck(store, RANGE1B.end(), VALUE2);
    }

    @Test
    public void testLoadCellEndRange4() {
        assertNotEquals("RANGE1A.end() != RANGE1B.end()", RANGE1A.end(), RANGE1B.end());
        assertNotEquals("RANGE1A.end() != RANGE1B.end()", RANGE1A.end(), RANGE2A.end());

        final BasicSpreadsheetRangeStore<String> store = this.createStore();

        store.saveValue(RANGE1A, VALUE1);
        store.saveValue(RANGE1B, VALUE2);
        store.saveValue(RANGE2A, VALUE3);

        this.loadCellReferenceAndCheck(store, RANGE2A.end(), VALUE3);
    }

    @Test
    public void testLoadCellMidRange2() {
        final BasicSpreadsheetRangeStore<String> store = this.createStore();

        store.saveValue(RANGE1A, VALUE1);
        store.saveValue(RANGE2A, VALUE2);
        store.saveValue(RANGE3, VALUE3);

        final SpreadsheetCellReference mid = RANGE1A.begin().add(1, 1);
        assertTrue("RANGE1A.begin < mid", RANGE1A.begin().compareTo(mid) < 0);
        assertTrue("mid< RANGE1A.end", mid.compareTo(RANGE1A.end()) < 0);

        this.loadCellReferenceAndCheck(store, mid, VALUE1);
    }

    // delete range.....................................................................

    @Test
    public void testDeleteRange() {
        final BasicSpreadsheetRangeStore<String> store = this.createStore();

        store.saveValue(RANGE1A, VALUE1);

        store.delete(RANGE1A);

        this.countAndCheck(store, 0);
    }

    @Test
    public void testDeleteRange2() {
        final BasicSpreadsheetRangeStore<String> store = this.createStore();

        store.saveValue(RANGE1A, VALUE1);
        store.saveValue(RANGE1A, VALUE2);

        store.delete(RANGE1A);

        this.countAndCheck(store, 0);

        this.loadRangeFails(store, RANGE1A);
    }

    @Test
    public void testDelete3() {
        final BasicSpreadsheetRangeStore<String> store = this.createStore();

        store.saveValue(RANGE1A, VALUE1);
        store.saveValue(RANGE1A, VALUE2);
        store.saveValue(RANGE2A, VALUE3);

        store.delete(RANGE1A);

        this.countAndCheck(store, 1);

        this.loadRangeFails(store, RANGE1A);
        this.loadRangeAndCheck(store, RANGE2A, VALUE3);
    }

    @Test
    public void testDelete4() {
        final BasicSpreadsheetRangeStore<String> store = this.createStore();

        store.saveValue(RANGE1A, VALUE1);
        store.saveValue(RANGE1A, VALUE2);
        store.saveValue(RANGE2A, VALUE3);
        store.saveValue(RANGE3, VALUE4);

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
        final BasicSpreadsheetRangeStore<String> store = this.createStore();

        store.saveValue(RANGE1A, VALUE1);
        store.replaceValue(RANGE1A, VALUE1, VALUE1);

        this.countAndCheck(store, 1);

        this.loadRangeAndCheck(store, RANGE1A, VALUE1);
    }

    @Test
    public void testReplaceValueInvalidOldValue() {
        final BasicSpreadsheetRangeStore<String> store = this.createStore();

        store.saveValue(RANGE1A, VALUE1);
        store.replaceValue(RANGE1A, VALUE3, VALUE2);

        this.countAndCheck(store, 1);

        this.loadRangeAndCheck(store, RANGE1A, VALUE1);
    }

    @Test
    public void testReplaceValue() {
        final BasicSpreadsheetRangeStore<String> store = this.createStore();

        store.saveValue(RANGE1A, VALUE1);
        store.replaceValue(RANGE1A, VALUE2, VALUE1);

        this.countAndCheck(store, 1);

        this.loadRangeAndCheck(store, RANGE1A, VALUE2);
    }

    @Test
    public void testReplaceValueRangeMultipleValues() {
        final BasicSpreadsheetRangeStore<String> store = this.createStore();

        store.saveValue(RANGE1A, VALUE1);
        store.saveValue(RANGE1A, VALUE2);
        store.replaceValue(RANGE1A, VALUE3, VALUE2);

        this.countAndCheck(store, 2);

        this.loadRangeAndCheck(store, RANGE1A, VALUE1, VALUE3);
    }

    @Test
    public void testReplaceValueRangeMultipleValues2() {
        final BasicSpreadsheetRangeStore<String> store = this.createStore();

        store.saveValue(RANGE1A, VALUE1);
        store.saveValue(RANGE1A, VALUE2);
        store.saveValue(RANGE2A, VALUE3);

        store.replaceValue(RANGE1A, VALUE4, VALUE2);

        this.countAndCheck(store, 3);

        this.loadRangeAndCheck(store, RANGE1A, VALUE1, VALUE4);
        this.loadRangeAndCheck(store, RANGE2A, VALUE3);
    }

    @Test
    public void testReplaceValueMany() {
        final BasicSpreadsheetRangeStore<String> store = this.createStore();

        store.saveValue(RANGE1A, VALUE1);
        store.saveValue(RANGE1A, VALUE2);
        store.saveValue(RANGE2A, VALUE3);
        store.saveValue(RANGE2A, VALUE4);

        store.replaceValue(RANGE1A, VALUE5, VALUE2);
        store.replaceValue(RANGE2A, VALUE6, VALUE3);

        this.countAndCheck(store, 4);

        this.loadRangeAndCheck(store, RANGE1A, VALUE1, VALUE5);
        this.loadRangeAndCheck(store, RANGE2A, VALUE4, VALUE6);
    }

    // delete value ......................................................................................

    @Test
    public void testDeleteValueUnknownIgnored() {
        final BasicSpreadsheetRangeStore<String> store = this.createStore();

        store.saveValue(RANGE1A, VALUE1);
        store.deleteValue(RANGE1A, VALUE3);

        this.countAndCheck(store, 1);

        this.loadRangeAndCheck(store, RANGE1A, VALUE1);
    }

    @Test
    public void testDeleteValue() {
        final BasicSpreadsheetRangeStore<String> store = this.createStore();

        store.saveValue(RANGE1A, VALUE1);
        store.deleteValue(RANGE1A, VALUE1);

        this.countAndCheck(store, 0);
    }

    @Test
    public void testDeleteValue2() {
        final BasicSpreadsheetRangeStore<String> store = this.createStore();

        store.saveValue(RANGE1A, VALUE1);
        store.saveValue(RANGE2A, VALUE2);
        store.deleteValue(RANGE1A, VALUE1);

        this.countAndCheck(store, 1); // VALUE2

        this.loadRangeFails(store, RANGE1A);
        this.loadRangeAndCheck(store, RANGE2A, VALUE2);
    }

    @Test
    public void testDeleteValueIgnored2() {
        final BasicSpreadsheetRangeStore<String> store = this.createStore();

        store.saveValue(RANGE1A, VALUE1);
        store.saveValue(RANGE2A, VALUE2);
        store.deleteValue(RANGE1A, VALUE2);

        this.countAndCheck(store, 2); // VALUE2

        this.loadRangeAndCheck(store, RANGE1A, VALUE1);
        this.loadRangeAndCheck(store, RANGE2A, VALUE2);
    }

    @Test
    public void testDeleteValueRangeMultipleValues() {
        final BasicSpreadsheetRangeStore<String> store = this.createStore();

        store.saveValue(RANGE1A, VALUE1);
        store.saveValue(RANGE1A, VALUE2);

        store.deleteValue(RANGE1A, VALUE1);

        this.countAndCheck(store, 1); // VALUE2

        this.loadRangeAndCheck(store, RANGE1A, VALUE2);
    }

    @Test
    public void testDeleteValueRangeMultipleValues2() {
        final BasicSpreadsheetRangeStore<String> store = this.createStore();

        store.saveValue(RANGE1A, VALUE1);
        store.saveValue(RANGE1A, VALUE2);
        store.saveValue(RANGE2A, VALUE3);

        store.deleteValue(RANGE1A, VALUE1);

        this.countAndCheck(store, 2); // VALUE2, VALUE3

        this.loadRangeAndCheck(store, RANGE1A, VALUE2);
        this.loadRangeAndCheck(store, RANGE2A, VALUE3);
    }

    @Test
    public void testDeleteValueDifferentRanges() {
        final BasicSpreadsheetRangeStore<String> store = this.createStore();

        store.saveValue(RANGE1A, VALUE1);
        store.saveValue(RANGE2A, VALUE2);
        store.saveValue(RANGE3, VALUE3);

        store.deleteValue(RANGE1A, VALUE1);

        this.countAndCheck(store, 2); // VALUE2, VALUE3

        this.loadRangeFails(store, RANGE1A);
        this.loadRangeAndCheck(store, RANGE2A, VALUE2);
        this.loadRangeAndCheck(store, RANGE3, VALUE3);
    }

    // helpers ...................................................................................................

    @Override
    protected BasicSpreadsheetRangeStore createStore() {
        return BasicSpreadsheetRangeStore.create();
    }

    @Override
    protected Class<BasicSpreadsheetRangeStore<String>> type() {
        return Cast.to(BasicSpreadsheetRangeStore.class);
    }

    @Override
    protected String value() {
        return VALUE1;
    }
}

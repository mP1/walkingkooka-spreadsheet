package walkingkooka.spreadsheet.store.range;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.spreadsheet.SpreadsheetRange;
import walkingkooka.spreadsheet.store.ReadOnlyStoreTesting;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ReadOnlySpreadsheetRangeStoreTest implements SpreadsheetRangeStoreTesting<ReadOnlySpreadsheetRangeStore<String>, String>,
        ReadOnlyStoreTesting<ReadOnlySpreadsheetRangeStore<String>, SpreadsheetRange, List<String>> {

    private final static SpreadsheetRange RANGE = SpreadsheetRange.with(SpreadsheetCellReference.parse("a1"), SpreadsheetCellReference.parse("b2"));
    private final static String VALUE = "value";

    @Test
    public void testSaveAndLoadRange() {
        final SpreadsheetRangeStore<String> store = SpreadsheetRangeStores.treeMap();

        store.addValue(RANGE, VALUE);

        this.countAndCheck(store, 1);

        this.loadRangeAndCheck(ReadOnlySpreadsheetRangeStore.with(store), RANGE, VALUE);
    }

    @Override
    public void testAddSaveWatcherAndSave() {
    }

    @Override
    public void testAddSaveWatcherAndSaveTwiceFiresOnce() {
    }

    @Override
    public void testAddSaveWatcherAndRemove() {
    }

    @Override
    public void testAddDeleteWatcherAndDelete() {
    }

    @Override
    public void testAddDeleteWatcherAndRemove() {
    }

    @Test
    public void testAddValueFails() {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.createStore().addValue(RANGE, VALUE);
        });
    }

    @Test
    public void testReplacealueFails() {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.createStore().replaceValue(RANGE, "old", "new");
        });
    }

    @Test
    public void testRemoveValueFails() {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.createStore().removeValue(RANGE, VALUE);
        });
    }

    @Test
    public void testRangesWithValue() {
        final SpreadsheetRangeStore<String> store = SpreadsheetRangeStores.treeMap();
        store.addValue(RANGE, VALUE);
        this.rangesWithValuesAndCheck(ReadOnlySpreadsheetRangeStore.with(store), VALUE, RANGE);
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(ReadOnlySpreadsheetRangeStore.with(new FakeSpreadsheetRangeStore<String>() {
            @Override
            public String toString() {
                return "ABC";
            }
        }), "ABC");
    }

    @Override
    public String valueValue() {
        return "hello";
    }

    @Override
    public ReadOnlySpreadsheetRangeStore<String> createStore() {
        return ReadOnlySpreadsheetRangeStore.with(SpreadsheetRangeStores.treeMap());
    }

    @Override
    public Class<ReadOnlySpreadsheetRangeStore<String>> type() {
        return Cast.to(ReadOnlySpreadsheetRangeStore.class);
    }
}

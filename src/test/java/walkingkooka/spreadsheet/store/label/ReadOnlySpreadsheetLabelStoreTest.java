package walkingkooka.spreadsheet.store.label;

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.SpreadsheetLabelMapping;
import walkingkooka.test.ToStringTesting;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ReadOnlySpreadsheetLabelStoreTest extends SpreadsheetLabelStoreTestCase<ReadOnlySpreadsheetLabelStore>
        implements ToStringTesting<ReadOnlySpreadsheetLabelStore> {

    @Test
    public void testWithNullStoreFails() {
        assertThrows(NullPointerException.class, () -> {
            ReadOnlySpreadsheetLabelStore.with(null);
        });
    }

    @Test
    public void testSaveAndLoad() {
        final SpreadsheetLabelStore store = SpreadsheetLabelStores.treeMap();

        final SpreadsheetLabelMapping mapping = SpreadsheetLabelMapping.with(LABEL, REFERENCE);
        store.save(mapping);

        assertSame(mapping, ReadOnlySpreadsheetLabelStore.with(store).loadOrFail(LABEL));
    }

    @Override
    public void testSaveDeleteLoad() {
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
    public void testCount() {
        this.countAndCheck(this.createStore2(), 1);
    }

    @Test
    public void testIds() {
        final SpreadsheetLabelStore store = SpreadsheetLabelStores.treeMap();

        final SpreadsheetLabelMapping a = this.mapping("a", 1, 2);
        final SpreadsheetLabelMapping b = this.mapping("b", 4, 8);
        final SpreadsheetLabelMapping c = this.mapping("c", 88, 99);

        store.save(a);
        store.save(b);
        store.save(c);

        this.idsAndCheck(ReadOnlySpreadsheetLabelStore.with(store), 0, 3, a.id(), b.id(), c.id());
    }

    @Override
    public void testIdsWindow() {
    }

    @Test
    public void testValues() {
        final SpreadsheetLabelStore store = SpreadsheetLabelStores.treeMap();

        final SpreadsheetLabelMapping a = this.mapping("a", 1, 2);
        final SpreadsheetLabelMapping b = this.mapping("b", 4, 8);
        final SpreadsheetLabelMapping c = this.mapping("c", 88, 99);

        store.save(a);
        store.save(b);
        store.save(c);

        this.valuesAndCheck(ReadOnlySpreadsheetLabelStore.with(store), a.id(), 3, a, b, c);
    }

    @Override
    public void testValuesWindow() {
    }

    @Test
    public void testToString() {
        final SpreadsheetLabelStore store = SpreadsheetLabelStores.fake();
        this.toStringAndCheck(ReadOnlySpreadsheetLabelStore.with(store), store.toString());
    }

    @Override
    public ReadOnlySpreadsheetLabelStore createStore() {
        return ReadOnlySpreadsheetLabelStore.with(SpreadsheetLabelStores.treeMap());
    }

    private ReadOnlySpreadsheetLabelStore createStore2() {
        final SpreadsheetLabelStore store = SpreadsheetLabelStores.treeMap();
        store.save(this.labelMapping());
        return ReadOnlySpreadsheetLabelStore.with(store);
    }

    private SpreadsheetLabelMapping labelMapping() {
        return SpreadsheetLabelMapping.with(this.labelName(), this.reference());
    }

    private SpreadsheetLabelName labelName() {
        return SpreadsheetLabelName.with("elephant");
    }

    private SpreadsheetCellReference reference() {
        return SpreadsheetCellReference.parse("A1");
    }

    @Override
    public Class<ReadOnlySpreadsheetLabelStore> type() {
        return ReadOnlySpreadsheetLabelStore.class;
    }
}

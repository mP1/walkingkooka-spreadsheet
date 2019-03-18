package walkingkooka.spreadsheet.store.cellreferences;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.set.Sets;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.TreeMap;

public class TreeMapSpreadsheetCellReferenceStoreTest extends SpreadsheetCellReferenceStoreTestCase<TreeMapSpreadsheetCellReferenceStore> {

    @Test
    public void testSaveAndLoad() {
        final TreeMapSpreadsheetCellReferenceStore store = this.createStore();

        final SpreadsheetCellReference a1 = SpreadsheetCellReference.parse("A1");
        final SpreadsheetCellReference b1 = SpreadsheetCellReference.parse("B1");

        store.saveReferences(a1, Sets.of(b1));
        this.loadAndCheck(store, a1, Sets.of(b1));
    }

    @Test
    public void testSaveAndLoad2() {
        final TreeMapSpreadsheetCellReferenceStore store = this.createStore();

        final SpreadsheetCellReference a1 = SpreadsheetCellReference.parse("A1");
        final SpreadsheetCellReference b1 = SpreadsheetCellReference.parse("B1");
        store.saveReferences(a1, Sets.of(b1));

        final SpreadsheetCellReference c1 = SpreadsheetCellReference.parse("C1");
        store.saveReferences(b1, Sets.of(c1));

        this.loadAndCheck(store, a1, Sets.of(b1));
        this.loadAndCheck(store, b1, Sets.of(c1));
    }

    // addReference.................................................................................

    @Test
    public void testSaveReferencesAddReferenceAndLoad() {
        final TreeMapSpreadsheetCellReferenceStore store = this.createStore();

        final SpreadsheetCellReference a1 = SpreadsheetCellReference.parse("A1");
        final SpreadsheetCellReference b1 = SpreadsheetCellReference.parse("B1");
        store.saveReferences(a1, Sets.of(b1));

        final SpreadsheetCellReference c1 = SpreadsheetCellReference.parse("c1");
        store.addReference(a1, c1);

        this.loadAndCheck(store, a1, Sets.of(b1, c1));
        this.loadFailCheck(store, b1);
        this.loadFailCheck(store, c1);
    }

    @Test
    public void testSaveReferencesAddReferenceAndLoad2() {
        final TreeMapSpreadsheetCellReferenceStore store = this.createStore();

        final SpreadsheetCellReference a1 = SpreadsheetCellReference.parse("A1");
        final SpreadsheetCellReference b1 = SpreadsheetCellReference.parse("B1");
        store.saveReferences(a1, Sets.of(b1));

        final SpreadsheetCellReference c1 = SpreadsheetCellReference.parse("c1");
        store.addReference(a1, c1);

        final SpreadsheetCellReference d1 = SpreadsheetCellReference.parse("d1");
        final SpreadsheetCellReference e1 = SpreadsheetCellReference.parse("e1");
        store.saveReferences(d1, Sets.of(e1, a1, b1, c1));

        this.loadAndCheck(store, a1, Sets.of(b1, c1));
        this.loadAndCheck(store, d1, Sets.of(a1, b1, c1, e1));
    }

    // removeReference.................................................................................

    @Test
    public void testSaveReferencesRemoveReferenceAndLoad() {
        final TreeMapSpreadsheetCellReferenceStore store = this.createStore();

        final SpreadsheetCellReference a1 = SpreadsheetCellReference.parse("A1");
        final SpreadsheetCellReference b1 = SpreadsheetCellReference.parse("B1");
        final SpreadsheetCellReference c1 = SpreadsheetCellReference.parse("c1");
        store.saveReferences(a1, Sets.of(b1, c1));

        store.removeReference(a1, c1);

        this.loadAndCheck(store, a1, Sets.of(b1));
        this.loadFailCheck(store, b1);
        this.loadFailCheck(store, c1);
    }

    // count.................................................................................

    @Test
    public void testSaveReferencesAddReferenceRemoveReferenceAndCount() {
        final TreeMapSpreadsheetCellReferenceStore store = this.createStore();

        final SpreadsheetCellReference a1 = SpreadsheetCellReference.parse("A1");
        final SpreadsheetCellReference b1 = SpreadsheetCellReference.parse("B1");
        final SpreadsheetCellReference c1 = SpreadsheetCellReference.parse("c1");
        store.saveReferences(a1, Sets.of(b1, c1));

        store.removeReference(a1, c1);
        store.addReference(a1, c1);

        this.countAndCheck(store, 1);
    }

    // StoreTesting.................................................................................

    @Override
    public TreeMapSpreadsheetCellReferenceStore createStore() {
        return TreeMapSpreadsheetCellReferenceStore.create();
    }

    @Override
    public Class<TreeMapSpreadsheetCellReferenceStore> type() {
        return TreeMapSpreadsheetCellReferenceStore.class;
    }

    // TypeNameTesting.................................................................................

    @Override
    public String typeNamePrefix() {
        return TreeMap.class.getSimpleName();
    }
}

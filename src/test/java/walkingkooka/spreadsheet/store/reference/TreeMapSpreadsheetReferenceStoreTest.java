package walkingkooka.spreadsheet.store.reference;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.set.Sets;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.TreeMap;

public class TreeMapSpreadsheetReferenceStoreTest extends SpreadsheetReferenceStoreTestCase<TreeMapSpreadsheetReferenceStore<SpreadsheetCellReference>, SpreadsheetCellReference> {

    @Test
    public void testSaveAndLoad() {
        final TreeMapSpreadsheetReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1();;
        final SpreadsheetCellReference b1 = this.b1();

        store.saveReferences(a1, Sets.of(b1));

        this.loadAndCheck(store, a1, b1);
        this.loadReferredAndCheck(store, b1, a1);
        this.loadReferredAndCheck(store, a1);
    }

    @Test
    public void testSaveAndLoad2() {
        final TreeMapSpreadsheetReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1();;
        final SpreadsheetCellReference b1 = this.b1();
        store.saveReferences(a1, Sets.of(b1));

        final SpreadsheetCellReference c1 = this.c1();
        store.saveReferences(b1, Sets.of(c1));

        this.loadAndCheck(store, a1, b1);
        this.loadAndCheck(store, b1, c1);
        this.loadFailCheck(store, c1);

        this.loadReferredAndCheck(store, b1, a1);
        this.loadReferredAndCheck(store, c1, b1);
    }

    @Test
    public void testSaveAndLoad3() {
        final TreeMapSpreadsheetReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1();;
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();

        store.saveReferences(a1, Sets.of(b1, c1));
        store.saveReferences(b1, Sets.of(c1));

        this.loadAndCheck(store, a1, b1, c1);
        this.loadAndCheck(store, b1, c1);
        this.loadFailCheck(store, c1);

        this.loadReferredAndCheck(store, b1, a1);
        this.loadReferredAndCheck(store, c1, a1, b1);
    }

    // addReference.................................................................................

    @Test
    public void testSaveReferencesAddReferenceAndLoad() {
        final TreeMapSpreadsheetReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1();;
        final SpreadsheetCellReference b1 = this.b1();
        store.saveReferences(a1, Sets.of(b1));

        final SpreadsheetCellReference c1 = this.c1();
        store.addReference(a1, c1);

        this.loadAndCheck(store, a1, b1, c1);
        this.loadFailCheck(store, b1);
        this.loadFailCheck(store, c1);

        this.loadReferredAndCheck(store, a1);
        this.loadReferredAndCheck(store, b1, a1);
        this.loadReferredAndCheck(store, c1, a1);
    }

    @Test
    public void testSaveReferencesAddReferenceAndLoad2() {
        final TreeMapSpreadsheetReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1();;
        final SpreadsheetCellReference b1 = this.b1();
        store.saveReferences(a1, Sets.of(b1));

        final SpreadsheetCellReference c1 = this.c1();
        store.addReference(a1, c1);

        final SpreadsheetCellReference d1 = this.d1();
        final SpreadsheetCellReference e1 = this.e1();
        store.saveReferences(d1, Sets.of(e1, a1, b1, c1));

        this.loadAndCheck(store, a1, b1, c1);
        this.loadAndCheck(store, d1, a1, b1, c1, e1);

        this.loadReferredAndCheck(store, a1, d1);
        this.loadReferredAndCheck(store, b1, a1, d1);
        this.loadReferredAndCheck(store, c1, a1, d1);
        this.loadReferredAndCheck(store, e1, d1);
    }

    // removeReference.................................................................................

    @Test
    public void testRemoveReferenceUnknownAndLoad() {
        final TreeMapSpreadsheetReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1();;
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();
        store.saveReferences(a1, Sets.of(b1));

        store.removeReference(a1, c1);

        this.loadAndCheck(store, a1, b1);
        this.loadFailCheck(store, b1);
        this.loadFailCheck(store, c1);

        this.loadReferredAndCheck(store, a1);
        this.loadReferredAndCheck(store, b1, a1);
        this.loadReferredAndCheck(store, c1);
    }

    @Test
    public void testSaveReferencesRemoveReferenceAndLoad() {
        final TreeMapSpreadsheetReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1();;
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();
        store.saveReferences(a1, Sets.of(b1, c1));

        store.removeReference(a1, c1);

        this.loadAndCheck(store, a1, b1);
        this.loadFailCheck(store, b1);
        this.loadFailCheck(store, c1);

        this.loadReferredAndCheck(store, a1);
        this.loadReferredAndCheck(store, b1, a1);
        this.loadReferredAndCheck(store, c1);
    }

    // addReference & removeReference.................................................................................

    @Test
    public void testSaveReferencesAndAddReferenceAndRemoveReference() {
        final TreeMapSpreadsheetReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1();;
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();
        final SpreadsheetCellReference d1 = this.d1();
        final SpreadsheetCellReference e1 = this.e1();

        store.addReference(a1, b1);
        store.addReference(a1, c1);

        store.saveReferences(a1, Sets.of(c1, d1, e1));

        this.loadAndCheck(store, a1, c1, d1, e1);
        this.loadFailCheck(store, b1);
        this.loadFailCheck(store, c1);
        this.loadFailCheck(store, d1);
        this.loadFailCheck(store, e1);

        this.loadReferredAndCheck(store, a1);
        this.loadReferredAndCheck(store, b1, a1);
        this.loadReferredAndCheck(store, c1, a1);
        this.loadReferredAndCheck(store, d1, a1);
        this.loadReferredAndCheck(store, e1, a1);
    }

    @Test
    public void testSaveReferencesAndAddReferenceAndRemoveReference2() {
        final TreeMapSpreadsheetReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1();;
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();
        final SpreadsheetCellReference d1 = this.d1();
        final SpreadsheetCellReference e1 = this.e1();

        store.addReference(a1, b1);
        store.addReference(a1, c1);

        store.saveReferences(a1, Sets.of(c1, d1, e1));
        store.saveReferences(b1, Sets.of(c1, d1, e1));

        store.removeReference(a1, e1);

        this.loadAndCheck(store, a1, c1, d1);
        this.loadAndCheck(store, b1, c1, d1, e1);
        this.loadFailCheck(store, c1);
        this.loadFailCheck(store, d1);
        this.loadFailCheck(store, e1);

        this.loadReferredAndCheck(store, a1);
        this.loadReferredAndCheck(store, b1, a1);
        this.loadReferredAndCheck(store, c1, a1, b1);
        this.loadReferredAndCheck(store, d1, a1, b1);
        this.loadReferredAndCheck(store, e1, b1);
    }

    @Test
    public void testSaveReferencesAndAddReferenceAndRemoveReference3() {
        final TreeMapSpreadsheetReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1();;
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();
        final SpreadsheetCellReference d1 = this.d1();
        final SpreadsheetCellReference e1 = this.e1();

        store.addReference(a1, b1);
        store.addReference(a1, c1);
        store.removeReference(a1, b1);

        this.loadAndCheck(store, a1, c1);
        this.loadFailCheck(store, b1);
        this.loadFailCheck(store, c1);
        this.loadFailCheck(store, d1);
        this.loadFailCheck(store, e1);

        this.loadReferredAndCheck(store, a1);
        this.loadReferredAndCheck(store, b1);
        this.loadReferredAndCheck(store, c1, a1);
        this.loadReferredAndCheck(store, d1);
        this.loadReferredAndCheck(store, e1);
    }

    // count.................................................................................

    @Test
    public void testSaveReferencesAddReferenceRemoveReferenceAndCount() {
        final TreeMapSpreadsheetReferenceStore<SpreadsheetCellReference> store = this.createStore();

        final SpreadsheetCellReference a1 = this.a1();;
        final SpreadsheetCellReference b1 = this.b1();
        final SpreadsheetCellReference c1 = this.c1();
        store.saveReferences(a1, Sets.of(b1, c1));

        store.removeReference(a1, c1);
        store.addReference(a1, c1);

        this.countAndCheck(store, 1);
    }

    // SpreadsheetReferenceStoreTesting.............................................................

    @Override
    public SpreadsheetCellReference id() {
        return this.a1();
    }

    private SpreadsheetCellReference a1() {
        return SpreadsheetCellReference.parse("A1");
    }

    private SpreadsheetCellReference b1() {
        return SpreadsheetCellReference.parse("b1");
    }

    private SpreadsheetCellReference c1() {
        return SpreadsheetCellReference.parse("c1");
    }

    private SpreadsheetCellReference d1() {
        return SpreadsheetCellReference.parse("d1");
    }

    private SpreadsheetCellReference e1() {
        return SpreadsheetCellReference.parse("e1");
    }

    // StoreTesting.................................................................................

    @Override
    public TreeMapSpreadsheetReferenceStore<SpreadsheetCellReference> createStore() {
        return TreeMapSpreadsheetReferenceStore.create();
    }

    @Override
    public Class<TreeMapSpreadsheetReferenceStore<SpreadsheetCellReference>> type() {
        return Cast.to(TreeMapSpreadsheetReferenceStore.class);
    }

    // TypeNameTesting.................................................................................

    @Override
    public String typeNamePrefix() {
        return TreeMap.class.getSimpleName();
    }
}

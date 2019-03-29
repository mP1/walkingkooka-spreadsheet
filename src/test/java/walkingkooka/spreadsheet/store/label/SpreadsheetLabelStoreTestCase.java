package walkingkooka.spreadsheet.store.label;

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.SpreadsheetLabelMapping;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetReferenceKind;

import static org.junit.jupiter.api.Assertions.assertSame;

public abstract class SpreadsheetLabelStoreTestCase<S extends SpreadsheetLabelStore> implements SpreadsheetLabelStoreTesting<S> {

    final static SpreadsheetCellReference REFERENCE = SpreadsheetReferenceKind.ABSOLUTE.column(1).setRow(SpreadsheetReferenceKind.ABSOLUTE.row(2));
    final static SpreadsheetLabelName LABEL = SpreadsheetLabelName.with("label123");

    @Test
    public final void testLoadUnknownFails() {
        this.loadFailCheck(LABEL);
    }

    @Test
    public void testSaveAndLoad() {
        final S store = this.createStore();

        final SpreadsheetLabelMapping mapping = SpreadsheetLabelMapping.with(LABEL, REFERENCE);
        store.save(mapping);

        assertSame(mapping, store.loadOrFail(LABEL));
    }

    @Test
    public void testSaveDeleteLoad() {
        final S store = this.createStore();

        final SpreadsheetLabelMapping mapping = SpreadsheetLabelMapping.with(LABEL, REFERENCE);
        store.save(mapping);
        store.delete(mapping.label());

        this.loadFailCheck(store, LABEL);
    }

    @Test
    public void testCount() {
        final S store = this.createStore();

        final SpreadsheetLabelMapping a = this.mapping("a", 1, 2);
        final SpreadsheetLabelMapping b = this.mapping("b", 4, 8);
        final SpreadsheetLabelMapping c = this.mapping("c", 88, 99);

        store.save(a);
        store.save(b);
        store.save(c);

        this.countAndCheck(store, 3);
    }

    @Test
    public void testIds() {
        final S store = this.createStore();

        final SpreadsheetLabelMapping a = this.mapping("a", 1, 2);
        final SpreadsheetLabelMapping b = this.mapping("b", 4, 8);
        final SpreadsheetLabelMapping c = this.mapping("c", 88, 99);

        store.save(a);
        store.save(b);
        store.save(c);

        this.idsAndCheck(store, 0, 3, a.id(), b.id(), c.id());
    }

    @Test
    public void testIdsWindow() {
        final S store = this.createStore();

        final SpreadsheetLabelMapping a = this.mapping("a", 1, 2);
        final SpreadsheetLabelMapping b = this.mapping("b", 4, 8);
        final SpreadsheetLabelMapping c = this.mapping("c", 77, 88);
        final SpreadsheetLabelMapping d = this.mapping("d", 88, 99);

        store.save(a);
        store.save(b);
        store.save(c);
        store.save(d);

        this.idsAndCheck(store, 1, 2, b.id(), c.id());
    }

    @Test
    public void testValues() {
        final S store = this.createStore();

        final SpreadsheetLabelMapping a = this.mapping("a", 1, 2);
        final SpreadsheetLabelMapping b = this.mapping("b", 4, 8);
        final SpreadsheetLabelMapping c = this.mapping("c", 88, 99);

        store.save(a);
        store.save(b);
        store.save(c);

        this.valuesAndCheck(store, a.id(), 3, a, b, c);
    }

    @Test
    public void testValuesWindow() {
        final S store = this.createStore();

        final SpreadsheetLabelMapping a = this.mapping("a", 1, 2);
        final SpreadsheetLabelMapping b = this.mapping("b", 4, 8);
        final SpreadsheetLabelMapping c = this.mapping("c", 77, 88);
        final SpreadsheetLabelMapping d = this.mapping("d", 88, 99);

        store.save(a);
        store.save(b);
        store.save(c);
        store.save(d);

        this.valuesAndCheck(store, b.id(), 2, b, c);
    }

    final SpreadsheetCellReference cell(final int column, final int row) {
        return SpreadsheetReferenceKind.ABSOLUTE.column(column)
                .setRow(SpreadsheetReferenceKind.ABSOLUTE.row(row));
    }

    final SpreadsheetLabelMapping mapping(final String label, final int column, final int row) {
        return SpreadsheetLabelMapping.with(SpreadsheetLabelName.with(label), cell(column, row));
    }

    public SpreadsheetLabelName id() {
        return SpreadsheetLabelName.with("abc123456789");
    }
}

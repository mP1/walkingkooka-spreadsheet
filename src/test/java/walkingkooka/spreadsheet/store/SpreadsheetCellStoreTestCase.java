package walkingkooka.spreadsheet.store;

import org.junit.Test;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.test.PackagePrivateClassTestCase;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetReferenceKind;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public abstract class SpreadsheetCellStoreTestCase<S extends SpreadsheetCellStore> extends PackagePrivateClassTestCase<S> {

    final static SpreadsheetCellReference REFERENCE = SpreadsheetReferenceKind.ABSOLUTE.column(1).setRow(SpreadsheetReferenceKind.ABSOLUTE.row(2));
    final static SpreadsheetLabelName LABEL = SpreadsheetLabelName.with("label123");

    @Test(expected = NullPointerException.class)
    public final void testLoadNullCellFails() {
        this.createSpreadsheetCellStore().load(null);
    }

    @Test(expected = NullPointerException.class)
    public final void testSaveNullFails() {
        this.createSpreadsheetCellStore().save(null);
    }

    @Test(expected = NullPointerException.class)
    public final void testDeleteNullFails() {
        this.createSpreadsheetCellStore().delete(null);
    }

    abstract S createSpreadsheetCellStore();

    final SpreadsheetCell loadOrFail(final SpreadsheetCellStore store,
                                     final SpreadsheetCellReference reference) {
        final Optional<SpreadsheetCell> cell = store.load(reference);
        if(!cell.isPresent()) {
            fail("Loading " + reference + " should have succeeded");
        }
        return cell.get();
    }

    final void loadFailCheck(final SpreadsheetCellReference reference) {
        this.loadFailCheck(this.createSpreadsheetCellStore(), reference);
    }

    final void loadFailCheck(final SpreadsheetCellStore store,
                                 final SpreadsheetCellReference reference) {
        final Optional<SpreadsheetCell> cell = store.load(reference);
        assertEquals("Expected reference " + reference + " to fail", Optional.empty(), cell);
    }

    final SpreadsheetCellReference cellReference(final int column, final int row) {
        return SpreadsheetCellReference.with(SpreadsheetReferenceKind.ABSOLUTE.column(column),
                SpreadsheetReferenceKind.ABSOLUTE.row(row));
    }
}

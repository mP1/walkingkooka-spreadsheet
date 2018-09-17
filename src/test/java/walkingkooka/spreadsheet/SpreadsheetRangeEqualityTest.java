package walkingkooka.spreadsheet;

import org.junit.Test;
import walkingkooka.test.HashCodeEqualsDefinedEqualityTestCase;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetReferenceKind;

public final class SpreadsheetRangeEqualityTest extends HashCodeEqualsDefinedEqualityTestCase<SpreadsheetRange> {

    private final static int COLUMN1 = 11;
    private final static int ROW1 = 22;

    private final static int COLUMN2 = 33;
    private final static int ROW2 = 44;

    @Test
    public void testDifferentBegin() {
        this.checkNotEquals(this.range(9, ROW1, COLUMN2, ROW2));
    }

    @Test
    public void testDifferentEnd() {
        this.checkNotEquals(this.range(COLUMN1, ROW1, COLUMN2, 99));
    }

    @Override
    protected SpreadsheetRange createObject() {
        return this.range(COLUMN1, ROW1, COLUMN2, ROW2);
    }

    private SpreadsheetRange range(final int column1, final int row1, final int column2, final int row2) {
        return SpreadsheetRange.with(this.cell(column1, row1), this.cell(column2, row2));
    }

    private SpreadsheetCellReference cell(final int column, final int row) {
        return SpreadsheetReferenceKind.ABSOLUTE.column(column).setRow(SpreadsheetReferenceKind.ABSOLUTE.row(row));
    }
}

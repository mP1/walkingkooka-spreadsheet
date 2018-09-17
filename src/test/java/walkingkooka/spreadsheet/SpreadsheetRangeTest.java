package walkingkooka.spreadsheet;

import org.junit.Test;
import walkingkooka.test.PublicClassTestCase;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetReferenceKind;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public final class SpreadsheetRangeTest extends PublicClassTestCase<SpreadsheetRange> {

    private final static int COLUMN1 = 10;
    private final static int ROW1 = 11;
    private final static int COLUMN2 = 20;
    private final static int ROW2 = 21;

    @Test(expected = NullPointerException.class)
    public void testWithNullBeginFails() {
        SpreadsheetRange.with(null, this.cell());
    }

    @Test(expected = NullPointerException.class)
    public void testWithNullEndFails() {
        SpreadsheetRange.with(this.cell(), null);
    }

    @Test
    public void testWith() {
        final SpreadsheetCellReference begin =this.cell(1, 2);
        final SpreadsheetCellReference end =this.cell(3, 4);

        final SpreadsheetRange range = SpreadsheetRange.with(begin, end);
        assertSame("begin", begin, range.begin());
        assertSame("end", end, range.end());
    }

    @Test
    public void testWith2() {
        final int column1 = 99;
        final int row1 = 2;
        final int column2 = 3;
        final int row2 = 4;

        final SpreadsheetRange range = this.range(column1, row1, column2, row2);
        this.check(range, column2, row1, column1, row2);
    }

    @Test
    public void testWith3() {
        final int column1 = 1;
        final int row1 = 99;
        final int column2 = 3;
        final int row2 = 4;

        final SpreadsheetRange range = this.range(column1, row1, column2, row2);
        this.check(range, column1, row2, column2, row1);
    }

    @Test
    public void testWith4() {
        final int column1 = 88;
        final int row1 = 99;
        final int column2 = 3;
        final int row2 = 4;

        final SpreadsheetRange range = this.range(column1, row1, column2, row2);
        this.check(range, column2, row2, column1, row1);
    }

    //helper.................................................................................................

    private SpreadsheetRange range() {
        return this.range(this.begin(), this.end());
    }

    private SpreadsheetCellReference begin() {
        return this.cell(COLUMN1, ROW1);
    }

    private SpreadsheetCellReference end() {
        return this.cell(COLUMN2, ROW2);
    }

    private SpreadsheetRange range(final int column1, final int row1, final int column2, final int row2) {
        return SpreadsheetRange.with(this.cell(column1, row1), this.cell(column2, row2));
    }

    private SpreadsheetRange range(final SpreadsheetCellReference begin, final SpreadsheetCellReference end) {
        return SpreadsheetRange.with(begin, end);
    }

    private SpreadsheetCellReference cell() {
        return this.cell(99, 88);
    }

    private SpreadsheetCellReference cell(final int column, final int row) {
        return SpreadsheetReferenceKind.ABSOLUTE.column(column).setRow(SpreadsheetReferenceKind.ABSOLUTE.row(row));
    }

    private void check(final SpreadsheetRange range, final int column1, final int row1, final int column2, final int row2) {
        this.checkBegin(range, column1, row1);
        this.checkEnd(range, column2, row2);
    }

    private void checkBegin(final SpreadsheetRange range, final int column, final int row) {
        this.checkBegin(range, this.cell(column, row));
    }

    private void checkBegin(final SpreadsheetRange range, final SpreadsheetCellReference begin) {
        assertEquals("range begin=" + range, begin, range.begin());
    }

    private void checkEnd(final SpreadsheetRange range, final int column, final int row) {
        this.checkEnd(range, this.cell(column, row));
    }

    private void checkEnd(final SpreadsheetRange range, final SpreadsheetCellReference end) {
        assertEquals("range end="+ range, end, range.end());
    }
    
    @Override
    protected Class<SpreadsheetRange> type() {
        return SpreadsheetRange.class;
    }
}

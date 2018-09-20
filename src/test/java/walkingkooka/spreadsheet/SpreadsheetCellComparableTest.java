package walkingkooka.spreadsheet;

import org.junit.Test;
import walkingkooka.compare.ComparableTestCase;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetReferenceKind;

public final class SpreadsheetCellComparableTest extends ComparableTestCase<SpreadsheetCell> {

    private final static int COLUMN = 1;
    private final static int ROW = 20;
    private final static String FORMULA = "3+4";

    @Test
    public void testDifferentFormulaEquals() {
        this.checkNotEquals(this.createComparable(COLUMN, ROW, FORMULA + "99"));
    }

    @Test
    public void testDifferentColumn() {
        this.compareToAndCheckLess(this.createComparable(99, ROW, FORMULA));
    }

    @Test
    public void testDifferentRow() {
        this.compareToAndCheckLess(this.createComparable(COLUMN, 99, FORMULA));
    }

    @Override
    protected SpreadsheetCell createComparable() {
        return this.createComparable(COLUMN, ROW, FORMULA);
    }

    private SpreadsheetCell createComparable(final int column, final int row, final String formula) {
        return SpreadsheetCell.with(this.reference(column, row), SpreadsheetFormula.with(formula));
    }

    private SpreadsheetCellReference reference(final int column, final int row) {
        return SpreadsheetCellReference.with(SpreadsheetReferenceKind.ABSOLUTE.column(column), SpreadsheetReferenceKind.ABSOLUTE.row(row));
    }
}

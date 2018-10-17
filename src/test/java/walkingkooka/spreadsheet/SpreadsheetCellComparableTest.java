package walkingkooka.spreadsheet;

import org.junit.Test;
import walkingkooka.compare.ComparableTestCase;
import walkingkooka.spreadsheet.style.SpreadsheetCellStyle;
import walkingkooka.spreadsheet.style.SpreadsheetTextStyle;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetReferenceKind;

import java.util.Optional;

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

    @Test
    public void testDifferentStyle() {
        this.compareToAndCheckEqual(this.createComparable().setStyle(SpreadsheetCellStyle.EMPTY.setText(SpreadsheetTextStyle.EMPTY.setItalics(SpreadsheetTextStyle.ITALICS))));
    }

    @Test
    public void testDifferentFormat() {
        this.compareToAndCheckEqual(this.createComparable().setFormat(Optional.of(SpreadsheetCellFormat.with("different-pattern", SpreadsheetCellFormat.NO_FORMATTER))));
    }

    @Test
    public void testDifferentFormatted() {
        this.compareToAndCheckEqual(this.createComparable().setFormatted(Optional.of(SpreadsheetFormattedCell.with("different-formatted", this.style()))));
    }

    @Override
    protected SpreadsheetCell createComparable() {
        return this.createComparable(COLUMN, ROW, FORMULA);
    }

    private SpreadsheetCell createComparable(final int column, final int row, final String formula) {
        return SpreadsheetCell.with(this.reference(column, row),
                SpreadsheetFormula.with(formula),
                this.style(),
                this.format(),
                this.formatted());
    }

    private SpreadsheetCellReference reference(final int column, final int row) {
        return SpreadsheetCellReference.with(SpreadsheetReferenceKind.ABSOLUTE.column(column), SpreadsheetReferenceKind.ABSOLUTE.row(row));
    }

    private SpreadsheetCellStyle style() {
        return SpreadsheetCellStyle.EMPTY.setText(SpreadsheetTextStyle.EMPTY.setBold(SpreadsheetTextStyle.BOLD));
    }

    private Optional<SpreadsheetCellFormat> format() {
        return SpreadsheetCell.NO_FORMAT;
    }

    private Optional<SpreadsheetFormattedCell> formatted() {
        return SpreadsheetCell.NO_FORMATTED_CELL;
    }

    @Override
    protected boolean compareAndEqualsMatch() {
        return false; // comparing does not include all properties, so compareTo == 0 <> equals
    }
}

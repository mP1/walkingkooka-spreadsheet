package walkingkooka.spreadsheet;

import org.junit.Test;
import walkingkooka.test.HashCodeEqualsDefinedEqualityTestCase;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetReferenceKind;
import walkingkooka.tree.expression.ExpressionNode;

import java.util.Optional;

public final class SpreadsheetCellEqualityTest extends HashCodeEqualsDefinedEqualityTestCase<SpreadsheetCell> {

    @Test
    public void testDifferentReference() {
        this.checkNotEquals(this.cell(reference(88, 99), this.formula()));
    }

    @Test
    public void testDifferentFormula() {
        this.checkNotEquals(this.cell(reference(),this.formula("=99+999")));
    }

    @Override
    protected SpreadsheetCell createObject() {
        return this.cell(reference(), this.formula());
    }

    private SpreadsheetCell cell(final SpreadsheetCellReference reference,
                                 final SpreadsheetFormula formula) {
        return SpreadsheetCell.with(reference, formula);
    }

    private static SpreadsheetCellReference reference() {
        return reference(12, 34);
    }

    private static SpreadsheetCellReference reference(final int column, final int row) {
        return SpreadsheetCellReference.with(SpreadsheetReferenceKind.ABSOLUTE.column(column), SpreadsheetReferenceKind.ABSOLUTE.row(row));
    }

    private SpreadsheetFormula formula(final String text) {
        return SpreadsheetFormula.with(text);
    }

    private SpreadsheetFormula formula() {
        return this.formula("=1+2");
    }
}

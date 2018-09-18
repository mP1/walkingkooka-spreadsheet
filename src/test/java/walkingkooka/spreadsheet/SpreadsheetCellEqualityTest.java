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
        this.checkNotEquals(this.cell(reference(88, 99), this.formula(), this.expression(), this.value(), this.error()));
    }

    @Test
    public void testDifferentFormula() {
        this.checkNotEquals(this.cell(reference(),this.formula("=99+999"), this.expression(), this.value(), this.error()));
    }

    @Test
    public void testDifferentExpression() {
        checkNotEquals(this.cell(reference(), this.formula(), this.expression("44"), this.value(), this.error()));
    }

    @Test
    public void testDifferentValue() {
        checkNotEquals(this.cell(reference(), this.formula(), this.expression(), this.value(), SpreadsheetCell.NO_ERROR),
                this.cell(reference(), this.formula(), this.expression(), this.value("different-value"), SpreadsheetCell.NO_ERROR));
    }

    @Test
    public void testDifferentError() {
        checkNotEquals(this.cell(reference(), this.formula(), this.expression(), this.value(), this.error("different error message")));
    }

    @Override
    protected SpreadsheetCell createObject() {
        return this.cell(reference(), this.formula(), this.expression(), this.value(), this.error());
    }

    private SpreadsheetCell cell(final SpreadsheetCellReference reference,
                                 final SpreadsheetFormula formula,
                                 final Optional<ExpressionNode> expression,
                                 final Optional<Object> value,
                                 final Optional<SpreadsheetError> error) {
        return SpreadsheetCell.with(reference, formula)
                .setExpression(expression)
                .setValue(value)
                .setError(error);
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

    private Optional<ExpressionNode> expression(final String text) {
        return Optional.of(ExpressionNode.text(text));
    }

    private Optional<ExpressionNode> expression() {
        return this.expression("=1+2");
    }

    private Optional<Object> value(final Object text) {
        return Optional.of(text);
    }

    private Optional<Object> value() {
        return this.value(3);
    }

    private Optional<SpreadsheetError> error(final String text) {
        return Optional.of(SpreadsheetError.with(text));
    }

    private Optional<SpreadsheetError> error() {
        return this.error("Error message #1");
    }
}

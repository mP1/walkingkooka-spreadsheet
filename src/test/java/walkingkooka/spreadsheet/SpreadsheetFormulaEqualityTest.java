package walkingkooka.spreadsheet;

import org.junit.Test;
import walkingkooka.test.HashCodeEqualsDefinedEqualityTestCase;
import walkingkooka.tree.expression.ExpressionNode;

import java.util.Optional;

public final class SpreadsheetFormulaEqualityTest extends HashCodeEqualsDefinedEqualityTestCase<SpreadsheetFormula> {

    private final static String TEXT = "a+2";

    @Test
    public void testDifferentText() {
        checkNotEquals(this.createFormula("99+88", this.expression(), this.value(), this.error()));
    }

    @Test
    public void testDifferentExpression() {
        checkNotEquals(this.createFormula(TEXT, this.expression("44"), this.value(), this.error()));
    }

    @Test
    public void testDifferentValue() {
        checkNotEquals(this.createFormula(TEXT, this.expression(), this.value(), SpreadsheetFormula.NO_ERROR),
                this.createFormula(TEXT, this.expression(), this.value("different-value"), SpreadsheetFormula.NO_ERROR));
    }

    @Test
    public void testDifferentError() {
        checkNotEquals(this.createFormula(TEXT, this.expression(), this.value(), this.error("different error message")));
    }
    
    @Test
    public void testDifferentSpreadsheetFormula() {
        this.checkNotEquals(SpreadsheetFormula.with("different"));
    }

    @Override
    protected SpreadsheetFormula createObject() {
        return SpreadsheetFormula.with("text");
    }

    private SpreadsheetFormula createFormula(final String formula,
                                             final Optional<ExpressionNode> expression,
                                             final Optional<Object> value,
                                             final Optional<SpreadsheetError> error) {
        return SpreadsheetFormula.with(formula)
                .setExpression(expression)
                .setValue(value)
                .setError(error);
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

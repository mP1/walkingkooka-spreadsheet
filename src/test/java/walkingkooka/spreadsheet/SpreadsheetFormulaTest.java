package walkingkooka.spreadsheet;

import org.junit.Test;
import walkingkooka.test.PublicClassTestCase;
import walkingkooka.tree.expression.ExpressionNode;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;


public final class SpreadsheetFormulaTest extends PublicClassTestCase<SpreadsheetFormula> {

    private final static String TEXT = "a+2";
    private final static String EXPRESSION = "1+2";
    private final static Object VALUE = 3;
    private final static String ERROR = "Message #1";

    private final static String DIFFERENT_TEXT = "99+99";

    @Test(expected = NullPointerException.class)
    public void testWithNullExpressionFails() {
        SpreadsheetFormula.with(null);
    }

    @Test
    public void testWith() {
        final SpreadsheetFormula formula = this.create();
        this.checkText(formula, TEXT);
        this.checkExpressionAbsent(formula);
        this.checkValueAbsent(formula);
        this.checkErrorAbsent(formula);
    }

    @Test
    public void testWithEmpty() {
        final String text = "";
        final SpreadsheetFormula formula = SpreadsheetFormula.with(text);
        this.checkText(formula, text);
    }

    @Test(expected = NullPointerException.class)
    public void testSetTextNullFails() {
        this.create().setText(null);
    }

    @Test
    public void testSetTextSame() {
        final SpreadsheetFormula formula = this.create();
        assertSame(formula, formula.setText(TEXT));
    }

    @Test
    public void testSetTextDifferent() {
        this.setTextAndCheck("different");
    }

    @Test
    public void testSetTextDifferentEmpty() {
        this.setTextAndCheck("");
    }

    private void setTextAndCheck(final String differentText) {
        final SpreadsheetFormula formula = this.create();
        final SpreadsheetFormula different = formula.setText(differentText);
        assertNotSame(formula, different);
        this.checkText(different, differentText);
    }

    // SetFormula.....................................................................................................

    @Test(expected = NullPointerException.class)
    public void testSetFormulaNullFails() {
        this.create().setText(null);
    }

    @Test
    public void testSetFormulaSame() {
        final SpreadsheetFormula formula = this.create();
        assertSame(formula, formula.setText(formula.text()));
    }

    @Test
    public void testSetFormulaDifferent() {
        final SpreadsheetFormula formula = this.create();
        final String differentText = "different";
        final SpreadsheetFormula different = formula.setText(differentText);
        assertNotSame(formula, different);
        
        this.checkText(different, differentText);
        this.checkExpressionAbsent(different);
        this.checkValueAbsent(different);
        this.checkErrorAbsent(different);
    }

    @Test
    public void testSetFormulaAfterSetExpression() {
        final SpreadsheetFormula formula = this.create()
                .setExpression(this.expression());
        final SpreadsheetFormula different = formula.setText(DIFFERENT_TEXT);
        assertNotSame(formula, different);
        
        this.checkText(different, DIFFERENT_TEXT);
        this.checkExpressionAbsent(different);
        this.checkValueAbsent(different);
        this.checkErrorAbsent(different);
    }

    @Test
    public void testSetFormulaAfterSetExpressionSetValue() {
        final SpreadsheetFormula formula = this.create()
                .setExpression(this.expression())
                .setValue(this.value());
        final SpreadsheetFormula different = formula.setText(DIFFERENT_TEXT);
        assertNotSame(formula, different);
        
        this.checkText(different, DIFFERENT_TEXT);
        this.checkExpressionAbsent(different);
        this.checkValueAbsent(different);
        this.checkErrorAbsent(different);
    }

    // SetExpression.....................................................................................................

    @Test(expected = NullPointerException.class)
    public void testSetExpressionNullFails() {
        this.create().setExpression(null);
    }

    @Test
    public void testSetExpressionSame() {
        final SpreadsheetFormula formula = this.create();
        assertSame(formula, formula.setExpression(formula.expression()));
    }

    @Test
    public void testSetExpressionDifferent() {
        final SpreadsheetFormula formula = this.create();
        final Optional<ExpressionNode> differentExpression = Optional.of(ExpressionNode.text("different!"));
        final SpreadsheetFormula different = formula.setExpression(differentExpression);
        assertNotSame(formula, different);
        
        this.checkText(different, TEXT);
        this.checkExpression(different, differentExpression);
        this.checkValueAbsent(different);
        this.checkErrorAbsent(different);
    }

    @Test
    public void testSetExpressionDifferentAndClear() {
        final SpreadsheetFormula formula = this.create()
                .setExpression(this.expression());
        final Optional<ExpressionNode> differentExpression = SpreadsheetFormula.NO_EXPRESSION;
        final SpreadsheetFormula different = formula.setExpression(differentExpression);
        assertNotSame(formula, different);
        
        this.checkText(different, TEXT);
        this.checkExpressionAbsent(different);
        this.checkValueAbsent(different);
        this.checkErrorAbsent(different);
    }

    @Test
    public void testSetExpressionDifferentAfterSetValue() {
        final SpreadsheetFormula formula = this.create()
                .setExpression(this.expression())
                .setValue(this.value());

        final Optional<ExpressionNode> differentExpression = Optional.of(ExpressionNode.text("different!"));
        final SpreadsheetFormula different = formula.setExpression(differentExpression);
        assertNotSame(formula, different);
        
        this.checkText(different, TEXT);
        this.checkExpression(different, differentExpression);
        this.checkValueAbsent(different);
        this.checkErrorAbsent(different);
    }

    // SetError.....................................................................................................

    @Test(expected = NullPointerException.class)
    public void testSetErrorNullFails() {
        this.create().setError(null);
    }

    @Test
    public void testSetErrorSame() {
        final SpreadsheetFormula formula = this.create();
        assertSame(formula, formula.setError(formula.error()));
    }

    @Test
    public void testSetErrorDifferent() {
        final SpreadsheetFormula formula = this.create();
        final Optional<SpreadsheetError> differentError = this.error("different");
        final SpreadsheetFormula different = formula.setError(differentError);
        assertNotSame(formula, different);
        
        this.checkText(different, TEXT);
        this.checkExpressionAbsent(different);
        this.checkValueAbsent(different);
        this.checkError(different, differentError);
    }

    @Test
    public void testSetErrorDifferent2() {
        final SpreadsheetFormula formula = this.create()
                .setError(this.error());
        final Optional<SpreadsheetError> differentError = this.error("different");
        final SpreadsheetFormula different = formula.setError(differentError);
        assertNotSame(formula, different);
        
        this.checkText(different, TEXT);
        this.checkExpressionAbsent(different);
        this.checkValueAbsent(different);
        this.checkError(different, differentError);
    }

    @Test
    public void testSetErrorDifferentAfterSetValue() {
        final SpreadsheetFormula formula = this.create()
                .setValue(this.value());
        final Optional<SpreadsheetError> differentError = this.error("different");
        final SpreadsheetFormula different = formula.setError(differentError);
        assertNotSame(formula, different);
        
        this.checkText(different, TEXT);
        this.checkExpressionAbsent(different);
        this.checkValueAbsent(different);
        this.checkError(different, differentError);
    }

    @Test
    public void testToStringWithValue() {
        assertEquals(TEXT + " (=" + VALUE + ")", this.create().setValue(this.value(VALUE)).toString());
    }

    @Test
    public void testToStringWithError() {
        assertEquals(TEXT + " (" + ERROR + ")", this.create().setError(this.error(ERROR)).toString());
    }

    @Test
    public void testToStringWithoutError() {
        assertEquals(TEXT, this.create().toString());
    }

    private SpreadsheetFormula create() {
        return SpreadsheetFormula.with(TEXT);
    }

    private void checkText(final SpreadsheetFormula formula, final String text) {
        assertEquals("text(ExpressionNode)", text, formula.text());
    }

    private Optional<ExpressionNode> expression() {
        return this.expression(EXPRESSION);
    }

    private Optional<ExpressionNode> expression(final String text) {
        return Optional.of(ExpressionNode.text(text));
    }

    private void checkExpression(final SpreadsheetFormula formula, final String expression) {
        this.checkExpression(formula, this.expression(expression));
    }

    private void checkExpression(final SpreadsheetFormula formula, final Optional<ExpressionNode> expression) {
        assertEquals("expression", expression, formula.expression());
    }

    private void checkExpressionAbsent(final SpreadsheetFormula formula) {
        this.checkExpression(formula, SpreadsheetFormula.NO_EXPRESSION);
    }

    private Optional<Object> value() {
        return this.value(VALUE);
    }

    private Optional<Object> value(final Object value) {
        return Optional.of(value);
    }

    private void checkValue(final SpreadsheetFormula formula, final Optional<Object> value) {
        assertEquals("value", value, formula.value());
    }

    private void checkValueAbsent(final SpreadsheetFormula formula) {
        this.checkValue(formula, SpreadsheetFormula.NO_VALUE);
    }

    private Optional<SpreadsheetError> error() {
        return this.error(ERROR);
    }

    private Optional<SpreadsheetError> error(final String error) {
        return Optional.of(SpreadsheetError.with(error));
    }

    private void checkErrorAbsent(final SpreadsheetFormula formula) {
        this.checkError(formula, SpreadsheetFormula.NO_ERROR);
    }

    private void checkError(final SpreadsheetFormula formula, final Optional<SpreadsheetError> error) {
        assertEquals("formula", error, formula.error());
    }
    

    @Override
    protected Class<SpreadsheetFormula> type() {
        return SpreadsheetFormula.class;
    }
}

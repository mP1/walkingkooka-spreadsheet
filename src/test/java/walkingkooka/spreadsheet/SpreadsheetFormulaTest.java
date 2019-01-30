package walkingkooka.spreadsheet;

import org.junit.Test;
import walkingkooka.test.ClassTestCase;
import walkingkooka.test.HashCodeEqualsDefinedTesting;
import walkingkooka.tree.expression.ExpressionNode;
import walkingkooka.tree.json.HasJsonNodeTesting;
import walkingkooka.type.MemberVisibility;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;


public final class SpreadsheetFormulaTest extends ClassTestCase<SpreadsheetFormula>
        implements HashCodeEqualsDefinedTesting<SpreadsheetFormula>,
        HasJsonNodeTesting<SpreadsheetFormula> {

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
        final SpreadsheetFormula formula = this.createObject();
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
        this.createObject().setText(null);
    }

    @Test
    public void testSetTextSame() {
        final SpreadsheetFormula formula = this.createObject();
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
        final SpreadsheetFormula formula = this.createObject();
        final SpreadsheetFormula different = formula.setText(differentText);
        assertNotSame(formula, different);
        this.checkText(different, differentText);
    }

    // SetFormula.....................................................................................................

    @Test(expected = NullPointerException.class)
    public void testSetFormulaNullFails() {
        this.createObject().setText(null);
    }

    @Test
    public void testSetFormulaSame() {
        final SpreadsheetFormula formula = this.createObject();
        assertSame(formula, formula.setText(formula.text()));
    }

    @Test
    public void testSetFormulaDifferent() {
        final SpreadsheetFormula formula = this.createObject();
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
        final SpreadsheetFormula formula = this.createObject()
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
        final SpreadsheetFormula formula = this.createObject()
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
        this.createObject().setExpression(null);
    }

    @Test
    public void testSetExpressionSame() {
        final SpreadsheetFormula formula = this.createObject();
        assertSame(formula, formula.setExpression(formula.expression()));
    }

    @Test
    public void testSetExpressionDifferent() {
        final SpreadsheetFormula formula = this.createObject();
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
        final SpreadsheetFormula formula = this.createObject()
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
        final SpreadsheetFormula formula = this.createObject()
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
        this.createObject().setError(null);
    }

    @Test
    public void testSetErrorSame() {
        final SpreadsheetFormula formula = this.createObject();
        assertSame(formula, formula.setError(formula.error()));
    }

    @Test
    public void testSetErrorDifferent() {
        final SpreadsheetFormula formula = this.createObject();
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
        final SpreadsheetFormula formula = this.createObject()
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
        final SpreadsheetFormula formula = this.createObject()
                .setValue(this.value());
        final Optional<SpreadsheetError> differentError = this.error("different");
        final SpreadsheetFormula different = formula.setError(differentError);
        assertNotSame(formula, different);
        
        this.checkText(different, TEXT);
        this.checkExpressionAbsent(different);
        this.checkValueAbsent(different);
        this.checkError(different, differentError);
    }

    // equals.......................................................................................................


    @Test
    public void testEqualsDifferentText() {
        checkNotEquals(this.createFormula("99+88", this.expression(), this.value(), this.error()));
    }

    @Test
    public void testEqualsDifferentExpression() {
        checkNotEquals(this.createFormula(TEXT, this.expression("44"), this.value(), this.error()));
    }

    @Test
    public void testEqualsDifferentValue() {
        checkNotEquals(this.createFormula(TEXT, this.expression(), this.value(), SpreadsheetFormula.NO_ERROR),
                this.createFormula(TEXT, this.expression(), this.value("different-value"), SpreadsheetFormula.NO_ERROR));
    }

    @Test
    public void testEqualsDifferentError() {
        checkNotEquals(this.createFormula(TEXT, this.expression(), this.value(), this.error("different error message")));
    }

    @Test
    public void testEqualsDifferentSpreadsheetFormula() {
        this.checkNotEquals(SpreadsheetFormula.with("different"));
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

    // toJsonNode...............................................................................................

    @Test
    public void testToJsonNodeText() {
        this.toJsonNodeAndCheck(SpreadsheetFormula.with(TEXT),
                "{ \"text\": \"a+2\"}");
    }

    @Test
    public void testToJsonNodeTextAndExpression() {
        this.toJsonNodeAndCheck(SpreadsheetFormula.with(TEXT)
                        .setExpression(this.expression()),
                "{ \"text\": \"a+2\"}");
    }

    @Test
    public void testToJsonNodeTextAndValue() {
        this.toJsonNodeAndCheck(SpreadsheetFormula.with(TEXT)
                        .setValue(Optional.of(123)),
                "{ \"text\": \"a+2\", \"value\": 123}");
    }

    @Test
    public void testToJsonNodeTextAndValue2() {
        this.toJsonNodeAndCheck(SpreadsheetFormula.with(TEXT)
                        .setValue(Optional.of("abc123")),
                "{ \"text\": \"a+2\", \"value\": \"abc123\"}");
    }

    @Test
    public void testToJsonNodeTextAndError() {
        this.toJsonNodeAndCheck(SpreadsheetFormula.with(TEXT)
                        .setError(Optional.of(SpreadsheetError.with("error123"))),
                "{ \"text\": \"a+2\", \"error\": \"error123\"}");
    }

    // toString...............................................................................................

    @Test
    public void testToStringWithValue() {
        assertEquals(TEXT + " (=" + VALUE + ")", this.createObject().setValue(this.value(VALUE)).toString());
    }

    @Test
    public void testToStringWithError() {
        assertEquals(TEXT + " (" + ERROR + ")", this.createObject().setError(this.error(ERROR)).toString());
    }

    @Test
    public void testToStringWithoutError() {
        assertEquals(TEXT, this.createObject().toString());
    }
    
    @Override
    public SpreadsheetFormula createObject() {
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

    @Override
    protected MemberVisibility typeVisibility() {
        return MemberVisibility.PUBLIC;
    }
}

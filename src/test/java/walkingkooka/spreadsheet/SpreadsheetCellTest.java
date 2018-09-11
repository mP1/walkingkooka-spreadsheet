package walkingkooka.spreadsheet;

import org.junit.Test;
import walkingkooka.test.PublicClassTestCase;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetReferenceKind;
import walkingkooka.tree.expression.ExpressionNode;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;


public final class SpreadsheetCellTest extends PublicClassTestCase<SpreadsheetCell> {

    private final static SpreadsheetCellReference REFERENCE = reference(12, 34);
    private final static String FORMULA = "=1+2";
    private final static String EXPRESSION = "1+2";
    private final static Object VALUE = 3;
    private final static String ERROR = "Message #1";

    @Test(expected = NullPointerException.class)
    public void testWithNullReferenceFails() {
        SpreadsheetCell.with(null, this.formula());
    }

    @Test(expected = NullPointerException.class)
    public void testWithNullFormulaFails() {
        SpreadsheetCell.with(REFERENCE, null);
    }

    @Test
    public void testWith() {
        final SpreadsheetCell cell = this.createCell();

        this.checkReference(cell, REFERENCE);
        this.checkFormula(cell, this.formula());
        this.checkExpressionAbsent(cell);
        this.checkValueAbsent(cell);
        this.checkErrorAbsent(cell);
    }

    // SetReference.....................................................................................................

    @Test(expected = NullPointerException.class)
    public void testSetReferenceNullFails() {
        this.createCell().setReference(null);
    }

    @Test
    public void testSetReferenceSame() {
        final SpreadsheetCell cell = this.createCell();
        assertSame(cell, cell.setReference(cell.reference()));
    }

    @Test
    public void testSetReferenceDifferent() {
        final SpreadsheetCell cell = this.createCell();
        final SpreadsheetCellReference differentReference = differentReference();
        final SpreadsheetCell different = cell.setReference(differentReference);
        assertNotSame(cell, different);

        this.checkReference(different, differentReference);
        this.checkFormula(different, this.formula());
        this.checkExpressionAbsent(different);
        this.checkErrorAbsent(different);

        this.checkReference(cell, REFERENCE);
        this.checkFormula(cell, this.formula());
        this.checkExpressionAbsent(cell);
        this.checkValueAbsent(cell);
        this.checkErrorAbsent(cell);
    }

    @Test
    public void testSetReferenceAfterSetExpression() {
        final SpreadsheetCell cell = this.createCell()
                .setExpression(this.expression());
        final SpreadsheetCellReference differentReference = differentReference();
        final SpreadsheetCell different = cell.setReference(differentReference);
        assertNotSame(cell, different);

        this.checkReference(different, differentReference);
        this.checkFormula(different, this.formula());
        this.checkExpressionAbsent(different);
        this.checkValueAbsent(different);
        this.checkErrorAbsent(different);
    }

    @Test
    public void testSetReferenceAfterSetExpressionSetValue() {
        final SpreadsheetCell cell = this.createCell()
                .setExpression(this.expression())
                .setValue(this.value());
        final SpreadsheetCellReference differentReference = differentReference();
        final SpreadsheetCell different = cell.setReference(differentReference);
        assertNotSame(cell, different);

        this.checkReference(different, differentReference);
        this.checkFormula(different, this.formula());
        this.checkExpressionAbsent(different);
        this.checkValueAbsent(different);
        this.checkErrorAbsent(different);
    }

    // SetFormula.....................................................................................................

    @Test(expected = NullPointerException.class)
    public void testSetFormulaNullFails() {
        this.createCell().setFormula(null);
    }

    @Test
    public void testSetFormulaSame() {
        final SpreadsheetCell cell = this.createCell();
        assertSame(cell, cell.setFormula(cell.formula()));
    }

    @Test
    public void testSetFormulaDifferent() {
        final SpreadsheetCell cell = this.createCell();
        final SpreadsheetFormula differentFormula = this.formula("different");
        final SpreadsheetCell different = cell.setFormula(differentFormula);
        assertNotSame(cell, different);

        this.checkReference(different, REFERENCE);
        this.checkFormula(different, differentFormula);
        this.checkExpressionAbsent(different);
        this.checkValueAbsent(different);
        this.checkErrorAbsent(different);
    }

    @Test
    public void testSetFormulaAfterSetExpression() {
        final SpreadsheetCell cell = this.createCell()
                .setExpression(this.expression());
        final SpreadsheetFormula differentFormula = differentFormula();
        final SpreadsheetCell different = cell.setFormula(differentFormula);
        assertNotSame(cell, different);

        this.checkReference(different, REFERENCE);
        this.checkFormula(different, differentFormula);
        this.checkExpressionAbsent(different);
        this.checkValueAbsent(different);
        this.checkErrorAbsent(different);
    }

    @Test
    public void testSetFormulaAfterSetExpressionSetValue() {
        final SpreadsheetCell cell = this.createCell()
                .setExpression(this.expression())
                .setValue(this.value());
        final SpreadsheetFormula differentFormula = differentFormula();
        final SpreadsheetCell different = cell.setFormula(differentFormula);
        assertNotSame(cell, different);

        this.checkReference(different, REFERENCE);
        this.checkFormula(different, differentFormula);
        this.checkExpressionAbsent(different);
        this.checkValueAbsent(different);
        this.checkErrorAbsent(different);
    }

    // SetExpression.....................................................................................................

    @Test(expected = NullPointerException.class)
    public void testSetExpressionNullFails() {
        this.createCell().setExpression(null);
    }

    @Test
    public void testSetExpressionSame() {
        final SpreadsheetCell cell = this.createCell();
        assertSame(cell, cell.setExpression(cell.expression()));
    }

    @Test
    public void testSetExpressionDifferent() {
        final SpreadsheetCell cell = this.createCell();
        final Optional<ExpressionNode> differentExpression = Optional.of(ExpressionNode.text("different!"));
        final SpreadsheetCell different = cell.setExpression(differentExpression);
        assertNotSame(cell, different);

        this.checkReference(different, REFERENCE);
        this.checkFormula(different, this.formula());
        this.checkExpression(different, differentExpression);
        this.checkValueAbsent(different);
        this.checkErrorAbsent(different);
    }

    @Test
    public void testSetExpressionDifferentAndClear() {
        final SpreadsheetCell cell = this.createCell()
                .setExpression(this.expression());
        final Optional<ExpressionNode> differentExpression = SpreadsheetCell.NO_EXPRESSION;
        final SpreadsheetCell different = cell.setExpression(differentExpression);
        assertNotSame(cell, different);

        this.checkReference(different, REFERENCE);
        this.checkFormula(different, this.formula());
        this.checkExpressionAbsent(different);
        this.checkValueAbsent(different);
        this.checkErrorAbsent(different);
    }

    @Test
    public void testSetExpressionDifferentAfterSetValue() {
        final SpreadsheetCell cell = this.createCell()
                .setExpression(this.expression())
                .setValue(this.value());

        final Optional<ExpressionNode> differentExpression = Optional.of(ExpressionNode.text("different!"));
        final SpreadsheetCell different = cell.setExpression(differentExpression);
        assertNotSame(cell, different);

        this.checkReference(different, REFERENCE);
        this.checkFormula(different, this.formula());
        this.checkExpression(different, differentExpression);
        this.checkValueAbsent(different);
        this.checkErrorAbsent(different);
    }


    // SetError.....................................................................................................

    @Test(expected = NullPointerException.class)
    public void testSetErrorNullFails() {
        this.createCell().setError(null);
    }

    @Test
    public void testSetErrorSame() {
        final SpreadsheetCell cell = this.createCell();
        assertSame(cell, cell.setError(cell.error()));
    }

    @Test
    public void testSetErrorDifferent() {
        final SpreadsheetCell cell = this.createCell();
        final Optional<SpreadsheetError> differentError = this.error("different");
        final SpreadsheetCell different = cell.setError(differentError);
        assertNotSame(cell, different);

        this.checkReference(different, REFERENCE);
        this.checkFormula(different, this.formula());
        this.checkExpressionAbsent(different);
        this.checkValueAbsent(different);
        this.checkError(different, differentError);
    }

    @Test
    public void testSetErrorDifferent2() {
        final SpreadsheetCell cell = this.createCell()
                .setError(this.error());
        final Optional<SpreadsheetError> differentError = this.error("different");
        final SpreadsheetCell different = cell.setError(differentError);
        assertNotSame(cell, different);

        this.checkReference(different, REFERENCE);
        this.checkFormula(different, this.formula());
        this.checkExpressionAbsent(different);
        this.checkValueAbsent(different);
        this.checkError(different, differentError);
    }

    @Test
    public void testSetErrorDifferentAfterSetValue() {
        final SpreadsheetCell cell = this.createCell()
                .setValue(this.value());
        final Optional<SpreadsheetError> differentError = this.error("different");
        final SpreadsheetCell different = cell.setError(differentError);
        assertNotSame(cell, different);

        this.checkReference(different, REFERENCE);
        this.checkFormula(different, this.formula());
        this.checkExpressionAbsent(different);
        this.checkValueAbsent(different);
        this.checkError(different, differentError);
    }
    
    // toString...............................................................................................

    @Test
    public void testToStringWithValue() {
        assertEquals(REFERENCE + "=" + this.formula(FORMULA) + " (=" + VALUE + ")", this.createCell().setValue(this.value(VALUE)).toString());
    }

    @Test
    public void testToStringWithError() {
        assertEquals(REFERENCE + "=" + this.formula(FORMULA) + " (" + ERROR + ")", this.createCell().setError(this.error(ERROR)).toString());
    }

    @Test
    public void testToStringWithoutError() {
        assertEquals(REFERENCE + "=" + this.formula(), this.createCell().toString());
    }

    private SpreadsheetCell createCell() {
        return SpreadsheetCell.with(REFERENCE, this.formula(FORMULA));
    }

    private static SpreadsheetCellReference differentReference() {
        return reference(99, 888);
    }

    private static SpreadsheetCellReference reference(final int column, final int row) {
        return SpreadsheetCellReference.with(SpreadsheetReferenceKind.ABSOLUTE.column(column), SpreadsheetReferenceKind.ABSOLUTE.row(row));
    }

    private void checkReference(final SpreadsheetCell cell, final SpreadsheetCellReference reference) {
        assertEquals("reference", reference, cell.reference());
    }
    
    private SpreadsheetFormula formula() {
        return this.formula(FORMULA);
    }

    private SpreadsheetFormula differentFormula() {
        return this.formula("=different");
    }

    private SpreadsheetFormula formula(final String text) {
        return SpreadsheetFormula.with(text);
    }

    private void checkFormula(final SpreadsheetCell cell, final SpreadsheetFormula formula) {
        assertEquals("formula", formula, cell.formula());
    }

    private Optional<ExpressionNode> expression() {
        return this.expression(EXPRESSION);
    }

    private Optional<ExpressionNode> expression(final String text) {
        return Optional.of(ExpressionNode.text(text));
    }

    private void checkExpression(final SpreadsheetCell cell, final String expression) {
        this.checkExpression(cell, this.expression(expression));
    }

    private void checkExpression(final SpreadsheetCell cell, final Optional<ExpressionNode> expression) {
        assertEquals("expression", expression, cell.expression());
    }

    private void checkExpressionAbsent(final SpreadsheetCell cell) {
        this.checkExpression(cell, SpreadsheetCell.NO_EXPRESSION);
    }

    private Optional<Object> value() {
        return this.value(VALUE);
    }

    private Optional<Object> value(final Object value) {
        return Optional.of(value);
    }

    private void checkValue(final SpreadsheetCell cell, final Optional<Object> value) {
        assertEquals("value", value, cell.value());
    }

    private void checkValueAbsent(final SpreadsheetCell cell) {
        this.checkValue(cell, SpreadsheetCell.NO_VALUE);
    }

    private Optional<SpreadsheetError> error() {
        return this.error(ERROR);
    }

    private Optional<SpreadsheetError> error(final String error) {
        return Optional.of(SpreadsheetError.with(error));
    }

    private void checkErrorAbsent(final SpreadsheetCell cell) {
        this.checkError(cell, SpreadsheetCell.NO_ERROR);
    }

    private void checkError(final SpreadsheetCell cell, final Optional<SpreadsheetError> error) {
        assertEquals("formula", error, cell.error());
    }

    @Override
    protected Class<SpreadsheetCell> type() {
        return SpreadsheetCell.class;
    }
}

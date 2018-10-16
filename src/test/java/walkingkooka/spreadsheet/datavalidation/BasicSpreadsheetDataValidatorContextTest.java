package walkingkooka.spreadsheet.datavalidation;

import org.junit.Test;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetReferenceKind;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionEvaluationContexts;
import walkingkooka.tree.expression.ExpressionReference;

import static org.junit.Assert.assertEquals;

public final class BasicSpreadsheetDataValidatorContextTest extends SpreadsheetDataValidatorContextTestCase<BasicSpreadsheetDataValidatorContext> {

    @Test(expected = NullPointerException.class)
    public void testWithNullCellReferenceFails() {
        BasicSpreadsheetDataValidatorContext.with(null, this.value(), this.expressionEvaluationContext());
    }

    @Test(expected = NullPointerException.class)
    public void testWithNullValueFails() {
        BasicSpreadsheetDataValidatorContext.with(this.cellReference(), null, this.expressionEvaluationContext());
    }

    @Test(expected = NullPointerException.class)
    public void testWithNullExpressionEvaluationContextFails() {
        BasicSpreadsheetDataValidatorContext.with(this.cellReference(), this.value(), null);
    }

    @Test
    public void testCellReference() {
        assertEquals(this.cellReference(), this.createContext().cellReference());
    }

    @Test
    public void testToString() {
        final ExpressionEvaluationContext context = this.expressionEvaluationContext();
        assertEquals(context.toString(), this.createContext(context).toString());
    }

    @Override
    protected BasicSpreadsheetDataValidatorContext createContext() {
        return this.createContext(expressionEvaluationContext());
    }

    protected BasicSpreadsheetDataValidatorContext createContext(final ExpressionEvaluationContext context) {
        return BasicSpreadsheetDataValidatorContext.with(cellReference(), value(), context);
    }

    private ExpressionReference cellReference() {
        return SpreadsheetReferenceKind.RELATIVE.column(1).setRow(SpreadsheetReferenceKind.RELATIVE.row(2));
    }

    private Object value() {
        return "abc123";
    }

    private ExpressionEvaluationContext expressionEvaluationContext() {
        return ExpressionEvaluationContexts.fake();
    }

    @Override
    protected Class<BasicSpreadsheetDataValidatorContext> type() {
        return BasicSpreadsheetDataValidatorContext.class;
    }
}

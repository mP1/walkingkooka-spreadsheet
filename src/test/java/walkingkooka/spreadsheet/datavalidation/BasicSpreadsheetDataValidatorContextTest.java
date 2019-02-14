package walkingkooka.spreadsheet.datavalidation;

import org.junit.jupiter.api.Test;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetReferenceKind;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionEvaluationContexts;
import walkingkooka.tree.expression.ExpressionReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicSpreadsheetDataValidatorContextTest implements SpreadsheetDataValidatorContextTesting<BasicSpreadsheetDataValidatorContext> {

    @Test
    public void testWithNullCellReferenceFails() {
        assertThrows(NullPointerException.class, () -> {
            BasicSpreadsheetDataValidatorContext.with(null, this.value(), this.expressionEvaluationContext());
        });
    }

    @Test
    public void testWithNullValueFails() {
        assertThrows(NullPointerException.class, () -> {
            BasicSpreadsheetDataValidatorContext.with(this.cellReference(), null, this.expressionEvaluationContext());
        });
    }

    @Test
    public void testWithNullExpressionEvaluationContextFails() {
        assertThrows(NullPointerException.class, () -> {
            BasicSpreadsheetDataValidatorContext.with(this.cellReference(), this.value(), null);
        });
    }

    @Test
    public void testCellReference() {
        assertEquals(this.cellReference(), this.createContext().cellReference());
    }

    @Test
    public void testToString() {
        final ExpressionEvaluationContext context = this.expressionEvaluationContext();
        this.toStringAndCheck(this.createContext(context), context.toString());
    }

    @Override
    public BasicSpreadsheetDataValidatorContext createContext() {
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
    public Class<BasicSpreadsheetDataValidatorContext> type() {
        return BasicSpreadsheetDataValidatorContext.class;
    }
}

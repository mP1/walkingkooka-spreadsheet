package walkingkooka.spreadsheet.datavalidation;

import org.junit.Test;
import walkingkooka.tree.expression.ExpressionNode;

import static org.junit.Assert.assertEquals;

public final class CustomFormulaSpreadsheetDataValidatorTest extends SpreadsheetDataValidatorTemplateTestCase<CustomFormulaSpreadsheetDataValidator, Object> {

    private final static long VALUE = 123;

    @Test
    public void testCustomFormulaTrue() {
        this.validatePassCheck(VALUE + 1);
    }

    @Test
    public void testCustomFormulaFalse() {
        this.validateFailCheck(VALUE - 1);
    }

    @Test
    public void testToString() {
        assertEquals("B3>123", this.createSpreadsheetDataValidator().toString());
    }

    @Override
    protected CustomFormulaSpreadsheetDataValidator createSpreadsheetDataValidator() {
        return CustomFormulaSpreadsheetDataValidator.with(this.expression());
    }

    private ExpressionNode expression() {
        return ExpressionNode.greaterThan(
                ExpressionNode.reference(this.cellReference()),
                ExpressionNode.valueOrFail(VALUE));
    }

    @Override
    protected Long value() {
        return VALUE;
    }

    @Override
    protected Class<Object> valueType() {
        return Object.class;
    }

    @Override
    protected Class<CustomFormulaSpreadsheetDataValidator> type() {
        return CustomFormulaSpreadsheetDataValidator.class;
    }
}

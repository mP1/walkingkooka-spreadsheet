package walkingkooka.spreadsheet.datavalidation;

import org.junit.jupiter.api.Test;
import walkingkooka.tree.expression.ExpressionNode;

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
        this.toStringAndCheck(this.createSpreadsheetDataValidator(), "B3>123");
    }

    @Override
    public CustomFormulaSpreadsheetDataValidator createSpreadsheetDataValidator() {
        return CustomFormulaSpreadsheetDataValidator.with(this.expression());
    }

    private ExpressionNode expression() {
        return ExpressionNode.greaterThan(
                ExpressionNode.reference(this.cellReference()),
                ExpressionNode.valueOrFail(VALUE));
    }

    @Override
    public Long value() {
        return VALUE;
    }

    @Override
    public Class<Object> valueType() {
        return Object.class;
    }

    @Override
    public Class<CustomFormulaSpreadsheetDataValidator> type() {
        return CustomFormulaSpreadsheetDataValidator.class;
    }
}

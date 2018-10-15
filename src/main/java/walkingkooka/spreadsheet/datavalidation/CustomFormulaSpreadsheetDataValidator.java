package walkingkooka.spreadsheet.datavalidation;

import walkingkooka.tree.expression.ExpressionNode;

import java.util.Objects;

/**
 * A {@link SpreadsheetDataValidator} that validates the cell value against a formula. During validation,
 * it creates a {@link walkingkooka.tree.expression.ExpressionEvaluationContext} that returns the given {@link Object value}
 * for this cell.
 */
final class CustomFormulaSpreadsheetDataValidator extends SpreadsheetDataValidatorTemplate<Object> {

    /**
     * Creates a new {@link CustomFormulaSpreadsheetDataValidator}.
     */
    static CustomFormulaSpreadsheetDataValidator with(final ExpressionNode customFormula) {
        Objects.requireNonNull(customFormula, "customFormula");

        return new CustomFormulaSpreadsheetDataValidator(customFormula);
    }

    /**
     * Private ctor use factory
     */
    private CustomFormulaSpreadsheetDataValidator(final ExpressionNode customFormula) {
        super();
        this.customFormula = customFormula;
    }

    @Override
    public Class<Object> valueType() {
        return Object.class;
    }

    @Override
    boolean validate0(final Object value, final SpreadsheetDataValidatorContext context) {
        return this.validate1(this.context(value, context));
    }

    private SpreadsheetDataValidatorContext context(final Object value, final SpreadsheetDataValidatorContext context) {
        return SpreadsheetDataValidatorContexts.basic(context.cellReference(), value, context);
    }

    private boolean validate1(final SpreadsheetDataValidatorContext context) {
        return this.customFormula.toBoolean(context);
    }

    @Override
    public String toString() {
        return this.customFormula.toString();
    }

    private final ExpressionNode customFormula;
}

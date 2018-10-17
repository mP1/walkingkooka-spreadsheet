package walkingkooka.spreadsheet.datavalidation;

import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionNode;
import walkingkooka.tree.expression.ExpressionNodeName;
import walkingkooka.tree.expression.ExpressionReference;

import java.math.MathContext;
import java.util.List;
import java.util.Objects;

/**
 * A {@link SpreadsheetDataValidatorContext} which wraps a {@link ExpressionEvaluationContext}.
 */
final class BasicSpreadsheetDataValidatorContext implements SpreadsheetDataValidatorContext {

    /**
     * Factory that creates a {@link BasicSpreadsheetDataValidatorContext} including the
     * cell and value being validated.
     */
    static BasicSpreadsheetDataValidatorContext with(final ExpressionReference cellReference,
                                                     final Object value,
                                                     final ExpressionEvaluationContext context) {
        Objects.requireNonNull(cellReference, "cellReference");
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(context, "context");

        return new BasicSpreadsheetDataValidatorContext(cellReference, value, context);
    }

    /**
     * Private ctor use factory.
     */
    private BasicSpreadsheetDataValidatorContext(final ExpressionReference cellReference,
                                                 final Object value,
                                                 final ExpressionEvaluationContext context) {
        super();
        this.cellReference = cellReference;
        this.value = ExpressionNode.valueOrFail(value);
        this.context = context;
    }

    @Override
    public char decimalPoint() {
        return this.context.decimalPoint();
    }

    @Override
    public char exponentSymbol() {
        return this.context.exponentSymbol();
    }

    @Override
    public char minusSign() {
        return this.context.minusSign();
    }

    @Override
    public char plusSign() {
        return this.context.plusSign();
    }

    @Override
    public ExpressionReference cellReference() {
        return this.cellReference;
    }

    private final ExpressionReference cellReference;

    @Override
    public Object function(final ExpressionNodeName functionName, final List<Object> list) {
        return this.context.function(functionName, list);
    }

    @Override
    public ExpressionNode reference(final ExpressionReference reference) {
        return this.cellReference().equals(reference) ?
                this.value :
                this.context.reference(reference);
    }

    private final ExpressionNode value;

    @Override
    public MathContext mathContext() {
        return this.context.mathContext();
    }

    @Override
    public <T> T convert(final Object value, final Class<T> targetType) {
        return this.context.convert(value, targetType);
    }

    private final ExpressionEvaluationContext context;

    @Override
    public String toString() {
        return this.context.toString();
    }
}

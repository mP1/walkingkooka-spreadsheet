package walkingkooka.spreadsheet.datavalidation;

import walkingkooka.Context;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionReference;

/**
 * The {@link Context} that accompanies each validation request.
 */
public interface SpreadsheetDataValidatorContext extends ExpressionEvaluationContext {

    /**
     * A {@link ExpressionReference} identifying the cell being validated.
     */
    ExpressionReference cellReference();
}

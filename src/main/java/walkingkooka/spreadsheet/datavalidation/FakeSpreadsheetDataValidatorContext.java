package walkingkooka.spreadsheet.datavalidation;

import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.FakeExpressionEvaluationContext;

public class FakeSpreadsheetDataValidatorContext extends FakeExpressionEvaluationContext implements SpreadsheetDataValidatorContext {

    @Override
    public ExpressionReference cellReference() {
        throw new UnsupportedOperationException();
    }
}

package walkingkooka.spreadsheet.datavalidation;

import walkingkooka.tree.expression.ExpressionEvaluationContextTestCase;

public abstract class SpreadsheetDataValidatorContextTestCase<C extends SpreadsheetDataValidatorContext> extends ExpressionEvaluationContextTestCase<C> {
    @Override
    protected String requiredNameSuffix() {
        return SpreadsheetDataValidatorContext.class.getSimpleName();
    }
}

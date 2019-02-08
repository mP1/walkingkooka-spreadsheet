package walkingkooka.spreadsheet.datavalidation;

import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.tree.expression.ExpressionEvaluationContextTestCase;

public abstract class SpreadsheetDataValidatorContextTestCase<C extends SpreadsheetDataValidatorContext> extends ExpressionEvaluationContextTestCase<C> {

    // TypeNameTesting .........................................................................................

    @Override
    public final String typeNameSuffix() {
        return SpreadsheetDataValidatorContext.class.getSimpleName();
    }
}

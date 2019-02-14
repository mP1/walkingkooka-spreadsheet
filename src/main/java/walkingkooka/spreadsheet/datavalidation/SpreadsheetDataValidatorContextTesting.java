package walkingkooka.spreadsheet.datavalidation;

import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.tree.expression.ExpressionEvaluationContextTesting;

public interface SpreadsheetDataValidatorContextTesting<C extends SpreadsheetDataValidatorContext> extends ExpressionEvaluationContextTesting<C> {

    // TypeNameTesting .........................................................................................

    @Override
    default String typeNameSuffix() {
        return SpreadsheetDataValidatorContext.class.getSimpleName();
    }
}

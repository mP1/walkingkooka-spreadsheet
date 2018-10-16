package walkingkooka.spreadsheet.datavalidation;

import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.type.PublicStaticHelper;

public final class SpreadsheetDataValidatorContexts implements PublicStaticHelper {

    /**
     * {@see BasicSpreadsheetDataValidatorContext}
     */
    public static SpreadsheetDataValidatorContext basic(final ExpressionReference cellReference,
                                                        final Object value,
                                                        final ExpressionEvaluationContext context) {
        return BasicSpreadsheetDataValidatorContext.with(cellReference, value, context);
    }

    /**
     * {@see FakeSpreadsheetDataValidatorContext}
     */
    public static SpreadsheetDataValidatorContext fake() {
        return new FakeSpreadsheetDataValidatorContext();
    }

    /**
     * Stop creation
     */
    private SpreadsheetDataValidatorContexts() {
        throw new UnsupportedOperationException();
    }
}

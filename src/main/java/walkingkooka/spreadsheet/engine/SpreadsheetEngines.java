package walkingkooka.spreadsheet.engine;

import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetParserContext;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetParserToken;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.type.PublicStaticHelper;

public final class SpreadsheetEngines implements PublicStaticHelper {

    /**
     * {@see BasicSpreadsheetEngine}
     */
    public static SpreadsheetEngine basic(final SpreadsheetId id,
                                       final Parser<SpreadsheetParserToken, SpreadsheetParserContext> parser,
                                       final SpreadsheetParserContext parserContext,
                                       final ExpressionEvaluationContext evaluationContext) {
        return BasicSpreadsheetEngine.with(id, parser, parserContext, evaluationContext);
    }

    /**
     * Stop creation
     */
    private SpreadsheetEngines() {
        throw new UnsupportedOperationException();
    }
}

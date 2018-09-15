package walkingkooka.spreadsheet.engine;

import walkingkooka.convert.Converter;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.label.SpreadsheetLabelStore;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetParserContext;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetParserToken;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionNodeName;
import walkingkooka.type.PublicStaticHelper;

import java.math.MathContext;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class SpreadsheetEngines implements PublicStaticHelper {

    /**
     * {@see BasicSpreadsheetEngine}
     */
    public static SpreadsheetEngine basic(final SpreadsheetId id,
                                          final SpreadsheetCellStore cellStore,
                                          final Parser<SpreadsheetParserToken, SpreadsheetParserContext> parser,
                                          final SpreadsheetParserContext parserContext,
                                          final Function<SpreadsheetEngine, ExpressionEvaluationContext> evaluationContextFactory) {
        return BasicSpreadsheetEngine.with(id, cellStore, parser, parserContext, evaluationContextFactory);
    }

    /**
     * {@see FakeSpreadsheetEngine}
     */
    public static SpreadsheetEngine fake() {
        return new FakeSpreadsheetEngine();
    }

    /**
     * {@see SpreadsheetEngineExpressionEvaluationContextFactoryFunction}
     */
    public static Function<SpreadsheetEngine, ExpressionEvaluationContext> spreadsheetEngineExpressionEvaluationContextFunction(final BiFunction<ExpressionNodeName, List<Object>, Object> functions,
                                                                                                                                final SpreadsheetLabelStore labelStore,
                                                                                                                                final MathContext mathContext,
                                                                                                                                final Converter converter) {
        return SpreadsheetEngineExpressionEvaluationContextFactoryFunction.with(functions, labelStore, mathContext, converter);
    }

    /**
     * Stop creation
     */
    private SpreadsheetEngines() {
        throw new UnsupportedOperationException();
    }
}

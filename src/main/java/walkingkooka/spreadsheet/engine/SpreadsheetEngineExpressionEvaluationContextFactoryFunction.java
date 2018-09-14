package walkingkooka.spreadsheet.engine;

import walkingkooka.convert.Converter;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionEvaluationContexts;
import walkingkooka.tree.expression.ExpressionNodeName;

import java.math.MathContext;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A {@link Function factory} that creates a {@link ExpressionEvaluationContext}.
 */
final class SpreadsheetEngineExpressionEvaluationContextFactoryFunction implements Function<SpreadsheetEngine, ExpressionEvaluationContext> {

    /**
     * Creates a new SpreadsheetEngineExpressionEvaluationContextFactoryFunction
     */
    static SpreadsheetEngineExpressionEvaluationContextFactoryFunction with(final BiFunction<ExpressionNodeName, List<Object>, Object> functions,
                                                                        final MathContext mathContext,
                                                                        final Converter converter) {
        Objects.requireNonNull(functions, "functions");
        Objects.requireNonNull(mathContext, "mathContext");
        Objects.requireNonNull(converter, "converter");

        return new SpreadsheetEngineExpressionEvaluationContextFactoryFunction(functions, mathContext, converter);
    }

    private SpreadsheetEngineExpressionEvaluationContextFactoryFunction(final BiFunction<ExpressionNodeName, List<Object>, Object> functions,
                                                                       final MathContext mathContext,
                                                                       final Converter converter) {
        this.functions = functions;
        this.mathContext = mathContext;
        this.converter = converter;
    }

    @Override
    public ExpressionEvaluationContext apply(final SpreadsheetEngine engine) {
        return ExpressionEvaluationContexts.basic(this.functions,
                SpreadsheetEngineExpressionEvaluationContextFactoryFunctionExpressionReferenceExpressionNodeFunction.with(engine),
                this.mathContext,
                this.converter);
    }

    private final BiFunction<ExpressionNodeName, List<Object>, Object> functions;
    private final MathContext mathContext;
    private final Converter converter;

}

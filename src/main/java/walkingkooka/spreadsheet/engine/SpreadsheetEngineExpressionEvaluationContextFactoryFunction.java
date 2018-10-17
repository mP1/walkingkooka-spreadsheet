package walkingkooka.spreadsheet.engine;

import walkingkooka.DecimalNumberContext;
import walkingkooka.convert.Converter;
import walkingkooka.spreadsheet.store.label.SpreadsheetLabelStore;
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
                                                                            final SpreadsheetLabelStore labelStore,
                                                                            final MathContext mathContext,
                                                                            final Converter converter,
                                                                            final DecimalNumberContext decimalNumberContext) {
        Objects.requireNonNull(functions, "functions");
        Objects.requireNonNull(labelStore, "labelStore");
        Objects.requireNonNull(mathContext, "mathContext");
        Objects.requireNonNull(converter, "converter");
        Objects.requireNonNull(decimalNumberContext, "decimalNumberContext");

        return new SpreadsheetEngineExpressionEvaluationContextFactoryFunction(functions,
                labelStore,
                mathContext,
                converter,
                decimalNumberContext);
    }

    private SpreadsheetEngineExpressionEvaluationContextFactoryFunction(final BiFunction<ExpressionNodeName, List<Object>, Object> functions,
                                                                        final SpreadsheetLabelStore labelStore,
                                                                        final MathContext mathContext,
                                                                        final Converter converter,
                                                                        final DecimalNumberContext decimalNumberContext) {
        this.functions = functions;
        this.labelStore = labelStore;
        this.mathContext = mathContext;
        this.converter = converter;
        this.decimalNumberContext = decimalNumberContext;
    }

    @Override
    public ExpressionEvaluationContext apply(final SpreadsheetEngine engine) {
        return ExpressionEvaluationContexts.basic(this.functions,
                SpreadsheetEngineExpressionEvaluationContextFactoryFunctionExpressionReferenceExpressionNodeFunction.with(engine, this.labelStore),
                this.mathContext,
                this.converter,
                this.decimalNumberContext);
    }

    private final BiFunction<ExpressionNodeName, List<Object>, Object> functions;
    private final SpreadsheetLabelStore labelStore;
    private final MathContext mathContext;
    private final Converter converter;
    private final DecimalNumberContext decimalNumberContext;

}

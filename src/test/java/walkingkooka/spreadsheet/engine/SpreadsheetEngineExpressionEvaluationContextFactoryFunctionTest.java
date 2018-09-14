package walkingkooka.spreadsheet.engine;

import org.junit.Test;
import walkingkooka.convert.Converter;
import walkingkooka.convert.Converters;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionNodeName;
import walkingkooka.util.FunctionTestCase;

import java.math.MathContext;
import java.util.List;
import java.util.function.BiFunction;

public final class SpreadsheetEngineExpressionEvaluationContextFactoryFunctionTest extends FunctionTestCase<SpreadsheetEngineExpressionEvaluationContextFactoryFunction,
        SpreadsheetEngine,
        ExpressionEvaluationContext> {

    @Test(expected = NullPointerException.class)
    public void testWithNullFunctionsFails() {
        SpreadsheetEngineExpressionEvaluationContextFactoryFunction.with(null, this.mathContext(), this.converter());
    }

    @Test(expected = NullPointerException.class)
    public void testWithNullMathContextFails() {
        SpreadsheetEngineExpressionEvaluationContextFactoryFunction.with(this.functions(), null, this.converter());
    }

    @Test(expected = NullPointerException.class)
    public void testWithNullConverterFails() {
        SpreadsheetEngineExpressionEvaluationContextFactoryFunction.with(this.functions(), this.mathContext(), null);
    }

    @Override
    protected SpreadsheetEngineExpressionEvaluationContextFactoryFunction createFunction() {
        return SpreadsheetEngineExpressionEvaluationContextFactoryFunction.with(this.functions(), this.mathContext(), this.converter());
    }

    private BiFunction<ExpressionNodeName, List<Object>, Object> functions() {
        return (name, params) -> {
            throw new UnsupportedOperationException();
        };
    }

    private MathContext mathContext() {
        return MathContext.DECIMAL32;
    }

    private Converter converter() {
        return Converters.simple();
    }

    @Override
    protected Class<SpreadsheetEngineExpressionEvaluationContextFactoryFunction> type() {
        return SpreadsheetEngineExpressionEvaluationContextFactoryFunction.class;
    }
}

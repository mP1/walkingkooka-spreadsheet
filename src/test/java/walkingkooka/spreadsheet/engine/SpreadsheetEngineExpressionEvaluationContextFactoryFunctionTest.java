package walkingkooka.spreadsheet.engine;

import org.junit.Test;
import walkingkooka.DecimalNumberContext;
import walkingkooka.DecimalNumberContexts;
import walkingkooka.convert.Converter;
import walkingkooka.convert.Converters;
import walkingkooka.spreadsheet.store.label.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.label.SpreadsheetLabelStores;
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
        SpreadsheetEngineExpressionEvaluationContextFactoryFunction.with(null,
                this.labelStore(),
                this.mathContext(),
                this.converter(),
                this.decimalNumberContext());
    }

    @Test(expected = NullPointerException.class)
    public void testWithNullLabelStoreFails() {
        SpreadsheetEngineExpressionEvaluationContextFactoryFunction.with(this.functions(),
                null,
                this.mathContext(),
                this.converter(),
                this.decimalNumberContext());
    }

    @Test(expected = NullPointerException.class)
    public void testWithNullMathContextFails() {
        SpreadsheetEngineExpressionEvaluationContextFactoryFunction.with(this.functions(),
                this.labelStore(),
                null,
                this.converter(),
                this.decimalNumberContext());
    }

    @Test(expected = NullPointerException.class)
    public void testWithNullConverterFails() {
        SpreadsheetEngineExpressionEvaluationContextFactoryFunction.with(this.functions(),
                this.labelStore(),
                this.mathContext(),
                null,
                this.decimalNumberContext());
    }

    @Test(expected = NullPointerException.class)
    public void testWithNullDecimalNumberContextFails() {
        SpreadsheetEngineExpressionEvaluationContextFactoryFunction.with(this.functions(),
                this.labelStore(),
                this.mathContext(),
                this.converter(),
                null);
    }

    @Override
    protected SpreadsheetEngineExpressionEvaluationContextFactoryFunction createFunction() {
        return SpreadsheetEngineExpressionEvaluationContextFactoryFunction.with(this.functions(),
                this.labelStore(),
                this.mathContext(),
                this.converter(),
                this.decimalNumberContext());
    }

    private BiFunction<ExpressionNodeName, List<Object>, Object> functions() {
        return (name, params) -> {
            throw new UnsupportedOperationException();
        };
    }

    private SpreadsheetLabelStore labelStore() {
        return SpreadsheetLabelStores.fake();
    }

    private MathContext mathContext() {
        return MathContext.DECIMAL32;
    }

    private Converter converter() {
        return Converters.simple();
    }

    private DecimalNumberContext decimalNumberContext() {
        return DecimalNumberContexts.fake();
    }

    @Override
    protected Class<SpreadsheetEngineExpressionEvaluationContextFactoryFunction> type() {
        return SpreadsheetEngineExpressionEvaluationContextFactoryFunction.class;
    }
}

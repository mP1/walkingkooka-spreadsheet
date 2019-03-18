package walkingkooka.spreadsheet.datavalidation;

import walkingkooka.collect.list.Lists;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetReferenceKind;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.FakeExpressionEvaluationContext;

public abstract class SpreadsheetDataValidatorTemplateTestCase<V extends SpreadsheetDataValidatorTemplate, T> implements SpreadsheetDataValidatorTesting<V, T> {

    SpreadsheetDataValidatorTemplateTestCase() {
        super();
    }

    @Override
    public SpreadsheetDataValidatorContext createContext() {
        return BasicSpreadsheetDataValidatorContext.with(this.cellReference(), this.value(), this.expressionEvaluationContext());
    }

    final ExpressionReference cellReference() {
        return SpreadsheetReferenceKind.RELATIVE.column(1).setRow(SpreadsheetReferenceKind.RELATIVE.row(2));
    }

    final ExpressionEvaluationContext expressionEvaluationContext() {
        final Converter all = Converters.collection(
                Lists.of(Converters.simple(),
                        Converters.truthyNumberBoolean()));

        return new FakeExpressionEvaluationContext() {
            @Override
            public <TT> TT convert(final Object value, final Class<TT> target) {
                return all.convert(value, target, ConverterContexts.basic(this));
            }
        };
    }
}

/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package walkingkooka.spreadsheet.datavalidation;

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.FakeExpressionEvaluationContext;
import walkingkooka.tree.expression.FunctionExpressionName;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionContext;
import walkingkooka.tree.expression.function.UnknownExpressionFunctionException;

import java.math.MathContext;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicSpreadsheetDataValidatorContextTest implements SpreadsheetDataValidatorContextTesting<BasicSpreadsheetDataValidatorContext> {

    @Test
    public void testWithNullCellReferenceFails() {
        assertThrows(NullPointerException.class, () -> BasicSpreadsheetDataValidatorContext.with(null, this.value(), this.expressionEvaluationContext()));
    }

    @Test
    public void testWithNullValueFails() {
        assertThrows(NullPointerException.class, () -> BasicSpreadsheetDataValidatorContext.with(this.cellReference(), null, this.expressionEvaluationContext()));
    }

    @Test
    public void testWithNullExpressionEvaluationContextFails() {
        assertThrows(NullPointerException.class, () -> BasicSpreadsheetDataValidatorContext.with(this.cellReference(), this.value(), null));
    }

    @Test
    public void testCellReference() {
        this.checkEquals(this.cellReference(), this.createContext().cellReference());
    }

    @Test
    public void testToString() {
        final ExpressionEvaluationContext context = this.expressionEvaluationContext();
        this.toStringAndCheck(this.createContext(context), context.toString());
    }

    @Override
    public BasicSpreadsheetDataValidatorContext createContext() {
        return this.createContext(expressionEvaluationContext());
    }

    private BasicSpreadsheetDataValidatorContext createContext(final ExpressionEvaluationContext context) {
        return BasicSpreadsheetDataValidatorContext.with(cellReference(), value(), context);
    }

    private ExpressionReference cellReference() {
        return SpreadsheetReferenceKind.RELATIVE.column(1).setRow(SpreadsheetReferenceKind.RELATIVE.row(2));
    }

    @SuppressWarnings("SameReturnValue")
    private Object value() {
        return "abc123";
    }

    private ExpressionEvaluationContext expressionEvaluationContext() {
        return new FakeExpressionEvaluationContext() {

            @Override
            public ExpressionFunction<?, ExpressionFunctionContext> function(final FunctionExpressionName name) {
                throw new UnknownExpressionFunctionException(name);
            }

            @Override
            public Object evaluate(final Expression expression) {
                return expression.toValue(this);
            }

            @Override
            public Object evaluate(final FunctionExpressionName name,
                                   final List<Object> parameters) {
                Objects.requireNonNull(name, "name");
                Objects.requireNonNull(parameters, "parameters");
                throw new UnknownExpressionFunctionException(name);
            }

            @Override
            public boolean isPure(final FunctionExpressionName name) {
                Objects.requireNonNull(name, "name");
                throw new UnsupportedOperationException();
            }

            @Override
            public MathContext mathContext() {
                return MathContext.DECIMAL32;
            }
        };
    }

    @Override
    public Class<BasicSpreadsheetDataValidatorContext> type() {
        return BasicSpreadsheetDataValidatorContext.class;
    }
}

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

package walkingkooka.spreadsheet.expression;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.TypeNameTesting;
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.SpreadsheetErrorKind;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionEvaluationContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.function.ExpressionFunctionTesting;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ErrorExpressionFunctionTest implements ExpressionFunctionTesting<ErrorExpressionFunction, SpreadsheetError, ExpressionEvaluationContext>,
    ClassTesting2<ErrorExpressionFunction>,
    TypeNameTesting<ErrorExpressionFunction> {

    private final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.BIG_DECIMAL;

    @Test
    public void testMissingParameterFails() {
        assertThrows(
            IllegalArgumentException.class,
            this::apply2
        );
    }

    @Test
    public void test1() {
        this.applyAndCheck(
            Lists.of(
                EXPRESSION_NUMBER_KIND.one()
            ),
            SpreadsheetErrorKind.withValue(1)
                .toError()
        );
    }

    @Override
    public ErrorExpressionFunction createBiFunction() {
        return ErrorExpressionFunction.INSTANCE;
    }

    @Override
    public int minimumParameterCount() {
        return 1;
    }

    @Override
    public Class<ErrorExpressionFunction> type() {
        return ErrorExpressionFunction.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public ExpressionEvaluationContext createContext() {
        return ExpressionEvaluationContexts.fake();
    }
}

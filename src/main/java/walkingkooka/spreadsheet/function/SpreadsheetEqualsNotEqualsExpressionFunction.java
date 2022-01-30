
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

package walkingkooka.spreadsheet.function;

import walkingkooka.Either;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.FunctionExpressionName;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionContext;

import java.math.MathContext;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * A {@link ExpressionFunction} that handles equality and not equality evaluation of {@link String} as a special case,
 * because {@link String} equality is not case sensitive.
 */
final class SpreadsheetEqualsNotEqualsExpressionFunction implements ExpressionFunctionContext {
    @Override
    public ExpressionFunction<?, ExpressionFunctionContext> function(FunctionExpressionName functionExpressionName) {
        return null;
    }

    @Override
    public Object evaluate(FunctionExpressionName functionExpressionName, List<Object> list) {
        return null;
    }

    @Override
    public Optional<Object> reference(ExpressionReference expressionReference) {
        return Optional.empty();
    }

    @Override
    public boolean canConvert(Object o, Class<?> aClass) {
        return false;
    }

    @Override
    public <T> Either<T, String> convert(Object o, Class<T> aClass) {
        return null;
    }

    @Override
    public int defaultYear() {
        return 0;
    }

    @Override
    public int twoDigitYear() {
        return 0;
    }

    @Override
    public String currencySymbol() {
        return null;
    }

    @Override
    public char decimalSeparator() {
        return 0;
    }

    @Override
    public String exponentSymbol() {
        return null;
    }

    @Override
    public char groupingSeparator() {
        return 0;
    }

    @Override
    public char percentageSymbol() {
        return 0;
    }

    @Override
    public Locale locale() {
        return null;
    }

    @Override
    public MathContext mathContext() {
        return null;
    }

    @Override
    public char negativeSign() {
        return 0;
    }

    @Override
    public char positiveSign() {
        return 0;
    }

    @Override
    public ExpressionNumberKind expressionNumberKind() {
        return null;
    }
}

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

package walkingkooka.spreadsheet.engine;

import walkingkooka.Either;
import walkingkooka.convert.ConverterContext;
import walkingkooka.spreadsheet.function.SpreadsheetExpressionFunctionContext;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.FunctionExpressionName;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionContext;

import java.math.MathContext;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

final class BasicSpreadsheetEngineContextSpreadsheetExpressionFunctionContext implements SpreadsheetExpressionFunctionContext {

    static BasicSpreadsheetEngineContextSpreadsheetExpressionFunctionContext with(final Optional<SpreadsheetCellReference> cell,
                                                                                  final ExpressionNumberKind expressionNumberKind,
                                                                                  final Function<FunctionExpressionName, ExpressionFunction<?, ExpressionFunctionContext>> functions,
                                                                                  final ConverterContext converterContext) {
        Objects.requireNonNull(cell, "cell");

        return new BasicSpreadsheetEngineContextSpreadsheetExpressionFunctionContext(
                cell,
                expressionNumberKind,
                functions,
                converterContext
        );
    }

    private BasicSpreadsheetEngineContextSpreadsheetExpressionFunctionContext(final Optional<SpreadsheetCellReference> cell,
                                                                              final ExpressionNumberKind expressionNumberKind,
                                                                              final Function<FunctionExpressionName, ExpressionFunction<?, ExpressionFunctionContext>> functions,
                                                                              final ConverterContext converterContext) {
        super();
        this.cell = cell;
        this.functions = functions;
        this.expressionNumberKind = expressionNumberKind;
        this.converterContext = converterContext;
    }

    @Override
    public ExpressionFunction<?, ExpressionFunctionContext> function(final FunctionExpressionName name) {
        return this.functions.apply(name);
    }

    private final Function<FunctionExpressionName, ExpressionFunction<?, ExpressionFunctionContext>> functions;

    @Override
    public Object evaluate(final FunctionExpressionName name,
                           final List<Object> parameters) {
        return this.function(name)
                .apply(parameters, this);
    }

    @Override
    public Optional<SpreadsheetCellReference> cell() {
        return this.cell;
    }

    private final Optional<SpreadsheetCellReference> cell;

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> target) {
        return this.converterContext.canConvert(value, target);
    }

    @Override
    public <T> Either<T, String> convert(final Object value,
                                         final Class<T> type) {
        return this.converterContext.convert(value, type);
    }

    @Override
    public int defaultYear() {
        return this.converterContext.defaultYear();
    }

    @Override
    public int twoDigitYear() {
        return this.converterContext.twoDigitYear();
    }

    @Override
    public Locale locale() {
        return this.converterContext.locale();
    }

    @Override
    public MathContext mathContext() {
        return this.converterContext.mathContext();
    }

    private final ConverterContext converterContext;

    @Override
    public ExpressionNumberKind expressionNumberKind() {
        return this.expressionNumberKind;
    }

    private final ExpressionNumberKind expressionNumberKind;

    @Override
    public String toString() {
        return this.cell().toString();
    }
}

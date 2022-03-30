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
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.function.SpreadsheetExpressionFunctionContext;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.ExpressionReference;
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

    static BasicSpreadsheetEngineContextSpreadsheetExpressionFunctionContext with(final Optional<SpreadsheetCell> cell,
                                                                                  final SpreadsheetCellStore cellStore,
                                                                                  final AbsoluteUrl serverUrl,
                                                                                  final SpreadsheetMetadata spreadsheetMetadata,
                                                                                  final Function<FunctionExpressionName, ExpressionFunction<?, ExpressionFunctionContext>> functions,
                                                                                  final Function<ExpressionReference, Optional<Object>> references) {
        Objects.requireNonNull(cell, "cell");

        return new BasicSpreadsheetEngineContextSpreadsheetExpressionFunctionContext(
                cell,
                cellStore,
                serverUrl,
                spreadsheetMetadata,
                functions,
                references
        );
    }

    private BasicSpreadsheetEngineContextSpreadsheetExpressionFunctionContext(final Optional<SpreadsheetCell> cell,
                                                                              final SpreadsheetCellStore cellStore,
                                                                              final AbsoluteUrl serverUrl,
                                                                              final SpreadsheetMetadata spreadsheetMetadata,
                                                                              final Function<FunctionExpressionName, ExpressionFunction<?, ExpressionFunctionContext>> functions,
                                                                              final Function<ExpressionReference, Optional<Object>> references) {
        super();
        this.cell = cell;
        this.cellStore = cellStore;
        this.serverUrl = serverUrl;
        this.spreadsheetMetadata = spreadsheetMetadata;
        this.functions = functions;
        this.references = references;
    }

    // SpreadsheetExpressionFunctionContext............................................................................

    @Override
    public Optional<SpreadsheetCell> cell() {
        return this.cell;
    }

    private final Optional<SpreadsheetCell> cell;

    @Override
    public Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference cellReference) {
        Objects.requireNonNull(cellReference, "cellReference");

        Optional<SpreadsheetCell> loaded;

        for (; ; ) {
            Optional<SpreadsheetCell> maybeCell = this.cell();
            if (maybeCell.isPresent()) {
                final SpreadsheetCell cell = maybeCell.get();
                if (cell.reference().equalsIgnoreReferenceKind(cellReference)) {
                    loaded = maybeCell;
                    break;
                }
            }

            loaded = this.cellStore.load(cellReference);
            break;
        }

        return loaded;
    }

    private final SpreadsheetCellStore cellStore;

    @Override
    public SpreadsheetMetadata spreadsheetMetadata() {
        return this.spreadsheetMetadata;
    }

    private final SpreadsheetMetadata spreadsheetMetadata;

    @Override
    public AbsoluteUrl serverUrl() {
        return serverUrl;
    }

    private final AbsoluteUrl serverUrl;

    // ExpressionFunctionContext........................................................................................

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
    public Optional<Object> reference(final ExpressionReference reference) {
        return this.references.apply(reference);
    }

    private final Function<ExpressionReference, Optional<Object>> references;

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> target) {
        return this.converterContext()
                .canConvert(value, target);
    }

    @Override
    public <T> Either<T, String> convert(final Object value,
                                         final Class<T> type) {
        return this.converterContext()
                .convert(value, type);
    }

    @Override
    public int defaultYear() {
        return this.converterContext()
                .defaultYear();
    }

    @Override
    public int twoDigitYear() {
        return this.converterContext()
                .twoDigitYear();
    }

    @Override
    public String currencySymbol() {
        return this.converterContext()
                .currencySymbol();
    }

    @Override
    public char decimalSeparator() {
        return this.converterContext()
                .decimalSeparator();
    }

    @Override
    public String exponentSymbol() {
        return this.converterContext()
                .exponentSymbol();
    }

    @Override
    public char groupingSeparator() {
        return this.converterContext()
                .groupingSeparator();
    }

    @Override
    public char percentageSymbol() {
        return this.converterContext()
                .percentageSymbol();
    }

    @Override
    public char negativeSign() {
        return this.converterContext()
                .negativeSign();
    }

    @Override
    public char positiveSign() {
        return this.converterContext()
                .positiveSign();
    }

    @Override
    public Locale locale() {
        return this.converterContext()
                .locale();
    }

    @Override
    public MathContext mathContext() {
        return this.converterContext()
                .mathContext();
    }

    private final ConverterContext converterContext() {
        return this.spreadsheetMetadata()
                .converterContext();
    }

    @Override
    public ExpressionNumberKind expressionNumberKind() {
        return this.spreadsheetMetadata()
                .expressionNumberKind();
    }

    @Override
    public String toString() {
        return this.cell().toString();
    }
}

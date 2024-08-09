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

import walkingkooka.Cast;
import walkingkooka.Either;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.provider.ConverterProvider;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetErrorKind;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParsers;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolver;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.FunctionExpressionName;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProvider;

import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

final class BasicSpreadsheetExpressionEvaluationContext implements SpreadsheetExpressionEvaluationContext {

    static BasicSpreadsheetExpressionEvaluationContext with(final Optional<SpreadsheetCell> cell,
                                                            final SpreadsheetCellStore cellStore,
                                                            final AbsoluteUrl serverUrl,
                                                            final SpreadsheetMetadata spreadsheetMetadata,
                                                            final ConverterProvider converterProvider,
                                                            final ExpressionFunctionProvider expressionFunctionProvider,
                                                            final ProviderContext providerContext,
                                                            final Function<ExpressionReference, Optional<Optional<Object>>> references,
                                                            final SpreadsheetLabelNameResolver SpreadsheetLabelNameResolver,
                                                            final Supplier<LocalDateTime> now) {
        Objects.requireNonNull(cell, "cell");
        Objects.requireNonNull(cellStore, "cellStore");
        Objects.requireNonNull(serverUrl, "serverUrl");
        Objects.requireNonNull(spreadsheetMetadata, "spreadsheetMetadata");
        Objects.requireNonNull(converterProvider, "converterProvider");
        Objects.requireNonNull(expressionFunctionProvider, "expressionFunctionProvider");
        Objects.requireNonNull(providerContext, "providerContext");
        Objects.requireNonNull(references, "references");
        Objects.requireNonNull(SpreadsheetLabelNameResolver, "SpreadsheetLabelNameResolver");
        Objects.requireNonNull(now, "now");

        return new BasicSpreadsheetExpressionEvaluationContext(
                cell,
                cellStore,
                serverUrl,
                spreadsheetMetadata,
                converterProvider,
                expressionFunctionProvider,
                providerContext,
                references,
                SpreadsheetLabelNameResolver,
                now
        );
    }

    private BasicSpreadsheetExpressionEvaluationContext(final Optional<SpreadsheetCell> cell,
                                                        final SpreadsheetCellStore cellStore,
                                                        final AbsoluteUrl serverUrl,
                                                        final SpreadsheetMetadata spreadsheetMetadata,
                                                        final ConverterProvider converterProvider,
                                                        final ExpressionFunctionProvider expressionFunctionProvider,
                                                        final ProviderContext providerContext,
                                                        final Function<ExpressionReference, Optional<Optional<Object>>> references,
                                                        final SpreadsheetLabelNameResolver SpreadsheetLabelNameResolver,
                                                        final Supplier<LocalDateTime> now) {
        super();
        this.cell = cell;
        this.cellStore = cellStore;
        this.serverUrl = serverUrl;

        this.spreadsheetMetadata = spreadsheetMetadata;
        this.converterProvider = converterProvider;
        this.expressionFunctionProvider = expressionFunctionProvider;
        this.providerContext = providerContext;

        this.references = references;
        this.SpreadsheetLabelNameResolver = SpreadsheetLabelNameResolver;
        this.now = now;
    }

    // SpreadsheetExpressionEvaluationContext............................................................................

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
    public SpreadsheetParserToken parseFormula(final TextCursor expression) {
        Objects.requireNonNull(expression, "expression");

        final SpreadsheetParserContext parserContext = this.spreadsheetMetadata()
                .parserContext(this.now);

        return SpreadsheetParsers.expression()
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(expression, parserContext)
                .get()
                .cast(SpreadsheetParserToken.class);
    }

    @Override
    public SpreadsheetSelection resolveLabel(final SpreadsheetLabelName labelName) {
        return this.SpreadsheetLabelNameResolver.resolveLabel(labelName);
    }

    private final SpreadsheetLabelNameResolver SpreadsheetLabelNameResolver;

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

    // HasConverter.....................................................................................................

    @Override
    public Converter<SpreadsheetConverterContext> converter() {
        return this.spreadsheetMetadata.expressionConverter(
                this.converterProvider,
                this.providerContext
        );
    }

    private final ConverterProvider converterProvider;

    // ExpressionEvaluationContext........................................................................................

    @Override
    public CaseSensitivity caseSensitivity() {
        return CaseSensitivity.INSENSITIVE;
    }

    @Override
    public Object evaluate(final Expression expression) {
        Objects.requireNonNull(expression, "expression");

        Object result;

        try {
            result = expression.toValue(this);
        } catch (final RuntimeException exception) {
            result = this.handleException(exception);
        }

        return result;
    }

    @Override
    public ExpressionFunction<?, ExpressionEvaluationContext> expressionFunction(final FunctionExpressionName name) {
        return this.expressionFunctionProvider.expressionFunction(
                name,
                this.providerContext
        );
    }

    @Override
    public boolean isPure(final FunctionExpressionName name) {
        return this.expressionFunction(name)
                .isPure(this);
    }

    private final ExpressionFunctionProvider expressionFunctionProvider;

    @Override
    public <T> T prepareParameter(final ExpressionFunctionParameter<T> parameter,
                                  final Object value) {
        return parameter.convertOrFail(value, this);
    }

    @Override
    public Object evaluateFunction(final ExpressionFunction<?, ? extends ExpressionEvaluationContext> function,
                                   final List<Object> parameters) {
        return function
                .apply(
                        this.prepareParameters(function, parameters),
                        Cast.to(this)
                );
    }

    @Override
    public Object handleException(final RuntimeException exception) {
        return SpreadsheetErrorKind.translate(exception);
    }

    private final ProviderContext providerContext;

    @Override
    public Optional<Optional<Object>> reference(final ExpressionReference reference) {
        return this.references.apply(reference);
    }

    private final Function<ExpressionReference, Optional<Optional<Object>>> references;

    // Convert..........................................................................................................

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
    public long dateOffset() {
        return this.converterContext()
                .dateOffset();
    }

    // DateTimeContext.................................................................................................

    @Override
    public List<String> ampms() {
        return this.converterContext()
                .ampms();
    }

    @Override
    public int defaultYear() {
        return this.converterContext()
                .defaultYear();
    }

    @Override
    public List<String> monthNames() {
        return this.converterContext()
                .monthNames();
    }

    @Override
    public List<String> monthNameAbbreviations() {
        return this.converterContext()
                .monthNameAbbreviations();
    }

    @Override
    public LocalDateTime now() {
        return this.converterContext()
                .now();
    }

    @Override
    public int twoToFourDigitYear(final int year) {
        return this.converterContext()
                .twoToFourDigitYear(year);
    }

    @Override
    public int twoDigitYear() {
        return this.converterContext()
                .twoDigitYear();
    }

    @Override
    public List<String> weekDayNames() {
        return this.converterContext()
                .weekDayNames();
    }

    @Override
    public List<String> weekDayNameAbbreviations() {
        return this.converterContext()
                .weekDayNameAbbreviations();
    }

    // DecimalNumberContext.............................................................................................

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
    public ExpressionNumberKind expressionNumberKind() {
        return this.spreadsheetMetadata()
                .expressionNumberKind();
    }

    @Override
    public char groupSeparator() {
        return this.converterContext()
                .groupSeparator();
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

    private ConverterContext converterContext() {
        return this.spreadsheetMetadata()
                .converterContext(
                        this.converterProvider,
                        this.now,
                        this.SpreadsheetLabelNameResolver,
                        this.providerContext
                );
    }

    private final Supplier<LocalDateTime> now;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.cell().toString();
    }
}

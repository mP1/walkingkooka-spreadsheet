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

import walkingkooka.Either;
import walkingkooka.ToStringBuilder;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionNumberContext;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.FunctionExpressionName;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;

import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * A {@link ExpressionEvaluationContext} used to assist turning a {@link walkingkooka.spreadsheet.parser.SpreadsheetParserToken} into an {@link Expression}.
 * <br>
 * None of the expression or evaluation type methods should be called and all throw {@link UnsupportedOperationException}.
 */
final class SpreadsheetEngineSpreadsheetExpressionEvaluationContext implements SpreadsheetExpressionEvaluationContext {

    static SpreadsheetEngineSpreadsheetExpressionEvaluationContext with(final Optional<SpreadsheetCell> cell,
                                                                        final AbsoluteUrl serverUrl,
                                                                        final SpreadsheetEngineContext context) {
        Objects.requireNonNull(cell, "cell");
        Objects.requireNonNull(serverUrl, "serverUrl");
        Objects.requireNonNull(context, "context");

        return new SpreadsheetEngineSpreadsheetExpressionEvaluationContext(
                cell,
                serverUrl,
                context
        );
    }

    private SpreadsheetEngineSpreadsheetExpressionEvaluationContext(final Optional<SpreadsheetCell> cell,
                                                                    final AbsoluteUrl serverUrl,
                                                                    final SpreadsheetEngineContext context) {
        super();
        this.cell = cell;
        this.serverUrl = serverUrl;
        this.context = context;
    }

    @Override
    public CaseSensitivity caseSensitivity() {
        return CaseSensitivity.INSENSITIVE;
    }

    @Override
    public SpreadsheetExpressionEvaluationContext context(final Function<ExpressionReference, Optional<Optional<Object>>> scoped) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object evaluate(final Expression expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isPure(final FunctionExpressionName name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Optional<Object>> reference(ExpressionReference reference) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T prepareParameter(final ExpressionFunctionParameter<T> parameter,
                                  final Object value) {
        return parameter.convertOrFail(value, this);
    }

    @Override
    public ExpressionFunction<?, ExpressionEvaluationContext> function(final FunctionExpressionName name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object evaluateFunction(final ExpressionFunction<?, ? extends ExpressionEvaluationContext> function,
                                   final List<Object> parameters) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object handleException(final RuntimeException exception) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type) {
        return this.converterContext()
                .canConvert(value, type);
    }

    @Override
    public <T> Either<T, String> convert(final Object value,
                                         final Class<T> target) {
        return this.converterContext()
                .convert(value, target);
    }

    private ConverterContext converterContext() {
        if (null == this.converterContext) {
            final SpreadsheetEngineContext context = this.context;

            this.converterContext = this.spreadsheetMetadata()
                    .converterContext(
                            context::now,
                            context::resolveIfLabel
                    );
        }
        return this.converterContext;
    }

    private ConverterContext converterContext;

    // DateTimeContext.................................................................................................

    @Override
    public List<String> ampms() {
        return this.dateTimeContext()
                .ampms();
    }

    @Override
    public int defaultYear() {
        return this.dateTimeContext()
                .defaultYear();
    }

    @Override
    public List<String> monthNames() {
        return this.dateTimeContext()
                .monthNames();
    }

    @Override
    public List<String> monthNameAbbreviations() {
        return this.dateTimeContext()
                .monthNameAbbreviations();
    }

    @Override
    public LocalDateTime now() {
        return this.dateTimeContext()
                .now();
    }

    @Override
    public int twoToFourDigitYear(final int year) {
        return this.dateTimeContext()
                .twoToFourDigitYear(year);
    }

    @Override
    public int twoDigitYear() {
        return this.dateTimeContext()
                .twoDigitYear();
    }

    @Override
    public List<String> weekDayNames() {
        return this.dateTimeContext()
                .weekDayNames();
    }

    @Override
    public List<String> weekDayNameAbbreviations() {
        return this.dateTimeContext()
                .weekDayNameAbbreviations();
    }

    private DateTimeContext dateTimeContext() {
        if (null == this.dateTimeContext) {
            this.dateTimeContext = this.spreadsheetMetadata()
                    .dateTimeContext(
                            this.context::now
                    );
        }

        return this.dateTimeContext;
    }

    private DateTimeContext dateTimeContext;

    // DecimalNumberContext.............................................................................................

    @Override
    public String currencySymbol() {
        return this.expressionNumberContext()
                .currencySymbol();
    }

    @Override
    public char decimalSeparator() {
        return this.expressionNumberContext()
                .decimalSeparator();
    }

    @Override
    public String exponentSymbol() {
        return this.expressionNumberContext()
                .exponentSymbol();
    }

    @Override
    public char groupSeparator() {
        return this.expressionNumberContext()
                .groupSeparator();
    }

    @Override
    public char percentageSymbol() {
        return this.expressionNumberContext()
                .percentageSymbol();
    }

    @Override
    public char negativeSign() {
        return this.expressionNumberContext()
                .negativeSign();
    }

    @Override
    public char positiveSign() {
        return this.expressionNumberContext()
                .positiveSign();
    }

    private ExpressionNumberContext expressionNumberContext() {
        return this.spreadsheetMetadata()
                .expressionNumberContext();
    }

    @Override
    public Locale locale() {
        return this.spreadsheetMetadata()
                .getOrFail(SpreadsheetMetadataPropertyName.LOCALE);
    }

    @Override
    public MathContext mathContext() {
        return this.spreadsheetMetadata()
                .mathContext();
    }

    @Override
    public ExpressionNumberKind expressionNumberKind() {
        return this.spreadsheetMetadata()
                .expressionNumberKind();
    }

    // SpreadsheetExpressionEvaluationContext...........................................................................

    @Override
    public Optional<SpreadsheetCell> cell() {
        return this.cell;
    }

    private final Optional<SpreadsheetCell> cell;

    @Override
    public Converter<SpreadsheetConverterContext> converter() {
        return this.spreadsheetMetadata()
                .converter();
    }

    @Override
    public Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference cell) {
        return this.context.storeRepository()
                .cells()
                .load(cell);
    }

    @Override
    public SpreadsheetParserToken parseExpression(final TextCursor formula) {
        return this.context.parseFormula(formula);
    }

    @Override
    public SpreadsheetSelection resolveIfLabel(final SpreadsheetSelection selection) {
        return this.context.resolveIfLabel(selection);
    }

    @Override
    public SpreadsheetMetadata spreadsheetMetadata() {
        return this.context.metadata();
    }

    @Override
    public AbsoluteUrl serverUrl() {
        return this.serverUrl;
    }

    private final AbsoluteUrl serverUrl;

    private final SpreadsheetEngineContext context;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return ToStringBuilder.empty()
                .separator(" ")
                .value(this.cell)
                .value(this.serverUrl)
                .value(this.context)
                .build();
    }
}

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

import walkingkooka.ToStringBuilder;
import walkingkooka.collect.set.Sets;
import walkingkooka.convert.provider.ConverterProvider;
import walkingkooka.convert.provider.ConverterProviderDelegator;
import walkingkooka.math.Fraction;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContextDelegator;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetErrorKind;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorProvider;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorProviderDelegator;
import walkingkooka.spreadsheet.conditionalformat.SpreadsheetConditionalFormattingRule;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContexts;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProvider;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProviderDelegator;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserProvider;
import walkingkooka.spreadsheet.parser.SpreadsheetParserProviderDelegator;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParsers;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.text.LineEnding;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.FunctionExpressionName;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionInfo;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProvider;
import walkingkooka.tree.text.TextNode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A basic and simple {@link SpreadsheetEngineContext}. Its accepts a variety of dependencies and uses them to handle
 * public methods requests.
 */
final class BasicSpreadsheetEngineContext implements SpreadsheetEngineContext,
        ConverterProviderDelegator,
        SpreadsheetComparatorProviderDelegator,
        SpreadsheetFormatterProviderDelegator,
        SpreadsheetParserProviderDelegator,
        ProviderContextDelegator {

    /**
     * Creates a new {@link BasicSpreadsheetEngineContext}
     */
    static BasicSpreadsheetEngineContext with(final SpreadsheetMetadata metadata,
                                              final ConverterProvider converterProvider,
                                              final SpreadsheetComparatorProvider spreadsheetComparatorProvider,
                                              final SpreadsheetFormatterProvider spreadsheetFormatterProvider,
                                              final ExpressionFunctionProvider expressionFunctionProvider,
                                              final SpreadsheetParserProvider spreadsheetParserProvider,
                                              final ProviderContext providerContext,
                                              final SpreadsheetEngine engine,
                                              final Function<BigDecimal, Fraction> fractioner,
                                              final SpreadsheetStoreRepository storeRepository,
                                              final AbsoluteUrl serverUrl,
                                              final Supplier<LocalDateTime> now) {
        Objects.requireNonNull(metadata, "metadata");
        Objects.requireNonNull(converterProvider, "converterProvider");
        Objects.requireNonNull(spreadsheetComparatorProvider, "spreadsheetComparatorProvider");
        Objects.requireNonNull(spreadsheetFormatterProvider, "spreadsheetFormatterProvider");
        Objects.requireNonNull(expressionFunctionProvider, "expressionFunctionProvider");
        Objects.requireNonNull(spreadsheetParserProvider, "spreadsheetParserProvider");
        Objects.requireNonNull(providerContext, "providerContext");
        Objects.requireNonNull(engine, "engine");
        Objects.requireNonNull(fractioner, "fractioner");
        Objects.requireNonNull(storeRepository, "storeRepository");
        Objects.requireNonNull(serverUrl, "serverUrl");
        Objects.requireNonNull(now, "now");

        return new BasicSpreadsheetEngineContext(
                metadata,
                converterProvider,
                spreadsheetComparatorProvider,
                spreadsheetFormatterProvider,
                expressionFunctionProvider,
                spreadsheetParserProvider,
                providerContext,
                engine,
                fractioner,
                storeRepository,
                serverUrl,
                now
        );
    }

    /**
     * Private ctor use factory.
     */
    private BasicSpreadsheetEngineContext(final SpreadsheetMetadata metadata,
                                          final ConverterProvider converterProvider,
                                          final SpreadsheetComparatorProvider spreadsheetComparatorProvider,
                                          final SpreadsheetFormatterProvider spreadsheetFormatterProvider,
                                          final ExpressionFunctionProvider expressionFunctionProvider,
                                          final SpreadsheetParserProvider spreadsheetParserProvider,
                                          final ProviderContext providerContext,
                                          final SpreadsheetEngine engine,
                                          final Function<BigDecimal, Fraction> fractioner,
                                          final SpreadsheetStoreRepository storeRepository,
                                          final AbsoluteUrl serverUrl,
                                          final Supplier<LocalDateTime> now) {
        super();

        this.metadata = metadata;

        this.converterProvider = converterProvider;

        this.spreadsheetComparatorProvider = spreadsheetComparatorProvider;

        this.parserContext = metadata.parserContext(now);

        this.expressionFunctionProvider = expressionFunctionProvider;
        this.referenceFunction = SpreadsheetEnginesExpressionReferenceFunction.with(
                engine,
                this
        );

        this.spreadsheetFormatterProvider = spreadsheetFormatterProvider;
        this.spreadsheetParserProvider = spreadsheetParserProvider;
        this.providerContext = providerContext;

        this.fractioner = fractioner;

        this.storeRepository = storeRepository;
        this.serverUrl = serverUrl;

        this.now = now;
    }

    // metadata........................................................................................................

    @Override
    public SpreadsheetMetadata spreadsheetMetadata() {
        return this.metadata;
    }

    private final SpreadsheetMetadata metadata;

    // resolveLabel.............................................................................................

    @Override
    public SpreadsheetSelection resolveLabel(final SpreadsheetLabelName labelName) {
        return BasicSpreadsheetEngineContextResolveLabelSpreadsheetSelectionVisitor.resolveLabel(
                labelName,
                this.storeRepository()
                        .labels()
        );
    }

    // ConverterProvider................................................................................................

    @Override
    public ConverterProvider converterProvider() {
        return this.converterProvider;
    }

    private final ConverterProvider converterProvider;

    // spreadsheetComparatorProvider.................................................................................................

    @Override
    public SpreadsheetComparatorProvider spreadsheetComparatorProvider() {
        return this.spreadsheetComparatorProvider;
    }

    private final SpreadsheetComparatorProvider spreadsheetComparatorProvider;

    // expressionFunctionProvider.................................................................................................

    @Override
    public ExpressionFunction<?, ExpressionEvaluationContext> expressionFunction(final FunctionExpressionName name,
                                                                                 final ProviderContext context) {
        return this.expressionFunctionProvider.expressionFunction(
                name,
                context
        );
    }

    @Override
    public Set<ExpressionFunctionInfo> expressionFunctionInfos() {
        return this.expressionFunctionProvider.expressionFunctionInfos();
    }

    private final ExpressionFunctionProvider expressionFunctionProvider;

    // parsing formula and executing.....................................................................................

    @Override
    public SpreadsheetParserToken parseFormula(final TextCursor formula) {
        return SpreadsheetParsers.valueOrExpression(
                        this.metadata.parser(
                                this, // SpreadsheetParserProvider
                                this // ProviderContext
                        )
                )
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(formula, this.parserContext)
                .get()
                .cast(SpreadsheetParserToken.class);
    }

    @Override
    public Optional<Expression> toExpression(final SpreadsheetParserToken token) {
        Objects.requireNonNull(token, "token");

        return token.toExpression(
                this.expressionEvaluationContext(
                        Optional.empty()// cell
                )
        );
    }

    /**
     * This parser is used to parse strings, date, date/time, time and numbers outside an expression but within a formula.
     */
    private final SpreadsheetParserContext parserContext;

    @Override
    public boolean isPure(final FunctionExpressionName function) {
        return this.expressionFunctionProvider.expressionFunction(
                function,
                        this
                ).isPure(this);
    }

    @Override
    public Object evaluate(final Expression expression,
                           final Optional<SpreadsheetCell> cell) {
        Objects.requireNonNull(expression, "expression");
        Objects.requireNonNull(cell, "cell");

        Object result;

        try {
            result = expression.toValue(
                    this.expressionEvaluationContext(cell)
            );
        } catch (final RuntimeException exception) {
            result = SpreadsheetErrorKind.translate(exception);
        }

        return result;
    }

    private SpreadsheetExpressionEvaluationContext expressionEvaluationContext(final Optional<SpreadsheetCell> cell) {
        return SpreadsheetExpressionEvaluationContexts.basic(
                cell,
                this.storeRepository.cells(),
                this.serverUrl,
                this.spreadsheetMetadata(),
                this.converterProvider,
                this.expressionFunctionProvider,
                this, // ProviderContext
                this.referenceFunction,
                this::resolveIfLabel,
                this.now
        );
    }

    private final AbsoluteUrl serverUrl;

    private final SpreadsheetEnginesExpressionReferenceFunction referenceFunction;

    // HasNow...........................................................................................................

    @Override
    public LocalDateTime now() {
        return this.now.get();
    }

    private final Supplier<LocalDateTime> now;

    /**
     * Used to convert a number into a fraction within expressions.
     */
    private final Function<BigDecimal, Fraction> fractioner;

    // formatValue......................................................................................................

    @Override
    public Optional<TextNode> formatValue(final Object value,
                                          final SpreadsheetFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");

        return formatter.format(
                value,
                this.spreadsheetMetadata()
                        .formatterContext(
                                this.converterProvider,
                                this.spreadsheetFormatterProvider,
                                this::now,
                                this::resolveIfLabel,
                                this // ProviderContext
                        )
        );
    }

    // SpreadsheetFormatterProvider.....................................................................................

    @Override
    public SpreadsheetFormatterProvider spreadsheetFormatterProvider() {
        return this.spreadsheetFormatterProvider;
    }

    private final SpreadsheetFormatterProvider spreadsheetFormatterProvider;

    // FORMAT .........................................................................................................

    /**
     * If a value is present use the {@link SpreadsheetFormatter} and apply the styling.
     */
    @Override
    public SpreadsheetCell formatValueAndStyle(final SpreadsheetCell cell,
                                               final Optional<SpreadsheetFormatter> formatter) {
        Objects.requireNonNull(cell, "cell");
        Objects.requireNonNull(formatter, "formatter");

        final SpreadsheetFormula formula = cell
                .formula();
        final Optional<Object> value = formula.value();

        return value.isPresent() ?
                this.applyConditionalRules(
                        cell.setFormattedValue(
                                Optional.of(
                                        this.formatValue(
                                                        value.get(),
                                                        formatter.orElse(
                                                                this.spreadsheetMetadata()
                                                                        .formatter(
                                                                                this.spreadsheetFormatterProvider,
                                                                                this // ProviderContext
                                                                        )
                                                        )
                                                )
                                                .map(
                                                        f -> cell.style()
                                                                .replace(f)
                                                )
                                                .orElse(TextNode.EMPTY_TEXT)
                                )
                        )
                ) :
                cell;
    }

    /**
     * Locates and formats the cell using any matching conditional formatting rules.
     */
    private SpreadsheetCell applyConditionalRules(final SpreadsheetCell cell) {
        SpreadsheetCell formatted = cell;

        // load rules for cell
        final Set<SpreadsheetConditionalFormattingRule> rules = Sets.sorted(SpreadsheetConditionalFormattingRule.PRIORITY_COMPARATOR);
        rules.addAll(
                this.storeRepository()
                        .rangeToConditionalFormattingRules()
                        .loadCellReferenceValues(cell.reference())
        );

        // apply them
        for (final SpreadsheetConditionalFormattingRule rule : rules) {
            final boolean ruleResult = this.evaluateAsBoolean(
                    rule.formula()
                            .expression()
                            .get(),
                    Optional.of(
                            cell
                    )
            );
            if (Boolean.TRUE.equals(ruleResult)) {
                final TextNode formattedText = cell.formattedValue()
                        .orElseThrow(() -> new BasicSpreadsheetEngineException("Missing formattedValue cell=" + cell));
                formatted = formatted.setFormattedValue(
                        Optional.of(
                                rule.style()
                                        .apply(cell)
                                        .replace(formattedText)
                        )
                );
                break;
            }
        }
        return formatted;
    }

    // SpreadsheetParserProvider........................................................................................

    @Override
    public SpreadsheetParserProvider spreadsheetParserProvider() {
        return this.spreadsheetParserProvider;
    }

    private final SpreadsheetParserProvider spreadsheetParserProvider;

    // ProviderContextDelegator.........................................................................................

    @Override
    public ProviderContext providerContext() {
        return this.providerContext;
    }

    private final ProviderContext providerContext;

    // Store............................................................................................................

    @Override
    public SpreadsheetStoreRepository storeRepository() {
        return this.storeRepository;
    }

    private final SpreadsheetStoreRepository storeRepository;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return ToStringBuilder.empty()
                .globalLength(Integer.MAX_VALUE)
                .valueLength(Integer.MAX_VALUE)
                .label("metadata")
                .value(this.metadata)
                .append(LineEnding.SYSTEM)
                .label("fractioner").value(this.fractioner)
                .append(LineEnding.SYSTEM)
                .label("serverUrl").value(this.serverUrl)
                .build();
    }
}

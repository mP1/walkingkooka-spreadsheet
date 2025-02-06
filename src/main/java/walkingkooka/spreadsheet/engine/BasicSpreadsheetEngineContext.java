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
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.convert.provider.ConverterSelector;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContextDelegator;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetCellRange;
import walkingkooka.spreadsheet.SpreadsheetErrorKind;
import walkingkooka.spreadsheet.compare.SpreadsheetColumnOrRowSpreadsheetComparatorNames;
import walkingkooka.spreadsheet.compare.SpreadsheetColumnOrRowSpreadsheetComparatorNamesList;
import walkingkooka.spreadsheet.compare.SpreadsheetColumnOrRowSpreadsheetComparators;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorName;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorNameList;
import walkingkooka.spreadsheet.conditionalformat.SpreadsheetConditionalFormattingRule;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContexts;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.formula.SpreadsheetFormulaParsers;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.provider.SpreadsheetProvider;
import walkingkooka.spreadsheet.provider.SpreadsheetProviderDelegator;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolver;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolvers;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.text.LineEnding;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionAliasSet;
import walkingkooka.tree.text.TextNode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * A basic and simple {@link SpreadsheetEngineContext}. Its accepts a variety of dependencies and uses them to handle
 * public methods requests.
 */
final class BasicSpreadsheetEngineContext implements SpreadsheetEngineContext,
        SpreadsheetProviderDelegator,
        ProviderContextDelegator {

    /**
     * Creates a new {@link BasicSpreadsheetEngineContext}
     */
    static BasicSpreadsheetEngineContext with(final AbsoluteUrl serverUrl,
                                              final SpreadsheetMetadata metadata,
                                              final SpreadsheetEngine engine,
                                              final SpreadsheetStoreRepository storeRepository,
                                              final SpreadsheetMetadataPropertyName<ExpressionFunctionAliasSet> functionAliases,
                                              final SpreadsheetProvider spreadsheetProvider,
                                              final ProviderContext providerContext) {
        Objects.requireNonNull(serverUrl, "serverUrl");
        Objects.requireNonNull(metadata, "metadata");
        Objects.requireNonNull(engine, "engine");
        Objects.requireNonNull(storeRepository, "storeRepository");
        Objects.requireNonNull(functionAliases, "functionAliases");
        Objects.requireNonNull(spreadsheetProvider, "spreadsheetProvider");
        Objects.requireNonNull(providerContext, "providerContext");


        return new BasicSpreadsheetEngineContext(
                serverUrl,
                metadata,
                engine,
                storeRepository,
                functionAliases,
                spreadsheetProvider,
                providerContext
        );
    }

    /**
     * Private ctor use factory.
     */
    private BasicSpreadsheetEngineContext(final AbsoluteUrl serverUrl,
                                          final SpreadsheetMetadata metadata,
                                          final SpreadsheetEngine engine,
                                          final SpreadsheetStoreRepository storeRepository,
                                          final SpreadsheetMetadataPropertyName<ExpressionFunctionAliasSet> functionAliases,
                                          final SpreadsheetProvider spreadsheetProvider,
                                          final ProviderContext providerContext) {
        super();

        this.serverUrl = serverUrl;

        this.metadata = metadata;

        this.referenceToValue = SpreadsheetEnginesExpressionReferenceToValueFunction.with(
                engine,
                this
        );

        this.storeRepository = storeRepository;

        this.labelNameResolver = SpreadsheetLabelNameResolvers.labelStore(
                storeRepository.labels()
        );

        this.engine = engine;

        this.functionAliases = functionAliases;
        this.spreadsheetProvider = spreadsheetProvider;
        this.providerContext = providerContext;

        this.parserContext = metadata.spreadsheetParserContext(providerContext);
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
        return this.labelNameResolver.resolveLabel(labelName);
    }

    private final SpreadsheetLabelNameResolver labelNameResolver;

    // parsing formula and executing.....................................................................................

    @Override
    public SpreadsheetFormulaParserToken parseFormula(final TextCursor formula) {
        return SpreadsheetFormulaParsers.valueOrExpression(
                        this.metadata.spreadsheetParser(
                                this, // SpreadsheetParserProvider
                                this // ProviderContext
                        )
                )
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(formula, this.parserContext)
                .get()
                .cast(SpreadsheetFormulaParserToken.class);
    }

    @Override
    public Optional<Expression> toExpression(final SpreadsheetFormulaParserToken token) {
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
    public boolean isPure(final ExpressionFunctionName function) {
        return this.spreadsheetProvider.expressionFunction(
                function,
                Lists.empty(),
                this
        ).isPure(this);
    }

    @Override
    public SpreadsheetEngineContext spreadsheetEngineContext(final SpreadsheetMetadataPropertyName<ExpressionFunctionAliasSet> functionAliases) {
        Objects.requireNonNull(functionAliases, "functionAliases");

        return this.functionAliases.equals(functionAliases) ?
                this :
                new BasicSpreadsheetEngineContext(
                        this.serverUrl,
                        this.metadata,
                        this.engine,
                        this.storeRepository,
                        functionAliases,
                        this.spreadsheetProvider,
                        this.providerContext
                );
    }

    private final SpreadsheetEngine engine;

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

    @Override
    public boolean evaluateAsBoolean(final Expression expression,
                                     final Optional<SpreadsheetCell> cell) {
        Objects.requireNonNull(expression, "expression");
        Objects.requireNonNull(cell, "cell");

        boolean result;

        try {
            result = expression.toBoolean(
                    this.expressionEvaluationContext(cell)
            );
        } catch (final RuntimeException exception) {
            result = false; // return false for any errors.
        }

        return result;
    }

    private SpreadsheetExpressionEvaluationContext expressionEvaluationContext(final Optional<SpreadsheetCell> cell) {
        Objects.requireNonNull(cell, "cell");

        final SpreadsheetProvider spreadsheetProvider = this.spreadsheetProvider;
        final SpreadsheetMetadata metadata = this.spreadsheetMetadata();

        final SpreadsheetMetadataPropertyName<ExpressionFunctionAliasSet> functionAliases = this.functionAliases;

        final SpreadsheetMetadataPropertyName<ConverterSelector> converterSelector;

        if (SpreadsheetMetadataPropertyName.FORMULA_FUNCTIONS.equals(functionAliases)) {
            converterSelector = SpreadsheetMetadataPropertyName.FORMULA_CONVERTER;
        } else {
            if (SpreadsheetMetadataPropertyName.FIND_FUNCTIONS.equals(functionAliases)) {
                converterSelector = SpreadsheetMetadataPropertyName.FIND_CONVERTER;
            } else {
                throw new IllegalArgumentException("Missing " + ConverterSelector.class.getSimpleName() + " for  " + functionAliases);
            }
        }

        return SpreadsheetExpressionEvaluationContexts.basic(
                cell,
                this.storeRepository,
                this.serverUrl,
                this.referenceToValue,
                metadata,
                metadata.spreadsheetConverterContext(
                        converterSelector,
                        this, // SpreadsheetLabelNameResolver,
                        spreadsheetProvider, // SpreadsheetConverterProvider
                        this.providerContext
                ),
                metadata.expressionFunctionProvider(
                        functionAliases,
                        spreadsheetProvider
                ), // ExpressionFunctionProvider,
                this // ProviderContext
        );
    }

    private final SpreadsheetMetadataPropertyName<ExpressionFunctionAliasSet> functionAliases;

    private final AbsoluteUrl serverUrl;

    private final SpreadsheetEnginesExpressionReferenceToValueFunction referenceToValue;

    // HasNow...........................................................................................................

    @Override
    public LocalDateTime now() {
        return this.providerContext.now();
    }

    // formatValue......................................................................................................

    @Override
    public Optional<TextNode> formatValue(final Object value,
                                          final SpreadsheetFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");

        return formatter.format(
                value,
                this.spreadsheetMetadata()
                        .spreadsheetFormatterContext(
                                this, // SpreadsheetLabelNameResolver,
                                this.spreadsheetProvider, // ConverterProvider,
                                this.spreadsheetProvider, // SpreadsheetFormatterProvider,
                                this // ProviderContext
                        )
        );
    }

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
                                                                        .spreadsheetFormatter(
                                                                                this.spreadsheetProvider, // SpreadsheetFormatterProvider,
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
        final Set<SpreadsheetConditionalFormattingRule> rules = SortedSets.tree(SpreadsheetConditionalFormattingRule.PRIORITY_COMPARATOR);
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

    // sort.............................................................................................................

    @Override
    public SpreadsheetCellRange sortCells(final SpreadsheetCellRange cells,
                                          final List<SpreadsheetColumnOrRowSpreadsheetComparatorNames> comparators,
                                          final BiConsumer<SpreadsheetCell, SpreadsheetCell> movedFromTo) {
        Objects.requireNonNull(cells, "cells");
        Objects.requireNonNull(comparators, "comparators");
        Objects.requireNonNull(movedFromTo, "movedFromTo");

        return this.sortCells0(
                cells,
                SpreadsheetColumnOrRowSpreadsheetComparatorNamesList.with(comparators),
                movedFromTo
        );
    }

    private SpreadsheetCellRange sortCells0(final SpreadsheetCellRange cells,
                                            final SpreadsheetColumnOrRowSpreadsheetComparatorNamesList columnOrRowAndComparatorNames,
                                            final BiConsumer<SpreadsheetCell, SpreadsheetCell> movedFromTo) {

        final SpreadsheetMetadata metadata = this.spreadsheetMetadata();
        final SpreadsheetComparatorNameList sortComparators = metadata.getOrFail(SpreadsheetMetadataPropertyName.SORT_COMPARATORS);

        final Set<SpreadsheetComparatorName> requiredNames = columnOrRowAndComparatorNames.names();

        final String missing = requiredNames.stream()
                .filter(n -> false == sortComparators.contains(n))
                .map(SpreadsheetComparatorName::toString)
                .collect(Collectors.joining(","));
        if (false == missing.isEmpty()) {
            throw new IllegalArgumentException("Invalid comparators: " + missing);
        }

        final List<SpreadsheetColumnOrRowSpreadsheetComparators> comparators = columnOrRowAndComparatorNames.stream()
                .map(n -> SpreadsheetColumnOrRowSpreadsheetComparators.with(
                        n.columnOrRow(),
                        n.comparatorNameAndDirections()
                                .stream()
                                .map(
                                        nad -> nad.direction()
                                                .apply(
                                                        this.spreadsheetComparator(
                                                                nad.name(),
                                                                Lists.empty(),
                                                                this // ProviderContext
                                                        )
                                                )
                                ).collect(Collectors.toList())
                )).collect(Collectors.toList());

        return cells.sort(
                comparators,
                movedFromTo, // moved cells
                metadata.sortSpreadsheetComparatorContext(
                        this, // ConverterProvider
                        this, // SpreadsheetLabelNameResolver
                        this // ProviderContext
                )
        );
    }

    // Store............................................................................................................

    @Override
    public SpreadsheetStoreRepository storeRepository() {
        return this.storeRepository;
    }

    private final SpreadsheetStoreRepository storeRepository;

    // SpreadsheetProvider..............................................................................................

    @Override
    public SpreadsheetProvider spreadsheetProvider() {
        return this.spreadsheetProvider;
    }

    private final SpreadsheetProvider spreadsheetProvider;

    // ProviderContextDelegator.........................................................................................

    @Override
    public ProviderContext providerContext() {
        return this.providerContext;
    }

    private final ProviderContext providerContext;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return ToStringBuilder.empty()
                .globalLength(Integer.MAX_VALUE)
                .valueLength(Integer.MAX_VALUE)
                .label("serverUrl").value(this.serverUrl)
                .value(LineEnding.NL)
                .label("metadata")
                .value(this.metadata)
                .build();
    }
}

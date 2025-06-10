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
import walkingkooka.spreadsheet.conditionalformat.SpreadsheetConditionalFormattingRule;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContexts;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.formula.SpreadsheetFormulaParsers;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.provider.SpreadsheetProvider;
import walkingkooka.spreadsheet.provider.SpreadsheetProviderDelegator;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoader;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoaders;
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
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProvider;
import walkingkooka.tree.text.TextNode;
import walkingkooka.validation.form.FormHandlerContext;
import walkingkooka.validation.form.FormHandlerContexts;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

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
                                              final SpreadsheetStoreRepository storeRepository,
                                              final SpreadsheetMetadataPropertyName<ExpressionFunctionAliasSet> functionAliases,
                                              final SpreadsheetProvider spreadsheetProvider,
                                              final ProviderContext providerContext) {
        Objects.requireNonNull(serverUrl, "serverUrl");
        Objects.requireNonNull(metadata, "metadata");
        Objects.requireNonNull(storeRepository, "storeRepository");
        Objects.requireNonNull(functionAliases, "functionAliases");
        Objects.requireNonNull(spreadsheetProvider, "spreadsheetProvider");
        Objects.requireNonNull(providerContext, "providerContext");


        return new BasicSpreadsheetEngineContext(
                serverUrl,
                metadata,
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
                                          final SpreadsheetStoreRepository storeRepository,
                                          final SpreadsheetMetadataPropertyName<ExpressionFunctionAliasSet> functionAliases,
                                          final SpreadsheetProvider spreadsheetProvider,
                                          final ProviderContext providerContext) {
        super();

        this.serverUrl = serverUrl;

        this.metadata = metadata;

        this.storeRepository = storeRepository;

        this.labelNameResolver = SpreadsheetLabelNameResolvers.labelStore(
                storeRepository.labels()
        );

        this.functionAliases = functionAliases;
        this.spreadsheetProvider = spreadsheetProvider;
        this.providerContext = providerContext;
    }

    @Override
    public AbsoluteUrl serverUrl() {
        return this.serverUrl;
    }

    private final AbsoluteUrl serverUrl;

    // metadata.........................................................................................................

    @Override
    public SpreadsheetMetadata spreadsheetMetadata() {
        return this.metadata;
    }

    private final SpreadsheetMetadata metadata;

    // SpreadsheetEngineContext.........................................................................................

    @Override
    public SpreadsheetEngineContext spreadsheetEngineContext(final SpreadsheetMetadataPropertyName<ExpressionFunctionAliasSet> functionAliases) {
        Objects.requireNonNull(functionAliases, "functionAliases");

        return this.functionAliases.equals(functionAliases) ?
                this :
                new BasicSpreadsheetEngineContext(
                        this.serverUrl,
                        this.metadata,
                        this.storeRepository,
                        functionAliases,
                        this.spreadsheetProvider,
                        this.providerContext
                );
    }

    // resolveLabel.....................................................................................................

    @Override
    public Optional<SpreadsheetSelection> resolveLabel(final SpreadsheetLabelName labelName) {
        return this.labelNameResolver.resolveLabel(labelName);
    }

    private final SpreadsheetLabelNameResolver labelNameResolver;

    // parsing formula and executing....................................................................................

    @Override
    public SpreadsheetFormulaParserToken parseFormula(final TextCursor formula,
                                                      final Optional<SpreadsheetCell> cell) {
        return SpreadsheetFormulaParsers.valueOrExpression(
                        this.metadata.spreadsheetParser(
                                this, // SpreadsheetParserProvider
                                this // ProviderContext
                        )
                )
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(
                        formula,
                        this.metadata.spreadsheetParserContext(
                                cell,
                                this // HasNow
                        )
                ).get()
                .cast(SpreadsheetFormulaParserToken.class);
    }

    @Override
    public Optional<Expression> toExpression(final SpreadsheetFormulaParserToken token) {
        Objects.requireNonNull(token, "token");

        return token.toExpression(
                this.spreadsheetExpressionEvaluationContext(
                        NO_CELL,
                        SpreadsheetExpressionReferenceLoaders.fake() // toExpression never loads references
                )
        );
    }

    @Override
    public SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext(final Optional<SpreadsheetCell> cell,
                                                                                         final SpreadsheetExpressionReferenceLoader loader) {
        Objects.requireNonNull(cell, "cell");
        Objects.requireNonNull(loader, "loader");

        final SpreadsheetMetadata metadata = this.spreadsheetMetadata();

        {
            final SpreadsheetMetadataPropertyName<ExpressionFunctionAliasSet> functionAliases = this.functionAliases;
            final SpreadsheetProvider spreadsheetProvider = this.spreadsheetProvider;

            // lazily create SpreadsheetConverterContext & ExpressionFunctionProvider, these can be shared even when
            // the $cell changes
            if (null == this.expressionFunctionProvider) {
                this.expressionFunctionProvider = metadata.expressionFunctionProvider(
                        functionAliases,
                        spreadsheetProvider
                );
            }

            if (null == this.spreadsheetConverterContext) {
                final SpreadsheetMetadataPropertyName<ConverterSelector> converterSelector;

                if (SpreadsheetMetadataPropertyName.FORMULA_FUNCTIONS.equals(functionAliases)) {
                    converterSelector = SpreadsheetMetadataPropertyName.FORMULA_CONVERTER;
                } else {
                    if (SpreadsheetMetadataPropertyName.FIND_FUNCTIONS.equals(functionAliases)) {
                        converterSelector = SpreadsheetMetadataPropertyName.FIND_CONVERTER;
                    } else {
                        if (SpreadsheetMetadataPropertyName.VALIDATOR_FUNCTIONS.equals(functionAliases)) {
                            converterSelector = SpreadsheetMetadataPropertyName.VALIDATOR_CONVERTER;
                        } else {
                            throw new IllegalArgumentException("Missing " + ConverterSelector.class.getSimpleName() + " for  " + functionAliases);
                        }
                    }
                }

                this.spreadsheetConverterContext = metadata.spreadsheetConverterContext(
                        cell,
                        SpreadsheetMetadata.NO_VALIDATION_REFERENCE,
                        converterSelector,
                        this, // SpreadsheetLabelNameResolver,
                        spreadsheetProvider, // SpreadsheetConverterProvider
                        this.providerContext
                );
            }

            if(null == this.formHandlerContext) {
                final FormHandlerContext<SpreadsheetExpressionReference, SpreadsheetDelta> formHandlerContext;
                if(SpreadsheetMetadataPropertyName.VALIDATOR_FUNCTIONS.equals(functionAliases)) {
                    // create from spreadsheetProvider using SpreadsheetMetadataPropertyName.VALIDATOR_FORM_HANDLER
                    // https://github.com/mP1/walkingkooka-spreadsheet/issues/6342
                    formHandlerContext = FormHandlerContexts.fake();
                } else {
                    formHandlerContext = FormHandlerContexts.fake();
                }
            }
        }

        return SpreadsheetExpressionEvaluationContexts.basic(
                cell,
                loader,
                this.serverUrl,
                metadata,
                this.storeRepository,
                this.spreadsheetConverterContext,
                FormHandlerContexts.fake(),
                this.expressionFunctionProvider,
                this // ProviderContext
        );
    }

    private final SpreadsheetMetadataPropertyName<ExpressionFunctionAliasSet> functionAliases;

    /**
     * Cache and share with all future {@link SpreadsheetExpressionEvaluationContext}.
     */
    private SpreadsheetConverterContext spreadsheetConverterContext;

    /**
     * Cached {@link FormHandlerContext}.
     */
    private FormHandlerContext<SpreadsheetExpressionReference, SpreadsheetDelta> formHandlerContext;

    /**
     * Cache and share with all future {@link SpreadsheetExpressionEvaluationContext}.
     */
    private ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider;

    @Override
    public boolean isPure(final ExpressionFunctionName function) {
        return this.spreadsheetProvider.expressionFunction(
                function,
                Lists.empty(),
                this
        ).isPure(this);
    }

    // formatValue......................................................................................................

    @Override
    public Optional<TextNode> formatValue(final SpreadsheetCell cell,
                                          final Optional<Object> value,
                                          final SpreadsheetFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");

        return formatter.format(
                value,
                this.spreadsheetMetadata()
                        .spreadsheetFormatterContext(
                                Optional.of(cell),
                                (final Optional<Object> v) -> this.spreadsheetEngineContext(
                                        SpreadsheetMetadataPropertyName.FORMATTING_FUNCTIONS
                                ).spreadsheetExpressionEvaluationContext(
                                        Optional.of(cell),
                                        SpreadsheetExpressionReferenceLoaders.fake()
                                ).addLocalVariable(
                                        SpreadsheetExpressionEvaluationContext.FORMAT_VALUE,
                                        v
                                ),
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
        final Optional<Object> value = formula.errorOrValue();

        return this.applyConditionalRules(
                cell.setFormattedValue(
                        Optional.of(
                                this.formatValue(
                                                cell,
                                                value,
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
        );
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
                        .findValuesWithCell(cell.reference())
        );

        // apply them
        for (final SpreadsheetConditionalFormattingRule rule : rules) {
            final boolean ruleResult = rule.formula()
                    .expression()
                    .get()
                    .toBoolean(
                            this.spreadsheetExpressionEvaluationContext(
                                    Optional.of(
                                            cell
                                    ),
                                    SpreadsheetExpressionReferenceLoaders.fake() ///
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

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
import walkingkooka.convert.CanConvert;
import walkingkooka.convert.CanConvertDelegator;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContextDelegator;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.locale.LocaleContext;
import walkingkooka.locale.LocaleContextDelegator;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.net.http.server.HttpHandler;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.route.Router;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetContext;
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.SpreadsheetErrorKind;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContexts;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;
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
import walkingkooka.terminal.TerminalContext;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProvider;
import walkingkooka.tree.text.TextNode;
import walkingkooka.validation.form.FormHandlerContext;
import walkingkooka.validation.form.FormHandlerContexts;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * A basic and simple {@link SpreadsheetEngineContext}. Its accepts a variety of dependencies and uses them to handle
 * public methods requests.
 */
final class BasicSpreadsheetEngineContext implements SpreadsheetEngineContext,
    EnvironmentContextDelegator,
    LocaleContextDelegator,
    CanConvertDelegator,
    SpreadsheetProviderDelegator {

    /**
     * Creates a new {@link BasicSpreadsheetEngineContext}
     */
    static BasicSpreadsheetEngineContext with(final SpreadsheetEngineContextMode mode,
                                              final SpreadsheetContext spreadsheetContext,
                                              final TerminalContext terminalContext) {
        Objects.requireNonNull(mode, "mode");
        Objects.requireNonNull(spreadsheetContext, "spreadsheetContext");
        Objects.requireNonNull(terminalContext, "terminalContext");

        final SpreadsheetLabelNameResolver spreadsheetLabelNameResolver = SpreadsheetLabelNameResolvers.labelStore(
            spreadsheetContext.storeRepository()
                .labels()
        );

        return new BasicSpreadsheetEngineContext(
            mode,
            null, // force cnConvert to be created.
            spreadsheetLabelNameResolver,
            spreadsheetContext,
            terminalContext
        );
    }

    /**
     * Private ctor use factory.
     */
    private BasicSpreadsheetEngineContext(final SpreadsheetEngineContextMode mode,
                                          final CanConvert canConvert,
                                          final SpreadsheetLabelNameResolver spreadsheetLabelNameResolver,
                                          final SpreadsheetContext spreadsheetContext,
                                          final TerminalContext terminalContext) {
        super();

        final SpreadsheetMetadata metadata = spreadsheetContext.spreadsheetMetadata();
        this.metadata = metadata;
        this.mode = mode;

        spreadsheetContext.setLocale(metadata.locale());

        this.spreadsheetLabelNameResolver = spreadsheetLabelNameResolver;

        // canConvert will be null when this ctor is called by #setSpreadsheetEngineContextMode
        this.canConvert = null == canConvert ?
            metadata.spreadsheetConverterContext(
                SpreadsheetMetadata.NO_CELL,
                SpreadsheetMetadata.NO_VALIDATION_REFERENCE,
                mode.converter(),
                spreadsheetLabelNameResolver,
                spreadsheetContext, // SpreadsheetProvider
                spreadsheetContext, // LocaleContext
                spreadsheetContext.providerContext()
            ) :
            canConvert;
        this.spreadsheetContext = spreadsheetContext;
        this.terminalContext = terminalContext;
    }

    @Override
    public SpreadsheetEngineContext spreadsheetEngineContext() {
        return this;
    }

    @Override
    public Router<HttpRequestAttribute<?>, HttpHandler> httpRouter() {
        throw new UnsupportedOperationException();
    }

    // HasSpreadsheetServerUrl..........................................................................................

    @Override
    public AbsoluteUrl serverUrl() {
        return this.spreadsheetContext.serverUrl();
    }

    // HasSpreadsheetMetadata...........................................................................................

    @Override
    public SpreadsheetMetadata spreadsheetMetadata() {
        if(null == this.metadata) {
            this.metadata = this.spreadsheetContext.spreadsheetMetadata();
        }

        return this.metadata;
    }

    /**
     * Will be updated whenever a new metadata is saved.
     */
    private SpreadsheetMetadata metadata;

    // CanConvertDelegator..............................................................................................

    @Override
    public CanConvert canConvert() {
        if(null == this.canConvert) {
            final SpreadsheetContext spreadsheetContext = this.spreadsheetContext;

            this.spreadsheetMetadata()
                .spreadsheetConverterContext(
                    SpreadsheetMetadata.NO_CELL,
                    SpreadsheetMetadata.NO_VALIDATION_REFERENCE,
                    this.mode.converter(),
                    this.spreadsheetLabelNameResolver,
                    spreadsheetContext, // SpreadsheetProvider
                    spreadsheetContext, // LocaleContext
                    spreadsheetContext.providerContext()
                );
        }

        return this.canConvert;
    }

    private CanConvert canConvert;

    // setSpreadsheetEngineContextMode..................................................................................

    @Override
    public SpreadsheetEngineContext setSpreadsheetEngineContextMode(final SpreadsheetEngineContextMode mode) {
        Objects.requireNonNull(mode, "mode");

        return this.mode == mode ?
            this :
            new BasicSpreadsheetEngineContext(
                mode,
                null,
                this.spreadsheetLabelNameResolver,
                this.spreadsheetContext,
                this.terminalContext
            );
    }

    // resolveLabel.....................................................................................................

    @Override
    public Optional<SpreadsheetSelection> resolveLabel(final SpreadsheetLabelName labelName) {
        return this.spreadsheetLabelNameResolver.resolveLabel(labelName);
    }

    private final SpreadsheetLabelNameResolver spreadsheetLabelNameResolver;

    // parsing formula and executing....................................................................................

    @Override
    public SpreadsheetFormulaParserToken parseFormula(final TextCursor formula,
                                                      final Optional<SpreadsheetCell> cell) {
        return SpreadsheetFormulaParsers.valueOrExpression(
                this.metadata.spreadsheetParser(
                    this, // SpreadsheetParserProvider
                    this.spreadsheetContext.providerContext()
                )
            )
            .orFailIfCursorNotEmpty(ParserReporters.basic())
            .parse(
                formula,
                this.metadata.spreadsheetParserContext(
                    cell,
                    this, // LocaleContext
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

        final SpreadsheetEngineContextMode mode = this.mode;
        final SpreadsheetMetadata metadata = this.spreadsheetMetadata();
        final SpreadsheetContext spreadsheetContext = this.spreadsheetContext;

        if (null == this.expressionFunctionProvider) {
            this.expressionFunctionProvider = metadata.expressionFunctionProvider(
                mode.function(),
                spreadsheetContext // spreadsheetProvider
            );
        }

        final ProviderContext providerContext = spreadsheetContext.providerContext();

        final SpreadsheetConverterContext spreadsheetConverterContext = metadata.spreadsheetConverterContext(
            cell,
            SpreadsheetMetadata.NO_VALIDATION_REFERENCE,
            mode.converter(),
            this, // SpreadsheetLabelNameResolver,
            spreadsheetContext, // SpreadsheetConverterProvider
            this, // LocaleContext
            providerContext
        );

        final FormHandlerContext<SpreadsheetExpressionReference, SpreadsheetDelta> formHandlerContext;
        if (SpreadsheetEngineContextMode.VALIDATION.equals(mode)) {
            // create from spreadsheetProvider using SpreadsheetMetadataPropertyName.VALIDATOR_FORM_HANDLER
            // https://github.com/mP1/walkingkooka-spreadsheet/issues/6342
            formHandlerContext = FormHandlerContexts.fake();
        } else {
            formHandlerContext = FormHandlerContexts.fake();
        }

        EnvironmentContext environmentContext = spreadsheetContext;
        if (mode.isReadOnlyEnvironmentContext()) {
            environmentContext = EnvironmentContexts.readOnly(environmentContext);
        }

        return SpreadsheetExpressionEvaluationContexts.basic(
            cell,
            loader,
            spreadsheetContext.serverUrl(),
            metadata,
            spreadsheetContext.storeRepository(),
            spreadsheetConverterContext,
            environmentContext,
            this::spreadsheetFormatterContext,
            formHandlerContext,
            this.terminalContext,
            this.expressionFunctionProvider,
            providerContext
        );
    }

    private final SpreadsheetEngineContextMode mode;

    private ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider;

    private final TerminalContext terminalContext;

    @Override
    public boolean isPure(final ExpressionFunctionName function) {
        return this.expressionFunction(
            function,
            Lists.empty(),
            this.spreadsheetContext.providerContext()
        ).isPure(this);
    }

    // formatValue......................................................................................................

    @Override
    public Optional<TextNode> formatValue(final SpreadsheetCell cell,
                                          final Optional<Object> value,
                                          final Optional<SpreadsheetFormatterSelector> formatter) {
        Objects.requireNonNull(formatter, "formatter");

        final SpreadsheetMetadata metadata = this.spreadsheetMetadata();
        final SpreadsheetContext spreadsheetContext = this.spreadsheetContext;
        final ProviderContext providerContext = spreadsheetContext.providerContext();

        final SpreadsheetFormatter spreadsheetFormatter = formatter
            .map((SpreadsheetFormatterSelector selector) -> this.spreadsheetFormatter(
                    selector,
                    providerContext
                )
            ).orElseGet(
                () -> metadata.spreadsheetFormatter(
                    this, // SpreadsheetFormatterProvider
                    providerContext
                )
            );

        return spreadsheetFormatter.format(
            value,
            this.spreadsheetFormatterContext(
                Optional.of(cell)
            )
        );
    }

    // FORMAT .........................................................................................................

    /**
     * If a value is present use the {@link SpreadsheetFormatter} and apply the styling.
     */
    @Override
    public SpreadsheetCell formatValueAndStyle(final SpreadsheetCell cell,
                                               final Optional<SpreadsheetFormatterSelector> formatter) {
        Objects.requireNonNull(cell, "cell");
        Objects.requireNonNull(formatter, "formatter");

        final SpreadsheetFormula formula = cell
            .formula();
        final Optional<Object> value = formula.errorOrValue();

        SpreadsheetCell formattedCell = cell;
        Optional<TextNode> formatted;

        try {
            formatted = this.formatValue(
                cell,
                value,
                formatter
            ).map(
                f -> cell.style()
                    .replace(f)
            );
        } catch (final UnsupportedOperationException rethrow) {
            throw rethrow;
        } catch (final RuntimeException cause) {
            String message = cause.getMessage();
            if(CharSequences.isNullOrEmpty(message)) {
                message = cause.getClass().getSimpleName();
            }

            final SpreadsheetError error = SpreadsheetErrorKind.FORMATTING.setMessageAndValue(
                message,
                formatter.orElse(null)
            );

            formatted = Optional.of(
                TextNode.text(
                    error.toExpressionError()
                        .get()
                        .text())
            );

            formattedCell = formattedCell.setFormula(
                formula.setError(
                    Optional.of(error)
                )
            );
        }

        return formattedCell.setFormattedValue(formatted);
    }

    private SpreadsheetFormatterContext spreadsheetFormatterContext(final Optional<SpreadsheetCell> cell) {
        final SpreadsheetContext context = this.spreadsheetContext;

        return this.metadata.spreadsheetFormatterContext(
            cell,
            (final Optional<Object> v) -> this.setSpreadsheetEngineContextMode(
                SpreadsheetEngineContextMode.FORMATTING
            ).spreadsheetExpressionEvaluationContext(
                cell,
                SpreadsheetExpressionReferenceLoaders.fake()
            ).addLocalVariable(
                SpreadsheetExpressionEvaluationContext.FORMAT_VALUE,
                v
            ),
            this, // SpreadsheetLabelNameResolver,
            context, // LocaleContext
            context, // spreadsheetProvider,
            context.providerContext() // ProviderContext
        );
    }

    // SpreadsheetContextDelegator......................................................................................

    @Override
    public SpreadsheetId spreadsheetId() {
        return this.spreadsheetContext.spreadsheetId();
    }

    @Override
    public SpreadsheetEngineContext setSpreadsheetId(final SpreadsheetId id) {
        final SpreadsheetContext before = this.spreadsheetContext;
        final SpreadsheetContext after = before.setSpreadsheetId(id);

        return before.equals(after) ?
            this :
            new BasicSpreadsheetEngineContext(
                this.mode,
                this.canConvert,
                this.spreadsheetLabelNameResolver,
                after,
                this.terminalContext
            );
    }

    @Override
    public SpreadsheetStoreRepository storeRepository() {
        return this.spreadsheetContext.storeRepository();
    }

    @Override
    public Optional<SpreadsheetMetadata> loadMetadata(final SpreadsheetId id) {
        return this.spreadsheetContext.loadMetadata(id);
    }

    @Override
    public SpreadsheetMetadata saveMetadata(final SpreadsheetMetadata metadata) {
        final SpreadsheetMetadata saved = this.spreadsheetContext.saveMetadata(metadata);
        if (this.spreadsheetId().equals(saved.getOrFail(SpreadsheetMetadataPropertyName.SPREADSHEET_ID))) {
            this.metadata = saved;

            // necessary because new SpreadsheetMetadata may have changed requiring a new ExpressionFunctionProvider, CanConvert
            this.expressionFunctionProvider = null;
            this.canConvert = null;
        }
        return saved;
    }

    @Override
    public void deleteMetadata(final SpreadsheetId id) {
        this.spreadsheetContext.deleteMetadata(id);
        if (this.spreadsheetId().equals(id)) {
            this.metadata = null;
            this.expressionFunctionProvider = null;
            this.canConvert = null;
        }
    }

    @Override
    public List<SpreadsheetMetadata> findMetadataBySpreadsheetName(final String name,
                                                                   final int offset,
                                                                   final int count) {
        return this.spreadsheetContext.findMetadataBySpreadsheetName(
            name,
            offset,
            count
        );
    }

    @Override
    public ProviderContext providerContext() {
        return this.spreadsheetContext.providerContext();
    }

    private final SpreadsheetContext spreadsheetContext;

    // EnvironmentContextDelegator......................................................................................

    @Override
    public SpreadsheetEngineContext cloneEnvironment() {
        final SpreadsheetContext before = this.spreadsheetContext;
        final SpreadsheetContext after = before.cloneEnvironment();

        // Recreate only if different cloned EnvironmentContext, cloned environment should be equals
        return before.equals(after) ?
            this :
            new BasicSpreadsheetEngineContext(
                this.mode,
                this.canConvert,
                this.spreadsheetLabelNameResolver,
                after,
                this.terminalContext
            );
    }

    @Override
    public Locale locale() {
        return this.spreadsheetContext.locale();
    }

    @Override
    public SpreadsheetEngineContext setLocale(final Locale locale) {
        this.spreadsheetContext.setLocale(locale);
        return this;
    }

    @Override
    public SpreadsheetEngineContext setUser(final Optional<EmailAddress> user) {
        this.spreadsheetContext.setUser(user);
        return this;
    }

    @Override
    public <T> SpreadsheetEngineContext setEnvironmentValue(final EnvironmentValueName<T> name,
                                                            final T value) {
        this.spreadsheetContext.setEnvironmentValue(name, value);
        return this;
    }

    @Override
    public SpreadsheetEngineContext removeEnvironmentValue(final EnvironmentValueName<?> name) {
        this.spreadsheetContext.removeEnvironmentValue(name);
        return this;
    }

    @Override
    public EnvironmentContext environmentContext() {
        return this.spreadsheetContext;
    }

    // LocaleContext....................................................................................................

    @Override
    public LocaleContext localeContext() {
        return this.spreadsheetContext;
    }

    // SpreadsheetProvider..............................................................................................

    @Override
    public SpreadsheetProvider spreadsheetProvider() {
        return this.spreadsheetContext;
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return ToStringBuilder.empty()
            .globalLength(Integer.MAX_VALUE)
            .valueLength(Integer.MAX_VALUE)
            .label("mode")
            .value(this.mode)
            .label("metadata")
            .value(this.metadata)
            .build();
    }
}

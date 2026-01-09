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
import walkingkooka.convert.ConverterLike;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.locale.LocaleContext;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.SpreadsheetContext;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContexts;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.formula.SpreadsheetFormulaParsers;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.parser.SpreadsheetParser;
import walkingkooka.spreadsheet.provider.SpreadsheetProvider;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoader;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoaders;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolver;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolvers;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.spreadsheet.value.SpreadsheetError;
import walkingkooka.spreadsheet.value.SpreadsheetErrorKind;
import walkingkooka.terminal.TerminalContext;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.tree.text.TextNode;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A basic and simple {@link SpreadsheetEngineContext}. Its accepts a variety of dependencies and uses them to handle
 * public methods requests.
 */
final class SpreadsheetEngineContextSharedSpreadsheetContext extends SpreadsheetEngineContextShared {

    /**
     * Creates a new {@link SpreadsheetEngineContextSharedSpreadsheetContext}
     */
    static SpreadsheetEngineContextSharedSpreadsheetContext with(final SpreadsheetMetadataMode mode,
                                                                 final SpreadsheetContext spreadsheetContext,
                                                                 final TerminalContext terminalContext) {
        Objects.requireNonNull(mode, "mode");
        Objects.requireNonNull(spreadsheetContext, "spreadsheetContext");
        Objects.requireNonNull(terminalContext, "terminalContext");

        final SpreadsheetLabelNameResolver spreadsheetLabelNameResolver = SpreadsheetLabelNameResolvers.labelStore(
            spreadsheetContext.storeRepository()
                .labels()
        );

        return new SpreadsheetEngineContextSharedSpreadsheetContext(
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
    private SpreadsheetEngineContextSharedSpreadsheetContext(final SpreadsheetMetadataMode mode,
                                                             final ConverterLike converterLike,
                                                             final SpreadsheetLabelNameResolver spreadsheetLabelNameResolver,
                                                             final SpreadsheetContext spreadsheetContext,
                                                             final TerminalContext terminalContext) {
        super();

        final SpreadsheetMetadata metadata = spreadsheetContext.spreadsheetMetadata();
        this.spreadsheetMetadata = metadata;
        this.mode = mode;

        spreadsheetContext.setLocale(metadata.locale());

        this.spreadsheetLabelNameResolver = spreadsheetLabelNameResolver;
        this.converterLike = converterLike;
        this.spreadsheetContext = spreadsheetContext;
        this.terminalContext = terminalContext;
    }

    // ConverterLikeDelegator...........................................................................................

    @Override
    public ConverterLike converterLike() {
        if (null == this.converterLike) {
            final SpreadsheetContext spreadsheetContext = this.spreadsheetContext;

            this.converterLike = this.spreadsheetMetadata()
                .spreadsheetConverterContext(
                    SpreadsheetMetadata.NO_CELL,
                    SpreadsheetMetadata.NO_VALIDATION_REFERENCE,
                    this.mode.converter(),
                    this.spreadsheetLabelNameResolver,
                    this.lineEnding(),
                    spreadsheetContext, // SpreadsheetProvider
                    spreadsheetContext, // LocaleContext
                    spreadsheetContext.providerContext()
                );
        }

        return this.converterLike;
    }

    private transient ConverterLike converterLike;

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
        Objects.requireNonNull(formula, "formula");
        Objects.requireNonNull(cell, "cell");

        final SpreadsheetMetadata metadata = this.spreadsheetMetadata();

        final SpreadsheetParser parser = cell.flatMap(SpreadsheetCell::parserSelector)
            .map(s -> this.spreadsheetParser(
                    s,
                    this.providerContext()
                )
            ).orElse(
                SpreadsheetFormulaParsers.valueOrExpression(
                    metadata.spreadsheetParser(
                        this, // SpreadsheetParserProvider
                        this.spreadsheetContext.providerContext()
                    )
                )
            );

        return parser.orFailIfCursorNotEmpty(ParserReporters.basic())
            .parse(
                formula,
                metadata.spreadsheetParserContext(
                    cell,
                    this, // LocaleContext
                    this // HasNow
                )
            ).get()
            .cast(SpreadsheetFormulaParserToken.class);
    }

    @Override
    public SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext(final Optional<SpreadsheetCell> cell,
                                                                                         final SpreadsheetExpressionReferenceLoader loader) {
        Objects.requireNonNull(cell, "cell");
        Objects.requireNonNull(loader, "loader");

        return SpreadsheetExpressionEvaluationContexts.spreadsheetContext(
            this.mode,
            cell,
            loader,
            this.spreadsheetLabelNameResolver,
            this.spreadsheetContext,
            this.terminalContext
        );
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
            if (CharSequences.isNullOrEmpty(message)) {
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

        return this.spreadsheetMetadata()
            .spreadsheetFormatterContext(
                cell,
                (final Optional<Object> v) -> this.setSpreadsheetMetadataMode(
                    SpreadsheetMetadataMode.FORMATTING
                ).spreadsheetExpressionEvaluationContext(
                    cell,
                    SpreadsheetExpressionReferenceLoaders.fake()
                ).addLocalVariable(
                    SpreadsheetExpressionEvaluationContext.FORMAT_VALUE,
                    v
                ),
                this, // SpreadsheetLabelNameResolver,
                this.lineEnding(),
                context, // LocaleContext
                context, // spreadsheetProvider,
                context.providerContext() // ProviderContext
            );
    }

    // SpreadsheetContextDelegator......................................................................................

    @Override
    public SpreadsheetStoreRepository storeRepository() {
        return this.spreadsheetContext.storeRepository();
    }

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
            new SpreadsheetEngineContextSharedSpreadsheetContext(
                this.mode,
                this.converterLike,
                this.spreadsheetLabelNameResolver,
                after,
                this.terminalContext
            );
    }

    @Override
    public SpreadsheetMetadata spreadsheetMetadata() {
        if (null == this.spreadsheetMetadata) {
            this.spreadsheetMetadata = this.spreadsheetContext.spreadsheetMetadata();
        }

        return this.spreadsheetMetadata;
    }

    /**
     * Will be updated whenever a new metadata is saved.
     */
    private transient SpreadsheetMetadata spreadsheetMetadata;

    @Override
    public Optional<SpreadsheetMetadata> loadMetadata(final SpreadsheetId id) {
        return this.spreadsheetContext.loadMetadata(id);
    }

    @Override
    public SpreadsheetMetadata saveMetadata(final SpreadsheetMetadata metadata) {
        final SpreadsheetMetadata saved = this.spreadsheetContext.saveMetadata(metadata);
        if (this.spreadsheetId().equals(saved.getOrFail(SpreadsheetMetadataPropertyName.SPREADSHEET_ID))) {
            this.spreadsheetMetadata = saved;

            // necessary because new SpreadsheetMetadata may have changed requiring a new ExpressionFunctionProvider, ConverterLike
            this.converterLike = null;
        }
        return saved;
    }

    @Override
    public void deleteMetadata(final SpreadsheetId id) {
        this.spreadsheetContext.deleteMetadata(id);
        if (this.spreadsheetId().equals(id)) {
            this.spreadsheetMetadata = null;
            this.converterLike = null;
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

    // setSpreadsheetMetadataMode.......................................................................................

    @Override
    public SpreadsheetEngineContext setSpreadsheetMetadataMode(final SpreadsheetMetadataMode mode) {
        return this.mode == mode ?
            this :
            new SpreadsheetEngineContextSharedSpreadsheetContext(
                Objects.requireNonNull(mode, "mode"),
                null, // force ConverterLike to be recreated
                this.spreadsheetLabelNameResolver,
                this.spreadsheetContext,
                this.terminalContext
            );
    }

    private final SpreadsheetMetadataMode mode;

    // LocaleContext....................................................................................................

    @Override
    public LocaleContext localeContext() {
        return this.spreadsheetContext;
    }

    // HasProviderContext...............................................................................................

    @Override
    public ProviderContext providerContext() {
        return this.spreadsheetContext.providerContext();
    }

    private final SpreadsheetContext spreadsheetContext;

    // SpreadsheetProvider..............................................................................................

    @Override
    public SpreadsheetProvider spreadsheetProvider() {
        return this.spreadsheetContext;
    }

    private final TerminalContext terminalContext;

    // EnvironmentContextDelegator......................................................................................

    @Override
    public SpreadsheetEngineContext setEnvironmentContext(final EnvironmentContext environmentContext) {
        final SpreadsheetContext before = this.spreadsheetContext;
        final SpreadsheetContext after = before.setEnvironmentContext(environmentContext);

        return before == after ?
            this :
            new SpreadsheetEngineContextSharedSpreadsheetContext(
                this.mode,
                null, // force re-create
                this.spreadsheetLabelNameResolver,
                after,
                this.terminalContext
            );
    }

    @Override
    public EnvironmentContext environmentContext() {
        return this.spreadsheetContext;
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.mode,
            this.spreadsheetContext,
            this.terminalContext
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            (other instanceof SpreadsheetEngineContextSharedSpreadsheetContext &&
                this.equals0((SpreadsheetEngineContextSharedSpreadsheetContext) other));
    }

    private boolean equals0(final SpreadsheetEngineContextSharedSpreadsheetContext other) {
        return this.mode.equals(other.mode) &&
            this.spreadsheetContext.equals(other.spreadsheetContext) &&
            this.terminalContext.equals(other.terminalContext);
    }

    @Override
    public String toString() {
        return ToStringBuilder.empty()
            .globalLength(Integer.MAX_VALUE)
            .valueLength(Integer.MAX_VALUE)
            .label("mode")
            .value(this.mode)
            .label("metadata")
            .value(this.spreadsheetMetadata)
            .build();
    }
}

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

import walkingkooka.Binary;
import walkingkooka.convert.BinaryNumberConverterFunction;
import walkingkooka.convert.ConverterLike;
import walkingkooka.currency.CurrencyContext;
import walkingkooka.currency.CurrencyLocaleContext;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.locale.LocaleContext;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.header.MediaTypeDetector;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.SpreadsheetContext;
import walkingkooka.spreadsheet.SpreadsheetContextSupplier;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContextFactory;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContextFactoryDelegate;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContexts;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.formula.SpreadsheetFormulaParsers;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParser;
import walkingkooka.spreadsheet.provider.SpreadsheetProvider;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoader;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolver;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolvers;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.store.StoreWatcher;
import walkingkooka.terminal.TerminalContext;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.tree.text.TextNode;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

final class SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext extends SpreadsheetEngineContextShared
    implements SpreadsheetEnvironmentContextFactoryDelegate {

    static SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext with(final MediaTypeDetector mediaTypeDetector,
                                                                            final BinaryNumberConverterFunction<SpreadsheetConverterContext> multiplier,
                                                                            final SpreadsheetContextSupplier spreadsheetContextSupplier,
                                                                            final CurrencyLocaleContext currencyLocaleContext,
                                                                            final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext,
                                                                            final SpreadsheetMetadataContext spreadsheetMetadataContext,
                                                                            final TerminalContext terminalContext,
                                                                            final SpreadsheetProvider spreadsheetProvider,
                                                                            final ProviderContext providerContext) {
        Objects.requireNonNull(mediaTypeDetector, "mediaTypeDetector");
        Objects.requireNonNull(multiplier, "multiplier");
        Objects.requireNonNull(spreadsheetContextSupplier, "spreadsheetContextSupplier");
        Objects.requireNonNull(currencyLocaleContext, "currencyLocaleContext");
        Objects.requireNonNull(spreadsheetEnvironmentContext, "spreadsheetEnvironmentContext");
        Objects.requireNonNull(spreadsheetMetadataContext, "spreadsheetMetadataContext");
        Objects.requireNonNull(terminalContext, "terminalContext");
        Objects.requireNonNull(spreadsheetProvider, "spreadsheetProvider");
        Objects.requireNonNull(providerContext, "providerContext");

        return new SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext(
            mediaTypeDetector,
            multiplier,
            spreadsheetContextSupplier,
            currencyLocaleContext, // CurrencyContext
            SpreadsheetEnvironmentContextFactory.with(
                multiplier,
                spreadsheetMetadataContext, // SpreadsheetMetadataLoader
                currencyLocaleContext,
                spreadsheetEnvironmentContext,
                spreadsheetProvider,
                providerContext
            ),
            spreadsheetMetadataContext,
            terminalContext
        );
    }

    private SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext(final MediaTypeDetector mediaTypeDetector,
                                                                        final BinaryNumberConverterFunction<SpreadsheetConverterContext> multiplier,
                                                                        final SpreadsheetContextSupplier spreadsheetContextSupplier,
                                                                        final CurrencyContext currencyContext,
                                                                        final SpreadsheetEnvironmentContextFactory spreadsheetEnvironmentContextFactory,
                                                                        final SpreadsheetMetadataContext spreadsheetMetadataContext,
                                                                        final TerminalContext terminalContext) {
        super();

        this.mediaTypeDetector = mediaTypeDetector;

        this.multiplier = multiplier;

        this.spreadsheetContextSupplier = spreadsheetContextSupplier;

        this.currencyContext = currencyContext;
        this.spreadsheetEnvironmentContextFactory = spreadsheetEnvironmentContextFactory;
        this.spreadsheetMetadataContext = spreadsheetMetadataContext;
        this.terminalContext = terminalContext;
    }

    private final TerminalContext terminalContext;

    // MediaTypeDetector................................................................................................

    @Override
    public MediaType detect(final String filename,
                            final Binary content) {
        return this.mediaTypeDetector.detect(
            filename,
            content
        );
    }

    private final MediaTypeDetector mediaTypeDetector;

    // ConverterLikeDelegator...........................................................................................

    @Override
    public ConverterLike converterLike() {
        return this.spreadsheetEnvironmentContextFactory.spreadsheetConverterContext();
    }

    // resolveLabel.....................................................................................................

    @Override
    public Optional<SpreadsheetSelection> resolveLabel(final SpreadsheetLabelName labelName) {
        Objects.requireNonNull(labelName, "labelName");

        return this.spreadsheetLabelNameResolver()
            .resolveLabel(labelName);
    }

    private SpreadsheetLabelNameResolver spreadsheetLabelNameResolver() {
        if (null == this.spreadsheetLabelNameResolver) {
            final SpreadsheetId spreadsheetIdOrNull = this.environmentValue(SPREADSHEET_ID)
                .orElse(null);

            this.spreadsheetLabelNameResolver = null == spreadsheetIdOrNull ?
                SpreadsheetLabelNameResolvers.empty() :
                SpreadsheetLabelNameResolvers.labelStore(
                    this.storeRepository()
                        .labels()
                );
        }
        return this.spreadsheetLabelNameResolver;
    }

    private transient SpreadsheetLabelNameResolver spreadsheetLabelNameResolver;

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
                        this.providerContext()
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

    // spreadsheetExpressionEvaluationContext...........................................................................

    @Override
    public SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext(final Optional<SpreadsheetCell> cell,
                                                                                         final SpreadsheetExpressionReferenceLoader loader) {
        Objects.requireNonNull(cell, "cell");
        Objects.requireNonNull(loader, "loader");

        final SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext;

        final SpreadsheetId spreadsheetIdOrNull = this.environmentValue(SPREADSHEET_ID)
            .orElse(null);
        final TerminalContext terminalContext = this.terminalContext;

        if (null == spreadsheetIdOrNull) {
            // ignore cell parameter for now

            final SpreadsheetEnvironmentContextFactory spreadsheetEnvironmentContextFactory = this.spreadsheetEnvironmentContextFactory;

            spreadsheetExpressionEvaluationContext = SpreadsheetExpressionEvaluationContexts.spreadsheetEnvironmentContext(
                this.mediaTypeDetector,
                this.multiplier,
                this.spreadsheetContextSupplier,
                spreadsheetEnvironmentContextFactory.currencyLocaleContext(),
                spreadsheetEnvironmentContextFactory.spreadsheetEnvironmentContext(),
                this.spreadsheetMetadataContext,
                terminalContext,
                spreadsheetEnvironmentContextFactory.spreadsheetProvider(),
                spreadsheetEnvironmentContextFactory.providerContext()
            );

        } else {
            spreadsheetExpressionEvaluationContext = SpreadsheetExpressionEvaluationContexts.spreadsheetContext(
                SpreadsheetMetadataMode.FORMULA,
                cell,
                loader,
                this.spreadsheetLabelNameResolver(),
                this.spreadsheetContextSupplier.spreadsheetContextOrFail(spreadsheetIdOrNull),
                terminalContext
            );
        }

        return spreadsheetExpressionEvaluationContext;
    }

    @Override
    public SpreadsheetEngine spreadsheetEngine() {
        return this.spreadsheetContextSupplier.spreadsheetContextOrFail(
            this.spreadsheetIdOrFail()
        ).spreadsheetEngine();
    }

    private final SpreadsheetContextSupplier spreadsheetContextSupplier;

    // formatValue......................................................................................................

    @Override
    public Optional<TextNode> formatValue(final SpreadsheetCell cell,
                                          final Optional<Object> value,
                                          final Optional<SpreadsheetFormatterSelector> formatter) {
        Objects.requireNonNull(cell, "cell");
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(formatter, "formatter");

        throw new UnsupportedOperationException();
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

        throw new UnsupportedOperationException();
    }

    // CurrencyContextDelegator.........................................................................................

    @Override
    public CurrencyContext currencyContext() {
        return this.currencyContext;
    }

    private final CurrencyContext currencyContext;

    // LocaleContext....................................................................................................

    @Override
    public LocaleContext localeContext() {
        return this.spreadsheetEnvironmentContextFactory.currencyLocaleContext();
    }


    @Override
    public BinaryNumberConverterFunction<SpreadsheetConverterContext> multiplier() {
        return this.multiplier;
    }

    private final BinaryNumberConverterFunction<SpreadsheetConverterContext> multiplier;

    // SpreadsheetContextDelegator......................................................................................

    @Override
    public SpreadsheetStoreRepository storeRepository() {
        return this.spreadsheetContextSupplier.spreadsheetContextOrFail(
            this.spreadsheetIdOrFail()
        ).storeRepository();
    }

    @Override
    public SpreadsheetMetadata spreadsheetMetadata() {
        return this.loadMetadataOrFail(
            this.spreadsheetIdOrFail()
        );
    }

    @Override
    public SpreadsheetMetadata createMetadata(final EmailAddress user,
                                              final Optional<Locale> locale) {
        return this.spreadsheetMetadataContext.createMetadata(
            user,
            locale
        );
    }

    @Override
    public Optional<SpreadsheetMetadata> loadMetadata(final SpreadsheetId id) {
        return this.spreadsheetMetadataContext.loadMetadata(id);
    }

    @Override
    public SpreadsheetMetadata saveMetadata(final SpreadsheetMetadata metadata) {
        return this.spreadsheetMetadataContext.saveMetadata(metadata);
    }

    @Override
    public void deleteMetadata(final SpreadsheetId id) {
        this.spreadsheetMetadataContext.deleteMetadata(id);
    }

    @Override
    public List<SpreadsheetMetadata> findMetadataBySpreadsheetName(final String name,
                                                                   final int offset,
                                                                   final int count) {
        return this.spreadsheetMetadataContext.findMetadataBySpreadsheetName(
            name,
            offset,
            count
        );
    }

    @Override
    public Runnable addMetadataWatcher(final StoreWatcher<SpreadsheetMetadata> watcher) {
        return this.spreadsheetMetadataContext.addMetadataWatcher(watcher);
    }

    @Override
    public Runnable addMetadataWatcherOnce(final StoreWatcher<SpreadsheetMetadata> watcher) {
        return this.spreadsheetMetadataContext.addMetadataWatcherOnce(watcher);
    }

    private final SpreadsheetMetadataContext spreadsheetMetadataContext;

    // setSpreadsheetMetadataMode.......................................................................................

    /**
     * Always returns a {@link SpreadsheetEngineContexts#spreadsheetContext(SpreadsheetMetadataMode, SpreadsheetContext, TerminalContext)},
     * which should allow cells to be parsed, evaluated and formatted.
     * <br>
     * This method will throw {@link walkingkooka.environment.MissingEnvironmentValueException} if the {@link SpreadsheetId} is missing.
     */
    @Override
    public SpreadsheetEngineContext setSpreadsheetMetadataMode(final SpreadsheetMetadataMode mode) {
        return SpreadsheetEngineContexts.spreadsheetContext(
            Objects.requireNonNull(mode, "mode"),
            this.spreadsheetContextSupplier.spreadsheetContextOrFail(
                this.spreadsheetIdOrFail()
            ),
            this.terminalContext
        );
    }

    // SpreadsheetEnvironmentContextDelegator...........................................................................

    @Override
    public SpreadsheetEngineContext setEnvironmentContext(final EnvironmentContext environmentContext) {
        final SpreadsheetEnvironmentContextFactory before = this.spreadsheetEnvironmentContextFactory;
        final SpreadsheetEnvironmentContextFactory after = before.setEnvironmentContext(environmentContext);

        return before == after ?
            this :
            new SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext(
                this.mediaTypeDetector,
                this.multiplier,
                this.spreadsheetContextSupplier,
                this.currencyContext,
                after,
                this.spreadsheetMetadataContext,
                this.terminalContext
            );
    }

    @Override
    public void setSpreadsheetId(final Optional<SpreadsheetId> spreadsheetId) {
        this.spreadsheetEnvironmentContextFactory.setSpreadsheetId(spreadsheetId);
    }

    @Override
    public ProviderContext providerContext() {
        return this.spreadsheetEnvironmentContextFactory.providerContext();
    }

    @Override
    public SpreadsheetProvider spreadsheetProvider() {
        return this.spreadsheetEnvironmentContextFactory.spreadsheetProvider();
    }

    @Override
    public SpreadsheetEnvironmentContext spreadsheetEnvironmentContext() {
        return this.spreadsheetEnvironmentContextFactory();
    }

    @Override
    public SpreadsheetEnvironmentContextFactory spreadsheetEnvironmentContextFactory() {
        return this.spreadsheetEnvironmentContextFactory;
    }

    private final SpreadsheetEnvironmentContextFactory spreadsheetEnvironmentContextFactory;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.multiplier,
            this.spreadsheetContextSupplier,
            this.spreadsheetEnvironmentContextFactory,
            this.spreadsheetMetadataContext,
            this.terminalContext
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            (other instanceof SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext &&
                this.equals0((SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext) other));
    }

    private boolean equals0(final SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext other) {
        return
            this.multiplier.equals(other.multiplier) &&
            this.spreadsheetContextSupplier.equals(other.spreadsheetContextSupplier) &&
            this.spreadsheetEnvironmentContextFactory.equals(other.spreadsheetEnvironmentContextFactory) &&
            this.spreadsheetMetadataContext.equals(other.spreadsheetMetadataContext) &&
            this.terminalContext.equals(other.terminalContext);
    }

    @Override
    public String toString() {
        return this.spreadsheetEnvironmentContextFactory.toString();
    }
}

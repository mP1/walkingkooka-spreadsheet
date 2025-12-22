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

import walkingkooka.convert.CanConvert;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.locale.LocaleContext;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.SpreadsheetContextSupplier;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContextFactory;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContextFactoryDelegate;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContexts;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.formula.SpreadsheetFormulaParsers;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
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
import walkingkooka.terminal.TerminalContext;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.tree.text.TextNode;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

final class SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext extends SpreadsheetEngineContextShared
    implements SpreadsheetEnvironmentContextFactoryDelegate {

    static SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext with(final SpreadsheetContextSupplier spreadsheetContextSupplier,
                                                                            final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext,
                                                                            final LocaleContext localeContext,
                                                                            final SpreadsheetMetadataContext spreadsheetMetadataContext,
                                                                            final TerminalContext terminalContext,
                                                                            final SpreadsheetProvider spreadsheetProvider,
                                                                            final ProviderContext providerContext) {
        Objects.requireNonNull(spreadsheetContextSupplier, "spreadsheetContextSupplier");
        Objects.requireNonNull(spreadsheetEnvironmentContext, "spreadsheetEnvironmentContext");
        Objects.requireNonNull(localeContext, "localeContext");
        Objects.requireNonNull(spreadsheetMetadataContext, "spreadsheetMetadataContext");
        Objects.requireNonNull(terminalContext, "terminalContext");
        Objects.requireNonNull(spreadsheetProvider, "spreadsheetProvider");
        Objects.requireNonNull(providerContext, "providerContext");

        return new SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext(
            spreadsheetContextSupplier,
            SpreadsheetEnvironmentContextFactory.with(
                spreadsheetEnvironmentContext,
                localeContext,
                spreadsheetProvider,
                providerContext
            ),
            spreadsheetMetadataContext,
            terminalContext
        );
    }

    private SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext(final SpreadsheetContextSupplier spreadsheetContextSupplier,
                                                                        final SpreadsheetEnvironmentContextFactory spreadsheetEnvironmentContextFactory,
                                                                        final SpreadsheetMetadataContext spreadsheetMetadataContext,
                                                                        final TerminalContext terminalContext) {
        super();

        this.spreadsheetContextSupplier = spreadsheetContextSupplier;

        this.spreadsheetEnvironmentContextFactory = spreadsheetEnvironmentContextFactory;
        this.spreadsheetMetadataContext = spreadsheetMetadataContext;
        this.terminalContext = terminalContext;
    }

    private final TerminalContext terminalContext;

    // CanConvertDelegator..............................................................................................

    @Override
    public CanConvert canConvert() {
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
                this.spreadsheetContextSupplier,
                spreadsheetEnvironmentContextFactory.localeContext(),
                spreadsheetEnvironmentContextFactory.spreadsheetEnvironmentContext(),
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

    // LocaleContext....................................................................................................

    @Override
    public LocaleContext localeContext() {
        return this.spreadsheetEnvironmentContextFactory.localeContext();
    }

    // SpreadsheetContextDelegator......................................................................................

    @Override
    public SpreadsheetStoreRepository storeRepository() {
        return this.spreadsheetContextSupplier.spreadsheetContextOrFail(this.spreadsheetId())
            .storeRepository();
    }

    @Override
    public SpreadsheetMetadata spreadsheetMetadata() {
        return this.loadMetadataOrFail(
            this.spreadsheetId()
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

    private final SpreadsheetMetadataContext spreadsheetMetadataContext;

    // setSpreadsheetMetadataMode.......................................................................................

    @Override
    public SpreadsheetEngineContext setSpreadsheetMetadataMode(final SpreadsheetMetadataMode mode) {
        Objects.requireNonNull(mode, "mode");

        throw new UnsupportedOperationException();
    }

    // EnvironmentContextDelegator......................................................................................

    @Override
    public SpreadsheetEngineContext setEnvironmentContext(final EnvironmentContext environmentContext) {
        final SpreadsheetEnvironmentContextFactory before = this.spreadsheetEnvironmentContextFactory;
        final SpreadsheetEnvironmentContextFactory after = before.setEnvironmentContext(environmentContext);

        return before == after ?
            this :
            new SpreadsheetEngineContextSharedSpreadsheetEnvironmentContext(
                this.spreadsheetContextSupplier,
                after,
                this.spreadsheetMetadataContext,
                this.terminalContext
            );
    }

    @Override
    public SpreadsheetEngineContext setSpreadsheetId(final SpreadsheetId spreadsheetId) {
        this.spreadsheetEnvironmentContextFactory.setSpreadsheetId(spreadsheetId);
        return this;
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
    public EnvironmentContext environmentContext() {
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
        return this.spreadsheetContextSupplier.equals(other.spreadsheetContextSupplier) &&
            this.spreadsheetEnvironmentContextFactory.equals(other.spreadsheetEnvironmentContextFactory) &&
            this.spreadsheetMetadataContext.equals(other.spreadsheetMetadataContext) &&
            this.terminalContext.equals(other.terminalContext);
    }

    @Override
    public String toString() {
        return this.spreadsheetEnvironmentContextFactory.toString();
    }
}

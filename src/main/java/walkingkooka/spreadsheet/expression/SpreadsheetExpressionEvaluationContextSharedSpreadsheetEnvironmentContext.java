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

import walkingkooka.collect.set.Sets;
import walkingkooka.convert.CanConvert;
import walkingkooka.convert.Converter;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContextMissingValues;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.locale.LocaleContext;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContextFactory;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContextFactoryDelegate;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.parser.SpreadsheetParser;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.provider.SpreadsheetProvider;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.validation.SpreadsheetValidatorContext;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.storage.Storage;
import walkingkooka.storage.expression.function.StorageExpressionEvaluationContext;
import walkingkooka.terminal.TerminalContext;
import walkingkooka.tree.expression.ExpressionNumberContext;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionAliasSet;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProvider;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProviders;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContextObjectPostProcessor;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;
import walkingkooka.validation.form.Form;
import walkingkooka.validation.form.FormField;

import java.math.MathContext;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * A {@link SpreadsheetExpressionEvaluationContext} using {@link EnvironmentValueName} to create each of the core components
 * required during evaluation, such as a {@link Converter} using the {@link #CONVERTER}. A full list of required
 * {@link EnvironmentValueName} are listed below.
 */
final class SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext extends SpreadsheetExpressionEvaluationContextShared
    implements SpreadsheetEnvironmentContextFactoryDelegate {

    static SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext with(final LocaleContext localeContext,
                                                                                          final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext,
                                                                                          final TerminalContext terminalContext,
                                                                                          final SpreadsheetProvider spreadsheetProvider,
                                                                                          final ProviderContext providerContext) {
        Objects.requireNonNull(localeContext, "localeContext");
        Objects.requireNonNull(spreadsheetEnvironmentContext, "spreadsheetEnvironmentContext");
        Objects.requireNonNull(terminalContext, "terminalContext");
        Objects.requireNonNull(spreadsheetProvider, "spreadsheetProvider");
        Objects.requireNonNull(providerContext, "providerContext");

        return new SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext(
            SpreadsheetEnvironmentContextFactory.with(
                spreadsheetEnvironmentContext,
                localeContext,
                spreadsheetProvider,
                providerContext
            ),
            terminalContext,
            null // ExpressionFunctionProvider
        );
    }

    private SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext(final SpreadsheetEnvironmentContextFactory spreadsheetEnvironmentContextFactory,
                                                                                      final TerminalContext terminalContext,
                                                                                      final ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider) {
        super(
            terminalContext
        );

        this.spreadsheetEnvironmentContextFactory = spreadsheetEnvironmentContextFactory;
        this.expressionFunctionProvider = expressionFunctionProvider; // may be null
    }

    /**
     * Lazily created {@link ExpressionFunctionProvider}, should be nulled whenever environment changes.
     */
    @Override
    ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider() {
        if (null == this.expressionFunctionProvider) {
            final EnvironmentContextMissingValues missing = this.spreadsheetEnvironmentContextFactory.environmentContextMissingValues();

            final ExpressionFunctionAliasSet functions = missing.getOrNull(FUNCTIONS);

            missing.reportIfMissing();

            this.expressionFunctionProvider = ExpressionFunctionProviders.aliases(
                functions,
                this.spreadsheetEnvironmentContextFactory.spreadsheetProvider()
            );
        }
        return this.expressionFunctionProvider;
    }

    private ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider;

    @Override
    ProviderContext providerContext() {
        return this.spreadsheetEnvironmentContextFactory.providerContext();
    }

    @Override
    Optional<Optional<Object>> handleSpreadsheetExpressionReference(final SpreadsheetExpressionReference reference) {
        return Optional.empty();
    }

    // SpreadsheetExpressionEvaluationContext............................................................................

    @Override
    public SpreadsheetExpressionEvaluationContext setCell(final Optional<SpreadsheetCell> cell) {
        Objects.requireNonNull(cell, "cell");
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetCell> cell() {
        return NO_CELL;
    }

    @Override
    public SpreadsheetMetadata spreadsheetMetadata() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSpreadsheetMetadata(final SpreadsheetMetadata metadata) {
        Objects.requireNonNull(metadata, "metadata");

        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference cell) {
        Objects.requireNonNull(cell, "cell");
        return Optional.empty();
    }

    @Override
    public Set<SpreadsheetCell> loadCellRange(final SpreadsheetCellRangeReference range) {
        Objects.requireNonNull(range, "range");
        return Sets.empty();
    }

    @Override
    public Optional<SpreadsheetLabelMapping> loadLabel(final SpreadsheetLabelName labelName) {
        Objects.requireNonNull(labelName, "labelName");
        return Optional.empty();
    }

    @Override
    public SpreadsheetFormatterContext spreadsheetFormatterContext(final Optional<SpreadsheetCell> cell) {
        Objects.requireNonNull(cell, "cell");

        throw new UnsupportedOperationException();
    }

    // SpreadsheetConverterContextDelegator.............................................................................

    @Override
    public Optional<SpreadsheetSelection> resolveLabel(final SpreadsheetLabelName labelName) {
        Objects.requireNonNull(labelName, "labelName");
        return Optional.empty();
    }

    @Override
    public CanConvert canConvert() {
        return this.spreadsheetConverterContext(); // inherit unrelated publics
    }

    // FormHandlerContext...............................................................................................

    @Override
    public Form<SpreadsheetExpressionReference> form() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Comparator<SpreadsheetExpressionReference> formFieldReferenceComparator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Object> loadFormFieldValue(final SpreadsheetExpressionReference reference) {
        Objects.requireNonNull(reference, "reference");
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta saveFormFieldValues(final List<FormField<SpreadsheetExpressionReference>> formFields) {
        Objects.requireNonNull(formFields, "formFields");
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetValidatorContext validatorContext(final SpreadsheetExpressionReference reference) {
        Objects.requireNonNull(reference, "reference");
        throw new UnsupportedOperationException();
    }

    // JsonNodeUnmarshallContext........................................................................................

    @Override
    public SpreadsheetExpressionEvaluationContext setObjectPostProcessor(final JsonNodeMarshallContextObjectPostProcessor processor) {
        final SpreadsheetEnvironmentContextFactory before = this.spreadsheetEnvironmentContextFactory;
        final SpreadsheetEnvironmentContextFactory after = before.setObjectPostProcessor(processor);

        return before.equals(after) ?
            this :
            this.setSpreadsheetEnvironmentContextFactory(after);
    }

    @Override
    public SpreadsheetExpressionEvaluationContext setPreProcessor(final JsonNodeUnmarshallContextPreProcessor processor) {
        final SpreadsheetEnvironmentContextFactory before = this.spreadsheetEnvironmentContextFactory;
        final SpreadsheetEnvironmentContextFactory after = before.setPreProcessor(processor);

        return before.equals(after) ?
            this :
            this.setSpreadsheetEnvironmentContextFactory(after);
    }

    private SpreadsheetExpressionEvaluationContext setSpreadsheetEnvironmentContextFactory(final SpreadsheetEnvironmentContextFactory factory) {
        return new SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext(
            factory,
            this.terminalContext,
            this.expressionFunctionProvider
        );
    }

    // StorageExpressionEvaluationContext...............................................................................

    @Override
    public Storage<StorageExpressionEvaluationContext> storage() {
        throw new UnsupportedOperationException();
    }

    // ValidationExpressionEvaluationContext............................................................................

    @Override
    public Optional<SpreadsheetColumnReference> nextEmptyColumn(final SpreadsheetRowReference row) {
        Objects.requireNonNull(row, "row");

        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetRowReference> nextEmptyRow(final SpreadsheetColumnReference column) {
        Objects.requireNonNull(column, "column");

        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Object> validationValue() {
        return Optional.empty();
    }

    // SpreadsheetEnvironmentContextFactoryDelegator....................................................................

    @Override
    public SpreadsheetExpressionEvaluationContext cloneEnvironment() {
        return this.setEnvironmentContext(
            this.spreadsheetEnvironmentContextFactory.cloneEnvironment()
        );
    }

    @Override
    public SpreadsheetExpressionEvaluationContext setEnvironmentContext(final EnvironmentContext environmentContext) {
        final SpreadsheetEnvironmentContextFactory before = this.spreadsheetEnvironmentContextFactory;
        final SpreadsheetEnvironmentContextFactory after = before.setEnvironmentContext(environmentContext);

        return before == after ?
            this :
            new SpreadsheetExpressionEvaluationContextSharedSpreadsheetEnvironmentContext(
                after,
                this.terminalContext,
                this.expressionFunctionProvider
            );
    }

    @Override
    public Converter<SpreadsheetConverterContext> converter() {
        return SpreadsheetEnvironmentContextFactoryDelegate.super.converter();
    }

    @Override
    public SpreadsheetConverterContext spreadsheetConverterContext() {
        return SpreadsheetEnvironmentContextFactoryDelegate.super.spreadsheetConverterContext();
    }

    @Override
    public DateTimeContext dateTimeContext() {
        return SpreadsheetEnvironmentContextFactoryDelegate.super.dateTimeContext();
    }

    @Override
    public DecimalNumberContext decimalNumberContext() {
        return SpreadsheetEnvironmentContextFactoryDelegate.super.decimalNumberContext();
    }

    @Override
    public ExpressionNumberContext expressionNumberContext() {
        return SpreadsheetEnvironmentContextFactoryDelegate.super.expressionNumberContext();
    }

    @Override
    public ExpressionNumberKind expressionNumberKind() {
        return SpreadsheetEnvironmentContextFactoryDelegate.super.expressionNumberKind();
    }

    @Override
    public JsonNodeMarshallContext jsonNodeMarshallContext() {
        return SpreadsheetEnvironmentContextFactoryDelegate.super.jsonNodeMarshallContext();
    }

    @Override
    public JsonNodeUnmarshallContext jsonNodeUnmarshallContext() {
        return SpreadsheetEnvironmentContextFactoryDelegate.super.jsonNodeUnmarshallContext();
    }

    @Override
    public MathContext mathContext() {
        return SpreadsheetEnvironmentContextFactoryDelegate.super.mathContext();
    }

    @Override
    public SpreadsheetParser spreadsheetParser() {
        return SpreadsheetEnvironmentContextFactoryDelegate.super.spreadsheetParser();
    }

    @Override
    public SpreadsheetParserContext spreadsheetParserContext() {
        return SpreadsheetEnvironmentContextFactoryDelegate.super.spreadsheetParserContext();
    }

    @Override
    public SpreadsheetEnvironmentContext environmentContext() {
        return this.spreadsheetEnvironmentContext();
    }

    @Override
    public SpreadsheetEnvironmentContextFactory spreadsheetEnvironmentContextFactory() {
        return this.spreadsheetEnvironmentContextFactory;
    }

    private final SpreadsheetEnvironmentContextFactory spreadsheetEnvironmentContextFactory;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.spreadsheetEnvironmentContextFactory.toString();
    }
}

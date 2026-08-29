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

package walkingkooka.spreadsheet.provider;

import walkingkooka.Cast;
import walkingkooka.ToStringBuilder;
import walkingkooka.UsesToStringBuilder;
import walkingkooka.convert.provider.ConverterProvider;
import walkingkooka.convert.provider.ConverterProviderDelegator;
import walkingkooka.currency.provider.CurrencyExchangeRaterProvider;
import walkingkooka.currency.provider.CurrencyExchangeRaterProviderDelegator;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorProvider;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorProviderDelegator;
import walkingkooka.spreadsheet.export.provider.SpreadsheetExporterProvider;
import walkingkooka.spreadsheet.export.provider.SpreadsheetExporterProviderDelegator;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterProvider;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterProviderDelegator;
import walkingkooka.spreadsheet.importer.provider.SpreadsheetImporterProvider;
import walkingkooka.spreadsheet.importer.provider.SpreadsheetImporterProviderDelegator;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserProvider;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserProviderDelegator;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProvider;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProviderDelegator;
import walkingkooka.validation.form.provider.FormHandlerProvider;
import walkingkooka.validation.form.provider.FormHandlerProviderDelegator;
import walkingkooka.validation.provider.ValidatorProvider;
import walkingkooka.validation.provider.ValidatorProviderDelegator;

import java.util.Objects;

/**
 * A {@link SpreadsheetProvider} that delegates all methods to the provided {@link walkingkooka.plugin.Provider}.
 */
final class SpreadsheetProviderBasic implements SpreadsheetProvider,
    SpreadsheetComparatorProviderDelegator,
    ConverterProviderDelegator,
    CurrencyExchangeRaterProviderDelegator,
    SpreadsheetExporterProviderDelegator,
    ExpressionFunctionProviderDelegator<SpreadsheetExpressionEvaluationContext>,
    FormHandlerProviderDelegator,
    SpreadsheetFormatterProviderDelegator,
    SpreadsheetImporterProviderDelegator,
    SpreadsheetParserProviderDelegator,
    ValidatorProviderDelegator,
    TreePrintable,
    UsesToStringBuilder {

    static SpreadsheetProviderBasic with(final SpreadsheetComparatorProvider spreadsheetComparatorProvider,
                                         final ConverterProvider converterProvider,
                                         final CurrencyExchangeRaterProvider currencyExchangeRaterProvider,
                                         final SpreadsheetExporterProvider spreadsheetExporterProvider,
                                         final ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider,
                                         final SpreadsheetFormatterProvider spreadsheetFormatterProvider,
                                         final FormHandlerProvider formHandlerProvider,
                                         final SpreadsheetImporterProvider spreadsheetImporterProvider,
                                         final SpreadsheetParserProvider spreadsheetParserProvider,
                                         final ValidatorProvider validatorProvider) {
        return new SpreadsheetProviderBasic(
            Objects.requireNonNull(spreadsheetComparatorProvider, "spreadsheetComparatorProvider"),
            Objects.requireNonNull(converterProvider, "converterProvider"),
            Objects.requireNonNull(currencyExchangeRaterProvider, "currencyExchangeRaterProvider"),
            Objects.requireNonNull(spreadsheetExporterProvider, "spreadsheetExporterProvider"),
            Objects.requireNonNull(expressionFunctionProvider, "expressionFunctionProvider"),
            Objects.requireNonNull(spreadsheetFormatterProvider, "spreadsheetFormatterProvider"),
            Objects.requireNonNull(formHandlerProvider, "formHandlerProvider"),
            Objects.requireNonNull(spreadsheetImporterProvider, "spreadsheetImporterProvider"),
            Objects.requireNonNull(spreadsheetParserProvider, "spreadsheetParserProvider"),
            Objects.requireNonNull(validatorProvider, "validatorProvider")
        );
    }

    private SpreadsheetProviderBasic(final SpreadsheetComparatorProvider spreadsheetComparatorProvider,
                                     final ConverterProvider converterProvider,
                                     final CurrencyExchangeRaterProvider currencyExchangeRaterProvider,
                                     final SpreadsheetExporterProvider spreadsheetExporterProvider,
                                     final ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider,
                                     final SpreadsheetFormatterProvider spreadsheetFormatterProvider,
                                     final FormHandlerProvider formHandlerProvider,
                                     final SpreadsheetImporterProvider spreadsheetImporterProvider,
                                     final SpreadsheetParserProvider spreadsheetParserProvider,
                                     final ValidatorProvider validatorProvider) {
        super();

        this.spreadsheetComparatorProvider = spreadsheetComparatorProvider;
        this.converterProvider = converterProvider;
        this.currencyExchangeRaterProvider = currencyExchangeRaterProvider;
        this.spreadsheetExporterProvider = spreadsheetExporterProvider;
        this.expressionFunctionProvider = expressionFunctionProvider;
        this.spreadsheetFormatterProvider = spreadsheetFormatterProvider;
        this.formHandlerProvider = formHandlerProvider;
        this.spreadsheetImporterProvider = spreadsheetImporterProvider;
        this.spreadsheetParserProvider = spreadsheetParserProvider;
        this.validatorProvider = validatorProvider;
    }

    @Override
    public SpreadsheetComparatorProvider spreadsheetComparatorProvider() {
        return this.spreadsheetComparatorProvider;
    }

    private final SpreadsheetComparatorProvider spreadsheetComparatorProvider;

    @Override
    public ConverterProvider converterProvider() {
        return this.converterProvider;
    }

    private final ConverterProvider converterProvider;

    @Override
    public CurrencyExchangeRaterProvider currencyExchangeRaterProvider() {
        return this.currencyExchangeRaterProvider;
    }

    private final CurrencyExchangeRaterProvider currencyExchangeRaterProvider;

    @Override
    public ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider() {
        return expressionFunctionProvider;
    }

    private final ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider;

    @Override
    public SpreadsheetExporterProvider spreadsheetExporterProvider() {
        return this.spreadsheetExporterProvider;
    }

    private final SpreadsheetExporterProvider spreadsheetExporterProvider;

    @Override
    public SpreadsheetFormatterProvider spreadsheetFormatterProvider() {
        return this.spreadsheetFormatterProvider;
    }

    private final SpreadsheetFormatterProvider spreadsheetFormatterProvider;

    @Override
    public FormHandlerProvider formHandlerProvider() {
        return this.formHandlerProvider;
    }

    private final FormHandlerProvider formHandlerProvider;

    @Override
    public SpreadsheetImporterProvider spreadsheetImporterProvider() {
        return this.spreadsheetImporterProvider;
    }

    private final SpreadsheetImporterProvider spreadsheetImporterProvider;

    @Override
    public SpreadsheetParserProvider spreadsheetParserProvider() {
        return this.spreadsheetParserProvider;
    }

    private final SpreadsheetParserProvider spreadsheetParserProvider;

    @Override
    public ValidatorProvider validatorProvider() {
        return this.validatorProvider;
    }

    private final ValidatorProvider validatorProvider;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.spreadsheetComparatorProvider,
            this.converterProvider,
            this.currencyExchangeRaterProvider,
            this.spreadsheetExporterProvider,
            this.expressionFunctionProvider,
            this.spreadsheetFormatterProvider,
            this.formHandlerProvider,
            this.spreadsheetImporterProvider,
            this.spreadsheetParserProvider,
            this.validatorProvider
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof SpreadsheetProviderBasic &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetProviderBasic other) {
        return this.spreadsheetComparatorProvider.equals(other.spreadsheetComparatorProvider) &&
            this.converterProvider.equals(other.converterProvider) &&
            this.currencyExchangeRaterProvider.equals(other.currencyExchangeRaterProvider) &&
            this.spreadsheetExporterProvider.equals(other.spreadsheetExporterProvider) &&
            this.expressionFunctionProvider.equals(other.expressionFunctionProvider) &&
            this.spreadsheetFormatterProvider.equals(other.spreadsheetFormatterProvider) &&
            this.formHandlerProvider.equals(other.formHandlerProvider) &&
            this.spreadsheetImporterProvider.equals(other.spreadsheetImporterProvider) &&
            this.spreadsheetParserProvider.equals(other.spreadsheetParserProvider) &&
            this.validatorProvider.equals(other.validatorProvider);
    }

    @Override
    public String toString() {
        return ToStringBuilder.buildFrom(this);
    }

    // UsesToStringBuilder..............................................................................................

    @Override
    public void buildToString(final ToStringBuilder builder) {
        builder.label("spreadsheetComparatorProvider")
            .value(this.spreadsheetComparatorProvider)
            .label("converterProvider")
            .value(this.converterProvider)
            .label("currencyExchangeRaterProvider")
            .value(this.currencyExchangeRaterProvider)
            .label("spreadsheetExporterProvider")
            .value(this.spreadsheetExporterProvider)
            .label("expressionFunctionProvider")
            .value(this.spreadsheetComparatorProvider)
            .label("spreadsheetFormatterProvider")
            .value(this.spreadsheetFormatterProvider)
            .label("spreadsheetImporterProvider")
            .value(this.spreadsheetImporterProvider)
            .label("spreadsheetParserProvider")
            .value(this.spreadsheetParserProvider)
            .label("validatorProvider")
            .value(this.validatorProvider)
            .build();
    }


    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.getClass().getSimpleName());
        printer.indent();
        {
            this.printTreeLabel(
                "comparatorProvider",
                this.spreadsheetComparatorProvider,
                printer
            );
            this.printTreeLabel(
                "converterProvider",
                this.converterProvider,
                printer
            );
            this.printTreeLabel(
                "currencyExchangeRaterProvider",
                this.currencyExchangeRaterProvider,
                printer
            );
            this.printTreeLabel(
                "spreadsheetExporterProvider",
                this.spreadsheetExporterProvider,
                printer
            );
            this.printTreeLabel(
                "expressionFunctionProvider",
                this.expressionFunctionProvider,
                printer
            );
            this.printTreeLabel(
                "spreadsheetFormatterProvider",
                this.spreadsheetFormatterProvider,
                printer
            );
            this.printTreeLabel(
                "formHandlerProvider",
                this.formHandlerProvider,
                printer
            );
            this.printTreeLabel(
                "spreadsheetImporterProvider",
                this.spreadsheetImporterProvider,
                printer
            );
            this.printTreeLabel(
                "spreadsheetParserProvider",
                this.spreadsheetParserProvider,
                printer
            );
            this.printTreeLabel(
                "validatorProvider",
                this.validatorProvider,
                printer
            );
        }
        printer.outdent();
    }

    private void printTreeLabel(final String label,
                                final Object value,
                                final IndentingPrinter printer) {
        printer.println(label);
        printer.indent();
        {
            TreePrintable.printTreeOrToString(
                value,
                printer
            );
        }
        printer.outdent();
    }
}

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
import walkingkooka.convert.provider.ConverterProvider;
import walkingkooka.convert.provider.ConverterProviderDelegator;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorProvider;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorProviderDelegator;
import walkingkooka.spreadsheet.export.provider.SpreadsheetExporterProvider;
import walkingkooka.spreadsheet.export.provider.SpreadsheetExporterProviderDelegator;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProvider;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProviderDelegator;
import walkingkooka.spreadsheet.importer.provider.SpreadsheetImporterProvider;
import walkingkooka.spreadsheet.importer.provider.SpreadsheetImporterProviderDelegator;
import walkingkooka.spreadsheet.parser.SpreadsheetParserProvider;
import walkingkooka.spreadsheet.parser.SpreadsheetParserProviderDelegator;
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
final class BasicSpreadsheetProvider implements SpreadsheetProvider,
    ConverterProviderDelegator,
    ExpressionFunctionProviderDelegator<SpreadsheetExpressionEvaluationContext>,
    SpreadsheetComparatorProviderDelegator,
    SpreadsheetExporterProviderDelegator,
    FormHandlerProviderDelegator,
    SpreadsheetFormatterProviderDelegator,
    SpreadsheetImporterProviderDelegator,
    SpreadsheetParserProviderDelegator,
    ValidatorProviderDelegator {

    static BasicSpreadsheetProvider with(final ConverterProvider converterProvider,
                                         final ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider,
                                         final SpreadsheetComparatorProvider spreadsheetComparatorProvider,
                                         final SpreadsheetExporterProvider spreadsheetExporterProvider,
                                         final SpreadsheetFormatterProvider spreadsheetFormatterProvider,
                                         final FormHandlerProvider formHandlerProvider,
                                         final SpreadsheetImporterProvider spreadsheetImporterProvider,
                                         final SpreadsheetParserProvider spreadsheetParserProvider,
                                         final ValidatorProvider validatorProvider) {
        return new BasicSpreadsheetProvider(
            Objects.requireNonNull(converterProvider, "converterProvider"),
            Objects.requireNonNull(expressionFunctionProvider, "expressionFunctionProvider"),
            Objects.requireNonNull(spreadsheetComparatorProvider, "spreadsheetComparatorProvider"),
            Objects.requireNonNull(spreadsheetExporterProvider, "spreadsheetExporterProvider"),
            Objects.requireNonNull(spreadsheetFormatterProvider, "spreadsheetFormatterProvider"),
            Objects.requireNonNull(formHandlerProvider, "formHandlerProvider"),
            Objects.requireNonNull(spreadsheetImporterProvider, "spreadsheetImporterProvider"),
            Objects.requireNonNull(spreadsheetParserProvider, "spreadsheetParserProvider"),
            Objects.requireNonNull(validatorProvider, "validatorProvider")
        );
    }

    private BasicSpreadsheetProvider(final ConverterProvider converterProvider,
                                     final ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider,
                                     final SpreadsheetComparatorProvider spreadsheetComparatorProvider,
                                     final SpreadsheetExporterProvider spreadsheetExporterProvider,
                                     final SpreadsheetFormatterProvider spreadsheetFormatterProvider,
                                     final FormHandlerProvider formHandlerProvider,
                                     final SpreadsheetImporterProvider spreadsheetImporterProvider,
                                     final SpreadsheetParserProvider spreadsheetParserProvider,
                                     final ValidatorProvider validatorProvider) {
        this.converterProvider = converterProvider;
        this.expressionFunctionProvider = expressionFunctionProvider;
        this.spreadsheetComparatorProvider = spreadsheetComparatorProvider;
        this.spreadsheetExporterProvider = spreadsheetExporterProvider;
        this.spreadsheetFormatterProvider = spreadsheetFormatterProvider;
        this.formHandlerProvider = formHandlerProvider;
        this.spreadsheetImporterProvider = spreadsheetImporterProvider;
        this.spreadsheetParserProvider = spreadsheetParserProvider;
        this.validatorProvider = validatorProvider;
    }

    @Override
    public ConverterProvider converterProvider() {
        return this.converterProvider;
    }

    private final ConverterProvider converterProvider;

    @Override
    public ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider() {
        return expressionFunctionProvider;
    }

    private final ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider;

    @Override
    public SpreadsheetComparatorProvider spreadsheetComparatorProvider() {
        return this.spreadsheetComparatorProvider;
    }

    private final SpreadsheetComparatorProvider spreadsheetComparatorProvider;

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
            this.converterProvider,
            this.expressionFunctionProvider,
            this.spreadsheetComparatorProvider,
            this.spreadsheetExporterProvider,
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
            other instanceof BasicSpreadsheetProvider &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final BasicSpreadsheetProvider other) {
        return this.converterProvider.equals(other.converterProvider) &&
            this.expressionFunctionProvider.equals(other.expressionFunctionProvider) &&
            this.spreadsheetComparatorProvider.equals(other.spreadsheetComparatorProvider) &&
            this.spreadsheetExporterProvider.equals(other.spreadsheetExporterProvider) &&
            this.spreadsheetFormatterProvider.equals(other.spreadsheetFormatterProvider) &&
            this.formHandlerProvider.equals(other.formHandlerProvider) &&
            this.spreadsheetImporterProvider.equals(other.spreadsheetImporterProvider) &&
            this.spreadsheetParserProvider.equals(other.spreadsheetParserProvider) &&
            this.validatorProvider.equals(other.validatorProvider);
    }

    @Override
    public String toString() {
        return this.converterProvider +
            " " +
            this.expressionFunctionProvider +
            " " +
            this.spreadsheetComparatorProvider +
            " " +
            this.spreadsheetFormatterProvider +
            " " +
            this.formHandlerProvider +
            " " +
            this.spreadsheetImporterProvider +
            " " +
            this.spreadsheetParserProvider +
            " " +
            this.validatorProvider;
    }
}

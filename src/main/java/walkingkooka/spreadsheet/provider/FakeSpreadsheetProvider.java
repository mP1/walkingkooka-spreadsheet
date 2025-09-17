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

import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.provider.ConverterInfoSet;
import walkingkooka.convert.provider.ConverterName;
import walkingkooka.convert.provider.ConverterSelector;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.compare.SpreadsheetComparator;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorInfoSet;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorName;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorSelector;
import walkingkooka.spreadsheet.export.SpreadsheetExporter;
import walkingkooka.spreadsheet.export.provider.SpreadsheetExporterInfoSet;
import walkingkooka.spreadsheet.export.provider.SpreadsheetExporterName;
import walkingkooka.spreadsheet.export.provider.SpreadsheetExporterSelector;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.format.FakeSpreadsheetFormatterProvider;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.importer.SpreadsheetImporter;
import walkingkooka.spreadsheet.importer.SpreadsheetImporterInfoSet;
import walkingkooka.spreadsheet.importer.SpreadsheetImporterName;
import walkingkooka.spreadsheet.importer.SpreadsheetImporterSelector;
import walkingkooka.spreadsheet.parser.SpreadsheetParser;
import walkingkooka.spreadsheet.parser.SpreadsheetParserInfoSet;
import walkingkooka.spreadsheet.parser.SpreadsheetParserName;
import walkingkooka.spreadsheet.parser.SpreadsheetParserSelector;
import walkingkooka.spreadsheet.parser.SpreadsheetParserSelectorToken;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionInfoSet;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionSelector;
import walkingkooka.validation.ValidationReference;
import walkingkooka.validation.Validator;
import walkingkooka.validation.ValidatorContext;
import walkingkooka.validation.form.FormHandler;
import walkingkooka.validation.form.FormHandlerContext;
import walkingkooka.validation.form.provider.FormHandlerInfoSet;
import walkingkooka.validation.form.provider.FormHandlerName;
import walkingkooka.validation.form.provider.FormHandlerSelector;
import walkingkooka.validation.provider.ValidatorInfoSet;
import walkingkooka.validation.provider.ValidatorName;
import walkingkooka.validation.provider.ValidatorSelector;

import java.util.List;
import java.util.Optional;

public class FakeSpreadsheetProvider extends FakeSpreadsheetFormatterProvider implements SpreadsheetProvider {

    public FakeSpreadsheetProvider() {

    }

    @Override
    public <C extends ConverterContext> Converter<C> converter(final ConverterSelector selector,
                                                               final ProviderContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <C extends ConverterContext> Converter<C> converter(final ConverterName name,
                                                               final List<?> value,
                                                               final ProviderContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ConverterInfoSet converterInfos() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetComparator<?> spreadsheetComparator(final SpreadsheetComparatorSelector selector,
                                                          final ProviderContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetComparator<?> spreadsheetComparator(final SpreadsheetComparatorName name,
                                                          final List<?> values,
                                                          final ProviderContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetComparatorInfoSet spreadsheetComparatorInfos() {
        throw new UnsupportedOperationException();
    }

    @Override
    public CaseSensitivity expressionFunctionNameCaseSensitivity() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetExporter spreadsheetExporter(final SpreadsheetExporterSelector selector,
                                                   final ProviderContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetExporter spreadsheetExporter(final SpreadsheetExporterName name,
                                                   final List<?> values,
                                                   final ProviderContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetExporterInfoSet spreadsheetExporterInfos() {
        throw new UnsupportedOperationException();
    }

    // FormHandlerContext...............................................................................................

    @Override
    public <R extends ValidationReference, S, C extends FormHandlerContext<R, S>> FormHandler<R, S, C> formHandler(final FormHandlerSelector selector,
                                                                                                                   final ProviderContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <R extends ValidationReference, S, C extends FormHandlerContext<R, S>> FormHandler<R, S, C> formHandler(final FormHandlerName name,
                                                                                                                   final List<?> values,
                                                                                                                   final ProviderContext providerContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FormHandlerInfoSet formHandlerInfos() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetImporter spreadsheetImporter(final SpreadsheetImporterSelector selector,
                                                   final ProviderContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetImporter spreadsheetImporter(final SpreadsheetImporterName name,
                                                   final List<?> values,
                                                   final ProviderContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetImporterInfoSet spreadsheetImporterInfos() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetParser spreadsheetParser(final SpreadsheetParserSelector selector,
                                               final ProviderContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetParser spreadsheetParser(final SpreadsheetParserName name,
                                               final List<?> values,
                                               final ProviderContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetParserSelectorToken> spreadsheetParserNextToken(final SpreadsheetParserSelector selector) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetFormatterSelector> spreadsheetFormatterSelector(final SpreadsheetParserSelector selector) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetParserInfoSet spreadsheetParserInfos() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ExpressionFunction<?, SpreadsheetExpressionEvaluationContext> expressionFunction(final ExpressionFunctionSelector selector,
                                                                                            final ProviderContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ExpressionFunction<?, SpreadsheetExpressionEvaluationContext> expressionFunction(final ExpressionFunctionName name,
                                                                                            final List<?> values,
                                                                                            final ProviderContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ExpressionFunctionInfoSet expressionFunctionInfos() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <R extends ValidationReference, C extends ValidatorContext<R>> Validator<R, C> validator(final ValidatorSelector validatorSelector,
                                                                                                    final ProviderContext providerContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <R extends ValidationReference, C extends ValidatorContext<R>> Validator<R, C> validator(final ValidatorName validatorName,
                                                                                                    final List<?> values,
                                                                                                    final ProviderContext providerContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ValidatorInfoSet validatorInfos() {
        throw new UnsupportedOperationException();
    }
}

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

import walkingkooka.convert.provider.ConverterProvider;
import walkingkooka.convert.provider.ConverterProviderDelegator;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorProvider;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorProviderDelegator;
import walkingkooka.spreadsheet.export.SpreadsheetExporterProvider;
import walkingkooka.spreadsheet.export.SpreadsheetExporterProviderDelegator;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProvider;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProviderDelegator;
import walkingkooka.spreadsheet.importer.SpreadsheetImporterProvider;
import walkingkooka.spreadsheet.importer.SpreadsheetImporterProviderDelegator;
import walkingkooka.spreadsheet.parser.SpreadsheetParserProvider;
import walkingkooka.spreadsheet.parser.SpreadsheetParserProviderDelegator;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProvider;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProviderDelegator;
import walkingkooka.validation.form.provider.FormHandlerProvider;
import walkingkooka.validation.form.provider.FormHandlerProviderDelegator;
import walkingkooka.validation.provider.ValidatorProvider;
import walkingkooka.validation.provider.ValidatorProviderDelegator;

public interface SpreadsheetProviderDelegator extends SpreadsheetProvider,
    ConverterProviderDelegator,
    ExpressionFunctionProviderDelegator<SpreadsheetExpressionEvaluationContext>,
    SpreadsheetComparatorProviderDelegator,
    SpreadsheetExporterProviderDelegator,
    SpreadsheetFormatterProviderDelegator,
    FormHandlerProviderDelegator,
    SpreadsheetImporterProviderDelegator,
    SpreadsheetParserProviderDelegator,
    ValidatorProviderDelegator {

    @Override
    default ConverterProvider converterProvider() {
        return this.spreadsheetProvider();
    }

    @Override
    default ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider() {
        return this.spreadsheetProvider();
    }

    @Override
    default SpreadsheetComparatorProvider spreadsheetComparatorProvider() {
        return this.spreadsheetProvider();
    }

    @Override
    default SpreadsheetExporterProvider spreadsheetExporterProvider() {
        return this.spreadsheetProvider();
    }

    @Override
    default SpreadsheetFormatterProvider spreadsheetFormatterProvider() {
        return this.spreadsheetProvider();
    }

    @Override
    default FormHandlerProvider formHandlerProvider() {
        return this.spreadsheetProvider();
    }

    @Override
    default SpreadsheetImporterProvider spreadsheetImporterProvider() {
        return this.spreadsheetProvider();
    }

    @Override
    default SpreadsheetParserProvider spreadsheetParserProvider() {
        return this.spreadsheetProvider();
    }

    @Override
    default ValidatorProvider validatorProvider() {
        return this.spreadsheetProvider();
    }

    SpreadsheetProvider spreadsheetProvider();
}

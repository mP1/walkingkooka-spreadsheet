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
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.compare.*;
import walkingkooka.spreadsheet.export.provider.SpreadsheetExporterProvider;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorProvider;
import walkingkooka.spreadsheet.export.*;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProvider;
import walkingkooka.spreadsheet.importer.provider.SpreadsheetImporterProvider;
import walkingkooka.spreadsheet.parser.SpreadsheetParserProvider;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProvider;
import walkingkooka.validation.form.provider.FormHandlerProvider;
import walkingkooka.validation.provider.ValidatorProvider;

public final class SpreadsheetProviders implements PublicStaticHelper {

    /**
     * {@see BasicSpreadsheetProvider}
     */
    public static SpreadsheetProvider basic(final ConverterProvider converterProvider,
                                            final ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider,
                                            final SpreadsheetComparatorProvider spreadsheetComparatorProvider,
                                            final SpreadsheetExporterProvider spreadsheetExporterProvider,
                                            final SpreadsheetFormatterProvider spreadsheetFormatterProvider,
                                            final FormHandlerProvider formHandlerProvider,
                                            final SpreadsheetImporterProvider spreadsheetImporterProvider,
                                            final SpreadsheetParserProvider spreadsheetParserProvider,
                                            final ValidatorProvider validatorProvider) {
        return BasicSpreadsheetProvider.with(
            converterProvider,
            expressionFunctionProvider,
            spreadsheetComparatorProvider,
            spreadsheetExporterProvider,
            spreadsheetFormatterProvider,
            formHandlerProvider,
            spreadsheetImporterProvider,
            spreadsheetParserProvider,
            validatorProvider
        );
    }

    /**
     * {@see FakeSpreadsheetProvider}
     */
    public static SpreadsheetProvider fake() {
        return new FakeSpreadsheetProvider();
    }

    private SpreadsheetProviders() {
        throw new UnsupportedOperationException();
    }
}

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
import walkingkooka.plugin.Provider;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorProvider;
import walkingkooka.spreadsheet.export.SpreadsheetExporterProvider;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProvider;
import walkingkooka.spreadsheet.importer.SpreadsheetImporterProvider;
import walkingkooka.spreadsheet.parser.SpreadsheetParserProvider;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProvider;
import walkingkooka.validation.form.provider.FormHandlerProvider;
import walkingkooka.validation.provider.ValidatorProvider;

/**
 * Aggregates all the {@link Provider provider(s)} used in a spreadsheet.
 * This should make it easier to pass instances of each around, as aswell as updates when a {@link walkingkooka.spreadsheet.meta.SpreadsheetMetadata} is updated.
 */
public interface SpreadsheetProvider extends ConverterProvider,
    ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext>,
    SpreadsheetComparatorProvider,
    SpreadsheetExporterProvider,
    SpreadsheetFormatterProvider,
    FormHandlerProvider,
    SpreadsheetParserProvider,
    SpreadsheetImporterProvider,
    ValidatorProvider {
}

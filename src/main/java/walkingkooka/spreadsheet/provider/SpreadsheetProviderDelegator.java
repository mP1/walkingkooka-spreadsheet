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
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorProvider;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorProviderDelegator;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProvider;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProviderDelegator;
import walkingkooka.spreadsheet.parser.SpreadsheetParserProvider;
import walkingkooka.spreadsheet.parser.SpreadsheetParserProviderDelegator;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProvider;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProviderDelegator;

public interface SpreadsheetProviderDelegator extends SpreadsheetProvider,
        ConverterProviderDelegator,
        ExpressionFunctionProviderDelegator,
        SpreadsheetComparatorProviderDelegator,
        SpreadsheetFormatterProviderDelegator,
        SpreadsheetParserProviderDelegator {

    @Override
    default ConverterProvider converterProvider() {
        return this.spreadsheetProvider();
    }

    @Override
    default ExpressionFunctionProvider expressionFunctionProvider() {
        return this.spreadsheetProvider();
    }

    @Override
    default SpreadsheetComparatorProvider spreadsheetComparatorProvider() {
        return this.spreadsheetProvider();
    }

    @Override
    default SpreadsheetFormatterProvider spreadsheetFormatterProvider() {
        return this.spreadsheetProvider();
    }

    @Override
    default SpreadsheetParserProvider spreadsheetParserProvider() {
        return this.spreadsheetProvider();
    }

    SpreadsheetProvider spreadsheetProvider();
}

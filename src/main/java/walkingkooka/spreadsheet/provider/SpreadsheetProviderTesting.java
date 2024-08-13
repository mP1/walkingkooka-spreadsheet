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

import walkingkooka.convert.provider.ConverterProviderTesting;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorProviderTesting;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProviderTesting;
import walkingkooka.spreadsheet.parser.SpreadsheetParserProviderTesting;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProviderTesting;

public interface SpreadsheetProviderTesting<T extends SpreadsheetProvider> extends ConverterProviderTesting<T>,
        ExpressionFunctionProviderTesting<T>,
        SpreadsheetComparatorProviderTesting<T>,
        SpreadsheetFormatterProviderTesting<T>,
        SpreadsheetParserProviderTesting<T> {

    @Override
    default T createConverterProvider() {
        return this.createSpreadsheetProvider();
    }

    @Override
    default T createSpreadsheetComparatorProvider() {
        return this.createSpreadsheetProvider();
    }

    @Override
    default T createSpreadsheetFormatterProvider() {
        return this.createSpreadsheetProvider();
    }

    @Override
    default T createSpreadsheetParserProvider() {
        return this.createSpreadsheetProvider();
    }

    @Override
    default T createExpressionFunctionProvider() {
        return this.createSpreadsheetProvider();
    }

    T createSpreadsheetProvider();
}

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
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorProvider;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProvider;
import walkingkooka.spreadsheet.parser.SpreadsheetParserProvider;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProvider;

public final class SpreadsheetProviders implements PublicStaticHelper {

    /**
     * {@see BasicSpreadsheetProvider}
     */
    public static SpreadsheetProvider basic(final ConverterProvider converterProvider,
                                            final ExpressionFunctionProvider expressionFunctionProvider,
                                            final SpreadsheetComparatorProvider spreadsheetComparatorProvider,
                                            final SpreadsheetFormatterProvider spreadsheetFormatterProvider,
                                            final SpreadsheetParserProvider spreadsheetParserProvider) {
        return BasicSpreadsheetProvider.with(
                converterProvider,
                expressionFunctionProvider,
                spreadsheetComparatorProvider,
                spreadsheetFormatterProvider,
                spreadsheetParserProvider
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
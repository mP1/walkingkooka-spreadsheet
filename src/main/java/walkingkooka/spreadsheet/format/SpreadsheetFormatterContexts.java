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

package walkingkooka.spreadsheet.format;

import walkingkooka.color.Color;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterProvider;
import walkingkooka.spreadsheet.value.SpreadsheetCell;

import java.util.Optional;
import java.util.function.Function;

public final class SpreadsheetFormatterContexts implements PublicStaticHelper {

    /**
     * {@see BasicSpreadsheetFormatterContext}
     */
    public static SpreadsheetFormatterContext basic(final Optional<SpreadsheetCell> cell,
                                                    final Function<Integer, Optional<Color>> numberToColor,
                                                    final Function<SpreadsheetColorName, Optional<Color>> nameToColor,
                                                    final int cellCharacterWidth,
                                                    final SpreadsheetFormatter defaultSpreadsheetFormatter,
                                                    final Function<Optional<Object>, SpreadsheetExpressionEvaluationContext> spreadsheetExpressionEvaluationContext,
                                                    final SpreadsheetConverterContext spreadsheetConverterContext,
                                                    final SpreadsheetFormatterProvider spreadsheetFormatterProvider,
                                                    final ProviderContext providerContext) {
        return BasicSpreadsheetFormatterContext.with(
            cell,
            numberToColor,
            nameToColor,
            cellCharacterWidth,
            defaultSpreadsheetFormatter,
            spreadsheetExpressionEvaluationContext,
            spreadsheetConverterContext,
            spreadsheetFormatterProvider,
            providerContext
        );
    }

    /**
     * {@see FakeSpreadsheetFormatterContext}
     */
    public static SpreadsheetFormatterContext fake() {
        return new FakeSpreadsheetFormatterContext();
    }

    /**
     * Stops creation
     */
    private SpreadsheetFormatterContexts() {
        throw new UnsupportedOperationException();
    }
}

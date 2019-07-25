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

package walkingkooka.spreadsheet.engine;

import walkingkooka.color.Color;
import walkingkooka.convert.Converter;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.Fraction;
import walkingkooka.spreadsheet.format.SpreadsheetTextFormatter;
import walkingkooka.spreadsheet.reference.store.SpreadsheetLabelStore;
import walkingkooka.tree.expression.ExpressionNodeName;
import walkingkooka.type.PublicStaticHelper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class SpreadsheetEngineContexts implements PublicStaticHelper {

    /**
     * {@see BasicSpreadsheetEngineContext}
     */
    public static SpreadsheetEngineContext basic(final BiFunction<ExpressionNodeName, List<Object>, Object> functions,
                                                 final SpreadsheetEngine engine,
                                                 final SpreadsheetLabelStore labelStore,
                                                 final Converter converter,
                                                 final DecimalNumberContext decimalNumberContext,
                                                 final DateTimeContext dateTimeContext,
                                                 final Function<Integer, Optional<Color>> numberToColor,
                                                 final Function<String, Optional<Color>> nameToColor,
                                                 final String generalDecimalFormatPattern,
                                                 final int width,
                                                 final Function<BigDecimal, Fraction> fractioner,
                                                 final SpreadsheetTextFormatter defaultSpreadsheetTextFormatter) {
        return BasicSpreadsheetEngineContext.with(functions,
                engine,
                labelStore,
                converter,
                decimalNumberContext,
                dateTimeContext,
                numberToColor,
                nameToColor,
                generalDecimalFormatPattern,
                width,
                fractioner,
                defaultSpreadsheetTextFormatter);
    }

    /**
     * {@see FakeSpreadsheetEngineContext}
     */
    public static SpreadsheetEngineContext fake() {
        return new FakeSpreadsheetEngineContext();
    }

    /**
     * Stops creation
     */
    private SpreadsheetEngineContexts() {
        throw new UnsupportedOperationException();
    }
}

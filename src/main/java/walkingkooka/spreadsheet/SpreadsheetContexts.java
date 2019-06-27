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

package walkingkooka.spreadsheet;

import walkingkooka.color.Color;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.spreadsheet.format.SpreadsheetTextFormatter;
import walkingkooka.tree.expression.ExpressionNodeName;
import walkingkooka.type.PublicStaticHelper;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class SpreadsheetContexts implements PublicStaticHelper {

    /**
     * {@see FakeSpreadsheetContext}
     */
    public static FakeSpreadsheetContext fake() {
        return new FakeSpreadsheetContext();
    }

    /**
     * {@see MemorySpreadsheetContext}
     */
    public static SpreadsheetContext memory(final Function<SpreadsheetId, DateTimeContext> spreadsheetIdDateTimeContext,
                                            final Function<SpreadsheetId, DecimalNumberContext> spreadsheetIdDecimalFormatContext,
                                            final Function<SpreadsheetId, SpreadsheetTextFormatter<?>> spreadsheetIdDefaultSpreadsheetTextFormatter,
                                            final Function<SpreadsheetId, BiFunction<ExpressionNodeName, List<Object>, Object>> spreadsheetIdFunctions,
                                            final Function<SpreadsheetId, String> spreadsheetIdGeneralDecimalFormatPattern,
                                            final Function<SpreadsheetId, Function<String, Color> > spreadsheetIdNameToColor,
                                            final Function<SpreadsheetId, Function<Integer, Color> > spreadsheetIdNumberToColor,
                                            final Function<SpreadsheetId, Integer> spreadsheetIdWidth) {
        return MemorySpreadsheetContext.with(spreadsheetIdDateTimeContext,
                spreadsheetIdDecimalFormatContext,
                spreadsheetIdDefaultSpreadsheetTextFormatter,
                spreadsheetIdFunctions,
                spreadsheetIdGeneralDecimalFormatPattern,
                spreadsheetIdNameToColor,
                spreadsheetIdNumberToColor,
                spreadsheetIdWidth);
    }

    /**
     * Stop creation
     */
    private SpreadsheetContexts() {
        throw new UnsupportedOperationException();
    }
}

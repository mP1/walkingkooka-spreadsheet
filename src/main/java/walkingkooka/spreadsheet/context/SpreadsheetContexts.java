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

package walkingkooka.spreadsheet.context;

import walkingkooka.color.Color;
import walkingkooka.convert.Converter;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.Fraction;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.http.server.hateos.HateosContentType;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.tree.Node;
import walkingkooka.tree.expression.ExpressionNodeName;
import walkingkooka.type.PublicStaticHelper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
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
    public static <N extends Node<N, ?, ?, ?>> SpreadsheetContext memory(final AbsoluteUrl base,
                                                                         final HateosContentType<N> contentType,
                                                                         final Function<BigDecimal, Fraction> fractioner,
                                                                         final Function<Optional<Locale>, SpreadsheetMetadata> metadataWithDefaults,
                                                                         final Function<SpreadsheetId, Converter> spreadsheetIdConverter,
                                                                         final Function<SpreadsheetId, DateTimeContext> spreadsheetIdDateTimeContext,
                                                                         final Function<SpreadsheetId, DecimalNumberContext> spreadsheetIdDecimalNumberContext,
                                                                         final Function<SpreadsheetId, SpreadsheetFormatter> spreadsheetIdDefaultSpreadsheetTextFormatter,
                                                                         final Function<SpreadsheetId, BiFunction<ExpressionNodeName, List<Object>, Object>> spreadsheetIdFunctions,
                                                                         final Function<SpreadsheetId, Function<String, Optional<Color>>> spreadsheetIdNameToColor,
                                                                         final Function<SpreadsheetId, Function<Integer, Optional<Color>>> spreadsheetIdNumberToColor,
                                                                         final Function<SpreadsheetId, Integer> spreadsheetIdWidth) {
        return MemorySpreadsheetContext.with(base,
                contentType,
                fractioner,
                metadataWithDefaults,
                spreadsheetIdConverter,
                spreadsheetIdDateTimeContext,
                spreadsheetIdDecimalNumberContext,
                spreadsheetIdDefaultSpreadsheetTextFormatter,
                spreadsheetIdFunctions,
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

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
import walkingkooka.convert.Converter;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.type.PublicStaticHelper;

import java.util.Optional;
import java.util.function.Function;

public final class SpreadsheetTextFormatContexts implements PublicStaticHelper {

    /**
     * {@see BasicSpreadsheetTextFormatContext}
     */
    public static SpreadsheetTextFormatContext basic(final Function<Integer, Optional<Color>> numberToColor,
                                                     final Function<String, Optional<Color>> nameToColor,
                                                     final String generalDecimalFormatPattern,
                                                     final int width,
                                                     final Converter converter,
                                                     final DateTimeContext dateTimeContext,
                                                     final DecimalNumberContext decimalNumberContext) {
        return BasicSpreadsheetTextFormatContext.with(numberToColor,
                nameToColor,
                generalDecimalFormatPattern,
                width,
                converter,
                dateTimeContext,
                decimalNumberContext);
    }

    /**
     * {@see FakeSpreadsheetTextFormatContext}
     */
    public static SpreadsheetTextFormatContext fake() {
        return new FakeSpreadsheetTextFormatContext();
    }

    /**
     * Stops creation
     */
    private SpreadsheetTextFormatContexts() {
        throw new UnsupportedOperationException();
    }
}

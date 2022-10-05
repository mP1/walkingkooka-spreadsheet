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

package walkingkooka.spreadsheet.convert;

import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.Converters;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateParsePatterns;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateTimeParsePatterns;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetNumberParsePatterns;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetTimeParsePatterns;

public final class SpreadsheetConverters implements PublicStaticHelper {

    /**
     * A basic {@link Converter} that supports number -> number, date -> datetime, time -> datetime.
     */
    public static <C extends ConverterContext> Converter<C> basic() {
        return Cast.to(CONVERTER);
    }

    private final static Converter<ConverterContext> CONVERTER = Converters.collection(
            Lists.of(
                    Converters.simple(),
                    Converters.numberNumber(),
                    Converters.localDateLocalDateTime(),
                    Converters.localTimeLocalDateTime()
            )
    );

    /**
     * {@see SpreadsheetErrorConverter}
     */
    public static <C extends ConverterContext> Converter<C> error() {
        return Cast.to(SpreadsheetErrorConverter.INSTANCE);
    }

    /**
     * {@see GeneralSpreadsheetConverter}
     */
    public static Converter<SpreadsheetConverterContext> general(final SpreadsheetFormatter dateFormatter,
                                                                 final SpreadsheetDateParsePatterns dateParser,
                                                                 final SpreadsheetFormatter dateTimeFormatter,
                                                                 final SpreadsheetDateTimeParsePatterns dateTimeParser,
                                                                 final SpreadsheetFormatter numberFormatter,
                                                                 final SpreadsheetNumberParsePatterns numberParser,
                                                                 final SpreadsheetFormatter textFormatter,
                                                                 final SpreadsheetFormatter timeFormatter,
                                                                 final SpreadsheetTimeParsePatterns timeParser,
                                                                 final long dateOffset) {
        return GeneralSpreadsheetConverter.with(dateFormatter,
                dateParser,
                dateTimeFormatter,
                dateTimeParser,
                numberFormatter,
                numberParser,
                textFormatter,
                timeFormatter,
                timeParser,
                dateOffset);
    }

    /**
     * {@see SpreadsheetSelectionToSpreadsheetSelectionConverter}
     */
    public static Converter<SpreadsheetConverterContext> selectionToSelection() {
        return SpreadsheetSelectionToSpreadsheetSelectionConverter.INSTANCE;
    }

    /**
     * {@see StringToSpreadsheetSelectionConverter}
     */
    public static Converter<SpreadsheetConverterContext> stringToSelection() {
        return StringToSpreadsheetSelectionConverter.INSTANCE;
    }

    /**
     * {@see StringToFormatPatternConverter}
     */
    public static Converter<SpreadsheetConverterContext> stringToFormatPattern(final String pattern) {
        return StringToFormatPatternConverter.with(pattern);
    }

    /**
     * {@see UnformattedNumberSpreadsheetConverter}
     */
    public static Converter<SpreadsheetConverterContext> unformattedNumber() {
        return UnformattedNumberSpreadsheetConverter.INSTANCE;
    }

    /**
     * Stop creation
     */
    private SpreadsheetConverters() {
        throw new UnsupportedOperationException();
    }
}

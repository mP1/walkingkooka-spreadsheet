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
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateParsePattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateTimeParsePattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetNumberParsePattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetTimeParsePattern;
import walkingkooka.spreadsheet.parser.SpreadsheetNumberParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberConverterContext;

public final class SpreadsheetConverters implements PublicStaticHelper {

    /**
     * A basic {@link Converter} that supports number -> number, date -> datetime, time -> datetime.
     */
    public static <C extends ExpressionNumberConverterContext> Converter<C> basic() {
        return Cast.to(CONVERTER);
    }

    private final static Converter<ExpressionNumberConverterContext> CONVERTER = Converters.collection(
            Lists.of(
                    Converters.simple(),
                    ExpressionNumber.toConverter(
                            Converters.numberNumber()
                    ),
                    Converters.localDateLocalDateTime(),
                    Converters.localTimeLocalDateTime()
            )
    );

    /**
     * {@see SpreadsheetErrorThrowingConverter}
     */
    public static Converter<ConverterContext> errorThrowing() {
        return SpreadsheetErrorThrowingConverter.INSTANCE;
    }

    /**
     * {@see SpreadsheetErrorToNumberConverter}
     */
    public static Converter<SpreadsheetConverterContext> errorToNumber() {
        return SpreadsheetErrorToNumberConverter.INSTANCE;
    }

    /**
     * {@see SpreadsheetErrorToStringConverter}
     */
    public static Converter<ConverterContext> errorToString() {
        return SpreadsheetErrorToStringConverter.INSTANCE;
    }

    /**
     * A {@link Converter} that uses the given {@link Parser} to parse text into a {@link SpreadsheetNumberParserToken} and converting
     * that into a {@link ExpressionNumber}.
     */
    public static Converter<SpreadsheetConverterContext> expressionNumber(final Parser<SpreadsheetParserContext> parser) {
        return Converters.parser(
                ExpressionNumber.class, // parserValueType
                parser, // parser
                (final SpreadsheetConverterContext scc) -> SpreadsheetParserContexts.basic(
                        scc,
                        scc,
                        '0' // valueSeparator not required because not parsing multiple values.
                ),
                (final ParserToken t,
                 final SpreadsheetConverterContext scc) -> t.cast(SpreadsheetNumberParserToken.class)
                        .toNumber(scc)
        );
    }

    /**
     * {@see GeneralSpreadsheetConverter}
     */
    public static Converter<SpreadsheetConverterContext> general(final SpreadsheetFormatter dateFormatter,
                                                                 final SpreadsheetDateParsePattern dateParser,
                                                                 final SpreadsheetFormatter dateTimeFormatter,
                                                                 final SpreadsheetDateTimeParsePattern dateTimeParser,
                                                                 final SpreadsheetFormatter numberFormatter,
                                                                 final SpreadsheetNumberParsePattern numberParser,
                                                                 final SpreadsheetFormatter textFormatter,
                                                                 final SpreadsheetFormatter timeFormatter,
                                                                 final SpreadsheetTimeParsePattern timeParser,
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

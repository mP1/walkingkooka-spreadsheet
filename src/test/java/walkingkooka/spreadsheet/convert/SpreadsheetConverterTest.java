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

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.ConverterTesting2;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetFormatters;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateTimeParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatNumberParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTextParserToken;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateParsePatterns;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateTimeParsePatterns;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetNumberParsePatterns;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetParsePatterns;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetTimeParsePatterns;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberConverterContext;
import walkingkooka.tree.expression.ExpressionNumberConverterContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetConverterTest extends SpreadsheetConverterTestCase<SpreadsheetConverter>
        implements ConverterTesting2<SpreadsheetConverter, ExpressionNumberConverterContext> {

    private final static long DATE_OFFSET = Converters.JAVA_EPOCH_OFFSET;
    private final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.DEFAULT;

    // with.............................................................................................................

    @Test
    public void testWithNullDateFormatterFails() {
        withFails(null,
                dateParser(),
                dateTimeFormatter(),
                dateTimeParser(),
                numberFormatter(),
                numberParser(),
                textFormatter(),
                timeFormatter(),
                timeParser());
    }

    @Test
    public void testWithNullDateParserFails() {
        withFails(dateFormatter(),
                null,
                dateTimeFormatter(),
                dateTimeParser(),
                numberFormatter(),
                numberParser(),
                textFormatter(),
                timeFormatter(),
                timeParser());
    }

    @Test
    public void testWithNullDateTimeFormatterFails() {
        withFails(dateFormatter(),
                dateParser(),
                null,
                dateTimeParser(),
                numberFormatter(),
                numberParser(),
                textFormatter(),
                timeFormatter(),
                timeParser());
    }

    @Test
    public void testWithNullDateTimeParserFails() {
        withFails(dateFormatter(),
                dateParser(),
                dateTimeFormatter(),
                null,
                numberFormatter(),
                numberParser(),
                textFormatter(),
                timeFormatter(),
                timeParser());
    }

    @Test
    public void testWithNullNumberFormatterFails() {
        withFails(dateFormatter(),
                dateParser(),
                dateTimeFormatter(),
                dateTimeParser(),
                null,
                numberParser(),
                textFormatter(),
                timeFormatter(),
                timeParser());
    }

    @Test
    public void testWithNullNumberParserFails() {
        withFails(dateFormatter(),
                dateParser(),
                dateTimeFormatter(),
                dateTimeParser(),
                numberFormatter(),
                null,
                textFormatter(),
                timeFormatter(),
                timeParser());
    }

    @Test
    public void testWithNullTextFormatterFails() {
        withFails(dateFormatter(),
                dateParser(),
                dateTimeFormatter(),
                dateTimeParser(),
                numberFormatter(),
                numberParser(),
                null,
                timeFormatter(),
                timeParser());
    }

    @Test
    public void testWithNullTimeFormatterFails() {
        withFails(dateFormatter(),
                dateParser(),
                dateTimeFormatter(),
                dateTimeParser(),
                numberFormatter(),
                numberParser(),
                textFormatter(),
                null,
                timeParser());
    }

    @Test
    public void testWithNullTimeParserFails() {
        withFails(dateFormatter(),
                dateParser(),
                dateTimeFormatter(),
                dateTimeParser(),
                numberFormatter(),
                numberParser(),
                textFormatter(),
                timeFormatter(),
                null);
    }

    private void withFails(final SpreadsheetFormatter dateFormatter,
                           final SpreadsheetDateParsePatterns dateParser,
                           final SpreadsheetFormatter dateTimeFormatter,
                           final SpreadsheetDateTimeParsePatterns dateTimeParser,
                           final SpreadsheetFormatter numberFormatter,
                           final SpreadsheetNumberParsePatterns numberParser,
                           final SpreadsheetFormatter textFormatter,
                           final SpreadsheetFormatter timeFormatter,
                           final SpreadsheetTimeParsePatterns timeParser) {
        assertThrows(NullPointerException.class, () -> SpreadsheetConverter.with(dateFormatter,
                dateParser,
                dateTimeFormatter,
                dateTimeParser,
                numberFormatter,
                numberParser,
                textFormatter,
                timeFormatter,
                timeParser,
                DATE_OFFSET));
    }

    // convert..........................................................................................................

    private final static int NUMBER_TRUE = 1;
    private final static int NUMBER_FALSE = 0;

    private final static LocalDate DATE = LocalDate.of(2000, 12, 31);
    private final static LocalDate DATE_TRUE = LocalDate.ofEpochDay(NUMBER_TRUE + DATE_OFFSET);
    private final static LocalDate DATE_FALSE = LocalDate.ofEpochDay(NUMBER_FALSE + DATE_OFFSET);

    private final static LocalTime TIME = LocalTime.of(12, 58, 59);
    private final static LocalTime TIME_TRUE = LocalTime.ofSecondOfDay(NUMBER_TRUE);
    private final static LocalTime TIME_FALSE = LocalTime.ofSecondOfDay(NUMBER_FALSE);

    private final static LocalDateTime DATE_TIME_TRUE = LocalDateTime.of(DATE_TRUE, LocalTime.MIDNIGHT);
    private final static LocalDateTime DATE_TIME_FALSE = LocalDateTime.of(DATE_FALSE, LocalTime.MIDNIGHT);
    private final static LocalDateTime DATE_TIME = LocalDateTime.of(DATE, TIME);

    private final static String STRING_TRUE = "true";

    // boolean..........................................................................................................

    @Test
    public void testBooleanTrueBoolean() {
        this.convertAndCheck(true);
    }

    @Test
    public void testBooleanFalseBoolean() {
        this.convertAndCheck(false);
    }

    @Test
    public void testBooleanTrueDate() {
        this.convertAndBackCheck(true, DATE_TRUE);
    }

    @Test
    public void testBooleanFalseDate() {
        this.convertAndBackCheck(false, DATE_FALSE);
    }

    @Test
    public void testBooleanTrueDateTime() {
        this.convertAndBackCheck(true, DATE_TIME_TRUE);
    }

    @Test
    public void testBooleanFalseDateTime() {
        this.convertAndBackCheck(false, DATE_TIME_FALSE);
    }

    @Test
    public void testBooleanTrueNumber() {
        this.convertNumberAndCheck(true, NUMBER_TRUE);
    }

    @Test
    public void testBooleanFalseNumber() {
        this.convertNumberAndCheck(false, NUMBER_FALSE);
    }

    @Test
    public void testBooleanTrueString() {
        this.convertAndCheck2(true, STRING_TRUE + TEXT_SUFFIX);
    }

    @Test
    public void testBooleanFalseString() {
        this.convertAndCheck2(false, false + TEXT_SUFFIX);
    }

    @Test
    public void testBooleanTrueTime() {
        this.convertAndBackCheck(true, TIME_TRUE);
    }

    @Test
    public void testBooleanFalseTime() {
        this.convertAndBackCheck(false, TIME_FALSE);
    }

    // Date.............................................................................................................

    @Test
    public void testDateTrueBoolean() {
        this.convertAndCheck2(DATE_TRUE, true);
    }

    @Test
    public void testDateFalseBoolean() {
        this.convertAndCheck2(DATE_FALSE, false);
    }

    @Test
    public void testDateDate() {
        this.convertAndCheck(LocalDate.of(2000, 12, 31));
    }

    @Test
    public void testDateTrueDateTime() {
        this.convertAndBackCheck(DATE_TRUE, LocalDateTime.of(DATE_TRUE, LocalTime.MIDNIGHT));
    }

    @Test
    public void testDateFalseDateTime() {
        this.convertAndBackCheck(DATE_FALSE, LocalDateTime.of(DATE_FALSE, LocalTime.MIDNIGHT));
    }

    @Test
    public void testDateTrueNumber() {
        this.convertNumberAndCheck(DATE_TRUE, NUMBER_TRUE);
    }

    @Test
    public void testDateFalseNumber() {
        this.convertNumberAndCheck(DATE_FALSE, NUMBER_FALSE);
    }

    @Test
    public void testDateNumber() {
        final int value = 100;
        this.convertNumberAndCheck(LocalDate.ofEpochDay(value), value);
    }

    @Test
    public void testDateTrueString() {
        this.convertAndCheck2(DATE, "D 2000-12-31");
    }

    @Test
    public void testDateTime() {
        this.convertFails(DATE, LocalTime.class);
    }

    // DateTime.........................................................................................................

    @Test
    public void testDateTimeTrueBoolean() {
        this.convertAndCheck2(DATE_TIME_TRUE, true);
    }

    @Test
    public void testDateTimeFalseBoolean() {
        this.convertAndCheck2(DATE_TIME_FALSE, false);
    }

    @Test
    public void testDateTimeDate() {
        this.convertAndCheck2(DATE_TIME, DATE);
    }

    @Test
    public void testDateTimeDateTime() {
        this.convertAndCheck(DATE_TIME);
    }

    @Test
    public void testDateTimeTrueNumber() {
        this.convertNumberAndCheck(DATE_TIME_TRUE, NUMBER_TRUE);
    }

    @Test
    public void testDateTimeFalseNumber() {
        this.convertNumberAndCheck(DATE_TIME_FALSE, NUMBER_FALSE);
    }

    @Test
    public void testDateTimeNumber() {
        final int value = 100;
        this.convertNumberAndCheck(LocalDateTime.of(LocalDate.ofEpochDay(value), LocalTime.MIDNIGHT), value);
    }

    @Test
    public void testDateTimeTrueString() {
        this.convertAndCheck2(DATE_TIME, "DT 2000-12-31 12-58");
    }

    @Test
    public void testDateTimeTime() {
        this.convertAndCheck2(DATE_TIME, TIME);
    }

    // Number...........................................................................................................

    @Test
    public void testNumberTrueBoolean() {
        this.convertNumberAndCheck(1, true);
    }

    @Test
    public void testNumberFalseBoolean() {
        this.convertNumberAndCheck(0, false);
    }

    @Test
    public void testNumberTrueDate() {
        this.convertAndBackCheck(1, DATE_TRUE);
    }

    @Test
    public void testNumberFalseDate() {
        this.convertAndBackCheck(0, DATE_FALSE);
    }

    @Test
    public void testNumberTrueDateTime() {
        this.convertAndBackCheck(1, DATE_TIME_TRUE);
    }

    @Test
    public void testNumberFalseDateTime() {
        this.convertAndBackCheck(0, DATE_TIME_FALSE);
    }

    @Test
    public void testNumberByte() {
        this.convertNumberAndCheck((byte) 123);
    }

    @Test
    public void testNumberShort() {
        this.convertNumberAndCheck((short) 123);
    }

    @Test
    public void testNumberInteger() {
        this.convertNumberAndCheck(123);
    }

    @Test
    public void testNumberLong() {
        this.convertNumberAndCheck(123L);
    }

    @Test
    public void testNumberFloat() {
        this.convertNumberAndCheck(123f);
    }

    @Test
    public void testNumberDouble() {
        this.convertNumberAndCheck(123.0);
    }

    @Test
    public void testNumberBigDecimal() {
        this.convertNumberAndCheck(BigDecimal.valueOf(123));
    }

    @Test
    public void testNumberBigInteger() {
        this.convertNumberAndCheck(BigInteger.valueOf(123));
    }

    @Test
    public void testNumberTrueString() {
        this.convertAndCheck2(12.5, "N 12D5");
    }

    @Test
    public void testNumberFalseString() {
        this.convertAndCheck2(false, TIME_FALSE);
    }

    @Test
    public void testNumberTime() {
        this.convertAndBackCheck(Converters.localTimeNumber()
                        .convertOrFail(TIME, BigDecimal.class, this.createContext()),
                TIME);
    }

    private void convertNumberAndCheck(final Number value) {
        this.convertNumberAndCheck(value, value);
    }

    private void convertNumberAndCheck(final Number value,
                                       final Object expected) {
        final Converter numberNumber = Converters.numberNumber();
        final ConverterContext context = this.createContext();

        this.convertAndBackCheck(numberNumber.convertOrFail(value, BigDecimal.class, context), expected);
        this.convertAndBackCheck(numberNumber.convertOrFail(value, BigInteger.class, context), expected);
        this.convertAndBackCheck(numberNumber.convertOrFail(value, Byte.class, context), expected);
        this.convertAndBackCheck(numberNumber.convertOrFail(value, Double.class, context), expected);
        this.convertAndBackCheck(numberNumber.convertOrFail(value, Float.class, context), expected);
        this.convertAndBackCheck(numberNumber.convertOrFail(value, Integer.class, context), expected);
        this.convertAndBackCheck(numberNumber.convertOrFail(value, Long.class, context), expected);
        this.convertAndBackCheck(numberNumber.convertOrFail(value, Short.class, context), expected);
    }

    // String.............................................................................................................

    @Test
    public void testStringString() {
        final String text = "abc123";
        this.convertAndCheck2(text, text);
    }

    // Time.............................................................................................................

    @Test
    public void testTimeTrueBoolean() {
        this.convertAndCheck2(TIME_TRUE, true);
    }

    @Test
    public void testTimeFalseBoolean() {
        this.convertAndCheck2(TIME_FALSE, false);
    }

    @Test
    public void testTimeDate() {
        this.convertFails(TIME, LocalDate.class);
    }

    @Test
    public void testTimeDateTime() {
        this.convertAndCheck2(TIME, LocalDateTime.of(DATE_FALSE, TIME));
    }

    @Test
    public void testTimeTrueNumber() {
        this.convertNumberAndCheck(TIME_TRUE, NUMBER_TRUE);
    }

    @Test
    public void testTimeFalseNumber() {
        this.convertNumberAndCheck(TIME_FALSE, NUMBER_FALSE);
    }

    @Test
    public void testTimeNumber() {
        final int value = 123;
        this.convertNumberAndCheck(LocalTime.ofSecondOfDay(value), value);
    }

    @Test
    public void testTimeTrueString() {
        this.convertAndCheck2(DATE, "D 2000-12-31");
    }

    @Test
    public void testTimeTime() {
        this.convertAndCheck(TIME);
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createConverter(), SpreadsheetConverter.class.getSimpleName());
    }

    // ConverterTesting.................................................................................................

    @Override
    public SpreadsheetConverter createConverter() {
        return SpreadsheetConverter.with(dateFormatter(),
                dateParser(),
                dateTimeFormatter(),
                dateTimeParser(),
                numberFormatter(),
                numberParser(),
                textFormatter(),
                timeFormatter(),
                timeParser(),
                DATE_OFFSET);
    }

    private SpreadsheetFormatter dateFormatter() {
        return dateTimeFormatter("\\D yyyy-mm-dd");
    }

    private SpreadsheetDateParsePatterns dateParser() {
        return SpreadsheetParsePatterns.parseDateParsePatterns("\\D yyyy-mm-dd");
    }

    private SpreadsheetFormatter dateTimeFormatter() {
        return dateTimeFormatter("\"DT\" yyyy-mm-dd hh-mm"); // dateTimeFormatter
    }

    private SpreadsheetDateTimeParsePatterns dateTimeParser() {
        return SpreadsheetParsePatterns.parseDateTimeParsePatterns("\"DT\" dd mm yyyy hh mm ss");
    }

    private SpreadsheetFormatter numberFormatter() {
        return formatter("\\N #.#",
                SpreadsheetFormatParsers.number(),
                SpreadsheetFormatNumberParserToken.class,
                SpreadsheetFormatters::number);
    }

    private SpreadsheetNumberParsePatterns numberParser() {
        return SpreadsheetParsePatterns.parseNumberParsePatterns("\"N\" #;\"N\" #.#");
    }

    private SpreadsheetFormatter textFormatter() {
        return formatter("@\"" + TEXT_SUFFIX + "\"",
                SpreadsheetFormatParsers.text(),
                SpreadsheetFormatTextParserToken.class,
                SpreadsheetFormatters::text);
    }

    private final static String TEXT_SUFFIX = "text-literal-123";

    private SpreadsheetFormatter timeFormatter() {
        return dateTimeFormatter("\\T hh-mm");
    }

    private SpreadsheetTimeParsePatterns timeParser() {
        return SpreadsheetParsePatterns.parseTimeParsePatterns("\\T hh mm ss");
    }

    private SpreadsheetFormatter dateTimeFormatter(final String pattern) {
        return formatter(pattern,
                SpreadsheetFormatParsers.dateTime(),
                SpreadsheetFormatDateTimeParserToken.class,
                (t) -> SpreadsheetFormatters.dateTime(t, v -> v instanceof LocalDateTime));
    }

    private <T extends SpreadsheetFormatParserToken> SpreadsheetFormatter formatter(final String pattern,
                                                                                    final Parser<SpreadsheetFormatParserContext> parser,
                                                                                    final Class<T> token,
                                                                                    final Function<T, SpreadsheetFormatter> formatterFactory) {
        return parser.orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(TextCursors.charSequence(pattern), SpreadsheetFormatParserContexts.basic())
                .map(token::cast)
                .map(formatterFactory)
                .orElse(SpreadsheetFormatters.fake()); // orElse wont happen.
    }

    @Override
    public ExpressionNumberConverterContext createContext() {
        return ExpressionNumberConverterContexts.basic(Converters.fake(),
                ConverterContexts.basic(
                        Converters.fake(),
                        DateTimeContexts.locale(
                                Locale.ENGLISH,
                                1900,
                                20
                        ),
                        DecimalNumberContexts.basic("C",
                                'D',
                                "E",
                                'G',
                                'M',
                                'P',
                                'L',
                                Locale.ENGLISH,
                                MathContext.DECIMAL32)),
                EXPRESSION_NUMBER_KIND);
    }

    private void convertAndCheck2(final Object value,
                                  final Object expected) {
        this.convertAndCheck(value,
                Cast.to(expected.getClass()),
                expected);
    }

    private void convertAndBackCheck(final Object value,
                                     final Object expected) {
        this.convertAndCheck2(value, expected);
        this.convertAndCheck2(expected, value);
    }

    private void convertNumberAndCheck(final Object value,
                                       final double expected) {
        final Converter<ExpressionNumberConverterContext> converter = this.createConverter();
        final ExpressionNumberConverterContext context = this.createContext();

        this.convertAndBackCheck(value, converter.convertOrFail(expected, BigDecimal.class, context));
        this.convertAndBackCheck(value, converter.convertOrFail(expected, BigInteger.class, context));
        this.convertAndBackCheck(value, converter.convertOrFail(expected, Byte.class, context));
        this.convertAndBackCheck(value, converter.convertOrFail(expected, Double.class, context));
        this.convertAndBackCheck(value, converter.convertOrFail(expected, Float.class, context));
        this.convertAndBackCheck(value, converter.convertOrFail(expected, Integer.class, context));
        this.convertAndBackCheck(value, converter.convertOrFail(expected, Long.class, context));
        this.convertAndBackCheck(value, converter.convertOrFail(expected, Short.class, context));
        this.convertAndBackCheck(value, converter.convertOrFail(expected, ExpressionNumber.class, context));
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetConverter> type() {
        return SpreadsheetConverter.class;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNameSuffix() {
        return "";
    }
}

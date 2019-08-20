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
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateParsePatterns;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateTimeParsePatterns;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetNumberParsePatterns;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetParsePatterns;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetTimeParsePatterns;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserReporters;

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
        implements ConverterTesting2<SpreadsheetConverter> {

    private final static long DATE_OFFSET = Converters.JAVA_EPOCH_OFFSET;

    // with.............................................................................................................

    @Test
    public void testWithNullDateFormatterFails() {
        withFails(null,
                dateParser(),
                dateTimeFormatter(),
                dateTimeParser(),
                numberFormatter(),
                numberParser(),
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
                timeFormatter(),
                null);
    }

    private void withFails(final SpreadsheetFormatter dateFormatter,
                           final SpreadsheetDateParsePatterns dateParser,
                           final SpreadsheetFormatter dateTimeFormatter,
                           final SpreadsheetDateTimeParsePatterns dateTimeParser,
                           final SpreadsheetFormatter numberFormatter,
                           final SpreadsheetNumberParsePatterns numberParser,
                           final SpreadsheetFormatter timeFormatter,
                           final SpreadsheetTimeParsePatterns timeParser) {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetConverter.with(dateFormatter,
                    dateParser,
                    dateTimeFormatter,
                    dateTimeParser,
                    numberFormatter,
                    numberParser,
                    timeFormatter,
                    timeParser,
                    DATE_OFFSET);
        });
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

    private final static LocalDateTime DATE_TIME = LocalDateTime.of(DATE, TIME);
    private final static LocalDateTime DATE_TIME_TRUE = LocalDateTime.of(DATE_TRUE, LocalTime.MIDNIGHT);
    private final static LocalDateTime DATE_TIME_FALSE = LocalDateTime.of(DATE_FALSE, LocalTime.MIDNIGHT);

    private final static String STRING_FALSE = "";
    private final static String STRING_TRUE = "true1";

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
        this.convertAndCheck2(true, STRING_TRUE);
    }

    @Test
    public void testBooleanFalseString() {
        this.convertAndCheck2(false, TIME_FALSE);
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
        this.convertAndCheck2(DATE_FALSE,false);
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
    public void testDateTrueString() {
        this.convertAndCheck2(DATE, "D 2000-12-31");
    }

    // Number...........................................................................................................

    @Test
    public void testNumberTrueBoolean() {
        this.convertNumberAndCheck(1,true);
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
        this.convertNumberAndCheck((byte)123);
    }

    @Test
    public void testNumberShort() {
        this.convertNumberAndCheck((short)123);
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
        this.convertAndCheck2(12.5, STRING_TRUE);
    }

    @Test
    public void testNumberFalseString() {
        this.convertAndCheck2(false, TIME_FALSE);
    }

    @Test
    public void testNumberTime() {
        this.convertAndBackCheck(Converters.localTimeNumber().convert(TIME, BigDecimal.class, this.createContext()),
                TIME);
    }

    private void convertNumberAndCheck(final Number value) {
        this.convertNumberAndCheck(value, value);
    }

    private void convertNumberAndCheck(final Number value,
                                       final Object expected) {
        final Converter numberNumber = Converters.numberNumber();
        final ConverterContext context = this.createContext();

        this.convertAndBackCheck(numberNumber.convert(value, BigDecimal.class, context), expected);
        this.convertAndBackCheck(numberNumber.convert(value, BigInteger.class, context), expected);
        this.convertAndBackCheck(numberNumber.convert(value, Byte.class, context), expected);
        this.convertAndBackCheck(numberNumber.convert(value, Double.class, context), expected);
        this.convertAndBackCheck(numberNumber.convert(value, Float.class, context), expected);
        this.convertAndBackCheck(numberNumber.convert(value, Integer.class, context), expected);
        this.convertAndBackCheck(numberNumber.convert(value, Long.class, context), expected);
        this.convertAndBackCheck(numberNumber.convert(value, Short.class, context), expected);
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
                SpreadsheetFormatters::localDateTime);
    }

    private <T extends SpreadsheetFormatParserToken> SpreadsheetFormatter formatter(final String pattern,
                                                                                    final Parser<SpreadsheetFormatParserContext> parser,
                                                                                    final Class<T> token,
                                                                                    final Function<T, SpreadsheetFormatter> formatterFactory) {
        return parser.orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(TextCursors.charSequence(pattern), SpreadsheetFormatParserContexts.basic())
                .map(token::cast)
                .map(t -> formatterFactory.apply(t))
                .orElse(SpreadsheetFormatters.fake()); // orElse wont happen.
    }

    @Override
    public ConverterContext createContext() {
        return ConverterContexts.basic(
                DateTimeContexts.locale(Locale.ENGLISH, 20),
                DecimalNumberContexts.basic("C",
                        'D',
                        'E',
                        'G',
                        'M',
                        'P',
                        'L',
                        Locale.ENGLISH,
                        MathContext.DECIMAL32));
    }

    private void convertAndCheck2(final Object value,
                                  final Object expected) {
        this.convertAndCheck(value, expected.getClass(), expected);
    }

    private void convertAndBackCheck(final Object value,
                                     final Object expected) {
        this.convertAndCheck2(value, expected);
        this.convertAndCheck2(expected, value);
    }

    private void convertNumberAndCheck(final Object value,
                                       final double expected) {
        final Converter numberNumber = Converters.numberNumber();
        final ConverterContext context = this.createContext();

        this.convertAndBackCheck(value, numberNumber.convert(expected, BigDecimal.class, context));
        this.convertAndBackCheck(value, numberNumber.convert(expected, BigInteger.class, context));
        this.convertAndBackCheck(value, numberNumber.convert(expected, Byte.class, context));
        this.convertAndBackCheck(value, numberNumber.convert(expected, Double.class, context));
        this.convertAndBackCheck(value, numberNumber.convert(expected, Float.class, context));
        this.convertAndBackCheck(value, numberNumber.convert(expected, Integer.class, context));
        this.convertAndBackCheck(value, numberNumber.convert(expected, Long.class, context));
        this.convertAndBackCheck(value, numberNumber.convert(expected, Short.class, context));
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

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

import org.junit.jupiter.api.Test;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateTimeParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.text.cursor.parser.Parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class LocalDateTimeSpreadsheetFormatterTest extends SpreadsheetFormatter3TestCase<
        LocalDateTimeSpreadsheetFormatter,
        SpreadsheetFormatDateTimeParserToken> {

    @Override
    public void testCanFormatFalse() {
        // LocalDateTimeSpreadsheetFormatter says it can format anything. It converts all values to LocalDateTime before formatting.
    }

    @Override
    public void testFormatUnsupportedValueFails() {
    }

    // year.............................................................................................................

    @Test
    public void testYear2000() {
        this.parseFormatAndCheck("y", this.text(), "00");
    }

    @Test
    public void testYear1999() {
        this.parseFormatAndCheck("y", "1999-12-31T15:58:59.000", "99");
    }

    @Test
    public void testYearYear2000() {
        this.parseFormatAndCheck("yy", this.text(), "00");
    }

    @Test
    public void testYearYear1999() {
        this.parseFormatAndCheck("yy", "1999-12-31T15:58:59.000", "99");
    }

    @Test
    public void testYearYearYear000() {
        this.parseFormatAndCheck("yyy", this.text(), "2000");
    }

    @Test
    public void testYearYearYear1999() {
        this.parseFormatAndCheck("yyy", "1999-12-31T15:58:59.000", "1999");
    }

    @Test
    public void testYearYearYearYear2000() {
        this.parseFormatAndCheck("yyyy", this.text(), "2000");
    }

    @Test
    public void testYearYearYearYear1999() {
        this.parseFormatAndCheck("yyyy", "1999-12-31T15:58:59.000", "1999");
    }

    @Test
    public void testYearYearYearYear789() {
        this.parseFormatAndCheck("yyyy", "0789-12-31T15:58:59.000", "789");
    }

    // month.............................................................................................

    @Test
    public void testMonthDecember() {
        this.parseFormatAndCheck("m", this.text(), "12");
    }

    @Test
    public void testMonthJanuary() {
        this.parseFormatAndCheck("m", "1999-01-31T15:58:59.000", "1");
    }

    @Test
    public void testMonthMonthDecember() {
        this.parseFormatAndCheck("mm", this.text(), "12");
    }

    @Test
    public void testMonthMonthJanuary() {
        this.parseFormatAndCheck("mm", "1999-01-31T15:58:59.000", "01");
    }

    @Test
    public void testMonthMonthMonthDecember() {
        this.parseFormatAndCheckMMM("2000-12-31T15:58:59.000", 12, "Dec!");
    }

    @Test
    public void testMonthMonthMonthJanuary() {
        this.parseFormatAndCheckMMM("1999-01-31T15:58:59.000", 1, "Jan!");
    }

    private void parseFormatAndCheckMMM(final String date, final int monthNumber, final String monthName) {
        this.parseFormatAndCheck("mmm",
                date,
                new TestSpreadsheetFormatterContext() {
                    @Override
                    public String monthNameAbbreviation(final int m) {
                        assertEquals(monthNumber, m, "month");
                        return monthName;
                    }
                },
                monthName);
    }

    @Test
    public void testMonthMonthMonthMonthDecember() {
        this.parseFormatAndCheckMMMM("2000-12-31T15:58:59.000", 12, "December!");
    }

    @Test
    public void testMonthMonthMonthMonthJanuary() {
        this.parseFormatAndCheckMMMM("1999-01-31T15:58:59.000", 1, "January!");
    }

    private void parseFormatAndCheckMMMM(final String dateTime, final int monthNumber, final String monthName) {
        this.parseFormatAndCheck("mmmm",
                dateTime,
                new TestSpreadsheetFormatterContext() {
                    @Override
                    public String monthName(final int m) {
                        assertEquals(monthNumber, m, "month");
                        return monthName;
                    }
                },
                monthName);
    }

    // day.............................................................................................

    @Test
    public void testDay31() {
        this.parseFormatAndCheck("d", this.text(), "31");
    }

    @Test
    public void testDay1() {
        this.parseFormatAndCheck("d", "1999-12-01T15:58:59.000", "1");
    }

    @Test
    public void testDayDay31() {
        this.parseFormatAndCheck("dd", this.text(), "31");
    }

    @Test
    public void testDayDay1() {
        this.parseFormatAndCheck("dd", "2000-12-01T15:58:59.000", "01");
    }

    @Test
    public void testDayDayDay31() {
        this.parseFormatAndCheckDDD("2000-12-31T15:58:59.000", 31, "Mon!");
    }

    @Test
    public void testDayDayDay1() {
        this.parseFormatAndCheckDDD("1999-12-01T15:58:59.000", 1, "Mon!");
    }

    private void parseFormatAndCheckDDD(final String dateTime, final int dayNumber, final String dayName) {
        this.parseFormatAndCheck("ddd",
                dateTime,
                new TestSpreadsheetFormatterContext() {
                    @Override
                    public String weekDayNameAbbreviation(final int d) {
                        assertEquals(dayNumber, d, "day");
                        return dayName;
                    }
                },
                dayName);
    }

    @Test
    public void testDayDayDayDay31() {
        this.parseFormatAndCheckDDDD("2000-12-31T15:58:59.000", 31, "Monday!");
    }

    @Test
    public void testDayDayDayDay1() {
        this.parseFormatAndCheckDDDD("1999-12-01T15:58:59.000", 1, "Monday!");
    }

    private void parseFormatAndCheckDDDD(final String date, final int dayNumber, final String dayName) {
        this.parseFormatAndCheck("dddd",
                date,
                new TestSpreadsheetFormatterContext() {
                    @Override
                    public String weekDayName(final int d) {
                        assertEquals(dayNumber, d, "day");
                        return dayName;
                    }
                },
                dayName);
    }

    // day month year.............................................................................................

    @Test
    public void testYearYearMonthDay() {
        this.parseFormatAndCheck("yymd", "1999-12-31T01:58:59.000", "991231");
    }

    @Test
    public void testMonthDayYearYear() {
        this.parseFormatAndCheck("mdyy", "1999-12-31T01:58:59.000", "123199");
    }

    // hour.............................................................................................

    @Test
    public void testHour12() {
        this.parseFormatAndCheck("h", "2000-06-29T12:58:59.000", "12");
    }

    @Test
    public void testHour1() {
        this.parseFormatAndCheck("h", "2000-06-29T01:58:59.000", "1");
    }

    @Test
    public void testHour15() {
        this.parseFormatAndCheck("h", "2000-06-01T15:58:59.000", "15");
    }

    @Test
    public void testHour1Ampm() {
        this.parseHourFormatAndCheck("hAM/PM", "2000-06-29T01:58:59.000", 1, "AM!", "1AM!");
    }

    @Test
    public void testHour12Ampm() {
        this.parseHourFormatAndCheck("hAM/PM", "2000-06-29T12:58:59.000", 12, "AM!", "12AM!");
    }

    @Test
    public void testHour15Ampm() {
        this.parseHourFormatAndCheck("hAM/PM", "2000-06-01T15:58:59.000", 15, "PM!", "3PM!");
    }

    @Test
    public void testHour23Ampm() {
        this.parseHourFormatAndCheck("hAM/PM", "2000-06-01T23:58:59.000", 23, "PM!", "11PM!");
    }

    @Test
    public void testHourHour1Ampm() {
        this.parseHourFormatAndCheck("hhAM/PM", "2000-06-29T01:58:59.000", 1, "AM!", "01AM!");
    }

    @Test
    public void testHourHour12Ampm() {
        this.parseHourFormatAndCheck("hhAM/PM", "2000-06-29T12:58:59.000", 12, "AM!", "12AM!");
    }

    @Test
    public void testHourHour15Ampm() {
        this.parseHourFormatAndCheck("hhAM/PM", "2000-06-01T15:58:59.000", 15, "PM!", "03PM!");
    }

    @Test
    public void testHourHour23Ampm() {
        this.parseHourFormatAndCheck("hhAM/PM", "2000-06-01T23:58:59.000", 23, "PM!", "11PM!");
    }

    @Test
    public void testHourHourHour1() {
        this.parseFormatAndCheck("hhh", "2000-06-29T01:58:59.000", "01");
    }

    @Test
    public void testHourHourHour15() {
        this.parseFormatAndCheck("hhh", "2000-06-01T15:58:59.000", "15");
    }

    @Test
    public void testHourHourHour1Ampm() {
        this.parseHourFormatAndCheck("hhhAM/PM", "2000-06-29T01:58:59.000", 1, "AM!", "01AM!");
    }

    @Test
    public void testHourHourHour12Ampm() {
        this.parseHourFormatAndCheck("hhhAM/PM", "2000-06-29T12:58:59.000", 12, "AM!", "12AM!");
    }

    // minute.............................................................................................

    @Test
    public void testHourMinute1() {
        this.parseFormatAndCheck("hm", "2000-06-29T12:01:59.000", "121");
    }

    @Test
    public void testHourMinute58() {
        this.parseFormatAndCheck("hm", "2000-06-30T12:58:59.000", "1258");
    }

    @Test
    public void testHourMinuteMinute1() {
        this.parseFormatAndCheck("hmm", "2000-06-29T12:01:59.000", "1201");
    }

    @Test
    public void testHourMinuteMinute58() {
        this.parseFormatAndCheck("hmm", "2000-06-30T12:58:59.000", "1258");
    }

    @Test
    public void testHourMinuteMinuteMinute1() {
        this.parseFormatAndCheck("hmmm", "2000-06-29T12:01:59.000", "1201");
    }

    @Test
    public void testHourMinuteMinuteMinute58() {
        this.parseFormatAndCheck("hmmm", "2000-06-30T12:58:59.000", "1258");
    }

    // second.............................................................................................

    @Test
    public void testSecond1() {
        this.parseFormatAndCheck("s", "2000-06-29T12:58:01.000", "1");
    }

    @Test
    public void testSecond59() {
        this.parseFormatAndCheck("s", "2000-06-30T12:58:59.000", "59");
    }

    @Test
    public void testSecondSecond1() {
        this.parseFormatAndCheck("ss", "2000-06-29T12:58:01.000", "01");
    }

    @Test
    public void testSecondSecond59() {
        this.parseFormatAndCheck("ss", "2000-06-30T12:58:59.000", "59");
    }

    @Test
    public void testSecondSecondSecond1() {
        this.parseFormatAndCheck("sss", "2000-06-29T12:58:01.000", "01");
    }

    @Test
    public void testSecondSecondSecond59() {
        this.parseFormatAndCheck("sss", "2000-06-30T12:58:59.000", "59");
    }

    // hour minute second.................................................................................

    @Test
    public void testHourHourMinuteMinuteSecondSecond125801() {
        this.parseHourFormatAndCheck("hhmmss", "2000-06-29T12:58:01.000", 12, "AM!", "125801");
    }

    @Test
    public void testHourHourMinuteMinuteSecondSecondAm125801() {
        this.parseHourFormatAndCheck("hhmmssAM/PM", "2000-06-29T12:58:01.000", 12, "AM!", "125801AM!");
    }

    @Test
    public void testHourHourMinuteMinuteSecondSecondDayDayMonthMonthYearYearYearYear() {
        this.parseHourFormatAndCheck("hhmmssAM/PMddmmyyyy", "2000-06-29T12:58:01.000", 12, "AM!", "125801AM!29062000");
    }

    @Test
    public void testHourHourMinuteMinuteSecondSecondDayDayMonthMonthYearYearYearYearAmPm() {
        this.parseHourFormatAndCheck("hhmmssAM/PMddmmyyyy", "2000-06-29T15:58:01.000", 15, "AM!", "035801AM!29062000");
    }

    private void parseHourFormatAndCheck(final String pattern,
                                         final String dateTime,
                                         final int hour,
                                         final String ampm,
                                         final String text) {
        this.parseFormatAndCheck(pattern,
                dateTime,
                new TestSpreadsheetFormatterContext() {
                    @Override
                    public String ampm(final int h) {
                        assertEquals(hour, h, "hour");
                        return ampm;
                    }
                },
                text);
    }
    // literals..........................................................................................................

    @Test
    public void testEscaped() {
        this.parseEscapedOrLiteralFormatAndCheck("\\A", "A");
    }

    @Test
    public void testCurrency() {
        this.parseEscapedOrLiteralFormatAndCheck("$");
    }

    @Test
    public void testMinus() {
        this.parseEscapedOrLiteralFormatAndCheck("-");
    }

    @Test
    public void testPlus() {
        this.parseEscapedOrLiteralFormatAndCheck("-");
    }

    @Test
    public void testSlash() {
        this.parseEscapedOrLiteralFormatAndCheck("/");
    }

    @Test
    public void testOpenParens() {
        this.parseEscapedOrLiteralFormatAndCheck("(");
    }

    @Test
    public void testCloseParens() {
        this.parseEscapedOrLiteralFormatAndCheck(")");
    }

    @Test
    public void testColon() {
        this.parseEscapedOrLiteralFormatAndCheck(":");
    }

    @Test
    public void testSpace() {
        this.parseEscapedOrLiteralFormatAndCheck(" ");
    }

    private void parseEscapedOrLiteralFormatAndCheck(final String pattern) {
        this.parseEscapedOrLiteralFormatAndCheck(pattern, pattern);
    }

    private void parseEscapedOrLiteralFormatAndCheck(final String pattern, final String text) {
        this.parseFormatAndCheck(pattern, this.text(), this.createContext(), text);
    }

    @Test
    public void testQuotedText() {
        this.parseFormatAndCheck("\"Hello\"", this.text(), "Hello");
    }

    // mixed.......................................................................................................

    @Test
    public void testDaySlashMonthSayYearSpaceHourColonMinuteColonSecond() {
        this.parseFormatAndCheck("d/m/yyyy h:m:s",
                "2000-12-31T15:58:59.000",
                "31/12/2000 15:58:59");
    }

    @Test
    public void testDaySlashMonthSayYearLiteralTHourColonMinuteColonSecond() {
        this.parseFormatAndCheck("d/m/yyyy\\Th:m:s",
                "2000-12-31T15:58:59.000",
                "31/12/2000T15:58:59");
    }

    @Test
    public void testDaySlashMonthSayYearLiteralTHourColonMinuteColonSecond2() {
        this.parseFormatAndCheck("d/m/yyyy\\Th:m:s",
                "2000-12-31T15:58:01.000",
                "31/12/2000T15:58:1");
    }

    @Test
    public void testDaySlashMonthSayYearLiteralTHourColonMinuteColonSecondSecond() {
        this.parseFormatAndCheck("d/m/yyyy\\Th:m:ss",
                "2000-12-31T15:58:59.000",
                "31/12/2000T15:58:59");
    }

    @Test
    public void testDaySlashMonthSayYearLiteralTHourColonMinuteColonSecondSecondRounding() {
        this.parseFormatAndCheck("d/m/yyyy\\Th:mm:ss",
                "2000-12-31T15:58:03.678",
                "31/12/2000T15:58:04");
    }

    @Test
    public void testDaySlashMonthSayYearLiteralTHourColonMinuteColonSecondMillis() {
        this.parseFormatAndCheck("d/m/yyyy\\Th:m:ss.0",
                "2000-12-31T15:58:04.100",
                "31/12/2000T15:58:04D1");
    }

    @Test
    public void testDaySlashMonthSayYearLiteralTHourColonMinuteColonSecondMillisRounding() {
        this.parseFormatAndCheck("d/m/yyyy\\Th:m:ss.0",
                "2000-12-31T15:58:04.167",
                "31/12/2000T15:58:04D2");
    }

    @Test
    public void testDaySlashMonthSayYearLiteralTHourColonMinuteColonSecondMillisRounding2() {
        this.parseFormatAndCheck("d/m/yyyy\\Th:m:ss.00",
                "2000-12-31T15:58:04.167",
                "31/12/2000T15:58:04D17");
    }

    // Date.............................................................................................................

    @Test
    public void testDate() {
        this.formatAndCheck(this.createFormatter("yyyy/mm/dd"),
                LocalDate.of(2000, 12, 31),
                new TestSpreadsheetFormatterContext() {

                    @Override
                    public boolean canConvert(final Object value,
                                              final Class<?> target) {
                        return Converters.localDateLocalDateTime().canConvert(value, target, ConverterContexts.fake());
                    }

                    @Override
                    public <T> T convert(final Object value, final Class<T> target) {
                        assertEquals(LocalDateTime.class, target, "target");
                        return Converters.localDateLocalDateTime().convert(value, target, ConverterContexts.fake());
                    }
                },
                "2000/12/31");
    }

    // Time.............................................................................................................

    @Test
    public void testTime() {
        this.formatAndCheck(this.createFormatter("hh/mm/ss"),
                LocalTime.of(12, 58, 59),
                new TestSpreadsheetFormatterContext() {

                    @Override
                    public boolean canConvert(final Object value,
                                              final Class<?> target) {
                        return value instanceof LocalTime && target == LocalDateTime.class;
                    }

                    @Override
                    public <T> T convert(final Object value, final Class<T> target) {
                        assertEquals(LocalDateTime.class, target, "target");
                        return target.cast(LocalDateTime.of(LocalDate.of(2000, 10, 31), LocalTime.class.cast(value)));
                    }
                },
                "12/58/59");
    }

    // helpers..........................................................................................................

    private void parseFormatAndCheck(final String pattern,
                                     final String value,
                                     final String text) {
        this.parseFormatAndCheck(pattern, value, this.createContext(), text);
    }

    private void parseFormatAndCheck(final String pattern,
                                     final String value,
                                     final SpreadsheetFormatterContext context,
                                     final String text) {
        this.parseFormatAndCheck0(pattern, value, context, SpreadsheetText.with(SpreadsheetText.WITHOUT_COLOR, text));
    }

    private void parseFormatAndCheck(final String pattern,
                                      final String value,
                                      final SpreadsheetFormatterContext context,
                                      final SpreadsheetText text) {
        this.formatAndCheck(this.createFormatter(pattern),
                this.parseLocalDateTime(value),
                context,
                text);
    }

    private void parseFormatAndCheck0(final String pattern,
                                      final String value,
                                      final SpreadsheetFormatterContext context,
                                      final SpreadsheetText text) {
        this.formatAndCheck(this.createFormatter(pattern),
                this.parseLocalDateTime(value),
                context,
                text);
    }

    @Override
    String pattern() {
        return "YYYYMMDDHHMMSS";
    }

    @Override
    Parser<SpreadsheetFormatParserContext> parser() {
        return SpreadsheetFormatParsers.dateTime();
    }

    //toString .......................................................................................................

    @Test
    public final void testToString() {
        this.toStringAndCheck(this.createFormatter(), this.pattern());
    }

    @Override
    LocalDateTimeSpreadsheetFormatter createFormatter0(final SpreadsheetFormatDateTimeParserToken token) {
        return LocalDateTimeSpreadsheetFormatter.with(token);
    }

    @Override
    public LocalDateTime value() {
        return this.parseLocalDateTime(this.text());
    }

    private String text() {
        return "2000-12-31T15:58:59.000";
    }

    @Override
    public SpreadsheetFormatterContext createContext() {
        return new TestSpreadsheetFormatterContext();
    }

    class TestSpreadsheetFormatterContext extends FakeSpreadsheetFormatterContext {

        @Override
        public char decimalSeparator() {
            return 'D';
        }

        @Override
        public boolean canConvert(final Object value,
                                  final Class<?> target) {
            return value instanceof LocalDateTime && LocalDateTime.class == target;
        }

        @Override
        public <T> T convert(final Object value, final Class<T> target) {
            assertEquals(LocalDateTime.class, target, "target");
            return target.cast(value);
        }
    }

    private LocalDateTime parseLocalDateTime(final String value) {
        return LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.ENGLISH));
    }

    @Override
    public Class<LocalDateTimeSpreadsheetFormatter> type() {
        return LocalDateTimeSpreadsheetFormatter.class;
    }
}

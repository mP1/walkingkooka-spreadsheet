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
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateTimeParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.text.cursor.parser.Parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class LocalDateTimeSpreadsheetTextFormatterTest extends SpreadsheetTextFormatter3TestCase<
        LocalDateTimeSpreadsheetTextFormatter,
        SpreadsheetFormatDateTimeParserToken> {

    @Override
    public void testCanFormatFalse() {
        // LocalDateTimeSpreadsheetTextFormatter says it can format anything. It converts all values to LocalDateTime before formatting.
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
        this.parseFormatAndCheck("y", "1999-12-31T15:58:59", "99");
    }

    @Test
    public void testYearYear2000() {
        this.parseFormatAndCheck("yy", this.text(), "00");
    }

    @Test
    public void testYearYear1999() {
        this.parseFormatAndCheck("yy", "1999-12-31T15:58:59", "99");
    }

    @Test
    public void testYearYearYear000() {
        this.parseFormatAndCheck("yyy", this.text(), "2000");
    }

    @Test
    public void testYearYearYear1999() {
        this.parseFormatAndCheck("yyy", "1999-12-31T15:58:59", "1999");
    }

    @Test
    public void testYearYearYearYear2000() {
        this.parseFormatAndCheck("yyyy", this.text(), "2000");
    }

    @Test
    public void testYearYearYearYear1999() {
        this.parseFormatAndCheck("yyyy", "1999-12-31T15:58:59", "1999");
    }

    @Test
    public void testYearYearYearYear789() {
        this.parseFormatAndCheck("yyyy", "0789-12-31T15:58:59", "789");
    }

    // month.............................................................................................

    @Test
    public void testMonthDecember() {
        this.parseFormatAndCheck("m", this.text(), "12");
    }

    @Test
    public void testMonthJanuary() {
        this.parseFormatAndCheck("m", "1999-01-31T15:58:59", "1");
    }

    @Test
    public void testMonthMonthDecember() {
        this.parseFormatAndCheck("mm", this.text(), "12");
    }

    @Test
    public void testMonthMonthJanuary() {
        this.parseFormatAndCheck("mm", "1999-01-31T15:58:59", "01");
    }

    @Test
    public void testMonthMonthMonthDecember() {
        this.parseFormatAndCheckMMM("2000-12-31T15:58:59", 12, "Dec!");
    }

    @Test
    public void testMonthMonthMonthJanuary() {
        this.parseFormatAndCheckMMM("1999-01-31T15:58:59", 1, "Jan!");
    }

    private void parseFormatAndCheckMMM(final String date, final int monthNumber, final String monthName) {
        this.parseFormatAndCheck("mmm",
                date,
                new TestSpreadsheetTextFormatContext() {
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
        this.parseFormatAndCheckMMMM("2000-12-31T15:58:59", 12, "December!");
    }

    @Test
    public void testMonthMonthMonthMonthJanuary() {
        this.parseFormatAndCheckMMMM("1999-01-31T15:58:59", 1, "January!");
    }

    private void parseFormatAndCheckMMMM(final String dateTime, final int monthNumber, final String monthName) {
        this.parseFormatAndCheck("mmmm",
                dateTime,
                new TestSpreadsheetTextFormatContext() {
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
        this.parseFormatAndCheck("d", "1999-12-01T15:58:59", "1");
    }

    @Test
    public void testDayDay31() {
        this.parseFormatAndCheck("dd", this.text(), "31");
    }

    @Test
    public void testDayDay1() {
        this.parseFormatAndCheck("dd", "2000-12-01T15:58:59", "01");
    }

    @Test
    public void testDayDayDay31() {
        this.parseFormatAndCheckDDD("2000-12-31T15:58:59", 31, "Mon!");
    }

    @Test
    public void testDayDayDay1() {
        this.parseFormatAndCheckDDD("1999-12-01T15:58:59", 1, "Mon!");
    }

    private void parseFormatAndCheckDDD(final String dateTime, final int dayNumber, final String dayName) {
        this.parseFormatAndCheck("ddd",
                dateTime,
                new TestSpreadsheetTextFormatContext() {
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
        this.parseFormatAndCheckDDDD("2000-12-31T15:58:59", 31, "Monday!");
    }

    @Test
    public void testDayDayDayDay1() {
        this.parseFormatAndCheckDDDD("1999-12-01T15:58:59", 1, "Monday!");
    }

    private void parseFormatAndCheckDDDD(final String date, final int dayNumber, final String dayName) {
        this.parseFormatAndCheck("dddd",
                date,
                new TestSpreadsheetTextFormatContext() {
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
        this.parseFormatAndCheck("yymd", "1999-12-31T01:58:59", "991231");
    }

    @Test
    public void testMonthDayYearYear() {
        this.parseFormatAndCheck("mdyy", "1999-12-31T01:58:59", "123199");
    }

    // hour.............................................................................................

    @Test
    public void testHour12() {
        this.parseFormatAndCheck("h", "2000-06-29T12:58:59", "12");
    }

    @Test
    public void testHour1() {
        this.parseFormatAndCheck("h", "2000-06-29T01:58:59", "1");
    }

    @Test
    public void testHour15() {
        this.parseFormatAndCheck("h", "2000-06-01T15:58:59", "15");
    }

    @Test
    public void testHour1Ampm() {
        this.parseHourFormatAndCheck("hAM/PM", "2000-06-29T01:58:59", 1, "AM!", "1AM!");
    }

    @Test
    public void testHour12Ampm() {
        this.parseHourFormatAndCheck("hAM/PM", "2000-06-29T12:58:59", 12, "AM!", "12AM!");
    }

    @Test
    public void testHour15Ampm() {
        this.parseHourFormatAndCheck("hAM/PM", "2000-06-01T15:58:59", 15, "PM!", "3PM!");
    }

    @Test
    public void testHour23Ampm() {
        this.parseHourFormatAndCheck("hAM/PM", "2000-06-01T23:58:59", 23, "PM!", "11PM!");
    }

    @Test
    public void testHourHour1Ampm() {
        this.parseHourFormatAndCheck("hhAM/PM", "2000-06-29T01:58:59", 1, "AM!", "01AM!");
    }

    @Test
    public void testHourHour12Ampm() {
        this.parseHourFormatAndCheck("hhAM/PM", "2000-06-29T12:58:59", 12, "AM!", "12AM!");
    }

    @Test
    public void testHourHour15Ampm() {
        this.parseHourFormatAndCheck("hhAM/PM", "2000-06-01T15:58:59", 15, "PM!", "03PM!");
    }

    @Test
    public void testHourHour23Ampm() {
        this.parseHourFormatAndCheck("hhAM/PM", "2000-06-01T23:58:59", 23, "PM!", "11PM!");
    }

    @Test
    public void testHourHourHour1() {
        this.parseFormatAndCheck("hhh", "2000-06-29T01:58:59", "01");
    }

    @Test
    public void testHourHourHour15() {
        this.parseFormatAndCheck("hhh", "2000-06-01T15:58:59", "15");
    }

    @Test
    public void testHourHourHour1Ampm() {
        this.parseHourFormatAndCheck("hhhAM/PM", "2000-06-29T01:58:59", 1, "AM!", "01AM!");
    }

    @Test
    public void testHourHourHour12Ampm() {
        this.parseHourFormatAndCheck("hhhAM/PM", "2000-06-29T12:58:59", 12, "AM!", "12AM!");
    }

    // minute.............................................................................................

    @Test
    public void testHourMinute1() {
        this.parseFormatAndCheck("hm", "2000-06-29T12:01:59", "121");
    }

    @Test
    public void testHourMinute58() {
        this.parseFormatAndCheck("hm", "2000-06-30T12:58:59", "1258");
    }

    @Test
    public void testHourMinuteMinute1() {
        this.parseFormatAndCheck("hmm", "2000-06-29T12:01:59", "1201");
    }

    @Test
    public void testHourMinuteMinute58() {
        this.parseFormatAndCheck("hmm", "2000-06-30T12:58:59", "1258");
    }

    @Test
    public void testHourMinuteMinuteMinute1() {
        this.parseFormatAndCheck("hmmm", "2000-06-29T12:01:59", "1201");
    }

    @Test
    public void testHourMinuteMinuteMinute58() {
        this.parseFormatAndCheck("hmmm", "2000-06-30T12:58:59", "1258");
    }

    // second.............................................................................................

    @Test
    public void testSecond1() {
        this.parseFormatAndCheck("s", "2000-06-29T12:58:01", "1");
    }

    @Test
    public void testSecond59() {
        this.parseFormatAndCheck("s", "2000-06-30T12:58:59", "59");
    }

    @Test
    public void testSecondSecond1() {
        this.parseFormatAndCheck("ss", "2000-06-29T12:58:01", "01");
    }

    @Test
    public void testSecondSecond59() {
        this.parseFormatAndCheck("ss", "2000-06-30T12:58:59", "59");
    }

    @Test
    public void testSecondSecondSecond1() {
        this.parseFormatAndCheck("sss", "2000-06-29T12:58:01", "01");
    }

    @Test
    public void testSecondSecondSecond59() {
        this.parseFormatAndCheck("sss", "2000-06-30T12:58:59", "59");
    }

    // hour minute second.................................................................................

    @Test
    public void testHourHourMinuteMinuteSecondSecond125801() {
        this.parseHourFormatAndCheck("hhmmss", "2000-06-29T12:58:01", 12, "AM!", "125801");
    }

    @Test
    public void testHourHourMinuteMinuteSecondSecondAm125801() {
        this.parseHourFormatAndCheck("hhmmssAM/PM", "2000-06-29T12:58:01", 12, "AM!", "125801AM!");
    }

    @Test
    public void testHourHourMinuteMinuteSecondSecondDayDayMonthMonthYearYearYearYear() {
        this.parseHourFormatAndCheck("hhmmssAM/PMddmmyyyy", "2000-06-29T12:58:01", 12, "AM!", "125801AM!29062000");
    }

    @Test
    public void testHourHourMinuteMinuteSecondSecondDayDayMonthMonthYearYearYearYearAmPm() {
        this.parseHourFormatAndCheck("hhmmssAM/PMddmmyyyy", "2000-06-29T15:58:01", 15, "AM!", "035801AM!29062000");
    }

    private void parseHourFormatAndCheck(final String pattern,
                                         final String dateTime,
                                         final int hour,
                                         final String ampm,
                                         final String text) {
        this.parseFormatAndCheck(pattern,
                dateTime,
                new TestSpreadsheetTextFormatContext() {
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
                "2000-12-31T15:58:59",
                "31/12/2000 15:58:59");
    }

    @Test
    public void testDaySlashMonthSayYearLiteralTHourColonMinuteColonSecond() {
        this.parseFormatAndCheck("d/m/yyyy\\Th:m:s",
                "2000-12-31T15:58:59",
                "31/12/2000T15:58:59");
    }

    // Date.............................................................................................................

    @Test
    public void testDate() {
        this.formatAndCheck(this.createFormatter("yyyy/mm/dd"),
                LocalDate.of(2000, 12, 31),
                new TestSpreadsheetTextFormatContext() {
                    @Override
                    public <T> T convert(final Object value, final Class<T> target) {
                        assertEquals(LocalDateTime.class, target, "target");
                        return target.cast(LocalDateTime.of(LocalDate.class.cast(value), LocalTime.MIDNIGHT));
                    }
                },
                "2000/12/31");
    }

    // Time.............................................................................................................

    @Test
    public void testTime() {
        this.formatAndCheck(this.createFormatter("hh/mm/ss"),
                LocalTime.of(12, 58, 59),
                new TestSpreadsheetTextFormatContext() {
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
                                     final SpreadsheetTextFormatContext context,
                                     final String text) {
        this.parseFormatAndCheck0(pattern, value, context, SpreadsheetFormattedText.with(SpreadsheetFormattedText.WITHOUT_COLOR, text));
    }

    private void parseFormatAndCheck0(final String pattern,
                                      final String value,
                                      final SpreadsheetTextFormatContext context,
                                      final SpreadsheetFormattedText text) {
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
    LocalDateTimeSpreadsheetTextFormatter createFormatter0(final SpreadsheetFormatDateTimeParserToken token) {
        return LocalDateTimeSpreadsheetTextFormatter.with(token);
    }

    @Override
    public LocalDateTime value() {
        return this.parseLocalDateTime(this.text());
    }

    private String text() {
        return "2000-12-31T15:58:59";
    }

    @Override
    public SpreadsheetTextFormatContext createContext() {
        return new TestSpreadsheetTextFormatContext();
    }

    class TestSpreadsheetTextFormatContext extends FakeSpreadsheetTextFormatContext {
        @Override
        public <T> T convert(final Object value, final Class<T> target) {
            assertEquals(LocalDateTime.class, target, "target");
            return target.cast(value);
        }
    }

    private LocalDateTime parseLocalDateTime(final String value) {
        return LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    @Override
    public Class<LocalDateTimeSpreadsheetTextFormatter> type() {
        return LocalDateTimeSpreadsheetTextFormatter.class;
    }
}

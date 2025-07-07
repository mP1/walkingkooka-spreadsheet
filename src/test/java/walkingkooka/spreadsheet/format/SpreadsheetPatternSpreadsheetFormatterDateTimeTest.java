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
import walkingkooka.Either;
import walkingkooka.collect.list.Lists;
import walkingkooka.color.Color;
import walkingkooka.spreadsheet.SpreadsheetColors;
import walkingkooka.spreadsheet.format.parser.DateTimeSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.text.cursor.parser.Parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetPatternSpreadsheetFormatterDateTimeTest extends SpreadsheetPatternSpreadsheetFormatterTestCase<
    SpreadsheetPatternSpreadsheetFormatterDateTime,
    DateTimeSpreadsheetFormatParserToken> {

    private final static Color RED = Color.parse("#FF0000");

    // with.............................................................................................................

    @Test
    public void testWithNullTypeTesterFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetPatternSpreadsheetFormatterDateTime.with(
                this.parsePatternOrFail(
                    this.pattern()
                ),
                null
            )
        );
    }

    // tests.............................................................................................................

    @Test
    public void testFormatNull() {
        this.formatAndCheck(
            this.createFormatter("dd/mm/yyyy"),
            Optional.empty(), // value,
            Optional.empty() // expected
        );
    }

    @Test
    public void testFormatDateFails() {
        this.formatAndCheck(
            this.createFormatter("dd/mm/yyyy"),
            LocalDate.of(
                2000,
                12,
                31
            )
        );
    }

    @Test
    public void testFormatTimeFails() {
        this.formatAndCheck(
            this.createFormatter("hh:mm:ss"),
            LocalTime.of(
                12,
                58,
                59
            )
        );
    }

    // year.............................................................................................................

    @Test
    public void testFormatYear2000() {
        this.parseFormatAndCheck("y", this.text(), "00");
    }

    @Test
    public void testFormatYear1999() {
        this.parseFormatAndCheck(
            "y",
            "1999-12-31T15:58:59.000",
            "99"
        );
    }

    @Test
    public void testFormatYearYear2000() {
        this.parseFormatAndCheck(
            "yy",
            this.text(),
            "00"
        );
    }

    @Test
    public void testFormatYearYear1999() {
        this.parseFormatAndCheck(
            "yy",
            "1999-12-31T15:58:59.000",
            "99"
        );
    }

    @Test
    public void testFormatYearYearYear000() {
        this.parseFormatAndCheck(
            "yyy",
            this.text(),
            "2000"
        );
    }

    @Test
    public void testFormatYearYearYear1999() {
        this.parseFormatAndCheck(
            "yyy",
            "1999-12-31T15:58:59.000",
            "1999"
        );
    }

    @Test
    public void testFormatYearYearYearYear2000() {
        this.parseFormatAndCheck(
            "yyyy",
            this.text(),
            "2000"
        );
    }

    @Test
    public void testFormatYearYearYearYear1999() {
        this.parseFormatAndCheck(
            "yyyy",
            "1999-12-31T15:58:59.000",
            "1999"
        );
    }

    @Test
    public void testFormatYearYearYearYear789() {
        this.parseFormatAndCheck(
            "yyyy",
            "0789-12-31T15:58:59.000",
            "789"
        );
    }

    // month............................................................................................................

    @Test
    public void testFormatMonthDecember() {
        this.parseFormatAndCheck(
            "m",
            this.text(),
            "12"
        );
    }

    @Test
    public void testFormatMonthJanuary() {
        this.parseFormatAndCheck(
            "m",
            "1999-01-31T15:58:59.000",
            "1"
        );
    }

    @Test
    public void testFormatMonthMonthDecember() {
        this.parseFormatAndCheck(
            "mm",
            this.text(),
            "12"
        );
    }

    @Test
    public void testFormatMonthMonthJanuary() {
        this.parseFormatAndCheck(
            "mm",
            "1999-01-31T15:58:59.000",
            "01"
        );
    }

    @Test
    public void testFormatMonthMonthMonthDecember() {
        this.parseFormatAndCheckMMM(
            "2000-12-31T15:58:59.000",
            11,
            "Dec!"
        );
    }

    @Test
    public void testFormatMonthMonthMonthJanuary() {
        this.parseFormatAndCheckMMM(
            "1999-01-31T15:58:59.000",
            0,
            "Jan!"
        );
    }

    private void parseFormatAndCheckMMM(final String date,
                                        final int monthNumber,
                                        final String monthName) {
        this.parseFormatAndCheck(
            "mmm",
            date,
            new TestSpreadsheetFormatterContext() {
                @Override
                public String monthNameAbbreviation(final int m) {
                    checkEquals(monthNumber, m, "month");
                    return monthName;
                }
            },
            monthName
        );
    }

    @Test
    public void testFormatMonthMonthMonthMonthDecember() {
        this.parseFormatAndCheckMMMM(
            "2000-12-31T15:58:59.000",
            11,
            "December!"
        );
    }

    @Test
    public void testFormatMonthMonthMonthMonthJanuary() {
        this.parseFormatAndCheckMMMM(
            "1999-01-31T15:58:59.000",
            0, "" +
                "January!"
        );
    }

    private void parseFormatAndCheckMMMM(final String dateTime,
                                         final int monthNumber,
                                         final String monthName) {
        this.parseFormatAndCheck(
            "mmmm",
            dateTime,
            new TestSpreadsheetFormatterContext() {
                @Override
                public String monthName(final int m) {
                    checkEquals(monthNumber, m, "month");
                    return monthName;
                }
            },
            monthName
        );
    }

    // day..............................................................................................................

    @Test
    public void testFormatDay31() {
        this.parseFormatAndCheck(
            "d",
            this.text(),
            "31"
        );
    }

    @Test
    public void testFormatDay1() {
        this.parseFormatAndCheck(
            "d",
            "1999-12-01T15:58:59.000",
            "1"
        );
    }

    @Test
    public void testFormatDayDay31() {
        this.parseFormatAndCheck(
            "dd",
            this.text(),
            "31"
        );
    }

    @Test
    public void testFormatDayDay1() {
        this.parseFormatAndCheck(
            "dd",
            "2000-12-01T15:58:59.000",
            "01"
        );
    }

    @Test
    public void testFormatDayDayDay31() {
        this.parseFormatAndCheckDDD(
            "2000-12-31T15:58:59.000",
            0,
            "Mon!"
        ); // dayOfWeek name
    }

    @Test
    public void testFormatDayDayDay1() {
        this.parseFormatAndCheckDDD(
            "1999-12-01T15:58:59.000",
            3,
            "Mon!"
        ); // dayOfWeek name
    }

    private void parseFormatAndCheckDDD(final String dateTime,
                                        final int dayNumber,
                                        final String dayName) {
        this.parseFormatAndCheck(
            "ddd",
            dateTime,
            new TestSpreadsheetFormatterContext() {
                @Override
                public String weekDayNameAbbreviation(final int d) {
                    checkEquals(dayNumber, d, "day");
                    return dayName;
                }
            },
            dayName
        );
    }

    @Test
    public void testFormatDayDayDayDay31() {
        this.parseFormatAndCheckDDDD(
            "2000-12-31T15:58:59.000",
            0,
            "Monday!"
        ); // dayOfWeek name
    }

    @Test
    public void testFormatDayDayDayDay1() {
        this.parseFormatAndCheckDDDD(
            "1999-12-01T15:58:59.000",
            3,
            "Monday!"
        ); // dayOfWeek name
    }

    private void parseFormatAndCheckDDDD(final String date,
                                         final int dayNumber,
                                         final String dayName) {
        this.parseFormatAndCheck(
            "dddd",
            date,
            new TestSpreadsheetFormatterContext() {
                @Override
                public String weekDayName(final int d) {
                    checkEquals(dayNumber, d, "day");
                    return dayName;
                }
            },
            dayName
        );
    }

    // day month year..................................................................................................

    @Test
    public void testFormatYearYearMonthDay() {
        this.parseFormatAndCheck(
            "yymd",
            "1999-12-31T01:58:59.000",
            "991231"
        );
    }

    @Test
    public void testFormatMonthDayYearYear() {
        this.parseFormatAndCheck(
            "mdyy",
            "1999-12-31T01:58:59.000",
            "123199"
        );
    }

    // hour.............................................................................................................

    @Test
    public void testFormatHour12() {
        this.parseFormatAndCheck(
            "h",
            "2000-06-29T12:58:59.000",
            "12"
        );
    }

    @Test
    public void testFormatHour1() {
        this.parseFormatAndCheck(
            "h",
            "2000-06-29T01:58:59.000",
            "1"
        );
    }

    @Test
    public void testFormatHour15() {
        this.parseFormatAndCheck(
            "h",
            "2000-06-01T15:58:59.000",
            "15"
        );
    }

    @Test
    public void testFormatHour1Ampm() {
        this.parseHourFormatAndCheck(
            "hAM/PM",
            "2000-06-29T01:58:59.000",
            1,
            "AM!",
            "1AM!"
        );
    }

    @Test
    public void testFormatHour12Ampm() {
        this.parseHourFormatAndCheck(
            "hAM/PM",
            "2000-06-29T12:58:59.000",
            12,
            "AM!",
            "12AM!"
        );
    }

    @Test
    public void testFormatHour15Ampm() {
        this.parseHourFormatAndCheck(
            "hAM/PM",
            "2000-06-01T15:58:59.000",
            15,
            "PM!",
            "3PM!"
        );
    }

    @Test
    public void testFormatHour23Ampm() {
        this.parseHourFormatAndCheck(
            "hAM/PM",
            "2000-06-01T23:58:59.000",
            23,
            "PM!",
            "11PM!"
        );
    }

    @Test
    public void testFormatHourHour1Ampm() {
        this.parseHourFormatAndCheck(
            "hhAM/PM",
            "2000-06-29T01:58:59.000",
            1,
            "AM!",
            "01AM!"
        );
    }

    @Test
    public void testFormatHourHour12Ampm() {
        this.parseHourFormatAndCheck(
            "hhAM/PM",
            "2000-06-29T12:58:59.000",
            12,
            "AM!",
            "12AM!"
        );
    }

    @Test
    public void testFormatHourHour15Ampm() {
        this.parseHourFormatAndCheck(
            "hhAM/PM",
            "2000-06-01T15:58:59.000",
            15,
            "PM!",
            "03PM!"
        );
    }

    @Test
    public void testFormatHourHour23Ampm() {
        this.parseHourFormatAndCheck(
            "hhAM/PM",
            "2000-06-01T23:58:59.000",
            23,
            "PM!",
            "11PM!"
        );
    }

    @Test
    public void testFormatHourHourHour1() {
        this.parseFormatAndCheck(
            "hhh",
            "2000-06-29T01:58:59.000",
            "01"
        );
    }

    @Test
    public void testFormatHourHourHour15() {
        this.parseFormatAndCheck(
            "hhh",
            "2000-06-01T15:58:59.000",
            "15"
        );
    }

    @Test
    public void testFormatHourHourHour1Ampm() {
        this.parseHourFormatAndCheck(
            "hhhAM/PM",
            "2000-06-29T01:58:59.000",
            1,
            "AM!",
            "01AM!"
        );
    }

    @Test
    public void testFormatHourHourHour12Ampm() {
        this.parseHourFormatAndCheck(
            "hhhAM/PM",
            "2000-06-29T12:58:59.000",
            12,
            "AM!",
            "12AM!"
        );
    }

    // minute...........................................................................................................

    @Test
    public void testFormatHourMinute1() {
        this.parseFormatAndCheck(
            "hm",
            "2000-06-29T12:01:59.000",
            "121"
        );
    }

    @Test
    public void testFormatHourMinute58() {
        this.parseFormatAndCheck(
            "hm",
            "2000-06-30T12:58:59.000",
            "1258"
        );
    }

    @Test
    public void testFormatHourMinuteMinute1() {
        this.parseFormatAndCheck(
            "hmm",
            "2000-06-29T12:01:59.000",
            "1201"
        );
    }

    @Test
    public void testFormatHourMinuteMinute58() {
        this.parseFormatAndCheck(
            "hmm",
            "2000-06-30T12:58:59.000",
            "1258"
        );
    }

    @Test
    public void testFormatHourMinuteMinuteMinute1() {
        this.parseFormatAndCheck(
            "hmmm",
            "2000-06-29T12:01:59.000",
            "1201"
        );
    }

    @Test
    public void testFormatHourMinuteMinuteMinute58() {
        this.parseFormatAndCheck(
            "hmmm",
            "2000-06-30T12:58:59.000",
            "1258"
        );
    }

    // second...........................................................................................................

    @Test
    public void testFormatSecond1() {
        this.parseFormatAndCheck(
            "s",
            "2000-06-29T12:58:01.000",
            "1"
        );
    }

    @Test
    public void testFormatSecond59() {
        this.parseFormatAndCheck(
            "s",
            "2000-06-30T12:58:59.000",
            "59"
        );
    }

    @Test
    public void testFormatSecondSecond1() {
        this.parseFormatAndCheck(
            "ss",
            "2000-06-29T12:58:01.000",
            "01"
        );
    }

    @Test
    public void testFormatSecondSecond59() {
        this.parseFormatAndCheck(
            "ss",
            "2000-06-30T12:58:59.000",
            "59"
        );
    }

    @Test
    public void testFormatSecondSecondSecond1() {
        this.parseFormatAndCheck(
            "sss",
            "2000-06-29T12:58:01.000",
            "01"
        );
    }

    @Test
    public void testFormatSecondSecondSecond59() {
        this.parseFormatAndCheck(
            "sss",
            "2000-06-30T12:58:59.000",
            "59"
        );
    }

    // milli............................................................................................................

    @Test
    public void testFormatSecondMillis1() {
        this.parseFormatAndCheck2(
            "s.0",
            "59D1"
        );
    }

    @Test
    public void testFormatSecondMillis2() {
        this.parseFormatAndCheck2(
            "s.00",
            "59D12"
        );
    }

    @Test
    public void testFormatSecondMillis3() {
        this.parseFormatAndCheck2(
            "s.000",
            "59D123"
        );
    }

    @Test
    public void testFormatSecondMillis4() {
        this.parseFormatAndCheck2(
            "s.0000",
            "59D1235"
        );
    }

    @Test
    public void testFormatSecondMillis5() {
        this.parseFormatAndCheck2(
            "s.00000",
            "59D12346"
        );
    }

    @Test
    public void testFormatSecondMillis6() {
        this.parseFormatAndCheck2(
            "s.000000",
            "59D123457"
        );
    }

    @Test
    public void testFormatSecondMillis7() {
        this.parseFormatAndCheck2(
            "s.0000000",
            "59D1234568"
        );
    }

    @Test
    public void testFormatSecondMillis8() {
        this.parseFormatAndCheck2(
            "s.00000000",
            "59D12345679"
        );
    }

    @Test
    public void testFormatSecondMillis9() {
        this.parseFormatAndCheck2(
            "s.000000000",
            "59D123456789"
        );
    }

    private void parseFormatAndCheck2(final String pattern,
                                      final String text) {
        this.parseFormatAndCheck(
            pattern,
            LocalDateTime.of(
                2000,
                12,
                31,
                12,
                58,
                59,
                123456789
            ),
            text
        );
    }

    // hour minute second...............................................................................................

    @Test
    public void testFormatHourHourMinuteMinuteSecondSecond125801() {
        this.parseHourFormatAndCheck(
            "hhmmss",
            "2000-06-29T12:58:01.000",
            12,
            "AM!",
            "125801"
        );
    }

    @Test
    public void testFormatHourHourMinuteMinuteSecondSecondAm125801() {
        this.parseHourFormatAndCheck(
            "hhmmssAM/PM",
            "2000-06-29T12:58:01.000",
            12,
            "AM!",
            "125801AM!"
        );
    }

    @Test
    public void testFormatHourHourMinuteMinuteSecondSecondDayDayMonthMonthYearYearYearYear() {
        this.parseHourFormatAndCheck(
            "hhmmssAM/PMddmmyyyy",
            "2000-06-29T12:58:01.000",
            12,
            "AM!",
            "125801AM!29062000"
        );
    }

    @Test
    public void testFormatHourHourMinuteMinuteSecondSecondDayDayMonthMonthYearYearYearYearAmPm() {
        this.parseHourFormatAndCheck(
            "hhmmssAM/PMddmmyyyy",
            "2000-06-29T15:58:01.000",
            15,
            "AM!",
            "035801AM!29062000"
        );
    }

    private void parseHourFormatAndCheck(final String pattern,
                                         final String dateTime,
                                         final int hour,
                                         final String ampm,
                                         final String text) {
        this.parseFormatAndCheck(
            pattern,
            dateTime,
            new TestSpreadsheetFormatterContext() {
                @Override
                public String ampm(final int h) {
                    checkEquals(hour, h, "hour");
                    return ampm;
                }
            },
            text
        );
    }
    // literals..........................................................................................................

    @Test
    public void testFormatEscaped() {
        this.parseEscapedOrLiteralFormatAndCheck(
            "\\A",
            "A"
        );
    }

    @Test
    public void testFormatCurrency() {
        this.parseEscapedOrLiteralFormatAndCheck("$");
    }

    @Test
    public void testFormatMinus() {
        this.parseEscapedOrLiteralFormatAndCheck("-");
    }

    @Test
    public void testFormatPlus() {
        this.parseEscapedOrLiteralFormatAndCheck("-");
    }

    @Test
    public void testFormatSlash() {
        this.parseEscapedOrLiteralFormatAndCheck("/");
    }

    @Test
    public void testFormatOpenParens() {
        this.parseEscapedOrLiteralFormatAndCheck("(");
    }

    @Test
    public void testFormatCloseParens() {
        this.parseEscapedOrLiteralFormatAndCheck(")");
    }

    @Test
    public void testFormatColon() {
        this.parseEscapedOrLiteralFormatAndCheck(":");
    }

    @Test
    public void testFormatSpace() {
        this.parseEscapedOrLiteralFormatAndCheck(" ");
    }

    private void parseEscapedOrLiteralFormatAndCheck(final String pattern) {
        this.parseEscapedOrLiteralFormatAndCheck(
            pattern,
            pattern
        );
    }

    private void parseEscapedOrLiteralFormatAndCheck(final String pattern,
                                                     final String text) {
        this.parseFormatAndCheck(
            pattern,
            this.text(),
            this.createContext(),
            text
        );
    }

    @Test
    public void testFormatQuotedText() {
        this.parseFormatAndCheck(
            "\"Hello\"",
            this.text(),
            "Hello"
        );
    }

    // mixed............................................................................................................

    @Test
    public void testFormatDaySlashMonthSayYearSpaceHourColonMinuteColonSecond() {
        this.parseFormatAndCheck(
            "d/m/yyyy h:m:s",
            "2000-12-31T15:58:59.000",
            "31/12/2000 15:58:59"
        );
    }

    @Test
    public void testFormatDaySlashMonthSayYearLiteralTHourColonMinuteColonSecond() {
        this.parseFormatAndCheck(
            "d/m/yyyy\\Th:m:s",
            "2000-12-31T15:58:59.000",
            "31/12/2000T15:58:59"
        );
    }

    @Test
    public void testFormatDaySlashMonthSayYearLiteralTHourColonMinuteColonSecond2() {
        this.parseFormatAndCheck(
            "d/m/yyyy\\Th:m:s",
            "2000-12-31T15:58:01.000",
            "31/12/2000T15:58:1"
        );
    }

    @Test
    public void testFormatDaySlashMonthSayYearLiteralTHourColonMinuteColonSecondSecond() {
        this.parseFormatAndCheck(
            "d/m/yyyy\\Th:m:ss",
            "2000-12-31T15:58:59.000",
            "31/12/2000T15:58:59"
        );
    }

    @Test
    public void testFormatDaySlashMonthSayYearLiteralTHourColonMinuteColonSecondSecondRounding() {
        this.parseFormatAndCheck(
            "d/m/yyyy\\Th:mm:ss",
            "2000-12-31T15:58:03.678",
            "31/12/2000T15:58:04"
        );
    }

    @Test
    public void testFormatDaySlashMonthSayYearLiteralTHourColonMinuteColonSecondMillis() {
        this.parseFormatAndCheck(
            "d/m/yyyy\\Th:m:ss.0",
            "2000-12-31T15:58:04.100",
            "31/12/2000T15:58:04D1"
        );
    }

    @Test
    public void testFormatDaySlashMonthSayYearLiteralTHourColonMinuteColonSecondMillisRounding() {
        this.parseFormatAndCheck(
            "d/m/yyyy\\Th:m:ss.0",
            "2000-12-31T15:58:04.167",
            "31/12/2000T15:58:04D2"
        );
    }

    @Test
    public void testFormatDaySlashMonthSayYearLiteralTHourColonMinuteColonSecondMillisRounding2() {
        this.parseFormatAndCheck(
            "d/m/yyyy\\Th:m:ss.00",
            "2000-12-31T15:58:04.167",
            "31/12/2000T15:58:04D17"
        );
    }

    @Test
    public void testFormatDaySlashMonthSayYearLiteralTHourColonMinuteColonSecondMillisRounding3() {
        this.parseFormatAndCheck(
            "d/m/yyyy\\Th:m:ss.000",
            "2000-12-31T15:58:04.167",
            "31/12/2000T15:58:04D167"
        );
    }

    @Test
    public void testFormatDaySlashMonthSayYearLiteralTHourColonMinuteColonSecondMillisRounding4() {
        this.parseFormatAndCheck(
            "d/m/yyyy\\Th:m:ss.0000",
            "2000-12-31T15:58:04.167",
            "31/12/2000T15:58:04D1670"
        );
    }

    @Test
    public void testFormatDaySlashMonthSayYearLiteralTHourColonMinuteColonSecondMillisRounding5() {
        this.parseFormatAndCheck(
            "d/m/yyyy\\Th:m:ss.00000",
            "2000-12-31T15:58:04.167",
            "31/12/2000T15:58:04D16700"
        );
    }

    @Test
    public void testFormatDaySlashMonthSayYearLiteralTHourColonMinuteColonSecondMillisRounding6() {
        this.parseFormatAndCheck(
            "d/m/yyyy\\Th:m:ss.000000",
            "2000-12-31T15:58:04.167",
            "31/12/2000T15:58:04D167000"
        );
    }

    @Test
    public void testFormatWithColorName() {
        this.formatAndCheck(
            this.createFormatter("[RED]hh/mm/ss"),
            LocalDateTime.of(
                1999,
                12,
                31,
                12,
                58,
                59
            ),
            SpreadsheetText.with("12/58/59")
                .setColor(
                    Optional.of(RED)
                )
        );
    }

    // helpers..........................................................................................................

    private void parseFormatAndCheck(final String pattern,
                                     final String value,
                                     final String text) {
        this.parseFormatAndCheck(pattern, value, this.createContext(), text);
    }

    private void parseFormatAndCheck(final String pattern,
                                     final LocalDateTime value,
                                     final String text) {
        this.formatAndCheck(
            this.createFormatter(pattern),
            value,
            this.createContext(),
            SpreadsheetText.with(text)
        );
    }

    private void parseFormatAndCheck(final String pattern,
                                     final String value,
                                     final SpreadsheetFormatterContext context,
                                     final String text) {
        this.parseFormatAndCheck(
            pattern,
            value,
            context,
            SpreadsheetText.with(text)
        );
    }

    private void parseFormatAndCheck(final String pattern,
                                     final String value,
                                     final SpreadsheetFormatterContext context,
                                     final SpreadsheetText text) {
        this.parseFormatAndCheck(
            pattern,
            this.parseLocalDateTime(value),
            context,
            text
        );
    }

    private void parseFormatAndCheck(final String pattern,
                                     final Temporal value,
                                     final SpreadsheetFormatterContext context,
                                     final SpreadsheetText text) {
        this.formatAndCheck(
            this.createFormatter(pattern),
            value,
            context,
            text
        );
    }

    @Override
    String pattern() {
        return "YYYYMMDDHHMMSS";
    }

    @Override
    Parser<SpreadsheetFormatParserContext> parser() {
        return SpreadsheetFormatParsers.dateTimeFormat();
    }

    // tokens...........................................................................................................

    @Test
    public void testTokens() {
        this.tokensAndCheck(
            this.createFormatter("d/mm/yyyy"),
            this.createContext(),
            SpreadsheetFormatterSelectorToken.with(
                "d",
                "d",
                Lists.of(
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "dd",
                        "dd"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "ddd",
                        "ddd"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "dddd",
                        "dddd"
                    )
                )
            ),
            SpreadsheetFormatterSelectorToken.with(
                "/",
                "/",
                SpreadsheetFormatterSelectorToken.NO_ALTERNATIVES
            ),
            SpreadsheetFormatterSelectorToken.with(
                "mm",
                "mm",
                Lists.of(
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "m",
                        "m"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "mmm",
                        "mmm"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "mmmm",
                        "mmmm"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "mmmmm",
                        "mmmmm"
                    )
                )
            ),
            SpreadsheetFormatterSelectorToken.with(
                "/",
                "/",
                SpreadsheetFormatterSelectorToken.NO_ALTERNATIVES
            ),
            SpreadsheetFormatterSelectorToken.with(
                "yyyy",
                "yyyy",
                Lists.of(
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "yy",
                        "yy"
                    )
                )
            )
        );
    }

    @Test
    public void testTokensWithColor() {
        this.tokensAndCheck(
            this.createFormatter("[RED]d/mm/yyyy"),
            this.createContext(),
            SpreadsheetFormatterSelectorToken.with(
                "[RED]",
                "[RED]",
                Stream.concat(
                    SpreadsheetColorName.DEFAULTS.stream()
                        .map(n -> "[" + n.text() + "]")
                        .map(t -> SpreadsheetFormatterSelectorTokenAlternative.with(t, t)),
                    IntStream.rangeClosed(
                            SpreadsheetColors.MIN,
                            SpreadsheetColors.MAX
                        ).mapToObj(n -> "[Color " + n + "]")
                        .map(t -> SpreadsheetFormatterSelectorTokenAlternative.with(t, t))
                ).collect(Collectors.toList())
            ),
            SpreadsheetFormatterSelectorToken.with(
                "d",
                "d",
                Lists.of(
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "dd",
                        "dd"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "ddd",
                        "ddd"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "dddd",
                        "dddd"
                    )
                )
            ),
            SpreadsheetFormatterSelectorToken.with(
                "/",
                "/",
                SpreadsheetFormatterSelectorToken.NO_ALTERNATIVES
            ),
            SpreadsheetFormatterSelectorToken.with(
                "mm",
                "mm",
                Lists.of(
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "m",
                        "m"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "mmm",
                        "mmm"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "mmmm",
                        "mmmm"
                    ),
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "mmmmm",
                        "mmmmm"
                    )
                )
            ),
            SpreadsheetFormatterSelectorToken.with(
                "/",
                "/",
                SpreadsheetFormatterSelectorToken.NO_ALTERNATIVES
            ),
            SpreadsheetFormatterSelectorToken.with(
                "yyyy",
                "yyyy",
                Lists.of(
                    SpreadsheetFormatterSelectorTokenAlternative.with(
                        "yy",
                        "yy"
                    )
                )
            )
        );
    }

    @Override
    SpreadsheetPatternSpreadsheetFormatterDateTime createFormatter0(final DateTimeSpreadsheetFormatParserToken token) {
        return SpreadsheetPatternSpreadsheetFormatterDateTime.with(
            token,
            LocalDateTime.class
        );
    }

    @Override
    public LocalDateTime value() {
        return this.parseLocalDateTime(this.text());
    }

    @SuppressWarnings("SameReturnValue")
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
        public char zeroDigit() {
            return '0';
        }

        @Override
        public boolean canConvert(final Object value,
                                  final Class<?> target) {
            return value instanceof LocalDateTime && LocalDateTime.class == target;
        }

        @Override
        public <T> Either<T, String> convert(final Object value, final Class<T> target) {
            checkEquals(LocalDateTime.class, target, "target");

            return this.successfulConversion(
                target.cast(value),
                target
            );
        }

        @Override
        public Optional<Color> colorName(final SpreadsheetColorName name) {
            checkEquals(
                SpreadsheetColorName.with("red"),
                name,
                "colorName"
            );
            return Optional.of(
                RED
            );
        }

        @Override
        public Optional<Color> colorNumber(final int number) {
            checkEquals(
                44,
                number,
                "colorNumber"
            );
            return Optional.of(
                RED
            );
        }
    }

    private LocalDateTime parseLocalDateTime(final String value) {
        return LocalDateTime.parse(
            value,
            DateTimeFormatter.ofPattern(
                "yyyy-MM-dd'T'HH:mm:ss.SSS",
                Locale.ENGLISH
            )
        );
    }

    // equals...........................................................................................................

    @Test
    public void testEqualsDifferentPattern() {
        this.checkNotEquals(
            this.createFormatter("dd/mm/yyyy")
        );
    }

    @Test
    public void testEqualsDifferentValueType() {
        final DateTimeSpreadsheetFormatParserToken token = this.parsePatternOrFail("dd/mm/yyyy");

        this.checkNotEquals(
            SpreadsheetPatternSpreadsheetFormatterDateTime.with(
                token,
                LocalDate.class
            ),
            SpreadsheetPatternSpreadsheetFormatterDateTime.with(
                token,
                LocalDateTime.class
            )
        );
    }

    //toString .........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createFormatter(),
            this.pattern()
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetPatternSpreadsheetFormatterDateTime> type() {
        return SpreadsheetPatternSpreadsheetFormatterDateTime.class;
    }
}

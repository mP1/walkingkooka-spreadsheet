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
import walkingkooka.spreadsheet.color.SpreadsheetColors;
import walkingkooka.spreadsheet.format.parser.DateTimeSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelectorToken;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelectorTokenAlternative;
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

    // year.............................................................................................................

    @Test
    public void testFormatDateTimeYear2000() {
        this.parseFormatAndCheck("y", this.text(), "00");
    }

    @Test
    public void testFormatDateTimeYear1999() {
        this.parseFormatAndCheck(
            "y",
            "1999-12-31T15:58:59.000",
            "99"
        );
    }

    @Test
    public void testFormatDateTimeYearYear2000() {
        this.parseFormatAndCheck(
            "yy",
            this.text(),
            "00"
        );
    }

    @Test
    public void testFormatDateTimeYearYear1999() {
        this.parseFormatAndCheck(
            "yy",
            "1999-12-31T15:58:59.000",
            "99"
        );
    }

    @Test
    public void testFormatDateTimeYearYearYear000() {
        this.parseFormatAndCheck(
            "yyy",
            this.text(),
            "2000"
        );
    }

    @Test
    public void testFormatDateTimeYearYearYear1999() {
        this.parseFormatAndCheck(
            "yyy",
            "1999-12-31T15:58:59.000",
            "1999"
        );
    }

    @Test
    public void testFormatDateTimeYearYearYearYear2000() {
        this.parseFormatAndCheck(
            "yyyy",
            this.text(),
            "2000"
        );
    }

    @Test
    public void testFormatDateTimeYearYearYearYear1999() {
        this.parseFormatAndCheck(
            "yyyy",
            "1999-12-31T15:58:59.000",
            "1999"
        );
    }

    @Test
    public void testFormatDateTimeYearYearYearYear789() {
        this.parseFormatAndCheck(
            "yyyy",
            "0789-12-31T15:58:59.000",
            "789"
        );
    }

    // month............................................................................................................

    @Test
    public void testFormatDateTimeMonthDecember() {
        this.parseFormatAndCheck(
            "m",
            this.text(),
            "12"
        );
    }

    @Test
    public void testFormatDateTimeMonthJanuary() {
        this.parseFormatAndCheck(
            "m",
            "1999-01-31T15:58:59.000",
            "1"
        );
    }

    @Test
    public void testFormatDateTimeMonthMonthDecember() {
        this.parseFormatAndCheck(
            "mm",
            this.text(),
            "12"
        );
    }

    @Test
    public void testFormatDateTimeMonthMonthJanuary() {
        this.parseFormatAndCheck(
            "mm",
            "1999-01-31T15:58:59.000",
            "01"
        );
    }

    @Test
    public void testFormatDateTimeMonthMonthMonthDecember() {
        this.parseFormatAndCheckMMM(
            "2000-12-31T15:58:59.000",
            11,
            "Dec!"
        );
    }

    @Test
    public void testFormatDateTimeMonthMonthMonthJanuary() {
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
    public void testFormatDateTimeMonthMonthMonthMonthDecember() {
        this.parseFormatAndCheckMMMM(
            "2000-12-31T15:58:59.000",
            11,
            "December!"
        );
    }

    @Test
    public void testFormatDateTimeMonthMonthMonthMonthJanuary() {
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
    public void testFormatDateTimeDay31() {
        this.parseFormatAndCheck(
            "d",
            this.text(),
            "31"
        );
    }

    @Test
    public void testFormatDateTimeDay1() {
        this.parseFormatAndCheck(
            "d",
            "1999-12-01T15:58:59.000",
            "1"
        );
    }

    @Test
    public void testFormatDateTimeDayDay31() {
        this.parseFormatAndCheck(
            "dd",
            this.text(),
            "31"
        );
    }

    @Test
    public void testFormatDateTimeDayDay1() {
        this.parseFormatAndCheck(
            "dd",
            "2000-12-01T15:58:59.000",
            "01"
        );
    }

    @Test
    public void testFormatDateTimeDayDayDay31() {
        this.parseFormatAndCheckDDD(
            "2000-12-31T15:58:59.000",
            0,
            "Mon!"
        ); // dayOfWeek name
    }

    @Test
    public void testFormatDateTimeDayDayDay1() {
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
    public void testFormatDateTimeDayDayDayDay31() {
        this.parseFormatAndCheckDDDD(
            "2000-12-31T15:58:59.000",
            0,
            "Monday!"
        ); // dayOfWeek name
    }

    @Test
    public void testFormatDateTimeDayDayDayDay1() {
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

    // day month year...................................................................................................

    @Test
    public void testFormatDateTimeYearYearMonthDay() {
        this.parseFormatAndCheck(
            "yymd",
            "1999-12-31T01:58:59.000",
            "991231"
        );
    }

    @Test
    public void testFormatDateTimeMonthDayYearYear() {
        this.parseFormatAndCheck(
            "mdyy",
            "1999-12-31T01:58:59.000",
            "123199"
        );
    }

    // hour.............................................................................................................

    @Test
    public void testFormatDateTimeHour12() {
        this.parseFormatAndCheck(
            "h",
            "2000-06-29T12:58:59.000",
            "12"
        );
    }

    @Test
    public void testFormatDateTimeHour1() {
        this.parseFormatAndCheck(
            "h",
            "2000-06-29T01:58:59.000",
            "1"
        );
    }

    @Test
    public void testFormatDateTimeHour15() {
        this.parseFormatAndCheck(
            "h",
            "2000-06-01T15:58:59.000",
            "15"
        );
    }

    @Test
    public void testFormatDateTimeHour1Ampm() {
        this.parseHourFormatAndCheck(
            "hAM/PM",
            "2000-06-29T01:58:59.000",
            1,
            "AM!",
            "1AM!"
        );
    }

    @Test
    public void testFormatDateTimeHour12Ampm() {
        this.parseHourFormatAndCheck(
            "hAM/PM",
            "2000-06-29T12:58:59.000",
            12,
            "AM!",
            "12AM!"
        );
    }

    @Test
    public void testFormatDateTimeHour15Ampm() {
        this.parseHourFormatAndCheck(
            "hAM/PM",
            "2000-06-01T15:58:59.000",
            15,
            "PM!",
            "3PM!"
        );
    }

    @Test
    public void testFormatDateTimeHour23Ampm() {
        this.parseHourFormatAndCheck(
            "hAM/PM",
            "2000-06-01T23:58:59.000",
            23,
            "PM!",
            "11PM!"
        );
    }

    @Test
    public void testFormatDateTimeHourHour1Ampm() {
        this.parseHourFormatAndCheck(
            "hhAM/PM",
            "2000-06-29T01:58:59.000",
            1,
            "AM!",
            "01AM!"
        );
    }

    @Test
    public void testFormatDateTimeHourHour12Ampm() {
        this.parseHourFormatAndCheck(
            "hhAM/PM",
            "2000-06-29T12:58:59.000",
            12,
            "AM!",
            "12AM!"
        );
    }

    @Test
    public void testFormatDateTimeHourHour15Ampm() {
        this.parseHourFormatAndCheck(
            "hhAM/PM",
            "2000-06-01T15:58:59.000",
            15,
            "PM!",
            "03PM!"
        );
    }

    @Test
    public void testFormatDateTimeHourHour23Ampm() {
        this.parseHourFormatAndCheck(
            "hhAM/PM",
            "2000-06-01T23:58:59.000",
            23,
            "PM!",
            "11PM!"
        );
    }

    @Test
    public void testFormatDateTimeHourHourHour1() {
        this.parseFormatAndCheck(
            "hhh",
            "2000-06-29T01:58:59.000",
            "01"
        );
    }

    @Test
    public void testFormatDateTimeHourHourHour15() {
        this.parseFormatAndCheck(
            "hhh",
            "2000-06-01T15:58:59.000",
            "15"
        );
    }

    @Test
    public void testFormatDateTimeHourHourHour1Ampm() {
        this.parseHourFormatAndCheck(
            "hhhAM/PM",
            "2000-06-29T01:58:59.000",
            1,
            "AM!",
            "01AM!"
        );
    }

    @Test
    public void testFormatDateTimeHourHourHour12Ampm() {
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
    public void testFormatDateTimeHourMinute1() {
        this.parseFormatAndCheck(
            "hm",
            "2000-06-29T12:01:59.000",
            "121"
        );
    }

    @Test
    public void testFormatDateTimeHourMinute58() {
        this.parseFormatAndCheck(
            "hm",
            "2000-06-30T12:58:59.000",
            "1258"
        );
    }

    @Test
    public void testFormatDateTimeHourMinuteMinute1() {
        this.parseFormatAndCheck(
            "hmm",
            "2000-06-29T12:01:59.000",
            "1201"
        );
    }

    @Test
    public void testFormatDateTimeHourMinuteMinute58() {
        this.parseFormatAndCheck(
            "hmm",
            "2000-06-30T12:58:59.000",
            "1258"
        );
    }

    @Test
    public void testFormatDateTimeHourMinuteMinuteMinute1() {
        this.parseFormatAndCheck(
            "hmmm",
            "2000-06-29T12:01:59.000",
            "1201"
        );
    }

    @Test
    public void testFormatDateTimeHourMinuteMinuteMinute58() {
        this.parseFormatAndCheck(
            "hmmm",
            "2000-06-30T12:58:59.000",
            "1258"
        );
    }

    // second...........................................................................................................

    @Test
    public void testFormatDateTimeSecond1() {
        this.parseFormatAndCheck(
            "s",
            "2000-06-29T12:58:01.000",
            "1"
        );
    }

    @Test
    public void testFormatDateTimeSecond59() {
        this.parseFormatAndCheck(
            "s",
            "2000-06-30T12:58:59.000",
            "59"
        );
    }

    @Test
    public void testFormatDateTimeSecondSecond1() {
        this.parseFormatAndCheck(
            "ss",
            "2000-06-29T12:58:01.000",
            "01"
        );
    }

    @Test
    public void testFormatDateTimeSecondSecond59() {
        this.parseFormatAndCheck(
            "ss",
            "2000-06-30T12:58:59.000",
            "59"
        );
    }

    @Test
    public void testFormatDateTimeSecondSecondSecond1() {
        this.parseFormatAndCheck(
            "sss",
            "2000-06-29T12:58:01.000",
            "01"
        );
    }

    @Test
    public void testFormatDateTimeSecondSecondSecond59() {
        this.parseFormatAndCheck(
            "sss",
            "2000-06-30T12:58:59.000",
            "59"
        );
    }

    // milli............................................................................................................

    @Test
    public void testFormatDateTimeSecondMillis1() {
        this.parseFormatAndCheck2(
            "s.0",
            "59D1"
        );
    }

    @Test
    public void testFormatDateTimeSecondMillis2() {
        this.parseFormatAndCheck2(
            "s.00",
            "59D12"
        );
    }

    @Test
    public void testFormatDateTimeSecondMillis3() {
        this.parseFormatAndCheck2(
            "s.000",
            "59D123"
        );
    }

    @Test
    public void testFormatDateTimeSecondMillis4() {
        this.parseFormatAndCheck2(
            "s.0000",
            "59D1235"
        );
    }

    @Test
    public void testFormatDateTimeSecondMillis5() {
        this.parseFormatAndCheck2(
            "s.00000",
            "59D12346"
        );
    }

    @Test
    public void testFormatDateTimeSecondMillis6() {
        this.parseFormatAndCheck2(
            "s.000000",
            "59D123457"
        );
    }

    @Test
    public void testFormatDateTimeSecondMillis7() {
        this.parseFormatAndCheck2(
            "s.0000000",
            "59D1234568"
        );
    }

    @Test
    public void testFormatDateTimeSecondMillis8() {
        this.parseFormatAndCheck2(
            "s.00000000",
            "59D12345679"
        );
    }

    @Test
    public void testFormatDateTimeSecondMillis9() {
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
    public void testFormatDateTimeHourHourMinuteMinuteSecondSecond125801() {
        this.parseHourFormatAndCheck(
            "hhmmss",
            "2000-06-29T12:58:01.000",
            12,
            "AM!",
            "125801"
        );
    }

    @Test
    public void testFormatDateTimeHourHourMinuteMinuteSecondSecondAm125801() {
        this.parseHourFormatAndCheck(
            "hhmmssAM/PM",
            "2000-06-29T12:58:01.000",
            12,
            "AM!",
            "125801AM!"
        );
    }

    @Test
    public void testFormatDateTimeHourHourMinuteMinuteSecondSecondDayDayMonthMonthYearYearYearYear() {
        this.parseHourFormatAndCheck(
            "hhmmssAM/PMddmmyyyy",
            "2000-06-29T12:58:01.000",
            12,
            "AM!",
            "125801AM!29062000"
        );
    }

    @Test
    public void testFormatDateTimeHourHourMinuteMinuteSecondSecondDayDayMonthMonthYearYearYearYearAmPm() {
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
    public void testFormatDateTimeEscaped() {
        this.parseEscapedOrLiteralFormatAndCheck(
            "\\A",
            "A"
        );
    }

    @Test
    public void testFormatDateTimeCurrency() {
        this.parseEscapedOrLiteralFormatAndCheck("$");
    }

    @Test
    public void testFormatDateTimeMinus() {
        this.parseEscapedOrLiteralFormatAndCheck("-");
    }

    @Test
    public void testFormatDateTimePlus() {
        this.parseEscapedOrLiteralFormatAndCheck("-");
    }

    @Test
    public void testFormatDateTimeSlash() {
        this.parseEscapedOrLiteralFormatAndCheck("/");
    }

    @Test
    public void testFormatDateTimeOpenParens() {
        this.parseEscapedOrLiteralFormatAndCheck("(");
    }

    @Test
    public void testFormatDateTimeCloseParens() {
        this.parseEscapedOrLiteralFormatAndCheck(")");
    }

    @Test
    public void testFormatDateTimeColon() {
        this.parseEscapedOrLiteralFormatAndCheck(":");
    }

    @Test
    public void testFormatDateTimeSpace() {
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
    public void testFormatDateTimeQuotedText() {
        this.parseFormatAndCheck(
            "\"Hello\"",
            this.text(),
            "Hello"
        );
    }

    // mixed............................................................................................................

    @Test
    public void testFormatDateTimeDaySlashMonthSayYearSpaceHourColonMinuteColonSecond() {
        this.parseFormatAndCheck(
            "d/m/yyyy h:m:s",
            "2000-12-31T15:58:59.000",
            "31/12/2000 15:58:59"
        );
    }

    @Test
    public void testFormatDateTimeDaySlashMonthSayYearLiteralTHourColonMinuteColonSecond() {
        this.parseFormatAndCheck(
            "d/m/yyyy\\Th:m:s",
            "2000-12-31T15:58:59.000",
            "31/12/2000T15:58:59"
        );
    }

    @Test
    public void testFormatDateTimeDaySlashMonthSayYearLiteralTHourColonMinuteColonSecond2() {
        this.parseFormatAndCheck(
            "d/m/yyyy\\Th:m:s",
            "2000-12-31T15:58:01.000",
            "31/12/2000T15:58:1"
        );
    }

    @Test
    public void testFormatDateTimeDaySlashMonthSayYearLiteralTHourColonMinuteColonSecondSecond() {
        this.parseFormatAndCheck(
            "d/m/yyyy\\Th:m:ss",
            "2000-12-31T15:58:59.000",
            "31/12/2000T15:58:59"
        );
    }

    @Test
    public void testFormatDateTimeDaySlashMonthSayYearLiteralTHourColonMinuteColonSecondSecondRounding() {
        this.parseFormatAndCheck(
            "d/m/yyyy\\Th:mm:ss",
            "2000-12-31T15:58:03.678",
            "31/12/2000T15:58:04"
        );
    }

    @Test
    public void testFormatDateTimeDaySlashMonthSayYearLiteralTHourColonMinuteColonSecondMillis() {
        this.parseFormatAndCheck(
            "d/m/yyyy\\Th:m:ss.0",
            "2000-12-31T15:58:04.100",
            "31/12/2000T15:58:04D1"
        );
    }

    @Test
    public void testFormatDateTimeDaySlashMonthSayYearLiteralTHourColonMinuteColonSecondMillisRounding() {
        this.parseFormatAndCheck(
            "d/m/yyyy\\Th:m:ss.0",
            "2000-12-31T15:58:04.167",
            "31/12/2000T15:58:04D2"
        );
    }

    @Test
    public void testFormatDateTimeDaySlashMonthSayYearLiteralTHourColonMinuteColonSecondMillisRounding2() {
        this.parseFormatAndCheck(
            "d/m/yyyy\\Th:m:ss.00",
            "2000-12-31T15:58:04.167",
            "31/12/2000T15:58:04D17"
        );
    }

    @Test
    public void testFormatDateTimeDaySlashMonthSayYearLiteralTHourColonMinuteColonSecondMillisRounding3() {
        this.parseFormatAndCheck(
            "d/m/yyyy\\Th:m:ss.000",
            "2000-12-31T15:58:04.167",
            "31/12/2000T15:58:04D167"
        );
    }

    @Test
    public void testFormatDateTimeDaySlashMonthSayYearLiteralTHourColonMinuteColonSecondMillisRounding4() {
        this.parseFormatAndCheck(
            "d/m/yyyy\\Th:m:ss.0000",
            "2000-12-31T15:58:04.167",
            "31/12/2000T15:58:04D1670"
        );
    }

    @Test
    public void testFormatDateTimeDaySlashMonthSayYearLiteralTHourColonMinuteColonSecondMillisRounding5() {
        this.parseFormatAndCheck(
            "d/m/yyyy\\Th:m:ss.00000",
            "2000-12-31T15:58:04.167",
            "31/12/2000T15:58:04D16700"
        );
    }

    @Test
    public void testFormatDateTimeDaySlashMonthSayYearLiteralTHourColonMinuteColonSecondMillisRounding6() {
        this.parseFormatAndCheck(
            "d/m/yyyy\\Th:m:ss.000000",
            "2000-12-31T15:58:04.167",
            "31/12/2000T15:58:04D167000"
        );
    }

    @Test
    public void testFormatDateTimeWithColorName() {
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

    @Test
    public void testFormatDate() {
        this.formatAndCheck(
            this.createFormatter("yyyy/mm/dd hh/mm/ss"),
            LocalDate.of(
                1999,
                12,
                31
            ),
            SpreadsheetText.with("1999/12/31 12/00/00")
        );
    }

    @Test
    public void testFormatTime() {
        this.formatAndCheck(
            this.createFormatter("yyyy/mm/dd hh/mm/ss"),
            LocalTime.of(
                12,
                58,
                59
            ),
            SpreadsheetText.with("1970/01/01 12/58/59")
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
            return (value instanceof LocalDate ||
                value instanceof LocalTime ||
                value instanceof LocalDateTime) &&
                LocalDateTime.class == target;
        }

        @Override
        public <T> Either<T, String> convert(final Object value,
                                             final Class<T> target) {
            if (LocalDateTime.class == target) {
                if (value instanceof LocalDate) {
                    return this.successfulConversion(
                        LocalDateTime.of(
                            LocalDate.class.cast(value),
                            LocalTime.NOON
                        ),
                        target
                    );
                } else {
                    if (value instanceof LocalTime) {
                        return this.successfulConversion(
                            LocalDateTime.of(
                                LocalDate.EPOCH,
                                LocalTime.class.cast(value)
                            ),
                            target
                        );
                    } else {
                        if (value instanceof LocalDateTime) {
                            return this.successfulConversion(
                                LocalDateTime.class.cast(value),
                                target
                            );
                        }
                    }
                }
            }

            return this.failConversion(
                value,
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

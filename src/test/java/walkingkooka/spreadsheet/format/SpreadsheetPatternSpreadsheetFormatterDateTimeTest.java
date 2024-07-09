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
import walkingkooka.color.Color;
import walkingkooka.convert.ConversionException;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateTimeParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.SequenceParserToken;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetPatternSpreadsheetFormatterDateTimeTest extends SpreadsheetPatternSpreadsheetFormatterTestCase<
        SpreadsheetPatternSpreadsheetFormatterDateTime,
        SpreadsheetFormatDateTimeParserToken> {

    private final static Color RED = Color.parse("#FF0000");

    // with.............................................................................................................

    @Test
    public void testWithNullTypeTesterFails() {
        assertThrows(NullPointerException.class, () -> SpreadsheetPatternSpreadsheetFormatterDateTime.with(this.parsePatternOrFail(this.pattern()), null));
    }

    // tests.............................................................................................................

    @Test
    public void testFormatConvertLocalDateTimeFails() {
        final LocalDateTime value = LocalDateTime.now();

        assertThrows(
                ConversionException.class,
                () -> this.createFormatter().format(
                        value,
                        new FakeSpreadsheetFormatterContext() {

                            @Override
                            public <T> Either<T, String> convert(final Object v,
                                                                 final Class<T> type) {
                                assertSame(value, v, "value");
                                checkEquals(LocalDateTime.class, type, "type");
                                return Either.right("Failed!");
                            }
                        }
                )
        );
    }

    @Test
    public void testConvertLocalDateTimeFails() {
        final LocalTime time = LocalTime.now();

        assertThrows(
                ConversionException.class,
                () -> this.createFormatter()
                        .formatSpreadsheetText(
                        time,
                        new FakeSpreadsheetFormatterContext() {
                            @Override
                            public <T> Either<T, String> convert(final Object value, final Class<T> target) {
                                assertSame(time, value, "value");
                                checkEquals(LocalDateTime.class, target, "target");

                                return this.failConversion(value, target);
                            }
                        })
        );
    }

    @SuppressWarnings("unused")
    @Override
    public void testCanFormatFalse() {
        // SpreadsheetPatternSpreadsheetFormatterDateTime says it can format anything. It converts all values to LocalDateTime before formatting.
    }

    @SuppressWarnings("unused")
    @Override
    public void testFormatUnsupportedValueFails() {
    }

    // year.............................................................................................................

    @Test
    public void testParseAndFormatYear2000() {
        this.parseFormatAndCheck("y", this.text(), "00");
    }

    @Test
    public void testParseAndFormatYear1999() {
        this.parseFormatAndCheck("y", "1999-12-31T15:58:59.000", "99");
    }

    @Test
    public void testParseAndFormatYearYear2000() {
        this.parseFormatAndCheck("yy", this.text(), "00");
    }

    @Test
    public void testParseAndFormatYearYear1999() {
        this.parseFormatAndCheck("yy", "1999-12-31T15:58:59.000", "99");
    }

    @Test
    public void testParseAndFormatYearYearYear000() {
        this.parseFormatAndCheck("yyy", this.text(), "2000");
    }

    @Test
    public void testParseAndFormatYearYearYear1999() {
        this.parseFormatAndCheck("yyy", "1999-12-31T15:58:59.000", "1999");
    }

    @Test
    public void testParseAndFormatYearYearYearYear2000() {
        this.parseFormatAndCheck("yyyy", this.text(), "2000");
    }

    @Test
    public void testParseAndFormatYearYearYearYear1999() {
        this.parseFormatAndCheck("yyyy", "1999-12-31T15:58:59.000", "1999");
    }

    @Test
    public void testParseAndFormatYearYearYearYear789() {
        this.parseFormatAndCheck("yyyy", "0789-12-31T15:58:59.000", "789");
    }

    // month.............................................................................................

    @Test
    public void testParseAndFormatMonthDecember() {
        this.parseFormatAndCheck("m", this.text(), "12");
    }

    @Test
    public void testParseAndFormatMonthJanuary() {
        this.parseFormatAndCheck("m", "1999-01-31T15:58:59.000", "1");
    }

    @Test
    public void testParseAndFormatMonthMonthDecember() {
        this.parseFormatAndCheck("mm", this.text(), "12");
    }

    @Test
    public void testParseAndFormatMonthMonthJanuary() {
        this.parseFormatAndCheck("mm", "1999-01-31T15:58:59.000", "01");
    }

    @Test
    public void testParseAndFormatMonthMonthMonthDecember() {
        this.parseFormatAndCheckMMM("2000-12-31T15:58:59.000", 11, "Dec!");
    }

    @Test
    public void testParseAndFormatMonthMonthMonthJanuary() {
        this.parseFormatAndCheckMMM("1999-01-31T15:58:59.000", 0, "Jan!");
    }

    private void parseFormatAndCheckMMM(final String date, final int monthNumber, final String monthName) {
        this.parseFormatAndCheck("mmm",
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
    public void testParseAndFormatMonthMonthMonthMonthDecember() {
        this.parseFormatAndCheckMMMM("2000-12-31T15:58:59.000", 11, "December!");
    }

    @Test
    public void testParseAndFormatMonthMonthMonthMonthJanuary() {
        this.parseFormatAndCheckMMMM("1999-01-31T15:58:59.000", 0, "January!");
    }

    private void parseFormatAndCheckMMMM(final String dateTime, final int monthNumber, final String monthName) {
        this.parseFormatAndCheck("mmmm",
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

    // day.............................................................................................

    @Test
    public void testParseAndFormatDay31() {
        this.parseFormatAndCheck("d", this.text(), "31");
    }

    @Test
    public void testParseAndFormatDay1() {
        this.parseFormatAndCheck("d", "1999-12-01T15:58:59.000", "1");
    }

    @Test
    public void testParseAndFormatDayDay31() {
        this.parseFormatAndCheck("dd", this.text(), "31");
    }

    @Test
    public void testParseAndFormatDayDay1() {
        this.parseFormatAndCheck("dd", "2000-12-01T15:58:59.000", "01");
    }

    @Test
    public void testParseAndFormatDayDayDay31() {
        this.parseFormatAndCheckDDD("2000-12-31T15:58:59.000", 0, "Mon!"); // dayOfWeek name
    }

    @Test
    public void testParseAndFormatDayDayDay1() {
        this.parseFormatAndCheckDDD("1999-12-01T15:58:59.000", 3, "Mon!"); // dayOfWeek name
    }

    private void parseFormatAndCheckDDD(final String dateTime, final int dayNumber, final String dayName) {
        this.parseFormatAndCheck("ddd",
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
    public void testParseAndFormatDayDayDayDay31() {
        this.parseFormatAndCheckDDDD("2000-12-31T15:58:59.000", 0, "Monday!"); // dayOfWeek name
    }

    @Test
    public void testParseAndFormatDayDayDayDay1() {
        this.parseFormatAndCheckDDDD("1999-12-01T15:58:59.000", 3, "Monday!"); // dayOfWeek name
    }

    private void parseFormatAndCheckDDDD(final String date, final int dayNumber, final String dayName) {
        this.parseFormatAndCheck("dddd",
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

    // day month year.............................................................................................

    @Test
    public void testParseAndFormatYearYearMonthDay() {
        this.parseFormatAndCheck("yymd", "1999-12-31T01:58:59.000", "991231");
    }

    @Test
    public void testParseAndFormatMonthDayYearYear() {
        this.parseFormatAndCheck("mdyy", "1999-12-31T01:58:59.000", "123199");
    }

    // hour.............................................................................................

    @Test
    public void testParseAndFormatHour12() {
        this.parseFormatAndCheck("h", "2000-06-29T12:58:59.000", "12");
    }

    @Test
    public void testParseAndFormatHour1() {
        this.parseFormatAndCheck("h", "2000-06-29T01:58:59.000", "1");
    }

    @Test
    public void testParseAndFormatHour15() {
        this.parseFormatAndCheck("h", "2000-06-01T15:58:59.000", "15");
    }

    @Test
    public void testParseAndFormatHour1Ampm() {
        this.parseHourFormatAndCheck("hAM/PM", "2000-06-29T01:58:59.000", 1, "AM!", "1AM!");
    }

    @Test
    public void testParseAndFormatHour12Ampm() {
        this.parseHourFormatAndCheck("hAM/PM", "2000-06-29T12:58:59.000", 12, "AM!", "12AM!");
    }

    @Test
    public void testParseAndFormatHour15Ampm() {
        this.parseHourFormatAndCheck("hAM/PM", "2000-06-01T15:58:59.000", 15, "PM!", "3PM!");
    }

    @Test
    public void testParseAndFormatHour23Ampm() {
        this.parseHourFormatAndCheck("hAM/PM", "2000-06-01T23:58:59.000", 23, "PM!", "11PM!");
    }

    @Test
    public void testParseAndFormatHourHour1Ampm() {
        this.parseHourFormatAndCheck("hhAM/PM", "2000-06-29T01:58:59.000", 1, "AM!", "01AM!");
    }

    @Test
    public void testParseAndFormatHourHour12Ampm() {
        this.parseHourFormatAndCheck("hhAM/PM", "2000-06-29T12:58:59.000", 12, "AM!", "12AM!");
    }

    @Test
    public void testParseAndFormatHourHour15Ampm() {
        this.parseHourFormatAndCheck("hhAM/PM", "2000-06-01T15:58:59.000", 15, "PM!", "03PM!");
    }

    @Test
    public void testParseAndFormatHourHour23Ampm() {
        this.parseHourFormatAndCheck("hhAM/PM", "2000-06-01T23:58:59.000", 23, "PM!", "11PM!");
    }

    @Test
    public void testParseAndFormatHourHourHour1() {
        this.parseFormatAndCheck("hhh", "2000-06-29T01:58:59.000", "01");
    }

    @Test
    public void testParseAndFormatHourHourHour15() {
        this.parseFormatAndCheck("hhh", "2000-06-01T15:58:59.000", "15");
    }

    @Test
    public void testParseAndFormatHourHourHour1Ampm() {
        this.parseHourFormatAndCheck("hhhAM/PM", "2000-06-29T01:58:59.000", 1, "AM!", "01AM!");
    }

    @Test
    public void testParseAndFormatHourHourHour12Ampm() {
        this.parseHourFormatAndCheck("hhhAM/PM", "2000-06-29T12:58:59.000", 12, "AM!", "12AM!");
    }

    // minute.............................................................................................

    @Test
    public void testParseAndFormatHourMinute1() {
        this.parseFormatAndCheck("hm", "2000-06-29T12:01:59.000", "121");
    }

    @Test
    public void testParseAndFormatHourMinute58() {
        this.parseFormatAndCheck("hm", "2000-06-30T12:58:59.000", "1258");
    }

    @Test
    public void testParseAndFormatHourMinuteMinute1() {
        this.parseFormatAndCheck("hmm", "2000-06-29T12:01:59.000", "1201");
    }

    @Test
    public void testParseAndFormatHourMinuteMinute58() {
        this.parseFormatAndCheck("hmm", "2000-06-30T12:58:59.000", "1258");
    }

    @Test
    public void testParseAndFormatHourMinuteMinuteMinute1() {
        this.parseFormatAndCheck("hmmm", "2000-06-29T12:01:59.000", "1201");
    }

    @Test
    public void testParseAndFormatHourMinuteMinuteMinute58() {
        this.parseFormatAndCheck("hmmm", "2000-06-30T12:58:59.000", "1258");
    }

    // second.............................................................................................

    @Test
    public void testParseAndFormatSecond1() {
        this.parseFormatAndCheck("s", "2000-06-29T12:58:01.000", "1");
    }

    @Test
    public void testParseAndFormatSecond59() {
        this.parseFormatAndCheck("s", "2000-06-30T12:58:59.000", "59");
    }

    @Test
    public void testParseAndFormatSecondSecond1() {
        this.parseFormatAndCheck("ss", "2000-06-29T12:58:01.000", "01");
    }

    @Test
    public void testParseAndFormatSecondSecond59() {
        this.parseFormatAndCheck("ss", "2000-06-30T12:58:59.000", "59");
    }

    @Test
    public void testParseAndFormatSecondSecondSecond1() {
        this.parseFormatAndCheck("sss", "2000-06-29T12:58:01.000", "01");
    }

    @Test
    public void testParseAndFormatSecondSecondSecond59() {
        this.parseFormatAndCheck("sss", "2000-06-30T12:58:59.000", "59");
    }

    // milli............................................................................................................

    @Test
    public void testParseAndFormatSecondMillis1() {
        this.parseFormatAndCheck2("s.0", "59D1");
    }

    @Test
    public void testParseAndFormatSecondMillis2() {
        this.parseFormatAndCheck2("s.00", "59D12");
    }

    @Test
    public void testParseAndFormatSecondMillis3() {
        this.parseFormatAndCheck2("s.000", "59D123");
    }

    @Test
    public void testParseAndFormatSecondMillis4() {
        this.parseFormatAndCheck2("s.0000", "59D1235");
    }

    @Test
    public void testParseAndFormatSecondMillis5() {
        this.parseFormatAndCheck2("s.00000", "59D12346");
    }

    @Test
    public void testParseAndFormatSecondMillis6() {
        this.parseFormatAndCheck2("s.000000", "59D123457");
    }

    @Test
    public void testParseAndFormatSecondMillis7() {
        this.parseFormatAndCheck2("s.0000000", "59D1234568");
    }

    @Test
    public void testParseAndFormatSecondMillis8() {
        this.parseFormatAndCheck2("s.00000000", "59D12345679");
    }

    @Test
    public void testParseAndFormatSecondMillis9() {
        this.parseFormatAndCheck2("s.000000000", "59D123456789");
    }

    private void parseFormatAndCheck2(final String pattern,
                                      final String text) {
        this.parseFormatAndCheck(pattern,
                LocalDateTime.of(2000, 12, 31, 12, 58, 59, 123456789),
                text);
    }

    // hour minute second...............................................................................................

    @Test
    public void testParseAndFormatHourHourMinuteMinuteSecondSecond125801() {
        this.parseHourFormatAndCheck("hhmmss", "2000-06-29T12:58:01.000", 12, "AM!", "125801");
    }

    @Test
    public void testParseAndFormatHourHourMinuteMinuteSecondSecondAm125801() {
        this.parseHourFormatAndCheck("hhmmssAM/PM", "2000-06-29T12:58:01.000", 12, "AM!", "125801AM!");
    }

    @Test
    public void testParseAndFormatHourHourMinuteMinuteSecondSecondDayDayMonthMonthYearYearYearYear() {
        this.parseHourFormatAndCheck("hhmmssAM/PMddmmyyyy", "2000-06-29T12:58:01.000", 12, "AM!", "125801AM!29062000");
    }

    @Test
    public void testParseAndFormatHourHourMinuteMinuteSecondSecondDayDayMonthMonthYearYearYearYearAmPm() {
        this.parseHourFormatAndCheck("hhmmssAM/PMddmmyyyy", "2000-06-29T15:58:01.000", 15, "AM!", "035801AM!29062000");
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
    public void testParseAndFormatEscaped() {
        this.parseEscapedOrLiteralFormatAndCheck("\\A", "A");
    }

    @Test
    public void testParseAndFormatCurrency() {
        this.parseEscapedOrLiteralFormatAndCheck("$");
    }

    @Test
    public void testParseAndFormatMinus() {
        this.parseEscapedOrLiteralFormatAndCheck("-");
    }

    @Test
    public void testParseAndFormatPlus() {
        this.parseEscapedOrLiteralFormatAndCheck("-");
    }

    @Test
    public void testParseAndFormatSlash() {
        this.parseEscapedOrLiteralFormatAndCheck("/");
    }

    @Test
    public void testParseAndFormatOpenParens() {
        this.parseEscapedOrLiteralFormatAndCheck("(");
    }

    @Test
    public void testParseAndFormatCloseParens() {
        this.parseEscapedOrLiteralFormatAndCheck(")");
    }

    @Test
    public void testParseAndFormatColon() {
        this.parseEscapedOrLiteralFormatAndCheck(":");
    }

    @Test
    public void testParseAndFormatSpace() {
        this.parseEscapedOrLiteralFormatAndCheck(" ");
    }

    private void parseEscapedOrLiteralFormatAndCheck(final String pattern) {
        this.parseEscapedOrLiteralFormatAndCheck(pattern, pattern);
    }

    private void parseEscapedOrLiteralFormatAndCheck(final String pattern, final String text) {
        this.parseFormatAndCheck(pattern, this.text(), this.createContext(), text);
    }

    @Test
    public void testParseAndFormatQuotedText() {
        this.parseFormatAndCheck("\"Hello\"", this.text(), "Hello");
    }

    // mixed.......................................................................................................

    @Test
    public void testParseAndFormatDaySlashMonthSayYearSpaceHourColonMinuteColonSecond() {
        this.parseFormatAndCheck("d/m/yyyy h:m:s",
                "2000-12-31T15:58:59.000",
                "31/12/2000 15:58:59");
    }

    @Test
    public void testParseAndFormatDaySlashMonthSayYearLiteralTHourColonMinuteColonSecond() {
        this.parseFormatAndCheck("d/m/yyyy\\Th:m:s",
                "2000-12-31T15:58:59.000",
                "31/12/2000T15:58:59");
    }

    @Test
    public void testParseAndFormatDaySlashMonthSayYearLiteralTHourColonMinuteColonSecond2() {
        this.parseFormatAndCheck("d/m/yyyy\\Th:m:s",
                "2000-12-31T15:58:01.000",
                "31/12/2000T15:58:1");
    }

    @Test
    public void testParseAndFormatDaySlashMonthSayYearLiteralTHourColonMinuteColonSecondSecond() {
        this.parseFormatAndCheck("d/m/yyyy\\Th:m:ss",
                "2000-12-31T15:58:59.000",
                "31/12/2000T15:58:59");
    }

    @Test
    public void testParseAndFormatDaySlashMonthSayYearLiteralTHourColonMinuteColonSecondSecondRounding() {
        this.parseFormatAndCheck("d/m/yyyy\\Th:mm:ss",
                "2000-12-31T15:58:03.678",
                "31/12/2000T15:58:04");
    }

    @Test
    public void testParseAndFormatDaySlashMonthSayYearLiteralTHourColonMinuteColonSecondMillis() {
        this.parseFormatAndCheck("d/m/yyyy\\Th:m:ss.0",
                "2000-12-31T15:58:04.100",
                "31/12/2000T15:58:04D1");
    }

    @Test
    public void testParseAndFormatDaySlashMonthSayYearLiteralTHourColonMinuteColonSecondMillisRounding() {
        this.parseFormatAndCheck("d/m/yyyy\\Th:m:ss.0",
                "2000-12-31T15:58:04.167",
                "31/12/2000T15:58:04D2");
    }

    @Test
    public void testParseAndFormatDaySlashMonthSayYearLiteralTHourColonMinuteColonSecondMillisRounding2() {
        this.parseFormatAndCheck("d/m/yyyy\\Th:m:ss.00",
                "2000-12-31T15:58:04.167",
                "31/12/2000T15:58:04D17");
    }

    @Test
    public void testParseAndFormatDaySlashMonthSayYearLiteralTHourColonMinuteColonSecondMillisRounding3() {
        this.parseFormatAndCheck("d/m/yyyy\\Th:m:ss.000",
                "2000-12-31T15:58:04.167",
                "31/12/2000T15:58:04D167");
    }

    @Test
    public void testParseAndFormatDaySlashMonthSayYearLiteralTHourColonMinuteColonSecondMillisRounding4() {
        this.parseFormatAndCheck("d/m/yyyy\\Th:m:ss.0000",
                "2000-12-31T15:58:04.167",
                "31/12/2000T15:58:04D1670");
    }

    @Test
    public void testParseAndFormatDaySlashMonthSayYearLiteralTHourColonMinuteColonSecondMillisRounding5() {
        this.parseFormatAndCheck("d/m/yyyy\\Th:m:ss.00000",
                "2000-12-31T15:58:04.167",
                "31/12/2000T15:58:04D16700");
    }

    @Test
    public void testParseAndFormatDaySlashMonthSayYearLiteralTHourColonMinuteColonSecondMillisRounding6() {
        this.parseFormatAndCheck("d/m/yyyy\\Th:m:ss.000000",
                "2000-12-31T15:58:04.167",
                "31/12/2000T15:58:04D167000");
    }

    // Date.............................................................................................................

    @Test
    public void testParseAndFormatDate() {
        this.parseFormatAndCheck(
                "yyyy/mm/dd",
                LocalDate.of(2000, 12, 31),
                SpreadsheetText.with("2000/12/31")
        );
    }

    @Test
    public void testParseAndFormatDateIncludesColorName() {
        this.parseFormatAndCheck(
                "[RED]yyyy/mm/dd",
                LocalDate.of(2000, 12, 31),
                SpreadsheetText.with("2000/12/31")
                        .setColor(
                                Optional.of(RED)
                        )
        );
    }

    @Test
    public void testParseAndFormatDateIncludesColorNumber() {
        this.parseFormatAndCheck(
                "[color44]yyyy/mm/dd",
                LocalDate.of(2000, 12, 31),
                SpreadsheetText.with("2000/12/31")
                        .setColor(
                                Optional.of(RED)
                        )
        );
    }

    private void parseFormatAndCheck(final String pattern,
                                     final LocalDate value,
                                     final SpreadsheetText text) {
        this.parseFormatAndCheck(
                pattern,
                value,
                new TestSpreadsheetFormatterContext() {

                    @Override
                    public boolean canConvert(final Object value,
                                              final Class<?> target) {
                        return Converters.localDateToLocalDateTime()
                                .canConvert(value, target, ConverterContexts.fake());
                    }

                    @Override
                    public <T> Either<T, String> convert(final Object value,
                                                         final Class<T> target) {
                        checkEquals(
                                LocalDateTime.class,
                                target,
                                "target"
                        );
                        return Converters.localDateToLocalDateTime()
                                .convert(
                                        value,
                                        target,
                                        ConverterContexts.fake()
                                );
                    }
                },
                text
        );
    }

    // Time.............................................................................................................

    @Test
    public void testParseAndFormatTime() {
        this.parseFormatAndCheck(
                "hh/mm/ss",
                LocalTime.of(12, 58, 59),
                SpreadsheetText.with("12/58/59")
        );
    }

    @Test
    public void testParseAndFormatTimeWithColorName() {
        this.parseFormatAndCheck(
                "[RED]hh/mm/ss",
                LocalTime.of(12, 58, 59),
                SpreadsheetText.with("12/58/59")
                        .setColor(
                                Optional.of(RED)
                        )
        );
    }

    private void parseFormatAndCheck(final String pattern,
                                     final LocalTime value,
                                     final SpreadsheetText text) {
        this.parseFormatAndCheck(
                pattern,
                value,
                new TestSpreadsheetFormatterContext() {

                    @Override
                    public boolean canConvert(final Object value,
                                              final Class<?> target) {
                        return value instanceof LocalTime && target == LocalDateTime.class;
                    }

                    @Override
                    public <T> Either<T, String> convert(final Object value,
                                                         final Class<T> target) {
                        checkEquals(
                                LocalDateTime.class,
                                target,
                                "target"
                        );

                        return this.successfulConversion(
                                target.cast(
                                        LocalDateTime.of(
                                                LocalDate.of(2000, 10, 31),
                                                (LocalTime) value
                                        )
                                ),
                                target
                        );
                    }
                },
                text
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
        this.formatAndCheck(this.createFormatter(pattern),
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
        return SpreadsheetFormatParsers.dateTimeFormat()
                .transform((v, c) -> v.cast(SequenceParserToken.class).value().get(0));
    }

    //toString .......................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createFormatter(), this.pattern());
    }

    @Override
    SpreadsheetPatternSpreadsheetFormatterDateTime createFormatter0(final SpreadsheetFormatDateTimeParserToken token) {
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
        return LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.ENGLISH));
    }

    @Override
    public Class<SpreadsheetPatternSpreadsheetFormatterDateTime> type() {
        return SpreadsheetPatternSpreadsheetFormatterDateTime.class;
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
        final SpreadsheetFormatDateTimeParserToken token = this.parsePatternOrFail("dd/mm/yyyy");

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
}

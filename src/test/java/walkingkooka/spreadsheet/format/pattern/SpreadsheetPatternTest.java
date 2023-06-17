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

package walkingkooka.spreadsheet.format.pattern;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.Either;
import walkingkooka.InvalidCharacterException;
import walkingkooka.collect.list.Lists;
import walkingkooka.color.Color;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.net.UrlFragment;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.format.FakeSpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterTesting;
import walkingkooka.spreadsheet.format.SpreadsheetText;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenKind;
import walkingkooka.spreadsheet.parser.FakeSpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetDateParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetDateTimeParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetNumberParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetTimeParserToken;
import walkingkooka.text.CharSequences;
import walkingkooka.text.HasTextTesting;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserTesting;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.FakeExpressionEvaluationContext;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetPatternTest implements ClassTesting2<SpreadsheetPattern>,
        ParserTesting,
        HasTextTesting,
        SpreadsheetFormatterTesting {

    private final static Color COLOR = Color.BLACK;

    // static factory method Locale.....................................................................................

    @Test
    public void testDateFormatPatternLocaleNullLocaleFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetPattern.dateFormatPatternLocale(null)
        );
    }

    @Test
    public void testDateParsePatternLocaleNullLocaleFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetPattern.dateParsePatternLocale(null)
        );
    }

    @Test
    public void testDateTimeFormatPatternLocaleNullLocaleFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetPattern.dateTimeFormatPatternLocale(null)
        );
    }

    @Test
    public void testDateTimeParsePatternLocaleNullLocaleFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetPattern.dateTimeParsePatternLocale(null)
        );
    }

    @Test
    public void testTimeFormatPatternLocaleNullLocaleFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetPattern.timeFormatPatternLocale(null)
        );
    }

    @Test
    public void testTimeParsePatternLocaleNullLocaleFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetPattern.timeParsePatternLocale(null)
        );
    }

    private final static Locale EN_AU = Locale.forLanguageTag("EN-AU");

    @Test
    public void testDateFormatPatternLocale() {
        this.formatPatternFormatAndCheck(
                SpreadsheetPattern.dateFormatPatternLocale(EN_AU),
                LocalDate.of(2000, 12, 31),
                "Sunday, 31 December 2000"
        );
    }

    @Test
    public void testDateTimeFormatPatternLocale() {
        this.formatPatternFormatAndCheck(
                SpreadsheetPattern.dateTimeFormatPatternLocale(EN_AU),
                LocalDateTime.of(2000, 12, 31, 12, 58),
                "Sunday, 31 December 2000 at 12:58:00 PM"
        );
    }

    @Test
    public void testTimeFormatPatternLocale() {
        this.formatPatternFormatAndCheck(
                SpreadsheetPattern.timeFormatPatternLocale(EN_AU),
                LocalTime.of(12, 58, 59),
                "12:58:59 PM"
        );
    }

    private <T> void formatPatternFormatAndCheck(final SpreadsheetFormatPattern formatPattern,
                                                 final T value,
                                                 final String formattedText) {
        this.formatPatternFormatAndCheck(
                formatPattern,
                value,
                SpreadsheetText.with(formattedText)
        );
    }

    private <T> void formatPatternFormatAndCheck(final SpreadsheetFormatPattern formatPattern,
                                                 final T value,
                                                 final SpreadsheetText formattedText) {
        this.formatAndCheck(
                formatPattern.formatter(),
                value,
                new FakeSpreadsheetFormatterContext() {

                    @Override
                    public boolean canConvert(final Object value,
                                              final Class<?> type) {
                        return this.convert(value, type)
                                .isLeft();
                    }

                    @Override
                    public <TT> Either<TT, String> convert(final Object value,
                                                           final Class<TT> target) {
                        if (target == LocalDateTime.class) {
                            if (value instanceof LocalDate) {
                                return Converters.localDateLocalDateTime().convert(value, target, ConverterContexts.fake());
                            }
                            if (value instanceof LocalDateTime) {
                                return this.successfulConversion(
                                        value,
                                        target
                                );
                            }
                            if (value instanceof LocalTime) {
                                return Converters.localTimeLocalDateTime().convert(value, target, ConverterContexts.fake());
                            }
                        }

                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public String ampm(final int hourOfDay) {
                        return this.dateTimeContext().ampm(hourOfDay);
                    }

                    @Override
                    public String weekDayName(final int day) {
                        return this.dateTimeContext().weekDayName(day);
                    }

                    @Override
                    public String monthName(int month) {
                        return this.dateTimeContext().monthName(month);
                    }

                    private DateTimeContext dateTimeContext() {
                        return DateTimeContexts.locale(
                                EN_AU,
                                1900,
                                50,
                                LocalDateTime::now
                        );
                    }

                    @Override
                    public Optional<Color> colorName(final SpreadsheetColorName name) {
                        return formattedText.color();
                    }

                },
                formattedText
        );
    }

    // Locale parse Date................................................................................................

    @Test
    public void testDateParsePatternLocaleDayMonthNumberTwoDigitYear() {
        this.dateParsePatternLocaleParseAndCheck(
                "31/12/00",
                LocalDate.of(2000, 12, 31)
        );
    }

    @Test
    public void testDateParsePatternLocaleDayMonthNumberFourDigitYear() {
        this.dateParsePatternLocaleParseAndCheck(
                "31/12/2000",
                LocalDate.of(2000, 12, 31)
        );
    }

    @Test
    public void testDateParsePatternLocaleDayMonthNameTwoDigitYear() {
        this.dateParsePatternLocaleParseAndCheck(
                "31 December 00",
                LocalDate.of(2000, 12, 31)
        );
    }

    @Test
    public void testDateParsePatternLocaleDayMonthNameFourDigitYear() {
        this.dateParsePatternLocaleParseAndCheck(
                "31 December 2000",
                LocalDate.of(2000, 12, 31)
        );
    }

    private void dateParsePatternLocaleParseAndCheck(final String text,
                                                     final LocalDate expected) {
        this.parsePatternAndCheck(
                SpreadsheetPattern.dateParsePatternLocale(EN_AU),
                text,
                (t, c) -> t.cast(SpreadsheetDateParserToken.class).toLocalDate(c),
                expected
        );
    }

    // Locale parse DateTime............................................................................................

    @Test
    public void testDateTimeParsePatternLocaleDayNumberTwoDigitYearHourMinute() {
        this.dateTimeParsePatternParseAndCheck(
                "31/12/00, 12:58",
                LocalDateTime.of(2000, 12, 31, 12, 58)
        );
    }

    @Test
    public void testDateTimeParsePatternLocaleDayNumberTwoDigitYearHourMinuteAmpm() {
        this.dateTimeParsePatternParseAndCheck(
                "31/12/00, 11:58 PM",
                LocalDateTime.of(2000, 12, 31, 23, 58)
        );
    }

    @Test
    public void testDateTimeParsePatternLocaleDayNumberTwoDigitYearHourMinuteSecond() {
        this.dateTimeParsePatternParseAndCheck(
                "31/12/00, 12:58:59",
                LocalDateTime.of(2000, 12, 31, 12, 58, 59)
        );
    }

    @Test
    public void testDateTimeParsePatternLocaleDayNumberTwoDigitYearHourMinuteSecondMillis5() {
        this.dateTimeParsePatternParseAndCheck(
                "31/12/00, 12:58:59.5",
                LocalDateTime.of(2000, 12, 31, 12, 58, 59, 500000000)
        );
    }

    @Test
    public void testDateTimeParsePatternLocaleDayNumberTwoDigitYearHourMinuteSecondMillis123() {
        this.dateTimeParsePatternParseAndCheck(
                "31/12/00, 12:58:59.123",
                LocalDateTime.of(2000, 12, 31, 12, 58, 59, 123000000)
        );
    }

    @Test
    public void testDateTimeParsePatternLocaleDayNumberTwoDigitYearHourMinuteSecondAmpm() {
        this.dateTimeParsePatternParseAndCheck(
                "31/12/00, 11:58:59 PM",
                LocalDateTime.of(2000, 12, 31, 23, 58, 59)
        );
    }

    @Test
    public void testDateTimeParsePatternLocaleDayNumberFourDigitYearHourMinute() {
        this.dateTimeParsePatternParseAndCheck(
                "31/12/2000, 12:58",
                LocalDateTime.of(2000, 12, 31, 12, 58)
        );
    }

    @Test
    public void testDateTimeParsePatternLocaleDayNumberFourDigitYearHourMinuteAmpm() {
        this.dateTimeParsePatternParseAndCheck(
                "31/12/2000, 11:58 PM",
                LocalDateTime.of(2000, 12, 31, 23, 58)
        );
    }

    @Test
    public void testDateTimeParsePatternLocaleDayNumberFourDigitYearHourMinuteSecond() {
        this.dateTimeParsePatternParseAndCheck(
                "31/12/2000, 12:58:59",
                LocalDateTime.of(2000, 12, 31, 12, 58, 59)
        );
    }

    @Test
    public void testDateTimeParsePatternLocaleDayNumberFourDigitYearHourMinuteSecondMilli1() {
        this.dateTimeParsePatternParseAndCheck(
                "31/12/2000, 12:58:59.1",
                LocalDateTime.of(2000, 12, 31, 12, 58, 59, 100000000)
        );
    }

    @Test
    public void testDateTimeParsePatternLocaleDayNumberFourDigitYearHourMinuteSecondMilli123() {
        this.dateTimeParsePatternParseAndCheck(
                "31/12/2000, 12:58:59.123",
                LocalDateTime.of(2000, 12, 31, 12, 58, 59, 123000000)
        );
    }

    @Test
    public void testDateTimeParsePatternLocaleDayNumberFourDigitYearHourMinuteSecondAmpm() {
        this.dateTimeParsePatternParseAndCheck(
                "31/12/2000, 11:58:59 PM",
                LocalDateTime.of(2000, 12, 31, 23, 58, 59)
        );
    }

    private void dateTimeParsePatternParseAndCheck(final String text,
                                                   final LocalDateTime expected) {
        this.parsePatternAndCheck(
                SpreadsheetPattern.dateTimeParsePatternLocale(EN_AU),
                text,
                (t, c) -> t.cast(SpreadsheetDateTimeParserToken.class).toLocalDateTime(c),
                expected
        );
    }

    // Locale parse Time................................................................................................

    @Test
    public void testTimeParsePatternLocaleHourMinute() {
        this.timeParsePatternLocaleAndCheck(
                "12:58",
                LocalTime.of(12, 58)
        );
    }

    @Test
    public void testTimeParsePatternLocaleHourMinuteSecond() {
        this.timeParsePatternLocaleAndCheck(
                "12:58:59",
                LocalTime.of(12, 58, 59)
        );
    }

    // https://github.com/mP1/walkingkooka-spreadsheet/issues/1442
// SpreadsheetPattern from Locale needs permutation with millis (added to seconds)
    @Test
    @Disabled("https://github.com/mP1/walkingkooka-spreadsheet/issues/1442")
    public void testTimeParsePatternLocaleHourMinuteSecondMillis() {
        this.timeParsePatternLocaleAndCheck(
                "12:58:59.123",
                LocalTime.of(12, 58, 59, 1230000)
        );
    }

    @Test
    public void testTimeParsePatternLocaleHourMinuteAmpm() {
        this.timeParsePatternLocaleAndCheck(
                "11:58 PM",
                LocalTime.of(23, 58)
        );
    }

    @Test
    public void testTimeParsePatternLocaleHourMinuteSecondAmpm() {
        this.timeParsePatternLocaleAndCheck(
                "11:58:59 PM",
                LocalTime.of(23, 58, 59)
        );
    }

    private void timeParsePatternLocaleAndCheck(final String text,
                                                final LocalTime expected) {
        this.parsePatternAndCheck(
                SpreadsheetPattern.timeParsePatternLocale(EN_AU),
                text,
                (t, c) -> t.cast(SpreadsheetTimeParserToken.class).toLocalTime(),
                expected
        );
    }

    private <T> void parsePatternAndCheck(final SpreadsheetParsePattern pattern,
                                          final String text,
                                          final BiFunction<ParserToken, ExpressionEvaluationContext, T> tokenToValue,
                                          final T expected) {
        final Parser<SpreadsheetParserContext> parser = pattern.parser();
        final TextCursor cursor = TextCursors.charSequence(text);

        final DateTimeContext dateTimeContext = DateTimeContexts.locale(
                EN_AU,
                1800,
                50,
                LocalDateTime::now
        );

        this.checkEquals(
                expected,
                parser.parse(
                                cursor,
                                new FakeSpreadsheetParserContext() {
                                    @Override
                                    public List<String> ampms() {
                                        return dateTimeContext.ampms();
                                    }

                                    @Override
                                    public char decimalSeparator() {
                                        return '.';
                                    }

                                    @Override
                                    public List<String> monthNames() {
                                        return dateTimeContext.monthNames();
                                    }

                                    @Override
                                    public List<String> monthNameAbbreviations() {
                                        return dateTimeContext.monthNameAbbreviations();
                                    }
                                })
                        .map(t -> tokenToValue.apply(
                                t,
                                new FakeExpressionEvaluationContext() {

                                    @Override
                                    public int defaultYear() {
                                        return dateTimeContext.defaultYear();
                                    }

                                    @Override
                                    public int twoDigitYear() {
                                        return dateTimeContext.twoDigitYear();
                                    }

                                })
                        )
                        .orElse(null),
                () -> "parse " + CharSequences.quoteAndEscape(text) + " parser: " + parser
        );
    }

    // dateParsePattern................................................................................................

    @Test
    public void testDateParsePatternWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetPattern.dateParsePattern((SimpleDateFormat) null)
        );
    }

    @Test
    public void testDateParsePattern() {
        final String pattern = "yyyy/mm/dd";
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        final SpreadsheetDateParsePattern dateParsePattern = SpreadsheetPattern.dateParsePattern(simpleDateFormat);

        this.parseAndCheck(
                dateParsePattern.parser(),
                new FakeSpreadsheetParserContext(),
                "1999/12/31",
                SpreadsheetParserToken.date(
                        Lists.of(
                                SpreadsheetParserToken.year(1999, "1999"),
                                SpreadsheetParserToken.textLiteral("/", "/"),
                                SpreadsheetParserToken.monthNumber(12, "12"),
                                SpreadsheetParserToken.textLiteral("/", "/"),
                                SpreadsheetParserToken.dayNumber(31, "31")
                        ),
                        "1999/12/31"
                ),
                "1999/12/31"
        );
    }

    // parseDateFormatPattern...........................................................................................

    @Test
    public void testParseDateFormatPatternNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetPattern.parseDateFormatPattern(null)
        );
    }

    @Test
    public void testParseDateFormatPatternIncompleteTextLiteralFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetPattern.parseDateFormatPattern("dd/mm/yyyy\"Incomplete")
        );
    }

    @Test
    public void testParseDateFormatPattern() {
        this.formatPatternFormatAndCheck(
                SpreadsheetPattern.parseDateFormatPattern("dd/mm/yyyy"),
                LocalDate.of(1999, 12, 31),
                "31/12/1999"
        );
    }

    @Test
    public void testParseDateFormatPatternSeveral() {
        this.formatPatternFormatAndCheck(
                SpreadsheetPattern.parseDateFormatPattern("dd/mm/yyyy;\"Ignored\";"),
                LocalDate.of(1999, 12, 31),
                "31/12/1999"
        );
    }

    @Test
    public void testParseDateFormatPatternColor() {
        this.formatPatternFormatAndCheck(
                SpreadsheetPattern.parseDateFormatPattern("[BLACK]dd/mm/yyyy"),
                LocalDate.of(1999, 12, 31),
                SpreadsheetText.with("31/12/1999")
                        .setColor(Optional.of(COLOR))
        );
    }

    @Test
    public void testParseDateFormatPatternWithGeneral() {
        final LocalDate date = LocalDate.of(1999, 12, 31);

        this.formatAndCheck(
                SpreadsheetPattern.parseDateFormatPattern("GENERAL").formatter(),
                date,
                new FakeSpreadsheetFormatterContext() {
                    @Override
                    public boolean canConvert(final Object value,
                                              final Class<?> type) {
                        return value.equals(date) && type == ExpressionNumber.class;
                    }

                    @Override
                    public <T> Either<T, String> convert(final Object value,
                                                         final Class<T> target) {
                        if (value instanceof ExpressionNumber && target == BigDecimal.class) {
                            return this.successfulConversion(
                                    ExpressionNumber.class.cast(value).bigDecimal(),
                                    target
                            );
                        }
                        if (value instanceof ExpressionNumber && target == ExpressionNumber.class) {
                            return this.successfulConversion(
                                    value,
                                    target
                            );
                        }

                        this.canConvertOrFail(value, target);
                        return this.successfulConversion(
                                ExpressionNumberKind.BIG_DECIMAL.create(1234),
                                target
                        );
                    }

                    @Override
                    public char decimalSeparator() {
                        return '.';
                    }

                    @Override
                    public MathContext mathContext() {
                        return MathContext.DECIMAL32;
                    }
                },
                "1234"
        );
    }

    // parseDateParsePattern...........................................................................................

    @Test
    public void testParseDateParsePatternParseNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetPattern.parseDateParsePattern(null)
        );
    }

    @Test
    public void testParseDateParsePatternIncompleteTextLiteralFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetPattern.parseDateParsePattern("dd/mm/yyyy\"Incomplete")
        );
    }

    @Test
    public void testParseDateParsePatternColorFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetPattern.parseDateParsePattern("[BLACK]dd/mm/yyyy")
        );
    }

    @Test
    public void testParseDateParsePatternGeneralFails() {
        assertThrows(
                InvalidCharacterException.class,
                () -> SpreadsheetPattern.parseDateParsePattern("General")
        );
    }

    @Test
    public void testParseDateParsePattern() {
        this.parseAndCheck(
                SpreadsheetPattern.parseDateParsePattern("yyyy/mm/dd").parser(),
                new FakeSpreadsheetParserContext(),
                "1999/12/31",
                SpreadsheetParserToken.date(
                        Lists.of(
                                SpreadsheetParserToken.year(1999, "1999"),
                                SpreadsheetParserToken.textLiteral("/", "/"),
                                SpreadsheetParserToken.monthNumber(12, "12"),
                                SpreadsheetParserToken.textLiteral("/", "/"),
                                SpreadsheetParserToken.dayNumber(31, "31")
                        ),
                        "1999/12/31"
                ),
                "1999/12/31"
        );
    }

    // dateTimeParsePattern.............................................................................................

    @Test
    public void testDateTimeParsePatternWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetPattern.dateTimeParsePattern((SimpleDateFormat) null)
        );
    }

    @Test
    public void testDateTimeParsePattern() {
        final String pattern = "yyyy/MM/dd HH:mm";
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        final SpreadsheetDateTimeParsePattern dateTimeParsePattern = SpreadsheetPattern.dateTimeParsePattern(simpleDateFormat);

        this.parseAndCheck(
                dateTimeParsePattern.parser(),
                new FakeSpreadsheetParserContext(),
                "1999/12/31 12:58",
                SpreadsheetParserToken.dateTime(
                        Lists.of(
                                SpreadsheetParserToken.year(1999, "1999"),
                                SpreadsheetParserToken.textLiteral("/", "/"),
                                SpreadsheetParserToken.monthNumber(12, "12"),
                                SpreadsheetParserToken.textLiteral("/", "/"),
                                SpreadsheetParserToken.dayNumber(31, "31"),
                                SpreadsheetParserToken.whitespace(" ", " "),
                                SpreadsheetParserToken.hour(12, "12"),
                                SpreadsheetParserToken.textLiteral(":", ":"),
                                SpreadsheetParserToken.minute(58, "58")
                        ),
                        "1999/12/31 12:58"
                ),
                "1999/12/31 12:58"
        );
    }

    // parseDateTimeFormatPattern.......................................................................................

    @Test
    public void testParseDateTimeFormatPatternParseNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetPattern.parseDateTimeFormatPattern(null)
        );
    }

    @Test
    public void testParseDateTimeFormatPatternIncompleteTextLiteralFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetPattern.parseDateTimeFormatPattern("dd/mm/yyyy\"Incomplete")
        );
    }

    @Test
    public void testParseDateTimeFormatPattern() {
        this.formatPatternFormatAndCheck(
                SpreadsheetPattern.parseDateTimeFormatPattern("dd/mm/yyyy hh/mm/ss \"Hello\""),
                LocalDateTime.of(1999, 12, 31, 12, 58, 59),
                "31/12/1999 12/58/59 Hello"
        );
    }

    @Test
    public void testParseDateTimeFormatPatternSeveral() {
        this.formatPatternFormatAndCheck(
                SpreadsheetPattern.parseDateTimeFormatPattern("dd/mm/yyyy hh/mm/ss \"Hello\";\"Ignored\""),
                LocalDateTime.of(1999, 12, 31, 12, 58, 59),
                "31/12/1999 12/58/59 Hello"
        );
    }

    @Test
    public void testParseDateTimeFormatPatternColor() {
        this.formatPatternFormatAndCheck(
                SpreadsheetPattern.parseDateTimeFormatPattern("[BLACK]dd/mm/yyyy hh/mm/ss \"Hello\""),
                LocalDateTime.of(1999, 12, 31, 12, 58, 59),
                SpreadsheetText.with("31/12/1999 12/58/59 Hello")
                        .setColor(Optional.of(COLOR))
        );
    }

    @Test
    public void testParseDateTimeFormatPatternWithGeneral() {
        final LocalDateTime dateTime = LocalDateTime.of(1999, 12, 31, 12, 58, 59);

        this.formatAndCheck(
                SpreadsheetPattern.parseDateTimeFormatPattern("GENERAL").formatter(),
                dateTime,
                new FakeSpreadsheetFormatterContext() {
                    @Override
                    public boolean canConvert(final Object value,
                                              final Class<?> type) {
                        return value.equals(dateTime) && type == ExpressionNumber.class;
                    }

                    @Override
                    public <T> Either<T, String> convert(final Object value,
                                                         final Class<T> target) {
                        if (value instanceof ExpressionNumber && target == BigDecimal.class) {
                            return this.successfulConversion(
                                    ExpressionNumber.class.cast(value).bigDecimal(),
                                    target
                            );
                        }
                        if (value instanceof ExpressionNumber && target == ExpressionNumber.class) {
                            return this.successfulConversion(
                                    value,
                                    target
                            );
                        }

                        this.canConvertOrFail(value, target);
                        return this.successfulConversion(
                                ExpressionNumberKind.BIG_DECIMAL.create(1234),
                                target
                        );
                    }

                    @Override
                    public char decimalSeparator() {
                        return '.';
                    }

                    @Override
                    public MathContext mathContext() {
                        return MathContext.DECIMAL32;
                    }
                },
                "1234"
        );
    }

    // DateTimeParsePattern............................................................................................

    @Test
    public void testParseDateTimeParsePatternParseNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetPattern.parseDateTimeParsePattern(null)
        );
    }

    @Test
    public void testParseDateTimeParsePatternIncompleteTextLiteralFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetPattern.parseDateTimeParsePattern("dd/mm/yyyy hh:mm:ss\"Incomplete")
        );
    }

    @Test
    public void testParseDateTimeParsePatternColorFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetPattern.parseDateTimeParsePattern("[BLACK]dd/mm/yyyy hh:mm:ss")
        );
    }

    @Test
    public void testParseDateTimeParsePatternGeneralFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetPattern.parseDateTimeParsePattern("General")
        );
    }

    @Test
    public void testParseDateTimeParsePattern() {
        this.parseAndCheck(
                SpreadsheetPattern.parseDateTimeParsePattern("yyyy/mm/dd hh:mm").parser(),
                new FakeSpreadsheetParserContext(),
                "1999/12/31 12:58",
                SpreadsheetParserToken.dateTime(
                        Lists.of(
                                SpreadsheetParserToken.year(1999, "1999"),
                                SpreadsheetParserToken.textLiteral("/", "/"),
                                SpreadsheetParserToken.monthNumber(12, "12"),
                                SpreadsheetParserToken.textLiteral("/", "/"),
                                SpreadsheetParserToken.dayNumber(31, "31"),
                                SpreadsheetParserToken.whitespace(" ", " "),
                                SpreadsheetParserToken.hour(12, "12"),
                                SpreadsheetParserToken.textLiteral(":", ":"),
                                SpreadsheetParserToken.minute(58, "58")
                        ),
                        "1999/12/31 12:58"
                ),
                "1999/12/31 12:58"
        );
    }

    // parseNumberTimeFormatPattern.....................................................................................

    @Test
    public void testParseNumberFormatPatternParseNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetPattern.parseNumberFormatPattern(null)
        );
    }

    @Test
    public void testParseNumberFormatPatternIncompleteTextLiteralFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetPattern.parseNumberFormatPattern("#\"Incomplete")
        );
    }

    @Test
    public void testParseNumberFormatPattern() {
        this.formatAndCheck(
                SpreadsheetPattern.parseNumberFormatPattern("0.00 \"Hello\"").formatter(),
                ExpressionNumberKind.DOUBLE.create(1.5),
                new FakeSpreadsheetFormatterContext() {

                    @Override
                    public <T> Either<T, String> convert(final Object value,
                                                         final Class<T> target) {
                        return this.successfulConversion(
                                ExpressionNumber.class.cast(value),
                                target
                        );
                    }

                    @Override
                    public char decimalSeparator() {
                        return '.';
                    }

                    @Override
                    public MathContext mathContext() {
                        return MathContext.DECIMAL128;
                    }
                },
                "1.50 Hello"
        );
    }

    @Test
    public void testParseNumberFormatPatternSeveral() {
        this.formatAndCheck(
                SpreadsheetPattern.parseNumberFormatPattern("0.00 \"Hello\";").formatter(),
                ExpressionNumberKind.BIG_DECIMAL.create(1.5),
                new FakeSpreadsheetFormatterContext() {

                    @Override
                    public <T> Either<T, String> convert(final Object value,
                                                         final Class<T> target) {
                        return this.successfulConversion(
                                ExpressionNumber.class.cast(value),
                                target
                        );
                    }

                    @Override
                    public char decimalSeparator() {
                        return '.';
                    }

                    @Override
                    public MathContext mathContext() {
                        return MathContext.DECIMAL128;
                    }
                },
                "1.50 Hello"
        );
    }

    @Test
    public void testParseNumberFormatPatternWithNumberIncludesPercent() {
        this.formatAndCheck(
                SpreadsheetPattern.parseNumberFormatPattern("0.0 \"Hello\"").formatter(),
                ExpressionNumberKind.DOUBLE.create(1.5),
                new FakeSpreadsheetFormatterContext() {

                    @Override
                    public <T> Either<T, String> convert(final Object value,
                                                         final Class<T> target) {
                        return this.successfulConversion(
                                ExpressionNumber.class.cast(value),
                                target
                        );
                    }

                    @Override
                    public char decimalSeparator() {
                        return '.';
                    }

                    @Override
                    public MathContext mathContext() {
                        return MathContext.DECIMAL128;
                    }
                },
                "1.5 Hello"
        );
    }

    @Test
    public void testParseNumberFormatPatternColor() {
        this.formatAndCheck(
                SpreadsheetPattern.parseNumberFormatPattern("[BLACK]0.0 \"Hello\"").formatter(),
                ExpressionNumberKind.DOUBLE.create(1.5),
                new FakeSpreadsheetFormatterContext() {

                    @Override
                    public <T> Either<T, String> convert(final Object value,
                                                         final Class<T> target) {
                        return this.successfulConversion(
                                ExpressionNumber.class.cast(value),
                                target
                        );
                    }

                    @Override
                    public char decimalSeparator() {
                        return '.';
                    }

                    @Override
                    public MathContext mathContext() {
                        return MathContext.DECIMAL128;
                    }

                    @Override
                    public Optional<Color> colorName(final SpreadsheetColorName name) {
                        return Optional.of(COLOR);
                    }
                },
                SpreadsheetText.with("1.5 Hello")
                        .setColor(Optional.of(COLOR))
        );
    }

    @Test
    public void testParseNumberFormatPatternWithGeneral() {
        final ExpressionNumber number = ExpressionNumberKind.BIG_DECIMAL.create(1.5);

        this.formatAndCheck(
                SpreadsheetPattern.parseNumberFormatPattern("GENERAL").formatter(),
                number,
                new FakeSpreadsheetFormatterContext() {
                    @Override
                    public boolean canConvert(final Object value,
                                              final Class<?> type) {
                        return value.equals(number) && type == ExpressionNumber.class;
                    }

                    @Override
                    public <T> Either<T, String> convert(final Object value,
                                                         final Class<T> target) {
                        this.canConvertOrFail(value, target);

                        return this.successfulConversion(
                                ExpressionNumber.class.cast(value),
                                target
                        );
                    }

                    @Override
                    public char decimalSeparator() {
                        return '.';
                    }

                    @Override
                    public MathContext mathContext() {
                        return MathContext.DECIMAL32;
                    }
                },
                "1.5"
        );
    }

    @Test
    public void testParseNumberFormatPatternWithPercent() {
        this.formatAndCheck(
                SpreadsheetPattern.parseNumberFormatPattern("0.0% \"Hello\"").formatter(),
                ExpressionNumberKind.DOUBLE.create(1.005),
                new FakeSpreadsheetFormatterContext() {

                    @Override
                    public <T> Either<T, String> convert(final Object value,
                                                         final Class<T> target) {
                        return this.successfulConversion(
                                ExpressionNumber.class.cast(value),
                                target
                        );
                    }

                    @Override
                    public char decimalSeparator() {
                        return '.';
                    }

                    @Override
                    public MathContext mathContext() {
                        return MathContext.DECIMAL128;
                    }

                    @Override
                    public char percentageSymbol() {
                        return '%';
                    }
                },
                "100.5% Hello"
        );
    }

    @Test
    public void testParseNumberFormatPatternWithPercentCustomPercentSymbol() {
        this.formatAndCheck(
                SpreadsheetPattern.parseNumberFormatPattern("0.0% \"Hello\"").formatter(),
                ExpressionNumberKind.DOUBLE.create(1.005),
                new FakeSpreadsheetFormatterContext() {

                    @Override
                    public <T> Either<T, String> convert(final Object value,
                                                         final Class<T> target) {
                        return this.successfulConversion(
                                ExpressionNumber.class.cast(value),
                                target
                        );
                    }

                    @Override
                    public char decimalSeparator() {
                        return '.';
                    }

                    @Override
                    public MathContext mathContext() {
                        return MathContext.DECIMAL128;
                    }

                    @Override
                    public char percentageSymbol() {
                        return '!';
                    }
                },
                "100.5! Hello"
        );
    }

    // parseNumberParsePattern..........................................................................................

    @Test
    public void testParseNumberParsePatternParseNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetPattern.parseNumberFormatPattern(null)
        );
    }

    @Test
    public void testParseNumberParsePatternIncompleteTextLiteralFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetPattern.parseNumberParsePattern("#\"Incomplete")
        );
    }

    @Test
    public void testParseNumberParsePatternColorFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetPattern.parseNumberParsePattern("[BLACK]#.#")
        );
    }

    @Test
    public void testParseNumberParsePatternGeneralFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetPattern.parseNumberParsePattern("General")
        );
    }

    @Test
    public void testParseNumberParsePattern() {
        this.parseAndCheck(
                SpreadsheetPattern.parseNumberParsePattern("#.#").parser(),
                new FakeSpreadsheetParserContext() {
                    @Override
                    public char decimalSeparator() {
                        return '.';
                    }

                    @Override
                    public char groupSeparator() {
                        return ',';
                    }

                    @Override
                    public char negativeSign() {
                        return '-';
                    }

                    @Override
                    public char positiveSign() {
                        return '+';
                    }
                },
                "1.5",
                SpreadsheetParserToken.number(
                        Lists.of(
                                SpreadsheetParserToken.digits("1", "1"),
                                SpreadsheetParserToken.decimalSeparatorSymbol(".", "."),
                                SpreadsheetParserToken.digits("5", "5")
                        ),
                        "1.5"
                ),
                "1.5"
        );
    }

    @Test
    public void testParseNumberParsePatternPercent() {
        this.parseAndCheck(
                SpreadsheetPattern.parseNumberParsePattern("#.#%").parser(),
                new FakeSpreadsheetParserContext() {
                    @Override
                    public char decimalSeparator() {
                        return '.';
                    }

                    @Override
                    public char groupSeparator() {
                        return ',';
                    }

                    @Override
                    public char negativeSign() {
                        return '-';
                    }

                    @Override
                    public char percentageSymbol() {
                        return '*';
                    }

                    @Override
                    public char positiveSign() {
                        return '+';
                    }
                },
                "1.5*",
                SpreadsheetParserToken.number(
                        Lists.of(
                                SpreadsheetParserToken.digits("1", "1"),
                                SpreadsheetParserToken.decimalSeparatorSymbol(".", "."),
                                SpreadsheetParserToken.digits("5", "5"),
                                SpreadsheetParserToken.percentSymbol("*", "*")
                        ),
                        "1.5*"
                ),
                "1.5*"
        );
    }

    // DecimalFormat....................................................................................................

    @Test
    public void testDecimalFormatWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetPattern.decimalFormat(null)
        );
    }

    @Test
    public void testDecimalFormatWithSingleQuoteTextLiteralFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetPattern.decimalFormat(
                        new DecimalFormat("'hello'#")
                )
        );
    }

    @Test
    public void testDecimalFormatFormat() throws Exception {
        this.decimalFormatParseAndCheck(
                "#.##",
                "1.25",
                1.25
        );
    }

    @Test
    public void testDecimalFormatCurrency() throws Exception {
        this.decimalFormatParseAndCheck(
                "$#.##",
                "$1.25",
                1.25
        );
    }

    @Test
    public void testDecimalFormatIncludesSpaces() throws Exception {
        this.decimalFormatParseAndCheck(
                " #.##",
                " 1.25",
                1.25
        );
    }

    private void decimalFormatParseAndCheck(final String pattern,
                                            final String text,
                                            final double expected) throws Exception {
        final DecimalFormat decimalFormat = new DecimalFormat(pattern);

        this.checkEquals(
                expected,
                decimalFormat.parse(text),
                () -> pattern + " parse " + CharSequences.quoteAndEscape(text)
        );

        final Parser<SpreadsheetParserContext> parser = SpreadsheetPattern.decimalFormat(decimalFormat)
                .parser();
        final ExpressionNumberKind kind = ExpressionNumberKind.DOUBLE;

        final SpreadsheetNumberParserToken token = parser.andEmptyTextCursor()
                .parse(
                        TextCursors.charSequence(text),
                        SpreadsheetParserContexts.basic(
                                DateTimeContexts.fake(),
                                DecimalNumberContexts.american(MathContext.DECIMAL32),
                                kind,
                                ','
                        )
                ).get()
                .cast(SpreadsheetNumberParserToken.class);

        this.checkEquals(
                kind.create(expected),
                token.toNumber(
                        new FakeExpressionEvaluationContext() {
                            @Override
                            public ExpressionNumberKind expressionNumberKind() {
                                return kind;
                            }
                        }
                )
        );
    }

    // parseTextFormatPattern..........................................................................................

    @Test
    public void testParseTextFormatPatternParseNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetPattern.parseTextFormatPattern(null)
        );
    }

    @Test
    public void testParseTextFormatPatternGeneralFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetPattern.parseTextFormatPattern("General")
        );
    }

    @Test
    public void testParseTextFormatPatternIncompleteTextLiteralFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetPattern.parseTextFormatPattern("@\"Incomplete")
        );
    }

    @Test
    public void testParseTextFormatPattern() {
        this.formatAndCheck(
                SpreadsheetPattern.parseTextFormatPattern("@@ \"Hello\"").formatter(),
                "Banana",
                new FakeSpreadsheetFormatterContext() {
                    @Override
                    public boolean canConvert(final Object value,
                                              final Class<?> type) {
                        return String.class == type;
                    }

                    @Override
                    public <T> Either<T, String> convert(final Object value,
                                                         final Class<T> target) {
                        return this.successfulConversion(value, target);
                    }
                },
                "BananaBanana Hello"
        );
    }

    @Test
    public void testParseTextFormatPatternColor() {
        this.formatAndCheck(
                SpreadsheetPattern.parseTextFormatPattern("[RED]@@ \"Hello\"").formatter(),
                "Banana",
                new FakeSpreadsheetFormatterContext() {
                    @Override
                    public boolean canConvert(final Object value,
                                              final Class<?> type) {
                        return String.class == type;
                    }

                    @Override
                    public <T> Either<T, String> convert(final Object value,
                                                         final Class<T> target) {
                        return this.successfulConversion(value, target);
                    }

                    @Override
                    public Optional<Color> colorName(final SpreadsheetColorName name) {
                        return Optional.of(COLOR);
                    }
                },
                SpreadsheetText.with("BananaBanana Hello").setColor(
                        Optional.of(COLOR)
                )
        );
    }

    // timeParsePattern................................................................................................

    @Test
    public void testTimeParsePatternWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetPattern.timeParsePattern((SimpleDateFormat) null)
        );
    }

    @Test
    public void testTimeParsePattern() {
        final String pattern = "HH/mm/ss";
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        final SpreadsheetTimeParsePattern timeParsePattern = SpreadsheetPattern.timeParsePattern(simpleDateFormat);

        this.parseAndCheck(
                timeParsePattern.parser(),
                new FakeSpreadsheetParserContext(),
                "12/58/59",
                SpreadsheetParserToken.time(
                        Lists.of(
                                SpreadsheetParserToken.hour(12, "12"),
                                SpreadsheetParserToken.textLiteral("/", "/"),
                                SpreadsheetParserToken.minute(58, "58"),
                                SpreadsheetParserToken.textLiteral("/", "/"),
                                SpreadsheetParserToken.seconds(59, "59")
                        ),
                        "12/58/59"
                ),
                "12/58/59"
        );
    }

    // parseTimeFormatPattern...........................................................................................

    @Test
    public void testParseTimeFormatPatternFormatNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetPattern.parseTimeFormatPattern(null)
        );
    }

    // https://github.com/mP1/walkingkooka-spreadsheet/issues/2909
    @Test
    public void testParseTimeFormatPatternGeneralShouldFail() {
        SpreadsheetPattern.parseTimeFormatPattern("General");
    }

    @Test
    public void testParseTimeFormatPatternIncompleteTextLiteralFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetPattern.parseTimeFormatPattern("hh:mm:ss\"Incomplete")
        );
    }

    @Test
    public void testParseTimeFormatPattern() {
        this.formatPatternFormatAndCheck(
                SpreadsheetPattern.parseTimeFormatPattern("hh/mm/ss"),
                LocalTime.of(12, 58, 59),
                "12/58/59"
        );
    }

    @Test
    public void testParseTimeFormatPatternSeveral() {
        this.formatPatternFormatAndCheck(
                SpreadsheetPattern.parseTimeFormatPattern("hh/mm/ss;\"Ignored\""),
                LocalTime.of(12, 58, 59),
                "12/58/59"
        );
    }

    @Test
    public void testParseTimeFormatPatternColor() {
        this.formatPatternFormatAndCheck(
                SpreadsheetPattern.parseTimeFormatPattern("[BLACK]hh/mm/ss"),
                LocalTime.of(12, 58, 59),
                SpreadsheetText.with("12/58/59")
                        .setColor(
                                Optional.of(COLOR)
                        )
        );
    }

    @Test
    public void testParseTimeFormatPatternWithGeneral() {
        final LocalTime time = LocalTime.of(12, 58, 59);

        this.formatAndCheck(
                SpreadsheetPattern.parseTimeFormatPattern("GENERAL").formatter(),
                time,
                new FakeSpreadsheetFormatterContext() {
                    @Override
                    public boolean canConvert(final Object value,
                                              final Class<?> type) {
                        return value.equals(time) && type == ExpressionNumber.class;
                    }

                    @Override
                    public <T> Either<T, String> convert(final Object value,
                                                         final Class<T> target) {
                        if (value instanceof ExpressionNumber && target == BigDecimal.class) {
                            return this.successfulConversion(
                                    ExpressionNumber.class.cast(value).bigDecimal(),
                                    target
                            );
                        }
                        if (value instanceof ExpressionNumber && target == ExpressionNumber.class) {
                            return this.successfulConversion(
                                    value,
                                    target
                            );
                        }

                        this.canConvertOrFail(value, target);
                        return this.successfulConversion(
                                ExpressionNumberKind.BIG_DECIMAL.create(1234),
                                target
                        );
                    }

                    @Override
                    public char decimalSeparator() {
                        return '.';
                    }

                    @Override
                    public MathContext mathContext() {
                        return MathContext.DECIMAL32;
                    }
                },
                "1234"
        );
    }

    // timeParsePattern.................................................................................................

    @Test
    public void testParseTimeParsePatternParseNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetPattern.parseTimeParsePattern(null)
        );
    }

    @Test
    public void testParseTimeParsePatternIncompleteTextLiteralFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetPattern.parseTimeParsePattern("hh:mm:ss\"Incomplete")
        );
    }

    @Test
    public void testParseTimeParsePatternColorFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetPattern.parseTimeParsePattern("[BLACK]hh:mm:ss")
        );
    }

    @Test
    public void testParseTimeParsePatternGeneralFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetPattern.parseTimeParsePattern("General")
        );
    }

    @Test
    public void testParseTimeParsePattern() {
        this.parseAndCheck(
                SpreadsheetPattern.parseTimeParsePattern("hh:mm").parser(),
                new FakeSpreadsheetParserContext(),
                "12:58",
                SpreadsheetParserToken.time(
                        Lists.of(
                                SpreadsheetParserToken.hour(12, "12"),
                                SpreadsheetParserToken.textLiteral(":", ":"),
                                SpreadsheetParserToken.minute(58, "58")
                        ),
                        "12:58"
                ),
                "12:58"
        );
    }

    // components......................................................................................................

    @Test
    public void testComponentsWithNullBiConsumerFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetPattern.parseTextFormatPattern("@").forEachComponent(null)
        );
    }

    @Test
    public void testComponentsDateFormat() {
        this.componentsAndCheck(
                SpreadsheetPattern.parseDateFormatPattern("dd/mm/yyyy"),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.DAY_WITH_LEADING_ZERO,
                        "dd"
                ),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.TEXT_LITERAL,
                        "/"
                ),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.MONTH_WITH_LEADING_ZERO,
                        "mm"
                ),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.TEXT_LITERAL,
                        "/"
                ),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.YEAR_FULL,
                        "yyyy"
                )
        );
    }

    @Test
    public void testComponentsDateTimeFormat() {
        this.componentsAndCheck(
                SpreadsheetPattern.parseDateTimeFormatPattern("dd/mm/yyyy hh:mm"),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.DAY_WITH_LEADING_ZERO,
                        "dd"
                ),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.TEXT_LITERAL,
                        "/"
                ),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.MONTH_WITH_LEADING_ZERO,
                        "mm"
                ),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.TEXT_LITERAL,
                        "/"
                ),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.YEAR_FULL,
                        "yyyy"
                ),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.TEXT_LITERAL,
                        " "
                ),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.HOUR_WITH_LEADING_ZERO,
                        "hh"
                ),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.TEXT_LITERAL,
                        ":"
                ),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.MINUTES_WITH_LEADING_ZERO,
                        "mm"
                )
        );
    }

    @Test
    public void testComponentsNumberFormat() {
        this.componentsAndCheck(
                SpreadsheetPattern.parseNumberFormatPattern("$#.00"),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.CURRENCY_SYMBOL,
                        "$"
                ),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.DIGIT,
                        "#"
                ),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.DECIMAL_PLACE,
                        "."
                ),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.DIGIT_ZERO,
                        "0"
                ),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.DIGIT_ZERO,
                        "0"
                )
        );
    }

    @Test
    public void testComponentsTextFormat() {
        this.componentsAndCheck(
                SpreadsheetPattern.parseTextFormatPattern("@\"Hello\""),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.TEXT_PLACEHOLDER,
                        "@"
                ),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.TEXT_LITERAL,
                        "\"Hello\""
                )
        );
    }

    @Test
    public void testComponentsTimeFormat() {
        this.componentsAndCheck(
                SpreadsheetPattern.parseTimeFormatPattern("hh:mm"),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.HOUR_WITH_LEADING_ZERO,
                        "hh"
                ),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.TEXT_LITERAL,
                        ":"
                ),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.MINUTES_WITH_LEADING_ZERO,
                        "mm"
                )
        );
    }

    @Test
    public void testComponentsDateParse() {
        this.componentsAndCheck(
                SpreadsheetPattern.parseDateParsePattern("dd/mm/yyyy"),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.DAY_WITH_LEADING_ZERO,
                        "dd"
                ),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.TEXT_LITERAL,
                        "/"
                ),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.MONTH_WITH_LEADING_ZERO,
                        "mm"
                ),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.TEXT_LITERAL,
                        "/"
                ),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.YEAR_FULL,
                        "yyyy"
                )
        );
    }

    @Test
    public void testComponentsDateTimeParse() {
        this.componentsAndCheck(
                SpreadsheetPattern.parseDateTimeParsePattern("dd/mm/yyyy hh:mm"),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.DAY_WITH_LEADING_ZERO,
                        "dd"
                ),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.TEXT_LITERAL,
                        "/"
                ),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.MONTH_WITH_LEADING_ZERO,
                        "mm"
                ),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.TEXT_LITERAL,
                        "/"
                ),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.YEAR_FULL,
                        "yyyy"
                ),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.TEXT_LITERAL,
                        " "
                ),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.HOUR_WITH_LEADING_ZERO,
                        "hh"
                ),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.TEXT_LITERAL,
                        ":"
                ),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.MINUTES_WITH_LEADING_ZERO,
                        "mm"
                )
        );
    }

    @Test
    public void testComponentsNumberParse() {
        this.componentsAndCheck(
                SpreadsheetPattern.parseNumberParsePattern("$#.00"),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.CURRENCY_SYMBOL,
                        "$"
                ),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.DIGIT,
                        "#"
                ),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.DECIMAL_PLACE,
                        "."
                ),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.DIGIT_ZERO,
                        "0"
                ),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.DIGIT_ZERO,
                        "0"
                )
        );
    }

    @Test
    public void testComponentsTimeParse() {
        this.componentsAndCheck(
                SpreadsheetPattern.parseTimeParsePattern("hh:mm"),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.HOUR_WITH_LEADING_ZERO,
                        "hh"
                ),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.TEXT_LITERAL,
                        ":"
                ),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.MINUTES_WITH_LEADING_ZERO,
                        "mm"
                )
        );
    }

    private void componentsAndCheck(final SpreadsheetPattern pattern,
                                    final Map.Entry<SpreadsheetFormatParserTokenKind, String>... expected) {
        final List<Map.Entry<SpreadsheetFormatParserTokenKind, String>> components = Lists.array();

        pattern.forEachComponent(
                (kind, text) -> components.add(
                        Map.entry(
                                kind,
                                text
                        )
                )
        );

        this.checkEquals(
                Lists.of(expected),
                components,
                () -> pattern + " components"
        );
    }

    // kind............................................................................................................

    @Test
    public void testKindDateParsePattern() {
        this.checkEquals(
                SpreadsheetPatternKind.DATE_PARSE_PATTERN,
                SpreadsheetPattern.parseDateParsePattern("dd/mm/yyyy").kind()
        );
    }

    @Test
    public void testKindDateTimeParsePattern() {
        this.checkEquals(
                SpreadsheetPatternKind.DATE_TIME_PARSE_PATTERN,
                SpreadsheetPattern.parseDateTimeParsePattern("dd/mm/yyyy hh:mm:ss").kind()
        );
    }

    @Test
    public void testKindNumberFormatPattern() {
        this.checkEquals(
                SpreadsheetPatternKind.NUMBER_FORMAT_PATTERN,
                SpreadsheetPattern.parseNumberFormatPattern("#.###").kind()
        );
    }

    @Test
    public void testKindTextFormatPattern() {
        this.checkEquals(
                SpreadsheetPatternKind.TEXT_FORMAT_PATTERN,
                SpreadsheetPattern.parseTextFormatPattern("@").kind()
        );
    }

    // HasText..........................................................................................................

    @Test
    public void testText() {
        final String text = "@\"Hello\"";
        this.textAndCheck(
                SpreadsheetPattern.parseTextFormatPattern(text),
                text
        );
    }

    @Test
    public void testText2() {
        final String text = "yyyy/mm//dd";
        this.textAndCheck(
                SpreadsheetPattern.parseDateTimeParsePattern(text),
                text
        );
    }

    // urlFragment......................................................................................................

    @Test
    public void testUrlFragmentDateParsePattern() {
        this.urlFragmentAndCheck(
                "dd/mm/yyyy",
                SpreadsheetPattern::parseDateParsePattern
        );
    }

    @Test
    public void testUrlFragmentDateTimeParsePattern() {
        this.urlFragmentAndCheck(
                "dd/mm/yyyy hh:mm:ss",
                SpreadsheetPattern::parseDateTimeParsePattern
        );
    }

    @Test
    public void testUrlFragmentNumberFormatPattern() {
        this.urlFragmentAndCheck(
                "#.###",
                SpreadsheetPattern::parseNumberFormatPattern
        );
    }

    @Test
    public void testUrlFragmentTextFormatPattern() {
        this.urlFragmentAndCheck(
                "@",
                SpreadsheetPattern::parseTextFormatPattern
        );
    }

    private void urlFragmentAndCheck(final String pattern,
                                     final Function<String, SpreadsheetPattern> factory) {
        this.checkEquals(
                UrlFragment.with(pattern),
                factory.apply(pattern).urlFragment()
        );
    }

    // Class............................................................................................................

    @Override
    public Class<SpreadsheetPattern> type() {
        return Cast.to(SpreadsheetPattern.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}

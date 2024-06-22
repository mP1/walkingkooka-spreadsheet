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
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.net.UrlFragment;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.convert.SpreadsheetConverters;
import walkingkooka.spreadsheet.format.FakeSpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
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
import walkingkooka.tree.expression.ExpressionNumberContexts;
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
import java.util.OptionalInt;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetPatternTest implements ClassTesting2<SpreadsheetPattern>,
        ParserTesting,
        HasSpreadsheetPatternKindTesting,
        HasTextTesting,
        SpreadsheetFormatterTesting {

    private final static Color COLOR = Color.BLACK;

    // static factory method Locale.....................................................................................

    @Test
    public void testDateFormatPatternLocaleNullLocaleFails() {
        this.parseFails(
                SpreadsheetPattern::dateFormatPatternLocale
        );
    }

    @Test
    public void testDateParsePatternLocaleNullLocaleFails() {
        this.parseFails(
                SpreadsheetPattern::dateParsePatternLocale
        );
    }

    @Test
    public void testDateTimeFormatPatternLocaleNullLocaleFails() {
        this.parseFails(
                SpreadsheetPattern::dateTimeFormatPatternLocale
        );
    }

    @Test
    public void testDateTimeParsePatternLocaleNullLocaleFails() {
        this.parseFails(
                SpreadsheetPattern::dateTimeParsePatternLocale
        );
    }

    @Test
    public void testTimeFormatPatternLocaleNullLocaleFails() {
        this.parseFails(
                SpreadsheetPattern::timeFormatPatternLocale
        );
    }

    @Test
    public void testTimeParsePatternLocaleNullLocaleFails() {
        this.parseFails(
                SpreadsheetPattern::timeParsePatternLocale
        );
    }

    private void parseFails(final Function<Locale, SpreadsheetPattern> parser) {
        assertThrows(
                NullPointerException.class,
                () -> parser.apply(null)
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
                                return Converters.localDateToLocalDateTime()
                                        .convert(
                                                value,
                                                target,
                                                ConverterContexts.fake()
                                        );
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
// SpreadsheetPattern parse Locale needs permutation with millis (added to seconds)
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
        this.parseFails(
                null,
                SpreadsheetPattern::parseDateFormatPattern
        );
    }

    @Test
    public void testParseDateFormatPatternColorNameFails() {
        this.parseFails(
                "[Black]",
                SpreadsheetPattern::parseDateFormatPattern
        );
    }

    @Test
    public void testParseDateFormatPatternColorNumberFails() {
        this.parseFails(
                "[Color 1]",
                SpreadsheetPattern::parseDateFormatPattern
        );
    }

    @Test
    public void testParseDateFormatPatternIncompleteTextLiteralFails() {
        this.parseFails(
                "dd/mm/yyyy\"Incomplete",
                SpreadsheetPattern::parseDateFormatPattern
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
                    public int generalFormatNumberDigitCount() {
                        return SpreadsheetFormatterContext.DEFAULT_GENERAL_FORMAT_NUMBER_DIGIT_COUNT;
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
        this.parseFails(
                null,
                SpreadsheetPattern::parseDateParsePattern
        );
    }

    @Test
    public void testParseDateParsePatternIncompleteTextLiteralFails() {
        this.parseFails(
                "dd/mm/yyyy\"Incomplete",
                SpreadsheetPattern::parseDateParsePattern
        );
    }

    @Test
    public void testParseDateParsePatternColorFails() {
        this.parseFails(
                "[BLACK]dd/mm/yyyy",
                SpreadsheetPattern::parseDateParsePattern
        );
    }

    @Test
    public void testParseDateParsePatternGeneralFails() {
        this.parseFails(
                "General",
                SpreadsheetPattern::parseDateParsePattern
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
        this.parseFails(
                null,
                SpreadsheetPattern::parseDateTimeFormatPattern
        );
    }

    @Test
    public void testParseDateTimeFormatPatternColorNameFails() {
        this.parseFails(
                "[Black]",
                SpreadsheetPattern::parseDateTimeFormatPattern
        );
    }

    @Test
    public void testParseDateTimeFormatPatternColorNumberFails() {
        this.parseFails(
                "[Color 1]",
                SpreadsheetPattern::parseDateTimeFormatPattern
        );
    }

    @Test
    public void testParseDateTimeFormatPatternIncompleteAmpmFails() {
        this.parseFails(
                "dd/mm/yyyy am",
                SpreadsheetPattern::parseDateTimeFormatPattern
        );
    }

    @Test
    public void testParseDateTimeFormatPatternIncompleteTextLiteralFails() {
        this.parseFails(
                "dd/mm/yyyy\"Incomplete",
                SpreadsheetPattern::parseDateTimeFormatPattern
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
                    public int generalFormatNumberDigitCount() {
                        return SpreadsheetFormatterContext.DEFAULT_GENERAL_FORMAT_NUMBER_DIGIT_COUNT;
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
        this.parseFails(
                null,
                SpreadsheetPattern::parseDateTimeParsePattern
        );
    }

    @Test
    public void testParseDateTimeParsePatternIncompleteTextLiteralFails() {
        this.parseFails(
                "dd/mm/yyyy hh:mm:ss\"Incomplete",
                SpreadsheetPattern::parseDateTimeParsePattern
        );
    }

    @Test
    public void testParseDateTimeParsePatternColorFails() {
        this.parseFails(
                "[BLACK]dd/mm/yyyy hh:mm:ss",
                SpreadsheetPattern::parseDateTimeParsePattern
        );
    }

    @Test
    public void testParseDateTimeParsePatternGeneralFails() {
        this.parseFails(
                "General",
                SpreadsheetPattern::parseDateTimeParsePattern
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
        this.parseFails(
                null,
                SpreadsheetPattern::parseNumberFormatPattern
        );
    }

    @Test
    public void testParseNumberFormatPatternColorNameFails() {
        this.parseFails(
                "[Black]",
                SpreadsheetPattern::parseNumberFormatPattern
        );
    }

    @Test
    public void testParseNumberFormatPatternColorNumberFails() {
        this.parseFails(
                "[Color 1]",
                SpreadsheetPattern::parseNumberFormatPattern
        );
    }

    @Test
    public void testParseNumberFormatPatternIncompleteExponentFails() {
        this.parseFails(
                "#E",
                SpreadsheetPattern::parseNumberFormatPattern
        );
    }

    @Test
    public void testParseNumberFormatPatternIncompleteTextLiteralFails() {
        this.parseFails(
                "#\"Incomplete",
                SpreadsheetPattern::parseNumberFormatPattern
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
                    public int generalFormatNumberDigitCount() {
                        return SpreadsheetFormatterContext.DEFAULT_GENERAL_FORMAT_NUMBER_DIGIT_COUNT;
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
    public void testParseNumberFormatPatternWithColorNameGeneral() {
        final ExpressionNumber number = ExpressionNumberKind.BIG_DECIMAL.create(1.5);
        final Color color = Color.parse("#123");

        this.formatAndCheck(
                SpreadsheetPattern.parseNumberFormatPattern("[Black]GENERAL")
                        .formatter(),
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
                    public int generalFormatNumberDigitCount() {
                        return SpreadsheetFormatterContext.DEFAULT_GENERAL_FORMAT_NUMBER_DIGIT_COUNT;
                    }

                    @Override
                    public MathContext mathContext() {
                        return MathContext.DECIMAL32;
                    }

                    @Override
                    public Optional<Color> colorName(final SpreadsheetColorName name) {
                        checkEquals(
                                SpreadsheetColorName.BLACK,
                                name,
                                "colorName"
                        );
                        return Optional.of(color);
                    }
                },
                SpreadsheetText.with("1.5")
                        .setColor(
                                Optional.of(color)
                        )
        );
    }

    @Test
    public void testParseNumberFormatPatternWithColorNumberGeneral() {
        final ExpressionNumber number = ExpressionNumberKind.BIG_DECIMAL.create(1.5);
        final Color color = Color.parse("#123");

        this.formatAndCheck(
                SpreadsheetPattern.parseNumberFormatPattern("[Color 45]GENERAL")
                        .formatter(),
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
                    public int generalFormatNumberDigitCount() {
                        return SpreadsheetFormatterContext.DEFAULT_GENERAL_FORMAT_NUMBER_DIGIT_COUNT;
                    }

                    @Override
                    public MathContext mathContext() {
                        return MathContext.DECIMAL32;
                    }

                    @Override
                    public Optional<Color> colorNumber(final int number) {
                        checkEquals(
                                45,
                                number,
                                "colorNumber"
                        );
                        return Optional.of(color);
                    }
                },
                SpreadsheetText.with("1.5")
                        .setColor(
                                Optional.of(color)
                        )
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
        this.parseFails(
                null,
                SpreadsheetPattern::parseNumberFormatPattern
        );
    }

    @Test
    public void testParseNumberParsePatternIncompleteTextLiteralFails() {
        this.parseFails(
                "#\"Incomplete",
                SpreadsheetPattern::parseNumberFormatPattern
        );
    }

    @Test
    public void testParseNumberParsePatternColorFails() {
        this.parseFails(
                "[BLACK]#.#",
                SpreadsheetPattern::parseNumberParsePattern
        );
    }

    @Test
    public void testParseNumberParsePatternGeneralFails() {
        this.parseFails(
                "General",
                SpreadsheetPattern::parseNumberParsePattern
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
    public void testDecimalFormatInternationalCurrencySymbol() throws Exception {
        this.decimalFormatParseAndCheck(
                "¤#.##",
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

    @Test
    public void testDecimalFormatIncludesDollarAndSpaces() throws Exception {
        this.decimalFormatParseAndCheck(
                "$ #.##",
                "$ 1.25",
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
                                ExpressionNumberContexts.basic(
                                        kind,
                                        DecimalNumberContexts.american(MathContext.DECIMAL32)
                                ),
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

    @Test
    public void testDecimalFormatGetInstanceAllLocales() {
        final SpreadsheetFormatterContext context = new FakeSpreadsheetFormatterContext() {

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
            public char groupSeparator() {
                return ',';
            }

            @Override
            public MathContext mathContext() {
                return MathContext.DECIMAL128;
            }
        };
        final ExpressionNumber number = ExpressionNumberKind.BIG_DECIMAL.create(12345.67);

        for (final Locale locale : Locale.getAvailableLocales()) {
            SpreadsheetPattern.decimalFormat(
                            (DecimalFormat) DecimalFormat.getInstance(locale))
                    .toFormat()
                    .formatter()
                    .format(
                            number,
                            context
                    );
        }
    }

    @Test
    public void testDecimalFormatGetNumberInstanceAllLocales() {
        final SpreadsheetFormatterContext context = new FakeSpreadsheetFormatterContext() {

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
            public char groupSeparator() {
                return ',';
            }

            @Override
            public MathContext mathContext() {
                return MathContext.DECIMAL128;
            }
        };
        final ExpressionNumber number = ExpressionNumberKind.BIG_DECIMAL.create(12345.67);

        for (final Locale locale : Locale.getAvailableLocales()) {
            SpreadsheetPattern.decimalFormat(
                            (DecimalFormat) DecimalFormat.getNumberInstance(locale))
                    .toFormat()
                    .formatter()
                    .format(
                            number,
                            context
                    );
        }
    }

    @Test
    public void testDecimalFormatGetPercentInstanceAllLocales() {
        final SpreadsheetFormatterContext context = new FakeSpreadsheetFormatterContext() {

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
            public char groupSeparator() {
                return ',';
            }

            @Override
            public char percentageSymbol() {
                return '%';
            }

            @Override
            public MathContext mathContext() {
                return MathContext.DECIMAL128;
            }
        };
        final ExpressionNumber number = ExpressionNumberKind.BIG_DECIMAL.create(12345.67);

        for (final Locale locale : Locale.getAvailableLocales()) {
            SpreadsheetPattern.decimalFormat(
                            (DecimalFormat) DecimalFormat.getPercentInstance(locale))
                    .toFormat()
                    .formatter()
                    .format(
                            number,
                            context
                    );
        }
    }

    @Test
    public void testDecimalFormatGetCurrencyInstanceAllLocales() {
        final SpreadsheetFormatterContext context = new FakeSpreadsheetFormatterContext() {

            @Override
            public boolean canConvert(final Object value,
                                      final Class<?> type) {
                return this.converter.canConvert(
                        value,
                        type,
                        this
                );
            }

            @Override
            public <T> Either<T, String> convert(final Object value,
                                                 final Class<T> target) {
                return this.converter.convert(
                        value,
                        target,
                        this
                );
            }

            private final Converter<SpreadsheetFormatterContext> converter = SpreadsheetConverters.basic();

            @Override
            public String currencySymbol() {
                return "$";
            }

            @Override
            public char decimalSeparator() {
                return '.';
            }

            @Override
            public ExpressionNumberKind expressionNumberKind() {
                return ExpressionNumberKind.BIG_DECIMAL;
            }

            @Override
            public char groupSeparator() {
                return ',';
            }

            @Override
            public char percentageSymbol() {
                return '%';
            }

            @Override
            public MathContext mathContext() {
                return MathContext.DECIMAL128;
            }
        };
        final ExpressionNumber number = ExpressionNumberKind.BIG_DECIMAL.create(12345.67);

        for (final Locale locale : Locale.getAvailableLocales()) {
            SpreadsheetPattern.decimalFormat(
                            (DecimalFormat) DecimalFormat.getCurrencyInstance(locale))
                    .toFormat()
                    .formatter()
                    .format(
                            number,
                            context
                    );
        }
    }

    // parseTextFormatPattern..........................................................................................

    @Test
    public void testParseTextFormatPatternParseNullFails() {
        this.parseFails(
                null,
                SpreadsheetPattern::parseTextFormatPattern
        );
    }

    @Test
    public void testParseTextFormatPatternColorNameFails() {
        this.parseFails(
                "[Black]",
                SpreadsheetPattern::parseTextFormatPattern
        );
    }

    @Test
    public void testParseTextFormatPatternColorNumberFails() {
        this.parseFails(
                "[Color 1]",
                SpreadsheetPattern::parseTextFormatPattern
        );
    }

    @Test
    public void testParseTextFormatPatternGeneralFails() {
        this.parseFails(
                "General",
                SpreadsheetPattern::parseTextFormatPattern
        );
    }

    @Test
    public void testParseTextFormatPatternIncompleteTextLiteralFails() {
        this.parseFails(
                "@\"Incomplete",
                SpreadsheetPattern::parseTextFormatPattern
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
        this.parseFails(
                null,
                (text) -> SpreadsheetPattern.timeParsePattern((SimpleDateFormat) null)
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
        this.parseFails(
                null,
                SpreadsheetPattern::parseTimeFormatPattern
        );
    }

    // https://github.com/mP1/walkingkooka-spreadsheet/issues/2909
    @Test
    public void testParseTimeFormatPatternGeneralShouldFail() {
        SpreadsheetPattern.parseTimeFormatPattern("General");
    }

    @Test
    public void testParseTimeFormatPatternColorNameFails() {
        this.parseFails(
                "[Black]",
                SpreadsheetPattern::parseTimeFormatPattern
        );
    }

    @Test
    public void testParseTimeFormatPatternColorNumberFails() {
        this.parseFails(
                "[Color 1]",
                SpreadsheetPattern::parseTimeFormatPattern
        );
    }

    @Test
    public void testParseTimeFormatPatternIncompleteAmpmFails() {
        this.parseFails(
                "hh:mm:ss am",
                SpreadsheetPattern::parseTimeFormatPattern
        );
    }

    @Test
    public void testParseTimeFormatPatternIncompleteTextLiteralFails() {
        this.parseFails(
                "hh:mm:ss\"Incomplete",
                SpreadsheetPattern::parseTimeFormatPattern
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
                    public int generalFormatNumberDigitCount() {
                        return SpreadsheetFormatterContext.DEFAULT_GENERAL_FORMAT_NUMBER_DIGIT_COUNT;
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
        this.parseFails(
                null,
                SpreadsheetPattern::parseTimeParsePattern
        );
    }

    @Test
    public void testParseTimeParsePatternIncompleteTextLiteralFails() {
        this.parseFails(
                "hh:mm:ss\"Incomplete",
                SpreadsheetPattern::parseTimeParsePattern
        );
    }

    @Test
    public void testParseTimeParsePatternColorFails() {
        this.parseFails(
                "[BLACK]hh:mm:ss",
                SpreadsheetPattern::parseTimeParsePattern
        );
    }

    @Test
    public void testParseTimeParsePatternGeneralFails() {
        this.parseFails(
                "General",
                SpreadsheetPattern::parseTimeParsePattern
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

    private void parseFails(final String text,
                            final Function<String, SpreadsheetPattern> parser) {
        this.parseFails(
                text,
                parser,
                null == text ? NullPointerException.class : IllegalArgumentException.class
        );
    }

    private void parseFails(final String text,
                            final Function<String, SpreadsheetPattern> parser,
                            final Class<? extends RuntimeException> thrown) {
        assertThrows(
                thrown,
                () -> parser.apply(text)
        );
    }

    @Test
    public void testParseInvalidCharacterThrowsInvalidCharacterException() {
        final InvalidCharacterException thrown = assertThrows(
                InvalidCharacterException.class,
                () -> SpreadsheetPattern.parseNumberFormatPattern("!Hello")
        );
        this.checkEquals(
                "Invalid character '!' at 0",
                thrown.getShortMessage()
        );
    }

    // components......................................................................................................

    @Test
    public void testComponentsWithNullBiConsumerFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetPattern.parseTextFormatPattern("@").components(null)
        );
    }

    @Test
    public void testComponentsDateFormatColorName() {
        this.componentsAndCheck(
                SpreadsheetPattern.parseDateFormatPattern("[RED]dd/mm/yyyy"),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.COLOR_NAME,
                        "[RED]"
                ),
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
    public void testComponentsDateFormatColorNameWhitespace() {
        this.componentsAndCheck(
                SpreadsheetPattern.parseDateFormatPattern("[RED ]dd/mm/yyyy"),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.COLOR_NAME,
                        "[RED ]"
                ),
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
    public void testComponentsDateFormatColorNumber() {
        this.componentsAndCheck(
                SpreadsheetPattern.parseDateFormatPattern("dd[Color 2]/mm/yyyy"),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.DAY_WITH_LEADING_ZERO,
                        "dd"
                ),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.COLOR_NUMBER,
                        "[Color 2]"
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
    public void testComponentsDateTimeFormatColorName() {
        this.componentsAndCheck(
                SpreadsheetPattern.parseDateTimeFormatPattern("[BLUE]dd/mm/yyyy hh:mm"),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.COLOR_NAME,
                        "[BLUE]"
                ),
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
    public void testComponentsDateTimeFormatColorNumber() {
        this.componentsAndCheck(
                SpreadsheetPattern.parseDateTimeFormatPattern("dd/mm/yyyy hh:mm[Color12]"),
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
                ),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.COLOR_NUMBER,
                        "[Color12]"
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
    public void testComponentsNumberFormatColorName() {
        this.componentsAndCheck(
                SpreadsheetPattern.parseNumberFormatPattern("$#.00[Green ]"),
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
                ),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.COLOR_NAME,
                        "[Green ]"
                )
        );
    }

    @Test
    public void testComponentsNumberFormatColorNumber() {
        this.componentsAndCheck(
                SpreadsheetPattern.parseNumberFormatPattern("$#.00[Color  12]"),
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
                ),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.COLOR_NUMBER,
                        "[Color  12]"
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
    public void testComponentsTextFormatColorName() {
        this.componentsAndCheck(
                SpreadsheetPattern.parseTextFormatPattern("[Yellow]@\"Hello\""),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.COLOR_NAME,
                        "[Yellow]"
                ),
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
    public void testComponentsTextFormatColorNumber() {
        this.componentsAndCheck(
                SpreadsheetPattern.parseTextFormatPattern("[Color1]@\"Hello\""),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.COLOR_NUMBER,
                        "[Color1]"
                ),
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
    public void testComponentsTimeFormatColorName() {
        this.componentsAndCheck(
                SpreadsheetPattern.parseTimeFormatPattern("[WHITE]hh:mm"),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.COLOR_NAME,
                        "[WHITE]"
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
    public void testComponentsTimeFormatColorNumber() {
        this.componentsAndCheck(
                SpreadsheetPattern.parseTimeFormatPattern("[Color4]hh:mm"),
                Map.entry(
                        SpreadsheetFormatParserTokenKind.COLOR_NUMBER,
                        "[Color4]"
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

        pattern.components(
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

    // kind & HasSpreadsheetPatterKind..................................................................................

    @Test
    public void testPatternKindDateParsePattern() {
        this.patternKindAndCheck(
                SpreadsheetPattern.parseDateParsePattern("dd/mm/yyyy"),
                SpreadsheetPatternKind.DATE_PARSE_PATTERN
        );
    }

    @Test
    public void testPatternKindDateTimeParsePattern() {
        this.patternKindAndCheck(
                SpreadsheetPattern.parseDateTimeParsePattern("dd/mm/yyyy hh:mm:ss"),
                SpreadsheetPatternKind.DATE_TIME_PARSE_PATTERN
        );
    }

    @Test
    public void testPatternKindNumberFormatPattern() {
        this.patternKindAndCheck(
                SpreadsheetPattern.parseNumberFormatPattern("#.###"),
                SpreadsheetPatternKind.NUMBER_FORMAT_PATTERN
        );
    }

    @Test
    public void testPatternKindTextFormatPattern() {
        this.patternKindAndCheck(
                SpreadsheetPattern.parseTextFormatPattern("@"),
                SpreadsheetPatternKind.TEXT_FORMAT_PATTERN
        );
    }

    private void patternKindAndCheck(final SpreadsheetPattern pattern,
                                     final SpreadsheetPatternKind expected) {
        this.checkEquals(
                expected,
                pattern.patternKind(),
                () -> pattern + " kind"
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

    // colorName........................................................................................................

    @Test
    public void testColorNameMultiplePatternsFails() {
        final IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                () -> SpreadsheetPattern.parseDateFormatPattern("dd;dd/mm/yyyy")
                        .colorName()
        );
        this.checkEquals(
                "Cannot get color name for multiple patterns=\"dd;dd/mm/yyyy\"",
                thrown.getMessage()
        );
    }

    @Test
    public void testColorNameParsePattern() {
        this.colorNameAndCheck(
                SpreadsheetPattern.parseDateParsePattern("dd/mm/yyyy")
        );
    }

    @Test
    public void testColorNameFormatPatternWithout() {
        this.colorNameAndCheck(
                SpreadsheetPattern.parseTextFormatPattern("@")
        );
    }

    @Test
    public void testColorNameFormatPatternWith() {
        this.colorNameAndCheck(
                SpreadsheetPattern.parseTextFormatPattern("[Red]@"),
                SpreadsheetColorName.with("Red")
        );
    }

    @Test
    public void testColorNameFormatPatternWith2() {
        this.colorNameAndCheck(
                SpreadsheetPattern.parseDateFormatPattern("[Red]dd;[Blue]dd")
                        .patterns()
                        .get(1),
                SpreadsheetColorName.with("Blue")
        );
    }

    private void colorNameAndCheck(final SpreadsheetPattern pattern) {
        this.colorNameAndCheck(
                pattern,
                Optional.empty()
        );
    }

    private void colorNameAndCheck(final SpreadsheetPattern pattern,
                                   final SpreadsheetColorName expected) {
        this.colorNameAndCheck(
                pattern,
                Optional.of(expected)
        );
    }

    private void colorNameAndCheck(final SpreadsheetPattern pattern,
                                   final Optional<SpreadsheetColorName> expected) {
        this.checkEquals(
                expected,
                pattern.colorName(),
                () -> pattern + " colorName"
        );
    }

    // colorNumber........................................................................................................

    @Test
    public void testColorNumberMultiplePatternsFails() {
        final IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                () -> SpreadsheetPattern.parseDateFormatPattern("dd;dd/mm/yyyy")
                        .colorNumber()
        );
        this.checkEquals(
                "Cannot get color name for multiple patterns=\"dd;dd/mm/yyyy\"",
                thrown.getMessage()
        );
    }

    @Test
    public void testColorNumberParsePattern() {
        this.colorNumberAndCheck(
                SpreadsheetPattern.parseDateParsePattern("dd/mm/yyyy")
        );
    }

    @Test
    public void testColorNumberFormatPatternWithout() {
        this.colorNumberAndCheck(
                SpreadsheetPattern.parseTextFormatPattern("@")
        );
    }

    @Test
    public void testColorNumberFormatPatternWith() {
        this.colorNumberAndCheck(
                SpreadsheetPattern.parseTextFormatPattern("[color 1]@"),
                1
        );
    }

    @Test
    public void testColorNumberFormatPatternWith2() {
        this.colorNumberAndCheck(
                SpreadsheetPattern.parseDateFormatPattern("[color 12]dd;[color 34]dd")
                        .patterns()
                        .get(1),
                34
        );
    }

    private void colorNumberAndCheck(final SpreadsheetPattern pattern) {
        this.colorNumberAndCheck(
                pattern,
                OptionalInt.empty()
        );
    }

    private void colorNumberAndCheck(final SpreadsheetPattern pattern,
                                     final int expected) {
        this.colorNumberAndCheck(
                pattern,
                OptionalInt.of(expected)
        );
    }

    private void colorNumberAndCheck(final SpreadsheetPattern pattern,
                                     final OptionalInt expected) {
        this.checkEquals(
                expected,
                pattern.colorNumber(),
                () -> pattern + " colorNumber"
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

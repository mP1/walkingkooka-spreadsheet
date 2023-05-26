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
import walkingkooka.collect.list.Lists;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.net.UrlFragment;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.format.FakeSpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterTesting;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenKind;
import walkingkooka.spreadsheet.parser.FakeSpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetDateParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetDateTimeParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetNumberParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetTimeParserToken;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.FakeExpressionEvaluationContext;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetPatternTest implements ClassTesting2<SpreadsheetPattern>,
        SpreadsheetFormatterTesting {


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
        this.formatAndCheck(
                formatPattern.formatter(),
                value,
                new FakeSpreadsheetFormatterContext() {
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

    // number...........................................................................................................

    @Test
    public void testNumberFormatPatternLocaleNullLocaleFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetPattern.numberFormatPatternLocale(null)
        );
    }

    @Test
    public void testNumberParsePatternLocaleNullLocaleFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetPattern.numberParsePatternLocale(null)
        );
    }

    @Test
    public void testNumberFormatLocale() {
        this.formatAndCheck(
                SpreadsheetPattern.numberFormatPatternLocale(EN_AU).formatter(),
                BigDecimal.valueOf(12.5),
                new FakeSpreadsheetFormatterContext() {

                    @Override
                    public boolean canConvert(final Object value,
                                              final Class<?> type) {
                        return true;
                    }

                    @Override
                    public <T> Either<T, String> convert(final Object value,
                                                         final Class<T> target) {
                        return Converters.simple()
                                .convert(
                                        value,
                                        target,
                                        ConverterContexts.fake()
                                );
                    }

                    @Override
                    public char decimalSeparator() {
                        return 'd';
                    }

                    @Override
                    public MathContext mathContext() {
                        return MathContext.DECIMAL32;
                    }

                    @Override
                    public char negativeSign() {
                        return 'n';
                    }

                    @Override
                    public char positiveSign() {
                        return 'p';
                    }

                    @Override
                    public ExpressionNumberKind expressionNumberKind() {
                        return ExpressionNumberKind.BIG_DECIMAL;
                    }
                },
                "12d5"
        );
    }

    @Test
    public void testNumberParsePatternLocale() {
        final Parser<SpreadsheetParserContext> parser = SpreadsheetPattern.numberParsePatternLocale(EN_AU).parser();
        final String text = "1d2";
        final TextCursor cursor = TextCursors.charSequence(text);

        this.checkEquals(
                ExpressionNumberKind.BIG_DECIMAL.create(1.2),
                parser.parse(
                        cursor,
                        new FakeSpreadsheetParserContext() {

                            @Override
                            public char decimalSeparator() {
                                return 'd';
                            }

                            @Override
                            public char groupingSeparator() {
                                return 'g';
                            }

                            @Override
                            public char negativeSign() {
                                return 'n';
                            }

                            @Override
                            public char positiveSign() {
                                return 'p';
                            }
                        }
                ).map(t -> t.cast(SpreadsheetNumberParserToken.class).toNumber(
                        new FakeExpressionEvaluationContext() {

                            @Override
                            public char negativeSign() {
                                return 'n';
                            }

                            @Override
                            public char positiveSign() {
                                return 'p';
                            }

                            @Override
                            public ExpressionNumberKind expressionNumberKind() {
                                return ExpressionNumberKind.BIG_DECIMAL;
                            }
                        })
                ).orElse(null),
                () -> "parse " + CharSequences.quoteAndEscape(text) + " parser: " + parser
        );
    }

    // forEachComponent..................................................................................................

    @Test
    public void testForEachComponentWithNullBiConsumerFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetPattern.parseTextFormatPattern("@").forEachComponent(null)
        );
    }

    @Test
    public void testForEachComponentDateFormat() {
        this.forEachComponentAndCheck(
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
    public void testForEachComponentDateTimeFormat() {
        this.forEachComponentAndCheck(
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
    public void testForEachComponentNumberFormat() {
        this.forEachComponentAndCheck(
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
    public void testForEachComponentTextFormat() {
        this.forEachComponentAndCheck(
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
    public void testForEachComponentTimeFormat() {
        this.forEachComponentAndCheck(
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
    public void testForEachComponentDateParse() {
        this.forEachComponentAndCheck(
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
    public void testForEachComponentDateTimeParse() {
        this.forEachComponentAndCheck(
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
    public void testForEachComponentNumberParse() {
        this.forEachComponentAndCheck(
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
    public void testForEachComponentTimeParse() {
        this.forEachComponentAndCheck(
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

    private void forEachComponentAndCheck(final SpreadsheetPattern pattern,
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

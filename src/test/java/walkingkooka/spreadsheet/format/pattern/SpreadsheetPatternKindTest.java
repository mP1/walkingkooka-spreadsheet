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

import org.junit.jupiter.api.Test;
import walkingkooka.Either;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.net.HasUrlFragmentTesting;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.format.FakeSpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterTesting;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenKind;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenVisitor;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.visit.Visiting;

import java.math.MathContext;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetPatternKindTest implements SpreadsheetFormatterTesting,
    HasUrlFragmentTesting,
    ClassTesting<SpreadsheetPatternKind> {

    @Test
    public void testDateTimeFormat() {
        this.typeNameAndCheck(
            SpreadsheetPatternKind.DATE_TIME_FORMAT_PATTERN,
            SpreadsheetDateTimeFormatPattern.class
        );
    }

    @Test
    public void testDateTimeParse() {
        this.typeNameAndCheck(
            SpreadsheetPatternKind.DATE_TIME_PARSE_PATTERN,
            SpreadsheetDateTimeParsePattern.class
        );
    }

    @Test
    public void testTextFormat() {
        this.typeNameAndCheck(
            SpreadsheetPatternKind.TEXT_FORMAT_PATTERN,
            SpreadsheetTextFormatPattern.class
        );
    }

    @Test
    public void testTimeFormat() {
        this.typeNameAndCheck(
            SpreadsheetPatternKind.TIME_FORMAT_PATTERN,
            SpreadsheetTimeFormatPattern.class
        );
    }

    @Test
    public void testTimeParse() {
        this.typeNameAndCheck(
            SpreadsheetPatternKind.TIME_PARSE_PATTERN,
            SpreadsheetTimeParsePattern.class
        );
    }

    private void typeNameAndCheck(final SpreadsheetPatternKind kind,
                                  final Class<? extends SpreadsheetPattern> expected) {
        this.checkEquals(
            JsonNodeContext.computeTypeName(expected),
            kind.typeName(),
            kind::toString
        );
    }

    @Test
    public void testFromTypeNameFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetPatternKind.fromTypeName("???")
        );
    }

    @Test
    public void testFromTypeName() {
        this.checkEquals(
            SpreadsheetPatternKind.TEXT_FORMAT_PATTERN,
            SpreadsheetPatternKind.fromTypeName(
                SpreadsheetPatternKind.TEXT_FORMAT_PATTERN.typeName()
            )
        );
    }

    @Test
    public void testFromTypeNameAll() {
        for (final SpreadsheetPatternKind kind : SpreadsheetPatternKind.values()) {

            this.checkEquals(
                kind,
                SpreadsheetPatternKind.fromTypeName(
                    kind.typeName()
                )
            );
        }
    }

    // parse............................................................................................................

    @Test
    public void testParseDateFormat() {
        this.parseAndCheck(
            "yyyy/mm/dd",
            SpreadsheetPatternKind.DATE_FORMAT_PATTERN,
            SpreadsheetPattern::parseDateFormatPattern
        );
    }

    @Test
    public void testParseDateParse() {
        this.parseAndCheck(
            "yyyy/mm/dd;yyyy/mm/dd;",
            SpreadsheetPatternKind.DATE_PARSE_PATTERN,
            SpreadsheetPattern::parseDateParsePattern
        );
    }

    @Test
    public void testParseDateTimeFormat() {
        this.parseAndCheck(
            "yyyy/mm/dd",
            SpreadsheetPatternKind.DATE_TIME_FORMAT_PATTERN,
            SpreadsheetPattern::parseDateTimeFormatPattern
        );
    }

    @Test
    public void testParseDateTimeParse() {
        this.parseAndCheck(
            "yyyy/mm/dd;yyyy/mm/dd;",
            SpreadsheetPatternKind.DATE_TIME_PARSE_PATTERN,
            SpreadsheetPattern::parseDateTimeParsePattern
        );
    }

    @Test
    public void testParseNumberFormat() {
        this.parseAndCheck(
            "$0.00",
            SpreadsheetPatternKind.NUMBER_FORMAT_PATTERN,
            SpreadsheetPattern::parseNumberFormatPattern
        );
    }

    @Test
    public void testParseNumberParse() {
        this.parseAndCheck(
            "$0.00,$0.000;",
            SpreadsheetPatternKind.NUMBER_PARSE_PATTERN,
            SpreadsheetPattern::parseNumberParsePattern
        );
    }

    @Test
    public void testParseTextFormat() {
        this.parseAndCheck(
            "@@@",
            SpreadsheetPatternKind.TEXT_FORMAT_PATTERN,
            SpreadsheetPattern::parseTextFormatPattern
        );
    }

    @Test
    public void testParseTimeFormat() {
        this.parseAndCheck(
            "hh/mm/ss",
            SpreadsheetPatternKind.TIME_FORMAT_PATTERN,
            SpreadsheetPattern::parseTimeFormatPattern
        );
    }

    @Test
    public void testParseTimeParse() {
        this.parseAndCheck(
            "hh/mm/ss;hh/mm/ss;",
            SpreadsheetPatternKind.TIME_PARSE_PATTERN,
            SpreadsheetPattern::parseTimeParsePattern
        );
    }

    private void parseAndCheck(final String pattern,
                               final SpreadsheetPatternKind kind,
                               final Function<String, SpreadsheetPattern> expected) {
        this.checkEquals(
            expected.apply(pattern),
            kind.parse(pattern),
            () -> "parse " + CharSequences.quoteAndEscape(pattern)
        );
    }

    // pattern..........................................................................................................

    @Test
    public void testPatternDateFormatPattern() {
        this.patternAndCheck(
            "[BLACK]dd/mm/yyyy",
            SpreadsheetPatternKind.DATE_FORMAT_PATTERN
        );
    }

    @Test
    public void testPatternDateParsePattern() {
        this.patternAndCheck(
            "dd/mm/yyyy",
            SpreadsheetPatternKind.DATE_PARSE_PATTERN
        );
    }

    @Test
    public void testPatternDateTimeFormatPattern() {
        this.patternAndCheck(
            "[BLACK]dd/mm/yyyy/hh/mm/ss",
            SpreadsheetPatternKind.DATE_TIME_FORMAT_PATTERN
        );
    }

    @Test
    public void testPatternDateTimeParsePattern() {
        this.patternAndCheck(
            "dd/mm/yyyy/hh/mm/ss",
            SpreadsheetPatternKind.DATE_TIME_PARSE_PATTERN
        );
    }

    @Test
    public void testPatternNumberFormatPattern() {
        this.patternAndCheck(
            "[BLACK]$0.00",
            SpreadsheetPatternKind.NUMBER_FORMAT_PATTERN
        );
    }

    @Test
    public void testPatternNumberParsePattern() {
        this.patternAndCheck(
            "$0.00",
            SpreadsheetPatternKind.NUMBER_PARSE_PATTERN
        );
    }

    @Test
    public void testPatternTextFormatPattern() {
        this.patternAndCheck(
            "[BLACK]@",
            SpreadsheetPatternKind.TEXT_FORMAT_PATTERN
        );
    }

    @Test
    public void testPatternTimeFormatPattern() {
        this.patternAndCheck(
            "[BLACK]hh/mm/ss",
            SpreadsheetPatternKind.TIME_FORMAT_PATTERN
        );
    }

    @Test
    public void testPatternTimeParsePattern() {
        this.patternAndCheck(
            "hh/mm/ss",
            SpreadsheetPatternKind.TIME_PARSE_PATTERN
        );
    }

    private void patternAndCheck(final String patternText,
                                 final SpreadsheetPatternKind kind) {
        final SpreadsheetPattern pattern = kind.parse(patternText);
        this.checkEquals(
            pattern,
            kind.pattern(pattern.value()),
            () -> kind + ".pattern " + pattern.value()
        );
    }

    // formatter........................................................................................................

    private final static Locale LOCALE = Locale.forLanguageTag("EN-AU");

    @Test
    public void testFormatterDateFormatPattern() {
        this.datePatternFormatAndCheck(SpreadsheetPatternKind.DATE_FORMAT_PATTERN);
    }

    @Test
    public void testFormatterDateParsePattern() {
        this.datePatternFormatAndCheck(SpreadsheetPatternKind.DATE_PARSE_PATTERN);
    }

    private void datePatternFormatAndCheck(final SpreadsheetPatternKind kind) {
        final LocalDate localDate = LocalDate.of(1999, 12, 31);

        this.formatAndCheck(
            kind.formatter(LOCALE),
            localDate,
            new FakeSpreadsheetFormatterContext() {
                @Override
                public boolean canConvert(final Object value,
                                          final Class<?> type) {
                    return value instanceof LocalDate && LocalDateTime.class == type;
                }

                @Override
                public <T> Either<T, String> convert(final Object value,
                                                     final Class<T> target) {
                    this.canConvertOrFail(value, target);
                    return this.successfulConversion(
                        LocalDateTime.of(
                            LocalDate.class.cast(value),
                            LocalTime.MIN
                        ),
                        target
                    );
                }

                @Override
                public List<String> monthNames() {
                    return this.dateTimeContext.monthNames();
                }

                @Override
                public String monthName(final int month) {
                    return this.dateTimeContext.monthName(month);
                }

                @Override
                public List<String> monthNameAbbreviations() {
                    return this.dateTimeContext.monthNameAbbreviations();
                }

                @Override
                public String monthNameAbbreviation(final int month) {
                    return this.dateTimeContext.monthNameAbbreviation(month);
                }

                @Override
                public List<String> weekDayNames() {
                    return this.dateTimeContext.weekDayNames();
                }

                @Override
                public String weekDayName(final int day) {
                    return this.dateTimeContext.weekDayName(day);
                }

                @Override
                public List<String> weekDayNameAbbreviations() {
                    return this.dateTimeContext.weekDayNameAbbreviations();
                }

                @Override
                public String weekDayNameAbbreviation(int day) {
                    return this.dateTimeContext.weekDayNameAbbreviation(day);
                }

                private final DateTimeContext dateTimeContext = DateTimeContexts.basic(
                    DateTimeSymbols.fromDateFormatSymbols(
                        new DateFormatSymbols(LOCALE)
                    ),
                    LOCALE,
                    1950,
                    50,
                    () -> {
                        throw new UnsupportedOperationException();
                    }
                );

                @Override
                public char zeroDigit() {
                    return '0';
                }
            },
            "Friday, 31 December 1999"
        );
    }

    @Test
    public void testFormatterDateTimeFormatPattern() {
        this.dateTimePatternFormatAndCheck(SpreadsheetPatternKind.DATE_TIME_FORMAT_PATTERN);
    }

    @Test
    public void testFormatterDateTimeParsePattern() {
        this.dateTimePatternFormatAndCheck(SpreadsheetPatternKind.DATE_TIME_PARSE_PATTERN);
    }

    private void dateTimePatternFormatAndCheck(final SpreadsheetPatternKind kind) {
        final LocalDateTime localDateTime = LocalDateTime.of(1999, 12, 31, 12, 58, 59);

        this.formatAndCheck(
            kind.formatter(LOCALE),
            localDateTime,
            new FakeSpreadsheetFormatterContext() {
                @Override
                public boolean canConvert(final Object value,
                                          final Class<?> type) {
                    return value instanceof LocalDateTime && LocalDateTime.class == type;
                }

                @Override
                public <T> Either<T, String> convert(final Object value,
                                                     final Class<T> target) {
                    this.canConvertOrFail(value, target);
                    return this.successfulConversion(
                        value,
                        target
                    );
                }

                @Override
                public List<String> ampms() {
                    return this.dateTimeContext.ampms();
                }

                @Override
                public String ampm(final int hourOfDay) {
                    return this.dateTimeContext.ampm(hourOfDay);
                }

                @Override
                public List<String> monthNames() {
                    return this.dateTimeContext.monthNames();
                }

                @Override
                public String monthName(final int month) {
                    return this.dateTimeContext.monthName(month);
                }

                @Override
                public List<String> monthNameAbbreviations() {
                    return this.dateTimeContext.monthNameAbbreviations();
                }

                @Override
                public String monthNameAbbreviation(final int month) {
                    return this.dateTimeContext.monthNameAbbreviation(month);
                }

                @Override
                public List<String> weekDayNames() {
                    return this.dateTimeContext.weekDayNames();
                }

                @Override
                public String weekDayName(final int day) {
                    return this.dateTimeContext.weekDayName(day);
                }

                @Override
                public List<String> weekDayNameAbbreviations() {
                    return this.dateTimeContext.weekDayNameAbbreviations();
                }

                @Override
                public String weekDayNameAbbreviation(int day) {
                    return this.dateTimeContext.weekDayNameAbbreviation(day);
                }

                private final DateTimeContext dateTimeContext = DateTimeContexts.basic(
                    DateTimeSymbols.fromDateFormatSymbols(
                        new DateFormatSymbols(LOCALE)
                    ),
                    LOCALE,
                    1950,
                    50,
                    () -> {
                        throw new UnsupportedOperationException();
                    }
                );

                @Override
                public char zeroDigit() {
                    return '0';
                }
            },
            "Friday, 31 December 1999 at 12:58:59 PM"
        );
    }

    @Test
    public void testFormatterNumberFormatPattern() {
        this.numberPatternFormatAndCheck(SpreadsheetPatternKind.NUMBER_FORMAT_PATTERN);
    }

    @Test
    public void testFormatterNumberParsePattern() {
        this.numberPatternFormatAndCheck(SpreadsheetPatternKind.NUMBER_PARSE_PATTERN);
    }

    private void numberPatternFormatAndCheck(final SpreadsheetPatternKind kind) {
        final ExpressionNumberKind expressionNumberKind = ExpressionNumberKind.DOUBLE;
        final ExpressionNumber number = expressionNumberKind.create(-12.56);

        this.formatAndCheck(
            kind.formatter(LOCALE),
            number,
            new FakeSpreadsheetFormatterContext() {
                @Override
                public boolean canConvert(final Object value,
                                          final Class<?> type) {
                    return value instanceof ExpressionNumber && ExpressionNumber.class == type;
                }

                @Override
                public <T> Either<T, String> convert(final Object value,
                                                     final Class<T> target) {
                    this.canConvertOrFail(value, target);
                    return this.successfulConversion(
                        value,
                        target
                    );
                }

                @Override
                public char decimalSeparator() {
                    return this.context.decimalSeparator();
                }

                @Override
                public char negativeSign() {
                    return this.context.negativeSign();
                }

                @Override
                public MathContext mathContext() {
                    return this.context.mathContext();
                }

                @Override
                public char zeroDigit() {
                    return '0';
                }

                private final DecimalNumberContext context = DecimalNumberContexts.american(MathContext.DECIMAL32);
            },
            "-12.56"
        );
    }

    @Test
    public void testFormatterTextFormatPattern() {
        final String text = "Abc123";

        this.formatAndCheck(
            SpreadsheetPatternKind.TEXT_FORMAT_PATTERN.formatter(LOCALE),
            text,
            new FakeSpreadsheetFormatterContext() {
                @Override
                public boolean canConvert(final Object value,
                                          final Class<?> type) {
                    return value instanceof String && String.class == type;
                }

                @Override
                public <T> Either<T, String> convert(final Object value,
                                                     final Class<T> target) {
                    this.canConvertOrFail(value, target);
                    return this.successfulConversion(
                        value,
                        target
                    );
                }
            },
            text
        );
    }

    @Test
    public void testFormatterTimeFormatPattern() {
        this.timePatternFormatAndCheck(SpreadsheetPatternKind.TIME_FORMAT_PATTERN);
    }

    @Test
    public void testFormatterTimeParsePattern() {
        this.timePatternFormatAndCheck(SpreadsheetPatternKind.TIME_PARSE_PATTERN);
    }

    private void timePatternFormatAndCheck(final SpreadsheetPatternKind kind) {
        final LocalTime localTime = LocalTime.of(12, 58, 59);

        this.formatAndCheck(
            kind.formatter(LOCALE),
            localTime,
            new FakeSpreadsheetFormatterContext() {
                @Override
                public boolean canConvert(final Object value,
                                          final Class<?> type) {
                    return value instanceof LocalTime && LocalDateTime.class == type;
                }

                @Override
                public <T> Either<T, String> convert(final Object value,
                                                     final Class<T> target) {
                    this.canConvertOrFail(value, target);
                    return this.successfulConversion(
                        LocalDateTime.of(
                            LocalDate.EPOCH,
                            LocalTime.class.cast(value)
                        ),
                        target
                    );
                }

                @Override
                public List<String> ampms() {
                    return this.dateTimeContext.ampms();
                }

                @Override
                public String ampm(final int hourOfDay) {
                    return this.dateTimeContext.ampm(hourOfDay);
                }

                @Override
                public List<String> monthNames() {
                    return this.dateTimeContext.monthNames();
                }

                @Override
                public String monthName(final int month) {
                    return this.dateTimeContext.monthName(month);
                }

                @Override
                public List<String> monthNameAbbreviations() {
                    return this.dateTimeContext.monthNameAbbreviations();
                }

                @Override
                public String monthNameAbbreviation(final int month) {
                    return this.dateTimeContext.monthNameAbbreviation(month);
                }

                @Override
                public List<String> weekDayNames() {
                    return this.dateTimeContext.weekDayNames();
                }

                @Override
                public String weekDayName(final int day) {
                    return this.dateTimeContext.weekDayName(day);
                }

                @Override
                public List<String> weekDayNameAbbreviations() {
                    return this.dateTimeContext.weekDayNameAbbreviations();
                }

                @Override
                public String weekDayNameAbbreviation(int day) {
                    return this.dateTimeContext.weekDayNameAbbreviation(day);
                }

                private final DateTimeContext dateTimeContext = DateTimeContexts.basic(
                    DateTimeSymbols.fromDateFormatSymbols(
                        new DateFormatSymbols(LOCALE)
                    ),
                    LOCALE,
                    1950,
                    50,
                    () -> {
                        throw new UnsupportedOperationException();
                    }
                );

                @Override
                public char zeroDigit() {
                    return '0';
                }
            },
            "12:58:59 PM"
        );
    }

    // HasUrlFragment....................................................................................................

    @Test
    public void testUrlFragmentDateFormatPattern() {
        this.urlFragmentAndCheck(
            SpreadsheetPatternKind.DATE_FORMAT_PATTERN,
            "formatter/date"
        );
    }

    @Test
    public void testUrlFragmentDateTimeFormatPattern() {
        this.urlFragmentAndCheck(
            SpreadsheetPatternKind.DATE_TIME_FORMAT_PATTERN,
            "formatter/date-time"
        );
    }

    @Test
    public void testUrlFragmentTimeParsePattern() {
        this.urlFragmentAndCheck(
            SpreadsheetPatternKind.TIME_PARSE_PATTERN,
            "parser/time"
        );
    }

    // isFormatPattern..................................................................................................

    @Test
    public void testIsFormatPatternDateFormatPattern() {
        this.isFormatPatternAndCheck(
            SpreadsheetPatternKind.DATE_FORMAT_PATTERN,
            true
        );
    }

    @Test
    public void testIsFormatPatternDateParsePattern() {
        this.isFormatPatternAndCheck(
            SpreadsheetPatternKind.DATE_PARSE_PATTERN,
            false
        );
    }

    private void isFormatPatternAndCheck(final SpreadsheetPatternKind kind,
                                         final boolean expected) {
        this.checkEquals(
            expected,
            kind.isFormatPattern(),
            () -> kind + " isFormatPattern"
        );
    }

    // toFormat.........................................................................................................

    @Test
    public void testToFormatDateFormatPattern() {
        this.toFormatAndCheck(
            SpreadsheetPatternKind.DATE_FORMAT_PATTERN
        );
    }

    @Test
    public void testToFormatDateTimeFormatPattern() {
        this.toFormatAndCheck(
            SpreadsheetPatternKind.DATE_TIME_FORMAT_PATTERN
        );
    }

    @Test
    public void testToFormatNumberFormatPattern() {
        this.toFormatAndCheck(
            SpreadsheetPatternKind.NUMBER_FORMAT_PATTERN
        );
    }

    @Test
    public void testToFormatTextFormatPattern() {
        this.toFormatAndCheck(
            SpreadsheetPatternKind.TEXT_FORMAT_PATTERN
        );
    }

    @Test
    public void testToFormatTimeFormatPattern() {
        this.toFormatAndCheck(
            SpreadsheetPatternKind.TIME_FORMAT_PATTERN
        );
    }

    private void toFormatAndCheck(final SpreadsheetPatternKind kind) {
        assertSame(
            kind,
            kind.toFormat(),
            kind::toString
        );
    }

    @Test
    public void testToFormatDateParsePattern() {
        this.toFormatAndCheck(
            SpreadsheetPatternKind.DATE_PARSE_PATTERN,
            SpreadsheetPatternKind.DATE_FORMAT_PATTERN
        );
    }

    @Test
    public void testToFormatDateTimeParsePattern() {
        this.toFormatAndCheck(
            SpreadsheetPatternKind.DATE_TIME_PARSE_PATTERN,
            SpreadsheetPatternKind.DATE_TIME_FORMAT_PATTERN
        );
    }

    @Test
    public void testToFormatNumberParsePattern() {
        this.toFormatAndCheck(
            SpreadsheetPatternKind.NUMBER_PARSE_PATTERN,
            SpreadsheetPatternKind.NUMBER_FORMAT_PATTERN
        );
    }

    @Test
    public void testToFormatTimeParsePattern() {
        this.toFormatAndCheck(
            SpreadsheetPatternKind.TIME_PARSE_PATTERN,
            SpreadsheetPatternKind.TIME_FORMAT_PATTERN
        );
    }

    private void toFormatAndCheck(final SpreadsheetPatternKind kind,
                                  final SpreadsheetPatternKind expected) {
        this.checkEquals(
            expected,
            kind.toFormat(),
            kind::toString
        );
    }

    // toFormat.........................................................................................................

    @Test
    public void testToParseDateFormatPattern() {
        this.toParseAndCheck(
            SpreadsheetPatternKind.DATE_PARSE_PATTERN
        );
    }

    @Test
    public void testToParseDateTimeFormatPattern() {
        this.toParseAndCheck(
            SpreadsheetPatternKind.DATE_TIME_PARSE_PATTERN
        );
    }

    @Test
    public void testToParseNumberFormatPattern() {
        this.toParseAndCheck(
            SpreadsheetPatternKind.NUMBER_PARSE_PATTERN
        );
    }

    @Test
    public void testToParseTimeFormatPattern() {
        this.toParseAndCheck(
            SpreadsheetPatternKind.TIME_PARSE_PATTERN
        );
    }

    private void toParseAndCheck(final SpreadsheetPatternKind kind) {
        this.toParseAndCheck(
            kind,
            kind
        );
    }

    @Test
    public void testToParseDateParsePattern() {
        this.toParseAndCheck(
            SpreadsheetPatternKind.DATE_FORMAT_PATTERN,
            SpreadsheetPatternKind.DATE_PARSE_PATTERN
        );
    }

    @Test
    public void testToParseDateTimeParsePattern() {
        this.toParseAndCheck(
            SpreadsheetPatternKind.DATE_TIME_FORMAT_PATTERN,
            SpreadsheetPatternKind.DATE_TIME_PARSE_PATTERN
        );
    }

    @Test
    public void testToParseNumberParsePattern() {
        this.toParseAndCheck(
            SpreadsheetPatternKind.NUMBER_FORMAT_PATTERN,
            SpreadsheetPatternKind.NUMBER_PARSE_PATTERN
        );
    }

    @Test
    public void testToParseTextFormatPattern() {
        this.toParseAndCheck(
            SpreadsheetPatternKind.TEXT_FORMAT_PATTERN,
            Optional.empty()
        );
    }

    @Test
    public void testToParseTimeParsePattern() {
        this.toParseAndCheck(
            SpreadsheetPatternKind.TIME_FORMAT_PATTERN,
            SpreadsheetPatternKind.TIME_PARSE_PATTERN
        );
    }

    private void toParseAndCheck(final SpreadsheetPatternKind kind,
                                 final SpreadsheetPatternKind expected) {
        this.toParseAndCheck(
            kind,
            Optional.of(expected)
        );
    }

    private void toParseAndCheck(final SpreadsheetPatternKind kind,
                                 final Optional<SpreadsheetPatternKind> expected) {
        this.checkEquals(
            expected,
            kind.toParse(),
            kind::toString
        );
    }

    // spreadsheetFormatParserTokenKinds................................................................................

    @Test
    public void testSpreadsheetFormatParserTokenKindsWithDateFormatPattern() {
        this.spreadsheetFormatParserTokenKindsAndCheck(
            SpreadsheetPatternKind.DATE_FORMAT_PATTERN,
            "dd/mm/yyyy \"Hello\""
        );
    }

    @Test
    public void testSpreadsheetFormatParserTokenKindsWithDateParsePattern() {
        this.spreadsheetFormatParserTokenKindsAndCheck(
            SpreadsheetPatternKind.DATE_PARSE_PATTERN,
            "dd/mm/yyyy \"Hello\";dd/mm/yyyy"
        );
    }

    @Test
    public void testSpreadsheetFormatParserTokenKindsWithDateTimeFormatPattern() {
        this.spreadsheetFormatParserTokenKindsAndCheck(
            SpreadsheetPatternKind.DATE_TIME_FORMAT_PATTERN,
            "dd/mm/yyyy hh:mm:ss\"Hello\""
        );
    }

    @Test
    public void testSpreadsheetFormatParserTokenKindsWithDateTimeParsePattern() {
        this.spreadsheetFormatParserTokenKindsAndCheck(
            SpreadsheetPatternKind.DATE_TIME_PARSE_PATTERN,
            "dd/mm/yyyy hh:mm:ss\"Hello\";dd/mm/yyyy hh:mm:ss"
        );
    }

    @Test
    public void testSpreadsheetFormatParserTokenKindsWithNumberFormatPattern() {
        this.spreadsheetFormatParserTokenKindsAndCheck(
            SpreadsheetPatternKind.NUMBER_FORMAT_PATTERN,
            "00.00 \"Hello\""
        );
    }

    @Test
    public void testSpreadsheetFormatParserTokenKindsWithNumberParsePattern() {
        this.spreadsheetFormatParserTokenKindsAndCheck(
            SpreadsheetPatternKind.NUMBER_PARSE_PATTERN,
            "$0.00 \"Hello\";00.00%"
        );
    }

    @Test
    public void testSpreadsheetFormatParserTokenKindsWithTextFormatPattern() {
        this.spreadsheetFormatParserTokenKindsAndCheck(
            SpreadsheetPatternKind.TEXT_FORMAT_PATTERN,
            "@\"Hello\"*a"
        );
    }

    @Test
    public void testSpreadsheetFormatParserTokenKindsWithTimeFormatPattern() {
        this.spreadsheetFormatParserTokenKindsAndCheck(
            SpreadsheetPatternKind.TIME_FORMAT_PATTERN,
            "hh:mm:ss.000 \"Hello\""
        );
    }

    @Test
    public void testSpreadsheetFormatParserTokenKindsWithTimeParsePattern() {
        this.spreadsheetFormatParserTokenKindsAndCheck(
            SpreadsheetPatternKind.TIME_PARSE_PATTERN,
            "hh:mm:ss \"Hello\";hh:mm:ss"
        );
    }

    private void spreadsheetFormatParserTokenKindsAndCheck(final SpreadsheetPatternKind patternKind,
                                                           final String pattern) {
        final SpreadsheetPattern spreadsheetPattern = patternKind.parse(pattern);

        final Map<SpreadsheetFormatParserToken, SpreadsheetFormatParserTokenKind> tokenToKinds = Maps.ordered();
        new SpreadsheetFormatParserTokenVisitor() {

            @Override
            protected Visiting startVisit(final SpreadsheetFormatParserToken token) {
                token.kind()
                    .ifPresent(k ->
                    {
                        if (false == patternKind.spreadsheetFormatParserTokenKinds().contains(k)) {
                            tokenToKinds.put(token, k);
                        }
                    });
                return super.startVisit(token);
            }
        }.accept(
            spreadsheetPattern.value()
        );

        this.checkEquals(
            Maps.empty(),
            tokenToKinds,
            () -> pattern + " " + patternKind
        );
    }

    // isFormatPattern..................................................................................................

    @Test
    public void testIsFormatPatternVIsParsePattern() {
        this.checkEquals(
            Lists.empty(),
            Arrays.stream(SpreadsheetPatternKind.values())
                .filter(k -> k.isFormatPattern() == k.isParsePattern())
                .collect(Collectors.toList())
        );
    }

    // checkSameOrFail..................................................................................................

    @Test
    public void testCheckSameOrFailWithNull() {
        SpreadsheetPatternKind.DATE_FORMAT_PATTERN.checkSameOrFail(null);
    }

    @Test
    public void testCheckSameOrFail() {
        SpreadsheetPatternKind.DATE_FORMAT_PATTERN.checkSameOrFail(
            SpreadsheetPattern.parseDateFormatPattern("dd/mm/yyyy")
        );
    }

    @Test
    public void testCheckSameOrFailInvalidThrows() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetPatternKind.DATE_FORMAT_PATTERN.checkSameOrFail(
                SpreadsheetPattern.parseTimeParsePattern("hh:mm")
            )
        );

        this.checkEquals(
            "Pattern \"hh:mm\" is not a TIME_PARSE_PATTERN.",
            thrown.getMessage(),
            "message"
        );
    }

    // spreadsheetMetadataPropertyName..................................................................................

    @Test
    public void testSpreadsheetMetadataPropertyNameDateFormatter() {
        this.spreadsheetMetadataPropertyNameAndCheck(
            SpreadsheetPatternKind.DATE_FORMAT_PATTERN,
            SpreadsheetMetadataPropertyName.DATE_FORMATTER
        );
    }

    @Test
    public void testSpreadsheetMetadataPropertyNameDateTimeFormatter() {
        this.spreadsheetMetadataPropertyNameAndCheck(
            SpreadsheetPatternKind.DATE_TIME_FORMAT_PATTERN,
            SpreadsheetMetadataPropertyName.DATE_TIME_FORMATTER
        );
    }

    private void spreadsheetMetadataPropertyNameAndCheck(final SpreadsheetPatternKind kind,
                                                         final SpreadsheetMetadataPropertyName<SpreadsheetFormatterSelector> expected) {
        this.checkEquals(
            expected,
            kind.spreadsheetMetadataPropertyName()
        );
    }

    // formatValues.....................................................................................................

    @Test
    public void testFormatValues() {
        this.checkEquals(
            Lists.of(
                SpreadsheetPatternKind.DATE_FORMAT_PATTERN,
                SpreadsheetPatternKind.DATE_TIME_FORMAT_PATTERN,
                SpreadsheetPatternKind.NUMBER_FORMAT_PATTERN,
                SpreadsheetPatternKind.TEXT_FORMAT_PATTERN,
                SpreadsheetPatternKind.TIME_FORMAT_PATTERN
            ),
            List.of(
                SpreadsheetPatternKind.formatValues()
            )
        );
    }

    @Test
    public void testFormatValuesCloned() {
        assertNotSame(
            SpreadsheetPatternKind.formatValues(),
            SpreadsheetPatternKind.formatValues()
        );
    }

    // parseValues.....................................................................................................

    @Test
    public void testParseValues() {
        this.checkEquals(
            Lists.of(
                SpreadsheetPatternKind.DATE_PARSE_PATTERN,
                SpreadsheetPatternKind.DATE_TIME_PARSE_PATTERN,
                SpreadsheetPatternKind.NUMBER_PARSE_PATTERN,
                SpreadsheetPatternKind.TIME_PARSE_PATTERN
            ),
            List.of(
                SpreadsheetPatternKind.parseValues()
            )
        );
    }

    @Test
    public void testParseValuesCloned() {
        assertNotSame(
            SpreadsheetPatternKind.parseValues(),
            SpreadsheetPatternKind.parseValues()
        );
    }

    // ClassTesting......................................................................................................

    @Override
    public Class<SpreadsheetPatternKind> type() {
        return SpreadsheetPatternKind.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}

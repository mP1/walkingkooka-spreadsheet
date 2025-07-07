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
import walkingkooka.color.Color;
import walkingkooka.convert.Converter;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.spreadsheet.format.FakeSpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetText;
import walkingkooka.spreadsheet.format.parser.DateSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.InvalidCharacterExceptionFactory;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.convert.ExpressionNumberConverters;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.math.MathContext;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public final class SpreadsheetDateFormatPatternTest extends SpreadsheetFormatPatternTestCase<SpreadsheetDateFormatPattern,
    DateSpreadsheetFormatParserToken> {

    @Test
    public void testWithAmpmFails() {
        this.withInvalidCharacterFails(this.ampm());
    }

    @Test
    public void testWithCurrencyFails() {
        this.withInvalidCharacterFails(this.currency());
    }

    @Test
    public void testWithDateTimeFails() {
        this.withInvalidCharacterFails(this.dateTime());
    }

    @Test
    public void testWithDecimalPointFails() {
        this.withInvalidCharacterFails(this.decimalPoint());
    }

    @Test
    public void testWithDigitFails() {
        this.withInvalidCharacterFails(this.digit());
    }

    @Test
    public void testWithDigitSpaceFails() {
        this.withInvalidCharacterFails(this.digitSpace());
    }

    @Test
    public void testWithDigitZeroFails() {
        this.withInvalidCharacterFails(this.digitZero());
    }

    @Test
    public void testWithExponentSymbolFails() {
        this.withInvalidCharacterFails(this.exponentSymbol());
    }

    @Test
    public void testWithGroupSeparatorFails() {
        this.withInvalidCharacterFails(this.groupSeparator());
    }

    @Test
    public void testWithHourFails() {
        this.withInvalidCharacterFails(this.hour());
    }

    @Test
    public void testWithNumberFails() {
        this.withInvalidCharacterFails(this.number());
    }

    @Test
    public void testWithPercentSymbolFails() {
        this.withInvalidCharacterFails(this.percentSymbol());
    }

    @Test
    public void testWithSecondsFails() {
        this.withInvalidCharacterFails(this.second());
    }

    @Test
    public void testWithTimeFails() {
        this.withInvalidCharacterFails(this.time());
    }

    // ParseString......................................................................................................

    @Test
    public void testParseStringDateTimePatternFails() {
        this.parseStringFails(
            "ddmmyyyy hhmmss",
            IllegalArgumentException.class
        );
    }

    @Test
    public void testParseStringNumberPatternFails() {
        this.parseStringFails(
            "0#00",
            IllegalArgumentException.class
        );
    }

    @Test
    public void testParseGeneral() {
        this.parseString("General");
    }

    // HasFormatter.....................................................................................................

    @Test
    public void testFormatterFormatYy() {
        this.formatAndCheck2(
            "yy",
            LocalDate.of(2000, 12, 31),
            "00"
        );
    }

    @Test
    public void testFormatterFormatYy2() {
        this.formatAndCheck2(
            "yy",
            LocalDate.of(1999, 12, 31),
            "99"
        );
    }

    @Test
    public void testFormatterFormatYyyy() {
        this.formatAndCheck2(
            "yyyy",
            LocalDate.of(2000, 12, 31),
            "2000"
        );
    }

    @Test
    public void testFormatterFormatYyyy2() {
        this.formatAndCheck2(
            "yyyy",
            LocalDate.of(1999, 12, 31),
            "1999"
        );
    }

    @Test
    public void testFormatterFormatM() {
        this.formatAndCheck2(
            "m",
            LocalDate.of(2000, 1, 31),
            "1"
        );
    }

    @Test
    public void testFormatterFormatM2() {
        this.formatAndCheck2(
            "m",
            LocalDate.of(2000, 12, 31),
            "12"
        );
    }

    @Test
    public void testFormatterFormatMm() {
        this.formatAndCheck2(
            "mm",
            LocalDate.of(2000, 1, 31),
            "01"
        );
    }

    @Test
    public void testFormatterFormatMm2() {
        this.formatAndCheck2(
            "mm",
            LocalDate.of(2000, 12, 31),
            "12"
        );
    }

    @Test
    public void testFormatterFormatMmm() {
        this.formatAndCheck2(
            "mmm",
            LocalDate.of(2000, 1, 31),
            "Jan."
        );
    }

    @Test
    public void testFormatterFormatMmm2() {
        this.formatAndCheck2(
            "mmm",
            LocalDate.of(2000, 12, 31),
            "Dec."
        );
    }

    @Test
    public void testFormatterFormatMmmm() {
        this.formatAndCheck2(
            "mmmm",
            LocalDate.of(2000, 1, 31),
            "January"
        );
    }

    @Test
    public void testFormatterFormatMmmm2() {
        this.formatAndCheck2(
            "mmmm",
            LocalDate.of(2000, 12, 31),
            "December"
        );
    }

    @Test
    public void testFormatterFormatMmmmm() {
        this.formatAndCheck2(
            "mmmmm",
            LocalDate.of(2000, 1, 31),
            "J"
        );
    }

    @Test
    public void testFormatterFormatMmmmm2() {
        this.formatAndCheck2(
            "mmmmm",
            LocalDate.of(2000, 12, 31),
            "D"
        );
    }

    @Test
    public void testFormatterFormatD() {
        this.formatAndCheck2(
            "d",
            LocalDate.of(2000, 12, 1),
            "1"
        );
    }

    @Test
    public void testFormatterFormatD2() {
        this.formatAndCheck2(
            "d",
            LocalDate.of(2000, 12, 31),
            "31"
        );
    }

    @Test
    public void testFormatterFormatDd() {
        this.formatAndCheck2(
            "dd",
            LocalDate.of(2000, 12, 1),
            "01"
        );
    }

    @Test
    public void testFormatterFormatDd2() {
        this.formatAndCheck2(
            "dd",
            LocalDate.of(2000, 12, 31),
            "31"
        );
    }

    @Test
    public void testFormatterFormatDdd() {
        this.formatAndCheck2(
            "ddd",
            LocalDate.of(2000, 12, 1),
            "Fri."
        );
    }

    @Test
    public void testFormatterFormatDdd2() {
        this.formatAndCheck2(
            "ddd",
            LocalDate.of(2000, 12, 31),
            "Sun."
        );
    }

    @Test
    public void testFormatterFormatDddd() {
        this.formatAndCheck2(
            "dddd",
            LocalDate.of(2000, 12, 1),
            "Friday"
        );
    }

    @Test
    public void testFormatterFormatDddd2() {
        this.formatAndCheck2(
            "dddd",
            LocalDate.of(2000, 12, 31),
            "Sunday"
        );
    }

    @Test
    public void testFormatterFormatDdddd() {
        this.formatAndCheck2(
            "ddddd",
            LocalDate.of(2000, 12, 1),
            "Friday"
        );
    }

    @Test
    public void testFormatterFormatDdddd2() {
        this.formatAndCheck2(
            "ddddd",
            LocalDate.of(2000, 12, 31),
            "Sunday"
        );
    }

    @Test
    public void testFormatterFormatYyyymmdd() {
        this.formatAndCheck2(
            "yyyymmdd",
            LocalDate.of(2000, 12, 31),
            "20001231"
        );
    }

    @Test
    public void testFormatterFormatYyyymmdd2() {
        this.formatAndCheck2(
            "yyyy,mm,dd",
            LocalDate.of(2000, 12, 31),
            "2000,12,31"
        );
    }

    @Test
    public void testFormatterFormatLiteral() {
        this.formatAndCheck2(
            ",",
            LocalDate.of(2000, 12, 31),
            ","
        );
    }

    @Test
    public void testFormatterFormatIncludesColorName() {
        this.formatAndCheck2(
            "[red]yyyymmdd",
            LocalDate.of(2000, 12, 31),
            SpreadsheetText.with("20001231")
                .setColor(Optional.of(RED))
        );
    }

    @Test
    public void testFormatterFormatIncludesColorNumber() {
        this.formatAndCheck2(
            "[color44]yyyymmdd",
            LocalDate.of(2000, 12, 31),
            SpreadsheetText.with("20001231")
                .setColor(Optional.of(RED))
        );
    }

    @Test
    public void testFormatterFormatIncludesColorNumberUsingArabicZeroDigit() {
        this.formatAndCheck2(
            "[color44]yyyymmdd",
            LocalDate.of(2000, 12, 31),
            ARABIC_ZERO_DIGIT,
            SpreadsheetText.with(
                arabicDigit(2) +
                    arabicDigit(0) +
                    arabicDigit(0) +
                    arabicDigit(0) +
                    arabicDigit(1) +
                    arabicDigit(2) +
                    arabicDigit(3) +
                    arabicDigit(1)
            ).setColor(Optional.of(RED))
        );
    }

    @Test
    public void testFormatterGeneral() {
        this.formatAndCheck2(
            "General",
            LocalDate.of(1999, 12, 31),
            "10956"
        );
    }

    @Test
    public void testFormatterGeneralUsingArabicZeroDigit() {
        this.formatAndCheck2(
            "General",
            LocalDate.of(1999, 12, 31),
            ARABIC_ZERO_DIGIT,
            arabicDigit(1) +
                arabicDigit(0) +
                arabicDigit(9) +
                arabicDigit(5) +
                arabicDigit(6)
        );
    }

    @Override
    SpreadsheetFormatterContext createContext(final char zeroDigit) {
        return new FakeSpreadsheetFormatterContext() {

            @Override
            public boolean canConvert(final Object value,
                                      final Class<?> target) {
                return this.converter.canConvert(
                    value,
                    target,
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

            private final Converter<FakeSpreadsheetFormatterContext> converter = Converters.collection(
                Lists.of(
                    ExpressionNumberConverters.toNumberOrExpressionNumber(
                        Converters.localDateToNumber()
                    ),
                    Converters.localDateToLocalDateTime()
                )
            );

            @Override
            public long dateOffset() {
                return Converters.JAVA_EPOCH_OFFSET;
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
            public int generalFormatNumberDigitCount() {
                return SpreadsheetFormatterContext.DEFAULT_GENERAL_FORMAT_NUMBER_DIGIT_COUNT;
            }

            @Override
            public MathContext mathContext() {
                return MathContext.DECIMAL32;
            }

            @Override
            public List<String> monthNames() {
                return this.dateTimeContext().monthNames();
            }

            @Override
            public String monthName(final int month) {
                return this.dateTimeContext().monthName(month);
            }

            @Override
            public List<String> monthNameAbbreviations() {
                return this.dateTimeContext().monthNameAbbreviations();
            }

            @Override
            public String monthNameAbbreviation(final int month) {
                return this.dateTimeContext().monthNameAbbreviation(month);
            }

            @Override
            public int twoDigitYear() {
                return this.dateTimeContext().twoDigitYear();
            }

            @Override
            public List<String> weekDayNames() {
                return this.dateTimeContext().weekDayNames();
            }

            @Override
            public String weekDayName(final int day) {
                return this.dateTimeContext().weekDayName(day);
            }

            @Override
            public List<String> weekDayNameAbbreviations() {
                return this.dateTimeContext().weekDayNameAbbreviations();
            }

            @Override
            public String weekDayNameAbbreviation(final int day) {
                return this.dateTimeContext().weekDayNameAbbreviation(day);
            }

            @Override
            public char zeroDigit() {
                return zeroDigit;
            }

            private DateTimeContext dateTimeContext() {
                return DateTimeContexts.basic(
                    DateTimeSymbols.fromDateFormatSymbols(
                        new DateFormatSymbols(Locale.forLanguageTag("EN-AU"))
                    ),
                    Locale.forLanguageTag("EN-AU"),
                    1900,
                    20,
                    LocalDateTime::now
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
        };
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
            this.createPattern(),
            "date-format-pattern\n" +
                "  \"ddmmyyyy \\\"text-literal\\\" \\\\!\"\n"
        );
    }

    // patterns..........................................................................................................

    @Test
    public void testPatterns() {
        final SpreadsheetDateFormatPattern pattern = this.createPattern("dd/mm/yyyy");

        this.patternsAndCheck2(
            pattern,
            Lists.of(pattern)
        );
    }

    @Test
    public void testPatternWithColor() {
        final SpreadsheetDateFormatPattern pattern = this.createPattern("[green]dd/mm/yyyy");

        this.patternsAndCheck(
            pattern,
            "[green]dd/mm/yyyy"
        );
    }

    // removeColor......................................................................................................

    @Test
    public void testRemoveColor() {
        final SpreadsheetDateFormatPattern pattern = this.createPattern("[green]dd/mm/yyyy");

        this.removeColorAndCheck(
            pattern,
            this.createPattern("dd/mm/yyyy")
        );
    }

    // setColorName.....................................................................................................

    @Test
    public void testSetColorName() {
        final SpreadsheetDateFormatPattern pattern = this.createPattern("dd/mm/yyyy");

        this.setColorNameAndCheck(
            pattern,
            SpreadsheetColorName.RED,
            "[Red]dd/mm/yyyy"
        );
    }

    @Test
    public void testSetColorNameRemovesPreviousColor() {
        final SpreadsheetDateFormatPattern pattern = this.createPattern("[green]dd/mm/yyyy");

        this.setColorNameAndCheck(
            pattern,
            SpreadsheetColorName.RED,
            "[Red]dd/mm/yyyy"
        );
    }

    // setColorNumber.....................................................................................................

    @Test
    public void testSetColorNumber() {
        final SpreadsheetDateFormatPattern pattern = this.createPattern("dd/mm/yyyy");

        this.setColorNumberAndCheck(
            pattern,
            12,
            "[color 12]dd/mm/yyyy"
        );
    }

    @Test
    public void testSetColorNumberRemovesPreviousColor() {
        final SpreadsheetDateFormatPattern pattern = this.createPattern("[green]dd/mm/yyyy");

        this.setColorNumberAndCheck(
            pattern,
            12,
            "[color 12]dd/mm/yyyy"
        );
    }

    // helpers..........................................................................................................

    @Override
    SpreadsheetDateFormatPattern createPattern(final ParserToken token) {
        return SpreadsheetDateFormatPattern.with(token);
    }

    @Override
    String patternText() {
        return "ddmmyyyy \"text-literal\" \\!";
    }

    @Override
    ParserToken parseFormatParserToken(final String text) {
        return SpreadsheetFormatParsers.dateFormat()
            .orFailIfCursorNotEmpty(ParserReporters.basic())
            .parse(
                TextCursors.charSequence(text),
                SpreadsheetFormatParserContexts.basic(InvalidCharacterExceptionFactory.POSITION)
            ).get();
    }

    @Override
    DateSpreadsheetFormatParserToken createFormatParserToken(final List<ParserToken> tokens,
                                                             final String text) {
        return SpreadsheetFormatParserToken.date(tokens, text);
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetDateFormatPattern> type() {
        return SpreadsheetDateFormatPattern.class;
    }

    // JsonNodeMarshallingTesting...........................................................................................

    @Override
    public SpreadsheetDateFormatPattern unmarshall(final JsonNode jsonNode,
                                                   final JsonNodeUnmarshallContext context) {
        return SpreadsheetDateFormatPattern.unmarshallDateFormatPattern(jsonNode, context);
    }

    // ParseStringTesting...............................................................................................

    @Override
    public SpreadsheetDateFormatPattern parseString(final String text) {
        return SpreadsheetDateFormatPattern.parseDateFormatPattern(text);
    }
}


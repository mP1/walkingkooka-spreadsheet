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
import walkingkooka.spreadsheet.format.FakeSpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetText;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateTimeParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.expression.ExpressionNumberConverters;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.math.MathContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public final class SpreadsheetDateTimeFormatPatternTest extends SpreadsheetFormatPatternTestCase<SpreadsheetDateTimeFormatPattern,
        SpreadsheetFormatDateTimeParserToken> {

    @Test
    public void testWithCurrencyFails() {
        this.withInvalidCharacterFails(this.currency());
    }

    @Test
    public void testWithDateFails() {
        this.withInvalidCharacterFails(this.date());
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
    public void testWithExponentSymbolFails() {
        this.withInvalidCharacterFails(this.exponentSymbol());
    }

    @Test
    public void testWithGroupSeparatorFails() {
        this.withInvalidCharacterFails(this.groupSeparator());
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
    public void testWithTimeFails() {
        this.withInvalidCharacterFails(this.time());
    }

    // ParseString.......................................................................................................

    @Test
    public void testParseStringNumberPatternFails() {
        this.parseStringFails("0#00", IllegalArgumentException.class);
    }


    @Test
    public void testParseGeneral() {
        this.parseString("General");
    }

    // helpers.........................................................................................................

    @Override
    SpreadsheetDateTimeFormatPattern createPattern(final ParserToken token) {
        return SpreadsheetDateTimeFormatPattern.with(token);
    }

    @Override
    String patternText() {
        return "dd/mm/yyyy hh:mm:ss.000 A/P \"text-literal\" \\!";
    }

    @Override
    SpreadsheetFormatDateTimeParserToken createFormatParserToken(final List<ParserToken> tokens,
                                                                 final String text) {
        return SpreadsheetFormatParserToken.dateTime(tokens, text);
    }

    @Override
    ParserToken parseFormatParserToken(final String text) {
        return SpreadsheetFormatParsers.dateTimeFormat()
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(TextCursors.charSequence(text), SpreadsheetFormatParserContexts.basic())
                .get();
    }

    // HasFormatter.....................................................................................................

    @Test
    public void testFormatterFormatYy() {
        this.formatAndCheck3(
                "yy",
                LocalDate.of(2000, 12, 31),
                "00"
        );
    }

    @Test
    public void testFormatterFormatYy2() {
        this.formatAndCheck3(
                "yy",
                LocalDate.of(1999, 12, 31),
                "99"
        );
    }

    @Test
    public void testFormatterFormatYyyy() {
        this.formatAndCheck3(
                "yyyy",
                LocalDate.of(2000, 12, 31),
                "2000"
        );
    }

    @Test
    public void testFormatterFormatYyyy2() {
        this.formatAndCheck3(
                "yyyy",
                LocalDate.of(1999, 12, 31),
                "1999"
        );
    }

    @Test
    public void testFormatterFormatM() {
        this.formatAndCheck3(
                "m",
                LocalDate.of(2000, 1, 31),
                "1"
        );
    }

    @Test
    public void testFormatterFormatM2() {
        this.formatAndCheck3(
                "m",
                LocalDate.of(2000, 12, 31),
                "12"
        );
    }

    @Test
    public void testFormatterFormatMm() {
        this.formatAndCheck3(
                "mm",
                LocalDate.of(2000, 1, 31),
                "01"
        );
    }

    @Test
    public void testFormatterFormatMm2() {
        this.formatAndCheck3(
                "mm",
                LocalDate.of(2000, 12, 31),
                "12"
        );
    }

    @Test
    public void testFormatterFormatMmm() {
        this.formatAndCheck3(
                "mmm",
                LocalDate.of(2000, 1, 31),
                "Jan."
        );
    }

    @Test
    public void testFormatterFormatMmm2() {
        this.formatAndCheck3(
                "mmm",
                LocalDate.of(2000, 12, 31),
                "Dec."
        );
    }

    @Test
    public void testFormatterFormatMmmm() {
        this.formatAndCheck3(
                "mmmm",
                LocalDate.of(2000, 1, 31),
                "January"
        );
    }

    @Test
    public void testFormatterFormatMmmm2() {
        this.formatAndCheck3(
                "mmmm",
                LocalDate.of(2000, 12, 31),
                "December"
        );
    }

    @Test
    public void testFormatterFormatMmmmm() {
        this.formatAndCheck3(
                "mmmmm",
                LocalDate.of(2000, 1, 31),
                "J"
        );
    }

    @Test
    public void testFormatterFormatMmmmm2() {
        this.formatAndCheck3(
                "mmmmm",
                LocalDate.of(2000, 12, 31),
                "D"
        );
    }

    @Test
    public void testFormatterFormatD() {
        this.formatAndCheck3(
                "d",
                LocalDate.of(2000, 12, 1),
                "1"
        );
    }

    @Test
    public void testFormatterFormatD2() {
        this.formatAndCheck3(
                "d",
                LocalDate.of(2000, 12, 31),
                "31"
        );
    }

    @Test
    public void testFormatterFormatDd() {
        this.formatAndCheck3(
                "dd",
                LocalDate.of(2000, 12, 1),
                "01"
        );
    }

    @Test
    public void testFormatterFormatDd2() {
        this.formatAndCheck3(
                "dd",
                LocalDate.of(2000, 12, 31),
                "31"
        );
    }

    @Test
    public void testFormatterFormatDdd() {
        this.formatAndCheck3(
                "ddd",
                LocalDate.of(2000, 12, 1),
                "Fri."
        );
    }

    @Test
    public void testFormatterFormatDdd2() {
        this.formatAndCheck3(
                "ddd",
                LocalDate.of(2000, 12, 31),
                "Sun."
        );
    }

    @Test
    public void testFormatterFormatDddd() {
        this.formatAndCheck3(
                "dddd",
                LocalDate.of(2000, 12, 1),
                "Friday"
        );
    }

    @Test
    public void testFormatterFormatDddd2() {
        this.formatAndCheck3(
                "dddd",
                LocalDate.of(2000, 12, 31),
                "Sunday"
        );
    }

    @Test
    public void testFormatterFormatDdddd() {
        this.formatAndCheck3(
                "ddddd",
                LocalDate.of(2000, 12, 1),
                "Friday"
        );
    }

    @Test
    public void testFormatterFormatDdddd2() {
        this.formatAndCheck3(
                "ddddd",
                LocalDate.of(2000, 12, 31),
                "Sunday"
        );
    }

    @Test
    public void testFormatterFormatYyyymmdd() {
        this.formatAndCheck3(
                "yyyymmdd",
                LocalDate.of(2000, 12, 31),
                "20001231"
        );
    }

    @Test
    public void testFormatterFormatYyyymmdd2() {
        this.formatAndCheck3(
                "yyyy,mm,dd",
                LocalDate.of(2000, 12, 31),
                "2000,12,31"
        );
    }

    @Test
    public void testFormatterFormatLiteral() {
        this.formatAndCheck3(
                ",",
                LocalDate.of(2000, 12, 31),
                ","
        );
    }

    private void formatAndCheck3(final String pattern,
                                 final LocalDate date,
                                 final String expected) {
        this.formatAndCheck2(
                pattern,
                LocalDateTime.of(date, LocalTime.of(12, 58, 59)),
                expected
        );
    }


    @Test
    public void testFormatterH1() {
        this.formatAndCheck4(
                "h",
                LocalTime.of(1, 58, 59),
                "1"
        );
    }

    @Test
    public void testFormatterH2() {
        this.formatAndCheck4(
                "h",
                LocalTime.of(12, 58, 59),
                "12"
        );
    }

    @Test
    public void testFormatterHh1() {
        this.formatAndCheck4(
                "hh",
                LocalTime.of(1, 58, 59),
                "01"
        );
    }

    @Test
    public void testFormatterHh2() {
        this.formatAndCheck4(
                "hh",
                LocalTime.of(12, 58, 59),
                "12"
        );
    }

    @Test
    public void testFormatterHhh() {
        this.formatAndCheck4(
                "hhh",
                LocalTime.of(12, 58, 59),
                "12"
        );
    }

    @Test
    public void testFormatterHM1() {
        this.formatAndCheck4(
                "hm",
                LocalTime.of(12, 1, 59),
                "121"
        );
    }

    @Test
    public void testFormatterHM2() {
        this.formatAndCheck4(
                "hm",
                LocalTime.of(12, 58, 59),
                "1258"
        );
    }

    @Test
    public void testFormatterHMm1() {
        this.formatAndCheck4(
                "hmm",
                LocalTime.of(12, 1, 59),
                "1201"
        );
    }

    @Test
    public void testFormatterHMm2() {
        this.formatAndCheck4(
                "hmm",
                LocalTime.of(12, 58, 59),
                "1258"
        );
    }

    @Test
    public void testFormatterHMmm() {
        this.formatAndCheck4(
                "hmmm",
                LocalTime.of(12, 58, 59),
                "1258"
        );
    }

    @Test
    public void testFormatterHMmmap12() {
        this.formatAndCheck4(
                "hmmma/p",
                LocalTime.of(12, 58, 59),
                "1258q"
        );
    }

    @Test
    public void testFormatterHMmmap23() {
        this.formatAndCheck4(
                "hmmma/p",
                LocalTime.of(23, 58, 59),
                "1158r"
        );
    }

    @Test
    public void testFormatterHMmmAmpm12Lower() {
        this.formatAndCheck4(
                "hmmmam/pm",
                LocalTime.of(12, 58, 59),
                "1258qam"
        );
    }

    @Test
    public void testFormatterHMmmAmpm23Lower() {
        this.formatAndCheck4(
                "hmmmam/pm",
                LocalTime.of(23, 58, 59),
                "1158rpm"
        );
    }

    @Test
    public void testFormatterHMmmAmpm12Upper() {
        this.formatAndCheck4(
                "hmmmAM/PM",
                LocalTime.of(12, 58, 59),
                "1258QAM"
        );
    }

    @Test
    public void testFormatterHMmmAmpm23Upper() {
        this.formatAndCheck4(
                "hmmmAM/PM",
                LocalTime.of(23, 58, 59),
                "1158RPM"
        );
    }

    @Test
    public void testFormatterS1() {
        this.formatAndCheck4(
                "s",
                LocalTime.of(12, 58, 1),
                "1"
        );
    }

    @Test
    public void testFormatterS2() {
        this.formatAndCheck4(
                "s",
                LocalTime.of(12, 58, 59),
                "59"
        );
    }

    @Test
    public void testFormatterSs1() {
        this.formatAndCheck4(
                "ss",
                LocalTime.of(12, 58, 1),
                "01"
        );
    }

    @Test
    public void testFormatterSs2() {
        this.formatAndCheck4(
                "ss",
                LocalTime.of(12, 58, 59),
                "59"
        );
    }

    @Test
    public void testFormatterSss() {
        this.formatAndCheck4(
                "sss",
                LocalTime.of(12, 58, 59),
                "59"
        );
    }

    @Test
    public void testFormatterSssDot() {
        this.formatAndCheck4(
                "sss.",
                LocalTime.of(12, 58, 59, 12345678),
                "59"
        );
    }

    @Test
    public void testFormatterSssDotZero() {
        this.formatAndCheck4(
                "sss.0",
                LocalTime.of(12, 58, 59, 123456789),
                "59d1"
        );
    }

    @Test
    public void testFormatterSssDotZeroZero() {
        this.formatAndCheck4(
                "sss.00",
                LocalTime.of(12, 58, 59, 123456789),
                "59d12"
        );
    }

    @Test
    public void testFormatterSssDotZeroZero2() {
        this.formatAndCheck4(
                "sss.00",
                LocalTime.of(12, 58, 59),
                "59d00"
        );
    }

    @Test
    public void testFormatterSssDotZeroZeroZero() {
        this.formatAndCheck4(
                "sss.000",
                LocalTime.of(12, 58, 59, 123456789),
                "59d123"
        );
    }

    @Test
    public void testFormatterFormatHhmmssDot0000() {
        this.formatAndCheck4(
                "hhmmss.0000",
                LocalTime.of(12, 58, 59, 123456789),
                "125859d1235"
        );
    }

    @Test
    public void testFormatterFormatASlashPLower() {
        this.formatAndCheck4(
                "a/p",
                LocalTime.of(12, 58, 59, 123456789),
                "q"
        );
    }

    @Test
    public void testFormatterFormatASlashPLowerPM() {
        this.formatAndCheck4(
                "a/p",
                LocalTime.of(23, 58, 59, 123456789),
                "r"
        );
    }

    @Test
    public void testFormatterFormatAmpmLower() {
        this.formatAndCheck4(
                "am/pm",
                LocalTime.of(12, 58, 59, 123456789),
                "qam"
        );
    }

    @Test
    public void testFormatterFormat12AMPM() {
        this.formatAndCheck4(
                "AM/PM",
                LocalTime.of(12, 58, 59, 123456789),
                "QAM"
        );
    }

    @Test
    public void testFormatterFormat23ampm() {
        this.formatAndCheck4(
                "am/pm",
                LocalTime.of(23, 58, 59, 123456789),
                "rpm"
        );
    }

    @Test
    public void testFormatterFormat23AMPM() {
        this.formatAndCheck4(
                "AM/PM",
                LocalTime.of(23, 58, 59, 123456789),
                "RPM"
        );
    }

    @Test
    public void testFormatterFormat12hmmap() {
        this.formatAndCheck4(
                "hhmma/p",
                LocalTime.of(12, 58, 59, 123456789),
                "1258q"
        );
    }

    @Test
    public void testFormatterFormat12hhmmAP() {
        this.formatAndCheck4(
                "hhmmA/P",
                LocalTime.of(12, 58, 59, 123456789),
                "1258Q"
        );
    }

    private void formatAndCheck4(final String pattern,
                                 final LocalTime time,
                                 final String expected) {
        this.formatAndCheck2(
                pattern,
                LocalDateTime.of(LocalDate.of(2000, 12, 31), time),
                expected
        );
    }

    @Test
    public void testFormatterYyyymmddhhmmss() {
        this.formatAndCheck2(
                "yyyy,mm,dd,hh,mm,ss",
                LocalDateTime.of(2000, 12, 31, 12, 58, 59),
                "2000,12,31,12,58,59"
        );
    }

    @Test
    public void testFormatterFormatIncludesColorName() {
        this.formatAndCheck2(
                "[red]yyyymmddhhmmss",
                LocalDateTime.of(2000, 12, 31, 12, 58, 59),
                SpreadsheetText.with("20001231125859")
                        .setColor(
                                Optional.of(RED)
                        )
        );
    }

    @Test
    public void testFormatterFormatIncludesColorNumber() {
        this.formatAndCheck2(
                "[color44]yyyymmddhhmmss",
                LocalDateTime.of(2000, 12, 31, 12, 58, 59),
                SpreadsheetText.with("20001231125859")
                        .setColor(Optional.of(RED))
        );
    }

    @Test
    public void testFormatterFormatOnePatternTrailingSeparator() {
        this.formatAndCheck2(
                "yyyymmddhhmmss;",
                LocalDateTime.of(2000, 12, 31, 12, 58, 59),
                SpreadsheetText.with("20001231125859")
        );
    }

    @Test
    public void testFormatterFormatFirstPattern() {
        this.formatAndCheck2(
                "[>0]yyyymmddhhmmss;yyyy",
                LocalDateTime.of(2000, 12, 31, 12, 58, 59),
                SpreadsheetText.with("20001231125859")
        );
    }

    @Test
    public void testFormatterFormatSecondPattern() {
        this.formatAndCheck2(
                "[=0]yyyyy;ddmmyyyyhhmmss",
                LocalDateTime.of(2000, 12, 31, 12, 58, 59),
                SpreadsheetText.with("31122000125859")
        );
    }

    @Test
    public void testFormatterFormatSecondPatternTrailingPattern() {
        this.formatAndCheck2(
                "[=0]yyyyy;ddmmyyyyhhmmss;",
                LocalDateTime.of(2000, 12, 31, 12, 58, 59),
                SpreadsheetText.with("31122000125859")
        );
    }

    @Test
    public void testFormatterGeneral() {
        this.formatAndCheck2(
                "General",
                LocalDateTime.of(1999, 12, 31, 12, 58, 59),
                "10956d540960648"
        );
    }

    @Override
    SpreadsheetFormatterContext createContext() {
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
                                    Converters.localDateTimeToNumber()
                            ),
                            Converters.simple()
                    )
            );

            @Override
            public long dateOffset() {
                return Converters.JAVA_EPOCH_OFFSET;
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

            private DateTimeContext dateTimeContext() {
                return DateTimeContexts.locale(
                        Locale.forLanguageTag("EN-AU"),
                        1900,
                        20,
                        LocalDateTime::now
                );
            }

            @Override
            public String ampm(final int hourOfDay) {
                return hourOfDay < 13 ?
                        "QAM" :
                        "RPM";
            }

            @Override
            public char decimalSeparator() {
                return 'd';
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
                "date-time-format-pattern\n" +
                        "  \"dd/mm/yyyy hh:mm:ss.000 A/P \\\"text-literal\\\" \\\\!\"\n"
        );
    }

    @Test
    public void testTreePrintWithSeparator() {
        final String pattern = "dd/mm/yyyy;";

        this.treePrintAndCheck(
                this.createPattern(pattern),
                "date-time-format-pattern\n" +
                        "  \"dd/mm/yyyy\" ;\n"
        );
    }

    @Test
    public void testTreePrintSeveralPatterns() {
        final String pattern = "dd/mm/yyyy;dd/mmm/yyyy";

        this.treePrintAndCheck(
                this.createPattern(pattern),
                "date-time-format-pattern\n" +
                        "  \"dd/mm/yyyy\" ;\n" +
                        "  \"dd/mmm/yyyy\"\n"
        );
    }

    @Test
    public void testTreePrintSeveralPatternsAndSeparator() {
        final String pattern = "dd/mm/yyyy;dd/mmm/yyyy;";

        this.treePrintAndCheck(
                this.createPattern(pattern),
                "date-time-format-pattern\n" +
                        "  \"dd/mm/yyyy\" ;\n" +
                        "  \"dd/mmm/yyyy\" ;\n"
        );
    }

    // patterns..........................................................................................................

    @Test
    public void testPatterns() {
        final SpreadsheetDateTimeFormatPattern pattern = this.createPattern("dd/mm/yyyy hh:mm:ss");

        this.patternsAndCheck2(
                pattern,
                pattern
        );
    }

    @Test
    public void testPatternsTwo() {
        final SpreadsheetDateTimeFormatPattern pattern = this.createPattern("hh:mm:ss;hh:mm");

        this.patternsAndCheck(
                pattern,
                "hh:mm:ss",
                "hh:mm"
        );
    }

    @Test
    public void testPatternsThree() {
        final SpreadsheetDateTimeFormatPattern pattern = this.createPattern("hh:mm:ss;hh:mm;hh");

        this.patternsAndCheck(
                pattern,
                "hh:mm:ss",
                "hh:mm",
                "hh"
        );
    }

    @Test
    public void testPatternsWithColor() {
        final SpreadsheetDateTimeFormatPattern pattern = this.createPattern("[red]hh:mm:ss;[yellow]hh:mm");

        this.patternsAndCheck(
                pattern,
                "[red]hh:mm:ss",
                "[yellow]hh:mm"
        );
    }

    // removeColor.......................................................................................................

    @Test
    public void testRemoveColor() {
        final SpreadsheetDateTimeFormatPattern pattern = this.createPattern("[green]dd/mm/yyyy hh:mm:ss");

        this.removeColorAndCheck(
                pattern,
                this.createPattern("dd/mm/yyyy hh:mm:ss")
        );
    }

    // setColorName.....................................................................................................

    @Test
    public void testSetColorName() {
        final SpreadsheetDateTimeFormatPattern pattern = this.createPattern("dd/mm/yyyy hh:mm:ss");

        this.setColorNameAndCheck(
                pattern,
                SpreadsheetColorName.RED,
                "[Red]dd/mm/yyyy hh:mm:ss"
        );
    }

    @Test
    public void testSetColorNameRemovesPreviousColor() {
        final SpreadsheetDateTimeFormatPattern pattern = this.createPattern("[green]dd/mm/yyyy hh:mm:ss");

        this.setColorNameAndCheck(
                pattern,
                SpreadsheetColorName.RED,
                "[Red]dd/mm/yyyy hh:mm:ss"
        );
    }

    // setColorNumber.....................................................................................................

    @Test
    public void testSetColorNumber() {
        final SpreadsheetDateTimeFormatPattern pattern = this.createPattern("dd/mm/yyyy hh:mm:ss");

        this.setColorNumberAndCheck(
                pattern,
                12,
                "[color 12]dd/mm/yyyy hh:mm:ss"
        );
    }

    @Test
    public void testSetColorNumberRemovesPreviousColor() {
        final SpreadsheetDateTimeFormatPattern pattern = this.createPattern("[green]dd/mm/yyyy hh:mm:ss");

        this.setColorNumberAndCheck(
                pattern,
                12,
                "[color 12]dd/mm/yyyy hh:mm:ss"
        );
    }

    // removeCondition..................................................................................................

    @Test
    public void testRemoveCondition() {
        final SpreadsheetDateTimeFormatPattern pattern = this.createPattern("[<0]dd/mm/yyyy hh:mm:ss");

        this.removeConditionAndCheck(
                pattern,
                this.createPattern("dd/mm/yyyy hh:mm:ss")
        );
    }

    @Test
    public void testRemoveConditionWithColor() {
        final SpreadsheetDateTimeFormatPattern pattern = this.createPattern("[<0][White]dd/mm/yyyy hh:mm:ss");

        this.removeConditionAndCheck(
                pattern,
                this.createPattern("[White]dd/mm/yyyy hh:mm:ss")
        );
    }

    @Test
    public void testRemoveConditionConditionMissing() {
        final SpreadsheetDateTimeFormatPattern pattern = this.createPattern("dd/mm/yyyy hh:mm:ss");

        this.removeConditionAndCheck(
                pattern
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetDateTimeFormatPattern> type() {
        return SpreadsheetDateTimeFormatPattern.class;
    }

    // JsonNodeMarshallingTesting...........................................................................................

    @Override
    public SpreadsheetDateTimeFormatPattern unmarshall(final JsonNode jsonNode,
                                                       final JsonNodeUnmarshallContext context) {
        return SpreadsheetDateTimeFormatPattern.unmarshallDateTimeFormatPattern(jsonNode, context);
    }

    // ParseStringTesting...............................................................................................

    @Override
    public SpreadsheetDateTimeFormatPattern parseString(final String text) {
        return SpreadsheetDateTimeFormatPattern.parseDateTimeFormatPattern(text);
    }
}


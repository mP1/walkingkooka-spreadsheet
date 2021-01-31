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
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.spreadsheet.format.FakeSpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public final class SpreadsheetDateFormatPatternTest extends SpreadsheetFormatPatternTestCase<SpreadsheetDateFormatPattern,
        SpreadsheetFormatDateParserToken,
        LocalDate> {

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
    public void testWithThousandFails() {
        this.withInvalidCharacterFails(this.thousands());
    }

    @Test
    public void testWithTimeFails() {
        this.withInvalidCharacterFails(this.time());
    }

    // ParseString......................................................................................................

    @Test
    public void testParseStringDateTimePatternFails() {
        this.parseStringFails("ddmmyyyy hhmmss", IllegalArgumentException.class);
    }

    @Test
    public void testParseStringNumberPatternFails() {
        this.parseStringFails("0#00", IllegalArgumentException.class);
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

    @Override
    SpreadsheetFormatterContext formatterContext() {
        return new FakeSpreadsheetFormatterContext() {

            @Override
            public boolean canConvert(final Object value,
                                      final Class<?> target) {
                return Converters.localDateLocalDateTime().canConvert(value, target, ConverterContexts.fake());
            }

            @Override
            public <T> Either<T, String> convert(final Object value,
                                                 final Class<T> target) {
                return Converters.localDateLocalDateTime()
                        .convert(value, target, ConverterContexts.fake());
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
                        20
                );
            }
        };
    }

    // helpers..........................................................................................................

    @Override
    SpreadsheetDateFormatPattern createPattern(final SpreadsheetFormatDateParserToken token) {
        return SpreadsheetDateFormatPattern.with(token);
    }

    @Override
    String patternText() {
        return "ddmmyyyy \"text-literal\" \\!";
    }

    @Override
    SpreadsheetFormatDateParserToken parseFormatParserToken(final String text) {
        return SpreadsheetFormatParsers.date()
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(TextCursors.charSequence(text), SpreadsheetFormatParserContexts.basic())
                .map(SpreadsheetFormatDateParserToken.class::cast)
                .get();
    }

    @Override
    SpreadsheetFormatDateParserToken createFormatParserToken(final List<ParserToken> tokens,
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


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
import walkingkooka.convert.Converter;
import walkingkooka.convert.Converters;
import walkingkooka.spreadsheet.format.FakeSpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.parser.DateSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.spreadsheet.formula.parser.DateSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.time.LocalDate;
import java.util.List;

public final class SpreadsheetDateParsePatternTest extends SpreadsheetParsePatternTestCase<SpreadsheetDateParsePattern,
    DateSpreadsheetFormatParserToken,
    DateSpreadsheetFormulaParserToken,
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
        this.parseStringFails("ddmmyyyy hhmmss", IllegalArgumentException.class);
    }

    @Test
    public void testParseStringNumberPatternFails() {
        this.parseStringFails("0#00", IllegalArgumentException.class);
    }

    // parser...........................................................................................................

    @Test
    public void testParseDateFails() {
        this.parseFails2(
            "dd/mm/yyyy",
            "123456"
        );
    }

    @Test
    public void testParseDateOnlyPattern() {
        this.parseAndCheck2(
            "dd/mm/yyyy",
            "31/12/2000",
            day31(),
            slash(),
            month12(),
            slash(),
            year2000()
        );
    }

    @Test
    public void testParseDateOnlyPatternSeparator() {
        this.parseAndCheck2(
            "dd/mm/yyyy;",
            "31/12/2000",
            day31(),
            slash(),
            month12(),
            slash(),
            year2000()
        );
    }

    @Test
    public void testParseDateOnlyPatternTwoDigitYear() {
        this.parseAndCheck2(
            "dd/mm/yy",
            "31/12/20",
            day31(),
            slash(),
            month12(),
            slash(),
            SpreadsheetFormulaParserToken.year(20, "20")
        );
    }

    @Test
    public void testParseDateOnlyPatternDefaultsYear() {
        this.parseAndCheck2(
            "dd/mm",
            "31/12",
            day31(),
            slash(),
            month12()
        );
    }

    @Test
    public void testParseDateOnlyPatternDefaultsMonth() {
        this.parseAndCheck2(
            "dd yyyy",
            "31 2000",
            day31(),
            whitespace1(),
            year2000()
        );
    }

    @Test
    public void testParseDateOnlyPatternDefaultsDay() {
        this.parseAndCheck2(
            "mm yyyy",
            "12 2000",
            month12(),
            whitespace1(),
            year2000()
        );
    }

    @Test
    public void testParseDateFirstPattern() {
        this.parseAndCheck2(
            "dd/mm/yyyy;yyyy/mm/dd",
            "31/12/2000",
            day31(),
            slash(),
            month12(),
            slash(),
            year2000()
        );
    }

    @Test
    public void testParseDateSecondPattern() {
        this.parseAndCheck2(
            "dd/mm/yyyy;yyyy/mm/dd",
            "2000/12/31",
            year2000(),
            slash(),
            month12(),
            slash(),
            day31()
        );
    }

    @Test
    public void testParseDateSecondPatternTrailingSeparator() {
        this.parseAndCheck2(
            "dd/mm/yyyy;yyyy/mm/dd;",
            "2000/12/31",
            year2000(),
            slash(),
            month12(),
            slash(),
            day31()
        );
    }

    @Test
    public void testParseDateShortMonth() {
        this.parseAndCheck2(
            "dd/mmm/yyy",
            "31/Dec/2000",
            day31(),
            slash(),
            monthDec(),
            slash(),
            year2000()
        );
    }

    @Test
    public void testParseDateShortMonthCommas() {
        this.parseAndCheck2(
            "dd,mmm,yyyy",
            "31,Dec,2000",
            day31(),
            comma(),
            monthDec(),
            comma(),
            year2000()
        );
    }

    @Test
    public void testParseDateBackslashEscaped() {
        this.parseAndCheck2(
            "dd\\dmmm\\myyyy\\y",
            "31dDecm2000y",
            day31(),
            textLiteral("d"),
            monthDec(),
            textLiteral("m"),
            year2000(),
            textLiteral("y")
        );
    }

    // convert..........................................................................................................

    @Test
    public void testConvertDateFails() {
        this.convertFails2(
            "dd/mm/yyyy",
            "123456"
        );
    }

    @Test
    public void testConvertDateOnlyPattern() {
        this.convertAndCheck2(
            "dd/mm/yyyy",
            "31/12/2000",
            LocalDate.of(2000, 12, 31)
        );
    }

    @Test
    public void testConvertDateOnlyPatternTwoDigitYear2039() {
        this.convertAndCheck2(
            "dd/mm/yy",
            "31/12/39",
            LocalDate.of(1939, 12, 31)
        );
    }

    @Test
    public void testConvertDateOnlyPatternTwoDigitYear2019() {
        this.convertAndCheck2(
            "dd/mm/yy",
            "31/12/19",
            LocalDate.of(2019, 12, 31)
        );
    }

    @Test
    public void testConvertDateOnlyPatternTwoDigitYear1980() {
        this.convertAndCheck2(
            "dd/mm/yy",
            "31/12/80",
            LocalDate.of(1980, 12, 31)
        );
    }

    @Test
    public void testConvertDateOnlyPatternDefaultsYear() {
        this.convertAndCheck2(
            "dd/mm",
            "31/12",
            LocalDate.of(DEFAULT_YEAR, 12, 31)
        );
    }

    @Test
    public void testConvertDateOnlyPatternDefaultsMonth() {
        this.convertAndCheck2(
            "dd yyyy",
            "31 2000",
            LocalDate.of(2000, 1, 31)
        );
    }

    @Test
    public void testConvertDateOnlyPatternDefaultsDay() {
        this.convertAndCheck2(
            "mm yyyy",
            "12 2000",
            LocalDate.of(2000, 12, 1)
        );
    }

    @Test
    public void testConvertDateFirstPattern() {
        this.convertAndCheck2(
            "dd/mm/yyyy;yyyy/mm/dd",
            "31/12/2000",
            LocalDate.of(2000, 12, 31)
        );
    }

    @Test
    public void testConvertDateSecondPattern() {
        this.convertAndCheck2(
            "dd/mm/yyyy;yyyy/mm/dd",
            "2000/12/31",
            LocalDate.of(2000, 12, 31)
        );
    }

    @Test
    public void testConvertDateShortMonth() {
        this.convertAndCheck2(
            "dd/mmm/yyy",
            "31/Dec/2000",
            LocalDate.of(2000, 12, 31)
        );
    }


    // parse............................................................................................................

    @Test
    public void testParseDate() {
        this.checkEquals(
            LocalDate.of(1999, 12, 31),
            this.createPattern("yyyy/mm/dd")
                .parse(
                    "1999/12/31",
                    this.parserContext()
                )
        );
    }

    @Test
    public void testParseDateWithArabicSpreadsheetParserContext() {
        this.checkEquals(
            LocalDate.of(1999, 12, 31),
            this.createPattern("yyyy/mm/dd")
                .parse(
                    arabicDigits(1999) +
                        "/" +
                        arabicDigits(12) +
                        "/" +
                        arabicDigits(31),
                    this.parserContext(ARABIC_DECIMAL_NUMBER_CONTEXT)
                )
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
            SpreadsheetPattern.parseDateParsePattern("ddmmyy"),
            "date-parse-pattern\n" +
                "  \"ddmmyy\"\n"
        );
    }

    @Test
    public void testTreePrint2() {
        this.treePrintAndCheck(
            SpreadsheetPattern.parseDateParsePattern("ddmmyy;yymmdd"),
            "date-parse-pattern\n" +
                "  \"ddmmyy\" ;\n" +
                "  \"yymmdd\"\n"
        );
    }

    // patterns..........................................................................................................

    @Test
    public void testPatterns() {
        final SpreadsheetDateParsePattern pattern = this.createPattern("dd/mm/yyyy");

        this.patternsAndCheck2(
            pattern,
            Lists.of(pattern)
        );
    }

    @Test
    public void testPatternsTwo() {
        final SpreadsheetDateParsePattern pattern = this.createPattern("ddmmyyyy;ddmmyy");

        this.patternsAndCheck(
            pattern,
            "ddmmyyyy",
            "ddmmyy"
        );
    }

    @Test
    public void testPatternsThree() {
        final SpreadsheetDateParsePattern pattern = this.createPattern("ddmmyyyy;ddmmyy;dd/mm/yyyy");

        this.patternsAndCheck(
            pattern,
            "ddmmyyyy",
            "ddmmyy",
            "dd/mm/yyyy"
        );
    }

    // formatter........................................................................................................

    @Test
    public void testFormatterAndFormat() {
        this.formatAndCheck2(
            "yyyymmdd",
            LocalDate.of(2000, 12, 31),
            "20001231"
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

            private final Converter<FakeSpreadsheetFormatterContext> converter = Converters.localDateToLocalDateTime();

            @Override
            public char zeroDigit() {
                return zeroDigit;
            }
        };
    }

    // helpers..........................................................................................................

    @Override
    SpreadsheetDateParsePattern createPattern(final ParserToken token) {
        return SpreadsheetDateParsePattern.with(token);
    }

    @Override
    String patternText() {
        return "ddmmyyyy \"text-literal\" \\!";
    }

    @Override
    Parser<SpreadsheetFormatParserContext> parser() {
        return SpreadsheetFormatParsers.dateParse();
    }

    @Override
    DateSpreadsheetFormatParserToken createFormatParserToken(final List<ParserToken> tokens,
                                                             final String text) {
        return SpreadsheetFormatParserToken.date(tokens, text);
    }

    @Override
    DateSpreadsheetFormulaParserToken parent(final List<ParserToken> tokens,
                                             final String text) {
        return SpreadsheetFormulaParserToken.date(tokens, text);
    }

    @Override
    Class<LocalDate> targetType() {
        return LocalDate.class;
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetDateParsePattern> type() {
        return SpreadsheetDateParsePattern.class;
    }

    // JsonNodeMarshallingTesting...........................................................................................

    @Override
    public SpreadsheetDateParsePattern unmarshall(final JsonNode jsonNode,
                                                  final JsonNodeUnmarshallContext context) {
        return SpreadsheetPattern.unmarshallDateParsePattern(jsonNode, context);
    }

    // ParseStringTesting...............................................................................................

    @Override
    public SpreadsheetDateParsePattern parseString(final String text) {
        return SpreadsheetPattern.parseDateParsePattern(text);
    }
}


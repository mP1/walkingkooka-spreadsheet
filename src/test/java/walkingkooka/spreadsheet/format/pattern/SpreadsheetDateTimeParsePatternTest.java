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
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateTimeParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.spreadsheet.parser.SpreadsheetDateTimeParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserReporterException;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetDateTimeParsePatternTest extends SpreadsheetParsePatternTestCase<SpreadsheetDateTimeParsePattern,
        SpreadsheetFormatDateTimeParserToken,
        SpreadsheetDateTimeParserToken,
        LocalDateTime> {

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

    // parser...........................................................................................................

    @Test
    public void testParseDateTimeFails() {
        this.parseFails2("hhmmss ddmmss",
                "12345!");
    }

    @Test
    public void testParseHourMinutes() {
        this.parseAndCheck2(
                "hh:mm",
                "11:58",
                hour11(),
                colon(),
                minute58()
        );
    }

    @Test
    public void testParseHourMinutesSeconds() {
        this.parseAndCheck2(
                "hh:mm:ss",
                "11:58:59",
                hour11(),
                colon(),
                minute58(),
                colon(),
                second59()
        );
    }

    @Test
    public void testParseHourMinutesSecondsDecimal() {
        this.parseAndCheck2(
                "hh:mm:ss.",
                "11:58:59" + DECIMAL,
                hour11(),
                colon(),
                minute58(),
                colon(),
                second59(),
                decimalSeparator()
        );
    }

    @Test
    public void testParseHourMinutesSecondsMillis() {
        this.parseAndCheck2(
                "hh:mm:ss.0",
                "11:58:59" + DECIMAL + "1",
                hour11(),
                colon(),
                minute58(),
                colon(),
                second59(),
                decimalSeparator(),
                milli(100_000_000, "1")
        );
    }

    @Test
    public void testParseHourMinutesSeconds2Millis() {
        this.parseAndCheck2(
                "hh:mm:ss.00",
                "11:58:59" + DECIMAL + "12",
                hour11(),
                colon(),
                minute58(),
                colon(),
                second59(),
                decimalSeparator(),
                milli(120_000_000, "12")
        );
    }

    @Test
    public void testParseHourMinutesSeconds3Millis() {
        this.parseAndCheck2(
                "hh:mm:ss.000",
                "11:58:59" + DECIMAL + "123",
                hour11(),
                colon(),
                minute58(),
                colon(),
                second59(),
                decimalSeparator(),
                milli(123_000_000, "123")
        );
    }

    @Test
    public void testParseHourMinutesSeconds3Millis2() {
        this.parseAndCheck2(
                "hh:mm:ss.000",
                "11:58:59" + DECIMAL + "12",
                hour11(),
                colon(),
                minute58(),
                colon(),
                second59(),
                decimalSeparator(),
                milli(120_000_000, "12")
        );
    }

    @Test
    public void testParseHourMinutesSeconds3Millis3() {
        this.parseAndCheck2(
                "hh:mm:ss.000",
                "11:58:59" + DECIMAL + "1",
                hour11(),
                colon(),
                minute58(),
                colon(),
                second59(),
                decimalSeparator(),
                milli(100_000_000, "1")
        );
    }

    @Test
    public void testParseHourMinutesSeconds3Millis4() {
        this.parseAndCheck2(
                "hh:mm:ss.000",
                "11:58:59" + DECIMAL,
                hour11(),
                colon(),
                minute58(),
                colon(),
                second59(),
                decimalSeparator()
        );
    }

    @Test
    public void testParseHourMinutesSecondsAmpm() {
        this.parseAndCheck2(
                "hh:mm:ss AM/PM",
                "11:58:59 PM",
                hour11(),
                colon(),
                minute58(),
                colon(),
                second59(),
                whitespace1(),
                pm()
        );
    }

    @Test
    public void testParseHourMinutesSecondsMillisAmpm() {
        this.parseAndCheck2(
                "hh:mm:ss.0 AM/PM",
                "11:58:59" + DECIMAL + "1 PM",
                hour11(),
                colon(),
                minute58(),
                colon(),
                second59(),
                decimalSeparator(),
                milli(100_000_000, "1"),
                whitespace1(),
                pm()
        );
    }

    @Test
    public void testParseHourDefaultsMinutes() {
        this.parseAndCheck2(
                "hh",
                "11",
                hour11()
        );
    }

    @Test
    public void testParseDateTimeYearMonthDay() {
        this.parseAndCheck2(
                "yyyymmdd",
                "20001231",
                year2000(),
                month12(),
                day31()
        );
    }

    @Test
    public void testParseDateTimeYear() {
        this.parseAndCheck2(
                "yyyy",
                "2000",
                year2000()
        );
    }

    @Test
    public void testParseDateTimeYearMonth() {
        this.parseAndCheck2(
                "yyyymm",
                "200012",
                year2000(),
                month12()
        );
    }

    @Test
    public void testParseDateTrailingSeparator() {
        this.parseAndCheck2(
                "yyyymm;",
                "200012",
                year2000(),
                month12()
        );
    }

    @Test
    public void testParseDateTimeMultiplePatterns() {
        this.parseAndCheck2(
                "\"A\"ddmmyyyy hhmmss;\"B\"ddmmyyyy hhmmss",
                "B31122000 115859",
                textLiteral("B"),
                day31(),
                month12(),
                year2000(),
                whitespace1(),
                hour11(),
                minute58(),
                second59()
        );
    }

    @Test
    public void testParseDateTimeMultiplePatternsTrailingSeparator() {
        this.parseAndCheck2(
                "\"A\"ddmmyyyy hhmmss;\"B\"ddmmyyyy hhmmss;",
                "B31122000 115859",
                textLiteral("B"),
                day31(),
                month12(),
                year2000(),
                whitespace1(),
                hour11(),
                minute58(),
                second59()
        );
    }

    @Test
    public void testParseDateTimeCommas() {
        this.parseAndCheck2(
                "dd,mm,yyyy,hh,mm,ss",
                "31,12,2000,11,58,59",
                day31(),
                comma(),
                month12(),
                comma(),
                year2000(),
                comma(),
                hour11(),
                comma(),
                minute58(),
                comma(),
                second59()
        );
    }

    @Test
    public void testParseDateBackslashEscaped() {
        this.parseAndCheck2(
                "dd\\dmmm\\myyyy\\yhh\\hmm\\mss\\s",
                "31dDecm2000y11h58m59s",
                day31(),
                textLiteral("d"),
                monthDec(),
                textLiteral("m"),
                year2000(),
                textLiteral("y"),
                hour11(),
                textLiteral("h"),
                minute58(),
                textLiteral("m"),
                second59(),
                textLiteral("s")
        );
    }

    // general........................................................................................................

    @Test
    public void testConvertDateTimeFails() {
        this.convertFails2("hhmmss ddmmss",
                "12345!");
    }

    @Test
    public void testConvertHourMinutesOnlyPattern() {
        this.convertAndCheck2("hh:mm",
                "11:59",
                LocalDateTime.of(DEFAULT_YEAR, 1, 1, 11, 59));
    }

    @Test
    public void testConvertHourMinutesSecondsOnlyPattern() {
        this.convertAndCheck2("hh:mm:ss",
                "11:58:59",
                LocalDateTime.of(DEFAULT_YEAR, 1, 1, 11, 58, 59));
    }

    @Test
    public void testConvertHourMinutesSecondsAmpmOnlyPattern() {
        this.convertAndCheck2("hh:mm:ss AM/PM",
                "11:58:59 PM",
                LocalDateTime.of(DEFAULT_YEAR, 1, 1, 23, 58, 59));
    }

    @Test
    public void testConvertHourDefaultsMinutes() {
        this.convertAndCheck2("hh",
                "11",
                LocalDateTime.of(DEFAULT_YEAR, 1, 1, 11, 0, 0));
    }

    @Test
    public void testConvertDateTimeYearMonthDay() {
        this.convertAndCheck2("yyyymmdd",
                "20001231",
                LocalDateTime.of(2000, 12, 31, 0, 0, 0));
    }

    @Test
    public void testConvertDateTimeYear() {
        this.convertAndCheck2("yyyy",
                "2000",
                LocalDateTime.of(2000, 1, 1, 0, 0, 0));
    }

    @Test
    public void testConvertDateTimeYearMonth() {
        this.convertAndCheck2("yyyymm",
                "200012",
                LocalDateTime.of(2000, 12, 1, 0, 0, 0));
    }

    @Test
    public void testConvertDateTimeMultiplePatterns() {
        this.convertAndCheck2("\"A\"ddmmyyyy hhmmss;\"B\"ddmmyyyy hhmmss",
                "B31122000 115859",
                LocalDateTime.of(2000, 12, 31, 11, 58, 59));
    }

    // parseDateTime....................................................................................................

    @Test
    public void testParseDateTimeNullTextFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createPattern()
                        .parseDateTime(
                                null,
                                SpreadsheetParserContexts.fake()
                        )
        );
    }

    @Test
    public void testParseDateTimeNullContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createPattern()
                        .parseDateTime(
                                "1",
                                null
                        )
        );
    }

    @Test
    public void testParseDateTimeInvalidFails() {
        assertThrows(
                ParserReporterException.class,
                () -> this.createPattern()
                        .parseDateTime(
                                "1",
                                this.parserContext()
                        )
        );
    }

    @Test
    public void testParseDateTime() {
        this.checkEquals(
                LocalDateTime.of(1999, 12, 31, 12, 58),
                this.createPattern("yyyy/mm/dd/hh/mm")
                        .parseDateTime(
                                "1999/12/31/12/58",
                                this.parserContext()
                        )
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
                SpreadsheetPattern.parseDateTimeParsePattern("ddmmyyhhmmss"),
                "date-time-parse-pattern\n" +
                        "  \"ddmmyyhhmmss\"\n"
        );
    }

    @Test
    public void testTreePrint2() {
        this.treePrintAndCheck(
                SpreadsheetPattern.parseDateTimeParsePattern("ddmmyyhhmmss;yymmdd"),
                "date-time-parse-pattern\n" +
                        "  \"ddmmyyhhmmss\"\n" +
                        "  \"yymmdd\"\n"
        );
    }

    // helpers.........................................................................................................

    @Override
    SpreadsheetDateTimeParsePattern createPattern(final ParserToken token) {
        return SpreadsheetDateTimeParsePattern.with(token);
    }

    @Override
    String patternText() {
        return "dd/mm/yyyy hh:mm:ss.000 A/P \"text-literal\" \\!";
    }

    @Override
    Parser<SpreadsheetFormatParserContext> parser() {
        return SpreadsheetFormatParsers.dateTimeParse();
    }

    @Override
    SpreadsheetFormatDateTimeParserToken createFormatParserToken(final List<ParserToken> tokens,
                                                                 final String text) {
        return SpreadsheetFormatParserToken.dateTime(tokens, text);
    }

    @Override
    SpreadsheetDateTimeParserToken parent(final List<ParserToken> tokens,
                                          final String text) {
        return SpreadsheetParserToken.dateTime(tokens, text);
    }

    @Override
    Class<LocalDateTime> targetType() {
        return LocalDateTime.class;
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetDateTimeParsePattern> type() {
        return SpreadsheetDateTimeParsePattern.class;
    }

    // JsonNodeMarshallingTesting...........................................................................................

    @Override
    public SpreadsheetDateTimeParsePattern unmarshall(final JsonNode jsonNode,
                                                      final JsonNodeUnmarshallContext context) {
        return SpreadsheetDateTimeParsePattern.unmarshallDateTimeParsePattern(jsonNode, context);
    }

    // ParseStringTesting...............................................................................................

    @Override
    public SpreadsheetDateTimeParsePattern parseString(final String text) {
        return SpreadsheetDateTimeParsePattern.parseDateTimeParsePattern(text);
    }
}


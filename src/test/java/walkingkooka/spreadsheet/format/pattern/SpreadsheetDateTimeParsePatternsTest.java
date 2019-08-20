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
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.ParserTokens;
import walkingkooka.tree.json.JsonNode;

import java.time.LocalDateTime;
import java.util.List;

public final class SpreadsheetDateTimeParsePatternsTest extends SpreadsheetParsePatternsTestCase<SpreadsheetDateTimeParsePatterns,
        SpreadsheetFormatDateTimeParserToken,
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
    public void testWithNumberFails() {
        this.withInvalidCharacterFails(this.number());
    }

    @Test
    public void testWithPercentSymbolFails() {
        this.withInvalidCharacterFails(this.percentSymbol());
    }

    @Test
    public void testWithThousandFails() {
        this.withInvalidCharacterFails(this.thousands());
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
    public void testParseHourMinutesOnlyPattern() {
        this.parseAndCheck2("hh:mm",
                "11:59",
                LocalDateTime.of(1900, 1, 1, 11, 59));
    }

    @Test
    public void testParseHourMinutesSecondsOnlyPattern() {
        this.parseAndCheck2("hh:mm:ss",
                "11:58:59",
                LocalDateTime.of(1900, 1, 1, 11, 58, 59));
    }

    @Test
    public void testParseHourMinutesSecondsAmpmOnlyPattern() {
        this.parseAndCheck2("hh:mm:ss AM/PM",
                "11:58:59 PM",
                LocalDateTime.of(1900, 1, 1, 23, 58, 59));
    }

    @Test
    public void testParseHourDefaultsMinutes() {
        this.parseAndCheck2("hh",
                "11",
                LocalDateTime.of(1900, 1, 1, 11, 0, 0));
    }

    @Test
    public void testParseDateTimeYearMonthDay() {
        this.parseAndCheck2("yyyymmdd",
                "20001231",
                LocalDateTime.of(2000, 12, 31, 0, 0, 0));
    }

    @Test
    public void testParseDateTimeYear() {
        this.parseAndCheck2("yyyy",
                "2000",
                LocalDateTime.of(2000, 1, 1, 0, 0, 0));
    }

    @Test
    public void testParseDateTimeYearMonth() {
        this.parseAndCheck2("yyyymm",
                "200012",
                LocalDateTime.of(2000, 12, 1, 0, 0, 0));
    }

    @Test
    public void testParseDateTimeMultiplePatterns() {
        this.parseAndCheck2("\"A\"ddmmyyyy hhmmss;\"B\"ddmmyyyy hhmmss",
                "B31122000 115859",
                LocalDateTime.of(2000, 12, 31, 11, 58, 59));
    }

    // converter........................................................................................................

    @Test
    public void testConvertDateTimeFails() {
        this.convertFails2("hhmmss ddmmss",
                "12345!");
    }

    @Test
    public void testConvertHourMinutesOnlyPattern() {
        this.convertAndCheck2("hh:mm",
                "11:59",
                LocalDateTime.of(1900, 1, 1, 11, 59));
    }

    @Test
    public void testConvertHourMinutesSecondsOnlyPattern() {
        this.convertAndCheck2("hh:mm:ss",
                "11:58:59",
                LocalDateTime.of(1900, 1, 1, 11, 58, 59));
    }

    @Test
    public void testConvertHourMinutesSecondsAmpmOnlyPattern() {
        this.convertAndCheck2("hh:mm:ss AM/PM",
                "11:58:59 PM",
                LocalDateTime.of(1900, 1, 1, 23, 58, 59));
    }

    @Test
    public void testConvertHourDefaultsMinutes() {
        this.convertAndCheck2("hh",
                "11",
                LocalDateTime.of(1900, 1, 1, 11, 0, 0));
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

    // helpers.........................................................................................................

    @Override
    SpreadsheetDateTimeParsePatterns createPattern(final List<SpreadsheetFormatDateTimeParserToken> tokens) {
        return SpreadsheetDateTimeParsePatterns.withTokens(tokens);
    }

    @Override
    String patternText() {
        return "dd/mm/yyyy hh:mm:ss.000 A/P \"text-literal\" \\!";
    }

    @Override
    SpreadsheetFormatDateTimeParserToken parseFormatParserToken(final String text) {
        return SpreadsheetFormatParsers.dateTime()
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(TextCursors.charSequence(text), SpreadsheetFormatParserContexts.basic())
                .map(SpreadsheetFormatDateTimeParserToken.class::cast)
                .get();
    }

    @Override
    SpreadsheetFormatDateTimeParserToken createFormatParserToken(final List<ParserToken> tokens,
                                                                 final String text) {
        return SpreadsheetFormatParserToken.dateTime(tokens, text);
    }

    @Override
    ParserToken parserParserToken(final LocalDateTime value, final String text) {
        return ParserTokens.localDateTime(value, text);
    }

    @Override
    Class<LocalDateTime> targetType() {
        return LocalDateTime.class;
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetDateTimeParsePatterns> type() {
        return SpreadsheetDateTimeParsePatterns.class;
    }

    // HasJsonNodeTesting................................................................................................

    @Override
    public SpreadsheetDateTimeParsePatterns fromJsonNode(final JsonNode jsonNode) {
        return SpreadsheetDateTimeParsePatterns.fromJsonNodeDateTimeParsePatterns(jsonNode);
    }

    // ParseStringTesting...............................................................................................

    @Override
    public SpreadsheetDateTimeParsePatterns parseString(final String text) {
        return SpreadsheetDateTimeParsePatterns.parseDateTime(text);
    }
}


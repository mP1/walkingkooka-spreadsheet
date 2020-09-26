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
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTimeParserToken;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.ParserTokens;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.time.LocalTime;
import java.util.List;

public final class SpreadsheetTimeParsePatternsTest extends SpreadsheetParsePatternsTestCase<SpreadsheetTimeParsePatterns,
        SpreadsheetFormatTimeParserToken,
        LocalTime> {

    @Test
    public void testWithDateFails() {
        this.withInvalidCharacterFails(this.date());
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
    public void testWithDayFails() {
        this.withInvalidCharacterFails(this.day());
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
    public void testWithPercentSymbolFails() {
        this.withInvalidCharacterFails(this.percentSymbol());
    }

    @Test
    public void testWithNumberFails() {
        this.withInvalidCharacterFails(this.number());
    }

    @Test
    public void testWithThousandsFails() {
        this.withInvalidCharacterFails(this.thousands());
    }

    @Test
    public void testWithYearFails() {
        this.withInvalidCharacterFails(this.year());
    }

    // Parse............................................................................................................

    @Test
    public void testParseStringDatePatternFails() {
        this.parseStringFails("ddmmyyyy", IllegalArgumentException.class);
    }

    @Test
    public void testParseStringNumberPatternFails() {
        this.parseStringFails("0#00", IllegalArgumentException.class);
    }

    // parser...........................................................................................................

    @Test
    public void testParseTimeFails() {
        this.parseFails2("hhmmss",
                "12345!");
    }

    @Test
    public void testParseHourMinutesOnlyPattern() {
        this.parseAndCheck2("hh:mm",
                "11:59",
                LocalTime.of(11, 59));
    }

    @Test
    public void testParseHourMinutesSecondsOnlyPattern() {
        this.parseAndCheck2("hh:mm:ss",
                "11:58:59",
                LocalTime.of(11, 58, 59));
    }

    @Test
    public void testParseHourMinutesSecondsAmpmOnlyPattern() {
        this.parseAndCheck2("hh:mm:ss AM/PM",
                "11:58:59 PM",
                LocalTime.of(23, 58, 59));
    }

    @Test
    public void testParseHourDefaultsMinutes() {
        this.parseAndCheck2("hh",
                "11",
                LocalTime.of(11, 0, 0));
    }

    @Test
    public void testParsePatternTrailingSeparator() {
        this.parseAndCheck2("hh;",
                "11",
                LocalTime.of(11, 0, 0));
    }

    @Test
    public void testParseHourMultiplePatterns() {
        this.parseAndCheck2("\"A\"hhmmss;\"B\"hhmmss",
                "B115859",
                LocalTime.of(11, 58, 59));
    }

    @Test
    public void testParseHourMultiplePatternsTrailingSeparator() {
        this.parseAndCheck2("\"A\"hhmmss;\"B\"hhmmss;",
                "B115859",
                LocalTime.of(11, 58, 59));
    }

    @Test
    public void testParseHourComma() {
        this.parseAndCheck2("hh,mm,ss;",
                "11,58,59",
                LocalTime.of(11, 58, 59));
    }

    // converter........................................................................................................

    @Test
    public void testConvertTimeFails() {
        this.convertFails2("hhmmss",
                "12345!");
    }

    @Test
    public void testConvertHourMinutesOnlyPattern() {
        this.convertAndCheck2("hh:mm",
                "11:59",
                LocalTime.of(11, 59));
    }

    @Test
    public void testConvertHourMinutesSecondsOnlyPattern() {
        this.convertAndCheck2("hh:mm:ss",
                "11:58:59",
                LocalTime.of(11, 58, 59));
    }

    @Test
    public void testConvertHourMinutesSecondsAmpmOnlyPattern() {
        this.convertAndCheck2("hh:mm:ss AM/PM",
                "11:58:59 PM",
                LocalTime.of(23, 58, 59));
    }

    @Test
    public void testConvertHourDefaultsMinutes() {
        this.convertAndCheck2("hh",
                "11",
                LocalTime.of(11, 0, 0));
    }

    @Test
    public void testConvertHourMultiplePatterns() {
        this.convertAndCheck2("\"A\"hhmmss;\"B\"hhmmss",
                "B115859",
                LocalTime.of(11, 58, 59));
    }

    // helpers..........................................................................................................

    @Override
    SpreadsheetTimeParsePatterns createPattern(final List<SpreadsheetFormatTimeParserToken> tokens) {
        return SpreadsheetTimeParsePatterns.withTokens(tokens);
    }

    @Override
    String patternText() {
        return "hh:mm:ss.000 A/P \"text-literal\" \\!";
    }

    @Override
    SpreadsheetFormatTimeParserToken parseFormatParserToken(final String text) {
        return SpreadsheetFormatParsers.time()
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(TextCursors.charSequence(text), SpreadsheetFormatParserContexts.basic())
                .map(SpreadsheetFormatTimeParserToken.class::cast)
                .get();
    }

    @Override
    SpreadsheetFormatTimeParserToken createFormatParserToken(final List<ParserToken> tokens,
                                                             final String text) {
        return SpreadsheetFormatParserToken.time(tokens, text);
    }

    @Override
    ParserToken parserParserToken(final LocalTime value, final String text) {
        return ParserTokens.localTime(value, text);
    }

    @Override
    Class<LocalTime> targetType() {
        return LocalTime.class;
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetTimeParsePatterns> type() {
        return SpreadsheetTimeParsePatterns.class;
    }

    // JsonNodeMarshallingTesting...........................................................................................

    @Override
    public SpreadsheetTimeParsePatterns unmarshall(final JsonNode jsonNode,
                                                   final JsonNodeUnmarshallContext context) {
        return SpreadsheetTimeParsePatterns.unmarshallTimeParsePatterns(jsonNode, context);
    }

    // ParseStringTesting...............................................................................................

    @Override
    public SpreadsheetTimeParsePatterns parseString(final String text) {
        return SpreadsheetTimeParsePatterns.parseTimeParsePatterns(text);
    }
}


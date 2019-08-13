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

package walkingkooka.spreadsheet.format;

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.ParserTokens;
import walkingkooka.tree.json.JsonNode;

import java.time.LocalDate;
import java.util.List;

public final class SpreadsheetDateParsePatternsTest extends SpreadsheetParsePatternsTestCase<SpreadsheetDateParsePatterns,
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

    // parser...........................................................................................................

    @Test
    public void testParseDateFails() {
        this.parseFails2("dd/mm/yyyy;yyyy/mm/dd",
                "123456");
    }

    @Test
    public void testParseDateOnlyPattern() {
        this.parseAndCheck2("dd/mm/yyyy",
                "31/12/2000",
                LocalDate.of(2000, 12, 31));
    }

    @Test
    public void testParseDateOnlyPatternTwoDigitYear() {
        this.parseAndCheck2("dd/mm/yy",
                "31/12/20",
                LocalDate.of(2020, 12, 31));
    }

    @Test
    public void testParseDateOnlyPatternDefaultsYear() {
        this.parseAndCheck2("dd/mm",
                "31/12",
                LocalDate.of(1900, 12, 31));
    }

    @Test
    public void testParseDateOnlyPatternDefaultsMonth() {
        this.parseAndCheck2("dd yyyy",
                "31 2000",
                LocalDate.of(2000, 1, 31));
    }

    @Test
    public void testParseDateOnlyPatternDefaultsDay() {
        this.parseAndCheck2("mm yyyy",
                "12 2000",
                LocalDate.of(2000, 12, 1));
    }

    @Test
    public void testParseDateFirstPattern() {
        this.parseAndCheck2("dd/mm/yyyy;yyyy/mm/dd",
                "31/12/2000",
                LocalDate.of(2000, 12, 31));
    }

    @Test
    public void testParseDateSecondPattern() {
        this.parseAndCheck2("dd/mm/yyyy;yyyy/mm/dd",
                "2000/12/31",
                LocalDate.of(2000, 12, 31));
    }

    @Test
    public void testParseDateShortMonth() {
        this.parseAndCheck2("dd/mmm/yyy;yyyy/mm/dd",
                "31/Dec/2000",
                LocalDate.of(2000, 12, 31));
    }

    // converter........................................................................................................

    @Test
    public void testConvertDateFails() {
        this.convertFails2("dd/mm/yyyy;yyyy/mm/dd",
                "123456");
    }

    @Test
    public void testConvertDateOnlyPattern() {
        this.convertAndCheck2("dd/mm/yyyy",
                "31/12/2000",
                LocalDate.of(2000, 12, 31));
    }

    @Test
    public void testConvertDateOnlyPatternTwoDigitYear() {
        this.convertAndCheck2("dd/mm/yy",
                "31/12/20",
                LocalDate.of(2020, 12, 31));
    }

    @Test
    public void testConvertDateOnlyPatternDefaultsYear() {
        this.convertAndCheck2("dd/mm",
                "31/12",
                LocalDate.of(1900, 12, 31));
    }

    @Test
    public void testConvertDateOnlyPatternDefaultsMonth() {
        this.convertAndCheck2("dd yyyy",
                "31 2000",
                LocalDate.of(2000, 1, 31));
    }

    @Test
    public void testConvertDateOnlyPatternDefaultsDay() {
        this.convertAndCheck2("mm yyyy",
                "12 2000",
                LocalDate.of(2000, 12, 1));
    }

    @Test
    public void testConvertDateFirstPattern() {
        this.convertAndCheck2("dd/mm/yyyy;yyyy/mm/dd",
                "31/12/2000",
                LocalDate.of(2000, 12, 31));
    }

    @Test
    public void testConvertDateSecondPattern() {
        this.convertAndCheck2("dd/mm/yyyy;yyyy/mm/dd",
                "2000/12/31",
                LocalDate.of(2000, 12, 31));
    }

    @Test
    public void testConvertDateShortMonth() {
        this.convertAndCheck2("dd/mmm/yyy;yyyy/mm/dd",
                "31/Dec/2000",
                LocalDate.of(2000, 12, 31));
    }

    // helpers..........................................................................................................

    @Override
    SpreadsheetDateParsePatterns createPattern(final List<SpreadsheetFormatDateParserToken> tokens) {
        return SpreadsheetDateParsePatterns.withTokens(tokens);
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

    @Override
    ParserToken parserParserToken(final LocalDate value, final String text) {
        return ParserTokens.localDate(value, text);
    }

    @Override
    Class<LocalDate> targetType() {
        return LocalDate.class;
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetDateParsePatterns> type() {
        return SpreadsheetDateParsePatterns.class;
    }

    // HasJsonNodeTesting................................................................................................

    @Override
    public SpreadsheetDateParsePatterns fromJsonNode(final JsonNode jsonNode) {
        return SpreadsheetDateParsePatterns.fromJsonNodeDate(jsonNode);
    }

    // ParseStringTesting...............................................................................................

    @Override
    public SpreadsheetDateParsePatterns parseString(final String text) {
        return SpreadsheetDateParsePatterns.parseDate(text);
    }
}


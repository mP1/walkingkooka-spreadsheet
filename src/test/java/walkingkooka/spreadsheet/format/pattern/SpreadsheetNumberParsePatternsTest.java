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
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatNumberParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.ParserTokens;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.math.BigDecimal;
import java.util.List;

public final class SpreadsheetNumberParsePatternsTest extends SpreadsheetParsePatternsTestCase<SpreadsheetNumberParsePatterns,
        SpreadsheetFormatNumberParserToken,
        BigDecimal> {

    @Test
    public void testWithAmpmFails() {
        this.withInvalidCharacterFails(this.ampm());
    }

    @Test
    public void testWithDateFails() {
        this.withInvalidCharacterFails(this.date());
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
    public void testWithMonthOrMinuteFails() {
        this.withInvalidCharacterFails(this.monthOrMinute());
    }

    @Test
    public void testWithSecondsFails() {
        this.withInvalidCharacterFails(this.second());
    }

    @Test
    public void testWithTimeFails() {
        this.withInvalidCharacterFails(this.time());
    }

    @Test
    public void testWithYearFails() {
        this.withInvalidCharacterFails(this.year());
    }

    // Parse............................................................................................................

    @Test
    public void testParseStringDatePatternFails() {
        this.parseStringFails("dd/mm/yyyy", IllegalArgumentException.class);
    }

    @Test
    public void testParseStringDateTimePatternFails() {
        this.parseStringFails("dd/mm/yyyy hh:mm:sss", IllegalArgumentException.class);
    }

    @Test
    public void testParseStringTimePatternFails() {
        this.parseStringFails("hh:mm:sss", IllegalArgumentException.class);
    }

    // converter........................................................................................................

    @Test
    public void testConvertFails() {
        this.convertFails2("#.00",
                "abc123");
    }

    @Test
    public void testConvertFails2() {
        this.convertFails2("$ #.00",
                "1.23");
    }

    @Test
    public void testConvertNumber() {
        this.convertAndCheck2("#.00",
                "1.23",
                BigDecimal.valueOf(1.23));
    }

    @Test
    public void testConvertNumberWithCurrency() {
        this.convertAndCheck2("$ #.00",
                "$ 1.23",
                BigDecimal.valueOf(1.23));
    }

    // parser........................................................................................................

    @Test
    public void testParseFails() {
        this.parseFails2("#.00",
                "abc123");
    }

    @Test
    public void testParseFails2() {
        this.parseFails2("$ #.00",
                "1.23");
    }

    @Test
    public void testParseNumber() {
        this.parseAndCheck2("#.00",
                "1.23",
                BigDecimal.valueOf(1.23));
    }

    @Test
    public void testParseNumberWithCurrency() {
        this.parseAndCheck2("$ #.00",
                "$ 1.23",
                BigDecimal.valueOf(1.23));
    }

    @Test
    public void testParseNumberTrailingSeparator() {
        this.parseAndCheck2("#.00;",
                "1.23",
                BigDecimal.valueOf(1.23));
    }

    @Test
    public void testParseNumberFirstPattern() {
        this.parseAndCheck2("0;0.0%",
                "9",
                BigDecimal.valueOf(9));
    }

    @Test
    public void testParseNumberSecondPattern() {
        this.parseAndCheck2("0.0;0$",
                "9",
                BigDecimal.valueOf(9 * 100));
    }

    @Test
    public void testParseNumberSecondPatternTrailingSeparator() {
        this.parseAndCheck2("$0.00;0.00;",
                "1.23",
                BigDecimal.valueOf(1.23));
    }

    // helpers.........................................................................................................

    @Override
    SpreadsheetNumberParsePatterns createPattern(final List<SpreadsheetFormatNumberParserToken> tokens) {
        return SpreadsheetNumberParsePatterns.withTokens(tokens);
    }

    @Override
    String patternText() {
        return "$ ###,##0.00 \"text-literal\" \\!";
    }

    @Override
    SpreadsheetFormatNumberParserToken parseFormatParserToken(final String text) {
        return SpreadsheetFormatParsers.number()
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(TextCursors.charSequence(text), SpreadsheetFormatParserContexts.basic())
                .map(SpreadsheetFormatNumberParserToken.class::cast)
                .get();
    }

    @Override
    SpreadsheetFormatNumberParserToken createFormatParserToken(final List<ParserToken> tokens,
                                                               final String text) {
        return SpreadsheetFormatParserToken.number(tokens, text);
    }

    @Override
    ParserToken parserParserToken(final BigDecimal value, final String text) {
        return ParserTokens.bigDecimal(value, text);
    }

    @Override
    Class<BigDecimal> targetType() {
        return BigDecimal.class;
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetNumberParsePatterns> type() {
        return SpreadsheetNumberParsePatterns.class;
    }

    // JsonNodeMarshallingTesting............................................................................................

    @Override
    public SpreadsheetNumberParsePatterns unmarshall(final JsonNode jsonNode,
                                                     final JsonNodeUnmarshallContext context) {
        return SpreadsheetNumberParsePatterns.unmarshallNumberParsePatterns(jsonNode, context);
    }

    // ParseStringTesting...............................................................................................

    @Override
    public SpreadsheetNumberParsePatterns parseString(final String text) {
        return SpreadsheetNumberParsePatterns.parseNumberParsePatterns(text);
    }
}

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
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatNumberParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.json.JsonNode;

import java.util.List;

public final class SpreadsheetNumberPatternsTest extends SpreadsheetPatternsTestCase<SpreadsheetNumberPatterns,
        SpreadsheetFormatNumberParserToken> {

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
    public void testParseDatePatternFails() {
        this.parseFails("dd/mm/yyyy", IllegalArgumentException.class);
    }

    @Test
    public void testParseDateTimePatternFails() {
        this.parseFails("dd/mm/yyyy hh:mm:sss", IllegalArgumentException.class);
    }

    @Test
    public void testParseTimePatternFails() {
        this.parseFails("hh:mm:sss", IllegalArgumentException.class);
    }

    // helpers.........................................................................................................

    @Override
    SpreadsheetNumberPatterns createPattern(final List<SpreadsheetFormatNumberParserToken> tokens) {
        return SpreadsheetNumberPatterns.withNumber0(tokens);
    }

    @Override
    String patternText() {
        return "$ ???,##0.00 \"text-literal\" \\!";
    }

    @Override
    SpreadsheetFormatNumberParserToken parseParserToken(final String text) {
        return SpreadsheetFormatParsers.number()
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(TextCursors.charSequence(text), SpreadsheetFormatParserContexts.basic())
                .map(SpreadsheetFormatNumberParserToken.class::cast)
                .get();
    }

    @Override
    SpreadsheetFormatNumberParserToken createParserToken(final List<ParserToken> tokens,
                                                         final String text) {
        return SpreadsheetFormatParserToken.number(tokens, text);
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetNumberPatterns> type() {
        return SpreadsheetNumberPatterns.class;
    }

    // HasJsonNodeTesting................................................................................................

    @Override
    public SpreadsheetNumberPatterns fromJsonNode(final JsonNode jsonNode) {
        return SpreadsheetNumberPatterns.fromJsonNodeNumber(jsonNode);
    }

    // ParseStringTesting...............................................................................................

    @Override
    public SpreadsheetNumberPatterns parse(final String text) {
        return SpreadsheetNumberPatterns.parseNumber(text);
    }
}

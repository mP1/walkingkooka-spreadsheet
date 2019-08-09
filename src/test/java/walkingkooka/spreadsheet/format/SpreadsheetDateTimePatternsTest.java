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
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateTimeParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.json.JsonNode;

import java.util.List;

public final class SpreadsheetDateTimePatternsTest extends SpreadsheetPatternsTestCase<SpreadsheetDateTimePatterns,
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

    // ParseString............................................................................................................

    @Test
    public void testParseStringNumberPatternFails() {
        this.parseStringFails("0#00", IllegalArgumentException.class);
    }

    // helpers.........................................................................................................

    @Override
    SpreadsheetDateTimePatterns createPattern(final List<SpreadsheetFormatDateTimeParserToken> tokens) {
        return SpreadsheetDateTimePatterns.withDateTime0(tokens);
    }

    @Override
    String patternText() {
        return "dd/mm/yyyy hh:mm:ss.000 A/P \"text-literal\" \\!";
    }

    @Override
    SpreadsheetFormatDateTimeParserToken parseParserToken(final String text) {
        return SpreadsheetFormatParsers.dateTime()
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(TextCursors.charSequence(text), SpreadsheetFormatParserContexts.basic())
                .map(SpreadsheetFormatDateTimeParserToken.class::cast)
                .get();
    }

    @Override
    SpreadsheetFormatDateTimeParserToken createParserToken(final List<ParserToken> tokens,
                                                           final String text) {
        return SpreadsheetFormatParserToken.dateTime(tokens, text);
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetDateTimePatterns> type() {
        return SpreadsheetDateTimePatterns.class;
    }

    // HasJsonNodeTesting................................................................................................

    @Override
    public SpreadsheetDateTimePatterns fromJsonNode(final JsonNode jsonNode) {
        return SpreadsheetDateTimePatterns.fromJsonNodeDateTime(jsonNode);
    }

    // ParseStringTesting...............................................................................................

    @Override
    public SpreadsheetDateTimePatterns parseString(final String text) {
        return SpreadsheetDateTimePatterns.parseDateTime(text);
    }
}


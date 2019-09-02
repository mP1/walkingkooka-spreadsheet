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
import walkingkooka.spreadsheet.format.FakeSpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTextParserToken;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.FromJsonNodeContext;

import java.util.List;

public final class SpreadsheetTextFormatPatternTest extends SpreadsheetFormatPatternTestCase<SpreadsheetTextFormatPattern,
        SpreadsheetFormatTextParserToken,
        String> {

    @Test
    public void testWithAmpmFails() {
        this.withInvalidCharacterFails(this.ampm());
    }

    @Test
    public void testWithBracketCloseFails() {
        this.withInvalidCharacterFails(this.bracketClose());
    }

    @Test
    public void testWithBracketOpenFails() {
        this.withInvalidCharacterFails(this.bracketOpen());
    }

    @Test
    public void testWithCurrencyFails() {
        this.withInvalidCharacterFails(this.currency());
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
    public void testWithDEcimalPointFails() {
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
        this.withInvalidCharacterFails(this.digitSpace());
    }

    @Test
    public void testWithEqualsSymbolFails() {
        this.withInvalidCharacterFails(this.equalsSymbol());
    }

    @Test
    public void testWithExponentSymbolFails() {
        this.withInvalidCharacterFails(this.exponentSymbol());
    }

    @Test
    public void testWithFractionSymbolFails() {
        this.withInvalidCharacterFails(this.fractionSymbol());
    }

    @Test
    public void testWithGreaterThanSymbolFails() {
        this.withInvalidCharacterFails(this.greaterThanSymbol());
    }

    @Test
    public void testWithGreaterThanEqualsSymbolFails() {
        this.withInvalidCharacterFails(this.greaterThanEqualsSymbol());
    }

    @Test
    public void testWithHourSymbolFails() {
        this.withInvalidCharacterFails(this.hour());
    }

    @Test
    public void testWithLessThanSymbolFails() {
        this.withInvalidCharacterFails(this.lessThanSymbol());
    }

    @Test
    public void testWithLessThanEqualsSymbolFails() {
        this.withInvalidCharacterFails(this.lessThanEqualsSymbol());
    }

    @Test
    public void testWithMonthOrMinuteSymbolFails() {
        this.withInvalidCharacterFails(this.monthOrMinute());
    }

    @Test
    public void testWithNotEqualsSymbolFails() {
        this.withInvalidCharacterFails(this.notEqualsSymbol());
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
    public void testWithSecondFails() {
        this.withInvalidCharacterFails(this.second());
    }

    @Test
    public void testWithThousandsFails() {
        this.withInvalidCharacterFails(this.thousands());
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
        this.parseStringFails("ddmmyyyy", IllegalArgumentException.class);
    }

    @Test
    public void testParseStringNumberPatternFails() {
        this.parseStringFails("0#00", IllegalArgumentException.class);
    }

    // helpers..........................................................................................................
    
    @Override
    SpreadsheetTextFormatPattern createPattern(final SpreadsheetFormatTextParserToken token) {
        return SpreadsheetTextFormatPattern.with(token);
    }

    @Override
    String patternText() {
        return "\"text-literal\" @*_";
    }

    @Override
    SpreadsheetFormatTextParserToken createFormatParserToken(final List<ParserToken> tokens,
                                                             final String text) {
        return SpreadsheetFormatParserToken.text(tokens, text);
    }

    @Override
    SpreadsheetFormatTextParserToken parseFormatParserToken(final String text) {
        return SpreadsheetFormatParsers.text()
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(TextCursors.charSequence(text), SpreadsheetFormatParserContexts.basic())
                .map(SpreadsheetFormatTextParserToken.class::cast)
                .get();
    }

    // HasFormatter.....................................................................................................

    @Test
    public void testFormatterFormat() {
        this.formatAndCheck(this.createPattern("* \"text-literal\" @").formatter(),
                "ABC123",
                new FakeSpreadsheetFormatterContext() {

                    @Override
                    public int width() {
                        return 2;
                    }
                },
                "  text-literal ABC123");
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetTextFormatPattern> type() {
        return SpreadsheetTextFormatPattern.class;
    }

    // JsonNodeMappingTesting...........................................................................................

    @Override
    public SpreadsheetTextFormatPattern fromJsonNode(final JsonNode jsonNode,
                                                     final FromJsonNodeContext context) {
        return SpreadsheetTextFormatPattern.fromJsonNodeTextFormatPattern(jsonNode, context);
    }

    // ParseStringTesting...............................................................................................

    @Override
    public SpreadsheetTextFormatPattern parseString(final String text) {
        return SpreadsheetTextFormatPattern.parseTextFormatPattern(text);
    }
}


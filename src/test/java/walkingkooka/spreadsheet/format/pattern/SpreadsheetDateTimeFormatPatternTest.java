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
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.spreadsheet.format.FakeSpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateTimeParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.map.FromJsonNodeContext;

import java.time.LocalDateTime;
import java.util.List;

public final class SpreadsheetDateTimeFormatPatternTest extends SpreadsheetFormatPatternTestCase<SpreadsheetDateTimeFormatPattern,
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

    // helpers.........................................................................................................

    @Override
    SpreadsheetDateTimeFormatPattern createPattern(final SpreadsheetFormatDateTimeParserToken token) {
        return SpreadsheetDateTimeFormatPattern.with(token);
    }

    @Override
    String patternText() {
        return "dd/mm/yyyy hh:mm:ss.000 A/P \"text-literal\" \\!";
    }
    
    @Override
    SpreadsheetFormatDateTimeParserToken createFormatParserToken(final List<ParserToken> tokens,
                                                                 final String text) {
        return SpreadsheetFormatParserToken.dateTime(tokens, text);
    }

    @Override
    SpreadsheetFormatDateTimeParserToken parseFormatParserToken(final String text) {
        return SpreadsheetFormatParsers.dateTime()
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(TextCursors.charSequence(text), SpreadsheetFormatParserContexts.basic())
                .map(SpreadsheetFormatDateTimeParserToken.class::cast)
                .get();
    }

    // HasFormatter.....................................................................................................

    @Test
    public void testFormatterFormat() {
        this.formatAndCheck(this.createPattern("yyyy mm dd hh mm ss \"abc\"").formatter(),
                LocalDateTime.of(2000, 12, 31, 12, 58, 59),
                new FakeSpreadsheetFormatterContext() {
                    @Override
                    public <T> T convert(final Object value,
                                         final Class<T> target) {
                        return Converters.simple()
                                .convert(value, target, ConverterContexts.fake());
                    }
                },
                "2000 12 31 12 58 59 abc");
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetDateTimeFormatPattern> type() {
        return SpreadsheetDateTimeFormatPattern.class;
    }

    // JsonNodeMappingTesting...........................................................................................

    @Override
    public SpreadsheetDateTimeFormatPattern fromJsonNode(final JsonNode jsonNode,
                                                         final FromJsonNodeContext context) {
        return SpreadsheetDateTimeFormatPattern.fromJsonNodeDateTimeFormatPattern(jsonNode, context);
    }

    // ParseStringTesting...............................................................................................

    @Override
    public SpreadsheetDateTimeFormatPattern parseString(final String text) {
        return SpreadsheetDateTimeFormatPattern.parseDateTimeFormatPattern(text);
    }
}


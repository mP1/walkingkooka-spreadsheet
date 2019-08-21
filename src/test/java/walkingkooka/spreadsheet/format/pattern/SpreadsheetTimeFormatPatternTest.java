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
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTimeParserToken;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.json.JsonNode;

import java.time.LocalTime;
import java.util.List;

public final class SpreadsheetTimeFormatPatternTest extends SpreadsheetFormatPatternTestCase<SpreadsheetTimeFormatPattern,
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

    // helpers..........................................................................................................
    
    @Override
    SpreadsheetTimeFormatPattern createPattern(final SpreadsheetFormatTimeParserToken token) {
        return SpreadsheetTimeFormatPattern.with(token);
    }

    @Override
    String patternText() {
        return "hh:mm:ss.000 A/P \"text-literal\" \\!";
    }

    @Override
    SpreadsheetFormatTimeParserToken createFormatParserToken(final List<ParserToken> tokens,
                                                             final String text) {
        return SpreadsheetFormatParserToken.time(tokens, text);
    }

    @Override
    SpreadsheetFormatTimeParserToken parseFormatParserToken(final String text) {
        return SpreadsheetFormatParsers.time()
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(TextCursors.charSequence(text), SpreadsheetFormatParserContexts.basic())
                .map(SpreadsheetFormatTimeParserToken.class::cast)
                .get();
    }

    // HasFormatter.....................................................................................................

    @Test
    public void testFormatterFormat() {
        this.formatAndCheck(this.createPattern("hh mm ss \"abc\"").formatter(),
                LocalTime.of(12, 58, 59),
                new FakeSpreadsheetFormatterContext() {
                    @Override
                    public <T> T convert(final Object value,
                                         final Class<T> target) {
                        return Converters.localTimeLocalDateTime().convert(value, target, ConverterContexts.fake());
                    }
                },
                "12 58 59 abc");
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetTimeFormatPattern> type() {
        return SpreadsheetTimeFormatPattern.class;
    }

    // HasJsonNodeTesting................................................................................................

    @Override
    public SpreadsheetTimeFormatPattern fromJsonNode(final JsonNode jsonNode) {
        return SpreadsheetTimeFormatPattern.fromJsonNodeTimeFormatPattern(jsonNode);
    }

    // ParseStringTesting...............................................................................................

    @Override
    public SpreadsheetTimeFormatPattern parseString(final String text) {
        return SpreadsheetTimeFormatPattern.parseTimeFormatPattern(text);
    }
}


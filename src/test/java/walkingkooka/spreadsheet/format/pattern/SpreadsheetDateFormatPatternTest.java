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
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.time.LocalDate;
import java.util.List;

public final class SpreadsheetDateFormatPatternTest extends SpreadsheetFormatPatternTestCase<SpreadsheetDateFormatPattern,
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

    // HasFormatter.....................................................................................................

    @Test
    public void testFormatterFormat() {
        this.formatAndCheck(this.createPattern("yyyy mm dd \"abc\"").formatter(),
                LocalDate.of(2000, 12, 31),
                new FakeSpreadsheetFormatterContext() {

                    @Override
                    public boolean canConvert(final Object value,
                                              final Class<?> target) {
                        return Converters.localDateLocalDateTime().canConvert(value, target, ConverterContexts.fake());
                    }

                    @Override
                    public <T> T convert(final Object value,
                                         final Class<T> target) {
                        return Converters.localDateLocalDateTime()
                                .convert(value, target, ConverterContexts.fake());
                    }
                },
                "2000 12 31 abc");
    }

    // helpers..........................................................................................................

    @Override
    SpreadsheetDateFormatPattern createPattern(final SpreadsheetFormatDateParserToken token) {
        return SpreadsheetDateFormatPattern.with(token);
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

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetDateFormatPattern> type() {
        return SpreadsheetDateFormatPattern.class;
    }

    // JsonNodeMarshallingTesting...........................................................................................

    @Override
    public SpreadsheetDateFormatPattern unmarshall(final JsonNode jsonNode,
                                                   final JsonNodeUnmarshallContext context) {
        return SpreadsheetDateFormatPattern.unmarshallDateFormatPattern(jsonNode, context);
    }

    // ParseStringTesting...............................................................................................

    @Override
    public SpreadsheetDateFormatPattern parseString(final String text) {
        return SpreadsheetDateFormatPattern.parseDateFormatPattern(text);
    }
}


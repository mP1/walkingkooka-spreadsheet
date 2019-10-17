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
import walkingkooka.Either;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.spreadsheet.format.FakeSpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatNumberParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.math.MathContext;
import java.util.List;

public final class SpreadsheetNumberFormatPatternTest extends SpreadsheetFormatPatternTestCase<SpreadsheetNumberFormatPattern,
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
    
    // helpers.........................................................................................................

    @Override
    SpreadsheetNumberFormatPattern createPattern(final SpreadsheetFormatNumberParserToken token) {
        return SpreadsheetNumberFormatPattern.with(token);
    }

    @Override
    String patternText() {
        return "$ ###,##0.00 \"text-literal\" \\!";
    }

    @Override
    SpreadsheetFormatNumberParserToken createFormatParserToken(final List<ParserToken> tokens,
                                                               final String text) {
        return SpreadsheetFormatParserToken.number(tokens, text);
    }

    @Override
    SpreadsheetFormatNumberParserToken parseFormatParserToken(final String text) {
        return SpreadsheetFormatParsers.number()
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(TextCursors.charSequence(text), SpreadsheetFormatParserContexts.basic())
                .map(SpreadsheetFormatNumberParserToken.class::cast)
                .get();
    }

    // HasFormatter.....................................................................................................

    @Test
    public void testFormatterFormat() {
        this.formatAndCheck(this.createPattern("#.000 \"abc\"").formatter(),
                123.5,
                new FakeSpreadsheetFormatterContext() {

                    @Override
                    public boolean canConvert(final Object value, final Class<?> target) {
                        try {
                            this.convert(value, target);
                            return true;
                        } catch (final Exception failed) {
                            return false;
                        }
                    }

                    @Override
                    public <T> Either<T, String> convert(final Object value,
                                                         final Class<T> target) {
                        return Converters.numberNumber()
                                .convert(value, target, ConverterContexts.fake());
                    }

                    @Override
                    public char decimalSeparator() {
                        return '.';
                    }

                    @Override
                    public MathContext mathContext() {
                        return MathContext.UNLIMITED;
                    }
                },
                "123.500 abc");
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetNumberFormatPattern> type() {
        return SpreadsheetNumberFormatPattern.class;
    }

    // JsonNodeMarshallingTesting............................................................................................

    @Override
    public SpreadsheetNumberFormatPattern unmarshall(final JsonNode jsonNode,
                                                     final JsonNodeUnmarshallContext context) {
        return SpreadsheetNumberFormatPattern.unmarshallNumberFormatPattern(jsonNode, context);
    }

    // ParseStringTesting...............................................................................................

    @Override
    public SpreadsheetNumberFormatPattern parseString(final String text) {
        return SpreadsheetNumberFormatPattern.parseNumberFormatPattern(text);
    }
}

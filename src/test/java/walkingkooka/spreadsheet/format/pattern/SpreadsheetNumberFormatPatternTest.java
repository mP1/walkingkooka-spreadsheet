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
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
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
        SpreadsheetFormatNumberParserToken,
        Double> {

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
    public void testFormatterFormatTextLiteral() {
        this.formatAndCheck2("\"abc\"",
                123.5,
                "abc");
    }

    @Test
    public void testFormatterFormatHash() {
        this.formatAndCheck2("#",
                0.0,
                "");
    }

    @Test
    public void testFormatterFormatHash2() {
        this.formatAndCheck2("#",
                1.0,
                "1");
    }

    @Test
    public void testFormatterFormatHash3() {
        this.formatAndCheck2("#",
                -2.0,
                "n2");
    }

    @Test
    public void testFormatterFormatHashHash() {
        this.formatAndCheck2("##",
                0.0,
                "");
    }

    @Test
    public void testFormatterFormatHashHash2() {
        this.formatAndCheck2("##",
                1.0,
                "1");
    }

    @Test
    public void testFormatterFormatHashHash3() {
        this.formatAndCheck2("##",
                -2.0,
                "n2");
    }

    @Test
    public void testFormatterFormatQuestion() {
        this.formatAndCheck2("?",
                0.0,
                " ");
    }

    @Test
    public void testFormatterFormatQuestion2() {
        this.formatAndCheck2("?",
                1.0,
                "1");
    }

    @Test
    public void testFormatterFormatQuestion3() {
        this.formatAndCheck2("?",
                -2.0,
                "n2");
    }

    @Test
    public void testFormatterFormatQuestionQuestion() {
        this.formatAndCheck2("??",
                0.0,
                "  ");
    }

    @Test
    public void testFormatterFormatQuestionQuestion2() {
        this.formatAndCheck2("??",
                1.0,
                " 1");
    }

    @Test
    public void testFormatterFormatQuestionQuestion3() {
        this.formatAndCheck2("??",
                -2.0,
                "n 2");
    }

    @Test
    public void testFormatterFormatZero() {
        this.formatAndCheck2("0",
                0.0,
                "0");
    }

    @Test
    public void testFormatterFormatZero2() {
        this.formatAndCheck2("0",
                1.0,
                "1");
    }

    @Test
    public void testFormatterFormatZero3() {
        this.formatAndCheck2("0",
                -2.0,
                "n2");
    }

    @Test
    public void testFormatterFormatZeroZero() {
        this.formatAndCheck2("00",
                0.0,
                "00");
    }

    @Test
    public void testFormatterFormatZeroZero2() {
        this.formatAndCheck2("00",
                1.0,
                "01");
    }

    @Test
    public void testFormatterFormatZeroZero3() {
        this.formatAndCheck2("00",
                -2.0,
                "n02");
    }

    @Test
    public void testFormatterFormatDecimalHash() {
        this.formatAndCheck2("0.#",
                0.0,
                "0d");
    }

    @Test
    public void testFormatterFormatDecimalHash2() {
        this.formatAndCheck2("0.#",
                1.0,
                "1d");
    }

    @Test
    public void testFormatterFormatDecimalHash3() {
        this.formatAndCheck2("0.#",
                -2.0,
                "n2d");
    }

    @Test
    public void testFormatterFormatDecimalHashHash() {
        this.formatAndCheck2("0.##",
                0.0,
                "0d");
    }

    @Test
    public void testFormatterFormatDecimalHashHash2() {
        this.formatAndCheck2("0.##",
                1.0,
                "1d");
    }

    @Test
    public void testFormatterFormatDecimalHashHash3() {
        this.formatAndCheck2("0.##",
                -2.0,
                "n2d");
    }

    @Test
    public void testFormatterFormatDecimalQuestion() {
        this.formatAndCheck2("0.?",
                0.0,
                "0d ");
    }

    @Test
    public void testFormatterFormatDecimalQuestion2() {
        this.formatAndCheck2("0.?",
                1.0,
                "1d ");
    }

    @Test
    public void testFormatterFormatDecimalQuestion3() {
        this.formatAndCheck2("0.?",
                -2.0,
                "n2d ");
    }

    @Test
    public void testFormatterFormatDecimalQuestionQuestion() {
        this.formatAndCheck2("0.??",
                0.0,
                "0d  ");
    }

    @Test
    public void testFormatterFormatDecimalQuestionQuestion2() {
        this.formatAndCheck2("0.??",
                1.0,
                "1d  ");
    }

    @Test
    public void testFormatterFormatDecimalQuestionQuestion3() {
        this.formatAndCheck2("0.??",
                -2.0,
                "n2d  ");
    }

    @Test
    public void testFormatterFormatDecimalZero() {
        this.formatAndCheck2("0.0",
                0.0,
                "0d0");
    }

    @Test
    public void testFormatterFormatDecimalZero2() {
        this.formatAndCheck2("0.0",
                1.0,
                "1d0");
    }

    @Test
    public void testFormatterFormatDecimalZero3() {
        this.formatAndCheck2("0.0",
                -2.0,
                "n2d0");
    }

    @Test
    public void testFormatterFormatDecimalZeroZero() {
        this.formatAndCheck2("0.00",
                0.0,
                "0d00");
    }

    @Test
    public void testFormatterFormatDecimalZeroZero2() {
        this.formatAndCheck2("0.00",
                1.0,
                "1d00");
    }

    @Test
    public void testFormatterFormatDecimalZeroZero3() {
        this.formatAndCheck2("0.00",
                -2.0,
                "n2d00");
    }

    @Test
    public void testFormatterFormatExponentHash() {
        this.formatAndCheck2("0E+#",
                -123.0,
                "n1x2");
    }

    @Test
    public void testFormatterFormatExponentHash2() {
        this.formatAndCheck2("0e+#",
                -123.0,
                "n1x2");
    }

    @Test
    public void testFormatterFormatExponentQuestion() {
        this.formatAndCheck2("0E+?",
                -123.0,
                "n1x2");
    }

    @Test
    public void testFormatterFormatExponentQuestion2() {
        this.formatAndCheck2("0e+?",
                -123.0,
                "n1x2");
    }

    @Test
    public void testFormatterFormatExponentZero() {
        this.formatAndCheck2("0E+0",
                -123.0,
                "n1x2");
    }

    @Test
    public void testFormatterFormatExponentZero2() {
        this.formatAndCheck2("0e+0",
                -123.0,
                "n1x2");
    }

    @Test
    public void testFormatterFormatMixed() {
        this.formatAndCheck2("\"before \"0E+#",
                -123.0,
                "before n1x2");
    }

    @Override
    SpreadsheetFormatterContext createContext() {
        return new FakeSpreadsheetFormatterContext() {

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
                return 'd';
            }

            @Override
            public String exponentSymbol() {
                return "x";
            }

            @Override
            public char negativeSign() {
                return 'n';
            }

            @Override
            public MathContext mathContext() {
                return MathContext.UNLIMITED;
            }
        };
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
                this.createPattern(),
                "number-format-pattern\n" +
                        "  \"$ ###,##0.00 \\\"text-literal\\\" \\\\!\"\n"
        );
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

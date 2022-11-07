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
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.spreadsheet.parser.SpreadsheetNumberParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;

public final class SpreadsheetNumberParsePatternsTest extends SpreadsheetParsePatternTestCase<SpreadsheetNumberParsePattern,
        SpreadsheetFormatNumberParserToken,
        SpreadsheetNumberParserToken,
        ExpressionNumber> {

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

    // general........................................................................................................

    @Test
    public void testConvertFails() {
        this.convertFails2(
                "#.00",
                "abc123"
        );
    }

    @Test
    public void testConvertFails2() {
        this.convertFails2(
                "$ #.00",
                "1" + DECIMAL + "23"
        );
    }

    @Test
    public void testConvertNumber() {
        this.convertAndCheck3(
                "#.00",
                "1" + DECIMAL + "23",
                1.23
        );
    }

    @Test
    public void testConvertNumberWithCurrency() {
        this.convertAndCheck3(
                "$#.00",
                CURRENCY + "1" + DECIMAL + "23",
                1.23
        );
    }

    private void convertAndCheck3(final String pattern,
                                  final String text,
                                  final Number value) {
        this.convertAndCheck2(
                pattern,
                text,
                EXPRESSION_NUMBER_KIND.create(value)
        );
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
                "1" + DECIMAL + "23");
    }

    @Test
    public void testParseNumber0() {
        this.parseAndCheck2(
                "#",
                "0",
                digit0()
        );
    }

    @Test
    public void testParseNumber1() {
        this.parseAndCheck2(
                "#",
                "1",
                digit1()
        );
    }

    @Test
    public void testParseNumber12() {
        this.parseAndCheck2(
                "#",
                "12",
                digit12()
        );
    }

    @Test
    public void testParseNumber0Decimal() {
        this.parseAndCheck2(
                "#.",
                "0" + DECIMAL,
                digit0(),
                decimalSeparator()
        );
    }

    @Test
    public void testParseNumber1Decimal() {
        this.parseAndCheck2(
                "#.",
                "1" + DECIMAL,
                digit1(),
                decimalSeparator()
        );
    }

    @Test
    public void testParseNumber0DecimalFive() {
        this.parseAndCheck2(
                "#.#",
                "0" + DECIMAL + "5",
                digit0(),
                decimalSeparator(),
                digit5()
        );
    }

    @Test
    public void testParseNumber0DecimalZeroFive() {
        this.parseAndCheck2(
                "#.##",
                "0" + DECIMAL + "05",
                digit0(),
                decimalSeparator(),
                digit05()
        );
    }

    @Test
    public void testParseNumber0DecimalZeroSevenFive() {
        this.parseAndCheck2(
                "#.##",
                "0" + DECIMAL + "075",
                digit0(),
                decimalSeparator(),
                digit075()
        );
    }

    @Test
    public void testParsePlusNumber0() {
        this.parseAndCheck2(
                "#",
                PLUS + "0",
                plus(),
                digit0()
        );
    }

    @Test
    public void testParsePlusNumber12() {
        this.parseAndCheck2(
                "#",
                PLUS + "12",
                plus(),
                digit12()
        );
    }

    @Test
    public void testParseMinusNumber0() {
        this.parseAndCheck2(
                "#",
                MINUS + "0",
                minus(),
                digit0()
        );
    }

    @Test
    public void testParseMinusNumber12() {
        this.parseAndCheck2(
                "#",
                MINUS + "12",
                minus(),
                digit12()
        );
    }

    @Test
    public void testParseCurrencyNumber0() {
        this.parseAndCheck2(
                "$#",
                CURRENCY + "0",
                currencyDollarSign(),
                digit0()
        );
    }

    @Test
    public void testParseCurrencyNumber12() {
        this.parseAndCheck2(
                "$#",
                CURRENCY + "12",
                currencyDollarSign(),
                digit12()
        );
    }

    @Test
    public void testParseCurrencyNumber12Decimal() {
        this.parseAndCheck2(
                "$#.",
                CURRENCY + "12" + DECIMAL,
                currencyDollarSign(),
                digit12(),
                decimalSeparator()
        );
    }

    @Test
    public void testParseCurrencyNumber0Decimal075() {
        this.parseAndCheck2(
                "$#.#",
                CURRENCY + "0" + DECIMAL + "075",
                currencyDollarSign(),
                digit0(),
                decimalSeparator(),
                digit075()
        );
    }

    @Test
    public void testParseNumberExponentNumber() {
        this.parseAndCheck2(
                "#E+#",
                "0" + EXPONENT + "1",
                digit0(),
                e(),
                digit1()
        );
    }

    @Test
    public void testParseNumberExponentPlusNumber() {
        this.parseAndCheck2(
                "#E+#",
                "0" + EXPONENT + PLUS + "1",
                digit0(),
                e(),
                plus(),
                digit1()
        );
    }

    @Test
    public void testParseNumberExponentMinusNumber() {
        this.parseAndCheck2(
                "#E+#",
                "0" + EXPONENT + MINUS + "1",
                digit0(),
                e(),
                minus(),
                digit1()
        );
    }

    @Test
    public void testParseNumberSeparator() {
        this.parseAndCheck2(
                "#;",
                "1",
                digit1()
        );
    }

    @Test
    public void testParseSecondPatternNumberDecimalNumber() {
        this.parseAndCheck2(
                "$#;#.#",
                "1" + DECIMAL + "5",
                digit1(),
                decimalSeparator(),
                digit5()
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
                SpreadsheetPattern.parseNumberParsePatterns("##.##;##"),
                "number-parse-patterns\n" +
                        "  \"##.##\"\n" +
                        "  \"##\"\n"
        );
    }

    // ToString........................................................................................................

    @Test
    public void testToString2() {
        this.toStringAndCheck(
                this.createPattern("#.##"),
                "\"#.##\""
        );
    }

    // helpers.........................................................................................................

    @Override
    SpreadsheetNumberParsePattern createPattern(final ParserToken token) {
        return SpreadsheetNumberParsePattern.with(token);
    }

    @Override
    String patternText() {
        return "$ ###,##0.00 \"text-literal\" \\!";
    }

    @Override
    Parser<SpreadsheetFormatParserContext> parser() {
        return SpreadsheetFormatParsers.numberParse();
    }

    @Override
    SpreadsheetFormatNumberParserToken createFormatParserToken(final List<ParserToken> tokens,
                                                               final String text) {
        return SpreadsheetFormatParserToken.number(tokens, text);
    }

    @Override
    SpreadsheetNumberParserToken parent(final List<ParserToken> tokens,
                                        final String text) {
        return SpreadsheetParserToken.number(tokens, text);
    }

    @Override
    Class<ExpressionNumber> targetType() {
        return ExpressionNumber.class;
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetNumberParsePattern> type() {
        return SpreadsheetNumberParsePattern.class;
    }

    // JsonNodeMarshallingTesting............................................................................................

    @Override
    public SpreadsheetNumberParsePattern unmarshall(final JsonNode jsonNode,
                                                    final JsonNodeUnmarshallContext context) {
        return SpreadsheetNumberParsePattern.unmarshallNumberParsePatterns(jsonNode, context);
    }

    // ParseStringTesting...............................................................................................

    @Override
    public SpreadsheetNumberParsePattern parseString(final String text) {
        return SpreadsheetNumberParsePattern.parseNumberParsePatterns(text);
    }
}

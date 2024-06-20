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
import walkingkooka.collect.list.Lists;
import walkingkooka.spreadsheet.format.FakeSpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatNumberParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.spreadsheet.parser.SpreadsheetNumberParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.math.MathContext;
import java.util.List;

public final class SpreadsheetNumberParsePatternTest extends SpreadsheetParsePatternTestCase<SpreadsheetNumberParsePattern,
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
    public void testWithMinuteFails() {
        this.withInvalidCharacterFails(this.minute());
    }

    @Test
    public void testWithMonthFails() {
        this.withInvalidCharacterFails(this.month());
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

    // parser........................................................................................................

    @Test
    public void testParseFails() {
        this.parseFails2("#.00",
                "abc123");
    }

    @Test
    public void testParseFails2() {
        this.parseFails2(
                "$ #.00",
                "1" + DECIMAL + "23"
        );
    }

    @Test
    public void testParseGroupSeparatorAfterDecimalFails() {
        this.parseFails2(
                "$ #.00",
                "1" + DECIMAL + GROUP_SEPARATOR + "23"
        );
    }

    @Test
    public void testParseGroupSeparatorAfterDecimalFails2() {
        this.parseFails2(
                "$ #.00",
                "1" + DECIMAL + "2" + GROUP_SEPARATOR + "3"
        );
    }

    @Test
    public void testParseGroupSeparatorWithinExponentFails() {
        this.parseFails2(
                "$ #.00",
                "1" + DECIMAL + EXPONENT + GROUP_SEPARATOR + "23"
        );
    }

    @Test
    public void testParseGroupSeparatorWithinExponentFails2() {
        this.parseFails2(
                "$ #.00",
                "1" + DECIMAL + EXPONENT + GROUP_SEPARATOR + PLUS + "23"
        );
    }

    @Test
    public void testParseGroupSeparatorWithinExponentFails3() {
        this.parseFails2(
                "$ #.00",
                "1" + DECIMAL + EXPONENT + GROUP_SEPARATOR + MINUS + "23"
        );
    }

    @Test
    public void testParseGroupSeparatorWithinExponentFails4() {
        this.parseFails2(
                "$ #.00",
                "1" + DECIMAL + "2" + EXPONENT + "2" + GROUP_SEPARATOR + "3"
        );
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

    @Test
    public void testParseIncludesGroupSeparator() {
        this.parseAndCheck2(
                "#",
                "1" + GROUP_SEPARATOR + "5",
                digit1(),
                groupSymbol(),
                digit5()
        );
    }

    @Test
    public void testParseTextLiteralDigit() {
        this.parseAndCheck2(
                "\"Number\"#",
                "Number5",
                SpreadsheetParserToken.textLiteral("Number", "Number"),
                digit5()
        );
    }

    @Test
    public void testParseWhitespaceDigit() {
        this.parseAndCheck2(
                " #",
                " 5",
                SpreadsheetParserToken.whitespace(" ", " "),
                digit5()
        );
    }

    // parse......................................................................................................

    @Test
    public void testParseNumber() {
        this.checkEquals(
                EXPRESSION_NUMBER_KIND.create(-1.25),
                this.createPattern("#.##")
                        .parse(
                                MINUS + "1" + DECIMAL + "25",
                                this.parserContext()
                        )
        );
    }

    // general........................................................................................................

    @Test
    public void testConvertUnparseableStringToExpressionNumberFails() {
        this.convertFails2(
                "#.00",
                "abc123"
        );
    }

    @Test
    public void testConvertUnparseableStringToExpressionNumberFails2() {
        this.convertFails2(
                "$ #.00",
                "1" + DECIMAL + "23"
        );
    }

    @Test
    public void testConvertStringToNumber() {
        this.convertStringToExpressionNumberAndCheck(
                "#.00",
                "1" + DECIMAL + "23",
                1.23
        );
    }

    @Test
    public void testConvertStringWithCurrencyToNumber() {
        this.convertStringToExpressionNumberAndCheck(
                "$#.00",
                CURRENCY + "1" + DECIMAL + "23",
                1.23
        );
    }

    private void convertStringToExpressionNumberAndCheck(final String pattern,
                                                         final String text,
                                                         final Number value) {
        this.convertAndCheck2(
                pattern,
                text,
                EXPRESSION_NUMBER_KIND.create(value)
        );
    }


    // TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
                SpreadsheetPattern.parseNumberParsePattern("#"),
                "number-parse-pattern\n" +
                        "  \"#\"\n"
        );
    }

    @Test
    public void testTreePrintWithSeparator() {
        final String pattern = "$0.00;";

        this.treePrintAndCheck(
                this.createPattern(pattern),
                "number-parse-pattern\n" +
                        "  \"$0.00\" ;\n"
        );
    }

    @Test
    public void testTreePrintSeveralPatterns() {
        final String pattern = "$0.0;$0.00";

        this.treePrintAndCheck(
                this.createPattern(pattern),
                "number-parse-pattern\n" +
                        "  \"$0.0\" ;\n" +
                        "  \"$0.00\"\n"
        );
    }

    @Test
    public void testTreePrintSeveralPatternsAndSeparator() {
        final String pattern = "$0.0;$0.00;";

        this.treePrintAndCheck(
                this.createPattern(pattern),
                "number-parse-pattern\n" +
                        "  \"$0.0\" ;\n" +
                        "  \"$0.00\" ;\n"
        );
    }

    // patterns.........................................................................................................

    @Test
    public void testPatterns() {
        final SpreadsheetNumberParsePattern pattern = this.createPattern("$#");

        this.patternsAndCheck2(
                pattern,
                Lists.of(pattern)
        );
    }

    @Test
    public void testPatternsTwo() {
        final SpreadsheetNumberParsePattern pattern = this.createPattern("$#.0;$#.00");

        this.patternsAndCheck(
                pattern,
                "$#.0",
                "$#.00"
        );
    }

    @Test
    public void testPatternsThree() {
        final SpreadsheetNumberParsePattern pattern = this.createPattern("$#.0;$#.00;$#.000");

        this.patternsAndCheck(
                pattern,
                "$#.0",
                "$#.00",
                "$#.000"
        );
    }

    // formatter........................................................................................................

    @Test
    public void testFormatterPatternPatternPatternWithNegativeNumber() {
        this.formatAndCheck2(
                "$0.00",
                -123,
                "cn123d00"
        );
    }

    @Override
    SpreadsheetFormatterContext createContext() {
        return new FakeSpreadsheetFormatterContext() {

            @Override
            public boolean canConvert(final Object value, final Class<?> target) {
                return value instanceof Number && target == ExpressionNumber.class;
            }

            @Override
            public <T> Either<T, String> convert(final Object value,
                                                 final Class<T> target) {
                if (value instanceof Number && ExpressionNumber.class == target) {
                    return this.successfulConversion(
                            ExpressionNumberKind.DOUBLE.create(Number.class.cast(value)),
                            target
                    );
                }

                return this.failConversion(value, target);
            }

            @Override
            public String currencySymbol() {
                return "c";
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
        return SpreadsheetNumberParsePattern.unmarshallNumberParsePattern(jsonNode, context);
    }

    // ParseStringTesting...............................................................................................

    @Override
    public SpreadsheetNumberParsePattern parseString(final String text) {
        return SpreadsheetNumberParsePattern.parseNumberParsePattern(text);
    }
}

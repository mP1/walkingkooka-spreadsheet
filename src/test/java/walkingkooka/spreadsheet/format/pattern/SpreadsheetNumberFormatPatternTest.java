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
import walkingkooka.color.Color;
import walkingkooka.spreadsheet.format.FakeSpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetText;
import walkingkooka.spreadsheet.format.parser.NumberSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.InvalidCharacterExceptionFactory;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import java.util.Optional;

public final class SpreadsheetNumberFormatPatternTest extends SpreadsheetFormatPatternTestCase<SpreadsheetNumberFormatPattern,
    NumberSpreadsheetFormatParserToken> {

    private final static String TEXT = "Text123";

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

    // helpers.........................................................................................................

    @Override
    SpreadsheetNumberFormatPattern createPattern(final ParserToken token) {
        return SpreadsheetNumberFormatPattern.with(token);
    }

    @Override
    String patternText() {
        return "$ ###,##0.00 \"text-literal\" \\!";
    }

    @Override
    NumberSpreadsheetFormatParserToken createFormatParserToken(final List<ParserToken> tokens,
                                                               final String text) {
        return SpreadsheetFormatParserToken.number(tokens, text);
    }

    @Override
    ParserToken parseFormatParserToken(final String text) {
        return SpreadsheetFormatParsers.numberFormat()
            .orFailIfCursorNotEmpty(ParserReporters.basic())
            .parse(
                TextCursors.charSequence(text),
                SpreadsheetFormatParserContexts.basic(InvalidCharacterExceptionFactory.POSITION)
            ).get();
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

    @Test
    public void testFormatterFormatIncludesColorName() {
        this.formatAndCheck2(
            "[red]#",
            -123.0,
            SpreadsheetText.with("n123")
                .setColor(
                    Optional.of(RED)
                )
        );
    }

    @Test
    public void testFormatterFormatIncludesColorNumber() {
        this.formatAndCheck2(
            "[color44]#",
            -123.0,
            SpreadsheetText.with("n123")
                .setColor(
                    Optional.of(RED)
                )
        );
    }

    // two patterns.....................................................................................................

    @Test
    public void testFormatterPatternPatternWithPositiveCurrencyNumber() {
        this.formatAndCheck2(
            "$0.0;[color44]0.00",
            123,
            "c123*0"
        );
    }

    @Test
    public void testFormatterPatternPatternWithPositiveNumber() {
        this.formatAndCheck2(
            "0.0;[color44]0.00",
            123,
            "123d0"
        );
    }

    @Test
    public void testFormatterPatternPatternWithNegativeCurrencyNumber() {
        this.formatAndCheck2(
            "[color44]0.0;$0.00",
            -123,
            "cn123*00"
        );
    }

    @Test
    public void testFormatterPatternPatternWithNegativeNumber() {
        this.formatAndCheck2(
            "[color44]0.0;0.00",
            -123,
            "n123d00"
        );
    }

    @Test
    public void testFormatterPatternPatternWithZero() {
        this.formatAndCheck2(
            "$0.0;[color44]0.00",
            0,
            "c0*0"
        );
    }

    @Test
    public void testFormatterPatternPatternWithText() {
        this.formatAndCheck2(
            "[color44]0;[color44]0.00",
            TEXT,
            TEXT
        );
    }

    // three patterns.....................................................................................................

    @Test
    public void testFormatterPatternPatternPatternWithPositiveNumber() {
        this.formatAndCheck2(
            "$0.0;[color44]0.00;[color44]0.000",
            123,
            "c123*0"
        );
    }

    @Test
    public void testFormatterPatternPatternPatternWithNegativeNumber() {
        this.formatAndCheck2(
            "[color44]0.0;$0.00;[color44]0.000",
            -123,
            "cn123*00"
        );
    }

    @Test
    public void testFormatterPatternPatternPatternWithZero() {
        this.formatAndCheck2(
            "[color44]0.0;[color44]0.00;$0.000",
            0,
            "c0*000"
        );
    }

    @Test
    public void testFormatterPatternPatternPatternWithText() {
        this.formatAndCheck2(
            "[color44]0.0;[color44]0.00;[color44]0.000",
            TEXT,
            TEXT
        );
    }

    // four patterns....................................................................................................

    @Test
    public void testFormatterPatternPatternPatternPatternWithPositiveNumber() {
        this.formatAndCheck2(
            "$0.0;[color44]0.00;[color44]0.000;[color44]@@@@",
            123,
            "c123*0"
        );
    }

    @Test
    public void testFormatterPatternPatternPatternPatternWithNegativeNumber() {
        this.formatAndCheck2(
            "[color44]0.0;$0.00;[color44]0.000;[color44]@@@@",
            -123,
            "cn123*00"
        );
    }

    @Test
    public void testFormatterPatternPatternPatternPatternWithZero() {
        this.formatAndCheck2(
            "[color44]0.0;[color44]0.00;$0.000;[color44]@@@@",
            0,
            "c0*000"
        );
    }

    @Test
    public void testFormatterPatternPatternPatternPatternWithText() {
        this.formatAndCheck2(
            "[color44]0.0;[color44]0.00;[color44]0.000;@@@@",
            TEXT,
            TEXT + TEXT + TEXT + TEXT
        );
    }

    @Test
    public void testFormatterPatternWithArabicZero() {
        this.formatAndCheck2(
            "00.00;",
            10.05,
            ARABIC_ZERO_DIGIT,
            arabicDigit(1) + arabicDigit(0) + "d" + arabicDigit(0) + arabicDigit(5)
        );
    }

    @Test
    public void testFormatterGeneral() {
        this.formatAndCheck2(
            "General",
            1.5,
            "1d5"
        );
    }

    @Test
    public void testFormatterGeneralWithArabicZero() {
        this.formatAndCheck2(
            "General",
            10.05,
            ARABIC_ZERO_DIGIT,
            arabicDigit(1) + arabicDigit(0) + "d" + arabicDigit(0) + arabicDigit(5)
        );
    }

    @Override
    SpreadsheetFormatterContext createContext(final char zeroDigit) {
        return new FakeSpreadsheetFormatterContext() {

            @Override
            public boolean canConvert(final Object value, final Class<?> target) {
                return TEXT.equals(value) && target == String.class ||
                    value instanceof Integer && target == BigDecimal.class ||
                    value instanceof Number && target == ExpressionNumber.class;
            }

            @Override
            public <T> Either<T, String> convert(final Object value,
                                                 final Class<T> target) {
                if (TEXT.equals(value) && String.class == target) {
                    return this.successfulConversion(
                        value,
                        target
                    );
                }
                if (value instanceof Integer && BigDecimal.class == target) {
                    return this.successfulConversion(
                        BigDecimal.valueOf(Integer.class.cast(value)),
                        target
                    );
                }
                if (value instanceof Number && ExpressionNumber.class == target) {
                    return this.successfulConversion(
                        ExpressionNumberKind.DOUBLE.create(Number.class.cast(value)),
                        target
                    );
                }

                return this.failConversion(value, target);
            }

            @Override
            public int generalFormatNumberDigitCount() {
                return SpreadsheetFormatterContext.DEFAULT_GENERAL_FORMAT_NUMBER_DIGIT_COUNT;
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
            public char monetaryDecimalSeparator() {
                return '*';
            }

            @Override
            public char negativeSign() {
                return 'n';
            }

            @Override
            public char zeroDigit() {
                return zeroDigit;
            }

            @Override
            public MathContext mathContext() {
                return MathContext.UNLIMITED;
            }

            @Override
            public Optional<Color> colorName(final SpreadsheetColorName name) {
                checkEquals(
                    SpreadsheetColorName.with("red"),
                    name,
                    "colorName"
                );
                return Optional.of(
                    RED
                );
            }

            @Override
            public Optional<Color> colorNumber(final int number) {
                checkEquals(
                    44,
                    number,
                    "colorNumber"
                );
                return Optional.of(
                    RED
                );
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

    @Test
    public void testTreePrintWithSeparator() {
        final String pattern = "$0.00;";

        this.treePrintAndCheck(
            this.createPattern(pattern),
            "number-format-pattern\n" +
                "  \"$0.00\" ;\n"
        );
    }

    @Test
    public void testTreePrintSeveralPatterns() {
        final String pattern = "$0.0;$0.00";

        this.treePrintAndCheck(
            this.createPattern(pattern),
            "number-format-pattern\n" +
                "  \"$0.0\" ;\n" +
                "  \"$0.00\"\n"
        );
    }

    @Test
    public void testTreePrintSeveralPatternsAndSeparator() {
        final String pattern = "$0.0;$0.00;";

        this.treePrintAndCheck(
            this.createPattern(pattern),
            "number-format-pattern\n" +
                "  \"$0.0\" ;\n" +
                "  \"$0.00\" ;\n"
        );
    }

    // patterns.........................................................................................................

    @Test
    public void testPatterns() {
        final SpreadsheetNumberFormatPattern pattern = this.createPattern("$#");

        this.patternsAndCheck2(
            pattern,
            Lists.of(pattern)
        );
    }

    @Test
    public void testPatternsTwo() {
        final SpreadsheetNumberFormatPattern pattern = this.createPattern("$#.0;$#.00");

        this.patternsAndCheck(
            pattern,
            "$#.0",
            "$#.00"
        );
    }

    @Test
    public void testPatternsThree() {
        final SpreadsheetNumberFormatPattern pattern = this.createPattern("$#.0;$#.00;$#.000");

        this.patternsAndCheck(
            pattern,
            "$#.0",
            "$#.00",
            "$#.000"
        );
    }

    @Test
    public void testPatternsWithColor() {
        final SpreadsheetNumberFormatPattern pattern = this.createPattern("$#.0;[RED]$#.00");

        this.patternsAndCheck(
            pattern,
            "$#.0",
            "[RED]$#.00"
        );
    }

    @Test
    public void testPatternsWithEqualsCondition() {
        final SpreadsheetNumberFormatPattern pattern = this.createPattern("[=10]$#.0;$#.00;$#.000");

        this.patternsAndCheck(
            pattern,
            "[=10]$#.0",
            "$#.00",
            "$#.000"
        );
    }

    @Test
    public void testPatternsWithGreaterThanCondition() {
        final SpreadsheetNumberFormatPattern pattern = this.createPattern("[>10]$#.0;$#.00;$#.000");

        this.patternsAndCheck(
            pattern,
            "[>10]$#.0",
            "$#.00",
            "$#.000"
        );
    }

    @Test
    public void testPatternsWithGreaterThanEqualsCondition() {
        final SpreadsheetNumberFormatPattern pattern = this.createPattern("[>=10]$#.0;$#.00;$#.000");

        this.patternsAndCheck(
            pattern,
            "[>=10]$#.0",
            "$#.00",
            "$#.000"
        );
    }

    @Test
    public void testPatternsWithLessThanCondition() {
        final SpreadsheetNumberFormatPattern pattern = this.createPattern("[<10]$#.0;$#.00;$#.000");

        this.patternsAndCheck(
            pattern,
            "[<10]$#.0",
            "$#.00",
            "$#.000"
        );
    }

    @Test
    public void testPatternsWithLessThanEqualsCondition() {
        final SpreadsheetNumberFormatPattern pattern = this.createPattern("[<=10]$#.0;$#.00;$#.000");

        this.patternsAndCheck(
            pattern,
            "[<=10]$#.0",
            "$#.00",
            "$#.000"
        );
    }

    @Test
    public void testPatternsWithNotEqualsCondition() {
        final SpreadsheetNumberFormatPattern pattern = this.createPattern("[<>10]$#.0;$#.00;$#.000");

        this.patternsAndCheck(
            pattern,
            "[<>10]$#.0",
            "$#.00",
            "$#.000"
        );
    }

    @Test
    public void testPatternsWithText() {
        final SpreadsheetNumberFormatPattern pattern = this.createPattern("0.00;\"Text\"");

        this.patternsAndCheck(
            pattern,
            "0.00",
            "\"Text\""
        );
    }

    @Test
    public void testPatternsWithGeneral() {
        final SpreadsheetNumberFormatPattern pattern = this.createPattern("0.00;General");

        this.patternsAndCheck(
            pattern,
            "0.00",
            "General"
        );
    }

    // removeColor.......................................................................................................

    @Test
    public void testRemoveColor() {
        final SpreadsheetNumberFormatPattern pattern = this.createPattern("[green]$0.00");

        this.removeColorAndCheck(
            pattern,
            this.createPattern("$0.00")
        );
    }

    // setColorName.....................................................................................................

    @Test
    public void testSetColorName() {
        final SpreadsheetNumberFormatPattern pattern = this.createPattern("$0.00");

        this.setColorNameAndCheck(
            pattern,
            SpreadsheetColorName.RED,
            "[Red]$0.00"
        );
    }

    @Test
    public void testSetColorNameRemovesPreviousColor() {
        final SpreadsheetNumberFormatPattern pattern = this.createPattern("[green]$0.00");

        this.setColorNameAndCheck(
            pattern,
            SpreadsheetColorName.RED,
            "[Red]$0.00"
        );
    }

    // setColorNumber.....................................................................................................

    @Test
    public void testSetColorNumber() {
        final SpreadsheetNumberFormatPattern pattern = this.createPattern("$0.00");

        this.setColorNumberAndCheck(
            pattern,
            12,
            "[color 12]$0.00"
        );
    }

    @Test
    public void testSetColorNumberRemovesPreviousColor() {
        final SpreadsheetNumberFormatPattern pattern = this.createPattern("[green]$0.00");

        this.setColorNumberAndCheck(
            pattern,
            12,
            "[color 12]$0.00"
        );
    }

    // removeCondition..................................................................................................

    @Test
    public void testRemoveCondition() {
        final SpreadsheetNumberFormatPattern pattern = this.createPattern("[<0]$0.00");

        this.removeConditionAndCheck(
            pattern,
            this.createPattern("$0.00")
        );
    }

    @Test
    public void testRemoveConditionWithColor() {
        final SpreadsheetNumberFormatPattern pattern = this.createPattern("[<0][Blue]$0.00");

        this.removeConditionAndCheck(
            pattern,
            this.createPattern("[Blue]$0.00")
        );
    }

    @Test
    public void testRemoveConditionMissing() {
        final SpreadsheetNumberFormatPattern pattern = this.createPattern("$0.00");

        this.removeConditionAndCheck(
            pattern
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

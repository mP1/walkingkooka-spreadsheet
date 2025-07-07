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
import walkingkooka.Cast;
import walkingkooka.Either;
import walkingkooka.collect.list.Lists;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.ConverterTesting;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContexts;
import walkingkooka.spreadsheet.convert.SpreadsheetConverters;
import walkingkooka.spreadsheet.format.FakeSpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.parser.NumberSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.spreadsheet.formula.parser.NumberSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolvers;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContexts;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.convert.JsonNodeConverterContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

public final class SpreadsheetNumberParsePatternTest extends SpreadsheetParsePatternTestCase<SpreadsheetNumberParsePattern,
    NumberSpreadsheetFormatParserToken,
    NumberSpreadsheetFormulaParserToken,
    ExpressionNumber> implements ConverterTesting {

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
            SpreadsheetFormulaParserToken.textLiteral("Number", "Number"),
            digit5()
        );
    }

    @Test
    public void testParseSpaceDigit() {
        this.parseAndCheck2(
            " #",
            " 5",
            SpreadsheetFormulaParserToken.textLiteral(" ", " "),
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
    public void testFormatterPatternPatternPatternWithNegativeCurrencyNumber() {
        this.formatAndCheck2(
            "$0.00",
            -123,
            "cn123*00"
        );
    }

    @Test
    public void testFormatterPatternPatternPatternWithNegativeNumber() {
        this.formatAndCheck2(
            "0.00",
            -123,
            "n123d00"
        );
    }

    @Override
    SpreadsheetFormatterContext createContext(final char zeroDigit) {
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
            public MathContext mathContext() {
                return MathContext.UNLIMITED;
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
        };
    }

    // converter........................................................................................................

    @Test
    public void testConvertPatternHashInvalidStringFails() {
        this.parsePatternAndConvertFails("#", "A");
    }

    // integer values single digit pattern..............................................................................

    @Test
    public void testConvertPatternHashBigDecimalZero() {
        this.parsePatternConvertAndCheck(
            "#",
            "0",
            BigDecimal.valueOf(0)
        );
    }

    @Test
    public void testConvertPatternHashBigDecimalPlusInteger() {
        this.parsePatternConvertAndCheck(
            "#",
            PLUS + "1",
            BigDecimal.ONE
        );
    }

    @Test
    public void testConvertPatternHashBigDecimalPlusInteger2() {
        this.parsePatternConvertAndCheck(
            "#",
            PLUS + "23",
            BigDecimal.valueOf(23)
        );
    }

    @Test
    public void testConvertPatternHashBigDecimalPlusInteger3() {
        this.parsePatternConvertAndCheck(
            "#",
            PLUS + "456",
            BigDecimal.valueOf(456)
        );
    }

    @Test
    public void testConvertPatternHashBigDecimalMinusInteger() {
        this.parsePatternConvertAndCheck(
            "#",
            MINUS + "1",
            BigDecimal.valueOf(-1)
        );
    }

    @Test
    public void testConvertPatternHashBigDecimalMinusInteger2() {
        this.parsePatternConvertAndCheck(
            "#",
            MINUS + "23",
            BigDecimal.valueOf(-23)
        );
    }

    @Test
    public void testConvertPatternHashBigDecimalMinusInteger3() {
        this.parsePatternConvertAndCheck(
            "#",
            MINUS + "456",
            BigDecimal.valueOf(-456)
        );
    }

    @Test
    public void testConvertPatternHashBigDecimalLeadingZeroInteger() {
        this.parsePatternConvertAndCheck(
            "#",
            MINUS + "0789",
            BigDecimal.valueOf(-789)
        );
    }

    @Test
    public void testConvertPatternHashBigDecimalLeadingZeroInteger2() {
        this.parsePatternConvertAndCheck(
            "#",
            MINUS + "00789",
            BigDecimal.valueOf(-789)
        );
    }

    @Test
    public void testConvertPatternQuestionMarkBigDecimalLeadingZeroInteger2() {
        this.parsePatternConvertAndCheck(
            "?",
            MINUS + "00789",
            BigDecimal.valueOf(-789)
        );
    }

    @Test
    public void testConvertPatternZeroBigDecimalLeadingZeroInteger2() {
        this.parsePatternConvertAndCheck(
            "0",
            MINUS + "00789",
            BigDecimal.valueOf(-789)
        );
    }

    @Test
    public void testConvertPatternHashHashBigDecimal() {
        this.parsePatternConvertAndCheck("##",
            "12",
            BigDecimal.valueOf(12));
    }

    @Test
    public void testConvertPatternQuestionQuestionQuestionBigDecimal() {
        this.parsePatternConvertAndCheck("???",
            "123",
            BigDecimal.valueOf(123));
    }

    @Test
    public void testConvertPatternZeroZeroZeroZeroBigDecimal() {
        this.parsePatternConvertAndCheck("0000",
            "1234",
            BigDecimal.valueOf(1234));
    }

    @Test
    public void testConvertPatternHashHashBigDecimalExtraPattern() {
        this.parsePatternConvertAndCheck(
            "##",
            "9",
            BigDecimal.valueOf(9)
        );
    }

    @Test
    public void testConvertPatternQuestionQuestionQuestionBigDecimalExtraPattern() {
        this.parsePatternConvertAndCheck(
            "???",
            "78",
            BigDecimal.valueOf(78)
        );
    }

    @Test
    public void testConvertPatternZeroZeroZeroZeroBigDecimalExtraPattern() {
        this.parsePatternConvertAndCheck(
            "0000",
            "6",
            BigDecimal.valueOf(6)
        );
    }

    // fraction values .................................................................................................

    @Test
    public void testConvertPatternHashBigDecimalFractionFails() {
        this.parsePatternAndConvertFails(
            "#",
            "0.5"
        );
    }

    @Test
    public void testConvertPatternHashDecimalHashBigDecimalFraction() {
        this.parsePatternConvertAndCheck(
            "#.#",
            "0" + DECIMAL + "5",
            BigDecimal.valueOf(0.5)
        );
    }

    @Test
    public void testConvertPatternQuestionDecimalQuestionBigDecimalFraction() {
        this.parsePatternConvertAndCheck(
            "?.?",
            "0" + DECIMAL + "5",
            BigDecimal.valueOf(0.5)
        );
    }

    @Test
    public void testConvertPatternZeroDecimalZeroBigDecimalFraction() {
        this.parsePatternConvertAndCheck(
            "0.0",
            "0" + DECIMAL + "5",
            BigDecimal.valueOf(0.5)
        );
    }

    @Test
    public void testConvertPatternZeroDecimalZeroBigDecimalFractionExtraPattern() {
        this.parsePatternConvertAndCheck(
            "0.00",
            "0" + DECIMAL + "5",
            BigDecimal.valueOf(0.5)
        );
    }

    @Test
    public void testConvertPatternZeroDecimalZeroBigDecimalFractionExtraPattern2() {
        this.parsePatternConvertAndCheck(
            "0.000",
            "0" + DECIMAL + "5",
            BigDecimal.valueOf(0.5)
        );
    }

    @Test
    public void testConvertPatternZeroDecimalZeroZeroBigDecimalFraction() {
        this.parsePatternConvertAndCheck(
            "0.00",
            "0" + DECIMAL + "56",
            BigDecimal.valueOf(0.56)
        );
    }

    @Test
    public void testConvertPatternZeroDecimalZeroZeroZeroBigDecimalFraction() {
        this.parsePatternConvertAndCheck(
            "0.000",
            "0" + DECIMAL + "56",
            BigDecimal.valueOf(0.56)
        );
    }

    // mixed patterns...................................................................................................

    @Test
    public void testConvertPatternHashQuestionZeroBigDecimalDigit() {
        this.parsePatternConvertAndCheck(
            "#?0",
            "1",
            BigDecimal.ONE
        );
    }

    @Test
    public void testConvertPatternHashQuestionZeroBigDecimalSpaceDigit() {
        this.parsePatternConvertAndCheck(
            "#?0",
            " 1",
            BigDecimal.ONE
        );
    }

    @Test
    public void testConvertPatternHashQuestionZeroBigDecimalDigitSpaceDigit() {
        this.parsePatternConvertAndCheck(
            "#?0",
            "0 1",
            BigDecimal.ONE
        );
    }

    @Test
    public void testConvertPatternHashQuestionZeroBigDecimalDigitSpaceDigit2() {
        this.parsePatternConvertAndCheck(
            "#?0",
            "3 4",
            BigDecimal.valueOf(34)
        );
    }

    // exponent.........................................................................................................

    @Test
    public void testConvertPatternHashExponentPlusHashBigDecimalDigitExponentDigit() {
        this.parsePatternConvertAndCheck(
            "#E+#",
            "2" + EXPONENT + PLUS + "3",
            BigDecimal.valueOf(2000)
        );
    }

    @Test
    public void testConvertPatternHashExponentMinusHashBigDecimalDigitExponentDigit() {
        this.parsePatternConvertAndCheck(
            "#E+#",
            "2" + EXPONENT + PLUS + "3",
            BigDecimal.valueOf(2000)
        );
    }

    @Test
    public void testConvertPatternHashExponentPlusHashBigDecimalDigitExponentDigit2() {
        this.parsePatternConvertAndCheck(
            "#E+#",
            "2" + EXPONENT + PLUS + "3",
            BigDecimal.valueOf(2000)
        );
    }

    @Test
    public void testConvertPatternHashExponentPlusHashBigDecimalDigitExponentPlusDigit() {
        this.parsePatternConvertAndCheck(
            "#E+#",
            "4" + EXPONENT + PLUS + "5",
            new BigDecimal("4E+5")
        );
    }

    @Test
    public void testConvertPatternHashExponentPlusHashBigDecimalDigitExponentMinusDigit() {
        this.parsePatternConvertAndCheck(
            "#E+#",
            "6" + EXPONENT + MINUS + "7",
            new BigDecimal("6E-7")
        );
    }

    @Test
    public void testConvertPatternHashExponentPlusHashHashBigDecimalDigitExponentDigit() {
        this.parsePatternConvertAndCheck(
            "#E+##",
            "8" + EXPONENT + PLUS + "90",
            new BigDecimal("8E+90")
        );
    }

    @Test
    public void testConvertPatternHashExponentPlusQuestionBigDecimalDigitExponentSpaceDigit() {
        this.parsePatternConvertAndCheck(
            "#E+?",
            "1" + EXPONENT + " 2",
            new BigDecimal("1E+2")
        );
    }

    // currency.........................................................................................................

    @Test
    public void testConvertPatternCurrencyHashBigDecimal() {
        this.parsePatternConvertAndCheck("$#",
            CURRENCY + "1",
            BigDecimal.ONE);
    }

    @Test
    public void testConvertPatternHashCurrencyBigDecimal() {
        this.parsePatternConvertAndCheck(
            "#$",
            "1" + CURRENCY,
            BigDecimal.ONE
        );
    }

    // percent..........................................................................................................

    @Test
    public void testConvertPatternPercentHashBigDecimalPercentDigit() {
        this.parsePatternConvertAndCheck(
            "%#",
            PERCENT + "1",
            BigDecimal.valueOf(0.01)
        );
    }

    @Test
    public void testConvertPatternHashPercentBigDecimalDigitPercent() {
        this.parsePatternConvertAndCheck(
            "#%",
            "12" + PERCENT,
            BigDecimal.valueOf(0.12)
        );
    }

    @Test
    public void testConvertPatternHashPercentBigDecimalDigitDigitDigitPercent() {
        this.parsePatternConvertAndCheck("#%",
            "123" + PERCENT,
            BigDecimal.valueOf(1.23));
    }

    @Test
    public void testConvertPatternHashDecimalPercentBigDecimalDigitDigitDigitPercent() {
        this.parsePatternConvertAndCheck("#.#%",
            "45" + DECIMAL + "6" + PERCENT,
            BigDecimal.valueOf(0.456));
    }

    // several patterns.................................................................................................

    @Test
    public void testConvertPatternFirstPatternMatches() {
        this.parsePatternConvertAndCheck("0;\"text-literal\"",
            "1",
            BigDecimal.ONE);
    }

    @Test
    public void testConvertPatternLastPatternMatches() {
        this.parsePatternConvertAndCheck("\"text-literal\";0",
            "2",
            BigDecimal.valueOf(2));
    }

    // other Number types...............................................................................................

    @Test
    public void testConvertPatternByte() {
        this.parsePatternConvertAndCheck(Byte.MAX_VALUE);
    }

    @Test
    public void testConvertPatternByteRangeFail() {
        this.parsePatternAndConvertFails("#",
            String.valueOf(Short.MAX_VALUE),
            Byte.class);
    }

    @Test
    public void testConvertPatternShort() {
        this.parsePatternConvertAndCheck(Short.MAX_VALUE);
    }

    @Test
    public void testConvertPatternShortRangeFail() {
        this.parsePatternAndConvertFails("#",
            String.valueOf(Integer.MAX_VALUE),
            Short.class);
    }

    @Test
    public void testConvertPatternInteger() {
        this.parsePatternConvertAndCheck(Integer.MAX_VALUE);
    }

    @Test
    public void testConvertPatternIntegerRangeFail() {
        this.parsePatternAndConvertFails("#",
            String.valueOf(Long.MAX_VALUE),
            Integer.class);
    }

    @Test
    public void testConvertPatternLong() {
        this.parsePatternConvertAndCheck(999L);
    }

    @Test
    public void testConvertPatternFloat() {
        this.parsePatternConvertAndCheck(123.5f);
    }

    @Test
    public void testConvertPatternDouble() {
        this.parsePatternConvertAndCheck(67.5);
    }

    @Test
    public void testConvertPatternBigDecimal() {
        this.parsePatternConvertAndCheck(1234567.5);
    }

    @Test
    public void testConvertPatternBigInteger() {
        this.parsePatternConvertAndCheck(1234567890);
    }

    private void parsePatternConvertAndCheck(final Number number) {
        this.parsePatternConvertAndCheck(
            "#.#;#",
            number.toString().replace('.', DECIMAL).replace('+', PLUS).replace('-', MINUS).replace("E", EXPONENT),
            number
        );
    }

    @Test
    public void testConvertPatternNumber() {
        this.convertAndCheck3(
            "##.##",
            "1" + DECIMAL + "25",
            1.25
        );
    }

    private void convertAndCheck3(final String pattern,
                                  final String text,
                                  final Number number) {
        this.convertAndCheck4(
            pattern,
            text,
            ExpressionNumberKind.BIG_DECIMAL,
            number
        );
        this.convertAndCheck4(
            pattern,
            text,
            ExpressionNumberKind.DOUBLE,
            number
        );
    }

    private void convertAndCheck4(final String pattern,
                                  final String text,
                                  final ExpressionNumberKind kind,
                                  final Number number) {
        this.convertAndCheck(
            SpreadsheetNumberParsePattern.parseNumberParsePattern(pattern).createConverter(),
            text,
            ExpressionNumber.class,
            this.createConverterContext(kind),
            kind.create(number)
        );
    }

    // helpers..........................................................................................................

    private void parsePatternAndConvertFails(final String pattern,
                                             final String text) {
        this.parsePatternAndConvertFails(pattern, text, BigDecimal.class);
    }

    private void parsePatternAndConvertFails(final String pattern,
                                             final String text,
                                             final Class<? extends Number> type) {
        this.convertFails(
            this.createConverter(pattern),
            text,
            type,
            this.createConverterContext(ExpressionNumberKind.DEFAULT)
        );
    }

    private void parsePatternConvertAndCheck(final String pattern,
                                             final String text,
                                             final Number expected) {
        this.parsePatternConvertAndCheck(
            pattern,
            text,
            Cast.to(expected.getClass()),
            expected
        );
    }

    private <N extends Number> void parsePatternConvertAndCheck(final String pattern,
                                                                final String text,
                                                                final Class<N> number,
                                                                final N expected) {
        this.convertAndCheck(
            this.createConverter(pattern),
            text,
            number,
            createConverterContext(ExpressionNumberKind.DEFAULT),
            expected
        );
    }

    private Converter<SpreadsheetConverterContext> createConverter(final String pattern) {
        return Cast.to(
            this.parseString(pattern)
                .createConverter()
        );
    }

    private SpreadsheetConverterContext createConverterContext(final ExpressionNumberKind kind) {
        return SpreadsheetConverterContexts.basic(
            SpreadsheetConverterContexts.NO_METADATA,
            SpreadsheetConverterContexts.NO_VALIDATION_REFERENCE,
            SpreadsheetConverters.textToText(),
            SpreadsheetLabelNameResolvers.fake(),
            JsonNodeConverterContexts.basic(
                ExpressionNumberConverterContexts.basic(
                    Converters.characterOrCharSequenceOrHasTextOrStringToCharacterOrCharSequenceOrString(),
                    ConverterContexts.basic(
                        Converters.JAVA_EPOCH_OFFSET, // dateOffset
                        Converters.fake(),
                        DateTimeContexts.fake(), // DateTimeContext unused
                        this.decimalNumberContext()),
                    kind
                ),
                JsonNodeMarshallUnmarshallContexts.fake()
            )
        );
    }

    // ToString.........................................................................................................

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
    NumberSpreadsheetFormatParserToken createFormatParserToken(final List<ParserToken> tokens,
                                                               final String text) {
        return SpreadsheetFormatParserToken.number(tokens, text);
    }

    @Override
    NumberSpreadsheetFormulaParserToken parent(final List<ParserToken> tokens,
                                               final String text) {
        return SpreadsheetFormulaParserToken.number(tokens, text);
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

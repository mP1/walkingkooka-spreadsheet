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

package walkingkooka.spreadsheet.format;

import org.junit.jupiter.api.Test;
import walkingkooka.Either;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.math.Fraction;
import walkingkooka.spreadsheet.format.parser.FractionSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.parser.Parser;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * In expectations all symbols are doubled, as a means to verify the context is supplying the values.
 */
public final class SpreadsheetPatternSpreadsheetFormatterFractionTest extends SpreadsheetPatternSpreadsheetFormatterTestCase<SpreadsheetPatternSpreadsheetFormatterFraction,
    FractionSpreadsheetFormatParserToken> {

    // with.............................................................................................................

    @Test
    public void testWithNullTokenFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetPatternSpreadsheetFormatterFraction.with(
                null,
                FRACTIONER
            )
        );
    }

    @Test
    public void testWithNullFractionerFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetPatternSpreadsheetFormatterFraction.with(
                this.token(),
                null
            )
        );
    }

    private FractionSpreadsheetFormatParserToken token() {
        return this.parsePatternOrFail("#/#");
    }

    // format text-literal, escaped etc.................................................................................

    @Test
    public void testFormatCloseParensFails() {
        this.parsePatternFails(")");
    }

    @Test
    public void testFormatColonFails() {
        this.parsePatternFails(":");
    }

    @Test
    public void testFormatDecimalFails() {
        this.parsePatternFails(".");
    }

    @Test
    public void testFormatMinusFails() {
        this.parsePatternFails("-");
    }

    @Test
    public void testFormatPlusFails() {
        this.parsePatternFails("+");
    }

    @Test
    public void testFormatOpenParensFails() {
        this.parsePatternFails("(");
    }

    @Test
    public void testFormatEscapedFails() {
        this.parsePatternFails("\\A");
    }

    @Test
    public void testFormatTextLiteralFails() {
        this.parsePatternFails("\"Quoted text\"");
    }

    @Test
    public void testFormatHashFails() {
        this.parsePatternFails("#");
    }

    @Test
    public void testFormatQuestionFails() {
        this.parsePatternFails("?");
    }

    @Test
    public void testFormatZeroFails() {
        this.parsePatternFails("0");
    }

    @Test
    public void testFormatHashDecimalFails() {
        this.parsePatternFails("#.");
    }

    @Test
    public void testFormatQuestionDecimalFails() {
        this.parsePatternFails("?.");
    }

    @Test
    public void testFormatZeroDecimalFails() {
        this.parsePatternFails("0.");
    }

    @Test
    public void testFormatHashSlashFails() {
        this.parsePatternFails("#/");
    }

    @Test
    public void testFormatQuestionSlashFails() {
        this.parsePatternFails("?/");
    }

    @Test
    public void testFormatZeroSlashFails() {
        this.parsePatternFails("0/");
    }

    private void parsePatternFails(final String pattern) {
        try {
            this.parsePatternOrFail(pattern);
            fail("Expected " + RuntimeException.class.getSimpleName() + " to be thrown with pattern " + CharSequences.quote(pattern));
        } catch (final RuntimeException expected) {
        }
    }

    // format...........................................................................................................

    @Test
    public void testFormatNull() {
        this.formatAndCheck(
            this.createFormatter("#/#"), // format
            Optional.empty(), // value
            Optional.empty() // expected
        );
    }

    // fraction space. space dot space .................................................................................

    @Test
    public void testFormatHashFractionHash0_0() {
        this.parseFormatAndCheck(
            "#/#",
            0.0,
            "/1"
        );
    }

    @Test
    public void testFormatHashFractionHash0_2() {
        this.parseFormatAndCheck(
            "#/#",
            0.2,
            "1/5"
        );
    }

    @Test
    public void testFormatHashFractionHash0_6() {
        this.parseFormatAndCheck(
            "#/#",
            0.6,
            "3/5"
        );
    }

    @Test
    public void testFormatHashFractionHash0_71() {
        this.parseFormatAndCheck(
            "#/#",
            0.61,
            "3/5"
        );
    }

    @Test
    public void testFormatHashFractionHash0_85() {
        this.parseFormatAndCheck(
            "#/#",
            0.85,
            "4/5"
        );
    }

    @Test
    public void testFormatHashFractionHash1_0() {
        this.parseFormatAndCheck(
            "#/#",
            1,
            "5/5"
        );
    }

    @Test
    public void testFormatHashFractionHash1_99() {
        this.parseFormatAndCheck(
            "#/#",
            1.99,
            "10/5"
        );
    }

    @Test
    public void testFormatHashFractionHash0_025() {
        this.parseFormatAndCheck(
            "#/#",
            0.025,
            "/1"
        );
    }

    // fraction space. space dot space .................................................................................

    @Test
    public void testFormatQuestionFractionQuestion0_0() {
        this.parseFormatAndCheck(
            "?/?",
            0.0,
            " /1"
        );
    }

    @Test
    public void testFormatQuestionFractionQuestion0_2() {
        this.parseFormatAndCheck(
            "?/?",
            0.2,
            "1/5"
        );
    }

    @Test
    public void testFormatQuestionFractionQuestion0_6() {
        this.parseFormatAndCheck(
            "?/?",
            0.6,
            "3/5"
        );
    }

    @Test
    public void testFormatQuestionFractionQuestion0_85() {
        this.parseFormatAndCheck(
            "?/?",
            0.85,
            "4/5"
        );
    }

    @Test
    public void testFormatQuestionFractionQuestion1_0() {
        this.parseFormatAndCheck(
            "?/?",
            1,
            "5/5"
        );
    }

    @Test
    public void testFormatQuestionFractionQuestion1_99() {
        this.parseFormatAndCheck(
            "?/?",
            1.99,
            "10/5"
        );
    }

    @Test
    public void testFormatQuestionFractionQuestion0_025() {
        this.parseFormatAndCheck(
            "?/?",
            0.025,
            " /1"
        );
    }

    // fraction zero. zero dot zero ....................................................................................

    @Test
    public void testFormatZeroFractionZero0_0() {
        this.parseFormatAndCheck(
            "0/0",
            0.0,
            "0/1"
        );
    }

    @Test
    public void testFormatZeroFractionZero0_2() {
        this.parseFormatAndCheck(
            "0/0",
            0.2,
            "1/5"
        );
    }

    @Test
    public void testFormatZeroFractionZero0_6() {
        this.parseFormatAndCheck(
            "0/0",
            0.6,
            "3/5"
        );
    }

    @Test
    public void testFormatZeroFractionZero0_71() {
        this.parseFormatAndCheck(
            "0/0",
            0.71,
            "3/5"
        );
    }

    @Test
    public void testFormatZeroFractionZero0_85() {
        this.parseFormatAndCheck(
            "0/0",
            0.85,
            "4/5"
        );
    }

    @Test
    public void testFormatZeroFractionZero1_0() {
        this.parseFormatAndCheck(
            "0/0",
            1,
            "5/5"
        );
    }

    @Test
    public void testFormatZeroFractionZero1_25() {
        this.parseFormatAndCheck(
            "0/0",
            1.25,
            "6/5"
        );
    }

    @Test
    public void testFormatZeroFractionZero1_975() {
        this.parseFormatAndCheck(
            "0/0",
            1.975,
            "10/5"
        );
    }

    @Test
    public void testFormatZeroFractionZero0_025() {
        this.parseFormatAndCheck(
            "0/0",
            0.025,
            "0/1"
        );
    }

    // long fraction patterns

    @Test
    public void testFormatHashFractionHashHashHash0_0000005() {
        this.parseFormatAndCheck(
            "#/###",
            0.0000005,
            "/1"
        );
    }

    @Test
    public void testFormatQuestionFractionQuestionQuestionQuestion0_0000005() {
        this.parseFormatAndCheck(
            "?/???",
            0.0000005,
            " /  1"
        );
    }

    @Test
    public void testFormatZeroFractionZeroZeroZero0_0000005() {
        this.parseFormatAndCheck(
            "0/000",
            0.0000005,
            "0/001"
        );
    }

    // hash space zero..................................................................................................

    @Test
    public void testFormatHashFractionSpaceHash1_05() {
        this.parseFormatAndCheck(
            "?/?#",
            1.05,
            "52/50"
        );
    }

    @Test
    public void testFormatHashFractionZeroHash1_05() {
        this.parseFormatAndCheck(
            "?/?0",
            1.05,
            "52/50"
        );
    }

    @Test
    public void testFormatHashFractionSpaceZeroHash1_00005() {
        this.parseFormatAndCheck(
            "?/?",
            1.005,
            "5/5"
        );
    }

    // currency ........................................................................................................

    @Test
    public void testFormatCurrency() {
        this.parseFormatAndCheck(
            "$0/0",
            0.4,
            "D2/5"
        );
    }

    // percentage ......................................................................................................

    @Test
    public void testFormatPercentage0() {
        this.parseFormatAndCheck(
            "0/0%",
            0,
            "0/1%"
        );
    }

    @Test
    public void testFormatPercentagePositive1() {
        this.parseFormatAndCheck(
            "0/0%",
            1,
            "5/5%"
        );
    }

    @Test
    public void testFormatPercentageNegative1() {
        this.parseFormatAndCheck(
            "0/0%",
            -1,
            "M5/5%"
        );
    }

    // thousands divider ...............................................................................................

    @Test
    public void testFormatThousandsDividerDigitComma0() {
        this.parseFormatAndCheck(
            "0/0,",
            1,
            "5/5,"
        );
    }

    @Test
    public void testFormatThousandsDividerCommaSlash12345() {
        this.parseFormatAndCheck(
            "0,/#",
            12345,
            "61725,/5"
        );
    }

    @Test
    public void testFormatThousandsDividerCommaTextLiteralSlash12345() {
        this.parseFormatAndCheck(
            "0,\"Text\"/#",
            12345,
            "61725,Text/5"
        );
    }

    @Test
    public void testFormatThousandsDividerCommaCommaSlash123456789() {
        this.parseFormatAndCheck(
            "0,,/#",
            123456789,
            "617283945,,/5"
        );
    }

    @Test
    public void testFormatWithArabicDigits() {
        this.formatAndCheck(
            this.createFormatter("0/0"),
            new BigDecimal(12),
            this.createContext(ARABIC_ZERO_DIGIT),
            arabicDigit(6) +
                arabicDigit(0) +
                "/" +
                arabicDigit(5)
        );
    }

    @Test
    public void testFormatWithArabicDigits2() {
        // 6150/50
        this.formatAndCheck(
            this.createFormatter("00/00"),
            new BigDecimal(123),
            this.createContext(ARABIC_ZERO_DIGIT),
            arabicDigit(6) +
                arabicDigit(1) +
                arabicDigit(5) +
                arabicDigit(0) +
                "/" +
                arabicDigit(5) +
                arabicDigit(0)
        );
    }

    //helpers ..........................................................................................................

    private void parseFormatAndCheck(final String pattern,
                                     final double value,
                                     final String text) {
        this.parseFormatAndCheck(
            pattern,
            String.valueOf(value),
            text
        );
    }

    private void parseFormatAndCheck(final String pattern,
                                     final String value,
                                     final String text) {
        this.parseFormatAndCheck(
            pattern,
            value,
            SpreadsheetText.with(text)
        );
    }

    private void parseFormatAndCheck(final String pattern,
                                     final String value,
                                     final SpreadsheetText text) {
        this.formatAndCheck(
            this.createFormatter(pattern),
            new BigDecimal(value),
            text
        );
    }

    @Override
    String pattern() {
        return "#/#";
    }

    @Override
    Parser<SpreadsheetFormatParserContext> parser() {
        return SpreadsheetFormatParsers.fraction();
    }

    @Override
    SpreadsheetPatternSpreadsheetFormatterFraction createFormatter0(final FractionSpreadsheetFormatParserToken token) {
        return SpreadsheetPatternSpreadsheetFormatterFraction.with(
            token,
            FRACTIONER
        );
    }

    private final static Function<BigDecimal, Fraction> FRACTIONER = SpreadsheetPatternSpreadsheetFormatterFractionTest::makeIntoFraction;

    private static Fraction makeIntoFraction(final BigDecimal value) {
        if (value.signum() == 0) {
            return Fraction.with(BigInteger.ZERO, BigInteger.ONE);
        }

        final int scale = value.scale();
        final BigDecimal two = BigDecimal.valueOf(2);

        return Fraction.with(
            value.scaleByPowerOfTen(scale).divide(two, MATH_CONTEXT).toBigInteger(),
            BigDecimal.ONE.scaleByPowerOfTen(scale).divide(two, MATH_CONTEXT).toBigInteger());
    }

    @Override
    public Optional<BigDecimal> value() {
        return Optional.of(
            new BigDecimal(123)
        );
    }

    @Override
    public SpreadsheetFormatterContext createContext() {
        return this.createContext('0');
    }

    public SpreadsheetFormatterContext createContext(final char zero) {
        return new FakeSpreadsheetFormatterContext() {
            @Override
            public String currencySymbol() {
                return "D";
            }

            @Override
            public <T> Either<T, String> convert(final Object value,
                                                 final Class<T> target) {
                return Converters.numberToNumber().convert(value, target, ConverterContexts.fake());
            }

            @Override
            public MathContext mathContext() {
                return MATH_CONTEXT;
            }

            @Override
            public char negativeSign() {
                return 'M';
            }

            @Override
            public char percentSymbol() {
                return 'P';
            }

            @Override
            public char zeroDigit() {
                return zero;
            }
        };
    }

    private final static MathContext MATH_CONTEXT = MathContext.UNLIMITED;

    // tokens...........................................................................................................

    @Test
    public void testTokens() {
        this.tokensAndCheck(
            this.createFormatter("00/00"),
            this.createContext(),
            SpreadsheetFormatterSelectorToken.with(
                "0",
                "0",
                SpreadsheetFormatterSelectorToken.NO_ALTERNATIVES
            ),
            SpreadsheetFormatterSelectorToken.with(
                "0",
                "0",
                SpreadsheetFormatterSelectorToken.NO_ALTERNATIVES
            ),
            SpreadsheetFormatterSelectorToken.with(
                "/",
                "/",
                SpreadsheetFormatterSelectorToken.NO_ALTERNATIVES
            ),
            SpreadsheetFormatterSelectorToken.with(
                "0",
                "0",
                SpreadsheetFormatterSelectorToken.NO_ALTERNATIVES
            ),
            SpreadsheetFormatterSelectorToken.with(
                "0",
                "0",
                SpreadsheetFormatterSelectorToken.NO_ALTERNATIVES
            )
        );
    }

    // equals...........................................................................................................

    @Test
    public void testEqualsDifferentToken() {
        this.checkNotEquals(
            this.createFormatter("#/#"),
            this.createFormatter("##/##")
        );
    }

    @Test
    public void testEqualsDifferentFractionerFunction() {
        final FractionSpreadsheetFormatParserToken token = this.token();

        this.checkNotEquals(
            SpreadsheetPatternSpreadsheetFormatterFraction.with(token, (v) -> null),
            SpreadsheetPatternSpreadsheetFormatterFraction.with(token, (v) -> null)
        );
    }

    // toString ........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createFormatter(), this.pattern());
    }

    // class ...........................................................................................................

    @Override
    public Class<SpreadsheetPatternSpreadsheetFormatterFraction> type() {
        return SpreadsheetPatternSpreadsheetFormatterFraction.class;
    }
}

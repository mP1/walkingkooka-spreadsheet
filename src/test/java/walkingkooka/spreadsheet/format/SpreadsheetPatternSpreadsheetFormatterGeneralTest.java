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
import walkingkooka.collect.list.Lists;
import walkingkooka.spreadsheet.format.parser.GeneralSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelectorToken;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Optional;

public final class SpreadsheetPatternSpreadsheetFormatterGeneralTest extends SpreadsheetPatternSpreadsheetFormatterTestCase<SpreadsheetPatternSpreadsheetFormatterGeneral, GeneralSpreadsheetFormatParserToken> {

    private final static ExpressionNumberKind KIND = ExpressionNumberKind.BIG_DECIMAL;

    @Test
    public void testFormatNull() {
        this.formatAndCheck(
            SpreadsheetPatternSpreadsheetFormatterGeneral.INSTANCE, // formatter
            Optional.empty(), // value
            Optional.empty() // expected
        );
    }

    @Test
    public void testFormatZeroBigDecimal() {
        this.formatAndCheck2(
            ExpressionNumberKind.BIG_DECIMAL.zero(),
            DecimalNumberContext.DEFAULT_NUMBER_DIGIT_COUNT,
            "0"
        );
    }

    @Test
    public void testFormatZeroDouble() {
        this.formatAndCheck2(
            ExpressionNumberKind.DOUBLE.zero(),
            DecimalNumberContext.DEFAULT_NUMBER_DIGIT_COUNT,
            "0"
        );
    }

    @Test
    public void testFormatSmallNumberBigDecimal() {
        this.formatAndCheck2(
            ExpressionNumberKind.BIG_DECIMAL.create(1),
            DecimalNumberContext.DEFAULT_NUMBER_DIGIT_COUNT,
            "1"
        );
    }

    @Test
    public void testFormatSmallNumberDouble() {
        this.formatAndCheck2(
            ExpressionNumberKind.DOUBLE.create(1),
            DecimalNumberContext.DEFAULT_NUMBER_DIGIT_COUNT,
            "1"
        );
    }

    @Test
    public void testFormatSmallNumberBigDecimal2() {
        this.formatAndCheck2(
            ExpressionNumberKind.BIG_DECIMAL.create(1.5),
            DecimalNumberContext.DEFAULT_NUMBER_DIGIT_COUNT,
            "1!5"
        );
    }

    @Test
    public void testFormatSmallNumberDouble2() {
        this.formatAndCheck2(
            ExpressionNumberKind.DOUBLE.create(1.5),
            DecimalNumberContext.DEFAULT_NUMBER_DIGIT_COUNT,
            "1!5"
        );
    }

    @Test
    public void testFormatSmallNumberBigDecimal3() {
        this.formatAndCheck2(
            ExpressionNumberKind.BIG_DECIMAL.create(-12.5),
            DecimalNumberContext.DEFAULT_NUMBER_DIGIT_COUNT,
            "N12!5"
        );
    }

    @Test
    public void testFormatSmallNumberDouble3() {
        this.formatAndCheck2(
            ExpressionNumberKind.DOUBLE.create(-12.5),
            DecimalNumberContext.DEFAULT_NUMBER_DIGIT_COUNT,
            "N12!5"
        );
    }

    @Test
    public void testFormatSmallNumberBigDecimalAlmostScientific() {
        this.formatAndCheck2(
            ExpressionNumberKind.BIG_DECIMAL.create(1.123456789),
            5,
            "1!12346"
        );
    }

    @Test
    public void testFormatSmallNumberBigDecimalAlmostScientific2() {
        this.formatAndCheck2(
            ExpressionNumberKind.BIG_DECIMAL.create(99.12345678901234567),
            13,
            "99!1234567890124"
        );
    }

    @Test
    public void testFormatSmallNumberDoubleAlmostScientific() {
        this.formatAndCheck2(
            ExpressionNumberKind.DOUBLE.create(1.23456789),
            5,
            "1!23457"
        );
    }

    @Test
    public void testFormatSmallNumberDoubleAlmostScientific2() {
        this.formatAndCheck2(
            ExpressionNumberKind.DOUBLE.create(12.345678),
            6,
            "12!345678"
        );
    }

    @Test
    public void testFormatNonScientificWithNonArabicDigits() {
        this.formatAndCheck2(
            ExpressionNumberKind.DOUBLE.create(1.5),
            6,
            '\u0660',
            "\u0661!\u0665"
        );
    }

    // scientific.......................................................................................................

    @Test
    public void testFormatScientificNumberBigDecimal() {
        this.formatAndCheck2(
            ExpressionNumberKind.BIG_DECIMAL.create(1E12),
            DecimalNumberContext.DEFAULT_NUMBER_DIGIT_COUNT,
            "1!X12"
        );
    }

    @Test
    public void testFormatScientificNumberDouble() {
        this.formatAndCheck2(
            ExpressionNumberKind.DOUBLE.create(1E12),
            DecimalNumberContext.DEFAULT_NUMBER_DIGIT_COUNT,
            "1!X12"
        );
    }

    @Test
    public void testFormatScientificNumberBigDecimal2() {
        this.formatAndCheck2(
            ExpressionNumberKind.BIG_DECIMAL.create(2.3E12),
            DecimalNumberContext.DEFAULT_NUMBER_DIGIT_COUNT,
            "2!3X12"
        );
    }

    @Test
    public void testFormatScientificNumberDouble2() {
        this.formatAndCheck2(
            ExpressionNumberKind.DOUBLE.create(2.3E12),
            DecimalNumberContext.DEFAULT_NUMBER_DIGIT_COUNT,
            "2!3X12"
        );
    }

    @Test
    public void testFormatScientificNumberBigDecimal3() {
        this.formatAndCheck2(
            ExpressionNumberKind.BIG_DECIMAL.create(-1.2E12),
            DecimalNumberContext.DEFAULT_NUMBER_DIGIT_COUNT,
            "N1!2X12"
        );
    }

    @Test
    public void testFormatScientificNumberDouble3() {
        this.formatAndCheck2(
            ExpressionNumberKind.DOUBLE.create(-1.2E12),
            DecimalNumberContext.DEFAULT_NUMBER_DIGIT_COUNT,
            "N1!2X12"
        );
    }

    @Test
    public void testFormatScientificNumberBigDecimal4() {
        this.formatAndCheck2(
            ExpressionNumberKind.BIG_DECIMAL.create(-1.2345678901E12),
            DecimalNumberContext.DEFAULT_NUMBER_DIGIT_COUNT,
            "N1!23456789X12"
        );
    }

    @Test
    public void testFormatScientificNumberDouble4() {
        this.formatAndCheck2(
            ExpressionNumberKind.DOUBLE.create(-1.2345678901E12),
            DecimalNumberContext.DEFAULT_NUMBER_DIGIT_COUNT,
            "N1!23456789X12"
        );
    }

    @Test
    public void testFormatScientificNumberDoubleDigitCount5() {
        this.formatAndCheck2(
            ExpressionNumberKind.DOUBLE.create(-1.2345678901E12),
            5, // generalFormatNumberDigitCount
            "N1!23457X12"
        );
    }

    @Test
    public void testFormatScientificNumberDoubleDigitCount10() {
        this.formatAndCheck2(
            ExpressionNumberKind.DOUBLE.create(-1.2345678901E12),
            10, // generalFormatNumberDigitCount
            "N1!2345678901X12"
        );
    }

    @Test
    public void testFormatScientificNumberDoubleDigitCount11() {
        this.formatAndCheck2(
            ExpressionNumberKind.DOUBLE.create(-1.2345678901E12),
            11, // generalFormatNumberDigitCount
            "N1!2345678901X12"
        );
    }

    @Test
    public void testFormatScientificNumberDoubleDigitCount12() {
        this.formatAndCheck2(
            ExpressionNumberKind.DOUBLE.create(-1.23456789012E12),
            12, // generalFormatNumberDigitCount
            "N1!23456789012X12"
        );
    }

    @Test
    public void testFormatScientificNumberWithNonHinduArabicDigits() {
        this.formatAndCheck2(
            ExpressionNumberKind.DOUBLE.create(1.2345678901E12),
            5, // generalFormatNumberDigitCount
            ARABIC_ZERO_DIGIT, // zeroDigit
            arabicDigit(1) +
                "!" +
                arabicDigit(2) +
                arabicDigit(3) +
                arabicDigit(4) +
                arabicDigit(5) +
                arabicDigit(7) +
                'X' +
                arabicDigit(1) +
                arabicDigit(2)
        );
    }

    private void formatAndCheck2(final ExpressionNumber number,
                                 final int generalFormatNumberDigitCount,
                                 final String text) {
        this.formatAndCheck2(
            number,
            generalFormatNumberDigitCount,
            '0', // zeroDigit
            text
        );
    }

    private void formatAndCheck2(final ExpressionNumber number,
                                 final int generalFormatNumberDigitCount,
                                 final char zeroDigit,
                                 final String text) {
        this.formatAndCheck(
            this.createFormatter(),
            number,
            this.createContext(
                generalFormatNumberDigitCount,
                zeroDigit
            ),
            text
        );
    }

    @Override
    String pattern() {
        return "General";
    }

    @Override
    Parser<SpreadsheetFormatParserContext> parser() {
        return SpreadsheetFormatParsers.general();
    }

    @Override
    SpreadsheetPatternSpreadsheetFormatterGeneral createFormatter0(final GeneralSpreadsheetFormatParserToken pattern) {
        Objects.requireNonNull(pattern, "pattern");

        return SpreadsheetPatternSpreadsheetFormatterGeneral.INSTANCE;
    }

    @Override
    public Object value() {
        return KIND.zero();
    }

    @Override
    public SpreadsheetFormatterContext createContext() {
        return this.createContext(
            8, // generalFormatNumberDigitCount
            '0'
        );
    }

    private SpreadsheetFormatterContext createContext(final int generalFormatNumberDigitCount,
                                                      final char zeroDigit) {
        return new FakeSpreadsheetFormatterContext() {

            @Override
            public boolean canConvert(final Object value,
                                      final Class<?> target) {
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
                final ExpressionNumber expressionNumber = ExpressionNumber.class.cast(value);
                return this.successfulConversion(
                    ExpressionNumber.class == target ?
                        expressionNumber :
                        target.cast(
                            target == BigDecimal.class ?
                                expressionNumber.bigDecimal() :
                                expressionNumber.doubleValue()
                        ),
                    target
                );
            }

            @Override
            public String currencySymbol() {
                return "C";
            }

            @Override
            public char decimalSeparator() {
                return '!';
            }

            @Override
            public String exponentSymbol() {
                return "X";
            }

            @Override
            public int generalFormatNumberDigitCount() {
                return generalFormatNumberDigitCount;
            }

            @Override
            public char groupSeparator() {
                return 'G';
            }

            @Override
            public MathContext mathContext() {
                return new MathContext(32, RoundingMode.HALF_UP);
            }

            @Override
            public char negativeSign() {
                return 'N';
            }

            @Override
            public char percentSymbol() {
                return 'R';
            }

            @Override
            public char positiveSign() {
                return 'P';
            }

            @Override
            public char zeroDigit() {
                return zeroDigit;
            }
        };
    }

    // tokens...................................................................................................

    @Test
    public void testTokens() {
        this.tokensAndCheck(
            SpreadsheetFormatterContexts.fake(),
            SpreadsheetFormatterSelectorToken.with(
                "General",
                "General",
                Lists.empty()
            )
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            SpreadsheetPatternSpreadsheetFormatterGeneral.INSTANCE,
            "General"
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetPatternSpreadsheetFormatterGeneral> type() {
        return SpreadsheetPatternSpreadsheetFormatterGeneral.class;
    }
}

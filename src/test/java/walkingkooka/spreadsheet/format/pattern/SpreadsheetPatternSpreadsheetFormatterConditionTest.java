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
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.currency.CurrencyLocaleContexts;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.spreadsheet.format.FakeSpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.parser.ConditionSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelectorToken;
import walkingkooka.text.LineEnding;
import walkingkooka.text.cursor.parser.BigDecimalParserToken;
import walkingkooka.text.cursor.parser.InvalidCharacterExceptionFactory;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContexts;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContext;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContexts;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetPatternSpreadsheetFormatterConditionTest extends SpreadsheetPatternSpreadsheetFormatterTestCase<SpreadsheetPatternSpreadsheetFormatterCondition,
    ConditionSpreadsheetFormatParserToken> {

    private final static String TEXT_PATTERN = "@!condition-true";

    @Test
    public void testWithNullWrappedFormatterFails() {
        assertThrows(NullPointerException.class, () -> SpreadsheetPatternSpreadsheetFormatterCondition.with(this.parsePatternOrFail(this.pattern()), null));
    }

    // EQ...............................................................................................................

    @Test
    public void testFormatEQWithNullValue() {
        this.formatAndCheck(
            this.createFormatter("[=50]"),
            Optional.empty(), // value
            Optional.empty() // expected
        ); // pass
    }

    @Test
    public void testFormatEQ() {
        this.formatConditionTrueAndCheck(
            "[=50]",
            "50",
            "50!condition-true"
        ); // pass
    }

    @Test
    public void testFormatEQ2() {
        this.formatConditionFalseAndCheck(
            "[=50]",
            "99"
        ); // fail
    }

    // GT...............................................................................................................

    @Test
    public void testFormatGT() {
        this.formatConditionTrueAndCheck(
            "[>9]",
            "50",
            "50!condition-true"
        ); // 50 > 9 pass
    }

    @Test
    public void testFormatGT2() {
        this.formatConditionFalseAndCheck(
            "[>9]",
            "5"
        ); // 5 > 9 fail
    }

    @Test
    public void testFormatGT3() {
        this.formatConditionFalseAndCheck(
            "[>9]",
            "9"
        ); // 9 > 9 fail
    }

    // GTE..............................................................................................................

    @Test
    public void testFormatGTE() {
        this.formatConditionTrueAndCheck(
            "[>=9]",
            "50",
            "50!condition-true"
        ); // 50 >= 9 pass
    }

    @Test
    public void testFormatGTE2() {
        this.formatConditionFalseAndCheck(
            "[>=9]",
            "5"
        ); // 5 >= 9 fail
    }

    @Test
    public void testFormatGTE3() {
        this.formatConditionTrueAndCheck(
            "[>=9]",
            "9",
            "9!condition-true"
        ); // 9 >= 9 pass
    }

    // LT...............................................................................................................

    @Test
    public void testFormatLT() {
        this.formatConditionFalseAndCheck(
            "[<9]",
            "50"
        ); // 50 < 9 fail
    }

    @Test
    public void testFormatLT2() {
        this.formatConditionTrueAndCheck(
            "[<9]",
            "5",
            "5!condition-true"
        ); // 5 < 9 pass
    }

    @Test
    public void testFormatLT3() {
        this.formatConditionFalseAndCheck(
            "[<9]",
            "9"
        ); // 9 < 9 fail
    }

    // LTE..............................................................................................................

    @Test
    public void testFormatLTE() {
        this.formatConditionFalseAndCheck(
            "[<=9]",
            "50"
        ); // 50 <= 9 fail
    }

    @Test
    public void testFormatLTE2() {
        this.formatConditionTrueAndCheck(
            "[<=9]",
            "5",
            "5!condition-true"
        ); // 5 <= 9 pass
    }

    @Test
    public void testFormatLTE3() {
        this.formatConditionTrueAndCheck(
            "[<=9]",
            "9",
            "9!condition-true"
        ); // 9 <= 9 pass
    }

    // NE...............................................................................................................

    @Test
    public void testFormatNE() {
        this.formatConditionTrueAndCheck(
            "[<>50]",
            "99",
            "99!condition-true"
        ); // == pass
    }

    @Test
    public void testFormatNE2() {
        this.formatConditionFalseAndCheck(
            "[<>50]",
            "50"
        ); // == fail
    }

    private void formatConditionFalseAndCheck(final String pattern,
                                              final String text) {
        this.formatAndCheck(
            this.createFormatter0(pattern),
            text,
            this.createContext()
        );
    }

    private void formatConditionTrueAndCheck(final String pattern,
                                             final String text,
                                             final String expected) {
        this.formatAndCheck(
            this.createFormatter0(pattern),
            text,
            expected
        );
    }

    private SpreadsheetPatternSpreadsheetFormatterCondition createFormatter0(final String expression) {
        return this.createFormatter0(this.parsePatternOrFail(expression));
    }

    @Override
    SpreadsheetPatternSpreadsheetFormatterCondition createFormatter0(final ConditionSpreadsheetFormatParserToken token) {
        return SpreadsheetPatternSpreadsheetFormatterCondition.with(
            token,
            SpreadsheetPatternSpreadsheetFormatters.text(
                SpreadsheetFormatParserToken.text(
                    Lists.of(
                        SpreadsheetFormatParserToken.textPlaceholder(
                            "@",
                            "@"
                        ),
                        SpreadsheetFormatParserToken.textLiteral(
                            "!condition-true",
                            "!condition-true"
                        )
                    ),
                    TEXT_PATTERN
                )
            )
        );
    }

    @Override
    Parser<SpreadsheetFormatParserContext> parser() {
        return SpreadsheetFormatParsers.condition();
    }

    @Override
    String pattern() {
        return "[>20]";
    }

    @Override
    public String value() {
        return "999";
    }

    @Override
    public SpreadsheetFormatterContext createContext() {
        return new FakeSpreadsheetFormatterContext() {
            @Override
            public boolean canConvert(final Object value,
                                      final Class<?> type) {
                return value instanceof String && type == String.class;
            }

            @Override
            public char decimalSeparator() {
                return '.';
            }

            @Override
            public String exponentSymbol() {
                return "E";
            }

            @Override
            public char negativeSign() {
                return '-';
            }

            @Override
            public char positiveSign() {
                return '+';
            }

            @Override
            public MathContext mathContext() {
                return MathContext.DECIMAL32;
            }

            @Override
            public <T> Either<T, String> convert(final Object value,
                                                 final Class<T> target) {
                if (value instanceof String && String.class == target) {
                    return this.successfulConversion(
                        value,
                        target
                    );
                }
                if (value instanceof String && BigDecimal.class == target) {
                    return this.successfulConversion(
                        new BigDecimal((String) value),
                        target
                    );
                }

                return this.converter.convert(
                    value,
                    target,
                    ExpressionNumberConverterContexts.basic(
                        Converters.characterOrCharSequenceOrHasTextOrStringToCharacterOrCharSequenceOrString(),
                        ConverterContexts.basic(
                            false, // canNumbersHaveGroupSeparator
                            Converters.JAVA_EPOCH_OFFSET, // dateOffset
                            TreePrintableTesting.INDENTATION,
                            LineEnding.NL,
                            ',', // valueSeparator
                            Converters.fake(),
                            CurrencyLocaleContexts.fake(),
                            DateTimeContexts.fake(),
                            this
                        ),
                        ExpressionNumberKind.DEFAULT
                    )
                );
            }

            private final Converter<ExpressionNumberConverterContext> converter = Converters.parser(
                BigDecimal.class,
                Parsers.bigDecimal(),
                (c) -> ParserContexts.basic(
                    false, // canNumbersHaveGroupSeparator
                    InvalidCharacterExceptionFactory.POSITION,
                    ',', // valueSeparator
                    c,
                    c
                ),
                (t, c) -> t.cast(BigDecimalParserToken.class).value()
            );
        };
    }

    // tokens...................................................................................................

    @Test
    public void testTokensEquals() {
        this.tokensAndCheck(
            this.createFormatter("[=0]"),
            this.createContext(),
            SpreadsheetFormatterSelectorToken.with(
                "[=0]",
                "[=0]",
                SpreadsheetFormatterSelectorToken.NO_ALTERNATIVES
            )
        );
    }

    @Test
    public void testTokensLessThan() {
        this.tokensAndCheck(
            this.createFormatter("[<1]"),
            this.createContext(),
            SpreadsheetFormatterSelectorToken.with(
                "[<1]",
                "[<1]",
                SpreadsheetFormatterSelectorToken.NO_ALTERNATIVES
            )
        );
    }

    // equals...........................................................................................................

    @Test
    public void testEqualsDifferentToken() {
        final SpreadsheetPatternSpreadsheetFormatter formatter = SpreadsheetPattern.parseTextFormatPattern("@")
            .formatter();

        this.checkNotEquals(
            SpreadsheetPatternSpreadsheetFormatterCondition.with(
                this.parsePatternOrFail("[<0]"),
                formatter
            ),
            SpreadsheetPatternSpreadsheetFormatterCondition.with(
                this.parsePatternOrFail("[>0]"),
                formatter
            )
        );
    }

    @Test
    public void testEqualsDifferentFormatter() {
        final ConditionSpreadsheetFormatParserToken token = this.parsePatternOrFail("[=0]");

        this.checkNotEquals(
            SpreadsheetPatternSpreadsheetFormatterCondition.with(
                token,
                SpreadsheetPattern.parseTextFormatPattern("@")
                    .formatter()
            ),
            SpreadsheetPatternSpreadsheetFormatterCondition.with(
                token,
                SpreadsheetPattern.parseTextFormatPattern("@@@")
                    .formatter()
            )
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createFormatter(),
            this.pattern() + " " + TEXT_PATTERN
        );
    }

    // Class............................................................................................................

    @Override
    public Class<SpreadsheetPatternSpreadsheetFormatterCondition> type() {
        return SpreadsheetPatternSpreadsheetFormatterCondition.class;
    }
}

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
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatConditionParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.text.cursor.parser.BigDecimalParserToken;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContexts;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.tree.expression.ExpressionNumberConverterContext;
import walkingkooka.tree.expression.ExpressionNumberConverterContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.math.BigDecimal;
import java.math.MathContext;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetPatternSpreadsheetFormatterConditionTest extends SpreadsheetPatternSpreadsheetFormatterTestCase<SpreadsheetPatternSpreadsheetFormatterCondition,
        SpreadsheetFormatConditionParserToken> {

    private final static String TEXT_PATTERN = "@!condition-true";

    @Test
    public void testWithNullWrappedFormatterFails() {
        assertThrows(NullPointerException.class, () -> SpreadsheetPatternSpreadsheetFormatterCondition.with(this.parsePatternOrFail(this.pattern()), null));
    }

    // EQ.....................................................................................

    @Test
    public void testFormattedEQ() {
        this.formatConditionTrueAndCheck(
                "[=50]",
                "50",
                "50!condition-true"
        ); // pass
    }

    @Test
    public void testFormattedEQ2() {
        this.formatConditionFalseAndCheck(
                "[=50]",
                "99"
        ); // fail
    }

    // GT.....................................................................................

    @Test
    public void testFormattedGT() {
        this.formatConditionTrueAndCheck(
                "[>9]",
                "50",
                "50!condition-true"
        ); // 50 > 9 pass
    }

    @Test
    public void testFormattedGT2() {
        this.formatConditionFalseAndCheck(
                "[>9]",
                "5"
        ); // 5 > 9 fail
    }

    @Test
    public void testFormattedGT3() {
        this.formatConditionFalseAndCheck(
                "[>9]",
                "9"
        ); // 9 > 9 fail
    }

    // GTE.....................................................................................

    @Test
    public void testFormattedGTE() {
        this.formatConditionTrueAndCheck(
                "[>=9]",
                "50",
                "50!condition-true"
        ); // 50 >= 9 pass
    }

    @Test
    public void testFormattedGTE2() {
        this.formatConditionFalseAndCheck(
                "[>=9]",
                "5"
        ); // 5 >= 9 fail
    }

    @Test
    public void testFormattedGTE3() {
        this.formatConditionTrueAndCheck(
                "[>=9]",
                "9",
                "9!condition-true"
        ); // 9 >= 9 pass
    }

    // LT.....................................................................................

    @Test
    public void testFormattedLT() {
        this.formatConditionFalseAndCheck(
                "[<9]",
                "50"
        ); // 50 < 9 fail
    }

    @Test
    public void testFormattedLT2() {
        this.formatConditionTrueAndCheck(
                "[<9]",
                "5",
                "5!condition-true"
        ); // 5 < 9 pass
    }

    @Test
    public void testFormattedLT3() {
        this.formatConditionFalseAndCheck(
                "[<9]",
                "9"
        ); // 9 < 9 fail
    }

    // LTE.....................................................................................

    @Test
    public void testFormattedLTE() {
        this.formatConditionFalseAndCheck(
                "[<=9]",
                "50"
        ); // 50 <= 9 fail
    }

    @Test
    public void testFormattedLTE2() {
        this.formatConditionTrueAndCheck(
                "[<=9]",
                "5",
                "5!condition-true"
        ); // 5 <= 9 pass
    }

    @Test
    public void testFormattedLTE3() {
        this.formatConditionTrueAndCheck(
                "[<=9]",
                "9",
                "9!condition-true"
        ); // 9 <= 9 pass
    }

    // NE.....................................................................................

    @Test
    public void testFormattedNE() {
        this.formatConditionTrueAndCheck(
                "[<>50]",
                "99",
                "99!condition-true"
        ); // == pass
    }

    @Test
    public void testFormattedNE2() {
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

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                this.createFormatter(),
                this.pattern() + " " + TEXT_PATTERN
        );
    }

    // helpers..........................................................................................................

    private SpreadsheetPatternSpreadsheetFormatterCondition createFormatter0(final String expression) {
        return this.createFormatter0(this.parsePatternOrFail(expression));
    }

    @Override
    SpreadsheetPatternSpreadsheetFormatterCondition createFormatter0(final SpreadsheetFormatConditionParserToken token) {
        return SpreadsheetPatternSpreadsheetFormatterCondition.with(
                token,
                SpreadsheetFormatters.text(
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
                                Converters.fake(),
                                ConverterContexts.basic(
                                        Converters.JAVA_EPOCH_OFFSET, // dateOffset
                                        Converters.fake(),
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
                    (c) -> ParserContexts.basic(c, c),
                    (t, c) -> t.cast(BigDecimalParserToken.class).value()
            );
        };
    }

    @Override
    public Class<SpreadsheetPatternSpreadsheetFormatterCondition> type() {
        return SpreadsheetPatternSpreadsheetFormatterCondition.class;
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
        final SpreadsheetFormatConditionParserToken token = this.parsePatternOrFail("[=0]");

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
}

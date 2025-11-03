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
import walkingkooka.Cast;
import walkingkooka.Either;
import walkingkooka.collect.list.Lists;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetPatternSpreadsheetFormatterCollectionTest extends SpreadsheetPatternSpreadsheetFormatterTestCase<SpreadsheetPatternSpreadsheetFormatterCollection, SpreadsheetFormatParserToken> {

    private final static Integer VALUE1 = 11;
    private final static Double VALUE2 = 222.5;
    private final static String TEXT1 = "1st";
    private final static String TEXT2 = "2nd";

    @Test
    public void testWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetPatternSpreadsheetFormatterCollection.with(null)
        );
    }

    @Test
    public void testWithEmptyFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetPatternSpreadsheetFormatterCollection.with(Lists.empty())
        );
    }

    @Test
    public void testWithOneUnwraps() {
        final SpreadsheetPatternSpreadsheetFormatter formatter = SpreadsheetFormatters.fakeSpreadsheetPattern();
        assertSame(
            formatter,
            SpreadsheetPatternSpreadsheetFormatterCollection.with(
                Lists.of(formatter)
            )
        );
    }

    // format...........................................................................................................

    @Test
    public void testFormatFirst() {
        this.formatAndCheck(VALUE1, TEXT1);
    }

    @Test
    public void testFormatSecond() {
        this.formatAndCheck(VALUE2, TEXT2);
    }

    @Test
    public void testFormatNull() {
        this.formatAndCheck(
            Optional.empty(),
            Optional.empty()
        );
    }

    @Test
    public void testFormatPositiveNumberWithAccountingPattern() {
        this.formatAndCheck(
            SpreadsheetPattern.parseNumberFormatPattern("(0.00);[RED]0.0000")
                .formatter(),
            ExpressionNumberKind.BIG_DECIMAL.create( 1.5),
            new FakeSpreadsheetFormatterContext() {

                @Override
                public <T> Either<T, String> convert(final Object value,
                                                     final Class<T> target) {
                    if(value instanceof ExpressionNumber && BigDecimal.class == target) {
                        return this.successfulConversion(
                            ExpressionNumber.class.cast(value)
                                .bigDecimal(),
                            target
                        );
                    }
                    if(value instanceof ExpressionNumber && ExpressionNumber.isClass(target)) {
                        return this.successfulConversion(
                            target.cast(value),
                            target
                        );
                    }

                    return this.failConversion(
                        value,
                        target
                    );
                }

                @Override
                public char decimalSeparator() {
                    return '.';
                }

                @Override
                public MathContext mathContext() {
                    return MathContext.DECIMAL32;
                }

                @Override
                public char zeroDigit() {
                    return '0';
                }
            },
            "(1.50)" // MINUS sign should not appear between parens
        );
    }

    @Test
    public void testFormatNegativeNumberWithAccountingPattern() {
        this.formatAndCheck(
            SpreadsheetPattern.parseNumberFormatPattern("[BLACK](0.00);(0.00)")
                .formatter(),
            ExpressionNumberKind.BIG_DECIMAL.create( -1.5),
            new FakeSpreadsheetFormatterContext() {

                @Override
                public <T> Either<T, String> convert(final Object value,
                                                     final Class<T> target) {
                    if(value instanceof ExpressionNumber && BigDecimal.class == target) {
                        return this.successfulConversion(
                            ExpressionNumber.class.cast(value)
                                .bigDecimal(),
                            target
                        );
                    }
                    if(value instanceof ExpressionNumber && ExpressionNumber.isClass(target)) {
                        return this.successfulConversion(
                            target.cast(value),
                            target
                        );
                    }

                    return this.failConversion(
                        value,
                        target
                    );
                }

                @Override
                public char decimalSeparator() {
                    return '.';
                }

                @Override
                public MathContext mathContext() {
                    return MathContext.DECIMAL32;
                }

                @Override
                public char zeroDigit() {
                    return '0';
                }
            },
            "(1.50)" // MINUS sign should not appear between parens
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
    SpreadsheetPatternSpreadsheetFormatterCollection createFormatter0(final SpreadsheetFormatParserToken token) {
        Objects.requireNonNull(token, "token"); // token is ignored by SpreadsheetPatternSpreadsheetFormatterCollection

        return Cast.to(
            SpreadsheetPatternSpreadsheetFormatterCollection.with(
                Lists.of(
                    FORMATTER1,
                    FORMATTER2
                )
            )
        );
    }

    private final SpreadsheetPatternSpreadsheetFormatter FORMATTER1 = formatter(VALUE1, TEXT1);

    private final SpreadsheetPatternSpreadsheetFormatter FORMATTER2 = formatter(VALUE2, TEXT2);

    private static SpreadsheetPatternSpreadsheetFormatter formatter(final Object value,
                                                                    final String text) {
        return new FakeSpreadsheetPatternSpreadsheetFormatter() {

            @Override
            public Optional<SpreadsheetText> formatSpreadsheetText(final Optional<Object> v,
                                                                   final SpreadsheetFormatterContext context) {
                Objects.requireNonNull(v, "value");
                Objects.requireNonNull(context, "context");

                return Optional.ofNullable(
                    value.equals(v.orElse(null)) ?
                        spreadsheetText(text) :
                        null
                );
            }

            @Override
            public String toString() {
                return String.valueOf(value);
            }
        };
    }

    private static SpreadsheetText spreadsheetText(final String text) {
        return SpreadsheetText.with(text);
    }

    @Override
    public Optional<Object> value() {
        return Optional.of(VALUE1);
    }

    @Override
    public SpreadsheetFormatterContext createContext() {
        return SpreadsheetFormatterContexts.fake();
    }

    // tokens...................................................................................................

    @Test
    public void testTokens() {
        this.tokensAndCheck();
    }

    // equals...........................................................................................................

    @Test
    public void testEqualsDifferentFormatters() {
        this.checkNotEquals(
            SpreadsheetPatternSpreadsheetFormatterCollection.with(
                Lists.of(
                    SpreadsheetPattern.parseTextFormatPattern("@")
                        .formatter(),
                    SpreadsheetPattern.parseTextFormatPattern("@@")
                        .formatter()
                )
            ),
            SpreadsheetPatternSpreadsheetFormatterCollection.with(
                Lists.of(
                    SpreadsheetPattern.parseTextFormatPattern("@@@")
                        .formatter()
                )
            )
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createFormatter(),
            VALUE1 + ";" + VALUE2
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetPatternSpreadsheetFormatterCollection> type() {
        return SpreadsheetPatternSpreadsheetFormatterCollection.class;
    }

    // type naming......................................................................................................

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }
}

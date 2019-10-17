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
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatConditionParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContexts;
import walkingkooka.text.cursor.parser.Parsers;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ConditionSpreadsheetFormatterTest extends SpreadsheetFormatter3TestCase<ConditionSpreadsheetFormatter,
        SpreadsheetFormatConditionParserToken> {

    private final static String TEXT_PATTERN = "!@@";

    @Test
    public void testWithNullWrappedFormatterFails() {
        assertThrows(NullPointerException.class, () -> ConditionSpreadsheetFormatter.with(this.parsePatternOrFail(this.pattern()), null));
    }

    // EQ.....................................................................................

    @Test
    public void testFormattedEQ() {
        this.formatAndCheck2("[=50]", "50"); // pass
    }

    @Test
    public void testFormattedEQ2() {
        this.formatFailAndCheck2("[=50]", "99"); // fail
    }

    // GT.....................................................................................

    @Test
    public void testFormattedGT() {
        this.formatAndCheck2("[>9]", "50"); // 50 > 9 pass
    }

    @Test
    public void testFormattedGT2() {
        this.formatFailAndCheck2("[>9]", "5"); // 5 > 9 fail
    }

    @Test
    public void testFormattedGT3() {
        this.formatFailAndCheck2("[>9]", "9"); // 9 > 9 fail
    }

    // GTE.....................................................................................

    @Test
    public void testFormattedGTE() {
        this.formatAndCheck2("[>=9]", "50"); // 50 >= 9 pass
    }

    @Test
    public void testFormattedGTE2() {
        this.formatFailAndCheck2("[>=9]", "5"); // 5 >= 9 fail
    }

    @Test
    public void testFormattedGTE3() {
        this.formatAndCheck2("[>=9]", "9"); // 9 >= 9 pass
    }

    // LT.....................................................................................

    @Test
    public void testFormattedLT() {
        this.formatFailAndCheck2("[<9]", "50"); // 50 < 9 fail
    }

    @Test
    public void testFormattedLT2() {
        this.formatAndCheck2("[<9]", "5"); // 5 < 9 pass
    }

    @Test
    public void testFormattedLT3() {
        this.formatFailAndCheck2("[<9]", "9"); // 9 < 9 fail
    }

    // LTE.....................................................................................

    @Test
    public void testFormattedLTE() {
        this.formatFailAndCheck2("[<=9]", "50"); // 50 <= 9 fail
    }

    @Test
    public void testFormattedLTE2() {
        this.formatAndCheck2("[<=9]", "5"); // 5 <= 9 pass
    }

    @Test
    public void testFormattedLTE3() {
        this.formatAndCheck2("[<=9]", "9"); // 9 <= 9 pass
    }

    // NE.....................................................................................

    @Test
    public void testFormattedNE() {
        this.formatAndCheck2("[!=50]", "99"); // == pass
    }

    @Test
    public void testFormattedNE2() {
        this.formatFailAndCheck2("[!=50]", "50"); // == fail
    }

    // helpers.........................................................................

    private void formatAndCheck2(final String pattern, final String text) {
        this.formatAndCheck(this.createFormatter0(pattern), text, text);
    }

    private void formatFailAndCheck2(final String pattern, final String text) {
        this.formatFailAndCheck(this.createFormatter0(pattern), text, this.createContext());
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createFormatter(), this.pattern() + " " + TEXT_PATTERN);
    }

    private ConditionSpreadsheetFormatter createFormatter0(final String expression) {
        return this.createFormatter0(this.parsePatternOrFail(expression));
    }

    @Override
    ConditionSpreadsheetFormatter createFormatter0(final SpreadsheetFormatConditionParserToken token) {
        return ConditionSpreadsheetFormatter.with(token, this.formatter());
    }

    private SpreadsheetFormatter formatter() {
        return new SpreadsheetFormatter() {

            @Override
            public boolean canFormat(final Object value,
                                     final SpreadsheetFormatterContext context) throws SpreadsheetFormatException {
                return value instanceof String;
            }

            @Override
            public Optional<SpreadsheetText> format(final Object value,
                                                    final SpreadsheetFormatterContext context) throws SpreadsheetFormatException {
                return Optional.of(SpreadsheetText.with(SpreadsheetText.WITHOUT_COLOR, (String) value));
            }

            @Override
            public String toString() {
                return TEXT_PATTERN;
            }
        };
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
        return "Text123";
    }

    @Override
    public SpreadsheetFormatterContext createContext() {
        return new FakeSpreadsheetFormatterContext() {
            @Override
            public char decimalSeparator() {
                return '.';
            }

            @Override
            public char exponentSymbol() {
                return 'E';
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
                return this.converter.convert(value,
                        target,
                        ConverterContexts.basic(DateTimeContexts.fake(), this));
            }

            private final Converter converter = Converters.parser(BigDecimal.class,
                    Parsers.bigDecimal(),
                    (c) -> ParserContexts.basic(c, c));
        };
    }

    @Override
    public Class<ConditionSpreadsheetFormatter> type() {
        return ConditionSpreadsheetFormatter.class;
    }
}

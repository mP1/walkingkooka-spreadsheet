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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.color.Color;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTextParserToken;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.SequenceParserToken;

import java.util.Optional;

public final class SpreadsheetPatternSpreadsheetFormatterTextTest extends SpreadsheetPatternSpreadsheetFormatterTestCase<SpreadsheetPatternSpreadsheetFormatterText, SpreadsheetFormatTextParserToken>
        implements HashCodeEqualsDefinedTesting2<SpreadsheetPatternSpreadsheetFormatterText> {

    private final static String TEXT = "Abc123";

    private final static Color RED = Color.parse("#FF0000");

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }
    @Test
    public void testPlaceholder() {
        this.parseFormatAndCheck("@", TEXT, TEXT);
    }

    @Test
    public void testQuotedTextAndPlaceholder() {
        final String quoted = "Quoted456";
        this.parseFormatAndCheck("@\"" + quoted + "\"@", TEXT, TEXT + quoted + TEXT);
    }

    @Test
    public void testTextAndUnderscore() {
        this.parseFormatAndCheck("@_A",
                TEXT,
                TEXT + "A");
    }

    @Test
    public void testTextAndStar() {
        this.parseFormatAndCheck("@*A",
                TEXT,
                new TestSpreadsheetFormatterContext() {

                    @Override
                    public int cellCharacterWidth() {
                        return TEXT.length() + 3;
                    }
                },
                TEXT + "AAA");
    }

    @Test
    public void testTextAndLeftAndRightParens() {
        this.parseFormatAndCheck("(@)",
                TEXT,
                "(" + TEXT + ")");
    }

    @Test
    public void testTextAndEscaped() {
        this.parseFormatAndCheck("@\\B",
                TEXT,
                TEXT + "B");
    }

    @Test
    public void testColorNameAndTextPlaceholder() {
        this.parseFormatAndCheck(
                "[RED]@",
                TEXT,
                new TestSpreadsheetFormatterContext() {

                },
                SpreadsheetText.with(TEXT)
                        .setColor(Optional.of(RED))
        );
    }

    @Test
    public void testColorNumberAndTextPlaceholder() {
        this.parseFormatAndCheck(
                "[color44]@",
                TEXT,
                new TestSpreadsheetFormatterContext() {

                },
                SpreadsheetText.with(TEXT)
                        .setColor(Optional.of(RED))
        );
    }

    private void parseFormatAndCheck(final String pattern,
                                     final String value,
                                     final String text) {
        this.parseFormatAndCheck(pattern, value, this.createContext(), text);
    }

    private void parseFormatAndCheck(final String pattern,
                                     final String value,
                                     final SpreadsheetFormatterContext context,
                                     final String text) {
        this.parseFormatAndCheck(
                pattern,
                value,
                context,
                SpreadsheetText.with(text)
        );
    }

    private void parseFormatAndCheck(final String pattern,
                                     final String value,
                                     final SpreadsheetFormatterContext context,
                                     final SpreadsheetText text) {
        this.formatAndCheck(this.createFormatter(pattern), value, context, text);
    }

    @Override
    String pattern() {
        return "@";
    }

    Parser<SpreadsheetFormatParserContext> parser() {
        return SpreadsheetFormatParsers.textFormat()
                .transform((v, c) -> v.cast(SequenceParserToken.class).value().get(0));
    }

    //toString .......................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createFormatter(), this.pattern());
    }

    @Override
    SpreadsheetPatternSpreadsheetFormatterText createFormatter0(final SpreadsheetFormatTextParserToken token) {
        return SpreadsheetPatternSpreadsheetFormatterText.with(token);
    }

    @Override
    public String value() {
        return "Text123";
    }

    @Override
    public SpreadsheetFormatterContext createContext() {
        return new TestSpreadsheetFormatterContext();
    }

    class TestSpreadsheetFormatterContext extends FakeSpreadsheetFormatterContext {

        TestSpreadsheetFormatterContext() {
            super();
        }

        @Override
        public boolean canConvert(final Object value,
                                  final Class<?> target) {
            return value instanceof String && String.class == target;
        }

        @Override
        public <T> Either<T, String> convert(final Object value, final Class<T> target) {
            return this.canConvert(value, target) ?
                    this.successfulConversion(
                            value.toString(),
                            target
                    ) :
                    this.failConversion(
                            value,
                            target
                    );
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
    }

    @Override
    public Class<SpreadsheetPatternSpreadsheetFormatterText> type() {
        return SpreadsheetPatternSpreadsheetFormatterText.class;
    }

    // equals...........................................................................................................

    @Test
    public void testEqualsDifferentToken() {
        this.checkNotEquals(
                this.createFormatter("@"),
                this.createFormatter("@@")
        );
    }

    @Override
    public SpreadsheetPatternSpreadsheetFormatterText createObject() {
        return this.createFormatter();
    }
}

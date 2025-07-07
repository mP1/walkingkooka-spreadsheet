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
import walkingkooka.color.Color;
import walkingkooka.spreadsheet.SpreadsheetColors;
import walkingkooka.spreadsheet.format.parser.ColorSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.spreadsheet.format.parser.TextSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.text.cursor.parser.Parser;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetPatternSpreadsheetFormatterColorTest extends SpreadsheetPatternSpreadsheetFormatterTestCase<SpreadsheetPatternSpreadsheetFormatterColor,
    ColorSpreadsheetFormatParserToken> {

    private final static String TEXT_PATTERN = "@@";

    @Test
    public void testWithNullWrappedFormatterFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetPatternSpreadsheetFormatterColor.with(
                this.parsePatternOrFail(
                    this.pattern()
                ),
                null
            )
        );
    }

    @Test
    public void testWithColorSpreadsheetFormatter() {
        final SpreadsheetPatternSpreadsheetFormatter text = SpreadsheetFormatters.text(
            SpreadsheetFormatParserToken.text(
                Lists.of(
                    SpreadsheetFormatParserToken.textPlaceholder("@", "@"),
                    SpreadsheetFormatParserToken.textPlaceholder("@", "@")
                ),
                "@@"
            )
        );
        final SpreadsheetPatternSpreadsheetFormatterColor color = SpreadsheetPatternSpreadsheetFormatterColor.with(
            this.parsePatternOrFail("[COLOR 1]"),
            text
        );
        final SpreadsheetPatternSpreadsheetFormatterColor wrapper = SpreadsheetPatternSpreadsheetFormatterColor.with(
            this.parsePatternOrFail("[COLOR 2]"),
            color
        );
        assertSame(
            text,
            wrapper.formatter,
            "formatter"
        );
    }

    @Test
    public void testWithWrappedFormatterFails() {
        this.formatAndCheck(
            SpreadsheetPatternSpreadsheetFormatterColor.with(
                this.parsePatternOrFail(
                    this.pattern()
                ),
                SpreadsheetFormatters.general()
            ),
            "Ignored text",
            this.createContext()
        );
    }

    // format...........................................................................................................

    @Test
    public void testFormatNullValue() {
        final Optional<Color> color = Optional.of(Color.BLACK);

        this.formatAndCheck(
            this.createFormatter("[BLACK]"),
            Optional.empty(), // value
            new TestSpreadsheetFormatterContext() {
                @Override
                public Optional<Color> colorName(final SpreadsheetColorName name) {
                    checkEquals(
                        SpreadsheetColorName.with("BLACK"),
                        name,
                        "color name"
                    );
                    return color;
                }
            },
            Optional.empty()
        );
    }

    @Test
    public void testFormatColorNameAndTextFormatted() {
        final String text = "abc123";
        final Optional<Color> color = Optional.of(Color.BLACK);
        this.parsePatternFormatAndCheck(
            "[RED]",
            text,
            new TestSpreadsheetFormatterContext() {
                @Override
                public Optional<Color> colorName(final SpreadsheetColorName name) {
                    checkEquals(SpreadsheetColorName.with("RED"), name, "color name");
                    return color;
                }
            },
            SpreadsheetText.with(text + text)
                .setColor(color)
        );
    }

    @Test
    public void testFormatColorNameAndTextFormattedAbsent() {
        final String text = "abc123";
        final Optional<Color> color = Optional.empty();
        this.parsePatternFormatAndCheck(
            "[RED]",
            text,
            new TestSpreadsheetFormatterContext() {
                @Override
                public Optional<Color> colorName(final SpreadsheetColorName name) {
                    checkEquals(SpreadsheetColorName.with("RED"), name, "color name");
                    return color;
                }
            },
            SpreadsheetText.with(text + text)
                .setColor(color)
        );
    }

    @Test
    public void testFormatColorNumberAndTextFormatted() {
        final String text = "abc123";
        final Optional<Color> color = Optional.of(Color.BLACK);
        this.parsePatternFormatAndCheck(
            "[COLOR 15]",
            text,
            new TestSpreadsheetFormatterContext() {
                @Override
                public Optional<Color> colorNumber(final int number) {
                    checkEquals(15, number);
                    return color;
                }
            },
            SpreadsheetText.with(text + text)
                .setColor(color)
        );
    }

    @Test
    public void testFormatColorNumberAndTextFormattedAfterDoubleWrapping() {
        final String text = "abc123";
        final Optional<Color> color = Optional.of(Color.BLACK);

        this.parsePatternFormatAndCheck(
            SpreadsheetPatternSpreadsheetFormatterColor.with(
                this.parsePatternOrFail("[COLOR 2]"),
                SpreadsheetPatternSpreadsheetFormatterColor.with(
                    this.parsePatternOrFail("[COLOR 1]"),
                    SpreadsheetFormatters.text(
                        SpreadsheetFormatParserToken.text(
                            Lists.of(
                                SpreadsheetFormatParserToken.textPlaceholder("@", "@"),
                                SpreadsheetFormatParserToken.textPlaceholder("@", "@")
                            ),
                            "@@"
                        )
                    )
                )
            ),
            text,
            new TestSpreadsheetFormatterContext() {
                @Override
                public Optional<Color> colorNumber(final int number) {
                    checkEquals(2, number);
                    return color;
                }
            },
            SpreadsheetText.with(text + text)
                .setColor(color)
        );
    }

    @Test
    public void testFormatColorNumberAndTextFormattedAbsent() {
        final String text = "abc123";
        final Optional<Color> color = Optional.empty();
        this.parsePatternFormatAndCheck(
            "[COLOR 15]",
            text,
            new TestSpreadsheetFormatterContext() {
                @Override
                public Optional<Color> colorNumber(final int number) {
                    checkEquals(15, number);
                    return color;
                }
            },
            SpreadsheetText.with(text + text)
                .setColor(color)
        );
    }

    private void parsePatternFormatAndCheck(final String pattern,
                                            final String value,
                                            final SpreadsheetFormatterContext context,
                                            final SpreadsheetText formattedText) {
        this.parsePatternFormatAndCheck(
            this.createFormatter(pattern),
            value,
            context,
            formattedText
        );
    }

    private void parsePatternFormatAndCheck(final SpreadsheetPatternSpreadsheetFormatterColor formatter,
                                            final String value,
                                            final SpreadsheetFormatterContext context,
                                            final SpreadsheetText formattedText) {
        this.formatAndCheck(
            formatter,
            value,
            context,
            formattedText.toTextNode()
        );
    }

    @Override
    SpreadsheetPatternSpreadsheetFormatterColor createFormatter0(final ColorSpreadsheetFormatParserToken token) {
        return SpreadsheetPatternSpreadsheetFormatterColor.with(
            token,
            this.textFormatter()
        );
    }

    private SpreadsheetPatternSpreadsheetFormatter textFormatter() {
        return SpreadsheetFormatters.text(
            this.parsePatternOrFail(
                SpreadsheetFormatParsers.textFormat(),
                TEXT_PATTERN
            ).cast(TextSpreadsheetFormatParserToken.class)
        );
    }

    @Override
    Parser<SpreadsheetFormatParserContext> parser() {
        return SpreadsheetFormatParsers.color();
    }

    private final static int COLOR_NUMBER = 13;

    @Override
    String pattern() {
        return "[COLOR " + COLOR_NUMBER + "]";
    }

    @Override
    public String value() {
        return "Text123";
    }

    private final static Color COLOR = Color.fromRgb(0xff00ff);

    @Override
    public SpreadsheetFormatterContext createContext() {
        return new TestSpreadsheetFormatterContext() {

            @Override
            public Optional<Color> colorNumber(final int number) {
                checkEquals(COLOR_NUMBER, number, "color number");
                return Optional.of(COLOR);
            }

            @Override
            public String toString() {
                return "colorNumber: " + COLOR_NUMBER + "=" + COLOR;
            }
        };
    }

    abstract static class TestSpreadsheetFormatterContext extends FakeSpreadsheetFormatterContext {

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
    }

    // tokens...........................................................................................................

    @Test
    public void testTokensColorNumber() {
        this.tokensAndCheck(
            this.createFormatter("[Color 1]"),
            this.createContext(),
            SpreadsheetFormatterSelectorToken.with(
                "[Color 1]",
                "[Color 1]",
                alternatives(
                    "Black,White,Red,Green,Blue,Yellow,Magenta,Cyan," +
                        IntStream.rangeClosed(SpreadsheetColors.MIN, SpreadsheetColors.MAX)
                            .filter(i -> i != 1)
                            .mapToObj(i -> "Color " + i)
                            .collect(Collectors.joining(","))
                )
            )
        );
    }

    @Test
    public void testTokensColorName() {
        this.tokensAndCheck(
            this.createFormatter("[Red]"),
            this.createContext(),
            SpreadsheetFormatterSelectorToken.with(
                "[Red]",
                "[Red]",
                alternatives(
                    "Black,White,Green,Blue,Yellow,Magenta,Cyan," +
                        IntStream.rangeClosed(SpreadsheetColors.MIN, SpreadsheetColors.MAX)
                            .mapToObj(i -> "Color " + i)
                            .collect(Collectors.joining(","))
                )
            )
        );
    }

    private List<SpreadsheetFormatterSelectorTokenAlternative> alternatives(final String csv) {
        return Arrays.stream(csv.split(","))
            .map(t -> "[" + t + "]")
            .map(t -> SpreadsheetFormatterSelectorTokenAlternative.with(
                    t,
                    t
                )
            ).collect(Collectors.toList());
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createFormatter(), this.pattern() + " " + TEXT_PATTERN);
    }

    // equals...........................................................................................................

    @Test
    public void testEqualsDifferentColor() {
        final SpreadsheetPatternSpreadsheetFormatter formatter = SpreadsheetPattern.parseTextFormatPattern("@")
            .formatter();

        this.checkNotEquals(
            SpreadsheetPatternSpreadsheetFormatterColor.with(
                this.parsePatternOrFail("[RED]"),
                formatter
            ),
            SpreadsheetPatternSpreadsheetFormatterColor.with(
                this.parsePatternOrFail("[COLOR01]"),
                formatter
            )
        );
    }

    @Test
    public void testEqualsDifferentFormatter() {
        final ColorSpreadsheetFormatParserToken token = this.parsePatternOrFail("[RED]");

        this.checkNotEquals(
            SpreadsheetPatternSpreadsheetFormatterColor.with(
                token,
                SpreadsheetPattern.parseTextFormatPattern("@")
                    .formatter()
            ),
            SpreadsheetPatternSpreadsheetFormatterColor.with(
                token,
                SpreadsheetPattern.parseTextFormatPattern("@@@")
                    .formatter()
            )
        );
    }

    // Class...........................................................................................................

    @Override
    public Class<SpreadsheetPatternSpreadsheetFormatterColor> type() {
        return SpreadsheetPatternSpreadsheetFormatterColor.class;
    }
}

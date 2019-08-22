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
import walkingkooka.color.Color;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatColorParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.text.cursor.parser.Parser;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ColorSpreadsheetFormatterTest extends SpreadsheetFormatter3TestCase<ColorSpreadsheetFormatter,
        SpreadsheetFormatColorParserToken> {

    private final static String TEXT_PATTERN = "@@";

    @Test
    public void testWithNullWrappedFormatterFails() {
        assertThrows(NullPointerException.class, () -> {
            ColorSpreadsheetFormatter.with(this.parsePatternOrFail(this.pattern()), null);
        });
    }

    @Test
    public void testWithColorSpreadsheetFormatter() {
        final SpreadsheetFormatter text = SpreadsheetFormatters.fake();
        final ColorSpreadsheetFormatter color = ColorSpreadsheetFormatter.with(this.parsePatternOrFail("[COLOR 1]"), text);
        final ColorSpreadsheetFormatter wrapper = ColorSpreadsheetFormatter.with(this.parsePatternOrFail("[COLOR 2]"), color);
        assertSame(text, wrapper.formatter, "formatter");
    }

    @Test
    public void testWrappedFormatterFails() {
        this.formatFailAndCheck(ColorSpreadsheetFormatter.with(this.parsePatternOrFail(this.pattern()),
                new FakeSpreadsheetFormatter() {
                    @Override
                    public Optional<SpreadsheetText> format(final Object value, final SpreadsheetFormatterContext context) {
                        return Optional.empty();
                    }
                }),
                "Ignored text",
                this.createContext());
    }

    @Test
    public void testColorNameAndTextFormatted() {
        final String text = "abc123";
        final Optional<Color> color = Optional.of(Color.BLACK);
        this.parseFormatAndCheck0(
                "[RED]",
                text,
                new FakeSpreadsheetFormatterContext() {
                    @Override
                    public Optional<Color> colorName(final SpreadsheetColorName name) {
                        assertEquals(SpreadsheetColorName.with("RED"), name, "color name");
                        return color;
                    }
                },
                SpreadsheetText.with(color, text + text));
    }

    @Test
    public void testColorNameAndTextFormattedAbsent() {
        final String text = "abc123";
        final Optional<Color> color = Optional.empty();
        this.parseFormatAndCheck0(
                "[RED]",
                text,
                new FakeSpreadsheetFormatterContext() {
                    @Override
                    public Optional<Color> colorName(final SpreadsheetColorName name) {
                        assertEquals(SpreadsheetColorName.with("RED"), name, "color name");
                        return color;
                    }
                },
                SpreadsheetText.with(color, text + text));
    }

    @Test
    public void testColorNumberAndTextFormatted() {
        final String text = "abc123";
        final Optional<Color> color = Optional.of(Color.BLACK);
        this.parseFormatAndCheck0(
                "[COLOR 15]",
                text,
                new FakeSpreadsheetFormatterContext() {
                    @Override
                    public Optional<Color> colorNumber(final int number) {
                        assertEquals(15, number);
                        return color;
                    }
                },
                SpreadsheetText.with(color, text + text));
    }

    @Test
    public void testColorNumberAndTextFormattedAfterDoubleWrapping() {
        final String text = "abc123";
        final Optional<Color> color = Optional.of(Color.BLACK);

        this.parseFormatAndCheck0(
                ColorSpreadsheetFormatter.with(this.parsePatternOrFail("[COLOR 2]"),
                        ColorSpreadsheetFormatter.with(this.parsePatternOrFail("[COLOR 1]"), new SpreadsheetFormatter() {
                            @Override
                            public boolean canFormat(final Object value) {
                                return true;
                            }

                            @Override
                            public Optional<SpreadsheetText> format(final Object value, final SpreadsheetFormatterContext context) {
                                assertEquals(text, value, "value");
                                return Optional.of(SpreadsheetText.with(SpreadsheetText.WITHOUT_COLOR, text + text));
                            }
                        })),
                text,
                new FakeSpreadsheetFormatterContext() {
                    @Override
                    public Optional<Color> colorNumber(final int number) {
                        assertEquals(2, number);
                        return color;
                    }
                },
                SpreadsheetText.with(color, text + text));
    }

    @Test
    public void testColorNumberAndTextFormattedAbsent() {
        final String text = "abc123";
        final Optional<Color> color = Optional.empty();
        this.parseFormatAndCheck0(
                "[COLOR 15]",
                text,
                new FakeSpreadsheetFormatterContext() {
                    @Override
                    public Optional<Color> colorNumber(final int number) {
                        assertEquals(15, number);
                        return color;
                    }
                },
                SpreadsheetText.with(color, text + text));
    }

    private void parseFormatAndCheck0(final String pattern,
                                      final String value,
                                      final SpreadsheetFormatterContext context,
                                      final SpreadsheetText formattedText) {
        this.parseFormatAndCheck0(this.createFormatter(pattern), value, context, formattedText);
    }

    private void parseFormatAndCheck0(final ColorSpreadsheetFormatter formatter,
                                      final String value,
                                      final SpreadsheetFormatterContext context,
                                      final SpreadsheetText formattedText) {
        this.formatAndCheck(formatter, value, context, Optional.of(formattedText));
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createFormatter(), this.pattern() + " " + TEXT_PATTERN);
    }

    @Override
    ColorSpreadsheetFormatter createFormatter0(final SpreadsheetFormatColorParserToken token) {
        return ColorSpreadsheetFormatter.with(token, this.textFormatter());
    }

    private SpreadsheetFormatter textFormatter() {
        return SpreadsheetFormatters.text(this.parsePatternOrFail(SpreadsheetFormatParsers.text(), TEXT_PATTERN).cast());
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
        return new FakeSpreadsheetFormatterContext() {
            @Override
            public Optional<Color> colorNumber(final int number) {
                assertEquals(COLOR_NUMBER, number, "color number");
                return Optional.of(COLOR);
            }

            @Override
            public String toString() {
                return "colorNumber: " + COLOR_NUMBER + "=" + COLOR;
            }
        };
    }

    @Override
    public Class<ColorSpreadsheetFormatter> type() {
        return ColorSpreadsheetFormatter.class;
    }
}

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
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatColorParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContext;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTextParserToken;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.SequenceParserToken;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetPatternSpreadsheetFormatterColorTest extends SpreadsheetPatternSpreadsheetFormatterTestCase<SpreadsheetPatternSpreadsheetFormatterColor,
        SpreadsheetFormatColorParserToken> {

    private final static String TEXT_PATTERN = "@@";

    @Test
    public void testWithNullWrappedFormatterFails() {
        assertThrows(NullPointerException.class, () -> SpreadsheetPatternSpreadsheetFormatterColor.with(this.parsePatternOrFail(this.pattern()), null));
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
    public void testWrappedFormatterFails() {
        this.formatAndCheck(
                SpreadsheetPatternSpreadsheetFormatterColor.with(
                        this.parsePatternOrFail(
                                this.pattern()
                        ),
                        SpreadsheetFormatters.general()
                ),
                "Ignored text",
                this.createContext());
    }

    @Test
    public void testColorNameAndTextFormatted() {
        final String text = "abc123";
        final Optional<Color> color = Optional.of(Color.BLACK);
        this.formatAndCheck(
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
    public void testColorNameAndTextFormattedAbsent() {
        final String text = "abc123";
        final Optional<Color> color = Optional.empty();
        this.formatAndCheck(
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
    public void testColorNumberAndTextFormatted() {
        final String text = "abc123";
        final Optional<Color> color = Optional.of(Color.BLACK);
        this.formatAndCheck(
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
    public void testColorNumberAndTextFormattedAfterDoubleWrapping() {
        final String text = "abc123";
        final Optional<Color> color = Optional.of(Color.BLACK);

        this.formatAndCheck(
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
    public void testColorNumberAndTextFormattedAbsent() {
        final String text = "abc123";
        final Optional<Color> color = Optional.empty();
        this.formatAndCheck(
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

    private void formatAndCheck(final String pattern,
                                final String value,
                                final SpreadsheetFormatterContext context,
                                final SpreadsheetText formattedText) {
        this.formatAndCheck(
                this.createFormatter(pattern),
                value,
                context,
                formattedText
        );
    }

    private void formatAndCheck(final SpreadsheetPatternSpreadsheetFormatterColor formatter,
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

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createFormatter(), this.pattern() + " " + TEXT_PATTERN);
    }

    @Override
    SpreadsheetPatternSpreadsheetFormatterColor createFormatter0(final SpreadsheetFormatColorParserToken token) {
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
                        ).cast(SequenceParserToken.class)
                        .value()
                        .get(0)
                        .cast(SpreadsheetFormatTextParserToken.class)
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

    @Override
    public Class<SpreadsheetPatternSpreadsheetFormatterColor> type() {
        return SpreadsheetPatternSpreadsheetFormatterColor.class;
    }
}

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
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ColorSpreadsheetTextFormatterTest extends SpreadsheetTextFormatter3TestCase<ColorSpreadsheetTextFormatter,
        SpreadsheetFormatColorParserToken> {

    private final static String TEXT_PATTERN = "@@";

    @Test
    public void testWithNullWrappedFormatterFails() {
        assertThrows(NullPointerException.class, () -> {
            ColorSpreadsheetTextFormatter.with(this.parsePatternOrFail(this.pattern()), null);
        });
    }

    @Test
    public void testWrappedFormatterFails() {
        this.formatFailAndCheck(ColorSpreadsheetTextFormatter.with(this.parsePatternOrFail(this.pattern()),
                new FakeSpreadsheetTextFormatter() {
                    @Override
                    public Optional<SpreadsheetFormattedText> format(final Object value, final SpreadsheetTextFormatContext context) {
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
                new FakeSpreadsheetTextFormatContext() {
                    @Override
                    public Optional<Color> colorName(final String name) {
                        assertEquals("RED", name, "color name");
                        return color;
                    }
                },
                SpreadsheetFormattedText.with(color, text + text));
    }

    @Test
    public void testColorNameAndTextFormattedAbsent() {
        final String text = "abc123";
        final Optional<Color> color = Optional.empty();
        this.parseFormatAndCheck0(
                "[RED]",
                text,
                new FakeSpreadsheetTextFormatContext() {
                    @Override
                    public Optional<Color> colorName(final String name) {
                        assertEquals("RED", name, "color name");
                        return color;
                    }
                },
                SpreadsheetFormattedText.with(color, text + text));
    }

    @Test
    public void testColorNumberAndTextFormatted() {
        final String text = "abc123";
        final Optional<Color> color = Optional.of(Color.BLACK);
        this.parseFormatAndCheck0(
                "[COLOR 15]",
                text,
                new FakeSpreadsheetTextFormatContext() {
                    @Override
                    public Optional<Color> colorNumber(final int number) {
                        assertEquals(15, number);
                        return color;
                    }
                },
                SpreadsheetFormattedText.with(color, text + text));
    }

    @Test
    public void testColorNumberAndTextFormattedAbsent() {
        final String text = "abc123";
        final Optional<Color> color = Optional.empty();
        this.parseFormatAndCheck0(
                "[COLOR 15]",
                text,
                new FakeSpreadsheetTextFormatContext() {
                    @Override
                    public Optional<Color> colorNumber(final int number) {
                        assertEquals(15, number);
                        return color;
                    }
                },
                SpreadsheetFormattedText.with(color, text + text));
    }

    private void parseFormatAndCheck0(final String pattern,
                                      final String value,
                                      final SpreadsheetTextFormatContext context,
                                      final SpreadsheetFormattedText formattedText) {
        this.formatAndCheck(this.createFormatter(pattern), value, context, Optional.of(formattedText));
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createFormatter(), this.pattern() + " " + TEXT_PATTERN);
    }

    @Override
    ColorSpreadsheetTextFormatter createFormatter0(final SpreadsheetFormatColorParserToken token) {
        return ColorSpreadsheetTextFormatter.with(token, this.textFormatter());
    }

    private SpreadsheetTextFormatter textFormatter() {
        return SpreadsheetTextFormatters.text(this.parsePatternOrFail(SpreadsheetFormatParsers.text(), TEXT_PATTERN).cast());
    }

    @Override
    Parser<SpreadsheetFormatParserContext> parser() {
        return SpreadsheetFormatParsers.color();
    }

    @Override
    String pattern() {
        return "[RED]";
    }

    @Override
    public String value() {
        return "Text123";
    }

    @Override
    public SpreadsheetTextFormatContext createContext() {
        return SpreadsheetTextFormatContexts.fake();
    }

    @Override
    public Class<ColorSpreadsheetTextFormatter> type() {
        return ColorSpreadsheetTextFormatter.class;
    }
}

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
import walkingkooka.color.WebColorName;
import walkingkooka.convert.Converter;
import walkingkooka.convert.Converters;
import walkingkooka.net.Url;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPatternKind;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;

import java.util.Optional;

public final class SpreadsheetFormatterSharedHyperlinkingTest extends SpreadsheetFormatterSharedTestCase<SpreadsheetFormatterSharedHyperlinking>
    implements TreePrintableTesting {

    private final static Color RED = WebColorName.RED.color();

    @Test
    public void testFormatValueWithText() {
        final String text = "HelloWorld";

        this.formatAndCheck(
            text,
            SpreadsheetText.with(text + text)
                .setColor(
                    Optional.of(RED)
                )
        );
    }

    @Test
    public void testFormatValueTextIncludesAbsoluteUrl() {
        final String text = " HelloWorld https://www.example.com";

        this.formatAndCheck(
            text,
            TextNode.style(
                TextNode.NO_CHILDREN
            ).setTextStyle(
                TextStyle.EMPTY.set(
                    TextStylePropertyName.COLOR,
                    RED
                )
            ).appendChild(
                TextNode.style(
                    Lists.of(
                        TextNode.text(" HelloWorld "),
                        TextNode.hyperlink(
                            Url.parseAbsolute("https://www.example.com")
                        ),
                        TextNode.text(" HelloWorld "),
                        TextNode.hyperlink(
                            Url.parseAbsolute("https://www.example.com")
                        )
                    )
                )
            )
        );
    }

    @Override
    public SpreadsheetFormatterSharedHyperlinking createFormatter() {
        return SpreadsheetFormatterSharedHyperlinking.with(
            SpreadsheetPatternKind.TEXT_FORMAT_PATTERN.parse("[RED]@@")
                .formatter()
        );
    }

    @Override
    public Object value() {
        return "HelloWorld";
    }

    @Override
    public SpreadsheetFormatterContext createContext() {
        return new FakeSpreadsheetFormatterContext() {
            @Override
            public boolean canConvert(final Object value,
                                      final Class<?> type) {
                return this.converter.canConvert(
                    value,
                    type,
                    this
                );
            }

            @Override
            public <T> Either<T, String> convert(final Object value,
                                                 final Class<T> target) {
                return this.converter.convert(
                    value,
                    target,
                    this
                );
            }

            private final Converter<SpreadsheetFormatterContext> converter = Converters.simple();

            @Override
            public Optional<Color> colorName(final SpreadsheetColorName name) {
                checkEquals(SpreadsheetColorName.RED, name);
                return Optional.of(RED);
            }
        };
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetFormatterSharedHyperlinking> type() {
        return SpreadsheetFormatterSharedHyperlinking.class;
    }
}

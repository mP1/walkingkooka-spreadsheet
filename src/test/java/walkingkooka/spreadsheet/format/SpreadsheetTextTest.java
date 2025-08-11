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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.map.Maps;
import walkingkooka.color.Color;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.CharSequences;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.text.HasTextNodeTesting;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStylePropertyName;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetTextTest implements ClassTesting2<SpreadsheetText>,
    HashCodeEqualsDefinedTesting2<SpreadsheetText>,
    HasTextNodeTesting,
    JsonNodeMarshallingTesting<SpreadsheetText>,
    ToStringTesting<SpreadsheetText>,
    TreePrintableTesting {

    private final static Optional<Color> COLOR = Optional.of(Color.BLACK);
    private final static String TEXT = "1/1/2000";

    @Test
    public void testWithNullTextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetText.with(null)
        );
    }

    @Test
    public void testWith() {
        final SpreadsheetText formatted = this.createFormattedText();
        this.check(
            formatted,
            SpreadsheetText.WITHOUT_COLOR,
            TEXT
        );
    }

    @Test
    public void testWithEmptyText() {
        final String text = "";
        this.check(
            SpreadsheetText.with(text),
            SpreadsheetText.WITHOUT_COLOR,
            text
        );
    }

    @Test
    public void testWithEmptyTextConstant() {
        final SpreadsheetText spreadsheetText = SpreadsheetText.with("");
        assertSame(
            spreadsheetText,
            SpreadsheetText.with("")
        );
    }

    // setColor..........................................................................................................

    @SuppressWarnings("OptionalAssignedToNull")
    @Test
    public void testSetColorNullFails() {
        assertThrows(NullPointerException.class, () -> this.createFormattedText().setColor(null));
    }

    @Test
    public void testSetColorSame() {
        final SpreadsheetText formatted = this.createFormattedText()
            .setColor(COLOR);
        assertSame(
            formatted,
            formatted.setColor(COLOR)
        );
    }

    @Test
    public void testSetColorDifferent() {
        final Optional<Color> differentColor = Optional.of(Color.fromRgb(123));
        final SpreadsheetText formatted = this.createFormattedText();
        final SpreadsheetText different = formatted.setColor(differentColor);
        assertNotSame(formatted, different);
        this.check(different, differentColor, TEXT);
    }

    private void check(final SpreadsheetText formatted, final Optional<Color> color, final String text) {
        this.checkEquals(color, formatted.color(), "color");
        this.checkEquals(text, formatted.text(), "text");
    }

    // ToTextNode.... ..................................................................................................

    @Test
    public void testToTextNodeWithoutColor() {
        final String text = "abc123";

        this.toTextNodeAndCheck(
            SpreadsheetText.with(text),
            TextNode.text(text));
    }

    @Test
    public void testToTextNodeWithColor() {
        final String text = "abc123";
        final Color color = Color.fromRgb(0x123456);

        this.toTextNodeAndCheck(
            SpreadsheetText.with(text)
                .setColor(Optional.of(color)),
            TextNode.text(text)
                .setAttributes(
                    Maps.of(
                        TextStylePropertyName.COLOR,
                        color
                    )
                )
        );
    }

    // HashCodeEqualsDefined ..................................................................................................

    @Test
    public void testEqualsDifferentColor() {
        this.checkNotEquals(
            SpreadsheetText.with(TEXT)
                .setColor(Optional.of(Color.WHITE))
        );
    }

    @Test
    public void testEqualsDifferentColor2() {
        this.checkNotEquals(
            SpreadsheetText.with(TEXT)
        );
    }

    @Test
    public void testEqualsDifferentText() {
        this.checkNotEquals(
            SpreadsheetText.with("different")
                .setColor(COLOR));
    }

    // toString ........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            SpreadsheetText.with(TEXT),
            CharSequences.quote(TEXT).toString()
        );
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void testToStringWithColor() {
        this.toStringAndCheck(
            SpreadsheetText.with(TEXT)
                .setColor(COLOR),
            COLOR.get() + " " + CharSequences.quote(TEXT)
        );
    }

    // factory..........................................................................................................

    private SpreadsheetText createFormattedText() {
        return SpreadsheetText.with(TEXT);
    }

    // json ............................................................................................................

    @Test
    public void testUnmarshallTextOnly() {
        this.unmarshallAndCheck(
            "{ \"text\":  \"1/1/2000\"}",
            SpreadsheetText.with(TEXT));
    }

    @Test
    public void testUnmarshallColorAndText() {
        this.unmarshallAndCheck(
            "{ \"color\": \"#000000\", \"text\":  \"1/1/2000\"}",
            SpreadsheetText.with(TEXT).setColor(COLOR));
    }

    @Test
    public void testMarshallTextOnly() {
        this.marshallAndCheck(
            SpreadsheetText.with(TEXT),
            "{ \"text\":  \"1/1/2000\"}"
        );
    }

    @Test
    public void testMarshallColorAndText() {
        this.marshallAndCheck(
            SpreadsheetText.with(TEXT)
                .setColor(COLOR),
            "{ \"color\": \"black\", \"text\":  \"1/1/2000\"}"
        );
    }

    @Test
    public void testJsonRoundtrip() {
        this.marshallRoundTripTwiceAndCheck(
            SpreadsheetText.with("text-abc-123")
                .setColor(Optional.of(Color.fromRgb(0x123456)))
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetText> type() {
        return SpreadsheetText.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // Object...........................................................................................................

    @Override
    public SpreadsheetText createObject() {
        return SpreadsheetText.with(TEXT)
            .setColor(COLOR);
    }

    // json.............................................................................................................

    @Override
    public SpreadsheetText unmarshall(final JsonNode from,
                                      final JsonNodeUnmarshallContext context) {
        return SpreadsheetText.unmarshall(from, context);
    }

    @Override
    public SpreadsheetText createJsonNodeMarshallingValue() {
        return this.createObject();
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrintEmptyText() {
        this.treePrintAndCheck(
            SpreadsheetText.EMPTY,
            "SpreadsheetText\n"
        );
    }

    @Test
    public void testTreePrintText() {
        this.treePrintAndCheck(
            SpreadsheetText.with("hello123"),
            "SpreadsheetText\n" +
                "  hello123\n"
        );
    }

    @Test
    public void testTreePrintColorAndText() {
        this.treePrintAndCheck(
            SpreadsheetText.with("hello123").setColor(Optional.of(Color.fromRgb(12345678))),
            "SpreadsheetText\n" +
                "  #bc614e\n" +
                "    hello123\n"
        );
    }
}

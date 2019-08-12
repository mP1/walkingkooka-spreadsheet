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
import walkingkooka.collect.map.Maps;
import walkingkooka.color.Color;
import walkingkooka.test.ClassTesting2;
import walkingkooka.test.HashCodeEqualsDefinedTesting;
import walkingkooka.test.ToStringTesting;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.text.HasTextNodeTesting;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStylePropertyName;
import walkingkooka.type.JavaVisibility;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetTextTest implements ClassTesting2<SpreadsheetText>,
        HashCodeEqualsDefinedTesting<SpreadsheetText>,
        HasTextNodeTesting,
        ToStringTesting<SpreadsheetText> {

    private final static Optional<Color> COLOR = Optional.of(Color.BLACK);
    private final static String TEXT = "1/1/2000";

    @Test
    public void testWithNullColorFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetText.with(null, TEXT);
        });
    }

    @Test
    public void testWithNullTextFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetText.with(COLOR, null);
        });
    }

    @Test
    public void testWith() {
        final SpreadsheetText formatted = this.createFormattedText();
        this.check(formatted, COLOR, TEXT);
    }

    @Test
    public void testWithEmptyColor() {
        this.createAndCheck(SpreadsheetText.WITHOUT_COLOR, TEXT);
    }

    @Test
    public void testWithEmptyText() {
        this.createAndCheck(COLOR, "");
    }

    private void createAndCheck(final Optional<Color> color, final String text) {
        final SpreadsheetText formatted = SpreadsheetText.with(color, text);
        this.check(formatted, color, text);
    }

    // setColor...........................................................

    @Test
    public void testSetColorNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createFormattedText().setColor(null);
        });
    }

    @Test
    public void testSetColorSame() {
        final SpreadsheetText formatted = this.createFormattedText();
        assertSame(formatted, formatted.setColor(COLOR));
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
        assertEquals(color, formatted.color(), "color");
        assertEquals(text, formatted.text(), "text");
    }

    // ToTextNode.... ..................................................................................................

    @Test
    public void testToTextNodeWithoutColor() {
        final String text = "abc123";

        this.toTextNodeAndCheck(SpreadsheetText.with(SpreadsheetText.WITHOUT_COLOR, text),
                TextNode.text(text));
    }

    @Test
    public void testToTextNodeWithColor() {
        final String text = "abc123";
        final Color color = Color.fromRgb(0x123456);

        this.toTextNodeAndCheck(SpreadsheetText.with(Optional.of(color), text),
                TextNode.text(text).setAttributes(Maps.of(TextStylePropertyName.TEXT_COLOR, color)));
    }

    // HashCodeEqualsDefined ..................................................................................................

    @Test
    public void testEqualsDifferentColor() {
        this.checkNotEquals(SpreadsheetText.with(Optional.of(Color.WHITE), TEXT));
    }

    @Test
    public void testEqualsDifferentColor2() {
        this.checkNotEquals(SpreadsheetText.with(SpreadsheetText.WITHOUT_COLOR, TEXT));
    }

    @Test
    public void testEqualsDifferentText() {
        this.checkNotEquals(SpreadsheetText.with(COLOR, "different"));
    }

    // toString ..................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createFormattedText(), COLOR.get() + " " + CharSequences.quote(TEXT));
    }

    @Test
    public void testToStringWithoutColor() {
        this.toStringAndCheck(SpreadsheetText.with(SpreadsheetText.WITHOUT_COLOR, TEXT),
                CharSequences.quote(TEXT).toString());
    }

    private SpreadsheetText createFormattedText() {
        return SpreadsheetText.with(COLOR, TEXT);
    }

    @Override
    public Class<SpreadsheetText> type() {
        return SpreadsheetText.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    @Override
    public SpreadsheetText createObject() {
        return SpreadsheetText.with(COLOR, TEXT);
    }
}

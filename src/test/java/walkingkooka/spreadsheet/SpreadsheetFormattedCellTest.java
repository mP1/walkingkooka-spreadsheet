/*
 * Copyright 2018 Miroslav Pokorny (github.com/mP1)
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
 *
 */

package walkingkooka.spreadsheet;

import org.junit.jupiter.api.Test;
import walkingkooka.color.Color;
import walkingkooka.spreadsheet.style.SpreadsheetCellStyle;
import walkingkooka.spreadsheet.style.SpreadsheetTextStyle;
import walkingkooka.test.ClassTestCase;
import walkingkooka.test.HashCodeEqualsDefinedTesting;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.json.HasJsonNodeTesting;
import walkingkooka.type.MemberVisibility;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetFormattedCellTest extends ClassTestCase<SpreadsheetFormattedCell>
        implements HashCodeEqualsDefinedTesting<SpreadsheetFormattedCell>,
        HasJsonNodeTesting<SpreadsheetFormattedCell> {

    private final static String TEXT = "abc123";

    @Test
    public void testWithNullTextFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetFormattedCell.with(null, this.style());
        });
    }

    @Test
    public void testWithNullStyleFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetFormattedCell.with(TEXT, null);
        });
    }

    @Test
    public void testWith() {
        final SpreadsheetFormattedCell formatted = this.createObject();
        this.check(formatted, TEXT, this.style());
    }

    @Test
    public void testWithEmptyText() {
        this.createAndCheck("", this.style());
    }

    private void createAndCheck(final String text, final SpreadsheetCellStyle style) {
        final SpreadsheetFormattedCell formatted = SpreadsheetFormattedCell.with(text, style);
        this.check(formatted, text, style);
    }

    // setText...........................................................

    @Test
    public void testSetTextNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createObject().setText(null);
        });
    }

    @Test
    public void testSetTextSame() {
        final SpreadsheetFormattedCell formatted = this.createObject();
        assertSame(formatted, formatted.setText(TEXT));
    }

    @Test
    public void testSetTextDifferent() {
        final String differentText = "different";
        final SpreadsheetFormattedCell formatted = this.createObject();
        final SpreadsheetFormattedCell different = formatted.setText(differentText);
        assertNotSame(formatted, different);
        this.check(different, differentText, this.style());
    }

    // setStyle...........................................................

    @Test
    public void testSetStyleNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createObject().setStyle(null);
        });
    }

    @Test
    public void testSetStyleSame() {
        final SpreadsheetFormattedCell formatted = this.createObject();
        assertSame(formatted, formatted.setStyle(this.style()));
    }

    @Test
    public void testSetStyleDifferent() {
        final SpreadsheetCellStyle differentStyle = this.style(SpreadsheetTextStyle.EMPTY.setItalics(SpreadsheetTextStyle.ITALICS));
        final SpreadsheetFormattedCell formatted = this.createObject();
        final SpreadsheetFormattedCell different = formatted.setStyle(differentStyle);
        assertNotSame(formatted, different);
        this.check(different, TEXT, differentStyle);
    }

    private void check(final SpreadsheetFormattedCell formatted, final String text, final SpreadsheetCellStyle style) {
        assertEquals(text, formatted.text(), "text");
        assertEquals(style, formatted.style(), "style");
    }

    // setColor.....................................................................

    @Test
    public void testSetTextColorNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createObject().setTextColor(null);
        });
    }

    @Test
    public void testSetTextColorSame() {
        final Color color = Color.fromRgb(123);
        final SpreadsheetFormattedCell formattedCell = SpreadsheetFormattedCell.with(TEXT, this.style(color));
        assertSame(formattedCell, formattedCell.setTextColor(color));
    }

    @Test
    public void testSetTextColorDifferentColor() {
        this.setTextColorAndCheck(SpreadsheetFormattedCell.with(TEXT, this.style(Color.BLACK)),
                Color.fromRgb(123));
    }

    @Test
    public void testSetTextColorReplaceNone() {
        this.setTextColorAndCheck(SpreadsheetFormattedCell.with(TEXT, this.style()),
                Color.fromRgb(123));
    }

    private void setTextColorAndCheck(final SpreadsheetFormattedCell formattedCell, final Color color) {
        final SpreadsheetFormattedCell different = formattedCell.setTextColor(color);
        assertNotSame(different, formattedCell);

        assertEquals(SpreadsheetFormattedCell.with(TEXT, this.style(color)),
                different,
                ()-> "textStyle " + formattedCell + " setColor " + color);
    }

    // equals.....................................................................................

    @Test
    public void testDifferentText() {
        this.checkNotEquals(SpreadsheetFormattedCell.with("different", this.style()));
    }

    @Test
    public void testDifferentStyle() {
        this.checkNotEquals(SpreadsheetFormattedCell.with(TEXT,
                this.style().setText(SpreadsheetTextStyle.EMPTY.setItalics(SpreadsheetTextStyle.ITALICS))));
    }

    // HasJsonNode ................................................................................................

    @Test
    public void testToJsonNode() {
        this.toJsonNodeAndCheck(this.createObject(),
                "{ \"text\": \"abc123\", \"style\": " + this.style().toJsonNode() + "}");
    }

    // toString ................................................................................................

    @Test
    public void testToString() {
        assertEquals(CharSequences.quote(TEXT) + " " + this.style(), this.createObject().toString());
    }

    @Test
    public void testToStringWithoutText() {
        assertEquals(this.style().toString(), SpreadsheetFormattedCell.with("", this.style()).toString());
    }
    
    // helpers ................................................................................

    @Override
    public SpreadsheetFormattedCell createObject() {
        return SpreadsheetFormattedCell.with(TEXT, this.style());
    }

    private SpreadsheetCellStyle style() {
        return this.style(this.textStyle());
    }

    private SpreadsheetTextStyle textStyle() {
        return SpreadsheetTextStyle.EMPTY.setBold(SpreadsheetTextStyle.BOLD);
    }

    private SpreadsheetCellStyle style(final SpreadsheetTextStyle textStyle) {
        return SpreadsheetCellStyle.EMPTY.setText(textStyle);
    }

    private SpreadsheetCellStyle style(final Color color) {
        return this.style(this.textStyle().setColor(Optional.of(color)));
    }

    @Override
    protected MemberVisibility typeVisibility() {
        return MemberVisibility.PUBLIC;
    }

    @Override
    protected Class<SpreadsheetFormattedCell> type() {
        return SpreadsheetFormattedCell.class;
    }
}

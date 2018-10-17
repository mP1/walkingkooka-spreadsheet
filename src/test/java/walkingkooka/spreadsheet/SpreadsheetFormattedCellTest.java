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

import org.junit.Test;
import walkingkooka.spreadsheet.style.SpreadsheetCellStyle;
import walkingkooka.spreadsheet.style.SpreadsheetTextStyle;
import walkingkooka.test.PublicClassTestCase;
import walkingkooka.text.CharSequences;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

public final class SpreadsheetFormattedCellTest extends PublicClassTestCase<SpreadsheetFormattedCell> {

    private final static String TEXT = "abc123";

    @Test(expected = NullPointerException.class)
    public void testWithNullTextFails() {
        SpreadsheetFormattedCell.with(null, this.style());
    }

    @Test(expected = NullPointerException.class)
    public void testWithNullStyleFails() {
        SpreadsheetFormattedCell.with(TEXT, null);
    }

    @Test
    public void testWith() {
        final SpreadsheetFormattedCell formatted = this.createFormattedText();
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

    @Test(expected = NullPointerException.class)
    public void testSetTextNullFails() {
        this.createFormattedText().setText(null);
    }

    @Test
    public void testSetTextSame() {
        final SpreadsheetFormattedCell formatted = this.createFormattedText();
        assertSame(formatted, formatted.setText(TEXT));
    }

    @Test
    public void testSetTextDifferent() {
        final String differentText = "different";
        final SpreadsheetFormattedCell formatted = this.createFormattedText();
        final SpreadsheetFormattedCell different = formatted.setText(differentText);
        assertNotSame(formatted, different);
        this.check(different, differentText, this.style());
    }

    // setStyle...........................................................

    @Test(expected = NullPointerException.class)
    public void testSetStyleNullFails() {
        this.createFormattedText().setStyle(null);
    }

    @Test
    public void testSetStyleSame() {
        final SpreadsheetFormattedCell formatted = this.createFormattedText();
        assertSame(formatted, formatted.setStyle(this.style()));
    }

    @Test
    public void testSetStyleDifferent() {
        final SpreadsheetCellStyle differentStyle = SpreadsheetCellStyle.EMPTY.setText(SpreadsheetTextStyle.EMPTY.setItalics(SpreadsheetTextStyle.ITALICS));
        final SpreadsheetFormattedCell formatted = this.createFormattedText();
        final SpreadsheetFormattedCell different = formatted.setStyle(differentStyle);
        assertNotSame(formatted, different);
        this.check(different, TEXT, differentStyle);
    }

    private void check(final SpreadsheetFormattedCell formatted, final String text, final SpreadsheetCellStyle style) {
        assertEquals("text", text, formatted.text());
        assertEquals("style", style, formatted.style());
    }

    @Test
    public void testToString() {
        assertEquals(CharSequences.quote(TEXT) + " " + this.style(), this.createFormattedText().toString());
    }

    @Test
    public void testToStringWithoutText() {
        assertEquals(this.style().toString(), SpreadsheetFormattedCell.with("", this.style()).toString());
    }

    private SpreadsheetFormattedCell createFormattedText() {
        return SpreadsheetFormattedCell.with(TEXT, this.style());
    }

    private SpreadsheetCellStyle style() {
        return SpreadsheetCellStyle.EMPTY.setText(SpreadsheetTextStyle.EMPTY.setBold(SpreadsheetTextStyle.BOLD));
    }

    @Override
    protected Class<SpreadsheetFormattedCell> type() {
        return SpreadsheetFormattedCell.class;
    }
}

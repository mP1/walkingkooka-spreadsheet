package walkingkooka.spreadsheet.style;

import org.junit.jupiter.api.Test;
import walkingkooka.color.Color;
import walkingkooka.spreadsheet.SpreadsheetFormattedCell;
import walkingkooka.test.ClassTesting2;
import walkingkooka.test.HashCodeEqualsDefinedTesting;
import walkingkooka.test.ToStringTesting;
import walkingkooka.tree.json.HasJsonNodeTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonNodeName;
import walkingkooka.type.MemberVisibility;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetCellStyleTest implements ClassTesting2<SpreadsheetCellStyle>,
        HashCodeEqualsDefinedTesting<SpreadsheetCellStyle>,
        HasJsonNodeTesting<SpreadsheetCellStyle>,
        ToStringTesting<SpreadsheetCellStyle> {

    @Test
    public void testWithNullTextFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetCellStyle.with(null);
        });
    }

    @Test
    public void testWith() {
        this.checkText(this.createObject(), this.text());
    }

    @Test
    public void testEmpty() {
        final SpreadsheetCellStyle style = SpreadsheetCellStyle.EMPTY;
        this.checkText(style, SpreadsheetTextStyle.EMPTY);
    }

    // setCellFormattedText....................................................................................

    @Test
    public void testSetCellFormattedText() {
        final SpreadsheetCellStyle style = this.createObject();
        final String text = "abc123";
        final SpreadsheetFormattedCell formatted = style.setCellFormattedText(text);
        assertEquals(text, formatted.text(), "text");
        assertEquals(style, formatted.style(), "style");
    }

    // setText......................................................................................................

    @Test
    public void testSetTextSame() {
        final SpreadsheetCellStyle style = this.createObject();
        assertSame(style, style.setText(this.text()));
    }

    @Test
    public void testSetTextNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createObject().setText(null);
        });
    }

    @Test
    public void testSetTextDifferent() {
        final SpreadsheetTextStyle different = SpreadsheetTextStyle.EMPTY.setItalics(SpreadsheetTextStyle.ITALICS);
        final SpreadsheetCellStyle style = this.createObject().setText(different);

        this.checkText(style, different);
    }

    // merge.........................................................................................................

    @Test
    public void testMergeNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createObject().merge(null);
        });
    }

    @Test
    public void testMergeSelf() {
        final SpreadsheetCellStyle style = this.createObject();
        assertSame(style, style.merge(style));
    }

    @Test
    public void testMergeWithEmpty() {
        final SpreadsheetCellStyle style = this.createObject();
        assertSame(style, style.merge(SpreadsheetCellStyle.EMPTY));
    }

    @Test
    public void testMerge1() {
        final SpreadsheetCellStyle style = this.createObject();
        assertSame(style, SpreadsheetCellStyle.EMPTY.merge(style));
    }

    @Test
    public void testMerge2() {
        final SpreadsheetCellStyle style = SpreadsheetCellStyle.EMPTY.setText(
                SpreadsheetTextStyle.EMPTY
                        .setBold(SpreadsheetTextStyle.BOLD)
                        .setItalics(SpreadsheetTextStyle.ITALICS));
        final SpreadsheetCellStyle other = SpreadsheetCellStyle.EMPTY.setText(
                SpreadsheetTextStyle.EMPTY
                        .setBold(SpreadsheetTextStyle.NO_BOLD)
                        .setUnderline(SpreadsheetTextStyle.UNDERLINE));

        this.mergeAndCheck(style, other, SpreadsheetCellStyle.EMPTY.setText(
                SpreadsheetTextStyle.EMPTY
                        .setBold(SpreadsheetTextStyle.BOLD)
                        .setItalics(SpreadsheetTextStyle.ITALICS)
                        .setUnderline(SpreadsheetTextStyle.UNDERLINE)));
    }

    private void mergeAndCheck(final SpreadsheetCellStyle style, final SpreadsheetCellStyle other, final SpreadsheetCellStyle expected) {
        assertEquals(expected,
                style.merge(other),
                () -> style + " merge " + other + " failed");
    }

    // isEmpty.........................................................................................................

    @Test
    public void testIsEmptyEmpty() {
        this.isEmptyAndCheck(SpreadsheetCellStyle.EMPTY,
                true);
    }

    @Test
    public void testIsEmptyTextEmpty() {
        this.isEmptyAndCheck(SpreadsheetCellStyle.EMPTY.setText(SpreadsheetTextStyle.EMPTY),
                true);
    }

    @Test
    public void testIsEmptyNotEmpty() {
        this.isEmptyAndCheck(this.createObject(), false);
    }

    @Test
    public void testIsEmptyNotEmpty2() {
        this.isEmptyAndCheck(SpreadsheetCellStyle.EMPTY.setText(SpreadsheetTextStyle.EMPTY.setBold(Optional.of(true))),
                false);
    }

    private void isEmptyAndCheck(final SpreadsheetCellStyle style, final boolean empty) {
        assertEquals(empty,
                style.isEmpty(),
                () -> style + " is empty");
    }

    // HasJsonNode.........................................................................................................

    // HasJsonNode fromJsonNode........................................................................................................

    @Test
    public void testFromJsonNodeBooleanFails() {
        this.fromJsonNodeFails(JsonNode.booleanNode(true));
    }

    @Test
    public void testFromJsonNodeNullFails() {
        this.fromJsonNodeFails(JsonNode.nullNode());
    }

    @Test
    public void testFromJsonNodeNumberFails() {
        this.fromJsonNodeFails(JsonNode.number(12));
    }

    @Test
    public void testFromJsonNodeArrayFails() {
        this.fromJsonNodeFails(JsonNode.array());
    }

    @Test
    public void testFromJsonNodeStringFails() {
        this.fromJsonNodeFails(JsonNode.string("fails"));
    }

    @Test
    public void testFromJsonNodeObjectUnknownPropertyFails() {
        this.fromJsonNodeFails(JsonNode.object().set(JsonNodeName.with("unknown-property-fails"), JsonNode.string("fails!")));
    }

    @Test
    public void testFromJsonNodeObjectEmpty() {
        this.fromJsonNodeAndCheck(JsonNode.object(),
                SpreadsheetCellStyle.EMPTY);
    }

    @Test
    public void testFromJsonNodeTextStyleWithFontFamilyName() {
        final SpreadsheetTextStyle text = SpreadsheetTextStyle.EMPTY.setFontFamily(Optional.of(FontFamilyName.with("Times New Roman")));

        this.fromJsonNodeAndCheck(JsonNode.object()
                        .set(SpreadsheetCellStyle.TEXT_PROPERTY, text.toJsonNode()),
                SpreadsheetCellStyle.EMPTY.setText(text));
    }

    @Test
    public void testFromJsonNodeTextStyle() {
        final SpreadsheetTextStyle text = this.text();

        this.fromJsonNodeAndCheck(JsonNode.object()
                        .set(SpreadsheetCellStyle.TEXT_PROPERTY, text.toJsonNode()),
                SpreadsheetCellStyle.EMPTY.setText(text));
    }

    // HasJsonNode toJsonNode........................................................................................................

    @Test
    public void testToJsonNodeEmpty() {
        this.toJsonNodeAndCheck(SpreadsheetCellStyle.EMPTY,
                JsonNode.object());
    }

    @Test
    public void testToJsonNode() {
        this.toJsonNodeAndCheck(this.createObject(),
                "{ \"text\": " + this.text().toJsonNode() + "}");
    }

    // toString.........................................................................................................

    @Test
    public void testToStringEmpty() {
        this.toStringAndCheck(SpreadsheetCellStyle.EMPTY, "");
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(SpreadsheetCellStyle.EMPTY.setText(SpreadsheetTextStyle.EMPTY
                        .setBold(Optional.of(true))
                        .setFontFamily(Optional.of(FontFamilyName.with("Times New Roman")))
                        .setColor(Optional.of(Color.fromRgb(123456)))),
                "Times New Roman #01e240 bold");
    }


    // helpers.........................................................................................................

    @Override
    public SpreadsheetCellStyle createObject() {
        return SpreadsheetCellStyle.with(this.text());
    }

    private SpreadsheetTextStyle text() {
        return SpreadsheetTextStyle.EMPTY.setBold(SpreadsheetTextStyle.BOLD);
    }

    private void checkText(final SpreadsheetCellStyle style, final SpreadsheetTextStyle text) {
        assertEquals(text, style.text(), "text");
    }

    @Override
    public Class<SpreadsheetCellStyle> type() {
        return SpreadsheetCellStyle.class;
    }

    @Override
    public MemberVisibility typeVisibility() {
        return MemberVisibility.PUBLIC;
    }

    // HasJsonNodeTesting............................................................

    @Override
    public SpreadsheetCellStyle fromJsonNode(final JsonNode jsonNode) {
        return SpreadsheetCellStyle.fromJsonNode(jsonNode);
    }
}

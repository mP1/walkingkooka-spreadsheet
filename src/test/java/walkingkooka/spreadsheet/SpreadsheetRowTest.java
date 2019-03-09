package walkingkooka.spreadsheet;

import org.junit.jupiter.api.Test;
import walkingkooka.compare.ComparableTesting;
import walkingkooka.test.ClassTesting2;
import walkingkooka.test.ToStringTesting;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetReferenceKind;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRowReference;
import walkingkooka.tree.json.HasJsonNodeTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.type.MemberVisibility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetRowTest implements ClassTesting2<SpreadsheetRow>,
        ComparableTesting<SpreadsheetRow>,
        HasJsonNodeTesting<SpreadsheetRow>,
        ToStringTesting<SpreadsheetRow> {


    private final static int ROW = 20;
    private final static SpreadsheetRowReference REFERENCE = reference(ROW);

    @Test
    public void testWithNullReferenceFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetRow.with(null);
        });
    }

    @Test
    public void testWith() {
        final SpreadsheetRow row = this.createRow();

        this.checkReference(row);
    }

    // SetReference.....................................................................................................

    @Test
    public void testSetReferenceNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createRow().setReference(null);
        });
    }

    @Test
    public void testSetReferenceSame() {
        final SpreadsheetRow row = this.createRow();
        assertSame(row, row.setReference(row.reference()));
    }

    @Test
    public void testSetReferenceDifferent() {
        final SpreadsheetRow row = this.createRow();
        final SpreadsheetRowReference differentReference = differentReference();
        final SpreadsheetRow different = row.setReference(differentReference);
        assertNotSame(row, different);

        this.checkReference(different, differentReference);

        this.checkReference(row);
    }

    // equals .............................................................................................

    @Test
    public void testCompareDifferentRow() {
        this.compareToAndCheckLess(this.createComparable(ROW + 999));
    }

    // HasJsonNode...............................................................................................

    // HasJsonNode.fromJsonNode.......................................................................................

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
    public void testFromJsonNodeObjectEmptyFails() {
        this.fromJsonNodeFails(JsonNode.object());
    }


    // HasJsonNode .toJsonNode.........................................................................
    @Test
    public void testJsonNode() {
        final SpreadsheetRowReference reference = this.reference(ROW);
        this.toJsonNodeAndCheck(reference, reference.toJsonNode());
    }

    // toString...............................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(SpreadsheetRow.with(REFERENCE), "$21");
    }

    private SpreadsheetRow createRow() {
        return this.createComparable();
    }

    @Override
    public SpreadsheetRow createComparable() {
        return this.createComparable(ROW);
    }

    private SpreadsheetRow createComparable(final int row) {
        return SpreadsheetRow.with(this.reference(row));
    }

    private static SpreadsheetRowReference differentReference() {
        return reference(999);
    }

    private static SpreadsheetRowReference reference(final int row) {
        return SpreadsheetReferenceKind.ABSOLUTE.row(row);
    }

    private void checkReference(final SpreadsheetRow row) {
        this.checkReference(row, REFERENCE);
    }

    private void checkReference(final SpreadsheetRow row, final SpreadsheetRowReference reference) {
        assertEquals(reference, row.reference(), "reference");
    }

    @Override
    public Class<SpreadsheetRow> type() {
        return SpreadsheetRow.class;
    }

    @Override
    public MemberVisibility typeVisibility() {
        return MemberVisibility.PUBLIC;
    }

    @Override
    public boolean compareAndEqualsMatch() {
        return true;
    }

    // HasJsonNodeTesting............................................................

    @Override
    public SpreadsheetRow createHasJsonNode() {
        return this.createObject();
    }

    @Override
    public SpreadsheetRow fromJsonNode(final JsonNode jsonNode) {
        return SpreadsheetRow.fromJsonNode(jsonNode);
    }
}

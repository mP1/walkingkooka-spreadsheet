package walkingkooka.spreadsheet;

import org.junit.Test;
import walkingkooka.test.ClassTestCase;
import walkingkooka.test.HashCodeEqualsDefinedTesting;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetReferenceKind;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.type.MemberVisibility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;


public final class SpreadsheetLabelMappingTest extends ClassTestCase<SpreadsheetLabelMapping>
        implements HashCodeEqualsDefinedTesting<SpreadsheetLabelMapping> {

    private final static SpreadsheetLabelName LABEL =SpreadsheetLabelName.with("label");
    private final static ExpressionReference REFERENCE = cell(1);

    @Test(expected = NullPointerException.class)
    public void testWithNullLabelFails() {
        SpreadsheetLabelMapping.with(null, REFERENCE);
    }

    @Test(expected = NullPointerException.class)
    public void testWithNullReferenceFails() {
        SpreadsheetLabelMapping.with(LABEL, null);
    }

    @Test
    public void testWith() {
        final SpreadsheetLabelMapping mapping = this.createObject();
        this.checkLabel(mapping, LABEL);
        this.checkReference(mapping, REFERENCE);
    }
    
    // setLabel.......................................................................................................

    @Test(expected = NullPointerException.class)
    public void testSetLabelNullFails() {
        this.createObject().setLabel(null);
    }

    @Test
    public void testSetLabelSame() {
        final SpreadsheetLabelMapping mapping = this.createObject();
        assertSame(mapping, mapping.setLabel(LABEL));
    }

    @Test
    public void testSetLabelDifferent() {
        final SpreadsheetLabelMapping mapping = this.createObject();
        final SpreadsheetLabelName differentLabel = SpreadsheetLabelName.with("different");
        final SpreadsheetLabelMapping different = mapping.setLabel(differentLabel);

        assertNotSame(mapping, different);
        this.checkLabel(different, differentLabel);
        this.checkReference(different, REFERENCE);
    }

    // setReference.......................................................................................................

    @Test(expected = NullPointerException.class)
    public void testSetReferenceNullFails() {
        this.createObject().setReference(null);
    }

    @Test
    public void testSetReferenceSame() {
        final SpreadsheetLabelMapping mapping = this.createObject();
        assertSame(mapping, mapping.setReference(REFERENCE));
    }

    @Test
    public void testSetReferenceDifferent() {
        final SpreadsheetLabelMapping mapping = this.createObject();
        final ExpressionReference differentReference = cell(999);
        final SpreadsheetLabelMapping different = mapping.setReference(differentReference);

        assertNotSame(mapping, different);
        this.checkLabel(different, LABEL);
        this.checkReference(different, differentReference);
    }

    // equals................................................................................

    @Test
    public void testEqualsDifferentLabel() {
        this.checkNotEquals(SpreadsheetLabelMapping.with(SpreadsheetLabelName.with("different"), REFERENCE));
    }

    @Test
    public void testEqualsDifferentCell() {
        this.checkNotEquals(SpreadsheetLabelMapping.with(LABEL, cell(99)));
    }

    // toString...............................................................................................

    @Test
    public void testToString() {
        assertEquals(LABEL + "=" + REFERENCE, this.createObject().toString());
    }

    // helpers...............................................................................................

    @Override
    public SpreadsheetLabelMapping createObject() {
        return SpreadsheetLabelMapping.with(LABEL, REFERENCE);
    }

    private void checkLabel(final SpreadsheetLabelMapping mapping, final SpreadsheetLabelName label) {
        assertEquals("label", label, mapping.label());
    }
    
    private void checkReference(final SpreadsheetLabelMapping mapping, final ExpressionReference reference) {
        assertEquals("reference", reference, mapping.reference());
    }

    private static ExpressionReference cell(final int column) {
        return SpreadsheetReferenceKind.ABSOLUTE.column(column)
                .setRow(SpreadsheetReferenceKind.RELATIVE.row(2));
    }

    @Override
    protected Class<SpreadsheetLabelMapping> type() {
        return SpreadsheetLabelMapping.class;
    }

    @Override
    protected MemberVisibility typeVisibility() {
        return MemberVisibility.PUBLIC;
    }
}

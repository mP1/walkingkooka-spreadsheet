package walkingkooka.spreadsheet;

import org.junit.jupiter.api.Test;
import walkingkooka.test.ClassTestCase;
import walkingkooka.test.HashCodeEqualsDefinedTesting;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetReferenceKind;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.type.MemberVisibility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;


public final class SpreadsheetLabelMappingTest extends ClassTestCase<SpreadsheetLabelMapping>
        implements HashCodeEqualsDefinedTesting<SpreadsheetLabelMapping> {

    private final static SpreadsheetLabelName LABEL =SpreadsheetLabelName.with("label");
    private final static ExpressionReference REFERENCE = cell(1);

    @Test
    public void testWithNullLabelFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetLabelMapping.with(null, REFERENCE);
        });
    }

    @Test
    public void testWithNullReferenceFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetLabelMapping.with(LABEL, null);
        });
    }

    @Test
    public void testWith() {
        final SpreadsheetLabelMapping mapping = this.createObject();
        this.checkLabel(mapping, LABEL);
        this.checkReference(mapping, REFERENCE);
    }
    
    // setLabel.......................................................................................................

    @Test
    public void testSetLabelNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createObject().setLabel(null);
        });
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

    @Test
    public void testSetReferenceNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createObject().setReference(null);
        });
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
        assertEquals(label, mapping.label(), "label");
    }
    
    private void checkReference(final SpreadsheetLabelMapping mapping, final ExpressionReference reference) {
        assertEquals(reference, mapping.reference(), "reference");
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

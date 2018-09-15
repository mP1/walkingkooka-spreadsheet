package walkingkooka.spreadsheet;

import org.junit.Test;
import walkingkooka.test.PublicClassTestCase;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetReferenceKind;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;


public final class SpreadsheetLabelMappingTest extends PublicClassTestCase<SpreadsheetLabelMapping> {

    private final static SpreadsheetLabelName LABEL =SpreadsheetLabelName.with("label");
    private final static SpreadsheetCellReference CELL = cell(1);

    @Test(expected = NullPointerException.class)
    public void testWithNullLabelFails() {
        SpreadsheetLabelMapping.with(null, CELL);
    }

    @Test(expected = NullPointerException.class)
    public void testWithNullCellFails() {
        SpreadsheetLabelMapping.with(LABEL, null);
    }

    @Test
    public void testWith() {
        final SpreadsheetLabelMapping mapping = this.createMapping();
        this.checkLabel(mapping, LABEL);
        this.checkCell(mapping, CELL);
    }
    
    // setLabel.......................................................................................................

    @Test(expected = NullPointerException.class)
    public void testSetLabelNullFails() {
        this.createMapping().setLabel(null);
    }

    @Test
    public void testSetLabelSame() {
        final SpreadsheetLabelMapping mapping = this.createMapping();
        assertSame(mapping, mapping.setLabel(LABEL));
    }

    @Test
    public void testSetLabelDifferent() {
        final SpreadsheetLabelMapping mapping = this.createMapping();
        final SpreadsheetLabelName differentLabel = SpreadsheetLabelName.with("different");
        final SpreadsheetLabelMapping different = mapping.setLabel(differentLabel);

        assertNotSame(mapping, different);
        this.checkLabel(different, differentLabel);
        this.checkCell(different, CELL);
    }

    // setCell.......................................................................................................

    @Test(expected = NullPointerException.class)
    public void testSetCellNullFails() {
        this.createMapping().setCell(null);
    }

    @Test
    public void testSetCellSame() {
        final SpreadsheetLabelMapping mapping = this.createMapping();
        assertSame(mapping, mapping.setCell(CELL));
    }

    @Test
    public void testSetCellDifferent() {
        final SpreadsheetLabelMapping mapping = this.createMapping();
        final SpreadsheetCellReference differentCell = cell(999);
        final SpreadsheetLabelMapping different = mapping.setCell(differentCell);

        assertNotSame(mapping, different);
        this.checkLabel(different, LABEL);
        this.checkCell(different, differentCell);
    }

    // toString...............................................................................................

    @Test
    public void testToString() {
        assertEquals(LABEL + "=" + CELL, this.createMapping().toString());
    }

    private SpreadsheetLabelMapping createMapping() {
        return SpreadsheetLabelMapping.with(LABEL, CELL);
    }

    private void checkLabel(final SpreadsheetLabelMapping mapping, final SpreadsheetLabelName label) {
        assertEquals("label", label, mapping.label());
    }
    
    private void checkCell(final SpreadsheetLabelMapping mapping, final SpreadsheetCellReference cell) {
        assertEquals("cell", cell, mapping.cell());
    }

    private static SpreadsheetCellReference cell(final int column) {
        return SpreadsheetReferenceKind.ABSOLUTE.column(column)
                .setRow(SpreadsheetReferenceKind.RELATIVE.row(2));
    }

    @Override
    protected Class<SpreadsheetLabelMapping> type() {
        return SpreadsheetLabelMapping.class;
    }
}

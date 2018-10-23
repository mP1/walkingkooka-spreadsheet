package walkingkooka.spreadsheet;

import org.junit.Test;
import walkingkooka.test.PublicClassTestCase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;


public final class SpreadsheetMetadataTest extends PublicClassTestCase<SpreadsheetMetadata> {

    private final static int COLUMN = 12;
    private final static int ROW = 34;
    
    @Test
    public void testWith() {
        final SpreadsheetMetadata meta = this.createSpreadsheetMeta();
        this.checkColumnCount(meta, COLUMN);
        this.checkRowCount(meta, ROW);
    }

    @Test
    public void testWithZeroZero() {
        final SpreadsheetMetadata meta = SpreadsheetMetadata.with(0, 0);
        this.checkColumnCount(meta, 0);
        this.checkRowCount(meta, 0);
    }
    
    // setColumnCount ....................................................................................................

    @Test(expected = IllegalArgumentException.class)
    public void testSetColumnCountNegativeFails() {
        this.createSpreadsheetMeta().setColumnCount(-1);
    }

    @Test
    public void testSetColumnCountSame() {
        final SpreadsheetMetadata meta = this.createSpreadsheetMeta();
        assertSame(meta, meta.setColumnCount(meta.columnCount()));
    }

    @Test
    public void testSetColumnCountDifferent() {
        final SpreadsheetMetadata meta = this.createSpreadsheetMeta();
        final int differentColumnCount = 999;
        final SpreadsheetMetadata different = meta.setColumnCount(differentColumnCount);
        assertNotSame(meta, different);

        this.checkColumnCount(different, differentColumnCount);
        this.checkRowCount(different, ROW);
    }

    // setRowCount ....................................................................................................

    @Test(expected = IllegalArgumentException.class)
    public void testSetRowCountNegativeFails() {
        this.createSpreadsheetMeta().setRowCount(-1);
    }

    @Test
    public void testSetRowCountSame() {
        final SpreadsheetMetadata meta = this.createSpreadsheetMeta();
        assertSame(meta, meta.setRowCount(meta.rowCount()));
    }

    @Test
    public void testSetRowCountDifferent() {
        final SpreadsheetMetadata meta = this.createSpreadsheetMeta();
        final int differentRowCount = 999;
        final SpreadsheetMetadata different = meta.setRowCount(differentRowCount);
        assertNotSame(meta, different);

        this.checkColumnCount(different, COLUMN);
        this.checkRowCount(different, differentRowCount);
    }

    // toString ....................................................................................................

    @Test
    public void testToString() {
        assertEquals("12x34", this.createSpreadsheetMeta().toString());
    }

    private SpreadsheetMetadata createSpreadsheetMeta() {
        return SpreadsheetMetadata.with(COLUMN, ROW);
    }

    private void checkColumnCount(final SpreadsheetMetadata meta, final int column) {
        assertEquals("column", column, meta.columnCount());
    }

    private void checkRowCount(final SpreadsheetMetadata meta, final int row) {
        assertEquals("row", row, meta.rowCount());
    }
    
    @Override
    protected Class<SpreadsheetMetadata> type() {
        return SpreadsheetMetadata.class;
    }
}

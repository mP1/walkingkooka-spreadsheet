package walkingkooka.spreadsheet;

import org.junit.Test;
import walkingkooka.test.ClassTestCase;
import walkingkooka.test.HashCodeEqualsDefinedTesting;
import walkingkooka.type.MemberVisibility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;


public final class SpreadsheetMetadataTest extends ClassTestCase<SpreadsheetMetadata>
        implements HashCodeEqualsDefinedTesting<SpreadsheetMetadata> {

    private final static int COLUMN = 12;
    private final static int ROW = 34;
    
    @Test
    public void testWith() {
        final SpreadsheetMetadata meta = this.createObject();
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
        this.createObject().setColumnCount(-1);
    }

    @Test
    public void testSetColumnCountSame() {
        final SpreadsheetMetadata meta = this.createObject();
        assertSame(meta, meta.setColumnCount(meta.columnCount()));
    }

    @Test
    public void testSetColumnCountDifferent() {
        final SpreadsheetMetadata meta = this.createObject();
        final int differentColumnCount = 999;
        final SpreadsheetMetadata different = meta.setColumnCount(differentColumnCount);
        assertNotSame(meta, different);

        this.checkColumnCount(different, differentColumnCount);
        this.checkRowCount(different, ROW);
    }

    // setRowCount ....................................................................................................

    @Test(expected = IllegalArgumentException.class)
    public void testSetRowCountNegativeFails() {
        this.createObject().setRowCount(-1);
    }

    @Test
    public void testSetRowCountSame() {
        final SpreadsheetMetadata meta = this.createObject();
        assertSame(meta, meta.setRowCount(meta.rowCount()));
    }

    @Test
    public void testSetRowCountDifferent() {
        final SpreadsheetMetadata meta = this.createObject();
        final int differentRowCount = 999;
        final SpreadsheetMetadata different = meta.setRowCount(differentRowCount);
        assertNotSame(meta, different);

        this.checkColumnCount(different, COLUMN);
        this.checkRowCount(different, differentRowCount);
    }

    // equals...............................................................................................

    @Test
    public void testDifferentColumnCount() {
        this.checkNotEquals(SpreadsheetMetadata.with(999, ROW));
    }

    @Test
    public void testDifferentRowCount() {
        this.checkNotEquals(SpreadsheetMetadata.with(COLUMN, 999));
    }

    // toString ....................................................................................................

    @Test
    public void testToString() {
        assertEquals("12x34", this.createObject().toString());
    }

    // helpers ........................................................................................


    @Override
    public SpreadsheetMetadata createObject() {
        return SpreadsheetMetadata.with(COLUMN, ROW);
    }

    private void checkColumnCount(final SpreadsheetMetadata meta, final int column) {
        assertEquals("column", column, meta.columnCount());
    }

    private void checkRowCount(final SpreadsheetMetadata meta, final int row) {
        assertEquals("row", row, meta.rowCount());
    }
    
    @Override
    public Class<SpreadsheetMetadata> type() {
        return SpreadsheetMetadata.class;
    }

    @Override
    protected MemberVisibility typeVisibility() {
        return MemberVisibility.PUBLIC;
    }
}

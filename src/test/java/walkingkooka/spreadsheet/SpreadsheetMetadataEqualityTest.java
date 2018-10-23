package walkingkooka.spreadsheet;

import org.junit.Test;
import walkingkooka.test.HashCodeEqualsDefinedEqualityTestCase;

public final class SpreadsheetMetadataEqualityTest extends HashCodeEqualsDefinedEqualityTestCase<SpreadsheetMetadata> {

    private final static int COLUMN = 12;
    private final static int ROW = 34;

    @Test
    public void testDifferentColumnCount() {
        this.checkNotEquals(SpreadsheetMetadata.with(999, ROW));
    }

    @Test
    public void testDifferentRowCount() {
        this.checkNotEquals(SpreadsheetMetadata.with(COLUMN, 999));
    }

    @Override
    protected SpreadsheetMetadata createObject() {
        return SpreadsheetMetadata.with(COLUMN, ROW);
    }
}

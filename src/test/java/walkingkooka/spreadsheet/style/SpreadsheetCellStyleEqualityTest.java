package walkingkooka.spreadsheet.style;

import org.junit.Test;
import walkingkooka.test.HashCodeEqualsDefinedEqualityTestCase;

public final class SpreadsheetCellStyleEqualityTest extends HashCodeEqualsDefinedEqualityTestCase<SpreadsheetCellStyle> {

    @Test
    public void testEmptyEquals() {
        this.checkEquals(SpreadsheetCellStyle.EMPTY, SpreadsheetCellStyle.EMPTY);
    }

    @Test
    public void testEmptyDifferent() {
        this.checkNotEquals(SpreadsheetCellStyle.EMPTY);
    }

    @Test
    public void testDifferentText() {
        this.checkNotEquals(SpreadsheetCellStyle.with(SpreadsheetTextStyle.EMPTY.setItalics(SpreadsheetTextStyle.ITALICS)));
    }

    @Override
    protected SpreadsheetCellStyle createObject() {
        return SpreadsheetCellStyle.with(SpreadsheetTextStyle.EMPTY.setBold(SpreadsheetTextStyle.BOLD));
    }
}

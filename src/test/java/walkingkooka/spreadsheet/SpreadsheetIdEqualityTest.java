package walkingkooka.spreadsheet;

import org.junit.Test;
import walkingkooka.test.HashCodeEqualsDefinedEqualityTestCase;

public final class SpreadsheetIdEqualityTest extends HashCodeEqualsDefinedEqualityTestCase<SpreadsheetId> {

    @Test
    public void testDifferentSpreadsheetId() {
        this.checkNotEquals(SpreadsheetId.with(999));
    }

    @Override
    protected SpreadsheetId createObject() {
        return SpreadsheetId.with(123);
    }
}

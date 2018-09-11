package walkingkooka.spreadsheet;

import org.junit.Test;
import walkingkooka.test.HashCodeEqualsDefinedEqualityTestCase;

public final class SpreadsheetErrorEqualityTest extends HashCodeEqualsDefinedEqualityTestCase<SpreadsheetError> {

    private final static String MESSAGE = "message #1";

    @Test
    public void testDifferentValue() {
        this.checkNotEquals(SpreadsheetError.with("different"));
    }

    @Test
    public void testDifferentCase() {
        this.checkNotEquals(SpreadsheetError.with(MESSAGE.toUpperCase()));
    }

    @Override
    protected SpreadsheetError createObject() {
        return SpreadsheetError.with(MESSAGE.toLowerCase());
    }
}

package walkingkooka.spreadsheet;

import org.junit.Test;
import walkingkooka.test.HashCodeEqualsDefinedEqualityTestCase;

public final class SpreadsheetDescriptionEqualityTest extends HashCodeEqualsDefinedEqualityTestCase<SpreadsheetDescription> {

    private final static String TEXT = "description #1";

    @Test
    public void testDifferentValue() {
        this.checkNotEquals(SpreadsheetDescription.with("different"));
    }

    @Test
    public void testDifferentCase() {
        this.checkNotEquals(SpreadsheetDescription.with(TEXT.toUpperCase()));
    }

    @Override
    protected SpreadsheetDescription createObject() {
        return SpreadsheetDescription.with(TEXT.toLowerCase());
    }
}

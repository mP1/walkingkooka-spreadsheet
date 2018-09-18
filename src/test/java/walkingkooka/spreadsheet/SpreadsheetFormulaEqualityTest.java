package walkingkooka.spreadsheet;

import org.junit.*;
import walkingkooka.test.*;

public final class SpreadsheetFormulaEqualityTest extends HashCodeEqualsDefinedEqualityTestCase<SpreadsheetFormula> {

    @Test
    public void testDifferentSpreadsheetFormula() {
        this.checkNotEquals(SpreadsheetFormula.with("different"));
    }

    @Override
    protected SpreadsheetFormula createObject() {
        return SpreadsheetFormula.with("text");
    }
}

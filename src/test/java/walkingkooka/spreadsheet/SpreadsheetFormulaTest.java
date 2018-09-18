package walkingkooka.spreadsheet;

import org.junit.*;
import walkingkooka.test.*;

import static org.junit.Assert.*;


public final class SpreadsheetFormulaTest extends PublicClassTestCase<SpreadsheetFormula> {

    private final static String VALUE = "a+2";

    @Test(expected = NullPointerException.class)
    public void testWithNullExpressionFails() {
        SpreadsheetFormula.with(null);
    }

    @Test
    public void testWith() {
        final SpreadsheetFormula formula = this.create();
        this.checkValue(formula, VALUE);
    }

    @Test
    public void testWithEmpty() {
        final String value = "";
        final SpreadsheetFormula formula = SpreadsheetFormula.with(value);
        this.checkValue(formula, value);
    }

    @Test(expected = NullPointerException.class)
    public void testSetValueNullFails() {
        this.create().setValue(null);
    }

    @Test
    public void testSetValueSame() {
        final SpreadsheetFormula formula = this.create();
        assertSame(formula, formula.setValue(VALUE));
    }

    @Test
    public void testSetValueDifferent() {
        this.setValueAndCheck("different");
    }

    @Test
    public void testSetValueDifferentEmpty() {
        this.setValueAndCheck("");
    }

    private void setValueAndCheck(final String differentValue) {
        final SpreadsheetFormula formula = this.create();
        final SpreadsheetFormula different = formula.setValue(differentValue);
        assertNotSame(formula, different);
        this.checkValue(different, differentValue);
    }

    @Test
    public void testToString() {
        assertEquals("" + VALUE, SpreadsheetFormula.with(VALUE).toString());
    }

    private SpreadsheetFormula create() {
        return SpreadsheetFormula.with(VALUE);
    }

    private void checkValue(final SpreadsheetFormula formula, final String value) {
        assertEquals("value(ExpressionNode)", value, formula.value());
    }


    @Override
    protected Class<SpreadsheetFormula> type() {
        return SpreadsheetFormula.class;
    }
}

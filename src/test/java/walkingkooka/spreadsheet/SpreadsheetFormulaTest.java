package walkingkooka.spreadsheet;

import org.junit.*;
import walkingkooka.test.*;
import walkingkooka.tree.expression.*;

import static org.junit.Assert.*;


public final class SpreadsheetFormulaTest extends PublicClassTestCase<SpreadsheetFormula> {

    private final static String VALUE = "a+2";

    @Test(expected = NullPointerException.class)
    public void testWithNullExpressionFails() {
        SpreadsheetFormula.with(null);
    }

    @Test
    public void testWith() {
        final SpreadsheetFormula formula = SpreadsheetFormula.with(VALUE);
        assertEquals("value(ExpressionNode)", VALUE, formula.value());
    }

    @Test
    public void testToString() {
        assertEquals("" + VALUE, SpreadsheetFormula.with(VALUE).toString());
    }

    @Override
    protected Class<SpreadsheetFormula> type() {
        return SpreadsheetFormula.class;
    }
}

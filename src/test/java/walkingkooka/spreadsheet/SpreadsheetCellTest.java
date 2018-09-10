package walkingkooka.spreadsheet;

import org.junit.*;
import walkingkooka.test.*;
import walkingkooka.tree.expression.*;

import static org.junit.Assert.*;


public final class SpreadsheetCellTest extends PublicClassTestCase<SpreadsheetCell> {

    private final static String FORMULA = "text123";

    @Test(expected = NullPointerException.class)
    public void testWithNullFormulaFails() {
        SpreadsheetCell.with(null);
    }

    @Test
    public void testWith() {
        final SpreadsheetFormula formula = this.formula(FORMULA);
        final SpreadsheetCell cell = SpreadsheetCell.with(formula);
        this.checkFormula(cell, formula);
    }

    @Test(expected = NullPointerException.class)
    public void testSetFormulaNullFails() {
        this.createCell().setFormula(null);
    }

    @Test
    public void testSetFormulaSame() {
        final SpreadsheetCell cell = this.createCell();
        assertSame(cell, cell.setFormula(cell.formula()));
    }

    @Test
    public void testSetFormulaDifferent() {
        final SpreadsheetCell cell = this.createCell();
        final SpreadsheetFormula differentFormula = this.formula("different");
        final SpreadsheetCell different = cell.setFormula(differentFormula);
        assertNotSame(cell, different);

        this.checkFormula(different, differentFormula);
        this.checkFormula(cell, this.formula());
    }

    @Test
    public void testToString() {
        assertEquals(this.formula(FORMULA).toString(), this.createCell().toString());
    }

    private SpreadsheetCell createCell() {
        return SpreadsheetCell.with(this.formula(FORMULA));
    }

    private SpreadsheetFormula formula() {
        return this.formula(FORMULA);
    }

    private SpreadsheetFormula formula(final String text) {
        return SpreadsheetFormula.with(ExpressionNode.text(text));
    }

    private void checkFormula(final SpreadsheetCell cell, final SpreadsheetFormula formula) {
        assertEquals("formula", formula, cell.formula());
    }

    @Override
    protected Class<SpreadsheetCell> type() {
        return SpreadsheetCell.class;
    }
}

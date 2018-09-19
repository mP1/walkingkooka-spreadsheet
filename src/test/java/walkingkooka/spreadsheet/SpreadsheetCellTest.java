package walkingkooka.spreadsheet;

import org.junit.Test;
import walkingkooka.test.PublicClassTestCase;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetReferenceKind;
import walkingkooka.tree.expression.ExpressionNode;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;


public final class SpreadsheetCellTest extends PublicClassTestCase<SpreadsheetCell> {

    private final static SpreadsheetCellReference REFERENCE = reference(12, 34);
    private final static String FORMULA = "=1+2";

    @Test(expected = NullPointerException.class)
    public void testWithNullReferenceFails() {
        SpreadsheetCell.with(null, this.formula());
    }

    @Test(expected = NullPointerException.class)
    public void testWithNullFormulaFails() {
        SpreadsheetCell.with(REFERENCE, null);
    }

    @Test
    public void testWith() {
        final SpreadsheetCell cell = this.createCell();

        this.checkReference(cell, REFERENCE);
        this.checkFormula(cell, this.formula());
    }

    // SetReference.....................................................................................................

    @Test(expected = NullPointerException.class)
    public void testSetReferenceNullFails() {
        this.createCell().setReference(null);
    }

    @Test
    public void testSetReferenceSame() {
        final SpreadsheetCell cell = this.createCell();
        assertSame(cell, cell.setReference(cell.reference()));
    }

    @Test
    public void testSetReferenceDifferent() {
        final SpreadsheetCell cell = this.createCell();
        final SpreadsheetCellReference differentReference = differentReference();
        final SpreadsheetCell different = cell.setReference(differentReference);
        assertNotSame(cell, different);

        this.checkReference(different, differentReference);
        this.checkFormula(different, this.formula());

        this.checkReference(cell, REFERENCE);
        this.checkFormula(cell, this.formula());
    }

    // SetFormula.....................................................................................................

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

        this.checkReference(different, REFERENCE);
        this.checkFormula(different, differentFormula);
    }

    // toString...............................................................................................

    @Test
    public void testToStringWithoutError() {
        assertEquals(REFERENCE + "=" + this.formula(), this.createCell().toString());
    }

    private SpreadsheetCell createCell() {
        return SpreadsheetCell.with(REFERENCE, this.formula(FORMULA));
    }

    private static SpreadsheetCellReference differentReference() {
        return reference(99, 888);
    }

    private static SpreadsheetCellReference reference(final int column, final int row) {
        return SpreadsheetCellReference.with(SpreadsheetReferenceKind.ABSOLUTE.column(column), SpreadsheetReferenceKind.ABSOLUTE.row(row));
    }

    private void checkReference(final SpreadsheetCell cell, final SpreadsheetCellReference reference) {
        assertEquals("reference", reference, cell.reference());
    }
    
    private SpreadsheetFormula formula() {
        return this.formula(FORMULA);
    }

    private SpreadsheetFormula differentFormula() {
        return this.formula("=different");
    }

    private SpreadsheetFormula formula(final String text) {
        return SpreadsheetFormula.with(text);
    }

    private void checkFormula(final SpreadsheetCell cell, final SpreadsheetFormula formula) {
        assertEquals("formula", formula, cell.formula());
    }


    @Override
    protected Class<SpreadsheetCell> type() {
        return SpreadsheetCell.class;
    }
}

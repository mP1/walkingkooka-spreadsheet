package walkingkooka.spreadsheet;

import org.junit.Test;
import walkingkooka.compare.ComparableTesting;
import walkingkooka.spreadsheet.style.SpreadsheetCellStyle;
import walkingkooka.spreadsheet.style.SpreadsheetTextStyle;
import walkingkooka.test.ClassTestCase;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetReferenceKind;
import walkingkooka.tree.json.HasJsonNodeTesting;
import walkingkooka.type.MemberVisibility;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;


public final class SpreadsheetCellTest extends ClassTestCase<SpreadsheetCell>
        implements ComparableTesting<SpreadsheetCell>,
        HasJsonNodeTesting<SpreadsheetCell> {


    private final static int COLUMN = 1;
    private final static int ROW = 20;
    private final static SpreadsheetCellReference REFERENCE = reference(COLUMN, ROW);
    private final static String FORMULA = "=1+2";

    @Test(expected = NullPointerException.class)
    public void testWithNullReferenceFails() {
        SpreadsheetCell.with(null, this.formula(), this.style(), this.format(), this.formatted());
    }

    @Test(expected = NullPointerException.class)
    public void testWithNullFormulaFails() {
        SpreadsheetCell.with(REFERENCE, null, this.style(), this.format(), this.formatted());
    }

    @Test(expected = NullPointerException.class)
    public void testWithNullStyleFails() {
        SpreadsheetCell.with(REFERENCE, this.formula(), null, this.format(), this.formatted());
    }

    @Test(expected = NullPointerException.class)
    public void testWithNullFormatFails() {
        SpreadsheetCell.with(REFERENCE, this.formula(), this.style(), null, this.formatted());
    }

    @Test(expected = NullPointerException.class)
    public void testWithNullFormattedFails() {
        SpreadsheetCell.with(REFERENCE, this.formula(), this.style(), this.format(), null);
    }

    @Test
    public void testWith() {
        final SpreadsheetCell cell = this.createCell();

        this.checkReference(cell);
        this.checkFormula(cell);
        this.checkStyle(cell);
        this.checkFormat(cell);
        this.checkFormatted(cell);
    }

    @Test
    public void testWithFormula() {
        final SpreadsheetCell cell = SpreadsheetCell.with(REFERENCE,
                this.formula(),
                this.style(),
                SpreadsheetCell.NO_FORMAT,
                SpreadsheetCell.NO_FORMATTED_CELL);
        this.checkReference(cell);
        this.checkFormula(cell);
        this.checkStyle(cell);
        this.checkNoFormat(cell);
        this.checkNoFormatted(cell);
    }

    @Test
    public void testWithFormulaAndFormat() {
        final SpreadsheetCell cell = SpreadsheetCell.with(REFERENCE,
                this.formula(),
                this.style(),
                this.format(),
                SpreadsheetCell.NO_FORMATTED_CELL);
        this.checkReference(cell);
        this.checkFormula(cell);
        this.checkStyle(cell);
        this.checkFormat(cell);
        this.checkNoFormatted(cell);
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

        this.checkReference(cell);
        this.checkFormula(cell);
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
        this.checkStyle(different, this.style());
        this.checkFormat(different);
        this.checkNoFormatted(different); // clear formatted because of formula / value change.
    }

    private void setFormulaAndCheck(final SpreadsheetFormula differentFormula) {
        final SpreadsheetCell cell = this.createCell();
        final SpreadsheetCell different = cell.setFormula(differentFormula);
        assertNotSame(cell, different);

        this.checkReference(different, REFERENCE);
        this.checkFormula(different, differentFormula);
        this.checkStyle(different, this.style());
        this.checkFormat(different);
        this.checkNoFormatted(different); // clear formatted because of formula / value change.
    }

    // SetStyle.....................................................................................................

    @Test(expected = NullPointerException.class)
    public void testSetStyleNullFails() {
        this.createCell().setStyle(null);
    }

    @Test
    public void testSetStyleSame() {
        final SpreadsheetCell cell = this.createCell();
        assertSame(cell, cell.setStyle(cell.style()));
    }

    @Test
    public void testSetStyleDifferent() {
        final SpreadsheetCell cell = this.createCell();
        final SpreadsheetCellStyle differentStyle = SpreadsheetCellStyle.EMPTY.setText(SpreadsheetTextStyle.EMPTY.setItalics(SpreadsheetTextStyle.ITALICS));
        final SpreadsheetCell different = cell.setStyle(differentStyle);
        assertNotSame(cell, different);

        this.checkReference(different, REFERENCE);
        this.checkFormula(different, this.formula());
        this.checkStyle(different, differentStyle);
        this.checkFormat(different);
        this.checkNoFormatted(different); // clear formatted because of style change
    }

    // SetFormat.....................................................................................................

    @Test(expected = NullPointerException.class)
    public void testSetFormatNullFails() {
        this.createCell().setFormat(null);
    }

    @Test
    public void testSetFormatSame() {
        final SpreadsheetCell cell = this.createCell();
        assertSame(cell, cell.setFormat(cell.format()));
    }

    @Test
    public void testSetFormatDifferent() {
        final SpreadsheetCell cell = this.createCell();
        final Optional<SpreadsheetCellFormat> differentFormat = Optional.of(SpreadsheetCellFormat.with("different-pattern", SpreadsheetCellFormat.NO_FORMATTER));
        final SpreadsheetCell different = cell.setFormat(differentFormat);
        assertNotSame(cell, different);

        this.checkReference(different, REFERENCE);
        this.checkFormula(different, this.formula());
        this.checkStyle(different, this.style());
        this.checkFormat(different, differentFormat);
        this.checkNoFormatted(different); // clear formatted because of format change
    }

    @Test
    public void testSetFormatWhenWithout() {
        final SpreadsheetCell cell = SpreadsheetCell.with(REFERENCE,
                this.formula(),
                this.style(),
                SpreadsheetCell.NO_FORMAT,
                SpreadsheetCell.NO_FORMATTED_CELL);
        final SpreadsheetCell different = cell.setFormat(this.format());
        assertNotSame(cell, different);

        this.checkReference(different);
        this.checkFormula(different);
        this.checkStyle(different);
        this.checkFormat(different);
        this.checkNoFormatted(different);
    }

    // SetFormatted.....................................................................................................

    @Test(expected = NullPointerException.class)
    public void testSetFormattedNullFails() {
        this.createCell().setFormatted(null);
    }

    @Test
    public void testSetFormattedSame() {
        final SpreadsheetCell cell = this.createCell();
        assertSame(cell, cell.setFormatted(cell.formatted()));
    }

    @Test
    public void testSetFormattedDifferent() {
        final SpreadsheetCell cell = this.createCell();
        final Optional<SpreadsheetFormattedCell> differentFormatted = Optional.of(SpreadsheetFormattedCell.with("different", SpreadsheetCellStyle.EMPTY));
        final SpreadsheetCell different = cell.setFormatted(differentFormatted);
        assertNotSame(cell, different);

        this.checkReference(different, REFERENCE);
        this.checkFormula(different, this.formula());
        this.checkStyle(different, this.style());
        this.checkFormat(different, this.format());
        this.checkFormatted(different, differentFormatted);
    }

    @Test
    public void testSetFormattedWhenWithout() {
        final SpreadsheetCell cell = SpreadsheetCell.with(REFERENCE,
                this.formula(),
                this.style(),
                SpreadsheetCell.NO_FORMAT,
                SpreadsheetCell.NO_FORMATTED_CELL);
        final SpreadsheetCell different = cell.setFormatted(this.formatted());
        assertNotSame(cell, different);

        this.checkReference(different);
        this.checkFormula(different);
        this.checkStyle(different);
        this.checkNoFormat(different);
        this.checkFormatted(different);
    }

    // equals .............................................................................................

    @Test
    public void testCompareDifferentFormulaEquals() {
        this.checkNotEquals(this.createComparable(COLUMN, ROW, FORMULA + "99"));
    }

    @Test
    public void testCompareDifferentColumn() {
        this.compareToAndCheckLess(this.createComparable(99, ROW, FORMULA));
    }

    @Test
    public void testCompareDifferentRow() {
        this.compareToAndCheckLess(this.createComparable(COLUMN, 99, FORMULA));
    }

    @Test
    public void testCompareDifferentStyle() {
        this.compareToAndCheckEqual(this.createComparable()
                .setStyle(SpreadsheetCellStyle.EMPTY
                        .setText(SpreadsheetTextStyle.EMPTY
                                .setItalics(SpreadsheetTextStyle.ITALICS))));
    }

    @Test
    public void testCompareDifferentFormat() {
        this.compareToAndCheckEqual(this.createComparable()
                .setFormat(Optional.of(SpreadsheetCellFormat.with("different-pattern", SpreadsheetCellFormat.NO_FORMATTER))));
    }

    @Test
    public void testCompareDifferentFormatted() {
        this.compareToAndCheckEqual(this.createComparable().setFormatted(Optional.of(SpreadsheetFormattedCell.with("different-formatted", this.style()))));
    }

    // HasJsonNode...............................................................................................

    @Test
    public void testJsonNode() {
        this.toJsonNodeAndCheck(SpreadsheetCell.with(this.reference(COLUMN, ROW),
                SpreadsheetFormula.with(FORMULA),
                this.style(),
                SpreadsheetCell.NO_FORMAT,
                SpreadsheetCell.NO_FORMATTED_CELL),
                "{\"reference\": \"$B$21\", \"formula\": {\"text\": \"=1+2\"}, \"style\": " + this.style().toJsonNode() + "}");
    }

    @Test
    public void testJsonNodeWithFormatted() {
        this.toJsonNodeAndCheck(this.createCell(),
                "{\"reference\": \"$B$21\", \"formula\": {\"text\": \"=1+2\"}, \"style\": " + this.style().toJsonNode() +
                        ", \"format\": " + this.format().get().toJsonNode() +
                        ", \"formatted\": " + this.formatted().get().toJsonNode() +
                        "}");
    }

    // toString...............................................................................................

    @Test
    public void testToStringWithoutErrorWithoutFormatWithoutFormatted() {
        assertEquals(REFERENCE + "=" + this.formula() + " bold", SpreadsheetCell.with(REFERENCE,
                this.formula(),
                this.style(),
                SpreadsheetCell.NO_FORMAT,
                SpreadsheetCell.NO_FORMATTED_CELL)
                .toString());
    }

    @Test
    public void testToStringWithoutErrorWithFormatWithoutFormatted() {
        assertEquals(REFERENCE + "=" + this.formula() + " bold \"pattern\"", SpreadsheetCell.with(REFERENCE,
                this.formula(),
                this.style(),
                this.format(),
                SpreadsheetCell.NO_FORMATTED_CELL)
                .toString());
    }

    @Test
    public void testToStringWithoutError() {
        assertEquals(REFERENCE + "=" + this.formula() + " bold \"pattern\" \"formatted-text\"", this.createCell().toString());
    }

    private SpreadsheetCell createCell() {
        return this.createComparable();
    }

    @Override
    public SpreadsheetCell createComparable() {
        return this.createComparable(COLUMN, ROW, FORMULA);
    }

    private SpreadsheetCell createComparable(final int column, final int row, final String formula) {
        return SpreadsheetCell.with(this.reference(column, row),
                SpreadsheetFormula.with(formula),
                this.style(),
                this.format(),
                this.formatted());
    }

    private static SpreadsheetCellReference differentReference() {
        return reference(99, 888);
    }

    private static SpreadsheetCellReference reference(final int column, final int row) {
        return SpreadsheetCellReference.with(SpreadsheetReferenceKind.ABSOLUTE.column(column), SpreadsheetReferenceKind.ABSOLUTE.row(row));
    }

    private void checkReference(final SpreadsheetCell cell) {
        this.checkReference(cell, REFERENCE);
    }

    private void checkReference(final SpreadsheetCell cell, final SpreadsheetCellReference reference) {
        assertEquals("reference", reference, cell.reference());
    }

    private SpreadsheetFormula formula() {
        return this.formula(FORMULA);
    }

    private SpreadsheetFormula formula(final String text) {
        return SpreadsheetFormula.with(text);
    }

    private void checkFormula(final SpreadsheetCell cell) {
        this.checkFormula(cell, this.formula());
    }

    private void checkFormula(final SpreadsheetCell cell, final SpreadsheetFormula formula) {
        assertEquals("formula", formula, cell.formula());
    }

    private SpreadsheetCellStyle style() {
        return SpreadsheetCellStyle.EMPTY.setText(SpreadsheetTextStyle.EMPTY.setBold(SpreadsheetTextStyle.BOLD));
    }

    private void checkStyle(final SpreadsheetCell cell) {
        this.checkStyle(cell, this.style());
    }

    private void checkStyle(final SpreadsheetCell cell, final SpreadsheetCellStyle style) {
        assertEquals("style", style, cell.style());
    }

    private Optional<SpreadsheetCellFormat> format() {
        return Optional.of(SpreadsheetCellFormat.with("pattern", SpreadsheetCellFormat.NO_FORMATTER));
    }

    private void checkNoFormat(final SpreadsheetCell cell) {
        this.checkFormat(cell, SpreadsheetCell.NO_FORMAT);
    }

    private void checkFormat(final SpreadsheetCell cell) {
        this.checkFormat(cell, this.format());
    }

    private void checkFormat(final SpreadsheetCell cell, final Optional<SpreadsheetCellFormat> format) {
        assertEquals("format", format, cell.format());
    }

    private Optional<SpreadsheetFormattedCell> formatted() {
        return Optional.of(SpreadsheetFormattedCell.with("formatted-text", SpreadsheetCellStyle.EMPTY));
    }

    private void checkNoFormatted(final SpreadsheetCell cell) {
        this.checkFormatted(cell, SpreadsheetCell.NO_FORMATTED_CELL);
    }

    private void checkFormatted(final SpreadsheetCell cell) {
        this.checkFormatted(cell, this.formatted());
    }

    private void checkFormatted(final SpreadsheetCell cell, final Optional<SpreadsheetFormattedCell> formatted) {
        assertEquals("formatted", formatted, cell.formatted());
    }

    @Override
    protected Class<SpreadsheetCell> type() {
        return SpreadsheetCell.class;
    }

    @Override
    protected MemberVisibility typeVisibility() {
        return MemberVisibility.PUBLIC;
    }

    @Override
    public boolean compareAndEqualsMatch() {
        return false; // comparing does not include all properties, so compareTo == 0 <> equals
    }
}

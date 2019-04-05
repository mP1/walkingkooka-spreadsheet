package walkingkooka.spreadsheet.store.reference;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.test.HashCodeEqualsDefinedTesting;
import walkingkooka.test.ToStringTesting;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class TargetAndSpreadsheetCellReferenceTest implements HashCodeEqualsDefinedTesting<TargetAndSpreadsheetCellReference<SpreadsheetLabelName>>,
        ToStringTesting<TargetAndSpreadsheetCellReference<SpreadsheetLabelName>> {

    @Test
    public void testWithNullTarget() {
        assertThrows(NullPointerException.class, () -> {
            TargetAndSpreadsheetCellReference.with(null, this.reference());
        });
    }

    @Test
    public void testWithNullCellReference() {
        assertThrows(NullPointerException.class, () -> {
            TargetAndSpreadsheetCellReference.with(this.label(), null);
        });
    }

    @Test
    public void testWithRefererEqualCellReference() {
        final SpreadsheetCellReference cell = this.reference();

        assertThrows(IllegalArgumentException.class, () -> {
            TargetAndSpreadsheetCellReference.with(cell, cell);
        });
    }

    @Test
    public void testWith() {
        final SpreadsheetLabelName label = this.label();
        final SpreadsheetCellReference reference = this.reference();
        final TargetAndSpreadsheetCellReference and = TargetAndSpreadsheetCellReference.with(label, reference);
        assertEquals(label, and.target(), "target");
        assertEquals(reference, and.reference(), "reference");
    }

    @Test
    public void testDifferentTarget() {
        this.checkNotEquals(TargetAndSpreadsheetCellReference.with(SpreadsheetLabelName.with("Different"), this.reference()));
    }

    @Test
    public void testDifferentCellReference() {
        this.checkNotEquals(TargetAndSpreadsheetCellReference.with(this.label(), SpreadsheetCellReference.parse("Z99")));
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createObject(), this.label() + "->" + this.reference());
    }

    @Override
    public TargetAndSpreadsheetCellReference<SpreadsheetLabelName> createObject() {
        return TargetAndSpreadsheetCellReference.with(this.label(), this.reference());
    }

    private SpreadsheetLabelName label() {
        return SpreadsheetLabelName.with("Label123");
    }

    private SpreadsheetCellReference reference() {
        return SpreadsheetCellReference.parse("A1");
    }

    @Override
    public Class<TargetAndSpreadsheetCellReference<SpreadsheetLabelName>> type() {
        return Cast.to(TargetAndSpreadsheetCellReference.class);
    }
}

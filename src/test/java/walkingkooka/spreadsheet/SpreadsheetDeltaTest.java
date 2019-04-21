package walkingkooka.spreadsheet;

import org.junit.jupiter.api.Test;
import walkingkooka.type.MemberVisibility;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetDeltaTest extends SpreadsheetDeltaTestCase<SpreadsheetDelta> {

    @Test
    public void testWithNullIdFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetDelta.with(null, this.cells());
        });
    }

    @Test
    public void testWithNullCellsFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetDelta.with(this.id(), null);
        });
    }

    @Test
    public void testWith() {
        final SpreadsheetId id = this.id();
        final Set<SpreadsheetCell> cells = this.cells();
        final SpreadsheetDelta delta = SpreadsheetDelta.with(id, cells);
        this.checkId(delta, id);
        this.checkCells(delta, cells);
    }

    // ClassTesting..........................................................................................

    @Override
    public Class<SpreadsheetDelta> type() {
        return SpreadsheetDelta.class;
    }

    @Override
    public MemberVisibility typeVisibility() {
        return MemberVisibility.PUBLIC;
    }
}

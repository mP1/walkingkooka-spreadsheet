package walkingkooka.spreadsheet;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.compare.Range;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.tree.json.HasJsonNode;
import walkingkooka.tree.json.JsonNode;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetDeltaNonWindowedTest extends SpreadsheetDeltaTestCase2<SpreadsheetDeltaNonWindowed> {

    @Test
    public final void testCellsReadOnly() {
        final SpreadsheetDelta delta = SpreadsheetDelta.with(this.id(), this.cells());
        final Set<SpreadsheetCell> cells = delta.cells();

        assertThrows(UnsupportedOperationException.class, () -> {
            cells.add(this.a1());
        });

        this.checkCells(delta, this.cells());
    }

    @Test
    public void testSetDifferentCells() {
        final SpreadsheetDeltaNonWindowed delta = this.createSpreadsheetDelta();
        final Set<SpreadsheetCell> cells = this.cells0("B2", "C3");
        final SpreadsheetDelta different = delta.setCells(cells);
        this.checkId(different);
        this.checkCells(different, cells);

        this.checkId(delta);
        this.checkCells(delta);
    }

    @Test
    public void testEqualsSpreadsheetDeltaWindowed() {
        this.checkNotEquals(SpreadsheetDeltaWindowed.with0(this.id(), this.cells(), this.window0("A1:Z99")));
    }

    @Test
    public void testToString() {
        final SpreadsheetId id = this.id();
        this.toStringAndCheck(SpreadsheetDelta.with(id, this.cells()), "cells: A1=1, B2=2, C3=3");
    }

    // HasJson....................................................................................................

    @Test
    public void testFromJson() {
        this.fromJsonNodeAndCheck(JsonNode.object()
                        .set(SpreadsheetDelta.ID_PROPERTY, this.id().toJsonNode())
                        .set(SpreadsheetDelta.CELLS_PROPERTY, HasJsonNode.toJsonNodeSet(this.cells())),
                this.createHasJsonNode());
    }

    @Test
    public void testToJsonNode() {
        this.toJsonNodeAndCheck(this.createHasJsonNode(),
                JsonNode.object()
                        .set(SpreadsheetDelta.ID_PROPERTY, this.id().toJsonNode())
                        .set(SpreadsheetDelta.CELLS_PROPERTY, HasJsonNode.toJsonNodeSet(this.cells())));
    }

    @Override
    public Class<SpreadsheetDeltaNonWindowed> type() {
        return SpreadsheetDeltaNonWindowed.class;
    }

    @Override
    SpreadsheetDeltaNonWindowed createSpreadsheetDelta(final SpreadsheetId id,
                                                       final Set<SpreadsheetCell> cells) {
        return SpreadsheetDeltaNonWindowed.with0(id, cells);
    }

    @Override
    List<Range<SpreadsheetCellReference>> window() {
        return Lists.empty();
    }
}

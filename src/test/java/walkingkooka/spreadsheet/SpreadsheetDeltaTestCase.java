package walkingkooka.spreadsheet;

import walkingkooka.collect.set.Sets;
import walkingkooka.test.ClassTesting2;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class SpreadsheetDeltaTestCase<D extends SpreadsheetDelta> implements ClassTesting2<D> {

    SpreadsheetDeltaTestCase() {
        super();
    }

    // helpers...............................................................................................

    final SpreadsheetId id() {
        return SpreadsheetId.with(1234L);
    }

    final Set<SpreadsheetCell> cells() {
        return Sets.of(this.a1(), this.b2(), this.c3());
    }

    final Set<SpreadsheetCell> cells0(final String... cellReferences) {
        return Arrays.stream(cellReferences)
                .map(r -> this.cell(r, "55"))
                .collect(Collectors.toSet());
    }

    final SpreadsheetCell a1() {
        return this.cell("A1", "1");
    }

    final SpreadsheetCell b2() {
        return this.cell("B2", "2");
    }

    final SpreadsheetCell c3() {
        return this.cell("C3", "3");
    }

    final SpreadsheetCell cell(final String cellReference, final String formulaText) {
        return SpreadsheetCell.with(SpreadsheetCellReference.parse(cellReference), SpreadsheetFormula.with(formulaText));
    }

    final void checkId(final SpreadsheetDelta delta) {
        this.checkId(delta, this.id());
    }

    final void checkId(final SpreadsheetDelta delta, final SpreadsheetId id) {
        assertEquals(id, delta.id(), "id");
    }

    final void checkCells(final SpreadsheetDelta delta) {
        this.checkCells(delta, this.cells());
    }

    final void checkCells(final SpreadsheetDelta delta, final Set<SpreadsheetCell> cells) {
        assertEquals(cells, delta.cells(), "cells");
    }
}

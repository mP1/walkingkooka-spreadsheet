/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package walkingkooka.spreadsheet.engine;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.set.Sets;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetDeltaTest implements ClassTesting2<SpreadsheetDelta> {

    @Test
    public void testEmpty() {
        final SpreadsheetDelta empty = SpreadsheetDelta.EMPTY;

        assertEquals(SpreadsheetDelta.NO_CELLS, empty.cells());
        assertEquals(SpreadsheetDelta.NO_LABELS, empty.labels());
        assertEquals(SpreadsheetDelta.NO_DELETED_CELLS, empty.deletedCells());
        assertEquals(SpreadsheetDelta.NO_COLUMN_WIDTHS, empty.columnWidths());
        assertEquals(SpreadsheetDelta.NO_ROW_HEIGHTS, empty.rowHeights());
    }

    @Test
    public void testNoWindowConstant() {
        assertEquals(Optional.empty(), SpreadsheetDelta.NO_WINDOW);
    }

    // cell.............................................................................................................

    @Test
    public void testCellWhenEmpty() {
        this.cellAndCheck(
                SpreadsheetDelta.EMPTY,
                SpreadsheetExpressionReference.parseCell("A1"),
                Optional.empty()
        );
    }

    @Test
    public void testCellNotFound() {
        this.cellAndCheck(
                SpreadsheetDelta.EMPTY.setCells(
                        Sets.of(
                                this.cell()
                        )
                ),
                SpreadsheetExpressionReference.parseCell("B2"),
                Optional.empty()
        );
    }

    @Test
    public void testCellFound() {
        final SpreadsheetCell cell = this.cell();
        this.cellAndCheck(
                SpreadsheetDelta.EMPTY.setCells(
                        Sets.of(cell)
                ),
                cell.reference(),
                Optional.of(cell)
        );
    }

    @Test
    public void testCellFoundDifferentKind() {
        final SpreadsheetCell cell = this.cell();
        final SpreadsheetCellReference reference = cell.reference();
        assertEquals(reference.toRelative(), reference, "reference should be relative");

        this.cellAndCheck(
                SpreadsheetDelta.EMPTY.setCells(
                        Sets.of(cell)
                ),
                reference.toAbsolute(),
                Optional.of(cell)
        );
    }

    private SpreadsheetCell cell() {
        return SpreadsheetCell.with(
                SpreadsheetExpressionReference.parseCell("A1"),
                SpreadsheetFormula.with("=1+2")
        );
    }

    private void cellAndCheck(final SpreadsheetDelta delta,
                              final SpreadsheetCellReference reference,
                              final Optional<SpreadsheetCell> cell) {
        assertEquals(
                cell,
                delta.cell(reference),
                () -> delta + " cell " + reference
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetDelta> type() {
        return SpreadsheetDelta.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}

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
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;

import java.util.ArrayList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetDeltaTest extends SpreadsheetDeltaTestCase<SpreadsheetDelta> {

    @Test
    public void testNoWindowConstant() {
        assertEquals(Lists.empty(), SpreadsheetDelta.NO_WINDOW);
    }

    @Test
    public void testWith() {
        final SpreadsheetDelta delta = SpreadsheetDelta.with(this.cells());
        this.checkCells(delta);
        this.checkColumnWidths(delta, SpreadsheetDelta.NO_COLUMN_WIDTHS);
        this.checkRowHeights(delta, SpreadsheetDelta.NO_ROW_HEIGHTS);
        this.checkWindow(delta, Lists.empty());
    }

    @Test
    public void testCopiedCellsSorted() {
        final SpreadsheetCell b2 = SpreadsheetCell.with(SpreadsheetCellReference.parseCell("b2"), SpreadsheetFormula.with("2"));
        final SpreadsheetCell c3 = SpreadsheetCell.with(SpreadsheetCellReference.parseCell("c3"), SpreadsheetFormula.with("3"));
        final SpreadsheetCell a1 = SpreadsheetCell.with(SpreadsheetCellReference.parseCell("a1"), SpreadsheetFormula.with("1"));

        final Set<SpreadsheetCell> cells = Sets.ordered();
        cells.add(b2);
        cells.add(c3);
        cells.add(a1);

        final SpreadsheetDelta delta = SpreadsheetDelta.with(SpreadsheetDelta.NO_CELLS)
                .setWindow(Lists.of(SpreadsheetExpressionReference.parseCellRange("A1:Z99")))
                .setCells(cells);
        assertEquals(Lists.of(a1, b2, c3), new ArrayList<>(delta.cells()), "cells should be sorted");
    }

    @Test
    public void testCopiedCellsSorted2() {
        final SpreadsheetCell c3 = SpreadsheetCell.with(SpreadsheetCellReference.parseCell("c3"), SpreadsheetFormula.with("3"));
        final SpreadsheetCell b2 = SpreadsheetCell.with(SpreadsheetCellReference.parseCell("b$2"), SpreadsheetFormula.with("2"));
        final SpreadsheetCell a1 = SpreadsheetCell.with(SpreadsheetCellReference.parseCell("$a$1"), SpreadsheetFormula.with("1"));

        final Set<SpreadsheetCell> cells = Sets.ordered();
        cells.add(c3);
        cells.add(b2);
        cells.add(a1);

        final SpreadsheetDelta delta = SpreadsheetDelta.with(SpreadsheetDelta.NO_CELLS)
                .setWindow(Lists.of(SpreadsheetExpressionReference.parseCellRange("A1:Z99")))
                .setCells(cells);
        assertEquals(Lists.of(a1, b2, c3), new ArrayList<>(delta.cells()), "cells should be sorted");
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

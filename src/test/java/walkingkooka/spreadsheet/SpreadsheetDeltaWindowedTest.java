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

package walkingkooka.spreadsheet;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.set.Sets;
import walkingkooka.compare.Range;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.tree.json.JsonNode;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetDeltaWindowedTest extends SpreadsheetDeltaTestCase2<SpreadsheetDeltaWindowed> {

    @Test
    public final void testCellsReadOnly() {
        final SpreadsheetDelta delta = SpreadsheetDelta.with(this.id(), this.cells())
                .setCells(this.cells());
        final Set<SpreadsheetCell> cells = delta.cells();

        assertThrows(UnsupportedOperationException.class, () -> {
            cells.add(this.a1());
        });

        this.checkCells(delta, this.cells());
    }

    @Test
    public void testSetCellsFiltered() {
        final SpreadsheetDelta delta = this.createSpreadsheetDelta();

        final Set<SpreadsheetCell> cells = Sets.of(this.a1(), this.b2(), this.cell("E99", "should be removed!"));
        final SpreadsheetDelta different = delta.setCells(cells);

        this.checkId(different);
        this.checkCells(different, Sets.of(this.a1(), this.b2()));
        this.checkWindow(different);

        this.checkId(delta);
        this.checkCells(delta, this.cells());
        this.checkWindow(delta);
    }

    @Test
    public void testEqualsSpreadsheetDeltaNonWindowed() {
        this.checkNotEquals(SpreadsheetDeltaNonWindowed.with0(this.id(), this.cells()));
    }

    @Test
    public void testEqualsDifferentWindow() {
        this.checkNotEquals(SpreadsheetDeltaWindowed.with0(this.id(), this.cells(), this.window0("A1:E9999")));
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createSpreadsheetDelta(), "cells: A1=1, B2=2, C3=3 window: (A1..E5),(F6..Z99)");
    }

    @Test
    public void testToJsonNode() {
        final SpreadsheetDelta delta = this.createSpreadsheetDelta();
        final JsonNode node = delta.toJsonNode();
        assertEquals(JsonNode.string("A1:E5,F6:Z99"),
                node.objectOrFail().getOrFail(SpreadsheetDelta.WINDOW_PROPERTY).removeParent(),
                () -> " window property incorrect " + node);
    }

    @Override
    public Class<SpreadsheetDeltaWindowed> type() {
        return SpreadsheetDeltaWindowed.class;
    }

    @Override
    SpreadsheetDeltaWindowed createSpreadsheetDelta(final SpreadsheetId id, final Set<SpreadsheetCell> cells) {
        return SpreadsheetDeltaWindowed.with0(id, cells, this.window());
    }

    @Override
    List<Range<SpreadsheetCellReference>> window() {
        return this.window0("A1:E5", "F6:Z99");
    }
}

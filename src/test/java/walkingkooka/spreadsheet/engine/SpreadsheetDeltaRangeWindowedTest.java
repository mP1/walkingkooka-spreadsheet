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
import walkingkooka.Cast;
import walkingkooka.collect.set.Sets;
import walkingkooka.compare.Range;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.reference.SpreadsheetRange;
import walkingkooka.tree.json.HasJsonNode;
import walkingkooka.tree.json.JsonNode;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertSame;

public final class SpreadsheetDeltaRangeWindowedTest extends SpreadsheetDeltaWindowedTestCase<SpreadsheetDeltaRangeWindowed<SpreadsheetId>, Range<SpreadsheetId>> {

    @Test
    public void testSetRangeSame() {
        final SpreadsheetDeltaRangeWindowed<SpreadsheetId> delta = this.createSpreadsheetDelta();
        assertSame(delta, delta.setRange(this.id()));
    }

    @Test
    public void testSetCellsFiltered() {
        final SpreadsheetDeltaRangeWindowed<SpreadsheetId> delta = this.createSpreadsheetDelta();

        final Set<SpreadsheetCell> cells = Sets.of(this.a1(), this.b2(), this.cell("E99", "should be removed!"));
        final SpreadsheetDelta<Range<SpreadsheetId>> different = delta.setCells(cells);

        this.checkId(different);
        this.checkCells(different, Sets.of(this.a1(), this.b2()));
        this.checkWindow(different);

        this.checkId(delta);
        this.checkCells(delta, this.cells());
        this.checkWindow(delta);
    }

    // HasJsonNode......................................................................................................

    @Test
    public void testFromJsonNodeRangeWindow() {
        this.fromJsonNodeAndCheck(JsonNode.object()
                        .set(SpreadsheetDelta.RANGE_PROPERTY, this.id().toJsonNode())
                        .set(SpreadsheetDelta.WINDOW_PROPERTY, JsonNode.string("A1:E5,F6:Z99")),
                this.createSpreadsheetDelta(this.id(), Sets.empty(), this.window()));
    }

    @Test
    public void testFromJsonNodeRangeCellsWindow() {
        this.fromJsonNodeAndCheck(JsonNode.object()
                        .set(SpreadsheetDelta.RANGE_PROPERTY, this.id().toJsonNode())
                        .set(SpreadsheetDelta.CELLS_PROPERTY, HasJsonNode.toJsonNodeSet(this.cells()))
                        .set(SpreadsheetDelta.WINDOW_PROPERTY, JsonNode.string("A1:E5,F6:Z99")),
                this.createSpreadsheetDelta(this.id(), this.cells(), this.window()));
    }

    @Test
    public void testToJsonNodeRangeWindow() {
        final SpreadsheetDelta delta = this.createSpreadsheetDelta(this.id(), Sets.empty(), this.window());

        this.toJsonNodeAndCheck(delta,
                JsonNode.object()
                        .set(SpreadsheetDelta.RANGE_PROPERTY, this.id().toJsonNode())
                        .set(SpreadsheetDelta.WINDOW_PROPERTY, JsonNode.string("A1:E5,F6:Z99")));
    }

    @Test
    public void testToJsonNodeRangeCellsWindow() {
        final SpreadsheetDelta delta = this.createSpreadsheetDelta(this.id(), this.cells(), this.window());

        this.toJsonNodeAndCheck(delta,
                JsonNode.object()
                        .set(SpreadsheetDelta.RANGE_PROPERTY, this.id().toJsonNode())
                        .set(SpreadsheetDelta.CELLS_PROPERTY, HasJsonNode.toJsonNodeSet(this.cells()))
                        .set(SpreadsheetDelta.WINDOW_PROPERTY, JsonNode.string("A1:E5,F6:Z99")));
    }

    // HasHateosLink....................................................................................................

    @Test
    public final void testHateosLinkId() {
        this.hateosLinkIdAndCheck("1e-2f");
    }

    // ToString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createSpreadsheetDelta(), "1e:2f cells: A1=1, B2=2, C3=3 window: A1:E5, F6:Z99");
    }

    @Override
    SpreadsheetDeltaRangeWindowed<SpreadsheetId> createSpreadsheetDelta(final Range<SpreadsheetId> id,
                                                                        final Set<SpreadsheetCell> cells,
                                                                        final List<SpreadsheetRange> window) {
        return SpreadsheetDeltaRangeWindowed.with(id, cells, window);
    }

    @Override
    Range<SpreadsheetId> id() {
        return this.range(0x1e, 0x2f);
    }

    @Override
    Range<SpreadsheetId> differentId() {
        return this.range(3, 4);
    }

    @Override
    public Class<SpreadsheetDeltaRangeWindowed<SpreadsheetId>> type() {
        return Cast.to(SpreadsheetDeltaRangeWindowed.class);
    }
}

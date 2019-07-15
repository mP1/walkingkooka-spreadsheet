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
import walkingkooka.Cast;
import walkingkooka.collect.set.Sets;
import walkingkooka.tree.json.HasJsonNode;
import walkingkooka.tree.json.JsonNode;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertSame;

public final class SpreadsheetDeltaIdWindowedTest extends SpreadsheetDeltaWindowedTestCase<SpreadsheetDeltaIdWindowed<SpreadsheetId>, Optional<SpreadsheetId>> {

    @Test
    public void testSetIdSame() {
        final SpreadsheetDeltaIdWindowed<SpreadsheetId> delta = this.createSpreadsheetDelta();
        assertSame(delta, delta.setId(this.id()));
    }

    @Test
    public void testSetCellsFiltered() {
        final SpreadsheetDeltaIdWindowed<SpreadsheetId> delta = this.createSpreadsheetDelta();

        final Set<SpreadsheetCell> cells = Sets.of(this.a1(), this.b2(), this.cell("E99", "should be removed!"));
        final SpreadsheetDelta<Optional<SpreadsheetId>> different = delta.setCells(cells);

        this.checkId(different);
        this.checkCells(different, Sets.of(this.a1(), this.b2()));
        this.checkWindow(different);

        this.checkId(delta);
        this.checkCells(delta, this.cells());
        this.checkWindow(delta);
    }

    // HasJsonNode......................................................................................................

    @Test
    public void testFromJsonIdCellsWindow() {
        this.fromJsonNodeAndCheck(JsonNode.object()
                        .set(SpreadsheetDelta.ID_PROPERTY, this.id().get().toJsonNodeWithType())
                        .set(SpreadsheetDelta.CELLS_PROPERTY, HasJsonNode.toJsonNodeSet(this.cells()))
                        .set(SpreadsheetDelta.WINDOW_PROPERTY, JsonNode.string("A1:E5,F6:Z99")),
                SpreadsheetDeltaIdWindowed.with(this.id(), this.cells(), this.window()));
    }

    @Test
    public void testFromJsonIdWindow() {
        this.fromJsonNodeAndCheck(JsonNode.object()
                        .set(SpreadsheetDelta.ID_PROPERTY, this.id().get().toJsonNodeWithType())
                        .set(SpreadsheetDelta.WINDOW_PROPERTY, JsonNode.string("A1:E5,F6:Z99")),
                SpreadsheetDeltaIdWindowed.with(this.id(), SpreadsheetDelta.NO_CELLS, this.window()));
    }

    @Test
    public void testFromJsonCellsWindow() {
        this.fromJsonNodeAndCheck(JsonNode.object()
                        .set(SpreadsheetDelta.CELLS_PROPERTY, HasJsonNode.toJsonNodeSet(this.cells()))
                        .set(SpreadsheetDelta.WINDOW_PROPERTY, JsonNode.string("A1:E5,F6:Z99")),
                SpreadsheetDeltaIdWindowed.with(EMPTY_ID, this.cells(), this.window()));
    }

    @Test
    public void testFromJsonWindow() {
        this.fromJsonNodeAndCheck(JsonNode.object()
                        .set(SpreadsheetDelta.WINDOW_PROPERTY, JsonNode.string("A1:E5,F6:Z99")),
                SpreadsheetDeltaIdWindowed.with(EMPTY_ID, SpreadsheetDelta.NO_CELLS, this.window()));
    }

    @Test
    public void testToJsonNodeIdCellsWindows() {
        this.toJsonNodeAndCheck(this.createHasJsonNode(),
                JsonNode.object()
                        .set(SpreadsheetDelta.ID_PROPERTY, this.id().get().toJsonNodeWithType())
                        .set(SpreadsheetDelta.CELLS_PROPERTY, HasJsonNode.toJsonNodeSet(this.cells()))
                        .set(SpreadsheetDelta.WINDOW_PROPERTY, JsonNode.string("A1:E5,F6:Z99")));
    }

    @Test
    public void testToJsonNodeIdWindow() {
        this.toJsonNodeAndCheck(SpreadsheetDeltaIdWindowed.with(this.id(), SpreadsheetDelta.NO_CELLS, this.window()),
                JsonNode.object()
                        .set(SpreadsheetDelta.ID_PROPERTY, this.id().get().toJsonNodeWithType())
                        .set(SpreadsheetDelta.WINDOW_PROPERTY, JsonNode.string("A1:E5,F6:Z99")));
    }

    @Test
    public void testToJsonNodeCellsWindows() {
        this.toJsonNodeAndCheck(SpreadsheetDeltaIdWindowed.with(EMPTY_ID, this.cells(), this.window()),
                JsonNode.object()
                        .set(SpreadsheetDelta.CELLS_PROPERTY, HasJsonNode.toJsonNodeSet(this.cells()))
                        .set(SpreadsheetDelta.WINDOW_PROPERTY, JsonNode.string("A1:E5,F6:Z99")));
    }

    @Test
    public void testToJsonNodeWindow() {
        this.toJsonNodeAndCheck(SpreadsheetDeltaIdWindowed.with(EMPTY_ID, SpreadsheetDelta.NO_CELLS, this.window()),
                JsonNode.object()
                        .set(SpreadsheetDelta.WINDOW_PROPERTY, JsonNode.string("A1:E5,F6:Z99")));
    }

    // HasHateosLink....................................................................................................

    @Test
    public final void testHateosLinkId() {
        this.hateosLinkIdAndCheck("12ef");
    }

    // ToString.........................................................................................................

    @Test
    public void testToStringWithId() {
        this.toStringAndCheck(this.createSpreadsheetDelta(), "12ef cells: A1=1, B2=2, C3=3 window: A1:E5, F6:Z99");
    }

    @Test
    public void testToStringWithoutId() {
        this.toStringAndCheck(SpreadsheetDeltaIdWindowed.with(EMPTY_ID, this.cells(), this.window()),
                "cells: A1=1, B2=2, C3=3 window: A1:E5, F6:Z99");
    }


    @Override
    SpreadsheetDeltaIdWindowed<SpreadsheetId> createSpreadsheetDelta(final Optional<SpreadsheetId> id,
                                                                     final Set<SpreadsheetCell> cells,
                                                                     final List<SpreadsheetRange> window) {
        return SpreadsheetDeltaIdWindowed.with(id, cells, window);
    }

    @Override
    Optional<SpreadsheetId> id() {
        return Optional.of(SpreadsheetId.with(0x12EF));
    }

    @Override
    Optional<SpreadsheetId> differentId() {
        return Optional.of(SpreadsheetId.with(0x45EF));
    }

    @Override
    public Class<SpreadsheetDeltaIdWindowed<SpreadsheetId>> type() {
        return Cast.to(SpreadsheetDeltaIdWindowed.class);
    }
}

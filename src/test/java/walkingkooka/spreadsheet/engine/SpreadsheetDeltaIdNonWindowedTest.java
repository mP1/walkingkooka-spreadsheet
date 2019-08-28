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
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.ToJsonNodeContext;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertSame;

public final class SpreadsheetDeltaIdNonWindowedTest extends SpreadsheetDeltaNonWindowedTestCase<SpreadsheetDeltaIdNonWindowed<SpreadsheetId>, Optional<SpreadsheetId>> {

    @Test
    public void testSetIdSame() {
        final SpreadsheetDeltaIdNonWindowed<SpreadsheetId> delta = this.createSpreadsheetDelta();
        assertSame(delta, delta.setId(this.id()));
    }

    @Test
    public void testEqualsSpreadsheetDeltaWindowed() {
        this.checkNotEquals(SpreadsheetDeltaIdWindowed.with(this.id(), this.cells(), this.window0("A1:Z99")));
    }

    // JsonNodeMappingTesting...........................................................................................

    @Test
    public void testFromJsonNode() {
        this.fromJsonNodeAndCheck(JsonNode.object(),
                this.createSpreadsheetDelta(Optional.empty(), SpreadsheetDelta.NO_CELLS));
    }

    @Test
    public void testFromJsonNodeId() {
        final Set<SpreadsheetCell> cells = SpreadsheetDelta.NO_CELLS;

        this.fromJsonNodeAndCheck(JsonNode.object()
                        .set(SpreadsheetDelta.ID_PROPERTY, this.toJsonNodeContext().toJsonNodeWithType(this.id().get())),
                this.createSpreadsheetDelta(this.id(), cells));
    }

    @Test
    public void testFromJsonIdCells() {
        final ToJsonNodeContext context = this.toJsonNodeContext();

        this.fromJsonNodeAndCheck(JsonNode.object()
                        .set(SpreadsheetDelta.ID_PROPERTY, context.toJsonNodeWithType(this.id().get()))
                        .set(SpreadsheetDelta.CELLS_PROPERTY, context.toJsonNodeSet(this.cells())),
                this.createJsonNodeMappingValue());
    }

    @Test
    public void testFromJsonCells() {
        this.fromJsonNodeAndCheck(JsonNode.object()
                        .set(SpreadsheetDelta.CELLS_PROPERTY, this.toJsonNodeContext().toJsonNodeSet(this.cells())),
                SpreadsheetDeltaIdNonWindowed.with(EMPTY_ID, this.cells()));
    }

    @Test
    public void testToJsonNode() {
        this.toJsonNodeAndCheck(SpreadsheetDeltaIdNonWindowed.with(Optional.<SpreadsheetId>empty(), SpreadsheetDelta.NO_CELLS),
                JsonNode.object());
    }

    @Test
    public void testToJsonNodeId() {
        final Set<SpreadsheetCell> cells = SpreadsheetDelta.NO_CELLS;

        this.toJsonNodeAndCheck(SpreadsheetDeltaIdNonWindowed.with(this.id(), cells),
                JsonNode.object()
                        .set(SpreadsheetDelta.ID_PROPERTY, this.toJsonNodeContext().toJsonNodeWithType(this.id().get())));
    }

    @Test
    public void testToJsonNodeIdCells() {
        final ToJsonNodeContext context = this.toJsonNodeContext();

        this.toJsonNodeAndCheck(this.createJsonNodeMappingValue(),
                JsonNode.object()
                        .set(SpreadsheetDelta.ID_PROPERTY, context.toJsonNodeWithType(this.id().get()))
                        .set(SpreadsheetDelta.CELLS_PROPERTY, context.toJsonNodeSet(this.cells())));
    }

    @Test
    public void testToJsonNodeCells() {
        this.toJsonNodeAndCheck(SpreadsheetDeltaIdNonWindowed.with(EMPTY_ID, this.cells()),
                JsonNode.object()
                        .set(SpreadsheetDelta.CELLS_PROPERTY, this.toJsonNodeContext().toJsonNodeSet(this.cells())));
    }

    // HasHateosLink....................................................................................................

    @Test
    public void testHateosLinkId() {
        this.hateosLinkIdAndCheck("12ef");
    }

    // toString..........................................................................................................

    @Test
    public void testToStringWithoutId() {
        this.toStringAndCheck(SpreadsheetDeltaIdNonWindowed.with(EMPTY_ID, this.cells()),
                "cells: A1=1, B2=2, C3=3");
    }

    @Test
    public void testToStringWithId() {
        this.toStringAndCheck(SpreadsheetDeltaIdNonWindowed.with(this.id(), this.cells()),
                "12ef cells: A1=1, B2=2, C3=3");
    }

    @Override
    SpreadsheetDeltaIdNonWindowed<SpreadsheetId> createSpreadsheetDelta(final Optional<SpreadsheetId> id,
                                                                        final Set<SpreadsheetCell> cells) {
        return SpreadsheetDeltaIdNonWindowed.with(id, cells);
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
    public Class<SpreadsheetDeltaIdNonWindowed<SpreadsheetId>> type() {
        return Cast.to(SpreadsheetDeltaIdNonWindowed.class);
    }
}

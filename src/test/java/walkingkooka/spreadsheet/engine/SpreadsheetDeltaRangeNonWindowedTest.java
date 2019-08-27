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
import walkingkooka.compare.Range;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.map.ToJsonNodeContext;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertSame;

public final class SpreadsheetDeltaRangeNonWindowedTest extends SpreadsheetDeltaNonWindowedTestCase<SpreadsheetDeltaRangeNonWindowed<SpreadsheetId>, Range<SpreadsheetId>> {

    @Test
    public void testSetRangeSame() {
        final SpreadsheetDeltaRangeNonWindowed<SpreadsheetId> delta = this.createSpreadsheetDelta();
        assertSame(delta, delta.setRange(this.id()));
    }

    @Test
    public void testEqualsSpreadsheetDeltaWindowed() {
        this.checkNotEquals(SpreadsheetDeltaRangeWindowed.with(this.id(), this.cells(), this.window0("A1:Z99")));
    }

    // JsonNodeMappingTesting...........................................................................................

    @Test
    public void testFromJsonRange() {
        this.fromJsonNodeAndCheck(JsonNode.object()
                        .set(SpreadsheetDelta.RANGE_PROPERTY, this.toJsonNodeContext().toJsonNode(this.id())),
                this.createSpreadsheetDelta(this.id(), SpreadsheetDelta.NO_CELLS));
    }

    @Test
    public void testFromJsonRangeCells() {
        final ToJsonNodeContext context = this.toJsonNodeContext();

        this.fromJsonNodeAndCheck(JsonNode.object()
                        .set(SpreadsheetDelta.RANGE_PROPERTY, context.toJsonNode(this.id()))
                        .set(SpreadsheetDelta.CELLS_PROPERTY, context.toJsonNodeSet(this.cells())),
                this.createSpreadsheetDelta(this.id(), this.cells()));
    }

    @Test
    public void testToJsonNodeRange() {
        this.toJsonNodeAndCheck(this.createSpreadsheetDelta(this.id(), SpreadsheetDelta.NO_CELLS),
                JsonNode.object()
                        .set(SpreadsheetDelta.RANGE_PROPERTY, this.toJsonNodeContext().toJsonNode(this.id())));
    }

    @Test
    public void testToJsonNodeRangeCells() {
        final ToJsonNodeContext context = this.toJsonNodeContext();

        this.toJsonNodeAndCheck(this.createSpreadsheetDelta(this.id(), this.cells()),
                JsonNode.object()
                        .set(SpreadsheetDelta.RANGE_PROPERTY, context.toJsonNode(this.id()))
                        .set(SpreadsheetDelta.CELLS_PROPERTY, context.toJsonNodeSet(this.cells())));
    }

    // HasHateosLink....................................................................................................

    @Test
    public final void testHateosLinkId() {
        this.hateosLinkIdAndCheck("1a-1f");
    }

    // toString..........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createSpreadsheetDelta(),
                "1a:1f cells: A1=1, B2=2, C3=3");
    }

    @Override
    SpreadsheetDeltaRangeNonWindowed<SpreadsheetId> createSpreadsheetDelta(final Range<SpreadsheetId> id,
                                                                           final Set<SpreadsheetCell> cells) {
        return SpreadsheetDeltaRangeNonWindowed.with(id, cells);
    }

    @Override
    Range<SpreadsheetId> id() {
        return range(0x1A, 0x1F);
    }

    @Override
    Range<SpreadsheetId> differentId() {
        return range(0x22, 0x3F);
    }

    @Override
    public Class<SpreadsheetDeltaRangeNonWindowed<SpreadsheetId>> type() {
        return Cast.to(SpreadsheetDeltaRangeNonWindowed.class);
    }
}

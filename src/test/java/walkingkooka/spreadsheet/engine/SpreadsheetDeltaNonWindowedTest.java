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
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.ToJsonNodeContext;

import java.util.Set;

public final class SpreadsheetDeltaNonWindowedTest extends SpreadsheetDeltaNonWindowedTestCase<SpreadsheetDeltaNonWindowed> {

    @Test
    public void testEqualsSpreadsheetDeltaWindowed() {
        this.checkNotEquals(SpreadsheetDeltaWindowed.withWindowed(this.cells(), this.window0("A1:Z99")));
    }

    // JsonNodeMappingTesting...........................................................................................

    @Test
    public void testFromJsonNode() {
        this.fromJsonNodeAndCheck(JsonNode.object(), this.createSpreadsheetDelta(SpreadsheetDelta.NO_CELLS));
    }

    @Test
    public void testFromJsonCells() {
        this.fromJsonNodeAndCheck(JsonNode.object()
                        .set(SpreadsheetDelta.CELLS_PROPERTY, this.toJsonNodeContext().toJsonNodeSet(this.cells())),
                SpreadsheetDeltaNonWindowed.withNonWindowed(this.cells()));
    }

    @Test
    public void testToJsonNode() {
        this.toJsonNodeAndCheck(SpreadsheetDeltaNonWindowed.withNonWindowed(SpreadsheetDelta.NO_CELLS),
                JsonNode.object());
    }

    @Test
    public void testToJsonNodeIdCells() {
        final ToJsonNodeContext context = this.toJsonNodeContext();

        this.toJsonNodeAndCheck(this.createJsonNodeMappingValue(),
                JsonNode.object()
                        .set(SpreadsheetDelta.CELLS_PROPERTY, context.toJsonNodeSet(this.cells())));
    }

    @Test
    public void testToJsonNodeCells() {
        this.toJsonNodeAndCheck(SpreadsheetDeltaNonWindowed.withNonWindowed(this.cells()),
                JsonNode.object()
                        .set(SpreadsheetDelta.CELLS_PROPERTY, this.toJsonNodeContext().toJsonNodeSet(this.cells())));
    }

    // toString..........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(SpreadsheetDeltaNonWindowed.withNonWindowed(this.cells()), "cells: A1=1, B2=2, C3=3");
    }

    @Override
    SpreadsheetDeltaNonWindowed createSpreadsheetDelta(final Set<SpreadsheetCell> cells) {
        return SpreadsheetDeltaNonWindowed.withNonWindowed(cells);
    }

    @Override
    public Class<SpreadsheetDeltaNonWindowed> type() {
        return SpreadsheetDeltaNonWindowed.class;
    }
}

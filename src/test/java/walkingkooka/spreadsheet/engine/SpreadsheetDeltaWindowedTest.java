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
import walkingkooka.spreadsheet.reference.SpreadsheetRange;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonString;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

public final class SpreadsheetDeltaWindowedTest extends SpreadsheetDeltaTestCase2<SpreadsheetDeltaWindowed> {

    public SpreadsheetDeltaWindowedTest() {
        super();
        assertNotEquals(this.window(), this.differentWindow(), "window v differentWindow must NOT be equal");
    }

    // JsonNodeMarshallingTesting...........................................................................................

    private final static JsonString WINDOW_JSON_STRING = JsonNode.string("A1:E5,F6:Z99");

    @Test
    public void testUnmarshallCells() {
        this.unmarshallAndCheck(JsonNode.object()
                        .set(SpreadsheetDelta.CELLS_PROPERTY, this.marshallContext().marshallSet(this.cells()))
                        .set(SpreadsheetDeltaWindowed.WINDOW_PROPERTY, WINDOW_JSON_STRING),
                SpreadsheetDeltaWindowed.withWindowed(this.cells(), this.window()));
    }

    @Test
    public void testJsonNodeMarshall() {
        this.marshallAndCheck(SpreadsheetDeltaWindowed.withWindowed(SpreadsheetDelta.NO_CELLS, this.window()),
                JsonNode.object()
                        .set(SpreadsheetDeltaWindowed.WINDOW_PROPERTY, WINDOW_JSON_STRING));
    }

    @Test
    public void testJsonNodeMarshallIdCells() {
        this.marshallAndCheck(this.createJsonNodeMappingValue(),
                JsonNode.object()
                        .set(SpreadsheetDeltaWindowed.CELLS_PROPERTY, this.marshallContext().marshallSet(this.cells()))
                        .set(SpreadsheetDeltaWindowed.WINDOW_PROPERTY, WINDOW_JSON_STRING));
    }

    @Test
    public void testJsonNodeMarshallCells() {
        this.marshallAndCheck(SpreadsheetDeltaWindowed.withWindowed(this.cells(), this.window()),
                JsonNode.object()
                        .set(SpreadsheetDelta.CELLS_PROPERTY, this.marshallContext().marshallSet(this.cells()))
                        .set(SpreadsheetDeltaWindowed.WINDOW_PROPERTY, WINDOW_JSON_STRING));
    }

    // equals...........................................................................................................

    @Test
    public final void testEqualsDifferentWindow() {
        this.checkNotEquals(this.createSpreadsheetDelta(this.cells(), this.differentWindow()));
    }

    // toString..........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(SpreadsheetDeltaWindowed.withWindowed(this.cells(), this.window()), "cells: A1=1, B2=2, C3=3 window: A1:E5, F6:Z99");
    }

    @Override
    final List<SpreadsheetRange> window() {
        return this.window0("A1:E5", "F6:Z99");
    }

    @Override
    final SpreadsheetDeltaWindowed createSpreadsheetDelta(final Set<SpreadsheetCell> cells) {
        return this.createSpreadsheetDelta(cells, this.window());
    }

    private SpreadsheetDeltaWindowed createSpreadsheetDelta(final Set<SpreadsheetCell> cells,
                                                            final List<SpreadsheetRange> window) {
        return SpreadsheetDeltaWindowed.withWindowed(cells, window);
    }

    @Override
    public Class<SpreadsheetDeltaWindowed> type() {
        return SpreadsheetDeltaWindowed.class;
    }
}

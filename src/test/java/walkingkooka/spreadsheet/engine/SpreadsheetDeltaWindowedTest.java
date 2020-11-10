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
import walkingkooka.spreadsheet.reference.SpreadsheetRectangle;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonString;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

public final class SpreadsheetDeltaWindowedTest extends SpreadsheetDeltaTestCase2<SpreadsheetDeltaWindowed> {

    public SpreadsheetDeltaWindowedTest() {
        super();
        assertNotEquals(this.window(), this.differentWindow(), "window v differentWindow must NOT be equal");
    }

    // JsonNodeMarshallingTesting...........................................................................................

    private final static JsonString WINDOW_PIXEL_RECTANGLE_JSON_STRING = JsonNode.string("300x50");
    private final static JsonString WINDOW_RANGE_JSON_STRING = JsonNode.string("A1:E5,F6:Z99");

    @Test
    public void testUnmarshallCells() {
        this.unmarshallAndCheck(JsonNode.object()
                        .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
                        .set(SpreadsheetDeltaWindowed.WINDOW_PROPERTY, WINDOW_RANGE_JSON_STRING),
                SpreadsheetDeltaWindowed.withWindowed(this.cells(), SpreadsheetDelta.NO_MAX_COLUMN_WIDTHS, SpreadsheetDelta.NO_MAX_ROW_HEIGHTS, this.window()));
    }

    @Test
    public void testJsonNodeMarshall() {
        this.marshallAndCheck(SpreadsheetDeltaWindowed.withWindowed(SpreadsheetDelta.NO_CELLS, SpreadsheetDelta.NO_MAX_COLUMN_WIDTHS, SpreadsheetDelta.NO_MAX_ROW_HEIGHTS, this.window()),
                JsonNode.object()
                        .set(SpreadsheetDeltaWindowed.WINDOW_PROPERTY, WINDOW_RANGE_JSON_STRING));
    }

    @Test
    public void testJsonNodeMarshallCells() {
        this.marshallAndCheck(SpreadsheetDeltaWindowed.withWindowed(this.cells(), SpreadsheetDelta.NO_MAX_COLUMN_WIDTHS, SpreadsheetDelta.NO_MAX_ROW_HEIGHTS, this.window()),
                JsonNode.object()
                        .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
                        .set(SpreadsheetDeltaWindowed.WINDOW_PROPERTY, WINDOW_RANGE_JSON_STRING));
    }

    @Test
    public void testJsonNodeMarshallCellsMaxColumnWidthsPixelRectangleWindow() {
        this.marshallAndCheck(SpreadsheetDeltaWindowed.withWindowed(this.cells(), this.maxColumnWidths(), SpreadsheetDelta.NO_MAX_ROW_HEIGHTS, this.windowPixelRectangle()),
                JsonNode.object()
                        .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
                        .set(SpreadsheetDelta.MAX_COLUMN_WIDTHS_PROPERTY, MAX_COLUMN_WIDTHS_JSON)
                        .set(SpreadsheetDeltaWindowed.WINDOW_PROPERTY, WINDOW_PIXEL_RECTANGLE_JSON_STRING));
    }

    @Test
    public void testJsonNodeMarshallCellsMaxColumnWidthsRangeWindow() {
        this.marshallAndCheck(SpreadsheetDeltaWindowed.withWindowed(this.cells(), this.maxColumnWidths(), SpreadsheetDelta.NO_MAX_ROW_HEIGHTS, this.window()),
                JsonNode.object()
                        .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
                        .set(SpreadsheetDelta.MAX_COLUMN_WIDTHS_PROPERTY, MAX_COLUMN_WIDTHS_JSON)
                        .set(SpreadsheetDeltaWindowed.WINDOW_PROPERTY, WINDOW_RANGE_JSON_STRING));
    }

    @Test
    public void testJsonNodeMarshallCellsMaxRowHeightsPixelRectangleWindow() {
        this.marshallAndCheck(SpreadsheetDeltaWindowed.withWindowed(this.cells(), SpreadsheetDelta.NO_MAX_COLUMN_WIDTHS, this.maxRowHeights(), this.windowPixelRectangle()),
                JsonNode.object()
                        .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
                        .set(SpreadsheetDelta.MAX_ROW_HEIGHTS_PROPERTY, MAX_ROW_HEIGHTS_JSON)
                        .set(SpreadsheetDeltaWindowed.WINDOW_PROPERTY, WINDOW_PIXEL_RECTANGLE_JSON_STRING));
    }

    @Test
    public void testJsonNodeMarshallCellsMaxRowHeightsRangeWindow() {
        this.marshallAndCheck(SpreadsheetDeltaWindowed.withWindowed(this.cells(), SpreadsheetDelta.NO_MAX_COLUMN_WIDTHS, this.maxRowHeights(), this.window()),
                JsonNode.object()
                        .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
                        .set(SpreadsheetDelta.MAX_ROW_HEIGHTS_PROPERTY, MAX_ROW_HEIGHTS_JSON)
                        .set(SpreadsheetDeltaWindowed.WINDOW_PROPERTY, WINDOW_RANGE_JSON_STRING));
    }

    @Test
    public void testJsonNodeMarshallCellsMaxColumnWidthsMaxRowHeightsPixelRectangleWindow() {
        this.marshallAndCheck(SpreadsheetDeltaWindowed.withWindowed(this.cells(), this.maxColumnWidths(), this.maxRowHeights(), this.windowPixelRectangle()),
                JsonNode.object()
                        .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
                        .set(SpreadsheetDelta.MAX_COLUMN_WIDTHS_PROPERTY, MAX_COLUMN_WIDTHS_JSON)
                        .set(SpreadsheetDelta.MAX_ROW_HEIGHTS_PROPERTY, MAX_ROW_HEIGHTS_JSON)
                        .set(SpreadsheetDeltaWindowed.WINDOW_PROPERTY, WINDOW_PIXEL_RECTANGLE_JSON_STRING));
    }

    @Test
    public void testJsonNodeMarshallCellsMaxColumnWidthsMaxRowHeightsRangeWindow() {
        this.marshallAndCheck(SpreadsheetDeltaWindowed.withWindowed(this.cells(), this.maxColumnWidths(), this.maxRowHeights(), this.window()),
                JsonNode.object()
                        .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
                        .set(SpreadsheetDelta.MAX_COLUMN_WIDTHS_PROPERTY, MAX_COLUMN_WIDTHS_JSON)
                        .set(SpreadsheetDelta.MAX_ROW_HEIGHTS_PROPERTY, MAX_ROW_HEIGHTS_JSON)
                        .set(SpreadsheetDeltaWindowed.WINDOW_PROPERTY, WINDOW_RANGE_JSON_STRING));
    }

    // equals...........................................................................................................

    @Test
    public final void testEqualsDifferentWindow() {
        this.checkNotEquals(this.createSpreadsheetDelta(this.cells(), this.differentWindow()));
    }

    // toString..........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(SpreadsheetDeltaWindowed.withWindowed(this.cells(),
                SpreadsheetDelta.NO_MAX_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_MAX_ROW_HEIGHTS,
                this.window()),
                "cells: A1=1, B2=2, C3=3 window: A1:E5, F6:Z99");
    }

    @Test
    public void testToStringMaxColumnWidths() {
        this.toStringAndCheck(SpreadsheetDeltaWindowed.withWindowed(this.cells(),
                this.maxColumnWidths(),
                SpreadsheetDelta.NO_MAX_ROW_HEIGHTS,
                this.window()),
                "cells: A1=1, B2=2, C3=3 max: A=50.0 window: A1:E5, F6:Z99");
    }

    @Test
    public void testToStringMaxMaxRowHeights() {
        this.toStringAndCheck(SpreadsheetDeltaWindowed.withWindowed(this.cells(),
                SpreadsheetDelta.NO_MAX_COLUMN_WIDTHS,
                this.maxRowHeights(),
                this.window()),
                "cells: A1=1, B2=2, C3=3 max: 1=75.0 window: A1:E5, F6:Z99");
    }

    @Test
    public void testToStringMaxColumnWidthsMaxRowHeights() {
        this.toStringAndCheck(SpreadsheetDeltaWindowed.withWindowed(this.cells(),
                this.maxColumnWidths(),
                this.maxRowHeights(),
                this.window()),
                "cells: A1=1, B2=2, C3=3 max: A=50.0, 1=75.0 window: A1:E5, F6:Z99");
    }

    // helpers..........................................................................................................

    @Override
    final List<SpreadsheetRectangle> window() {
        return this.window0("A1:E5", "F6:Z99");
    }

    private List<SpreadsheetRectangle> windowPixelRectangle() {
        return this.window0("300x50");
    }

    @Override
    final SpreadsheetDeltaWindowed createSpreadsheetDelta(final Set<SpreadsheetCell> cells) {
        return this.createSpreadsheetDelta(cells, this.window());
    }

    private SpreadsheetDeltaWindowed createSpreadsheetDelta(final Set<SpreadsheetCell> cells,
                                                            final List<SpreadsheetRectangle> window) {
        return SpreadsheetDeltaWindowed.withWindowed(cells, this.maxColumnWidths(), this.maxRowHeights(), window);
    }

    @Override
    public Class<SpreadsheetDeltaWindowed> type() {
        return SpreadsheetDeltaWindowed.class;
    }
}

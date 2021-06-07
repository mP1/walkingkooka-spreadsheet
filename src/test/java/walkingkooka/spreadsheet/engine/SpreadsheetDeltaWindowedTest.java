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
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRectangle;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonString;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

public final class SpreadsheetDeltaWindowedTest extends SpreadsheetDeltaTestCase2<SpreadsheetDeltaWindowed> {

    public SpreadsheetDeltaWindowedTest() {
        super();
        assertNotEquals(this.window(), this.differentWindow(), "window v differentWindow must NOT be equal");
    }

    @Test
    public void testSetCellToLabelsEmpty() {
        final SpreadsheetDeltaWindowed before = this.createSpreadsheetDelta();
        final Map<SpreadsheetCellReference, Set<SpreadsheetLabelName>> different = Maps.empty();

        final SpreadsheetDelta after = before.setCellToLabels(different);
        assertNotSame(before, after);
        this.checkCellToLabels(after, different);
        this.checkCells(after, before.cells());
    }

    @Test
    public void testSetCellToLabelsMissingCellKept() {
        final SpreadsheetDeltaWindowed before = this.createSpreadsheetDelta();
        final Map<SpreadsheetCellReference, Set<SpreadsheetLabelName>> different = Maps.of(
                SpreadsheetCellReference.parseCellReference("A3"), Sets.of(SpreadsheetLabelName.labelName("Different"))
        );

        final SpreadsheetDelta after = before.setCellToLabels(different);
        assertNotSame(before, after);
        this.checkCellToLabels(after, different);
        this.checkCells(after, before.cells());
    }

    @Test
    public void testSetCellToLabelsOutsideWindowFiltered() {
        final SpreadsheetDeltaWindowed before = this.createSpreadsheetDelta();
        final Map<SpreadsheetCellReference, Set<SpreadsheetLabelName>> different = Maps.of(
                SpreadsheetCellReference.parseCellReference("Z99"), Sets.of(SpreadsheetLabelName.labelName("Different"))
        );

        final SpreadsheetDelta after = before.setCellToLabels(different);
        assertNotSame(before, after);
        this.checkCellToLabels(after, Maps.empty());
        this.checkCells(after, before.cells());
    }

    @Test
    public void testSetCellToLabelsOutsideWindowFiltered2() {
        final SpreadsheetDeltaWindowed before = this.createSpreadsheetDelta();

        final SpreadsheetCellReference a1 = this.a1().reference();
        final Set<SpreadsheetLabelName> kept = Sets.of(SpreadsheetLabelName.labelName("Kept"));

        final SpreadsheetCellReference a3 = SpreadsheetCellReference.parseCellReference("A3");
        final Set<SpreadsheetLabelName> kept3 = Sets.of(SpreadsheetLabelName.labelName("Kept2"));

        final Map<SpreadsheetCellReference, Set<SpreadsheetLabelName>> different = Maps.of(
                a1, kept,
                a3, kept3,
                SpreadsheetCellReference.parseCellReference("Z99"), Sets.of(SpreadsheetLabelName.labelName("Lost"))
        );

        final SpreadsheetDelta after = before.setCellToLabels(different);
        assertNotSame(before, after);
        this.checkCellToLabels(after, Maps.of(a1, kept, a3, kept3));
        this.checkCells(after, before.cells());
    }

    // TreePrintable.....................................................................................................

    @Test
    public void testPrintTree() {
        this.treePrintAndCheck(
                SpreadsheetDeltaWindowed.withWindowed(
                        this.cells(),
                        SpreadsheetDelta.NO_CELL_TO_LABELS,
                        SpreadsheetDelta.NO_MAX_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_MAX_ROW_HEIGHTS,
                        this.window()
                ),
                "SpreadsheetDelta\n" +
                        "  cells:\n" +
                        "    Cell A1\n" +
                        "      Formula\n" +
                        "        text: \"1\"\n" +
                        "    Cell B2\n" +
                        "      Formula\n" +
                        "        text: \"2\"\n" +
                        "    Cell C3\n" +
                        "      Formula\n" +
                        "        text: \"3\"\n" +
                        "  window:\n" +
                        "    A1:E5\n" +
                        "    F6:Y99\n"
        );
    }

    @Test
    public void testPrintTreeCellToLabels() {
        this.treePrintAndCheck(
                SpreadsheetDeltaWindowed.withWindowed(
                        this.cells(),
                        this.cellToLabels(),
                        SpreadsheetDelta.NO_MAX_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_MAX_ROW_HEIGHTS,
                        this.window()
                ),
                "SpreadsheetDelta\n" +
                        "  cells:\n" +
                        "    Cell A1\n" +
                        "      Formula\n" +
                        "        text: \"1\"\n" +
                        "    Cell B2\n" +
                        "      Formula\n" +
                        "        text: \"2\"\n" +
                        "    Cell C3\n" +
                        "      Formula\n" +
                        "        text: \"3\"\n" +
                        "  cellToLabels:\n" +
                        "    A1: LabelA1A, LabelA1B\n" +
                        "    B2: LabelB2\n" +
                        "    C3: LabelC3\n" +
                        "  window:\n" +
                        "    A1:E5\n" +
                        "    F6:Y99\n"
        );
    }

    @Test
    public void testPrintTreeColumnWidths() {
        this.treePrintAndCheck(
                SpreadsheetDeltaWindowed.withWindowed(
                        this.cells(),
                        SpreadsheetDelta.NO_CELL_TO_LABELS,
                        this.maxColumnWidths(),
                        SpreadsheetDelta.NO_MAX_ROW_HEIGHTS,
                        this.window()
                ),
                "SpreadsheetDelta\n" +
                        "  cells:\n" +
                        "    Cell A1\n" +
                        "      Formula\n" +
                        "        text: \"1\"\n" +
                        "    Cell B2\n" +
                        "      Formula\n" +
                        "        text: \"2\"\n" +
                        "    Cell C3\n" +
                        "      Formula\n" +
                        "        text: \"3\"\n" +
                        "  columnWidths:\n" +
                        "    A: 50.0\n" +
                        "  window:\n" +
                        "    A1:E5\n" +
                        "    F6:Y99\n"
        );
    }

    @Test
    public void testPrintTreeRowHeights() {
        this.treePrintAndCheck(
                SpreadsheetDeltaWindowed.withWindowed(
                        this.cells(),
                        SpreadsheetDelta.NO_CELL_TO_LABELS,
                        SpreadsheetDelta.NO_MAX_COLUMN_WIDTHS,
                        this.maxRowHeights(),
                        this.window()
                ),
                "SpreadsheetDelta\n" +
                        "  cells:\n" +
                        "    Cell A1\n" +
                        "      Formula\n" +
                        "        text: \"1\"\n" +
                        "    Cell B2\n" +
                        "      Formula\n" +
                        "        text: \"2\"\n" +
                        "    Cell C3\n" +
                        "      Formula\n" +
                        "        text: \"3\"\n" +
                        "  rowHeights:\n" +
                        "    1: 75.0\n" +
                        "  window:\n" +
                        "    A1:E5\n" +
                        "    F6:Y99\n"
        );
    }

    @Test
    public void testPrintTreeNothingEmpty() {
        this.treePrintAndCheck(
                SpreadsheetDeltaWindowed.withWindowed(
                        this.cells(),
                        this.cellToLabels(),
                        this.maxColumnWidths(),
                        this.maxRowHeights(),
                        this.window()
                ),
                "SpreadsheetDelta\n" +
                        "  cells:\n" +
                        "    Cell A1\n" +
                        "      Formula\n" +
                        "        text: \"1\"\n" +
                        "    Cell B2\n" +
                        "      Formula\n" +
                        "        text: \"2\"\n" +
                        "    Cell C3\n" +
                        "      Formula\n" +
                        "        text: \"3\"\n" +
                        "  cellToLabels:\n" +
                        "    A1: LabelA1A, LabelA1B\n" +
                        "    B2: LabelB2\n" +
                        "    C3: LabelC3\n" +
                        "  columnWidths:\n" +
                        "    A: 50.0\n" +
                        "  rowHeights:\n" +
                        "    1: 75.0\n" +
                        "  window:\n" +
                        "    A1:E5\n" +
                        "    F6:Y99\n"
        );
    }

    // JsonNodeMarshallingTesting...........................................................................................

    private final static JsonString WINDOW_PIXEL_RECTANGLE_JSON_STRING = JsonNode.string("B9:300:50");
    private final static JsonString WINDOW_RANGE_JSON_STRING = JsonNode.string("A1:E5,F6:Y99");

    @Test
    public void testUnmarshallCells() {
        this.unmarshallAndCheck(JsonNode.object()
                        .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
                        .set(SpreadsheetDeltaWindowed.WINDOW_PROPERTY, WINDOW_RANGE_JSON_STRING),
                SpreadsheetDeltaWindowed.withWindowed(
                        this.cells(),
                        SpreadsheetDelta.NO_CELL_TO_LABELS,
                        SpreadsheetDelta.NO_MAX_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_MAX_ROW_HEIGHTS,
                        this.window())
        );
    }

    @Test
    public void testJsonNodeMarshall() {
        this.marshallAndCheck(SpreadsheetDeltaWindowed.withWindowed(
                SpreadsheetDelta.NO_CELLS,
                SpreadsheetDelta.NO_CELL_TO_LABELS,
                SpreadsheetDelta.NO_MAX_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_MAX_ROW_HEIGHTS,
                this.window()
                ),
                JsonNode.object()
                        .set(SpreadsheetDeltaWindowed.WINDOW_PROPERTY, WINDOW_RANGE_JSON_STRING));
    }

    @Test
    public void testJsonNodeMarshallCells() {
        this.marshallAndCheck(SpreadsheetDeltaWindowed.withWindowed(
                this.cells(), 
                SpreadsheetDelta.NO_CELL_TO_LABELS,
                SpreadsheetDelta.NO_MAX_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_MAX_ROW_HEIGHTS,
                this.window()
                ),
                JsonNode.object()
                        .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
                        .set(SpreadsheetDeltaWindowed.WINDOW_PROPERTY, WINDOW_RANGE_JSON_STRING));
    }

    @Test
    public void testJsonNodeMarshallCellsMaxColumnWidthsPixelRectangleWindow() {
        this.marshallAndCheck(SpreadsheetDeltaWindowed.withWindowed(
                this.cells(),
                SpreadsheetDelta.NO_CELL_TO_LABELS,
                this.maxColumnWidths(),
                SpreadsheetDelta.NO_MAX_ROW_HEIGHTS,
                this.windowPixelRectangle()
                ),
                JsonNode.object()
                        .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
                        .set(SpreadsheetDelta.MAX_COLUMN_WIDTHS_PROPERTY, MAX_COLUMN_WIDTHS_JSON)
                        .set(SpreadsheetDeltaWindowed.WINDOW_PROPERTY, WINDOW_PIXEL_RECTANGLE_JSON_STRING));
    }

    @Test
    public void testJsonNodeMarshallCellsMaxColumnWidthsRangeWindow() {
        this.marshallAndCheck(SpreadsheetDeltaWindowed.withWindowed(
                this.cells(),
                SpreadsheetDelta.NO_CELL_TO_LABELS,
                this.maxColumnWidths(),
                SpreadsheetDelta.NO_MAX_ROW_HEIGHTS,
                this.window()
                ),
                JsonNode.object()
                        .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
                        .set(SpreadsheetDelta.MAX_COLUMN_WIDTHS_PROPERTY, MAX_COLUMN_WIDTHS_JSON)
                        .set(SpreadsheetDeltaWindowed.WINDOW_PROPERTY, WINDOW_RANGE_JSON_STRING));
    }

    @Test
    public void testJsonNodeMarshallCellsMaxRowHeightsPixelRectangleWindow() {
        this.marshallAndCheck(SpreadsheetDeltaWindowed.withWindowed(
                this.cells(),
                SpreadsheetDelta.NO_CELL_TO_LABELS,
                SpreadsheetDelta.NO_MAX_COLUMN_WIDTHS,
                this.maxRowHeights(),
                this.windowPixelRectangle()
                ),
                JsonNode.object()
                        .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
                        .set(SpreadsheetDelta.MAX_ROW_HEIGHTS_PROPERTY, MAX_ROW_HEIGHTS_JSON)
                        .set(SpreadsheetDeltaWindowed.WINDOW_PROPERTY, WINDOW_PIXEL_RECTANGLE_JSON_STRING));
    }

    @Test
    public void testJsonNodeMarshallCellsMaxRowHeightsRangeWindow() {
        this.marshallAndCheck(SpreadsheetDeltaWindowed.withWindowed(
                this.cells(),
                SpreadsheetDelta.NO_CELL_TO_LABELS,
                SpreadsheetDelta.NO_MAX_COLUMN_WIDTHS,
                this.maxRowHeights(),
                this.window()
                ),
                JsonNode.object()
                        .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
                        .set(SpreadsheetDelta.MAX_ROW_HEIGHTS_PROPERTY, MAX_ROW_HEIGHTS_JSON)
                        .set(SpreadsheetDeltaWindowed.WINDOW_PROPERTY, WINDOW_RANGE_JSON_STRING));
    }

    @Test
    public void testJsonNodeMarshallCellsMaxColumnWidthsMaxRowHeightsPixelRectangleWindow() {
        this.marshallAndCheck(SpreadsheetDeltaWindowed.withWindowed(
                this.cells(),
                SpreadsheetDelta.NO_CELL_TO_LABELS,
                this.maxColumnWidths(),
                this.maxRowHeights(),
                this.windowPixelRectangle()
                ),
                JsonNode.object()
                        .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
                        .set(SpreadsheetDelta.MAX_COLUMN_WIDTHS_PROPERTY, MAX_COLUMN_WIDTHS_JSON)
                        .set(SpreadsheetDelta.MAX_ROW_HEIGHTS_PROPERTY, MAX_ROW_HEIGHTS_JSON)
                        .set(SpreadsheetDeltaWindowed.WINDOW_PROPERTY, WINDOW_PIXEL_RECTANGLE_JSON_STRING));
    }

    @Test
    public void testJsonNodeMarshallCellsMaxColumnWidthsMaxRowHeightsRangeWindow() {
        this.marshallAndCheck(SpreadsheetDeltaWindowed.withWindowed(
                this.cells(),
                SpreadsheetDelta.NO_CELL_TO_LABELS,
                this.maxColumnWidths(),
                this.maxRowHeights(),
                this.window()
                ),
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
                SpreadsheetDelta.NO_CELL_TO_LABELS,
                SpreadsheetDelta.NO_MAX_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_MAX_ROW_HEIGHTS,
                this.window()),
                "cells: A1=1, B2=2, C3=3 window: A1:E5, F6:Y99");
    }

    @Test
    public void testToStringCellToLabels() {
        this.toStringAndCheck(SpreadsheetDeltaWindowed.withWindowed(this.cells(),
                this.cellToLabels(),
                SpreadsheetDelta.NO_MAX_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_MAX_ROW_HEIGHTS,
                this.window()),
                "cells: A1=1, B2=2, C3=3 cellToLabels: A1: LabelA1A, LabelA1B, B2: LabelB2, C3: LabelC3 window: A1:E5, F6:Y99");
    }

    @Test
    public void testToStringMaxColumnWidths() {
        this.toStringAndCheck(SpreadsheetDeltaWindowed.withWindowed(this.cells(),
                SpreadsheetDelta.NO_CELL_TO_LABELS,
                this.maxColumnWidths(),
                SpreadsheetDelta.NO_MAX_ROW_HEIGHTS,
                this.window()),
                "cells: A1=1, B2=2, C3=3 max: A=50.0 window: A1:E5, F6:Y99");
    }

    @Test
    public void testToStringMaxMaxRowHeights() {
        this.toStringAndCheck(SpreadsheetDeltaWindowed.withWindowed(this.cells(),
                SpreadsheetDelta.NO_CELL_TO_LABELS,
                SpreadsheetDelta.NO_MAX_COLUMN_WIDTHS,
                this.maxRowHeights(),
                this.window()),
                "cells: A1=1, B2=2, C3=3 max: 1=75.0 window: A1:E5, F6:Y99");
    }

    @Test
    public void testToStringMaxColumnWidthsMaxRowHeights() {
        this.toStringAndCheck(SpreadsheetDeltaWindowed.withWindowed(this.cells(),
                SpreadsheetDelta.NO_CELL_TO_LABELS,
                this.maxColumnWidths(),
                this.maxRowHeights(),
                this.window()),
                "cells: A1=1, B2=2, C3=3 max: A=50.0, 1=75.0 window: A1:E5, F6:Y99");
    }

    // helpers..........................................................................................................

    @Override
    final List<SpreadsheetRectangle> window() {
        return this.window0("A1:E5", "F6:Y99");
    }

    private List<SpreadsheetRectangle> windowPixelRectangle() {
        return this.window0("B9:300:50");
    }

    @Override
    final SpreadsheetDeltaWindowed createSpreadsheetDelta(final Set<SpreadsheetCell> cells) {
        return this.createSpreadsheetDelta(cells, this.window());
    }

    private SpreadsheetDeltaWindowed createSpreadsheetDelta(final Set<SpreadsheetCell> cells,
                                                            final List<SpreadsheetRectangle> window) {
        return SpreadsheetDeltaWindowed.withWindowed(
                cells,
                this.cellToLabels(),
                this.maxColumnWidths(),
                this.maxRowHeights(),
                window
        );
    }

    @Override
    public Class<SpreadsheetDeltaWindowed> type() {
        return SpreadsheetDeltaWindowed.class;
    }
}

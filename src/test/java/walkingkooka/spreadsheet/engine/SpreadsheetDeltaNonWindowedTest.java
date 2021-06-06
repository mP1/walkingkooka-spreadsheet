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
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.reference.SpreadsheetRectangle;
import walkingkooka.tree.json.JsonNode;

import java.util.List;
import java.util.Set;

public final class SpreadsheetDeltaNonWindowedTest extends SpreadsheetDeltaTestCase2<SpreadsheetDeltaNonWindowed> {

    @Test
    public void testWith() {
        final SpreadsheetDeltaNonWindowed delta = this.createSpreadsheetDelta();
        this.checkCells(delta);
        this.checkWindow(delta, SpreadsheetDelta.NO_WINDOW);
    }

    @Test
    public void testSetDifferentCells() {
        final SpreadsheetDeltaNonWindowed delta = this.createSpreadsheetDelta();
        final Set<SpreadsheetCell> cells = this.cells0("B2", "C3");
        final SpreadsheetDelta different = delta.setCells(cells);
        this.checkCells(different, cells);

        this.checkCells(delta);
    }

    // TreePrintable.....................................................................................................

    @Test
    public void testPrintTree() {
        this.treePrintAndCheck(
                SpreadsheetDeltaNonWindowed.withNonWindowed(
                        this.cells(),
                        SpreadsheetDelta.NO_MAX_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_MAX_ROW_HEIGHTS
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
                        "        text: \"3\"\n"
        );
    }

    @Test
    public void testPrintTreeColumnWidths() {
        this.treePrintAndCheck(
                SpreadsheetDeltaNonWindowed.withNonWindowed(
                        this.cells(),
                        this.maxColumnWidths(),
                        SpreadsheetDelta.NO_MAX_ROW_HEIGHTS
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
                        "    A: 50.0\n"
        );
    }

    @Test
    public void testPrintTreeRowHeights() {
        this.treePrintAndCheck(
                SpreadsheetDeltaNonWindowed.withNonWindowed(
                        this.cells(),
                        SpreadsheetDelta.NO_MAX_COLUMN_WIDTHS,
                        this.maxRowHeights()
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
                        "    1: 75.0\n"
        );
    }

    @Test
    public void testPrintTreeColumnWidthsAndRowHeights() {
        this.treePrintAndCheck(
                SpreadsheetDeltaNonWindowed.withNonWindowed(
                        this.cells(),
                        this.maxColumnWidths(),
                        this.maxRowHeights()
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
                        "  rowHeights:\n" +
                        "    1: 75.0\n"
        );
    }

    // equals...........................................................................................................

    @Test
    public void testEqualsSpreadsheetDeltaWindowed() {
        this.checkNotEquals(SpreadsheetDeltaWindowed.withWindowed(this.cells(),
                this.maxColumnWidths(),
                this.maxRowHeights(),
                this.window0("A1:Z99")));
    }

    // JsonNodeMarshallingTesting...........................................................................................

    @Test
    public void testFromJsonCells() {
        this.unmarshallAndCheck(JsonNode.object()
                        .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson()),
                SpreadsheetDeltaNonWindowed.withNonWindowed(this.cells(),
                        SpreadsheetDelta.NO_MAX_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_MAX_ROW_HEIGHTS));
    }

    @Test
    public void testJsonNodeMarshall() {
        this.marshallAndCheck(SpreadsheetDeltaNonWindowed.withNonWindowed(SpreadsheetDelta.NO_CELLS,
                SpreadsheetDelta.NO_MAX_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_MAX_ROW_HEIGHTS),
                JsonNode.object());
    }

    @Test
    public void testJsonNodeMarshallCells() {
        this.marshallAndCheck(SpreadsheetDeltaNonWindowed.withNonWindowed(this.cells(), SpreadsheetDelta.NO_MAX_COLUMN_WIDTHS, SpreadsheetDelta.NO_MAX_ROW_HEIGHTS),
                JsonNode.object()
                        .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
        );
    }

    @Test
    public void testJsonNodeMarshallCellsMaxCellWidths() {
        this.marshallAndCheck(SpreadsheetDeltaNonWindowed.withNonWindowed(this.cells(), this.maxColumnWidths(), SpreadsheetDelta.NO_MAX_ROW_HEIGHTS),
                JsonNode.object()
                        .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
                        .set(SpreadsheetDelta.MAX_COLUMN_WIDTHS_PROPERTY, MAX_COLUMN_WIDTHS_JSON)
        );
    }

    @Test
    public void testJsonNodeMarshallCellsMaxRowHeights() {
        this.marshallAndCheck(SpreadsheetDeltaNonWindowed.withNonWindowed(this.cells(), SpreadsheetDelta.NO_MAX_COLUMN_WIDTHS, this.maxRowHeights()),
                JsonNode.object()
                        .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
                        .set(SpreadsheetDelta.MAX_ROW_HEIGHTS_PROPERTY, MAX_ROW_HEIGHTS_JSON)
        );
    }

    @Test
    public void testJsonNodeMarshallCellsMaxCellWidthsMaxRowHeights() {
        this.marshallAndCheck(SpreadsheetDeltaNonWindowed.withNonWindowed(this.cells(), this.maxColumnWidths(), this.maxRowHeights()),
                JsonNode.object()
                        .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
                        .set(SpreadsheetDelta.MAX_COLUMN_WIDTHS_PROPERTY, MAX_COLUMN_WIDTHS_JSON)
                        .set(SpreadsheetDelta.MAX_ROW_HEIGHTS_PROPERTY, MAX_ROW_HEIGHTS_JSON)
        );
    }

    // toString..........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(SpreadsheetDeltaNonWindowed.withNonWindowed(this.cells(),
                SpreadsheetDelta.NO_MAX_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_MAX_ROW_HEIGHTS),
                "cells: A1=1, B2=2, C3=3");
    }

    @Test
    public void testToStringMaxColumnWidths() {
        this.toStringAndCheck(SpreadsheetDeltaNonWindowed.withNonWindowed(this.cells(),
                this.maxColumnWidths(),
                SpreadsheetDelta.NO_MAX_ROW_HEIGHTS),
                "cells: A1=1, B2=2, C3=3 max: A=50.0");
    }

    @Test
    public void testToStringMaxRowHeights() {
        this.toStringAndCheck(SpreadsheetDeltaNonWindowed.withNonWindowed(this.cells(),
                SpreadsheetDelta.NO_MAX_COLUMN_WIDTHS,
                this.maxRowHeights()),
                "cells: A1=1, B2=2, C3=3 max: 1=75.0");
    }

    @Test
    public void testToStringMaxColumnWidthsMaxRowHeights() {
        this.toStringAndCheck(SpreadsheetDeltaNonWindowed.withNonWindowed(this.cells(),
                this.maxColumnWidths(),
                this.maxRowHeights()),
                "cells: A1=1, B2=2, C3=3 max: A=50.0, 1=75.0");
    }

    @Override
    SpreadsheetDeltaNonWindowed createSpreadsheetDelta(final Set<SpreadsheetCell> cells) {
        return SpreadsheetDeltaNonWindowed.withNonWindowed(cells, this.maxColumnWidths(), this.maxRowHeights());
    }

    @Override
    final List<SpreadsheetRectangle> window() {
        return Lists.empty();
    }

    @Override
    public Class<SpreadsheetDeltaNonWindowed> type() {
        return SpreadsheetDeltaNonWindowed.class;
    }
}

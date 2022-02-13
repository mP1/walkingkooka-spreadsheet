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
import walkingkooka.spreadsheet.reference.SpreadsheetCellRange;
import walkingkooka.spreadsheet.reference.SpreadsheetViewportSelection;
import walkingkooka.tree.json.JsonNode;

import java.util.Optional;
import java.util.Set;

public final class SpreadsheetDeltaNonWindowedTest extends SpreadsheetDeltaTestCase<SpreadsheetDeltaNonWindowed> {

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
    public void testPrintTreeNoCells() {
        this.treePrintAndCheck(
                SpreadsheetDeltaNonWindowed.withNonWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        SpreadsheetDelta.NO_CELLS,
                        SpreadsheetDelta.NO_COLUMNS,
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_ROWS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_ROW_HEIGHTS
                ),
                "SpreadsheetDelta\n"
        );
    }

    @Test
    public void testPrintTreeSelection() {
        this.treePrintAndCheck(
                SpreadsheetDeltaNonWindowed.withNonWindowed(
                        this.selection(),
                        SpreadsheetDelta.NO_CELLS,
                        SpreadsheetDelta.NO_COLUMNS,
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_ROWS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_ROW_HEIGHTS
                ),
                "SpreadsheetDelta\n" +
                        "  selection:\n" +
                        "    A1:B2 BOTTOM_RIGHT\n"
        );
    }

    @Test
    public void testPrintTreeCells() {
        this.treePrintAndCheck(
                SpreadsheetDeltaNonWindowed.withNonWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        this.cells(),
                        SpreadsheetDelta.NO_COLUMNS,
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_ROWS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_ROW_HEIGHTS
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
    public void testPrintTreeColumns() {
        this.treePrintAndCheck(
                SpreadsheetDeltaNonWindowed.withNonWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        SpreadsheetDelta.NO_CELLS,
                        this.columns(),
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_ROWS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_ROW_HEIGHTS
                ),
                "SpreadsheetDelta\n" +
                        "  columns:\n" +
                        "    A\n" +
                        "      hidden\n" +
                        "    B\n" +
                        "    C\n"
        );
    }

    @Test
    public void testPrintTreeLabels() {
        this.treePrintAndCheck(
                SpreadsheetDeltaNonWindowed.withNonWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        SpreadsheetDelta.NO_CELLS,
                        SpreadsheetDelta.NO_COLUMNS,
                        this.labels(),
                        SpreadsheetDelta.NO_ROWS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_ROW_HEIGHTS
                ),
                "SpreadsheetDelta\n" +
                        "  labels:\n" +
                        "    LabelA1A: A1\n" +
                        "    LabelA1B: A1\n" +
                        "    LabelB2: B2\n" +
                        "    LabelC3: C3\n"
        );
    }

    @Test
    public void testPrintTreeRows() {
        this.treePrintAndCheck(
                SpreadsheetDeltaNonWindowed.withNonWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        SpreadsheetDelta.NO_CELLS,
                        SpreadsheetDelta.NO_COLUMNS,
                        SpreadsheetDelta.NO_LABELS,
                        this.rows(),
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_ROW_HEIGHTS
                ),
                "SpreadsheetDelta\n" +
                        "  rows:\n" +
                        "    1\n" +
                        "      hidden\n" +
                        "    2\n" +
                        "    3\n"
        );
    }

    @Test
    public void testPrintTreeCellsAndLabels() {
        this.treePrintAndCheck(
                SpreadsheetDeltaNonWindowed.withNonWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        this.cells(),
                        SpreadsheetDelta.NO_COLUMNS,
                        this.labels(),
                        SpreadsheetDelta.NO_ROWS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_ROW_HEIGHTS
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
                        "  labels:\n" +
                        "    LabelA1A: A1\n" +
                        "    LabelA1B: A1\n" +
                        "    LabelB2: B2\n" +
                        "    LabelC3: C3\n"
        );
    }

    @Test
    public void testPrintTreeDeletedCellsJson() {
        this.treePrintAndCheck(
                SpreadsheetDeltaNonWindowed.withNonWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        this.cells(),
                        SpreadsheetDelta.NO_COLUMNS,
                        this.labels(),
                        SpreadsheetDelta.NO_ROWS,
                        this.deletedCells(),
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_ROW_HEIGHTS
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
                        "  labels:\n" +
                        "    LabelA1A: A1\n" +
                        "    LabelA1B: A1\n" +
                        "    LabelB2: B2\n" +
                        "    LabelC3: C3\n" +
                        "  deletedCells:\n" +
                        "    C1\n" +
                        "    C2\n"
        );
    }

    @Test
    public void testPrintTreeColumnWidths() {
        this.treePrintAndCheck(
                SpreadsheetDeltaNonWindowed.withNonWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        this.cells(),
                        SpreadsheetDelta.NO_COLUMNS,
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_ROWS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        this.columnWidths(),
                        SpreadsheetDelta.NO_ROW_HEIGHTS
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
                        SpreadsheetDelta.NO_SELECTION,
                        this.cells(),
                        SpreadsheetDelta.NO_COLUMNS,
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_ROWS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        this.rowHeights()
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
    public void testPrintTreeNothingEmpty() {
        this.treePrintAndCheck(
                SpreadsheetDeltaNonWindowed.withNonWindowed(
                        this.selection(),
                        this.cells(),
                        SpreadsheetDelta.NO_COLUMNS,
                        this.labels(),
                        SpreadsheetDelta.NO_ROWS,
                        this.deletedCells(),
                        this.columnWidths(),
                        this.rowHeights()
                ),
                "SpreadsheetDelta\n" +
                        "  selection:\n" +
                        "    A1:B2 BOTTOM_RIGHT\n" +
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
                        "  labels:\n" +
                        "    LabelA1A: A1\n" +
                        "    LabelA1B: A1\n" +
                        "    LabelB2: B2\n" +
                        "    LabelC3: C3\n" +
                        "  deletedCells:\n" +
                        "    C1\n" +
                        "    C2\n" +
                        "  columnWidths:\n" +
                        "    A: 50.0\n" +
                        "  rowHeights:\n" +
                        "    1: 75.0\n"
        );
    }

    // JsonNodeMarshallingTesting...........................................................................................

    @Override
    void unmarshallSelectionAndCheck(final SpreadsheetViewportSelection selection) {
        this.unmarshallAndCheck(
                JsonNode.object()
                        .set(
                                SpreadsheetDelta.SELECTION_PROPERTY,
                                this.marshallContext()
                                        .marshall(selection)
                        ),
                SpreadsheetDeltaWindowed.withWindowed(
                        Optional.ofNullable(
                                selection
                        ),
                        SpreadsheetDelta.NO_CELLS,
                        SpreadsheetDelta.NO_COLUMNS,
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_ROWS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_ROW_HEIGHTS,
                        SpreadsheetDelta.NO_WINDOW
                )
        );
    }

    @Test
    public void testUnmarshallCells() {
        this.unmarshallAndCheck(
                JsonNode.object()
                        .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson()),
                SpreadsheetDeltaNonWindowed.withNonWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        this.cells(),
                        SpreadsheetDelta.NO_COLUMNS,
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_ROWS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_ROW_HEIGHTS)
        );
    }

    @Test
    public void testUnmarshallLabels() {
        this.unmarshallAndCheck(
                JsonNode.object()
                        .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
                        .set(SpreadsheetDelta.LABELS_PROPERTY, labelsJson()),
                SpreadsheetDeltaNonWindowed.withNonWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        this.cells(),
                        SpreadsheetDelta.NO_COLUMNS,
                        this.labels(),
                        SpreadsheetDelta.NO_ROWS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_ROW_HEIGHTS)
        );
    }

    @Test
    public void testUnmarshallDeletedJson() {
        this.unmarshallAndCheck(
                JsonNode.object()
                        .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
                        .set(SpreadsheetDelta.DELETED_CELLS_PROPERTY, deletedCellsJson()),
                SpreadsheetDeltaNonWindowed.withNonWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        this.cells(),
                        SpreadsheetDelta.NO_COLUMNS,
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_ROWS,
                        this.deletedCells(),
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_ROW_HEIGHTS)
        );
    }

    @Test
    public void testMarshall() {
        this.marshallAndCheck(
                SpreadsheetDeltaNonWindowed.withNonWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        SpreadsheetDelta.NO_CELLS,
                        SpreadsheetDelta.NO_COLUMNS,
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_ROWS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_ROW_HEIGHTS),
                JsonNode.object()
        );
    }

    @Test
    public void testMarshallSelection() {
        this.marshallAndCheck(
                SpreadsheetDeltaNonWindowed.withNonWindowed(
                        this.selection(),
                        SpreadsheetDelta.NO_CELLS,
                        SpreadsheetDelta.NO_COLUMNS,
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_ROWS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_ROW_HEIGHTS
                ),
                JsonNode.object()
                        .set(SpreadsheetDelta.SELECTION_PROPERTY, this.selectionJson())
        );
    }

    @Test
    public void testMarshallCells() {
        this.marshallAndCheck(
                SpreadsheetDeltaNonWindowed.withNonWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        this.cells(),
                        SpreadsheetDelta.NO_COLUMNS,
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_ROWS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_ROW_HEIGHTS
                ),
                JsonNode.object()
                        .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
        );
    }

    @Test
    public void testMarshallColumns() {
        this.marshallAndCheck(
                SpreadsheetDeltaNonWindowed.withNonWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        SpreadsheetDelta.NO_CELLS,
                        this.columns(),
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_ROWS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_ROW_HEIGHTS
                ),
                JsonNode.object()
                        .set(SpreadsheetDelta.COLUMNS_PROPERTY, this.columnsJson())
        );
    }

    @Test
    public void testMarshallRows() {
        this.marshallAndCheck(
                SpreadsheetDeltaNonWindowed.withNonWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        SpreadsheetDelta.NO_CELLS,
                        SpreadsheetDelta.NO_COLUMNS,
                        SpreadsheetDelta.NO_LABELS,
                        this.rows(),
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_ROW_HEIGHTS
                ),
                JsonNode.object()
                        .set(SpreadsheetDelta.ROWS_PROPERTY, this.rowsJson())
        );
    }

    @Test
    public void testMarshallCellsAndCellsToLabels() {
        this.marshallAndCheck(
                SpreadsheetDeltaNonWindowed.withNonWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        this.cells(),
                        SpreadsheetDelta.NO_COLUMNS,
                        this.labels(),
                        SpreadsheetDelta.NO_ROWS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_ROW_HEIGHTS
                ),
                JsonNode.object()
                        .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
                        .set(SpreadsheetDelta.LABELS_PROPERTY, labelsJson())
        );
    }

    @Test
    public void testMarshallCellsDeletedCells() {
        this.marshallAndCheck(
                SpreadsheetDeltaNonWindowed.withNonWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        this.cells(),
                        SpreadsheetDelta.NO_COLUMNS,
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_ROWS,
                        this.deletedCells(),
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_ROW_HEIGHTS
                ),
                JsonNode.object()
                        .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
                        .set(SpreadsheetDelta.DELETED_CELLS_PROPERTY, deletedCellsJson())
        );
    }

    @Test
    public void testMarshallCellsMaxCellWidths() {
        this.marshallAndCheck(
                SpreadsheetDeltaNonWindowed.withNonWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        this.cells(),
                        SpreadsheetDelta.NO_COLUMNS,
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_ROWS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        this.columnWidths(),
                        SpreadsheetDelta.NO_ROW_HEIGHTS
                ),
                JsonNode.object()
                        .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
                        .set(SpreadsheetDelta.COLUMN_WIDTHS_PROPERTY, COLUMN_WIDTHS_JSON)
        );
    }

    @Test
    public void testMarshallCellsRowHeights() {
        this.marshallAndCheck(
                SpreadsheetDeltaNonWindowed.withNonWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        this.cells(),
                        SpreadsheetDelta.NO_COLUMNS,
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_ROWS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        this.rowHeights()
                ),
                JsonNode.object()
                        .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
                        .set(SpreadsheetDelta.ROW_HEIGHTS_PROPERTY, ROW_HEIGHTS_JSON)
        );
    }

    @Test
    public void testMarshallCellsMaxCellWidthsRowHeights() {
        this.marshallAndCheck(
                SpreadsheetDeltaNonWindowed.withNonWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        this.cells(),
                        SpreadsheetDelta.NO_COLUMNS,
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_ROWS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        this.columnWidths(),
                        this.rowHeights()
                ),
                JsonNode.object()
                        .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
                        .set(SpreadsheetDelta.COLUMN_WIDTHS_PROPERTY, COLUMN_WIDTHS_JSON)
                        .set(SpreadsheetDelta.ROW_HEIGHTS_PROPERTY, ROW_HEIGHTS_JSON)
        );
    }

    // toString..........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                SpreadsheetDeltaNonWindowed.withNonWindowed(
                        this.selection(),
                        this.cells(),
                        SpreadsheetDelta.NO_COLUMNS,
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_ROWS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_ROW_HEIGHTS
                ),
                "selection: A1:B2 BOTTOM_RIGHT cells: A1=1, B2=2, C3=3");
    }

    @Test
    public void testToStringLabels() {
        this.toStringAndCheck(
                SpreadsheetDeltaNonWindowed.withNonWindowed(
                        this.selection(),
                        this.cells(),
                        SpreadsheetDelta.NO_COLUMNS,
                        this.labels(),
                        SpreadsheetDelta.NO_ROWS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_ROW_HEIGHTS
                ),
                "selection: A1:B2 BOTTOM_RIGHT cells: A1=1, B2=2, C3=3 labels: LabelA1A=A1, LabelA1B=A1, LabelB2=B2, LabelC3=C3");
    }

    @Test
    public void testToStringDeletedCells() {
        this.toStringAndCheck(
                SpreadsheetDeltaNonWindowed.withNonWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        this.cells(),
                        SpreadsheetDelta.NO_COLUMNS,
                        this.labels(),
                        SpreadsheetDelta.NO_ROWS,
                        this.deletedCells(),
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_ROW_HEIGHTS
                ),
                "cells: A1=1, B2=2, C3=3 labels: LabelA1A=A1, LabelA1B=A1, LabelB2=B2, LabelC3=C3 deletedCells: C1, C2");
    }

    @Test
    public void testToStringColumnWidths() {
        this.toStringAndCheck(
                SpreadsheetDeltaNonWindowed.withNonWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        this.cells(),
                        SpreadsheetDelta.NO_COLUMNS,
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_ROWS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        this.columnWidths(),
                        SpreadsheetDelta.NO_ROW_HEIGHTS
                ),
                "cells: A1=1, B2=2, C3=3 max: A=50.0");
    }

    @Test
    public void testToStringRowHeights() {
        this.toStringAndCheck(
                SpreadsheetDeltaNonWindowed.withNonWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        this.cells(),
                        SpreadsheetDelta.NO_COLUMNS,
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_ROWS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        this.rowHeights()
                ),
                "cells: A1=1, B2=2, C3=3 max: 1=75.0");
    }

    @Test
    public void testToStringColumnWidthsRowHeights() {
        this.toStringAndCheck(
                SpreadsheetDeltaNonWindowed.withNonWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        this.cells(),
                        SpreadsheetDelta.NO_COLUMNS,
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_ROWS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        this.columnWidths(),
                        this.rowHeights()
                ),
                "cells: A1=1, B2=2, C3=3 max: A=50.0, 1=75.0");
    }

    @Override
    SpreadsheetDeltaNonWindowed createSpreadsheetDelta(final Set<SpreadsheetCell> cells) {
        return SpreadsheetDeltaNonWindowed.withNonWindowed(
                this.selection(),
                cells,
                this.columns(),
                this.labels(),
                this.rows(),
                this.deletedCells(),
                this.columnWidths(),
                this.rowHeights()
        );
    }

    @Override
    Optional<SpreadsheetCellRange> window() {
        return SpreadsheetDelta.NO_WINDOW;
    }

    @Override
    public Class<SpreadsheetDeltaNonWindowed> type() {
        return SpreadsheetDeltaNonWindowed.class;
    }
}

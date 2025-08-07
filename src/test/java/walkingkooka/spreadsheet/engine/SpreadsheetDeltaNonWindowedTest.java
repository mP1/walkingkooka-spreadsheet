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
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewport;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewportWindows;
import walkingkooka.tree.json.JsonNode;

import java.util.Optional;
import java.util.Set;

public final class SpreadsheetDeltaNonWindowedTest extends SpreadsheetDeltaTestCase<SpreadsheetDeltaNonWindowed> {

    @Test
    public void testWith() {
        final SpreadsheetDeltaNonWindowed delta = this.createSpreadsheetDelta();
        this.cellsAndCheck(delta);
        this.rowCountAndCheck(delta);
        this.rowCountAndCheck(delta);
        this.windowAndCheck(delta, SpreadsheetDelta.NO_WINDOW);
    }

    @Test
    public void testSetDifferentCells() {
        final SpreadsheetDeltaNonWindowed delta = this.createSpreadsheetDelta();
        final Set<SpreadsheetCell> cells = this.cells0("B2", "C3");
        final SpreadsheetDelta different = delta.setCells(cells);
        this.cellsAndCheck(different, cells);

        this.cellsAndCheck(delta);
    }

    @Override
    SpreadsheetDeltaNonWindowed createSpreadsheetDelta(final Set<SpreadsheetCell> cells) {
        return SpreadsheetDeltaNonWindowed.withNonWindowed(
            this.viewport(),
            cells,
            this.columns(),
            this.forms(),
            this.labels(),
            this.rows(),
            this.references(),
            this.deletedCells(),
            this.deletedColumns(),
            this.deletedRows(),
            this.deletedLabels(),
            this.matchedCells(),
            this.columnWidths(),
            this.rowHeights(),
            this.columnCount(),
            this.rowCount()
        );
    }

    @Override
    SpreadsheetViewportWindows window() {
        return SpreadsheetViewportWindows.EMPTY;
    }

    // json.............................................................................................................

    @Override
    void unmarshallViewportAndCheck(final SpreadsheetViewport viewport) {
        this.unmarshallAndCheck(
            JsonNode.object()
                .set(
                    SpreadsheetDelta.VIEWPORT_SELECTION_PROPERTY,
                    this.marshallContext()
                        .marshall(viewport)
                ),
            SpreadsheetDeltaWindowed.withWindowed(
                Optional.ofNullable(
                    viewport
                ),
                SpreadsheetDelta.NO_CELLS,
                SpreadsheetDelta.NO_COLUMNS,
                SpreadsheetDelta.NO_FORMS,
                SpreadsheetDelta.NO_LABELS,
                SpreadsheetDelta.NO_ROWS,
                SpreadsheetDelta.NO_REFERENCES,
                SpreadsheetDelta.NO_DELETED_CELLS,
                SpreadsheetDelta.NO_DELETED_COLUMNS,
                SpreadsheetDelta.NO_DELETED_ROWS,
                SpreadsheetDelta.NO_DELETED_LABELS,
                SpreadsheetDelta.NO_MATCHED_CELLS,
                SpreadsheetDelta.NO_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_ROW_HEIGHTS,
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT,
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
                SpreadsheetDelta.NO_VIEWPORT,
                this.cells(),
                SpreadsheetDelta.NO_COLUMNS,
                SpreadsheetDelta.NO_FORMS,
                SpreadsheetDelta.NO_LABELS,
                SpreadsheetDelta.NO_ROWS,
                SpreadsheetDelta.NO_REFERENCES,
                SpreadsheetDelta.NO_DELETED_CELLS,
                SpreadsheetDelta.NO_DELETED_COLUMNS,
                SpreadsheetDelta.NO_DELETED_ROWS,
                SpreadsheetDelta.NO_DELETED_LABELS,
                SpreadsheetDelta.NO_MATCHED_CELLS,
                SpreadsheetDelta.NO_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_ROW_HEIGHTS,
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT
            )
        );
    }

    @Test
    public void testUnmarshallLabels() {
        this.unmarshallAndCheck(
            JsonNode.object()
                .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
                .set(SpreadsheetDelta.LABELS_PROPERTY, labelsJson()),
            SpreadsheetDeltaNonWindowed.withNonWindowed(
                SpreadsheetDelta.NO_VIEWPORT,
                this.cells(),
                SpreadsheetDelta.NO_COLUMNS,
                SpreadsheetDelta.NO_FORMS,
                this.labels(),
                SpreadsheetDelta.NO_ROWS,
                SpreadsheetDelta.NO_REFERENCES,
                SpreadsheetDelta.NO_DELETED_CELLS,
                SpreadsheetDelta.NO_DELETED_COLUMNS,
                SpreadsheetDelta.NO_DELETED_ROWS,
                SpreadsheetDelta.NO_DELETED_LABELS,
                SpreadsheetDelta.NO_MATCHED_CELLS,
                SpreadsheetDelta.NO_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_ROW_HEIGHTS,
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT
            )
        );
    }

    @Test
    public void testUnmarshallReferences() {
        this.unmarshallAndCheck(
            JsonNode.object()
                .set(SpreadsheetDelta.REFERENCES_PROPERTY, this.referencesJson()),
            SpreadsheetDeltaNonWindowed.withNonWindowed(
                SpreadsheetDelta.NO_VIEWPORT,
                SpreadsheetDelta.NO_CELLS,
                SpreadsheetDelta.NO_COLUMNS,
                SpreadsheetDelta.NO_FORMS,
                SpreadsheetDelta.NO_LABELS,
                SpreadsheetDelta.NO_ROWS,
                this.references(),
                SpreadsheetDelta.NO_DELETED_CELLS,
                SpreadsheetDelta.NO_DELETED_COLUMNS,
                SpreadsheetDelta.NO_DELETED_ROWS,
                SpreadsheetDelta.NO_DELETED_LABELS,
                SpreadsheetDelta.NO_MATCHED_CELLS,
                SpreadsheetDelta.NO_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_ROW_HEIGHTS,
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT
            )
        );
    }

    @Test
    public void testUnmarshallCellsAndDeletedCellsJson() {
        this.unmarshallAndCheck(
            JsonNode.object()
                .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
                .set(SpreadsheetDelta.DELETED_CELLS_PROPERTY, deletedCellsJson()),
            SpreadsheetDeltaNonWindowed.withNonWindowed(
                SpreadsheetDelta.NO_VIEWPORT,
                this.cells(),
                SpreadsheetDelta.NO_COLUMNS,
                SpreadsheetDelta.NO_FORMS,
                SpreadsheetDelta.NO_LABELS,
                SpreadsheetDelta.NO_ROWS,
                SpreadsheetDelta.NO_REFERENCES,
                this.deletedCells(),
                SpreadsheetDelta.NO_DELETED_COLUMNS,
                SpreadsheetDelta.NO_DELETED_ROWS,
                SpreadsheetDelta.NO_DELETED_LABELS,
                SpreadsheetDelta.NO_MATCHED_CELLS,
                SpreadsheetDelta.NO_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_ROW_HEIGHTS,
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT
            )
        );
    }

    @Test
    public void testMarshall() {
        this.marshallAndCheck(
            SpreadsheetDeltaNonWindowed.withNonWindowed(
                SpreadsheetDelta.NO_VIEWPORT,
                SpreadsheetDelta.NO_CELLS,
                SpreadsheetDelta.NO_COLUMNS,
                SpreadsheetDelta.NO_FORMS,
                SpreadsheetDelta.NO_LABELS,
                SpreadsheetDelta.NO_ROWS,
                SpreadsheetDelta.NO_REFERENCES,
                SpreadsheetDelta.NO_DELETED_CELLS,
                SpreadsheetDelta.NO_DELETED_COLUMNS,
                SpreadsheetDelta.NO_DELETED_ROWS,
                SpreadsheetDelta.NO_DELETED_LABELS,
                SpreadsheetDelta.NO_MATCHED_CELLS,
                SpreadsheetDelta.NO_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_ROW_HEIGHTS,
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT
            ),
            JsonNode.object()
        );
    }

    @Test
    public void testMarshallViewport() {
        this.marshallAndCheck(
            SpreadsheetDeltaNonWindowed.withNonWindowed(
                this.viewport(),
                SpreadsheetDelta.NO_CELLS,
                SpreadsheetDelta.NO_COLUMNS,
                SpreadsheetDelta.NO_FORMS,
                SpreadsheetDelta.NO_LABELS,
                SpreadsheetDelta.NO_ROWS,
                SpreadsheetDelta.NO_REFERENCES,
                SpreadsheetDelta.NO_DELETED_CELLS,
                SpreadsheetDelta.NO_DELETED_COLUMNS,
                SpreadsheetDelta.NO_DELETED_ROWS,
                SpreadsheetDelta.NO_DELETED_LABELS,
                SpreadsheetDelta.NO_MATCHED_CELLS,
                SpreadsheetDelta.NO_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_ROW_HEIGHTS,
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT
            ),
            JsonNode.object()
                .set(SpreadsheetDelta.VIEWPORT_SELECTION_PROPERTY, this.viewportJson())
        );
    }

    @Test
    public void testMarshallCells() {
        this.marshallAndCheck(
            SpreadsheetDeltaNonWindowed.withNonWindowed(
                SpreadsheetDelta.NO_VIEWPORT,
                this.cells(),
                SpreadsheetDelta.NO_COLUMNS,
                SpreadsheetDelta.NO_FORMS,
                SpreadsheetDelta.NO_LABELS,
                SpreadsheetDelta.NO_ROWS,
                SpreadsheetDelta.NO_REFERENCES,
                SpreadsheetDelta.NO_DELETED_CELLS,
                SpreadsheetDelta.NO_DELETED_COLUMNS,
                SpreadsheetDelta.NO_DELETED_ROWS,
                SpreadsheetDelta.NO_DELETED_LABELS,
                SpreadsheetDelta.NO_MATCHED_CELLS,
                SpreadsheetDelta.NO_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_ROW_HEIGHTS,
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT
            ),
            JsonNode.object()
                .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
        );
    }

    @Test
    public void testMarshallColumns() {
        this.marshallAndCheck(
            SpreadsheetDeltaNonWindowed.withNonWindowed(
                SpreadsheetDelta.NO_VIEWPORT,
                SpreadsheetDelta.NO_CELLS,
                this.columns(),
                SpreadsheetDelta.NO_FORMS,
                SpreadsheetDelta.NO_LABELS,
                SpreadsheetDelta.NO_ROWS,
                SpreadsheetDelta.NO_REFERENCES,
                SpreadsheetDelta.NO_DELETED_CELLS,
                SpreadsheetDelta.NO_DELETED_COLUMNS,
                SpreadsheetDelta.NO_DELETED_ROWS,
                SpreadsheetDelta.NO_DELETED_LABELS,
                SpreadsheetDelta.NO_MATCHED_CELLS,
                SpreadsheetDelta.NO_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_ROW_HEIGHTS,
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT
            ),
            JsonNode.object()
                .set(SpreadsheetDelta.COLUMNS_PROPERTY, this.columnsJson())
        );
    }

    @Test
    public void testMarshallForms() {
        this.marshallAndCheck(
            SpreadsheetDeltaNonWindowed.withNonWindowed(
                SpreadsheetDelta.NO_VIEWPORT,
                SpreadsheetDelta.NO_CELLS,
                SpreadsheetDelta.NO_COLUMNS,
                this.forms(),
                SpreadsheetDelta.NO_LABELS,
                SpreadsheetDelta.NO_ROWS,
                SpreadsheetDelta.NO_REFERENCES,
                SpreadsheetDelta.NO_DELETED_CELLS,
                SpreadsheetDelta.NO_DELETED_COLUMNS,
                SpreadsheetDelta.NO_DELETED_ROWS,
                SpreadsheetDelta.NO_DELETED_LABELS,
                SpreadsheetDelta.NO_MATCHED_CELLS,
                SpreadsheetDelta.NO_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_ROW_HEIGHTS,
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT
            ),
            JsonNode.object()
                .set(SpreadsheetDelta.FORMS_PROPERTY, this.formsJson())
        );
    }

    @Test
    public void testMarshallRows() {
        this.marshallAndCheck(
            SpreadsheetDeltaNonWindowed.withNonWindowed(
                SpreadsheetDelta.NO_VIEWPORT,
                SpreadsheetDelta.NO_CELLS,
                SpreadsheetDelta.NO_COLUMNS,
                SpreadsheetDelta.NO_FORMS,
                SpreadsheetDelta.NO_LABELS,
                this.rows(),
                SpreadsheetDelta.NO_REFERENCES,
                SpreadsheetDelta.NO_DELETED_CELLS,
                SpreadsheetDelta.NO_DELETED_COLUMNS,
                SpreadsheetDelta.NO_DELETED_ROWS,
                SpreadsheetDelta.NO_DELETED_LABELS,
                SpreadsheetDelta.NO_MATCHED_CELLS,
                SpreadsheetDelta.NO_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_ROW_HEIGHTS,
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT
            ),
            JsonNode.object()
                .set(SpreadsheetDelta.ROWS_PROPERTY, this.rowsJson())
        );
    }

    @Test
    public void testMarshallReferences() {
        this.marshallAndCheck(
            SpreadsheetDeltaNonWindowed.withNonWindowed(
                SpreadsheetDelta.NO_VIEWPORT,
                SpreadsheetDelta.NO_CELLS,
                SpreadsheetDelta.NO_COLUMNS,
                SpreadsheetDelta.NO_FORMS,
                SpreadsheetDelta.NO_LABELS,
                SpreadsheetDelta.NO_ROWS,
                this.references(),
                SpreadsheetDelta.NO_DELETED_CELLS,
                SpreadsheetDelta.NO_DELETED_COLUMNS,
                SpreadsheetDelta.NO_DELETED_ROWS,
                SpreadsheetDelta.NO_DELETED_LABELS,
                SpreadsheetDelta.NO_MATCHED_CELLS,
                SpreadsheetDelta.NO_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_ROW_HEIGHTS,
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT
            ),
            JsonNode.object()
                .set(
                    SpreadsheetDelta.REFERENCES_PROPERTY,
                    this.referencesJson()
                )
        );
    }

    @Test
    public void testMarshallCellsAndCellsToLabels() {
        this.marshallAndCheck(
            SpreadsheetDeltaNonWindowed.withNonWindowed(
                SpreadsheetDelta.NO_VIEWPORT,
                this.cells(),
                SpreadsheetDelta.NO_COLUMNS,
                SpreadsheetDelta.NO_FORMS,
                this.labels(),
                SpreadsheetDelta.NO_ROWS,
                SpreadsheetDelta.NO_REFERENCES,
                SpreadsheetDelta.NO_DELETED_CELLS,
                SpreadsheetDelta.NO_DELETED_COLUMNS,
                SpreadsheetDelta.NO_DELETED_ROWS,
                SpreadsheetDelta.NO_DELETED_LABELS,
                SpreadsheetDelta.NO_MATCHED_CELLS,
                SpreadsheetDelta.NO_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_ROW_HEIGHTS,
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT
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
                SpreadsheetDelta.NO_VIEWPORT,
                this.cells(),
                SpreadsheetDelta.NO_COLUMNS,
                SpreadsheetDelta.NO_FORMS,
                SpreadsheetDelta.NO_LABELS,
                SpreadsheetDelta.NO_ROWS,
                SpreadsheetDelta.NO_REFERENCES,
                this.deletedCells(),
                SpreadsheetDelta.NO_DELETED_COLUMNS,
                SpreadsheetDelta.NO_DELETED_ROWS,
                SpreadsheetDelta.NO_DELETED_LABELS,
                SpreadsheetDelta.NO_MATCHED_CELLS,
                SpreadsheetDelta.NO_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_ROW_HEIGHTS,
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT
            ),
            JsonNode.object()
                .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
                .set(SpreadsheetDelta.DELETED_CELLS_PROPERTY, deletedCellsJson())
        );
    }

    @Test
    public void testMarshallCellsDeletedColumns() {
        this.marshallAndCheck(
            SpreadsheetDeltaNonWindowed.withNonWindowed(
                SpreadsheetDelta.NO_VIEWPORT,
                this.cells(),
                SpreadsheetDelta.NO_COLUMNS,
                SpreadsheetDelta.NO_FORMS,
                SpreadsheetDelta.NO_LABELS,
                SpreadsheetDelta.NO_ROWS,
                SpreadsheetDelta.NO_REFERENCES,
                SpreadsheetDelta.NO_DELETED_CELLS,
                this.deletedColumns(),
                SpreadsheetDelta.NO_DELETED_ROWS,
                SpreadsheetDelta.NO_DELETED_LABELS,
                SpreadsheetDelta.NO_MATCHED_CELLS,
                SpreadsheetDelta.NO_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_ROW_HEIGHTS,
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT
            ),
            JsonNode.object()
                .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
                .set(SpreadsheetDelta.DELETED_COLUMNS_PROPERTY, deletedColumnsJson())
        );
    }

    @Test
    public void testMarshallCellsDeletedRows() {
        this.marshallAndCheck(
            SpreadsheetDeltaNonWindowed.withNonWindowed(
                SpreadsheetDelta.NO_VIEWPORT,
                this.cells(),
                SpreadsheetDelta.NO_COLUMNS,
                SpreadsheetDelta.NO_FORMS,
                SpreadsheetDelta.NO_LABELS,
                SpreadsheetDelta.NO_ROWS,
                SpreadsheetDelta.NO_REFERENCES,
                SpreadsheetDelta.NO_DELETED_CELLS,
                SpreadsheetDelta.NO_DELETED_COLUMNS,
                this.deletedRows(),
                SpreadsheetDelta.NO_DELETED_LABELS,
                SpreadsheetDelta.NO_MATCHED_CELLS,
                SpreadsheetDelta.NO_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_ROW_HEIGHTS,
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT
            ),
            JsonNode.object()
                .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
                .set(SpreadsheetDelta.DELETED_ROWS_PROPERTY, deletedRowsJson())
        );
    }

    @Test
    public void testMarshallCellsDeletedLabels() {
        this.marshallAndCheck(
            SpreadsheetDeltaNonWindowed.withNonWindowed(
                SpreadsheetDelta.NO_VIEWPORT,
                this.cells(),
                SpreadsheetDelta.NO_COLUMNS,
                SpreadsheetDelta.NO_FORMS,
                SpreadsheetDelta.NO_LABELS,
                SpreadsheetDelta.NO_ROWS,
                SpreadsheetDelta.NO_REFERENCES,
                SpreadsheetDelta.NO_DELETED_CELLS,
                SpreadsheetDelta.NO_DELETED_COLUMNS,
                SpreadsheetDelta.NO_DELETED_ROWS,
                this.deletedLabels(),
                SpreadsheetDelta.NO_MATCHED_CELLS,
                SpreadsheetDelta.NO_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_ROW_HEIGHTS,
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT
            ),
            JsonNode.object()
                .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
                .set(SpreadsheetDelta.DELETED_LABELS_PROPERTY, deletedLabelsJson())
        );
    }

    @Test
    public void testMarshallCellsMatchedCells() {
        this.marshallAndCheck(
            SpreadsheetDeltaNonWindowed.withNonWindowed(
                SpreadsheetDelta.NO_VIEWPORT,
                this.cells(),
                SpreadsheetDelta.NO_COLUMNS,
                SpreadsheetDelta.NO_FORMS,
                SpreadsheetDelta.NO_LABELS,
                SpreadsheetDelta.NO_ROWS,
                SpreadsheetDelta.NO_REFERENCES,
                SpreadsheetDelta.NO_DELETED_CELLS,
                SpreadsheetDelta.NO_DELETED_COLUMNS,
                SpreadsheetDelta.NO_DELETED_ROWS,
                SpreadsheetDelta.NO_DELETED_LABELS,
                this.matchedCells(),
                SpreadsheetDelta.NO_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_ROW_HEIGHTS,
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT
            ),
            JsonNode.object()
                .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
                .set(SpreadsheetDelta.MATCHED_CELLS_PROPERTY, matchedCellsJson())
        );
    }

    @Test
    public void testMarshallCellsColumnWidths() {
        this.marshallAndCheck(
            SpreadsheetDeltaNonWindowed.withNonWindowed(
                SpreadsheetDelta.NO_VIEWPORT,
                this.cells(),
                SpreadsheetDelta.NO_COLUMNS,
                SpreadsheetDelta.NO_FORMS,
                SpreadsheetDelta.NO_LABELS,
                SpreadsheetDelta.NO_ROWS,
                SpreadsheetDelta.NO_REFERENCES,
                SpreadsheetDelta.NO_DELETED_CELLS,
                SpreadsheetDelta.NO_DELETED_COLUMNS,
                SpreadsheetDelta.NO_DELETED_ROWS,
                SpreadsheetDelta.NO_DELETED_LABELS,
                SpreadsheetDelta.NO_MATCHED_CELLS,
                this.columnWidths(),
                SpreadsheetDelta.NO_ROW_HEIGHTS,
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT
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
                SpreadsheetDelta.NO_VIEWPORT,
                this.cells(),
                SpreadsheetDelta.NO_COLUMNS,
                SpreadsheetDelta.NO_FORMS,
                SpreadsheetDelta.NO_LABELS,
                SpreadsheetDelta.NO_ROWS,
                SpreadsheetDelta.NO_REFERENCES,
                SpreadsheetDelta.NO_DELETED_CELLS,
                SpreadsheetDelta.NO_DELETED_COLUMNS,
                SpreadsheetDelta.NO_DELETED_ROWS,
                SpreadsheetDelta.NO_DELETED_LABELS,
                SpreadsheetDelta.NO_MATCHED_CELLS,
                SpreadsheetDelta.NO_COLUMN_WIDTHS,
                this.rowHeights(),
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT
            ),
            JsonNode.object()
                .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
                .set(SpreadsheetDelta.ROW_HEIGHTS_PROPERTY, ROW_HEIGHTS_JSON)
        );
    }

    @Test
    public void testMarshallCellsColumnWidthsRowHeights() {
        this.marshallAndCheck(
            SpreadsheetDeltaNonWindowed.withNonWindowed(
                SpreadsheetDelta.NO_VIEWPORT,
                this.cells(),
                SpreadsheetDelta.NO_COLUMNS,
                SpreadsheetDelta.NO_FORMS,
                SpreadsheetDelta.NO_LABELS,
                SpreadsheetDelta.NO_ROWS,
                SpreadsheetDelta.NO_REFERENCES,
                SpreadsheetDelta.NO_DELETED_CELLS,
                SpreadsheetDelta.NO_DELETED_COLUMNS,
                SpreadsheetDelta.NO_DELETED_ROWS,
                SpreadsheetDelta.NO_DELETED_LABELS,
                SpreadsheetDelta.NO_MATCHED_CELLS,
                this.columnWidths(),
                this.rowHeights(),
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT
            ),
            JsonNode.object()
                .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
                .set(SpreadsheetDelta.COLUMN_WIDTHS_PROPERTY, COLUMN_WIDTHS_JSON)
                .set(SpreadsheetDelta.ROW_HEIGHTS_PROPERTY, ROW_HEIGHTS_JSON)
        );
    }

    @Test
    public void testMarshallCellsRowCountRowCount() {
        this.marshallAndCheck(
            SpreadsheetDeltaNonWindowed.withNonWindowed(
                SpreadsheetDelta.NO_VIEWPORT,
                this.cells(),
                SpreadsheetDelta.NO_COLUMNS,
                SpreadsheetDelta.NO_FORMS,
                SpreadsheetDelta.NO_LABELS,
                SpreadsheetDelta.NO_ROWS,
                SpreadsheetDelta.NO_REFERENCES,
                SpreadsheetDelta.NO_DELETED_CELLS,
                SpreadsheetDelta.NO_DELETED_COLUMNS,
                SpreadsheetDelta.NO_DELETED_ROWS,
                SpreadsheetDelta.NO_DELETED_LABELS,
                SpreadsheetDelta.NO_MATCHED_CELLS,
                SpreadsheetDelta.NO_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_ROW_HEIGHTS,
                this.columnCount(),
                this.rowCount()
            ),
            JsonNode.object()
                .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
                .set(SpreadsheetDelta.COLUMN_COUNT_PROPERTY, COLUMN_COUNT_JSON)
                .set(SpreadsheetDelta.ROW_COUNT_PROPERTY, ROW_COUNT_JSON)
        );
    }

    // toString..........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            SpreadsheetDeltaNonWindowed.withNonWindowed(
                this.viewport(),
                this.cells(),
                SpreadsheetDelta.NO_COLUMNS,
                SpreadsheetDelta.NO_FORMS,
                SpreadsheetDelta.NO_LABELS,
                SpreadsheetDelta.NO_ROWS,
                SpreadsheetDelta.NO_REFERENCES,
                SpreadsheetDelta.NO_DELETED_CELLS,
                SpreadsheetDelta.NO_DELETED_COLUMNS,
                SpreadsheetDelta.NO_DELETED_ROWS,
                SpreadsheetDelta.NO_DELETED_LABELS,
                SpreadsheetDelta.NO_MATCHED_CELLS,
                SpreadsheetDelta.NO_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_ROW_HEIGHTS,
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT
            ),
            "home: A1 width: 100.0 height: 40.0 anchoredSelection: A1:B2 BOTTOM_RIGHT cells: A1 1, B2 2, C3 3"
        );
    }

    @Test
    public void testToStringForms() {
        this.toStringAndCheck(
            SpreadsheetDeltaNonWindowed.withNonWindowed(
                this.viewport(),
                this.cells(),
                SpreadsheetDelta.NO_COLUMNS,
                this.forms(),
                SpreadsheetDelta.NO_LABELS,
                SpreadsheetDelta.NO_ROWS,
                SpreadsheetDelta.NO_REFERENCES,
                SpreadsheetDelta.NO_DELETED_CELLS,
                SpreadsheetDelta.NO_DELETED_COLUMNS,
                SpreadsheetDelta.NO_DELETED_ROWS,
                SpreadsheetDelta.NO_DELETED_LABELS,
                SpreadsheetDelta.NO_MATCHED_CELLS,
                SpreadsheetDelta.NO_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_ROW_HEIGHTS,
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT
            ),
            "home: A1 width: 100.0 height: 40.0 anchoredSelection: A1:B2 BOTTOM_RIGHT cells: A1 1, B2 2, C3 3 forms: Form111 fields=A1 \"Label111\" text"
        );
    }

    @Test
    public void testToStringLabels() {
        this.toStringAndCheck(
            SpreadsheetDeltaNonWindowed.withNonWindowed(
                this.viewport(),
                this.cells(),
                SpreadsheetDelta.NO_COLUMNS,
                SpreadsheetDelta.NO_FORMS,
                this.labels(),
                SpreadsheetDelta.NO_ROWS,
                SpreadsheetDelta.NO_REFERENCES,
                SpreadsheetDelta.NO_DELETED_CELLS,
                SpreadsheetDelta.NO_DELETED_COLUMNS,
                SpreadsheetDelta.NO_DELETED_ROWS,
                SpreadsheetDelta.NO_DELETED_LABELS,
                SpreadsheetDelta.NO_MATCHED_CELLS,
                SpreadsheetDelta.NO_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_ROW_HEIGHTS,
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT
            ),
            "home: A1 width: 100.0 height: 40.0 anchoredSelection: A1:B2 BOTTOM_RIGHT cells: A1 1, B2 2, C3 3 labels: LabelA1A=A1, LabelA1B=A1, LabelB2=B2, LabelC3=C3:D4"
        );
    }

    @Test
    public void testToStringReferences() {
        this.toStringAndCheck(
            SpreadsheetDeltaNonWindowed.withNonWindowed(
                this.viewport(),
                SpreadsheetDelta.NO_CELLS,
                SpreadsheetDelta.NO_COLUMNS,
                SpreadsheetDelta.NO_FORMS,
                SpreadsheetDelta.NO_LABELS,
                SpreadsheetDelta.NO_ROWS,
                this.references(),
                SpreadsheetDelta.NO_DELETED_CELLS,
                SpreadsheetDelta.NO_DELETED_COLUMNS,
                SpreadsheetDelta.NO_DELETED_ROWS,
                SpreadsheetDelta.NO_DELETED_LABELS,
                SpreadsheetDelta.NO_MATCHED_CELLS,
                SpreadsheetDelta.NO_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_ROW_HEIGHTS,
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT
            ),
            "home: A1 width: 100.0 height: 40.0 anchoredSelection: A1:B2 BOTTOM_RIGHT references: A1: B2, C3:D4, LabelA1A"
        );
    }

    @Test
    public void testToStringDeletedCells() {
        this.toStringAndCheck(
            SpreadsheetDeltaNonWindowed.withNonWindowed(
                SpreadsheetDelta.NO_VIEWPORT,
                this.cells(),
                SpreadsheetDelta.NO_COLUMNS,
                SpreadsheetDelta.NO_FORMS,
                this.labels(),
                SpreadsheetDelta.NO_ROWS,
                SpreadsheetDelta.NO_REFERENCES,
                this.deletedCells(),
                SpreadsheetDelta.NO_DELETED_COLUMNS,
                SpreadsheetDelta.NO_DELETED_ROWS,
                SpreadsheetDelta.NO_DELETED_LABELS,
                SpreadsheetDelta.NO_MATCHED_CELLS,
                SpreadsheetDelta.NO_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_ROW_HEIGHTS,
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT
            ),
            "cells: A1 1, B2 2, C3 3 labels: LabelA1A=A1, LabelA1B=A1, LabelB2=B2, LabelC3=C3:D4 deletedCells: C1, C2"
        );
    }

    @Test
    public void testToStringDeletedColumns() {
        this.toStringAndCheck(
            SpreadsheetDeltaNonWindowed.withNonWindowed(
                SpreadsheetDelta.NO_VIEWPORT,
                SpreadsheetDelta.NO_CELLS,
                SpreadsheetDelta.NO_COLUMNS,
                SpreadsheetDelta.NO_FORMS,
                SpreadsheetDelta.NO_LABELS,
                SpreadsheetDelta.NO_ROWS,
                SpreadsheetDelta.NO_REFERENCES,
                SpreadsheetDelta.NO_DELETED_CELLS,
                this.deletedColumns(),
                SpreadsheetDelta.NO_DELETED_ROWS,
                SpreadsheetDelta.NO_DELETED_LABELS,
                SpreadsheetDelta.NO_MATCHED_CELLS,
                SpreadsheetDelta.NO_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_ROW_HEIGHTS,
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT
            ),
            "deletedColumns: C, D");
    }

    @Test
    public void testToStringDeletedRows() {
        this.toStringAndCheck(
            SpreadsheetDeltaNonWindowed.withNonWindowed(
                SpreadsheetDelta.NO_VIEWPORT,
                SpreadsheetDelta.NO_CELLS,
                SpreadsheetDelta.NO_COLUMNS,
                SpreadsheetDelta.NO_FORMS,
                SpreadsheetDelta.NO_LABELS,
                SpreadsheetDelta.NO_ROWS,
                SpreadsheetDelta.NO_REFERENCES,
                SpreadsheetDelta.NO_DELETED_CELLS,
                SpreadsheetDelta.NO_DELETED_COLUMNS,
                this.deletedRows(),
                SpreadsheetDelta.NO_DELETED_LABELS,
                SpreadsheetDelta.NO_MATCHED_CELLS,
                SpreadsheetDelta.NO_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_ROW_HEIGHTS,
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT
            ),
            "deletedRows: 3, 4");
    }

    @Test
    public void testToStringDeletedLabels() {
        this.toStringAndCheck(
            SpreadsheetDeltaNonWindowed.withNonWindowed(
                SpreadsheetDelta.NO_VIEWPORT,
                SpreadsheetDelta.NO_CELLS,
                SpreadsheetDelta.NO_COLUMNS,
                SpreadsheetDelta.NO_FORMS,
                SpreadsheetDelta.NO_LABELS,
                SpreadsheetDelta.NO_ROWS,
                SpreadsheetDelta.NO_REFERENCES,
                SpreadsheetDelta.NO_DELETED_CELLS,
                SpreadsheetDelta.NO_DELETED_COLUMNS,
                SpreadsheetDelta.NO_DELETED_ROWS,
                this.deletedLabels(),
                SpreadsheetDelta.NO_MATCHED_CELLS,
                SpreadsheetDelta.NO_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_ROW_HEIGHTS,
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT
            ),
            "deletedLabels: DeletedLabel111, DeletedLabel222");
    }

    @Test
    public void testToStringMatchedCells() {
        this.toStringAndCheck(
            SpreadsheetDeltaNonWindowed.withNonWindowed(
                SpreadsheetDelta.NO_VIEWPORT,
                this.cells(),
                SpreadsheetDelta.NO_COLUMNS,
                SpreadsheetDelta.NO_FORMS,
                this.labels(),
                SpreadsheetDelta.NO_ROWS,
                SpreadsheetDelta.NO_REFERENCES,
                SpreadsheetDelta.NO_DELETED_CELLS,
                SpreadsheetDelta.NO_DELETED_COLUMNS,
                SpreadsheetDelta.NO_DELETED_ROWS,
                SpreadsheetDelta.NO_DELETED_LABELS,
                this.matchedCells(),
                SpreadsheetDelta.NO_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_ROW_HEIGHTS,
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT
            ),
            "cells: A1 1, B2 2, C3 3 labels: LabelA1A=A1, LabelA1B=A1, LabelB2=B2, LabelC3=C3:D4 matchedCells: A1, B2, C3"
        );
    }


    @Test
    public void testToStringColumnWidths() {
        this.toStringAndCheck(
            SpreadsheetDeltaNonWindowed.withNonWindowed(
                SpreadsheetDelta.NO_VIEWPORT,
                this.cells(),
                SpreadsheetDelta.NO_COLUMNS,
                SpreadsheetDelta.NO_FORMS,
                SpreadsheetDelta.NO_LABELS,
                SpreadsheetDelta.NO_ROWS,
                SpreadsheetDelta.NO_REFERENCES,
                SpreadsheetDelta.NO_DELETED_CELLS,
                SpreadsheetDelta.NO_DELETED_COLUMNS,
                SpreadsheetDelta.NO_DELETED_ROWS,
                SpreadsheetDelta.NO_DELETED_LABELS,
                SpreadsheetDelta.NO_MATCHED_CELLS,
                this.columnWidths(),
                SpreadsheetDelta.NO_ROW_HEIGHTS,
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT
            ),
            "cells: A1 1, B2 2, C3 3 max: A=50.0"
        );
    }

    @Test
    public void testToStringRowHeights() {
        this.toStringAndCheck(
            SpreadsheetDeltaNonWindowed.withNonWindowed(
                SpreadsheetDelta.NO_VIEWPORT,
                this.cells(),
                SpreadsheetDelta.NO_COLUMNS,
                SpreadsheetDelta.NO_FORMS,
                SpreadsheetDelta.NO_LABELS,
                SpreadsheetDelta.NO_ROWS,
                SpreadsheetDelta.NO_REFERENCES,
                SpreadsheetDelta.NO_DELETED_CELLS,
                SpreadsheetDelta.NO_DELETED_COLUMNS,
                SpreadsheetDelta.NO_DELETED_ROWS,
                SpreadsheetDelta.NO_DELETED_LABELS,
                SpreadsheetDelta.NO_MATCHED_CELLS,
                SpreadsheetDelta.NO_COLUMN_WIDTHS,
                this.rowHeights(),
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT
            ),
            "cells: A1 1, B2 2, C3 3 max: 1=75.0"
        );
    }

    @Test
    public void testToStringColumnWidthsRowHeights() {
        this.toStringAndCheck(
            SpreadsheetDeltaNonWindowed.withNonWindowed(
                SpreadsheetDelta.NO_VIEWPORT,
                this.cells(),
                SpreadsheetDelta.NO_COLUMNS,
                SpreadsheetDelta.NO_FORMS,
                SpreadsheetDelta.NO_LABELS,
                SpreadsheetDelta.NO_ROWS,
                SpreadsheetDelta.NO_REFERENCES,
                SpreadsheetDelta.NO_DELETED_CELLS,
                SpreadsheetDelta.NO_DELETED_COLUMNS,
                SpreadsheetDelta.NO_DELETED_ROWS,
                SpreadsheetDelta.NO_DELETED_LABELS,
                SpreadsheetDelta.NO_MATCHED_CELLS,
                this.columnWidths(),
                this.rowHeights(),
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT
            ),
            "cells: A1 1, B2 2, C3 3 max: A=50.0, 1=75.0");
    }

    @Test
    public void testToStringRowCountRowCount() {
        this.toStringAndCheck(
            SpreadsheetDeltaNonWindowed.withNonWindowed(
                SpreadsheetDelta.NO_VIEWPORT,
                this.cells(),
                SpreadsheetDelta.NO_COLUMNS,
                SpreadsheetDelta.NO_FORMS,
                SpreadsheetDelta.NO_LABELS,
                SpreadsheetDelta.NO_ROWS,
                SpreadsheetDelta.NO_REFERENCES,
                SpreadsheetDelta.NO_DELETED_CELLS,
                SpreadsheetDelta.NO_DELETED_COLUMNS,
                SpreadsheetDelta.NO_DELETED_ROWS,
                SpreadsheetDelta.NO_DELETED_LABELS,
                SpreadsheetDelta.NO_MATCHED_CELLS,
                SpreadsheetDelta.NO_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_ROW_HEIGHTS,
                this.columnCount(),
                this.rowCount()
            ),
            "cells: A1 1, B2 2, C3 3 columnCount: 88 rowCount: 99"
        );
    }

    // TreePrintable.....................................................................................................

    @Test
    public void testPrintTreeNoCells() {
        this.treePrintAndCheck(
            SpreadsheetDeltaNonWindowed.withNonWindowed(
                SpreadsheetDelta.NO_VIEWPORT,
                SpreadsheetDelta.NO_CELLS,
                SpreadsheetDelta.NO_COLUMNS,
                SpreadsheetDelta.NO_FORMS,
                SpreadsheetDelta.NO_LABELS,
                SpreadsheetDelta.NO_ROWS,
                SpreadsheetDelta.NO_REFERENCES,
                SpreadsheetDelta.NO_DELETED_CELLS,
                SpreadsheetDelta.NO_DELETED_COLUMNS,
                SpreadsheetDelta.NO_DELETED_ROWS,
                SpreadsheetDelta.NO_DELETED_LABELS,
                SpreadsheetDelta.NO_MATCHED_CELLS,
                SpreadsheetDelta.NO_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_ROW_HEIGHTS,
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT
            ),
            "SpreadsheetDelta\n"
        );
    }

    @Test
    public void testPrintTreeViewport() {
        this.treePrintAndCheck(
            SpreadsheetDeltaNonWindowed.withNonWindowed(
                this.viewport(),
                SpreadsheetDelta.NO_CELLS,
                SpreadsheetDelta.NO_COLUMNS,
                SpreadsheetDelta.NO_FORMS,
                SpreadsheetDelta.NO_LABELS,
                SpreadsheetDelta.NO_ROWS,
                SpreadsheetDelta.NO_REFERENCES,
                SpreadsheetDelta.NO_DELETED_CELLS,
                SpreadsheetDelta.NO_DELETED_COLUMNS,
                SpreadsheetDelta.NO_DELETED_ROWS,
                SpreadsheetDelta.NO_DELETED_LABELS,
                SpreadsheetDelta.NO_MATCHED_CELLS,
                SpreadsheetDelta.NO_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_ROW_HEIGHTS,
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT
            ),
            "SpreadsheetDelta\n" +
                "  viewport:\n" +
                "    SpreadsheetViewport\n" +
                "      rectangle:\n" +
                "        SpreadsheetViewportRectangle\n" +
                "          home: A1\n" +
                "          width: 100.0\n" +
                "          height: 40.0\n" +
                "      anchoredSelection:cell-range A1:B2 BOTTOM_RIGHT\n"
        );
    }

    @Test
    public void testPrintTreeCells() {
        this.treePrintAndCheck(
            SpreadsheetDeltaNonWindowed.withNonWindowed(
                SpreadsheetDelta.NO_VIEWPORT,
                this.cells(),
                SpreadsheetDelta.NO_COLUMNS,
                SpreadsheetDelta.NO_FORMS,
                SpreadsheetDelta.NO_LABELS,
                SpreadsheetDelta.NO_ROWS,
                SpreadsheetDelta.NO_REFERENCES,
                SpreadsheetDelta.NO_DELETED_CELLS,
                SpreadsheetDelta.NO_DELETED_COLUMNS,
                SpreadsheetDelta.NO_DELETED_ROWS,
                SpreadsheetDelta.NO_DELETED_LABELS,
                SpreadsheetDelta.NO_MATCHED_CELLS,
                SpreadsheetDelta.NO_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_ROW_HEIGHTS,
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT
            ),
            "SpreadsheetDelta\n" +
                "  cells:\n" +
                "    Cell A1\n" +
                "      Formula\n" +
                "        text:\n" +
                "          \"1\"\n" +
                "    Cell B2\n" +
                "      Formula\n" +
                "        text:\n" +
                "          \"2\"\n" +
                "    Cell C3\n" +
                "      Formula\n" +
                "        text:\n" +
                "          \"3\"\n"
        );
    }

    @Test
    public void testPrintTreeColumns() {
        this.treePrintAndCheck(
            SpreadsheetDeltaNonWindowed.withNonWindowed(
                SpreadsheetDelta.NO_VIEWPORT,
                SpreadsheetDelta.NO_CELLS,
                Sets.of(
                    this.a(),
                    this.hiddenD()
                ),
                SpreadsheetDelta.NO_FORMS,
                SpreadsheetDelta.NO_LABELS,
                SpreadsheetDelta.NO_ROWS,
                SpreadsheetDelta.NO_REFERENCES,
                SpreadsheetDelta.NO_DELETED_CELLS,
                SpreadsheetDelta.NO_DELETED_COLUMNS,
                SpreadsheetDelta.NO_DELETED_ROWS,
                SpreadsheetDelta.NO_DELETED_LABELS,
                SpreadsheetDelta.NO_MATCHED_CELLS,
                SpreadsheetDelta.NO_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_ROW_HEIGHTS,
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT
            ),
            "SpreadsheetDelta\n" +
                "  columns:\n" +
                "    A\n" +
                "    D\n" +
                "      hidden\n"
        );
    }

    @Test
    public void testPrintTreeForms() {
        this.treePrintAndCheck(
            SpreadsheetDeltaNonWindowed.withNonWindowed(
                SpreadsheetDelta.NO_VIEWPORT,
                SpreadsheetDelta.NO_CELLS,
                SpreadsheetDelta.NO_COLUMNS,
                this.forms(),
                SpreadsheetDelta.NO_LABELS,
                SpreadsheetDelta.NO_ROWS,
                SpreadsheetDelta.NO_REFERENCES,
                SpreadsheetDelta.NO_DELETED_CELLS,
                SpreadsheetDelta.NO_DELETED_COLUMNS,
                SpreadsheetDelta.NO_DELETED_ROWS,
                SpreadsheetDelta.NO_DELETED_LABELS,
                SpreadsheetDelta.NO_MATCHED_CELLS,
                SpreadsheetDelta.NO_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_ROW_HEIGHTS,
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT
            ),
            "SpreadsheetDelta\n" +
                "  forms:\n" +
                "    Form\n" +
                "      Form111\n" +
                "      fields:\n" +
                "        FormField\n" +
                "          reference:\n" +
                "            cell A1\n" +
                "          label:\n" +
                "            \"Label111\"\n" +
                "          type:\n" +
                "            text\n"
        );
    }

    @Test
    public void testPrintTreeLabels() {
        this.treePrintAndCheck(
            SpreadsheetDeltaNonWindowed.withNonWindowed(
                SpreadsheetDelta.NO_VIEWPORT,
                SpreadsheetDelta.NO_CELLS,
                SpreadsheetDelta.NO_COLUMNS,
                SpreadsheetDelta.NO_FORMS,
                this.labels(),
                SpreadsheetDelta.NO_ROWS,
                SpreadsheetDelta.NO_REFERENCES,
                SpreadsheetDelta.NO_DELETED_CELLS,
                SpreadsheetDelta.NO_DELETED_COLUMNS,
                SpreadsheetDelta.NO_DELETED_ROWS,
                SpreadsheetDelta.NO_DELETED_LABELS,
                SpreadsheetDelta.NO_MATCHED_CELLS,
                SpreadsheetDelta.NO_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_ROW_HEIGHTS,
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT
            ),
            "SpreadsheetDelta\n" +
                "  labels:\n" +
                "    LabelA1A: A1\n" +
                "    LabelA1B: A1\n" +
                "    LabelB2: B2\n" +
                "    LabelC3: C3:D4\n"
        );
    }

    @Test
    public void testPrintTreeRows() {
        this.treePrintAndCheck(
            SpreadsheetDeltaNonWindowed.withNonWindowed(
                SpreadsheetDelta.NO_VIEWPORT,
                SpreadsheetDelta.NO_CELLS,
                SpreadsheetDelta.NO_COLUMNS,
                SpreadsheetDelta.NO_FORMS,
                SpreadsheetDelta.NO_LABELS,
                Sets.of(
                    this.row1(),
                    this.hiddenRow4()
                ),
                SpreadsheetDelta.NO_REFERENCES,
                SpreadsheetDelta.NO_DELETED_CELLS,
                SpreadsheetDelta.NO_DELETED_COLUMNS,
                SpreadsheetDelta.NO_DELETED_ROWS,
                SpreadsheetDelta.NO_DELETED_LABELS,
                SpreadsheetDelta.NO_MATCHED_CELLS,
                SpreadsheetDelta.NO_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_ROW_HEIGHTS,
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT
            ),
            "SpreadsheetDelta\n" +
                "  rows:\n" +
                "    1\n" +
                "    4\n" +
                "      hidden\n"
        );
    }

    @Test
    public void testPrintTreeCellsAndLabels() {
        this.treePrintAndCheck(
            SpreadsheetDeltaNonWindowed.withNonWindowed(
                SpreadsheetDelta.NO_VIEWPORT,
                this.cells(),
                SpreadsheetDelta.NO_COLUMNS,
                SpreadsheetDelta.NO_FORMS,
                this.labels(),
                SpreadsheetDelta.NO_ROWS,
                SpreadsheetDelta.NO_REFERENCES,
                SpreadsheetDelta.NO_DELETED_CELLS,
                SpreadsheetDelta.NO_DELETED_COLUMNS,
                SpreadsheetDelta.NO_DELETED_ROWS,
                SpreadsheetDelta.NO_DELETED_LABELS,
                SpreadsheetDelta.NO_MATCHED_CELLS,
                SpreadsheetDelta.NO_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_ROW_HEIGHTS,
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT
            ),
            "SpreadsheetDelta\n" +
                "  cells:\n" +
                "    Cell A1\n" +
                "      Formula\n" +
                "        text:\n" +
                "          \"1\"\n" +
                "    Cell B2\n" +
                "      Formula\n" +
                "        text:\n" +
                "          \"2\"\n" +
                "    Cell C3\n" +
                "      Formula\n" +
                "        text:\n" +
                "          \"3\"\n" +
                "  labels:\n" +
                "    LabelA1A: A1\n" +
                "    LabelA1B: A1\n" +
                "    LabelB2: B2\n" +
                "    LabelC3: C3:D4\n"
        );
    }

    @Test
    public void testPrintTreeReferences() {
        this.treePrintAndCheck(
            SpreadsheetDeltaNonWindowed.withNonWindowed(
                SpreadsheetDelta.NO_VIEWPORT,
                SpreadsheetDelta.NO_CELLS,
                SpreadsheetDelta.NO_COLUMNS,
                SpreadsheetDelta.NO_FORMS,
                SpreadsheetDelta.NO_LABELS,
                SpreadsheetDelta.NO_ROWS,
                this.references(),
                SpreadsheetDelta.NO_DELETED_CELLS,
                SpreadsheetDelta.NO_DELETED_COLUMNS,
                SpreadsheetDelta.NO_DELETED_ROWS,
                SpreadsheetDelta.NO_DELETED_LABELS,
                SpreadsheetDelta.NO_MATCHED_CELLS,
                SpreadsheetDelta.NO_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_ROW_HEIGHTS,
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT
            ),
            "SpreadsheetDelta\n" +
                "  references:\n" +
                "    A1:\n" +
                "      B2,C3:D4,LabelA1A\n"
        );
    }

    @Test
    public void testPrintTreeDeletedCells() {
        this.treePrintAndCheck(
            SpreadsheetDeltaNonWindowed.withNonWindowed(
                SpreadsheetDelta.NO_VIEWPORT,
                this.cells(),
                SpreadsheetDelta.NO_COLUMNS,
                SpreadsheetDelta.NO_FORMS,
                this.labels(),
                SpreadsheetDelta.NO_ROWS,
                SpreadsheetDelta.NO_REFERENCES,
                this.deletedCells(),
                SpreadsheetDelta.NO_DELETED_COLUMNS,
                SpreadsheetDelta.NO_DELETED_ROWS,
                SpreadsheetDelta.NO_DELETED_LABELS,
                SpreadsheetDelta.NO_MATCHED_CELLS,
                SpreadsheetDelta.NO_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_ROW_HEIGHTS,
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT
            ),
            "SpreadsheetDelta\n" +
                "  cells:\n" +
                "    Cell A1\n" +
                "      Formula\n" +
                "        text:\n" +
                "          \"1\"\n" +
                "    Cell B2\n" +
                "      Formula\n" +
                "        text:\n" +
                "          \"2\"\n" +
                "    Cell C3\n" +
                "      Formula\n" +
                "        text:\n" +
                "          \"3\"\n" +
                "  labels:\n" +
                "    LabelA1A: A1\n" +
                "    LabelA1B: A1\n" +
                "    LabelB2: B2\n" +
                "    LabelC3: C3:D4\n" +
                "  deletedCells:\n" +
                "    C1,C2\n"
        );
    }

    @Test
    public void testPrintTreeDeletedColumns() {
        this.treePrintAndCheck(
            SpreadsheetDeltaNonWindowed.withNonWindowed(
                SpreadsheetDelta.NO_VIEWPORT,
                SpreadsheetDelta.NO_CELLS,
                SpreadsheetDelta.NO_COLUMNS,
                SpreadsheetDelta.NO_FORMS,
                SpreadsheetDelta.NO_LABELS,
                SpreadsheetDelta.NO_ROWS,
                SpreadsheetDelta.NO_REFERENCES,
                SpreadsheetDelta.NO_DELETED_CELLS,
                this.deletedColumns(),
                SpreadsheetDelta.NO_DELETED_ROWS,
                SpreadsheetDelta.NO_DELETED_LABELS,
                SpreadsheetDelta.NO_MATCHED_CELLS,
                SpreadsheetDelta.NO_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_ROW_HEIGHTS,
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT
            ),
            "SpreadsheetDelta\n" +
                "  deletedColumns:\n" +
                "    C,D\n"
        );
    }

    @Test
    public void testPrintTreeDeletedRows() {
        this.treePrintAndCheck(
            SpreadsheetDeltaNonWindowed.withNonWindowed(
                SpreadsheetDelta.NO_VIEWPORT,
                SpreadsheetDelta.NO_CELLS,
                SpreadsheetDelta.NO_COLUMNS,
                SpreadsheetDelta.NO_FORMS,
                SpreadsheetDelta.NO_LABELS,
                SpreadsheetDelta.NO_ROWS,
                SpreadsheetDelta.NO_REFERENCES,
                SpreadsheetDelta.NO_DELETED_CELLS,
                SpreadsheetDelta.NO_DELETED_COLUMNS,
                this.deletedRows(),
                SpreadsheetDelta.NO_DELETED_LABELS,
                SpreadsheetDelta.NO_MATCHED_CELLS,
                SpreadsheetDelta.NO_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_ROW_HEIGHTS,
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT
            ),
            "SpreadsheetDelta\n" +
                "  deletedRows:\n" +
                "    3,4\n"
        );
    }

    @Test
    public void testPrintTreeDeletedLabels() {
        this.treePrintAndCheck(
            SpreadsheetDeltaNonWindowed.withNonWindowed(
                SpreadsheetDelta.NO_VIEWPORT,
                SpreadsheetDelta.NO_CELLS,
                SpreadsheetDelta.NO_COLUMNS,
                SpreadsheetDelta.NO_FORMS,
                SpreadsheetDelta.NO_LABELS,
                SpreadsheetDelta.NO_ROWS,
                SpreadsheetDelta.NO_REFERENCES,
                SpreadsheetDelta.NO_DELETED_CELLS,
                SpreadsheetDelta.NO_DELETED_COLUMNS,
                SpreadsheetDelta.NO_DELETED_ROWS,
                this.deletedLabels(),
                SpreadsheetDelta.NO_MATCHED_CELLS,
                SpreadsheetDelta.NO_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_ROW_HEIGHTS,
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT
            ),
            "SpreadsheetDelta\n" +
                "  deletedLabels:\n" +
                "    DeletedLabel111,DeletedLabel222\n"
        );
    }

    @Test
    public void testPrintTreeMatchedCells() {
        this.treePrintAndCheck(
            SpreadsheetDeltaNonWindowed.withNonWindowed(
                SpreadsheetDelta.NO_VIEWPORT,
                this.cells(),
                SpreadsheetDelta.NO_COLUMNS,
                SpreadsheetDelta.NO_FORMS,
                this.labels(),
                SpreadsheetDelta.NO_ROWS,
                SpreadsheetDelta.NO_REFERENCES,
                SpreadsheetDelta.NO_DELETED_CELLS,
                SpreadsheetDelta.NO_DELETED_COLUMNS,
                SpreadsheetDelta.NO_DELETED_ROWS,
                SpreadsheetDelta.NO_DELETED_LABELS,
                this.matchedCells(),
                SpreadsheetDelta.NO_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_ROW_HEIGHTS,
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT
            ),
            "SpreadsheetDelta\n" +
                "  cells:\n" +
                "    Cell A1\n" +
                "      Formula\n" +
                "        text:\n" +
                "          \"1\"\n" +
                "    Cell B2\n" +
                "      Formula\n" +
                "        text:\n" +
                "          \"2\"\n" +
                "    Cell C3\n" +
                "      Formula\n" +
                "        text:\n" +
                "          \"3\"\n" +
                "  labels:\n" +
                "    LabelA1A: A1\n" +
                "    LabelA1B: A1\n" +
                "    LabelB2: B2\n" +
                "    LabelC3: C3:D4\n" +
                "  matchedCells:\n" +
                "    A1,B2,C3\n"
        );
    }

    @Test
    public void testPrintTreeColumnWidths() {
        this.treePrintAndCheck(
            SpreadsheetDeltaNonWindowed.withNonWindowed(
                SpreadsheetDelta.NO_VIEWPORT,
                this.cells(),
                SpreadsheetDelta.NO_COLUMNS,
                SpreadsheetDelta.NO_FORMS,
                SpreadsheetDelta.NO_LABELS,
                SpreadsheetDelta.NO_ROWS,
                SpreadsheetDelta.NO_REFERENCES,
                SpreadsheetDelta.NO_DELETED_CELLS,
                SpreadsheetDelta.NO_DELETED_COLUMNS,
                SpreadsheetDelta.NO_DELETED_ROWS,
                SpreadsheetDelta.NO_DELETED_LABELS,
                SpreadsheetDelta.NO_MATCHED_CELLS,
                this.columnWidths(),
                SpreadsheetDelta.NO_ROW_HEIGHTS,
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT
            ),
            "SpreadsheetDelta\n" +
                "  cells:\n" +
                "    Cell A1\n" +
                "      Formula\n" +
                "        text:\n" +
                "          \"1\"\n" +
                "    Cell B2\n" +
                "      Formula\n" +
                "        text:\n" +
                "          \"2\"\n" +
                "    Cell C3\n" +
                "      Formula\n" +
                "        text:\n" +
                "          \"3\"\n" +
                "  columnWidths:\n" +
                "    A: 50.0\n"
        );
    }

    @Test
    public void testPrintTreeRowHeights() {
        this.treePrintAndCheck(
            SpreadsheetDeltaNonWindowed.withNonWindowed(
                SpreadsheetDelta.NO_VIEWPORT,
                this.cells(),
                SpreadsheetDelta.NO_COLUMNS,
                SpreadsheetDelta.NO_FORMS,
                SpreadsheetDelta.NO_LABELS,
                SpreadsheetDelta.NO_ROWS,
                SpreadsheetDelta.NO_REFERENCES,
                SpreadsheetDelta.NO_DELETED_CELLS,
                SpreadsheetDelta.NO_DELETED_COLUMNS,
                SpreadsheetDelta.NO_DELETED_ROWS,
                SpreadsheetDelta.NO_DELETED_LABELS,
                SpreadsheetDelta.NO_MATCHED_CELLS,
                SpreadsheetDelta.NO_COLUMN_WIDTHS,
                this.rowHeights(),
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT
            ),
            "SpreadsheetDelta\n" +
                "  cells:\n" +
                "    Cell A1\n" +
                "      Formula\n" +
                "        text:\n" +
                "          \"1\"\n" +
                "    Cell B2\n" +
                "      Formula\n" +
                "        text:\n" +
                "          \"2\"\n" +
                "    Cell C3\n" +
                "      Formula\n" +
                "        text:\n" +
                "          \"3\"\n" +
                "  rowHeights:\n" +
                "    1: 75.0\n"
        );
    }

    @Test
    public void testPrintTreeNothingEmpty() {
        this.treePrintAndCheck(
            SpreadsheetDeltaNonWindowed.withNonWindowed(
                this.viewport(),
                this.cells(),
                SpreadsheetDelta.NO_COLUMNS,
                this.forms(),
                this.labels(),
                SpreadsheetDelta.NO_ROWS,
                SpreadsheetDelta.NO_REFERENCES,
                this.deletedCells(),
                this.deletedColumns(),
                this.deletedRows(),
                this.deletedLabels(),
                this.matchedCells(),
                this.columnWidths(),
                this.rowHeights(),
                this.columnCount(),
                this.rowCount()
            ),
            "SpreadsheetDelta\n" +
                "  viewport:\n" +
                "    SpreadsheetViewport\n" +
                "      rectangle:\n" +
                "        SpreadsheetViewportRectangle\n" +
                "          home: A1\n" +
                "          width: 100.0\n" +
                "          height: 40.0\n" +
                "      anchoredSelection:cell-range A1:B2 BOTTOM_RIGHT\n" +
                "  cells:\n" +
                "    Cell A1\n" +
                "      Formula\n" +
                "        text:\n" +
                "          \"1\"\n" +
                "    Cell B2\n" +
                "      Formula\n" +
                "        text:\n" +
                "          \"2\"\n" +
                "    Cell C3\n" +
                "      Formula\n" +
                "        text:\n" +
                "          \"3\"\n" +
                "  forms:\n" +
                "    Form\n" +
                "      Form111\n" +
                "      fields:\n" +
                "        FormField\n" +
                "          reference:\n" +
                "            cell A1\n" +
                "          label:\n" +
                "            \"Label111\"\n" +
                "          type:\n" +
                "            text\n" +
                "  labels:\n" +
                "    LabelA1A: A1\n" +
                "    LabelA1B: A1\n" +
                "    LabelB2: B2\n" +
                "    LabelC3: C3:D4\n" +
                "  deletedCells:\n" +
                "    C1,C2\n" +
                "  deletedColumns:\n" +
                "    C,D\n" +
                "  deletedRows:\n" +
                "    3,4\n" +
                "  deletedLabels:\n" +
                "    DeletedLabel111,DeletedLabel222\n" +
                "  matchedCells:\n" +
                "    A1,B2,C3\n" +
                "  columnWidths:\n" +
                "    A: 50.0\n" +
                "  rowHeights:\n" +
                "    1: 75.0\n" +
                "  columnCount: 88\n" +
                "  rowCount: 99\n"
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetDeltaNonWindowed> type() {
        return SpreadsheetDeltaNonWindowed.class;
    }
}

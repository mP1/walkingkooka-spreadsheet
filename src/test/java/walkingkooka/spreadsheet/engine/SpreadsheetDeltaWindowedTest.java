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
import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.spreadsheet.SpreadsheetRow;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReferenceSet;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReferenceSet;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameSet;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReferenceSet;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewport;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewportWindows;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonString;
import walkingkooka.validation.form.Form;

import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotSame;

public final class SpreadsheetDeltaWindowedTest extends SpreadsheetDeltaTestCase<SpreadsheetDeltaWindowed> {

    public SpreadsheetDeltaWindowedTest() {
        super();
        this.checkNotEquals(
            this.window(),
            this.differentWindow(),
            "window v differentWindow must NOT be equal"
        );
    }

    @Test
    public void testSetLabelsMissingCellKept() {
        final SpreadsheetDeltaWindowed before = this.createSpreadsheetDelta();
        final Set<SpreadsheetLabelMapping> different = Sets.of(
            SpreadsheetLabelName.labelName("Different")
                .setLabelMappingReference(SpreadsheetSelection.parseCell("A3"))
        );

        final SpreadsheetDelta after = before.setLabels(different);
        assertNotSame(before, after);
        this.labelsAndCheck(after, different);
        this.cellsAndCheck(after, before.cells());
    }

    @Test
    public void testSetLabelsOutsideWindowFiltered() {
        final SpreadsheetDeltaWindowed before = this.createSpreadsheetDelta();
        final Set<SpreadsheetLabelMapping> different = Sets.of(
            SpreadsheetLabelName.labelName("Different").setLabelMappingReference(SpreadsheetSelection.parseCell("Z99"))
        );

        final SpreadsheetDelta after = before.setLabels(different);
        assertNotSame(before, after);
        this.labelsAndCheck(after, SpreadsheetDelta.NO_LABELS);
        this.cellsAndCheck(after, before.cells());
    }

    @Test
    public void testSetLabelsOutsideWindowFiltered2() {
        final SpreadsheetDeltaWindowed before = this.createSpreadsheetDelta();

        final SpreadsheetCellReference a1 = this.a1().reference();
        final SpreadsheetLabelName kept = SpreadsheetLabelName.labelName("Kept");

        final SpreadsheetCellReference a3 = SpreadsheetSelection.parseCell("A3");
        final SpreadsheetLabelName kept3 = SpreadsheetLabelName.labelName("Kept2");

        final Set<SpreadsheetLabelMapping> different = Sets.of(
            kept.setLabelMappingReference(a1),
            kept3.setLabelMappingReference(a3),
            SpreadsheetLabelName.labelName("Lost").setLabelMappingReference(SpreadsheetSelection.parseCell("Z99"))
        );

        final SpreadsheetDelta after = before.setLabels(different);
        assertNotSame(before, after);
        this.labelsAndCheck(after, Sets.of(kept.setLabelMappingReference(a1), kept3.setLabelMappingReference(a3)));
        this.cellsAndCheck(after, before.cells());
    }

    // setColumnWidths..................................................................................................

    @Test
    public void testSetColumnWidthsOutsideWindowFiltered() {
        final SpreadsheetDeltaWindowed before = this.createSpreadsheetDelta();

        final Map<SpreadsheetColumnReference, Double> different = Map.of(
            SpreadsheetSelection.parseColumn("F"), 30.0
        );

        final SpreadsheetDelta after = before.setColumnWidths(different);
        assertNotSame(before, after);
        this.columnWidthsAndCheck(after, SpreadsheetDelta.NO_COLUMN_WIDTHS);
        this.cellsAndCheck(after, before.cells());
    }

    @Test
    public void testSetColumnWidthsOutsideWindowFiltered2() {
        final SpreadsheetDeltaWindowed before = this.createSpreadsheetDelta();

        final SpreadsheetColumnReference kept = SpreadsheetSelection.parseColumn("B");
        final Map<SpreadsheetColumnReference, Double> different = Map.of(
            kept, 20.0,
            SpreadsheetSelection.parseColumn("F"), 30.0
        );

        final SpreadsheetDelta after = before.setColumnWidths(different);
        assertNotSame(before, after);
        this.columnWidthsAndCheck(
            after,
            Map.of(kept, 20.0)
        );
        this.cellsAndCheck(after, before.cells());
    }

    // setRowHeights..................................................................................................

    @Test
    public void testSetRowHeightsOutsideWindowFiltered() {
        final SpreadsheetDeltaWindowed before = this.createSpreadsheetDelta();

        final Map<SpreadsheetRowReference, Double> different = Map.of(
            SpreadsheetSelection.parseRow("6"), 30.0
        );

        final SpreadsheetDelta after = before.setRowHeights(different);
        assertNotSame(before, after);
        this.rowHeightsAndCheck(after, SpreadsheetDelta.NO_ROW_HEIGHTS);
        this.cellsAndCheck(after, before.cells());
    }

    @Test
    public void testSetRowHeightsOutsideWindowFiltered2() {
        final SpreadsheetDeltaWindowed before = this.createSpreadsheetDelta();

        final SpreadsheetRowReference kept = SpreadsheetSelection.parseRow("2");
        final Map<SpreadsheetRowReference, Double> different = Map.of(
            kept, 20.0,
            SpreadsheetSelection.parseRow("6"), 30.0
        );

        final SpreadsheetDelta after = before.setRowHeights(different);
        assertNotSame(before, after);
        this.rowHeightsAndCheck(
            after,
            Map.of(kept, 20.0)
        );
        this.cellsAndCheck(after, before.cells());
    }

    // setWindow........................................................................................................

    @Test
    public void testSetWindowMultiple() {
        final Optional<SpreadsheetViewport> viewport = this.viewport();

        final SpreadsheetCell a1 = this.a1();
        final SpreadsheetCell b2 = this.b2();
        final Set<SpreadsheetCell> cells = Sets.of(
            a1,
            b2,
            this.c3()
        );
        final SpreadsheetColumn a = this.a();
        final SpreadsheetColumn b = this.b();
        final SpreadsheetColumn c = this.c();
        final Set<SpreadsheetColumn> columns = Sets.of(
            a,
            b,
            c
        );

        final Set<Form<SpreadsheetExpressionReference>> forms = this.forms();

        final SpreadsheetLabelMapping labelA1a = this.label1a().setLabelMappingReference(a1.reference());
        final SpreadsheetLabelMapping label2b = SpreadsheetSelection.labelName("label2b").setLabelMappingReference(b2.reference());

        final Set<SpreadsheetLabelMapping> labels = Sets.of(
            labelA1a,
            label2b
        );

        final SpreadsheetRow row1 = this.row1();
        final SpreadsheetRow row2 = this.row2();
        final Set<SpreadsheetRow> rows = Sets.of(
            row1,
            row2,
            this.row3()
        );


        final SpreadsheetCellReference d4 = SpreadsheetSelection.parseCell("d4");
        final SpreadsheetCellReference e5 = SpreadsheetSelection.parseCell("e5");
        final SpreadsheetCellReferenceSet deletedCells = SpreadsheetCellReferenceSet.parse(
            d4 + "," + e5
        );

        final SpreadsheetColumnReference e = SpreadsheetSelection.parseColumn("f");
        final SpreadsheetColumnReferenceSet deletedColumns = SpreadsheetColumnReferenceSet.parse(
            e.toString()
        );

        final SpreadsheetRowReference row5 = SpreadsheetSelection.parseRow("5");
        final SpreadsheetRowReferenceSet deletedRows = SpreadsheetRowReferenceSet.parse(
            row5.text()
        );

        final Map<SpreadsheetCellReference, Set<SpreadsheetExpressionReference>> references = this.references();

        final SpreadsheetLabelNameSet deletedLabels = SpreadsheetLabelNameSet.parse("DeletedLabel111,DeletedLabel222");

        final SpreadsheetCellReferenceSet matchedCells = SpreadsheetCellReferenceSet.parse("Z99");

        final Map<SpreadsheetColumnReference, Double> columnWidths = Maps.of(
            a.reference(), 10.0,
            b.reference(), 20.0,
            c.reference(), 30.0,
            e, 60.0
        );
        final Map<SpreadsheetRowReference, Double> rowHeights = Maps.of(
            this.row1().reference(), 10.0,
            this.row2().reference(), 20.0,
            this.row3().reference(), 30.0,
            row5, 60.0
        );

        final OptionalInt columnCount = OptionalInt.of(88);
        final OptionalInt rowCount = OptionalInt.of(88);

        final SpreadsheetViewportWindows window = SpreadsheetViewportWindows.EMPTY;

        final SpreadsheetDeltaWindowed before = SpreadsheetDeltaWindowed.withWindowed(
            viewport,
            cells,
            columns,
            forms,
            labels,
            rows,
            references,
            deletedCells,
            deletedColumns,
            deletedRows,
            deletedLabels,
            matchedCells,
            columnWidths,
            rowHeights,
            columnCount,
            rowCount,
            window
        );

        this.viewportAndCheck(before, viewport);

        this.cellsAndCheck(before, cells);
        this.columnsAndCheck(before, columns);
        this.labelsAndCheck(before, labels);
        this.rowsAndCheck(before, rows);

        this.referencesAndCheck(before, references);

        this.deletedCellsAndCheck(before, deletedCells);
        this.deletedColumnsAndCheck(before, deletedColumns);
        this.deletedRowsAndCheck(before, deletedRows);

        this.matchedCellsAndCheck(before, matchedCells);

        this.columnCountAndCheck(before, columnCount);
        this.rowCountAndCheck(before, rowCount);

        this.windowAndCheck(before, window);

        final SpreadsheetViewportWindows window2 = SpreadsheetViewportWindows.parse("a1,e5:f6");

        final SpreadsheetDelta after = before.setWindow(window2);

        this.cellsAndCheck(after, Sets.of(a1));
        this.columnsAndCheck(after, Sets.of(a));
        this.labelsAndCheck(after, Sets.of(labelA1a));
        this.rowsAndCheck(after, Sets.of(row1));

        this.referencesAndCheck(after, references);

        this.deletedCellsAndCheck(after, Sets.of(e5));
        this.deletedColumnsAndCheck(after, Sets.of(e));
        this.deletedRowsAndCheck(after, Sets.of(row5));

        this.columnCountAndCheck(after, columnCount);
        this.rowCountAndCheck(after, rowCount);

        this.windowAndCheck(after, window2);
    }

    // helpers..........................................................................................................

    @Override
    SpreadsheetDeltaWindowed createSpreadsheetDelta(final Set<SpreadsheetCell> cells) {
        return this.createSpreadsheetDelta(
            cells,
            this.window()
        );
    }

    private SpreadsheetDeltaWindowed createSpreadsheetDelta(final Set<SpreadsheetCell> cells,
                                                            final SpreadsheetViewportWindows window) {
        return SpreadsheetDeltaWindowed.withWindowed(
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
            this.rowCount(),
            window
        );
    }

    @Override
    SpreadsheetViewportWindows window() {
        return SpreadsheetViewportWindows.parse("A1:E5");
    }

    // toString..........................................................................................................

    @Test
    public void testToStringViewport() {
        this.toStringAndCheck(
            SpreadsheetDeltaWindowed.withWindowed(
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
                SpreadsheetDelta.NO_TOTAL_HEIGHT,
                this.window()
            ),
            "home: A1 width: 100.0 height: 40.0 anchoredSelection: A1:B2 BOTTOM_RIGHT window: A1:E5");
    }

    @Test
    public void testToStringCells() {
        this.toStringAndCheck(
            SpreadsheetDeltaWindowed.withWindowed(
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
                SpreadsheetDelta.NO_TOTAL_HEIGHT,
                this.window()
            ),
            "cells: A1 \"1\", B2 \"2\", C3 \"3\" window: A1:E5"
        );
    }

    @Test
    public void testToStringLabels() {
        this.toStringAndCheck(
            SpreadsheetDeltaWindowed.withWindowed(
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
                SpreadsheetDelta.NO_TOTAL_HEIGHT,
                this.window()
            ),
            "cells: A1 \"1\", B2 \"2\", C3 \"3\" labels: LabelA1A=A1, LabelA1B=A1, LabelB2=B2, LabelC3=C3:D4 window: A1:E5"
        );
    }

    @Test
    public void testToStringDeletedCells() {
        this.toStringAndCheck(
            SpreadsheetDeltaWindowed.withWindowed(
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
                SpreadsheetDelta.NO_TOTAL_HEIGHT,
                this.window()
            ),
            "cells: A1 \"1\", B2 \"2\", C3 \"3\" labels: LabelA1A=A1, LabelA1B=A1, LabelB2=B2, LabelC3=C3:D4 deletedCells: C1, C2 window: A1:E5"
        );
    }

    @Test
    public void testToStringDeletedColumns() {
        this.toStringAndCheck(
            SpreadsheetDeltaWindowed.withWindowed(
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
                SpreadsheetDelta.NO_TOTAL_HEIGHT,
                this.window()
            ),
            "deletedColumns: C, D window: A1:E5");
    }

    @Test
    public void testToStringDeletedRows() {
        this.toStringAndCheck(
            SpreadsheetDeltaWindowed.withWindowed(
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
                SpreadsheetDelta.NO_TOTAL_HEIGHT,
                this.window()
            ),
            "deletedRows: 3, 4 window: A1:E5");
    }

    @Test
    public void testToStringDeletedLabels() {
        this.toStringAndCheck(
            SpreadsheetDeltaWindowed.withWindowed(
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
                SpreadsheetDelta.NO_TOTAL_HEIGHT,
                this.window()
            ),
            "deletedLabels: DeletedLabel111, DeletedLabel222 window: A1:E5");
    }

    @Test
    public void testToStringMatchedCells() {
        this.toStringAndCheck(
            SpreadsheetDeltaWindowed.withWindowed(
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
                this.matchedCells(),
                SpreadsheetDelta.NO_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_ROW_HEIGHTS,
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT,
                this.window()
            ),
            "matchedCells: A1, B2, C3 window: A1:E5");
    }

    @Test
    public void testToStringColumnWidths() {
        this.toStringAndCheck(
            SpreadsheetDeltaWindowed.withWindowed(
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
                SpreadsheetDelta.NO_TOTAL_HEIGHT,
                this.window()
            ),
            "cells: A1 \"1\", B2 \"2\", C3 \"3\" max: A=50.0 window: A1:E5"
        );
    }

    @Test
    public void testToStringRowCountHeights() {
        this.toStringAndCheck(
            SpreadsheetDeltaWindowed.withWindowed(
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
                SpreadsheetDelta.NO_TOTAL_HEIGHT,
                this.window()
            ),
            "cells: A1 \"1\", B2 \"2\", C3 \"3\" max: 1=75.0 window: A1:E5"
        );
    }

    @Test
    public void testToStringColumnWidthsRowHeights() {
        this.toStringAndCheck(
            SpreadsheetDeltaWindowed.withWindowed(
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
                SpreadsheetDelta.NO_TOTAL_HEIGHT,
                this.window()
            ),
            "cells: A1 \"1\", B2 \"2\", C3 \"3\" max: A=50.0, 1=75.0 window: A1:E5"
        );
    }

    @Test
    public void testToStringColumnCountRowCount() {
        this.toStringAndCheck(
            SpreadsheetDeltaWindowed.withWindowed(
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
                this.rowCount(),
                this.window()
            ),
            "cells: A1 \"1\", B2 \"2\", C3 \"3\" columnCount: 88 rowCount: 99 window: A1:E5"
        );
    }

    // TreePrintable.....................................................................................................

    @Test
    public void testPrintTreeEmptyOnlyWindow() {
        this.treePrintAndCheck(
            SpreadsheetDeltaWindowed.withWindowed(
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
                SpreadsheetDelta.NO_TOTAL_HEIGHT,
                this.window()
            ),
            "SpreadsheetDelta\n" +
                "  window:\n" +
                "    A1:E5\n"
        );
    }

    @Test
    public void testPrintTreeOnlyViewport() {
        this.treePrintAndCheck(
            SpreadsheetDeltaWindowed.withWindowed(
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
                SpreadsheetDelta.NO_TOTAL_HEIGHT,
                this.window()
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
                "  window:\n" +
                "    A1:E5\n"
        );
    }

    @Test
    public void testPrintTreeCells() {
        this.treePrintAndCheck(
            SpreadsheetDeltaWindowed.withWindowed(
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
                SpreadsheetDelta.NO_TOTAL_HEIGHT,
                this.window()
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
                "  window:\n" +
                "    A1:E5\n"
        );
    }

    @Test
    public void testPrintTreeColumns() {
        this.treePrintAndCheck(
            SpreadsheetDeltaWindowed.withWindowed(
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
                SpreadsheetDelta.NO_TOTAL_HEIGHT,
                this.window()
            ),
            "SpreadsheetDelta\n" +
                "  columns:\n" +
                "    A\n" +
                "    D\n" +
                "      hidden\n" +
                "  window:\n" +
                "    A1:E5\n"
        );
    }

    @Test
    public void testPrintTreeForms() {
        this.treePrintAndCheck(
            SpreadsheetDeltaWindowed.withWindowed(
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
                SpreadsheetDelta.NO_TOTAL_HEIGHT,
                this.window()
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
                "            text\n" +
                "  window:\n" +
                "    A1:E5\n"
        );
    }

    @Test
    public void testPrintTreeLabels() {
        this.treePrintAndCheck(
            SpreadsheetDeltaWindowed.withWindowed(
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
                SpreadsheetDelta.NO_TOTAL_HEIGHT,
                this.window()
            ),
            "SpreadsheetDelta\n" +
                "  labels:\n" +
                "    LabelA1A: A1\n" +
                "    LabelA1B: A1\n" +
                "    LabelB2: B2\n" +
                "    LabelC3: C3:D4\n" +
                "  window:\n" +
                "    A1:E5\n"
        );
    }

    @Test
    public void testPrintTreeRows() {
        this.treePrintAndCheck(
            SpreadsheetDeltaWindowed.withWindowed(
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
                SpreadsheetDelta.NO_TOTAL_HEIGHT,
                this.window()
            ),
            "SpreadsheetDelta\n" +
                "  rows:\n" +
                "    1\n" +
                "    4\n" +
                "      hidden\n" +
                "  window:\n" +
                "    A1:E5\n"
        );
    }

    @Test
    public void testPrintTreeCellsAndLabels() {
        this.treePrintAndCheck(
            SpreadsheetDeltaWindowed.withWindowed(
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
                SpreadsheetDelta.NO_TOTAL_HEIGHT,
                this.window()
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
                "  window:\n" +
                "    A1:E5\n"
        );
    }

    @Test
    public void testPrintTreeReferences() {
        this.treePrintAndCheck(
            SpreadsheetDeltaWindowed.withWindowed(
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
                SpreadsheetDelta.NO_TOTAL_HEIGHT,
                this.window()
            ),
            "SpreadsheetDelta\n" +
                "  references:\n" +
                "    A1:\n" +
                "      B2,C3:D4,LabelA1A\n" +
                "  window:\n" +
                "    A1:E5\n"
        );
    }

    @Test
    public void testPrintTreeDeletedCells() {
        this.treePrintAndCheck(
            SpreadsheetDeltaWindowed.withWindowed(
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
                SpreadsheetDelta.NO_TOTAL_HEIGHT,
                this.window()
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
                "    C1,C2\n" +
                "  window:\n" +
                "    A1:E5\n"
        );
    }

    @Test
    public void testPrintTreeDeletedColumns() {
        this.treePrintAndCheck(
            SpreadsheetDeltaWindowed.withWindowed(
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
                SpreadsheetDelta.NO_TOTAL_HEIGHT,
                this.window()
            ),
            "SpreadsheetDelta\n" +
                "  deletedColumns:\n" +
                "    C,D\n" +
                "  window:\n" +
                "    A1:E5\n"
        );
    }

    @Test
    public void testPrintTreeDeletedRows() {
        this.treePrintAndCheck(
            SpreadsheetDeltaWindowed.withWindowed(
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
                SpreadsheetDelta.NO_TOTAL_HEIGHT,
                this.window()
            ),
            "SpreadsheetDelta\n" +
                "  deletedRows:\n" +
                "    3,4\n" +
                "  window:\n" +
                "    A1:E5\n"
        );
    }

    @Test
    public void testPrintTreeDeletedLabels() {
        this.treePrintAndCheck(
            SpreadsheetDeltaWindowed.withWindowed(
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
                SpreadsheetLabelNameSet.parse("DeletedLabel111,DeletedLabel222"),
                SpreadsheetDelta.NO_MATCHED_CELLS,
                SpreadsheetDelta.NO_COLUMN_WIDTHS,
                SpreadsheetDelta.NO_ROW_HEIGHTS,
                SpreadsheetDelta.NO_TOTAL_WIDTH,
                SpreadsheetDelta.NO_TOTAL_HEIGHT,
                this.window()
            ),
            "SpreadsheetDelta\n" +
                "  deletedLabels:\n" +
                "    DeletedLabel111,DeletedLabel222\n" +
                "  window:\n" +
                "    A1:E5\n"
        );
    }

    @Test
    public void testPrintTreeColumnWidths() {
        this.treePrintAndCheck(
            SpreadsheetDeltaWindowed.withWindowed(
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
                SpreadsheetDelta.NO_TOTAL_HEIGHT,
                this.window()
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
                "    A: 50.0\n" +
                "  window:\n" +
                "    A1:E5\n"
        );
    }

    @Test
    public void testPrintTreeRowHeights() {
        this.treePrintAndCheck(
            SpreadsheetDeltaWindowed.withWindowed(
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
                SpreadsheetDelta.NO_TOTAL_HEIGHT,
                this.window()
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
                "    1: 75.0\n" +
                "  window:\n" +
                "    A1:E5\n"
        );
    }

    @Test
    public void testPrintTreeNothingEmpty() {
        this.treePrintAndCheck(
            SpreadsheetDeltaWindowed.withWindowed(
                this.viewport(),
                this.cells(),
                SpreadsheetDelta.NO_COLUMNS,
                SpreadsheetDelta.NO_FORMS,
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
                this.rowCount(),
                this.window()
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
                "  rowCount: 99\n" +
                "  window:\n" +
                "    A1:E5\n"
        );
    }

    // json.............................................................................................................

    private final static JsonString WINDOW_JSON_STRING = JsonNode.string("A1:E5");

    @Override
    void unmarshallViewportAndCheck(final SpreadsheetViewport viewport) {
        this.unmarshallAndCheck(
            JsonNode.object()
                .set(
                    SpreadsheetDelta.VIEWPORT_SELECTION_PROPERTY,
                    this.marshallContext()
                        .marshall(viewport)
                )
                .set(SpreadsheetDelta.WINDOW_PROPERTY, WINDOW_JSON_STRING),
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
                this.window()
            )
        );
    }

    @Test
    public void testMarshallCells() {
        this.marshallAndCheck(
            SpreadsheetDeltaWindowed.withWindowed(
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
                SpreadsheetDelta.NO_TOTAL_HEIGHT,
                this.window()
            ),
            JsonNode.object()
                .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
                .set(SpreadsheetDeltaWindowed.WINDOW_PROPERTY, WINDOW_JSON_STRING)
        );
    }

    @Test
    public void testMarshallColumns() {
        this.marshallAndCheck(
            SpreadsheetDeltaWindowed.withWindowed(
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
                SpreadsheetDelta.NO_TOTAL_HEIGHT,
                this.window()
            ),
            JsonNode.object()
                .set(SpreadsheetDelta.COLUMNS_PROPERTY, this.columnsJson())
                .set(SpreadsheetDeltaWindowed.WINDOW_PROPERTY, WINDOW_JSON_STRING)
        );
    }

    @Test
    public void testMarshallRows() {
        this.marshallAndCheck(
            SpreadsheetDeltaWindowed.withWindowed(
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
                SpreadsheetDelta.NO_TOTAL_HEIGHT,
                this.window()
            ),
            JsonNode.object()
                .set(SpreadsheetDelta.ROWS_PROPERTY, this.rowsJson())
                .set(SpreadsheetDeltaWindowed.WINDOW_PROPERTY, WINDOW_JSON_STRING)
        );
    }

    @Test
    public void testMarshallCellsLabels() {
        this.marshallAndCheck(
            SpreadsheetDeltaWindowed.withWindowed(
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
                SpreadsheetDelta.NO_TOTAL_HEIGHT,
                this.window()
            ),
            JsonNode.object()
                .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
                .set(SpreadsheetDelta.LABELS_PROPERTY, labelsJson())
                .set(SpreadsheetDeltaWindowed.WINDOW_PROPERTY, WINDOW_JSON_STRING)
        );
    }

    @Test
    public void testMarshallReferences() {
        this.marshallAndCheck(
            SpreadsheetDeltaWindowed.withWindowed(
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
                SpreadsheetDelta.NO_TOTAL_HEIGHT,
                this.window()
            ),
            JsonNode.object()
                .set(SpreadsheetDelta.REFERENCES_PROPERTY, this.referencesJson())
                .set(SpreadsheetDeltaWindowed.WINDOW_PROPERTY, WINDOW_JSON_STRING)
        );
    }

    @Test
    public void testMarshallCellsColumnWidthsWindow() {
        this.marshallAndCheck(
            SpreadsheetDeltaWindowed.withWindowed(
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
                SpreadsheetDelta.NO_TOTAL_HEIGHT,
                this.window()
            ),
            JsonNode.object()
                .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
                .set(SpreadsheetDelta.COLUMN_WIDTHS_PROPERTY, COLUMN_WIDTHS_JSON)
                .set(SpreadsheetDeltaWindowed.WINDOW_PROPERTY, WINDOW_JSON_STRING)
        );
    }

    @Test
    public void testMarshallCellsRowHeightsWindow() {
        this.marshallAndCheck(
            SpreadsheetDeltaWindowed.withWindowed(
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
                SpreadsheetDelta.NO_TOTAL_HEIGHT,
                this.window()
            ),
            JsonNode.object()
                .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
                .set(SpreadsheetDelta.ROW_HEIGHTS_PROPERTY, ROW_HEIGHTS_JSON)
                .set(SpreadsheetDeltaWindowed.WINDOW_PROPERTY, WINDOW_JSON_STRING)
        );
    }

    @Test
    public void testMarshallCellsColumnCountWindow() {
        this.marshallAndCheck(
            SpreadsheetDeltaWindowed.withWindowed(
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
                SpreadsheetDelta.NO_TOTAL_HEIGHT,
                this.window()
            ),
            JsonNode.object()
                .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
                .set(SpreadsheetDelta.COLUMN_COUNT_PROPERTY, COLUMN_COUNT_JSON)
                .set(SpreadsheetDeltaWindowed.WINDOW_PROPERTY, WINDOW_JSON_STRING)
        );
    }

    @Test
    public void testMarshallCellsRowCountWindow() {
        this.marshallAndCheck(
            SpreadsheetDeltaWindowed.withWindowed(
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
                this.rowCount(),
                this.window()
            ),
            JsonNode.object()
                .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
                .set(SpreadsheetDelta.ROW_COUNT_PROPERTY, ROW_COUNT_JSON)
                .set(SpreadsheetDeltaWindowed.WINDOW_PROPERTY, WINDOW_JSON_STRING)
        );
    }

    @Test
    public void testMarshallNothingEmpty() {
        this.marshallAndCheck(
            SpreadsheetDeltaWindowed.withWindowed(
                this.viewport(),
                this.cells(),
                SpreadsheetDelta.NO_COLUMNS,
                this.forms(),
                this.labels(),
                SpreadsheetDelta.NO_ROWS,
                this.references(),
                this.deletedCells(),
                this.deletedColumns(),
                this.deletedRows(),
                this.deletedLabels(),
                this.matchedCells(),
                this.columnWidths(),
                this.rowHeights(),
                this.columnCount(),
                this.rowCount(),
                this.window()
            ),
            JsonNode.object()
                .set(SpreadsheetDelta.VIEWPORT_SELECTION_PROPERTY, this.viewportJson())
                .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
                .set(SpreadsheetDelta.FORMS_PROPERTY, formsJson())
                .set(SpreadsheetDelta.LABELS_PROPERTY, labelsJson())
                .set(SpreadsheetDelta.REFERENCES_PROPERTY, referencesJson())
                .set(SpreadsheetDelta.DELETED_CELLS_PROPERTY, deletedCellsJson())
                .set(SpreadsheetDelta.DELETED_COLUMNS_PROPERTY, deletedColumnsJson())
                .set(SpreadsheetDelta.DELETED_ROWS_PROPERTY, deletedRowsJson())
                .set(SpreadsheetDelta.DELETED_LABELS_PROPERTY, deletedLabelsJson())
                .set(SpreadsheetDelta.MATCHED_CELLS_PROPERTY, matchedCellsJson())
                .set(SpreadsheetDelta.COLUMN_WIDTHS_PROPERTY, COLUMN_WIDTHS_JSON)
                .set(SpreadsheetDelta.ROW_HEIGHTS_PROPERTY, ROW_HEIGHTS_JSON)
                .set(SpreadsheetDelta.COLUMN_COUNT_PROPERTY, COLUMN_COUNT_JSON)
                .set(SpreadsheetDelta.ROW_COUNT_PROPERTY, ROW_COUNT_JSON)
                .set(SpreadsheetDeltaWindowed.WINDOW_PROPERTY, WINDOW_JSON_STRING)
        );
    }

    @Test
    public void testMarshallEmptyWindow() {
        this.marshallAndCheck(
            SpreadsheetDeltaWindowed.withWindowed(
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
                SpreadsheetDelta.NO_TOTAL_HEIGHT,
                this.window()
            ),
            JsonNode.object()
                .set(SpreadsheetDeltaWindowed.WINDOW_PROPERTY, WINDOW_JSON_STRING)
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetDeltaWindowed> type() {
        return SpreadsheetDeltaWindowed.class;
    }
}

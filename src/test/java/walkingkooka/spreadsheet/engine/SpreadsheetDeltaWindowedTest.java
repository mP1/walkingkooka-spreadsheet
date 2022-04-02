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
import walkingkooka.spreadsheet.reference.SpreadsheetCellRange;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.reference.SpreadsheetViewportSelection;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonString;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotSame;

public final class SpreadsheetDeltaWindowedTest extends SpreadsheetDeltaTestCase<SpreadsheetDeltaWindowed> {

    public SpreadsheetDeltaWindowedTest() {
        super();
        this.checkNotEquals(this.window(), this.differentWindow(), "window v differentWindow must NOT be equal");
    }

    @Test
    public void testSetLabelsMissingCellKept() {
        final SpreadsheetDeltaWindowed before = this.createSpreadsheetDelta();
        final Set<SpreadsheetLabelMapping> different = Sets.of(
                SpreadsheetLabelName.labelName("Different")
                        .mapping(SpreadsheetCellReference.parseCell("A3"))
        );

        final SpreadsheetDelta after = before.setLabels(different);
        assertNotSame(before, after);
        this.checkLabels(after, different);
        this.checkCells(after, before.cells());
    }

    @Test
    public void testSetLabelsOutsideWindowFiltered() {
        final SpreadsheetDeltaWindowed before = this.createSpreadsheetDelta();
        final Set<SpreadsheetLabelMapping> different = Sets.of(
                SpreadsheetLabelName.labelName("Different").mapping(SpreadsheetCellReference.parseCell("Z99"))
        );

        final SpreadsheetDelta after = before.setLabels(different);
        assertNotSame(before, after);
        this.checkLabels(after, SpreadsheetDelta.NO_LABELS);
        this.checkCells(after, before.cells());
    }

    @Test
    public void testSetLabelsOutsideWindowFiltered2() {
        final SpreadsheetDeltaWindowed before = this.createSpreadsheetDelta();

        final SpreadsheetCellReference a1 = this.a1().reference();
        final SpreadsheetLabelName kept = SpreadsheetLabelName.labelName("Kept");

        final SpreadsheetCellReference a3 = SpreadsheetCellReference.parseCell("A3");
        final SpreadsheetLabelName kept3 = SpreadsheetLabelName.labelName("Kept2");

        final Set<SpreadsheetLabelMapping> different = Sets.of(
                kept.mapping(a1),
                kept3.mapping(a3),
                SpreadsheetLabelName.labelName("Lost").mapping(SpreadsheetCellReference.parseCell("Z99"))
        );

        final SpreadsheetDelta after = before.setLabels(different);
        assertNotSame(before, after);
        this.checkLabels(after, Sets.of(kept.mapping(a1), kept3.mapping(a3)));
        this.checkCells(after, before.cells());
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
        this.checkColumnWidths(after, SpreadsheetDelta.NO_COLUMN_WIDTHS);
        this.checkCells(after, before.cells());
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
        this.checkColumnWidths(
                after,
                Map.of(kept, 20.0)
        );
        this.checkCells(after, before.cells());
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
        this.checkRowHeights(after, SpreadsheetDelta.NO_ROW_HEIGHTS);
        this.checkCells(after, before.cells());
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
        this.checkRowHeights(
                after,
                Map.of(kept, 20.0)
        );
        this.checkCells(after, before.cells());
    }

    // setWindow........................................................................................................

    @Test
    public void testSetWindowMultiple() {
        final Optional<SpreadsheetViewportSelection> selection = this.selection();

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

        final SpreadsheetLabelMapping labelA1a = this.label1a().mapping(a1.reference());
        final SpreadsheetLabelMapping label2b = SpreadsheetSelection.labelName("label2b").mapping(b2.reference());

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
        final Set<SpreadsheetCellReference> deletedCells = Sets.of(
                d4,
                e5
        );

        final SpreadsheetColumnReference e = SpreadsheetSelection.parseColumn("f");
        final Set<SpreadsheetColumnReference> deletedColumns = Sets.of(
                e
        );

        final SpreadsheetRowReference row5 = SpreadsheetSelection.parseRow("5");
        final Set<SpreadsheetRowReference> deletedRows = Sets.of(
                row5
        );
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
        final Set<SpreadsheetCellRange> window = SpreadsheetDelta.NO_WINDOW;

        final SpreadsheetDeltaWindowed before = SpreadsheetDeltaWindowed.withWindowed(
                selection,
                cells,
                columns,
                labels,
                rows,
                deletedCells,
                deletedColumns,
                deletedRows,
                columnWidths,
                rowHeights,
                window
        );

        this.checkSelection(before, selection);

        this.checkCells(before, cells);
        this.checkColumns(before, columns);
        this.checkLabels(before, labels);
        this.checkRows(before, rows);

        this.checkDeletedCells(before, deletedCells);
        this.checkDeletedColumns(before, deletedColumns);
        this.checkDeletedRows(before, deletedRows);

        this.checkWindow(before, window);

        final Set<SpreadsheetCellRange> window2 = SpreadsheetSelection.parseWindow("a1,e5:f6");

        final SpreadsheetDelta after = before.setWindow(window2);

        this.checkCells(after, Sets.of(a1));
        this.checkColumns(after, Sets.of(a));
        this.checkLabels(after, Sets.of(labelA1a));
        this.checkRows(after, Sets.of(row1));

        this.checkDeletedCells(after, Sets.of(e5));
        this.checkDeletedColumns(after, Sets.of(e));
        this.checkDeletedRows(after, Sets.of(row5));

        this.checkWindow(after, window2);
    }

    // TreePrintable.....................................................................................................

    @Test
    public void testPrintTreeEmptyOnlyWindow() {
        this.treePrintAndCheck(
                SpreadsheetDeltaWindowed.withWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        SpreadsheetDelta.NO_CELLS,
                        SpreadsheetDelta.NO_COLUMNS,
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_ROWS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        SpreadsheetDelta.NO_DELETED_COLUMNS,
                        SpreadsheetDelta.NO_DELETED_ROWS,
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_ROW_HEIGHTS,
                        this.window()
                ),
                "SpreadsheetDelta\n" +
                        "  window:\n" +
                        "    A1:E5\n"
        );
    }

    @Test
    public void testPrintTreeSelection() {
        this.treePrintAndCheck(
                SpreadsheetDeltaWindowed.withWindowed(
                        this.selection(),
                        SpreadsheetDelta.NO_CELLS,
                        SpreadsheetDelta.NO_COLUMNS,
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_ROWS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        SpreadsheetDelta.NO_DELETED_COLUMNS,
                        SpreadsheetDelta.NO_DELETED_ROWS,
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_ROW_HEIGHTS,
                        this.window()
                ),
                "SpreadsheetDelta\n" +
                        "  selection:\n" +
                        "    A1:B2 BOTTOM_RIGHT\n" +
                        "  window:\n" +
                        "    A1:E5\n"
        );
    }

    @Test
    public void testPrintTreeCells() {
        this.treePrintAndCheck(
                SpreadsheetDeltaWindowed.withWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        this.cells(),
                        SpreadsheetDelta.NO_COLUMNS,
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_ROWS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        SpreadsheetDelta.NO_DELETED_COLUMNS,
                        SpreadsheetDelta.NO_DELETED_ROWS,
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_ROW_HEIGHTS,
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
                        "    A1:E5\n"
        );
    }

    @Test
    public void testPrintTreeColumns() {
        this.treePrintAndCheck(
                SpreadsheetDeltaWindowed.withWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        SpreadsheetDelta.NO_CELLS,
                        Sets.of(
                                this.a(),
                                this.hiddenD()
                        ),
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_ROWS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        SpreadsheetDelta.NO_DELETED_COLUMNS,
                        SpreadsheetDelta.NO_DELETED_ROWS,
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_ROW_HEIGHTS,
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
    public void testPrintTreeLabels() {
        this.treePrintAndCheck(
                SpreadsheetDeltaWindowed.withWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        SpreadsheetDelta.NO_CELLS,
                        SpreadsheetDelta.NO_COLUMNS,
                        this.labels(),
                        SpreadsheetDelta.NO_ROWS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        SpreadsheetDelta.NO_DELETED_COLUMNS,
                        SpreadsheetDelta.NO_DELETED_ROWS,
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_ROW_HEIGHTS,
                        this.window()
                ),
                "SpreadsheetDelta\n" +
                        "  labels:\n" +
                        "    LabelA1A: A1\n" +
                        "    LabelA1B: A1\n" +
                        "    LabelB2: B2\n" +
                        "    LabelC3: C3\n" +
                        "  window:\n" +
                        "    A1:E5\n"
        );
    }

    @Test
    public void testPrintTreeRows() {
        this.treePrintAndCheck(
                SpreadsheetDeltaWindowed.withWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        SpreadsheetDelta.NO_CELLS,
                        SpreadsheetDelta.NO_COLUMNS,
                        SpreadsheetDelta.NO_LABELS,
                        Sets.of(
                                this.row1(),
                                this.hiddenRow4()
                        ),
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        SpreadsheetDelta.NO_DELETED_COLUMNS,
                        SpreadsheetDelta.NO_DELETED_ROWS,
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_ROW_HEIGHTS,
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
                        SpreadsheetDelta.NO_SELECTION,
                        this.cells(),
                        SpreadsheetDelta.NO_COLUMNS,
                        this.labels(),
                        SpreadsheetDelta.NO_ROWS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        SpreadsheetDelta.NO_DELETED_COLUMNS,
                        SpreadsheetDelta.NO_DELETED_ROWS,
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_ROW_HEIGHTS,
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
                        "  labels:\n" +
                        "    LabelA1A: A1\n" +
                        "    LabelA1B: A1\n" +
                        "    LabelB2: B2\n" +
                        "    LabelC3: C3\n" +
                        "  window:\n" +
                        "    A1:E5\n"
        );
    }

    @Test
    public void testPrintTreeDeletedCells() {
        this.treePrintAndCheck(
                SpreadsheetDeltaWindowed.withWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        this.cells(),
                        SpreadsheetDelta.NO_COLUMNS,
                        this.labels(),
                        SpreadsheetDelta.NO_ROWS,
                        this.deletedCells(),
                        SpreadsheetDelta.NO_DELETED_COLUMNS,
                        SpreadsheetDelta.NO_DELETED_ROWS,
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_ROW_HEIGHTS,
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
                        "  labels:\n" +
                        "    LabelA1A: A1\n" +
                        "    LabelA1B: A1\n" +
                        "    LabelB2: B2\n" +
                        "    LabelC3: C3\n" +
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
                        SpreadsheetDelta.NO_SELECTION,
                        SpreadsheetDelta.NO_CELLS,
                        SpreadsheetDelta.NO_COLUMNS,
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_ROWS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        this.deletedColumns(),
                        SpreadsheetDelta.NO_DELETED_ROWS,
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_ROW_HEIGHTS,
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
                        SpreadsheetDelta.NO_SELECTION,
                        SpreadsheetDelta.NO_CELLS,
                        SpreadsheetDelta.NO_COLUMNS,
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_ROWS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        SpreadsheetDelta.NO_DELETED_COLUMNS,
                        this.deletedRows(),
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_ROW_HEIGHTS,
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
    public void testPrintTreeColumnWidths() {
        this.treePrintAndCheck(
                SpreadsheetDeltaWindowed.withWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        this.cells(),
                        SpreadsheetDelta.NO_COLUMNS,
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_ROWS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        SpreadsheetDelta.NO_DELETED_COLUMNS,
                        SpreadsheetDelta.NO_DELETED_ROWS,
                        this.columnWidths(),
                        SpreadsheetDelta.NO_ROW_HEIGHTS,
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
                        "    A1:E5\n"
        );
    }

    @Test
    public void testPrintTreeRowHeights() {
        this.treePrintAndCheck(
                SpreadsheetDeltaWindowed.withWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        this.cells(),
                        SpreadsheetDelta.NO_COLUMNS,
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_ROWS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        SpreadsheetDelta.NO_DELETED_COLUMNS,
                        SpreadsheetDelta.NO_DELETED_ROWS,
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        this.rowHeights(),
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
                        "    A1:E5\n"
        );
    }

    @Test
    public void testPrintTreeNothingEmpty() {
        this.treePrintAndCheck(
                SpreadsheetDeltaWindowed.withWindowed(
                        this.selection(),
                        this.cells(),
                        SpreadsheetDelta.NO_COLUMNS,
                        this.labels(),
                        SpreadsheetDelta.NO_ROWS,
                        this.deletedCells(),
                        this.deletedColumns(),
                        this.deletedRows(),
                        this.columnWidths(),
                        this.rowHeights(),
                        this.window()
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
                        "    C1,C2\n" +
                        "  deletedColumns:\n" +
                        "    C,D\n" +
                        "  deletedRows:\n" +
                        "    3,4\n" +
                        "  columnWidths:\n" +
                        "    A: 50.0\n" +
                        "  rowHeights:\n" +
                        "    1: 75.0\n" +
                        "  window:\n" +
                        "    A1:E5\n"
        );
    }

    // JsonNodeMarshallingTesting...........................................................................................

    private final static JsonString WINDOW_JSON_STRING = JsonNode.string("A1:E5");

    @Override
    void unmarshallSelectionAndCheck(final SpreadsheetViewportSelection selection) {
        this.unmarshallAndCheck(
                JsonNode.object()
                        .set(
                                SpreadsheetDelta.SELECTION_PROPERTY,
                                this.marshallContext()
                                        .marshall(selection)
                        )
                        .set(SpreadsheetDelta.WINDOW_PROPERTY, WINDOW_JSON_STRING),
                SpreadsheetDeltaWindowed.withWindowed(
                        Optional.ofNullable(
                                selection
                        ),
                        SpreadsheetDelta.NO_CELLS,
                        SpreadsheetDelta.NO_COLUMNS,
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_ROWS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        SpreadsheetDelta.NO_DELETED_COLUMNS,
                        SpreadsheetDelta.NO_DELETED_ROWS,
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_ROW_HEIGHTS,
                        this.window()
                )
        );
    }

    @Test
    public void testMarshallCells() {
        this.marshallAndCheck(
                SpreadsheetDeltaWindowed.withWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        this.cells(),
                        SpreadsheetDelta.NO_COLUMNS,
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_ROWS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        SpreadsheetDelta.NO_DELETED_COLUMNS,
                        SpreadsheetDelta.NO_DELETED_ROWS,
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_ROW_HEIGHTS,
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
                        SpreadsheetDelta.NO_SELECTION,
                        SpreadsheetDelta.NO_CELLS,
                        this.columns(),
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_ROWS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        SpreadsheetDelta.NO_DELETED_COLUMNS,
                        SpreadsheetDelta.NO_DELETED_ROWS,
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_ROW_HEIGHTS,
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
                        SpreadsheetDelta.NO_SELECTION,
                        SpreadsheetDelta.NO_CELLS,
                        SpreadsheetDelta.NO_COLUMNS,
                        SpreadsheetDelta.NO_LABELS,
                        this.rows(),
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        SpreadsheetDelta.NO_DELETED_COLUMNS,
                        SpreadsheetDelta.NO_DELETED_ROWS,
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_ROW_HEIGHTS,
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
                        SpreadsheetDelta.NO_SELECTION,
                        this.cells(),
                        SpreadsheetDelta.NO_COLUMNS,
                        this.labels(),
                        SpreadsheetDelta.NO_ROWS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        SpreadsheetDelta.NO_DELETED_COLUMNS,
                        SpreadsheetDelta.NO_DELETED_ROWS,
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_ROW_HEIGHTS,
                        this.window()
                ),
                JsonNode.object()
                        .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
                        .set(SpreadsheetDelta.LABELS_PROPERTY, labelsJson())
                        .set(SpreadsheetDeltaWindowed.WINDOW_PROPERTY, WINDOW_JSON_STRING)
        );
    }

    @Test
    public void testMarshallCellsColumnWidthsWindow() {
        this.marshallAndCheck(
                SpreadsheetDeltaWindowed.withWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        this.cells(),
                        SpreadsheetDelta.NO_COLUMNS,
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_ROWS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        SpreadsheetDelta.NO_DELETED_COLUMNS,
                        SpreadsheetDelta.NO_DELETED_ROWS,
                        this.columnWidths(),
                        SpreadsheetDelta.NO_ROW_HEIGHTS,
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
                        SpreadsheetDelta.NO_SELECTION,
                        this.cells(),
                        SpreadsheetDelta.NO_COLUMNS,
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_ROWS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        SpreadsheetDelta.NO_DELETED_COLUMNS,
                        SpreadsheetDelta.NO_DELETED_ROWS,
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        this.rowHeights(),
                        this.window()
                ),
                JsonNode.object()
                        .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
                        .set(SpreadsheetDelta.ROW_HEIGHTS_PROPERTY, ROW_HEIGHTS_JSON)
                        .set(SpreadsheetDeltaWindowed.WINDOW_PROPERTY, WINDOW_JSON_STRING)
        );
    }

    @Test
    public void testMarshallNothingEmpty() {
        this.marshallAndCheck(
                SpreadsheetDeltaWindowed.withWindowed(
                        this.selection(),
                        this.cells(),
                        SpreadsheetDelta.NO_COLUMNS,
                        this.labels(),
                        SpreadsheetDelta.NO_ROWS,
                        this.deletedCells(),
                        this.deletedColumns(),
                        this.deletedRows(),
                        this.columnWidths(),
                        this.rowHeights(),
                        this.window()
                ),
                JsonNode.object()
                        .set(SpreadsheetDelta.SELECTION_PROPERTY, this.selectionJson())
                        .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
                        .set(SpreadsheetDelta.LABELS_PROPERTY, labelsJson())
                        .set(SpreadsheetDelta.DELETED_CELLS_PROPERTY, deletedCellsJson())
                        .set(SpreadsheetDelta.DELETED_COLUMNS_PROPERTY, deletedColumnsJson())
                        .set(SpreadsheetDelta.DELETED_ROWS_PROPERTY, deletedRowsJson())
                        .set(SpreadsheetDelta.COLUMN_WIDTHS_PROPERTY, COLUMN_WIDTHS_JSON)
                        .set(SpreadsheetDelta.ROW_HEIGHTS_PROPERTY, ROW_HEIGHTS_JSON)
                        .set(SpreadsheetDeltaWindowed.WINDOW_PROPERTY, WINDOW_JSON_STRING)
        );
    }

    @Test
    public void testMarshallEmptyWindow() {
        this.marshallAndCheck(
                SpreadsheetDeltaWindowed.withWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        SpreadsheetDelta.NO_CELLS,
                        SpreadsheetDelta.NO_COLUMNS,
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_ROWS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        SpreadsheetDelta.NO_DELETED_COLUMNS,
                        SpreadsheetDelta.NO_DELETED_ROWS,
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_ROW_HEIGHTS,
                        this.window()
                ),
                JsonNode.object()
                        .set(SpreadsheetDeltaWindowed.WINDOW_PROPERTY, WINDOW_JSON_STRING)
        );
    }

    // toString..........................................................................................................

    @Test
    public void testToStringSelection() {
        this.toStringAndCheck(
                SpreadsheetDeltaWindowed.withWindowed(
                        this.selection(),
                        SpreadsheetDelta.NO_CELLS,
                        SpreadsheetDelta.NO_COLUMNS,
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_ROWS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        SpreadsheetDelta.NO_DELETED_COLUMNS,
                        SpreadsheetDelta.NO_DELETED_ROWS,
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_ROW_HEIGHTS,
                        this.window()
                ),
                "selection: A1:B2 BOTTOM_RIGHT window: A1:E5");
    }

    @Test
    public void testToStringCells() {
        this.toStringAndCheck(
                SpreadsheetDeltaWindowed.withWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        this.cells(),
                        SpreadsheetDelta.NO_COLUMNS,
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_ROWS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        SpreadsheetDelta.NO_DELETED_COLUMNS,
                        SpreadsheetDelta.NO_DELETED_ROWS,
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_ROW_HEIGHTS,
                        this.window()
                ),
                "cells: A1=1, B2=2, C3=3 window: A1:E5");
    }

    @Test
    public void testToStringLabels() {
        this.toStringAndCheck(
                SpreadsheetDeltaWindowed.withWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        this.cells(),
                        SpreadsheetDelta.NO_COLUMNS,
                        this.labels(),
                        SpreadsheetDelta.NO_ROWS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        SpreadsheetDelta.NO_DELETED_COLUMNS,
                        SpreadsheetDelta.NO_DELETED_ROWS,
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_ROW_HEIGHTS,
                        this.window()
                ),
                "cells: A1=1, B2=2, C3=3 labels: LabelA1A=A1, LabelA1B=A1, LabelB2=B2, LabelC3=C3 window: A1:E5");
    }

    @Test
    public void testToStringDeletedCells() {
        this.toStringAndCheck(
                SpreadsheetDeltaWindowed.withWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        this.cells(),
                        SpreadsheetDelta.NO_COLUMNS,
                        this.labels(),
                        SpreadsheetDelta.NO_ROWS,
                        this.deletedCells(),
                        SpreadsheetDelta.NO_DELETED_COLUMNS,
                        SpreadsheetDelta.NO_DELETED_ROWS,
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_ROW_HEIGHTS,
                        this.window()
                ),
                "cells: A1=1, B2=2, C3=3 labels: LabelA1A=A1, LabelA1B=A1, LabelB2=B2, LabelC3=C3 deletedCells: C1, C2 window: A1:E5");
    }

    @Test
    public void testToStringDeletedColumns() {
        this.toStringAndCheck(
                SpreadsheetDeltaWindowed.withWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        SpreadsheetDelta.NO_CELLS,
                        SpreadsheetDelta.NO_COLUMNS,
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_ROWS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        this.deletedColumns(),
                        SpreadsheetDelta.NO_DELETED_ROWS,
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_ROW_HEIGHTS,
                        this.window()
                ),
                "deletedColumns: C, D window: A1:E5");
    }

    @Test
    public void testToStringDeletedRows() {
        this.toStringAndCheck(
                SpreadsheetDeltaWindowed.withWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        SpreadsheetDelta.NO_CELLS,
                        SpreadsheetDelta.NO_COLUMNS,
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_ROWS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        SpreadsheetDelta.NO_DELETED_COLUMNS,
                        this.deletedRows(),
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_ROW_HEIGHTS,
                        this.window()
                ),
                "deletedRows: 3, 4 window: A1:E5");
    }

    @Test
    public void testToStringColumnWidths() {
        this.toStringAndCheck(
                SpreadsheetDeltaWindowed.withWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        this.cells(),
                        SpreadsheetDelta.NO_COLUMNS,
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_ROWS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        SpreadsheetDelta.NO_DELETED_COLUMNS,
                        SpreadsheetDelta.NO_DELETED_ROWS,
                        this.columnWidths(),
                        SpreadsheetDelta.NO_ROW_HEIGHTS,
                        this.window()
                ),
                "cells: A1=1, B2=2, C3=3 max: A=50.0 window: A1:E5");
    }

    @Test
    public void testToStringMaxRowHeights() {
        this.toStringAndCheck(
                SpreadsheetDeltaWindowed.withWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        this.cells(),
                        SpreadsheetDelta.NO_COLUMNS,
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_ROWS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        SpreadsheetDelta.NO_DELETED_COLUMNS,
                        SpreadsheetDelta.NO_DELETED_ROWS,
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        this.rowHeights(),
                        this.window()
                ),
                "cells: A1=1, B2=2, C3=3 max: 1=75.0 window: A1:E5");
    }

    @Test
    public void testToStringColumnWidthsRowHeights() {
        this.toStringAndCheck(
                SpreadsheetDeltaWindowed.withWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        this.cells(),
                        SpreadsheetDelta.NO_COLUMNS,
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_ROWS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        SpreadsheetDelta.NO_DELETED_COLUMNS,
                        SpreadsheetDelta.NO_DELETED_ROWS,
                        this.columnWidths(),
                        this.rowHeights(),
                        this.window()
                ),
                "cells: A1=1, B2=2, C3=3 max: A=50.0, 1=75.0 window: A1:E5");
    }

    // helpers..........................................................................................................

    @Override
    Set<SpreadsheetCellRange> window() {
        return SpreadsheetSelection.parseWindow("A1:E5");
    }

    @Override
    SpreadsheetDeltaWindowed createSpreadsheetDelta(final Set<SpreadsheetCell> cells) {
        return this.createSpreadsheetDelta(
                cells,
                this.window()
        );
    }

    private SpreadsheetDeltaWindowed createSpreadsheetDelta(final Set<SpreadsheetCell> cells,
                                                            final Set<SpreadsheetCellRange> window) {
        return SpreadsheetDeltaWindowed.withWindowed(
                this.selection(),
                cells,
                this.columns(),
                this.labels(),
                this.rows(),
                this.deletedCells(),
                this.deletedColumns(),
                this.deletedRows(),
                this.columnWidths(),
                this.rowHeights(),
                window
        );
    }

    @Override
    public Class<SpreadsheetDeltaWindowed> type() {
        return SpreadsheetDeltaWindowed.class;
    }
}

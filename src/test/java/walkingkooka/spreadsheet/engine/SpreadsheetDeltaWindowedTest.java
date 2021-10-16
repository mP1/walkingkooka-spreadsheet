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
import walkingkooka.spreadsheet.reference.SpreadsheetCellRange;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonString;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

public final class SpreadsheetDeltaWindowedTest extends SpreadsheetDeltaTestCase<SpreadsheetDeltaWindowed> {

    public SpreadsheetDeltaWindowedTest() {
        super();
        assertNotEquals(this.window(), this.differentWindow(), "window v differentWindow must NOT be equal");
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

    // TreePrintable.....................................................................................................

    @Test
    public void testPrintTreeEmptyOnlyWindow() {
        this.treePrintAndCheck(
                SpreadsheetDeltaWindowed.withWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        SpreadsheetDelta.NO_CELLS,
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
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
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_ROW_HEIGHTS,
                        this.window()
                ),
                "SpreadsheetDelta\n" +
                        "  selection:\n" +
                        "    A1:B2\n" +
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
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
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
    public void testPrintTreeLabels() {
        this.treePrintAndCheck(
                SpreadsheetDeltaWindowed.withWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        SpreadsheetDelta.NO_CELLS,
                        this.labels(),
                        SpreadsheetDelta.NO_DELETED_CELLS,
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
    public void testPrintTreeCellsAndLabels() {
        this.treePrintAndCheck(
                SpreadsheetDeltaWindowed.withWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        this.cells(),
                        this.labels(),
                        SpreadsheetDelta.NO_DELETED_CELLS,
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
                        this.labels(),
                        this.deletedCells(),
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
                        "    C1\n" +
                        "    C2\n" +
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
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
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
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
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
                        this.labels(),
                        this.deletedCells(),
                        this.columnWidths(),
                        this.rowHeights(),
                        this.window()
                ),
                "SpreadsheetDelta\n" +
                        "  selection:\n" +
                        "    A1:B2\n" +
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
                        "    1: 75.0\n" +
                        "  window:\n" +
                        "    A1:E5\n"
        );
    }

    // JsonNodeMarshallingTesting...........................................................................................

    private final static JsonString WINDOW_JSON_STRING = JsonNode.string("A1:E5");

    @Override
    final void unmarshallSelectionAndCheck(final SpreadsheetSelection selection) {
        this.unmarshallAndCheck(
                JsonNode.object()
                        .set(SpreadsheetDelta.SELECTION_PROPERTY, this.marshallContext().marshallWithType(selection))
                        .set(SpreadsheetDelta.WINDOW_PROPERTY, WINDOW_JSON_STRING),
                SpreadsheetDeltaWindowed.withWindowed(
                        Optional.ofNullable(
                                selection
                        ),
                        SpreadsheetDelta.NO_CELLS,
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
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
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
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
    public void testMarshallCellsLabels() {
        this.marshallAndCheck(
                SpreadsheetDeltaWindowed.withWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        this.cells(),
                        this.labels(),
                        SpreadsheetDelta.NO_DELETED_CELLS,
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
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
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
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        this.rowHeights(),
                        this.window()
                ),
                JsonNode.object()
                        .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
                        .set(SpreadsheetDelta.MAX_ROW_HEIGHTS_PROPERTY, MAX_ROW_HEIGHTS_JSON)
                        .set(SpreadsheetDeltaWindowed.WINDOW_PROPERTY, WINDOW_JSON_STRING)
        );
    }

    @Test
    public void testMarshallNothingEmpty() {
        this.marshallAndCheck(
                SpreadsheetDeltaWindowed.withWindowed(
                        this.selection(),
                        this.cells(),
                        this.labels(),
                        this.deletedCells(),
                        this.columnWidths(),
                        this.rowHeights(),
                        this.window()
                ),
                JsonNode.object()
                        .set(SpreadsheetDelta.SELECTION_PROPERTY, this.selectionJson())
                        .set(SpreadsheetDelta.CELLS_PROPERTY, cellsJson())
                        .set(SpreadsheetDelta.LABELS_PROPERTY, labelsJson())
                        .set(SpreadsheetDelta.DELETED_CELLS_PROPERTY, deletedCellsJson())
                        .set(SpreadsheetDelta.COLUMN_WIDTHS_PROPERTY, COLUMN_WIDTHS_JSON)
                        .set(SpreadsheetDelta.MAX_ROW_HEIGHTS_PROPERTY, MAX_ROW_HEIGHTS_JSON)
                        .set(SpreadsheetDeltaWindowed.WINDOW_PROPERTY, WINDOW_JSON_STRING)
        );
    }

    @Test
    public void testMarshallEmptyWindow() {
        this.marshallAndCheck(
                SpreadsheetDeltaWindowed.withWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        SpreadsheetDelta.NO_CELLS,
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
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
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_ROW_HEIGHTS,
                        this.window()
                ),
                "selection: A1:B2 window: A1:E5");
    }

    @Test
    public void testToStringCells() {
        this.toStringAndCheck(
                SpreadsheetDeltaWindowed.withWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        this.cells(),
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
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
                        this.labels(),
                        SpreadsheetDelta.NO_DELETED_CELLS,
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
                        this.labels(),
                        this.deletedCells(),
                        SpreadsheetDelta.NO_COLUMN_WIDTHS,
                        SpreadsheetDelta.NO_ROW_HEIGHTS,
                        this.window()
                ),
                "cells: A1=1, B2=2, C3=3 labels: LabelA1A=A1, LabelA1B=A1, LabelB2=B2, LabelC3=C3 deletedCells: C1, C2 window: A1:E5");
    }

    @Test
    public void testToStringColumnWidths() {
        this.toStringAndCheck(
                SpreadsheetDeltaWindowed.withWindowed(
                        SpreadsheetDelta.NO_SELECTION,
                        this.cells(),
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
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
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
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
                        SpreadsheetDelta.NO_LABELS,
                        SpreadsheetDelta.NO_DELETED_CELLS,
                        this.columnWidths(),
                        this.rowHeights(),
                        this.window()
                ),
                "cells: A1=1, B2=2, C3=3 max: A=50.0, 1=75.0 window: A1:E5");
    }

    // helpers..........................................................................................................

    @Override
    final Optional<SpreadsheetCellRange> window() {
        return this.window0("A1:E5");
    }

    @Override
    final SpreadsheetDeltaWindowed createSpreadsheetDelta(final Set<SpreadsheetCell> cells) {
        return this.createSpreadsheetDelta(cells, this.window());
    }

    private SpreadsheetDeltaWindowed createSpreadsheetDelta(final Set<SpreadsheetCell> cells,
                                                            final Optional<SpreadsheetCellRange> window) {
        return SpreadsheetDeltaWindowed.withWindowed(
                this.selection(),
                cells,
                this.labels(),
                this.deletedCells(),
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

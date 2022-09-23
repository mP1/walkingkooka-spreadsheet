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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import walkingkooka.collect.set.Sets;
import walkingkooka.color.Color;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetCellFormat;
import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetRow;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRange;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReferenceOrRange;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.reference.SpreadsheetViewportSelection;
import walkingkooka.spreadsheet.reference.SpreadsheetViewportSelectionAnchor;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.json.InvalidPropertyJsonNodeException;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonObject;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;
import walkingkooka.tree.json.patch.PatchableTesting;
import walkingkooka.tree.text.FontStyle;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;

import java.math.MathContext;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetDeltaTest implements ClassTesting2<SpreadsheetDelta>,
        PatchableTesting<SpreadsheetDelta> {

    @Test
    public void testEmpty() {
        final SpreadsheetDelta empty = SpreadsheetDelta.EMPTY;

        this.checkEquals(SpreadsheetDelta.NO_CELLS, empty.cells());
        this.checkEquals(SpreadsheetDelta.NO_LABELS, empty.labels());
        this.checkEquals(SpreadsheetDelta.NO_DELETED_CELLS, empty.deletedCells());
        this.checkEquals(SpreadsheetDelta.NO_COLUMN_WIDTHS, empty.columnWidths());
        this.checkEquals(SpreadsheetDelta.NO_ROW_HEIGHTS, empty.rowHeights());
    }

    @Test
    public void testNoWindowConstant() {
        this.checkEquals(
                Sets.empty(),
                SpreadsheetDelta.NO_WINDOW
        );
    }

    // setLabel.........................................................................................................

    @Test
    public void testSetLabelsWithLabelRangeInsideWindow() {
        final Set<SpreadsheetLabelMapping> mappings = this.labelMappingsLabel123ToC3E5();

        final SpreadsheetDelta delta = SpreadsheetDelta.EMPTY
                .setWindow(SpreadsheetSelection.parseWindow("B2:F6"))
                .setLabels(mappings);

        this.checkEquals(
                mappings,
                delta.labels(),
                "labels"
        );
    }

    @Test
    public void testSetLabelsWithLabelRangePartiallyOutsideWindow() {
        final Set<SpreadsheetLabelMapping> mappings = this.labelMappingsLabel123ToC3E5();

        final SpreadsheetDelta delta = SpreadsheetDelta.EMPTY
                .setWindow(SpreadsheetSelection.parseWindow("B2:D4"))
                .setLabels(mappings);

        this.checkEquals(
                mappings,
                delta.labels(),
                "labels"
        );
    }

    @Test
    public void testSetLabelsWithLabelRangeWholeyOutsideWindow() {
        final Set<SpreadsheetLabelMapping> mappings = this.labelMappingsLabel123ToC3E5();

        final SpreadsheetDelta delta = SpreadsheetDelta.EMPTY
                .setWindow(SpreadsheetSelection.parseWindow("A1:B2"))
                .setLabels(mappings);

        this.checkEquals(
                SpreadsheetDelta.NO_LABELS,
                delta.labels(),
                "labels"
        );
    }

    private Set<SpreadsheetLabelMapping> labelMappingsLabel123ToC3E5() {
        return Sets.of(
                SpreadsheetSelection.labelName("Label123")
                        .mapping(SpreadsheetSelection.parseCellRange("C3:E5"))
        );
    }

    // cell.............................................................................................................

    @Test
    public void testCellWhenEmpty() {
        this.cellAndCheck(
                SpreadsheetDelta.EMPTY,
                SpreadsheetSelection.parseCell("A1"),
                Optional.empty()
        );
    }

    @Test
    public void testCellNotFound() {
        this.cellAndCheck(
                SpreadsheetDelta.EMPTY.setCells(
                        Sets.of(
                                this.cell()
                        )
                ),
                SpreadsheetSelection.parseCell("B2"),
                Optional.empty()
        );
    }

    @Test
    public void testCellFound() {
        final SpreadsheetCell cell = this.cell();
        this.cellAndCheck(
                SpreadsheetDelta.EMPTY.setCells(
                        Sets.of(cell)
                ),
                cell.reference(),
                Optional.of(cell)
        );
    }

    @Test
    public void testCellFoundDifferentKind() {
        final SpreadsheetCell cell = this.cell();
        final SpreadsheetCellReference reference = cell.reference();
        this.checkEquals(reference.toRelative(), reference, "reference should be relative");

        this.cellAndCheck(
                SpreadsheetDelta.EMPTY.setCells(
                        Sets.of(cell)
                ),
                reference.toAbsolute(),
                Optional.of(cell)
        );
    }

    private SpreadsheetCell cell() {
        return SpreadsheetSelection.parseCell("A1")
                .setFormula(
                        SpreadsheetFormula.EMPTY
                                .setText("=1+2")
                );
    }

    private void cellAndCheck(final SpreadsheetDelta delta,
                              final SpreadsheetCellReference reference,
                              final Optional<SpreadsheetCell> cell) {
        this.checkEquals(
                cell,
                delta.cell(reference),
                () -> delta + " cell " + reference
        );
    }

    // column.............................................................................................................

    @Test
    public void testColumnWhenEmpty() {
        this.columnAndCheck(
                SpreadsheetDelta.EMPTY,
                SpreadsheetSelection.parseColumn("A"),
                Optional.empty()
        );
    }

    @Test
    public void testColumnNotFound() {
        this.columnAndCheck(
                SpreadsheetDelta.EMPTY.setColumns(
                        Sets.of(
                                this.column()
                        )
                ),
                SpreadsheetSelection.parseColumn("B"),
                Optional.empty()
        );
    }

    @Test
    public void testColumnFound() {
        final SpreadsheetColumn column = this.column();
        this.columnAndCheck(
                SpreadsheetDelta.EMPTY.setColumns(
                        Sets.of(column)
                ),
                column.reference(),
                Optional.of(column)
        );
    }

    @Test
    public void testColumnFoundDifferentKind() {
        final SpreadsheetColumn column = this.column();
        final SpreadsheetColumnReference reference = column.reference();
        this.checkEquals(reference.toRelative(), reference, "reference should be relative");

        this.columnAndCheck(
                SpreadsheetDelta.EMPTY.setColumns(
                        Sets.of(column)
                ),
                reference.toRelative(),
                Optional.of(column)
        );
    }

    private SpreadsheetColumn column() {
        return SpreadsheetSelection.parseColumn("A")
                .column()
                .setHidden(true);
    }

    private void columnAndCheck(final SpreadsheetDelta delta,
                                final SpreadsheetColumnReference reference,
                                final Optional<SpreadsheetColumn> column) {
        this.checkEquals(
                column,
                delta.column(reference),
                () -> delta + " column " + reference
        );
    }

    // row.............................................................................................................

    @Test
    public void testRowWhenEmpty() {
        this.rowAndCheck(
                SpreadsheetDelta.EMPTY,
                SpreadsheetSelection.parseRow("1"),
                Optional.empty()
        );
    }

    @Test
    public void testRowNotFound() {
        final SpreadsheetRow row = this.row();

        this.rowAndCheck(
                SpreadsheetDelta.EMPTY.setRows(
                        Sets.of(row)
                ),
                row.reference().add(1),
                Optional.empty()
        );
    }

    @Test
    public void testRowFound() {
        final SpreadsheetRow row = this.row();

        this.rowAndCheck(
                SpreadsheetDelta.EMPTY.setRows(
                        Sets.of(row)
                ),
                row.reference(),
                Optional.of(row)
        );
    }

    @Test
    public void testRowFoundDifferentKind() {
        final SpreadsheetRow row = this.row();
        final SpreadsheetRowReference reference = row.reference();
        this.checkEquals(reference.toRelative(), reference, "reference should be relative");

        this.rowAndCheck(
                SpreadsheetDelta.EMPTY.setRows(
                        Sets.of(row)
                ),
                reference.toRelative(),
                Optional.of(row)
        );
    }

    private SpreadsheetRow row() {
        return SpreadsheetSelection.parseRow("1")
                .row()
                .setHidden(true);
    }

    private void rowAndCheck(final SpreadsheetDelta delta,
                             final SpreadsheetRowReference reference,
                             final Optional<SpreadsheetRow> row) {
        this.checkEquals(
                row,
                delta.row(reference),
                () -> delta + " row " + reference
        );
    }

    // Patch............................................................................................................

    @Test
    public void testPatchAllEmptyObject() {
        this.patchAndCheck(
                SpreadsheetDelta.EMPTY,
                JsonNode.object()
        );
    }

    @Test
    public void testPatchAllLabelsFails() {
        this.patchInvalidPropertyFails2(
                SpreadsheetDelta.LABELS_PROPERTY,
                JsonNode.nullNode()

        );
    }

    @Test
    public void testPatchAllDeletedCellsFails() {
        this.patchInvalidPropertyFails2(
                SpreadsheetDelta.DELETED_CELLS_PROPERTY,
                JsonNode.nullNode()
        );
    }

    @Test
    public void testPatchAllDeletedColumnsFails() {
        this.patchInvalidPropertyFails2(
                SpreadsheetDelta.DELETED_COLUMNS_PROPERTY,
                JsonNode.nullNode()
        );
    }

    @Test
    public void testPatchAllDeletedRowsFails() {
        this.patchInvalidPropertyFails2(
                SpreadsheetDelta.DELETED_ROWS_PROPERTY,
                JsonNode.nullNode()
        );
    }

    @Test
    public void testPatchAllColumnWidthFails() {
        this.patchInvalidPropertyFails2(
                SpreadsheetDelta.COLUMN_WIDTHS_PROPERTY,
                JsonNode.nullNode()
        );
    }

    @Test
    public void testPatchAllRowHeightFails() {
        this.patchInvalidPropertyFails2(
                SpreadsheetDelta.ROW_HEIGHTS_PROPERTY,
                JsonNode.nullNode()
        );
    }

    private void patchInvalidPropertyFails2(final JsonPropertyName key,
                                            final JsonNode value) {
        this.patchInvalidPropertyFails(
                JsonNode.object()
                        .set(
                                key,
                                value
                        ),
                key,
                JsonNode.nullNode()
        );
    }

    @Test
    public void testPatchAllNoViewportSelection() {
        this.patchViewportSelectionAndCheck(
                SpreadsheetDelta.NO_VIEWPORT_SELECTION,
                SpreadsheetDelta.NO_VIEWPORT_SELECTION
        );
    }

    @Test
    public void testPatchAllViewportSelection() {
        this.patchViewportSelectionAndCheck(
                SpreadsheetDelta.NO_VIEWPORT_SELECTION,
                Optional.of(
                        SpreadsheetSelection.parseColumn("C")
                                .setAnchor(SpreadsheetViewportSelectionAnchor.NONE)
                )
        );
    }

    private void patchViewportSelectionAndCheck(final Optional<SpreadsheetViewportSelection> before,
                                                final Optional<SpreadsheetViewportSelection> after) {
        final SpreadsheetDelta delta = SpreadsheetDelta.EMPTY
                .setViewportSelection(after);

        this.patchAndCheck(
                SpreadsheetDelta.EMPTY
                        .setViewportSelection(before),
                marshall(delta),
                delta
        );
    }

    @Test
    public void testPatchAllViewportSelectionCleared() {
        this.patchAndCheck(
                SpreadsheetDelta.EMPTY
                        .setViewportSelection(
                                Optional.of(
                                        SpreadsheetSelection.parseCellRange("A1:B2")
                                                .setAnchor(SpreadsheetViewportSelectionAnchor.BOTTOM_RIGHT)
                                )
                        )
                ,
                JsonNode.object()
                        .set(
                                SpreadsheetDelta.VIEWPORT_SELECTION_PROPERTY,
                                JsonNode.nullNode()
                        ),
                SpreadsheetDelta.EMPTY
        );
    }

    @Test
    public void testPatchAllNewCell() {
        final SpreadsheetCell cell = SpreadsheetSelection.parseCell("a1")
                .setFormula(
                        SpreadsheetFormula.EMPTY
                                .setText("=1")
                );
        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY;

        final SpreadsheetDelta after = before.setCells(
                Sets.of(
                        cell
                )
        );

        this.patchAndCheck(
                before,
                marshall(after),
                after
        );
    }

    @Test
    public void testPatchAllReplacesCell() {
        final SpreadsheetCell cell = SpreadsheetSelection.parseCell("a1")
                .setFormula(
                        SpreadsheetFormula.EMPTY
                                .setText("=1")
                );
        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
                .setCells(
                        Sets.of(cell)
                );

        final SpreadsheetDelta after = before.setCells(
                Sets.of(
                        cell.setFormula(
                                SpreadsheetFormula.EMPTY
                                        .setText("=2")
                        )
                )
        );

        this.patchAndCheck(
                before,
                marshall(after),
                after
        );
    }

    @Test
    public void testPatchAllCellsRemoved() {
        final SpreadsheetDelta without = SpreadsheetDelta.EMPTY
                .setViewportSelection(
                        Optional.of(
                                SpreadsheetSelection.parseCellRange("A1:B2")
                                        .setAnchor(
                                                SpreadsheetViewportSelectionAnchor.TOP_LEFT
                                        )
                        )
                );
        this.patchAndCheck(
                without.setCells(
                        Sets.of(
                                SpreadsheetSelection.parseCell("a1")
                                        .setFormula(
                                                SpreadsheetFormula.EMPTY
                                                        .setText("=1")
                                        )
                        )
                )
                ,
                JsonNode.object()
                        .set(
                                SpreadsheetDelta.CELLS_PROPERTY,
                                JsonNode.nullNode()
                        ),
                without
        );
    }

    @Test
    public void testPatchAllNewColumn() {
        final SpreadsheetColumn column = SpreadsheetSelection.parseColumn("Z")
                .column();
        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY;

        final SpreadsheetDelta after = before.setColumns(
                Sets.of(
                        column
                )
        );

        this.patchAndCheck(
                before,
                marshall(after),
                after
        );
    }

    @Test
    public void testPatchAllNewRow() {
        final SpreadsheetRow row = SpreadsheetSelection.parseRow("1")
                .row();
        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY;

        final SpreadsheetDelta after = before.setRows(
                Sets.of(
                        row
                )
        );

        this.patchAndCheck(
                before,
                marshall(after),
                after
        );
    }

    @Test
    public void testPatchAllWindow() {
        final SpreadsheetCell a1 = SpreadsheetSelection.parseCell("a1")
                .setFormula(
                        SpreadsheetFormula.EMPTY
                                .setText("=1")
                );
        final SpreadsheetCell b2 = SpreadsheetSelection.parseCell("b2")
                .setFormula(
                        SpreadsheetFormula.EMPTY
                                .setText("=99")
                );

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
                .setCells(
                        Sets.of(a1, b2)
                );

        final Set<SpreadsheetCellRange> afterWindow = Sets.of(
                SpreadsheetSelection.parseCellRange("A1")
        );
        final SpreadsheetDelta after = before.setWindow(
                afterWindow
        );

        this.patchAndCheck(
                before,
                marshall(after),
                before.setWindow(
                        afterWindow
                )
        );
    }

    @Test
    public void testPatchAllWindowReplaced() {
        final SpreadsheetCell a1 = SpreadsheetSelection.parseCell("a1")
                .setFormula(
                        SpreadsheetFormula.EMPTY
                                .setText("=1")
                );
        final SpreadsheetCell b2 = SpreadsheetSelection.parseCell("b2")
                .setFormula(
                        SpreadsheetFormula.EMPTY
                                .setText("=99")
                );

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
                .setCells(
                        Sets.of(a1, b2)
                ).setWindow(
                        SpreadsheetSelection.parseWindow("A1:B2")
                );

        final Set<SpreadsheetCellRange> afterWindow = SpreadsheetSelection.parseWindow("A1");

        final SpreadsheetDelta after = before.setWindow(
                afterWindow
        );

        this.patchAndCheck(
                before,
                marshall(after),
                before.setWindow(
                        afterWindow
                )
        );
    }

    @Test
    public void testPatchAllWindowRemoved() {
        final SpreadsheetCell a1 = SpreadsheetSelection.parseCell("a1")
                .setFormula(
                        SpreadsheetFormula.EMPTY
                                .setText("=1")
                );
        final SpreadsheetCell b2 = SpreadsheetSelection.parseCell("b2")
                .setFormula(
                        SpreadsheetFormula.EMPTY
                                .setText("=99")
                );

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
                .setCells(
                        Sets.of(a1, b2)
                ).setWindow(
                        SpreadsheetSelection.parseWindow("A1:B2")
                );

        this.patchAndCheck(
                before,
                JsonNode.object()
                        .set(SpreadsheetDelta.WINDOW_PROPERTY, JsonNode.nullNode()),
                before.setWindow(SpreadsheetDelta.NO_WINDOW)
        );
    }

    // PatchCells.....................................................................................................

    @Test
    public void testPatchCellsWithNullSelectionFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetDelta.EMPTY.patchCells(
                        null,
                        JsonNode.object(),
                        JsonNodeUnmarshallContexts.fake()
                )
        );
    }

    @Test
    public void testPatchCellsColumnsFails() {
        this.patchCellInvalidPropertyFails2(
                SpreadsheetDelta.COLUMNS_PROPERTY,
                JsonNode.nullNode()
        );
    }

    @Test
    public void testPatchCellsLabelsFails() {
        this.patchCellInvalidPropertyFails2(
                SpreadsheetDelta.LABELS_PROPERTY,
                JsonNode.nullNode()
        );
    }

    @Test
    public void testPatchCellsRowsFails() {
        this.patchCellInvalidPropertyFails2(
                SpreadsheetDelta.ROWS_PROPERTY,
                JsonNode.nullNode()
        );
    }

    @Test
    public void testPatchCellsDeletedCellsFails() {
        this.patchCellInvalidPropertyFails2(
                SpreadsheetDelta.DELETED_CELLS_PROPERTY,
                JsonNode.nullNode()
        );
    }

    @Test
    public void testPatchCellsDeletedRowsFails() {
        this.patchCellInvalidPropertyFails2(
                SpreadsheetDelta.DELETED_ROWS_PROPERTY,
                JsonNode.nullNode()
        );
    }

    @Test
    public void testPatchCellsColumnWidthFails() {
        this.patchCellInvalidPropertyFails2(
                SpreadsheetDelta.COLUMN_WIDTHS_PROPERTY,
                JsonNode.nullNode()
        );
    }

    @Test
    public void testPatchCellsRowHeightFails() {
        this.patchCellInvalidPropertyFails2(
                SpreadsheetDelta.ROW_HEIGHTS_PROPERTY,
                JsonNode.nullNode()
        );
    }

    private void patchCellInvalidPropertyFails2(final JsonPropertyName key,
                                                final JsonNode value) {
        final JsonNode patch = JsonNode.object()
                .set(
                        key,
                        value
                );

        final InvalidPropertyJsonNodeException thrown = assertThrows(
                InvalidPropertyJsonNodeException.class,
                () -> SpreadsheetDelta.EMPTY.patchCells(
                        SpreadsheetSelection.parseCell("A1"),
                        patch,
                        this.createPatchContext()
                )
        );
        this.checkEquals(key, thrown.name(), "name");
        this.checkEquals(value.removeParent(), thrown.node().removeParent(), "node");
    }

    @Test
    public void testPatchCellsWithEmptyObject() {
        this.patchCellsAndCheck(
                SpreadsheetDelta.EMPTY,
                SpreadsheetSelection.parseCell("Z99"),
                JsonNode.object(),
                SpreadsheetDelta.EMPTY
        );
    }

    @Test
    public void testPatchCellsNoSelection() {
        this.patchCellSelectionAndCheck(
                SpreadsheetDelta.NO_VIEWPORT_SELECTION,
                SpreadsheetDelta.NO_VIEWPORT_SELECTION
        );
    }

    @Test
    public void testPatchCellsSelection() {
        this.patchCellSelectionAndCheck(
                SpreadsheetDelta.NO_VIEWPORT_SELECTION,
                Optional.of(
                        SpreadsheetSelection.parseColumn("C")
                                .setAnchor(SpreadsheetViewportSelectionAnchor.NONE)
                )
        );
    }

    private void patchCellSelectionAndCheck(final Optional<SpreadsheetViewportSelection> before,
                                            final Optional<SpreadsheetViewportSelection> after) {
        final SpreadsheetDelta delta = SpreadsheetDelta.EMPTY
                .setViewportSelection(after);

        this.patchCellsAndCheck(
                SpreadsheetDelta.EMPTY
                        .setViewportSelection(before),
                SpreadsheetSelection.parseCell("Z99"),
                marshall(delta),
                delta
        );
    }

    @Test
    public void testPatchCellsSelectionCleared() {
        this.patchCellsAndCheck(
                SpreadsheetDelta.EMPTY
                        .setViewportSelection(
                                Optional.of(
                                        SpreadsheetSelection.parseCellRange("A1:B2")
                                                .setAnchor(SpreadsheetViewportSelectionAnchor.BOTTOM_RIGHT)
                                )
                        )
                ,
                SpreadsheetSelection.parseCell("A1"),
                JsonNode.object()
                        .set(
                                SpreadsheetDelta.VIEWPORT_SELECTION_PROPERTY,
                                JsonNode.nullNode()
                        ),
                SpreadsheetDelta.EMPTY
        );
    }

    @Test
    public void testPatchCellsNewCell() {
        final SpreadsheetCell cell = SpreadsheetSelection.parseCell("A1")
                .setFormula(SpreadsheetFormula.EMPTY);
        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY;

        final SpreadsheetDelta after = before.setCells(
                Sets.of(
                        cell
                )
        );

        this.patchCellsAndCheck(
                before,
                cell.reference(),
                marshall(after),
                after
        );
    }

    @Test
    public void testPatchCellsNewCell2() {
        final SpreadsheetCell cell = SpreadsheetSelection.parseCell("A1")
                .setFormula(SpreadsheetFormula.EMPTY);
        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY;

        final SpreadsheetDelta after = before.setCells(
                Sets.of(
                        cell
                )
        );

        this.patchCellsAndCheck(
                before,
                cell.reference(),
                marshall(after),
                after
        );
    }

    @Test
    public void testPatchCellsReplacesCell() {
        final SpreadsheetCell cell = SpreadsheetSelection.parseCell("A1")
                .setFormula(SpreadsheetFormula.EMPTY);

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
                .setCells(
                        Sets.of(cell)
                );

        final SpreadsheetDelta after = before.setCells(
                Sets.of(
                        cell.setFormula(SpreadsheetFormula.EMPTY.setText("=1"))
                )
        );

        this.patchCellsAndCheck(
                before,
                cell.reference(),
                marshall(after),
                after
        );
    }

    @Test
    public void testPatchCellsCellsRemoved() {
        final SpreadsheetDelta without = SpreadsheetDelta.EMPTY
                .setViewportSelection(
                        Optional.of(
                                SpreadsheetSelection.parseCellRange("A1:B2")
                                        .setAnchor(SpreadsheetViewportSelectionAnchor.TOP_LEFT)
                        )
                );
        this.patchCellsAndCheck(
                without.setCells(
                        Sets.of(
                                SpreadsheetSelection.parseCell("b2")
                                        .setFormula(SpreadsheetFormula.EMPTY)
                        )
                ),
                SpreadsheetSelection.parseCellOrCellRange("B2"),
                JsonNode.object()
                        .set(
                                SpreadsheetDelta.CELLS_PROPERTY,
                                JsonNode.nullNode()
                        ),
                without
        );
    }

    @Test
    public void testPatchCellsCellWithWindow() {
        final SpreadsheetCell a1 = SpreadsheetSelection.parseCell("A1")
                .setFormula(SpreadsheetFormula.EMPTY);
        final SpreadsheetCell b2 = SpreadsheetSelection.parseCell("B2")
                .setFormula(SpreadsheetFormula.EMPTY);

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
                .setCells(
                        Sets.of(a1, b2)
                ).setWindow(
                        SpreadsheetSelection.parseWindow("A1:A2")
                );

        final SpreadsheetDelta after = before.setCells(
                Sets.of(a1)
        ).setWindow(
                SpreadsheetSelection.parseWindow("A1:A2")
        );

        this.patchCellsAndCheck(
                before,
                SpreadsheetSelection.parseCellOrCellRange("A1:A2"),
                marshall(after),
                after
        );
    }

    @Test
    public void testPatchCellAndFormatFails() {
        final SpreadsheetCell a1 = SpreadsheetSelection.parseCell("A1")
                .setFormula(SpreadsheetFormula.EMPTY);
        final SpreadsheetDelta delta = SpreadsheetDelta.EMPTY.setCells(
                Sets.of(a1)
        );
        final JsonNode patch = this.marshall(delta)
                .objectOrFail()
                .set(
                        SpreadsheetDelta.FORMAT_PROPERTY,
                        this.marshall(SpreadsheetCellFormat.with("@"))
                );

        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetDelta.EMPTY.patchCells(
                        a1.reference(),
                        patch,
                        this.createPatchContext()
                )
        );
        this.checkEquals(
                "Patch must not contain both \"cells\" and \"format\"",
                thrown.getMessage(),
                "message"
        );
    }


    @Test
    public void testPatchCellWithStyleAndFormatFails() {
        final JsonNode patch = JsonNode.object()
                .set(
                        SpreadsheetDelta.FORMAT_PROPERTY,
                        this.marshall(SpreadsheetCellFormat.with("@"))
                ).set(
                        SpreadsheetDelta.STYLE_PROPERTY,
                        this.marshall(TextStyle.EMPTY)
                );

        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetDelta.EMPTY.patchCells(
                        SpreadsheetSelection.parseCellOrCellRange("Z99"),
                        patch,
                        this.createPatchContext()
                )
        );
        this.checkEquals(
                "Patch must not contain both \"format\" and \"style\"",
                thrown.getMessage(),
                "message"
        );
    }

    @Test
    public void testPatchCellsWithFormatWithMissingCells() {
        final Optional<SpreadsheetCellFormat> beforeFormat = Optional.of(
                SpreadsheetCellFormat.with("@before")
        );

        final SpreadsheetCell a1 = SpreadsheetSelection.parseCell("A1")
                .setFormula(SpreadsheetFormula.EMPTY)
                .setFormat(beforeFormat);

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
                .setCells(
                        Sets.of(a1)
                );

        final SpreadsheetCellFormat format = SpreadsheetCellFormat.with("@patched");

        final SpreadsheetDelta after = before.setCells(
                Sets.of(
                        a1.setFormat(Optional.of(format)),
                        SpreadsheetSelection.parseCell("A2")
                                .setFormula(SpreadsheetFormula.EMPTY)
                                .setFormat(Optional.of(format))
                )
        );

        this.patchCellsAndCheck(
                before,
                SpreadsheetSelection.parseCellRange("A1:A2"),
                JsonNode.object()
                        .set(
                                SpreadsheetDelta.FORMAT_PROPERTY,
                                marshall(format)
                        ),
                after
        );
    }

    @Test
    public void testPatchCellsWithFormat() {
        final Optional<SpreadsheetCellFormat> beforeFormat = Optional.of(
                SpreadsheetCellFormat.with("@before")
        );

        final SpreadsheetCell a1 = SpreadsheetSelection.parseCell("A1")
                .setFormula(SpreadsheetFormula.EMPTY)
                .setFormat(beforeFormat);
        final SpreadsheetCell a2 = SpreadsheetSelection.parseCell("A2")
                .setFormula(SpreadsheetFormula.EMPTY)
                .setFormat(beforeFormat);

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
                .setCells(
                        Sets.of(a1, a2)
                );

        final SpreadsheetCellFormat format = SpreadsheetCellFormat.with("@patched");

        final SpreadsheetDelta after = before.setCells(
                Sets.of(
                        a1.setFormat(Optional.of(format)),
                        a2.setFormat(Optional.of(format))
                )
        );

        this.patchCellsAndCheck(
                before,
                SpreadsheetSelection.parseCellRange("A1:A2"),
                JsonNode.object()
                        .set(
                                SpreadsheetDelta.FORMAT_PROPERTY,
                                marshall(format)
                        ),
                after
        );
    }

    @Test
    public void testPatchCellsWithFormatNullClears() {
        final SpreadsheetCell a1 = SpreadsheetSelection.parseCell("A1")
                .setFormula(SpreadsheetFormula.EMPTY);
        final SpreadsheetCell a2 = SpreadsheetSelection.parseCell("A2")
                .setFormula(SpreadsheetFormula.EMPTY);

        final Optional<SpreadsheetCellFormat> format = Optional.of(
                SpreadsheetCellFormat.with("@before")
        );

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
                .setCells(
                        Sets.of(
                                a1.setFormat(format),
                                a2.setFormat(format)
                        )
                );

        final SpreadsheetDelta after = before.setCells(
                Sets.of(
                        a1, a2
                )
        );

        this.patchCellsAndCheck(
                before,
                SpreadsheetSelection.parseCellRange("A1:A2"),
                JsonNode.object()
                        .set(
                                SpreadsheetDelta.FORMAT_PROPERTY,
                                JsonNode.nullNode()
                        ),
                after
        );
    }

    @Test
    public void testPatchCellsWithFormatAndWindow() {
        final Optional<SpreadsheetCellFormat> beforeFormat = Optional.of(
                SpreadsheetCellFormat.with("@before")
        );

        final SpreadsheetCell a1 = SpreadsheetSelection.parseCell("A1")
                .setFormula(SpreadsheetFormula.EMPTY)
                .setFormat(beforeFormat);
        final SpreadsheetCell a2 = SpreadsheetSelection.parseCell("A2")
                .setFormula(SpreadsheetFormula.EMPTY)
                .setFormat(beforeFormat);

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
                .setCells(
                        Sets.of(a1, a2)
                ).setWindow(
                        SpreadsheetSelection.parseWindow("A1:A2")
                );

        final SpreadsheetCellFormat format = SpreadsheetCellFormat.with("@patched");

        final SpreadsheetDelta after = before.setCells(
                Sets.of(
                        a1.setFormat(Optional.of(format)),
                        a2.setFormat(Optional.of(format))
                )
        );

        this.patchCellsAndCheck(
                before,
                SpreadsheetSelection.parseCellRange("A1:A2"),
                JsonNode.object()
                        .set(
                                SpreadsheetDelta.FORMAT_PROPERTY,
                                marshall(format)
                        ),
                after
        );
    }

    @Test
    public void testPatchCellAndStyleFails() {
        final SpreadsheetCell a1 = SpreadsheetSelection.parseCell("A1")
                .setFormula(SpreadsheetFormula.EMPTY);
        final SpreadsheetDelta delta = SpreadsheetDelta.EMPTY.setCells(
                Sets.of(a1)
        );
        final JsonNode patch = this.marshall(delta)
                .objectOrFail()
                .set(
                        SpreadsheetDelta.STYLE_PROPERTY,
                        this.marshall(TextStyle.EMPTY)
                );

        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetDelta.EMPTY.patchCells(
                        a1.reference(),
                        patch,
                        this.createPatchContext()
                )
        );
        this.checkEquals(
                "Patch must not contain both \"cells\" and \"style\"",
                thrown.getMessage(),
                "message"
        );
    }

    @Test
    public void testPatchCellsWithStyleMissing() {
        final SpreadsheetCell a1 = SpreadsheetSelection.parseCell("A1")
                .setFormula(SpreadsheetFormula.EMPTY);

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
                .setCells(
                        Sets.of(a1)
                ).setWindow(
                        SpreadsheetSelection.parseWindow("A1:A2")
                );

        final TextStyle style = TextStyle.EMPTY
                .set(TextStylePropertyName.COLOR, Color.parse("#123456"));

        final SpreadsheetDelta after = before.setCells(
                Sets.of(
                        a1.setStyle(style),
                        SpreadsheetSelection.parseCell("A2")
                                .setFormula(SpreadsheetFormula.EMPTY)
                                .setStyle(style)
                )
        );

        this.patchCellsAndCheck(
                before,
                SpreadsheetSelection.parseCellRange("A1:A2"),
                JsonNode.object()
                        .set(
                                SpreadsheetDelta.STYLE_PROPERTY,
                                marshall(style)
                        ),
                after
        );
    }

    @Test
    public void testPatchCellsWithStyle() {
        final SpreadsheetCell a1 = SpreadsheetSelection.parseCell("A1")
                .setFormula(SpreadsheetFormula.EMPTY);
        final SpreadsheetCell a2 = SpreadsheetSelection.parseCell("A2")
                .setFormula(SpreadsheetFormula.EMPTY);

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
                .setCells(
                        Sets.of(a1, a2)
                ).setWindow(
                        SpreadsheetSelection.parseWindow("A1:A2")
                );

        final TextStyle style = TextStyle.EMPTY
                .set(TextStylePropertyName.COLOR, Color.parse("#123456"));

        final SpreadsheetDelta after = before.setCells(
                Sets.of(
                        a1.setStyle(style),
                        a2.setStyle(style)
                )
        );

        this.patchCellsAndCheck(
                before,
                SpreadsheetSelection.parseCellRange("A1:A2"),
                JsonNode.object()
                        .set(
                                SpreadsheetDelta.STYLE_PROPERTY,
                                marshall(style)
                        ),
                after
        );
    }

    @Test
    public void testPatchCellsWithStyleMerging() {
        final TextStyle beforeStyle = TextStyle.EMPTY
                .set(TextStylePropertyName.FONT_STYLE, FontStyle.ITALIC);

        final SpreadsheetCell a1 = SpreadsheetSelection.parseCell("A1")
                .setFormula(SpreadsheetFormula.EMPTY)
                .setStyle(beforeStyle);
        final SpreadsheetCell a2 = SpreadsheetSelection.parseCell("A2")
                .setFormula(SpreadsheetFormula.EMPTY)
                .setStyle(beforeStyle);

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
                .setCells(
                        Sets.of(a1, a2)
                ).setWindow(
                        SpreadsheetSelection.parseWindow("A1:A2")
                );

        final TextStyle patchStyle = TextStyle.EMPTY
                .set(TextStylePropertyName.COLOR, Color.parse("#123456"));

        final TextStyle patchedStyle = beforeStyle.merge(patchStyle);

        final SpreadsheetDelta after = before.setCells(
                Sets.of(
                        a1.setStyle(patchedStyle),
                        a2.setStyle(patchedStyle)
                )
        );

        this.patchCellsAndCheck(
                before,
                SpreadsheetSelection.parseCellOrCellRange("A1:A2"),
                JsonNode.object()
                        .set(
                                SpreadsheetDelta.STYLE_PROPERTY,
                                marshall(patchStyle)
                        ),
                after
        );
    }

    @Test
    public void testPatchCellsWithStyleNullClears() {
        final SpreadsheetCell a1 = SpreadsheetSelection.parseCell("A1")
                .setFormula(SpreadsheetFormula.EMPTY);
        final SpreadsheetCell a2 = SpreadsheetSelection.parseCell("A2")
                .setFormula(SpreadsheetFormula.EMPTY);

        final TextStyle style = TextStyle.EMPTY
                .set(TextStylePropertyName.FONT_STYLE, FontStyle.ITALIC);

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
                .setCells(
                        Sets.of(
                                a1.setStyle(style),
                                a2.setStyle(style)
                        )
                ).setWindow(
                        SpreadsheetSelection.parseWindow("A1:A2")
                );

        final SpreadsheetDelta after = before.setCells(
                Sets.of(
                        a1, a2
                )
        );

        this.patchCellsAndCheck(
                before,
                SpreadsheetSelection.parseCellRange("A1:A2"),
                JsonNode.object()
                        .set(
                                SpreadsheetDelta.STYLE_PROPERTY,
                                JsonNode.nullNode() // clears previous style (font-style=italics)
                        ),
                after
        );
    }

    private void patchCellsAndCheck(final SpreadsheetDelta before,
                                    final SpreadsheetCellReferenceOrRange cellReferenceOrRange,
                                    final JsonNode patch,
                                    final SpreadsheetDelta expected) {
        this.checkEquals(
                expected,
                before.patchCells(
                        cellReferenceOrRange,
                        patch,
                        this.createPatchContext()
                ),
                () -> "patch cells " + cellReferenceOrRange + "\n" + patch
        );
    }

    // PatchColumns.....................................................................................................

    @Test
    public void testPatchColumnsCellsFails() {
        this.patchColumnInvalidPropertyFails2(
                SpreadsheetDelta.CELLS_PROPERTY,
                JsonNode.nullNode()

        );
    }

    @Test
    public void testPatchColumnsLabelsFails() {
        this.patchColumnInvalidPropertyFails2(
                SpreadsheetDelta.LABELS_PROPERTY,
                JsonNode.nullNode()

        );
    }

    @Test
    public void testPatchColumnsRowsFails() {
        this.patchColumnInvalidPropertyFails2(
                SpreadsheetDelta.ROWS_PROPERTY,
                JsonNode.nullNode()

        );
    }

    @Test
    public void testPatchColumnsDeletedCellsFails() {
        this.patchColumnInvalidPropertyFails2(
                SpreadsheetDelta.DELETED_CELLS_PROPERTY,
                JsonNode.nullNode()
        );
    }

    @Test
    public void testPatchColumnsDeletedRowsFails() {
        this.patchColumnInvalidPropertyFails2(
                SpreadsheetDelta.DELETED_ROWS_PROPERTY,
                JsonNode.nullNode()
        );
    }

    @Test
    public void testPatchColumnsColumnWidthFails() {
        this.patchColumnInvalidPropertyFails2(
                SpreadsheetDelta.COLUMN_WIDTHS_PROPERTY,
                JsonNode.nullNode()
        );
    }

    @Test
    public void testPatchColumnsRowHeightFails() {
        this.patchColumnInvalidPropertyFails2(
                SpreadsheetDelta.ROW_HEIGHTS_PROPERTY,
                JsonNode.nullNode()
        );
    }

    private void patchColumnInvalidPropertyFails2(final JsonPropertyName key,
                                                  final JsonNode value) {
        final JsonNode patch = JsonNode.object()
                .set(
                        key,
                        value
                );

        final InvalidPropertyJsonNodeException thrown = assertThrows(
                InvalidPropertyJsonNodeException.class,
                () -> SpreadsheetDelta.EMPTY.patchColumns(patch, this.createPatchContext())
        );
        this.checkEquals(key, thrown.name(), "name");
        this.checkEquals(value.removeParent(), thrown.node().removeParent(), "node");
    }

    @Test
    public void testPatchColumnsWithEmptyObject() {
        this.patchColumnsAndCheck(
                SpreadsheetDelta.EMPTY,
                JsonNode.object(),
                SpreadsheetDelta.EMPTY
        );
    }

    @Test
    public void testPatchColumnsNoSelection() {
        this.patchColumnSelectionAndCheck(
                SpreadsheetDelta.NO_VIEWPORT_SELECTION,
                SpreadsheetDelta.NO_VIEWPORT_SELECTION
        );
    }

    @Test
    public void testPatchColumnsSelection() {
        this.patchColumnSelectionAndCheck(
                SpreadsheetDelta.NO_VIEWPORT_SELECTION,
                Optional.of(
                        SpreadsheetSelection.parseColumn("C")
                                .setAnchor(SpreadsheetViewportSelectionAnchor.NONE)
                )
        );
    }

    private void patchColumnSelectionAndCheck(final Optional<SpreadsheetViewportSelection> before,
                                              final Optional<SpreadsheetViewportSelection> after) {
        final SpreadsheetDelta delta = SpreadsheetDelta.EMPTY
                .setViewportSelection(after);

        this.patchColumnsAndCheck(
                SpreadsheetDelta.EMPTY
                        .setViewportSelection(before),
                marshall(delta),
                delta
        );
    }

    @Test
    public void testPatchColumnsSelectionCleared() {
        this.patchColumnsAndCheck(
                SpreadsheetDelta.EMPTY
                        .setViewportSelection(
                                Optional.of(
                                        SpreadsheetSelection.parseCellRange("A1:B2")
                                                .setAnchor(SpreadsheetViewportSelectionAnchor.BOTTOM_RIGHT)
                                )
                        )
                ,
                JsonNode.object()
                        .set(
                                SpreadsheetDelta.VIEWPORT_SELECTION_PROPERTY,
                                JsonNode.nullNode()
                        ),
                SpreadsheetDelta.EMPTY
        );
    }

    @Test
    public void testPatchColumnsNewColumn() {
        final SpreadsheetColumn column = SpreadsheetSelection.parseColumn("Z")
                .column();
        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY;

        final SpreadsheetDelta after = before.setColumns(
                Sets.of(
                        column
                )
        );

        this.patchColumnsAndCheck(
                before,
                marshall(after),
                after
        );
    }

    @Test
    public void testPatchColumnsNewColumn2() {
        final SpreadsheetColumn column = SpreadsheetSelection.parseColumn("Z")
                .column();
        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY;

        final SpreadsheetDelta after = before.setColumns(
                Sets.of(
                        column
                )
        );

        this.patchColumnsAndCheck(
                before,
                marshall(after),
                after
        );
    }

    @Test
    public void testPatchColumnsReplacesColumn() {
        final SpreadsheetColumn column = SpreadsheetSelection.parseColumn("Z")
                .column();
        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
                .setColumns(
                        Sets.of(column.setHidden(false))
                );

        final SpreadsheetDelta after = before.setColumns(
                Sets.of(
                        column.setHidden(true)
                )
        );

        this.patchColumnsAndCheck(
                before,
                marshall(after),
                after
        );
    }

    @Test
    public void testPatchColumnsReplacesColumn2() {
        final SpreadsheetColumn column = SpreadsheetSelection.parseColumn("Z")
                .column();
        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
                .setColumns(
                        Sets.of(column.setHidden(true))
                );

        final SpreadsheetDelta after = before.setColumns(
                Sets.of(
                        column.setHidden(false)
                )
        );

        this.patchColumnsAndCheck(
                before,
                marshall(after),
                after
        );
    }

    @Test
    public void testPatchColumnsCellsRemoved() {
        final SpreadsheetDelta without = SpreadsheetDelta.EMPTY
                .setViewportSelection(
                        Optional.of(
                                SpreadsheetSelection.parseCellRange("A1:B2")
                                        .setAnchor(SpreadsheetViewportSelectionAnchor.TOP_LEFT)
                        )
                );
        this.patchColumnsAndCheck(
                without.setColumns(
                        Sets.of(
                                SpreadsheetSelection.parseColumn("b")
                                        .column()
                        )
                )
                ,
                JsonNode.object()
                        .set(
                                SpreadsheetDelta.COLUMNS_PROPERTY,
                                JsonNode.nullNode()
                        ),
                without
        );
    }

    @Test
    public void testPatchColumnsColumnWithWindow() {
        final SpreadsheetColumn a = SpreadsheetSelection.parseColumn("a")
                .column();
        final SpreadsheetColumn b = SpreadsheetSelection.parseColumn("b")
                .column();

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
                .setColumns(
                        Sets.of(a, b)
                ).setWindow(
                        SpreadsheetSelection.parseWindow("A1:B2")
                );

        final SpreadsheetDelta after = before.setColumns(
                Sets.of(a)
        ).setWindow(
                SpreadsheetSelection.parseWindow("A1")
        );

        this.patchColumnsAndCheck(
                before,
                marshall(after),
                after
        );
    }

    private void patchColumnsAndCheck(final SpreadsheetDelta before,
                                      final JsonNode patch,
                                      final SpreadsheetDelta expected) {
        this.checkEquals(
                expected,
                before.patchColumns(patch, this.createPatchContext()),
                () -> "patch columns\n" + patch
        );
    }

    // PatchRows.....................................................................................................

    @Test
    public void testPatchRowsCellsFails() {
        this.patchRowInvalidPropertyFails2(
                SpreadsheetDelta.CELLS_PROPERTY,
                JsonNode.nullNode()
        );
    }

    @Test
    public void testPatchRowsColumnsFails() {
        this.patchRowInvalidPropertyFails2(
                SpreadsheetDelta.COLUMNS_PROPERTY,
                JsonNode.nullNode()
        );
    }

    @Test
    public void testPatchRowsLabelsFails() {
        this.patchRowInvalidPropertyFails2(
                SpreadsheetDelta.LABELS_PROPERTY,
                JsonNode.nullNode()

        );
    }

    @Test
    public void testPatchRowsDeletedCellsFails() {
        this.patchRowInvalidPropertyFails2(
                SpreadsheetDelta.DELETED_CELLS_PROPERTY,
                JsonNode.nullNode()
        );
    }

    @Test
    public void testPatchRowsDeletedColumnsFails() {
        this.patchRowInvalidPropertyFails2(
                SpreadsheetDelta.DELETED_COLUMNS_PROPERTY,
                JsonNode.nullNode()
        );
    }

    @Test
    public void testPatchRowsColumnWidthFails() {
        this.patchRowInvalidPropertyFails2(
                SpreadsheetDelta.COLUMN_WIDTHS_PROPERTY,
                JsonNode.nullNode()
        );
    }

    @Test
    public void testPatchRowsHeightFails() {
        this.patchRowInvalidPropertyFails2(
                SpreadsheetDelta.ROW_HEIGHTS_PROPERTY,
                JsonNode.nullNode()
        );
    }

    private void patchRowInvalidPropertyFails2(final JsonPropertyName key,
                                               final JsonNode value) {
        final JsonNode patch = JsonNode.object()
                .set(
                        key,
                        value
                );

        final InvalidPropertyJsonNodeException thrown = assertThrows(
                InvalidPropertyJsonNodeException.class,
                () -> SpreadsheetDelta.EMPTY.patchRows(patch, this.createPatchContext())
        );
        this.checkEquals(key, thrown.name(), "name");
        this.checkEquals(value.removeParent(), thrown.node().removeParent(), "node");
    }

    @Test
    public void testPatchRowsWithEmptyObject() {
        this.patchRowsAndCheck(
                SpreadsheetDelta.EMPTY,
                JsonNode.object(),
                SpreadsheetDelta.EMPTY
        );
    }

    @Test
    public void testPatchRowsNoSelection() {
        this.patchRowSelectionAndCheck(
                SpreadsheetDelta.NO_VIEWPORT_SELECTION,
                SpreadsheetDelta.NO_VIEWPORT_SELECTION
        );
    }

    @Test
    public void testPatchRowsSelection() {
        this.patchRowSelectionAndCheck(
                SpreadsheetDelta.NO_VIEWPORT_SELECTION,
                Optional.of(
                        SpreadsheetSelection.parseRow("3")
                                .setAnchor(SpreadsheetViewportSelectionAnchor.NONE)
                )
        );
    }

    private void patchRowSelectionAndCheck(final Optional<SpreadsheetViewportSelection> before,
                                           final Optional<SpreadsheetViewportSelection> after) {
        final SpreadsheetDelta delta = SpreadsheetDelta.EMPTY
                .setViewportSelection(after);

        this.patchRowsAndCheck(
                SpreadsheetDelta.EMPTY
                        .setViewportSelection(before),
                marshall(delta),
                delta
        );
    }

    @Test
    public void testPatchRowsSelectionCleared() {
        this.patchRowsAndCheck(
                SpreadsheetDelta.EMPTY
                        .setViewportSelection(
                                Optional.of(
                                        SpreadsheetSelection.parseCellRange("A1:B2")
                                                .setAnchor(SpreadsheetViewportSelectionAnchor.BOTTOM_RIGHT)
                                )
                        )
                ,
                JsonNode.object()
                        .set(
                                SpreadsheetDelta.VIEWPORT_SELECTION_PROPERTY,
                                JsonNode.nullNode()
                        ),
                SpreadsheetDelta.EMPTY
        );
    }

    @Test
    public void testPatchRowsNewRow() {
        final SpreadsheetRow row = SpreadsheetSelection.parseRow("9")
                .row();
        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY;

        final SpreadsheetDelta after = before.setRows(
                Sets.of(
                        row
                )
        );

        this.patchRowsAndCheck(
                before,
                marshall(after),
                after
        );
    }

    @Test
    public void testPatchRowsNewRow2() {
        final SpreadsheetRow row = SpreadsheetSelection.parseRow("9")
                .row();
        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY;

        final SpreadsheetDelta after = before.setRows(
                Sets.of(
                        row
                )
        );

        this.patchRowsAndCheck(
                before,
                marshall(after),
                after
        );
    }

    @Test
    public void testPatchRowsReplacesRow() {
        final SpreadsheetRow row = SpreadsheetSelection.parseRow("9")
                .row();
        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
                .setRows(
                        Sets.of(
                                row.setHidden(false)
                        )
                );

        final SpreadsheetDelta after = before.setRows(
                Sets.of(
                        row.setHidden(true)
                )
        );

        this.patchRowsAndCheck(
                before,
                marshall(after),
                after
        );
    }

    @Test
    public void testPatchRowsReplacesRow2() {
        final SpreadsheetRow row = SpreadsheetSelection.parseRow("9")
                .row();
        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
                .setRows(
                        Sets.of(row.setHidden(true))
                );

        final SpreadsheetDelta after = before.setRows(
                Sets.of(
                        row.setHidden(false)
                )
        );

        this.patchRowsAndCheck(
                before,
                marshall(after),
                after
        );
    }

    @Test
    public void testPatchRowsCellsRemoved() {
        final SpreadsheetDelta without = SpreadsheetDelta.EMPTY
                .setViewportSelection(
                        Optional.of(
                                SpreadsheetSelection.parseCellRange("A1:B2")
                                        .setAnchor(SpreadsheetViewportSelectionAnchor.TOP_LEFT)
                        )
                );
        this.patchRowsAndCheck(
                without.setRows(
                        Sets.of(
                                SpreadsheetSelection.parseRow("2")
                                        .row()
                        )
                )
                ,
                JsonNode.object()
                        .set(
                                SpreadsheetDelta.ROWS_PROPERTY,
                                JsonNode.nullNode()
                        ),
                without
        );
    }

    @Test
    public void testPatchRowsRowWithWindow() {
        final SpreadsheetRow row1 = SpreadsheetSelection.parseRow("1")
                .row();
        final SpreadsheetRow row2 = SpreadsheetSelection.parseRow("2")
                .row();

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
                .setRows(
                        Sets.of(row1, row2)
                ).setWindow(
                        SpreadsheetSelection.parseWindow("A1:B2")
                );

        final SpreadsheetDelta after = before.setRows(
                Sets.of(row1)
        ).setWindow(
                SpreadsheetSelection.parseWindow("A1")
        );

        this.patchRowsAndCheck(
                before,
                marshall(after),
                after
        );
    }

    private void patchRowsAndCheck(final SpreadsheetDelta before,
                                   final JsonNode patch,
                                   final SpreadsheetDelta expected) {
        this.checkEquals(
                expected,
                before.patchRows(patch, this.createPatchContext()),
                () -> "patch rows\n" + patch
        );
    }

    private JsonNode marshall(final Object object) {
        return JsonNodeMarshallContexts.basic()
                .marshall(object);
    }

    @Override
    public SpreadsheetDelta createPatchable() {
        return SpreadsheetDelta.EMPTY;
    }

    @Override
    public JsonNode createPatch() {
        return JsonNode.object();
    }

    @Override
    public JsonNodeUnmarshallContext createPatchContext() {
        return JsonNodeUnmarshallContexts.basic(
                ExpressionNumberKind.BIG_DECIMAL,
                MathContext.UNLIMITED
        );
    }

    // resolveCellLabels.....................................................................................................

    final Function<SpreadsheetLabelName, SpreadsheetCellReference> LABEL_TO_CELL = (l) -> {
        throw new UnsupportedOperationException();
    };

    @Test
    public void testResolveLabelsNullJsonFails() {
        Assertions.assertThrows(
                NullPointerException.class,
                () ->
                        SpreadsheetDelta.resolveCellLabels(null, LABEL_TO_CELL)
        );
    }

    @Test
    public void testResolveLabelsNullCellToLabelsFails() {
        Assertions.assertThrows(
                NullPointerException.class,
                () ->
                        SpreadsheetDelta.resolveCellLabels(JsonNode.object(), null)
        );
    }

    @Test
    public void testResolveLabelsCellPropertyAbsent() {
        this.resolveCellLabelsAndCheck(
                JsonNode.object(),
                LABEL_TO_CELL
        );
    }

    @Test
    public void testResolveLabelsCellPropertyNull() {
        this.resolveCellLabelsAndCheck(
                JsonNode.object()
                        .set(SpreadsheetDelta.CELLS_PROPERTY, JsonNode.nullNode()),
                LABEL_TO_CELL
        );
    }

    @Test
    public void testResolveLabelsOnlyCells() {
        final SpreadsheetDelta delta = SpreadsheetDelta.EMPTY
                .setCells(
                        Sets.of(
                                SpreadsheetSelection.parseCell("A1")
                                        .setFormula(
                                                SpreadsheetFormula.EMPTY
                                        )
                        )
                );

        this.resolveCellLabelsAndCheck(
                marshall(delta).objectOrFail(),
                LABEL_TO_CELL
        );
    }


    @Test
    public void testResolveLabelsOnlyCells2() {
        final SpreadsheetDelta delta = SpreadsheetDelta.EMPTY
                .setCells(
                        Sets.of(
                                SpreadsheetSelection.parseCell("A1")
                                        .setFormula(
                                                SpreadsheetFormula.EMPTY
                                        ),
                                SpreadsheetSelection.parseCell("B2")
                                        .setFormula(
                                                SpreadsheetFormula.EMPTY
                                        )
                        )
                );

        this.resolveCellLabelsAndCheck(
                marshall(delta).objectOrFail(),
                LABEL_TO_CELL
        );
    }

    @Test
    public void testResolveLabelsIncludesLabel() {
        final SpreadsheetCellReference z99 = SpreadsheetSelection.parseCell("Z99");

        final SpreadsheetDelta delta = SpreadsheetDelta.EMPTY
                .setCells(
                        Sets.of(
                                z99.setFormula(
                                        SpreadsheetFormula.EMPTY
                                ),
                                SpreadsheetSelection.parseCell("B2")
                                        .setFormula(
                                                SpreadsheetFormula.EMPTY
                                        )
                        )
                );
        final JsonObject json = marshall(delta).objectOrFail();

        final String label = "Label123";
        final JsonObject jsonWithLabel = JsonNode.parse(
                json.toString().replace("Z99", label)
        ).objectOrFail();

        this.resolveCellLabelsAndCheck(
                jsonWithLabel,
                (l) -> {
                    this.checkEquals(label, l.value(), "label");
                    return z99;
                },
                json
        );
    }

    private void resolveCellLabelsAndCheck(final JsonObject json,
                                           final Function<SpreadsheetLabelName, SpreadsheetCellReference> labelToCell) {
        this.resolveCellLabelsAndCheck(
                json,
                labelToCell,
                json
        );
    }

    private void resolveCellLabelsAndCheck(final JsonObject json,
                                           final Function<SpreadsheetLabelName, SpreadsheetCellReference> labelToCell,
                                           final JsonObject expected) {
        this.checkEquals(
                expected,
                SpreadsheetDelta.resolveCellLabels(json, labelToCell),
                () -> "resolveCellLabels " + json
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetDelta> type() {
        return SpreadsheetDelta.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}

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
import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetRow;
import walkingkooka.spreadsheet.SpreadsheetViewportWindows;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetParsePattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReferenceOrRange;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.reference.SpreadsheetViewport;
import walkingkooka.spreadsheet.reference.SpreadsheetViewportAnchor;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.json.InvalidPropertyJsonNodeException;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonObject;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
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
                SpreadsheetViewportWindows.EMPTY,
                SpreadsheetDelta.NO_WINDOW
        );
    }

    // setLabel.........................................................................................................

    @Test
    public void testSetLabelsWithLabelRangeInsideWindow() {
        final Set<SpreadsheetLabelMapping> mappings = this.labelMappingsLabel123ToC3E5();

        final SpreadsheetDelta delta = SpreadsheetDelta.EMPTY
                .setWindow(SpreadsheetViewportWindows.parse("B2:F6"))
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
                .setWindow(SpreadsheetViewportWindows.parse("B2:D4"))
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
                .setWindow(SpreadsheetViewportWindows.parse("A1:B2"))
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
                SpreadsheetSelection.A1,
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
        return SpreadsheetSelection.A1
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

    // Patch argument factories.........................................................................................

    // formatPatternPatch...............................................................................................

    @Test
    public void testFormatPatternPatchWithNullContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetDelta.formatPatternPatch(
                        SpreadsheetPattern.parseDateFormatPattern("ddmmyyyyy"),
                        null
                )
        );
    }

    @Test
    public void testFormatPatternPatch() {
        final SpreadsheetFormatPattern pattern = SpreadsheetFormatPattern.parseTextFormatPattern("@@");

        this.checkEquals(
                JsonNode.object()
                        .set(
                                SpreadsheetDelta.FORMAT_PATTERN_PROPERTY,
                                marshallWithType(pattern)
                        )
                ,
                SpreadsheetDelta.formatPatternPatch(
                        pattern,
                        MARSHALL_CONTEXT
                )
        );
    }

    @Test
    public void testFormatPatternPatchWithNullPattern() {
        this.checkEquals(
                JsonNode.object()
                        .set(
                                SpreadsheetDelta.FORMAT_PATTERN_PROPERTY,
                                JsonNode.nullNode()
                        )
                ,
                SpreadsheetDelta.formatPatternPatch(
                        null,
                        MARSHALL_CONTEXT
                )
        );
    }

    // parsePatternPatch...............................................................................................

    @Test
    public void testParsePatternPatchWithNullContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetDelta.parsePatternPatch(
                        SpreadsheetPattern.parseDateParsePattern("ddmmyyyyy"),
                        null
                )
        );
    }

    @Test
    public void testParsePatternPatch() {
        final SpreadsheetParsePattern pattern = SpreadsheetParsePattern.parseNumberParsePattern("0.00");

        this.checkEquals(
                JsonNode.object()
                        .set(
                                SpreadsheetDelta.PARSE_PATTERN_PROPERTY,
                                marshallWithType(pattern)
                        )
                ,
                SpreadsheetDelta.parsePatternPatch(
                        pattern,
                        MARSHALL_CONTEXT
                )
        );
    }

    @Test
    public void testParsePatternPatchWithNullPattern() {
        this.checkEquals(
                JsonNode.object()
                        .set(
                                SpreadsheetDelta.PARSE_PATTERN_PROPERTY,
                                JsonNode.nullNode()
                        )
                ,
                SpreadsheetDelta.parsePatternPatch(
                        null,
                        MARSHALL_CONTEXT
                )
        );
    }
    // stylePatch.......................................................................................................

    @Test
    public void testStylePatchWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetDelta.stylePatch(
                        null
                )
        );
    }

    @Test
    public void testStylePatchSetOrReplace() {
        final TextStylePropertyName<Color> propertyName = TextStylePropertyName.COLOR;
        final Color color = Color.parse("#123456");

        final JsonNode stylePatch = propertyName.patch(color);

        final JsonObject deltaPatch = SpreadsheetDelta.stylePatch(stylePatch);
        this.stylePatchAndCheck(
                deltaPatch,
                JsonNode.object()
                        .set(
                                SpreadsheetDelta.STYLE_PROPERTY,
                                stylePatch
                        )
        );

        // cell = 1+2 with background-color=#ffffff
        final TextStyle style = TextStyle.EMPTY.set(
                TextStylePropertyName.BACKGROUND_COLOR,
                Color.parse("#ffffff")
        );

        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(
                SpreadsheetFormula.EMPTY
                        .setText("=1+2")
        );

        this.patchCellsAndCheck(
                SpreadsheetDelta.EMPTY.setCells(
                        Sets.of(
                                cell.setStyle(style)
                        )
                ),
                cell.reference(),
                deltaPatch,
                SpreadsheetDelta.EMPTY.setCells(
                        Sets.of(
                                cell.setStyle(
                                        style.set(
                                                propertyName,
                                                color
                                        )
                                )
                        )
                )
        );
    }

    @Test
    public void testStylePatchRemoves() {
        final TextStylePropertyName<Color> propertyName = TextStylePropertyName.COLOR;
        final Color color = null;

        final JsonNode stylePatch = propertyName.patch(color);

        final JsonObject deltaPatch = SpreadsheetDelta.stylePatch(stylePatch);
        this.stylePatchAndCheck(
                deltaPatch,
                JsonNode.object()
                        .set(
                                SpreadsheetDelta.STYLE_PROPERTY,
                                stylePatch
                        )
        );

        // cell = 1+2 with background-color=#ffffff
        final TextStyle style = TextStyle.EMPTY.set(
                TextStylePropertyName.BACKGROUND_COLOR,
                Color.parse("#ffffff")
        ).set(
                propertyName,
                Color.BLACK
        );

        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(
                SpreadsheetFormula.EMPTY
                        .setText("=1+2")
        );

        this.patchCellsAndCheck(
                SpreadsheetDelta.EMPTY.setCells(
                        Sets.of(
                                cell.setStyle(style)
                        )
                ),
                cell.reference(),
                deltaPatch,
                SpreadsheetDelta.EMPTY.setCells(
                        Sets.of(
                                cell.setStyle(
                                        style.setOrRemove(
                                                propertyName,
                                                color
                                        )
                                )
                        )
                )
        );
    }

    private void stylePatchAndCheck(final JsonNode patch,
                                    final JsonNode expected) {
        this.checkEquals(
                expected,
                patch,
                () -> "stylePatch " + patch
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
    public void testPatchAllStyleMissingSelectionFails() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetDelta.EMPTY.patch(
                        SpreadsheetDelta.stylePatch(
                                TextStylePropertyName.COLOR.patch(null)
                        ),
                        JsonNodeUnmarshallContexts.fake()
                )
        );

        this.checkEquals(
                "Patch includes \"style\" but is missing selection.",
                thrown.getMessage(),
                "message"
        );
    }

    @Test
    public void testPatchAllNoViewport() {
        this.patchViewportAndCheck(
                SpreadsheetDelta.NO_VIEWPORT_SELECTION,
                SpreadsheetDelta.NO_VIEWPORT_SELECTION
        );
    }

    @Test
    public void testPatchAllViewport() {
        this.patchViewportAndCheck(
                SpreadsheetDelta.NO_VIEWPORT_SELECTION,
                Optional.of(
                        SpreadsheetSelection.parseColumn("C")
                                .setAnchor(SpreadsheetViewportAnchor.NONE)
                )
        );
    }

    private void patchViewportAndCheck(final Optional<SpreadsheetViewport> before,
                                                final Optional<SpreadsheetViewport> after) {
        final SpreadsheetDelta delta = SpreadsheetDelta.EMPTY
                .setViewport(after);

        this.patchAndCheck(
                SpreadsheetDelta.EMPTY
                        .setViewport(before),
                marshall(delta),
                delta
        );
    }

    @Test
    public void testPatchAllViewportCleared() {
        this.patchAndCheck(
                SpreadsheetDelta.EMPTY
                        .setViewport(
                                Optional.of(
                                        SpreadsheetSelection.parseCellRange("A1:B2")
                                                .setAnchor(SpreadsheetViewportAnchor.BOTTOM_RIGHT)
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
                .setViewport(
                        Optional.of(
                                SpreadsheetSelection.parseCellRange("A1:B2")
                                        .setAnchor(
                                                SpreadsheetViewportAnchor.TOP_LEFT
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

        final SpreadsheetViewportWindows afterWindow = SpreadsheetViewportWindows.parse("A1");
        final SpreadsheetDelta after = before.setWindow(afterWindow);

        this.patchAndCheck(
                before,
                marshall(after),
                before.setWindow(afterWindow)
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
                        SpreadsheetViewportWindows.parse("A1:B2")
                );

        final SpreadsheetViewportWindows afterWindow = SpreadsheetViewportWindows.parse("A1");

        final SpreadsheetDelta after = before.setWindow(afterWindow);

        this.patchAndCheck(
                before,
                marshall(after),
                before.setWindow(afterWindow)
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
                        SpreadsheetViewportWindows.parse("A1:B2")
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
                        SpreadsheetSelection.A1,
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
        this.patchCellViewportAndCheck(
                SpreadsheetDelta.NO_VIEWPORT_SELECTION,
                SpreadsheetDelta.NO_VIEWPORT_SELECTION
        );
    }

    @Test
    public void testPatchCellsViewport() {
        this.patchCellViewportAndCheck(
                SpreadsheetDelta.NO_VIEWPORT_SELECTION,
                Optional.of(
                        SpreadsheetSelection.parseColumn("C")
                                .setAnchor(SpreadsheetViewportAnchor.NONE)
                )
        );
    }

    private void patchCellViewportAndCheck(final Optional<SpreadsheetViewport> before,
                                                    final Optional<SpreadsheetViewport> after) {
        final SpreadsheetDelta delta = SpreadsheetDelta.EMPTY
                .setViewport(after);

        this.patchCellsAndCheck(
                SpreadsheetDelta.EMPTY
                        .setViewport(before),
                SpreadsheetSelection.parseCell("Z99"),
                marshall(delta),
                delta
        );
    }

    @Test
    public void testPatchCellsViewportCleared() {
        this.patchCellsAndCheck(
                SpreadsheetDelta.EMPTY
                        .setViewport(
                                Optional.of(
                                        SpreadsheetSelection.parseCellRange("A1:B2")
                                                .setAnchor(SpreadsheetViewportAnchor.BOTTOM_RIGHT)
                                )
                        )
                ,
                SpreadsheetSelection.A1,
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
        final SpreadsheetCell cell = SpreadsheetSelection.A1
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
        final SpreadsheetCell cell = SpreadsheetSelection.A1
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
        final SpreadsheetCell cell = SpreadsheetSelection.A1
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
                .setViewport(
                        Optional.of(
                                SpreadsheetSelection.parseCellRange("A1:B2")
                                        .setAnchor(SpreadsheetViewportAnchor.TOP_LEFT)
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
        final SpreadsheetCell a1 = SpreadsheetSelection.A1
                .setFormula(SpreadsheetFormula.EMPTY);
        final SpreadsheetCell b2 = SpreadsheetSelection.parseCell("B2")
                .setFormula(SpreadsheetFormula.EMPTY);

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
                .setCells(
                        Sets.of(a1, b2)
                ).setWindow(
                        SpreadsheetViewportWindows.parse("A1:A2")
                );

        final SpreadsheetDelta after = before.setCells(
                Sets.of(a1)
        ).setWindow(
                SpreadsheetViewportWindows.parse("A1:A2")
        );

        this.patchCellsAndCheck(
                before,
                SpreadsheetSelection.parseCellOrCellRange("A1:A2"),
                marshall(after),
                after
        );
    }

    @Test
    public void testPatchCellAndFormatPatternFails() {
        final SpreadsheetCell a1 = SpreadsheetSelection.A1
                .setFormula(SpreadsheetFormula.EMPTY);
        final SpreadsheetDelta delta = SpreadsheetDelta.EMPTY.setCells(
                Sets.of(a1)
        );
        final JsonNode patch = this.marshall(delta)
                .objectOrFail()
                .merge(
                        SpreadsheetDelta.formatPatternPatch(
                                SpreadsheetPattern.parseTextFormatPattern("@"),
                                MARSHALL_CONTEXT
                        )
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
                "Patch must not contain both \"cells\" and \"format-pattern\"",
                thrown.getMessage(),
                "message"
        );
    }

    @Test
    public void testPatchCellAndParsePatternFails() {
        final SpreadsheetCell a1 = SpreadsheetSelection.A1
                .setFormula(SpreadsheetFormula.EMPTY);
        final SpreadsheetDelta delta = SpreadsheetDelta.EMPTY.setCells(
                Sets.of(a1)
        );
        final JsonNode patch = this.marshall(delta)
                .objectOrFail()
                .merge(
                        SpreadsheetDelta.parsePatternPatch(
                                SpreadsheetPattern.parseDateParsePattern("dd/mm/yyyy"),
                                MARSHALL_CONTEXT
                        )
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
                "Patch must not contain both \"cells\" and \"parse-pattern\"",
                thrown.getMessage(),
                "message"
        );
    }

    @Test
    public void testPatchCellFormatPatternAndParsePatternFails() {
        final JsonNode patch = SpreadsheetDelta.formatPatternPatch(
                SpreadsheetPattern.parseTextFormatPattern("@"),
                MARSHALL_CONTEXT
        ).merge(
                SpreadsheetDelta.parsePatternPatch(
                        SpreadsheetPattern.parseDateParsePattern("dd/mm/yyyy"),
                        MARSHALL_CONTEXT
                )
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
                "Patch must not contain both \"format-pattern\" and \"parse-pattern\"",
                thrown.getMessage(),
                "message"
        );
    }

    @Test
    public void testPatchCellWithFormatPatternAndStyleFails() {
        final JsonNode patch = SpreadsheetDelta.formatPatternPatch(
                SpreadsheetPattern.parseTextFormatPattern("@"),
                MARSHALL_CONTEXT
        ).merge(
                SpreadsheetDelta.stylePatch(
                        JsonNode.object()
                )
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
                "Patch must not contain both \"format-pattern\" and \"style\"",
                thrown.getMessage(),
                "message"
        );
    }

    @Test
    public void testPatchCellWithParsePatternAndStyleFails() {
        final JsonNode patch = SpreadsheetDelta.parsePatternPatch(
                SpreadsheetPattern.parseDateParsePattern("dd/mm/yyyy"),
                MARSHALL_CONTEXT
        ).merge(
                SpreadsheetDelta.stylePatch(
                        JsonNode.object()
                )
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
                "Patch must not contain both \"parse-pattern\" and \"style\"",
                thrown.getMessage(),
                "message"
        );
    }

    @Test
    public void testPatchCellsWithFormatPatternWithMissingCells() {
        final Optional<SpreadsheetFormatPattern> beforeFormat = Optional.of(
                SpreadsheetPattern.parseTextFormatPattern("@\"before\"")
        );

        final SpreadsheetCell a1 = SpreadsheetSelection.A1
                .setFormula(SpreadsheetFormula.EMPTY)
                .setFormatPattern(beforeFormat);

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
                .setCells(
                        Sets.of(a1)
                );

        final SpreadsheetFormatPattern formatPattern = SpreadsheetPattern.parseTextFormatPattern("@\"patched\"");

        final SpreadsheetDelta after = before.setCells(
                Sets.of(
                        a1.setFormatPattern(Optional.of(formatPattern)),
                        SpreadsheetSelection.parseCell("A2")
                                .setFormula(SpreadsheetFormula.EMPTY)
                                .setFormatPattern(Optional.of(formatPattern))
                )
        );

        this.patchCellsAndCheck(
                before,
                SpreadsheetSelection.parseCellRange("A1:A2"),
                SpreadsheetDelta.formatPatternPatch(
                        formatPattern,
                        MARSHALL_CONTEXT
                ),
                after
        );
    }

    @Test
    public void testPatchCellsWithFormatPattern() {
        final Optional<SpreadsheetFormatPattern> beforeFormat = Optional.of(
                SpreadsheetPattern.parseTextFormatPattern("@\"before\"")
        );

        final SpreadsheetCell a1 = SpreadsheetSelection.A1
                .setFormula(SpreadsheetFormula.EMPTY)
                .setFormatPattern(beforeFormat);
        final SpreadsheetCell a2 = SpreadsheetSelection.parseCell("A2")
                .setFormula(SpreadsheetFormula.EMPTY)
                .setFormatPattern(beforeFormat);

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
                .setCells(
                        Sets.of(a1, a2)
                );

        final SpreadsheetFormatPattern formatPattern = SpreadsheetPattern.parseTextFormatPattern("@\"patched\"");

        final SpreadsheetDelta after = before.setCells(
                Sets.of(
                        a1.setFormatPattern(Optional.of(formatPattern)),
                        a2.setFormatPattern(Optional.of(formatPattern))
                )
        );

        this.patchCellsAndCheck(
                before,
                SpreadsheetSelection.parseCellRange("A1:A2"),
                SpreadsheetDelta.formatPatternPatch(
                        formatPattern,
                        MARSHALL_CONTEXT
                ),
                after
        );
    }

    @Test
    public void testPatchCellsWithFormatPatternNullClears() {
        final SpreadsheetCell a1 = SpreadsheetSelection.A1
                .setFormula(SpreadsheetFormula.EMPTY);
        final SpreadsheetCell a2 = SpreadsheetSelection.parseCell("A2")
                .setFormula(SpreadsheetFormula.EMPTY);

        final Optional<SpreadsheetFormatPattern> format = Optional.of(
                SpreadsheetPattern.parseTextFormatPattern("@\"before\"")
        );

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
                .setCells(
                        Sets.of(
                                a1.setFormatPattern(format),
                                a2.setFormatPattern(format)
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
                SpreadsheetDelta.formatPatternPatch(
                        null,
                        MARSHALL_CONTEXT
                ),
                after
        );
    }

    @Test
    public void testPatchCellsWithFormatPatternNullClears2() {
        final SpreadsheetCell a1 = SpreadsheetSelection.A1
                .setFormula(SpreadsheetFormula.EMPTY);

        final Optional<SpreadsheetFormatPattern> format = Optional.of(
                SpreadsheetPattern.parseTextFormatPattern("@\"before\"")
        );

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
                .setCells(
                        Sets.of(
                                a1.setFormatPattern(format)
                        )
                );

        final SpreadsheetDelta after = before.setCells(
                Sets.of(
                        a1,
                        SpreadsheetSelection.parseCell("A2")
                                .setFormula(SpreadsheetFormula.EMPTY)
                )
        );

        this.patchCellsAndCheck(
                before,
                SpreadsheetSelection.parseCellRange("A1:A2"),
                SpreadsheetDelta.formatPatternPatch(
                        null,
                        MARSHALL_CONTEXT
                ),
                after
        );
    }

    @Test
    public void testPatchCellsWithFormatPatternAndWindow() {
        final Optional<SpreadsheetFormatPattern> beforeFormat = Optional.of(
                SpreadsheetPattern.parseTextFormatPattern("@\"before\"")
        );

        final SpreadsheetCell a1 = SpreadsheetSelection.A1
                .setFormula(SpreadsheetFormula.EMPTY)
                .setFormatPattern(beforeFormat);
        final SpreadsheetCell a2 = SpreadsheetSelection.parseCell("A2")
                .setFormula(SpreadsheetFormula.EMPTY)
                .setFormatPattern(beforeFormat);

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
                .setCells(
                        Sets.of(a1, a2)
                ).setWindow(
                        SpreadsheetViewportWindows.parse("A1:A2")
                );

        final SpreadsheetFormatPattern formatPattern = SpreadsheetPattern.parseTextFormatPattern("@\"patched\"");

        final SpreadsheetDelta after = before.setCells(
                Sets.of(
                        a1.setFormatPattern(Optional.of(formatPattern)),
                        a2.setFormatPattern(Optional.of(formatPattern))
                )
        );

        this.patchCellsAndCheck(
                before,
                SpreadsheetSelection.parseCellRange("A1:A2"),
                SpreadsheetDelta.formatPatternPatch(
                        formatPattern,
                        MARSHALL_CONTEXT
                ),
                after
        );
    }


    @Test
    public void testPatchCellsWithParsePattern() {
        final Optional<SpreadsheetParsePattern> beforeFormat = Optional.of(
                SpreadsheetPattern.parseNumberParsePattern("\"before\"")
        );

        final SpreadsheetCell a1 = SpreadsheetSelection.A1
                .setFormula(SpreadsheetFormula.EMPTY)
                .setParsePattern(beforeFormat);
        final SpreadsheetCell a2 = SpreadsheetSelection.parseCell("A2")
                .setFormula(SpreadsheetFormula.EMPTY)
                .setParsePattern(beforeFormat);

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
                .setCells(
                        Sets.of(a1, a2)
                );

        final SpreadsheetParsePattern parsePattern = SpreadsheetPattern.parseNumberParsePattern("\"patched\"");

        final SpreadsheetDelta after = before.setCells(
                Sets.of(
                        a1.setParsePattern(Optional.of(parsePattern)),
                        a2.setParsePattern(Optional.of(parsePattern))
                )
        );

        this.patchCellsAndCheck(
                before,
                SpreadsheetSelection.parseCellRange("A1:A2"),
                SpreadsheetDelta.parsePatternPatch(
                        parsePattern,
                        MARSHALL_CONTEXT
                ),
                after
        );
    }

    @Test
    public void testPatchCellsWithParsePatternNullClears() {
        final SpreadsheetCell a1 = SpreadsheetSelection.A1
                .setFormula(SpreadsheetFormula.EMPTY);
        final SpreadsheetCell a2 = SpreadsheetSelection.parseCell("A2")
                .setFormula(SpreadsheetFormula.EMPTY);

        final Optional<SpreadsheetParsePattern> format = Optional.of(
                SpreadsheetPattern.parseNumberParsePattern("#\"should be cleared\"")
        );

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
                .setCells(
                        Sets.of(
                                a1.setParsePattern(format),
                                a2.setParsePattern(format)
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
                SpreadsheetDelta.parsePatternPatch(
                        null,
                        MARSHALL_CONTEXT
                ),
                after
        );
    }

    @Test
    public void testPatchCellsWithParsePatternNullClearsAbsentCell() {
        final SpreadsheetCell a1 = SpreadsheetSelection.A1
                .setFormula(SpreadsheetFormula.EMPTY);

        final Optional<SpreadsheetParsePattern> format = Optional.of(
                SpreadsheetPattern.parseNumberParsePattern("#\"should be cleared\"")
        );

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
                .setCells(
                        Sets.of(
                                a1.setParsePattern(format)
                        )
                );

        final SpreadsheetDelta after = before.setCells(
                Sets.of(
                        a1,
                        SpreadsheetSelection.parseCell("A2")
                                .setFormula(SpreadsheetFormula.EMPTY)
                )
        );

        this.patchCellsAndCheck(
                before,
                SpreadsheetSelection.parseCellRange("A1:A2"),
                SpreadsheetDelta.parsePatternPatch(
                        null,
                        MARSHALL_CONTEXT
                ),
                after
        );
    }

    @Test
    public void testPatchCellAndStyleFails() {
        final SpreadsheetCell a1 = SpreadsheetSelection.A1
                .setFormula(SpreadsheetFormula.EMPTY);
        final SpreadsheetDelta delta = SpreadsheetDelta.EMPTY.setCells(
                Sets.of(a1)
        );
        final JsonNode patch = this.marshall(delta)
                .objectOrFail()
                .merge(
                        SpreadsheetDelta.stylePatch(
                                JsonNode.object()
                        )
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
        final SpreadsheetCell a1 = SpreadsheetSelection.A1
                .setFormula(SpreadsheetFormula.EMPTY);

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
                .setCells(
                        Sets.of(a1)
                ).setWindow(
                        SpreadsheetViewportWindows.parse("A1:A2")
                );

        final JsonNode stylePatch = TextStylePropertyName.COLOR.patch(
                Color.parse("#123456")
        );

        final TextStyle style = TextStyle.EMPTY.patch(
                stylePatch,
                this.createPatchContext()
        );

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
                SpreadsheetDelta.stylePatch(
                        stylePatch
                ),
                after
        );
    }

    @Test
    public void testPatchCellsWithStyle() {
        final SpreadsheetCell a1 = SpreadsheetSelection.A1
                .setFormula(SpreadsheetFormula.EMPTY);
        final SpreadsheetCell a2 = SpreadsheetSelection.parseCell("A2")
                .setFormula(SpreadsheetFormula.EMPTY);

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
                .setCells(
                        Sets.of(a1, a2)
                ).setWindow(
                        SpreadsheetViewportWindows.parse("A1:A2")
                );

        final JsonNode stylePatch = TextStylePropertyName.COLOR.patch(
                Color.parse("#123456")
        );
        final TextStyle style = TextStyle.EMPTY
                .patch(
                        stylePatch,
                        this.createPatchContext()
                );

        final SpreadsheetDelta after = before.setCells(
                Sets.of(
                        a1.setStyle(style),
                        a2.setStyle(style)
                )
        );

        this.patchCellsAndCheck(
                before,
                SpreadsheetSelection.parseCellRange("A1:A2"),
                SpreadsheetDelta.stylePatch(stylePatch),
                after
        );
    }

    @Test
    public void testPatchCellsWithStyleMerging() {
        final TextStyle beforeStyle = TextStyle.EMPTY
                .set(TextStylePropertyName.FONT_STYLE, FontStyle.ITALIC);

        final SpreadsheetCell a1 = SpreadsheetSelection.A1
                .setFormula(SpreadsheetFormula.EMPTY)
                .setStyle(beforeStyle);
        final SpreadsheetCell a2 = SpreadsheetSelection.parseCell("A2")
                .setFormula(SpreadsheetFormula.EMPTY)
                .setStyle(beforeStyle);

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
                .setCells(
                        Sets.of(a1, a2)
                ).setWindow(
                        SpreadsheetViewportWindows.parse("A1:A2")
                );

        final JsonNode stylePatch = TextStylePropertyName.COLOR.patch(
                Color.parse("#123456")
        );
        final TextStyle patchStyle = TextStyle.EMPTY
                .patch(
                        stylePatch,
                        this.createPatchContext()
                );

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
                SpreadsheetDelta.stylePatch(
                        stylePatch
                ),
                after
        );
    }

    @Test
    public void testPatchCellsWithStyleNullClears() {
        final SpreadsheetCell a1 = SpreadsheetSelection.A1
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
                        SpreadsheetViewportWindows.parse("A1:A2")
                );

        final SpreadsheetDelta after = before.setCells(
                Sets.of(
                        a1, a2
                )
        );

        this.patchCellsAndCheck(
                before,
                SpreadsheetSelection.parseCellRange("A1:A2"),
                SpreadsheetDelta.stylePatch(
                        JsonNode.nullNode()
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
    public void testPatchColumnsFormatPatternFails() {
        this.patchColumnInvalidPropertyFails2(
                SpreadsheetDelta.FORMAT_PATTERN_PROPERTY,
                JsonNode.nullNode()

        );
    }

    @Test
    public void testPatchColumnsParsePatternFails() {
        this.patchColumnInvalidPropertyFails2(
                SpreadsheetDelta.PARSE_PATTERN_PROPERTY,
                JsonNode.nullNode()

        );
    }

    @Test
    public void testPatchColumnsStyleFails() {
        this.patchColumnInvalidPropertyFails2(
                SpreadsheetDelta.STYLE_PROPERTY,
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
        this.patchColumnViewportAndCheck(
                SpreadsheetDelta.NO_VIEWPORT_SELECTION,
                SpreadsheetDelta.NO_VIEWPORT_SELECTION
        );
    }

    @Test
    public void testPatchColumnsViewport() {
        this.patchColumnViewportAndCheck(
                SpreadsheetDelta.NO_VIEWPORT_SELECTION,
                Optional.of(
                        SpreadsheetSelection.parseColumn("C")
                                .setAnchor(SpreadsheetViewportAnchor.NONE)
                )
        );
    }

    private void patchColumnViewportAndCheck(final Optional<SpreadsheetViewport> before,
                                                      final Optional<SpreadsheetViewport> after) {
        final SpreadsheetDelta delta = SpreadsheetDelta.EMPTY
                .setViewport(after);

        this.patchColumnsAndCheck(
                SpreadsheetDelta.EMPTY
                        .setViewport(before),
                marshall(delta),
                delta
        );
    }

    @Test
    public void testPatchColumnsViewportCleared() {
        this.patchColumnsAndCheck(
                SpreadsheetDelta.EMPTY
                        .setViewport(
                                Optional.of(
                                        SpreadsheetSelection.parseCellRange("A1:B2")
                                                .setAnchor(SpreadsheetViewportAnchor.BOTTOM_RIGHT)
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
                .setViewport(
                        Optional.of(
                                SpreadsheetSelection.parseCellRange("A1:B2")
                                        .setAnchor(SpreadsheetViewportAnchor.TOP_LEFT)
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
                        SpreadsheetViewportWindows.parse("A1:B2")
                );

        final SpreadsheetDelta after = before.setColumns(
                Sets.of(a)
        ).setWindow(
                SpreadsheetViewportWindows.parse("A1")
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
    public void testPatchRowsFormatPatternFails() {
        this.patchRowInvalidPropertyFails2(
                SpreadsheetDelta.FORMAT_PATTERN_PROPERTY,
                JsonNode.nullNode()

        );
    }

    @Test
    public void testPatchRowsParsePatternFails() {
        this.patchRowInvalidPropertyFails2(
                SpreadsheetDelta.PARSE_PATTERN_PROPERTY,
                JsonNode.nullNode()

        );
    }

    @Test
    public void testPatchRowsStyleFails() {
        this.patchRowInvalidPropertyFails2(
                SpreadsheetDelta.STYLE_PROPERTY,
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
        this.patchRowViewportAndCheck(
                SpreadsheetDelta.NO_VIEWPORT_SELECTION,
                SpreadsheetDelta.NO_VIEWPORT_SELECTION
        );
    }

    @Test
    public void testPatchRowsViewport() {
        this.patchRowViewportAndCheck(
                SpreadsheetDelta.NO_VIEWPORT_SELECTION,
                Optional.of(
                        SpreadsheetSelection.parseRow("3")
                                .setAnchor(SpreadsheetViewportAnchor.NONE)
                )
        );
    }

    private void patchRowViewportAndCheck(final Optional<SpreadsheetViewport> before,
                                                   final Optional<SpreadsheetViewport> after) {
        final SpreadsheetDelta delta = SpreadsheetDelta.EMPTY
                .setViewport(after);

        this.patchRowsAndCheck(
                SpreadsheetDelta.EMPTY
                        .setViewport(before),
                marshall(delta),
                delta
        );
    }

    @Test
    public void testPatchRowsViewportCleared() {
        this.patchRowsAndCheck(
                SpreadsheetDelta.EMPTY
                        .setViewport(
                                Optional.of(
                                        SpreadsheetSelection.parseCellRange("A1:B2")
                                                .setAnchor(SpreadsheetViewportAnchor.BOTTOM_RIGHT)
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
                .setViewport(
                        Optional.of(
                                SpreadsheetSelection.parseCellRange("A1:B2")
                                        .setAnchor(SpreadsheetViewportAnchor.TOP_LEFT)
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
                        SpreadsheetViewportWindows.parse("A1:B2")
                );

        final SpreadsheetDelta after = before.setRows(
                Sets.of(row1)
        ).setWindow(
                SpreadsheetViewportWindows.parse("A1")
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
        return MARSHALL_CONTEXT.marshall(object);
    }

    private final static JsonNodeMarshallContext MARSHALL_CONTEXT = JsonNodeMarshallContexts.basic();

    private JsonNode marshallWithType(final Object object) {
        return JsonNodeMarshallContexts.basic()
                .marshallWithType(object);
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
                                SpreadsheetSelection.A1
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
                                SpreadsheetSelection.A1
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

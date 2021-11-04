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
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRange;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.reference.SpreadsheetViewportSelection;
import walkingkooka.spreadsheet.reference.SpreadsheetViewportSelectionAnchor;
import walkingkooka.tree.expression.ExpressionNumberContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonObject;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;
import walkingkooka.tree.json.patch.PatchableTesting;

import java.math.MathContext;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetDeltaTest implements ClassTesting2<SpreadsheetDelta>,
        PatchableTesting<SpreadsheetDelta> {

    @Test
    public void testEmpty() {
        final SpreadsheetDelta empty = SpreadsheetDelta.EMPTY;

        assertEquals(SpreadsheetDelta.NO_CELLS, empty.cells());
        assertEquals(SpreadsheetDelta.NO_LABELS, empty.labels());
        assertEquals(SpreadsheetDelta.NO_DELETED_CELLS, empty.deletedCells());
        assertEquals(SpreadsheetDelta.NO_COLUMN_WIDTHS, empty.columnWidths());
        assertEquals(SpreadsheetDelta.NO_ROW_HEIGHTS, empty.rowHeights());
    }

    @Test
    public void testNoWindowConstant() {
        assertEquals(Optional.empty(), SpreadsheetDelta.NO_WINDOW);
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
        assertEquals(reference.toRelative(), reference, "reference should be relative");

        this.cellAndCheck(
                SpreadsheetDelta.EMPTY.setCells(
                        Sets.of(cell)
                ),
                reference.toAbsolute(),
                Optional.of(cell)
        );
    }

    private SpreadsheetCell cell() {
        return SpreadsheetCell.with(
                SpreadsheetSelection.parseCell("A1"),
                SpreadsheetFormula.EMPTY
                        .setText("=1+2")
        );
    }

    private void cellAndCheck(final SpreadsheetDelta delta,
                              final SpreadsheetCellReference reference,
                              final Optional<SpreadsheetCell> cell) {
        assertEquals(
                cell,
                delta.cell(reference),
                () -> delta + " cell " + reference
        );
    }

    // Patch............................................................................................................

    @Test
    public void testPatchEmptyObject() {
        this.patchAndCheck(
                SpreadsheetDelta.EMPTY,
                JsonNode.object()
        );
    }

    @Test
    public void testPatchLabelsFails() {
        this.patchInvalidPropertyFails(
                JsonNode.object()
                        .set(
                                SpreadsheetDelta.LABELS_PROPERTY,
                                JsonNode.nullNode()
                        ),
                SpreadsheetDelta.LABELS_PROPERTY,
                JsonNode.nullNode()
        );
    }

    @Test
    public void testPatchDeletedCellsFails() {
        this.patchInvalidPropertyFails(
                JsonNode.object()
                        .set(
                                SpreadsheetDelta.DELETED_CELLS_PROPERTY,
                                JsonNode.nullNode()
                        ),
                SpreadsheetDelta.DELETED_CELLS_PROPERTY,
                JsonNode.nullNode()
        );
    }

    @Test
    public void testPatchColumnWidthFails() {
        this.patchInvalidPropertyFails(
                JsonNode.object()
                        .set(
                                SpreadsheetDelta.COLUMN_WIDTHS_PROPERTY,
                                JsonNode.nullNode()
                        ),
                SpreadsheetDelta.COLUMN_WIDTHS_PROPERTY,
                JsonNode.nullNode()
        );
    }

    @Test
    public void testPatchRowHeightFails() {
        this.patchInvalidPropertyFails(
                JsonNode.object()
                        .set(
                                SpreadsheetDelta.ROW_HEIGHTS_PROPERTY,
                                JsonNode.nullNode()
                        ),
                SpreadsheetDelta.ROW_HEIGHTS_PROPERTY,
                JsonNode.nullNode()
        );
    }

    @Test
    public void testPatchNoSelection() {
        this.patchSelectionAndCheck(
                SpreadsheetDelta.NO_SELECTION,
                SpreadsheetDelta.NO_SELECTION
        );
    }

    @Test
    public void testPatchSelection() {
        this.patchSelectionAndCheck(
                SpreadsheetDelta.NO_SELECTION,
                Optional.of(
                        SpreadsheetSelection.parseColumn("C")
                                .setAnchor(SpreadsheetViewportSelection.NO_ANCHOR)
                )
        );
    }

    private void patchSelectionAndCheck(final Optional<SpreadsheetViewportSelection> before,
                                        final Optional<SpreadsheetViewportSelection> after) {
        final SpreadsheetDelta delta = SpreadsheetDelta.EMPTY
                .setSelection(after);

        this.patchAndCheck(
                SpreadsheetDelta.EMPTY
                        .setSelection(before),
                marshall(delta),
                delta
        );
    }

    @Test
    public void testPatchSelectionCleared() {
        this.patchAndCheck(
                SpreadsheetDelta.EMPTY
                        .setSelection(
                                Optional.of(
                                        SpreadsheetSelection.parseCellRange("A1:B2")
                                                .setAnchor(
                                                        Optional.of(SpreadsheetViewportSelectionAnchor.BOTTOM_RIGHT)
                                                )
                                )
                        )
                ,
                JsonNode.object()
                        .set(
                                SpreadsheetDelta.SELECTION_PROPERTY,
                                JsonNode.nullNode()
                        ),
                SpreadsheetDelta.EMPTY
        );
    }

    @Test
    public void testPatchUnknownCellFails() {
        final SpreadsheetCell cell = SpreadsheetCell.with(
                SpreadsheetSelection.parseCell("a1"),
                SpreadsheetFormula.EMPTY
        );
        final SpreadsheetDelta delta = SpreadsheetDelta.EMPTY
                .setCells(
                        Sets.of(cell)
                );

        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class, () -> {
                    SpreadsheetDelta.EMPTY
                            .patch(marshall(delta), this.createPatchContext());
                }
        );
        assertEquals("Missing patch cell: A1", thrown.getMessage(), "message");
    }

    @Test
    public void testPatchReplacesCell() {
        final SpreadsheetCell cell = SpreadsheetCell.with(
                SpreadsheetSelection.parseCell("a1"),
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
    public void testPatchCellsRemoved() {
        final SpreadsheetDelta without = SpreadsheetDelta.EMPTY
                .setSelection(
                        Optional.of(
                                SpreadsheetSelection.parseCellRange("A1:B2")
                                        .setAnchor(
                                                Optional.of(
                                                        SpreadsheetViewportSelectionAnchor.TOP_LEFT
                                                )
                                        )
                        )
                );
        this.patchAndCheck(
                without.setCells(
                        Sets.of(
                                SpreadsheetCell.with(
                                        SpreadsheetSelection.parseCell("a1"),
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
    public void testPatchCellWithWindow() {
        final SpreadsheetCell a1 = SpreadsheetCell.with(
                SpreadsheetSelection.parseCell("a1"),
                SpreadsheetFormula.EMPTY
                        .setText("=1")
        );
        final SpreadsheetCell b2 = SpreadsheetCell.with(
                SpreadsheetSelection.parseCell("b2"),
                SpreadsheetFormula.EMPTY
                        .setText("=99")
        );

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
                .setCells(
                        Sets.of(a1, b2)
                ).setWindow(
                        Optional.of(
                                SpreadsheetSelection.parseCellRange("A1:B2")
                        )
                );

        final SpreadsheetDelta after = before.setCells(
                Sets.of(
                        a1.setFormula(
                                SpreadsheetFormula.EMPTY
                                        .setText("=2")
                        ),
                        b2
                )
        ).setWindow(
                Optional.of(
                        SpreadsheetSelection.parseCellRange("A1")
                )
        );

        this.patchAndCheck(
                before,
                marshall(after),
                after
        );
    }

    @Test
    public void testPatchWindow() {
        final SpreadsheetCell a1 = SpreadsheetCell.with(
                SpreadsheetSelection.parseCell("a1"),
                SpreadsheetFormula.EMPTY
                        .setText("=1")
        );
        final SpreadsheetCell b2 = SpreadsheetCell.with(
                SpreadsheetSelection.parseCell("b2"),
                SpreadsheetFormula.EMPTY
                        .setText("=99")
        );

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
                .setCells(
                        Sets.of(a1, b2)
                );

        final Optional<SpreadsheetCellRange> afterWindow = Optional.of(
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
    public void testPatchWindowReplaced() {
        final SpreadsheetCell a1 = SpreadsheetCell.with(
                SpreadsheetSelection.parseCell("a1"),
                SpreadsheetFormula.EMPTY
                        .setText("=1")
        );
        final SpreadsheetCell b2 = SpreadsheetCell.with(
                SpreadsheetSelection.parseCell("b2"),
                SpreadsheetFormula.EMPTY
                        .setText("=99")
        );

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
                .setCells(
                        Sets.of(a1, b2)
                ).setWindow(
                        Optional.of(
                                SpreadsheetSelection.parseCellRange("A1:B2")
                        )
                );

        final Optional<SpreadsheetCellRange> afterWindow = Optional.of(
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
    public void testPatchWindowRemoved() {
        final SpreadsheetCell a1 = SpreadsheetCell.with(
                SpreadsheetSelection.parseCell("a1"),
                SpreadsheetFormula.EMPTY
                        .setText("=1")
        );
        final SpreadsheetCell b2 = SpreadsheetCell.with(
                SpreadsheetSelection.parseCell("b2"),
                SpreadsheetFormula.EMPTY
                        .setText("=99")
        );

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
                .setCells(
                        Sets.of(a1, b2)
                ).setWindow(
                        Optional.of(
                                SpreadsheetSelection.parseCellRange("A1:B2")
                        )
                );

        this.patchAndCheck(
                before,
                JsonNode.object()
                        .set(SpreadsheetDelta.WINDOW_PROPERTY, JsonNode.nullNode()),
                before.setWindow(SpreadsheetDelta.NO_WINDOW)
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
                ExpressionNumberContexts.basic(
                        ExpressionNumberKind.BIG_DECIMAL,
                        MathContext.UNLIMITED
                )
        );
    }

    // resolveCellLabels.....................................................................................................

    final Function<SpreadsheetLabelName, SpreadsheetCellReference> LABEL_TO_CELL = (l) -> {
        throw new UnsupportedOperationException();
    };

    @Test
    public void testResolveLabelsNullJsonFails() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            SpreadsheetDelta.resolveCellLabels(null, LABEL_TO_CELL);
        });
    }

    @Test
    public void testResolveLabelsNullCellToLabelsFails() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            SpreadsheetDelta.resolveCellLabels(JsonNode.object(), null);
        });
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
                                SpreadsheetCell.with(
                                        SpreadsheetSelection.parseCell("A1"),
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
                                SpreadsheetCell.with(
                                        SpreadsheetSelection.parseCell("A1"),
                                        SpreadsheetFormula.EMPTY
                                ),
                                SpreadsheetCell.with(
                                        SpreadsheetSelection.parseCell("B2"),
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
                                SpreadsheetCell.with(
                                        z99,
                                        SpreadsheetFormula.EMPTY
                                ),
                                SpreadsheetCell.with(
                                        SpreadsheetSelection.parseCell("B2"),
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
                    assertEquals(label, l.value(), "label");
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
        assertEquals(
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

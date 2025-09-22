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
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.color.Color;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.spreadsheet.SpreadsheetRow;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserSelector;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReferenceOrRange;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewport;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewportAnchor;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewportWindows;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonObject;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;
import walkingkooka.tree.json.patch.PatchableTesting;
import walkingkooka.tree.text.FontStyle;
import walkingkooka.tree.text.TextAlign;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;
import walkingkooka.validation.ValidationValueTypeName;
import walkingkooka.validation.provider.ValidatorSelector;

import java.math.MathContext;
import java.text.DateFormatSymbols;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetDeltaTest implements ClassTesting2<SpreadsheetDelta>,
    PatchableTesting<SpreadsheetDelta>,
    TreePrintableTesting {

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
                .setLabelMappingReference(
                    SpreadsheetSelection.parseCellRange("C3:E5")
                )
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

    // cellsPatch.......................................................................................................

    @Test
    public void testCellsPatchWithNullCellsFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetDelta.cellsPatch(
                null,
                MARSHALL_CONTEXT
            )
        );
    }

    @Test
    public void testCellsPatchWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetDelta.cellsPatch(
                Sets.empty(),
                null
            )
        );
    }

    @Test
    public void testCellsPatch() {
        final SpreadsheetFormula formula = SpreadsheetFormula.EMPTY.setText("=1");

        this.cellsPatchAndCheck(
            Sets.of(SpreadsheetSelection.A1.setFormula(formula)),
            JsonNode.object()
                .set(
                    SpreadsheetDelta.CELLS_PROPERTY,
                    JsonNode.object()
                        .set(
                            JsonPropertyName.with("A1"),
                            JsonNode.object()
                                .set(
                                    JsonPropertyName.with("formula"),
                                    marshall(formula)
                                )
                        )
                )
        );
    }

    @Test
    public void testCellsPatchEmptyCells() {
        this.cellsPatchAndCheck(
            Sets.empty(),
            JsonNode.object()
                .set(
                    SpreadsheetDelta.CELLS_PROPERTY,
                    JsonNode.object()
                )
        );
    }

    @Test
    public void testCellsPatchMultipleCells() {
        final SpreadsheetFormula formula1 = SpreadsheetFormula.EMPTY.setText("=1");
        final SpreadsheetFormula formula2 = SpreadsheetFormula.EMPTY.setText("=22");

        final Set<SpreadsheetCell> cells = Sets.of(
            SpreadsheetSelection.A1.setFormula(formula1),
            SpreadsheetSelection.parseCell("A2")
                .setFormula(formula2)
        );

        this.cellsPatchAndCheck(
            cells,
            JsonNode.object()
                .set(
                    SpreadsheetDelta.CELLS_PROPERTY,
                    JsonNode.object()
                        .set(
                            JsonPropertyName.with("A1"),
                            JsonNode.object()
                                .set(
                                    JsonPropertyName.with("formula"),
                                    marshall(formula1)
                                )
                        ).set(
                            JsonPropertyName.with("A2"),
                            JsonNode.object()
                                .set(
                                    JsonPropertyName.with("formula"),
                                    marshall(formula2)
                                )
                        )
                )
        );
    }

    private void cellsPatchAndCheck(final Set<SpreadsheetCell> cells,
                                    final JsonObject expected) {
        final JsonNode patch = SpreadsheetDelta.cellsPatch(
            cells,
            MARSHALL_CONTEXT
        );

        this.checkEquals(
            expected,
            patch
        );

        this.patchAndCheck(
            SpreadsheetDelta.EMPTY,
            patch,
            SpreadsheetDelta.EMPTY.setCells(cells)
        );
    }

    // cellsDateTimeSymbolsPatch........................................................................................

    @Test
    public void testCellsDateTimeSymbolsPatchWithNullCellsToDateTimeSymbolsFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetDelta.cellsDateTimeSymbolsPatch(
                null,
                MARSHALL_CONTEXT
            )
        );
    }

    @Test
    public void testCellsDateTimeSymbolsPatchWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetDelta.cellsDateTimeSymbolsPatch(
                Maps.empty(),
                null
            )
        );
    }

    @Test
    public void testCellsDateTimeSymbolsPatch() {
        final DateTimeSymbols symbols = DateTimeSymbols.fromDateFormatSymbols(
            new DateFormatSymbols(Locale.FRANCE)
        );

        this.cellsDateTimeSymbolsPatchAndCheck(
            Maps.of(
                SpreadsheetSelection.A1,
                Optional.of(symbols)
            ),
            JsonNode.object()
                .set(
                    SpreadsheetDelta.CELLS_PROPERTY,
                    JsonNode.object()
                        .set(
                            JsonPropertyName.with("A1"),
                            JsonNode.object()
                                .set(
                                    JsonPropertyName.with("dateTimeSymbols"),
                                    marshall(symbols)
                                )
                        )
                )
        );
    }

    @Test
    public void testCellsDateTimeSymbolsPatchEmptyCells() {
        this.cellsDateTimeSymbolsPatchAndCheck(
            Maps.empty(),
            JsonNode.object()
                .set(
                    SpreadsheetDelta.CELLS_PROPERTY,
                    JsonNode.object()
                )
        );
    }

    @Test
    public void testCellsDateTimeSymbolsPatchMultipleCells() {
        final DateTimeSymbols symbols1 = DateTimeSymbols.fromDateFormatSymbols(
            new DateFormatSymbols(Locale.FRANCE)
        );
        final DateTimeSymbols symbols2 = DateTimeSymbols.fromDateFormatSymbols(
            new DateFormatSymbols(Locale.GERMANY)
        );
        final DateTimeSymbols symbols3 = null;

        final Map<SpreadsheetCellReference, Optional<DateTimeSymbols>> cellToSymbols = Maps.of(
            SpreadsheetSelection.A1,
            Optional.of(symbols1),
            SpreadsheetSelection.parseCell("A2"),
            Optional.of(symbols2),
            SpreadsheetSelection.parseCell("A3"),
            Optional.ofNullable(symbols3)
        );

        this.cellsDateTimeSymbolsPatchAndCheck(
            cellToSymbols,
            JsonNode.object()
                .set(
                    SpreadsheetDelta.CELLS_PROPERTY,
                    JsonNode.object()
                        .set(
                            JsonPropertyName.with("A1"),
                            JsonNode.object()
                                .set(
                                    JsonPropertyName.with("dateTimeSymbols"),
                                    marshall(symbols1)
                                )
                        ).set(
                            JsonPropertyName.with("A2"),
                            JsonNode.object()
                                .set(
                                    JsonPropertyName.with("dateTimeSymbols"),
                                    marshall(symbols2)
                                )
                        ).set(
                            JsonPropertyName.with("A3"),
                            JsonNode.object()
                                .set(
                                    JsonPropertyName.with("dateTimeSymbols"),
                                    marshall(null)
                                )
                        )
                )
        );
    }

    private void cellsDateTimeSymbolsPatchAndCheck(final Map<SpreadsheetCellReference, Optional<DateTimeSymbols>> cellToDateTimeSymbols,
                                                   final JsonObject expected) {
        final JsonNode patch = SpreadsheetDelta.cellsDateTimeSymbolsPatch(
            cellToDateTimeSymbols,
            MARSHALL_CONTEXT
        );

        this.checkEquals(
            expected,
            patch
        );

        final Set<SpreadsheetCell> beforePatchCells = SortedSets.tree(SpreadsheetCell.REFERENCE_COMPARATOR);
        final Set<SpreadsheetCell> patchedCells = SortedSets.tree(SpreadsheetCell.REFERENCE_COMPARATOR);
        final TextStyle style = TextStyle.EMPTY.set(
            TextStylePropertyName.COLOR,
            Color.BLACK
        );

        for (final Map.Entry<SpreadsheetCellReference, Optional<DateTimeSymbols>> cellAndDateTimeSymbols : cellToDateTimeSymbols.entrySet()) {
            final SpreadsheetCellReference cellReference = cellAndDateTimeSymbols.getKey();

            final SpreadsheetCell cell = cellReference.setFormula(
                SpreadsheetFormula.EMPTY.setText("'Patched over")
            ).setStyle(style);


            beforePatchCells.add(cell);
            patchedCells.add(
                cell.setDateTimeSymbols(cellAndDateTimeSymbols.getValue())
            );
        }

        this.patchAndCheck(
            SpreadsheetDelta.EMPTY.setCells(beforePatchCells),
            patch,
            SpreadsheetDelta.EMPTY.setCells(patchedCells)
        );
    }

    // cellsDecimalNumberSymbolsPatch...................................................................................

    @Test
    public void testCellsDecimalNumberSymbolsPatchWithNullCellsToDecimalNumberSymbolsFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetDelta.cellsDecimalNumberSymbolsPatch(
                null,
                MARSHALL_CONTEXT
            )
        );
    }

    @Test
    public void testCellsDecimalNumberSymbolsPatchWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetDelta.cellsDecimalNumberSymbolsPatch(
                Maps.empty(),
                null
            )
        );
    }

    @Test
    public void testCellsDecimalNumberSymbolsPatch() {
        final DecimalNumberSymbols symbols = DecimalNumberSymbols.fromDecimalFormatSymbols(
            '+',
            new DecimalFormatSymbols(Locale.FRANCE)
        );

        this.cellsDecimalNumberSymbolsPatchAndCheck(
            Maps.of(
                SpreadsheetSelection.A1,
                Optional.of(symbols)
            ),
            JsonNode.object()
                .set(
                    SpreadsheetDelta.CELLS_PROPERTY,
                    JsonNode.object()
                        .set(
                            JsonPropertyName.with("A1"),
                            JsonNode.object()
                                .set(
                                    JsonPropertyName.with("decimalNumberSymbols"),
                                    marshall(symbols)
                                )
                        )
                )
        );
    }

    @Test
    public void testCellsDecimalNumberSymbolsPatchEmptyCells() {
        this.cellsDecimalNumberSymbolsPatchAndCheck(
            Maps.empty(),
            JsonNode.object()
                .set(
                    SpreadsheetDelta.CELLS_PROPERTY,
                    JsonNode.object()
                )
        );
    }

    @Test
    public void testCellsDecimalNumberSymbolsPatchMultipleCells() {
        final DecimalNumberSymbols symbols1 = DecimalNumberSymbols.fromDecimalFormatSymbols(
            '+',
            new DecimalFormatSymbols(Locale.FRANCE)
        );
        final DecimalNumberSymbols symbols2 = DecimalNumberSymbols.fromDecimalFormatSymbols(
            '+',
            new DecimalFormatSymbols(Locale.GERMANY)
        );
        final DecimalNumberSymbols symbols3 = null;

        final Map<SpreadsheetCellReference, Optional<DecimalNumberSymbols>> cellToSymbols = Maps.of(
            SpreadsheetSelection.A1,
            Optional.of(symbols1),
            SpreadsheetSelection.parseCell("A2"),
            Optional.of(symbols2),
            SpreadsheetSelection.parseCell("A3"),
            Optional.ofNullable(symbols3)
        );

        this.cellsDecimalNumberSymbolsPatchAndCheck(
            cellToSymbols,
            JsonNode.object()
                .set(
                    SpreadsheetDelta.CELLS_PROPERTY,
                    JsonNode.object()
                        .set(
                            JsonPropertyName.with("A1"),
                            JsonNode.object()
                                .set(
                                    JsonPropertyName.with("decimalNumberSymbols"),
                                    marshall(symbols1)
                                )
                        ).set(
                            JsonPropertyName.with("A2"),
                            JsonNode.object()
                                .set(
                                    JsonPropertyName.with("decimalNumberSymbols"),
                                    marshall(symbols2)
                                )
                        ).set(
                            JsonPropertyName.with("A3"),
                            JsonNode.object()
                                .set(
                                    JsonPropertyName.with("decimalNumberSymbols"),
                                    marshall(null)
                                )
                        )
                )
        );
    }

    private void cellsDecimalNumberSymbolsPatchAndCheck(final Map<SpreadsheetCellReference, Optional<DecimalNumberSymbols>> cellToDecimalNumberSymbols,
                                                        final JsonObject expected) {
        final JsonNode patch = SpreadsheetDelta.cellsDecimalNumberSymbolsPatch(
            cellToDecimalNumberSymbols,
            MARSHALL_CONTEXT
        );

        this.checkEquals(
            expected,
            patch
        );

        final Set<SpreadsheetCell> beforePatchCells = SortedSets.tree(SpreadsheetCell.REFERENCE_COMPARATOR);
        final Set<SpreadsheetCell> patchedCells = SortedSets.tree(SpreadsheetCell.REFERENCE_COMPARATOR);
        final TextStyle style = TextStyle.EMPTY.set(
            TextStylePropertyName.COLOR,
            Color.BLACK
        );

        for (final Map.Entry<SpreadsheetCellReference, Optional<DecimalNumberSymbols>> cellAndDecimalNumberSymbols : cellToDecimalNumberSymbols.entrySet()) {
            final SpreadsheetCellReference cellReference = cellAndDecimalNumberSymbols.getKey();

            final SpreadsheetCell cell = cellReference.setFormula(
                SpreadsheetFormula.EMPTY.setText("'Patched over")
            ).setStyle(style);


            beforePatchCells.add(cell);
            patchedCells.add(
                cell.setDecimalNumberSymbols(cellAndDecimalNumberSymbols.getValue())
            );
        }

        this.patchAndCheck(
            SpreadsheetDelta.EMPTY.setCells(beforePatchCells),
            patch,
            SpreadsheetDelta.EMPTY.setCells(patchedCells)
        );
    }

    // cellsFormulaTextPatch............................................................................................

    @Test
    public void testCellsFormulaTextPatchWithNullMapFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetDelta.cellsFormulaTextPatch(null)
        );
    }

    @Test
    public void testCellsFormulaTextPatch() {
        final String formulaText1 = "=1";
        final String formulaText2 = "=22";

        this.cellsFormulaTextPatchAndCheck(
            Maps.of(
                SpreadsheetSelection.A1,
                formulaText1,
                SpreadsheetSelection.parseCell("A2"),
                formulaText2
            ),
            "{\n" +
                "  \"cells\": {\n" +
                "    \"A1\": {\n" +
                "      \"formula\": {\n" +
                "        \"text\": \"=1\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"A2\": {\n" +
                "      \"formula\": {\n" +
                "        \"text\": \"=22\"\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}"
        );
    }

    private void cellsFormulaTextPatchAndCheck(final Map<SpreadsheetCellReference, String> cellToFormulaTexts,
                                               final String expected) {
        final JsonNode patch = SpreadsheetDelta.cellsFormulaTextPatch(cellToFormulaTexts);

        this.checkEquals(
            JsonNode.parse(expected),
            patch
        );

        final Set<SpreadsheetCell> beforePatchCells = SortedSets.tree(SpreadsheetCell.REFERENCE_COMPARATOR);
        final Set<SpreadsheetCell> patchedCells = SortedSets.tree(SpreadsheetCell.REFERENCE_COMPARATOR);
        final TextStyle style = TextStyle.EMPTY.set(
            TextStylePropertyName.COLOR,
            Color.BLACK
        );

        for (final Map.Entry<SpreadsheetCellReference, String> cellAndFormulaText : cellToFormulaTexts.entrySet()) {
            final SpreadsheetCellReference cell = cellAndFormulaText.getKey();

            final SpreadsheetFormula formula = SpreadsheetFormula.EMPTY.setText("'Patched over");

            beforePatchCells.add(
                cell.setFormula(formula)
                    .setStyle(style)
            );
            patchedCells.add(
                cell.setFormula(
                    formula.setText(
                        cellAndFormulaText.getValue()
                    )
                ).setStyle(style)
            );
        }

        this.patchAndCheck(
            SpreadsheetDelta.EMPTY.setCells(beforePatchCells),
            patch,
            SpreadsheetDelta.EMPTY.setCells(patchedCells)
        );
    }

    // cellsFormatterPatch..............................................................................................

    @Test
    public void testCellsFormatterPatchWithNullCellsFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetDelta.cellsFormatterPatch(
                null,
                MARSHALL_CONTEXT
            )
        );
    }

    @Test
    public void testCellsFormatterPatchWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetDelta.cellsFormatterPatch(
                Maps.empty(),
                null
            )
        );
    }

    @Test
    public void testCellsFormatterPatch() {
        final Optional<SpreadsheetFormatterSelector> formatter = Optional.of(
            SpreadsheetPattern.parseDateFormatPattern("yyyy/mm/ddd")
                .spreadsheetFormatterSelector()
        );

        final Map<SpreadsheetCellReference, Optional<SpreadsheetFormatterSelector>> cellToFormatter = Maps.of(
            SpreadsheetSelection.A1,
            formatter
        );

        this.cellsFormatterPatchAndCheck(
            cellToFormatter,
            JsonNode.object()
                .set(
                    SpreadsheetDelta.CELLS_PROPERTY,
                    JsonNode.object()
                        .set(
                            JsonPropertyName.with("A1"),
                            JsonNode.object()
                                .set(
                                    SpreadsheetDelta.FORMATTER_PROPERTY,
                                    marshall(formatter.get())
                                )
                        )
                )
        );
    }

    @Test
    public void testCellsFormatterPatchEmptyPattern() {
        final Optional<SpreadsheetFormatterSelector> formatter = Optional.empty();

        final Map<SpreadsheetCellReference, Optional<SpreadsheetFormatterSelector>> cellToFormatter = Maps.of(
            SpreadsheetSelection.A1,
            formatter
        );

        this.cellsFormatterPatchAndCheck(
            cellToFormatter,
            JsonNode.object()
                .set(
                    SpreadsheetDelta.CELLS_PROPERTY,
                    JsonNode.object()
                        .set(
                            JsonPropertyName.with("A1"),
                            JsonNode.object()
                                .set(
                                    SpreadsheetDelta.FORMATTER_PROPERTY,
                                    marshall(null)
                                )
                        )
                )
        );
    }

    @Test
    public void testCellsFormatterPatchEmptyCells() {
        this.cellsFormatterPatchAndCheck(
            Maps.empty(),
            JsonNode.object()
                .set(
                    SpreadsheetDelta.CELLS_PROPERTY,
                    JsonNode.object()
                )
        );
    }

    @Test
    public void testCellsFormatterPatchMultipleCells() {
        final Optional<SpreadsheetFormatterSelector> formatter1 = Optional.of(
            SpreadsheetPattern.parseDateFormatPattern("yyyy/mm/ddd")
                .spreadsheetFormatterSelector()
        );
        final Optional<SpreadsheetFormatterSelector> formatter2 = Optional.of(
            SpreadsheetPattern.parseTimeFormatPattern("hh:mm")
                .spreadsheetFormatterSelector()
        );

        final Map<SpreadsheetCellReference, Optional<SpreadsheetFormatterSelector>> cellToFormatter = Maps.of(
            SpreadsheetSelection.A1,
            formatter1,
            SpreadsheetSelection.parseCell("A2"),
            formatter2
        );

        this.cellsFormatterPatchAndCheck(
            cellToFormatter,
            JsonNode.object()
                .set(
                    SpreadsheetDelta.CELLS_PROPERTY,
                    JsonNode.object()
                        .set(
                            JsonPropertyName.with("A1"),
                            JsonNode.object()
                                .set(
                                    SpreadsheetDelta.FORMATTER_PROPERTY,
                                    marshall(formatter1.get())
                                )
                        ).set(
                            JsonPropertyName.with("A2"),
                            JsonNode.object()
                                .set(
                                    SpreadsheetDelta.FORMATTER_PROPERTY,
                                    marshall(formatter2.get())
                                )
                        )
                )
        );
    }

    private void cellsFormatterPatchAndCheck(final Map<SpreadsheetCellReference, Optional<SpreadsheetFormatterSelector>> cellToFormatter,
                                             final JsonObject expected) {
        final JsonNode patch = SpreadsheetDelta.cellsFormatterPatch(
            cellToFormatter,
            MARSHALL_CONTEXT
        );

        this.checkEquals(
            expected,
            patch
        );

        final Set<SpreadsheetCell> beforePatchCells = SortedSets.tree(SpreadsheetCell.REFERENCE_COMPARATOR);
        final Set<SpreadsheetCell> patchedCells = SortedSets.tree(SpreadsheetCell.REFERENCE_COMPARATOR);

        final SpreadsheetFormula formula = SpreadsheetFormula.EMPTY.setText("=1");

        for (final Map.Entry<SpreadsheetCellReference, Optional<SpreadsheetFormatterSelector>> cellAndFormatter : cellToFormatter.entrySet()) {
            final SpreadsheetCellReference cell = cellAndFormatter.getKey();

            beforePatchCells.add(
                cell.setFormula(
                    SpreadsheetFormula.EMPTY.setText("=1")
                ).setFormatter(
                    Optional.of(
                        SpreadsheetPattern.DEFAULT_TEXT_FORMAT_PATTERN.spreadsheetFormatterSelector()
                    )
                )
            );
            patchedCells.add(
                cell.setFormula(formula)
                    .setFormatter(
                        cellAndFormatter.getValue()
                    )
            );
        }

        this.patchAndCheck(
            SpreadsheetDelta.EMPTY.setCells(beforePatchCells),
            patch,
            SpreadsheetDelta.EMPTY.setCells(patchedCells)
        );
    }

    // cellsLocalePatch.................................................................................................

    @Test
    public void testCellsLocalePatchWithNullCellsToLocaleFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetDelta.cellsLocalePatch(
                null,
                MARSHALL_CONTEXT
            )
        );
    }

    @Test
    public void testCellsLocalePatchWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetDelta.cellsLocalePatch(
                Maps.empty(),
                null
            )
        );
    }

    @Test
    public void testCellsLocalePatch() {
        final Locale locale = Locale.FRANCE;

        this.cellsLocalePatchAndCheck(
            Maps.of(
                SpreadsheetSelection.A1,
                Optional.of(locale)
            ),
            JsonNode.object()
                .set(
                    SpreadsheetDelta.CELLS_PROPERTY,
                    JsonNode.object()
                        .set(
                            JsonPropertyName.with("A1"),
                            JsonNode.object()
                                .set(
                                    JsonPropertyName.with("locale"),
                                    marshall(locale)
                                )
                        )
                )
        );
    }

    @Test
    public void testCellsLocalePatchEmptyCells() {
        this.cellsLocalePatchAndCheck(
            Maps.empty(),
            JsonNode.object()
                .set(
                    SpreadsheetDelta.CELLS_PROPERTY,
                    JsonNode.object()
                )
        );
    }

    @Test
    public void testCellsLocalePatchMultipleCells() {
        final Locale locale1 = Locale.ENGLISH;
        final Locale locale2 = Locale.FRANCE;
        final Locale locale3 = null;

        final Map<SpreadsheetCellReference, Optional<Locale>> cellToSymbols = Maps.of(
            SpreadsheetSelection.A1,
            Optional.of(locale1),
            SpreadsheetSelection.parseCell("A2"),
            Optional.of(locale2),
            SpreadsheetSelection.parseCell("A3"),
            Optional.ofNullable(locale3)
        );

        this.cellsLocalePatchAndCheck(
            cellToSymbols,
            JsonNode.object()
                .set(
                    SpreadsheetDelta.CELLS_PROPERTY,
                    JsonNode.object()
                        .set(
                            JsonPropertyName.with("A1"),
                            JsonNode.object()
                                .set(
                                    JsonPropertyName.with("locale"),
                                    marshall(locale1)
                                )
                        ).set(
                            JsonPropertyName.with("A2"),
                            JsonNode.object()
                                .set(
                                    JsonPropertyName.with("locale"),
                                    marshall(locale2)
                                )
                        ).set(
                            JsonPropertyName.with("A3"),
                            JsonNode.object()
                                .set(
                                    JsonPropertyName.with("locale"),
                                    marshall(null)
                                )
                        )
                )
        );
    }

    private void cellsLocalePatchAndCheck(final Map<SpreadsheetCellReference, Optional<Locale>> cellToLocale,
                                          final JsonObject expected) {
        final JsonNode patch = SpreadsheetDelta.cellsLocalePatch(
            cellToLocale,
            MARSHALL_CONTEXT
        );

        this.checkEquals(
            expected,
            patch
        );

        final Set<SpreadsheetCell> beforePatchCells = SortedSets.tree(SpreadsheetCell.REFERENCE_COMPARATOR);
        final Set<SpreadsheetCell> patchedCells = SortedSets.tree(SpreadsheetCell.REFERENCE_COMPARATOR);
        final TextStyle style = TextStyle.EMPTY.set(
            TextStylePropertyName.COLOR,
            Color.BLACK
        );

        for (final Map.Entry<SpreadsheetCellReference, Optional<Locale>> cellAndLocale : cellToLocale.entrySet()) {
            final SpreadsheetCellReference cellReference = cellAndLocale.getKey();

            final SpreadsheetCell cell = cellReference.setFormula(
                SpreadsheetFormula.EMPTY.setText("'Patched over")
            ).setStyle(style);


            beforePatchCells.add(cell);
            patchedCells.add(
                cell.setLocale(cellAndLocale.getValue())
            );
        }

        this.patchAndCheck(
            SpreadsheetDelta.EMPTY.setCells(beforePatchCells),
            patch,
            SpreadsheetDelta.EMPTY.setCells(patchedCells)
        );
    }

    // cellsParserPatch.................................................................................................

    @Test
    public void testCellsParserPatchWithNullCellsFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetDelta.cellsParserPatch(
                null,
                MARSHALL_CONTEXT
            )
        );
    }

    @Test
    public void testCellsParserPatchWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetDelta.cellsParserPatch(
                Maps.empty(),
                null
            )
        );
    }

    @Test
    public void testCellsParserPatch() {
        final Optional<SpreadsheetParserSelector> parser = Optional.of(
            SpreadsheetPattern.parseDateParsePattern("yyyy/mm/ddd")
                .spreadsheetParserSelector()
        );

        final Map<SpreadsheetCellReference, Optional<SpreadsheetParserSelector>> cellToParsers = Maps.of(
            SpreadsheetSelection.A1,
            parser
        );

        this.cellsParserPatchAndCheck(
            cellToParsers,
            JsonNode.object()
                .set(
                    SpreadsheetDelta.CELLS_PROPERTY,
                    JsonNode.object()
                        .set(
                            JsonPropertyName.with("A1"),
                            JsonNode.object()
                                .set(
                                    JsonPropertyName.with("parser"),
                                    marshall(parser.get())
                                )
                        )
                )
        );
    }

    @Test
    public void testCellsParserPatchEmptyPattern() {
        final Map<SpreadsheetCellReference, Optional<SpreadsheetParserSelector>> cellToParsers = Maps.of(
            SpreadsheetSelection.A1,
            SpreadsheetCell.NO_PARSER
        );

        this.cellsParserPatchAndCheck(
            cellToParsers,
            JsonNode.object()
                .set(
                    SpreadsheetDelta.CELLS_PROPERTY,
                    JsonNode.object()
                        .set(
                            JsonPropertyName.with("A1"),
                            JsonNode.object()
                                .set(
                                    JsonPropertyName.with("parser"),
                                    marshall(null)
                                )
                        )
                )
        );
    }

    @Test
    public void testCellsParserPatchEmptyCells() {
        this.cellsParserPatchAndCheck(
            Maps.empty(),
            JsonNode.object()
                .set(
                    SpreadsheetDelta.CELLS_PROPERTY,
                    JsonNode.object()
                )
        );
    }

    @Test
    public void testCellsParserPatchMultipleCells() {
        final Optional<SpreadsheetParserSelector> parserSelector1 = Optional.of(
            SpreadsheetPattern.parseDateParsePattern("yyyy/mm/ddd")
                .spreadsheetParserSelector()
        );
        final Optional<SpreadsheetParserSelector> parserSelector2 = Optional.of(
            SpreadsheetPattern.parseTimeParsePattern("hh:mm")
                .spreadsheetParserSelector()
        );

        final Map<SpreadsheetCellReference, Optional<SpreadsheetParserSelector>> cellToParsers = Maps.of(
            SpreadsheetSelection.A1,
            parserSelector1,
            SpreadsheetSelection.parseCell("A2"),
            parserSelector2
        );

        this.cellsParserPatchAndCheck(
            cellToParsers,
            JsonNode.object()
                .set(
                    SpreadsheetDelta.CELLS_PROPERTY,
                    JsonNode.object()
                        .set(
                            JsonPropertyName.with("A1"),
                            JsonNode.object()
                                .set(
                                    JsonPropertyName.with("parser"),
                                    marshall(parserSelector1.get())
                                )
                        ).set(
                            JsonPropertyName.with("A2"),
                            JsonNode.object()
                                .set(
                                    JsonPropertyName.with("parser"),
                                    marshall(parserSelector2.get())
                                )
                        )
                )
        );
    }

    private void cellsParserPatchAndCheck(final Map<SpreadsheetCellReference, Optional<SpreadsheetParserSelector>> cellToParser,
                                          final JsonObject expected) {
        final JsonNode patch = SpreadsheetDelta.cellsParserPatch(
            cellToParser,
            MARSHALL_CONTEXT
        );

        this.checkEquals(
            expected,
            patch
        );

        final Set<SpreadsheetCell> beforePatchCells = SortedSets.tree(SpreadsheetCell.REFERENCE_COMPARATOR);
        final Set<SpreadsheetCell> patchedCells = SortedSets.tree(SpreadsheetCell.REFERENCE_COMPARATOR);

        final SpreadsheetFormula formula = SpreadsheetFormula.EMPTY.setText("=1");

        for (final Map.Entry<SpreadsheetCellReference, Optional<SpreadsheetParserSelector>> cellAndParser : cellToParser.entrySet()) {
            final SpreadsheetCellReference cell = cellAndParser.getKey();

            beforePatchCells.add(
                cell.setFormula(
                    SpreadsheetFormula.EMPTY.setText("=1")
                ).setParser(
                    Optional.of(
                        SpreadsheetPattern.parseNumberParsePattern("$0.00")
                            .spreadsheetParserSelector()
                    )
                )
            );
            patchedCells.add(
                cell.setFormula(formula)
                    .setParser(
                        cellAndParser.getValue()
                    )
            );
        }

        this.patchAndCheck(
            SpreadsheetDelta.EMPTY.setCells(beforePatchCells),
            patch,
            SpreadsheetDelta.EMPTY.setCells(patchedCells)
        );
    }

    // cellsStylePatch..................................................................................................

    @Test
    public void testCellsStylePatchWithNullCellsFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetDelta.cellsStylePatch(
                null,
                MARSHALL_CONTEXT
            )
        );
    }

    @Test
    public void testCellsStylePatchWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetDelta.cellsStylePatch(
                Maps.empty(),
                null
            )
        );
    }

    @Test
    public void testCellsStylePatch() {
        final TextStyle style = TextStyle.EMPTY.set(
            TextStylePropertyName.COLOR,
            Color.BLACK
        );

        this.cellsStylePatchAndCheck(
            Maps.of(
                SpreadsheetSelection.A1,
                style
            ),
            JsonNode.object()
                .set(
                    SpreadsheetDelta.CELLS_PROPERTY,
                    JsonNode.object()
                        .set(
                            JsonPropertyName.with("A1"),
                            JsonNode.object()
                                .set(
                                    JsonPropertyName.with("style"),
                                    marshall(style)
                                )
                        )
                )
        );
    }

    @Test
    public void testCellsStylePatchEmptyCells() {
        this.cellsStylePatchAndCheck(
            Maps.empty(),
            JsonNode.object()
                .set(
                    SpreadsheetDelta.CELLS_PROPERTY,
                    JsonNode.object()
                )
        );
    }

    @Test
    public void testCellsStylePatchMultipleCells() {
        final TextStyle style1 = TextStyle.EMPTY.set(
            TextStylePropertyName.COLOR,
            Color.BLACK
        );
        final TextStyle style2 = TextStyle.EMPTY.set(
            TextStylePropertyName.COLOR,
            Color.WHITE
        );

        final Map<SpreadsheetCellReference, TextStyle> cellToStyles = Maps.of(
            SpreadsheetSelection.A1,
            style1,
            SpreadsheetSelection.parseCell("A2"),
            style2
        );

        this.cellsStylePatchAndCheck(
            cellToStyles,
            JsonNode.object()
                .set(
                    SpreadsheetDelta.CELLS_PROPERTY,
                    JsonNode.object()
                        .set(
                            JsonPropertyName.with("A1"),
                            JsonNode.object()
                                .set(
                                    JsonPropertyName.with("style"),
                                    marshall(style1)
                                )
                        ).set(
                            JsonPropertyName.with("A2"),
                            JsonNode.object()
                                .set(
                                    JsonPropertyName.with("style"),
                                    marshall(style2)
                                )
                        )
                )
        );
    }

    private void cellsStylePatchAndCheck(final Map<SpreadsheetCellReference, TextStyle> cellToStyles,
                                         final JsonObject expected) {
        final JsonNode patch = SpreadsheetDelta.cellsStylePatch(
            cellToStyles,
            MARSHALL_CONTEXT
        );

        this.checkEquals(
            expected,
            patch
        );

        final Set<SpreadsheetCell> beforePatchCells = SortedSets.tree(SpreadsheetCell.REFERENCE_COMPARATOR);
        final Set<SpreadsheetCell> patchedCells = SortedSets.tree(SpreadsheetCell.REFERENCE_COMPARATOR);
        final SpreadsheetFormula formula = SpreadsheetFormula.EMPTY.setText("=1+2");

        for (final Map.Entry<SpreadsheetCellReference, TextStyle> cellAndStyle : cellToStyles.entrySet()) {
            final SpreadsheetCellReference cell = cellAndStyle.getKey();

            beforePatchCells.add(
                cell.setFormula(formula)
                    .setStyle(
                        TextStyle.EMPTY.set(TextStylePropertyName.COLOR, Color.parse("#123456"))
                            .set(TextStylePropertyName.TEXT_ALIGN, TextAlign.CENTER)
                    )
            );
            patchedCells.add(
                cell.setFormula(formula)
                    .setStyle(
                        cellAndStyle.getValue()
                            .set(TextStylePropertyName.TEXT_ALIGN, TextAlign.CENTER)
                    )
            );
        }

        this.patchAndCheck(
            SpreadsheetDelta.EMPTY.setCells(beforePatchCells),
            patch,
            SpreadsheetDelta.EMPTY.setCells(patchedCells)
        );
    }

    // cellsValidatorPatch.................................................................................................

    @Test
    public void testCellsValidatorPatchWithNullCellsFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetDelta.cellsValidatorPatch(
                null,
                MARSHALL_CONTEXT
            )
        );
    }

    @Test
    public void testCellsValidatorPatchWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetDelta.cellsValidatorPatch(
                Maps.empty(),
                null
            )
        );
    }

    @Test
    public void testCellsValidatorPatch() {
        final Optional<ValidatorSelector> validator = Optional.of(
            ValidatorSelector.parse("hello")
        );

        final Map<SpreadsheetCellReference, Optional<ValidatorSelector>> cellToValidators = Maps.of(
            SpreadsheetSelection.A1,
            validator
        );

        this.cellsValidatorPatchAndCheck(
            cellToValidators,
            JsonNode.object()
                .set(
                    SpreadsheetDelta.CELLS_PROPERTY,
                    JsonNode.object()
                        .set(
                            JsonPropertyName.with("A1"),
                            JsonNode.object()
                                .set(
                                    JsonPropertyName.with("validator"),
                                    marshall(validator.get())
                                )
                        )
                )
        );
    }

    @Test
    public void testCellsValidatorPatchEmptyPattern() {
        final Map<SpreadsheetCellReference, Optional<ValidatorSelector>> cellToValidators = Maps.of(
            SpreadsheetSelection.A1,
            SpreadsheetCell.NO_VALIDATOR
        );

        this.cellsValidatorPatchAndCheck(
            cellToValidators,
            JsonNode.object()
                .set(
                    SpreadsheetDelta.CELLS_PROPERTY,
                    JsonNode.object()
                        .set(
                            JsonPropertyName.with("A1"),
                            JsonNode.object()
                                .set(
                                    JsonPropertyName.with("validator"),
                                    marshall(null)
                                )
                        )
                )
        );
    }

    @Test
    public void testCellsValidatorPatchEmptyCells() {
        this.cellsValidatorPatchAndCheck(
            Maps.empty(),
            JsonNode.object()
                .set(
                    SpreadsheetDelta.CELLS_PROPERTY,
                    JsonNode.object()
                )
        );
    }

    @Test
    public void testCellsValidatorPatchMultipleCells() {
        final Optional<ValidatorSelector> validatorSelector1 = Optional.of(
            ValidatorSelector.parse("hello1")
        );
        final Optional<ValidatorSelector> validatorSelector2 = Optional.of(
            ValidatorSelector.parse("hello2")
        );
        final Optional<ValidatorSelector> validatorSelector3 = Optional.empty();

        final Map<SpreadsheetCellReference, Optional<ValidatorSelector>> cellToValidators = Maps.of(
            SpreadsheetSelection.A1,
            validatorSelector1,
            SpreadsheetSelection.parseCell("A2"),
            validatorSelector2,
            SpreadsheetSelection.parseCell("A3"),
            validatorSelector3
        );

        this.cellsValidatorPatchAndCheck(
            cellToValidators,
            JsonNode.object()
                .set(
                    SpreadsheetDelta.CELLS_PROPERTY,
                    JsonNode.object()
                        .set(
                            JsonPropertyName.with("A1"),
                            JsonNode.object()
                                .set(
                                    JsonPropertyName.with("validator"),
                                    marshall(validatorSelector1.get())
                                )
                        ).set(
                            JsonPropertyName.with("A2"),
                            JsonNode.object()
                                .set(
                                    JsonPropertyName.with("validator"),
                                    marshall(validatorSelector2.get())
                                )
                        ).set(
                            JsonPropertyName.with("A3"),
                            JsonNode.object()
                                .set(
                                    JsonPropertyName.with("validator"),
                                    marshall(null)
                                )
                        )
                )
        );
    }

    private void cellsValidatorPatchAndCheck(final Map<SpreadsheetCellReference, Optional<ValidatorSelector>> cellToValidator,
                                             final JsonObject expected) {
        final JsonNode patch = SpreadsheetDelta.cellsValidatorPatch(
            cellToValidator,
            MARSHALL_CONTEXT
        );

        this.checkEquals(
            expected,
            patch
        );

        final Set<SpreadsheetCell> beforePatchCells = SortedSets.tree(SpreadsheetCell.REFERENCE_COMPARATOR);
        final Set<SpreadsheetCell> patchedCells = SortedSets.tree(SpreadsheetCell.REFERENCE_COMPARATOR);

        final SpreadsheetFormula formula = SpreadsheetFormula.EMPTY.setText("=1");

        for (final Map.Entry<SpreadsheetCellReference, Optional<ValidatorSelector>> cellAndValidator : cellToValidator.entrySet()) {
            final SpreadsheetCellReference cell = cellAndValidator.getKey();

            beforePatchCells.add(
                cell.setFormula(
                    SpreadsheetFormula.EMPTY.setText("=1")
                ).setValidator(
                    Optional.of(
                        ValidatorSelector.parse("before")
                    )
                )
            );
            patchedCells.add(
                cell.setFormula(formula)
                    .setValidator(
                        cellAndValidator.getValue()
                    )
            );
        }

        this.patchAndCheck(
            SpreadsheetDelta.EMPTY.setCells(beforePatchCells),
            patch,
            SpreadsheetDelta.EMPTY.setCells(patchedCells)
        );
    }

    // cellsValuePatch..................................................................................................

    @Test
    public void testCellsValuePatchWithNullMapFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetDelta.cellsValuePatch(
                null,
                JsonNodeMarshallContexts.fake()
            )
        );
    }

    @Test
    public void testCellsValuePatchWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetDelta.cellsValuePatch(
                Maps.empty(),
                null
            )
        );
    }

    @Test
    public void testCellsValuePatch() {
        final Optional<Object> value1 = Optional.of(
            UNMARSHALL_CONTEXT.expressionNumberKind()
                .create(12.5)
        );
        final Optional<Object> value2 = Optional.empty();

        this.cellsValuePatchAndCheck(
            Maps.of(
                SpreadsheetSelection.A1,
                value1,
                SpreadsheetSelection.parseCell("A2"),
                value2
            ),
            "{\n" +
                "  \"cells\": {\n" +
                "    \"A1\": {\n" +
                "      \"formula\": {\n" +
                "        \"value\": {\n" +
                "          \"type\": \"expression-number\",\n" +
                "          \"value\": \"12.5\"\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    \"A2\": {\n" +
                "      \"formula\": {\n" +
                "        \"value\": null\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}"
        );
    }

    private void cellsValuePatchAndCheck(final Map<SpreadsheetCellReference, Optional<Object>> cellToValues,
                                         final String expected) {
        final JsonNode patch = SpreadsheetDelta.cellsValuePatch(
            cellToValues,
            MARSHALL_CONTEXT
        );

        this.checkEquals(
            JsonNode.parse(expected),
            patch
        );

        final Set<SpreadsheetCell> beforePatchCells = SortedSets.tree(SpreadsheetCell.REFERENCE_COMPARATOR);
        final Set<SpreadsheetCell> patchedCells = SortedSets.tree(SpreadsheetCell.REFERENCE_COMPARATOR);
        final TextStyle style = TextStyle.EMPTY.set(
            TextStylePropertyName.COLOR,
            Color.BLACK
        );

        for (final Map.Entry<SpreadsheetCellReference, Optional<Object>> cellAndValue : cellToValues.entrySet()) {
            final SpreadsheetCellReference cell = cellAndValue.getKey();

            final SpreadsheetFormula formula = SpreadsheetFormula.EMPTY.setText("=1+2");

            beforePatchCells.add(
                cell.setFormula(formula)
                    .setStyle(style)
            );
            patchedCells.add(
                cell.setFormula(
                    formula.setValue(
                        cellAndValue.getValue()
                    )
                ).setStyle(style)
            );
        }

        this.patchAndCheck(
            SpreadsheetDelta.EMPTY.setCells(beforePatchCells),
            patch,
            SpreadsheetDelta.EMPTY.setCells(patchedCells)
        );
    }

    // cellsValueTypePatch..............................................................................................

    @Test
    public void testCellsValueTypePatchWithNullMapFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetDelta.cellsValueTypePatch(
                null,
                JsonNodeMarshallContexts.fake()
            )
        );
    }

    @Test
    public void testCellsValueTypePatchWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetDelta.cellsValueTypePatch(
                Maps.empty(),
                null
            )
        );
    }

    @Test
    public void testCellsValueTypePatch() {
        final Optional<ValidationValueTypeName> valueType1 = Optional.of(ValidationValueTypeName.with("hello-value-type"));
        final Optional<ValidationValueTypeName> valueType2 = Optional.empty();

        this.cellsValueTypePatchAndCheck(
            Maps.of(
                SpreadsheetSelection.A1,
                valueType1,
                SpreadsheetSelection.parseCell("A2"),
                valueType2
            ),
            "{\n" +
                "  \"cells\": {\n" +
                "    \"A1\": {\n" +
                "      \"formula\": {\n" +
                "        \"valueType\": \"hello-value-type\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"A2\": {\n" +
                "      \"formula\": {\n" +
                "        \"valueType\": null\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}"
        );
    }

    private void cellsValueTypePatchAndCheck(final Map<SpreadsheetCellReference, Optional<ValidationValueTypeName>> cellToValueTypes,
                                             final String expected) {
        final JsonNode patch = SpreadsheetDelta.cellsValueTypePatch(
            cellToValueTypes,
            MARSHALL_CONTEXT
        );

        this.checkEquals(
            JsonNode.parse(expected),
            patch
        );

        final Set<SpreadsheetCell> beforePatchCells = SortedSets.tree(SpreadsheetCell.REFERENCE_COMPARATOR);
        final Set<SpreadsheetCell> patchedCells = SortedSets.tree(SpreadsheetCell.REFERENCE_COMPARATOR);
        final TextStyle style = TextStyle.EMPTY.set(
            TextStylePropertyName.COLOR,
            Color.BLACK
        );

        for (final Map.Entry<SpreadsheetCellReference, Optional<ValidationValueTypeName>> cellAndValueType : cellToValueTypes.entrySet()) {
            final SpreadsheetCellReference cell = cellAndValueType.getKey();

            final SpreadsheetFormula formula = SpreadsheetFormula.EMPTY.setText("'Patched over");

            beforePatchCells.add(
                cell.setFormula(formula)
                    .setStyle(style)
            );
            patchedCells.add(
                cell.setFormula(
                    formula.setValueType(cellAndValueType.getValue())
                ).setStyle(style)
            );
        }

        this.patchAndCheck(
            SpreadsheetDelta.EMPTY.setCells(beforePatchCells),
            patch,
            SpreadsheetDelta.EMPTY.setCells(patchedCells)
        );
    }

    // dateTimeSymbolsPatch.............................................................................................

    @Test
    public void testDateTimeSymbolsPatchWithNullPatternFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetDelta.dateTimeSymbolsPatch(
                null,
                MARSHALL_CONTEXT
            )
        );
    }

    @Test
    public void testDateTimeSymbolsPatchWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetDelta.dateTimeSymbolsPatch(
                Optional.of(
                    DateTimeSymbols.fromDateFormatSymbols(
                        new DateFormatSymbols(Locale.FRANCE)
                    )
                ),
                null
            )
        );
    }

    @Test
    public void testDateTimeSymbolsPatch() {
        final Optional<DateTimeSymbols> symbols = Optional.of(
            DateTimeSymbols.fromDateFormatSymbols(
                new DateFormatSymbols(Locale.FRANCE)
            )
        );

        this.checkEquals(
            JsonNode.object()
                .set(
                    SpreadsheetDelta.DATE_TIME_SYMBOLS_PROPERTY,
                    MARSHALL_CONTEXT.marshallOptional(symbols)
                )
            ,
            SpreadsheetDelta.dateTimeSymbolsPatch(
                symbols,
                MARSHALL_CONTEXT
            )
        );
    }

    @Test
    public void testDateTimeSymbolsPatchWithEmpty() {
        final Optional<DateTimeSymbols> symbols = Optional.empty();

        this.checkEquals(
            JsonNode.object()
                .set(
                    SpreadsheetDelta.DATE_TIME_SYMBOLS_PROPERTY,
                    MARSHALL_CONTEXT.marshallOptional(symbols)
                )
            ,
            SpreadsheetDelta.dateTimeSymbolsPatch(
                symbols,
                MARSHALL_CONTEXT
            )
        );
    }

    // decimalNumberSymbolsPatch........................................................................................

    @Test
    public void testDecimalNumberSymbolsPatchWithNullPatternFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetDelta.decimalNumberSymbolsPatch(
                null,
                MARSHALL_CONTEXT
            )
        );
    }

    @Test
    public void testDecimalNumberSymbolsPatchWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetDelta.decimalNumberSymbolsPatch(
                Optional.of(
                    DecimalNumberSymbols.fromDecimalFormatSymbols(
                        '+',
                        new DecimalFormatSymbols(Locale.FRANCE)
                    )
                ),
                null
            )
        );
    }

    @Test
    public void testDecimalNumberSymbolsPatch() {
        final Optional<DecimalNumberSymbols> symbols = Optional.of(
            DecimalNumberSymbols.fromDecimalFormatSymbols(
                '+',
                new DecimalFormatSymbols(Locale.FRANCE)
            )
        );

        this.checkEquals(
            JsonNode.object()
                .set(
                    SpreadsheetDelta.DECIMAL_NUMBER_SYMBOLS_PROPERTY,
                    MARSHALL_CONTEXT.marshallOptional(symbols)
                )
            ,
            SpreadsheetDelta.decimalNumberSymbolsPatch(
                symbols,
                MARSHALL_CONTEXT
            )
        );
    }

    @Test
    public void testDecimalNumberSymbolsPatchWithEmpty() {
        final Optional<DecimalNumberSymbols> symbols = Optional.empty();

        this.checkEquals(
            JsonNode.object()
                .set(
                    SpreadsheetDelta.DECIMAL_NUMBER_SYMBOLS_PROPERTY,
                    MARSHALL_CONTEXT.marshallOptional(symbols)
                )
            ,
            SpreadsheetDelta.decimalNumberSymbolsPatch(
                symbols,
                MARSHALL_CONTEXT
            )
        );
    }

    // formulaPatch.....................................................................................................

    @Test
    public void testFormulaPatchWithNullPatchFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetDelta.formulaPatch(null)
        );
    }

    @Test
    public void testFormulaPatch() {
        final SpreadsheetFormula formula = SpreadsheetFormula.EMPTY.setText("=1+2");

        final JsonNode patch = SpreadsheetDelta.formulaPatch(
            MARSHALL_CONTEXT.marshall(formula)
        );

        this.checkEquals(
            JsonNode.object()
                .set(
                    SpreadsheetDelta.FORMULA_PROPERTY,
                    marshall(formula)
                )
            ,
            patch
        );

        final TextStyle style = TextStyle.EMPTY.set(
            TextStylePropertyName.TEXT_ALIGN,
            TextAlign.CENTER
        );

        final SpreadsheetCell a1 = SpreadsheetSelection.A1.setFormula(
            SpreadsheetFormula.EMPTY.setText("'Will be Patched over")
        ).setStyle(style);

        final SpreadsheetCell a2 = SpreadsheetSelection.parseCell("a2")
            .setFormula(
                SpreadsheetFormula.EMPTY.setText("'Will be Patched over")
            );

        this.patchCellsAndCheck(
            SpreadsheetDelta.EMPTY.setCells(
                Sets.of(
                    a1,
                    a2
                )
            ),
            SpreadsheetSelection.parseCellOrCellRange("a1:a3"),
            patch,
            SpreadsheetDelta.EMPTY.setCells(
                Sets.of(
                    a1.setFormula(formula),
                    a2.setFormula(formula),
                    SpreadsheetSelection.parseCell("a3")
                        .setFormula(formula)
                )
            )
        );
    }

    // formatterPatch...................................................................................................

    @Test
    public void testFormatterPatchWithNullFormatterFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetDelta.formatterPatch(
                null,
                MARSHALL_CONTEXT
            )
        );
    }

    @Test
    public void testFormatterPatchWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetDelta.formatterPatch(
                Optional.of(
                    SpreadsheetPattern.parseDateFormatPattern("ddmmyyyyy")
                        .spreadsheetFormatterSelector()
                ),
                null
            )
        );
    }

    @Test
    public void testFormatterPatch() {
        final SpreadsheetFormatterSelector formatter = SpreadsheetPattern.parseTextFormatPattern("@@")
            .spreadsheetFormatterSelector();

        this.checkEquals(
            JsonNode.object()
                .set(
                    SpreadsheetDelta.FORMATTER_PROPERTY,
                    marshall(formatter)
                )
            ,
            SpreadsheetDelta.formatterPatch(
                Optional.of(formatter),
                MARSHALL_CONTEXT
            )
        );
    }

    @Test
    public void testFormatterPatchWithEmptyFormatter() {
        this.checkEquals(
            JsonNode.object()
                .setNull(
                    SpreadsheetDelta.FORMATTER_PROPERTY
                )
            ,
            SpreadsheetDelta.formatterPatch(
                Optional.empty(),
                MARSHALL_CONTEXT
            )
        );
    }

    // localePatch......................................................................................................

    @Test
    public void testLocalePatchWithNullLocaleFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetDelta.localePatch(
                null,
                MARSHALL_CONTEXT
            )
        );
    }

    @Test
    public void testLocalePatchWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetDelta.localePatch(
                Optional.of(
                    Locale.ENGLISH
                ),
                null
            )
        );
    }

    @Test
    public void testLocalePatch() {
        final Locale locale = Locale.forLanguageTag("en-AU");

        this.checkEquals(
            JsonNode.object()
                .set(
                    SpreadsheetDelta.LOCALE_PROPERTY,
                    marshall(locale)
                )
            ,
            SpreadsheetDelta.localePatch(
                Optional.of(locale),
                MARSHALL_CONTEXT
            )
        );
    }

    @Test
    public void testLocalePatchWithEmptyLocale() {
        this.checkEquals(
            JsonNode.object()
                .setNull(
                    SpreadsheetDelta.LOCALE_PROPERTY
                )
            ,
            SpreadsheetDelta.localePatch(
                Optional.empty(),
                MARSHALL_CONTEXT
            )
        );
    }

    // parserPatch......................................................................................................

    @Test
    public void testParserPatchWithNullSelectorFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetDelta.parserPatch(
                null,
                MARSHALL_CONTEXT
            )
        );
    }

    @Test
    public void testParserPatchWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetDelta.parserPatch(
                Optional.of(
                    SpreadsheetPattern.parseDateParsePattern("ddmmyyyyy")
                        .spreadsheetParserSelector()
                ),
                null
            )
        );
    }

    @Test
    public void testParserPatch() {
        final SpreadsheetParserSelector parserSelector = SpreadsheetPattern.parseNumberParsePattern("0.00")
            .spreadsheetParserSelector();

        this.checkEquals(
            JsonNode.object()
                .set(
                    SpreadsheetDelta.PARSER_PROPERTY,
                    marshall(parserSelector)
                )
            ,
            SpreadsheetDelta.parserPatch(
                Optional.of(parserSelector),
                MARSHALL_CONTEXT
            )
        );
    }

    @Test
    public void testParserPatchWithEmptySelector() {
        this.checkEquals(
            JsonNode.object()
                .setNull(
                    SpreadsheetDelta.PARSER_PROPERTY
                )
            ,
            SpreadsheetDelta.parserPatch(
                Optional.empty(),
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

        final JsonNode stylePatch = propertyName.stylePatch(color);

        final JsonNode deltaPatch = SpreadsheetDelta.stylePatch(stylePatch);
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

        final JsonNode stylePatch = propertyName.stylePatch(color);

        final JsonNode deltaPatch = SpreadsheetDelta.stylePatch(stylePatch);
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

    // validatorPatch...................................................................................................

    @Test
    public void testValidatorPatchWithNullPatternFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetDelta.validatorPatch(
                null,
                MARSHALL_CONTEXT
            )
        );
    }

    @Test
    public void testValidatorPatchWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetDelta.validatorPatch(
                Optional.of(
                    ValidatorSelector.parse("hello")
                ),
                null
            )
        );
    }

    @Test
    public void testValidatorPatch() {
        final ValidatorSelector validatorSelector = ValidatorSelector.parse("hello");

        this.checkEquals(
            JsonNode.object()
                .set(
                    SpreadsheetDelta.VALIDATOR_PROPERTY,
                    marshall(validatorSelector)
                )
            ,
            SpreadsheetDelta.validatorPatch(
                Optional.of(validatorSelector),
                MARSHALL_CONTEXT
            )
        );
    }

    @Test
    public void testValidatorPatchWithEmptyPattern() {
        this.checkEquals(
            JsonNode.object()
                .setNull(
                    SpreadsheetDelta.VALIDATOR_PROPERTY
                )
            ,
            SpreadsheetDelta.validatorPatch(
                Optional.empty(),
                MARSHALL_CONTEXT
            )
        );
    }

    // valuePatch.......................................................................................................

    @Test
    public void testValuePatchWithNullValueFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetDelta.valuePatch(
                null,
                MARSHALL_CONTEXT
            )
        );
    }

    @Test
    public void testValuePatchWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetDelta.valuePatch(
                Optional.empty(),
                null
            )
        );
    }

    @Test
    public void testValuePatch() {
        final Optional<Object> value = Optional.of(
            ExpressionNumberKind.BIG_DECIMAL.create(123)
        );

        this.checkEquals(
            JsonNode.parse(
                "{\n" +
                    "  \"formula\": {\n" +
                    "    \"value\": {\n" +
                    "      \"type\": \"expression-number\",\n" +
                    "      \"value\": \"123\"\n" +
                    "    }\n" +
                    "  }\n" +
                    "}"
            ),
            SpreadsheetDelta.valuePatch(
                value,
                MARSHALL_CONTEXT
            )
        );
    }

    // valueTypePatch...................................................................................................

    @Test
    public void testValueTypePatchWithNullValueTypeFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetDelta.valueTypePatch(
                null,
                MARSHALL_CONTEXT
            )
        );
    }

    @Test
    public void testValueTypePatchWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetDelta.valueTypePatch(
                Optional.empty(),
                null
            )
        );
    }

    @Test
    public void testValueTypePatch() {
        final Optional<ValidationValueTypeName> valueType = Optional.of(
            ValidationValueTypeName.with("Text123")
        );

        this.checkEquals(
            JsonNode.parse(
                "{\n" +
                    "  \"formula\": {\n" +
                    "    \"valueType\": \"Text123\"\n" +
                    "  }\n" +
                    "}"
            ),
            SpreadsheetDelta.valueTypePatch(
                valueType,
                MARSHALL_CONTEXT
            )
        );
    }

    // Patch............................................................................................................

    @Test
    public void testPatchWithEmptyObject() {
        this.patchAndCheck(
            SpreadsheetDelta.EMPTY,
            JsonNode.object()
        );
    }

    @Test
    public void testPatchWithLabelsFails() {
        this.patchInvalidPropertyFails2(
            SpreadsheetDelta.LABELS_PROPERTY,
            JsonNode.nullNode()

        );
    }

    @Test
    public void testPatchWithDeletedCellsFails() {
        this.patchInvalidPropertyFails2(
            SpreadsheetDelta.DELETED_CELLS_PROPERTY,
            JsonNode.nullNode()
        );
    }

    @Test
    public void testPatchWithDeletedColumnsFails() {
        this.patchInvalidPropertyFails2(
            SpreadsheetDelta.DELETED_COLUMNS_PROPERTY,
            JsonNode.nullNode()
        );
    }

    @Test
    public void testPatchWithDeletedRowsFails() {
        this.patchInvalidPropertyFails2(
            SpreadsheetDelta.DELETED_ROWS_PROPERTY,
            JsonNode.nullNode()
        );
    }

    @Test
    public void testPatchWithColumnWidthFails() {
        this.patchInvalidPropertyFails2(
            SpreadsheetDelta.COLUMN_WIDTHS_PROPERTY,
            JsonNode.nullNode()
        );
    }

    @Test
    public void testPatchWithRowHeightFails() {
        this.patchInvalidPropertyFails2(
            SpreadsheetDelta.ROW_HEIGHTS_PROPERTY,
            JsonNode.nullNode()
        );
    }

    private void patchInvalidPropertyFails2(final JsonPropertyName key,
                                            final JsonNode value) {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createPatchable()
                    .patch(
                        JsonNode.object()
                            .set(
                                key,
                                value
                            ),
                        this.createPatchContext()
                    )
        );
    }

    @Test
    public void testPatchWithNoViewport() {
        this.patchViewportAndCheck(
            SpreadsheetDelta.NO_VIEWPORT,
            SpreadsheetDelta.NO_VIEWPORT
        );
    }

    @Test
    public void testPatchWithViewport() {
        this.patchViewportAndCheck(
            SpreadsheetDelta.NO_VIEWPORT,
            Optional.of(
                SpreadsheetSelection.A1.viewportRectangle(100, 40)
                    .viewport()
                    .setAnchoredSelection(
                        Optional.of(
                            SpreadsheetSelection.parseColumn("C")
                                .setDefaultAnchor()
                        )
                    )
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
    public void testPatchWithViewportCleared() {
        this.patchAndCheck(
            SpreadsheetDelta.EMPTY
                .setViewport(
                    Optional.of(
                        SpreadsheetSelection.A1.viewportRectangle(100, 40)
                            .viewport()
                            .setAnchoredSelection(
                                Optional.of(
                                    SpreadsheetSelection.parseCellRange("A1:B2")
                                        .setAnchor(SpreadsheetViewportAnchor.BOTTOM_RIGHT)
                                )
                            )
                    )
                )
            ,
            JsonNode.object()
                .setNull(
                    SpreadsheetDelta.VIEWPORT_SELECTION_PROPERTY
                ),
            SpreadsheetDelta.EMPTY
        );
    }

    @Test
    public void testPatchWithNewCell() {
        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(
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
    public void testPatchWithReplacesCell() {
        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(
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
    public void testPatchWithCellsRemoved() {
        final SpreadsheetDelta without = SpreadsheetDelta.EMPTY
            .setViewport(
                Optional.of(
                    SpreadsheetSelection.A1.viewportRectangle(100, 40)
                        .viewport()
                        .setAnchoredSelection(
                            Optional.of(
                                SpreadsheetSelection.parseCellRange("A1:B2")
                                    .setAnchor(
                                        SpreadsheetViewportAnchor.TOP_LEFT
                                    )
                            )
                        )
                )
            );
        this.patchAndCheck(
            without.setCells(
                Sets.of(
                    SpreadsheetSelection.A1.setFormula(
                        SpreadsheetFormula.EMPTY
                            .setText("=1")
                    )
                )
            )
            ,
            JsonNode.object()
                .setNull(SpreadsheetDelta.CELLS_PROPERTY),
            without
        );
    }

    @Test
    public void testPatchWithNewColumn() {
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
    public void testPatchWithNewRow() {
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
    public void testPatchWithWindow() {
        final SpreadsheetCell a1 = SpreadsheetSelection.A1.setFormula(
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
    public void testPatchWithWindowReplaced() {
        final SpreadsheetCell a1 = SpreadsheetSelection.A1.setFormula(
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
    public void testPatchWithWindowRemoved() {
        final SpreadsheetCell a1 = SpreadsheetSelection.A1.setFormula(
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
                .set(
                    SpreadsheetDelta.WINDOW_PROPERTY,
                    JsonNode.string("")
                ),
            before.setWindow(SpreadsheetDelta.NO_WINDOW)
        );
    }

    @Test
    public void testPatchWithDateTimeSymbols() {
        final SpreadsheetCell a1 = SpreadsheetSelection.A1
            .setFormula(SpreadsheetFormula.EMPTY)
            .setDateTimeSymbols(
                Optional.of(
                    DateTimeSymbols.fromDateFormatSymbols(
                        new DateFormatSymbols(Locale.ENGLISH)
                    )
                )
            );
        final SpreadsheetCell a2 = SpreadsheetSelection.parseCell("A2")
            .setFormula(SpreadsheetFormula.EMPTY)
            .setDateTimeSymbols(
                Optional.empty()
            );

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
            .setCells(
                Sets.of(a1, a2)
            );

        final Optional<DateTimeSymbols> symbols = Optional.of(
            DateTimeSymbols.fromDateFormatSymbols(
                new DateFormatSymbols(Locale.GERMANY)
            )
        );

        final SpreadsheetDelta after = before.setCells(
            Sets.of(
                a1.setDateTimeSymbols(symbols),
                a2.setDateTimeSymbols(symbols)
            )
        );

        this.patchAndCheck(
            before,
            SpreadsheetDelta.dateTimeSymbolsPatch(
                symbols,
                MARSHALL_CONTEXT
            ),
            after
        );
    }

    @Test
    public void testPatchWithDecimalNumberSymbols() {
        final SpreadsheetCell a1 = SpreadsheetSelection.A1
            .setFormula(SpreadsheetFormula.EMPTY)
            .setDecimalNumberSymbols(
                Optional.of(
                    DecimalNumberSymbols.fromDecimalFormatSymbols(
                        '+',
                        new DecimalFormatSymbols(Locale.ENGLISH)
                    )
                )
            );
        final SpreadsheetCell a2 = SpreadsheetSelection.parseCell("A2")
            .setFormula(SpreadsheetFormula.EMPTY)
            .setDecimalNumberSymbols(
                Optional.empty()
            );

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
            .setCells(
                Sets.of(a1, a2)
            );

        final Optional<DecimalNumberSymbols> symbols = Optional.of(
            DecimalNumberSymbols.fromDecimalFormatSymbols(
                '+',
                new DecimalFormatSymbols(Locale.FRANCE)
            )
        );

        final SpreadsheetDelta after = before.setCells(
            Sets.of(
                a1.setDecimalNumberSymbols(symbols),
                a2.setDecimalNumberSymbols(symbols)
            )
        );

        this.patchAndCheck(
            before,
            SpreadsheetDelta.decimalNumberSymbolsPatch(
                symbols,
                MARSHALL_CONTEXT
            ),
            after
        );
    }

    @Test
    public void testPatchWithFormatter() {
        final Optional<SpreadsheetFormatterSelector> formatter = Optional.of(
            SpreadsheetFormatterSelector.parse("hello-formatter")
        );

        final SpreadsheetCell a1 = SpreadsheetSelection.A1.setFormula(
            SpreadsheetFormula.EMPTY
                .setText("=1")
        );
        final SpreadsheetCell b2 = SpreadsheetSelection.parseCell("b2")
            .setFormula(
                SpreadsheetFormula.EMPTY
                    .setText("=99")
            );

        this.patchAndCheck(
            SpreadsheetDelta.EMPTY
                .setCells(
                    Sets.of(
                        a1,
                        b2
                    )
                ),
            SpreadsheetDelta.formatterPatch(
                formatter,
                MARSHALL_CONTEXT
            ),
            SpreadsheetDelta.EMPTY
                .setCells(
                    Sets.of(
                        a1.setFormatter(formatter),
                        b2.setFormatter(formatter)
                    )
                )
        );
    }

    @Test
    public void testPatchWithLocale() {
        final SpreadsheetCell a1 = SpreadsheetSelection.A1
            .setFormula(SpreadsheetFormula.EMPTY)
            .setLocale(
                Optional.of(Locale.ENGLISH)
            );
        final SpreadsheetCell a2 = SpreadsheetSelection.parseCell("A2")
            .setFormula(SpreadsheetFormula.EMPTY)
            .setLocale(
                Optional.empty()
            );

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
            .setCells(
                Sets.of(a1, a2)
            );

        final Optional<Locale> locale = Optional.of(Locale.FRANCE);

        final SpreadsheetDelta after = before.setCells(
            Sets.of(
                a1.setLocale(locale),
                a2.setLocale(locale)
            )
        );

        this.patchAndCheck(
            before,
            SpreadsheetDelta.localePatch(
                locale,
                MARSHALL_CONTEXT
            ),
            after
        );
    }
    
    @Test
    public void testPatchWithParser() {
        final Optional<SpreadsheetParserSelector> parser = Optional.of(
            SpreadsheetParserSelector.parse("hello-parser")
        );

        final SpreadsheetCell a1 = SpreadsheetSelection.A1.setFormula(
            SpreadsheetFormula.EMPTY
                .setText("=1")
        );
        final SpreadsheetCell b2 = SpreadsheetSelection.parseCell("b2")
            .setFormula(
                SpreadsheetFormula.EMPTY
                    .setText("=99")
            );

        this.patchAndCheck(
            SpreadsheetDelta.EMPTY
                .setCells(
                    Sets.of(
                        a1,
                        b2
                    )
                ),
            SpreadsheetDelta.parserPatch(
                parser,
                MARSHALL_CONTEXT
            ),
            SpreadsheetDelta.EMPTY
                .setCells(
                    Sets.of(
                        a1.setParser(parser),
                        b2.setParser(parser)
                    )
                )
        );
    }

    @Test
    public void testPatchWithStyle() {
        final TextStyle style = TextStyle.EMPTY.set(
            TextStylePropertyName.COLOR,
            Color.BLACK
        );

        final SpreadsheetCell a1 = SpreadsheetSelection.A1.setFormula(
            SpreadsheetFormula.EMPTY
                .setText("=1")
        );
        final SpreadsheetCell b2 = SpreadsheetSelection.parseCell("b2")
            .setFormula(
                SpreadsheetFormula.EMPTY
                    .setText("=99")
            );

        this.patchAndCheck(
            SpreadsheetDelta.EMPTY
                .setCells(
                    Sets.of(
                        a1,
                        b2
                    )
                ),
            SpreadsheetDelta.stylePatch(
                this.marshall(style)
            ),
            SpreadsheetDelta.EMPTY
                .setCells(
                    Sets.of(
                        a1.setStyle(style),
                        b2.setStyle(style)
                    )
                )
        );
    }

    @Test
    public void testPatchWithValidator() {
        final SpreadsheetCell a1 = SpreadsheetSelection.A1
            .setFormula(SpreadsheetFormula.EMPTY)
            .setValidator(
                Optional.of(
                    ValidatorSelector.parse("before-validator")
                )
            );
        final SpreadsheetCell a2 = SpreadsheetSelection.parseCell("A2")
            .setFormula(SpreadsheetFormula.EMPTY)
            .setValidator(
                Optional.empty()
            );

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
            .setCells(
                Sets.of(a1, a2)
            );

        final Optional<ValidatorSelector> validator = Optional.of(
            ValidatorSelector.parse("patched-validator1")
        );

        final SpreadsheetDelta after = before.setCells(
            Sets.of(
                a1.setValidator(validator),
                a2.setValidator(validator)
            )
        );

        this.patchAndCheck(
            before,
            SpreadsheetDelta.validatorPatch(
                validator,
                MARSHALL_CONTEXT
            ),
            after
        );
    }

    @Test
    public void testPatchWithValue() {
        final SpreadsheetCell a1 = SpreadsheetSelection.A1
            .setFormula(
                SpreadsheetFormula.EMPTY.setValue(
                    Optional.of(111)
                )
            );
        final SpreadsheetCell a2 = SpreadsheetSelection.parseCell("A2")
            .setFormula(
                SpreadsheetFormula.EMPTY.setValue(
                    Optional.of(222)
                )
            );

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
            .setCells(
                Sets.of(a1, a2)
            );

        final Optional<Object> value = Optional.of(
            "Hello"
        );

        final SpreadsheetDelta after = before.setCells(
            Sets.of(
                a1.setFormula(
                    a1.formula()
                        .setValue(value)
                ),
                a2.setFormula(
                    a2.formula()
                        .setValue(value)
                )
            )
        );

        this.patchAndCheck(
            before,
            SpreadsheetDelta.valuePatch(
                value,
                MARSHALL_CONTEXT
            ),
            after
        );
    }

    @Test
    public void testPatchWithValueType() {
        final Optional<ValidationValueTypeName> valueType = Optional.of(
            ValidationValueTypeName.with("hello-value-type")
        );

        final SpreadsheetCell a1 = SpreadsheetSelection.A1.setFormula(
            SpreadsheetFormula.EMPTY
                .setText("=1")
                .setValueType(
                    Optional.of(
                        ValidationValueTypeName.with("lost-value-type")
                    )
                )
        );
        final SpreadsheetCell b2 = SpreadsheetSelection.parseCell("b2")
            .setFormula(
                SpreadsheetFormula.EMPTY
                    .setText("=99")
            );

        this.patchAndCheck(
            SpreadsheetDelta.EMPTY
                .setCells(
                    Sets.of(
                        a1,
                        b2
                    )
                ),
            SpreadsheetDelta.valueTypePatch(
                valueType,
                MARSHALL_CONTEXT
            ),
            SpreadsheetDelta.EMPTY
                .setCells(
                    Sets.of(
                        a1.setFormula(
                            a1.formula()
                                .setValueType(valueType)
                        ),
                        b2.setFormula(
                            b2.formula()
                                .setValueType(valueType)
                        )
                    )
                )
        );
    }

    @Test
    public void testPatchCellFormatterAndParser() {
        final SpreadsheetCell a1 = SpreadsheetSelection.A1.setFormula(
            SpreadsheetFormula.EMPTY
                .setText("=1")
                .setValueType(
                    Optional.of(
                        ValidationValueTypeName.with("lost-value-type")
                    )
                )
        );
        final SpreadsheetCell b2 = SpreadsheetSelection.parseCell("b2")
            .setFormula(
                SpreadsheetFormula.EMPTY
                    .setText("=99")
            );

        final Optional<SpreadsheetFormatterSelector> formatter = Optional.of(
            SpreadsheetFormatterSelector.parse("patched-formatter")
        );

        final Optional<SpreadsheetParserSelector> parser = Optional.of(
            SpreadsheetParserSelector.parse("patched-parser")
        );


        final JsonNode patch = SpreadsheetDelta.formatterPatch(
                formatter,
                MARSHALL_CONTEXT
            ).objectOrFail()
            .merge(
                SpreadsheetDelta.parserPatch(
                    parser,
                    MARSHALL_CONTEXT
                ).objectOrFail()
            );

        this.patchAndCheck(
            SpreadsheetDelta.EMPTY
                .setCells(
                    Sets.of(
                        a1.setFormatter(
                            Optional.of(
                                SpreadsheetFormatterSelector.parse("lost-formatter")
                            )
                        ),
                        b2.setParser(
                            Optional.of(
                                SpreadsheetParserSelector.parse("lost-parser")
                            )
                        )
                    )
                ),
            patch,
            SpreadsheetDelta.EMPTY
                .setCells(
                    Sets.of(
                        a1.setFormatter(formatter)
                            .setParser(parser),
                        b2.setFormatter(formatter)
                            .setParser(parser)
                    )
                )
        );
    }

    // PatchCells.......................................................................................................

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
    public void testPatchCellsWithPatchCellsOutsideSelectionFails() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetDelta.EMPTY.patchCells(
                SpreadsheetSelection.A1,
                marshall(
                    SpreadsheetDelta.EMPTY
                        .setCells(
                            Sets.of(
                                SpreadsheetSelection.parseCell("A2")
                                    .setFormula(
                                        SpreadsheetFormula.EMPTY.setText("=1")
                                    )
                            )
                        )
                ),
                JsonNodeUnmarshallContexts.fake()
            )
        );
        this.checkEquals(
            "Patch includes cells A2 outside A1",
            thrown.getMessage(),
            "messages"
        );
    }

    @Test
    public void testPatchCellsWithPatchCellsOutsideSelectionFails2() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetDelta.EMPTY.patchCells(
                SpreadsheetSelection.parseCell("A2"),
                marshall(
                    SpreadsheetDelta.EMPTY
                        .setCells(
                            Sets.of(
                                SpreadsheetSelection.A1
                                    .setFormula(
                                        SpreadsheetFormula.EMPTY.setText("=1")
                                    ),
                                SpreadsheetSelection.parseCell("A2")
                                    .setFormula(
                                        SpreadsheetFormula.EMPTY.setText("=2")
                                    ),
                                SpreadsheetSelection.parseCell("B1")
                                    .setFormula(
                                        SpreadsheetFormula.EMPTY.setText("=3")
                                    )
                            )
                        )
                ),
                JsonNodeUnmarshallContexts.fake()
            )
        );
        this.checkEquals(
            "Patch includes cells A1, B1 outside A2",
            thrown.getMessage(),
            "messages"
        );
    }

    @Test
    public void testPatchCellsWithPatchCellsOutsideSelectionFails3() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetDelta.EMPTY.patchCells(
                SpreadsheetSelection.A1,
                marshall(
                    SpreadsheetDelta.EMPTY
                        .setCells(
                            Sets.of(
                                SpreadsheetSelection.A1
                                    .setFormula(
                                        SpreadsheetFormula.EMPTY.setText("=1")
                                    ),
                                SpreadsheetSelection.parseCell("A2")
                                    .setFormula(
                                        SpreadsheetFormula.EMPTY.setText("=2")
                                    ),
                                SpreadsheetSelection.parseCell("B1")
                                    .setFormula(
                                        SpreadsheetFormula.EMPTY.setText("=3")
                                    )
                            )
                        )
                ),
                this.createPatchContext() // required to unmarshall A1
            )
        );
        this.checkEquals(
            "Patch includes cells B1, A2 outside A1",
            thrown.getMessage(),
            "messages"
        );
    }

    @Test
    public void testPatchCellsColumnsFails() {
        this.patchCellsWithInvalidPropertyFails(
            SpreadsheetDelta.COLUMNS_PROPERTY,
            JsonNode.nullNode()
        );
    }

    @Test
    public void testPatchCellsLabelsFails() {
        this.patchCellsWithInvalidPropertyFails(
            SpreadsheetDelta.LABELS_PROPERTY,
            JsonNode.nullNode()
        );
    }

    @Test
    public void testPatchCellsRowsFails() {
        this.patchCellsWithInvalidPropertyFails(
            SpreadsheetDelta.ROWS_PROPERTY,
            JsonNode.nullNode()
        );
    }

    @Test
    public void testPatchCellsDeletedCellsFails() {
        this.patchCellsWithInvalidPropertyFails(
            SpreadsheetDelta.DELETED_CELLS_PROPERTY,
            JsonNode.nullNode()
        );
    }

    @Test
    public void testPatchCellsDeletedRowsFails() {
        this.patchCellsWithInvalidPropertyFails(
            SpreadsheetDelta.DELETED_ROWS_PROPERTY,
            JsonNode.nullNode()
        );
    }

    @Test
    public void testPatchCellsColumnWidthFails() {
        this.patchCellsWithInvalidPropertyFails(
            SpreadsheetDelta.COLUMN_WIDTHS_PROPERTY,
            JsonNode.nullNode()
        );
    }

    @Test
    public void testPatchCellsRowHeightFails() {
        this.patchCellsWithInvalidPropertyFails(
            SpreadsheetDelta.ROW_HEIGHTS_PROPERTY,
            JsonNode.nullNode()
        );
    }

    @Test
    public void testPatchCellsFormattedValueFails() {
        this.patchCellsWithInvalidPropertyFails(
            JsonPropertyName.with("formattedValue"),
            JsonNode.nullNode()
        );
    }

    private void patchCellsWithInvalidPropertyFails(final JsonPropertyName key,
                                                    final JsonNode value) {
        final JsonNode patch = JsonNode.object()
            .set(
                key,
                value
            );

        assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetDelta.EMPTY.patchCells(
                SpreadsheetSelection.A1,
                patch,
                this.createPatchContext()
            )
        );
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
            SpreadsheetDelta.NO_VIEWPORT,
            SpreadsheetDelta.NO_VIEWPORT
        );
    }

    @Test
    public void testPatchCellsViewport() {
        this.patchCellViewportAndCheck(
            SpreadsheetDelta.NO_VIEWPORT,
            Optional.of(
                SpreadsheetSelection.A1.viewportRectangle(100, 40)
                    .viewport()
                    .setAnchoredSelection(
                        Optional.of(
                            SpreadsheetSelection.parseColumn("C")
                                .setDefaultAnchor()
                        )
                    )
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
                        SpreadsheetSelection.A1.viewportRectangle(100, 40)
                            .viewport()
                            .setAnchoredSelection(
                                Optional.of(
                                    SpreadsheetSelection.parseCellRange("A1:B2")
                                        .setAnchor(SpreadsheetViewportAnchor.BOTTOM_RIGHT)
                                )

                            )
                    )
                )
            ,
            SpreadsheetSelection.A1,
            JsonNode.object()
                .setNull(SpreadsheetDelta.VIEWPORT_SELECTION_PROPERTY),
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
                    SpreadsheetSelection.A1.viewportRectangle(100, 40)
                        .viewport()
                        .setAnchoredSelection(
                            Optional.of(
                                SpreadsheetSelection.parseCellRange("A1:B2")
                                    .setAnchor(SpreadsheetViewportAnchor.TOP_LEFT)
                            )
                        )
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
                .setNull(SpreadsheetDelta.CELLS_PROPERTY),
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
    public void testPatchCellAndFormatterFails() {
        final SpreadsheetCell a1 = SpreadsheetSelection.A1
            .setFormula(SpreadsheetFormula.EMPTY);
        final SpreadsheetDelta delta = SpreadsheetDelta.EMPTY.setCells(
            Sets.of(a1)
        );
        final JsonNode patch = this.marshall(delta)
            .objectOrFail()
            .merge(
                SpreadsheetDelta.formatterPatch(
                    Optional.of(
                        SpreadsheetPattern.parseTextFormatPattern("@")
                            .spreadsheetFormatterSelector()
                    ),
                    MARSHALL_CONTEXT
                ).objectOrFail()
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
            "Invalid patch includes: cell, formatter",
            thrown.getMessage(),
            "message"
        );
    }

    @Test
    public void testPatchCellAndParserFails() {
        final SpreadsheetCell a1 = SpreadsheetSelection.A1
            .setFormula(SpreadsheetFormula.EMPTY);
        final SpreadsheetDelta delta = SpreadsheetDelta.EMPTY.setCells(
            Sets.of(a1)
        );
        final JsonNode patch = this.marshall(delta)
            .objectOrFail()
            .merge(
                SpreadsheetDelta.parserPatch(
                    Optional.of(
                        SpreadsheetPattern.parseDateParsePattern("dd/mm/yyyy")
                            .spreadsheetParserSelector()
                    ),
                    MARSHALL_CONTEXT
                ).objectOrFail()
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
            "Invalid patch includes: cell, parser",
            thrown.getMessage(),
            "message"
        );
    }

    @Test
    public void testPatchCellsWithFormula() {
        final TextStyle style = TextStyle.EMPTY.set(
            TextStylePropertyName.TEXT_ALIGN,
            TextAlign.CENTER
        );

        final SpreadsheetCell a1 = SpreadsheetSelection.A1
            .setFormula(SpreadsheetFormula.EMPTY.setText("'will be patched over"))
            .setStyle(style);

        final SpreadsheetCell a3 = SpreadsheetSelection.A1
            .setFormula(SpreadsheetFormula.EMPTY.setText("'not patched"));

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
            .setCells(
                Sets.of(
                    a1,
                    a3
                )
            );

        final SpreadsheetFormula patched = SpreadsheetFormula.EMPTY.setText("'patched formula");

        final SpreadsheetDelta after = before.setCells(
            Sets.of(
                a1.setFormula(patched),
                SpreadsheetSelection.parseCell("A2")
                    .setFormula(patched),
                a3
            )
        );

        this.patchCellsAndCheck(
            before,
            SpreadsheetSelection.parseCellRange("A1:A2"),
            SpreadsheetDelta.formulaPatch(
                MARSHALL_CONTEXT.marshall(patched)
            ),
            after
        );
    }

    @Test
    public void testPatchCellsWithFormulaValue() {
        final SpreadsheetCell a1 = SpreadsheetSelection.A1
            .setFormula(
                SpreadsheetFormula.EMPTY.setText("=1")
                    .setValue(
                        Optional.of("LostValue")
                    )
            ).setStyle(
                TextStyle.EMPTY.set(
                    TextStylePropertyName.TEXT_ALIGN,
                    TextAlign.CENTER
                )
            );

        final SpreadsheetCell a2 = SpreadsheetSelection.parseCell("A2")
            .setFormula(
                SpreadsheetFormula.EMPTY.setText("=2")
            );

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
            .setCells(
                Sets.of(
                    a1,
                    a2
                )
            );

        final Optional<Object> value = Optional.of("PatchedValue");

        final SpreadsheetDelta after = before.setCells(
            Sets.of(
                a1.setFormula(
                    SpreadsheetFormula.EMPTY.setValue(value)
                ),
                a2.setFormula(
                    SpreadsheetFormula.EMPTY.setValue(value)
                ),
                SpreadsheetSelection.parseCell("a3")
                    .setFormula(
                        SpreadsheetFormula.EMPTY.setValue(value)
                    )
            )
        );

        this.patchCellsAndCheck(
            before,
            SpreadsheetSelection.parseCellRange("A1:A3"),
            SpreadsheetDelta.formulaPatch(
                SpreadsheetFormula.valuePatch(
                    value,
                    MARSHALL_CONTEXT
                )
            ),
            after
        );
    }

    @Test
    public void testPatchCellsWithFormulaValueType() {
        final SpreadsheetCell a1 = SpreadsheetSelection.A1
            .setFormula(
                SpreadsheetFormula.EMPTY.setText("'will be patched over")
                    .setValueType(
                        Optional.of(
                            ValidationValueTypeName.with("LostValueType")
                        )
                    )
            ).setStyle(
                TextStyle.EMPTY.set(
                    TextStylePropertyName.TEXT_ALIGN,
                    TextAlign.CENTER
                )
            );

        final SpreadsheetCell a3 = SpreadsheetSelection.A1
            .setFormula(
                SpreadsheetFormula.EMPTY.setText("'not patched")
            );

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
            .setCells(
                Sets.of(
                    a1,
                    a3
                )
            );

        final Optional<ValidationValueTypeName> typeName = Optional.of(
            ValidationValueTypeName.with("patchedValueType")
        );

        final SpreadsheetDelta after = before.setCells(
            Sets.of(
                a1.setFormula(
                    a1.formula()
                        .setValueType(typeName)
                ),
                SpreadsheetSelection.parseCell("A2")
                    .setFormula(
                        SpreadsheetFormula.EMPTY.setValueType(typeName)
                    )
            )
        );

        this.patchCellsAndCheck(
            before,
            SpreadsheetSelection.parseCellRange("A1:A2"),
            SpreadsheetDelta.formulaPatch(
                SpreadsheetFormula.valueTypePatch(
                    typeName,
                    MARSHALL_CONTEXT
                )
            ),
            after
        );
    }

    @Test
    public void testPatchCellsWithDateTimeSymbols() {
        final SpreadsheetCell a1 = SpreadsheetSelection.A1
            .setFormula(SpreadsheetFormula.EMPTY)
            .setDateTimeSymbols(
                Optional.of(
                    DateTimeSymbols.fromDateFormatSymbols(
                        new DateFormatSymbols(Locale.ENGLISH)
                    )
                )
            );
        final SpreadsheetCell a2 = SpreadsheetSelection.parseCell("A2")
            .setFormula(SpreadsheetFormula.EMPTY)
            .setDateTimeSymbols(
                Optional.empty()
            );

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
            .setCells(
                Sets.of(a1, a2)
            );

        final Optional<DateTimeSymbols> symbols1 = Optional.of(
            DateTimeSymbols.fromDateFormatSymbols(
                new DateFormatSymbols(Locale.FRANCE)
            )
        );
        final Optional<DateTimeSymbols> symbols2 = Optional.of(
            DateTimeSymbols.fromDateFormatSymbols(
                new DateFormatSymbols(Locale.GERMANY)
            )
        );

        final SpreadsheetDelta after = before.setCells(
            Sets.of(
                a1.setDateTimeSymbols(symbols1),
                a2.setDateTimeSymbols(symbols2)
            )
        );

        this.patchCellsAndCheck(
            before,
            SpreadsheetSelection.parseCellRange("A1:A2"),
            SpreadsheetDelta.cellsDateTimeSymbolsPatch(
                Maps.of(
                    SpreadsheetSelection.A1,
                    symbols1,
                    SpreadsheetSelection.parseCell("A2"),
                    symbols2
                ),
                MARSHALL_CONTEXT
            ),
            after
        );
    }

    @Test
    public void testPatchCellsWithDecimalNumberSymbols() {
        final SpreadsheetCell a1 = SpreadsheetSelection.A1
            .setFormula(SpreadsheetFormula.EMPTY)
            .setDecimalNumberSymbols(
                Optional.of(
                    DecimalNumberSymbols.fromDecimalFormatSymbols(
                        '+',
                        new DecimalFormatSymbols(Locale.ENGLISH)
                    )
                )
            );
        final SpreadsheetCell a2 = SpreadsheetSelection.parseCell("A2")
            .setFormula(SpreadsheetFormula.EMPTY)
            .setDecimalNumberSymbols(
                Optional.empty()
            );

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
            .setCells(
                Sets.of(a1, a2)
            );

        final Optional<DecimalNumberSymbols> symbols1 = Optional.of(
            DecimalNumberSymbols.fromDecimalFormatSymbols(
                '+',
                new DecimalFormatSymbols(Locale.FRANCE)
            )
        );
        final Optional<DecimalNumberSymbols> symbols2 = Optional.of(
            DecimalNumberSymbols.fromDecimalFormatSymbols(
                '+',
                new DecimalFormatSymbols(Locale.GERMANY)
            )
        );

        final SpreadsheetDelta after = before.setCells(
            Sets.of(
                a1.setDecimalNumberSymbols(symbols1),
                a2.setDecimalNumberSymbols(symbols2)
            )
        );

        this.patchCellsAndCheck(
            before,
            SpreadsheetSelection.parseCellRange("A1:A2"),
            SpreadsheetDelta.cellsDecimalNumberSymbolsPatch(
                Maps.of(
                    SpreadsheetSelection.A1,
                    symbols1,
                    SpreadsheetSelection.parseCell("A2"),
                    symbols2
                ),
                MARSHALL_CONTEXT
            ),
            after
        );
    }

    @Test
    public void testPatchCellsWithFormatterWithMissingCells() {
        final Optional<SpreadsheetFormatterSelector> beforeFormatter = Optional.of(
            SpreadsheetPattern.parseTextFormatPattern("@\"before\"")
                .spreadsheetFormatterSelector()
        );

        final SpreadsheetCell a1 = SpreadsheetSelection.A1
            .setFormula(SpreadsheetFormula.EMPTY)
            .setFormatter(beforeFormatter);

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
            .setCells(
                Sets.of(a1)
            );

        final SpreadsheetFormatterSelector patchFormatter = SpreadsheetPattern.parseTextFormatPattern("@\"patched\"")
            .spreadsheetFormatterSelector();

        final SpreadsheetDelta after = before.setCells(
            Sets.of(
                a1.setFormatter(Optional.of(patchFormatter)),
                SpreadsheetSelection.parseCell("A2")
                    .setFormula(SpreadsheetFormula.EMPTY)
                    .setFormatter(Optional.of(patchFormatter))
            )
        );

        this.patchCellsAndCheck(
            before,
            SpreadsheetSelection.parseCellRange("A1:A2"),
            SpreadsheetDelta.formatterPatch(
                Optional.of(patchFormatter),
                MARSHALL_CONTEXT
            ),
            after
        );
    }

    @Test
    public void testPatchCellsWithFormatter() {
        final Optional<SpreadsheetFormatterSelector> beforeFormatter = Optional.of(
            SpreadsheetPattern.parseTextFormatPattern("@\"before\"")
                .spreadsheetFormatterSelector()
        );

        final SpreadsheetCell a1 = SpreadsheetSelection.A1
            .setFormula(SpreadsheetFormula.EMPTY)
            .setFormatter(beforeFormatter);
        final SpreadsheetCell a2 = SpreadsheetSelection.parseCell("A2")
            .setFormula(SpreadsheetFormula.EMPTY)
            .setFormatter(beforeFormatter);

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
            .setCells(
                Sets.of(a1, a2)
            );

        final SpreadsheetFormatterSelector patchedFormatter = SpreadsheetPattern.parseTextFormatPattern("@\"patched\"")
            .spreadsheetFormatterSelector();

        final SpreadsheetDelta after = before.setCells(
            Sets.of(
                a1.setFormatter(Optional.of(patchedFormatter)),
                a2.setFormatter(Optional.of(patchedFormatter))
            )
        );

        this.patchCellsAndCheck(
            before,
            SpreadsheetSelection.parseCellRange("A1:A2"),
            SpreadsheetDelta.formatterPatch(
                Optional.of(patchedFormatter),
                MARSHALL_CONTEXT
            ),
            after
        );
    }

    @Test
    public void testPatchCellsWithFormatterEmptyClears() {
        final SpreadsheetCell a1 = SpreadsheetSelection.A1
            .setFormula(SpreadsheetFormula.EMPTY);
        final SpreadsheetCell a2 = SpreadsheetSelection.parseCell("A2")
            .setFormula(SpreadsheetFormula.EMPTY);

        final Optional<SpreadsheetFormatterSelector> formatter = Optional.of(
            SpreadsheetPattern.parseTextFormatPattern("@\"before\"")
                .spreadsheetFormatterSelector()
        );

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
            .setCells(
                Sets.of(
                    a1.setFormatter(formatter),
                    a2.setFormatter(formatter)
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
            SpreadsheetDelta.formatterPatch(
                Optional.empty(),
                MARSHALL_CONTEXT
            ),
            after
        );
    }

    @Test
    public void testPatchCellsWithFormatterEmptyClears2() {
        final SpreadsheetCell a1 = SpreadsheetSelection.A1
            .setFormula(SpreadsheetFormula.EMPTY);

        final Optional<SpreadsheetFormatterSelector> beforeFormatter = Optional.of(
            SpreadsheetPattern.parseTextFormatPattern("@\"before\"")
                .spreadsheetFormatterSelector()
        );

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
            .setCells(
                Sets.of(
                    a1.setFormatter(beforeFormatter)
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
            SpreadsheetDelta.formatterPatch(
                Optional.empty(),
                MARSHALL_CONTEXT
            ),
            after
        );
    }

    @Test
    public void testPatchCellsWithFormatterAndWindow() {
        final Optional<SpreadsheetFormatterSelector> beforeFormatter = Optional.of(
            SpreadsheetPattern.parseTextFormatPattern("@\"before\"")
                .spreadsheetFormatterSelector()
        );

        final SpreadsheetCell a1 = SpreadsheetSelection.A1
            .setFormula(SpreadsheetFormula.EMPTY)
            .setFormatter(beforeFormatter);
        final SpreadsheetCell a2 = SpreadsheetSelection.parseCell("A2")
            .setFormula(SpreadsheetFormula.EMPTY)
            .setFormatter(beforeFormatter);

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
            .setCells(
                Sets.of(a1, a2)
            ).setWindow(
                SpreadsheetViewportWindows.parse("A1:A2")
            );

        final SpreadsheetFormatterSelector patchedFormatter = SpreadsheetPattern.parseTextFormatPattern("@\"patched\"")
            .spreadsheetFormatterSelector();

        final SpreadsheetDelta after = before.setCells(
            Sets.of(
                a1.setFormatter(Optional.of(patchedFormatter)),
                a2.setFormatter(Optional.of(patchedFormatter))
            )
        );

        this.patchCellsAndCheck(
            before,
            SpreadsheetSelection.parseCellRange("A1:A2"),
            SpreadsheetDelta.formatterPatch(
                Optional.of(patchedFormatter),
                MARSHALL_CONTEXT
            ),
            after
        );
    }

    @Test
    public void testPatchCellsWithLocale() {
        final SpreadsheetCell a1 = SpreadsheetSelection.A1
            .setFormula(SpreadsheetFormula.EMPTY.setText("=1"));

        final SpreadsheetCell a2 = SpreadsheetSelection.parseCell("A2")
            .setFormula(SpreadsheetFormula.EMPTY.setText("=2"));

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
            .setCells(
                Sets.of(
                    a1.setLocale(
                        Optional.of(Locale.FRENCH)
                    ),
                    a2
                )
            );

        final Optional<Locale> locale = Optional.of(Locale.ENGLISH);

        final SpreadsheetDelta after = before.setCells(
            Sets.of(
                a1.setLocale(locale),
                a2.setLocale(locale),
                SpreadsheetSelection.parseCell("A3")
                    .setFormula(SpreadsheetFormula.EMPTY)
                    .setLocale(locale)
            )
        );

        this.patchCellsAndCheck(
            before,
            SpreadsheetSelection.parseCellRange("A1:A3"),
            SpreadsheetDelta.localePatch(
                locale,
                MARSHALL_CONTEXT
            ),
            after
        );
    }

    @Test
    public void testPatchCellsWithParser() {
        final Optional<SpreadsheetParserSelector> beforeParser = Optional.of(
            SpreadsheetPattern.parseNumberParsePattern("\"before\"")
                .spreadsheetParserSelector()
        );

        final SpreadsheetCell a1 = SpreadsheetSelection.A1
            .setFormula(SpreadsheetFormula.EMPTY)
            .setParser(beforeParser);
        final SpreadsheetCell a2 = SpreadsheetSelection.parseCell("A2")
            .setFormula(SpreadsheetFormula.EMPTY)
            .setParser(beforeParser);

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
            .setCells(
                Sets.of(a1, a2)
            );

        final SpreadsheetParserSelector parser = SpreadsheetPattern.parseNumberParsePattern("\"patched\"")
            .spreadsheetParserSelector();

        final SpreadsheetDelta after = before.setCells(
            Sets.of(
                a1.setParser(Optional.of(parser)),
                a2.setParser(Optional.of(parser))
            )
        );

        this.patchCellsAndCheck(
            before,
            SpreadsheetSelection.parseCellRange("A1:A2"),
            SpreadsheetDelta.parserPatch(
                Optional.of(parser),
                MARSHALL_CONTEXT
            ),
            after
        );
    }

    @Test
    public void testPatchCellsWithParserEmptyClears() {
        final SpreadsheetCell a1 = SpreadsheetSelection.A1
            .setFormula(SpreadsheetFormula.EMPTY);
        final SpreadsheetCell a2 = SpreadsheetSelection.parseCell("A2")
            .setFormula(SpreadsheetFormula.EMPTY);

        final Optional<SpreadsheetParserSelector> parser = Optional.of(
            SpreadsheetPattern.parseNumberParsePattern("#\"should be cleared\"")
                .spreadsheetParserSelector()
        );

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
            .setCells(
                Sets.of(
                    a1.setParser(parser),
                    a2.setParser(parser)
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
            SpreadsheetDelta.parserPatch(
                Optional.empty(),
                MARSHALL_CONTEXT
            ),
            after
        );
    }

    @Test
    public void testPatchCellsWithParserEmptylClearsAbsentCell() {
        final SpreadsheetCell a1 = SpreadsheetSelection.A1
            .setFormula(SpreadsheetFormula.EMPTY);

        final Optional<SpreadsheetParserSelector> parser = Optional.of(
            SpreadsheetPattern.parseNumberParsePattern("#\"should be cleared\"")
                .spreadsheetParserSelector()
        );

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
            .setCells(
                Sets.of(
                    a1.setParser(parser)
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
            SpreadsheetDelta.parserPatch(
                Optional.empty(),
                MARSHALL_CONTEXT
            ),
            after
        );
    }

    @Test
    public void testPatchCellsAndStyleFails() {
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
                ).objectOrFail()
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
            "Invalid patch includes: cell, style",
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

        final JsonNode stylePatch = TextStylePropertyName.COLOR.stylePatch(
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

        final JsonNode stylePatch = TextStylePropertyName.COLOR.stylePatch(
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
    public void testPatchCellsWithStyleNullValue() {
        final TextStylePropertyName<Color> propertyName = TextStylePropertyName.COLOR;
        final Color propertyValue = Color.parse("#123456");
        final TextStyle style = TextStyle.EMPTY.set(
            propertyName,
            propertyValue
        ).set(
            TextStylePropertyName.TEXT_ALIGN,
            TextAlign.CENTER
        );

        final SpreadsheetCell a1 = SpreadsheetSelection.A1
            .setFormula(SpreadsheetFormula.EMPTY.setText("=1"))
            .setStyle(style);
        final SpreadsheetCell a2 = SpreadsheetSelection.parseCell("A2")
            .setFormula(SpreadsheetFormula.EMPTY.setText("=2"))
            .setStyle(style);

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
            .setCells(
                Sets.of(a1, a2)
            ).setWindow(
                SpreadsheetViewportWindows.parse("A1:A2")
            );

        final JsonNode stylePatch = TextStylePropertyName.COLOR.stylePatch(null);
        final TextStyle patchedStyle = style.patch(
            stylePatch,
            this.createPatchContext()
        );

        final SpreadsheetDelta after = before.setCells(
            Sets.of(
                a1.setStyle(patchedStyle),
                a2.setStyle(patchedStyle)
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
    public void testPatchCellsMissingCellWithStyleNullValue() {
        final TextStylePropertyName<Color> propertyName = TextStylePropertyName.COLOR;
        final Color propertyValue = Color.parse("#123456");
        final TextStyle beforeStyle = TextStyle.EMPTY.set(
            propertyName,
            propertyValue
        ).set(
            TextStylePropertyName.TEXT_ALIGN,
            TextAlign.CENTER
        );

        final SpreadsheetCell a2 = SpreadsheetSelection.parseCell("A2")
            .setFormula(SpreadsheetFormula.EMPTY.setText("=2"))
            .setStyle(beforeStyle);

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
            .setCells(
                Sets.of(a2)
            ).setWindow(
                SpreadsheetViewportWindows.parse("A1:A2")
            );

        final JsonNode stylePatch = TextStylePropertyName.COLOR.stylePatch(null);

        // A1 will be created with no formula and only the stylePatch
        final JsonNodeUnmarshallContext patchContext = this.createPatchContext();
        final SpreadsheetDelta after = before.setCells(
            Sets.of(
                SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                    .setStyle(
                        TextStyle.EMPTY.patch(
                            stylePatch,
                            patchContext
                        )
                    ),
                a2.setStyle(
                    beforeStyle.patch(
                        stylePatch,
                        patchContext
                    )
                )
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
        final TextStyle beforeStyle1 = TextStyle.EMPTY
            .set(
                TextStylePropertyName.COLOR,
                Color.parse("#111111")
            );

        final SpreadsheetCell a1 = SpreadsheetSelection.A1
            .setFormula(SpreadsheetFormula.EMPTY)
            .setStyle(beforeStyle1);

        final TextStyle beforeStyle2 = TextStyle.EMPTY
            .set(
                TextStylePropertyName.COLOR,
                Color.parse("#222222")
            );

        final SpreadsheetCell a2 = SpreadsheetSelection.parseCell("A2")
            .setFormula(SpreadsheetFormula.EMPTY)
            .setStyle(beforeStyle2);

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
            .setCells(
                Sets.of(
                    a1,
                    a2
                )
            ).setWindow(
                SpreadsheetViewportWindows.parse("A1:A2")
            );

        final TextStyle patchStyle = TextStyle.EMPTY.set(
            TextStylePropertyName.TEXT_ALIGN,
            TextAlign.CENTER
        );

        final JsonNode stylePatch = TextStylePropertyName.TEXT_ALIGN.stylePatch(
            TextAlign.CENTER
        );

        final SpreadsheetDelta after = before.setCells(
            Sets.of(
                a1.setStyle(
                    beforeStyle1.merge(patchStyle)
                ),
                a2.setStyle(
                    beforeStyle2.merge(patchStyle)
                )
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

    @Test
    public void testPatchCellsWithValidator() {
        final SpreadsheetCell a1 = SpreadsheetSelection.A1
            .setFormula(SpreadsheetFormula.EMPTY)
            .setValidator(
                Optional.of(
                    ValidatorSelector.parse("before-validator")
                )
            );
        final SpreadsheetCell a2 = SpreadsheetSelection.parseCell("A2")
            .setFormula(SpreadsheetFormula.EMPTY)
            .setValidator(
                Optional.empty()
            );

        final SpreadsheetDelta before = SpreadsheetDelta.EMPTY
            .setCells(
                Sets.of(a1, a2)
            );

        final Optional<ValidatorSelector> validator1 = Optional.of(
            ValidatorSelector.parse("patched-validator1")
        );
        final Optional<ValidatorSelector> validator2 = Optional.of(
            ValidatorSelector.parse("patched-validator2")
        );

        final SpreadsheetDelta after = before.setCells(
            Sets.of(
                a1.setValidator(validator1),
                a2.setValidator(validator2)
            )
        );

        this.patchCellsAndCheck(
            before,
            SpreadsheetSelection.parseCellRange("A1:A2"),
            SpreadsheetDelta.cellsValidatorPatch(
                Maps.of(
                    SpreadsheetSelection.A1,
                    validator1,
                    SpreadsheetSelection.parseCell("A2"),
                    validator2
                ),
                MARSHALL_CONTEXT
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
            SpreadsheetDelta.FORMATTER_PROPERTY,
            JsonNode.nullNode()

        );
    }

    @Test
    public void testPatchColumnsParserFails() {
        this.patchColumnInvalidPropertyFails2(
            SpreadsheetDelta.PARSER_PROPERTY,
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

        assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetDelta.EMPTY.patchColumns(patch, this.createPatchContext())
        );
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
            SpreadsheetDelta.NO_VIEWPORT,
            SpreadsheetDelta.NO_VIEWPORT
        );
    }

    @Test
    public void testPatchColumnsViewport() {
        this.patchColumnViewportAndCheck(
            SpreadsheetDelta.NO_VIEWPORT,
            Optional.of(
                SpreadsheetSelection.A1.viewportRectangle(100, 40)
                    .viewport()
                    .setAnchoredSelection(
                        Optional.of(
                            SpreadsheetSelection.parseColumn("C")
                                .setDefaultAnchor()
                        )
                    )
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
                        SpreadsheetSelection.A1.viewportRectangle(100, 40)
                            .viewport()
                            .setAnchoredSelection(
                                Optional.of(
                                    SpreadsheetSelection.parseCellRange("A1:B2")
                                        .setAnchor(SpreadsheetViewportAnchor.BOTTOM_RIGHT)
                                )
                            )
                    )
                )
            ,
            JsonNode.object()
                .setNull(SpreadsheetDelta.VIEWPORT_SELECTION_PROPERTY),
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
                    SpreadsheetSelection.A1.viewportRectangle(100, 40)
                        .viewport()
                        .setAnchoredSelection(
                            Optional.of(
                                SpreadsheetSelection.parseCellRange("A1:B2")
                                    .setAnchor(SpreadsheetViewportAnchor.TOP_LEFT)
                            )
                        )
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
                .setNull(SpreadsheetDelta.COLUMNS_PROPERTY),
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

    // PatchRows........................................................................................................

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
            SpreadsheetDelta.FORMATTER_PROPERTY,
            JsonNode.nullNode()

        );
    }

    @Test
    public void testPatchRowsParserFails() {
        this.patchRowInvalidPropertyFails2(
            SpreadsheetDelta.PARSER_PROPERTY,
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

        assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetDelta.EMPTY.patchRows(
                patch,
                this.createPatchContext()
            )
        );
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
            SpreadsheetDelta.NO_VIEWPORT,
            SpreadsheetDelta.NO_VIEWPORT
        );
    }

    @Test
    public void testPatchRowsViewport() {
        this.patchRowViewportAndCheck(
            SpreadsheetDelta.NO_VIEWPORT,
            Optional.of(
                SpreadsheetSelection.A1.viewportRectangle(100, 40)
                    .viewport()
                    .setAnchoredSelection(
                        Optional.of(
                            SpreadsheetSelection.parseRow("3")
                                .setDefaultAnchor()
                        )
                    )
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
                        SpreadsheetSelection.A1.viewportRectangle(100, 40)
                            .viewport()
                            .setAnchoredSelection(
                                Optional.of(
                                    SpreadsheetSelection.parseCellRange("A1:B2")
                                        .setAnchor(SpreadsheetViewportAnchor.BOTTOM_RIGHT)
                                )
                            )
                    )
                )
            ,
            JsonNode.object()
                .setNull(SpreadsheetDelta.VIEWPORT_SELECTION_PROPERTY),
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
                    SpreadsheetSelection.A1.viewportRectangle(100, 40)
                        .viewport()
                        .setAnchoredSelection(
                            Optional.of(
                                SpreadsheetSelection.parseCellRange("A1:B2")
                                    .setAnchor(SpreadsheetViewportAnchor.TOP_LEFT)
                            )
                        )
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
                .setNull(SpreadsheetDelta.ROWS_PROPERTY),
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

    private final static JsonNodeUnmarshallContext UNMARSHALL_CONTEXT = JsonNodeUnmarshallContexts.basic(
        ExpressionNumberKind.BIG_DECIMAL,
        MathContext.DECIMAL32
    );

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

    // resolveCellLabels................................................................................................

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
                .setNull(SpreadsheetDelta.CELLS_PROPERTY),
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

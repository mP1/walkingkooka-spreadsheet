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
import walkingkooka.collect.set.Sets;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.logging.CanLogs;
import walkingkooka.logging.LoggingLevel;
import walkingkooka.spreadsheet.SpreadsheetContexts;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetColumnOrRowSpreadsheetComparatorNamesList;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContextTesting;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.meta.HasSpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStore;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStores;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReferencePath;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepositories;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.spreadsheet.validation.SpreadsheetValidationReference;
import walkingkooka.spreadsheet.validation.form.SpreadsheetForms;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewport;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewportRectangle;
import walkingkooka.terminal.TerminalContexts;
import walkingkooka.text.printer.Printers;
import walkingkooka.tree.expression.Expression;
import walkingkooka.validation.ValueType;
import walkingkooka.validation.form.Form;
import walkingkooka.validation.form.FormName;

import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetEngineLoggingTest implements SpreadsheetEngineTesting2<SpreadsheetEngineLogging>,
    HasSpreadsheetMetadataTesting,
    SpreadsheetEnvironmentContextTesting {

    private final static FormName FORM_NAME = FormName.with("Form1");

    @Test
    public void testWithNullEngineFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetEngineLogging.with(null)
        );
    }

    @Test
    public void testEvaluate() {
        this.logAndCheck(
            (final SpreadsheetEngine engine,
             final SpreadsheetEngineContext context) -> {
                engine.evaluate(
                    "1+2",
                    context
                );
            },
            "environment DEBUG environmentValue spreadsheetId change null to 123\n" +
                "engine INFO evaluate \"1+2\"\n" +
                "engine INFO evaluate \"1+2\" > 3\n"
        );
    }

    @Test
    public void testLoadCells() {
        this.logAndCheck(
            (final SpreadsheetEngine engine,
             final SpreadsheetEngineContext context) -> {
                engine.loadCells(
                    CELL_REFERENCE,
                    SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                    SpreadsheetDeltaProperties.ALL,
                    context
                );
            },
            "environment DEBUG environmentValue spreadsheetId change null to 123\n" +
                "engine INFO loadCells $A$2 COMPUTE_IF_NECESSARY [CELLS, COLUMNS, LABELS, REFERENCES, ROWS, DELETED_CELLS, DELETED_COLUMNS, DELETED_ROWS, DELETED_LABELS, MATCHED_CELLS, COLUMN_WIDTHS, ROW_HEIGHTS, COLUMN_COUNT, ROW_COUNT]\n" +
                "engine INFO loadCells $A$2 COMPUTE_IF_NECESSARY [CELLS, COLUMNS, LABELS, REFERENCES, ROWS, DELETED_CELLS, DELETED_COLUMNS, DELETED_ROWS, DELETED_LABELS, MATCHED_CELLS, COLUMN_WIDTHS, ROW_HEIGHTS, COLUMN_COUNT, ROW_COUNT] > deletedCells: A2 max: A=100.0, 2=50.0\n"
        );
    }

    @Test
    public void testLoadMultipleCells() {
        this.logAndCheck(
            (final SpreadsheetEngine engine,
             final SpreadsheetEngineContext context) -> {
                engine.loadMultipleCellRanges(
                    Sets.of(
                        CELL_REFERENCE.toRange()
                    ),
                    SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                    SpreadsheetDeltaProperties.ALL,
                    context
                );
            },
            "environment DEBUG environmentValue spreadsheetId change null to 123\n" +
                "engine INFO loadMultipleCellRanges: [$A$2] COMPUTE_IF_NECESSARY [CELLS, COLUMNS, LABELS, REFERENCES, ROWS, DELETED_CELLS, DELETED_COLUMNS, DELETED_ROWS, DELETED_LABELS, MATCHED_CELLS, COLUMN_WIDTHS, ROW_HEIGHTS, COLUMN_COUNT, ROW_COUNT]\n" +
                "engine INFO loadMultipleCellRanges: [$A$2]  COMPUTE_IF_NECESSARY [CELLS, COLUMNS, LABELS, REFERENCES, ROWS, DELETED_CELLS, DELETED_COLUMNS, DELETED_ROWS, DELETED_LABELS, MATCHED_CELLS, COLUMN_WIDTHS, ROW_HEIGHTS, COLUMN_COUNT, ROW_COUNT] > deletedCells: A2 max: A=100.0, 2=50.0 window: $A$2\n"
        );
    }

    @Test
    public void testSaveCell() {
        this.logAndCheck(
            (final SpreadsheetEngine engine,
             final SpreadsheetEngineContext context) -> {
                engine.saveCell(
                    CELL_REFERENCE.setFormula(
                        SpreadsheetFormula.EMPTY.setText("=1+2")
                    ),
                    context
                );
            },
            "environment DEBUG environmentValue spreadsheetId change null to 123\n" +
                "engine INFO saveCell: A2 \"=1+2\"\n" +
                "environment DEBUG environmentValue spreadsheetCell change null to A2 \"=1+2\" value=3\n" +
                "engine INFO saveCell: A2 \"=1+2\" > cells: A2 \"=1+2\" value: 3 formattedValue: \"3.\" max: A=100.0, 2=50.0 columnCount=1 rowCount=2\n"
        );
    }

    @Test
    public void testSaveCells() {
        this.logAndCheck(
            (final SpreadsheetEngine engine,
             final SpreadsheetEngineContext context) -> {
                engine.saveCells(
                    Sets.of(
                        CELL_REFERENCE.setFormula(
                            SpreadsheetFormula.EMPTY.setText("=1+2")
                        )
                    ),
                    context
                );
            },
            "environment DEBUG environmentValue spreadsheetId change null to 123\n" +
                "engine INFO saveCells [A2 \"=1+2\"]\n" +
                "environment DEBUG environmentValue spreadsheetCell change null to A2 \"=1+2\" value=3\n" +
                "engine INFO saveCells [A2 \"=1+2\"] > cells: A2 \"=1+2\" value: 3 formattedValue: \"3.\" max: A=100.0, 2=50.0 columnCount=1 rowCount=2\n"
        );
    }

    @Test
    public void testDeleteCells() {
        this.logAndCheck(
            (final SpreadsheetEngine engine,
             final SpreadsheetEngineContext context) -> {
                engine.deleteCells(
                    CELL_REFERENCE,
                    context
                );
            },
            "environment DEBUG environmentValue spreadsheetId change null to 123\n" +
                "engine INFO deleteCells $A$2\n" +
                "engine INFO deleteCells $A$2 > \n"
        );
    }

    @Test
    public void testFillCells() {
        this.logAndCheck(
            (final SpreadsheetEngine engine,
             final SpreadsheetEngineContext context) -> {
                engine.fillCells(
                    Sets.of(
                        CELL_REFERENCE.setFormula(
                            SpreadsheetFormula.EMPTY
                        )
                    ),
                    SpreadsheetSelection.parseCellRange("$A$2:B2"),
                    SpreadsheetSelection.parseCellRange("B2:C3"),
                    context
                );
            },
            "environment DEBUG environmentValue spreadsheetId change null to 123\n" +
                "engine INFO fillCells [A2] from: $A$2:B2 to: B2:C3\n" +
                "environment DEBUG environmentValue spreadsheetCell change null to B2\n" +
                "environment DEBUG environmentValue spreadsheetCell change B2 to B3\n" +
                "environment DEBUG environmentValue spreadsheetCell change B3 to B2 formattedValue=\"\"\n" +
                "environment DEBUG environmentValue spreadsheetCell change B2 formattedValue=\"\" to B3 formattedValue=\"\"\n" +
                "environment DEBUG environmentValue spreadsheetCell change B3 formattedValue=\"\" to B2 formattedValue=\"\"\n" +
                "environment DEBUG environmentValue spreadsheetCell change B2 formattedValue=\"\" to B3 formattedValue=\"\"\n" +
                "engine INFO fillCells [A2] from: $A$2:B2 to: B2:C3 > cells: B2 formattedValue: \"\", B3 formattedValue: \"\" max: B=100.0, 2=50.0, 3=50.0 columnCount=2 rowCount=3\n"
        );
    }

    @Test
    public void testFilterCells() {
        this.logAndCheck(
            (final SpreadsheetEngine engine,
             final SpreadsheetEngineContext context) -> {
                engine.filterCells(
                    Sets.of(
                        CELL_REFERENCE.setFormula(
                            SpreadsheetFormula.EMPTY
                        )
                    ),
                    ValueType.NUMBER,
                    Expression.value(true),
                    context
                );
            },
            "environment DEBUG environmentValue spreadsheetId change null to 123\n" +
                "engine INFO filterCells [A2] number/Number true\n" +
                "engine INFO filterCells [A2] number/Number true > []\n"
        );
    }

    @Test
    public void testFindCellsWithReference() {
        this.logAndCheck(
            (final SpreadsheetEngine engine,
             final SpreadsheetEngineContext context) -> {
                engine.findCellsWithReference(
                    CELL_REFERENCE,
                    0,
                    1,
                    context
                );
            },
            "environment DEBUG environmentValue spreadsheetId change null to 123\n" +
                "engine INFO findCellsWithReference $A$2 offset: 0 count: 1\n" +
                "engine INFO findCellsWithReference $A$2 offset: 0 count: 1 > \n"
        );
    }

    @Test
    public void testFindFormulaReferences() {
        this.logAndCheck(
            (final SpreadsheetEngine engine,
             final SpreadsheetEngineContext context) -> {
                engine.findFormulaReferences(
                    CELL_REFERENCE,
                    0,
                    1,
                    SpreadsheetDeltaProperties.ALL,
                    context
                );
            },
            "environment DEBUG environmentValue spreadsheetId change null to 123\n" +
                "engine INFO findFormulaReferences $A$2 offset: 0 count: 1 [CELLS, COLUMNS, LABELS, REFERENCES, ROWS, DELETED_CELLS, DELETED_COLUMNS, DELETED_ROWS, DELETED_LABELS, MATCHED_CELLS, COLUMN_WIDTHS, ROW_HEIGHTS, COLUMN_COUNT, ROW_COUNT]\n" +
                "engine INFO findFormulaReferences $A$2 offset: 0 count: 1 [CELLS, COLUMNS, LABELS, REFERENCES, ROWS, DELETED_CELLS, DELETED_COLUMNS, DELETED_ROWS, DELETED_LABELS, MATCHED_CELLS, COLUMN_WIDTHS, ROW_HEIGHTS, COLUMN_COUNT, ROW_COUNT] > \n"
        );
    }

    @Test
    public void testQueryCells() {
        this.logAndCheck(
            (final SpreadsheetEngine engine,
             final SpreadsheetEngineContext context) -> {
                engine.queryCells(
                    SpreadsheetSelection.parseCellRange("$A$2:B2"),
                    SpreadsheetCellRangeReferencePath.BULR,
                    1,
                    20,
                    ValueType.NUMBER,
                    Expression.value(true),
                    SpreadsheetDeltaProperties.ALL,
                    context
                );
            },
            "environment DEBUG environmentValue spreadsheetId change null to 123\n" +
                "engine INFO queryCells $A$2:B2 offset: 1 count: 20 number/Number true [CELLS, COLUMNS, LABELS, REFERENCES, ROWS, DELETED_CELLS, DELETED_COLUMNS, DELETED_ROWS, DELETED_LABELS, MATCHED_CELLS, COLUMN_WIDTHS, ROW_HEIGHTS, COLUMN_COUNT, ROW_COUNT]\n" +
                "engine INFO queryCells $A$2:B2 offset: 1 count: 20 number/Number true [CELLS, COLUMNS, LABELS, REFERENCES, ROWS, DELETED_CELLS, DELETED_COLUMNS, DELETED_ROWS, DELETED_LABELS, MATCHED_CELLS, COLUMN_WIDTHS, ROW_HEIGHTS, COLUMN_COUNT, ROW_COUNT] > \n"
        );
    }

    @Test
    public void testSortCells() {
        this.logAndCheck(
            (final SpreadsheetEngine engine,
             final SpreadsheetEngineContext context) -> {
                engine.sortCells(
                    SpreadsheetSelection.parseCellRange("$A$2:B2"),
                    SpreadsheetColumnOrRowSpreadsheetComparatorNamesList.parse("A=day-of-month"),
                    SpreadsheetDeltaProperties.ALL,
                    context
                );
            },
            "environment DEBUG environmentValue spreadsheetId change null to 123\n" +
                "engine INFO sortCells $A$2:B2 comparatorNames: [A=day-of-month] [CELLS, COLUMNS, LABELS, REFERENCES, ROWS, DELETED_CELLS, DELETED_COLUMNS, DELETED_ROWS, DELETED_LABELS, MATCHED_CELLS, COLUMN_WIDTHS, ROW_HEIGHTS, COLUMN_COUNT, ROW_COUNT]\n" +
                "engine INFO sortCells $A$2:B2 comparatorNames: [A=day-of-month] [CELLS, COLUMNS, LABELS, REFERENCES, ROWS, DELETED_CELLS, DELETED_COLUMNS, DELETED_ROWS, DELETED_LABELS, MATCHED_CELLS, COLUMN_WIDTHS, ROW_HEIGHTS, COLUMN_COUNT, ROW_COUNT] > \n"
        );
    }

    @Test
    public void testLoadColumn() {
        this.logAndCheck(
            (final SpreadsheetEngine engine,
             final SpreadsheetEngineContext context) -> {
                engine.loadColumn(
                    CELL_REFERENCE.column(),
                    context
                );
            },
            "environment DEBUG environmentValue spreadsheetId change null to 123\n" +
                "engine INFO loadColumn $A\n" +
                "engine INFO loadColumn $A > \n"
        );
    }

    @Test
    public void testSaveColumn() {
        this.logAndCheck(
            (final SpreadsheetEngine engine,
             final SpreadsheetEngineContext context) -> {
                engine.saveColumn(
                    CELL_REFERENCE.column()
                        .column(),
                    context
                );
            },
            "environment DEBUG environmentValue spreadsheetId change null to 123\n" +
                "engine INFO saveColumn A\n" +
                "engine INFO saveColumn A > columns: A\n"
        );
    }

    @Test
    public void testDeleteColumns() {
        this.logAndCheck(
            (final SpreadsheetEngine engine,
             final SpreadsheetEngineContext context) -> {
                engine.deleteColumns(
                    CELL_REFERENCE.column(),
                    2,
                    context
                );
            },
            "environment DEBUG environmentValue spreadsheetId change null to 123\n" +
                "engine INFO deleteColumns $A count: 2\n" +
                "engine INFO deleteColumns $A count: 2 > \n"
        );
    }

    @Test
    public void testLoadRow() {
        this.logAndCheck(
            (final SpreadsheetEngine engine,
             final SpreadsheetEngineContext context) -> {
                engine.loadRow(
                    CELL_REFERENCE.row(),
                    context
                );
            },
            "environment DEBUG environmentValue spreadsheetId change null to 123\n" +
                "engine INFO loadRow $2\n" +
                "engine INFO loadRow $2 > \n"
        );
    }

    @Test
    public void testSaveRow() {
        this.logAndCheck(
            (final SpreadsheetEngine engine,
             final SpreadsheetEngineContext context) -> {
                engine.saveRow(
                    CELL_REFERENCE.row()
                        .row(),
                    context
                );
            },
            "environment DEBUG environmentValue spreadsheetId change null to 123\n" +
                "engine INFO saveRow 2\n" +
                "engine INFO saveRow 2 > rows: 2\n"
        );
    }

    @Test
    public void testDeleteRows() {
        this.logAndCheck(
            (final SpreadsheetEngine engine,
             final SpreadsheetEngineContext context) -> {
                engine.deleteRows(
                    CELL_REFERENCE.row(),
                    2,
                    context
                );
            },
            "environment DEBUG environmentValue spreadsheetId change null to 123\n" +
                "engine INFO deleteRows $2 count: 2\n" +
                "engine INFO deleteRows $2 count: 2 > \n"
        );
    }

    @Test
    public void testInsertColumns() {
        this.logAndCheck(
            (final SpreadsheetEngine engine,
             final SpreadsheetEngineContext context) -> {
                engine.insertColumns(
                    CELL_REFERENCE.column(),
                    2,
                    context
                );
            },
            "environment DEBUG environmentValue spreadsheetId change null to 123\n" +
                "engine INFO insertColumns $A count: 2\n" +
                "engine INFO insertColumns $A count: 2 > \n"
        );
    }

    @Test
    public void testInsertRows() {
        this.logAndCheck(
            (final SpreadsheetEngine engine,
             final SpreadsheetEngineContext context) -> {
                engine.insertRows(
                    CELL_REFERENCE.row(),
                    2,
                    context
                );
            },
            "environment DEBUG environmentValue spreadsheetId change null to 123\n" +
                "engine INFO insertRows $2 count: 2\n" +
                "engine INFO insertRows $2 count: 2 > \n"
        );
    }

    @Test
    public void testLoadForm() {
        this.logAndCheck(
            (final SpreadsheetEngine engine,
             final SpreadsheetEngineContext context) -> {
                engine.loadForm(
                    FORM_NAME,
                    context
                );
            },
            "environment DEBUG environmentValue spreadsheetId change null to 123\n" +
                "engine INFO loadForm Form1\n" +
                "engine INFO loadForm Form1 > \n"
        );
    }

    @Test
    public void testSaveForm() {
        this.logAndCheck(
            (final SpreadsheetEngine engine,
             final SpreadsheetEngineContext context) -> {
                engine.saveForm(
                    SpreadsheetForms.form(
                        FORM_NAME
                    ).setFields(
                        Lists.of(
                            SpreadsheetForms.field(
                                CELL_REFERENCE
                            )
                        )
                    ),
                    context
                );
            },
            "environment DEBUG environmentValue spreadsheetId change null to 123\n" +
                "engine INFO saveForm Form1 fields=$A$2\n" +
                "engine INFO saveForm Form1 fields=$A$2 > forms: Form1 fields=$A$2 deletedCells: A2 max: A=100.0, 2=50.0\n"
        );
    }

    @Test
    public void testDeleteForm() {
        this.logAndCheck(
            (final SpreadsheetEngine engine,
             final SpreadsheetEngineContext context) -> {
                engine.deleteForm(
                    FORM_NAME,
                    context
                );
            },
            "environment DEBUG environmentValue spreadsheetId change null to 123\n" +
                "engine INFO deleteForm Form1\n" +
                "engine INFO deleteForm Form1 > \n"
        );
    }

    @Test
    public void testPrepareForm() {
        this.logAndCheck(
            (final SpreadsheetEngine engine,
             final SpreadsheetEngineContext context) -> {
                engine.saveForm(
                    SpreadsheetForms.form(
                        FORM_NAME
                    ).setFields(
                        Lists.of(
                            SpreadsheetForms.field(
                                CELL_REFERENCE
                            )
                        )
                    ),
                    context
                );

                engine.prepareForm(
                    FORM_NAME,
                    CELL_REFERENCE,
                    context
                );
            },
            "environment DEBUG environmentValue spreadsheetId change null to 123\n" +
                "engine INFO saveForm Form1 fields=$A$2\n" +
                "engine INFO saveForm Form1 fields=$A$2 > forms: Form1 fields=$A$2 deletedCells: A2 max: A=100.0, 2=50.0\n" +
                "engine INFO prepareForm Form1 $A$2\n" +
                "engine INFO prepareForm Form1 $A$2 > forms: Form1 fields=$A$2\n"
        );
    }

    @Test
    public void testFindFormsByName() {
        this.logAndCheck(
            (final SpreadsheetEngine engine,
             final SpreadsheetEngineContext context) -> {
                engine.findFormsByName(
                    "Form1",
                    0,
                    1,
                    context
                );
            },
            "environment DEBUG environmentValue spreadsheetId change null to 123\n" +
                "engine INFO findFormsByName \"Form1\" offset: 0 count: 1\n" +
                "engine INFO findFormsByName \"Form1\" offset: 0 count: 1 > \n"
        );
    }

    @Test
    public void testSubmitForm() {
        this.logAndCheck(
            (final SpreadsheetEngine engine,
             final SpreadsheetEngineContext context) -> {
                final Form<SpreadsheetValidationReference> form = SpreadsheetForms.form(
                    FORM_NAME
                );

                engine.saveForm(
                    form,
                    context
                );
                engine.submitForm(
                    form,
                    CELL_REFERENCE,
                    context
                );
            },
            "environment DEBUG environmentValue spreadsheetId change null to 123\n" +
                "engine INFO saveForm Form1\n" +
                "engine INFO saveForm Form1 > forms: Form1\n" +
                "engine INFO submitForm Form1 $A$2\n" +
                "engine INFO submitForm Form1 $A$2 > \n"
        );
    }

    @Test
    public void testLoadLabel() {
        this.logAndCheck(
            (final SpreadsheetEngine engine,
             final SpreadsheetEngineContext context) -> {
                engine.loadLabel(
                    LABEL,
                    context
                );
            },
            "environment DEBUG environmentValue spreadsheetId change null to 123\n" +
                "engine INFO loadLabel LABEL123\n" +
                "engine INFO loadLabel LABEL123 > \n"
        );
    }

    @Test
    public void testSaveLabel() {
        this.logAndCheck(
            (final SpreadsheetEngine engine,
             final SpreadsheetEngineContext context) -> {
                engine.saveLabel(
                    LABEL.setLabelMappingReference(CELL_REFERENCE),
                    context
                );
            },
            "environment DEBUG environmentValue spreadsheetId change null to 123\n" +
                "engine INFO saveLabel LABEL123=$A$2\n" +
                "engine INFO saveLabel LABEL123=$A$2 > labels: LABEL123=$A$2\n"
        );
    }

    @Test
    public void testDeleteLabel() {
        this.logAndCheck(
            (final SpreadsheetEngine engine,
             final SpreadsheetEngineContext context) -> {
                engine.deleteLabel(
                    LABEL,
                    context
                );
            },
            "environment DEBUG environmentValue spreadsheetId change null to 123\n" +
                "engine INFO deleteLabel LABEL123\n" +
                "engine INFO deleteLabel LABEL123 > \n"
        );
    }

    @Test
    public void testFindLabelByName() {
        this.logAndCheck(
            (final SpreadsheetEngine engine,
             final SpreadsheetEngineContext context) -> {
                engine.findLabelsByName(
                    "Label123",
                    0,
                    1,
                    context
                );
            },
            "environment DEBUG environmentValue spreadsheetId change null to 123\n" +
                "engine INFO findLabelsByName \"Label123\" offset: 0 count: 1\n" +
                "engine INFO findLabelsByName \"Label123\" offset: 0 count: 1 > \n"
        );
    }

    @Test
    public void testFindLabelsWithReference() {
        this.logAndCheck(
            (final SpreadsheetEngine engine,
             final SpreadsheetEngineContext context) -> {
                engine.findLabelsWithReference(
                    CELL_REFERENCE,
                    0,
                    1,
                    context
                );
            },
            "environment DEBUG environmentValue spreadsheetId change null to 123\n" +
                "engine INFO findLabelsWithReference $A$2 offset: 0 count: 1\n" +
                "engine INFO findLabelsWithReference $A$2 offset: 0 count: 1 > \n"
        );
    }

    @Test
    public void testColumnWidth() {
        this.logAndCheck(
            (final SpreadsheetEngine engine,
             final SpreadsheetEngineContext context) -> {
                engine.columnWidth(
                    CELL_REFERENCE.column(),
                    context
                );
            },
            "environment DEBUG environmentValue spreadsheetId change null to 123\n" +
                "engine INFO columnWidth $A\n" +
                "engine INFO columnWidth $A > 100.0\n"
        );
    }

    @Test
    public void testColumnCount() {
        this.logAndCheck(
            (final SpreadsheetEngine engine,
             final SpreadsheetEngineContext context) -> {
                engine.columnCount(
                    context
                );
            },
            "environment DEBUG environmentValue spreadsheetId change null to 123\n" +
                "engine INFO columnCount\n" +
                "engine INFO columnCount > 0\n"
        );
    }

    @Test
    public void testRowHeight() {
        this.logAndCheck(
            (final SpreadsheetEngine engine,
             final SpreadsheetEngineContext context) -> {
                engine.rowHeight(
                    CELL_REFERENCE.row(),
                    context
                );
            },
            "environment DEBUG environmentValue spreadsheetId change null to 123\n" +
                "engine INFO rowHeight $2\n" +
                "engine INFO rowHeight $2 > 50.0\n"
        );
    }

    @Test
    public void testRowCount() {
        this.logAndCheck(
            (final SpreadsheetEngine engine,
             final SpreadsheetEngineContext context) -> {
                engine.rowCount(
                    context
                );
            },
            "environment DEBUG environmentValue spreadsheetId change null to 123\n" +
                "engine INFO rowCount\n" +
                "engine INFO rowCount > 0\n"
        );
    }

    @Test
    public void testWindow() {
        this.logAndCheck(
            (final SpreadsheetEngine engine,
             final SpreadsheetEngineContext context) -> {
                engine.window(
                    SpreadsheetViewport.with(
                        SpreadsheetViewportRectangle.with(
                            CELL_REFERENCE,
                            100,
                            200
                        )
                    ),
                    context
                );
            },
            "environment DEBUG environmentValue spreadsheetId change null to 123\n" +
                "engine INFO viewport home: A2 width: 100.0 height: 200.0\n" +
                "engine INFO viewport home: A2 width: 100.0 height: 200.0 > A2:A5\n"
        );
    }

    @Test
    public void testNavigate() {
        this.logAndCheck(
            (final SpreadsheetEngine engine,
             final SpreadsheetEngineContext context) -> {
                engine.navigate(
                    SpreadsheetViewport.with(
                        SpreadsheetViewportRectangle.with(
                            CELL_REFERENCE,
                            100,
                            200
                        )
                    ),
                    context
                );
            },
            "environment DEBUG environmentValue spreadsheetId change null to 123\n" +
                "engine INFO navigate home: A2 width: 100.0 height: 200.0\n" +
                "engine INFO navigate home: A2 width: 100.0 height: 200.0 > Optional[home: A2 width: 100.0 height: 200.0]\n"
        );
    }

    private void logAndCheck(final BiConsumer<SpreadsheetEngine, SpreadsheetEngineContext> run,
                             final String expected) {
        final StringBuilder log = new StringBuilder();

        final SpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext(
            log,
            engine
        );

        run.accept(
            engine,
            context
        );

        this.checkEquals(
            expected,
            log.toString()
        );
    }

    @Override
    public SpreadsheetEngineLogging createSpreadsheetEngine() {
        return SpreadsheetEngineLogging.with(
            SpreadsheetEngines.basic()
        );
    }

    @Override
    public SpreadsheetEngineContext createContext() {
        return this.createContext(
            new StringBuilder(), // log
            this.createSpreadsheetEngine()
        );
    }

    private SpreadsheetEngineContext createContext(final StringBuilder log,
                                                   final SpreadsheetEngine engine) {
        final SpreadsheetMetadataStore metadataStore = SpreadsheetMetadataStores.treeMap();

        metadataStore.save(
            SPREADSHEET_METADATA.set(
                SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
                SPREADSHEET_ID
            )
        );

        final SpreadsheetStoreRepository repo = SpreadsheetStoreRepositories.treeMap(metadataStore);

        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment()
            .setEnvironmentContext(
                EnvironmentContexts.map(
                    CanLogs.printer(
                        Printers.stringBuilder(
                            log,
                            LINE_ENDING
                        )
                    ),
                    CHARSET,
                    CURRENCY,
                    INDENTATION,
                    LINE_ENDING,
                    LOCALE,
                    LoggingLevel.DEBUG,
                    HAS_NOW,
                    OPTIONAL_USER
                )
            );

        spreadsheetEnvironmentContext.setSpreadsheetId(OPTIONAL_SPREADSHEET_ID);

        return SpreadsheetEngineContexts.spreadsheetContext(
            SpreadsheetMetadataMode.FORMULA,
            SpreadsheetContexts.fixedSpreadsheetId(
                MEDIA_TYPE_DETECTOR,
                SPREADSHEET_METADATA_CREATOR,
                MULTIPLIER,
                engine,
                repo, // SpreadsheetStoreRepository
                (c) -> {
                    throw new UnsupportedOperationException();
                }, // httpRouterFactory
                CURRENCY_LOCALE_CONTEXT,
                spreadsheetEnvironmentContext,
                SPREADSHEET_PROVIDER,
                PROVIDER_CONTEXT
            ),
            TerminalContexts.fake()
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetEngineLogging> type() {
        return SpreadsheetEngineLogging.class;
    }
}

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
import walkingkooka.currency.CurrencyLocaleContextTesting;
import walkingkooka.datetime.DateTimeContextTesting;
import walkingkooka.math.DecimalNumberContextTesting;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.ThrowableTesting;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetColumnOrRowSpreadsheetComparatorNames;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReferencePath;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.spreadsheet.value.SpreadsheetColumn;
import walkingkooka.spreadsheet.value.SpreadsheetValueType;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewport;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewportRectangle;
import walkingkooka.text.BinaryTextContextTesting;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.HasExpressionNumberKindTesting;
import walkingkooka.validation.form.Form;
import walkingkooka.validation.form.FormName;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetEngineTesting2<E extends SpreadsheetEngine> extends SpreadsheetEngineTesting,
    ClassTesting2<E>,
    BinaryTextContextTesting,
    CurrencyLocaleContextTesting,
    DateTimeContextTesting,
    DecimalNumberContextTesting,
    HasExpressionNumberKindTesting,
    TreePrintableTesting,
    ThrowableTesting {

    // evaluate.........................................................................................................

    @Test
    default void testEvaluateWithNullExpressionFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .evaluate(
                    null,
                    this.createContext()
                )
        );
    }

    @Test
    default void testEvaluateWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .evaluate(
                    "",
                    null
                )
        );
    }

    default void evaluateAndCheck(final String expression,
                                  final Object expected) {
        this.evaluateAndCheck(
            this.createSpreadsheetEngine(),
            expression,
            this.createContext(),
            expected
        );
    }

    // cells............................................................................................................

    @Test
    default void testLoadCellsNullSelectionFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .loadCells(
                    null,
                    SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                    SpreadsheetDeltaProperties.ALL,
                    this.createContext()
                )
        );
    }

    @Test
    default void testLoadCellsNullEvaluationFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .loadCells(
                    CELL_REFERENCE,
                    null, // evaluation
                    SpreadsheetDeltaProperties.ALL,
                    this.createContext()
                )
        );
    }

    @Test
    default void testLoadCellsNullSpreadsheetDeltaPropertiesFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .loadCells(
                    CELL_REFERENCE,
                    SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                    null,
                    this.createContext()
                )
        );
    }

    @Test
    default void testLoadCellsNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .loadCells(
                    CELL_REFERENCE,
                    SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                    SpreadsheetDeltaProperties.ALL,
                    null
                )
        );
    }

    // loadMultipleCellRanges...........................................................................................

    @Test
    default void testLoadMultipleCellRangesWithNullCellRangesFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .loadMultipleCellRanges(
                    null,
                    SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                    SpreadsheetDeltaProperties.ALL,
                    SpreadsheetEngineContexts.fake()
                )
        );
    }

    @Test
    default void testLoadMultipleCellRangesWithNullSpreadsheetExpressionEvaluationFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .loadMultipleCellRanges(
                    Sets.of(SpreadsheetSelection.ALL_CELLS),
                    null,
                    SpreadsheetDeltaProperties.ALL,
                    SpreadsheetEngineContexts.fake()
                )
        );
    }

    @Test
    default void testLoadMultipleCellRangesWithNullDeltaPropertiesFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .loadMultipleCellRanges(
                    Sets.of(SpreadsheetSelection.ALL_CELLS),
                    SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                    null,
                    SpreadsheetEngineContexts.fake()
                )
        );
    }

    @Test
    default void testLoadMultipleCellRangesWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .loadMultipleCellRanges(
                    Sets.of(SpreadsheetSelection.ALL_CELLS),
                    SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                    SpreadsheetDeltaProperties.ALL,
                    null
                )
        );
    }

    // saveCell.........................................................................................................

    @Test
    default void testSaveCellNullCellFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .saveCell(
                    null,
                    this.createContext()
                )
        );
    }

    @Test
    default void testSaveCellNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .saveCell(
                    CELL_REFERENCE.setFormula(
                        SpreadsheetFormula.EMPTY.setText("1")
                    ),
                    null));
    }

    // deleteCells......................................................................................................

    @Test
    default void testDeleteCellsNullCellFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .deleteCells(
                    null,
                    this.createContext()
                )
        );
    }

    @Test
    default void testDeleteCellsNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine().deleteCells(
                CELL_REFERENCE,
                null
            )
        );
    }

    // fillCell.........................................................................................................

    @Test
    default void testFillCellsNullCellsFails() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.A1;
        final SpreadsheetCellRangeReference range = cell.cellRange(cell);

        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine().fillCells(null,
                range,
                range,
                this.createContext()
            )
        );
    }

    @Test
    default void testFillCellsNullFromFails() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.A1;
        final SpreadsheetCell spreadsheetCell = cell.setFormula(
            SpreadsheetFormula.EMPTY
                .setText("1")
        );
        final SpreadsheetCellRangeReference range = cell.cellRange(cell);

        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine().fillCells(Lists.of(spreadsheetCell),
                null,
                range,
                this.createContext()
            )
        );
    }

    @Test
    default void testFillCellsNullToFails() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.A1;
        final SpreadsheetCell spreadsheetCell = cell.setFormula(
            SpreadsheetFormula.EMPTY
                .setText("1")
        );
        final SpreadsheetCellRangeReference range = cell.cellRange(cell);

        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .fillCells(
                    Lists.of(spreadsheetCell),
                    range,
                    null,
                    this.createContext()
                )
        );
    }

    @Test
    default void testFillCellsNullContextFails() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.A1;
        final SpreadsheetCell spreadsheetCell = cell.setFormula(
            SpreadsheetFormula.EMPTY
                .setText("1")
        );
        final SpreadsheetCellRangeReference range = cell.cellRange(cell);

        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .fillCells(
                    Lists.of(spreadsheetCell),
                    range,
                    range,
                    null)
        );
    }

    @Test
    default void testFillCellsCellOutOfFromRangeFails() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseCell("B2");
        final SpreadsheetCell spreadsheetCell = cell.setFormula(
            SpreadsheetFormula.EMPTY
                .setText("1")
        );
        final SpreadsheetCellRangeReference range = SpreadsheetCellRangeReference.bounds(Lists.of(SpreadsheetSelection.parseCell("C3")));

        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> this.createSpreadsheetEngine()
                .fillCells(Lists.of(spreadsheetCell),
                    range,
                    range,
                    this.createContext()
                )
        );
        this.getMessageAndCheck(
            thrown,
            "Several cells B2 are outside the range C3"
        );
    }

    @Test
    default void testFillCellsCellOutOfFromRangeFails2() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseCell("B2");
        final SpreadsheetCell spreadsheetCell = cell.setFormula(
            SpreadsheetFormula.EMPTY
                .setText("1")
        );
        final SpreadsheetCellRangeReference range = SpreadsheetSelection.parseCellRange("C3:D4");

        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> this.createSpreadsheetEngine()
                .fillCells(Lists.of(spreadsheetCell),
                    range,
                    range,
                    this.createContext()
                )
        );

        this.getMessageAndCheck(
            thrown,
            "Several cells B2 are outside the range C3:D4"
        );
    }

    @Test
    default void testFillCellsOneCellsOutOfManyOutOfRange() {
        final SpreadsheetCell b2 = SpreadsheetSelection.parseCell("B2")
            .setFormula(
                SpreadsheetFormula.EMPTY
                    .setText("1")
            );
        final SpreadsheetCell c3 = SpreadsheetSelection.parseCell("C3")
            .setFormula(
                SpreadsheetFormula.EMPTY
                    .setText("2")
            );
        final SpreadsheetCell d4 = SpreadsheetSelection.parseCell("D4")
            .setFormula(
                SpreadsheetFormula.EMPTY
                    .setText("3")
            );

        final SpreadsheetCellRangeReference range = SpreadsheetSelection.parseCellRange("C3:D4");

        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> this.createSpreadsheetEngine()
                .fillCells(
                    Lists.of(b2, c3, d4),
                    range,
                    range,
                    this.createContext()
                )
        );

        this.getMessageAndCheck(
            thrown,
            "Several cells B2 are outside the range C3:D4"
        );
    }

    @Test
    default void testFillCellsSeveralCellsOutOfFromRangeFails() {
        final SpreadsheetCell b2 = SpreadsheetSelection.parseCell("B2")
            .setFormula(
                SpreadsheetFormula.EMPTY
                    .setText("1")
            );
        final SpreadsheetCell c3 = SpreadsheetSelection.parseCell("C3")
            .setFormula(
                SpreadsheetFormula.EMPTY
                    .setText("2")
            );
        final SpreadsheetCell d4 = SpreadsheetSelection.parseCell("D4")
            .setFormula(
                SpreadsheetFormula.EMPTY
                    .setText("3")
            );
        final SpreadsheetCell e5 = SpreadsheetSelection.parseCell("E5")
            .setFormula(
                SpreadsheetFormula.EMPTY
                    .setText("4")
            );

        final SpreadsheetCellRangeReference range = SpreadsheetSelection.parseCellRange("C3:D4");

        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> this.createSpreadsheetEngine()
                .fillCells(
                    Lists.of(b2, c3, d4, e5),
                    range,
                    range,
                    this.createContext()
                )
        );

        this.getMessageAndCheck(
            thrown,
            "Several cells B2, E5 are outside the range C3:D4"
        );
    }

    // filterCells......................................................................................................

    @Test
    default void testFilterCellsWithNullCellsFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .filterCells(
                    null, // cells
                    SpreadsheetValueType.ANY,
                    Expression.value(true),
                    SpreadsheetEngineContexts.fake()
                )
        );
    }

    @Test
    default void testFilterCellsWithNullValueTypeFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .filterCells(
                    Sets.empty(), // cells
                    null, // valueType
                    Expression.value(true),
                    SpreadsheetEngineContexts.fake()
                )
        );
    }

    @Test
    default void testFilterCellsWithNullExpressionFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .filterCells(
                    Sets.of(
                        SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                    ),
                    SpreadsheetValueType.ANY,
                    null, // expression
                    SpreadsheetEngineContexts.fake()
                )
        );
    }

    @Test
    default void testFilterCellsWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .filterCells(
                    Sets.of(
                        SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                    ),
                    SpreadsheetValueType.ANY,
                    Expression.value(true),
                    null // context
                )
        );
    }

    @Test
    default void testFilterCellsWithEmptyCells() {
        this.filterCellsAndCheck(
            this.createSpreadsheetEngine(),
            Sets.empty(),
            SpreadsheetValueType.ANY,
            Expression.value(true),
            this.createContext()
        );
    }

    @Test
    default void testFilterCellsWithExpressionFalse() {
        this.filterCellsAndCheck(
            this.createSpreadsheetEngine(),
            Sets.of(
                SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY),
                SpreadsheetSelection.parseCell("B2")
                    .setFormula(SpreadsheetFormula.EMPTY)
            ),
            SpreadsheetValueType.ANY,
            Expression.value(false),
            this.createContext()
        );
    }

    // findCellsWithReference..........................................................................................

    @Test
    default void testFindCellsWithReferenceWithNullReferenceFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .findCellsWithReference(
                    null,
                    0, // offset
                    0, // count,
                    SpreadsheetEngineContexts.fake()
                )
        );
    }

    @Test
    default void testFindCellsWithReferencesWithNegativeOffsetFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createSpreadsheetEngine()
                .findCellsWithReference(
                    SpreadsheetSelection.A1,
                    -1, // offset
                    0, // count,
                    SpreadsheetEngineContexts.fake()
                )
        );
    }

    @Test
    default void testFindCellsWithReferencesWithNegativeCountFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createSpreadsheetEngine()
                .findCellsWithReference(
                    SpreadsheetSelection.A1,
                    0, // offset
                    -1, // count,
                    SpreadsheetEngineContexts.fake()
                )
        );
    }

    @Test
    default void testFindCellsWithReferencesWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .findCellsWithReference(
                    SpreadsheetSelection.A1,
                    0, // offset
                    0, // count,
                    null
                )
        );
    }

    // findFormulaReferences............................................................................................

    @Test
    default void testFindFormulaReferencesWithNullCellRangeFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .findFormulaReferences(
                    null, // cell
                    0, // offset
                    1, // count
                    SpreadsheetDeltaProperties.ALL,
                    SpreadsheetEngineContexts.fake()
                )
        );
    }

    @Test
    default void testFindFormulaReferencesWithInvalidOffsetFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createSpreadsheetEngine()
                .findFormulaReferences(
                    SpreadsheetSelection.A1, // cell
                    -1, // offset
                    1, // count
                    SpreadsheetDeltaProperties.ALL,
                    SpreadsheetEngineContexts.fake()
                )
        );
    }

    @Test
    default void testFindFormulaReferencesWithInvalidCountFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createSpreadsheetEngine()
                .findFormulaReferences(
                    SpreadsheetSelection.A1, // cell
                    0, // offset
                    -1, // count
                    SpreadsheetDeltaProperties.ALL,
                    SpreadsheetEngineContexts.fake()
                )
        );
    }

    @Test
    default void testFindFormulaReferencesWithNullPropertiesFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .findFormulaReferences(
                    SpreadsheetSelection.A1, // cell
                    0, // offset
                    1, // count
                    null,
                    SpreadsheetEngineContexts.fake()
                )
        );
    }

    @Test
    default void testFindFormulaReferencesWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .findFormulaReferences(
                    SpreadsheetSelection.A1, // cell
                    0, // offset
                    1, // count
                    SpreadsheetDeltaProperties.ALL,
                    null
                )
        );
    }

    // queryCells.......................................................................................................

    @Test
    default void testQueryCellsWithNullCellsFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .queryCells(
                    null, // range
                    SpreadsheetCellRangeReferencePath.LRTD, // path
                    0, // offset
                    100, // count
                    SpreadsheetValueType.ANY,
                    Expression.value(true), // expression
                    SpreadsheetDeltaProperties.ALL,
                    SpreadsheetEngineContexts.fake() // context
                )
        );
    }

    @Test
    default void testQueryCellsWithNullPathFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .queryCells(
                    SpreadsheetSelection.ALL_CELLS, // range
                    null, // path
                    0, // offset
                    100, // count
                    SpreadsheetValueType.ANY,
                    Expression.value(true), // expression
                    SpreadsheetDeltaProperties.ALL,
                    SpreadsheetEngineContexts.fake() // context
                )
        );
    }

    @Test
    default void testQueryCellsWithInvalidOffsetFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createSpreadsheetEngine()
                .queryCells(
                    SpreadsheetSelection.ALL_CELLS, // range
                    SpreadsheetCellRangeReferencePath.LRTD, // path
                    -1, // offset
                    0,  // count
                    SpreadsheetValueType.ANY,
                    Expression.value(true), // expression
                    SpreadsheetDeltaProperties.ALL,
                    SpreadsheetEngineContexts.fake() // context
                )
        );
    }

    @Test
    default void testQueryCellsWithInvalidCountFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createSpreadsheetEngine()
                .queryCells(
                    SpreadsheetSelection.ALL_CELLS, // range
                    SpreadsheetCellRangeReferencePath.LRTD, // path
                    0, // offset
                    -1, // count
                    SpreadsheetValueType.ANY,
                    Expression.value(true), // expression
                    SpreadsheetDeltaProperties.ALL,
                    SpreadsheetEngineContexts.fake() // context
                )
        );
    }

    @Test
    default void testQueryCellsWithNullValueTypeFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .queryCells(
                    SpreadsheetSelection.ALL_CELLS, // range
                    SpreadsheetCellRangeReferencePath.LRTD, // path
                    0, // offset
                    100, // count
                    null, // valueType
                    Expression.value(true), // expression
                    SpreadsheetDeltaProperties.ALL,
                    SpreadsheetEngineContexts.fake() // context
                )
        );
    }

    @Test
    default void testQueryCellsWithNullExpressionFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .queryCells(
                    SpreadsheetSelection.ALL_CELLS, // range
                    SpreadsheetCellRangeReferencePath.LRTD, // path
                    0, // offset
                    100, // count
                    SpreadsheetValueType.ANY,
                    null, // expression
                    SpreadsheetDeltaProperties.ALL,
                    SpreadsheetEngineContexts.fake() // context
                )
        );
    }

    @Test
    default void testQueryCellsWithNullDeltaPropertiesFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .queryCells(
                    SpreadsheetSelection.ALL_CELLS, // range
                    SpreadsheetCellRangeReferencePath.LRTD, // path
                    0, // offset
                    100, // count
                    SpreadsheetValueType.ANY,
                    Expression.value(true), // expression
                    null,
                    SpreadsheetEngineContexts.fake() // context
                )
        );
    }

    @Test
    default void testQueryCellsWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .queryCells(
                    SpreadsheetSelection.ALL_CELLS, // range
                    SpreadsheetCellRangeReferencePath.LRTD, // path
                    0, // offset
                    100, // count
                    SpreadsheetValueType.ANY,
                    Expression.value(true), // expression
                    SpreadsheetDeltaProperties.ALL,
                    null // context
                )
        );
    }

    // sortCells........................................................................................................

    @Test
    default void testSortCellsWithNullCellRangeFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .sortCells(
                    null,
                    SpreadsheetColumnOrRowSpreadsheetComparatorNames.parseList("1=string"),
                    Sets.empty(), // deltaProperties
                    SpreadsheetEngineContexts.fake()
                )
        );
    }

    @Test
    default void testSortCellsWithNullComparatorsFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .sortCells(
                    SpreadsheetSelection.A1.toCellRange(),
                    null,
                    Sets.empty(), // deltaProperties
                    SpreadsheetEngineContexts.fake()
                )
        );
    }

    @Test
    default void testSortCellsWithNullDeltaPropertiesFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .sortCells(
                    SpreadsheetSelection.A1.toCellRange(),
                    SpreadsheetColumnOrRowSpreadsheetComparatorNames.parseList("1=string"),
                    null, // deltaProperties
                    SpreadsheetEngineContexts.fake()
                )
        );
    }

    @Test
    default void testSortCellsWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .sortCells(
                    SpreadsheetSelection.A1.toCellRange(),
                    SpreadsheetColumnOrRowSpreadsheetComparatorNames.parseList("1=string"),
                    Sets.empty(), // deltaProperties
                    null
                )
        );
    }

    // saveColumn.......................................................................................................

    @Test
    default void testSaveColumnNullColumnFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .saveColumn(
                    null,
                    this.createContext()
                )
        );
    }

    @Test
    default void testSaveColumnNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .saveColumn(
                    SpreadsheetColumn.with(COLUMN),
                    null
                )
        );
    }

    // deleteColumns....................................................................................................

    @Test
    default void testDeleteColumnsNullColumnFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .deleteColumns(
                    null,
                    1,
                    this.createContext()
                )
        );
    }

    @Test
    default void testDeleteColumnsNegativeCountFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createSpreadsheetEngine()
                .deleteColumns(
                    COLUMN,
                    -1,
                    this.createContext()
                )
        );
    }

    @Test
    default void testDeleteColumnsNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .deleteColumns(
                    COLUMN,
                    1,
                    null
                )
        );
    }

    // saveRow...........................................................................................................

    @Test
    default void testSaveRowNullRowFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .saveRow(
                    null,
                    this.createContext()
                )
        );
    }

    @Test
    default void testSaveRowNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .saveRow(
                    ROW.row(),
                    null
                )
        );
    }

    // deleteRows.......................................................................................................

    @Test
    default void testDeleteRowsNullRowFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .deleteRows(
                    null,
                    1,
                    this.createContext()
                )
        );
    }

    @Test
    default void testDeleteRowsNegativeCountFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createSpreadsheetEngine()
                .deleteRows(
                    ROW,
                    -1,
                    this.createContext()
                )
        );
    }

    @Test
    default void testDeleteRowsNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .deleteRows(
                    ROW,
                    1,
                    null
                )
        );
    }

    // insertColumns....................................................................................................

    @Test
    default void testInsertColumnsNullColumnFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .insertColumns(
                    null,
                    1,
                    this.createContext()
                )
        );
    }

    @Test
    default void testInsertColumnsNegativeCountFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createSpreadsheetEngine()
                .insertColumns(
                    COLUMN,
                    -1,
                    this.createContext()
                )
        );
    }

    @Test
    default void testInsertColumnsNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .insertColumns(
                    COLUMN,
                    1,
                    null
                )
        );
    }

    // insertRows.......................................................................................................

    @Test
    default void testInsertRowsNullRowFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .insertRows(
                    null,
                    1,
                    this.createContext()
                )
        );
    }

    @Test
    default void testInsertRowsNegativeCountFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createSpreadsheetEngine()
                .insertRows(
                    ROW,
                    -1,
                    this.createContext()
                )
        );
    }

    @Test
    default void testInsertRowsNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .insertRows(
                    ROW,
                    1,
                    null
                )
        );
    }

    // loadForm.........................................................................................................

    @Test
    default void testLoadFormWithNullFormNameFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .loadForm(
                    null,
                    this.createContext()
                )
        );
    }

    @Test
    default void testLoadFormWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .loadForm(
                    FormName.with("HelloForm123"),
                    null
                )
        );
    }

    // saveForm.........................................................................................................

    @Test
    default void testSaveFormWithNullFormFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .saveForm(
                    null,
                    this.createContext()
                )
        );
    }

    @Test
    default void testSaveFormWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .saveForm(
                    Form.with(
                        FormName.with("HelloForm123")
                    ),
                    null
                )
        );
    }

    // deleteForm.......................................................................................................

    @Test
    default void testDeleteFormWithNullFormFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .deleteForm(
                    null,
                    this.createContext()
                )
        );
    }

    @Test
    default void testDeleteFormWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .deleteForm(
                    FormName.with("HelloForm123"),
                    null
                )
        );
    }

    // loadForms........................................................................................................

    @Test
    default void testLoadFormsWithInvalidOffsetFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createSpreadsheetEngine()
                .loadForms(
                    -1, // offset
                    0, // count
                    SpreadsheetEngineContexts.fake()
                )
        );
    }

    @Test
    default void testLoadFormsWithInvalidCountFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createSpreadsheetEngine()
                .loadForms(
                    0, // offset
                    -1, // count
                    SpreadsheetEngineContexts.fake()
                )
        );
    }

    @Test
    default void testLoadFormsWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .loadForms(
                    0, // offset
                    1, // count
                    null
                )
        );
    }

    // prepareForm......................................................................................................

    @Test
    default void testPrepareFormWithNullFormNameFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .prepareForm(
                    null,
                    SpreadsheetSelection.A1,
                    this.createContext()
                )
        );
    }

    @Test
    default void testPrepareFormWithNullSelectionFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .prepareForm(
                    FormName.with("HelloForm"),
                    null,
                    this.createContext()
                )
        );
    }

    @Test
    default void testPrepareFormWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .prepareForm(
                    FormName.with("HelloForm123"),
                    SpreadsheetSelection.A1,
                    null
                )
        );
    }

    // submitForm.......................................................................................................

    @Test
    default void testSubmitFormWithNullFormNameFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .submitForm(
                    null,
                    SpreadsheetSelection.A1,
                    this.createContext()
                )
        );
    }

    @Test
    default void testSubmitFormWithNullSelectionFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .submitForm(
                    Form.with(
                        FormName.with("Form1")
                    ),
                    null,
                    this.createContext()
                )
        );
    }

    @Test
    default void testSubmitFormWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .submitForm(
                    Form.with(
                        FormName.with("Form1")
                    ),
                    SpreadsheetSelection.A1,
                    null
                )
        );
    }

    // findFormsByName..................................................................................................

    @Test
    default void testFindFormsByNameWithNullTextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .findFormsByName(
                    null,
                    0, // offset
                    0, // count,
                    SpreadsheetEngineContexts.fake()
                )
        );
    }

    @Test
    default void testFindFormsByNameWithNegativeOffsetFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createSpreadsheetEngine()
                .findFormsByName(
                    "",
                    -1, // offset
                    0, // count,
                    SpreadsheetEngineContexts.fake()
                )
        );
    }

    @Test
    default void testFindFormsByNameWithNegativeCountFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createSpreadsheetEngine()
                .findFormsByName(
                    "",
                    0, // offset
                    -1, // count,
                    SpreadsheetEngineContexts.fake()
                )
        );
    }

    @Test
    default void testFindFormsByNameWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .findFormsByName(
                    "",
                    0, // offset
                    0, // count,
                    null
                )
        );
    }

    // saveLabel........................................................................................................

    @Test
    default void testSaveLabelNullMappingFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .saveLabel(
                    null,
                    this.createContext()
                )
        );
    }

    @Test
    default void testSaveLabelNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .saveLabel(
                    SpreadsheetLabelMapping.with(
                        SpreadsheetSelection.labelName("LABEL123"),
                        SpreadsheetSelection.A1
                    ),
                    null
                )
        );
    }

    // deleteLabel......................................................................................................

    @Test
    default void testDeleteLabelNullMappingFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .deleteLabel(
                    null,
                    this.createContext()
                )
        );
    }

    @Test
    default void testDeleteLabelNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .deleteLabel(
                    SpreadsheetSelection.labelName("label"),
                    null
                )
        );
    }

    // loadLabel........................................................................................................

    @Test
    default void testLoadLabelWithNullNameFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .loadLabel(
                    null,
                    SpreadsheetEngineContexts.fake()
                )
        );
    }

    @Test
    default void testLoadLabelWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .loadLabel(
                    SpreadsheetSelection.labelName("Label123"),
                    null
                )
        );
    }

    // loadLabels.......................................................................................................

    @Test
    default void testLoadLabelsWithInvalidOffsetFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createSpreadsheetEngine()
                .loadLabels(
                    -1, // offset
                    0, // count
                    SpreadsheetEngineContexts.fake()
                )
        );
    }

    @Test
    default void testLoadLabelsWithInvalidCountFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createSpreadsheetEngine()
                .loadLabels(
                    0, // offset
                    -1, // count
                    SpreadsheetEngineContexts.fake()
                )
        );
    }

    @Test
    default void testLoadLabelsWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .loadLabels(
                    0, // offset
                    1, // count
                    null
                )
        );
    }

    // findLabelsByName................................................................................................

    @Test
    default void testFindLabelsByNameWithNullTextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .findLabelsByName(
                    null,
                    0, // offset
                    0, // count,
                    SpreadsheetEngineContexts.fake()
                )
        );
    }

    @Test
    default void testFindLabelsByNameWithNegativeOffsetFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createSpreadsheetEngine()
                .findLabelsByName(
                    "",
                    -1, // offset
                    0, // count,
                    SpreadsheetEngineContexts.fake()
                )
        );
    }

    @Test
    default void testFindLabelsByNameWithNegativeCountFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createSpreadsheetEngine()
                .findLabelsByName(
                    "",
                    0, // offset
                    -1, // count,
                    SpreadsheetEngineContexts.fake()
                )
        );
    }

    @Test
    default void testFindLabelsByNameWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .findLabelsByName(
                    "",
                    0, // offset
                    0, // count,
                    null
                )
        );
    }
    
    // findLabelsWithReference..........................................................................................

    @Test
    default void testFindLabelsWithReferenceWithNullReferenceFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .findLabelsWithReference(
                    null,
                    0, // offset
                    0, // count,
                    SpreadsheetEngineContexts.fake()
                )
        );
    }

    @Test
    default void testFindLabelsWithReferenceWithNegativeOffsetFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createSpreadsheetEngine()
                .findLabelsWithReference(
                    SpreadsheetSelection.A1,
                    -1, // offset
                    0, // count,
                    SpreadsheetEngineContexts.fake()
                )
        );
    }

    @Test
    default void testFindLabelsWithReferenceWithNegativeCountFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createSpreadsheetEngine()
                .findLabelsWithReference(
                    SpreadsheetSelection.A1,
                    0, // offset
                    -1, // count,
                    SpreadsheetEngineContexts.fake()
                )
        );
    }

    @Test
    default void testFindLabelsWithReferenceWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .findLabelsWithReference(
                    SpreadsheetSelection.A1,
                    0, // offset
                    0, // count,
                    null
                )
        );
    }

    // columnCount......................................................................................................

    @Test
    default void testColumnCountNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .columnCount(null)
        );
    }

    // columnWidth......................................................................................................

    @Test
    default void testColumnWidthWithNullColumnFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .columnWidth(
                    null,
                    SpreadsheetEngineContexts.fake()
                )
        );
    }

    @Test
    default void testColumnWidthWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .columnWidth(
                    SpreadsheetSelection.parseColumn("Z"),
                    null
                )
        );
    }

    default void columnWidthAndCheck(final SpreadsheetColumnReference column,
                                     final SpreadsheetEngineContext context,
                                     final double expected) {
        this.columnWidthAndCheck(
            this.createSpreadsheetEngine(),
            column,
            context,
            expected
        );
    }

    // rowCount......................................................................................................

    @Test
    default void testRowCountNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .rowCount(null)
        );
    }


    // rowHeight........................................................................................................

    @Test
    default void testRowHeightWithNullRowFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .rowHeight(
                    null,
                    SpreadsheetEngineContexts.fake()
                )
        );
    }

    @Test
    default void testRowHeightWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .rowHeight(
                    SpreadsheetSelection.parseRow("1"),
                    null
                )
        );
    }

    default void rowHeightAndCheck(final SpreadsheetRowReference row,
                                   final SpreadsheetEngineContext context,
                                   final double expected) {
        this.rowHeightAndCheck(
            this.createSpreadsheetEngine(),
            row,
            context,
            expected
        );
    }

    // navigate.........................................................................................................

    @Test
    default void testNavigateWithNullViewportFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .navigate(
                    null,
                    SpreadsheetEngineContexts.fake()
                )
        );
    }

    @Test
    default void testNavigateWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine().navigate(
                SpreadsheetViewport.with(
                    SpreadsheetViewportRectangle.with(
                        SpreadsheetSelection.A1,
                        1,
                        2
                    )
                ),
                null
            )
        );
    }

    // window...........................................................................................................

    @Test
    default void testWindowWithNullSpreadsheetViewportFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .window(
                    null,
                    SpreadsheetEngineContexts.fake()
                )
        );
    }

    @Test
    default void testWindowWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetEngine()
                .window(
                    SpreadsheetViewport.with(
                        SpreadsheetViewportRectangle.with(
                            SpreadsheetSelection.A1,
                            1, // width
                            2 // height
                        )
                    ),
                    null
                )
        );
    }

    E createSpreadsheetEngine();

    SpreadsheetEngineContext createContext();

    // class............................................................................................................

    @Override
    default JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}

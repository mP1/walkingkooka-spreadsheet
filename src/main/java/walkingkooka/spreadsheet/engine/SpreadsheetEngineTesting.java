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
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.ThrowableTesting;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.SpreadsheetValueType;
import walkingkooka.spreadsheet.SpreadsheetViewportRectangle;
import walkingkooka.spreadsheet.SpreadsheetViewportWindows;
import walkingkooka.spreadsheet.compare.SpreadsheetColumnOrRowSpreadsheetComparatorNames;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReferencePath;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.reference.SpreadsheetViewport;
import walkingkooka.spreadsheet.store.SpreadsheetLabelStore;
import walkingkooka.store.Store;
import walkingkooka.text.CharSequences;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContext;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContexts;
import walkingkooka.tree.expression.convert.ExpressionNumberConverters;
import walkingkooka.validation.form.DuplicateFormFieldReferencesException;
import walkingkooka.validation.form.Form;
import walkingkooka.validation.form.FormName;

import java.math.MathContext;
import java.text.DateFormatSymbols;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public interface SpreadsheetEngineTesting<E extends SpreadsheetEngine> extends ClassTesting2<E>,
        TreePrintableTesting,
        ThrowableTesting {

    SpreadsheetColumnReference COLUMN = SpreadsheetReferenceKind.ABSOLUTE.column(1);
    SpreadsheetRowReference ROW = SpreadsheetReferenceKind.ABSOLUTE.row(2);
    SpreadsheetCellReference CELL_REFERENCE = COLUMN.setRow(ROW);
    SpreadsheetLabelName LABEL = SpreadsheetSelection.labelName("LABEL123");

    // cells............................................................................................................

    @Test
    default void testLoadCellsNullSelectionFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSpreadsheetEngine()
                        .loadCells(
                                (SpreadsheetSelection) null,
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

    default SpreadsheetCell loadCellOrFail(final SpreadsheetEngine engine,
                                           final SpreadsheetCellReference cell,
                                           final SpreadsheetEngineEvaluation evaluation,
                                           final SpreadsheetEngineContext context) {
        final SpreadsheetDelta delta = engine.loadCells(
                cell,
                evaluation,
                SpreadsheetDeltaProperties.ALL,
                context
        );

        return delta.cells()
                .stream()
                .filter(c -> c.reference().equalsIgnoreReferenceKind(cell))
                .findFirst()
                .orElseGet(() -> {
                    Assertions.fail("Loading " + cell + " failed to return requested cell, cells: " + delta);
                    return null;
                });
    }

    default void loadCellFailsCheck(final SpreadsheetEngine engine,
                                    final SpreadsheetCellReference cell,
                                    final SpreadsheetEngineEvaluation evaluation,
                                    final SpreadsheetEngineContext context) {
        final SpreadsheetDelta loaded = engine.loadCells(
                cell,
                evaluation,
                SpreadsheetDeltaProperties.ALL,
                context
        );

        this.checkEquals(
                Optional.empty(),
                loaded.cells()
                        .stream()
                        .filter(c -> c.reference().equals(cell))
                        .findFirst(),
                "Expected reference " + cell + " to fail"
        );
    }

    @SuppressWarnings("UnusedReturnValue")
    default SpreadsheetCell loadCellAndWithoutValueOrErrorCheck(final SpreadsheetEngine engine,
                                                                final SpreadsheetCellReference cell,
                                                                final SpreadsheetEngineEvaluation evaluation,
                                                                final SpreadsheetEngineContext context) {
        final SpreadsheetCell spreadsheetCell = this.loadCellOrFail(
                engine,
                cell,
                evaluation,
                context
        );
        this.checkEquals(
                SpreadsheetFormula.NO_VALUE,
                spreadsheetCell.formula()
                        .errorOrValue(),
                () -> "values parse returned cells=" + spreadsheetCell
        );
        return spreadsheetCell;
    }

    default SpreadsheetCell loadCellAndFormulaTextCheck(final SpreadsheetEngine engine,
                                                        final SpreadsheetCellReference cell,
                                                        final SpreadsheetEngineEvaluation evaluation,
                                                        final SpreadsheetEngineContext context,
                                                        final String formulaText) {
        final SpreadsheetCell spreadsheetCell = this.loadCellOrFail(
                engine,
                cell,
                evaluation,
                context
        );
        this.cellFormulaTextAndCheck(
                spreadsheetCell,
                formulaText
        );
        return spreadsheetCell;
    }

    @SuppressWarnings("UnusedReturnValue")
    default SpreadsheetCell loadCellAndFormulaTextAndErrorOrValueCheck(final SpreadsheetEngine engine,
                                                                       final SpreadsheetCellReference cell,
                                                                       final SpreadsheetEngineEvaluation evaluation,
                                                                       final SpreadsheetEngineContext context,
                                                                       final String formulaText,
                                                                       final Object value) {
        final SpreadsheetCell spreadsheetCell = this.loadCellAndFormulaTextCheck(
                engine,
                cell,
                evaluation,
                context,
                formulaText
        );
        this.cellFormulaErrorOrValueAndCheck(
                spreadsheetCell,
                value
        );
        return spreadsheetCell;
    }

    default void loadCellAndErrorCheck(final SpreadsheetEngine engine,
                                       final SpreadsheetCellReference cell,
                                       final SpreadsheetEngineEvaluation evaluation,
                                       final SpreadsheetEngineContext context,
                                       final String errorContains) {
        final SpreadsheetCell spreadsheetCell = this.loadCellOrFail(
                engine,
                cell,
                evaluation,
                context
        );
        final SpreadsheetFormula formula = spreadsheetCell.formula();
        final Optional<SpreadsheetError> maybeError = formula.error();

        this.checkNotEquals(
                SpreadsheetFormula.NO_ERROR,
                maybeError,
                () -> "formula missing error=" + formula
        );

        final SpreadsheetError error = maybeError.get();
        final String errorText = error.message();
        assertTrue(
                errorText.contains(errorContains),
                () -> "Error message " +
                        CharSequences.quoteAndEscape(errorText) +
                        " missing " +
                        CharSequences.quoteAndEscape(errorContains)
        );
    }

    default SpreadsheetCell loadCellsAndCheck(final SpreadsheetEngine engine,
                                              final SpreadsheetCellReference cell,
                                              final SpreadsheetEngineEvaluation evaluation,
                                              final SpreadsheetEngineContext context,
                                              final Object value,
                                              final String formattedValueText) {
        return this.loadCellsAndCheck(
                engine,
                cell,
                evaluation,
                context,
                value,
                formattedValueText,
                null
        );
    }

    default SpreadsheetCell loadCellsAndCheck(final SpreadsheetEngine engine,
                                              final SpreadsheetCellReference cell,
                                              final SpreadsheetEngineEvaluation evaluation,
                                              final SpreadsheetEngineContext context,
                                              final Object value,
                                              final String formattedValueText,
                                              final String errorContains) {
        final SpreadsheetCell spreadsheetCell1 = this.loadCellOrFail(
                engine,
                cell,
                evaluation,
                context
        );
        this.cellFormulaErrorOrValueAndCheck(
                spreadsheetCell1,
                value
        );
        final SpreadsheetCell spreadsheetCell = spreadsheetCell1;

        this.cellFormattedValueAndCheck(spreadsheetCell, formattedValueText);

        if (null != errorContains) {
            final SpreadsheetFormula formula = spreadsheetCell.formula();
            final Optional<SpreadsheetError> maybeError = formula.error();

            this.checkNotEquals(
                    SpreadsheetFormula.NO_ERROR,
                    maybeError,
                    () -> "formula missing error=" + formula
            );

            final SpreadsheetError error = maybeError.get();
            final String errorText = error.message();
            assertTrue(
                    errorText.contains(errorContains),
                    () -> "Error message " +
                            CharSequences.quoteAndEscape(errorText) +
                            " missing " +
                            CharSequences.quoteAndEscape(errorContains)
            );
        }

        return spreadsheetCell;
    }

    default void loadCellsAndCheck(final SpreadsheetEngine engine,
                                   final SpreadsheetSelection cell,
                                   final SpreadsheetEngineEvaluation evaluation,
                                   final Set<SpreadsheetDeltaProperties> deltaProperties,
                                   final SpreadsheetEngineContext context,
                                   final SpreadsheetDelta loaded) {
        this.checkEquals(
                loaded,
                engine.loadCells(
                        cell,
                        evaluation,
                        deltaProperties,
                        context
                ),
                () -> "loadCell " + cell);
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

    default void loadMultipleCellRangesAndCheck(final SpreadsheetEngine engine,
                                                final String cellRanges,
                                                final SpreadsheetEngineEvaluation evaluation,
                                                final Set<SpreadsheetDeltaProperties> deltaProperties,
                                                final SpreadsheetEngineContext context,
                                                final SpreadsheetCell... updated) {
        this.loadMultipleCellRangesAndCheck(
                engine,
                SpreadsheetViewportWindows.parse(cellRanges)
                        .cellRanges(),
                evaluation,
                deltaProperties,
                context,
                updated
        );
    }

    default void loadMultipleCellRangesAndCheck(final SpreadsheetEngine engine,
                                                final Set<SpreadsheetCellRangeReference> cellRanges,
                                                final SpreadsheetEngineEvaluation evaluation,
                                                final Set<SpreadsheetDeltaProperties> deltaProperties,
                                                final SpreadsheetEngineContext context,
                                                final SpreadsheetCell... updated) {
        final SpreadsheetDelta result = engine.loadMultipleCellRanges(
                cellRanges,
                evaluation,
                deltaProperties,
                context
        );

        final SpreadsheetDelta expected = SpreadsheetDelta.EMPTY
                .setCells(
                        Sets.of(updated)
                ).setColumnCount(
                        OptionalInt.of(
                                engine.columnCount(context)
                        )
                )
                .setRowCount(
                        OptionalInt.of(
                                engine.rowCount(context)
                        )
                ).setWindow(
                        result.window()
                );
        this.checkEquals(
                expected,
                result,
                () -> "loadCells " + cellRanges + " " + evaluation
        );
    }

    default void loadMultipleCellRangesAndCheck(final SpreadsheetEngine engine,
                                                final Set<SpreadsheetCellRangeReference> cellRanges,
                                                final SpreadsheetEngineEvaluation evaluation,
                                                final Set<SpreadsheetDeltaProperties> deltaProperties,
                                                final SpreadsheetEngineContext context,
                                                final SpreadsheetDelta delta) {
        checkEquals(
                delta,
                engine.loadMultipleCellRanges(
                        cellRanges,
                        evaluation,
                        deltaProperties,
                        context
                ),
                () -> "loadCells " + cellRanges + " " + evaluation
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

    default void saveCellAndCheck(final SpreadsheetEngine engine,
                                  final SpreadsheetCell save,
                                  final SpreadsheetEngineContext context,
                                  final SpreadsheetDelta delta) {
        checkEquals(
                delta,
                engine.saveCell(save, context),
                () -> "saveCell " + save
        );
    }

    default void saveCellsAndCheck(final SpreadsheetEngine engine,
                                   final Set<SpreadsheetCell> save,
                                   final SpreadsheetEngineContext context,
                                   final SpreadsheetDelta delta) {
        checkEquals(
                delta,
                engine.saveCells(
                        save,
                        context
                ),
                () -> "saveCells " + save
        );
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

    default void deleteCellAndCheck(final SpreadsheetEngine engine,
                                    final SpreadsheetSelection delete,
                                    final SpreadsheetEngineContext context,
                                    final SpreadsheetDelta delta) {
        checkEquals(
                delta,
                engine.deleteCells(
                        delete,
                        context
                ),
                () -> "deleteCell " + delete
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
        checkMessage(
                thrown,
                "Several cells [B2 1] are outside the range C3"
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

        checkMessage(
                thrown,
                "Several cells [B2 1] are outside the range C3:D4"
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

        checkMessage(
                thrown,
                "Several cells [B2 1] are outside the range C3:D4"
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

        checkMessage(
                thrown,
                "Several cells [B2 1, E5 4] are outside the range C3:D4"
        );
    }

    default void fillCellsAndCheck(final SpreadsheetEngine engine,
                                   final Collection<SpreadsheetCell> cells,
                                   final SpreadsheetCellRangeReference from,
                                   final SpreadsheetCellRangeReference to,
                                   final SpreadsheetEngineContext context,
                                   final SpreadsheetDelta delta) {
        checkEquals(
                delta,
                engine.fillCells(cells, from, to, context),
                () -> "fillCells " + cells + " " + from + " to " + to
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
                                SpreadsheetValueType.ANY, // valueType
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
    default void testFilterCellsWithEmptyValueTypeFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> this.createSpreadsheetEngine()
                        .filterCells(
                                Sets.empty(), // cells
                                "", // valueType
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
                                SpreadsheetValueType.ANY, // valueType
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
                                SpreadsheetValueType.ANY, // valueType
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

    default void filterCellsAndCheck(final SpreadsheetEngine engine,
                                     final Set<SpreadsheetCell> cells,
                                     final String valueType,
                                     final Expression expression,
                                     final SpreadsheetEngineContext context,
                                     final SpreadsheetCell... expected) {
        this.filterCellsAndCheck(
                engine,
                cells,
                valueType,
                expression,
                context,
                Sets.of(
                        expected
                )
        );
    }

    default void filterCellsAndCheck(final SpreadsheetEngine engine,
                                     final Set<SpreadsheetCell> cells,
                                     final String valueType,
                                     final Expression expression,
                                     final SpreadsheetEngineContext context,
                                     final Set<SpreadsheetCell> expected) {
        this.checkEquals(
                expected,
                engine.filterCells(
                        cells,
                        valueType,
                        expression,
                        context
                ),
                () -> "filterCells " + cells + " " + valueType + " " + expression
        );
    }

    // findCells........................................................................................................

    @Test
    default void testFindCellsWithNullCellsFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSpreadsheetEngine()
                        .findCells(
                                null, // range
                                SpreadsheetCellRangeReferencePath.LRTD, // path
                                0, // offset
                                100, // count
                                SpreadsheetValueType.ANY, // valueType
                                Expression.value(true), // expression
                                SpreadsheetDeltaProperties.ALL,
                                SpreadsheetEngineContexts.fake() // context
                        )
        );
    }

    @Test
    default void testFindCellsWithNullPathFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSpreadsheetEngine()
                        .findCells(
                                SpreadsheetSelection.ALL_CELLS, // range
                                null, // path
                                0, // offset
                                100, // count
                                SpreadsheetValueType.ANY, // valueType
                                Expression.value(true), // expression
                                SpreadsheetDeltaProperties.ALL,
                                SpreadsheetEngineContexts.fake() // context
                        )
        );
    }

    @Test
    default void testFindCellsWithInvalidOffsetFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> this.createSpreadsheetEngine()
                        .findCells(
                                SpreadsheetSelection.ALL_CELLS, // range
                                SpreadsheetCellRangeReferencePath.LRTD, // path
                                -1, // offset
                                0,  // count
                                SpreadsheetValueType.ANY, // valueType
                                Expression.value(true), // expression
                                SpreadsheetDeltaProperties.ALL,
                                SpreadsheetEngineContexts.fake() // context
                        )
        );
    }

    @Test
    default void testFindCellsWithInvalidCountFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> this.createSpreadsheetEngine()
                        .findCells(
                                SpreadsheetSelection.ALL_CELLS, // range
                                SpreadsheetCellRangeReferencePath.LRTD, // path
                                0, // offset
                                -1, // count
                                SpreadsheetValueType.ANY, // valueType
                                Expression.value(true), // expression
                                SpreadsheetDeltaProperties.ALL,
                                SpreadsheetEngineContexts.fake() // context
                        )
        );
    }

    @Test
    default void testFindCellsWithNullValueTypeFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSpreadsheetEngine()
                        .findCells(
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
    default void testFindCellsWithNullExpressionFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSpreadsheetEngine()
                        .findCells(
                                SpreadsheetSelection.ALL_CELLS, // range
                                SpreadsheetCellRangeReferencePath.LRTD, // path
                                0, // offset
                                100, // count
                                SpreadsheetValueType.ANY, // valueType
                                null, // expression
                                SpreadsheetDeltaProperties.ALL,
                                SpreadsheetEngineContexts.fake() // context
                        )
        );
    }

    @Test
    default void testFindCellsWithNullDeltaPropertiesFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSpreadsheetEngine()
                        .findCells(
                                SpreadsheetSelection.ALL_CELLS, // range
                                SpreadsheetCellRangeReferencePath.LRTD, // path
                                0, // offset
                                100, // count
                                SpreadsheetValueType.ANY, // valueType
                                Expression.value(true), // expression
                                null,
                                SpreadsheetEngineContexts.fake() // context
                        )
        );
    }

    @Test
    default void testFindCellsWithNullContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSpreadsheetEngine()
                        .findCells(
                                SpreadsheetSelection.ALL_CELLS, // range
                                SpreadsheetCellRangeReferencePath.LRTD, // path
                                0, // offset
                                100, // count
                                SpreadsheetValueType.ANY, // valueType
                                Expression.value(true), // expression
                                SpreadsheetDeltaProperties.ALL,
                                null // context
                        )
        );
    }

    default void findCellsAndCheck(
            final SpreadsheetEngine engine,
            final SpreadsheetCellRangeReference range,
            final SpreadsheetCellRangeReferencePath path,
            final int offset,
            final int count,
            final String valueType,
            final Expression expression,
            final Set<SpreadsheetDeltaProperties> deltaProperties,
            final SpreadsheetEngineContext context,
            final SpreadsheetDelta expected) {
        this.checkEquals(
                expected,
                engine.findCells(
                        range,
                        path,
                        offset,
                        count,
                        valueType,
                        expression,
                        deltaProperties,
                        context
                ),
                () -> "findCells range=" + range + " path=" + path + " offset=" + offset + " count=" + count + " valueType=" + valueType + " expression=" + expression + " deltaProperties=" + deltaProperties
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

    default void findCellsWithReferenceAndCheck(final SpreadsheetEngine engine,
                                                final SpreadsheetExpressionReference reference,
                                                final int offset,
                                                final int count,
                                                final SpreadsheetEngineContext context,
                                                final SpreadsheetDelta expected) {
        this.checkEquals(
                expected,
                engine.findCellsWithReference(
                        reference,
                        offset,
                        count,
                        context
                ),
                () -> "findCellsWithReference " + reference + " offset=" + offset + " count=" + count
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

    default void findFormulaReferencesAndCheck(final SpreadsheetEngine engine,
                                               final SpreadsheetCellReference cell,
                                               final int offset,
                                               final int count,
                                               final Set<SpreadsheetDeltaProperties> properties,
                                               final SpreadsheetEngineContext context,
                                               final SpreadsheetDelta expected) {
        this.checkEquals(
                expected,
                engine.findFormulaReferences(
                        cell,
                        offset,
                        count,
                        properties,
                        context
                ),
                () -> "findFormulaReferences cell=" + cell + ", offset=" + offset + ", count=" + count + ", properties=" + properties
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

    default void sortCellsAndCheck(final SpreadsheetEngine engine,
                                   final String cellRange,
                                   final String comparators,
                                   final Set<SpreadsheetDeltaProperties> deltaProperties,
                                   final SpreadsheetEngineContext context,
                                   final SpreadsheetDelta expected) {
        this.sortCellsAndCheck(
                engine,
                SpreadsheetSelection.parseCellRange(cellRange),
                SpreadsheetColumnOrRowSpreadsheetComparatorNames.parseList(comparators),
                deltaProperties,
                context,
                expected
        );
    }

    default void sortCellsAndCheck(final SpreadsheetEngine engine,
                                   final SpreadsheetCellRangeReference cellRange,
                                   final List<SpreadsheetColumnOrRowSpreadsheetComparatorNames> comparators,
                                   final Set<SpreadsheetDeltaProperties> deltaProperties,
                                   final SpreadsheetEngineContext context,
                                   final SpreadsheetDelta expected) {
        this.checkEquals(
                expected,
                engine.sortCells(
                        cellRange,
                        comparators,
                        deltaProperties,
                        context
                ),
                () -> cellRange + " " + comparators + " " + deltaProperties
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

    default void deleteColumnsAndCheck(final SpreadsheetEngine engine,
                                       final SpreadsheetColumnReference column,
                                       final int count,
                                       final SpreadsheetEngineContext context,
                                       final SpreadsheetCell... updated) {
        final SpreadsheetDelta result = engine.deleteColumns(
                column,
                count,
                context
        );

        this.checkEquals(
                SpreadsheetDelta.EMPTY.setCells(
                                Sets.of(updated)
                        ).setColumnCount(
                                OptionalInt.of(
                                        engine.columnCount(context)
                                )
                        )
                        .setRowCount(
                                OptionalInt.of(
                                        engine.rowCount(context)
                                )
                        ),
                result,
                () -> "deleteColumns column: " + column + " count: " + count
        );
    }

    default void deleteColumnsAndCheck(final SpreadsheetEngine engine,
                                       final SpreadsheetColumnReference column,
                                       final int count,
                                       final SpreadsheetEngineContext context,
                                       final SpreadsheetDelta delta) {
        checkEquals(
                delta,
                engine.deleteColumns(column, count, context),
                () -> "deleteColumns column: " + column + " count: " + count
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


    default void deleteRowsAndCheck(final SpreadsheetEngine engine,
                                    final SpreadsheetRowReference row,
                                    final int count,
                                    final SpreadsheetEngineContext context,
                                    final SpreadsheetCell... updated) {
        final SpreadsheetDelta result = engine.deleteRows(row, count, context);

        final SpreadsheetDelta expected = SpreadsheetDelta.EMPTY
                .setCells(
                        Sets.of(updated)

                ).setColumnCount(
                        OptionalInt.of(
                                engine.columnCount(context)
                        )
                )
                .setRowCount(
                        OptionalInt.of(
                                engine.rowCount(context)
                        )
                );
        this.checkEquals(
                expected,
                result,
                () -> "deleteRows row: " + row + " count: " + count
        );
    }

    default void deleteRowsAndCheck(final SpreadsheetEngine engine,
                                    final SpreadsheetRowReference row,
                                    final int count,
                                    final SpreadsheetEngineContext context,
                                    final SpreadsheetDelta delta) {
        checkEquals(
                delta,
                engine.deleteRows(row, count, context),
                () -> "deleteRows row: " + row + " count: " + count
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

    default void insertColumnsAndCheck(final SpreadsheetEngine engine,
                                       final SpreadsheetColumnReference column,
                                       final int count,
                                       final SpreadsheetEngineContext context,
                                       final SpreadsheetCell... updated) {
        final SpreadsheetDelta result = engine.insertColumns(
                column,
                count,
                context
        );

        final SpreadsheetDelta expected = SpreadsheetDelta.EMPTY
                .setCells(
                        Sets.of(updated)
                ).setColumnCount(
                        OptionalInt.of(
                                engine.columnCount(context)
                        )
                )
                .setRowCount(
                        OptionalInt.of(
                                engine.rowCount(context)
                        )
                );

        this.checkEquals(
                expected,
                result,
                () -> "insertColumns column: " + column + " count: " + count
        );
    }

    default void insertColumnsAndCheck(final SpreadsheetEngine engine,
                                       final SpreadsheetColumnReference column,
                                       final int count,
                                       final SpreadsheetEngineContext context,
                                       final SpreadsheetDelta delta) {
        checkEquals(
                delta,
                engine.insertColumns(column, count, context),
                () -> "insertColumns column: " + column + " count: " + count
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

    default void insertRowsAndCheck(final SpreadsheetEngine engine,
                                    final SpreadsheetRowReference row,
                                    final int count,
                                    final SpreadsheetEngineContext context,
                                    final SpreadsheetCell... updated) {
        final SpreadsheetDelta result = engine.insertRows(
                row,
                count,
                context
        );

        final SpreadsheetDelta expected = SpreadsheetDelta.EMPTY
                .setCells(
                        Sets.of(updated)
                ).setColumnCount(
                        OptionalInt.of(
                                engine.columnCount(context)
                        )
                )
                .setRowCount(
                        OptionalInt.of(
                                engine.rowCount(context)
                        )
                );

        this.checkEquals(
                expected,
                result,
                () -> "insertRows row: " + row + " count: " + count
        );
    }

    default void insertRowsAndCheck(final SpreadsheetEngine engine,
                                    final SpreadsheetRowReference row,
                                    final int count,
                                    final SpreadsheetEngineContext context,
                                    final SpreadsheetDelta delta) {
        checkEquals(
                delta,
                engine.insertRows(row, count, context),
                () -> "insertRows row: " + row + " count: " + count
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

    default void loadFormAndCheck(final SpreadsheetEngine engine,
                                  final FormName formName,
                                  final SpreadsheetEngineContext context,
                                  final SpreadsheetDelta expected) {
        this.checkEquals(
                expected,
                engine.loadForm(
                        formName,
                        context
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

    default void saveFormWithDuplicateFieldsCheck(final SpreadsheetEngine engine,
                                                  final Form<SpreadsheetExpressionReference> form,
                                                  final SpreadsheetEngineContext context,
                                                  final SpreadsheetExpressionReference... expected) {
        this.saveFormWithDuplicateFieldsCheck(
                engine,
                form,
                context,
                Sets.of(expected)
        );
    }

    default void saveFormWithDuplicateFieldsCheck(final SpreadsheetEngine engine,
                                                  final Form<SpreadsheetExpressionReference> form,
                                                  final SpreadsheetEngineContext context,
                                                  final Set<SpreadsheetExpressionReference> expected) {
        final DuplicateFormFieldReferencesException thrown = assertThrows(
                DuplicateFormFieldReferencesException.class,
                () -> engine.saveForm(
                        form,
                        context
                )
        );

        this.checkEquals(
                expected,
                thrown.references()
        );
    }

    default void saveFormAndCheck(final SpreadsheetEngine engine,
                                  final Form<SpreadsheetExpressionReference> form,
                                  final SpreadsheetEngineContext context,
                                  final SpreadsheetDelta expected) {
        this.checkEquals(
                expected,
                engine.saveForm(
                        form,
                        context
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

    default void deleteFormAndCheck(final SpreadsheetEngine engine,
                                    final FormName form,
                                    final SpreadsheetEngineContext context,
                                    final SpreadsheetDelta expected) {
        this.checkEquals(
                expected,
                engine.deleteForm(
                        form,
                        context
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

    default void loadFormsAndCheck(final SpreadsheetEngine engine,
                                   final int offset,
                                   final int count,
                                   final SpreadsheetEngineContext context,
                                   final SpreadsheetDelta expected) {
        this.checkEquals(
                expected,
                engine.loadForms(
                        offset,
                        count,
                        context
                ),
                () -> "loadForms offset=" + offset + " count=" + count
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

    default void prepareFormAndCheck(final SpreadsheetEngine engine,
                                     final FormName formName,
                                     final SpreadsheetExpressionReference selection,
                                     final SpreadsheetEngineContext context,
                                     final SpreadsheetDelta expected) {
        this.checkEquals(
                expected,
                engine.prepareForm(
                        formName,
                        selection,
                        context
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

    default void submitFormAndCheck(final SpreadsheetEngine engine,
                                      final Form<SpreadsheetExpressionReference> form,
                                      final SpreadsheetExpressionReference selection,
                                      final SpreadsheetEngineContext context,
                                      final SpreadsheetDelta expected) {
        this.checkEquals(
                expected,
                engine.submitForm(
                        form,
                        selection,
                        context
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

    default void saveLabelAndCheck(final SpreadsheetEngine engine,
                                   final SpreadsheetLabelMapping label,
                                   final SpreadsheetEngineContext context,
                                   final SpreadsheetDelta delta) {
        this.checkEquals(
                delta,
                engine.saveLabel(
                        label,
                        context
                ),
                () -> "saveLabel " + label
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

    default void deleteLabelAndCheck(final SpreadsheetEngine engine,
                                     final SpreadsheetLabelName label,
                                     final SpreadsheetEngineContext context,
                                     final SpreadsheetCell... cells) {
        this.deleteLabelAndCheck(
                engine,
                label,
                context,
                Sets.of(cells)
        );
    }

    default void deleteLabelAndCheck(final SpreadsheetEngine engine,
                                     final SpreadsheetLabelName label,
                                     final SpreadsheetEngineContext context,
                                     final Set<SpreadsheetCell> cells) {
        final SpreadsheetDelta result = engine.deleteLabel(
                label,
                context
        );


        final SpreadsheetDelta expected = SpreadsheetDelta.EMPTY
                .setCells(cells)
                .setDeletedLabels(
                        Sets.of(label)
                )
                .setColumnCount(
                        OptionalInt.of(
                                engine.columnCount(context)
                        )
                ).setRowCount(
                        OptionalInt.of(
                                engine.rowCount(context)
                        )
                );
        this.checkEquals(
                expected,
                result,
                () -> "deleteLabel " + label
        );
    }

    default void deleteLabelAndCheck(final SpreadsheetEngine engine,
                                     final SpreadsheetLabelName label,
                                     final SpreadsheetEngineContext context,
                                     final SpreadsheetDelta delta) {
        this.checkEquals(
                delta,
                engine.deleteLabel(label, context),
                () -> "deleteLabel " + label
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

    default void loadLabelAndCheck(final SpreadsheetEngine engine,
                                   final SpreadsheetLabelName label,
                                   final SpreadsheetEngineContext context,
                                   final SpreadsheetDelta expected) {
        this.checkEquals(
                expected,
                engine.loadLabel(label, context),
                () -> "loadLabel " + label
        );
    }

    default void loadLabelAndCheck(final SpreadsheetLabelStore labelStore,
                                   final SpreadsheetLabelName label) {
        this.loadLabelAndCheck(
                labelStore,
                label,
                Optional.empty()
        );
    }

    default void loadLabelAndCheck(final SpreadsheetLabelStore labelStore,
                                   final SpreadsheetLabelName label,
                                   final SpreadsheetExpressionReference reference) {
        this.loadLabelAndCheck(
                labelStore,
                label,
                SpreadsheetLabelMapping.with(label, reference)
        );
    }

    default void loadLabelAndCheck(final SpreadsheetLabelStore labelStore,
                                   final SpreadsheetLabelName label,
                                   final SpreadsheetLabelMapping mapping) {
        this.loadLabelAndCheck(
                labelStore,
                label,
                Optional.of(mapping)
        );
    }

    default void loadLabelAndCheck(final SpreadsheetLabelStore labelStore,
                                   final SpreadsheetLabelName label,
                                   final Optional<SpreadsheetLabelMapping> mapping) {
        this.checkEquals(
                mapping,
                labelStore.load(label),
                () -> "label " + label + " loaded");
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

    default void loadLabelsAndCheck(final SpreadsheetEngine engine,
                                    final int offset,
                                    final int count,
                                    final SpreadsheetEngineContext context,
                                    final SpreadsheetLabelMapping... mappings) {
        this.loadLabelsAndCheck(
                engine,
                offset,
                count,
                context,
                SpreadsheetDelta.EMPTY.setLabels(
                        Sets.of(mappings)
                )
        );
    }

    default void loadLabelsAndCheck(final SpreadsheetEngine engine,
                                    final int offset,
                                    final int count,
                                    final SpreadsheetEngineContext context,
                                    final SpreadsheetDelta expected) {
        this.checkEquals(
                expected,
                engine.loadLabels(
                        offset,
                        count,
                        context
                ),
                () -> "loadLabels offset=" + offset + " count=" + count
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

    default void findLabelsWithReferenceAndCheck(final SpreadsheetEngine engine,
                                                 final SpreadsheetExpressionReference reference,
                                                 final int offset,
                                                 final int count,
                                                 final SpreadsheetEngineContext context,
                                                 final SpreadsheetLabelMapping... expected) {
        this.findLabelsWithReferenceAndCheck(
                engine,
                reference,
                offset,
                count,
                context,
                Sets.of(expected)
        );
    }

    default void findLabelsWithReferenceAndCheck(final SpreadsheetEngine engine,
                                                 final SpreadsheetExpressionReference reference,
                                                 final int offset,
                                                 final int count,
                                                 final SpreadsheetEngineContext context,
                                                 final Set<SpreadsheetLabelMapping> expected) {
        this.findLabelsWithReferenceAndCheck(
                engine,
                reference,
                offset,
                count,
                context,
                SpreadsheetDelta.EMPTY.setLabels(expected)
        );
    }

    default void findLabelsWithReferenceAndCheck(final SpreadsheetEngine engine,
                                                 final SpreadsheetExpressionReference reference,
                                                 final int offset,
                                                 final int count,
                                                 final SpreadsheetEngineContext context,
                                                 final SpreadsheetDelta expected) {
        this.checkEquals(
                expected,
                engine.findLabelsWithReference(
                        reference,
                        offset,
                        count,
                        context
                ),
                () -> "findLabelsWithReference " + reference + " offset=" + offset + " count=" + count
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

    default void columnCountAndCheck(final SpreadsheetEngine engine,
                                     final SpreadsheetEngineContext context,
                                     final int expected) {
        this.checkEquals(
                expected,
                engine.columnCount(context),
                () -> "columnCount " + engine
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
        this.columnWidthAndCheck(this.createSpreadsheetEngine(), column, context, expected);
    }

    default void columnWidthAndCheck(final SpreadsheetEngine engine,
                                     final SpreadsheetColumnReference column,
                                     final SpreadsheetEngineContext context,
                                     final double expected) {
        this.checkEquals(
                expected,
                engine.columnWidth(column, context),
                () -> "columnWidth " + column + " of " + engine
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

    default void rowCountAndCheck(final SpreadsheetEngine engine,
                                  final SpreadsheetEngineContext context,
                                  final int expected) {
        this.checkEquals(
                expected,
                engine.rowCount(context),
                () -> "rowCount " + engine
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
        this.rowHeightAndCheck(this.createSpreadsheetEngine(), row, context, expected);
    }

    default void rowHeightAndCheck(final SpreadsheetEngine engine,
                                   final SpreadsheetRowReference row,
                                   final SpreadsheetEngineContext context,
                                   final double expected) {
        this.checkEquals(expected,
                engine.rowHeight(row, context),
                () -> "rowHeight " + row + " of " + engine);
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

    default void navigateAndCheck(final SpreadsheetEngine engine,
                                  final SpreadsheetViewport viewport,
                                  final SpreadsheetEngineContext context,
                                  final SpreadsheetViewport expected) {
        this.navigateAndCheck(
                engine,
                viewport,
                context,
                Optional.of(expected)
        );
    }

    default void navigateAndCheck(final SpreadsheetEngine engine,
                                  final SpreadsheetViewport viewport,
                                  final SpreadsheetEngineContext context,
                                  final Optional<SpreadsheetViewport> expected) {
        this.checkEquals(
                expected,
                engine.navigate(viewport, context),
                () -> "navigate " + viewport
        );
    }

    // window...........................................................................................................

    @Test
    default void testWindowWithNullRectangleFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSpreadsheetEngine()
                        .window(
                                null,
                                false,
                                SpreadsheetEngine.NO_SELECTION,
                                SpreadsheetEngineContexts.fake()
                        )
        );
    }

    @Test
    default void testWindowWithNullSelectionFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSpreadsheetEngine()
                        .window(
                                SpreadsheetViewportRectangle.with(
                                        SpreadsheetSelection.A1,
                                        1, // width
                                        2 // height
                                ),
                                false,
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
                                SpreadsheetViewportRectangle.with(
                                        SpreadsheetSelection.A1,
                                        1, // width
                                        2 // height
                                ),
                                false,
                                SpreadsheetEngine.NO_SELECTION,
                                null
                        )
        );
    }

    default void windowAndCheck(
            final SpreadsheetEngine engine,
            final SpreadsheetViewportRectangle viewport,
            final boolean includeFrozenColumnsRows,
            final Optional<SpreadsheetSelection> selection,
            final SpreadsheetEngineContext context,
            final String window) {
        this.windowAndCheck(
                engine,
                viewport,
                includeFrozenColumnsRows,
                selection,
                context,
                SpreadsheetViewportWindows.parse(window)
        );
    }

    default void windowAndCheck(
            final SpreadsheetEngine engine,
            final SpreadsheetViewportRectangle viewport,
            final boolean includeFrozenColumnsRows,
            final Optional<SpreadsheetSelection> selection,
            final SpreadsheetEngineContext context,
            final SpreadsheetCellRangeReference... window) {
        this.windowAndCheck(
                engine,
                viewport,
                includeFrozenColumnsRows,
                selection,
                context,
                SpreadsheetViewportWindows.with(
                        Sets.of(window)
                )
        );
    }

    default void windowAndCheck(
            final SpreadsheetEngine engine,
            final SpreadsheetViewportRectangle viewport,
            final boolean includeFrozenColumnsRows,
            final Optional<SpreadsheetSelection> selection,
            final SpreadsheetEngineContext context,
            final SpreadsheetViewportWindows window) {
        this.checkEquals(
                window,
                engine.window(
                        viewport,
                        includeFrozenColumnsRows,
                        selection,
                        context
                ),
                () -> "window " + viewport +
                        (includeFrozenColumnsRows ? " includeFrozenColumnsRows" : "") +
                        selection.orElse(null)
        );
    }

    E createSpreadsheetEngine();

    SpreadsheetEngineContext createContext();

    default void countAndCheck(final Store<?, ?> store,
                               final int count) {
        this.checkEquals(count,
                store.count(),
                "record count in " + store);
    }

    // helpers..........................................................................................................

    default void cellFormulaTextAndCheck(final SpreadsheetCell cell,
                                         final String formula) {
        this.checkEquals(
                formula,
                cell.formula().text(),
                () -> "formula.text parse returned cell=" + cell
        );
    }

    default void cellFormulaErrorOrValueAndCheck(final SpreadsheetCell cell,
                                                 final Object value) {
        this.checkEquals(
                value,
                cell.formula()
                        .errorOrValue()
                        .orElse(null
                        ),
                () -> "formula values returned cell=" + cell);
    }

    default void cellFormattedValueAndCheck(final SpreadsheetCell cell) {
        this.checkEquals(
                Optional.empty(),
                cell.formattedValue(),
                "formattedValue text absent"
        );
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    default void cellFormattedValueAndCheck(final SpreadsheetCell cell,
                                            final String text) {
        this.checkNotEquals(
                Optional.empty(),
                cell.formattedValue(),
                "formattedValue present"
        );
        this.checkEquals(
                text,
                cell.formattedValue()
                        .get()
                        .text(),
                "formattedText"
        );
    }

    default Converter<ExpressionNumberConverterContext> converter() {
        return Converters.collection(
                Lists.of(
                        Converters.simple(),
                        ExpressionNumberConverters.toNumberOrExpressionNumber(Converters.numberToNumber()),
                        ExpressionNumberConverters.numberOrExpressionNumberToNumber()
                                .to(
                                        Number.class,
                                        Converters.numberToNumber()
                                )
                )
        );
    }

    default ExpressionNumberConverterContext converterContext() {
        return ExpressionNumberConverterContexts.basic(
                this.converter(),
                ConverterContexts.basic(
                        Converters.JAVA_EPOCH_OFFSET,
                        Converters.fake(),
                        this.dateTimeContext(),
                        this.decimalNumberContext()
                ),
                this.expressionNumberKind()
        );
    }

    default ExpressionNumberKind expressionNumberKind() {
        return ExpressionNumberKind.BIG_DECIMAL;
    }

    default DateTimeContext dateTimeContext() {
        final Locale locale = this.decimalNumberContext()
                .locale();

        return DateTimeContexts.basic(
                DateTimeSymbols.fromDateFormatSymbols(
                        new DateFormatSymbols(locale)
                ),
                locale,
                1900,
                50,
                LocalDateTime::now
        );
    }

    default DecimalNumberContext decimalNumberContext() {
        return DecimalNumberContexts.american(MathContext.DECIMAL32);
    }

    // class............................................................................................................

    @Override
    default JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}

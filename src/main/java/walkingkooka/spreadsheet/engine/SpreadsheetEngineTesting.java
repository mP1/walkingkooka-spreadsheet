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
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.ThrowableTesting;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetViewport;
import walkingkooka.spreadsheet.SpreadsheetViewportWindows;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRange;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.reference.SpreadsheetViewportSelection;
import walkingkooka.spreadsheet.reference.SpreadsheetViewportSelectionAnchor;
import walkingkooka.spreadsheet.reference.store.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.store.Store;
import walkingkooka.text.CharSequences;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberConverterContext;
import walkingkooka.tree.expression.ExpressionNumberConverterContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.Collection;
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

    @Test
    default void testSaveCellNullCellFails() {
        assertThrows(NullPointerException.class, () -> this.createSpreadsheetEngine().saveCell(null,
                this.createContext()));
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

    @Test
    default void testDeleteColumnsNullColumnFails() {
        assertThrows(NullPointerException.class, () -> this.createSpreadsheetEngine().deleteColumns(null, 1, this.createContext()));
    }

    @Test
    default void testDeleteColumnsNegativeCountFails() {
        assertThrows(IllegalArgumentException.class, () -> this.createSpreadsheetEngine().deleteColumns(COLUMN, -1, this.createContext()));
    }

    @Test
    default void testDeleteColumnsNullContextFails() {
        assertThrows(NullPointerException.class, () -> this.createSpreadsheetEngine().deleteColumns(COLUMN, 1, null));
    }

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

    @Test
    default void testDeleteRowsNullRowFails() {
        assertThrows(NullPointerException.class, () -> this.createSpreadsheetEngine().deleteRows(null, 1, this.createContext()));
    }

    @Test
    default void testDeleteRowsNegativeCountFails() {
        assertThrows(IllegalArgumentException.class, () -> this.createSpreadsheetEngine().deleteRows(ROW, -1, this.createContext()));
    }

    @Test
    default void testDeleteRowsNullContextFails() {
        assertThrows(NullPointerException.class, () -> this.createSpreadsheetEngine().deleteRows(ROW, 1, null));
    }

    @Test
    default void testInsertColumnsNullColumnFails() {
        assertThrows(NullPointerException.class, () -> this.createSpreadsheetEngine().insertColumns(null, 1, this.createContext()));
    }

    @Test
    default void testInsertColumnsNegativeCountFails() {
        assertThrows(IllegalArgumentException.class, () -> this.createSpreadsheetEngine().insertColumns(COLUMN, -1, this.createContext()));
    }

    @Test
    default void testInsertColumnsNullContextFails() {
        assertThrows(NullPointerException.class, () -> this.createSpreadsheetEngine().insertColumns(COLUMN, 1, null));
    }

    @Test
    default void testInsertRowsNullRowFails() {
        assertThrows(NullPointerException.class, () -> this.createSpreadsheetEngine().insertRows(null, 1, this.createContext()));
    }

    @Test
    default void testInsertRowsNegativeCountFails() {
        assertThrows(IllegalArgumentException.class, () -> this.createSpreadsheetEngine().insertRows(ROW, -1, this.createContext()));
    }

    @Test
    default void testInsertRowsNullContextFails() {
        assertThrows(NullPointerException.class, () -> this.createSpreadsheetEngine().insertRows(ROW, 1, null));
    }

    @Test
    default void testFillCellsNullCellsFails() {
        final SpreadsheetCellReference reference = SpreadsheetSelection.A1;
        final SpreadsheetCellRange range = reference.cellRange(reference);

        assertThrows(NullPointerException.class, () -> this.createSpreadsheetEngine().fillCells(null,
                range,
                range,
                this.createContext()));
    }

    @Test
    default void testFillCellsNullFromFails() {
        final SpreadsheetCellReference reference = SpreadsheetSelection.A1;
        final SpreadsheetCell cell = reference.setFormula(
                SpreadsheetFormula.EMPTY
                        .setText("1")
        );
        final SpreadsheetCellRange range = reference.cellRange(reference);

        assertThrows(NullPointerException.class, () -> this.createSpreadsheetEngine().fillCells(Lists.of(cell),
                null,
                range,
                this.createContext()));
    }

    @Test
    default void testFillCellsNullToFails() {
        final SpreadsheetCellReference reference = SpreadsheetSelection.A1;
        final SpreadsheetCell cell = reference.setFormula(
                SpreadsheetFormula.EMPTY
                        .setText("1")
        );
        final SpreadsheetCellRange range = reference.cellRange(reference);

        assertThrows(NullPointerException.class, () -> this.createSpreadsheetEngine().fillCells(Lists.of(cell),
                range,
                null,
                this.createContext()));
    }

    @Test
    default void testFillCellsNullContextFails() {
        final SpreadsheetCellReference reference = SpreadsheetSelection.A1;
        final SpreadsheetCell cell = reference.setFormula(
                SpreadsheetFormula.EMPTY
                        .setText("1")
        );
        final SpreadsheetCellRange range = reference.cellRange(reference);

        assertThrows(NullPointerException.class, () -> this.createSpreadsheetEngine().fillCells(Lists.of(cell),
                range,
                range,
                null));
    }

    @Test
    default void testFillCellsCellOutOfFromRangeFails() {
        final SpreadsheetCellReference reference = SpreadsheetSelection.parseCell("B2");
        final SpreadsheetCell cell = reference.setFormula(
                SpreadsheetFormula.EMPTY
                        .setText("1")
        );
        final SpreadsheetCellRange range = SpreadsheetCellRange.fromCells(Lists.of(SpreadsheetSelection.parseCell("C3")));

        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> this.createSpreadsheetEngine().fillCells(Lists.of(cell),
                range,
                range,
                this.createContext()));
        checkMessage(thrown, "Several cells [B2=1] are outside the range C3");
    }

    @Test
    default void testFillCellsCellOutOfFromRangeFails2() {
        final SpreadsheetCellReference reference = SpreadsheetSelection.parseCell("B2");
        final SpreadsheetCell cell = reference.setFormula(
                SpreadsheetFormula.EMPTY
                        .setText("1")
        );
        final SpreadsheetCellRange range = SpreadsheetCellRange.parseCellRange("C3:D4");

        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> this.createSpreadsheetEngine().fillCells(Lists.of(cell),
                range,
                range,
                this.createContext()));

        checkMessage(thrown, "Several cells [B2=1] are outside the range C3:D4");
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

        final SpreadsheetCellRange range = SpreadsheetCellRange.parseCellRange("C3:D4");

        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> this.createSpreadsheetEngine().fillCells(Lists.of(b2, c3, d4),
                range,
                range,
                this.createContext()));

        checkMessage(thrown, "Several cells [B2=1] are outside the range C3:D4");
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

        final SpreadsheetCellRange range = SpreadsheetCellRange.parseCellRange("C3:D4");

        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> this.createSpreadsheetEngine().fillCells(Lists.of(b2, c3, d4, e5),
                range,
                range,
                this.createContext()));

        checkMessage(thrown, "Several cells [B2=1, E5=4] are outside the range C3:D4");
    }

    @Test
    default void testSaveLabelNullMappingFails() {
        assertThrows(NullPointerException.class, () -> this.createSpreadsheetEngine().saveLabel(null, this.createContext()));
    }

    @Test
    default void testSaveLabelNullContextFails() {
        assertThrows(NullPointerException.class, () -> this.createSpreadsheetEngine().saveLabel(SpreadsheetLabelMapping.with(SpreadsheetSelection.labelName("LABEL123"),
                SpreadsheetSelection.A1), null));
    }

    default void saveLabelAndCheck(final SpreadsheetEngine engine,
                                   final SpreadsheetLabelMapping label,
                                   final SpreadsheetEngineContext context,
                                   final SpreadsheetCell... cells) {
        this.saveLabelAndCheck(engine, label, context, Sets.of(cells));
    }

    default void saveLabelAndCheck(final SpreadsheetEngine engine,
                                   final SpreadsheetLabelMapping label,
                                   final SpreadsheetEngineContext context,
                                   final Set<SpreadsheetCell> cells) {
        final SpreadsheetDelta result = engine.saveLabel(
                label,
                context
        );

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final int columns = cellStore.columns();
        final int rows = cellStore.rows();

        final SpreadsheetDelta expected = SpreadsheetDelta.EMPTY
                .setCells(cells)
                .setMaxColumn(
                        -1 == columns ?
                                OptionalInt.empty() :
                                OptionalInt.of(
                                        columns
                                )
                ).setMaxRow(
                        -1 == rows ?
                                OptionalInt.empty() :
                                OptionalInt.of(
                                        rows
                                )
                );
        this.checkEquals(
                expected,
                result,
                () -> "saveLabel " + label
        );
    }

    default void saveLabelAndCheck(final SpreadsheetEngine engine,
                                   final SpreadsheetLabelMapping label,
                                   final SpreadsheetEngineContext context,
                                   final SpreadsheetDelta delta) {
        this.checkEquals(delta,
                engine.saveLabel(label, context),
                () -> "saveLabel " + label);
    }

    @Test
    default void testRemoveLabelNullMappingFails() {
        assertThrows(NullPointerException.class, () -> this.createSpreadsheetEngine().removeLabel(null, this.createContext()));
    }

    @Test
    default void testRemoveLabelNullContextFails() {
        assertThrows(NullPointerException.class, () -> this.createSpreadsheetEngine().removeLabel(SpreadsheetSelection.labelName("label"), null));
    }

    default void removeLabelAndCheck(final SpreadsheetEngine engine,
                                     final SpreadsheetLabelName label,
                                     final SpreadsheetEngineContext context,
                                     final SpreadsheetCell... cells) {
        this.removeLabelAndCheck(engine, label, context, Sets.of(cells));
    }

    default void removeLabelAndCheck(final SpreadsheetEngine engine,
                                     final SpreadsheetLabelName label,
                                     final SpreadsheetEngineContext context,
                                     final Set<SpreadsheetCell> cells) {
        final SpreadsheetDelta result = engine.removeLabel(
                label,
                context
        );

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final int columns = cellStore.columns();
        final int rows = cellStore.rows();

        final SpreadsheetDelta expected = SpreadsheetDelta.EMPTY
                .setMaxColumn(
                        -1 == columns ?
                                OptionalInt.empty() :
                                OptionalInt.of(columns)
                ).setMaxRow(
                        -1 == rows ?
                                OptionalInt.empty() :
                                OptionalInt.of(rows)
                );
        this.checkEquals(
                expected,
                result,
                () -> "removeLabel " + label
        );
    }

    default void removeLabelAndCheck(final SpreadsheetEngine engine,
                                     final SpreadsheetLabelName label,
                                     final SpreadsheetEngineContext context,
                                     final SpreadsheetDelta delta) {
        this.checkEquals(delta,
                engine.removeLabel(label, context),
                () -> "removeLabel " + label);
    }

    @Test
    default void testLoadLabelNullMappingFails() {
        assertThrows(NullPointerException.class, () -> this.createSpreadsheetEngine().loadLabel(null, SpreadsheetEngineContexts.fake()));
    }

    default void loadLabelAndCheck(final SpreadsheetEngine engine,
                                   final SpreadsheetLabelName label,
                                   final SpreadsheetEngineContext context,
                                   final SpreadsheetLabelMapping mapping) {
        this.checkEquals(Optional.of(mapping),
                engine.loadLabel(label, context),
                () -> "loadLabel " + label);
    }

    default void loadLabelAndFailCheck(final SpreadsheetEngine engine,
                                       final SpreadsheetLabelName label,
                                       final SpreadsheetEngineContext context) {
        this.checkEquals(Optional.empty(),
                engine.loadLabel(label, context),
                () -> "loadLabel " + label);
    }

    @Test
    default void testColumnWidthNullColumnReferenceFails() {
        assertThrows(NullPointerException.class, () -> this.createSpreadsheetEngine().columnWidth(null, this.createContext()));
    }

    @Test
    default void testColumnWidthNullContextFails() {
        assertThrows(NullPointerException.class, () -> this.createSpreadsheetEngine().columnWidth(SpreadsheetColumnReference.parseColumn("A"), null));
    }

    @Test
    default void testRowHeightNullRowReferenceFails() {
        assertThrows(NullPointerException.class, () -> this.createSpreadsheetEngine().rowHeight(null, this.createContext()));
    }

    @Test
    default void testRowHeightNullContextFails() {
        assertThrows(NullPointerException.class, () -> this.createSpreadsheetEngine().rowHeight(SpreadsheetRowReference.parseRow("1"), null));
    }

    // allColumnsWidth..................................................................................................

    @Test
    default void testAllColumnsWidthNullContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSpreadsheetEngine()
                        .allColumnsWidth(null)
        );
    }

    @Test
    default void testAllColumnsWidthWhenNoCells() {
        this.allColumnsWidthAndCheck(
                this.createContext(),
                0
        );
    }

    // allRowsHeight....................................................................................................

    @Test
    default void testAllRowsHeightNullContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSpreadsheetEngine()
                        .allRowsHeight(null)
        );
    }

    // navigate.........................................................................................................

    @Test
    default void testNavigateNullSelectionFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSpreadsheetEngine()
                        .navigate(
                                null,
                                this.createContext()
                        )
        );
    }

    @Test
    default void testNavigateNullContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSpreadsheetEngine()
                        .navigate(
                                SpreadsheetSelection.parseColumn("A")
                                        .setAnchor(SpreadsheetViewportSelectionAnchor.NONE),
                                null
                        )
        );
    }

    E createSpreadsheetEngine();

    SpreadsheetEngineContext createContext();

    default SpreadsheetCell loadCellOrFail(final SpreadsheetEngine engine,
                                           final SpreadsheetCellReference reference,
                                           final SpreadsheetEngineEvaluation evaluation,
                                           final SpreadsheetEngineContext context) {
        final SpreadsheetDelta delta = engine.loadCells(
                reference,
                evaluation,
                SpreadsheetDeltaProperties.ALL,
                context
        );

        return delta.cells()
                .stream()
                .filter(c -> c.reference().equalsIgnoreReferenceKind(reference))
                .findFirst()
                .orElseGet(() -> {
                    Assertions.fail("Loading " + reference + " failed to return requested cell, cells: " + delta);
                    return null;
                });
    }

    default void loadCellFailCheck(final SpreadsheetCellReference reference,
                                   final SpreadsheetEngineEvaluation evaluation) {
        this.loadCellFailCheck(reference, evaluation, this.createContext());
    }

    default void loadCellFailCheck(final SpreadsheetCellReference reference,
                                   final SpreadsheetEngineEvaluation evaluation,
                                   final SpreadsheetEngineContext context) {
        this.loadCellFailCheck(
                this.createSpreadsheetEngine(),
                reference,
                evaluation,
                context
        );
    }

    default void loadCellFailCheck(final SpreadsheetEngine engine,
                                   final SpreadsheetCellReference reference,
                                   final SpreadsheetEngineContext context) {
        this.loadCellFailCheck(
                engine,
                reference,
                SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                context
        );
    }

    default void loadCellFailCheck(final SpreadsheetEngine engine,
                                   final SpreadsheetCellReference reference,
                                   final SpreadsheetEngineEvaluation evaluation,
                                   final SpreadsheetEngineContext context) {
        final SpreadsheetDelta loaded = engine.loadCells(
                reference,
                evaluation,
                SpreadsheetDeltaProperties.ALL,
                context
        );

        this.checkEquals(Optional.empty(),
                loaded.cells()
                        .stream()
                        .filter(c -> c.reference().equals(reference))
                        .findFirst(),
                "Expected reference " + reference + " to fail");
    }

    @SuppressWarnings("UnusedReturnValue")
    default SpreadsheetCell loadCellAndCheckWithoutValueOrError(final SpreadsheetEngine engine,
                                                                final SpreadsheetCellReference reference,
                                                                final SpreadsheetEngineEvaluation evaluation,
                                                                final Set<SpreadsheetDeltaProperties> deltaProperties,
                                                                final SpreadsheetEngineContext context) {
        final SpreadsheetCell cell = this.loadCellOrFail(
                engine,
                reference,
                evaluation,
                context
        );
        this.checkEquals(
                SpreadsheetFormula.NO_VALUE,
                cell.formula().value(),
                () -> "values from returned cells=" + cell);
        return cell;
    }

    default SpreadsheetCell loadCellAndCheckFormula(final SpreadsheetEngine engine,
                                                    final SpreadsheetCellReference reference,
                                                    final SpreadsheetEngineEvaluation evaluation,
                                                    final SpreadsheetEngineContext context,
                                                    final String formula) {
        final SpreadsheetCell cell = this.loadCellOrFail(
                engine,
                reference,
                evaluation,
                context
        );
        this.checkFormula(cell, formula);
        return cell;
    }

    @SuppressWarnings("UnusedReturnValue")
    default SpreadsheetCell loadCellAndCheckFormulaAndValue(final SpreadsheetEngine engine,
                                                            final SpreadsheetCellReference reference,
                                                            final SpreadsheetEngineEvaluation evaluation,
                                                            final SpreadsheetEngineContext context,
                                                            final String formula,
                                                            final Object value) {
        final SpreadsheetCell cell = this.loadCellAndCheckFormula(
                engine,
                reference,
                evaluation,
                context,
                formula
        );
        this.checkValue(cell, value);
        return cell;
    }

    default SpreadsheetCell loadCellAndCheckValue(final SpreadsheetEngine engine,
                                                  final SpreadsheetCellReference reference,
                                                  final SpreadsheetEngineEvaluation evaluation,
                                                  final SpreadsheetEngineContext context,
                                                  final Object value) {
        final SpreadsheetCell cell = this.loadCellOrFail(
                engine,
                reference,
                evaluation,
                context
        );
        this.checkValue(cell, value);
        return cell;
    }

    default SpreadsheetCell loadCellAndCheckFormatted(final SpreadsheetEngine engine,
                                                      final SpreadsheetCellReference reference,
                                                      final SpreadsheetEngineEvaluation evaluation,
                                                      final SpreadsheetEngineContext context,
                                                      final Object value,
                                                      final String text) {
        return this.loadCellAndCheck(
                engine,
                reference,
                evaluation,
                context,
                value,
                text,
                null
        );
    }

    default void loadCellAndCheckError(final SpreadsheetEngine engine,
                                       final SpreadsheetCellReference reference,
                                       final SpreadsheetEngineEvaluation evaluation,
                                       final SpreadsheetEngineContext context,
                                       final String errorContains) {
        final SpreadsheetCell cell = this.loadCellOrFail(
                engine,
                reference,
                evaluation,
                context
        );
        final SpreadsheetFormula formula = cell.formula();
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

    default SpreadsheetCell loadCellAndCheck(final SpreadsheetEngine engine,
                                             final SpreadsheetCellReference reference,
                                             final SpreadsheetEngineEvaluation evaluation,
                                             final SpreadsheetEngineContext context,
                                             final Object value,
                                             final String text,
                                             final String errorContains) {
        final SpreadsheetCell cell = this.loadCellAndCheckValue(
                engine,
                reference,
                evaluation,
                context,
                value
        );

        this.checkFormattedText(cell, text);

        if (null != errorContains) {
            final SpreadsheetFormula formula = cell.formula();
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

        return cell;
    }

    default void loadCellAndCheck(final SpreadsheetEngine engine,
                                  final SpreadsheetCellReference reference,
                                  final SpreadsheetEngineEvaluation evaluation,
                                  final Set<SpreadsheetDeltaProperties> deltaProperties,
                                  final SpreadsheetEngineContext context,
                                  final SpreadsheetDelta loaded) {
        this.checkEquals(
                loaded,
                engine.loadCells(
                        reference,
                        evaluation,
                        deltaProperties,
                        context
                ),
                () -> "loadCell " + reference);
    }

    default void loadLabelAndCheck(final SpreadsheetLabelStore labelStore,
                                   final SpreadsheetLabelName label,
                                   final SpreadsheetExpressionReference reference) {
        this.loadLabelAndCheck(labelStore,
                label,
                SpreadsheetLabelMapping.with(label, reference));
    }

    default void loadLabelAndCheck(final SpreadsheetLabelStore labelStore,
                                   final SpreadsheetLabelName label,
                                   final SpreadsheetLabelMapping mapping) {
        this.checkEquals(Optional.of(mapping),
                labelStore.load(label),
                () -> "label " + label + " loaded");
    }

    default void loadLabelFailCheck(final SpreadsheetLabelStore labelStore,
                                    final SpreadsheetLabelName label) {
        this.checkEquals(Optional.empty(),
                labelStore.load(label),
                "label loaded failed");
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
                engine.saveCells(save, context),
                () -> "saveCells " + save
        );
    }

    default void deleteCellAndCheck(final SpreadsheetEngine engine,
                                    final SpreadsheetSelection delete,
                                    final SpreadsheetEngineContext context,
                                    final SpreadsheetDelta delta) {
        checkEquals(
                delta,
                engine.deleteCells(delete, context),
                () -> "deleteCell " + delete
        );
    }

    default void countAndCheck(final Store<?, ?> store, final int count) {
        this.checkEquals(count,
                store.count(),
                "record count in " + store);
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
        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final int columns = cellStore.columns();
        final int rows = cellStore.rows();

        this.checkEquals(
                SpreadsheetDelta.EMPTY.setCells(
                        Sets.of(updated)
                ).setMaxColumn(
                        -1 == columns ?
                                OptionalInt.empty() :
                                OptionalInt.of(columns)
                ).setMaxRow(
                        -1 == rows ?
                                OptionalInt.empty() :
                                OptionalInt.of(rows)
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

    default void deleteRowsAndCheck(final SpreadsheetEngine engine,
                                    final SpreadsheetRowReference row,
                                    final int count,
                                    final SpreadsheetEngineContext context,
                                    final SpreadsheetCell... updated) {
        final SpreadsheetDelta result = engine.deleteRows(row, count, context);

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final int columns = cellStore.columns();
        final int rows = cellStore.rows();

        final SpreadsheetDelta expected = SpreadsheetDelta.EMPTY
                .setCells(
                        Sets.of(updated)

                ).setMaxColumn(
                        -1 == columns ?
                                OptionalInt.empty() :
                                OptionalInt.of(columns)
                ).setMaxRow(
                        -1 == rows ?
                                OptionalInt.empty() :
                                OptionalInt.of(rows)
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

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetDelta expected = SpreadsheetDelta.EMPTY
                .setCells(
                        Sets.of(updated)
                ).setMaxColumn(
                        OptionalInt.of(
                                cellStore.columns()
                        )
                ).setMaxRow(
                        OptionalInt.of(
                                cellStore.rows()
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

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final SpreadsheetDelta expected = SpreadsheetDelta.EMPTY
                .setCells(
                        Sets.of(updated)
                ).setMaxColumn(
                        OptionalInt.of(
                                cellStore.columns()
                        )
                ).setMaxRow(
                        OptionalInt.of(
                                cellStore.rows()
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

    default void loadCellsAndCheck(final SpreadsheetEngine engine,
                                   final String cells,
                                   final SpreadsheetEngineEvaluation evaluation,
                                   final Set<SpreadsheetDeltaProperties> deltaProperties,
                                   final SpreadsheetEngineContext context,
                                   final SpreadsheetCell... updated) {
        this.loadCellsAndCheck(
                engine,
                SpreadsheetViewportWindows.parse(cells)
                        .cellRanges(),
                evaluation,
                deltaProperties,
                context,
                updated
        );
    }

    default void loadCellsAndCheck(final SpreadsheetEngine engine,
                                   final Set<SpreadsheetCellRange> ranges,
                                   final SpreadsheetEngineEvaluation evaluation,
                                   final Set<SpreadsheetDeltaProperties> deltaProperties,
                                   final SpreadsheetEngineContext context,
                                   final SpreadsheetCell... updated) {
        final SpreadsheetDelta result = engine.loadCells(
                ranges,
                evaluation,
                deltaProperties,
                context
        );

        final SpreadsheetCellStore cellStore = context.storeRepository()
                .cells();

        final int columns = cellStore.columns();
        final int rows = cellStore.rows();

        final SpreadsheetDelta expected = SpreadsheetDelta.EMPTY
                .setCells(
                        Sets.of(updated)

                ).setMaxColumn(
                        -1 == columns ?
                                OptionalInt.empty() :
                                OptionalInt.of(columns)
                ).setMaxRow(
                        -1 == rows ?
                                OptionalInt.empty() :
                                OptionalInt.of(rows)
                ).setWindow(
                        result.window()
                );
        this.checkEquals(
                expected,
                result,
                () -> "loadCells " + ranges + " " + evaluation
        );
    }

    default void loadCellsAndCheck(final SpreadsheetEngine engine,
                                   final Set<SpreadsheetCellRange> ranges,
                                   final SpreadsheetEngineEvaluation evaluation,
                                   final Set<SpreadsheetDeltaProperties> deltaProperties,
                                   final SpreadsheetEngineContext context,
                                   final SpreadsheetDelta delta) {
        checkEquals(
                delta,
                engine.loadCells(
                        ranges,
                        evaluation,
                        deltaProperties,
                        context
                ),
                () -> "loadCells " + ranges + " " + evaluation
        );
    }

    default void fillCellsAndCheck(final SpreadsheetEngine engine,
                                   final Collection<SpreadsheetCell> cells,
                                   final SpreadsheetCellRange from,
                                   final SpreadsheetCellRange to,
                                   final SpreadsheetEngineContext context,
                                   final SpreadsheetDelta delta) {
        checkEquals(
                delta,
                engine.fillCells(cells, from, to, context),
                () -> "fillCells " + cells + " " + from + " to " + to
        );
    }

    default void checkFormula(final SpreadsheetCell cell, final String formula) {
        this.checkEquals(formula,
                cell.formula().text(),
                () -> "formula.text from returned cell=" + cell);
    }

    default void checkValue(final SpreadsheetCell cell, final Object value) {
        this.checkEquals(
                value,
                cell.formula().value().orElse(null),
                () -> "values from returned cell=" + cell);
    }

    default void checkFormattedText(final SpreadsheetCell cell) {
        this.checkEquals(Optional.empty(), cell.formatted(), "formatted text absent");
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    default void checkFormattedText(final SpreadsheetCell cell, final String text) {
        this.checkNotEquals(Optional.empty(), cell.formatted(), "formatted text present");
        this.checkEquals(text, cell.formatted().get().text(), "formattedText");
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
        this.checkEquals(expected,
                engine.columnWidth(column, context),
                () -> "columnWidth " + column + " of " + engine);
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

    // allColumnsWidthAndCheck.........................................................................................

    default void allColumnsWidthAndCheck(final SpreadsheetEngineContext context,
                                         final double expected) {
        this.allColumnsWidthAndCheck(
                this.createSpreadsheetEngine(),
                context,
                expected
        );
    }

    default void allColumnsWidthAndCheck(final SpreadsheetEngine engine,
                                         final SpreadsheetEngineContext context,
                                         final double expected) {
        this.checkEquals(
                expected,
                engine.allColumnsWidth(context),
                () -> "allColumnsWidth of " + engine
        );
    }

    // allRowsHeightAndCheck.........................................................................................

    default void allRowsHeightAndCheck(final SpreadsheetEngineContext context,
                                       final double expected) {
        this.allRowsHeightAndCheck(
                this.createSpreadsheetEngine(),
                context,
                expected
        );
    }

    default void allRowsHeightAndCheck(final SpreadsheetEngine engine,
                                       final SpreadsheetEngineContext context,
                                       final double expected) {
        this.checkEquals(
                expected,
                engine.allRowsHeight(context),
                () -> "allRowsHeight of " + engine
        );
    }

    // window...........................................................................................................

    default void windowAndCheck(
            final SpreadsheetEngine engine,
            final SpreadsheetViewport viewport,
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
            final SpreadsheetViewport viewport,
            final boolean includeFrozenColumnsRows,
            final Optional<SpreadsheetSelection> selection,
            final SpreadsheetEngineContext context,
            final SpreadsheetCellRange... window) {
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
            final SpreadsheetViewport viewport,
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

    // navigate.........................................................................................................

    default void navigateAndCheck(final SpreadsheetEngine engine,
                                  final SpreadsheetViewportSelection selection,
                                  final SpreadsheetEngineContext context,
                                  final SpreadsheetViewportSelection expected) {
        this.navigateAndCheck(
                engine,
                selection,
                context,
                Optional.of(expected)
        );
    }

    default void navigateAndCheck(final SpreadsheetEngine engine,
                                  final SpreadsheetViewportSelection selection,
                                  final SpreadsheetEngineContext context,
                                  final Optional<SpreadsheetViewportSelection> expected) {
        this.checkEquals(
                expected,
                engine.navigate(selection, context),
                () -> "navigate " + selection
        );
    }

    default Converter<ExpressionNumberConverterContext> converter() {
        return Converters.collection(
                Lists.of(
                        Converters.simple(),
                        ExpressionNumber.toConverter(Converters.numberNumber()),
                        ExpressionNumber.fromConverter(Converters.numberNumber())
                )
        );
    }

    default ExpressionNumberConverterContext converterContext() {
        return ExpressionNumberConverterContexts.basic(this.converter(),
                ConverterContexts.basic(Converters.fake(),
                        this.dateTimeContext(),
                        this.decimalNumberContext()),
                this.expressionNumberKind());
    }

    default ExpressionNumberKind expressionNumberKind() {
        return ExpressionNumberKind.DEFAULT;
    }

    default DateTimeContext dateTimeContext() {
        return DateTimeContexts.locale(
                this.decimalNumberContext().locale(),
                1900,
                50,
                LocalDateTime::now
        );
    }

    default DecimalNumberContext decimalNumberContext() {
        return DecimalNumberContexts.american(MathContext.DECIMAL32);
    }

    @Override
    default JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}

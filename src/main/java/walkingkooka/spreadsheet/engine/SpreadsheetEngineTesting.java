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
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRange;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.store.SpreadsheetLabelStore;
import walkingkooka.store.Store;
import walkingkooka.test.ClassTesting2;
import walkingkooka.test.ThrowableTesting;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.type.JavaVisibility;

import java.math.MathContext;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public interface SpreadsheetEngineTesting<E extends SpreadsheetEngine> extends ClassTesting2<E>,
        ThrowableTesting {

    SpreadsheetColumnReference COLUMN = SpreadsheetReferenceKind.ABSOLUTE.column(1);
    SpreadsheetRowReference ROW = SpreadsheetReferenceKind.ABSOLUTE.row(2);
    SpreadsheetCellReference REFERENCE = COLUMN.setRow(ROW);
    SpreadsheetLabelName LABEL = SpreadsheetExpressionReference.labelName("LABEL123");

    @Test
    default void testLoadCellNullCellFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().loadCell(null, SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY, this.createContext());
        });
    }

    @Test
    default void testLoadCellNullEvaluationFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().loadCell(REFERENCE,
                    null,
                    this.createContext());
        });
    }

    @Test
    default void testLoadCellNullContextFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().loadCell(REFERENCE,
                    SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                    null);
        });
    }

    @Test
    default void testSaveCellNullCellFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().saveCell(null,
                    this.createContext());
        });
    }

    @Test
    default void testSaveCellNullContextFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().saveCell(SpreadsheetCell.with(REFERENCE, SpreadsheetFormula.with("1")),
                    null);
        });
    }

    @Test
    default void testDeleteCellNullCellFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().deleteCell(null,
                    this.createContext());
        });
    }

    @Test
    default void testDeleteCellNullContextFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().deleteCell(REFERENCE,
                    null);
        });
    }

    @Test
    default void testDeleteColumnsNullColumnFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().deleteColumns(null, 1, this.createContext());
        });
    }

    @Test
    default void testDeleteColumnsNegativeCountFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            this.createSpreadsheetEngine().deleteColumns(COLUMN, -1, this.createContext());
        });
    }

    @Test
    default void testDeleteColumnsNullContextFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().deleteColumns(COLUMN, 1, null);
        });
    }

    @Test
    default void testDeleteRowsNullRowFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().deleteRows(null, 1, this.createContext());
        });
    }

    @Test
    default void testDeleteRowsNegativeCountFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            this.createSpreadsheetEngine().deleteRows(ROW, -1, this.createContext());
        });
    }

    @Test
    default void testDeleteRowsNullContextFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().deleteRows(ROW, 1, null);
        });
    }

    @Test
    default void testInsertColumnsNullColumnFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().insertColumns(null, 1, this.createContext());
        });
    }

    @Test
    default void testInsertColumnsNegativeCountFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            this.createSpreadsheetEngine().insertColumns(COLUMN, -1, this.createContext());
        });
    }

    @Test
    default void testInsertColumnsNullContextFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().insertColumns(COLUMN, 1, null);
        });
    }

    @Test
    default void testInsertRowsNullRowFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().insertRows(null, 1, this.createContext());
        });
    }

    @Test
    default void testInsertRowsNegativeCountFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            this.createSpreadsheetEngine().insertRows(ROW, -1, this.createContext());
        });
    }

    @Test
    default void testInsertRowsNullContextFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().insertRows(ROW, 1, null);
        });
    }

    @Test
    default void testFillCellsNullCellsFails() {
        final SpreadsheetCellReference reference = SpreadsheetCellReference.parseCellReference("A1");
        final SpreadsheetRange range = reference.spreadsheetRange(reference);

        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().fillCells(null,
                    range,
                    range,
                    this.createContext());
        });
    }

    @Test
    default void testFillCellsNullFromFails() {
        final SpreadsheetCellReference reference = SpreadsheetCellReference.parseCellReference("A1");
        final SpreadsheetCell cell = SpreadsheetCell.with(reference, SpreadsheetFormula.with("1"));
        final SpreadsheetRange range = reference.spreadsheetRange(reference);

        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().fillCells(Lists.of(cell),
                    null,
                    range,
                    this.createContext());
        });
    }

    @Test
    default void testFillCellsNullToFails() {
        final SpreadsheetCellReference reference = SpreadsheetCellReference.parseCellReference("A1");
        final SpreadsheetCell cell = SpreadsheetCell.with(reference, SpreadsheetFormula.with("1"));
        final SpreadsheetRange range = reference.spreadsheetRange(reference);

        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().fillCells(Lists.of(cell),
                    range,
                    null,
                    this.createContext());
        });
    }

    @Test
    default void testFillCellsNullContextFails() {
        final SpreadsheetCellReference reference = SpreadsheetCellReference.parseCellReference("A1");
        final SpreadsheetCell cell = SpreadsheetCell.with(reference, SpreadsheetFormula.with("1"));
        final SpreadsheetRange range = reference.spreadsheetRange(reference);

        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().fillCells(Lists.of(cell),
                    range,
                    range,
                    null);
        });
    }

    @Test
    default void testFillCellsCellOutOfFromRangeFails() {
        final SpreadsheetCellReference reference = SpreadsheetCellReference.parseCellReference("B2");
        final SpreadsheetCell cell = SpreadsheetCell.with(reference, SpreadsheetFormula.with("1"));

        final SpreadsheetRange range = SpreadsheetRange.fromCells(Lists.of(SpreadsheetCellReference.parseCellReference("C3")));

        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            this.createSpreadsheetEngine().fillCells(Lists.of(cell),
                    range,
                    range,
                    this.createContext());
        });
        checkMessage(thrown, "Several cells [B2=1] are outside the range C3");
    }

    @Test
    default void testFillCellsCellOutOfFromRangeFails2() {
        final SpreadsheetCellReference reference = SpreadsheetCellReference.parseCellReference("B2");
        final SpreadsheetCell cell = SpreadsheetCell.with(reference, SpreadsheetFormula.with("1"));

        final SpreadsheetRange range = SpreadsheetRange.parseRange("C3:D4");

        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            this.createSpreadsheetEngine().fillCells(Lists.of(cell),
                    range,
                    range,
                    this.createContext());
        });

        checkMessage(thrown, "Several cells [B2=1] are outside the range C3:D4");
    }

    @Test
    default void testFillCellsOneCellsOutOfManyOutOfRange() {
        final SpreadsheetCell b2 = SpreadsheetCell.with(SpreadsheetCellReference.parseCellReference("B2"), SpreadsheetFormula.with("1"));
        final SpreadsheetCell c3 = SpreadsheetCell.with(SpreadsheetCellReference.parseCellReference("C3"), SpreadsheetFormula.with("2"));
        final SpreadsheetCell d4 = SpreadsheetCell.with(SpreadsheetCellReference.parseCellReference("D4"), SpreadsheetFormula.with("3"));

        final SpreadsheetRange range = SpreadsheetRange.parseRange("C3:D4");

        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            this.createSpreadsheetEngine().fillCells(Lists.of(b2, c3, d4),
                    range,
                    range,
                    this.createContext());
        });

        checkMessage(thrown, "Several cells [B2=1] are outside the range C3:D4");
    }

    @Test
    default void testFillCellsSeveralCellsOutOfFromRangeFails() {
        final SpreadsheetCell b2 = SpreadsheetCell.with(SpreadsheetCellReference.parseCellReference("B2"), SpreadsheetFormula.with("1"));
        final SpreadsheetCell c3 = SpreadsheetCell.with(SpreadsheetCellReference.parseCellReference("C3"), SpreadsheetFormula.with("2"));
        final SpreadsheetCell d4 = SpreadsheetCell.with(SpreadsheetCellReference.parseCellReference("D4"), SpreadsheetFormula.with("3"));
        final SpreadsheetCell e5 = SpreadsheetCell.with(SpreadsheetCellReference.parseCellReference("E5"), SpreadsheetFormula.with("4"));

        final SpreadsheetRange range = SpreadsheetRange.parseRange("C3:D4");

        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            this.createSpreadsheetEngine().fillCells(Lists.of(b2, c3, d4, e5),
                    range,
                    range,
                    this.createContext());
        });

        checkMessage(thrown, "Several cells [B2=1, E5=4] are outside the range C3:D4");
    }

    @Test
    default void testSaveLabelNullMappingFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().saveLabel(null, this.createContext());
        });
    }

    @Test
    default void testSaveLabelNullContextFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().saveLabel(SpreadsheetLabelMapping.with(SpreadsheetExpressionReference.labelName("LABEL123"),
                    SpreadsheetExpressionReference.parseCellReference("A1")), null);
        });
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
        this.saveLabelAndCheck(engine,
                label,
                context,
                SpreadsheetDelta.with(cells));
    }

    default void saveLabelAndCheck(final SpreadsheetEngine engine,
                                   final SpreadsheetLabelMapping label,
                                   final SpreadsheetEngineContext context,
                                   final SpreadsheetDelta delta) {
        assertEquals(delta,
                engine.saveLabel(label, context),
                () -> "saveLabel " + label);
    }

    @Test
    default void testRemoveLabelNullMappingFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().removeLabel(null, this.createContext());
        });
    }

    @Test
    default void testRemoveLabelNullContextFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().removeLabel(SpreadsheetExpressionReference.labelName("label"), null);
        });
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
        this.removeLabelAndCheck(engine,
                label,
                context,
                SpreadsheetDelta.with(cells));
    }

    default void removeLabelAndCheck(final SpreadsheetEngine engine,
                                     final SpreadsheetLabelName label,
                                     final SpreadsheetEngineContext context,
                                     final SpreadsheetDelta delta) {
        assertEquals(delta,
                engine.removeLabel(label, context),
                () -> "removeLabel " + label);
    }

    @Test
    default void testLoadLabelNullMappingFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createSpreadsheetEngine().loadLabel(null);
        });
    }

    default void loadLabelAndCheck(final SpreadsheetEngine engine,
                                   final SpreadsheetLabelName label,
                                   final SpreadsheetLabelMapping mapping) {
        assertEquals(Optional.of(mapping),
                engine.loadLabel(label),
                () -> "loadLabel " + label);
    }

    default void loadLabelAndFailCheck(final SpreadsheetEngine engine,
                                       final SpreadsheetLabelName label) {
        assertEquals(Optional.empty(),
                engine.loadLabel(label),
                () -> "loadLabel " + label);
    }

    E createSpreadsheetEngine();

    SpreadsheetEngineContext createContext();

    default void spreadsheetIdAndCheck(final SpreadsheetEngine engine, final SpreadsheetId id) {
        assertEquals(id,
                engine.id(),
                () -> engine.toString());
    }

    default SpreadsheetCell loadCellOrFail(final SpreadsheetEngine engine,
                                           final SpreadsheetCellReference reference,
                                           final SpreadsheetEngineEvaluation evaluation,
                                           final SpreadsheetEngineContext context) {
        final SpreadsheetDelta delta = engine.loadCell(reference, evaluation, context);

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
        this.loadCellFailCheck(this.createSpreadsheetEngine(), reference, evaluation, context);
    }

    default void loadCellFailCheck(final SpreadsheetEngine engine,
                                   final SpreadsheetCellReference reference,
                                   final SpreadsheetEngineContext context) {
        this.loadCellFailCheck(engine, reference, SpreadsheetEngineEvaluation.SKIP_EVALUATE, context);
    }

    default void loadCellFailCheck(final SpreadsheetEngine engine,
                                   final SpreadsheetCellReference reference,
                                   final SpreadsheetEngineEvaluation evaluation,
                                   final SpreadsheetEngineContext context) {
        final SpreadsheetDelta loaded = engine.loadCell(reference, evaluation, context);

        assertEquals(Optional.empty(),
                loaded.cells()
                        .stream()
                        .filter(c -> c.reference().equals(reference))
                        .findFirst(),
                "Expected reference " + reference + " to fail");
    }

    default SpreadsheetCell loadCellAndCheckWithoutValueOrError(final SpreadsheetEngine engine,
                                                                final SpreadsheetCellReference reference,
                                                                final SpreadsheetEngineEvaluation evaluation,
                                                                final SpreadsheetEngineContext context) {
        final SpreadsheetCell cell = this.loadCellOrFail(engine, reference, evaluation, context);
        assertEquals(null,
                this.valueOrError(cell, null),
                () -> "values from returned cells=" + cell);
        return cell;
    }

    default SpreadsheetCell loadCellAndCheckFormula(final SpreadsheetEngine engine,
                                                    final SpreadsheetCellReference reference,
                                                    final SpreadsheetEngineEvaluation evaluation,
                                                    final SpreadsheetEngineContext context,
                                                    final String formula) {
        final SpreadsheetCell cell = this.loadCellOrFail(engine, reference, evaluation, context);
        this.checkFormula(cell, formula);
        return cell;
    }

    default SpreadsheetCell loadCellAndCheckFormulaAndValue(final SpreadsheetEngine engine,
                                                            final SpreadsheetCellReference reference,
                                                            final SpreadsheetEngineEvaluation evaluation,
                                                            final SpreadsheetEngineContext context,
                                                            final String formula,
                                                            final Object value) {
        final SpreadsheetCell cell = this.loadCellAndCheckFormula(engine, reference, evaluation, context, formula);
        this.checkValueOrError(cell, value);
        return cell;
    }

    default SpreadsheetCell loadCellAndCheckValue(final SpreadsheetEngine engine,
                                                  final SpreadsheetCellReference reference,
                                                  final SpreadsheetEngineEvaluation evaluation,
                                                  final SpreadsheetEngineContext context,
                                                  final Object value) {
        final SpreadsheetCell cell = this.loadCellOrFail(engine, reference, evaluation, context);
        this.checkValueOrError(cell, value);
        return cell;
    }

    default SpreadsheetCell loadCellAndCheckFormatted(final SpreadsheetEngine engine,
                                                      final SpreadsheetCellReference reference,
                                                      final SpreadsheetEngineEvaluation evaluation,
                                                      final SpreadsheetEngineContext context,
                                                      final Object value,
                                                      final String text) {
        final SpreadsheetCell cell = this.loadCellAndCheckValue(engine, reference, evaluation, context, value);
        this.checkFormattedText(cell, text);
        return cell;
    }

    default void loadCellAndCheckError(final SpreadsheetEngine engine,
                                       final SpreadsheetCellReference reference,
                                       final SpreadsheetEngineEvaluation evaluation,
                                       final SpreadsheetEngineContext context,
                                       final String errorContains) {
        final SpreadsheetCell cell = this.loadCellOrFail(engine, reference, evaluation, context);

        final Optional<SpreadsheetError> error = cell.formula().error();
        assertNotEquals(SpreadsheetFormula.NO_ERROR,
                error,
                () -> "Expected error missing=" + cell);
        assertTrue(error.get().value().contains(errorContains),
                () -> "Error message " + error + " missing " + CharSequences.quoteAndEscape(errorContains));
    }

    default void loadCellAndCheck(final SpreadsheetEngine engine,
                                  final SpreadsheetCellReference reference,
                                  final SpreadsheetEngineEvaluation evaluation,
                                  final SpreadsheetEngineContext context,
                                  final SpreadsheetCell... cells) {
        assertEquals(SpreadsheetDelta.with(Sets.of(cells)),
                engine.loadCell(reference, evaluation, context),
                () -> "loadCell " + reference);
    }

    default void loadLabelAndCheck(final SpreadsheetLabelStore labelStore,
                                   final SpreadsheetLabelName label,
                                   final ExpressionReference reference) {
        this.loadLabelAndCheck(labelStore,
                label,
                SpreadsheetLabelMapping.with(label, reference));
    }

    default void loadLabelAndCheck(final SpreadsheetLabelStore labelStore,
                                   final SpreadsheetLabelName label,
                                   final SpreadsheetLabelMapping mapping) {
        assertEquals(Optional.of(mapping),
                labelStore.load(label),
                () -> "label " + label + " loaded");
    }

    default void loadLabelFailCheck(final SpreadsheetLabelStore labelStore,
                                    final SpreadsheetLabelName label) {
        assertEquals(Optional.empty(),
                labelStore.load(label),
                "label loaded failed");
    }

    default void saveCellAndCheck(final SpreadsheetEngine engine,
                                  final SpreadsheetCell save,
                                  final SpreadsheetEngineContext context,
                                  final SpreadsheetCell... updated) {
        this.saveCellAndCheck(engine,
                save,
                context,
                SpreadsheetDelta.with(Sets.of(updated)));
    }

    default void saveCellAndCheck(final SpreadsheetEngine engine,
                                  final SpreadsheetCell save,
                                  final SpreadsheetEngineContext context,
                                  final SpreadsheetDelta delta) {
        assertEquals(delta,
                engine.saveCell(save, context),
                () -> "saveCell " + save);
    }

    default void deleteCellAndCheck(final SpreadsheetEngine engine,
                                    final SpreadsheetCellReference delete,
                                    final SpreadsheetEngineContext context,
                                    final SpreadsheetCell... updated) {
        this.deleteCellAndCheck(engine,
                delete,
                context,
                SpreadsheetDelta.with(Sets.of(updated)));
    }

    default void deleteCellAndCheck(final SpreadsheetEngine engine,
                                    final SpreadsheetCellReference delete,
                                    final SpreadsheetEngineContext context,
                                    final SpreadsheetDelta delta) {
        assertEquals(delta,
                engine.deleteCell(delete, context),
                () -> "deleteCell " + delete);
    }

    default void countAndCheck(final Store<?, ?> store, final int count) {
        assertEquals(count,
                store.count(),
                "record count in " + store);
    }

    default void deleteColumnsAndCheck(final SpreadsheetEngine engine,
                                       final SpreadsheetColumnReference column,
                                       final int count,
                                       final SpreadsheetEngineContext context,
                                       final SpreadsheetCell... updated) {
        this.deleteColumnsAndCheck(engine,
                column,
                count,
                context,
                SpreadsheetDelta.with(Sets.of(updated)));
    }

    default void deleteColumnsAndCheck(final SpreadsheetEngine engine,
                                       final SpreadsheetColumnReference column,
                                       final int count,
                                       final SpreadsheetEngineContext context,
                                       final SpreadsheetDelta delta) {
        assertEquals(delta,
                engine.deleteColumns(column, count, context),
                () -> "deleteColumns column: " + column + " count: " + count);
    }

    default void deleteRowsAndCheck(final SpreadsheetEngine engine,
                                    final SpreadsheetRowReference row,
                                    final int count,
                                    final SpreadsheetEngineContext context,
                                    final SpreadsheetCell... updated) {
        this.deleteRowsAndCheck(engine,
                row,
                count,
                context,
                SpreadsheetDelta.with(Sets.of(updated)));
    }

    default void deleteRowsAndCheck(final SpreadsheetEngine engine,
                                    final SpreadsheetRowReference column,
                                    final int count,
                                    final SpreadsheetEngineContext context,
                                    final SpreadsheetDelta delta) {
        assertEquals(delta,
                engine.deleteRows(column, count, context),
                () -> "deleteRows column: " + column + " count: " + count);
    }

    default void insertColumnsAndCheck(final SpreadsheetEngine engine,
                                       final SpreadsheetColumnReference column,
                                       final int count,
                                       final SpreadsheetEngineContext context,
                                       final SpreadsheetCell... updated) {
        this.insertColumnsAndCheck(engine,
                column,
                count,
                context,
                SpreadsheetDelta.with(Sets.of(updated)));
    }

    default void insertColumnsAndCheck(final SpreadsheetEngine engine,
                                       final SpreadsheetColumnReference column,
                                       final int count,
                                       final SpreadsheetEngineContext context,
                                       final SpreadsheetDelta delta) {
        assertEquals(delta,
                engine.insertColumns(column, count, context),
                () -> "insertColumns column: " + column + " count: " + count);
    }

    default void insertRowsAndCheck(final SpreadsheetEngine engine,
                                    final SpreadsheetRowReference row,
                                    final int count,
                                    final SpreadsheetEngineContext context,
                                    final SpreadsheetCell... updated) {
        this.insertRowsAndCheck(engine,
                row,
                count,
                context,
                SpreadsheetDelta.with(Sets.of(updated)));
    }

    default void insertRowsAndCheck(final SpreadsheetEngine engine,
                                    final SpreadsheetRowReference column,
                                    final int count,
                                    final SpreadsheetEngineContext context,
                                    final SpreadsheetDelta delta) {
        assertEquals(delta,
                engine.insertRows(column, count, context),
                () -> "insertRows column: " + column + " count: " + count);
    }

    default void fillCellsAndCheck(final SpreadsheetEngine engine,
                                   final Collection<SpreadsheetCell> cells,
                                   final SpreadsheetRange from,
                                   final SpreadsheetRange to,
                                   final SpreadsheetEngineContext context,
                                   final SpreadsheetCell... updated) {
        this.fillCellsAndCheck(engine,
                cells,
                from,
                to,
                context,
                SpreadsheetDelta.with(Sets.of(updated)));
    }

    default void fillCellsAndCheck(final SpreadsheetEngine engine,
                                   final Collection<SpreadsheetCell> cells,
                                   final SpreadsheetRange from,
                                   final SpreadsheetRange to,
                                   final SpreadsheetEngineContext context,
                                   final SpreadsheetDelta delta) {
        assertEquals(delta,
                engine.fillCells(cells, from, to, context),
                () -> "fillCells " + cells + " " + from + " to " + to);

        // load and check updated cells again...
        delta.cells()
                .forEach(c -> this.loadCellAndCheck(engine,
                        c.reference(),
                        SpreadsheetEngineEvaluation.SKIP_EVALUATE,
                        context,
                        c));
    }

    default void checkFormula(final SpreadsheetCell cell, final String formula) {
        assertEquals(formula,
                cell.formula().text(),
                () -> "formula.text from returned cell=" + cell);
    }

    default void checkValueOrError(final SpreadsheetCell cell, final Object value) {
        assertEquals(value,
                this.valueOrError(cell, "Value and Error absent (" + cell + ")"),
                () -> "values from returned cell=" + cell);
    }

    default Object valueOrError(final SpreadsheetCell cell, final Object bothAbsent) {
        final SpreadsheetFormula formula = cell.formula();
        return formula.value()
                .orElse(formula.error()
                        .map(e -> (Object) e.value())
                        .orElse(bothAbsent));
    }

    default void checkFormattedText(final SpreadsheetCell cell, final String text) {
        assertNotEquals(Optional.empty(), cell.formatted(), "formatted text absent");
        assertEquals(text, cell.formatted().get().text(), "formattedText");
    }

    default ConverterContext converterContext() {
        return ConverterContexts.basic(this.dateTimeContext(), this.decimalNumberContext());
    }

    default DateTimeContext dateTimeContext() {
        return DateTimeContexts.locale(this.decimalNumberContext().locale(), 50);
    }

    default DecimalNumberContext decimalNumberContext() {
        return DecimalNumberContexts.american(MathContext.DECIMAL32);
    }

    @Override
    default JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}

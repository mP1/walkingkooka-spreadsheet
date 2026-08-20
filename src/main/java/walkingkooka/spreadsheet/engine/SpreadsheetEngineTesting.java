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
import walkingkooka.collect.set.Sets;
import walkingkooka.reflect.ThrowableTesting;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetColumnOrRowSpreadsheetComparatorNames;
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
import walkingkooka.spreadsheet.store.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.validation.SpreadsheetValidationReference;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.spreadsheet.value.SpreadsheetError;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewport;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewportWindows;
import walkingkooka.store.Store;
import walkingkooka.text.BinaryTextContextTesting;
import walkingkooka.text.CharSequences;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.expression.Expression;
import walkingkooka.validation.ValueType;
import walkingkooka.validation.form.DuplicateFormFieldReferencesException;
import walkingkooka.validation.form.Form;
import walkingkooka.validation.form.FormName;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public interface SpreadsheetEngineTesting extends BinaryTextContextTesting,
    TreePrintableTesting,
    ThrowableTesting {

    SpreadsheetColumnReference COLUMN = SpreadsheetReferenceKind.ABSOLUTE.column(1);
    SpreadsheetRowReference ROW = SpreadsheetReferenceKind.ABSOLUTE.row(2);
    SpreadsheetCellReference CELL_REFERENCE = COLUMN.setRow(ROW);
    SpreadsheetLabelName LABEL = SpreadsheetSelection.labelName("LABEL123");

    default void evaluateAndCheck(final SpreadsheetEngine engine,
                                  final String expression,
                                  final SpreadsheetEngineContext context,
                                  final Object expected) {
        this.checkEquals(
            expected,
            engine.evaluate(
                expression,
                context
            )
        );
    }

    // cells............................................................................................................

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
        final SpreadsheetCell spreadsheetCell = this.loadCellOrFail(
            engine,
            cell,
            evaluation,
            context
        );
        this.cellFormulaErrorOrValueAndCheck(
            spreadsheetCell,
            value
        );

        this.cellFormattedValueAndCheck(
            spreadsheetCell,
            formattedValueText
        );

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

    default void filterCellsAndCheck(final SpreadsheetEngine engine,
                                     final Set<SpreadsheetCell> cells,
                                     final ValueType valueType,
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
                                     final ValueType valueType,
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

    // findCellsWithReference..........................................................................................

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

    // queryCells.......................................................................................................

    default void queryCellsAndCheck(
        final SpreadsheetEngine engine,
        final SpreadsheetCellRangeReference range,
        final SpreadsheetCellRangeReferencePath path,
        final int offset,
        final int count,
        final ValueType valueType,
        final Expression expression,
        final Set<SpreadsheetDeltaProperties> deltaProperties,
        final SpreadsheetEngineContext context,
        final SpreadsheetDelta expected) {
        this.checkEquals(
            expected,
            engine.queryCells(
                range,
                path,
                offset,
                count,
                valueType,
                expression,
                deltaProperties,
                context
            ),
            () -> "queryCells range=" + range + " path=" + path + " offset=" + offset + " count=" + count + " valueType=" + valueType + " expression=" + expression + " deltaProperties=" + deltaProperties
        );
    }

    // sortCells........................................................................................................

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

    // deleteColumns....................................................................................................

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

    // deleteRows.......................................................................................................

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

    default void saveFormWithDuplicateFieldsCheck(final SpreadsheetEngine engine,
                                                  final Form<SpreadsheetValidationReference> form,
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
                                                  final Form<SpreadsheetValidationReference> form,
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
                                  final Form<SpreadsheetValidationReference> form,
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

    default void submitFormAndCheck(final SpreadsheetEngine engine,
                                    final Form<SpreadsheetValidationReference> form,
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

    // findFormsByName..................................................................................................

    default void findFormsByNameAndCheck(final SpreadsheetEngine engine,
                                         final String text,
                                         final int offset,
                                         final int count,
                                         final SpreadsheetEngineContext context,
                                         final Form<SpreadsheetValidationReference>... expected) {
        this.findFormsByNameAndCheck(
            engine,
            text,
            offset,
            count,
            context,
            Sets.of(expected)
        );
    }

    default void findFormsByNameAndCheck(final SpreadsheetEngine engine,
                                         final String text,
                                         final int offset,
                                         final int count,
                                         final SpreadsheetEngineContext context,
                                         final Set<Form<SpreadsheetValidationReference>> expected) {
        this.findFormsByNameAndCheck(
            engine,
            text,
            offset,
            count,
            context,
            SpreadsheetDelta.EMPTY.setForms(expected)
        );
    }

    default void findFormsByNameAndCheck(final SpreadsheetEngine engine,
                                         final String text,
                                         final int offset,
                                         final int count,
                                         final SpreadsheetEngineContext context,
                                         final SpreadsheetDelta expected) {
        this.checkEquals(
            expected,
            engine.findFormsByName(
                text,
                offset,
                count,
                context
            ),
            () -> "findFormsByName " + CharSequences.quoteAndEscape(text) + " offset=" + offset + " count=" + count
        );
    }

    // saveLabel........................................................................................................

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

    // findLabelsByName.................................................................................................

    default void findLabelsByNameAndCheck(final SpreadsheetEngine engine,
                                          final String text,
                                          final int offset,
                                          final int count,
                                          final SpreadsheetEngineContext context,
                                          final SpreadsheetLabelMapping... expected) {
        this.findLabelsByNameAndCheck(
            engine,
            text,
            offset,
            count,
            context,
            Sets.of(expected)
        );
    }

    default void findLabelsByNameAndCheck(final SpreadsheetEngine engine,
                                          final String text,
                                          final int offset,
                                          final int count,
                                          final SpreadsheetEngineContext context,
                                          final Set<SpreadsheetLabelMapping> expected) {
        this.findLabelsByNameAndCheck(
            engine,
            text,
            offset,
            count,
            context,
            SpreadsheetDelta.EMPTY.setLabels(expected)
        );
    }

    default void findLabelsByNameAndCheck(final SpreadsheetEngine engine,
                                          final String text,
                                          final int offset,
                                          final int count,
                                          final SpreadsheetEngineContext context,
                                          final SpreadsheetDelta expected) {
        this.checkEquals(
            expected,
            engine.findLabelsByName(
                text,
                offset,
                count,
                context
            ),
            () -> "findLabelsByName " + CharSequences.quoteAndEscape(text) + " offset=" + offset + " count=" + count
        );
    }

    // findLabelsWithReference..........................................................................................

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

    // rowCount.........................................................................................................

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

    default void rowHeightAndCheck(final SpreadsheetEngine engine,
                                   final SpreadsheetRowReference row,
                                   final SpreadsheetEngineContext context,
                                   final double expected) {
        this.checkEquals(expected,
            engine.rowHeight(row, context),
            () -> "rowHeight " + row + " of " + engine);
    }

    // navigate.........................................................................................................

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

    default void windowAndCheck(
        final SpreadsheetEngine engine,
        final SpreadsheetViewport viewport,
        final SpreadsheetEngineContext context,
        final String window) {
        this.windowAndCheck(
            engine,
            viewport,
            context,
            SpreadsheetViewportWindows.parse(window)
        );
    }

    default void windowAndCheck(
        final SpreadsheetEngine engine,
        final SpreadsheetViewport viewport,
        final SpreadsheetEngineContext context,
        final SpreadsheetCellRangeReference... window) {
        this.windowAndCheck(
            engine,
            viewport,
            context,
            SpreadsheetViewportWindows.with(
                Sets.of(window)
            )
        );
    }

    default void windowAndCheck(
        final SpreadsheetEngine engine,
        final SpreadsheetViewport viewport,
        final SpreadsheetEngineContext context,
        final SpreadsheetViewportWindows window) {
        this.checkEquals(
            window,
            engine.window(
                viewport,
                context
            ),
            () -> "window " + viewport
        );
    }

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
}

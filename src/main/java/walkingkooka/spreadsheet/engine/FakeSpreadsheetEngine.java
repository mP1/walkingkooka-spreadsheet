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


import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.spreadsheet.SpreadsheetRow;
import walkingkooka.spreadsheet.SpreadsheetViewportRectangle;
import walkingkooka.spreadsheet.SpreadsheetViewportWindows;
import walkingkooka.spreadsheet.compare.SpreadsheetColumnOrRowSpreadsheetComparatorNames;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReferencePath;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.reference.SpreadsheetViewport;
import walkingkooka.test.Fake;
import walkingkooka.tree.expression.Expression;
import walkingkooka.validation.ValidationValueTypeName;
import walkingkooka.validation.form.Form;
import walkingkooka.validation.form.FormName;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class FakeSpreadsheetEngine implements SpreadsheetEngine, Fake {

    @Override
    public SpreadsheetDelta loadCells(final SpreadsheetSelection selection,
                                      final SpreadsheetEngineEvaluation evaluation,
                                      final Set<SpreadsheetDeltaProperties> deltaProperties,
                                      final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta loadMultipleCellRanges(final Set<SpreadsheetCellRangeReference> cellRanges,
                                                   final SpreadsheetEngineEvaluation evaluation,
                                                   final Set<SpreadsheetDeltaProperties> deltaProperties,
                                                   final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta saveCell(final SpreadsheetCell cell,
                                     final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta saveCells(final Set<SpreadsheetCell> cells,
                                      final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta deleteCells(final SpreadsheetSelection cells,
                                        final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta fillCells(final Collection<SpreadsheetCell> cells,
                                      final SpreadsheetCellRangeReference from,
                                      final SpreadsheetCellRangeReference to,
                                      final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<SpreadsheetCell> filterCells(final Set<SpreadsheetCell> cells,
                                            final ValidationValueTypeName valueType,
                                            final Expression expression,
                                            final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta findCells(final SpreadsheetCellRangeReference cellRange,
                                      final SpreadsheetCellRangeReferencePath path,
                                      final int offset,
                                      final int count,
                                      final ValidationValueTypeName valueType,
                                      final Expression expression,
                                      final Set<SpreadsheetDeltaProperties> deltaProperties,
                                      final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta sortCells(final SpreadsheetCellRangeReference cellRange,
                                      final List<SpreadsheetColumnOrRowSpreadsheetComparatorNames> comparatorNames,
                                      final Set<SpreadsheetDeltaProperties> deltaProperties,
                                      final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta findFormulaReferences(final SpreadsheetCellReference cell,
                                                  final int offset,
                                                  final int count,
                                                  final Set<SpreadsheetDeltaProperties> properties,
                                                  final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta loadColumn(final SpreadsheetColumnReference column,
                                       final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta saveColumn(final SpreadsheetColumn column,
                                       final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta deleteColumns(final SpreadsheetColumnReference column,
                                          final int count,
                                          final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta loadRow(final SpreadsheetRowReference row,
                                    final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta saveRow(final SpreadsheetRow row,
                                    final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta deleteRows(final SpreadsheetRowReference row,
                                       final int count,
                                       final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta insertColumns(final SpreadsheetColumnReference column,
                                          final int count,
                                          final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta insertRows(final SpreadsheetRowReference row,
                                       final int count,
                                       final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta loadForm(final FormName name,
                                     final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta saveForm(final Form<SpreadsheetExpressionReference> form,
                                     final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta deleteForm(final FormName form,
                                       final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta loadForms(final int offset,
                                      final int count,
                                      final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta prepareForm(final FormName name,
                                        final SpreadsheetExpressionReference selection,
                                        final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta submitForm(final Form<SpreadsheetExpressionReference> form,
                                       final SpreadsheetExpressionReference selection,
                                       final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta saveLabel(final SpreadsheetLabelMapping mapping,
                                      final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta deleteLabel(final SpreadsheetLabelName label,
                                        final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta loadLabel(final SpreadsheetLabelName name,
                                      final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta loadLabels(final int offset,
                                       final int count,
                                       final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta findLabelsByName(final String text,
                                             final int offset,
                                             final int count,
                                             final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta findLabelsWithReference(final SpreadsheetExpressionReference reference,
                                                    final int offset,
                                                    final int count,
                                                    final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta findCellsWithReference(final SpreadsheetExpressionReference reference,
                                                   final int offset,
                                                   final int count,
                                                   final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double columnWidth(final SpreadsheetColumnReference column,
                              final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double rowHeight(final SpreadsheetRowReference row,
                            final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int columnCount(final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int rowCount(final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetViewportWindows window(final SpreadsheetViewportRectangle viewportRectangle,
                                             final boolean includeFrozenColumnsRows,
                                             final Optional<SpreadsheetSelection> selection,
                                             final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetViewport> navigate(final SpreadsheetViewport viewport,
                                                  final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }
}

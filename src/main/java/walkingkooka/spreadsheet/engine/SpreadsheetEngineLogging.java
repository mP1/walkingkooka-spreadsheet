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

import walkingkooka.spreadsheet.compare.provider.SpreadsheetColumnOrRowSpreadsheetComparatorNames;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReferencePath;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.validation.SpreadsheetValidationReference;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.spreadsheet.value.SpreadsheetColumn;
import walkingkooka.spreadsheet.value.SpreadsheetRow;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewport;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewportWindows;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.expression.Expression;
import walkingkooka.validation.ValueType;
import walkingkooka.validation.form.Form;
import walkingkooka.validation.form.FormName;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

final class SpreadsheetEngineLogging implements SpreadsheetEngine {

    static SpreadsheetEngineLogging with(final SpreadsheetEngine engine) {
        return engine instanceof SpreadsheetEngineLogging ?
            (SpreadsheetEngineLogging) engine :
            new SpreadsheetEngineLogging(
                Objects.requireNonNull(engine, "engine")
            );
    }

    private SpreadsheetEngineLogging(final SpreadsheetEngine engine) {
        super();
        this.engine = engine;
    }

    @Override
    public Object evaluate(final String expression,
                           final SpreadsheetEngineContext context) {
        return context.logEnterAndExitEngine(
            () -> {
                if (context.isInfoEnabled()) {
                    context.info("evaluate " + CharSequences.quoteIfChars(expression));
                }

                final Object value = this.engine.evaluate(
                    expression,
                    context
                );
                if (context.isInfoEnabled()) {
                    context.info("evaluate " + CharSequences.quoteIfChars(expression) + " > " + CharSequences.quoteIfChars(value));
                }
                return value;
            }
        );
    }

    @Override
    public SpreadsheetDelta loadCells(final SpreadsheetSelection selection,
                                      final SpreadsheetEngineEvaluation evaluation,
                                      final Set<SpreadsheetDeltaProperties> deltaProperties,
                                      final SpreadsheetEngineContext context) {
        return context.logEnterAndExitEngine(
            () -> {
                if (context.isInfoEnabled()) {
                    context.info("loadCells " + selection + " " + evaluation + " " + deltaProperties);
                }

                final SpreadsheetDelta value = this.engine.loadCells(
                    selection,
                    evaluation,
                    deltaProperties,
                    context
                );
                if (context.isInfoEnabled()) {
                    context.info("loadCells " + selection + " " + evaluation + " " + deltaProperties + " > " + value);
                }
                return value;
            }
        );
    }

    @Override
    public SpreadsheetDelta loadMultipleCellRanges(final Set<SpreadsheetCellRangeReference> cellRanges,
                                                   final SpreadsheetEngineEvaluation evaluation,
                                                   final Set<SpreadsheetDeltaProperties> deltaProperties, SpreadsheetEngineContext context) {
        return context.logEnterAndExitEngine(
            () -> {
                if (context.isInfoEnabled()) {
                    context.info("loadMultipleCellRanges: " + cellRanges + " " + evaluation + " " + deltaProperties);
                }

                final SpreadsheetDelta value = this.engine.loadMultipleCellRanges(
                    cellRanges,
                    evaluation,
                    deltaProperties,
                    context
                );
                if (context.isInfoEnabled()) {
                    context.info("loadMultipleCellRanges: " + cellRanges + "  " + evaluation + " " + deltaProperties + " > " + value);
                }
                return value;
            }
        );
    }

    @Override
    public SpreadsheetDelta saveCell(final SpreadsheetCell cell,
                                     final SpreadsheetEngineContext context) {
        return context.logEnterAndExitEngine(
            () -> {
                if (context.isInfoEnabled()) {
                    context.info("saveCell: " + cell);
                }

                final SpreadsheetDelta value = this.engine.saveCell(
                    cell,
                    context
                );
                if (context.isInfoEnabled()) {
                    context.info("saveCell: " + cell + " > " + value);
                }
                return value;
            }
        );
    }

    @Override
    public SpreadsheetDelta saveCells(final Set<SpreadsheetCell> cells,
                                      final SpreadsheetEngineContext context) {
        return context.logEnterAndExitEngine(
            () -> {
                if (context.isInfoEnabled()) {
                    context.info("saveCells " + cells);
                }

                final SpreadsheetDelta value = this.engine.saveCells(
                    cells,
                    context
                );
                if (context.isInfoEnabled()) {
                    context.info("saveCells " + cells + " > " + value);
                }
                return value;
            }
        );
    }

    @Override
    public SpreadsheetDelta deleteCells(final SpreadsheetSelection cells,
                                        final SpreadsheetEngineContext context) {
        return context.logEnterAndExitEngine(
            () -> {
                if (context.isInfoEnabled()) {
                    context.info("deleteCells " + cells);
                }

                final SpreadsheetDelta value = this.engine.deleteCells(
                    cells,
                    context
                );
                if (context.isInfoEnabled()) {
                    context.info("deleteCells " + cells + " > " + value);
                }
                return value;
            }
        );
    }

    @Override
    public SpreadsheetDelta fillCells(final Collection<SpreadsheetCell> cells,
                                      final SpreadsheetCellRangeReference from,
                                      final SpreadsheetCellRangeReference to,
                                      final SpreadsheetEngineContext context) {
        return context.logEnterAndExitEngine(
            () -> {
                if (context.isInfoEnabled()) {
                    context.info("fillCells " + cells + " from: " + from + " to: " + to);
                }

                final SpreadsheetDelta value = this.engine.fillCells(
                    cells,
                    from,
                    to,
                    context
                );
                if (context.isInfoEnabled()) {
                    context.info("fillCells " + cells + " from: " + from + " to: " + to + " > " + value);
                }
                return value;
            }
        );
    }

    @Override
    public Set<SpreadsheetCell> filterCells(final Set<SpreadsheetCell> cells,
                                            final ValueType valueType,
                                            final Expression expression,
                                            final SpreadsheetEngineContext context) {
        return context.logEnterAndExitEngine(
            () -> {
                if (context.isInfoEnabled()) {
                    context.info("filterCells " + cells + " " + valueType + " " + expression);
                }

                final Set<SpreadsheetCell> value = this.engine.filterCells(
                    cells,
                    valueType,
                    expression,
                    context
                );
                if (context.isInfoEnabled()) {
                    context.info("filterCells " + cells + " " + valueType + " " + expression + " > " + value);
                }
                return value;
            }
        );
    }

    @Override
    public SpreadsheetDelta findCellsWithReference(final SpreadsheetExpressionReference reference,
                                                   final int offset,
                                                   final int count,
                                                   final SpreadsheetEngineContext context) {
        return context.logEnterAndExitEngine(
            () -> {
                if (context.isInfoEnabled()) {
                    context.info("findCellsWithReference " + reference + " offset: " + offset + " count: " + count);
                }

                final SpreadsheetDelta value = this.engine.findCellsWithReference(
                    reference,
                    offset,
                    count,
                    context
                );
                if (context.isInfoEnabled()) {
                    context.info("findCellsWithReference " + reference + " offset: " + offset + " count: " + count + " > " + value);
                }
                return value;
            }
        );
    }

    @Override
    public SpreadsheetDelta findFormulaReferences(final SpreadsheetCellReference cell,
                                                  final int offset,
                                                  final int count,
                                                  final Set<SpreadsheetDeltaProperties> properties,
                                                  final SpreadsheetEngineContext context) {
        return context.logEnterAndExitEngine(
            () -> {
                if (context.isInfoEnabled()) {
                    context.info("findFormulaReferences " + cell + " offset: " + offset + " count: " + count + " " + properties);
                }

                final SpreadsheetDelta value = this.engine.findFormulaReferences(
                    cell,
                    offset,
                    count,
                    properties,
                    context
                );
                if (context.isInfoEnabled()) {
                    context.info("findFormulaReferences " + cell + " offset: " + offset + " count: " + count + " " + properties + " > " + value);
                }
                return value;
            }
        );
    }

    @Override
    public SpreadsheetDelta queryCells(final SpreadsheetCellRangeReference cellRange,
                                       final SpreadsheetCellRangeReferencePath path,
                                       final int offset,
                                       final int count,
                                       final ValueType valueType,
                                       final Expression expression,
                                       final Set<SpreadsheetDeltaProperties> deltaProperties,
                                       final SpreadsheetEngineContext context) {
        return context.logEnterAndExitEngine(
            () -> {
                if (context.isInfoEnabled()) {
                    context.info("queryCells " + cellRange + " offset: " + offset + " count: " + count + " " + valueType + " " + expression + " " + deltaProperties);
                }

                final SpreadsheetDelta value = this.engine.queryCells(
                    cellRange,
                    path,
                    offset,
                    count,
                    valueType,
                    expression,
                    deltaProperties,
                    context
                );
                if (context.isInfoEnabled()) {
                    context.info("queryCells " + cellRange + " offset: " + offset + " count: " + count + " " + valueType + " " + expression + " " + deltaProperties + " > " + value);
                }
                return value;
            }
        );
    }

    @Override
    public SpreadsheetDelta sortCells(final SpreadsheetCellRangeReference cellRange,
                                      final List<SpreadsheetColumnOrRowSpreadsheetComparatorNames> comparatorNames,
                                      final Set<SpreadsheetDeltaProperties> deltaProperties,
                                      final SpreadsheetEngineContext context) {
        return context.logEnterAndExitEngine(
            () -> {
                if (context.isInfoEnabled()) {
                    context.info("sortCells " + cellRange + " comparatorNames: " + comparatorNames + " " + deltaProperties);
                }

                final SpreadsheetDelta value = this.engine.sortCells(
                    cellRange,
                    comparatorNames,
                    deltaProperties,
                    context
                );
                if (context.isInfoEnabled()) {
                    context.info("sortCells " + cellRange + " comparatorNames: " + comparatorNames + " " + deltaProperties + " > " + value);
                }
                return value;
            }
        );
    }

    @Override
    public SpreadsheetDelta loadColumn(final SpreadsheetColumnReference column,
                                       final SpreadsheetEngineContext context) {
        return context.logEnterAndExitEngine(
            () -> {
                if (context.isInfoEnabled()) {
                    context.info("loadColumn " + column);
                }

                final SpreadsheetDelta value = this.engine.loadColumn(
                    column,
                    context
                );
                if (context.isInfoEnabled()) {
                    context.info("loadColumn " + column + " > " + value);
                }
                return value;
            }
        );
    }

    @Override
    public SpreadsheetDelta saveColumn(final SpreadsheetColumn column,
                                       final SpreadsheetEngineContext context) {
        return context.logEnterAndExitEngine(
            () -> {
                if (context.isInfoEnabled()) {
                    context.info("saveColumn " + column);
                }

                final SpreadsheetDelta value = this.engine.saveColumn(
                    column,
                    context
                );
                if (context.isInfoEnabled()) {
                    context.info("saveColumn " + column + " > " + value);
                }
                return value;
            }
        );
    }

    @Override
    public SpreadsheetDelta deleteColumns(final SpreadsheetColumnReference column,
                                          final int count,
                                          final SpreadsheetEngineContext context) {
        return context.logEnterAndExitEngine(
            () -> {
                if (context.isInfoEnabled()) {
                    context.info("deleteColumns " + column + " count: " + count);
                }

                final SpreadsheetDelta value = this.engine.deleteColumns(
                    column,
                    count,
                    context
                );
                if (context.isInfoEnabled()) {
                    context.info("deleteColumns " + column + " count: " + count + " > " + value);
                }
                return value;
            }
        );
    }

    @Override
    public SpreadsheetDelta loadRow(final SpreadsheetRowReference row,
                                    final SpreadsheetEngineContext context) {
        return context.logEnterAndExitEngine(
            () -> {
                if (context.isInfoEnabled()) {
                    context.info("loadRow " + row);
                }

                final SpreadsheetDelta value = this.engine.loadRow(
                    row,
                    context
                );
                if (context.isInfoEnabled()) {
                    context.info("loadRow " + row + " > " + value);
                }
                return value;
            }
        );
    }

    @Override
    public SpreadsheetDelta saveRow(final SpreadsheetRow row,
                                    final SpreadsheetEngineContext context) {
        return context.logEnterAndExitEngine(
            () -> {
                if (context.isInfoEnabled()) {
                    context.info("saveRow " + row);
                }

                final SpreadsheetDelta value = this.engine.saveRow(
                    row,
                    context
                );
                if (context.isInfoEnabled()) {
                    context.info("saveRow " + row + " > " + value);
                }
                return value;
            }
        );
    }

    @Override
    public SpreadsheetDelta deleteRows(final SpreadsheetRowReference row,
                                       final int count,
                                       final SpreadsheetEngineContext context) {
        return context.logEnterAndExitEngine(
            () -> {
                if (context.isInfoEnabled()) {
                    context.info("deleteRows " + row + " count: " + count);
                }

                final SpreadsheetDelta value = this.engine.deleteRows(
                    row,
                    count,
                    context
                );
                if (context.isInfoEnabled()) {
                    context.info("deleteRows " + row + " count: " + count + " > " + value);
                }
                return value;
            }
        );
    }

    @Override
    public SpreadsheetDelta insertColumns(final SpreadsheetColumnReference column,
                                          final int count,
                                          final SpreadsheetEngineContext context) {
        return context.logEnterAndExitEngine(
            () -> {
                if (context.isInfoEnabled()) {
                    context.info("insertColumns " + column + " count: " + count);
                }

                final SpreadsheetDelta value = this.engine.insertColumns(
                    column,
                    count,
                    context
                );
                if (context.isInfoEnabled()) {
                    context.info("insertColumns " + column + " count: " + count + " > " + value);
                }
                return value;
            }
        );
    }

    @Override
    public SpreadsheetDelta insertRows(final SpreadsheetRowReference row,
                                       final int count,
                                       final SpreadsheetEngineContext context) {
        return context.logEnterAndExitEngine(
            () -> {
                if (context.isInfoEnabled()) {
                    context.info("insertRows " + row + " count: " + count);
                }

                final SpreadsheetDelta value = this.engine.insertRows(
                    row,
                    count,
                    context
                );
                if (context.isInfoEnabled()) {
                    context.info("insertRows " + row + " count: " + count + " > " + value);
                }
                return value;
            }
        );
    }

    @Override
    public SpreadsheetDelta loadForm(final FormName name,
                                     final SpreadsheetEngineContext context) {
        return context.logEnterAndExitEngine(
            () -> {
                if (context.isInfoEnabled()) {
                    context.info("loadForm " + name);
                }

                final SpreadsheetDelta value = this.engine.loadForm(
                    name,
                    context
                );
                if (context.isInfoEnabled()) {
                    context.info("loadForm " + name + " > " + value);
                }
                return value;
            }
        );
    }

    @Override
    public SpreadsheetDelta loadForms(final int offset,
                                      final int count,
                                      final SpreadsheetEngineContext context) {
        return context.logEnterAndExitEngine(
            () -> {
                if (context.isInfoEnabled()) {
                    context.info("loadForms offset: " + offset + " count: " + count);
                }

                final SpreadsheetDelta value = this.engine.loadForms(
                    offset,
                    count,
                    context
                );
                if (context.isInfoEnabled()) {
                    context.info("loadForms offset: " + offset + " count: " + count + " > " + value);
                }
                return value;
            }
        );
    }

    @Override
    public SpreadsheetDelta saveForm(final Form<SpreadsheetValidationReference> form,
                                     final SpreadsheetEngineContext context) {
        return context.logEnterAndExitEngine(
            () -> {
                if (context.isInfoEnabled()) {
                    context.info("saveForm " + form);
                }

                final SpreadsheetDelta value = this.engine.saveForm(
                    form,
                    context
                );
                if (context.isInfoEnabled()) {
                    context.info("saveForm " + form + " > " + value);
                }
                return value;
            }
        );
    }

    @Override
    public SpreadsheetDelta deleteForm(final FormName name,
                                       final SpreadsheetEngineContext context) {
        return context.logEnterAndExitEngine(
            () -> {
                if (context.isInfoEnabled()) {
                    context.info("deleteForm " + name);
                }

                final SpreadsheetDelta value = this.engine.deleteForm(
                    name,
                    context
                );
                if (context.isInfoEnabled()) {
                    context.info("deleteForm " + name + " > " + value);
                }
                return value;
            }
        );
    }

    @Override
    public SpreadsheetDelta prepareForm(final FormName name,
                                        final SpreadsheetExpressionReference selection,
                                        final SpreadsheetEngineContext context) {
        return context.logEnterAndExitEngine(
            () -> {
                if (context.isInfoEnabled()) {
                    context.info("prepareForm " + name + " " + selection);
                }

                final SpreadsheetDelta value = this.engine.prepareForm(
                    name,
                    selection,
                    context
                );
                if (context.isInfoEnabled()) {
                    context.info("prepareForm " + name + " " + selection + " > " + value);
                }
                return value;
            }
        );
    }

    @Override
    public SpreadsheetDelta findFormsByName(final String text,
                                            final int offset,
                                            final int count,
                                            final SpreadsheetEngineContext context) {
        return context.logEnterAndExitEngine(
            () -> {
                if (context.isInfoEnabled()) {
                    context.info("findFormsByName " + CharSequences.quoteAndEscape(text) + " offset: " + offset + " count: " + count);
                }

                final SpreadsheetDelta value = this.engine.findFormsByName(
                    text,
                    offset,
                    count,
                    context
                );
                if (context.isInfoEnabled()) {
                    context.info("findFormsByName " + CharSequences.quoteAndEscape(text) + " offset: " + offset + " count: " + count + " > " + value);
                }
                return value;
            }
        );

    }

    @Override
    public SpreadsheetDelta submitForm(final Form<SpreadsheetValidationReference> form,
                                       final SpreadsheetExpressionReference selection,
                                       final SpreadsheetEngineContext context) {
        return context.logEnterAndExitEngine(
            () -> {
                if (context.isInfoEnabled()) {
                    context.info("submitForm " + form + " " + selection);
                }

                final SpreadsheetDelta value = this.engine.submitForm(
                    form,
                    selection,
                    context
                );
                if (context.isInfoEnabled()) {
                    context.info("submitForm " + form + " " + selection + " > " + value);
                }
                return value;
            }
        );
    }

    @Override
    public SpreadsheetDelta saveLabel(final SpreadsheetLabelMapping mapping,
                                      final SpreadsheetEngineContext context) {
        return context.logEnterAndExitEngine(
            () -> {
                if (context.isInfoEnabled()) {
                    context.info("saveLabel " + mapping);
                }

                final SpreadsheetDelta value = this.engine.saveLabel(
                    mapping,
                    context
                );
                if (context.isInfoEnabled()) {
                    context.info("saveLabel " + mapping + " > " + value);
                }
                return value;
            }
        );
    }

    @Override
    public SpreadsheetDelta deleteLabel(final SpreadsheetLabelName label,
                                        final SpreadsheetEngineContext context) {
        return context.logEnterAndExitEngine(
            () -> {
                if (context.isInfoEnabled()) {
                    context.info("deleteLabel " + label);
                }

                final SpreadsheetDelta value = this.engine.deleteLabel(
                    label,
                    context
                );
                if (context.isInfoEnabled()) {
                    context.info("deleteLabel " + label + " > " + value);
                }
                return value;
            }
        );
    }

    @Override
    public SpreadsheetDelta loadLabel(final SpreadsheetLabelName name,
                                      final SpreadsheetEngineContext context) {
        return context.logEnterAndExitEngine(
            () -> {
                if (context.isInfoEnabled()) {
                    context.info("loadLabel " + name);
                }

                final SpreadsheetDelta value = this.engine.loadLabel(
                    name,
                    context
                );
                if (context.isInfoEnabled()) {
                    context.info("loadLabel " + name + " > " + value);
                }
                return value;
            }
        );
    }

    @Override
    public SpreadsheetDelta loadLabels(final int offset,
                                       final int count,
                                       final SpreadsheetEngineContext context) {
        return context.logEnterAndExitEngine(
            () -> {
                if (context.isInfoEnabled()) {
                    context.info("loadLabels offset: " + offset + " count: " + count);
                }

                final SpreadsheetDelta value = this.engine.loadLabels(
                    offset,
                    count,
                    context
                );
                if (context.isInfoEnabled()) {
                    context.info("loadLabels offset: " + offset + " count: " + count + " > " + value);
                }
                return value;
            }
        );
    }

    @Override
    public SpreadsheetDelta findLabelsByName(final String text,
                                             final int offset,
                                             final int count,
                                             final SpreadsheetEngineContext context) {
        return context.logEnterAndExitEngine(
            () -> {
                if (context.isInfoEnabled()) {
                    context.info("findLabelsByName " + CharSequences.quoteAndEscape(text) + " offset: " + offset + " count: " + count);
                }

                final SpreadsheetDelta value = this.engine.findLabelsByName(
                    text,
                    offset,
                    count,
                    context
                );
                if (context.isInfoEnabled()) {
                    context.info("findLabelsByName " + CharSequences.quoteAndEscape(text) + " offset: " + offset + " count: " + count + " > " + value);
                }
                return value;
            }
        );
    }

    @Override
    public SpreadsheetDelta findLabelsWithReference(final SpreadsheetExpressionReference reference,
                                                    final int offset,
                                                    final int count,
                                                    final SpreadsheetEngineContext context) {
        return context.logEnterAndExitEngine(
            () -> {
                if (context.isInfoEnabled()) {
                    context.info("findLabelsWithReference " + reference + " offset: " + offset + " count: " + count);
                }

                final SpreadsheetDelta value = this.engine.findLabelsWithReference(
                    reference,
                    offset,
                    count,
                    context
                );
                if (context.isInfoEnabled()) {
                    context.info("findLabelsWithReference " + reference + " offset: " + offset + " count: " + count + " > " + value);
                }
                return value;
            }
        );
    }

    @Override
    public double columnWidth(final SpreadsheetColumnReference column,
                              final SpreadsheetEngineContext context) {
        return context.logEnterAndExitEngine(
            () -> {
                if (context.isInfoEnabled()) {
                    context.info("columnWidth " + column);
                }

                final double value = this.engine.columnWidth(
                    column,
                    context
                );
                if (context.isInfoEnabled()) {
                    context.info("columnWidth " + column + " > " + value);
                }
                return value;
            }
        );
    }

    @Override
    public double rowHeight(final SpreadsheetRowReference row,
                            final SpreadsheetEngineContext context) {
        return context.logEnterAndExitEngine(
            () -> {
                if (context.isInfoEnabled()) {
                    context.info("rowHeight " + row);
                }

                final double value = this.engine.rowHeight(
                    row,
                    context
                );
                if (context.isInfoEnabled()) {
                    context.info("rowHeight " + row + " > " + value);
                }
                return value;
            }
        );
    }

    @Override
    public int columnCount(final SpreadsheetEngineContext context) {
        return context.logEnterAndExitEngine(
            () -> {
                if (context.isInfoEnabled()) {
                    context.info("columnCount");
                }

                final int value = this.engine.columnCount(
                    context
                );
                if (context.isInfoEnabled()) {
                    context.info("columnCount > " + value);
                }
                return value;
            }
        );
    }

    @Override
    public int rowCount(final SpreadsheetEngineContext context) {
        return context.logEnterAndExitEngine(
            () -> {
                if (context.isInfoEnabled()) {
                    context.info("rowCount");
                }

                final int value = this.engine.rowCount(
                    context
                );
                if (context.isInfoEnabled()) {
                    context.info("rowCount > " + value);
                }
                return value;
            }
        );
    }

    @Override
    public SpreadsheetViewportWindows window(final SpreadsheetViewport viewport,
                                             final SpreadsheetEngineContext context) {
        return context.logEnterAndExitEngine(
            () -> {
                if (context.isInfoEnabled()) {
                    context.info("viewport " + viewport);
                }

                final SpreadsheetViewportWindows value = this.engine.window(
                    viewport,
                    context
                );
                if (context.isInfoEnabled()) {
                    context.info("viewport " + viewport + " > " + value);
                }
                return value;
            }
        );
    }

    @Override
    public Optional<SpreadsheetViewport> navigate(final SpreadsheetViewport viewport,
                                                  final SpreadsheetEngineContext context) {
        return context.logEnterAndExitEngine(
            () -> {
                if (context.isInfoEnabled()) {
                    context.info("navigate " + viewport);
                }

                final Optional<SpreadsheetViewport> value = this.engine.navigate(
                    viewport,
                    context
                );
                if (context.isInfoEnabled()) {
                    context.info("navigate " + viewport + " > " + value);
                }
                return value;
            }
        );
    }

    private final SpreadsheetEngine engine;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.engine.toString();
    }
}

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
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.reference.SpreadsheetViewport;
import walkingkooka.tree.expression.Expression;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * The engine or host for the active spreadsheet.
 */
public interface SpreadsheetEngine {

    /**
     * Loads the requested {@link SpreadsheetColumnReference} which may include parsing the formula as necessary and then
     * evaluating the value of the requested cells.<br>
     * <ul>
     * <li>If the cell is not found it will not be present in the resulting {@link SpreadsheetDelta}.</li>
     * <li>If parsing or evaluating the cell fails it will be present with an error.</li>
     * <li>Any additional cells that require recomputing because of the requested cell will also be returned.</li>
     * <li>If the cell was not found and labels are requested {@link SpreadsheetDeltaProperties#LABELS} any labels will be returned.</li>
     * </ul>
     */
    SpreadsheetDelta loadCells(final SpreadsheetSelection selection,
                               final SpreadsheetEngineEvaluation evaluation,
                               final Set<SpreadsheetDeltaProperties> deltaProperties,
                               final SpreadsheetEngineContext context);

    /**
     * Loads a range of cells. This is useful to fill a range that fills the viewportRectangle.
     */
    SpreadsheetDelta loadCells(final Set<SpreadsheetCellRangeReference> cellRanges,
                               final SpreadsheetEngineEvaluation evaluation,
                               final Set<SpreadsheetDeltaProperties> deltaProperties,
                               final SpreadsheetEngineContext context);

    /**
     * Saves the cell, and updates all affected (referenced cells) returning all updated cells.
     */
    SpreadsheetDelta saveCell(final SpreadsheetCell cell,
                              final SpreadsheetEngineContext context);

    /**
     * Saves All the cell, and updates all affected (referenced cells) returning all updated cells.
     */
    SpreadsheetDelta saveCells(final Set<SpreadsheetCell> cells,
                               final SpreadsheetEngineContext context);

    /**
     * Deletes the cellS, removing references and updates and returns all affected (referenced cells).
     */
    SpreadsheetDelta deleteCells(final SpreadsheetSelection cells,
                                 final SpreadsheetEngineContext context);

    /**
     * Fill may be used to perform several operations.
     * <ul>
     * <li>If $cells are empty the $parse is ignored and the {@link SpreadsheetCellRangeReference $to} has all cells deleted, aka DELETE</li>
     * <li>If $cells is NOT empty and $parse and $to are equal the cells are saved without modification, aka SAVE</li>
     * <li>If $cells is NOT empty and $parse is smaller than $to cells are repeated and ABSOLUTE references updated aka FILL or COPY then PASTE</li>
     * </ul>
     */
    SpreadsheetDelta fillCells(final Collection<SpreadsheetCell> cells,
                               final SpreadsheetCellRangeReference from,
                               final SpreadsheetCellRangeReference to,
                               final SpreadsheetEngineContext context);

    /**
     * Executes the {@link Expression} for each and every {@link SpreadsheetCell} as the current cell. Cells are only
     * kept if the {@link Expression} returns true.
     */
    Set<SpreadsheetCell> filterCells(final Set<SpreadsheetCell> cells,
                                     final String valueType,
                                     final Expression expression,
                                     final SpreadsheetEngineContext context);

    /**
     * Returns the first count of {@link SpreadsheetCell} that match the given {@link String valueType} filtered by
     * the given {@link Expression}.
     */
    Set<SpreadsheetCell> findCells(final SpreadsheetCellRangeReference cellRange,
                                   final SpreadsheetCellRangeReferencePath path,
                                   final int offset,
                                   final int max,
                                   final String valueType,
                                   final Expression expression,
                                   final SpreadsheetEngineContext context);

    /**
     * Sorts the selection of cells using the provided {@link walkingkooka.spreadsheet.compare.SpreadsheetComparator comparators}.
     */
    SpreadsheetDelta sortCells(final SpreadsheetCellRangeReference cellRange,
                               final List<SpreadsheetColumnOrRowSpreadsheetComparatorNames> comparatorNames,
                               final Set<SpreadsheetDeltaProperties> deltaProperties,
                               final SpreadsheetEngineContext context);

    /**
     * Loads the given {@link SpreadsheetColumn}
     */
    SpreadsheetDelta loadColumn(final SpreadsheetColumnReference column,
                                final SpreadsheetEngineContext context);

    /**
     * Saves the {@link SpreadsheetColumn}, and updates all affected (referenced cells).
     */
    SpreadsheetDelta saveColumn(final SpreadsheetColumn column,
                                final SpreadsheetEngineContext context);

    /**
     * Deletes the identified columns, updates all absolute references as necessary in both formulas and label mappings.
     */
    SpreadsheetDelta deleteColumns(final SpreadsheetColumnReference column,
                                   final int count,
                                   final SpreadsheetEngineContext context);

    /**
     * Loads the given {@link SpreadsheetRow}
     */
    SpreadsheetDelta loadRow(final SpreadsheetRowReference row,
                             final SpreadsheetEngineContext context);

    /**
     * Saves the {@link SpreadsheetRow}, and updates all affected (referenced cells).
     */
    SpreadsheetDelta saveRow(final SpreadsheetRow row,
                             final SpreadsheetEngineContext context);

    /**
     * Deletes the identified rows, updates all absolute references as necessary in both formulas and label mappings.
     */
    SpreadsheetDelta deleteRows(final SpreadsheetRowReference row,
                                final int count,
                                final SpreadsheetEngineContext context);

    /**
     * Inserts the identified columns, updates all absolute references as necessary in both formulas and label mappings.
     */
    SpreadsheetDelta insertColumns(final SpreadsheetColumnReference column,
                                   final int count,
                                   final SpreadsheetEngineContext context);

    /**
     * Inserts the identified rows, updates all absolute references as necessary in both formulas and label mappings.
     */
    SpreadsheetDelta insertRows(final SpreadsheetRowReference row,
                                final int count,
                                final SpreadsheetEngineContext context);

    /**
     * Sets a new label mapping or replaces an existing one returning a {@link SpreadsheetDelta} which may or may not
     * have affected and updated cells.
     */
    SpreadsheetDelta saveLabel(final SpreadsheetLabelMapping mapping,
                               final SpreadsheetEngineContext context);

    /**
     * Removes the given {@link SpreadsheetLabelName} if it exists which may also cause cells to be updated, due a now missing label.
     */
    SpreadsheetDelta deleteLabel(final SpreadsheetLabelName label,
                                 final SpreadsheetEngineContext context);

    /**
     * Loads the given label if it exists.
     */
    Optional<SpreadsheetLabelMapping> loadLabel(final SpreadsheetLabelName name,
                                                final SpreadsheetEngineContext context);

    /**
     * Returns the column width for the given {@link SpreadsheetColumnReference}, if none is present,
     * defaulting to {@link walkingkooka.tree.text.TextStylePropertyName#WIDTH} parse the {@link walkingkooka.spreadsheet.meta.SpreadsheetMetadata}.
     * If the column is hidden this will return 0.
     */
    double columnWidth(final SpreadsheetColumnReference column,
                       final SpreadsheetEngineContext context);

    /**
     * Returns the row height for the given {@link SpreadsheetRowReference}, if none is present,
     * defaulting to {@link walkingkooka.tree.text.TextStylePropertyName#HEIGHT} parse the {@link walkingkooka.spreadsheet.meta.SpreadsheetMetadata}.
     * If the row is hidden this will return 0.
     */
    double rowHeight(final SpreadsheetRowReference row,
                     final SpreadsheetEngineContext context);

    /**
     * Returns the number of columns in this spreadsheet or 0 when empty.
     */
    int columnCount(final SpreadsheetEngineContext context);

    /**
     * Returns the number of rows in this spreadsheet or 0 when empty.
     */
    int rowCount(final SpreadsheetEngineContext context);

    /**
     * An absent {@link SpreadsheetSelection}.
     */
    Optional<SpreadsheetSelection> NO_SELECTION = Optional.empty();

    /**
     * Translates the {@link SpreadsheetViewportRectangle} into the actual {@link SpreadsheetViewportWindows} of cells that occupy that space.
     * The combination of parameters make it possible to load the range of cells that occupy the selected range and
     * automatically pan across as necessary to include the provided {@link SpreadsheetSelection}.
     * The {@link SpreadsheetSelection} must contain a single
     * <ul>
     *     <li>{@link SpreadsheetCellReference}</li>
     *     <li>{@link SpreadsheetColumnReference}</li>
     *     <li>{@link SpreadsheetRowReference}</li>
     * </ul>
     * If the selection is a {@link SpreadsheetLabelName} it will be resolved into a non label before continuing.
     */
    SpreadsheetViewportWindows window(final SpreadsheetViewportRectangle viewportRectangle,
                                      final boolean includeFrozenColumnsRows,
                                      final Optional<SpreadsheetSelection> selection,
                                      final SpreadsheetEngineContext context);

    /**
     * Performs the given {@link SpreadsheetViewport}, honouring any present {@link SpreadsheetViewport#navigations()},
     * skipping hidden columns and rows. Note the enclosing {@link walkingkooka.spreadsheet.meta.SpreadsheetMetadata} is NOT
     * updated.
     */
    Optional<SpreadsheetViewport> navigate(final SpreadsheetViewport viewport,
                                           final SpreadsheetEngineContext context);
}

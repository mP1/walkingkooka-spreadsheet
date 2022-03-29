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
import walkingkooka.spreadsheet.SpreadsheetViewport;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRange;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.reference.SpreadsheetViewportSelection;

import java.util.Collection;
import java.util.Optional;

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
     * </ul>
     */
    SpreadsheetDelta loadCell(final SpreadsheetCellReference cell,
                              final SpreadsheetEngineEvaluation evaluation,
                              final SpreadsheetEngineContext context);

    /**
     * Saves the cell, and updates all affected (referenced cells) returning all updated cells.
     */
    SpreadsheetDelta saveCell(final SpreadsheetCell cell,
                              final SpreadsheetEngineContext context);

    /**
     * Deletes the cell, removing references and updates and returns all affected (referenced cells).
     */
    SpreadsheetDelta deleteCell(final SpreadsheetCellReference cell,
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
     * Loads a range of cells. Thsi is useful to fill a range that fills the viewport.
     */
    SpreadsheetDelta loadCells(final SpreadsheetCellRange range,
                               final SpreadsheetEngineEvaluation evaluation,
                               final SpreadsheetEngineContext context);

    /**
     * Fill may be used to perform several operations.
     * <ul>
     * <li>If $cells are empty the $from is ignored and the {@link SpreadsheetCellRange $to} has all cells deleted, aka DELETE</li>
     * <li>If $cells is NOT empty and $from and $to are equal the cells are saved without modification, aka SAVE</li>
     * <li>If $cells is NOT empty and $from is smaller than $to cells are repeated and ABSOLUTE references updated aka FILL or COPY then PASTE</li>
     * </ul>
     */
    SpreadsheetDelta fillCells(final Collection<SpreadsheetCell> cells,
                               final SpreadsheetCellRange from,
                               final SpreadsheetCellRange to,
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
    SpreadsheetDelta removeLabel(final SpreadsheetLabelName label,
                                 final SpreadsheetEngineContext context);

    /**
     * Loads the given label if it exists.
     */
    Optional<SpreadsheetLabelMapping> loadLabel(final SpreadsheetLabelName name,
                                                final SpreadsheetEngineContext context);

    /**
     * Returns the column width for the given {@link SpreadsheetColumnReference}.
     * If the column is hidden this will return 0.
     */
    double columnWidth(final SpreadsheetColumnReference column,
                       final SpreadsheetEngineContext context);

    /**
     * Returns the row height for the given {@link SpreadsheetRowReference}.
     * If the row is hidden this will return 0.
     */
    double rowHeight(final SpreadsheetRowReference row,
                     final SpreadsheetEngineContext context);

    /**
     * An absent {@link SpreadsheetSelection}.
     */
    Optional<SpreadsheetSelection> NO_SELECTION = Optional.empty();

    /**
     * Translates the {@link SpreadsheetViewport} into the actual {@link SpreadsheetCellRange} of cells that occupy that space.
     * The combination of parameters make it possible to load the range of cells that occupy the selected range and
     * automatically pan across as necessary to include the provided {@link SpreadsheetSelection}.
     */
    SpreadsheetCellRange range(final SpreadsheetViewport viewport,
                               final Optional<SpreadsheetSelection> selection,
                               final SpreadsheetEngineContext context);

    /**
     * An absent {@link SpreadsheetViewportSelection}.
     */
    Optional<SpreadsheetViewportSelection> NO_VIEWPORT_SELECTION = Optional.empty();

    /**
     * Performs the given {@link SpreadsheetViewportSelection}, honouring any present {@link SpreadsheetViewportSelection#navigation()},
     * skipping hidden columns and rows. If no {@link SpreadsheetViewportSelection#navigation()} is present, the
     * available {@link SpreadsheetViewportSelection#selection()} if hidden is replaced with {@link #NO_VIEWPORT_SELECTION}.
     */
    Optional<SpreadsheetViewportSelection> navigate(final SpreadsheetViewportSelection selection,
                                                    final SpreadsheetEngineContext context);
}

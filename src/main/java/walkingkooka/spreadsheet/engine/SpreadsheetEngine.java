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
import walkingkooka.spreadsheet.SpreadsheetViewport;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRange;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;

import java.util.Collection;
import java.util.Optional;

/**
 * The engine or host for the active spreadsheet.
 */
public interface SpreadsheetEngine {

    /**
     * Loads which includes parsing the formula as necessary and evaluating the value of the requested cells.
     * Invalid cell requests will be ignored and absent fromt he result. If parsing or evaluation fails the cell will have an error.
     * If the cell is absent but has a label mapping the {@link SpreadsheetDelta#cellToLabels()} will contain this mapping.
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
     * Deletes the identified columns, updates all absolute references as necessary in both formulas and label mappings.
     */
    SpreadsheetDelta deleteColumns(final SpreadsheetColumnReference column,
                                   final int count,
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
    SpreadsheetDelta loadCells(final SpreadsheetRange range,
                               final SpreadsheetEngineEvaluation evaluation,
                               final SpreadsheetEngineContext context);

    /**
     * Fill may be used to perform several operations.
     * <ul>
     * <li>If $cells are empty the $from is ignored and the {@link SpreadsheetRange $to} has all cells deleted, aka DELETE</li>
     * <li>If $cells is NOT empty and $from and $to are equal the cells are saved without modification, aka SAVE</li>
     * <li>If $cells is NOT empty and $from is smaller than $to cells are repeated and ABSOLUTE references updated aka FILL or COPY then PASTE</li>
     * </ul>
     */
    SpreadsheetDelta fillCells(final Collection<SpreadsheetCell> cells,
                               final SpreadsheetRange from,
                               final SpreadsheetRange to,
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
     * Returns the column width for the given {@link SpreadsheetColumnReference}
     */
    double columnWidth(final SpreadsheetColumnReference column,
                       final SpreadsheetEngineContext context);

    /**
     * Returns the row height for the given {@link SpreadsheetRowReference}
     */
    double rowHeight(final SpreadsheetRowReference row,
                     final SpreadsheetEngineContext context);


    /**
     * Translates the {@link SpreadsheetViewport} into the actual {@link SpreadsheetRange} of cells that occupy that space.
     */
    SpreadsheetRange range(final SpreadsheetViewport viewport,
                           final SpreadsheetEngineContext context);
}

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

import walkingkooka.compare.Range;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetId;
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
     * The id for this spreadsheet.
     */
    SpreadsheetId id();

    /**
     * Loads which includes parsing the formula as necessary and evaluating the value of the requested cells.
     * Invalid cell requests will be ignored and absent fromthe result. If parsing or evaluation fails the cell will have an error.
     */
    SpreadsheetDelta<Optional<SpreadsheetCellReference>> loadCell(final SpreadsheetCellReference cell,
                                                                  final SpreadsheetEngineEvaluation evaluation,
                                                                  final SpreadsheetEngineContext context);

    /**
     * Saves the cell, and updates all affected (referenced cells) returning all updated cells.
     */
    SpreadsheetDelta<Optional<SpreadsheetCellReference>> saveCell(final SpreadsheetCell cell,
                                                                  final SpreadsheetEngineContext context);

    /**
     * Deletes the cell, removing references and updates and returns all affected (referenced cells).
     */
    SpreadsheetDelta<Optional<SpreadsheetCellReference>> deleteCell(final SpreadsheetCellReference cell,
                                                                    final SpreadsheetEngineContext context);

    /**
     * Deletes the identified columns, updates all absolute references as necessary in both formulas and label mappings.
     */
    SpreadsheetDelta<Range<SpreadsheetColumnReference>> deleteColumns(final SpreadsheetColumnReference column,
                                                                      final int count,
                                                                      final SpreadsheetEngineContext context);

    /**
     * Deletes the identified rows, updates all absolute references as necessary in both formulas and label mappings.
     */
    SpreadsheetDelta<Range<SpreadsheetRowReference>> deleteRows(final SpreadsheetRowReference row,
                                                                final int count,
                                                                final SpreadsheetEngineContext context);

    /**
     * Inserts the identified columns, updates all absolute references as necessary in both formulas and label mappings.
     */
    SpreadsheetDelta<Range<SpreadsheetColumnReference>> insertColumns(final SpreadsheetColumnReference column,
                                                                      final int count,
                                                                      final SpreadsheetEngineContext context);

    /**
     * Inserts the identified rows, updates all absolute references as necessary in both formulas and label mappings.
     */
    SpreadsheetDelta<Range<SpreadsheetRowReference>> insertRows(final SpreadsheetRowReference row,
                                                                final int count,
                                                                final SpreadsheetEngineContext context);

    /**
     * Copies the provided cells into this spreadsheet at the given range. The source range is smaller, where possible
     * it will be duplicated along both axis or repeated into the target range.<br>
     * This is ideal for a copy/paste function, where the pasted range must have all cells relative references fixed.
     * Note prior to copying the area should be cleared, as this only copies the given it doesnt clear the entire range,
     * before the actual copying.
     */
    SpreadsheetDelta<Range<SpreadsheetCellReference>> copyCells(final Collection<SpreadsheetCell> from,
                                                                final SpreadsheetRange to,
                                                                final SpreadsheetEngineContext context);

    /**
     * Sets a new label mapping or replaces an existing one returning a {@link SpreadsheetDelta} which may or may not
     * have affected and updated cells.
     */
    SpreadsheetDelta<Optional<SpreadsheetLabelName>> saveLabel(final SpreadsheetLabelMapping mapping,
                                                               final SpreadsheetEngineContext context);

    /**
     * Removes the given {@link SpreadsheetLabelName} if it exists which may also cause cells to be updated, due a now missing label.
     */
    SpreadsheetDelta<Optional<SpreadsheetLabelName>> removeLabel(final SpreadsheetLabelName label,
                                                                 final SpreadsheetEngineContext context);

    /**
     * Loads the given label if it exists.
     */
    Optional<SpreadsheetLabelMapping> loadLabel(final SpreadsheetLabelName name);
}

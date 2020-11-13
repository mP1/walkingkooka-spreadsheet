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

package walkingkooka.spreadsheet.store;

import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetCellBox;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.store.Store;

import java.util.Set;

/**
 * A store that holds all cells for a spreadsheet. No additional actions are supported.
 */
public interface SpreadsheetCellStore extends Store<SpreadsheetCellReference, SpreadsheetCell> {

    /**
     * The highest row number
     */
    int rows();

    /**
     * The highest column number
     */
    int columns();

    /**
     * Returns a view of all cells in the given row.
     */
    Set<SpreadsheetCell> row(SpreadsheetRowReference row);

    /**
     * Returns a view of all cells in the given column.
     */
    Set<SpreadsheetCell> column(SpreadsheetColumnReference column);

    /**
     * Returns the max column width for the given {@link SpreadsheetColumnReference}
     */
    double maxColumnWidth(final SpreadsheetColumnReference column);

    /**
     * Returns the max row height for the given {@link SpreadsheetRowReference}
     */
    double maxRowHeight(final SpreadsheetRowReference column);

    /**
     * Locates the {@link SpreadsheetCellBox} at the given coordinates.
     */
    SpreadsheetCellBox cellBox(final double x, final double y);
}

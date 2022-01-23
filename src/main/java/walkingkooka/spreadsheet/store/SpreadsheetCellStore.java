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
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.store.SpreadsheetStore;
import walkingkooka.store.Store;

import java.util.Set;

/**
 * A {@link Store} that holds all cells for a spreadsheet. No additional actions are supported.
 * Parameters of type {@link SpreadsheetCellReference}, {@link SpreadsheetColumnReference} or {@link SpreadsheetRowReference}
 * ignore their {@link walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind}, either form should return the same results.
 */
public interface SpreadsheetCellStore extends SpreadsheetStore<SpreadsheetCellReference, SpreadsheetCell> {

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
    Set<SpreadsheetCell> row(final SpreadsheetRowReference row);

    /**
     * Returns a view of all cells in the given column.
     */
    Set<SpreadsheetCell> column(final SpreadsheetColumnReference column);

    /**
     * Returns the max column width for the given {@link SpreadsheetColumnReference}
     */
    double maxColumnWidth(final SpreadsheetColumnReference column);

    /**
     * Returns the max row height for the given {@link SpreadsheetRowReference}
     */
    double maxRowHeight(final SpreadsheetRowReference row);
}

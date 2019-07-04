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

package walkingkooka.spreadsheet.store.cell;

import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetCellReference;
import walkingkooka.spreadsheet.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.SpreadsheetRowReference;
import walkingkooka.spreadsheet.store.SpreadsheetStore;

import java.util.Set;

/**
 * A store that holds all cells for a spreadsheet. No additional actions are supported.
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
    Set<SpreadsheetCell> row(SpreadsheetRowReference row);

    /**
     * Returns a view of all cells in the given column.
     */
    Set<SpreadsheetCell> column(SpreadsheetColumnReference column);
}

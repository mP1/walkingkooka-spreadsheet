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
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRange;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.store.SpreadsheetStore;
import walkingkooka.store.Store;
import walkingkooka.text.CharSequences;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * A {@link Store} that holds all cells for a spreadsheet. No additional actions are supported.
 * Parameters of type {@link SpreadsheetCellReference}, {@link SpreadsheetColumnReference} or {@link SpreadsheetRowReference}
 * ignore their {@link walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind}, either form should return the same results.
 */
public interface SpreadsheetCellStore extends SpreadsheetStore<SpreadsheetCellReference, SpreadsheetCell> {

    /**
     * Attempts to load all the cells in the given {@link SpreadsheetCellRange}.
     */
    default List<SpreadsheetCell> loadCells(final SpreadsheetCellRange range) {
        Objects.requireNonNull(range, "range");

        return this.between(
                range.begin(),
                range.end()
        );
    }

    /**
     * Default implementation that deletes all the cells in the given {@link SpreadsheetCellRange}.
     */
    void deleteCells(final SpreadsheetCellRange range);

    /**
     * Clears the parsed formula for all existing cells.
     */
    default void clearParsedFormulaExpressions() {
        for (final SpreadsheetCell cell : this.all()) {
            final SpreadsheetFormula formulaBefore = cell.formula();
            final SpreadsheetFormula formulaAfter = formulaBefore.setExpression(SpreadsheetFormula.NO_EXPRESSION);
            if (false == formulaBefore.equals(formulaAfter)) {
                this.save(
                        cell.setFormula(formulaAfter)
                );
            }
        }
    }

    /**
     * Clears the formatted output for all existing cells.
     */
    default void clearFormatted() {
        for (final SpreadsheetCell cell : this.all()) {
            final SpreadsheetCell after = cell.setFormatted(SpreadsheetCell.NO_FORMATTED_CELL);
            if (false == cell.equals(after)) {
                this.save(after);
            }
        }
    }

    /**
     * The total number of rows, with 0 returned for none.
     */
    int rowCount();

    /**
     * The total number of columns, with 0 returned for none.
     */
    int columnCount();

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

    static void checkFindCellsWithValueType(final SpreadsheetCellRange range,
                                            final String valueTypeName) {
        Objects.requireNonNull(range, "range");
        CharSequences.failIfNullOrEmpty(valueTypeName, "valueTypeName");
    }

    /**
     * Finds all cells with a value type equal to the given. This means empty cells without a formula will never be found,
     * while those with errors will have a value type of {@link walkingkooka.spreadsheet.SpreadsheetError}.
     */
    Set<SpreadsheetCell> findCellsWithValueType(final SpreadsheetCellRange range,
                                                final String valueTypeName);

    static void checkCountCellsWithValueType(final SpreadsheetCellRange range,
                                             final String valueTypeName) {
        Objects.requireNonNull(range, "range");
        CharSequences.failIfNullOrEmpty(valueTypeName, "valueTypeName");
    }

    /**
     * Counts all cells with a value type equal to the given. This means empty cells without a formula will never be found,
     * while those with errors will have a value type of {@link walkingkooka.spreadsheet.SpreadsheetError}.
     */
    int countCellsWithValueType(final SpreadsheetCellRange range,
                                final String valueTypeName);
}

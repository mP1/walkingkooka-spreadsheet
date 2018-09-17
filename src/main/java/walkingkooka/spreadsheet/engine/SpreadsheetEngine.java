package walkingkooka.spreadsheet.engine;

import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRowReference;

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
    Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference cell, final SpreadsheetEngineLoading loading);

    /**
     * Deletes the identified columns, updates all absolute references as necessary in both formulas and label mappings.
     */
    void deleteColumns(final SpreadsheetColumnReference column, final int count);

    /**
     * Deletes the identified rows, updates all absolute references as necessary in both formulas and label mappings.
     */
    void deleteRows(final SpreadsheetRowReference row, final int count);

    /**
     * Inserts the identified columns, updates all absolute references as necessary in both formulas and label mappings.
     */
    void insertColumns(final SpreadsheetColumnReference column, final int count);

    /**
     * Inserts the identified rows, updates all absolute references as necessary in both formulas and label mappings.
     */
    void insertRows(final SpreadsheetRowReference row, final int count);
}

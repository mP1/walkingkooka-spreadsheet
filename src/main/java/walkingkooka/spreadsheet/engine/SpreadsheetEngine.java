package walkingkooka.spreadsheet.engine;

import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;

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
}

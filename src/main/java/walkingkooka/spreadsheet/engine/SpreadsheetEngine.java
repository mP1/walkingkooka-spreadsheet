package walkingkooka.spreadsheet.engine;

import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.SpreadsheetRange;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRowReference;

import java.util.Collection;
import java.util.List;
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
    Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference cell,
                                       final SpreadsheetEngineLoading loading,
                                       final SpreadsheetEngineContext context);

    /**
     * Deletes the identified columns, updates all absolute references as necessary in both formulas and label mappings.
     */
    void deleteColumns(final SpreadsheetColumnReference column,
                       final int count,
                       final SpreadsheetEngineContext context);

    /**
     * Deletes the identified rows, updates all absolute references as necessary in both formulas and label mappings.
     */
    void deleteRows(final SpreadsheetRowReference row,
                    final int count,
                    final SpreadsheetEngineContext context);

    /**
     * Inserts the identified columns, updates all absolute references as necessary in both formulas and label mappings.
     */
    void insertColumns(final SpreadsheetColumnReference column,
                       final int count,
                       final SpreadsheetEngineContext context);

    /**
     * Inserts the identified rows, updates all absolute references as necessary in both formulas and label mappings.
     */
    void insertRows(final SpreadsheetRowReference row,
                    final int count,
                    final SpreadsheetEngineContext context);

    /**
     * Copies the provided cells into this spreadsheet at the given range. The source range is smaller, where possible
     * it will be duplicated along both axis or repeated into the target range.<br>
     * This is ideal for a copy/paste function, where the pasted range must have all cells relative references fixed.
     * Note prior to copying the area should be cleared, as this only copies the given it doesnt clear the entire range,
     * before the actual copying.
     */
    void copy(final Collection<SpreadsheetCell> from,
              final SpreadsheetRange to,
              final SpreadsheetEngineContext context);
}

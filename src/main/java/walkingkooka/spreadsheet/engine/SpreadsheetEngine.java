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

    /**
     * Sets or replaces a label to reference mapping. It is acceptable to set to an non existing {@link SpreadsheetCellReference}
     */
    void setLabel(final SpreadsheetLabelName label, final SpreadsheetCellReference reference);

    /**
     * Removes the given label if it exists. Invalid or unknown names are ignored.
     */
    void removeLabel(SpreadsheetLabelName label);

    /**
     * Retrieves the {@link SpreadsheetCellReference} if one is present for the given {@link SpreadsheetLabelName}
     */
    Optional<SpreadsheetCellReference> label(final SpreadsheetLabelName label);
}

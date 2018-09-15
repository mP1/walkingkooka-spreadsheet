package walkingkooka.spreadsheet.store.label;

import walkingkooka.spreadsheet.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.store.Store;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;

import java.util.Collection;

/**
 * A store that holds all label to cell references for a spreadsheet. No additional actions are supported.
 */
public interface SpreadsheetLabelStore extends Store<SpreadsheetLabelName, SpreadsheetLabelMapping> {

    /**
     * Returns a view of all mappings in the given row.
     */
    Collection<SpreadsheetLabelMapping> all();
}

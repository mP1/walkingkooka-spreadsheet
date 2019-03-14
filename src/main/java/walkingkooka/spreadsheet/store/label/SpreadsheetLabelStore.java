package walkingkooka.spreadsheet.store.label;

import walkingkooka.spreadsheet.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.store.Store;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.Collection;
import java.util.Set;

/**
 * A store that holds all label to cell references for a spreadsheet. No additional actions are supported.
 */
public interface SpreadsheetLabelStore extends Store<SpreadsheetLabelName, SpreadsheetLabelMapping> {

    /**
     * Returns a view of all mappings in the given row.
     */
    Collection<SpreadsheetLabelMapping> all();

    /**
     * Returns all {@link SpreadsheetCellReference} for the given {@link SpreadsheetLabelName}, including resolving
     * label to label references until they resolve to cells.
     */
    Set<? super ExpressionReference> loadCellReferencesOrRanges(final SpreadsheetLabelName label);
}
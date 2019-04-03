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
     * Returns all {@link SpreadsheetCellReference} for the given {@link SpreadsheetLabelName}, including resolving
     * label to label references until they resolve to cells.
     */
    Set<? super ExpressionReference> loadCellReferencesOrRanges(final SpreadsheetLabelName label);

    /**
     * Returns all {@link SpreadsheetLabelName} that eventually map to the {@link SpreadsheetCellReference}, including resolving
     * label to label references until they resolve to cells.
     */
    Set<SpreadsheetLabelName> labels(final SpreadsheetCellReference cell);
}
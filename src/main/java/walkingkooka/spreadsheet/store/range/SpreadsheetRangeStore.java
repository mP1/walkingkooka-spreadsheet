package walkingkooka.spreadsheet.store.range;

import walkingkooka.spreadsheet.SpreadsheetRange;
import walkingkooka.spreadsheet.store.Store;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * A store that holds one or more values for {@link SpreadsheetRange}.
 */
public interface SpreadsheetRangeStore<V> extends Store<SpreadsheetRange, List<V>> {

    /**
     * Values dont include the actual range, thereore this {@link Store} method is invalid.
     */
    @Override
    default List<V> save(final List<V> value) {
        Objects.requireNonNull(value, "value");
        throw new UnsupportedOperationException();
    }

    /**
     * Loads all the {@link SpreadsheetRange} that cover the cell
     */
    Set<SpreadsheetRange> loadCellReferenceRanges(final SpreadsheetCellReference cell);

    /**
     * Load all the values for a single cell.
     */
    Set<V> loadCellReferenceValues(final SpreadsheetCellReference cell);

    /**
     * Add a single value to the given {@link SpreadsheetRange}. If the mapping exists nothing happens.
     */
    void saveValue(final SpreadsheetRange range, final V value);

    /**
     * If the old value exists replace it with the new value. If old does not exist the replace fails.
     */
    boolean replaceValue(final SpreadsheetRange range, final V newValue, final V oldValue);

    /**
     * Delete a single value if it exists for the given {@link SpreadsheetRange}
     */
    void deleteValue(final SpreadsheetRange range, final V value);
}

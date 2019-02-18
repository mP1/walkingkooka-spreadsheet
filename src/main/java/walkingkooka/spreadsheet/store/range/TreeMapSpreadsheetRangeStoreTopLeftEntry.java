package walkingkooka.spreadsheet.store.range;

import walkingkooka.compare.Comparators;
import walkingkooka.spreadsheet.SpreadsheetRange;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.Comparator;

final class TreeMapSpreadsheetRangeStoreTopLeftEntry<V> extends TreeMapSpreadsheetRangeStoreEntry<V> {

    static <V> TreeMapSpreadsheetRangeStoreTopLeftEntry<V> with(final SpreadsheetRange range, final V value) {
        return new TreeMapSpreadsheetRangeStoreTopLeftEntry<V>(range, value);
    }

    private TreeMapSpreadsheetRangeStoreTopLeftEntry(final SpreadsheetRange range, final V value) {
        super(range, value);
    }

    @Override
    Comparator<SpreadsheetCellReference> comparator() {
        return Comparators.naturalOrdering();
    }

    @Override
    SpreadsheetCellReference primaryCellReference(final SpreadsheetRange range) {
        return range.begin();
    }

    @Override
    SpreadsheetCellReference secondaryCellReference(SpreadsheetRange range) {
        return range.end();
    }
}

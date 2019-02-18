package walkingkooka.spreadsheet.store.range;

import walkingkooka.compare.Comparators;
import walkingkooka.spreadsheet.SpreadsheetRange;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.Comparator;

final class TreeMapSpreadsheetRangeStoreBottomRightEntry<V> extends TreeMapSpreadsheetRangeStoreEntry<V> {

    static <V> TreeMapSpreadsheetRangeStoreBottomRightEntry<V> with(final SpreadsheetRange range, final V value) {
        return new TreeMapSpreadsheetRangeStoreBottomRightEntry<V>(range, value);
    }

    private TreeMapSpreadsheetRangeStoreBottomRightEntry(final SpreadsheetRange range, final V value) {
        super(range, value);
    }

    @Override
    Comparator<SpreadsheetCellReference> comparator() {
        return Comparators.<SpreadsheetCellReference>naturalOrdering().reversed();
    }

    @Override
    SpreadsheetCellReference primaryCellReference(final SpreadsheetRange range) {
        return range.end();
    }

    @Override
    SpreadsheetCellReference secondaryCellReference(SpreadsheetRange range) {
        return range.begin();
    }
}

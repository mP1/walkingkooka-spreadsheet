package walkingkooka.spreadsheet.store.range;

import walkingkooka.compare.Comparators;
import walkingkooka.spreadsheet.SpreadsheetRange;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.Comparator;

final class BasicSpreadsheetRangeStoreBottomRightEntry<V> extends BasicSpreadsheetRangeStoreEntry<V> {

    static <V> BasicSpreadsheetRangeStoreBottomRightEntry<V> with(final SpreadsheetRange range, final V value) {
        return new BasicSpreadsheetRangeStoreBottomRightEntry<V>(range, value);
    }

    private BasicSpreadsheetRangeStoreBottomRightEntry(final SpreadsheetRange range, final V value) {
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

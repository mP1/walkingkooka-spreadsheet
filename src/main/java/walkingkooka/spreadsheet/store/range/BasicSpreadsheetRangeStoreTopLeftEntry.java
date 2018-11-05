package walkingkooka.spreadsheet.store.range;

import walkingkooka.compare.Comparators;
import walkingkooka.spreadsheet.SpreadsheetRange;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.Comparator;

final class BasicSpreadsheetRangeStoreTopLeftEntry<V> extends BasicSpreadsheetRangeStoreEntry<V> {

    static <V> BasicSpreadsheetRangeStoreTopLeftEntry<V> with(final SpreadsheetRange range, final V value) {
        return new BasicSpreadsheetRangeStoreTopLeftEntry<V>(range, value);
    }

    private BasicSpreadsheetRangeStoreTopLeftEntry(final SpreadsheetRange range, final V value) {
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

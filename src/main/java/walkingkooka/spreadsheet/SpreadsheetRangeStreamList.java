package walkingkooka.spreadsheet;

import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.AbstractList;
import java.util.List;
import java.util.stream.Stream;

/**
 * A {@link List} that provides a {@link Stream} creating elements using the index.
 * The {@link #get(int)} becomes a factory which uses the index to create the {@link SpreadsheetCellReference},
 * {@link walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReference} or
 * {@link walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRowReference}.
 */
abstract class SpreadsheetRangeStreamList<R> extends AbstractList<R> {

    SpreadsheetRangeStreamList(final SpreadsheetRange range) {
        super();

        this.size = this.size(range);
        this.toString = this.toStringPrefix() + range.toString();
    }

    /**
     * Compute the size of this list and cache for future use.
     */
    abstract int size(final SpreadsheetRange cell);

    final int column(final SpreadsheetCellReference cell) {
        return cell.column().value();
    }

    final int row(final SpreadsheetCellReference cell) {
        return cell.row().value();
    }

    abstract String toStringPrefix();

    @Override
    public final int size() {
        return this.size;
    }

    private final int size;

    @Override
    public final String toString() {
        return this.toString.toString();
    }

    private final String toString;
}

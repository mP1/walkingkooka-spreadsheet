package walkingkooka.spreadsheet;

import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetReferenceKind;

import java.util.List;

/**
 * A {@link List} which will provides a {@link java.util.stream.Stream} that gives {@link SpreadsheetCellReference}.
 */
final class SpreadsheetRangeSpreadsheetCellReferenceStreamList extends SpreadsheetRangeStreamList<SpreadsheetCellReference> {

    static SpreadsheetRangeSpreadsheetCellReferenceStreamList with(final SpreadsheetRange range) {
        return new SpreadsheetRangeSpreadsheetCellReferenceStreamList(range);
    }

    private SpreadsheetRangeSpreadsheetCellReferenceStreamList(final SpreadsheetRange range) {
        super(range);

        final SpreadsheetCellReference begin = range.begin();
        this.rowOffset = this.row(begin);
        this.width = range.width();
        this.columnOffset = this.column(range.begin());
    }

    @Override
    int size(final SpreadsheetRange cell) {
        return cell.width() * cell.height();
    }

    @Override
    public SpreadsheetCellReference get(final int index) {
        return SpreadsheetReferenceKind.ABSOLUTE.column(this.columnOffset + (index % this.width))
                .setRow(SpreadsheetReferenceKind.ABSOLUTE.row(this.rowOffset + (index / this.width)));
    }

    @Override
    String toStringPrefix() {
        return "Cell";
    }

    private final int width;
    private final int rowOffset;
    private final int columnOffset;
}

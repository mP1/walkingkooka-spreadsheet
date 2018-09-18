package walkingkooka.spreadsheet;

import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetReferenceKind;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRowReference;

import java.util.List;

/**
 * A {@link List} which will provides a {@link java.util.stream.Stream} that gives {@link SpreadsheetRowReference}.
 */
final class SpreadsheetRangeSpreadsheetRowReferenceStreamList extends SpreadsheetRangeStreamList<SpreadsheetRowReference> {

    static SpreadsheetRangeSpreadsheetRowReferenceStreamList with(final SpreadsheetRange range) {
        return new SpreadsheetRangeSpreadsheetRowReferenceStreamList(range);
    }

    private SpreadsheetRangeSpreadsheetRowReferenceStreamList(final SpreadsheetRange range) {
        super(range);
        this.offset = range.begin().row().value();
    }

    @Override
    int size(final SpreadsheetRange range) {
        return range.height();
    }

    @Override
    public SpreadsheetRowReference get(final int value) {
        return SpreadsheetReferenceKind.ABSOLUTE.row(this.offset + value);
    }

    private final int offset;

    @Override
    String toStringPrefix() {
        return "Row";
    }
}

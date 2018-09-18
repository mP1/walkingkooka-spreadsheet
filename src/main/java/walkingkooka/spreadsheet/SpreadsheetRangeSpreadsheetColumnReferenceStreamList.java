package walkingkooka.spreadsheet;

import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetReferenceKind;

import java.util.List;

/**
 * A {@link List} which will provides a {@link java.util.stream.Stream} that gives {@link SpreadsheetColumnReference}.
 */
final class SpreadsheetRangeSpreadsheetColumnReferenceStreamList extends SpreadsheetRangeStreamList<SpreadsheetColumnReference> {

    static SpreadsheetRangeSpreadsheetColumnReferenceStreamList with(final SpreadsheetRange range) {
        return new SpreadsheetRangeSpreadsheetColumnReferenceStreamList(range);
    }

    private SpreadsheetRangeSpreadsheetColumnReferenceStreamList(final SpreadsheetRange range) {
        super(range);
        this.offset = range.begin().column().value();
    }

    @Override
    int size(final SpreadsheetRange range) {
        return range.width();
    }

    @Override
    public SpreadsheetColumnReference get(final int value) {
        return SpreadsheetReferenceKind.ABSOLUTE.column(this.offset + value);
    }

    private final int offset;

    @Override
    String toStringPrefix() {
        return "Column";
    }
}

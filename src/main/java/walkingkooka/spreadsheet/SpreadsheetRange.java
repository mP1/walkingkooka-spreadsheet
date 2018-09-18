package walkingkooka.spreadsheet;

import walkingkooka.Cast;
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStore;
import walkingkooka.test.HashCodeEqualsDefined;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetReferenceKind;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRowReference;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Holds a range.
 */
public final class SpreadsheetRange implements HashCodeEqualsDefined {

    /**
     * Factory that creates a {@link SpreadsheetRange}
     */
    public static SpreadsheetRange with(final SpreadsheetCellReference begin, final SpreadsheetCellReference end) {
        Objects.requireNonNull(begin, "begin");
        Objects.requireNonNull(end, "end");

        return new SpreadsheetRange(begin, end);
    }

    /**
     * Private ctor
     */
    private SpreadsheetRange(final SpreadsheetCellReference begin, final SpreadsheetCellReference end) {
        this.begin = begin.lower(end);
        this.end =  end.upper(begin);
    }

    /**
     * Returns a {@link SpreadsheetCellReference} that holds the top left cell reference.
     */
    public SpreadsheetCellReference begin() {
        return this.begin;
    }
    
    private final SpreadsheetCellReference begin;

    /**
     * Returns a {@link SpreadsheetCellReference} that holds the bottom right cell reference.
     */
    public SpreadsheetCellReference end() {
        return this.end;
    }

    private final SpreadsheetCellReference end;

    /**
     * Returns the width of this range.
     */
    public int width() {
        return this.end().column().value() - this.begin().column().value();
    }

    /**
     * Returns the height of this range.
     */
    public int height() {
        return this.end().row().value() - this.begin().row().value();
    }

    /**
     * A stream that provides all {@link SpreadsheetColumnReference}.
     */
    public Stream<SpreadsheetColumnReference> columnStream() {
        return IntStream.range(this.begin().column().value(), this.end.column().value())
                .mapToObj(i -> SpreadsheetReferenceKind.ABSOLUTE.column(i));
    }
    
    /**
     * A stream that provides all {@link SpreadsheetRowReference}.
     */
    public Stream<SpreadsheetRowReference> rowStream() {
        return IntStream.range(this.begin().row().value(), this.end.row().value())
                .mapToObj(i -> SpreadsheetReferenceKind.ABSOLUTE.row(i));
    }

    /**
     * A stream that provides all {@link SpreadsheetCellReference}.
     */
    public Stream<SpreadsheetCellReference> cellStream() {
        final SpreadsheetCellReference begin = this.begin();
        
        final int rowOffset = begin.row().value();
        final int width = this.width();
        final int columnOffset = begin.column().value();

        return IntStream.range(0, width * this.height())
                .mapToObj(index -> {
                        return SpreadsheetReferenceKind.ABSOLUTE.column(columnOffset + (index % width))
                        .setRow(SpreadsheetReferenceKind.ABSOLUTE.row(rowOffset + (index / width)));}
                        );
    }

    public void clear(final SpreadsheetCellStore store) {
        Objects.requireNonNull(store, "store");

        this.cellStream().forEach(c -> store.delete(c));
    }

    // HashCodeEqualsDefined.......................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(this.begin, this.end);
    }

    public boolean equals(final Object other) {
        return this == other ||
               other instanceof SpreadsheetRange &&
               this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetRange other) {
        return this.begin.equals(other.begin) &&
               this.end.equals(other.end);
    }

    @Override
    public String toString() {
        return this.begin + ".." + this.end;
    }
}

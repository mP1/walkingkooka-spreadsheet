package walkingkooka.spreadsheet.store.range;

import walkingkooka.spreadsheet.SpreadsheetRange;
import walkingkooka.spreadsheet.store.FakeStore;
import walkingkooka.test.Fake;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class FakeSpreadsheetRangeStore<V> extends FakeStore<SpreadsheetRange, List<V>> implements SpreadsheetRangeStore<V>, Fake {

    @Override
    public Set<SpreadsheetRange> loadCellReferenceRanges(final SpreadsheetCellReference cell) {
        Objects.requireNonNull(cell, "cell");

        throw new UnsupportedOperationException();
    }

    @Override
    public Set<V> loadCellReferenceValues(final SpreadsheetCellReference cell) {
        Objects.requireNonNull(cell, "cell");

        throw new UnsupportedOperationException();
    }

    @Override
    public void addValue(final SpreadsheetRange range, final V value) {
        Objects.requireNonNull(range, "range");
        Objects.requireNonNull(value, "value");

        throw new UnsupportedOperationException();
    }

    @Override
    public boolean replaceValue(final SpreadsheetRange range, final V newValue, final V oldValue) {
        Objects.requireNonNull(range, "range");
        Objects.requireNonNull(newValue, "newValue");
        Objects.requireNonNull(oldValue, "oldValue");

        throw new UnsupportedOperationException();
    }

    @Override
    public void removeValue(final SpreadsheetRange range, final V value) {
        Objects.requireNonNull(range, "range");
        Objects.requireNonNull(value, "value");

        throw new UnsupportedOperationException();
    }

    @Override
    public Set<SpreadsheetRange> rangesWithValue(final V value) {
        Objects.requireNonNull(value, "value");

        throw new UnsupportedOperationException();
    }
}

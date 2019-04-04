package walkingkooka.spreadsheet.store.range;

import walkingkooka.spreadsheet.SpreadsheetRange;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

final class ReadOnlySpreadsheetRangeStore<V> implements SpreadsheetRangeStore<V> {

    static <V> ReadOnlySpreadsheetRangeStore<V> with(final SpreadsheetRangeStore<V> store) {
        Objects.requireNonNull(store, "store");
        return new ReadOnlySpreadsheetRangeStore<>(store);
    }

    private ReadOnlySpreadsheetRangeStore(final SpreadsheetRangeStore<V> store) {
        this.store = store;
    }

    public Optional<List<V>> load(final SpreadsheetRange id) {
        return store.load(id);
    }

    public List<V> save(final List<V> value) {
        Objects.requireNonNull(value, "value");
        throw new UnsupportedOperationException();
    }

    @Override
    public Runnable addSaveWatcher(final Consumer<List<V>> saved) {
        Objects.requireNonNull(saved, "saved");
        throw new UnsupportedOperationException();
    }

    public void delete(final SpreadsheetRange id) {
        Objects.requireNonNull(id, "id");
        throw new UnsupportedOperationException();
    }

    @Override
    public Runnable addDeleteWatcher(final Consumer<SpreadsheetRange> deleted) {
        Objects.requireNonNull(deleted, "saved");
        throw new UnsupportedOperationException();
    }

    @Override
    public int count() {
        return store.count();
    }

    @Override
    public Set<SpreadsheetRange> ids(final int from, final int count) {
        return store.ids(from, count);
    }

    @Override
    public List<List<V>> values(final SpreadsheetRange from, final int count) {
        return store.values(from, count);
    }

    @Override
    public Set<SpreadsheetRange> loadCellReferenceRanges(final SpreadsheetCellReference cell) {
        return store.loadCellReferenceRanges(cell);
    }

    @Override
    public Set<V> loadCellReferenceValues(final SpreadsheetCellReference cell) {
        return store.loadCellReferenceValues(cell);
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

        return this.store.rangesWithValue(value);
    }

    private final SpreadsheetRangeStore<V> store;

    @Override
    public String toString() {
        return this.store.toString();
    }
}

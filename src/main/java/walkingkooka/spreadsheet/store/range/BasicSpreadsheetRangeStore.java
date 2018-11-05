package walkingkooka.spreadsheet.store.range;

import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetRange;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

/**
 * A {@link SpreadsheetRangeStore} that uses two {@link TreeMap} to navigate and store range to value mappings.
 */
final class BasicSpreadsheetRangeStore<V> implements SpreadsheetRangeStore<V> {

    /**
     * Factory that creates a new {@link BasicSpreadsheetRangeStore}
     */
    static BasicSpreadsheetRangeStore create() {
        return new BasicSpreadsheetRangeStore();
    }

    /**
     * Private ctor.
     */
    private BasicSpreadsheetRangeStore() {
        super();
    }

    // load .....................................................................................................

    @Override
    public Optional<List<V>> load(final SpreadsheetRange range) {
        Objects.requireNonNull(range, "range");

        final BasicSpreadsheetRangeStoreTopLeftEntry<V> value = this.topLeft.get(range.begin());
        return null != value ?
                value.load(range) :
                Optional.empty();
    }

    // loadCell .....................................................................................................

    @Override
    public Set<V> loadCellReference(final SpreadsheetCellReference cell) {
        Objects.requireNonNull(cell, "cell");

        final Set<V> values = Sets.ordered();
        this.loadCellReferenceTopLeft(cell, values);
        this.loadCellReferenceBottomRight(cell, values);

        return Sets.readOnly(values);
    }

    private void loadCellReferenceTopLeft(final SpreadsheetCellReference cell, final Collection<V> values) {

        // loop over all entries until cell is after entry.
        for (Map.Entry<SpreadsheetCellReference, BasicSpreadsheetRangeStoreTopLeftEntry<V>> refAndValues :
                this.topLeft.tailMap(cell, true)
                        .entrySet()) {
            final SpreadsheetCellReference topLeft = refAndValues.getKey();
            if (topLeft.compareTo(cell) > 0) {
                break;
            }
            refAndValues.getValue().loadCellReference(cell, values);
        }
    }

    private void loadCellReferenceBottomRight(final SpreadsheetCellReference cell, final Collection<V> values) {

        // loop over all entries until cell is after entry.
        for (Map.Entry<SpreadsheetCellReference, BasicSpreadsheetRangeStoreBottomRightEntry<V>> refAndValues :
                this.bottomRight.tailMap(cell, true)
                        .entrySet()) {
            final SpreadsheetCellReference bottomRight = refAndValues.getKey();
            if (bottomRight.compareTo(cell) < 0) {
                break;
            }
            refAndValues.getValue().loadCellReference(cell, values);
        }
    }

    // save .....................................................................................................

    @Override
    public void saveValue(final SpreadsheetRange range, final V value) {
        Objects.requireNonNull(range, "range");
        Objects.requireNonNull(value, "value");

        this.saveTopLeft(range, value);
        this.saveBottomRight(range, value);
    }

    private void saveTopLeft(final SpreadsheetRange range, final V value) {
        final SpreadsheetCellReference topLeft = range.begin();
        final BasicSpreadsheetRangeStoreEntry<V> values = this.topLeft.get(topLeft);
        if (null != values) {
            values.save(range, value);
        } else {
            this.topLeft.put(topLeft,
                    BasicSpreadsheetRangeStoreTopLeftEntry.with(range, value));
        }
    }

    private void saveBottomRight(final SpreadsheetRange range, final V value) {
        final SpreadsheetCellReference bottomRight = range.end();
        final BasicSpreadsheetRangeStoreEntry<V> values = this.bottomRight.get(bottomRight);
        if (null != values) {
            values.save(range, value);
        } else {
            this.bottomRight.put(bottomRight,
                    BasicSpreadsheetRangeStoreBottomRightEntry.with(range, value));
        }
    }

    @Override
    public boolean replaceValue(final SpreadsheetRange range, final V newValue, final V oldValue) {
        Objects.requireNonNull(range, "range");
        Objects.requireNonNull(newValue, "newValue");
        Objects.requireNonNull(oldValue, "oldValue");

        return !oldValue.equals(newValue) &&
                this.replace0(range, newValue, oldValue) &&
                this.replace1(range, newValue, oldValue);
    }

    private boolean replace0(final SpreadsheetRange range, final V newValue, final V oldValue) {
        final SpreadsheetCellReference topLeft = range.begin();
        final BasicSpreadsheetRangeStoreEntry<V> values = this.topLeft.get(topLeft);
        return null != values &&
                values.replace(range, newValue, oldValue);
    }

    private boolean replace1(final SpreadsheetRange range, final V newValue, final V oldValue) {
        final SpreadsheetCellReference bottomRight = range.end();
        final BasicSpreadsheetRangeStoreEntry<V> values = this.bottomRight.get(bottomRight);
        return null != values &&
                values.replace(range, newValue, oldValue);
    }

    // delete .....................................................................................................

    /**
     * Only deletes the given value for the given range if it exists.
     */
    @Override
    public void deleteValue(final SpreadsheetRange range, final V value) {
        Objects.requireNonNull(range, "range");
        Objects.requireNonNull(value, "value");

        this.deleteTopLeftValue(range, value);
        this.deleteBottomRightValue(range, value);
    }

    private void deleteTopLeftValue(final SpreadsheetRange range, final V value) {
        final SpreadsheetCellReference topLeft = range.begin();
        BasicSpreadsheetRangeStoreEntry<V> topLeftValues = this.topLeft.get(topLeft);
        if (null != topLeftValues) {
            if (topLeftValues.delete(range, value)) {
                this.topLeft.remove(topLeft);
            }
        }
    }

    private void deleteBottomRightValue(final SpreadsheetRange range, final V value) {
        final SpreadsheetCellReference bottomRight = range.begin();
        BasicSpreadsheetRangeStoreEntry<V> bottomRightValues = this.bottomRight.get(bottomRight);
        if (null != bottomRightValues) {
            if (bottomRightValues.delete(range, value)) {
                this.bottomRight.remove(bottomRight);
            }
        }
    }

    /**
     * Only deletes values that match the given range exactly.
     */
    @Override
    public void delete(final SpreadsheetRange range) {
        Objects.requireNonNull(range, "range");

        if (null != this.topLeft.remove(range.begin())) {
            this.bottomRight.remove(range.end());
        }
    }

    // count.........................................................................................................

    @Override
    public int count() {
        return this.topLeft.values()
                .stream()
                .mapToInt(e -> e.count())
                .sum();
    }

    /**
     * The top left cell is the key, with the value holding all ranges that share the same top/left cell.
     * To locate matching values, all entries will be filtered.
     */
    private NavigableMap<SpreadsheetCellReference, BasicSpreadsheetRangeStoreTopLeftEntry<V>> topLeft = new TreeMap<>();

    private NavigableMap<SpreadsheetCellReference, BasicSpreadsheetRangeStoreBottomRightEntry<V>> bottomRight = new TreeMap<>();

    // toString.........................................................................................................

    @Override
    public String toString() {
        return this.topLeft.toString();
    }
}

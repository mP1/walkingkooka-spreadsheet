package walkingkooka.spreadsheet.store.range;

import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetRange;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A {@link SpreadsheetRangeStore} that uses two {@link TreeMap} to navigate and store range to value mappings.
 */
final class TreeMapSpreadsheetRangeStore<V> implements SpreadsheetRangeStore<V> {

    /**
     * Factory that creates a new {@link TreeMapSpreadsheetRangeStore}
     */
    static TreeMapSpreadsheetRangeStore create() {
        return new TreeMapSpreadsheetRangeStore();
    }

    /**
     * Private ctor.
     */
    private TreeMapSpreadsheetRangeStore() {
        super();
    }

    // load .....................................................................................................

    @Override
    public Optional<List<V>> load(final SpreadsheetRange range) {
        Objects.requireNonNull(range, "range");

        final TreeMapSpreadsheetRangeStoreTopLeftEntry<V> value = this.topLeft.get(range.begin());
        return null != value ?
                value.load(range) :
                Optional.empty();
    }

    // loadRanges .....................................................................................................

    @Override
    public Set<SpreadsheetRange> loadCellReferenceRanges(final SpreadsheetCellReference cell) {
        Objects.requireNonNull(cell, "cell");

        final Set<SpreadsheetRange> values = Sets.ordered();

//        this.gatherTopLeft(cell, entry -> values.add(entry.range));
//        this.gatherBottomRight(cell, entry -> values.add(entry.range));

        this.gatherTopLeft(cell, entry -> entry.loadCellReferenceRanges(cell, values));
        this.gatherBottomRight(cell, entry -> entry.loadCellReferenceRanges(cell, values));

        return Sets.readOnly(values);
    }

    // loadCellReferences .....................................................................................................

    @Override
    public Set<V> loadCellReferenceValues(final SpreadsheetCellReference cell) {
        Objects.requireNonNull(cell, "cell");

        final Set<V> values = Sets.ordered();

        this.gatherTopLeft(cell, entry -> entry.loadCellReferenceValues(cell, values));
        this.gatherBottomRight(cell, entry -> entry.loadCellReferenceValues(cell, values));

        return Sets.readOnly(values);
    }

    // loadCellReferenceRanges & loadCellReferences ................................................................................

    private void gatherTopLeft(final SpreadsheetCellReference cell,
                               final Consumer<TreeMapSpreadsheetRangeStoreTopLeftEntry<V>> values) {
        this.gather(this.topLeft,
                cell,
                cellReference -> cellReference.compareTo(cell) > 0,
                values);
    }

    private void gatherBottomRight(final SpreadsheetCellReference cell,
                                   final Consumer<TreeMapSpreadsheetRangeStoreBottomRightEntry<V>> values) {
        this.gather(this.bottomRight,
                cell,
                cellReference -> cellReference.compareTo(cell) < 0,
                values);
    }

    private <E extends TreeMapSpreadsheetRangeStoreEntry<V>> void gather(final NavigableMap<SpreadsheetCellReference, E> cellReferenceToEntry,
                                                                         final SpreadsheetCellReference cell,
                                                                         final Predicate<SpreadsheetCellReference> stop,
                                                                         final Consumer<E> values) {
        // loop over all entries until cell is after entry.
        for (Map.Entry<SpreadsheetCellReference, E> refAndValues : cellReferenceToEntry.tailMap(cell, true)
                .entrySet()) {
            if (stop.test(refAndValues.getKey())) {
                break;
            }
            values.accept(refAndValues.getValue());
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
        final TreeMapSpreadsheetRangeStoreEntry<V> values = this.topLeft.get(topLeft);
        if (null != values) {
            values.save(range, value);
        } else {
            this.topLeft.put(topLeft,
                    TreeMapSpreadsheetRangeStoreTopLeftEntry.with(range, value));
        }
    }

    private void saveBottomRight(final SpreadsheetRange range, final V value) {
        final SpreadsheetCellReference bottomRight = range.end();
        final TreeMapSpreadsheetRangeStoreEntry<V> values = this.bottomRight.get(bottomRight);
        if (null != values) {
            values.save(range, value);
        } else {
            this.bottomRight.put(bottomRight,
                    TreeMapSpreadsheetRangeStoreBottomRightEntry.with(range, value));
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
        final TreeMapSpreadsheetRangeStoreEntry<V> values = this.topLeft.get(topLeft);
        return null != values &&
                values.replace(range, newValue, oldValue);
    }

    private boolean replace1(final SpreadsheetRange range, final V newValue, final V oldValue) {
        final SpreadsheetCellReference bottomRight = range.end();
        final TreeMapSpreadsheetRangeStoreEntry<V> values = this.bottomRight.get(bottomRight);
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
        TreeMapSpreadsheetRangeStoreEntry<V> topLeftValues = this.topLeft.get(topLeft);
        if (null != topLeftValues) {
            if (topLeftValues.delete(range, value)) {
                this.topLeft.remove(topLeft);
            }
        }
    }

    private void deleteBottomRightValue(final SpreadsheetRange range, final V value) {
        final SpreadsheetCellReference bottomRight = range.begin();
        TreeMapSpreadsheetRangeStoreEntry<V> bottomRightValues = this.bottomRight.get(bottomRight);
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
    private final NavigableMap<SpreadsheetCellReference, TreeMapSpreadsheetRangeStoreTopLeftEntry<V>> topLeft = new TreeMap<>();

    private final NavigableMap<SpreadsheetCellReference, TreeMapSpreadsheetRangeStoreBottomRightEntry<V>> bottomRight = new TreeMap<>();

    // toString.........................................................................................................

    @Override
    public String toString() {
        return this.topLeft.toString();
    }
}

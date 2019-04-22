package walkingkooka.spreadsheet.store.range;

import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetRange;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

/**
 * Holds all the values that share a common range top left
 */
abstract class TreeMapSpreadsheetRangeStoreEntry<V> implements Comparable<TreeMapSpreadsheetRangeStoreEntry<V>> {

    /**
     * Package private ctor to limit subclassing.
     */
    TreeMapSpreadsheetRangeStoreEntry(final SpreadsheetRange range, final V value) {
        super();

        this.range = range;

        this.secondaryCellReferenceToValues = new TreeMap<>(this.comparator());
        final Set<V> values = Sets.ordered();
        values.add(value);
        this.secondaryCellReferenceToValues.put(this.secondaryCellReference(range), values);
    }

    final SpreadsheetRange range;

    abstract Comparator<SpreadsheetCellReference> comparator();

    // load............................................................................................

    final Optional<List<V>> load(final SpreadsheetRange range) {
        Objects.requireNonNull(range, "range");

        final Set<V> values = this.secondaryCellReferenceToValues.get(range.end());
        return null != values ?
                this.copyToSet(values) :
                Optional.empty();
    }

    private Optional<List<V>> copyToSet(final Set<V> values) {
        final List<V> list = Lists.array();
        list.addAll(values);
        return Optional.of(Lists.readOnly(list));
    }

    // load cell reference values............................................................................................

    /**
     * Add values for entries that include the given cell.
     */
    final void loadCellReferenceRanges(final SpreadsheetCellReference cell,
                                       final Collection<SpreadsheetRange> ranges) {

        if (this.range.contains(cell)) {
            ranges.add(this.range);
        }
    }

    // load cell reference values............................................................................................

    /**
     * Add values for entries that include the given cell.
     */
    final void loadCellReferenceValues(final SpreadsheetCellReference cell,
                                       final Collection<V> values) {

        // loop over all entries until cell is after entry.
        final Comparator<SpreadsheetCellReference> comparator = this.comparator();
        for (Map.Entry<SpreadsheetCellReference, Set<V>> refAndValues : this.secondaryCellReferenceToValues.entrySet()) {
            if (comparator.compare(cell, refAndValues.getKey()) > 0) {
                break;
            }
            values.addAll(refAndValues.getValue());
        }
    }

    // cell is AFTER entry...

    // save...................................................................................................

    final void save(final SpreadsheetRange range, final V value) {
        final SpreadsheetCellReference ref = this.secondaryCellReference(range);
        Set<V> values = this.secondaryCellReferenceToValues.get(ref);
        if (null == values) {
            values = Sets.ordered();
            this.secondaryCellReferenceToValues.put(ref, values);
        }
        values.add(value);
    }

    final boolean replace(final SpreadsheetRange range, final V newValue, final V oldValue) {
        boolean replaced = false;

        final Set<V> values = this.secondaryCellReferenceToValues.get(this.secondaryCellReference(range));
        if (null != values) {
            if (values.remove(oldValue)) {
                values.add(newValue);
                replaced = true;
            }
        }

        return replaced;
    }

    // delete....................................................................................................

    /**
     * Deletes/removes all values that match the given range, and returns true if there are no longer any values.
     */
    final boolean delete(final SpreadsheetRange range, final V value) {
        boolean empty = false;

        final SpreadsheetCellReference end = this.secondaryCellReference(range);
        final Set<V> values = this.secondaryCellReferenceToValues.get(end);
        if (null != values) {
            if (values.remove(value)) {
                if (values.isEmpty()) {
                    this.secondaryCellReferenceToValues.remove(end);
                    empty = this.secondaryCellReferenceToValues.isEmpty();
                }
            }
        }

        return empty;
    }

    private SpreadsheetCellReference primaryCellReference() {
        return this.primaryCellReference(this.range);
    }

    abstract SpreadsheetCellReference primaryCellReference(final SpreadsheetRange range);

    abstract SpreadsheetCellReference secondaryCellReference(final SpreadsheetRange range);

    // count....................................................................................................

    /**
     * The number of values for all cell references.
     */
    final int count() {
        return this.secondaryCellReferenceToValues.values()
                .stream()
                .mapToInt(v -> v.size())
                .sum();
    }

    // Comparable....................................................................................................

    /**
     * Compares row then column.
     */
    @Override
    public final int compareTo(final TreeMapSpreadsheetRangeStoreEntry<V> other) {
        return this.compareTo0(other.primaryCellReference());
    }

    private int compareTo0(final SpreadsheetCellReference other) {
        final SpreadsheetCellReference ref = this.primaryCellReference();
        int result = ref.row().compareTo(other.row());
        if (0 != result) {
            result = ref.column().compareTo(other.column());
        }
        return result;
    }

    // toString....................................................................................................

    @Override
    public final String toString() {
        return this.primaryCellReference() + "=" + this.secondaryCellReferenceToValues;
    }

    /**
     * The bottom right of the range to its values. This entire entry is sorted using the range begin and so is not used here.
     */
    final NavigableMap<SpreadsheetCellReference, Set<V>> secondaryCellReferenceToValues;
}

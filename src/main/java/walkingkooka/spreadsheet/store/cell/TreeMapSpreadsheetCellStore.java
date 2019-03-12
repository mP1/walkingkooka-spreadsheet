package walkingkooka.spreadsheet.store.cell;

import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

/**
 * A {@link SpreadsheetCellStore} that uses a {@link Map}.
 */
final class TreeMapSpreadsheetCellStore extends SpreadsheetCellStoreTemplate {

    /**
     * Factory that creates a new {@link TreeMapSpreadsheetCellStore}
     */
    static TreeMapSpreadsheetCellStore create() {
        return new TreeMapSpreadsheetCellStore();
    }

    /**
     * Private ctor.
     */
    private TreeMapSpreadsheetCellStore() {
        super();
    }

    @Override
    Optional<SpreadsheetCell> load0(final SpreadsheetCellReference reference) {
        return Optional.ofNullable(this.cells.get(reference));
    }

    /**
     * Accepts a potentially updated cell.
     */
    @Override
    SpreadsheetCellReference save0(final SpreadsheetCell cell) {
        final SpreadsheetCellReference key = cell.reference();
        this.cells.put(key, cell);
        return key;
    }

    /**
     * Deletes a single cell, ignoring invalid requests.
     */
    @Override
    void delete0(final SpreadsheetCellReference reference) {
        this.cells.remove(reference);
    }

    @Override
    public int count() {
        return this.cells.size();
    }

    @Override
    public int rows() {
        return this.max(c -> c.row().value());
    }

    @Override
    public int columns() {
        return this.max(c -> c.column().value());
    }

    private int max(final ToIntFunction<SpreadsheetCellReference> value) {
        return this.cells.keySet()
                .stream()
                .mapToInt(value)
                .max()
                .orElse(0);
    }

    @Override
    Set<SpreadsheetCell> row0(final int row) {
        return this.filter(c -> c.reference().row().value() == row);
    }

    @Override
    Set<SpreadsheetCell> column0(final int column) {
        return this.filter(c -> c.reference().column().value() == column);
    }

    private Set<SpreadsheetCell> filter(final Predicate<SpreadsheetCell> filter) {
        return this.cells.values()
                .stream()
                .filter(filter)
                .collect(Collectors.toCollection(Sets::sorted));
    }

    /**
     * All cells present in this spreadsheet
     */
    private final Map<SpreadsheetCellReference, SpreadsheetCell> cells = Maps.sorted();

    @Override
    public String toString() {
        return this.cells.toString();
    }
}

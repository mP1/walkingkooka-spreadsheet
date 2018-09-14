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
final class BasicSpreadsheetCellStore extends SpreadsheetCellStoreTemplate {

    /**
     * Factory that creates a new {@link BasicSpreadsheetCellStore}
     */
    static BasicSpreadsheetCellStore create() {
        return new BasicSpreadsheetCellStore();
    }

    /**
     * Private ctor.
     */
    private BasicSpreadsheetCellStore() {
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
    void save0(final SpreadsheetCell cell) {
        this.cells.put(cell.reference(), cell);
    }

    /**
     * Deletes a single cell, ignoring invalid requests.
     */
    @Override
    void delete0(final SpreadsheetCellReference reference) {
        this.cells.remove(reference);
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
    Collection<SpreadsheetCell> row0(final int row) {
        return this.filter(c -> c.reference().row().value() == row);
    }

    @Override
    Collection<SpreadsheetCell> column0(final int column) {
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

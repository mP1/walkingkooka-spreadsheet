package walkingkooka.spreadsheet.store;

import walkingkooka.collect.map.Maps;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.ToIntFunction;

/**
 * A {@link SpreadsheetCellStore} that uses a {@link Map}.
 */
final class BasicSpreadsheetCellStore implements SpreadsheetCellStore {

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
    public Optional<SpreadsheetCell> load(final SpreadsheetCellReference reference) {
        Objects.requireNonNull(reference, "references");

        return Optional.ofNullable(this.cells.get(reference));
    }

    /**
     * Accepts a potentially updated cell.
     */
    @Override
    public void save(final SpreadsheetCell cell) {
        Objects.requireNonNull(cell, "cell");
        this.cells.put(cell.reference(), cell);
    }

    /**
     * Deletes a single cell, ignoring invalid requests.
     */
    @Override
    public void delete(final SpreadsheetCellReference reference) {
        Objects.requireNonNull(reference, "reference");
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

    /**
     * All cells present in this spreadsheet
     */
    private final Map<SpreadsheetCellReference, SpreadsheetCell> cells = Maps.sorted();

    @Override
    public String toString() {
        return this.cells.toString();
    }
}

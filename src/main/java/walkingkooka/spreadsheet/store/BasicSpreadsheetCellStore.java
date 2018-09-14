package walkingkooka.spreadsheet.store;

import walkingkooka.collect.map.Maps;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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

    /**
     * All cells present in this spreadsheet
     */
    private final Map<SpreadsheetCellReference, SpreadsheetCell> cells = Maps.sorted();

    @Override
    public String toString() {
        return this.cells.toString();
    }
}

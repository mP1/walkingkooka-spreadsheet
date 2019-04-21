package walkingkooka.spreadsheet;

import walkingkooka.build.tostring.ToStringBuilder;
import walkingkooka.collect.list.Lists;
import walkingkooka.compare.Range;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.tree.json.JsonNode;

import java.util.List;
import java.util.Set;

/**
 * A {@link SpreadsheetDelta} without any window/filtering.
 */
final class SpreadsheetDeltaNonWindowed extends SpreadsheetDelta {

    /**
     * Factory that creates a new {@link SpreadsheetDeltaNonWindowed} without copying or filtering the cells.
     */
    static SpreadsheetDeltaNonWindowed with0(final SpreadsheetId id,
                                             final Set<SpreadsheetCell> cells) {
        return new SpreadsheetDeltaNonWindowed(id, cells);
    }

    private SpreadsheetDeltaNonWindowed(final SpreadsheetId id,
                                        final Set<SpreadsheetCell> cells) {
        super(id, cells);
    }

    @Override
    public SpreadsheetDelta setCells(final Set<SpreadsheetCell> cells) {
        checkCells(cells);

        final Set<SpreadsheetCell> copy = copyCells(cells);
        return this.cells.equals(copy) ?
                this :
                new SpreadsheetDeltaNonWindowed(this.id, copy);
    }

    /**
     * There is no window.
     */
    @Override
    public List<Range<SpreadsheetCellReference>> window() {
        return Lists.empty();
    }

    @Override
    boolean canBeEquals(final Object other) {
        return other instanceof SpreadsheetDeltaNonWindowed;
    }

    @Override
    boolean equals1(final SpreadsheetDelta other) {
        return other instanceof SpreadsheetDeltaNonWindowed;
    }

    @Override
    public JsonNode toJsonNode() {
        return this.toJsonNodeIdAndCells();
    }
}

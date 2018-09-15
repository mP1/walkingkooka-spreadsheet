package walkingkooka.spreadsheet;

import walkingkooka.Cast;
import walkingkooka.test.HashCodeEqualsDefined;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;

import java.util.Objects;

/**
 * Holds a label to cell mapping.
 */
public final class SpreadsheetLabelMapping implements HashCodeEqualsDefined {

    /**
     * Creates a new {@link SpreadsheetLabelMapping}
     */
    public static SpreadsheetLabelMapping with(final SpreadsheetLabelName label, final SpreadsheetCellReference cell) {
        checkLabel(label);
        checkCell(cell);
        return new SpreadsheetLabelMapping(label, cell);
    }

    /**
     * Private ctor use factory
     */
    private SpreadsheetLabelMapping(final SpreadsheetLabelName label, final SpreadsheetCellReference cell) {
        this.label = label;
        this.cell = cell;
    }

    public SpreadsheetLabelName label() {
        return this.label;
    }
    
    public SpreadsheetLabelMapping setLabel(final SpreadsheetLabelName label) {
        checkLabel(label);
        return this.label.equals(label) ?
               this :
               this.replace(label, cell);
    }
    
    private static void checkLabel(final SpreadsheetLabelName label) {
        Objects.requireNonNull(label, "label");
    }
    
    private final SpreadsheetLabelName label;

    public SpreadsheetCellReference cell() {
        return this.cell;
    }

    public SpreadsheetLabelMapping setCell(final SpreadsheetCellReference cell) {
        checkCell(cell);
        return this.cell.equals(cell) ?
                this :
                this.replace(this.label, cell);
    }
    
    private final SpreadsheetCellReference cell;

    private static void checkCell(final SpreadsheetCellReference cell) {
        Objects.requireNonNull(cell, "cell");
    }

    private SpreadsheetLabelMapping replace(final SpreadsheetLabelName label, final SpreadsheetCellReference cell) {
        return new SpreadsheetLabelMapping(label, cell);
    }

    // HashCodeEqualsDefined..........................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(this.label, this.cell);
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetLabelMapping &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetLabelMapping other) {
        return this.label.equals(other.label) &
               this.cell.equals(other.cell);
    }

    @Override
    public String toString() {
        return this.label + "=" + this.cell;
    }
}

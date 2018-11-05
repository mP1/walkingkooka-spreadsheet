package walkingkooka.spreadsheet;

import walkingkooka.Cast;
import walkingkooka.test.HashCodeEqualsDefined;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.Objects;

/**
 * Holds a {@link SpreadsheetLabelName label} to {@link ExpressionReference} mapping.
 */
public final class SpreadsheetLabelMapping implements HashCodeEqualsDefined {

    /**
     * Creates a new {@link SpreadsheetLabelMapping}
     */
    public static SpreadsheetLabelMapping with(final SpreadsheetLabelName label, final ExpressionReference reference) {
        checkLabel(label);
        checkReference(reference);
        return new SpreadsheetLabelMapping(label, reference);
    }

    /**
     * Private ctor use factory
     */
    private SpreadsheetLabelMapping(final SpreadsheetLabelName label, final ExpressionReference reference) {
        this.label = label;
        this.reference = reference;
    }

    public SpreadsheetLabelName label() {
        return this.label;
    }
    
    public SpreadsheetLabelMapping setLabel(final SpreadsheetLabelName label) {
        checkLabel(label);
        return this.label.equals(label) ?
               this :
               this.replace(label, reference);
    }
    
    private static void checkLabel(final SpreadsheetLabelName label) {
        Objects.requireNonNull(label, "label");
    }
    
    private final SpreadsheetLabelName label;

    public ExpressionReference reference() {
        return this.reference;
    }

    public SpreadsheetLabelMapping setReference(final ExpressionReference reference) {
        checkReference(reference);
        return this.reference.equals(reference) ?
                this :
                this.replace(this.label, reference);
    }
    
    private final ExpressionReference reference;

    private static void checkReference(final ExpressionReference reference) {
        Objects.requireNonNull(reference, "reference");
    }

    private SpreadsheetLabelMapping replace(final SpreadsheetLabelName label, final ExpressionReference reference) {
        return new SpreadsheetLabelMapping(label, reference);
    }

    // HashCodeEqualsDefined..........................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(this.label, this.reference);
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetLabelMapping &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetLabelMapping other) {
        return this.label.equals(other.label) &
               this.reference.equals(other.reference);
    }

    @Override
    public String toString() {
        return this.label + "=" + this.reference;
    }
}

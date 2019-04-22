package walkingkooka.spreadsheet.store.reference;

import walkingkooka.Cast;
import walkingkooka.test.HashCodeEqualsDefined;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.Objects;

/**
 * Holds a {@link ExpressionReference source} to a {@link SpreadsheetCellReference}.
 */
public final class TargetAndSpreadsheetCellReference<T extends ExpressionReference & Comparable<T>> implements HashCodeEqualsDefined {

    public static <T extends ExpressionReference & Comparable<T>> TargetAndSpreadsheetCellReference<T> with(final T target,
                                                                                                            final SpreadsheetCellReference reference) {
        Objects.requireNonNull(target, "target");
        Objects.requireNonNull(reference, "reference");
        if (target.equals(reference)) {
            throw new IllegalArgumentException("Target equals reference=" + target);
        }

        return new TargetAndSpreadsheetCellReference<>(target, reference);
    }

    private TargetAndSpreadsheetCellReference(final T target, final SpreadsheetCellReference reference) {
        this.target = target;
        this.reference = reference;
    }

    public T target() {
        return this.target;
    }

    private final T target;

    public SpreadsheetCellReference reference() {
        return this.reference;
    }

    private final SpreadsheetCellReference reference;

    @Override
    public int hashCode() {
        return Objects.hash(this.target, this.reference);
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof TargetAndSpreadsheetCellReference &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final TargetAndSpreadsheetCellReference other) {
        return this.target.equals(other.target) &&
                this.reference.equals(other.reference);
    }


    @Override
    public String toString() {
        return this.target + "->" + this.reference;
    }
}

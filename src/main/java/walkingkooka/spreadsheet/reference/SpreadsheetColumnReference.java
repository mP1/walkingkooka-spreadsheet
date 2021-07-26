/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package walkingkooka.spreadsheet.reference;

import walkingkooka.Cast;

import java.util.Objects;

/**
 * Represents a column reference. The {@link Comparable} method ignores the {@link SpreadsheetReferenceKind} component
 * only comparing the value.
 */
public final class SpreadsheetColumnReference extends SpreadsheetColumnOrRowReference implements Comparable<SpreadsheetColumnReference> {

    /**
     * The maximum value, columns -1.
     */
    // https://support.office.com/en-us/article/excel-specifications-and-limits-1672b34d-7043-467e-8e27-269d656771c3
    final static int MAX_VALUE = 16384 - 1; // inclusive
    final static int RADIX = 26;

    final static String MAX_TOSTRING = toString0(MAX_VALUE + 1, SpreadsheetReferenceKind.RELATIVE);

    /**
     * Factory that creates a new column.
     */
    static SpreadsheetColumnReference with(final int value, final SpreadsheetReferenceKind referenceKind) {
        checkValue(value);
        checkReferenceKind(referenceKind);

        return value < CACHE_SIZE ?
                referenceKind.columnFromCache(value) :
                new SpreadsheetColumnReference(value, referenceKind);
    }

    static final SpreadsheetColumnReference[] ABSOLUTE = fillCache(i -> new SpreadsheetColumnReference(i, SpreadsheetReferenceKind.ABSOLUTE),
            new SpreadsheetColumnReference[CACHE_SIZE]);
    static final SpreadsheetColumnReference[] RELATIVE = fillCache(i -> new SpreadsheetColumnReference(i, SpreadsheetReferenceKind.RELATIVE),
            new SpreadsheetColumnReference[CACHE_SIZE]);

    /**
     * Private ctor use factory
     */
    private SpreadsheetColumnReference(final int value, final SpreadsheetReferenceKind referenceKind) {
        super(value, referenceKind);
    }

    /**
     * Would be setter that returns a {@link SpreadsheetColumnReference} with the given {@link SpreadsheetReferenceKind}
     * creating a new instance if necessary.
     */
    @Override
    public SpreadsheetColumnReference setReferenceKind(final SpreadsheetReferenceKind referenceKind) {
        checkReferenceKind(referenceKind);

        return (SpreadsheetColumnReference) this.setReferenceKind0(referenceKind);
    }

    @Override
    SpreadsheetColumnReference replaceReferenceKind(final SpreadsheetReferenceKind referenceKind) {
        return new SpreadsheetColumnReference(this.value, referenceKind);
    }

    @Override
    public SpreadsheetColumnReference add(final int value) {
        return Cast.to(this.add0(value));
    }

    @Override
    public SpreadsheetColumnReference addSaturated(final int value) {
        return Cast.to(this.addSaturated0(value));
    }

    @Override
    int max() {
        return MAX_VALUE;
    }

    /**
     * Would be setter that returns a {@link SpreadsheetColumnReference} with the given value creating a new
     * instance if it is different.
     */
    public SpreadsheetColumnReference setValue(final int value) {
        checkValue(value);
        return this.value == value ?
                this :
                new SpreadsheetColumnReference(value, this.referenceKind());
    }

    private static void checkValue(final int value) {
        if (value < 0 || value > MAX_VALUE) {
            throw new IllegalArgumentException(invalidColumnValue(value));
        }
    }

    private static String invalidColumnValue(final int value) {
        return "Invalid column value " + value + " expected between 0 and " + (1 + MAX_VALUE);
    }

    /**
     * Creates a {@link SpreadsheetCellReference} fromt this column and the new row.
     */
    public SpreadsheetCellReference setRow(final SpreadsheetRowReference row) {
        return SpreadsheetExpressionReference.cellReference(this, row);
    }

    public String hateosLinkId() {
        final StringBuilder b = new StringBuilder();
        toString1(this.value, b);
        return b.toString();
    }

    // max..............................................................................................................

    /**
     * Returns the max or right most column.
     */
    public SpreadsheetColumnReference max(final SpreadsheetColumnReference other) {
        Objects.requireNonNull(other, "other");
        return this.value >= other.value ?
                this :
                other;
    }

    // min..............................................................................................................

    /**
     * Returns the min or left most column.
     */
    public SpreadsheetColumnReference min(final SpreadsheetColumnReference other) {
        Objects.requireNonNull(other, "other");
        return this.value <= other.value ?
                this :
                other;
    }

    // Predicate<SpreadsheetCellReference>..............................................................................

    /**
     * Returns true if the given {@link SpreadsheetCellReference} has this column.
     */
    @Override
    public boolean test(final SpreadsheetCellReference reference) {
        return this.equalsIgnoreReferenceKind(reference.column());
    }

    // testRange........................................................................................................

    @Override
    public boolean testRange(final SpreadsheetRange range) {
        Objects.requireNonNull(range, "range");

        return this.compareTo(range.begin().column()) >= 0 &&
                this.compareTo(range.end().column()) <= 0;
    }

    // toRelative.......................................................................................................

    @Override
    public SpreadsheetColumnReference toRelative() {
        return this.setReferenceKind(SpreadsheetReferenceKind.RELATIVE);
    }

    // SpreadsheetSelectionVisitor......................................................................................

    @Override
    void accept(final SpreadsheetSelectionVisitor visitor) {
        visitor.visit(this);
    }

    // Object...........................................................................................................

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof SpreadsheetColumnReference;
    }

    /**
     * Returns true if the values ignoring the {@link SpreadsheetReferenceKind}.
     */
    public boolean equalsIgnoreReferenceKind(final SpreadsheetColumnReference other) {
        return this.equalsIgnoreReferenceKind0(other);
    }

    @Override
    public String toString() {
        return toString0(this.value, this.referenceKind());
    }

    private static String toString0(final int value, final SpreadsheetReferenceKind referenceKind) {
        // 0=A, 1=B, AA = 26 * 1
        final StringBuilder b = new StringBuilder();
        b.append(referenceKind.prefix());

        toString1(value, b);

        return b.toString();
    }

    private static void toString1(final int value, final StringBuilder b) {
        final int v = (value / RADIX);
        if (v > 0) {
            toString1(v - 1, b);
        }
        final int c = (value % RADIX) + 'A';
        b.append((char) c);
    }

    // Comparable......................................................................................................

    @Override
    public int compareTo(final SpreadsheetColumnReference other) {
        checkOther(other);
        return this.value - other.value;
    }
}

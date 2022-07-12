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
import walkingkooka.collect.Range;
import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStore;
import walkingkooka.spreadsheet.store.SpreadsheetRowStore;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

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

    // both MIN & MAX constants must appear after ABSOLUTE & RELATIVE to avoid nulls in j2cl...........................

    /**
     * The left most possible column
     */
    public final static SpreadsheetColumnReference MIN = with(0, SpreadsheetReferenceKind.RELATIVE);

    /**
     * The right most possible column
     */
    public final static SpreadsheetColumnReference MAX = with(MAX_VALUE, SpreadsheetReferenceKind.RELATIVE);

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
        return SpreadsheetSelection.cell(this, row);
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
        return this.testColumn(reference.column());
    }

    // testXXX.........................................................................................................

    @Override
    public boolean testCellRange(final SpreadsheetCellRange range) {
        checkCellRange(range);

        return this.compareTo(range.begin().column()) >= 0 &&
                this.compareTo(range.end().column()) <= 0;
    }

    @Override
    public boolean testColumn(final SpreadsheetColumnReference column) {
        return this.equalsIgnoreReferenceKind(column);
    }

    @Override
    public boolean testRow(final SpreadsheetRowReference row) {
        return false;
    }

    // toRelative.......................................................................................................

    @Override
    public SpreadsheetColumnReference toRelative() {
        return this.setReferenceKind(SpreadsheetReferenceKind.RELATIVE);
    }

    // SpreadsheetColumn................................................................................................

    /**
     * Factory that returns a {@link SpreadsheetColumn} with this {@link SpreadsheetColumnReference}
     */
    public SpreadsheetColumn column() {
        return SpreadsheetColumn.with(this);
    }

    // SpreadsheetSelectionVisitor......................................................................................

    @Override
    void accept(final SpreadsheetSelectionVisitor visitor) {
        visitor.visit(this);
    }

    // TreePrintable....................................................................................................

    @Override
    public String selectionTypeName() {
        return "column";
    }

    // range/columnRange.......................................................................................

    /**
     * Creates a {@link Range} from the this and the other {@link SpreadsheetColumnReference}.
     */
    public Range<SpreadsheetColumnReference> range(final SpreadsheetColumnReference other) {
        Objects.requireNonNull(other, "other");

        return createRange(this, other);
    }

    /**
     * Creates a {@link SpreadsheetColumnReferenceRange} from the this and the other {@link SpreadsheetColumnReference}.
     */
    public SpreadsheetColumnReferenceRange columnRange(final SpreadsheetColumnReference other) {
        return SpreadsheetColumnReferenceRange.with(
                this.range(other)
        );
    }

    /**
     * Returns a {@link SpreadsheetColumnReferenceRange} holding only this column.
     */
    public SpreadsheetColumnReferenceRange columnRange() {
        return this.columnRange(this);
    }

    // SpreadsheetViewportSelectionNavigation...........................................................................

    @Override
    public SpreadsheetViewportSelectionAnchor defaultAnchor() {
        return SpreadsheetViewportSelectionAnchor.COLUMN;
    }

    @Override
    public boolean isHidden(final Predicate<SpreadsheetColumnReference> hiddenColumnTester,
                            final Predicate<SpreadsheetRowReference> hiddenRowTester) {
        return hiddenColumnTester.test(this);
    }

    @Override
    Optional<SpreadsheetSelection> left(final SpreadsheetViewportSelectionAnchor anchor,
                                        final SpreadsheetColumnStore columnStore,
                                        final SpreadsheetRowStore rowStore) {
        return Cast.to(
                columnStore.leftSkipHidden(this)
        );
    }

    @Override
    Optional<SpreadsheetSelection> right(final SpreadsheetViewportSelectionAnchor anchor,
                                         final SpreadsheetColumnStore columnStore,
                                         final SpreadsheetRowStore rowStore) {
        return Cast.to(
                columnStore.rightSkipHidden(this)
        );
    }

    @Override
    Optional<SpreadsheetSelection> up(final SpreadsheetViewportSelectionAnchor anchor,
                                      final SpreadsheetColumnStore columnStore,
                                      final SpreadsheetRowStore rowStore) {
        return this.emptyIfHidden(
                columnStore,
                rowStore
        );
    }

    @Override
    Optional<SpreadsheetSelection> down(final SpreadsheetViewportSelectionAnchor anchor,
                                        final SpreadsheetColumnStore columnStore,
                                        final SpreadsheetRowStore rowStore) {
        return this.emptyIfHidden(
                columnStore,
                rowStore
        );
    }

    @Override
    Optional<SpreadsheetViewportSelection> extendLeft(final SpreadsheetViewportSelectionAnchor anchor,
                                                      final SpreadsheetColumnStore columnStore,
                                                      final SpreadsheetRowStore rowStore) {
        return this.extendRange(
                this.left(anchor, columnStore, rowStore),
                anchor
        ).map(
                s -> s.setAnchorOrDefault(SpreadsheetViewportSelectionAnchor.RIGHT)
        );
    }

    @Override
    Optional<SpreadsheetViewportSelection> extendRight(final SpreadsheetViewportSelectionAnchor anchor,
                                                       final SpreadsheetColumnStore columnStore,
                                                       final SpreadsheetRowStore rowStore) {
        return this.extendRange(
                this.right(anchor, columnStore, rowStore),
                anchor
        ).map(
                s -> s.setAnchorOrDefault(SpreadsheetViewportSelectionAnchor.LEFT)
        );
    }

    @Override
    Optional<SpreadsheetViewportSelection> extendUp(final SpreadsheetViewportSelectionAnchor anchor,
                                                    final SpreadsheetColumnStore columnStore,
                                                    final SpreadsheetRowStore rowStore) {
        return this.setAnchorEmptyIfHidden(
                anchor,
                columnStore,
                rowStore
        );
    }

    @Override
    Optional<SpreadsheetViewportSelection> extendDown(final SpreadsheetViewportSelectionAnchor anchor,
                                                      final SpreadsheetColumnStore columnStore,
                                                      final SpreadsheetRowStore rowStore) {
        return this.setAnchorEmptyIfHidden(
                anchor,
                columnStore,
                rowStore
        );
    }

    @Override
    Optional<SpreadsheetSelection> extendRange(final Optional<? extends SpreadsheetSelection> other,
                                               final SpreadsheetViewportSelectionAnchor anchor) {
        return other.map(
                o -> this.columnRange((SpreadsheetColumnReference) o).simplify()
        );
    }

    // focused...........................................................................................................

    @Override
    public SpreadsheetColumnReference focused(final SpreadsheetViewportSelectionAnchor anchor) {
        this.checkAnchor(anchor);
        return this;
    }

    // Object...........................................................................................................

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof SpreadsheetColumnReference;
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

    // COLUMN_OR_ROW_REFERENCE_COMPARATOR..............................................................................

    @Override
    int compareSpreadsheetColumnOrRowReference(final SpreadsheetColumnOrRowReference other) {
        return other.compareSpreadsheetColumnOrRowReference0(this);
    }

    @Override
    int compareSpreadsheetColumnOrRowReference0(final SpreadsheetColumnReference other) {
        return other.value - this.value;
    }

    @Override
    int compareSpreadsheetColumnOrRowReference0(final SpreadsheetRowReference other) {
        return 1; // columns are less than rows. but because of the dispatch this is reversed
    }
}

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
import walkingkooka.spreadsheet.SpreadsheetRow;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Represents a row reference. The {@link Comparable} method ignores the {@link SpreadsheetReferenceKind} component
 * only comparing the value.
 */
public final class SpreadsheetRowReference extends SpreadsheetColumnOrRowReference implements Comparable<SpreadsheetRowReference> {

    // https://support.office.com/en-us/article/excel-specifications-and-limits-1672b34d-7043-467e-8e27-269d656771c3
    public final static int MAX_VALUE = 1_048_576 - 1; // max value inclusive

    public final static int RADIX = 10;

    static SpreadsheetRowReference[] absoluteCache() {
        if (null == ABSOLUTE_CACHE) {
            ABSOLUTE_CACHE = fillCache(
                    i -> new SpreadsheetRowReference(
                            i,
                            SpreadsheetReferenceKind.ABSOLUTE
                    ),
                    new SpreadsheetRowReference[CACHE_SIZE]
            );
        }
        return ABSOLUTE_CACHE;
    }

    /**
     * Lazy cache to help prevent NPE from very early {@link SpreadsheetReferenceKind#firstColumn()}
     */
    private static SpreadsheetRowReference[] ABSOLUTE_CACHE;

    static SpreadsheetRowReference[] relativeCache() {
        if (null == RELATIVE_CACHE) {
            RELATIVE_CACHE = fillCache(
                    i -> new SpreadsheetRowReference(
                            i,
                            SpreadsheetReferenceKind.RELATIVE
                    ),
                    new SpreadsheetRowReference[CACHE_SIZE]
            );
        }
        return RELATIVE_CACHE;
    }

    private static SpreadsheetRowReference[] RELATIVE_CACHE;

    /**
     * Factory that creates a new row.
     */
    static SpreadsheetRowReference with(final int value, final SpreadsheetReferenceKind referenceKind) {
        checkValue(value);
        checkReferenceKind(referenceKind);

        return value < CACHE_SIZE ?
                referenceKind.rowFromCache(value) :
                new SpreadsheetRowReference(value, referenceKind);
    }

    private static String invalidRowValue(final int value) {
        return "Invalid row value " + value + " expected between 0 and " + (MAX_VALUE + 1);
    }

    private SpreadsheetRowReference(final int value, final SpreadsheetReferenceKind referenceKind) {
        super(value, referenceKind);
    }

    /**
     * Would be setter that returns a {@link SpreadsheetRowReference} with the given {@link SpreadsheetReferenceKind}
     * creating a new instance if necessary.
     */
    @Override
    public SpreadsheetRowReference setReferenceKind(final SpreadsheetReferenceKind referenceKind) {
        checkReferenceKind(referenceKind);

        return (SpreadsheetRowReference) this.setReferenceKind0(referenceKind);
    }

    @Override
    SpreadsheetRowReference replaceReferenceKind(final SpreadsheetReferenceKind referenceKind) {
        return new SpreadsheetRowReference(this.value, referenceKind);
    }

    @Override
    public SpreadsheetRowReference add(final int value) {
        return Cast.to(this.add0(value));
    }

    @Override
    public SpreadsheetRowReference addSaturated(final int value) {
        return Cast.to(this.addSaturated0(value));
    }

    @Override
    int max() {
        return MAX_VALUE;
    }

    /**
     * Would be setter that returns a {@link SpreadsheetRowReference} with the given value creating a new
     * instance if it is different.
     */
    public SpreadsheetRowReference setValue(final int value) {
        checkValue(value);
        return this.value == value ?
                this :
                new SpreadsheetRowReference(value, this.referenceKind());
    }

    private static void checkValue(final int value) {
        if (value < 0 || value > MAX_VALUE) {
            throw new IllegalArgumentException(invalidRowValue(value));
        }
    }

    /**
     * Creates a {@link SpreadsheetCellReference} from this row and the given column.
     */
    public SpreadsheetCellReference setColumn(final SpreadsheetColumnReference column) {
        return column.setRow(this);
    }

    // SpreadsheetRow................................................................................................

    /**
     * Factory that returns a {@link SpreadsheetRow} with this {@link SpreadsheetRowReference}
     */
    public SpreadsheetRow row() {
        return SpreadsheetRow.with(this);
    }

    public String hateosLinkId() {
        return String.valueOf(this.value + 1);
    }

    // max..............................................................................................................

    /**
     * Returns the max or bottom most row.
     */
    public SpreadsheetRowReference max(final SpreadsheetRowReference other) {
        Objects.requireNonNull(other, "other");
        return this.value >= other.value ?
                this :
                other;
    }

    // min..............................................................................................................

    /**
     * Returns the min or top most row.
     */
    public SpreadsheetRowReference min(final SpreadsheetRowReference other) {
        Objects.requireNonNull(other, "other");
        return this.value <= other.value ?
                this :
                other;
    }

    // testXXX.........................................................................................................

    /**
     * Returns true if the given {@link SpreadsheetCellReference} has this row.
     */
    @Override
    boolean testCell0(final SpreadsheetCellReference cell) {
        return this.testRow0(cell.row());
    }

    @Override
    boolean testCellRange0(final SpreadsheetCellRange range) {
        checkCellRange(range);

        return this.compareTo(range.begin().row()) >= 0 &&
                this.compareTo(range.end().row()) <= 0;
    }

    @Override
    boolean testColumn0(final SpreadsheetColumnReference column) {
        return false;
    }

    @Override
    boolean testRow0(final SpreadsheetRowReference row) {
        return this.equalsIgnoreReferenceKind(row);
    }

    // range/rowRange.......................................................................................

    /**
     * Creates a {@link Range} from the this and the other {@link SpreadsheetRowReference}.
     */
    public Range<SpreadsheetRowReference> range(final SpreadsheetRowReference other) {
        Objects.requireNonNull(other, "other");

        return createRange(
                this,
                other
        );
    }

    /**
     * Creates a {@link SpreadsheetRowReferenceRange} from the this and the other {@link SpreadsheetRowReference}.
     */
    public SpreadsheetRowReferenceRange rowRange(final SpreadsheetRowReference other) {
        return SpreadsheetRowReferenceRange.with(this.range(other));
    }

    @Override
    public SpreadsheetColumnReference toColumn() {
        throw new UnsupportedOperationException(this.toString());
    }

    @Override
    public SpreadsheetColumnReferenceRange toColumnRange() {
        throw new UnsupportedOperationException(this.toString());
    }

    @Override
    public SpreadsheetRowReference toRow() {
        return this;
    }

    /**
     * Returns a {@link SpreadsheetRowReferenceRange} holding only this row.
     */
    public SpreadsheetRowReferenceRange toRowRange() {
        return SpreadsheetRowReferenceRange.with(Range.singleton(this));
    }

    // toRelative.......................................................................................................

    @Override
    public SpreadsheetRowReference toRelative() {
        return this.setReferenceKind(SpreadsheetReferenceKind.RELATIVE);
    }

    // SpreadsheetSelectionVisitor......................................................................................

    @Override
    void accept(final SpreadsheetSelectionVisitor visitor) {
        visitor.visit(this);
    }

    // SpreadsheetViewportSelectionNavigation...........................................................................

    @Override
    public SpreadsheetViewportSelectionAnchor defaultAnchor() {
        return SpreadsheetViewportSelectionAnchor.ROW;
    }

    @Override
    public boolean isHidden(final Predicate<SpreadsheetColumnReference> hiddenColumnTester,
                            final Predicate<SpreadsheetRowReference> hiddenRowTester) {
        return hiddenRowTester.test(this);
    }

    @Override
    Optional<SpreadsheetSelection> leftColumn(final SpreadsheetViewportSelectionAnchor anchor,
                                              final SpreadsheetViewportSelectionNavigationContext context) {
        return this.emptyIfHidden(context);
    }

    @Override
    Optional<SpreadsheetSelection> leftPixels(final SpreadsheetViewportSelectionAnchor anchor,
                                              final int count,
                                              final SpreadsheetViewportSelectionNavigationContext context) {
        return this.emptyIfHidden(context);
    }

    @Override
    Optional<SpreadsheetSelection> rightColumn(final SpreadsheetViewportSelectionAnchor anchor,
                                               final SpreadsheetViewportSelectionNavigationContext context) {
        return this.emptyIfHidden(context);
    }

    @Override
    Optional<SpreadsheetSelection> rightPixels(final SpreadsheetViewportSelectionAnchor anchor,
                                               final int count,
                                               final SpreadsheetViewportSelectionNavigationContext context) {
        return this.emptyIfHidden(context);
    }

    @Override
    Optional<SpreadsheetSelection> upRow(final SpreadsheetViewportSelectionAnchor anchor,
                                         final SpreadsheetViewportSelectionNavigationContext context) {
        return Cast.to(
                context.upRow(
                        this
                )
        );
    }

    @Override
    Optional<SpreadsheetSelection> upPixels(final SpreadsheetViewportSelectionAnchor anchor,
                                            final int count,
                                            final SpreadsheetViewportSelectionNavigationContext context) {
        return Cast.to(
                context.upPixels(
                        this,
                        count
                )
        );
    }

    @Override
    Optional<SpreadsheetSelection> downRow(final SpreadsheetViewportSelectionAnchor anchor,
                                           final SpreadsheetViewportSelectionNavigationContext context) {
        return Cast.to(
                context.downRow(this)
        );
    }


    @Override
    Optional<SpreadsheetSelection> downPixels(final SpreadsheetViewportSelectionAnchor anchor,
                                              final int count,
                                              final SpreadsheetViewportSelectionNavigationContext context) {
        return Cast.to(
                context.downPixels(
                        this,
                        count
                )
        );
    }

    @Override
    Optional<SpreadsheetViewportSelection> extendLeftColumn(final SpreadsheetViewportSelectionAnchor anchor,
                                                            final SpreadsheetViewportSelectionNavigationContext context) {
        return this.setAnchorEmptyIfHidden(
                anchor,
                context
        );
    }

    @Override
    Optional<SpreadsheetViewportSelection> extendLeftPixels(final SpreadsheetViewportSelectionAnchor anchor,
                                                            final int count,
                                                            final SpreadsheetViewportSelectionNavigationContext context) {
        return this.setAnchorEmptyIfHidden(
                anchor,
                context
        );
    }

    @Override
    Optional<SpreadsheetViewportSelection> extendRightColumn(final SpreadsheetViewportSelectionAnchor anchor,
                                                             final SpreadsheetViewportSelectionNavigationContext context) {
        return this.setAnchorEmptyIfHidden(
                anchor,
                context
        );
    }

    @Override
    Optional<SpreadsheetViewportSelection> extendRightPixels(final SpreadsheetViewportSelectionAnchor anchor,
                                                             final int count,
                                                             final SpreadsheetViewportSelectionNavigationContext context) {
        return this.setAnchorEmptyIfHidden(
                anchor,
                context
        );
    }

    @Override
    Optional<SpreadsheetViewportSelection> extendUpRow(final SpreadsheetViewportSelectionAnchor anchor,
                                                       final SpreadsheetViewportSelectionNavigationContext context) {
        return this.extendRange(
                this.upRow(
                        anchor,
                        context
                ),
                anchor
        ).map(
                s -> s.setAnchorOrDefault(SpreadsheetViewportSelectionAnchor.BOTTOM)
        );
    }

    @Override
    Optional<SpreadsheetViewportSelection> extendUpPixels(final SpreadsheetViewportSelectionAnchor anchor,
                                                          final int count,
                                                          final SpreadsheetViewportSelectionNavigationContext context) {
        return this.extendRange(
                this.upPixels(
                        anchor,
                        count,
                        context
                ),
                anchor
        ).map(
                s -> s.setAnchorOrDefault(SpreadsheetViewportSelectionAnchor.BOTTOM)
        );
    }

    @Override
    Optional<SpreadsheetViewportSelection> extendDownRow(final SpreadsheetViewportSelectionAnchor anchor,
                                                         final SpreadsheetViewportSelectionNavigationContext context) {
        return this.extendRange(
                this.downRow(
                        anchor,
                        context
                ),
                anchor
        ).map(
                s -> s.setAnchorOrDefault(SpreadsheetViewportSelectionAnchor.TOP)
        );
    }

    @Override
    Optional<SpreadsheetViewportSelection> extendDownPixels(final SpreadsheetViewportSelectionAnchor anchor,
                                                            final int count,
                                                            final SpreadsheetViewportSelectionNavigationContext context) {
        return this.extendRange(
                this.downPixels(
                        anchor,
                        count,
                        context
                ),
                anchor
        ).map(
                s -> s.setAnchorOrDefault(SpreadsheetViewportSelectionAnchor.TOP)
        );
    }

    @Override
    Optional<SpreadsheetSelection> extendRange(final Optional<? extends SpreadsheetSelection> other,
                                               final SpreadsheetViewportSelectionAnchor anchor) {
        return other.map(
                o -> this.rowRange((SpreadsheetRowReference) o).simplify()
        );
    }

    // focused...........................................................................................................

    @Override
    public SpreadsheetRowReference focused(final SpreadsheetViewportSelectionAnchor anchor) {
        this.checkAnchor(anchor);
        return this;
    }

    // Object...........................................................................................................

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof SpreadsheetRowReference;
    }

    @Override
    public String toString() {
        // in text form columns start at 1 but internally are zero based.
        return this.referenceKind().prefix() + (this.value + 1);
    }

    // Comparable......................................................................................................

    @Override
    public int compareTo(final SpreadsheetRowReference other) {
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
        return -1; // rows are more than columns, but because of dispatch this is reversed
    }

    @Override
    int compareSpreadsheetColumnOrRowReference0(final SpreadsheetRowReference other) {
        return other.value - this.value;
    }
}

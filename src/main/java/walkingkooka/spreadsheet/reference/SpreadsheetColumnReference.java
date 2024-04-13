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
import walkingkooka.spreadsheet.parser.SpreadsheetColumnReferenceParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
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

    static SpreadsheetColumnReference[] absoluteCache() {
        if (null == ABSOLUTE_CACHE) {
            ABSOLUTE_CACHE = fillCache(
                    i -> new SpreadsheetColumnReference(
                            i,
                            SpreadsheetReferenceKind.ABSOLUTE
                    ),
                    new SpreadsheetColumnReference[CACHE_SIZE]
            );
        }
        return ABSOLUTE_CACHE;
    }

    /**
     * Lazy cache to help prevent NPE parse very early {@link SpreadsheetReferenceKind#firstColumn()}
     */
    private static SpreadsheetColumnReference[] ABSOLUTE_CACHE;

    static SpreadsheetColumnReference[] relativeCache() {
        if (null == RELATIVE_CACHE) {
            RELATIVE_CACHE = fillCache(
                    i -> new SpreadsheetColumnReference(
                            i,
                            SpreadsheetReferenceKind.RELATIVE
                    ),
                    new SpreadsheetColumnReference[CACHE_SIZE]
            );
        }
        return RELATIVE_CACHE;
    }

    private static SpreadsheetColumnReference[] RELATIVE_CACHE;

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
        return this.add0(value)
                .toColumn();
    }

    @Override
    public SpreadsheetColumnReference addSaturated(final int value) {
        return this.addSaturated0(value)
                .toColumn();
    }

    @Override
    public SpreadsheetColumnReference add(final int column,
                                          final int row) {
        checkRowDeltaIsZero(row);
        return this.add(column);
    }

    @Override
    public SpreadsheetColumnReference addSaturated(final int column,
                                                   final int row) {
        checkRowDeltaIsZero(row);
        return this.addSaturated(column);
    }

    @Override
    public SpreadsheetColumnReference addIfRelative(final int delta) {
        return 0 == delta || this.referenceKind() == SpreadsheetReferenceKind.ABSOLUTE ?
                this :
                this.add(delta);
    }

    // replaceReferencesMapper..........................................................................................

    @Override
    Function<SpreadsheetCellReference, Optional<SpreadsheetCellReference>> replaceReferencesMapper0(final SpreadsheetSelection moveTo) {
        if (moveTo.isRowReference() || moveTo.isRowRangeReference()) {
            throw new IllegalArgumentException("Expected column(s) or cell(s) but got " + moveTo);
        }
        return SpreadsheetSelectionReplaceReferencesMapperFunction.with(
                moveTo.toColumn().value() - this.value,
                0
        );
    }

    // max.............................................................................................................

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

    // testXXX.........................................................................................................

    /**
     * Returns true if the given {@link SpreadsheetCellReference} has this column.
     */
    @Override
    boolean testCell0(final SpreadsheetCellReference cell) {
        return this.testColumn0(cell.column());
    }

    @Override
    boolean testCellRange0(final SpreadsheetCellRangeReference range) {
        checkCellRange(range);

        return this.compareTo(range.begin().column()) >= 0 &&
                this.compareTo(range.end().column()) <= 0;
    }

    @Override
    boolean testColumn0(final SpreadsheetColumnReference column) {
        return this.equalsIgnoreReferenceKind(column);
    }

    @Override
    boolean testRow0(final SpreadsheetRowReference row) {
        return false;
    }

    // toCell............................................................................................................

    @Override
    public SpreadsheetCellReference toCell() {
        return this.setRow(
                SpreadsheetReferenceKind.RELATIVE.firstRow()
        );
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

    // range/columnRange.......................................................................................

    /**
     * Creates a {@link Range} with the given {@link SpreadsheetColumnReference}.
     */
    public Range<SpreadsheetColumnReference> range(final SpreadsheetColumnReference other) {
        Objects.requireNonNull(other, "other");

        return createRange(this, other);
    }

    /**
     * Creates a {@link SpreadsheetColumnRangeReference} using the given {@link SpreadsheetColumnReference}.
     */
    public SpreadsheetColumnRangeReference columnRange(final SpreadsheetColumnReference other) {
        return SpreadsheetColumnRangeReference.with(
                this.range(other)
        );
    }

    @Override
    public SpreadsheetColumnReference toColumn() {
        return this;
    }

    /**
     * Returns a {@link SpreadsheetColumnRangeReference} holding only this column.
     */
    @Override
    public SpreadsheetColumnRangeReference toColumnRange() {
        return this.columnRange(this);
    }

    @Override
    public SpreadsheetRowReference toRow() {
        throw new UnsupportedOperationException(this.toString());
    }

    @Override
    public SpreadsheetRowRangeReference toRowRange() {
        throw new UnsupportedOperationException(this.toString());
    }

    // pick.............................................................................................................

    @Override
    public <T> T pick(final T cellOrCellRangeOrLabel,
                      final T columnOrColumnRange,
                      final T rowOrRowRange) {
        return columnOrColumnRange;
    }

    // SpreadsheetViewportNavigation....................................................................................

    @Override
    public SpreadsheetViewportAnchor defaultAnchor() {
        return SpreadsheetViewportAnchor.COLUMN;
    }

    @Override
    public boolean isHidden(final Predicate<SpreadsheetColumnReference> hiddenColumnTester,
                            final Predicate<SpreadsheetRowReference> hiddenRowTester) {
        return hiddenColumnTester.test(this);
    }

    @Override
    Optional<SpreadsheetSelection> leftColumn(final SpreadsheetViewportAnchor anchor,
                                              final SpreadsheetViewportNavigationContext context) {
        return Cast.to(
                context.leftColumn(this)
        );
    }

    @Override
    Optional<SpreadsheetSelection> leftPixels(final SpreadsheetViewportAnchor anchor,
                                              final int count,
                                              final SpreadsheetViewportNavigationContext context) {
        return Cast.to(
                context.leftPixels(
                        this,
                        count
                )
        );
    }

    @Override
    Optional<SpreadsheetSelection> rightColumn(final SpreadsheetViewportAnchor anchor,
                                               final SpreadsheetViewportNavigationContext context) {
        return Cast.to(
                context.rightColumn(this)
        );
    }

    @Override
    Optional<SpreadsheetSelection> rightPixels(final SpreadsheetViewportAnchor anchor,
                                               final int count,
                                               final SpreadsheetViewportNavigationContext context) {
        return Cast.to(
                context.rightPixels(
                        this,
                        count
                )
        );
    }

    @Override
    Optional<SpreadsheetSelection> upRow(final SpreadsheetViewportAnchor anchor,
                                         final SpreadsheetViewportNavigationContext context) {
        return this.emptyIfHidden(context);
    }

    @Override
    Optional<SpreadsheetSelection> upPixels(final SpreadsheetViewportAnchor anchor,
                                            final int count,
                                            final SpreadsheetViewportNavigationContext context) {
        return this.emptyIfHidden(context);
    }

    @Override
    Optional<SpreadsheetSelection> downRow(final SpreadsheetViewportAnchor anchor,
                                           final SpreadsheetViewportNavigationContext context) {
        return this.emptyIfHidden(context);
    }

    @Override
    Optional<SpreadsheetSelection> downPixels(final SpreadsheetViewportAnchor anchor,
                                              final int count,
                                              final SpreadsheetViewportNavigationContext context) {
        return this.emptyIfHidden(context);
    }

    @Override
    Optional<AnchoredSpreadsheetSelection> extendLeftColumn(final SpreadsheetViewportAnchor anchor,
                                                            final SpreadsheetViewportNavigationContext context) {
        return this.extendRange(
                this.leftColumn(
                        anchor,
                        context
                ),
                anchor
        ).map(
                s -> s.setAnchorOrDefault(SpreadsheetViewportAnchor.RIGHT)
        );
    }

    @Override
    Optional<AnchoredSpreadsheetSelection> extendLeftPixels(final SpreadsheetViewportAnchor anchor,
                                                            final int count,
                                                            final SpreadsheetViewportNavigationContext context) {
        return this.extendRange(
                this.leftPixels(
                        anchor,
                        count,
                        context
                ),
                anchor
        ).map(
                s -> s.setAnchorOrDefault(SpreadsheetViewportAnchor.RIGHT)
        );
    }

    @Override
    Optional<AnchoredSpreadsheetSelection> extendRightColumn(final SpreadsheetViewportAnchor anchor,
                                                             final SpreadsheetViewportNavigationContext context) {
        return this.extendRange(
                this.rightColumn(
                        anchor,
                        context
                ),
                anchor
        ).map(
                s -> s.setAnchorOrDefault(SpreadsheetViewportAnchor.LEFT)
        );
    }

    @Override
    Optional<AnchoredSpreadsheetSelection> extendRightPixels(final SpreadsheetViewportAnchor anchor,
                                                             final int count,
                                                             final SpreadsheetViewportNavigationContext context) {
        return this.extendRange(
                this.rightPixels(
                        anchor,
                        count,
                        context
                ),
                anchor
        ).map(
                s -> s.setAnchorOrDefault(SpreadsheetViewportAnchor.LEFT)
        );
    }

    @Override
    Optional<AnchoredSpreadsheetSelection> extendUpRow(final SpreadsheetViewportAnchor anchor,
                                                       final SpreadsheetViewportNavigationContext context) {
        return this.setAnchorEmptyIfHidden(
                anchor,
                context
        );
    }

    @Override
    Optional<AnchoredSpreadsheetSelection> extendUpPixels(final SpreadsheetViewportAnchor anchor,
                                                          final int count,
                                                          final SpreadsheetViewportNavigationContext context) {
        return this.setAnchorEmptyIfHidden(
                anchor,
                context
        );
    }

    @Override
    Optional<AnchoredSpreadsheetSelection> extendDownRow(final SpreadsheetViewportAnchor anchor,
                                                         final SpreadsheetViewportNavigationContext context) {
        return this.setAnchorEmptyIfHidden(
                anchor,
                context
        );
    }


    @Override
    Optional<AnchoredSpreadsheetSelection> extendDownPixels(final SpreadsheetViewportAnchor anchor,
                                                            final int count,
                                                            final SpreadsheetViewportNavigationContext context) {
        return this.setAnchorEmptyIfHidden(
                anchor,
                context
        );
    }

    @Override
    Optional<SpreadsheetSelection> extendRange(final Optional<? extends SpreadsheetSelection> other,
                                               final SpreadsheetViewportAnchor anchor) {
        return other.map(
                o -> this.columnRange((SpreadsheetColumnReference) o)
                        .toScalarIfUnit()
        );
    }

    // focused...........................................................................................................

    @Override
    public SpreadsheetColumnReference focused(final SpreadsheetViewportAnchor anchor) {
        this.checkAnchor(anchor);
        return this;
    }

    // HasParserToken...................................................................................................

    @Override
    public SpreadsheetColumnReferenceParserToken toParserToken() {
        return SpreadsheetParserToken.columnReference(
                this,
                this.text()
        );
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

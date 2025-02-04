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
import walkingkooka.spreadsheet.formula.RowSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.SpreadsheetFormulaParserToken;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
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
     * Lazy cache to help prevent NPE parse very early {@link SpreadsheetReferenceKind#firstColumn()}
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

    @Override
    public SpreadsheetColumnOrRowReferenceKind columnOrRowReferenceKind() {
        return SpreadsheetColumnOrRowReferenceKind.ROW;
    }

    /**
     * Would be setter that returns a {@link SpreadsheetRowReference} with the given {@link SpreadsheetReferenceKind}
     * creating a new instance if necessary.
     */
    @Override
    public SpreadsheetRowReference setReferenceKind(final SpreadsheetReferenceKind referenceKind) {
        checkReferenceKind(referenceKind);

        return this.setReferenceKind0(referenceKind).toRow();
    }

    @Override
    SpreadsheetRowReference replaceReferenceKind(final SpreadsheetReferenceKind referenceKind) {
        return new SpreadsheetRowReference(this.value, referenceKind);
    }

    @Override
    public SpreadsheetRowReference add(final int value) {
        return this.add0(value)
                .toRow();
    }

    @Override
    public SpreadsheetRowReference addSaturated(final int value) {
        return this.addSaturated0(value)
                .toRow();
    }

    @Override
    public SpreadsheetRowReference add(final int column,
                                       final int row) {
        checkColumnDeltaIsZero(column);
        return this.add(row);
    }

    @Override
    public SpreadsheetRowReference addSaturated(final int column,
                                                final int row) {
        checkColumnDeltaIsZero(column);
        return this.addSaturated(row);
    }

    @Override
    public SpreadsheetRowReference addIfRelative(final int delta) {
        return 0 == delta || this.referenceKind() == SpreadsheetReferenceKind.ABSOLUTE ?
                this :
                this.add(delta);
    }

    // replaceReferencesMapper..........................................................................................

    @Override
    Optional<Function<SpreadsheetCellReference, Optional<SpreadsheetCellReference>>> replaceReferencesMapper0(final SpreadsheetSelection moveTo) {
        if (moveTo.isColumn() || moveTo.isColumnRange()) {
            throw new IllegalArgumentException("Expected rows(s) or cell(s) but got " + moveTo);
        }

        final int delta = moveTo.toRow()
                .value() -
                this.value;

        return Optional.ofNullable(
                0 != delta ?
                        SpreadsheetSelectionReplaceReferencesMapperFunction.with(
                                0,
                                delta
                        ) :
                        null
        );
    }

    // max..............................................................................................................

    @Override
    int max() {
        return MAX_VALUE;
    }

    /**
     * Would be setter that returns a {@link SpreadsheetRowReference} with the given value creating a new
     * instance if it is different.
     */
    @Override
    public SpreadsheetRowReference setValue(final int value) {
        checkValue(value);
        return this.value == value ?
                this :
                new SpreadsheetRowReference(value, this.referenceKind());
    }

    private static void checkValue(final int value) {
        if (value < 0 || value > MAX_VALUE) {
            throw new IllegalRowArgumentException(
                    invalidRowValue(value)
            );
        }
    }

    /**
     * Creates a {@link SpreadsheetCellReference} parse this row and the given column.
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
    boolean testCellRange0(final SpreadsheetCellRangeReference range) {
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
     * Creates a {@link Range} using the given {@link SpreadsheetRowReference}.
     */
    public Range<SpreadsheetRowReference> range(final SpreadsheetRowReference other) {
        Objects.requireNonNull(other, "other");

        return createRange(
                this,
                other
        );
    }

    /**
     * Creates a {@link SpreadsheetRowRangeReference} using the given {@link SpreadsheetRowReference}.
     */
    public SpreadsheetRowRangeReference rowRange(final SpreadsheetRowReference other) {
        return SpreadsheetRowRangeReference.with(this.range(other));
    }

    // toCell............................................................................................................

    @Override
    public SpreadsheetCellReference toCell() {
        return this.setColumn(
                SpreadsheetReferenceKind.RELATIVE.firstColumn()
        );
    }

    @Override
    public SpreadsheetColumnReference toColumn() {
        throw new UnsupportedOperationException(this.toString());
    }

    @Override
    public SpreadsheetColumnRangeReference toColumnRange() {
        throw new UnsupportedOperationException(this.toString());
    }

    @Override
    public SpreadsheetRowReference toRow() {
        return this;
    }

    /**
     * Returns a {@link SpreadsheetRowRangeReference} holding only this row.
     */
    @Override
    public SpreadsheetRowRangeReference toRowRange() {
        return SpreadsheetRowRangeReference.with(Range.singleton(this));
    }

    // toRange..........................................................................................................

    @Override
    public SpreadsheetRowRangeReference toRange() {
        return this.toRowRange();
    }

    // toRelative.......................................................................................................

    @Override
    public SpreadsheetRowReference toRelative() {
        return this.setReferenceKind(SpreadsheetReferenceKind.RELATIVE);
    }

    // pick.............................................................................................................

    @Override
    public <T> T pick(final T cellOrCellRangeOrLabel,
                      final T columnOrColumnRange,
                      final T rowOrRowRange) {
        return rowOrRowRange;
    }

    // SpreadsheetSelectionVisitor......................................................................................

    @Override
    void accept(final SpreadsheetSelectionVisitor visitor) {
        visitor.visit(this);
    }

    // SpreadsheetViewportNavigation...........................................................................

    @Override
    public SpreadsheetViewportAnchor defaultAnchor() {
        return SpreadsheetViewportAnchor.ROW;
    }

    @Override
    public boolean isHidden(final Predicate<SpreadsheetColumnReference> hiddenColumnTester,
                            final Predicate<SpreadsheetRowReference> hiddenRowTester) {
        return hiddenRowTester.test(this);
    }

    @Override
    Optional<SpreadsheetSelection> leftColumn(final SpreadsheetViewportAnchor anchor,
                                              final SpreadsheetViewportNavigationContext context) {
        return this.emptyIfHidden(context);
    }

    @Override
    Optional<SpreadsheetSelection> leftPixels(final SpreadsheetViewportAnchor anchor,
                                              final int count,
                                              final SpreadsheetViewportNavigationContext context) {
        return this.emptyIfHidden(context);
    }

    @Override
    Optional<SpreadsheetSelection> rightColumn(final SpreadsheetViewportAnchor anchor,
                                               final SpreadsheetViewportNavigationContext context) {
        return this.emptyIfHidden(context);
    }

    @Override
    Optional<SpreadsheetSelection> rightPixels(final SpreadsheetViewportAnchor anchor,
                                               final int count,
                                               final SpreadsheetViewportNavigationContext context) {
        return this.emptyIfHidden(context);
    }

    @Override
    Optional<SpreadsheetSelection> upRow(final SpreadsheetViewportAnchor anchor,
                                         final SpreadsheetViewportNavigationContext context) {
        return Cast.to(
                context.upRow(
                        this
                )
        );
    }

    @Override
    Optional<SpreadsheetSelection> upPixels(final SpreadsheetViewportAnchor anchor,
                                            final int count,
                                            final SpreadsheetViewportNavigationContext context) {
        return Cast.to(
                context.upPixels(
                        this,
                        count
                )
        );
    }

    @Override
    Optional<SpreadsheetSelection> downRow(final SpreadsheetViewportAnchor anchor,
                                           final SpreadsheetViewportNavigationContext context) {
        return Cast.to(
                context.downRow(this)
        );
    }


    @Override
    Optional<SpreadsheetSelection> downPixels(final SpreadsheetViewportAnchor anchor,
                                              final int count,
                                              final SpreadsheetViewportNavigationContext context) {
        return Cast.to(
                context.downPixels(
                        this,
                        count
                )
        );
    }

    @Override
    Optional<AnchoredSpreadsheetSelection> extendLeftColumn(final SpreadsheetViewportAnchor anchor,
                                                            final SpreadsheetViewportNavigationContext context) {
        return this.setAnchorEmptyIfHidden(
                anchor,
                context
        );
    }

    @Override
    Optional<AnchoredSpreadsheetSelection> extendLeftPixels(final SpreadsheetViewportAnchor anchor,
                                                            final int count,
                                                            final SpreadsheetViewportNavigationContext context) {
        return this.setAnchorEmptyIfHidden(
                anchor,
                context
        );
    }

    @Override
    Optional<AnchoredSpreadsheetSelection> extendRightColumn(final SpreadsheetViewportAnchor anchor,
                                                             final SpreadsheetViewportNavigationContext context) {
        return this.setAnchorEmptyIfHidden(
                anchor,
                context
        );
    }

    @Override
    Optional<AnchoredSpreadsheetSelection> extendRightPixels(final SpreadsheetViewportAnchor anchor,
                                                             final int count,
                                                             final SpreadsheetViewportNavigationContext context) {
        return this.setAnchorEmptyIfHidden(
                anchor,
                context
        );
    }

    @Override
    Optional<AnchoredSpreadsheetSelection> extendUpRow(final SpreadsheetViewportAnchor anchor,
                                                       final SpreadsheetViewportNavigationContext context) {
        return this.extendRange(
                this.upRow(
                        anchor,
                        context
                ),
                anchor
        ).map(
                s -> s.setAnchorOrDefault(SpreadsheetViewportAnchor.BOTTOM)
        );
    }

    @Override
    Optional<AnchoredSpreadsheetSelection> extendUpPixels(final SpreadsheetViewportAnchor anchor,
                                                          final int count,
                                                          final SpreadsheetViewportNavigationContext context) {
        return this.extendRange(
                this.upPixels(
                        anchor,
                        count,
                        context
                ),
                anchor
        ).map(
                s -> s.setAnchorOrDefault(SpreadsheetViewportAnchor.BOTTOM)
        );
    }

    @Override
    Optional<AnchoredSpreadsheetSelection> extendDownRow(final SpreadsheetViewportAnchor anchor,
                                                         final SpreadsheetViewportNavigationContext context) {
        return this.extendRange(
                this.downRow(
                        anchor,
                        context
                ),
                anchor
        ).map(
                s -> s.setAnchorOrDefault(SpreadsheetViewportAnchor.TOP)
        );
    }

    @Override
    Optional<AnchoredSpreadsheetSelection> extendDownPixels(final SpreadsheetViewportAnchor anchor,
                                                            final int count,
                                                            final SpreadsheetViewportNavigationContext context) {
        return this.extendRange(
                this.downPixels(
                        anchor,
                        count,
                        context
                ),
                anchor
        ).map(
                s -> s.setAnchorOrDefault(SpreadsheetViewportAnchor.TOP)
        );
    }

    @Override
    Optional<SpreadsheetSelection> extendRange(final Optional<? extends SpreadsheetSelection> other,
                                               final SpreadsheetViewportAnchor anchor) {
        return other.map(
                o -> this.rowRange((SpreadsheetRowReference) o)
                        .toScalarIfUnit()
        );
    }

    // focused...........................................................................................................

    @Override
    public SpreadsheetRowReference focused(final SpreadsheetViewportAnchor anchor) {
        this.checkAnchor(anchor);
        return this;
    }

    // HasParserToken...................................................................................................

    @Override
    public RowSpreadsheetFormulaParserToken toParserToken() {
        return SpreadsheetFormulaParserToken.row(
                this,
                this.text()
        );
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

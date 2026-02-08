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
import walkingkooka.Value;
import walkingkooka.collect.Range;
import walkingkooka.spreadsheet.formula.parser.RowSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.value.SpreadsheetRow;
import walkingkooka.spreadsheet.viewport.AnchoredSpreadsheetSelection;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewportAnchor;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewportNavigationContext;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Represents a row reference. The {@link Comparable} method ignores the {@link SpreadsheetReferenceKind} component
 * only comparing the value.
 */
public final class SpreadsheetRowReference extends SpreadsheetRowReferenceOrRange
    implements Value<Integer>,
    Comparable<SpreadsheetRowReference>,
    SpreadsheetColumnOrRowReference{

    final static int MIN_VALUE = 0;

    // https://support.office.com/en-us/article/excel-specifications-and-limits-1672b34d-7043-467e-8e27-269d656771c3
    final static int MAX_VALUE = 1_048_576 - 1; // max value inclusive

    final static int RADIX = 10;

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
        Objects.requireNonNull(referenceKind, "referenceKind");

        return value < CACHE_SIZE ?
            referenceKind.rowFromCache(value) :
            new SpreadsheetRowReference(value, referenceKind);
    }

    private SpreadsheetRowReference(final int value,
                                    final SpreadsheetReferenceKind referenceKind) {
        super();

        this.value = value;
        this.referenceKind = referenceKind;
    }

    @Override
    public Integer value() {
        return this.value;
    }

    final int value;

    /**
     * Would be setter that returns a {@link SpreadsheetRowReference} with the given value creating a new
     * instance if it is different.
     */
    public SpreadsheetRowReference setValue(final int value) {
        return this.value == value ?
            this :
            new SpreadsheetRowReference(
                checkValue(value),
                this.referenceKind()
            );
    }

    private static int checkValue(final int value) {
        if (value < SpreadsheetRowReference.MIN_VALUE || value > MAX_VALUE) {
            throw new IllegalRowArgumentException(
                "Invalid row=" + value + " not between " + MIN_VALUE + " and " + (MAX_VALUE + 1)
            );
        }
        return value;
    }

    public SpreadsheetReferenceKind referenceKind() {
        return this.referenceKind;
    }

    private final SpreadsheetReferenceKind referenceKind;

    /**
     * Would be setter that returns a {@link SpreadsheetRowReference} with the given {@link SpreadsheetReferenceKind}
     * creating a new instance if necessary.
     */
    public SpreadsheetRowReference setReferenceKind(final SpreadsheetReferenceKind referenceKind) {
        Objects.requireNonNull(referenceKind, "referenceKind");

        return this.referenceKind == referenceKind ?
            this :
            new SpreadsheetRowReference(
                this.value,
                referenceKind
            );
    }

    @Override
    public Set<SpreadsheetViewportAnchor> anchors() {
        return NONE_ANCHORS;
    }

    // addXXX...........................................................................................................

    @Override
    public SpreadsheetRowReference add(final int value) {
        return this.setValue(
            this.value + value
        );
    }

    @Override
    public SpreadsheetRowReference addSaturated(final int value) {
        return this.setValue(
            Math.min(
                Math.max(
                    this.value + value,
                    MIN_VALUE
                ),
                MAX_VALUE
            )
        );
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
        return this.referenceKind() == SpreadsheetReferenceKind.RELATIVE ?
            this.add(delta) :
            this;
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
    boolean testCellNonNull(final SpreadsheetCellReference cell) {
        return this.testRowNonNull(cell.row());
    }

    @Override
    boolean testCellRangeNonNull(final SpreadsheetCellRangeReference range) {
        Objects.requireNonNull(range, "range");

        return this.compareTo(range.begin().row()) >= 0 &&
            this.compareTo(range.end().row()) <= 0;
    }

    @Override
    boolean testRowNonNull(final SpreadsheetRowReference row) {
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

    // isXXX............................................................................................................

    @Override
    public long count() {
        return 1;
    }

    /**
     * Only returns true if this is the first column or row.
     */
    @Override
    public boolean isFirst() {
        return MIN_VALUE == this.value;
    }

    /**
     * Only returns true if this is the last column or row.
     */
    @Override
    public boolean isLast() {
        return MAX_VALUE == this.value;
    }

    // toXXX............................................................................................................

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

    /**
     * A column or row is already simplified.
     */
    @Override
    public SpreadsheetSelection toScalar() {
        return this;
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

    // SpreadsheetSelectionVisitor......................................................................................

    @Override
    void accept(final SpreadsheetSelectionVisitor visitor) {
        visitor.visit(this);
    }

    // SpreadsheetViewportNavigation....................................................................................

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
    public Optional<SpreadsheetSelection> moveLeftColumn(final SpreadsheetViewportAnchor anchor,
                                                         final SpreadsheetViewportNavigationContext context) {
        return this.emptyIfHidden(context);
    }

    @Override
    public Optional<SpreadsheetSelection> moveLeftPixels(final SpreadsheetViewportAnchor anchor,
                                                         final int count,
                                                         final SpreadsheetViewportNavigationContext context) {
        return this.emptyIfHidden(context);
    }

    @Override
    public Optional<SpreadsheetSelection> moveRightColumn(final SpreadsheetViewportAnchor anchor,
                                                          final SpreadsheetViewportNavigationContext context) {
        return this.emptyIfHidden(context);
    }

    @Override
    public Optional<SpreadsheetSelection> moveRightPixels(final SpreadsheetViewportAnchor anchor,
                                                          final int count,
                                                          final SpreadsheetViewportNavigationContext context) {
        return this.emptyIfHidden(context);
    }

    @Override
    public Optional<SpreadsheetSelection> moveUpRow(final SpreadsheetViewportAnchor anchor,
                                                    final SpreadsheetViewportNavigationContext context) {
        return Cast.to(
            context.moveUpRow(
                this
            )
        );
    }

    @Override
    public Optional<SpreadsheetSelection> moveUpPixels(final SpreadsheetViewportAnchor anchor,
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
    public Optional<SpreadsheetSelection> moveDownRow(final SpreadsheetViewportAnchor anchor,
                                                      final SpreadsheetViewportNavigationContext context) {
        return Cast.to(
            context.downRow(this)
        );
    }


    @Override
    public Optional<SpreadsheetSelection> moveDownPixels(final SpreadsheetViewportAnchor anchor,
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
    public Optional<AnchoredSpreadsheetSelection> extendLeftColumn(final SpreadsheetViewportAnchor anchor,
                                                                   final SpreadsheetViewportNavigationContext context) {
        return this.setAnchorEmptyIfHidden(
            anchor,
            context
        );
    }

    @Override
    public Optional<AnchoredSpreadsheetSelection> extendLeftPixels(final SpreadsheetViewportAnchor anchor,
                                                                   final int count,
                                                                   final SpreadsheetViewportNavigationContext context) {
        return this.setAnchorEmptyIfHidden(
            anchor,
            context
        );
    }

    @Override
    public Optional<AnchoredSpreadsheetSelection> extendRightColumn(final SpreadsheetViewportAnchor anchor,
                                                                    final SpreadsheetViewportNavigationContext context) {
        return this.setAnchorEmptyIfHidden(
            anchor,
            context
        );
    }

    @Override
    public Optional<AnchoredSpreadsheetSelection> extendRightPixels(final SpreadsheetViewportAnchor anchor,
                                                                    final int count,
                                                                    final SpreadsheetViewportNavigationContext context) {
        return this.setAnchorEmptyIfHidden(
            anchor,
            context
        );
    }

    @Override
    public Optional<AnchoredSpreadsheetSelection> extendUpRow(final SpreadsheetViewportAnchor anchor,
                                                              final SpreadsheetViewportNavigationContext context) {
        return this.extendRange(
            this.moveUpRow(
                anchor,
                context
            ),
            anchor
        ).map(
            s -> s.setAnchorOrDefault(SpreadsheetViewportAnchor.BOTTOM)
        );
    }

    @Override
    public Optional<AnchoredSpreadsheetSelection> extendUpPixels(final SpreadsheetViewportAnchor anchor,
                                                                 final int count,
                                                                 final SpreadsheetViewportNavigationContext context) {
        return this.extendRange(
            this.moveUpPixels(
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
    public Optional<AnchoredSpreadsheetSelection> extendDownRow(final SpreadsheetViewportAnchor anchor,
                                                                final SpreadsheetViewportNavigationContext context) {
        return this.extendRange(
            this.moveDownRow(
                anchor,
                context
            ),
            anchor
        ).map(
            s -> s.setAnchorOrDefault(SpreadsheetViewportAnchor.TOP)
        );
    }

    @Override
    public Optional<AnchoredSpreadsheetSelection> extendDownPixels(final SpreadsheetViewportAnchor anchor,
                                                                   final int count,
                                                                   final SpreadsheetViewportNavigationContext context) {
        return this.extendRange(
            this.moveDownPixels(
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
    public int hashCode() {
        return Objects.hash(this.value, this.referenceKind);
    }

    @Override
    boolean equalsNotSameAndNotNull(final Object other,
                                    final boolean includeKind) {
        return this.equals1(
            (SpreadsheetRowReference) other,
            includeKind
        );
    }

    boolean equals1(final SpreadsheetRowReference other,
                    final boolean includeKind) {
        return this.value == other.value &&
            (includeKind ? this.referenceKind == other.referenceKind : true);
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        // in text form columns start at 1 but internally are zero based.
        return this.referenceKind().prefix() + (this.value + 1);
    }

    // Comparable......................................................................................................

    @Override
    public int compareTo(final SpreadsheetRowReference other) {
        Objects.requireNonNull(other, "other");

        return this.value - other.value;
    }

    // SpreadsheetSelectionIgnoresReferenceKindComparator...............................................................

    @Override
    int spreadsheetSelectionIgnoresReferenceKindComparatorPriority() {
        return SpreadsheetSelectionIgnoresReferenceKindComparator.ROW;
    }
}

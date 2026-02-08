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
import walkingkooka.spreadsheet.formula.parser.ColumnSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.value.SpreadsheetColumn;
import walkingkooka.spreadsheet.viewport.AnchoredSpreadsheetSelection;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewportAnchor;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewportNavigationContext;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Represents a column reference. The {@link Comparable} method ignores the {@link SpreadsheetReferenceKind} component
 * only comparing the value.
 */
public final class SpreadsheetColumnReference extends SpreadsheetColumnReferenceOrRange
    implements Value<Integer>,
    Comparable<SpreadsheetColumnReference>,
    SpreadsheetColumnOrRowReference {

    final static int MIN_VALUE = 0;

    /**
     * The maximum value, columns -1.
     */
    // https://support.office.com/en-us/article/excel-specifications-and-limits-1672b34d-7043-467e-8e27-269d656771c3
    final static int MAX_VALUE = 16384 - 1; // inclusive
    final static int RADIX = 26;

    /**
     * The highest legal column.
     */
    public final static String MAX_VALUE_STRING = toString0(
        MAX_VALUE + 1,
        SpreadsheetReferenceKind.RELATIVE
    );

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
        Objects.requireNonNull(referenceKind, "referenceKind");

        return value < CACHE_SIZE ?
            referenceKind.columnFromCache(value) :
            new SpreadsheetColumnReference(value, referenceKind);
    }

    /**
     * Private ctor use factory
     */
    private SpreadsheetColumnReference(final int value,
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
     * Would be setter that returns a {@link SpreadsheetColumnReference} with the given value creating a new
     * instance if it is different.
     */
    public SpreadsheetColumnReference setValue(final int value) {
        return this.value == value ?
            this :
            new SpreadsheetColumnReference(
                checkValue(value),
                this.referenceKind()
            );
    }

    private static int checkValue(final int value) {
        if (value < MIN_VALUE || value > MAX_VALUE) {
            throw new IllegalColumnArgumentException(
                "Invalid column=" + value + " not between " + MIN_VALUE + " and " + (1 + MAX_VALUE)
            );
        }
        return value;
    }

    public SpreadsheetReferenceKind referenceKind() {
        return this.referenceKind;
    }

    private final SpreadsheetReferenceKind referenceKind;

    /**
     * Would be setter that returns a {@link SpreadsheetColumnReference} with the given {@link SpreadsheetReferenceKind}
     * creating a new instance if necessary.
     */
    public SpreadsheetColumnReference setReferenceKind(final SpreadsheetReferenceKind referenceKind) {
        Objects.requireNonNull(referenceKind, "referenceKind");

        return this.referenceKind == referenceKind ?
            this :
            new SpreadsheetColumnReference(
                this.value,
                referenceKind
            );
    }

    @Override
    public Set<SpreadsheetViewportAnchor> anchors() {
        return NONE_ANCHORS;
    }

    // add..............................................................................................................

    @Override
    public SpreadsheetColumnReference add(final int value) {
        return this.setValue(
            this.value + value
        );
    }

    @Override
    public SpreadsheetColumnReference addSaturated(final int value) {
        return this.setValue(
            Math.min(
                Math.max(
                    this.value + value,
                    MIN_VALUE
                ),
                this.max()
            )
        );
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
        return this.referenceKind() == SpreadsheetReferenceKind.ABSOLUTE ?
            this :
            this.add(delta);
    }

    // replaceReferencesMapper..........................................................................................

    @Override
    Optional<Function<SpreadsheetCellReference, Optional<SpreadsheetCellReference>>> replaceReferencesMapper0(final SpreadsheetSelection moveTo) {
        if (moveTo.isRow() || moveTo.isRowRange()) {
            throw new IllegalArgumentException("Expected column(s) or cell(s) but got " + moveTo);
        }
        final int delta = moveTo.toColumn()
            .value() -
            this.value;

        return Optional.ofNullable(
            0 != delta ?
                SpreadsheetSelectionReplaceReferencesMapperFunction.with(
                    delta,
                    0
                ) :
                null
        );
    }

    // max.............................................................................................................

    int max() {
        return MAX_VALUE;
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
    boolean testCellNonNull(final SpreadsheetCellReference cell) {
        return this.testColumnNonNull(cell.column());
    }

    @Override
    boolean testCellRangeNonNull(final SpreadsheetCellRangeReference range) {
        Objects.requireNonNull(range, "range");

        return this.compareTo(range.begin().column()) >= 0 &&
            this.compareTo(range.end().column()) <= 0;
    }

    @Override
    boolean testColumnNonNull(final SpreadsheetColumnReference column) {
        return this.equalsIgnoreReferenceKind(column);
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

    // range/columnRange................................................................................................

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
        return this.value == this.max();
    }

    // toXXX............................................................................................................

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

    /**
     * A column or row is already simplified.
     */
    @Override
    public SpreadsheetSelection toScalar() {
        return this;
    }

    // toRange..........................................................................................................

    @Override
    public SpreadsheetColumnRangeReference toRange() {
        return this.toColumnRange();
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
    public Optional<SpreadsheetSelection> moveLeftColumn(final SpreadsheetViewportAnchor anchor,
                                                         final SpreadsheetViewportNavigationContext context) {
        return Cast.to(
            context.moveLeft(this)
        );
    }

    @Override
    public Optional<SpreadsheetSelection> moveLeftPixels(final SpreadsheetViewportAnchor anchor,
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
    public Optional<SpreadsheetSelection> moveRightColumn(final SpreadsheetViewportAnchor anchor,
                                                          final SpreadsheetViewportNavigationContext context) {
        return Cast.to(
            context.moveRightColumn(this)
        );
    }

    @Override
    public Optional<SpreadsheetSelection> moveRightPixels(final SpreadsheetViewportAnchor anchor,
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
    public Optional<SpreadsheetSelection> moveUpRow(final SpreadsheetViewportAnchor anchor,
                                                    final SpreadsheetViewportNavigationContext context) {
        return this.emptyIfHidden(context);
    }

    @Override
    public Optional<SpreadsheetSelection> moveUpPixels(final SpreadsheetViewportAnchor anchor,
                                                       final int count,
                                                       final SpreadsheetViewportNavigationContext context) {
        return this.emptyIfHidden(context);
    }

    @Override
    public Optional<SpreadsheetSelection> moveDownRow(final SpreadsheetViewportAnchor anchor,
                                                      final SpreadsheetViewportNavigationContext context) {
        return this.emptyIfHidden(context);
    }

    @Override
    public Optional<SpreadsheetSelection> moveDownPixels(final SpreadsheetViewportAnchor anchor,
                                                         final int count,
                                                         final SpreadsheetViewportNavigationContext context) {
        return this.emptyIfHidden(context);
    }

    @Override
    public Optional<AnchoredSpreadsheetSelection> extendLeftColumn(final SpreadsheetViewportAnchor anchor,
                                                                   final SpreadsheetViewportNavigationContext context) {
        return this.extendRange(
            this.moveLeftColumn(
                anchor,
                context
            ),
            anchor
        ).map(
            s -> s.setAnchorOrDefault(SpreadsheetViewportAnchor.RIGHT)
        );
    }

    @Override
    public Optional<AnchoredSpreadsheetSelection> extendLeftPixels(final SpreadsheetViewportAnchor anchor,
                                                                   final int count,
                                                                   final SpreadsheetViewportNavigationContext context) {
        return this.extendRange(
            this.moveLeftPixels(
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
    public Optional<AnchoredSpreadsheetSelection> extendRightColumn(final SpreadsheetViewportAnchor anchor,
                                                                    final SpreadsheetViewportNavigationContext context) {
        return this.extendRange(
            this.moveRightColumn(
                anchor,
                context
            ),
            anchor
        ).map(
            s -> s.setAnchorOrDefault(SpreadsheetViewportAnchor.LEFT)
        );
    }

    @Override
    public Optional<AnchoredSpreadsheetSelection> extendRightPixels(final SpreadsheetViewportAnchor anchor,
                                                                    final int count,
                                                                    final SpreadsheetViewportNavigationContext context) {
        return this.extendRange(
            this.moveRightPixels(
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
    public Optional<AnchoredSpreadsheetSelection> extendUpRow(final SpreadsheetViewportAnchor anchor,
                                                              final SpreadsheetViewportNavigationContext context) {
        return this.setAnchorEmptyIfHidden(
            anchor,
            context
        );
    }

    @Override
    public Optional<AnchoredSpreadsheetSelection> extendUpPixels(final SpreadsheetViewportAnchor anchor,
                                                                 final int count,
                                                                 final SpreadsheetViewportNavigationContext context) {
        return this.setAnchorEmptyIfHidden(
            anchor,
            context
        );
    }

    @Override
    public Optional<AnchoredSpreadsheetSelection> extendDownRow(final SpreadsheetViewportAnchor anchor,
                                                                final SpreadsheetViewportNavigationContext context) {
        return this.setAnchorEmptyIfHidden(
            anchor,
            context
        );
    }


    @Override
    public Optional<AnchoredSpreadsheetSelection> extendDownPixels(final SpreadsheetViewportAnchor anchor,
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
            o -> this.columnRange(o.toColumn())
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
    public ColumnSpreadsheetFormulaParserToken toParserToken() {
        return SpreadsheetFormulaParserToken.column(
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
            (SpreadsheetColumnReference) other,
            includeKind
        );
    }

    boolean equals1(final SpreadsheetColumnReference other,
                    final boolean includeKind) {
        return this.value == other.value &&
            (includeKind ? this.referenceKind == other.referenceKind : true);
    }

    // Object...........................................................................................................

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
        Objects.requireNonNull(other, "other");

        return this.value - other.value;
    }

    // SpreadsheetSelectionIgnoresReferenceKindComparator...............................................................

    @Override
    int spreadsheetSelectionIgnoresReferenceKindComparatorPriority() {
        return SpreadsheetSelectionIgnoresReferenceKindComparator.COLUMN;
    }
}

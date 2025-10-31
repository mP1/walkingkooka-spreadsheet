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
import walkingkooka.collect.HasRange;
import walkingkooka.collect.HasRangeBounds;
import walkingkooka.collect.Range;
import walkingkooka.collect.RangeBound;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.viewport.AnchoredSpreadsheetSelection;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewportAnchor;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewportNavigationContext;
import walkingkooka.text.CharSequences;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

/**
 * Holds a row range.
 */
@SuppressWarnings("lgtm[java/inconsistent-equals-and-hashcode]")
public final class SpreadsheetRowRangeReference extends SpreadsheetRowReferenceOrRange
    implements Comparable<SpreadsheetRowRangeReference>,
    HasRange<SpreadsheetRowReference>,
    HasRangeBounds<SpreadsheetRowReference>,
    Iterable<SpreadsheetRowReference> {

    /**
     * A {@link SpreadsheetRowRangeReference} that includes all rows.
     */
    public static final SpreadsheetRowRangeReference ALL = SpreadsheetReferenceKind.RELATIVE.firstRow()
        .rowRange(
            SpreadsheetReferenceKind.RELATIVE.lastRow()
        );

    /**
     * Factory that creates a {@link SpreadsheetRowRangeReference}
     */
    static SpreadsheetRowRangeReference with(final Range<SpreadsheetRowReference> range) {
        SpreadsheetSelectionRangeRangeVisitor.check(range);

        return new SpreadsheetRowRangeReference(range);
    }

    private SpreadsheetRowRangeReference(final Range<SpreadsheetRowReference> range) {
        super();
        this.range = range;
    }

    /**
     * Returns the top left row/row reference.
     */
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public SpreadsheetRowReference begin() {
        return this.range.lowerBound()
            .value()
            .get(); // must exist
    }

    /**
     * Returns the bottom right row/row reference.
     */
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public SpreadsheetRowReference end() {
        return this.range.upperBound()
            .value()
            .get(); // must exist
    }

    @Override
    public Range<SpreadsheetRowReference> range() {
        return this.range;
    }

    private final Range<SpreadsheetRowReference> range;


    public SpreadsheetRowRangeReference setRange(final Range<SpreadsheetRowReference> range) {
        Objects.requireNonNull(range, "range");

        return this.range.equals(range) ?
            this :
            with(range);
    }

    /**
     * Creates a {@link SpreadsheetCellRangeReference} combining this row range and the given column range.
     */
    public SpreadsheetCellRangeReference setColumnRange(final SpreadsheetColumnRangeReference columnRangeReference) {
        Objects.requireNonNull(columnRangeReference, "columnRangeReference");

        return columnRangeReference.setRowRange(this);
    }

    // add..............................................................................................................

    @Override
    public SpreadsheetRowRangeReference add(final int value) {
        return this.setRange(
            Range.with(
                RangeBound.inclusive(
                    this.begin().add(value)
                ),
                RangeBound.inclusive(
                    this.end().add(value)
                )
            )
        );
    }

    // addSaturated.....................................................................................................

    @Override
    public SpreadsheetRowRangeReference addSaturated(final int value) {
        return this.setRange(
            Range.with(
                RangeBound.inclusive(
                    this.begin()
                        .addSaturated(value)
                ),
                RangeBound.inclusive(
                    this.end()
                        .addSaturated(value)
                )
            )
        );
    }

    // add column/row...................................................................................................

    @Override
    public SpreadsheetRowRangeReference add(final int column,
                                            final int row) {
        checkColumnDeltaIsZero(column);
        return this.add(row);
    }

    @Override
    public SpreadsheetRowRangeReference addSaturated(final int column,
                                                     final int row) {
        checkColumnDeltaIsZero(column);
        return this.addSaturated(row);
    }

    @Override
    public SpreadsheetRowRangeReference addIfRelative(final int delta) {
        return this.setRange(
            Range.with(
                RangeBound.inclusive(
                    this.begin()
                        .addIfRelative(delta)
                ),
                RangeBound.inclusive(
                    this.end()
                        .addIfRelative(delta)
                )
            )
        );
    }

    // testXXX.........................................................................................................

    @Override
    boolean testCellNonNull(final SpreadsheetCellReference cell) {
        return this.testRowNonNull(cell.row());
    }

    @Override
    boolean testCellRangeNonNull(final SpreadsheetCellRangeReference range) {
        return this.end().compareTo(range.begin().row()) >= 0 &&
            this.begin().compareTo(range.end().row()) <= 0;
    }

    /**
     * Tests if the given {@link SpreadsheetRowReference} is within this {@link SpreadsheetRowRangeReference}.
     */
    @Override
    boolean testRowNonNull(final SpreadsheetRowReference row) {
        return this.range.test(row);
    }

    // count............................................................................................................

    /**
     * Returns the number of rows in this range.
     */
    @Override
    public long count() {
        return this.end()
            .value()
            - this.begin()
            .value()
            + 1;
    }

    // isXXX............................................................................................................

    @Override
    public boolean isFirst() {
        return this.begin().isFirst() && this.isUnit();
    }

    @Override
    public boolean isLast() {
        return this.begin().isLast() && this.isUnit();
    }

    // toXXX............................................................................................................

    @Override
    public SpreadsheetRowReference toRow() {
        return this.begin();
    }

    @Override
    public SpreadsheetRowRangeReference toRowRange() {
        return this;
    }

    // toScalar.........................................................................................................

    @Override
    public SpreadsheetRowReference toScalar() {
        return this.begin();
    }

    // toRange..........................................................................................................

    @Override
    public SpreadsheetRowRangeReference toRange() {
        return this;
    }

    // toRelative.......................................................................................................

    @Override
    public SpreadsheetRowRangeReference toRelative() {
        final SpreadsheetRowRangeReference relative = this.begin()
            .toRelative()
            .rowRange(this.end()
                .toRelative());
        return this.equals(relative) ?
            this :
            relative;
    }

    // replaceReferencesMapper..........................................................................................

    @Override
    Optional<Function<SpreadsheetCellReference, Optional<SpreadsheetCellReference>>> replaceReferencesMapper0(final SpreadsheetSelection movedTo) {
        return this.toScalar()
            .replaceReferencesMapper0(movedTo);
    }

    // SpreadsheetSelectionVisitor......................................................................................

    @Override
    void accept(final SpreadsheetSelectionVisitor visitor) {
        visitor.visit(this);
    }

    // SpreadsheetViewportNavigation...........................................................................

    @Override
    public Set<SpreadsheetViewportAnchor> anchors() {
        return ANCHORS;
    }

    private final static Set<SpreadsheetViewportAnchor> ANCHORS = Sets.readOnly(
        EnumSet.of(
            SpreadsheetViewportAnchor.TOP,
            SpreadsheetViewportAnchor.BOTTOM
        )
    );

    @Override
    public SpreadsheetViewportAnchor defaultAnchor() {
        return SpreadsheetViewportAnchor.ROW_RANGE;
    }

    /**
     * Complains if this row range is not a valid frozen rows range representation.
     * A frozen row range must begin with row 1
     */
    public void frozenRowsCheck() {
        if (this.begin().value() != 0) {
            throw new IllegalArgumentException("Range must begin at '1' but was " + CharSequences.quoteAndEscape(this.toString()));
        }
    }

    /**
     * A {@link SpreadsheetCellReference} is hidden if either begin or end is hidden.
     */
    @Override
    public boolean isHidden(final Predicate<SpreadsheetColumnReference> hiddenColumnTester,
                            final Predicate<SpreadsheetRowReference> hiddenRowTester) {
        return isHiddenRange(
            this,
            hiddenColumnTester,
            hiddenRowTester
        );
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
        return anchor.opposite()
            .row(this)
            .moveUpRow(
                anchor,
                context
            );
    }

    @Override
    public Optional<SpreadsheetSelection> moveUpPixels(final SpreadsheetViewportAnchor anchor,
                                                       final int count,
                                                       final SpreadsheetViewportNavigationContext context) {
        return anchor.opposite()
            .row(this)
            .moveUpPixels(
                anchor,
                count,
                context
            );
    }

    @Override
    public Optional<SpreadsheetSelection> moveDownRow(final SpreadsheetViewportAnchor anchor,
                                                      final SpreadsheetViewportNavigationContext context) {
        return anchor.opposite()
            .row(this)
            .moveDownRow(
                anchor,
                context
            );
    }

    @Override
    public Optional<SpreadsheetSelection> downPixels(final SpreadsheetViewportAnchor anchor,
                                                     final int count,
                                                     final SpreadsheetViewportNavigationContext context) {
        return anchor.opposite()
            .row(this)
            .downPixels(
                anchor,
                count,
                context
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
        return this.extendRow(
            this.isUnit() ? SpreadsheetViewportAnchor.BOTTOM : anchor,
            r -> Cast.to(
                r.moveUpRow(
                    anchor,
                    context
                )
            )
        );
    }

    @Override
    public Optional<AnchoredSpreadsheetSelection> extendUpPixels(final SpreadsheetViewportAnchor anchor,
                                                                 final int count,
                                                                 final SpreadsheetViewportNavigationContext context) {
        return this.extendRow(
            this.isUnit() ? SpreadsheetViewportAnchor.BOTTOM : anchor,
            r -> Cast.to(
                r.moveUpPixels(
                    anchor,
                    count,
                    context
                )
            )
        );
    }

    @Override
    public Optional<AnchoredSpreadsheetSelection> extendDownRow(final SpreadsheetViewportAnchor anchor,
                                                                final SpreadsheetViewportNavigationContext context) {
        return this.extendRow(
            this.isUnit() ? SpreadsheetViewportAnchor.TOP : anchor,
            r -> Cast.to(
                r.moveDownRow(
                    anchor,
                    context
                )
            )
        );
    }

    @Override
    public Optional<AnchoredSpreadsheetSelection> extendDownPixels(final SpreadsheetViewportAnchor anchor,
                                                                   final int count,
                                                                   final SpreadsheetViewportNavigationContext context) {
        return this.extendRow(
            this.isUnit() ? SpreadsheetViewportAnchor.TOP : anchor,
            r -> Cast.to(
                r.downPixels(
                    anchor,
                    count,
                    context
                )
            )
        );
    }

    private Optional<AnchoredSpreadsheetSelection> extendRow(final SpreadsheetViewportAnchor anchor,
                                                             final Function<SpreadsheetRowReference, Optional<SpreadsheetRowReference>> move) {
        return this.extendRange(
            move.apply(
                anchor.opposite()
                    .row(this)
            ),
            anchor
        ).map(s -> s.setAnchorOrDefault(anchor));
    }

    @Override
    Optional<SpreadsheetSelection> extendRange(final Optional<? extends SpreadsheetSelection> other,
                                               final SpreadsheetViewportAnchor anchor) {
        return other.map(
            s -> anchor.row(this)
                .rowRange(s.toRow())
                .toScalarIfUnit()
        );
    }

    // focused...........................................................................................................

    @Override
    public SpreadsheetRowReference focused(final SpreadsheetViewportAnchor anchor) {
        this.checkAnchor(anchor);
        return anchor.opposite()
            .row(this);
    }

    // Iterable.........................................................................................................

    @Override
    public Iterator<SpreadsheetRowReference> iterator() {
        return IntStream.rangeClosed(
                this.begin().value(),
                this.end().value()
            )
            .boxed()
            .map(SpreadsheetReferenceKind.RELATIVE::row)
            .iterator();
    }

    // HasParserToken...................................................................................................

    @Override
    public SpreadsheetFormulaParserToken toParserToken() {
        throw new UnsupportedOperationException();
    }

    // hashCode/equals..................................................................................................

    @Override
    public int hashCode() {
        return this.range.hashCode();
    }

    @Override
    boolean equalsNotSameAndNotNull(final Object other,
                                    final boolean includeKind) {
        return this.equals1(
            (SpreadsheetRowRangeReference) other,
            includeKind
        );
    }

    private boolean equals1(final SpreadsheetRowRangeReference other,
                            final boolean includeKind) {
        return this.begin().equalsNotSameAndNotNull(other.begin(), includeKind) &&
            this.end().equalsNotSameAndNotNull(other.end(), includeKind);
    }

    // toString.........................................................................................................

    @Override
    public String toString() {
        return this.isUnit() ?
            this.begin().toString() :
            this.begin() + SEPARATOR.string() + this.end();
    }

    // Comparable.......................................................................................................

    @Override
    public int compareTo(final SpreadsheetRowRangeReference other) {
        int result = this.begin().compareTo(other.begin());
        if (0 == result) {
            result = this.end().compareTo(other.end());
        }
        return result;
    }

    // SpreadsheetSelectionIgnoresReferenceKindComparator...............................................................

    @Override
    int spreadsheetSelectionIgnoresReferenceKindComparatorPriority() {
        return SpreadsheetSelectionIgnoresReferenceKindComparator.ROW_RANGE;
    }
}

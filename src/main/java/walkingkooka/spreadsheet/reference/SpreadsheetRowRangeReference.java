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
import walkingkooka.collect.RangeBound;
import walkingkooka.text.CharSequences;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Holds a row range.
 */
@SuppressWarnings("lgtm[java/inconsistent-equals-and-hashcode]")
public final class SpreadsheetRowRangeReference extends SpreadsheetColumnOrRowRangeReference<SpreadsheetRowReference>
        implements Comparable<SpreadsheetRowRangeReference> {

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

    /**
     * Private ctor
     */
    private SpreadsheetRowRangeReference(final Range<SpreadsheetRowReference> range) {
        super(range);
    }

    /**
     * Creates a {@link SpreadsheetCellRangeReference} combining this row range and the given column range.
     */
    public SpreadsheetCellRangeReference setColumnRange(final SpreadsheetColumnRangeReference columnRangeReference) {
        Objects.requireNonNull(columnRangeReference, "columnRangeReference");

        return columnRangeReference.setRowRange(this);
    }

    public SpreadsheetRowRangeReference setRange(final Range<SpreadsheetRowReference> range) {
        return this.setRange0(range);
    }

    @Override
    SpreadsheetRowRangeReference replace(final Range<SpreadsheetRowReference> range) {
        return with(range);
    }

    // add..............................................................................................................

    @Override
    public SpreadsheetRowRangeReference add(final int value) {
        return this.add0(value)
                .toRowRange();
    }

    @Override
    SpreadsheetRowRangeReference addNonZero(final int value) {
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
        return this.addSaturated0(value)
                .toRowRange();
    }

    @Override
    SpreadsheetRowRangeReference addSaturatedNonZero(final int value) {
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

    @Override
    boolean testColumnNonNull(final SpreadsheetColumnReference column) {
        return false;
    }

    /**
     * Tests if the given {@link SpreadsheetRowReference} is within this {@link SpreadsheetRowRangeReference}.
     */
    @Override
    boolean testRowNonNull(final SpreadsheetRowReference row) {
        return this.range.test(row);
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
        return this.begin();
    }

    @Override
    public SpreadsheetRowRangeReference toRowRange() {
        return this;
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

    // SpreadsheetSelectionVisitor......................................................................................

    @Override
    void accept(final SpreadsheetSelectionVisitor visitor) {
        visitor.visit(this);
    }

    // Iterable.........................................................................................................

    @Override
    SpreadsheetRowReference iteratorIntToReference(final int value) {
        return SpreadsheetReferenceKind.RELATIVE.row(value);
    }

    // SpreadsheetViewportNavigation...........................................................................

    @Override
    Set<SpreadsheetViewportAnchor> anchors() {
        return ANCHORS;
    }

    private final static Set<SpreadsheetViewportAnchor> ANCHORS = EnumSet.of(
            SpreadsheetViewportAnchor.TOP,
            SpreadsheetViewportAnchor.BOTTOM
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
        return anchor.opposite()
                .row(this)
                .upRow(
                        anchor,
                        context
                );
    }

    @Override
    Optional<SpreadsheetSelection> upPixels(final SpreadsheetViewportAnchor anchor,
                                            final int count,
                                            final SpreadsheetViewportNavigationContext context) {
        return anchor.opposite()
                .row(this)
                .upPixels(
                        anchor,
                        count,
                        context
                );
    }

    @Override
    Optional<SpreadsheetSelection> downRow(final SpreadsheetViewportAnchor anchor,
                                           final SpreadsheetViewportNavigationContext context) {
        return anchor.opposite()
                .row(this)
                .downRow(
                        anchor,
                        context
                );
    }

    @Override
    Optional<SpreadsheetSelection> downPixels(final SpreadsheetViewportAnchor anchor,
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
        return this.extendRow(
                this.isSingle() ? SpreadsheetViewportAnchor.BOTTOM : anchor,
                r -> Cast.to(
                        r.upRow(
                                anchor,
                                context
                        )
                )
        );
    }

    @Override
    Optional<AnchoredSpreadsheetSelection> extendUpPixels(final SpreadsheetViewportAnchor anchor,
                                                          final int count,
                                                          final SpreadsheetViewportNavigationContext context) {
        return this.extendRow(
                this.isSingle() ? SpreadsheetViewportAnchor.BOTTOM : anchor,
                r -> Cast.to(
                        r.upPixels(
                                anchor,
                                count,
                                context
                        )
                )
        );
    }

    @Override
    Optional<AnchoredSpreadsheetSelection> extendDownRow(final SpreadsheetViewportAnchor anchor,
                                                         final SpreadsheetViewportNavigationContext context) {
        return this.extendRow(
                this.isSingle() ? SpreadsheetViewportAnchor.TOP : anchor,
                r -> Cast.to(
                        r.downRow(
                                anchor,
                                context
                        )
                )
        );
    }

    @Override
    Optional<AnchoredSpreadsheetSelection> extendDownPixels(final SpreadsheetViewportAnchor anchor,
                                                            final int count,
                                                            final SpreadsheetViewportNavigationContext context) {
        return this.extendRow(
                this.isSingle() ? SpreadsheetViewportAnchor.TOP : anchor,
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

    // Object...........................................................................................................

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

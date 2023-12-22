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

import walkingkooka.collect.Range;
import walkingkooka.collect.RangeBound;
import walkingkooka.text.CharSequences;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Holds a column range.
 */
@SuppressWarnings("lgtm[java/inconsistent-equals-and-hashcode]")
public final class SpreadsheetColumnReferenceRange extends SpreadsheetColumnOrRowReferenceRange<SpreadsheetColumnReference>
        implements Comparable<SpreadsheetColumnReferenceRange> {

    /**
     * A {@link SpreadsheetColumnReferenceRange} that includes all columns.
     */
    public static final SpreadsheetColumnReferenceRange ALL = SpreadsheetReferenceKind.RELATIVE.firstColumn()
            .columnRange(
                    SpreadsheetReferenceKind.RELATIVE.lastColumn()
            );

    /**
     * Factory that creates a {@link SpreadsheetColumnReferenceRange}
     */
    static SpreadsheetColumnReferenceRange with(final Range<SpreadsheetColumnReference> range) {
        SpreadsheetRangeRangeVisitor.check(range);

        return new SpreadsheetColumnReferenceRange(range);
    }

    /**
     * Private ctor
     */
    private SpreadsheetColumnReferenceRange(final Range<SpreadsheetColumnReference> range) {
        super(range);
    }

    public SpreadsheetColumnReferenceRange setRange(final Range<SpreadsheetColumnReference> range) {
        return this.setRange0(range);
    }

    @Override
    SpreadsheetColumnReferenceRange replace(final Range<SpreadsheetColumnReference> range) {
        return with(range);
    }

    /**
     * Creates a {@link SpreadsheetCellRange} combining this column range and the given row range.
     */
    public SpreadsheetCellRange setRowReferenceRange(final SpreadsheetRowReferenceRange row) {
        checkRowReferenceRange(row);

        final SpreadsheetColumnReference columnBegin = this.begin();
        final SpreadsheetRowReference rowBegin = row.begin();

        final SpreadsheetColumnReference columnEnd = this.end();
        final SpreadsheetRowReference rowEnd = row.end();

        return columnBegin.setRow(rowBegin)
                .cellRange(
                        columnEnd.setRow(rowEnd)
                );
    }

    // add..............................................................................................................

    @Override
    public SpreadsheetColumnReferenceRange add(final int value) {
        return this.add0(value)
                .toColumnRange();
    }

    @Override
    SpreadsheetColumnReferenceRange addNonZero(final int value) {
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
    public SpreadsheetColumnReferenceRange addSaturated(final int value) {
        return this.addSaturated0(value)
                .toColumnRange();
    }

    @Override
    SpreadsheetColumnReferenceRange addSaturatedNonZero(final int value) {
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

    @Override
    public SpreadsheetColumnReferenceRange add(final int column,
                                               final int row) {
        checkRowDeltaIsZero(row);
        return this.add(column);
    }

    @Override
    public SpreadsheetColumnReferenceRange addSaturated(final int column,
                                                        final int row) {
        checkRowDeltaIsZero(row);
        return this.addSaturated(column);
    }

    @Override
    public SpreadsheetColumnReferenceRange addIfRelative(final int delta) {
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
    boolean testCell0(final SpreadsheetCellReference cell) {
        return this.testColumn0(cell.column());
    }

    @Override
    boolean testCellRange0(final SpreadsheetCellRange range) {
        return this.end().compareTo(range.begin().column()) >= 0 &&
                this.begin().compareTo(range.end().column()) <= 0;
    }

    /**
     * Tests if the given {@link SpreadsheetColumnReference} is within this {@link SpreadsheetColumnReferenceRange}.
     */
    @Override
    boolean testColumn0(final SpreadsheetColumnReference column) {
        return this.range.test(column);
    }

    @Override
    boolean testRow0(final SpreadsheetRowReference row) {
        return false;
    }

    @Override
    public SpreadsheetColumnReference toColumn() {
        return this.begin();
    }

    @Override
    public SpreadsheetColumnReferenceRange toColumnRange() {
        return this;
    }

    @Override
    public SpreadsheetRowReference toRow() {
        throw new UnsupportedOperationException(this.toString());
    }

    @Override
    public SpreadsheetRowReferenceRange toRowRange() {
        throw new UnsupportedOperationException(this.toString());
    }

    @Override
    public SpreadsheetColumnReferenceRange toRelative() {
        final SpreadsheetColumnReferenceRange relative = this.begin()
                .toRelative()
                .columnRange(this.end()
                        .toRelative());
        return this.equals(relative) ?
                this :
                relative;
    }

    @Override
    Set<SpreadsheetViewportAnchor> anchors() {
        return ANCHORS;
    }

    private final static Set<SpreadsheetViewportAnchor> ANCHORS = EnumSet.of(
            SpreadsheetViewportAnchor.LEFT,
            SpreadsheetViewportAnchor.RIGHT
    );

    @Override
    public SpreadsheetViewportAnchor defaultAnchor() {
        return SpreadsheetViewportAnchor.COLUMN_RANGE;
    }

    /**
     * Complains if this column range is not a valid frozen columns range representation.
     * A frozen column range must begin with column A
     */
    public void frozenColumnsCheck() {
        if (this.begin().value() != 0) {
            throw new IllegalArgumentException("Range must begin at 'A' but was " + CharSequences.quoteAndEscape(this.toString()));
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
        return anchor.opposite()
                .column(this)
                .leftColumn(
                        anchor,
                        context
                );
    }

    @Override
    Optional<SpreadsheetSelection> leftPixels(final SpreadsheetViewportAnchor anchor,
                                              final int count,
                                              final SpreadsheetViewportNavigationContext context) {
        return anchor.opposite()
                .column(this)
                .leftPixels(
                        anchor,
                        count,
                        context
                );
    }

    @Override
    Optional<SpreadsheetSelection> rightColumn(final SpreadsheetViewportAnchor anchor,
                                               final SpreadsheetViewportNavigationContext context) {
        return anchor.opposite()
                .column(this)
                .rightColumn(
                        anchor,
                        context
                );
    }

    @Override
    Optional<SpreadsheetSelection> rightPixels(final SpreadsheetViewportAnchor anchor,
                                               final int count,
                                               final SpreadsheetViewportNavigationContext context) {
        return anchor.opposite()
                .column(this)
                .rightPixels(
                        anchor,
                        count,
                        context
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
        return this.extendColumn(
                this.isSingle() ? SpreadsheetViewportAnchor.RIGHT : anchor,
                context::leftColumn
        );
    }

    @Override
    Optional<AnchoredSpreadsheetSelection> extendLeftPixels(final SpreadsheetViewportAnchor anchor,
                                                            final int count,
                                                            final SpreadsheetViewportNavigationContext context) {
        return this.extendColumn(
                this.isSingle() ? SpreadsheetViewportAnchor.RIGHT : anchor,
                c -> context.leftPixels(
                        c,
                        count
                )
        );
    }

    @Override
    Optional<AnchoredSpreadsheetSelection> extendRightColumn(final SpreadsheetViewportAnchor anchor,
                                                             final SpreadsheetViewportNavigationContext context) {
        return this.extendColumn(
                this.isSingle() ? SpreadsheetViewportAnchor.LEFT : anchor,
                context::rightColumn
        );
    }

    @Override
    Optional<AnchoredSpreadsheetSelection> extendRightPixels(final SpreadsheetViewportAnchor anchor,
                                                             final int count,
                                                             final SpreadsheetViewportNavigationContext context) {
        return this.extendColumn(
                this.isSingle() ? SpreadsheetViewportAnchor.LEFT : anchor,
                c -> context.rightPixels(
                        c,
                        count
                )
        );
    }

    private Optional<AnchoredSpreadsheetSelection> extendColumn(final SpreadsheetViewportAnchor anchor,
                                                                final Function<SpreadsheetColumnReference, Optional<SpreadsheetColumnReference>> move) {
        return this.extendRange(
                move.apply(
                        anchor.opposite()
                                .column(this)
                ),
                anchor
        ).map(s -> s.setAnchorOrDefault(anchor));
    }

    @Override
    Optional<SpreadsheetSelection> extendRange(final Optional<? extends SpreadsheetSelection> other,
                                               final SpreadsheetViewportAnchor anchor) {
        return other.map(
                s -> anchor.column(this)
                        .columnRange((SpreadsheetColumnReference) s)
                        .simplify()
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

    // focused...........................................................................................................

    @Override
    public SpreadsheetColumnReference focused(final SpreadsheetViewportAnchor anchor) {
        this.checkAnchor(anchor);
        return anchor.opposite()
                .column(this);
    }

    // SpreadsheetSelectionVisitor......................................................................................

    @Override
    void accept(final SpreadsheetSelectionVisitor visitor) {
        visitor.visit(this);
    }

    // Iterable.........................................................................................................

    @Override
    SpreadsheetColumnReference iteratorIntToReference(final int value) {
        return SpreadsheetReferenceKind.RELATIVE.column(value);
    }

    // Object...........................................................................................................

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof SpreadsheetColumnReferenceRange;
    }

    // Comparable.......................................................................................................

    @Override
    public int compareTo(final SpreadsheetColumnReferenceRange other) {
        int result = this.begin().compareTo(other.begin());
        if (0 == result) {
            result = this.end().compareTo(other.end());
        }
        return result;
    }
}

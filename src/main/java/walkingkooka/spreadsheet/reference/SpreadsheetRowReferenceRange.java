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
import walkingkooka.text.CharSequences;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Holds a row range.
 */
@SuppressWarnings("lgtm[java/inconsistent-equals-and-hashcode]")
public final class SpreadsheetRowReferenceRange extends SpreadsheetColumnOrRowReferenceRange<SpreadsheetRowReference>
        implements Comparable<SpreadsheetRowReferenceRange> {

    /**
     * A {@link SpreadsheetRowReferenceRange} that includes all rows.
     */
    public static final SpreadsheetRowReferenceRange ALL = SpreadsheetReferenceKind.RELATIVE.firstRow()
            .rowRange(
                    SpreadsheetReferenceKind.RELATIVE.lastRow()
            );

    /**
     * Factory that creates a {@link SpreadsheetRowReferenceRange}
     */
    static SpreadsheetRowReferenceRange with(final Range<SpreadsheetRowReference> range) {
        SpreadsheetRangeRangeVisitor.check(range);

        return new SpreadsheetRowReferenceRange(range);
    }

    /**
     * Private ctor
     */
    private SpreadsheetRowReferenceRange(final Range<SpreadsheetRowReference> range) {
        super(range);
    }

    /**
     * Creates a {@link SpreadsheetCellRange} combining this row range and the given column range.
     */
    public SpreadsheetCellRange setColumnReferenceRange(final SpreadsheetColumnReferenceRange columnReferenceRange) {
        checkColumnReferenceRange(columnReferenceRange);

        return columnReferenceRange.setRowReferenceRange(this);
    }

    public SpreadsheetRowReferenceRange setRange(final Range<SpreadsheetRowReference> range) {
        return this.setRange0(range);
    }

    @Override
    SpreadsheetRowReferenceRange replace(final Range<SpreadsheetRowReference> range) {
        return with(range);
    }

    // testXXX.........................................................................................................

    @Override
    boolean testCell0(final SpreadsheetCellReference cell) {
        return this.testRow0(cell.row());
    }

    @Override
    boolean testCellRange0(final SpreadsheetCellRange range) {
        return this.end().compareTo(range.begin().row()) >= 0 &&
                this.begin().compareTo(range.end().row()) <= 0;
    }

    @Override
    boolean testColumn0(final SpreadsheetColumnReference column) {
        return false;
    }

    /**
     * Tests if the given {@link SpreadsheetRowReference} is within this {@link SpreadsheetRowReferenceRange}.
     */
    @Override
    boolean testRow0(final SpreadsheetRowReference row) {
        return this.range.test(row);
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
        return this.begin();
    }

    @Override
    public SpreadsheetRowReferenceRange toRowRange() {
        return this;
    }

    @Override
    public SpreadsheetRowReferenceRange toRelative() {
        final SpreadsheetRowReferenceRange relative = this.begin()
                .toRelative()
                .rowRange(this.end()
                        .toRelative());
        return this.equals(relative) ?
                this :
                relative;
    }

    @Override
    void accept(final SpreadsheetSelectionVisitor visitor) {
        visitor.visit(this);
    }

    // Iterable.........................................................................................................

    @Override
    SpreadsheetRowReference iteratorIntToReference(final int value) {
        return SpreadsheetReferenceKind.RELATIVE.row(value);
    }

    // SpreadsheetViewportSelectionNavigation...........................................................................

    @Override
    Set<SpreadsheetViewportSelectionAnchor> anchors() {
        return ANCHORS;
    }

    private final static Set<SpreadsheetViewportSelectionAnchor> ANCHORS = EnumSet.of(
            SpreadsheetViewportSelectionAnchor.TOP,
            SpreadsheetViewportSelectionAnchor.BOTTOM
    );

    @Override
    public SpreadsheetViewportSelectionAnchor defaultAnchor() {
        return SpreadsheetViewportSelectionAnchor.ROW_RANGE;
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
        return anchor.row(this)
                .upRow(
                        anchor,
                        context
                );
    }

    @Override
    Optional<SpreadsheetSelection> upPixels(final SpreadsheetViewportSelectionAnchor anchor,
                                            final int count,
                                            final SpreadsheetViewportSelectionNavigationContext context) {
        return anchor.row(this)
                .upPixels(
                        anchor,
                        count,
                        context
                );
    }

    @Override
    Optional<SpreadsheetSelection> downRow(final SpreadsheetViewportSelectionAnchor anchor,
                                           final SpreadsheetViewportSelectionNavigationContext context) {
        return anchor.row(this)
                .downRow(
                        anchor,
                        context
                );
    }

    @Override
    Optional<SpreadsheetSelection> downPixels(final SpreadsheetViewportSelectionAnchor anchor,
                                              final int count,
                                              final SpreadsheetViewportSelectionNavigationContext context) {
        return anchor.row(this)
                .downPixels(
                        anchor,
                        count,
                        context
                );
    }

    @Override
    Optional<SpreadsheetViewport> extendLeftColumn(final SpreadsheetViewportSelectionAnchor anchor,
                                                   final SpreadsheetViewportSelectionNavigationContext context) {
        return this.setAnchorEmptyIfHidden(
                anchor,
                context
        );
    }

    @Override
    Optional<SpreadsheetViewport> extendLeftPixels(final SpreadsheetViewportSelectionAnchor anchor,
                                                   final int count,
                                                   final SpreadsheetViewportSelectionNavigationContext context) {
        return this.setAnchorEmptyIfHidden(
                anchor,
                context
        );
    }

    @Override
    Optional<SpreadsheetViewport> extendRightColumn(final SpreadsheetViewportSelectionAnchor anchor,
                                                    final SpreadsheetViewportSelectionNavigationContext context) {
        return this.setAnchorEmptyIfHidden(
                anchor,
                context
        );
    }

    @Override
    Optional<SpreadsheetViewport> extendRightPixels(final SpreadsheetViewportSelectionAnchor anchor,
                                                    final int count,
                                                    final SpreadsheetViewportSelectionNavigationContext context) {
        return this.setAnchorEmptyIfHidden(
                anchor,
                context
        );
    }

    @Override
    Optional<SpreadsheetViewport> extendUpRow(final SpreadsheetViewportSelectionAnchor anchor,
                                              final SpreadsheetViewportSelectionNavigationContext context) {
        return this.extendRow(
                this.isSingle() ? SpreadsheetViewportSelectionAnchor.BOTTOM : anchor,
                r -> Cast.to(
                        r.upRow(
                                anchor,
                                context
                        )
                )
        );
    }

    @Override
    Optional<SpreadsheetViewport> extendUpPixels(final SpreadsheetViewportSelectionAnchor anchor,
                                                 final int count,
                                                 final SpreadsheetViewportSelectionNavigationContext context) {
        return this.extendRow(
                this.isSingle() ? SpreadsheetViewportSelectionAnchor.BOTTOM : anchor,
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
    Optional<SpreadsheetViewport> extendDownRow(final SpreadsheetViewportSelectionAnchor anchor,
                                                final SpreadsheetViewportSelectionNavigationContext context) {
        return this.extendRow(
                this.isSingle() ? SpreadsheetViewportSelectionAnchor.TOP : anchor,
                r -> Cast.to(
                        r.downRow(
                                anchor,
                                context
                        )
                )
        );
    }

    @Override
    Optional<SpreadsheetViewport> extendDownPixels(final SpreadsheetViewportSelectionAnchor anchor,
                                                   final int count,
                                                   final SpreadsheetViewportSelectionNavigationContext context) {
        return this.extendRow(
                this.isSingle() ? SpreadsheetViewportSelectionAnchor.TOP : anchor,
                r -> Cast.to(
                        r.downPixels(
                                anchor,
                                count,
                                context
                        )
                )
        );
    }

    private Optional<SpreadsheetViewport> extendRow(final SpreadsheetViewportSelectionAnchor anchor,
                                                    final Function<SpreadsheetRowReference, Optional<SpreadsheetRowReference>> move) {
        return this.extendRange(
                move.apply(anchor.row(this)),
                anchor
        ).map(s -> s.setAnchorOrDefault(anchor));
    }

    @Override
    Optional<SpreadsheetSelection> extendRange(final Optional<? extends SpreadsheetSelection> other,
                                               final SpreadsheetViewportSelectionAnchor anchor) {
        return other.map(
                s -> anchor.fixedRow(this)
                        .rowRange((SpreadsheetRowReference) s)
                        .simplify()
        );
    }

    // focused...........................................................................................................

    @Override
    public SpreadsheetRowReference focused(final SpreadsheetViewportSelectionAnchor anchor) {
        this.checkAnchor(anchor);
        return anchor.row(this);
    }

    // Object...........................................................................................................

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof SpreadsheetRowReferenceRange;
    }

    // Comparable.......................................................................................................

    @Override
    public int compareTo(final SpreadsheetRowReferenceRange other) {
        int result = this.begin().compareTo(other.begin());
        if (0 == result) {
            result = this.end().compareTo(other.end());
        }
        return result;
    }
}

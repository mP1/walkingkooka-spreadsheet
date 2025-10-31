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
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetCellRange;
import walkingkooka.spreadsheet.formula.parser.CellRangeSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.viewport.AnchoredSpreadsheetSelection;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewportAnchor;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewportNavigationContext;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewportWindows;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * Holds a range. Note the begin component is always before the end, with rows being the significant axis before column.
 * <br>
 * When {@link #compareTo(SpreadsheetCellRangeReference)} the {@link SpreadsheetReferenceKind} is ignored, which is also true
 * of other {@link SpreadsheetSelection}.
 */
@SuppressWarnings("lgtm[java/inconsistent-equals-and-hashcode]")
public final class SpreadsheetCellRangeReference extends SpreadsheetCellReferenceOrRange
    implements Comparable<SpreadsheetCellRangeReference>,
    CanReplaceReferences<SpreadsheetCellRangeReference>,
    HasRange<SpreadsheetCellReference>,
    HasRangeBounds<SpreadsheetCellReference>,
    Iterable<SpreadsheetCellReference> {

    /**
     * A {@link SpreadsheetColumnRangeReference} that includes all cells.
     */
    public static final SpreadsheetCellRangeReference ALL = SpreadsheetColumnRangeReference.ALL
        .setRowRange(SpreadsheetRowRangeReference.ALL);

    /**
     * Computes the range of the given cells.
     */
    public static SpreadsheetCellRangeReference bounds(final List<SpreadsheetCellReference> cells) {
        Objects.requireNonNull(cells, "cells");

        final List<SpreadsheetCellReference> copy = Lists.immutable(cells);

        SpreadsheetCellRangeReference range;
        switch (copy.size()) {
            case 0:
                throw new IllegalArgumentException("Cells empty");
            case 1:
                range = with(
                    Range.singleton(
                        copy.get(0)
                    )
                );
                break;
            default:
                range = computeEnclosingBounds(copy);
                break;
        }

        return range;
    }

    @SuppressWarnings("lgtm[java/dereferenced-value-may-be-null]")
    private static SpreadsheetCellRangeReference computeEnclosingBounds(final List<SpreadsheetCellReference> cells) {
        SpreadsheetColumnReference left = null;
        SpreadsheetRowReference top = null;

        SpreadsheetColumnReference right = null;
        SpreadsheetRowReference bottom = null;

        for (final SpreadsheetCellReference cell : cells) {
            if (null == left) {
                left = cell.column();
                right = left;

                top = cell.row();
                bottom = top;
            } else {
                final SpreadsheetColumnReference column = cell.column();
                left = left.min(column);
                right = right.max(column);

                final SpreadsheetRowReference row = cell.row();
                top = top.min(row);
                bottom = bottom.max(row);
            }
        }

        return left.setRow(top).cellRange(right.setRow(bottom));
    }

    /**
     * Factory that creates a {@link SpreadsheetCellRangeReference}
     */
    static SpreadsheetCellRangeReference with(final Range<SpreadsheetCellReference> range) {
        SpreadsheetSelectionRangeRangeVisitor.check(range);

        return new SpreadsheetCellRangeReference(range);
    }

    /**
     * Private ctor
     */
    private SpreadsheetCellRangeReference(final Range<SpreadsheetCellReference> range) {
        super();
        this.range = range;
    }

    /**
     * Returns the top left cell reference.
     */
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public SpreadsheetCellReference begin() {
        return this.range.lowerBound().value().get(); // must exist
    }

    /**
     * Returns the bottom right cell reference.
     */
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public SpreadsheetCellReference end() {
        return this.range.upperBound().value().get(); // must exist
    }

    @Override
    public long count() {
        return this.columnRange().count() *
            this.rowRange().count();
    }

    @Override
    public boolean isFirst() {
        return this.begin().isFirst() && this.isUnit();
    }

    @Override
    public boolean isLast() {
        return this.begin().isLast() && this.isUnit();
    }

    /**
     * Returns a {@link SpreadsheetCellReference} that holds the top left cell reference.
     */
    @Override
    public Range<SpreadsheetCellReference> range() {
        return this.range;
    }

    private final Range<SpreadsheetCellReference> range;

    /**
     * Would be setter that accepts a pair of coordinates, and returns a range with those values,
     * creating a new instance if necessary.
     */
    public SpreadsheetCellRangeReference setRange(final Range<SpreadsheetCellReference> range) {
        return this.range.equals(range) ?
            this :
            with(range);
    }

    /**
     * Getter that returns the {@link SpreadsheetColumnRangeReference} component.
     */
    public SpreadsheetColumnRangeReference columnRange() {
        return columnRange(
            Range.greaterThanEquals(this.begin().column())
                .and(
                    Range.lessThanEquals(this.end().column())
                )
        );
    }

    /**
     * Would be setter that combines the new column reference range and the current row reference range,
     * returning a {@link SpreadsheetCellRangeReference} with the result.
     */
    public SpreadsheetCellRangeReference setColumnRange(final SpreadsheetColumnRangeReference columnRange) {
        Objects.requireNonNull(columnRange, "columnRangeReference");

        return this.setRange(
            Range.greaterThanEquals(
                this.begin()
                    .row()
                    .setColumn(columnRange.begin())
            ).and(
                Range.lessThanEquals(
                    this.end()
                        .row()
                        .setColumn(columnRange.end())
                )
            )
        );
    }

    /**
     * Getter that returns the {@link SpreadsheetRowRangeReference} component.
     */
    public SpreadsheetRowRangeReference rowRange() {
        return rowRange(
            Range.greaterThanEquals(this.begin().row())
                .and(
                    Range.lessThanEquals(this.end().row())
                )
        );
    }

    /**
     * Would be setter that combines the new column reference range and the current column reference range,
     * returning a {@link SpreadsheetCellRangeReference} with the result.
     */
    public SpreadsheetCellRangeReference setRowRange(final SpreadsheetRowRangeReference rowRange) {
        Objects.requireNonNull(rowRange, "rowRange");

        return this.setRange(
            Range.greaterThanEquals(this.begin().column().setRow(rowRange.begin()))
                .and(
                    Range.lessThanEquals(this.end().column().setRow(rowRange.end()))
                )
        );
    }

    /**
     * Returns the width of this range.
     */
    public int width() {
        return this.end()
            .column()
            .value()
            -
            this.begin()
                .column()
                .value()
            + 1;
    }

    /**
     * Returns the height of this range.
     */
    public int height() {
        return this.end()
            .row()
            .value()
            -
            this.begin()
                .row()
                .value()
            + 1;
    }

    // toXXX............................................................................................................

    @Override
    public SpreadsheetCellReference toCell() {
        return this.begin();
    }

    @Override
    public SpreadsheetColumnReference toColumn() {
        return this.begin().toColumn();
    }

    @Override
    public SpreadsheetColumnRangeReference toColumnRange() {
        return this.columnRange();
    }

    @Override
    public SpreadsheetRowReference toRow() {
        return this.toCell().row();
    }

    @Override
    public SpreadsheetRowRangeReference toRowRange() {
        return this.rowRange();
    }

    // xxxStream........................................................................................................

    /**
     * A stream that provides all {@link SpreadsheetColumnReference}.
     */
    public Stream<SpreadsheetColumnReference> columnStream() {
        return IntStream.range(this.begin().column().value(), this.end().column().value())
            .mapToObj(CELL_SPREADSHEET_REFERENCE_KIND::column);
    }

    /**
     * A stream that provides all {@link SpreadsheetRowReference}.
     */
    public Stream<SpreadsheetRowReference> rowStream() {
        return IntStream.range(this.begin().row().value(), this.end().row().value())
            .mapToObj(CELL_SPREADSHEET_REFERENCE_KIND::row);
    }

    /**
     * A stream that provides all {@link SpreadsheetCellReference}.
     */
    public Stream<SpreadsheetCellReference> cellStream() {
        final SpreadsheetCellReference begin = this.begin();

        final int rowOffset = begin.row().value();
        final int width = this.width();
        final int columnOffset = begin.column().value();

        return LongStream.range(0, (long) width * this.height())
            .mapToObj(index -> CELL_SPREADSHEET_REFERENCE_KIND.column(columnOffset + (int) (index % width))
                .setRow(CELL_SPREADSHEET_REFERENCE_KIND.row(rowOffset + (int) (index / width)))
            );
    }

    private final static SpreadsheetReferenceKind CELL_SPREADSHEET_REFERENCE_KIND = SpreadsheetReferenceKind.RELATIVE;

    /**
     * Visits all the {@link SpreadsheetCellReference} within this range, and dispatches either the present or absent
     * {@link Consumer} with present cells. The absent {@link Consumer} will receive absolute {@link SpreadsheetCellReference}.
     * Cells will be visited column across then rows down.
     */
    public void cells(final Collection<SpreadsheetCell> cells,
                      final Consumer<? super SpreadsheetCell> present,
                      final Consumer<? super SpreadsheetCellReference> absent) {
        this.cellStream()
            .forEach(SpreadsheetCellRangeReferenceCellsConsumer.with(cells, present, absent));
    }

    /**
     * {@see SpreadsheetCellRangeReferenceSortedMapSpreadsheetCellIterator}
     */
    public Iterator<SpreadsheetCell> cellsIterator(final SortedMap<SpreadsheetCellReference, SpreadsheetCell> referenceToCell) {
        return SpreadsheetCellRangeReferenceSortedMapSpreadsheetCellIterator.with(
            this,
            referenceToCell
        );
    }

    // SpreadsheetCellRange.............................................................................................

    /**
     * {@see SpreadsheetCellRange}
     */
    public SpreadsheetCellRange setValue(final Set<SpreadsheetCell> value) {
        return SpreadsheetCellRange.with(
            this,
            value
        );
    }

    // navigation.......................................................................................................

    @Override
    public Set<SpreadsheetViewportAnchor> anchors() {
        return ANCHORS;
    }

    private final static Set<SpreadsheetViewportAnchor> ANCHORS = Sets.readOnly(
        EnumSet.of(
            SpreadsheetViewportAnchor.TOP_LEFT,
            SpreadsheetViewportAnchor.TOP_RIGHT,
            SpreadsheetViewportAnchor.BOTTOM_LEFT,
            SpreadsheetViewportAnchor.BOTTOM_RIGHT
        )
    );

    @Override
    public SpreadsheetViewportAnchor defaultAnchor() {
        return SpreadsheetViewportAnchor.CELL_RANGE;
    }

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
        return anchor.opposite()
            .cell(this)
            .moveLeftColumn(
                anchor,
                context
            );
    }

    @Override
    public Optional<SpreadsheetSelection> moveLeftPixels(final SpreadsheetViewportAnchor anchor,
                                                         final int count,
                                                         final SpreadsheetViewportNavigationContext context) {
        return anchor.opposite()
            .cell(this)
            .moveLeftPixels(
                anchor,
                count,
                context
            );
    }

    @Override
    public Optional<SpreadsheetSelection> upRow(final SpreadsheetViewportAnchor anchor,
                                                final SpreadsheetViewportNavigationContext context) {
        return anchor.opposite()
            .cell(this)
            .upRow(
                anchor,
                context
            );
    }

    @Override
    public Optional<SpreadsheetSelection> upPixels(final SpreadsheetViewportAnchor anchor,
                                                   final int count,
                                                   final SpreadsheetViewportNavigationContext context) {
        return anchor.opposite()
            .cell(this)
            .upPixels(
                anchor,
                count,
                context
            );
    }

    @Override
    public Optional<SpreadsheetSelection> rightColumn(final SpreadsheetViewportAnchor anchor,
                                                      final SpreadsheetViewportNavigationContext context) {
        return anchor.opposite()
            .cell(this)
            .rightColumn(
                anchor,
                context
            );
    }

    @Override
    public Optional<SpreadsheetSelection> rightPixels(final SpreadsheetViewportAnchor anchor,
                                                      final int count,
                                                      final SpreadsheetViewportNavigationContext context) {
        return anchor.opposite()
            .cell(this)
            .rightPixels(
                anchor,
                count,
                context
            );
    }

    @Override
    public Optional<SpreadsheetSelection> downRow(final SpreadsheetViewportAnchor anchor,
                                                  final SpreadsheetViewportNavigationContext context) {
        return anchor.opposite()
            .cell(this)
            .downRow(
                anchor,
                context
            );
    }

    @Override
    public Optional<SpreadsheetSelection> downPixels(final SpreadsheetViewportAnchor anchor,
                                                     final int count,
                                                     final SpreadsheetViewportNavigationContext context) {
        return anchor.opposite()
            .cell(this)
            .downPixels(
                anchor,
                count,
                context
            );
    }

    @Override
    public Optional<AnchoredSpreadsheetSelection> extendLeftColumn(final SpreadsheetViewportAnchor anchor,
                                                                   final SpreadsheetViewportNavigationContext context) {
        return this.extendColumn(
            anchor,
            (c) -> Cast.to(
                c.moveLeftColumn(
                    anchor,
                    context
                )
            ),
            anchor::setRight,
            context
        );
    }

    @Override
    public Optional<AnchoredSpreadsheetSelection> extendLeftPixels(final SpreadsheetViewportAnchor anchor,
                                                                   final int count,
                                                                   final SpreadsheetViewportNavigationContext context) {
        return this.extendColumn(
            anchor,
            (c) -> Cast.to(
                c.moveLeftPixels(
                    anchor,
                    count,
                    context
                )
            ),
            anchor::setRight,
            context
        );
    }

    @Override
    public Optional<AnchoredSpreadsheetSelection> extendRightColumn(final SpreadsheetViewportAnchor anchor,
                                                                    final SpreadsheetViewportNavigationContext context) {
        return this.extendColumn(
            anchor,
            (c) -> Cast.to(
                c.rightColumn(
                    anchor,
                    context
                )
            ),
            anchor::setLeft,
            context
        );
    }

    @Override
    public Optional<AnchoredSpreadsheetSelection> extendRightPixels(final SpreadsheetViewportAnchor anchor,
                                                                    final int count,
                                                                    final SpreadsheetViewportNavigationContext context) {
        return this.extendColumn(
            anchor,
            (c) -> Cast.to(
                c.rightPixels(
                    anchor,
                    count,
                    context
                )
            ),
            anchor::setLeft,
            context
        );
    }

    private Optional<AnchoredSpreadsheetSelection> extendColumn(final SpreadsheetViewportAnchor anchor,
                                                                final Function<SpreadsheetColumnReference, Optional<SpreadsheetColumnReference>> move,
                                                                final Supplier<SpreadsheetViewportAnchor> singleColumnAnchor,
                                                                final SpreadsheetViewportNavigationContext context) {
        final SpreadsheetRowRangeReference rowRange = this.rowRange();
        final SpreadsheetColumnRangeReference columnRange = this.columnRange();

        return rowRange.isHidden(context) ?
            Optional.empty() :
            this.extendRange(
                move.apply(
                    anchor.opposite()
                        .column(columnRange)
                ).map(c -> c.setRow(
                    anchor.opposite()
                        .row(rowRange)
                )),
                anchor
            ).map(s -> s.setAnchorOrDefault(
                columnRange.isUnit() ?
                    singleColumnAnchor.get() :
                    anchor
            ));
    }

    @Override
    public Optional<AnchoredSpreadsheetSelection> extendUpRow(final SpreadsheetViewportAnchor anchor,
                                                              final SpreadsheetViewportNavigationContext context) {
        return this.extendRow(
            anchor,
            (r) -> Cast.to(
                r.upRow(
                    anchor,
                    context
                )
            ),
            anchor::setBottom,
            context
        );
    }

    @Override
    public Optional<AnchoredSpreadsheetSelection> extendUpPixels(final SpreadsheetViewportAnchor anchor,
                                                                 final int count,
                                                                 final SpreadsheetViewportNavigationContext context) {
        return this.extendRow(
            anchor,
            (r) -> Cast.to(
                r.upPixels(
                    anchor,
                    count,
                    context
                )
            ),
            anchor::setBottom,
            context
        );
    }

    @Override
    public Optional<AnchoredSpreadsheetSelection> extendDownRow(final SpreadsheetViewportAnchor anchor,
                                                                final SpreadsheetViewportNavigationContext context) {
        return this.extendRow(
            anchor,
            (r) -> Cast.to(
                r.downRow(
                    anchor,
                    context
                )
            ),
            anchor::setTop,
            context
        );
    }

    @Override
    public Optional<AnchoredSpreadsheetSelection> extendDownPixels(final SpreadsheetViewportAnchor anchor,
                                                                   final int count,
                                                                   final SpreadsheetViewportNavigationContext context) {
        return this.extendRow(
            anchor,
            (r) -> Cast.to(
                r.downPixels(
                    anchor,
                    count,
                    context
                )
            ),
            anchor::setTop,
            context
        );
    }

    private Optional<AnchoredSpreadsheetSelection> extendRow(final SpreadsheetViewportAnchor anchor,
                                                             final Function<SpreadsheetRowReference, Optional<SpreadsheetRowReference>> move,
                                                             final Supplier<SpreadsheetViewportAnchor> singleRowAnchor,
                                                             final SpreadsheetViewportNavigationContext context) {
        final SpreadsheetColumnRangeReference columnRange = this.columnRange();
        final SpreadsheetRowRangeReference rowRange = this.rowRange();

        return columnRange.isHidden(context) ?
            Optional.empty() :
            this.extendRange(
                move.apply(
                    anchor.opposite()
                        .row(rowRange)
                ).map(c -> c.setColumn(
                    anchor.opposite()
                        .column(columnRange)
                )),
                anchor
            ).map(s -> s.setAnchorOrDefault(
                rowRange.isUnit() ?
                    singleRowAnchor.get() :
                    anchor
            ));
    }

    @Override
    Optional<SpreadsheetSelection> extendRange(final Optional<? extends SpreadsheetSelection> other,
                                               final SpreadsheetViewportAnchor anchor) {
        return other.map(
            s -> anchor.cell(this)
                .cellRange(s.toCell())
                .toScalarIfUnit()
        );
    }

    // focused..........................................................................................................

    @Override
    public SpreadsheetCellReference focused(final SpreadsheetViewportAnchor anchor) {
        this.checkAnchor(anchor);
        return anchor.opposite()
            .column(this.columnRange())
            .setRow(
                anchor.opposite()
                    .row(this.rowRange()));
    }

    // toScalar.........................................................................................................

    @Override
    public SpreadsheetSelection toScalar() {
        return this.begin();
    }

    // toRange..........................................................................................................

    @Override
    public SpreadsheetCellRangeReference toRange() {
        return this;
    }

    // SpreadsheetSelection.............................................................................................

    @Override
    void accept(final SpreadsheetSelectionVisitor visitor) {
        visitor.visit(this);
    }

    // Iterable.........................................................................................................

    @Override
    public Iterator<SpreadsheetCellReference> iterator() {
        return this.cellStream()
            .iterator();
    }

    // add column/row...................................................................................................

    @Override
    public SpreadsheetCellRangeReference add(final int column,
                                             final int row) {
        return this.setColumnRange(
            this.columnRange().add(column)
        ).setRowRange(
            this.rowRange().add(row)
        );
    }

    @Override
    public SpreadsheetCellRangeReference addSaturated(final int column,
                                                      final int row) {
        return this.setColumnRange(
            this.columnRange().addSaturated(column)
        ).setRowRange(
            this.rowRange().addSaturated(row)
        );
    }

    /**
     * Adds the given deltas to the relative tokens of this {@link SpreadsheetCellRangeReference}.
     */
    @Override
    public SpreadsheetCellRangeReference addIfRelative(final int columnDelta,
                                                       final int rowDelta) {
        return this.setColumnRange(
            this.columnRange()
                .addIfRelative(columnDelta)
        ).setRowRange(
            this.rowRange()
                .addIfRelative(rowDelta)
        );
    }

    // replaceReferencesMapper..........................................................................................

    @Override
    Optional<Function<SpreadsheetCellReference, Optional<SpreadsheetCellReference>>> replaceReferencesMapper0(final SpreadsheetSelection movedTo) {
        return this.toScalar()
            .replaceReferencesMapper0(movedTo);
    }

    // CanReplaceReferences.............................................................................................

    @Override
    public SpreadsheetCellRangeReference replaceReferences(final Function<SpreadsheetCellReference, Optional<SpreadsheetCellReference>> mapper) {
        Objects.requireNonNull(mapper, "mapper");

        final Function<SpreadsheetCellReference, SpreadsheetCellReference> mapper2 =
            (cell) -> mapper.apply(cell)
                .orElseThrow(() -> new IllegalArgumentException("Mapper must return a cell"));

        final SpreadsheetCellReference begin = this.begin();
        final SpreadsheetCellReference end = this.end();

        final SpreadsheetCellReference begin2 = mapper2.apply(begin);
        final SpreadsheetCellReference end2 = mapper2.apply(end);

        SpreadsheetCellRangeReference replaced = this;

        if (false == begin.equals(begin2) && false == end.equals(end2)) {
            SpreadsheetColumnReference left = begin2.column();
            SpreadsheetColumnReference right = end2.column();

            if (left.compareTo(right) > 0) {
                final SpreadsheetColumnReference swap = left;
                left = right;
                right = swap;
            }

            SpreadsheetRowReference top = begin2.row();
            SpreadsheetRowReference bottom = end2.row();

            if (top.compareTo(bottom) > 0) {
                final SpreadsheetRowReference swap = top;
                top = bottom;
                bottom = swap;
            }

            replaced = left.setRow(top)
                .cellRange(
                    right.setRow(bottom)
                );
        }

        return replaced;
    }

    // testXXXX.........................................................................................................

    /**
     * Tests if this range contains the given {@link SpreadsheetCellReference}.
     */
    @Override
    boolean testCellNonNull(final SpreadsheetCellReference cell) {
        return this.testColumnNonNull(cell.column()) &&
            this.testRowNonNull(cell.row());
    }

    /**
     * Returns true if any part of the given range intersects this range.
     * {@link SpreadsheetCellRangeReference} that overlaps and contain cells inside and outside this range will return true.
     */
    @Override
    boolean testCellRangeNonNull(final SpreadsheetCellRangeReference range) {
        Objects.requireNonNull(range, "range");

        return this.columnRange()
            .testCellRangeNonNull(range) &&
            this.rowRange()
                .testCellRangeNonNull(range);
    }

    /**
     * Returns true if the column is within this range.
     */
    @Override
    boolean testColumnNonNull(final SpreadsheetColumnReference column) {
        return this.columnRange()
            .testColumnNonNull(column);
    }

    /**
     * Returns true if the row is within this range.
     */
    @Override
    boolean testRowNonNull(final SpreadsheetRowReference row) {
        return this.rowRange()
            .testRowNonNull(row);
    }

    // containsAll......................................................................................................

    /**
     * Only returns true if the given {@link SpreadsheetViewportWindows} is entirely within this {@link SpreadsheetCellRangeReference}.
     */
    boolean containsAll0(final SpreadsheetViewportWindows windows) {
        return this.equalsIgnoreReferenceKind(ALL_CELLS) ||
            windows.isEmpty() ||
            windows.cellRanges()
                .stream()
                .allMatch(this::containsAll);
    }

    // containsAll......................................................................................................

    /**
     * Only returns true if the given {@link SpreadsheetCellRangeReference} is entirely within this {@link SpreadsheetCellRangeReference}.
     */
    public boolean containsAll(final SpreadsheetCellRangeReference cellRange) {
        Objects.requireNonNull(cellRange, "cellRange");

        return this.testCellNonNull(
            cellRange.begin()
        ) && this.testCellNonNull(
            cellRange.end()
        );
    }

    // HasParserToken...................................................................................................

    @Override
    public CellRangeSpreadsheetFormulaParserToken toParserToken() {
        return SpreadsheetFormulaParserToken.cellRange(
            Lists.of(
                this.begin()
                    .toParserToken(),
                this.end()
                    .toParserToken()
            ),
            this.text()
        );
    }

    // HashCodeEqualsDefined............................................................................................

    @Override
    public int hashCode() {
        return this.range.hashCode();
    }

    @Override
    boolean equalsNotSameAndNotNull(final Object other,
                                    final boolean includeKind) {
        return this.equals1(
            (SpreadsheetCellRangeReference) other,
            includeKind
        );
    }

    private boolean equals1(final SpreadsheetCellRangeReference other,
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

    // toRelative.......................................................................................................

    @Override
    public SpreadsheetCellRangeReference toRelative() {
        final SpreadsheetCellRangeReference relative = this.begin()
            .toRelative()
            .cellRange(this.end()
                .toRelative());
        return this.equals(relative) ?
            this :
            relative;
    }

    // Comparable.......................................................................................................

    /**
     * Compares two {@link SpreadsheetCellRangeReference} where the {@link SpreadsheetReferenceKind} is irrelevant.
     */
    @Override
    public int compareTo(final SpreadsheetCellRangeReference other) {
        final int compare = this.begin().compareTo(other.begin());
        return 0 == compare ?
            this.end().compareTo(other.end()) :
            compare;
    }

    // SpreadsheetSelectionIgnoresReferenceKindComparator...............................................................

    @Override
    int spreadsheetSelectionIgnoresReferenceKindComparatorPriority() {
        return SpreadsheetSelectionIgnoresReferenceKindComparator.CELL_RANGE;
    }
}

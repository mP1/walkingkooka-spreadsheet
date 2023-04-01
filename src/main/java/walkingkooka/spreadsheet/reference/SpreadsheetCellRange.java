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

import walkingkooka.collect.HasRange;
import walkingkooka.collect.HasRangeBounds;
import walkingkooka.collect.Range;
import walkingkooka.collect.list.Lists;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetViewport;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStore;
import walkingkooka.spreadsheet.store.SpreadsheetRowStore;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Holds a range. Note the begin component is always before the end, with rows being the significant axis before column.
 * <br>
 * When {@link #compareTo(SpreadsheetCellRange)} the {@link SpreadsheetReferenceKind} is ignored, which is also true
 * of other {@link SpreadsheetSelection}.
 */
@SuppressWarnings("lgtm[java/inconsistent-equals-and-hashcode]")
public final class SpreadsheetCellRange extends SpreadsheetCellReferenceOrRange
        implements Comparable<SpreadsheetCellRange>,
        HasRange<SpreadsheetCellReference>,
        HasRangeBounds<SpreadsheetCellReference>,
        Iterable<SpreadsheetCellReference> {

    /**
     * A {@link SpreadsheetColumnReferenceRange} that includes all cells.
     */
    public static final SpreadsheetCellRange ALL = SpreadsheetColumnReferenceRange.ALL
            .setRowReferenceRange(SpreadsheetRowReferenceRange.ALL);

    /**
     * Computes the range of the given cells.
     */
    public static SpreadsheetCellRange fromCells(final List<SpreadsheetCellReference> cells) {
        Objects.requireNonNull(cells, "cells");

        final List<SpreadsheetCellReference> copy = Lists.immutable(cells);

        SpreadsheetCellRange range;
        switch (copy.size()) {
            case 0:
                throw new IllegalArgumentException("Cells empty");
            case 1:
                range = with(Range.singleton(copy.get(0)));
                break;
            default:
                range = computeEnclosingRange(copy);
                break;
        }

        return range;
    }

    @SuppressWarnings("lgtm[java/dereferenced-value-may-be-null]")
    private static SpreadsheetCellRange computeEnclosingRange(final List<SpreadsheetCellReference> cells) {
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
     * Factory that creates a {@link SpreadsheetCellRange}
     */
    static SpreadsheetCellRange with(final Range<SpreadsheetCellReference> range) {
        SpreadsheetRangeRangeVisitor.check(range);

        return new SpreadsheetCellRange(range);
    }

    /**
     * Private ctor
     */
    private SpreadsheetCellRange(final Range<SpreadsheetCellReference> range) {
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
    public int count() {
        return this.columnReferenceRange().count() *
                this.rowReferenceRange().count();
    }

    @Override
    public boolean isAll() {
        return this.begin().isFirst() && this.end().isLast();
    }

    @Override
    public boolean isFirst() {
        return this.begin().isFirst() && this.isSingleCell();
    }

    @Override
    public boolean isLast() {
        return this.begin().isLast() && this.isSingleCell();
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
    public SpreadsheetCellRange setRange(final Range<SpreadsheetCellReference> range) {
        return this.range.equals(range) ?
                this :
                with(range);
    }

    /**
     * Getter that returns the {@link SpreadsheetColumnReferenceRange} component.
     */
    public SpreadsheetColumnReferenceRange columnReferenceRange() {
        return columnRange(
                Range.greaterThanEquals(this.begin().column())
                        .and(
                                Range.lessThanEquals(this.end().column())
                        )
        );
    }

    /**
     * Would be setter that combines the new column reference range and the current row reference range,
     * returning a {@link SpreadsheetCellRange} with the result.
     */
    public SpreadsheetCellRange setColumnReferenceRange(final SpreadsheetColumnReferenceRange columnReferenceRange) {
        checkColumnReferenceRange(columnReferenceRange);

        return this.setRange(
                Range.greaterThanEquals(this.begin().row().setColumn(columnReferenceRange.begin()))
                        .and(
                                Range.lessThanEquals(this.end().row().setColumn(columnReferenceRange.end()))
                        )
        );
    }

    /**
     * Getter that returns the {@link SpreadsheetRowReferenceRange} component.
     */
    public SpreadsheetRowReferenceRange rowReferenceRange() {
        return rowRange(
                Range.greaterThanEquals(this.begin().row())
                        .and(
                                Range.lessThanEquals(this.end().row())
                        )
        );
    }

    /**
     * Would be setter that combines the new column reference range and the current column reference range,
     * returning a {@link SpreadsheetCellRange} with the result.
     */
    public SpreadsheetCellRange setRowReferenceRange(final SpreadsheetRowReferenceRange rowReferenceRange) {
        checkRowReferenceRange(rowReferenceRange);

        return this.setRange(
                Range.greaterThanEquals(this.begin().column().setRow(rowReferenceRange.begin()))
                        .and(
                                Range.lessThanEquals(this.end().column().setRow(rowReferenceRange.end()))
                        )
        );
    }

    /**
     * Returns true only if this range covers a single cell.
     */
    public boolean isSingleCell() {
        return this.begin()
                .equalsIgnoreReferenceKind(this.end());
    }

    /**
     * Returns the width of this range.
     */
    public int width() {
        return this.end().column().value() - this.begin().column().value() + 1;
    }

    /**
     * Returns the height of this range.
     */
    public int height() {
        return this.end().row().value() - this.begin().row().value() + 1;
    }

    @Override
    public SpreadsheetCellReference toCell() {
        return this.begin();
    }

    @Override
    public SpreadsheetColumnReference toColumn() {
        return this.begin().toColumn();
    }

    @Override
    public SpreadsheetColumnReferenceRange toColumnRange() {
        return this.columnReferenceRange();
    }

    @Override
    public SpreadsheetRowReference toRow() {
        return this.toCell().row();
    }

    @Override
    public SpreadsheetRowReferenceRange toRowRange() {
        return this.rowReferenceRange();
    }

    /**
     * Creates a {@link SpreadsheetViewport} with the begin or top/left cell.
     */
    @Override
    public SpreadsheetViewport viewport(final double width,
                                        final double height) {
        return SpreadsheetViewport.with(
                this.begin(),
                width,
                height
        );
    }

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

        return IntStream.range(0, width * this.height())
                .mapToObj(index -> CELL_SPREADSHEET_REFERENCE_KIND.column(columnOffset + (index % width))
                        .setRow(CELL_SPREADSHEET_REFERENCE_KIND.row(rowOffset + (index / width)))
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
                .forEach(SpreadsheetCellRangeCellsConsumer.with(cells, present, absent));
    }

    // navigation.......................................................................................................

    @Override
    Set<SpreadsheetViewportSelectionAnchor> anchors() {
        return ANCHORS;
    }

    private final static EnumSet<SpreadsheetViewportSelectionAnchor> ANCHORS = EnumSet.of(
            SpreadsheetViewportSelectionAnchor.TOP_LEFT,
            SpreadsheetViewportSelectionAnchor.TOP_RIGHT,
            SpreadsheetViewportSelectionAnchor.BOTTOM_LEFT,
            SpreadsheetViewportSelectionAnchor.BOTTOM_RIGHT
    );

    @Override
    public SpreadsheetViewportSelectionAnchor defaultAnchor() {
        return SpreadsheetViewportSelectionAnchor.CELL_RANGE;
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
    Optional<SpreadsheetSelection> left(final SpreadsheetViewportSelectionAnchor anchor,
                                        final SpreadsheetColumnStore columnStore,
                                        final SpreadsheetRowStore rowStore) {
        return anchor.cell(this)
                .left(anchor, columnStore, rowStore);
    }

    @Override
    Optional<SpreadsheetSelection> up(final SpreadsheetViewportSelectionAnchor anchor,
                                      final SpreadsheetColumnStore columnStore,
                                      final SpreadsheetRowStore rowStore) {
        return anchor.cell(this)
                .up(anchor, columnStore, rowStore);
    }

    @Override
    Optional<SpreadsheetSelection> right(final SpreadsheetViewportSelectionAnchor anchor,
                                         final SpreadsheetColumnStore columnStore,
                                         final SpreadsheetRowStore rowStore) {
        return anchor.cell(this)
                .right(anchor, columnStore, rowStore);
    }

    @Override
    Optional<SpreadsheetSelection> down(final SpreadsheetViewportSelectionAnchor anchor,
                                        final SpreadsheetColumnStore columnStore,
                                        final SpreadsheetRowStore rowStore) {
        return anchor.cell(this)
                .down(anchor, columnStore, rowStore);
    }

    @Override
    Optional<SpreadsheetViewportSelection> extendLeft(final SpreadsheetViewportSelectionAnchor anchor,
                                                      final SpreadsheetColumnStore columnStore,
                                                      final SpreadsheetRowStore rowStore) {
        return this.extendColumn(
                anchor,
                columnStore::leftSkipHidden,
                anchor::setRight,
                columnStore,
                rowStore
        );
    }

    @Override
    Optional<SpreadsheetViewportSelection> extendRight(final SpreadsheetViewportSelectionAnchor anchor,
                                                       final SpreadsheetColumnStore columnStore,
                                                       final SpreadsheetRowStore rowStore) {
        return this.extendColumn(
                anchor,
                columnStore::rightSkipHidden,
                anchor::setLeft,
                columnStore,
                rowStore
        );
    }

    private Optional<SpreadsheetViewportSelection> extendColumn(final SpreadsheetViewportSelectionAnchor anchor,
                                                                final Function<SpreadsheetColumnReference, Optional<SpreadsheetColumnReference>> move,
                                                                final Supplier<SpreadsheetViewportSelectionAnchor> singleColumnAnchor,
                                                                final SpreadsheetColumnStore columnStore,
                                                                final SpreadsheetRowStore rowStore) {
        final SpreadsheetRowReferenceRange rowRange = this.rowReferenceRange();
        final SpreadsheetColumnReferenceRange columnRange = this.columnReferenceRange();

        return rowRange.isHidden(columnStore, rowStore) ?
                Optional.empty() :
                this.extendRange(
                move.apply(
                        anchor.column(columnRange)
                ).map(c -> c.setRow(
                        anchor.row(rowRange)
                )),
                anchor
        ).map(s -> s.setAnchorOrDefault(
                columnRange.isSingle() ?
                        singleColumnAnchor.get() :
                        anchor
        ));
    }

    @Override
    Optional<SpreadsheetSelection> extendRange(final Optional<? extends SpreadsheetSelection> other,
                                               final SpreadsheetViewportSelectionAnchor anchor) {
        return other.map(
                s -> anchor.fixedCell(this)
                        .cellRange((SpreadsheetCellReference) s)
                        .simplify()
        );
    }

    @Override
    Optional<SpreadsheetViewportSelection> extendUp(final SpreadsheetViewportSelectionAnchor anchor,
                                                    final SpreadsheetColumnStore columnStore,
                                                    final SpreadsheetRowStore rowStore) {
        return this.extendRow(
                anchor,
                (r) -> r.up(rowStore),
                anchor::setBottom,
                columnStore,
                rowStore
        );
    }

    @Override
    Optional<SpreadsheetViewportSelection> extendDown(final SpreadsheetViewportSelectionAnchor anchor,
                                                      final SpreadsheetColumnStore columnStore,
                                                      final SpreadsheetRowStore rowStore) {
        return this.extendRow(
                anchor,
                (r) -> r.down(rowStore),
                anchor::setTop,
                columnStore,
                rowStore
        );
    }

    private Optional<SpreadsheetViewportSelection> extendRow(final SpreadsheetViewportSelectionAnchor anchor,
                                                             final Function<SpreadsheetRowReference, Optional<SpreadsheetRowReference>> move,
                                                             final Supplier<SpreadsheetViewportSelectionAnchor> singleRowAnchor,
                                                             final SpreadsheetColumnStore columnStore,
                                                             final SpreadsheetRowStore rowStore) {
        final SpreadsheetColumnReferenceRange columnRange = this.columnReferenceRange();
        final SpreadsheetRowReferenceRange rowRange = this.rowReferenceRange();

        return columnRange.isHidden(columnStore, rowStore) ?
                Optional.empty() :
                this.extendRange(
                        move.apply(
                                anchor.row(rowRange)
                        ).map(c -> c.setColumn(
                                anchor.column(columnRange)
                        )),
                        anchor
                ).map(s -> s.setAnchorOrDefault(
                        rowRange.isSingle() ?
                                singleRowAnchor.get() :
                                anchor
                ));
    }

    // focused...........................................................................................................

    @Override
    public SpreadsheetCellReference focused(final SpreadsheetViewportSelectionAnchor anchor) {
        this.checkAnchor(anchor);
        return anchor.column(this.columnReferenceRange())
                .setRow(anchor.row(this.rowReferenceRange()));
    }

    // simplify.........................................................................................................

    @Override
    public SpreadsheetSelection simplify() {
        return this.isSingleCell() ?
                this.begin() :
                this;
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

    // testXXXX.........................................................................................................

    /**
     * Tests if this range test the given {@link SpreadsheetCellReference}.
     */
    @Override
    boolean testCell0(final SpreadsheetCellReference cell) {
        return this.testColumn0(cell.column()) &&
                this.testRow0(cell.row());
    }

    /**
     * Returns true if any part of the given range intersects this range.
     */
    @Override
    boolean testCellRange0(final SpreadsheetCellRange range) {
        checkCellRange(range);

        return this.columnReferenceRange().testCellRange0(range) &&
                this.rowReferenceRange().testCellRange0(range);
    }

    /**
     * Returns true if the column is within this range.
     */
    @Override
    boolean testColumn0(final SpreadsheetColumnReference column) {
        return this.columnReferenceRange()
                .testColumn0(column);
    }

    /**
     * Returns true if the row is within this range.
     */
    @Override
    boolean testRow0(final SpreadsheetRowReference row) {
        return this.rowReferenceRange()
                .testRow0(row);
    }

    // HashCodeEqualsDefined.......................................................................................

    @Override
    public int hashCode() {
        return this.range.hashCode();
    }

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof SpreadsheetCellRange;
    }

    @Override
    boolean equals0(final Object other,
                    final boolean includeKind) {
        return this.equals1(
                (SpreadsheetCellRange) other,
                includeKind
        );
    }

    private boolean equals1(final SpreadsheetCellRange other,
                            final boolean includeKind) {
        return this.begin().equals0(other.begin(), includeKind) &&
                this.end().equals0(other.end(), includeKind);
    }

    // toString........................................................................................................

    @Override
    public String toString() {
        return this.isSingleCell() ?
                this.begin().toString() :
                this.begin() + SEPARATOR.string() + this.end();
    }

    // toRelative.......................................................................................................

    @Override
    public SpreadsheetCellRange toRelative() {
        final SpreadsheetCellRange relative = this.begin()
                .toRelative()
                .cellRange(this.end()
                        .toRelative());
        return this.equals(relative) ?
                this :
                relative;
    }

    // Comparable......................................................................................................

    /**
     * Compares two {@link SpreadsheetCellRange} where the {@link SpreadsheetReferenceKind} is irrelevant.
     */
    @Override
    public int compareTo(final SpreadsheetCellRange other) {
        final int compare = this.begin().compareTo(other.begin());
        return 0 == compare ?
                this.end().compareTo(other.end()) :
                compare;
    }
}

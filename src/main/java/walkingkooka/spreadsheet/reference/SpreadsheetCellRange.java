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
import walkingkooka.collect.list.Lists;
import walkingkooka.spreadsheet.SpreadsheetCell;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Holds a range. Note the begin component is always before the end, with rows being the significant axis before column.
 */
@SuppressWarnings("lgtm[java/inconsistent-equals-and-hashcode]")
public final class SpreadsheetCellRange extends SpreadsheetExpressionReference {

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
                range = computeRangeFromManyCells(copy);
                break;
        }

        return range;
    }

    @SuppressWarnings("lgtm[java/dereferenced-value-may-be-null]")
    private static SpreadsheetCellRange computeRangeFromManyCells(final List<SpreadsheetCellReference> cells) {
        SpreadsheetColumnReference left = null;
        SpreadsheetRowReference top = null;

        SpreadsheetColumnReference right = null;
        SpreadsheetRowReference bottom = null;

        for (SpreadsheetCellReference cell : cells) {
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

        return left.setRow(top).spreadsheetCellRange(right.setRow(bottom));
    }

    /**
     * Factory that creates a {@link SpreadsheetCellRange}
     */
    public static SpreadsheetCellRange with(final Range<SpreadsheetCellReference> range) {
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
    public SpreadsheetCellReference begin() {
        return this.range.lowerBound().value().get(); // must exist
    }

    /**
     * Returns the bottom right cell reference.
     */
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public SpreadsheetCellReference end() {
        return this.range.upperBound().value().get(); // must exist
    }

    /**
     * Returns a {@link SpreadsheetCellReference} that holds the top left cell reference.
     */
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
     * Returns true only if this range covers a single cell.
     */
    public boolean isSingleCell() {
        return this.begin().equals(this.end());
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

    // SpreadsheetSelection.............................................................................................

    @Override
    void accept(final SpreadsheetSelectionVisitor visitor) {
        visitor.visit(this);
    }

    // SpreadsheetExpressionReferenceVisitor............................................................................

    @Override
    void accept(final SpreadsheetExpressionReferenceVisitor visitor) {
        visitor.visit(this);
    }

    // Predicate........................................................................................................

    /**
     * Tests if this range test the given {@link SpreadsheetCellReference}.
     */
    @Override
    public boolean test(final SpreadsheetCellReference reference) {
        checkCellReference(reference);

        return reference.column()
                .testCellRange(this) &&
                reference.row()
                        .testCellRange(this);
    }

    // testCellRange.....................................................................................................

    @Override
    public boolean testCellRange(final SpreadsheetCellRange range) {
        throw new UnsupportedOperationException(); // TODO implement when cell range selections are supported.
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
    boolean equals0(final Object other) {
        return this.equals1(Cast.to(other));
    }

    private boolean equals1(final SpreadsheetCellRange other) {
        return this.range.equals(other.range);
    }

    // toString........................................................................................................

    @Override
    public String toString() {
        return this.isSingleCell() ?
                this.begin().toString() :
                this.begin() + SEPARATOR + this.end();
    }

    // equalsIgnoreReferenceKind........................................................................................

    @Override
    boolean equalsIgnoreReferenceKind0(final Object other) {
        return this.equalsIgnoreReferenceKind1((SpreadsheetCellRange) other);
    }

    private boolean equalsIgnoreReferenceKind1(final SpreadsheetCellRange other) {
        return this.begin().equalsIgnoreReferenceKind(other.begin()) &&
                this.end().equalsIgnoreReferenceKind(other.end());
    }

    // toRelative.......................................................................................................

    @Override
    public SpreadsheetCellRange toRelative() {
        final SpreadsheetCellRange relative = this.begin()
                .toRelative()
                .spreadsheetCellRange(this.end()
                        .toRelative());
        return this.equals(relative) ?
                this :
                relative;
    }
}

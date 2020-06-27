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
import walkingkooka.collect.list.Lists;
import walkingkooka.compare.Comparators;
import walkingkooka.collect.Range;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.text.CharSequences;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Holds a range. Note the begin component is always before the end, with rows being the significant axis before column.
 */
@SuppressWarnings("lgtm[java/inconsistent-equals-and-hashcode]")
public final class SpreadsheetRange extends SpreadsheetExpressionReference implements
        Comparable<SpreadsheetRange>,
        Predicate<SpreadsheetCellReference> {

    /**
     * Factory that parses some text holding a range.
     */
    static SpreadsheetRange parseRange0(final String text) {
        CharSequences.failIfNullOrEmpty(text, "text");

        final int colon = text.indexOf(SEPARATOR);

        return -1 == colon ?
                cellToRange(parseRange1(text, "range", text)) :
                parseRange2(text, colon);
    }

    private static SpreadsheetCellReference parseRange1(final String component,
                                                        final String label,
                                                        final String text) {
        try {
            return SpreadsheetExpressionReference.parseCellReference(component);
        } catch (final IllegalArgumentException cause) {
            throw new IllegalArgumentException("Invalid " + label + " in " + CharSequences.quote(text), cause);
        }
    }

    static SpreadsheetRange cellToRange(final SpreadsheetCellReference cell) {
        return with(cell.range(cell));
    }

    private static SpreadsheetRange parseRange2(final String text,
                                                final int colon) {
        if (0 == colon) {
            throw new IllegalArgumentException("Missing begin =" + CharSequences.quote(text));
        }

        if (colon + SEPARATOR.length() == text.length()) {
            throw new IllegalArgumentException("Missing end =" + CharSequences.quote(text));
        }

        return parseRange1(text.substring(0, colon), "begin", text)
                .spreadsheetRange(parseRange1(text.substring(colon + SEPARATOR.length()), "end", text));
    }

    /**
     * Separator between two cell references.
     */
    private final static String SEPARATOR = ":";

    /**
     * Computes the range of the given cells.
     */
    public static SpreadsheetRange fromCells(final List<SpreadsheetCellReference> cells) {
        Objects.requireNonNull(cells, "cells");

        final List<SpreadsheetCellReference> copy = Lists.immutable(cells);

        SpreadsheetRange range;
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
    private static SpreadsheetRange computeRangeFromManyCells(final List<SpreadsheetCellReference> cells) {
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
                // column
                final SpreadsheetColumnReference cellColumn = cell.column();
                if (cellColumn.compareTo(left) < Comparators.EQUAL) {
                    left = cellColumn;
                } else {
                    if (cellColumn.compareTo(right) > Comparators.EQUAL) {
                        right = cellColumn;
                    }
                }

                // row
                final SpreadsheetRowReference cellRow = cell.row();
                if (cellRow.compareTo(top) < Comparators.EQUAL) {
                    top = cellRow;
                } else {
                    if (cellRow.compareTo(bottom) > Comparators.EQUAL) {
                        bottom = cellRow;
                    }
                }
            }
        }

        return left.setRow(top).spreadsheetRange(right.setRow(bottom));
    }

    /**
     * Factory that creates a {@link SpreadsheetRange}
     */
    public static SpreadsheetRange with(final Range<SpreadsheetCellReference> range) {
        SpreadsheetRangeRangeVisitor.check(range);

        return new SpreadsheetRange(range);
    }

    /**
     * Private ctor
     */
    private SpreadsheetRange(final Range<SpreadsheetCellReference> range) {
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
    public SpreadsheetRange setRange(final Range<SpreadsheetCellReference> range) {
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
                .mapToObj(SpreadsheetReferenceKind.ABSOLUTE::column);
    }

    /**
     * A stream that provides all {@link SpreadsheetRowReference}.
     */
    public Stream<SpreadsheetRowReference> rowStream() {
        return IntStream.range(this.begin().row().value(), this.end().row().value())
                .mapToObj(SpreadsheetReferenceKind.ABSOLUTE::row);
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
                .mapToObj(index -> SpreadsheetReferenceKind.ABSOLUTE.column(columnOffset + (index % width))
                        .setRow(SpreadsheetReferenceKind.ABSOLUTE.row(rowOffset + (index / width)))
                );
    }

    /**
     * Visits all the {@link SpreadsheetCellReference} within this range, and dispatches either the present or absent
     * {@link Consumer} with present cells. The absent {@link Consumer} will receive absolute {@link SpreadsheetCellReference}.
     * Cells will be visited column across then rows down.
     */
    public void cells(final Collection<SpreadsheetCell> cells,
                      final Consumer<? super SpreadsheetCell> present,
                      final Consumer<? super SpreadsheetCellReference> absent) {
        this.cellStream()
                .forEach(SpreadsheetRangeCellsConsumer.with(cells, present, absent));
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
        Objects.requireNonNull(reference, "reference");

        final SpreadsheetRowReference row = reference.row();
        final SpreadsheetColumnReference column = reference.column();

        final SpreadsheetCellReference begin = this.begin();
        final SpreadsheetCellReference end = this.end();

        return row.compareTo(begin.row()) >= 0 &&
                column.compareTo(begin.column()) >= 0 &&
                row.compareTo(end.row()) <= 0 &&
                column.compareTo(end.column()) <= 0;

    }

    // HashCodeEqualsDefined.......................................................................................

    @Override
    public int hashCode() {
        return this.range.hashCode();
    }

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof SpreadsheetRange;
    }

    @Override
    boolean equals0(final Object other) {
        return this.equals1(Cast.to(other));
    }

    private boolean equals1(final SpreadsheetRange other) {
        return this.range.equals(other.range);
    }

    @Override
    public String toString() {
        return this.isSingleCell() ?
                this.begin().toString() :
                this.begin() + SEPARATOR + this.end();
    }

    // SpreadsheetExpressionReferenceComparator........................................................................

    @Override
    public int compareTo(final SpreadsheetRange other) {
        throw new UnsupportedOperationException();
    }

    @Override
    final int compare(final SpreadsheetExpressionReference other) {
        throw new UnsupportedOperationException();
    }

    @Override
    final int compare0(final SpreadsheetCellReference other) {
        throw new UnsupportedOperationException();
    }

    @Override
    final int compare0(final SpreadsheetLabelName other) {
        throw new UnsupportedOperationException();
    }
}

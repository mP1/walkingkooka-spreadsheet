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

package walkingkooka.spreadsheet;

import walkingkooka.Cast;
import walkingkooka.compare.Range;
import walkingkooka.net.http.server.hateos.HasHateosLinkId;
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStore;
import walkingkooka.text.CharSequences;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Holds a range. Note the begin component is always before the end, with rows being the significant axis before column.
 */
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
                cell0(parseRange1(text, "range", text)) :
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

    static SpreadsheetRange cell0(final SpreadsheetCellReference cell) {
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
    public final static String SEPARATOR = ":";

    /**
     * Computes the range of the given cells.
     */
    public static SpreadsheetRange fromCells(final List<SpreadsheetCellReference> cells) {
        Objects.requireNonNull(cells, "cells");

        SpreadsheetCellReference topLeft = null;
        SpreadsheetCellReference bottomRight = null;

        for (SpreadsheetCellReference cell : cells) {
            if (null == topLeft) {
                topLeft = cell;
                bottomRight = cell;
            } else {
                topLeft = topLeft.lower(cell);
                bottomRight = bottomRight.upper(cell);
            }
        }

        return topLeft.spreadsheetRange(bottomRight);
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
    public SpreadsheetCellReference begin() {
        return this.range.lowerBound().value().get(); // must exist
    }

    /**
     * Returns the bottom right cell reference.
     */
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
                .mapToObj(i -> SpreadsheetReferenceKind.ABSOLUTE.column(i));
    }

    /**
     * A stream that provides all {@link SpreadsheetRowReference}.
     */
    public Stream<SpreadsheetRowReference> rowStream() {
        return IntStream.range(this.begin().row().value(), this.end().row().value())
                .mapToObj(i -> SpreadsheetReferenceKind.ABSOLUTE.row(i));
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
                .mapToObj(index -> {
                            return SpreadsheetReferenceKind.ABSOLUTE.column(columnOffset + (index % width))
                                    .setRow(SpreadsheetReferenceKind.ABSOLUTE.row(rowOffset + (index / width)));
                        }
                );
    }

    public void clear(final SpreadsheetCellStore store) {
        Objects.requireNonNull(store, "store");

        this.cellStream().forEach(c -> store.delete(c));
    }

    // is...............................................................................................................

    @Override
    public boolean isCellReference() {
        return false;
    }

    @Override
    public boolean isLabelName() {
        return false;
    }

    @Override
    public boolean isRange() {
        return true;
    }

    // HasHateosLinkId..................................................................................................

    @Override
    public String hateosLinkId() {
        final String begin = this.begin().hateosLinkId();
        return this.isSingleCell() ?
                begin :
                begin + HasHateosLinkId.HATEOS_LINK_RANGE_SEPARATOR + this.end().hateosLinkId();
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

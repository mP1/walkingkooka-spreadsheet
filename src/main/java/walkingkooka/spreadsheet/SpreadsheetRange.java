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
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStore;
import walkingkooka.test.HashCodeEqualsDefined;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.json.HasJsonNode;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonNodeException;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Holds a range. Note the begin component is always before the end, with rows being the significant axis before column.
 */
public final class SpreadsheetRange implements ExpressionReference,
        HashCodeEqualsDefined,
        HasJsonNode,
        Comparable<SpreadsheetRange>,
        Predicate<SpreadsheetCellReference> {

    /**
     * Factory that parses some text holding a range.
     */
    public static SpreadsheetRange parse(final String text) {
        CharSequences.failIfNullOrEmpty(text, "text");

        final int colon = text.indexOf(SEPARATOR);

        return -1 == colon ?
                cell0(parse1(text, "range", text)) :
                parse0(text, colon);
    }

    private static SpreadsheetRange parse0(final String text,
                                           final int colon) {
        if (0 == colon) {
            throw new IllegalArgumentException("Missing begin =" + CharSequences.quote(text));
        }

        if (colon + SEPARATOR.length() == text.length() - 1) {
            throw new IllegalArgumentException("Missing end =" + CharSequences.quote(text));
        }

        return parse1(text.substring(0, colon), "begin", text)
                .spreadsheetRange(parse1(text.substring(colon + SEPARATOR.length()), "end", text));
    }

    private static SpreadsheetCellReference parse1(final String component,
                                                   final String label,
                                                   final String text) {
        try {
            return SpreadsheetExpressionReference.parseCellReference(component);
        } catch (final IllegalArgumentException cause) {
            throw new IllegalArgumentException("Invalid " + label + " in " + CharSequences.quote(text), cause);
        }
    }

    private final static String SEPARATOR = ":";

    /**
     * Computes the range of the given cells.
     */
    public static SpreadsheetRange from(final List<SpreadsheetCellReference> cells) {
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
    public static SpreadsheetRange cell(final SpreadsheetCellReference cell) {
        Objects.requireNonNull(cell, "cell");
        return cell0(cell);
    }

    private static SpreadsheetRange cell0(final SpreadsheetCellReference cell) {
        return with(cell.range(cell));
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

    // HasJsonNode......................................................................................................

    /**
     * Factory that creates a {@link SpreadsheetRange} from a {@link JsonNode} holding the range in string form.
     */
    public static SpreadsheetRange fromJsonNode(final JsonNode node) {
        Objects.requireNonNull(node, "node");

        try {
            return parse(node.stringValueOrFail());
        } catch (final JsonNodeException cause) {
            throw new IllegalArgumentException(cause.getMessage(), cause);
        }
    }

    @Override
    public JsonNode toJsonNode() {
        return JsonNode.string(this.toString());
    }

    static {
        HasJsonNode.register("spreadsheet-range",
                SpreadsheetRange::fromJsonNode,
                SpreadsheetRange.class);
    }

    // HashCodeEqualsDefined.......................................................................................

    @Override
    public int hashCode() {
        return this.range.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetRange &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetRange other) {
        return this.range.equals(other.range);
    }

    @Override
    public String toString() {
        return this.isSingleCell() ?
                this.begin().toString() :
                this.begin() + SEPARATOR + this.end();
    }

    // Comparable.......................................................................................

    @Override
    public int compareTo(final SpreadsheetRange other) {
        throw new UnsupportedOperationException();
    }
}

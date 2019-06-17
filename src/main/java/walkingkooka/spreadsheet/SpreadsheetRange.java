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
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStore;
import walkingkooka.test.HashCodeEqualsDefined;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.json.HasJsonNode;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonNodeException;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Holds a range. Note the begin component is always before the end, with rows being the significant axis before column.
 */
public final class SpreadsheetRange implements ExpressionReference,
        HashCodeEqualsDefined,
        HasJsonNode,
        Comparable<SpreadsheetRange> {

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

        return with(parse1(text.substring(0, colon), "begin", text),
                parse1(text.substring(colon + SEPARATOR.length()), "end", text));
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

        return SpreadsheetRange.with(topLeft, bottomRight);
    }

    /**
     * Factory that creates a {@link SpreadsheetRange}
     */
    public static SpreadsheetRange cell(final SpreadsheetCellReference cell) {
        Objects.requireNonNull(cell, "cell");
        return cell0(cell);
    }

    private static SpreadsheetRange cell0(final SpreadsheetCellReference cell) {
        return new SpreadsheetRange(cell, cell);
    }

    /**
     * Factory that creates a {@link SpreadsheetRange}
     */
    public static SpreadsheetRange with(final SpreadsheetCellReference begin, final SpreadsheetCellReference end) {
        checkBegin(begin);
        checkEnd(end);

        return new SpreadsheetRange(begin.lower(end), end.upper(begin));
    }

    /**
     * Private ctor
     */
    private SpreadsheetRange(final SpreadsheetCellReference begin, final SpreadsheetCellReference end) {
        this.begin = begin;
        this.end = end;
    }

    /**
     * Returns a {@link SpreadsheetCellReference} that holds the top left cell reference.
     */
    public SpreadsheetCellReference begin() {
        return this.begin;
    }

    private final SpreadsheetCellReference begin;

    /**
     * Returns a {@link SpreadsheetCellReference} that holds the bottom right cell reference.
     */
    public SpreadsheetCellReference end() {
        return this.end;
    }

    private final SpreadsheetCellReference end;


    /**
     * Would be setter that accepts a pair of coordinates, and returns a range with those values,
     * creating a new instance if necessary.
     */
    public SpreadsheetRange setBeginAndEnd(final SpreadsheetCellReference begin,
                                           final SpreadsheetCellReference end) {
        checkBegin(begin);
        checkEnd(end);

        final SpreadsheetCellReference begin0 = begin.lower(end);
        final SpreadsheetCellReference end0 = end.upper(begin);

        return this.begin.equals(begin0) && this.end.equals(end0) ?
                this :
                new SpreadsheetRange(begin0, end0);
    }

    private static void checkBegin(final SpreadsheetCellReference begin) {
        Objects.requireNonNull(begin, "begin");
    }

    private static void checkEnd(final SpreadsheetCellReference end) {
        Objects.requireNonNull(end, "end");
    }

    /**
     * Returns true only if this range covers a single cell.
     */
    public boolean isSingleCell() {
        return this.width() == 1 && this.height() == 1;
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
     * Tests if this range contains the given {@link SpreadsheetCellReference}.
     */
    public boolean contains(final SpreadsheetCellReference reference) {
        Objects.requireNonNull(reference, "reference");

        final SpreadsheetRowReference row = reference.row();
        final SpreadsheetColumnReference column = reference.column();

        final SpreadsheetCellReference begin = this.begin;
        final SpreadsheetCellReference end = this.end;

        return row.compareTo(begin.row()) >= 0 &&
                column.compareTo(begin.column()) >= 0 &&
                row.compareTo(end.row()) <= 0 &&
                column.compareTo(end.column()) <= 0;
    }

    /**
     * A stream that provides all {@link SpreadsheetColumnReference}.
     */
    public Stream<SpreadsheetColumnReference> columnStream() {
        return IntStream.range(this.begin().column().value(), this.end.column().value())
                .mapToObj(i -> SpreadsheetReferenceKind.ABSOLUTE.column(i));
    }

    /**
     * A stream that provides all {@link SpreadsheetRowReference}.
     */
    public Stream<SpreadsheetRowReference> rowStream() {
        return IntStream.range(this.begin().row().value(), this.end.row().value())
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

    // HasJsonNode...........................................................................................

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
        return Objects.hash(this.begin, this.end);
    }

    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetRange &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetRange other) {
        return this.begin.equals(other.begin) &&
                this.end.equals(other.end);
    }

    @Override
    public String toString() {
        return this.isSingleCell() ?
                this.begin.toString() :
                this.begin + SEPARATOR + this.end;
    }

    // Comparable.......................................................................................

    @Override
    public int compareTo(final SpreadsheetRange other) {
        throw new UnsupportedOperationException();
    }
}

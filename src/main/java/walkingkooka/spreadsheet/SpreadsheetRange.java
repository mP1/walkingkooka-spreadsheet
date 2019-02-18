package walkingkooka.spreadsheet;

import walkingkooka.Cast;
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStore;
import walkingkooka.test.HashCodeEqualsDefined;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetReferenceKind;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRowReference;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.json.HasJsonNode;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonStringNode;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Holds a range. Note the begin component is always before the end, with rows being the significant axis before column.
 */
public final class SpreadsheetRange implements ExpressionReference, 
        HashCodeEqualsDefined,
        HasJsonNode {

    /**
     * Factory that parses some text holding a range.
     */
    public static SpreadsheetRange parse(final String text) {
        CharSequences.failIfNullOrEmpty(text, "text");

        final int dash = text.indexOf(SEPARATOR);
        if (-1 == dash) {
            throw new IllegalArgumentException("Missing begin and end separator " + CharSequences.quote(SEPARATOR) + "=" + CharSequences.quote(text));
        }

        if (0 == dash) {
            throw new IllegalArgumentException("Missing begin =" + CharSequences.quote(text));
        }

        if (dash + SEPARATOR.length() == text.length() - 1) {
            throw new IllegalArgumentException("Missing end =" + CharSequences.quote(text));
        }

        return with(parse0(text.substring(0, dash), "begin", text),
                parse0(text.substring(dash + SEPARATOR.length()), "end", text));
    }

    private static SpreadsheetCellReference parse0(final String component,
                                                   final String label,
                                                   final String text) {
        try {
            return SpreadsheetCellReference.parse(component);
        } catch (final IllegalArgumentException cause) {
            throw new IllegalArgumentException("Invalid " + label + " in " + CharSequences.quote(text), cause);
        }
    }

    private final static String SEPARATOR = "..";

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

        return SpreadsheetRange.with(topLeft, bottomRight.add(1, 1));
    }

    /**
     * Factory that creates a {@link SpreadsheetRange}
     */
    public static SpreadsheetRange cell(final SpreadsheetCellReference cell) {
        Objects.requireNonNull(cell, "cell");
        return new SpreadsheetRange(cell, cell.add(1, 1));
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
        return this.end().column().value() - this.begin().column().value();
    }

    /**
     * Returns the height of this range.
     */
    public int height() {
        return this.end().row().value() - this.begin().row().value();
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

        if (!node.isString()) {
            throw new IllegalArgumentException("Node is not an string=" + node);
        }

        return parse(JsonStringNode.class.cast(node).value());
    }

    @Override
    public JsonNode toJsonNode() {
        return JsonNode.string(this.toString());
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
        return this.begin + SEPARATOR + this.end;
    }
}

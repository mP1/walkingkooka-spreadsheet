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
import walkingkooka.compare.Comparators;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.spreadsheet.parser.SpreadsheetCellReferenceParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParsers;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserException;
import walkingkooka.text.cursor.parser.ParserReporters;

import java.util.Objects;
import java.util.Optional;

/**
 * A {@link SpreadsheetExpressionReference reference} that includes a defined name or column and row. Note the
 * {@link #compareTo(SpreadsheetCellReference)} ignores the {@link SpreadsheetReferenceKind} of the column and row.
 */
@SuppressWarnings("lgtm[java/inconsistent-equals-and-hashcode]")
public final class SpreadsheetCellReference extends SpreadsheetCellReferenceOrLabelName
        implements Comparable<SpreadsheetCellReference>,
        HateosResource<String> {

    /**
     * Parsers a range of cell referencs.
     */
    static Range<SpreadsheetCellReference> parseCellReferenceRange0(final String text) {
        return Range.parse(text,
                SpreadsheetParsers.RANGE_SEPARATOR.character(),
                SpreadsheetCellReference::parseCellReference);
    }

    /**
     * Parsers the text expecting a valid {@link SpreadsheetCellReference} or fails.
     */
    static SpreadsheetCellReference parseCellReference0(final String text) {
        try {
            return PARSER.parse(TextCursors.charSequence(text),
                    SpreadsheetReferenceSpreadsheetParserContext.INSTANCE)
                    .get()
                    .cast(SpreadsheetCellReferenceParserToken.class)
                    .cell();
        } catch (final ParserException cause) {
            throw new IllegalArgumentException(cause.getMessage(), cause);
        }
    }

    private static final Parser<SpreadsheetParserContext> PARSER = SpreadsheetParsers.columnAndRow().orReport(ParserReporters.basic());

    /**
     * Factory that creates a {@link SpreadsheetCellReference} with the given column and row.
     */
    static SpreadsheetCellReference with(final SpreadsheetColumnReference column, final SpreadsheetRowReference row) {
        checkColumn(column);
        checkRow(row);

        return new SpreadsheetCellReference(column, row);
    }

    private SpreadsheetCellReference(final SpreadsheetColumnReference column, final SpreadsheetRowReference row) {
        super();
        this.column = column;
        this.row = row;
    }

    /**
     * Adds a delta to the row and column and returns a {@link SpreadsheetCellReference} with the updated values.
     * Row and column values of 0 and 0 will return this.
     */
    public SpreadsheetCellReference add(final int column, final int row) {
        return this.addColumn(column)
                .addRow(row);
    }

    public SpreadsheetRowReference row() {
        return this.row;
    }

    public SpreadsheetCellReference setRow(final SpreadsheetRowReference row) {
        checkRow(row);
        return this.row.equals(row) ?
                this :
                this.replace(this.column, row);
    }

    private final SpreadsheetRowReference row;

    private static void checkRow(final SpreadsheetRowReference row) {
        Objects.requireNonNull(row, "row");
    }

    /**
     * Adds a delta to the row, performing a would be update if the row value is not zero.
     */
    public SpreadsheetCellReference addRow(final int row) {
        return this.setRow(this.row().add(row));
    }

    /**
     * Adds with saturation a delta to the row, performing a would be update if the row value is not zero.
     */
    public SpreadsheetCellReference addRowSaturated(final int row) {
        return this.setRow(this.row().addSaturated(row));
    }

    public SpreadsheetColumnReference column() {
        return this.column;
    }

    public SpreadsheetCellReference setColumn(final SpreadsheetColumnReference column) {
        checkColumn(column);
        return this.column.equals(column) ?
                this :
                this.replace(column, this.row);
    }

    private final SpreadsheetColumnReference column;

    private static void checkColumn(final SpreadsheetColumnReference column) {
        Objects.requireNonNull(column, "column");
    }

    /**
     * Returns this in absolute form, creating a new instance if necessary.
     */
    public SpreadsheetCellReference toAbsolute() {
        return this.toSpreadsheetReferenceKind(SpreadsheetReferenceKind.ABSOLUTE);
    }

    /**
     * Returns a {@link SpreadsheetCellReference} with both the column and row set to {@link SpreadsheetReferenceKind#RELATIVE}.
     */
    public SpreadsheetCellReference toRelative() {
        return this.toSpreadsheetReferenceKind(SpreadsheetReferenceKind.RELATIVE);
    }

    /**
     * Returns a {@link SpreadsheetCellReference} with both the column and row set to {@link SpreadsheetReferenceKind#RELATIVE}.
     */
    private SpreadsheetCellReference toSpreadsheetReferenceKind(final SpreadsheetReferenceKind kind) {
        return this.setColumn(this.column().setReferenceKind(kind))
                .setRow(this.row().setReferenceKind(kind));
    }

    /**
     * Adds a delta to the column, performing a would be update if the column value is not zero.
     */
    public SpreadsheetCellReference addColumn(final int column) {
        return this.setColumn(this.column().add(column));
    }

    /**
     * Adds with saturation a delta to the column, performing a would be update if the column value is not zero
     */
    public SpreadsheetCellReference addColumnSaturated(final int column) {
        return this.setColumn(this.column().addSaturated(column));
    }

    private SpreadsheetCellReference replace(final SpreadsheetColumnReference column, final SpreadsheetRowReference row) {
        return new SpreadsheetCellReference(column, row);
    }

    // Predicate<SpreadsheetCellReference>..............................................................................

    /**
     * Returns true if the other {@link SpreadsheetCellReference} has the same column and row ignoring {@link SpreadsheetReferenceKind}.
     */
    @Override
    public boolean test(final SpreadsheetCellReference reference) {
        Objects.requireNonNull(reference, "reference");

        return this.equalsIgnoreReferenceKind(reference);
    }

    // testRange........................................................................................................

    @Override
    public boolean testRange(final SpreadsheetRange range) {
        Objects.requireNonNull(range, "range");
        return range.test(this);
    }

    // range/spreadsheetRange...........................................................................................

    /**
     * Creates a {@link Range} from the this and the other {@link SpreadsheetCellReference}.
     */
    public Range<SpreadsheetCellReference> range(final SpreadsheetCellReference other) {
        Objects.requireNonNull(other, "other");

        final SpreadsheetColumnReference column = this.column;
        final SpreadsheetColumnReference column2 = other.column;

        SpreadsheetColumnReference left = column.min(column2);
        SpreadsheetColumnReference right = column.max(column2);

        final SpreadsheetRowReference row = this.row;
        final SpreadsheetRowReference row2 = other.row;

        SpreadsheetRowReference top = row.min(row2);
        SpreadsheetRowReference bottom = row.max(row2);

        return Range.greaterThanEquals(left.setRow(top))
                .and(Range.lessThanEquals(right.setRow(bottom)));
    }

    /**
     * Creates a {@link SpreadsheetRange} from the this and the other {@link SpreadsheetCellReference}.
     */
    public SpreadsheetRange spreadsheetRange(final SpreadsheetCellReference other) {
        return SpreadsheetRange.with(this.range(other));
    }

    /**
     * Returns a {@link SpreadsheetRange} holding only this cell.
     */
    public SpreadsheetRange toSpreadsheetRange() {
        return SpreadsheetRange.with(Range.singleton(this));
    }

    // HateosResource...................................................................................................

    @Override
    public String hateosLinkId() {
        return this.column.hateosLinkId() + this.row.hateosLinkId();
    }

    @Override
    public Optional<String> id() {
        return Optional.of(this.hateosLinkId());
    }

    // SpreadsheetSelectionVisitor......................................................................................

    @Override
    void accept(final SpreadsheetSelectionVisitor visitor) {
        visitor.visit(this);
    }

    // SpreadsheetExpressionReferenceVisitor............................................................................

    @Override
    void accept(final SpreadsheetExpressionReferenceVisitor visitor) {
        visitor.visit(this);
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(this.column, this.row);
    }

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof SpreadsheetCellReference;
    }

    @Override
    boolean equals0(final Object other) {
        return this.equals1(Cast.to(other));
    }

    private boolean equals1(final SpreadsheetCellReference other) {
        return this.column.equals(other.column) &&
                this.row.equals(other.row);
    }

    @Override
    public String toString() {
        return "" + this.column + this.row;
    }

    // Comparable ......................................................................................................

    @Override
    public int compareTo(final SpreadsheetCellReference other) {
        final int result = this.column.value - other.column.value;
        return Comparators.EQUAL != result ?
                result :
                this.row.value - other.row.value;
    }

    // equalsIgnoreReferenceKind........................................................................................

    /**
     * Returns true if the other {@link SpreadsheetCellReference} is equal ignoring {@link SpreadsheetReferenceKind}.
     */
    @Override
    boolean equalsIgnoreReferenceKind0(final Object other) {
        return this.equalsIgnoreReferenceKind1((SpreadsheetCellReference) other);
    }

    private boolean equalsIgnoreReferenceKind1(final SpreadsheetCellReference other) {
        return this.column.equalsIgnoreReferenceKind(other.column) &&
                this.row.equalsIgnoreReferenceKind(other.row);
    }
}

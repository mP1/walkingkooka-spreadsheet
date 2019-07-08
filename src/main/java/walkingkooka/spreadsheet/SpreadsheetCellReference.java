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
import walkingkooka.compare.Comparators;
import walkingkooka.compare.LowerOrUpper;
import walkingkooka.compare.Range;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.net.http.server.hateos.HasHateosLinkId;
import walkingkooka.spreadsheet.parser.SpreadsheetCellReferenceParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.spreadsheet.parser.SpreadsheetParsers;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserException;
import walkingkooka.text.cursor.parser.ParserReporters;

import java.math.MathContext;
import java.util.Objects;

/**
 * A reference that includes a defined name or column and row.
 */
public final class SpreadsheetCellReference extends SpreadsheetExpressionReference
        implements Comparable<SpreadsheetCellReference>,
        LowerOrUpper<SpreadsheetCellReference> {

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
            final SpreadsheetCellReferenceParserToken token = PARSER.parse(TextCursors.charSequence(text),
                    SpreadsheetParserContexts.basic(DecimalNumberContexts.american(MathContext.DECIMAL32)))
                    .get().cast();
            return token.cell();
        } catch (final ParserException cause) {
            throw new IllegalArgumentException(cause.getMessage(), cause);
        }
    }

    private static final Parser<ParserContext> PARSER = SpreadsheetParsers.columnAndRow().orReport(ParserReporters.basic());

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
     * Adds a delta to the column, performing a would be update if the column value is not zero.
     */
    public SpreadsheetCellReference addColumn(final int column) {
        return this.setColumn(this.column().add(column));
    }

    private SpreadsheetCellReference replace(final SpreadsheetColumnReference column, final SpreadsheetRowReference row) {
        return new SpreadsheetCellReference(column, row);
    }

    /**
     * Returns the lower (Comparison wise) of two {@link SpreadsheetCellReference}
     */
    public SpreadsheetCellReference lower(final SpreadsheetCellReference other) {
        checkOther(other);

        return this.setColumn(this.column.lower(other.column))
                .setRow(this.row.lower(other.row));
    }

    /**
     * Returns the upper (Comparison wise) of two {@link SpreadsheetCellReference}
     */
    public SpreadsheetCellReference upper(final SpreadsheetCellReference other) {
        checkOther(other);

        return this.setColumn(this.column.upper(other.column))
                .setRow(this.row.upper(other.row));
    }

    private static void checkOther(final SpreadsheetCellReference other) {
        Objects.requireNonNull(other, "other");
    }

    // range/spreadsheetRange...........................................................................................

    /**
     * Creates a {@link Range} from the this and the other {@link SpreadsheetCellReference}.
     */
    public Range<SpreadsheetCellReference> range(final SpreadsheetCellReference other) {
        Objects.requireNonNull(other, "other");

        final SpreadsheetCellReference begin = this.lower(other);
        final SpreadsheetCellReference end = this.upper(other);

        return Range.greaterThanEquals(begin)
                .and(Range.lessThanEquals(end));
    }

    /**
     * Creates a {@link SpreadsheetRange} from the this and the other {@link SpreadsheetCellReference}.
     */
    public SpreadsheetRange spreadsheetRange(final SpreadsheetCellReference other) {
        return SpreadsheetRange.with(this.range(other));
    }

    // is...............................................................................................................

    @Override
    public boolean isCellReference() {
        return true;
    }

    @Override
    public boolean isLabelName() {
        return false;
    }

    @Override
    public boolean isRange() {
        return false;
    }

    // HasHateosLink....................................................................................................

    @Override
    public String hateosLinkId() {
        return this.column.hateosLinkId() + this.row.hateosLinkId();
    }

    // SpreadsheetExpressionReferenceVisitor............................................................................

    @Override
    void accept(final SpreadsheetExpressionReferenceVisitor visitor) {
        visitor.visit(this);
    }

    // HashCodeEqualsDefined............................................................................................

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

    // Comparable ..................................................................................................

    @Override
    public int compareTo(final SpreadsheetCellReference other) {
        // reverse sign because #compare0 does compare in reverse because of double dispatch.
        return -this.compare0(other);
    }

    // SpreadsheetExpressionReferenceComparator........................................................................

    @Override
    final int compare(final SpreadsheetExpressionReference other) {
        return other.compare0(this);
    }

    @Override
    final int compare0(final SpreadsheetCellReference other) {
        final int result = other.column.value - this.column.value;
        return Comparators.EQUAL != result ?
                result :
                other.row.value - this.row.value;
    }

    @Override
    final int compare0(final SpreadsheetLabelName other) {
        return LABEL_COMPARED_WITH_CELL_RESULT;
    }
}

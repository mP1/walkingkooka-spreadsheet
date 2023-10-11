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

import walkingkooka.InvalidCharacterException;
import walkingkooka.collect.Range;
import walkingkooka.compare.Comparators;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetViewportRectangle;
import walkingkooka.spreadsheet.parser.SpreadsheetCellReferenceParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.spreadsheet.parser.SpreadsheetParsers;
import walkingkooka.text.cursor.MaxPositionTextCursor;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserException;
import walkingkooka.text.cursor.parser.ParserToken;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A {@link SpreadsheetExpressionReference reference} that includes a defined name or column and row. Note the
 * {@link #compareTo(SpreadsheetCellReference)} ignores the {@link SpreadsheetReferenceKind} of the column and row.
 */
@SuppressWarnings("lgtm[java/inconsistent-equals-and-hashcode]")
public final class SpreadsheetCellReference extends SpreadsheetCellReferenceOrRange
        implements Comparable<SpreadsheetCellReference>,
        HateosResource<String> {

    /**
     * Parsers the text expecting a valid {@link SpreadsheetCellReference} or fails.
     */
    static SpreadsheetCellReference parseCell0(final String text) {
        try {
            final MaxPositionTextCursor textCursor = TextCursors.maxPosition(
                    TextCursors.charSequence(text)
            );
            final Optional<ParserToken> token = PARSER.parse(
                    textCursor,
                    SpreadsheetParserContexts.fake()
            );
            if (false == token.isPresent() || false == textCursor.isEmpty()) {
                throw new InvalidCharacterException(
                        text,
                        textCursor.max()
                );
            }
            return token.get()
                    .cast(SpreadsheetCellReferenceParserToken.class)
                    .cell();
        } catch (final ParserException cause) {
            throw new IllegalArgumentException(cause.getMessage(), cause);
        }
    }

    // Used by SpreadsheetSelection
    static final Parser<SpreadsheetParserContext> PARSER = SpreadsheetParsers.cell();

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
        return this.setSpreadsheetReferenceKind(SpreadsheetReferenceKind.ABSOLUTE);
    }

    /**
     * Returns a {@link SpreadsheetCellReference} with both the column and row set to {@link SpreadsheetReferenceKind#RELATIVE}.
     */
    public SpreadsheetCellReference toRelative() {
        return this.setSpreadsheetReferenceKind(SpreadsheetReferenceKind.RELATIVE);
    }

    /**
     * Returns a {@link SpreadsheetCellReference} with both the column and row set to {@link SpreadsheetReferenceKind#RELATIVE}.
     */
    private SpreadsheetCellReference setSpreadsheetReferenceKind(final SpreadsheetReferenceKind kind) {
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

    @Override
    public long count() {
        return 1;
    }

    @Override
    public boolean isAll() {
        return false;
    }

    @Override
    public boolean isFirst() {
        return this.column().isFirst() && this.row().isFirst();
    }

    @Override
    public boolean isLast() {
        return this.column().isLast() && this.row().isLast();
    }

    @Override
    public SpreadsheetCellReference toCell() {
        return this;
    }

    @Override
    public SpreadsheetColumnReference toColumn() {
        return this.column();
    }

    @Override
    public SpreadsheetColumnReferenceRange toColumnRange() {
        return this.toColumn().toColumnRange();
    }

    @Override
    public SpreadsheetRowReference toRow() {
        return this.row();
    }

    @Override
    public SpreadsheetRowReferenceRange toRowRange() {
        return this.row().toRowRange();
    }

    @Override
    public SpreadsheetSelection simplify() {
        return this;
    }

    @Override
    Set<SpreadsheetViewportAnchor> anchors() {
        return ANCHORS;
    }

    private final static Set<SpreadsheetViewportAnchor> ANCHORS = EnumSet.of(SpreadsheetViewportAnchor.NONE);

    // SpreadsheetViewportRectangle.....................................................................................

    /**
     * Creates a {@link SpreadsheetViewportRectangle} using this as the top/left.
     */
    public SpreadsheetViewportRectangle viewportRectangle(final double width,
                                                          final double height) {
        return SpreadsheetViewportRectangle.with(this, width, height);
    }

    // setFormula.......................................................................................................

    /**
     * Factory that creates a {@link SpreadsheetCell} using this {@link SpreadsheetCellReference} and the given
     * {@link SpreadsheetFormula}.
     */
    public SpreadsheetCell setFormula(final SpreadsheetFormula formula) {
        return SpreadsheetCell.with(
                this,
                formula
        );
    }

    // test............................................................................................................

    /**
     * Returns true if the other {@link SpreadsheetCellReference} has the same column and row ignoring {@link SpreadsheetReferenceKind}.
     */
    @Override
    boolean testCell0(final SpreadsheetCellReference cell) {
        return this.equalsIgnoreReferenceKind(cell);
    }

    @Override
    boolean testCellRange0(final SpreadsheetCellRange range) {
        return range.testCell0(this);
    }

    @Override
    boolean testColumn0(final SpreadsheetColumnReference column) {
        return this.column().equalsIgnoreReferenceKind(column);
    }

    @Override
    boolean testRow0(final SpreadsheetRowReference row) {
        return this.row().equalsIgnoreReferenceKind(row);
    }

    // range/cellRange.......................................................................................

    /**
     * Creates a {@link Range} parse the this and the other {@link SpreadsheetCellReference}.
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

        return createRange(
                left.setRow(top),
                right.setRow(bottom)
        );
    }

    /**
     * Creates a {@link SpreadsheetCellRange} parse the this and the other {@link SpreadsheetCellReference}.
     */
    public SpreadsheetCellRange cellRange(final SpreadsheetCellReference other) {
        return SpreadsheetCellRange.with(this.range(other));
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

    // SpreadsheetViewportNavigation...........................................................................

    @Override
    public SpreadsheetViewportAnchor defaultAnchor() {
        return SpreadsheetViewportAnchor.CELL;
    }

    /**
     * A {@link SpreadsheetCellReference} is hidden if either its column or row is hidden.
     */
    @Override
    public boolean isHidden(final Predicate<SpreadsheetColumnReference> hiddenColumnTester,
                            final Predicate<SpreadsheetRowReference> hiddenRowTester) {
        return this.column().isHidden(hiddenColumnTester, hiddenRowTester) ||
                this.row().isHidden(hiddenColumnTester, hiddenRowTester);
    }

    @Override
    Optional<SpreadsheetSelection> leftColumn(final SpreadsheetViewportAnchor anchor,
                                              final SpreadsheetViewportNavigationContext context) {
        return this.leftOrRightColumn(
                context,
                context::leftColumn
        );
    }

    @Override
    Optional<SpreadsheetSelection> rightColumn(final SpreadsheetViewportAnchor anchor,
                                               final SpreadsheetViewportNavigationContext context) {
        return this.leftOrRightColumn(
                context,
                context::rightColumn
        );
    }

    private Optional<SpreadsheetSelection> leftOrRightColumn(final SpreadsheetViewportNavigationContext context,
                                                             final Function<SpreadsheetColumnReference, Optional<SpreadsheetColumnReference>> leftOrRight) {
        final SpreadsheetRowReference row = this.row();

        return context.isRowHidden(row) ?
                Optional.empty() :
                leftOrRight.apply(this.column())
                        .map(c -> c.setRow(row));
    }

    @Override
    Optional<SpreadsheetSelection> leftPixels(final SpreadsheetViewportAnchor anchor,
                                              final int count,
                                              final SpreadsheetViewportNavigationContext context) {
        return this.leftOrRightPixels(
                context,
                count,
                context::leftPixels
        );
    }

    @Override
    Optional<SpreadsheetSelection> rightPixels(final SpreadsheetViewportAnchor anchor,
                                               final int count,
                                               final SpreadsheetViewportNavigationContext context) {
        return this.leftOrRightPixels(
                context,
                count,
                context::rightPixels
        );
    }

    private Optional<SpreadsheetSelection> leftOrRightPixels(final SpreadsheetViewportNavigationContext context,
                                                             final int count,
                                                             final BiFunction<SpreadsheetColumnReference, Integer, Optional<SpreadsheetColumnReference>> leftOrRight) {
        final SpreadsheetRowReference row = this.row();

        return context.isRowHidden(row) ?
                Optional.empty() :
                leftOrRight.apply(this.column(), count)
                        .map(c -> c.setRow(row));
    }

    @Override
    Optional<SpreadsheetSelection> upRow(final SpreadsheetViewportAnchor anchor,
                                         final SpreadsheetViewportNavigationContext context) {
        return this.upOrDownRow(
                context,
                context::upRow
        );
    }

    @Override
    Optional<SpreadsheetSelection> downRow(final SpreadsheetViewportAnchor anchor,
                                           final SpreadsheetViewportNavigationContext context) {
        return this.upOrDownRow(
                context,
                context::downRow
        );
    }

    private Optional<SpreadsheetSelection> upOrDownRow(final SpreadsheetViewportNavigationContext context,
                                                       final Function<SpreadsheetRowReference, Optional<SpreadsheetRowReference>> upOrDown) {
        final SpreadsheetColumnReference column = this.column();

        return context.isColumnHidden(column) ?
                Optional.empty() :
                upOrDown.apply(this.row())
                        .map(c -> c.setColumn(column));
    }

    @Override
    Optional<SpreadsheetSelection> upPixels(final SpreadsheetViewportAnchor anchor,
                                            final int count,
                                            final SpreadsheetViewportNavigationContext context) {
        return this.upOrDownPixels(
                context,
                count,
                context::upPixels
        );
    }

    @Override
    Optional<SpreadsheetSelection> downPixels(final SpreadsheetViewportAnchor anchor,
                                              final int count,
                                              final SpreadsheetViewportNavigationContext context) {
        return this.upOrDownPixels(
                context,
                count,
                context::downPixels
        );
    }

    private Optional<SpreadsheetSelection> upOrDownPixels(final SpreadsheetViewportNavigationContext context,
                                                          final int count,
                                                          final BiFunction<SpreadsheetRowReference, Integer, Optional<SpreadsheetRowReference>> upOrDown) {
        final SpreadsheetColumnReference column = this.column();

        return context.isColumnHidden(column) ?
                Optional.empty() :
                upOrDown.apply(this.row(), count)
                        .map(c -> c.setColumn(column));
    }

    @Override
    Optional<SpreadsheetViewport> extendLeftColumn(final SpreadsheetViewportAnchor anchor,
                                                   final SpreadsheetViewportNavigationContext context) {
        return this.extendRange(
                this.leftColumn(
                        anchor,
                        context
                ),
                anchor
        ).map(this::setAnchorOrDefaultBottomRight);
    }

    @Override
    Optional<SpreadsheetViewport> extendLeftPixels(final SpreadsheetViewportAnchor anchor,
                                                   final int count,
                                                   final SpreadsheetViewportNavigationContext context) {
        return this.extendRange(
                this.leftPixels(
                        anchor,
                        count,
                        context
                ),
                anchor
        ).map(this::setAnchorOrDefaultBottomRight);
    }

    @Override
    Optional<SpreadsheetViewport> extendUpRow(final SpreadsheetViewportAnchor anchor,
                                              final SpreadsheetViewportNavigationContext context) {
        return this.extendRange(
                this.upRow(
                        anchor,
                        context
                ),
                anchor
        ).map(this::setAnchorOrDefaultBottomRight);
    }

    @Override
    Optional<SpreadsheetViewport> extendUpPixels(final SpreadsheetViewportAnchor anchor,
                                                 final int count,
                                                 final SpreadsheetViewportNavigationContext context) {
        return this.extendRange(
                this.upPixels(
                        anchor,
                        count,
                        context
                ),
                anchor
        ).map(this::setAnchorOrDefaultBottomRight);
    }

    private SpreadsheetViewport setAnchorOrDefaultBottomRight(final SpreadsheetSelection selection) {
        return selection.setAnchorOrDefault(SpreadsheetViewportAnchor.BOTTOM_RIGHT);
    }

    @Override
    Optional<SpreadsheetViewport> extendRightColumn(final SpreadsheetViewportAnchor anchor,
                                                    final SpreadsheetViewportNavigationContext context) {
        return this.extendRange(
                this.rightColumn(
                        anchor,
                        context
                ),
                anchor
        ).map(this::setAnchorOrDefaultTopLeft);
    }

    @Override
    Optional<SpreadsheetViewport> extendRightPixels(final SpreadsheetViewportAnchor anchor,
                                                    final int count,
                                                    final SpreadsheetViewportNavigationContext context) {
        return this.extendRange(
                this.rightPixels(
                        anchor,
                        count,
                        context
                ),
                anchor
        ).map(this::setAnchorOrDefaultTopLeft);
    }

    @Override
    Optional<SpreadsheetViewport> extendDownRow(final SpreadsheetViewportAnchor anchor,
                                                final SpreadsheetViewportNavigationContext context) {
        return this.extendRange(
                this.downRow(
                        anchor,
                        context
                ),
                anchor
        ).map(this::setAnchorOrDefaultTopLeft);
    }

    @Override
    Optional<SpreadsheetViewport> extendDownPixels(final SpreadsheetViewportAnchor anchor,
                                                   final int count,
                                                   final SpreadsheetViewportNavigationContext context) {
        return this.extendRange(
                this.downPixels(
                        anchor,
                        count,
                        context
                ),
                anchor
        ).map(this::setAnchorOrDefaultTopLeft);
    }

    private SpreadsheetViewport setAnchorOrDefaultTopLeft(final SpreadsheetSelection selection) {
        return selection.setAnchorOrDefault(SpreadsheetViewportAnchor.TOP_LEFT);
    }

    @Override
    Optional<SpreadsheetSelection> extendRange(final Optional<? extends SpreadsheetSelection> other,
                                               final SpreadsheetViewportAnchor anchor) {
        return other.map(
                o -> this.cellRange((SpreadsheetCellReference) o)
                        .simplify()
        );
    }

    // focused...........................................................................................................

    @Override
    public SpreadsheetCellReference focused(final SpreadsheetViewportAnchor anchor) {
        this.checkAnchor(anchor);
        return this;
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
    boolean equals0(final Object other,
                    final boolean includeKind) {
        return this.equals1(
                (SpreadsheetCellReference) other,
                includeKind
        );
    }

    private boolean equals1(final SpreadsheetCellReference other,
                            final boolean includeKind) {
        return this.column.equals1(other.column, includeKind) &&
                this.row.equals1(other.row, includeKind);
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
}

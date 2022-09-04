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

import walkingkooka.collect.Range;
import walkingkooka.compare.Comparators;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetViewport;
import walkingkooka.spreadsheet.parser.SpreadsheetCellReferenceParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParsers;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStore;
import walkingkooka.spreadsheet.store.SpreadsheetRowStore;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserException;
import walkingkooka.text.cursor.parser.ParserReporters;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
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
     * A {@link SpreadsheetCellReference} with A1.
     */
    public final static SpreadsheetCellReference A1 = SpreadsheetReferenceKind.RELATIVE.firstColumn()
            .setRow(
                    SpreadsheetReferenceKind.RELATIVE.firstRow()
            );

    /**
     * Parsers the text expecting a valid {@link SpreadsheetCellReference} or fails.
     */
    static SpreadsheetCellReference parseCell0(final String text) {
        try {
            return PARSER.parse(TextCursors.charSequence(text),
                            SpreadsheetCellReferenceSpreadsheetParserContext.INSTANCE)
                    .get()
                    .cast(SpreadsheetCellReferenceParserToken.class)
                    .cell();
        } catch (final ParserException cause) {
            throw new IllegalArgumentException(cause.getMessage(), cause);
        }
    }

    // Used by SpreadsheetSelection
    static final Parser<SpreadsheetParserContext> PARSER = SpreadsheetParsers.columnAndRow()
            .orFailIfCursorNotEmpty(ParserReporters.basic())
            .orReport(ParserReporters.basic());

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
    public int count() {
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
    public SpreadsheetCellReference toCellOrFail() {
        return this;
    }

    @Override
    public SpreadsheetSelection simplify() {
        return this;
    }

    @Override
    Set<SpreadsheetViewportSelectionAnchor> anchors() {
        return ANCHORS;
    }

    private final Set<SpreadsheetViewportSelectionAnchor> ANCHORS = EnumSet.of(SpreadsheetViewportSelectionAnchor.NONE);

    // SpreadsheetViewport..............................................................................................

    /**
     * Creates a {@link SpreadsheetViewport} using this as the top/left.
     */
    public SpreadsheetViewport viewport(final double width,
                                        final double height) {
        return SpreadsheetViewport.with(this, width, height);
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

    // Predicate<SpreadsheetCellReference>..............................................................................

    /**
     * Returns true if the other {@link SpreadsheetCellReference} has the same column and row ignoring {@link SpreadsheetReferenceKind}.
     */
    @Override
    public boolean test(final SpreadsheetCellReference reference) {
        checkCellReference(reference);

        return this.equalsIgnoreReferenceKind(reference);
    }

    // testCellRange.....................................................................................................

    @Override
    public boolean testCellRange(final SpreadsheetCellRange range) {
        checkCellRange(range);
        return range.test(this);
    }

    @Override
    public boolean testColumn(final SpreadsheetColumnReference column) {
        return this.column().equalsIgnoreReferenceKind(column);
    }

    @Override
    public boolean testRow(final SpreadsheetRowReference row) {
        return this.row().equalsIgnoreReferenceKind(row);
    }

    // range/cellRange.......................................................................................

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

        return createRange(
                left.setRow(top),
                right.setRow(bottom)
        );
    }

    /**
     * Creates a {@link SpreadsheetCellRange} from the this and the other {@link SpreadsheetCellReference}.
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

    // TreePrintable....................................................................................................

    @Override
    public String selectionTypeName() {
        return "cell";
    }

    // SpreadsheetViewportSelectionNavigation...........................................................................

    @Override
    public SpreadsheetViewportSelectionAnchor defaultAnchor() {
        return SpreadsheetViewportSelectionAnchor.CELL;
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
    Optional<SpreadsheetSelection> left(final SpreadsheetViewportSelectionAnchor anchor,
                                        final SpreadsheetColumnStore columnStore,
                                        final SpreadsheetRowStore rowStore) {
        return this.leftOrRight(
                columnStore,
                rowStore,
                columnStore::leftSkipHidden
        );
    }

    @Override
    Optional<SpreadsheetSelection> right(final SpreadsheetViewportSelectionAnchor anchor,
                                         final SpreadsheetColumnStore columnStore,
                                         final SpreadsheetRowStore rowStore) {
        return this.leftOrRight(
                columnStore,
                rowStore,
                columnStore::rightSkipHidden
        );
    }

    private Optional<SpreadsheetSelection> leftOrRight(final SpreadsheetColumnStore columnStore,
                                                       final SpreadsheetRowStore rowStore,
                                                       final Function<SpreadsheetColumnReference, Optional<SpreadsheetColumnReference>> leftOrRight) {
        final SpreadsheetRowReference row = this.row();

        return row.isHidden(columnStore, rowStore) ?
                Optional.empty() :
                leftOrRight.apply(this.column())
                        .map(c -> c.setRow(row));
    }

    @Override
    Optional<SpreadsheetSelection> up(final SpreadsheetViewportSelectionAnchor anchor,
                                      final SpreadsheetColumnStore columnStore,
                                      final SpreadsheetRowStore rowStore) {
        return this.upOrDown(
                columnStore,
                rowStore,
                rowStore::upSkipHidden
        );
    }

    @Override
    Optional<SpreadsheetSelection> down(final SpreadsheetViewportSelectionAnchor anchor,
                                        final SpreadsheetColumnStore columnStore,
                                        final SpreadsheetRowStore rowStore) {
        return this.upOrDown(
                columnStore,
                rowStore,
                rowStore::downSkipHidden
        );
    }

    private Optional<SpreadsheetSelection> upOrDown(final SpreadsheetColumnStore columnStore,
                                                    final SpreadsheetRowStore rowStore,
                                                    final Function<SpreadsheetRowReference, Optional<SpreadsheetRowReference>> upOrDown) {
        final SpreadsheetColumnReference column = this.column();

        return column.isHidden(columnStore, rowStore) ?
                Optional.empty() :
                upOrDown.apply(this.row())
                        .map(c -> c.setColumn(column));
    }

    @Override
    Optional<SpreadsheetViewportSelection> extendLeft(final SpreadsheetViewportSelectionAnchor anchor,
                                                      final SpreadsheetColumnStore columnStore,
                                                      final SpreadsheetRowStore rowStore) {
        return this.extendRange(
                this.left(anchor, columnStore, rowStore),
                anchor
        ).map(this::setAnchorOrDefaultBottomRight);
    }

    @Override
    Optional<SpreadsheetViewportSelection> extendUp(final SpreadsheetViewportSelectionAnchor anchor,
                                                    final SpreadsheetColumnStore columnStore,
                                                    final SpreadsheetRowStore rowStore) {
        return this.extendRange(
                this.up(anchor, columnStore, rowStore),
                anchor
        ).map(this::setAnchorOrDefaultBottomRight);
    }

    private SpreadsheetViewportSelection setAnchorOrDefaultBottomRight(final SpreadsheetSelection selection) {
        return selection.setAnchorOrDefault(SpreadsheetViewportSelectionAnchor.BOTTOM_RIGHT);
    }

    @Override
    Optional<SpreadsheetViewportSelection> extendRight(final SpreadsheetViewportSelectionAnchor anchor,
                                                       final SpreadsheetColumnStore columnStore,
                                                       final SpreadsheetRowStore rowStore) {
        return this.extendRange(
                this.right(anchor, columnStore, rowStore),
                anchor
        ).map(this::setAnchorOrDefaultTopLeft);
    }

    @Override
    Optional<SpreadsheetViewportSelection> extendDown(final SpreadsheetViewportSelectionAnchor anchor,
                                                      final SpreadsheetColumnStore columnStore,
                                                      final SpreadsheetRowStore rowStore) {
        return this.extendRange(
                this.down(anchor, columnStore, rowStore),
                anchor
        ).map(this::setAnchorOrDefaultTopLeft);
    }

    private SpreadsheetViewportSelection setAnchorOrDefaultTopLeft(final SpreadsheetSelection selection) {
        return selection.setAnchorOrDefault(SpreadsheetViewportSelectionAnchor.TOP_LEFT);
    }

    @Override
    Optional<SpreadsheetSelection> extendRange(final Optional<? extends SpreadsheetSelection> other,
                                               final SpreadsheetViewportSelectionAnchor anchor) {
        return other.map(
                o -> this.cellRange((SpreadsheetCellReference) o)
                        .simplify()
        );
    }

    // focused...........................................................................................................

    @Override
    public SpreadsheetCellReference focused(final SpreadsheetViewportSelectionAnchor anchor) {
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

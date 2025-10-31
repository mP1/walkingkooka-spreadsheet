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
import walkingkooka.collect.list.Lists;
import walkingkooka.compare.Comparators;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.formula.parser.CellSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.viewport.AnchoredSpreadsheetSelection;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewportAnchor;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewportNavigationContext;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewportRectangle;
import walkingkooka.text.cursor.parser.ParserToken;

import java.util.Comparator;
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
    CanReplaceReferences<SpreadsheetCellReference>,
    HateosResource<String> {

    /**
     * {@see SpreadsheetCellReferenceComparator}.
     */
    public static Comparator<SpreadsheetCell> cellComparator(final Comparator<SpreadsheetCellReference> comparator) {
        return SpreadsheetCellReferenceComparator.with(comparator);
    }

    /**
     * Factory that creates a {@link SpreadsheetCellReference} with the given column and row.
     */
    static SpreadsheetCellReference with(final SpreadsheetColumnReference column, final SpreadsheetRowReference row) {
        return new SpreadsheetCellReference(
            checkColumn(column),
            checkRow(row)
        );
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
    @Override
    public SpreadsheetCellReference add(final int column, final int row) {
        return this.addColumn(column)
            .addRow(row);
    }

    @Override
    public SpreadsheetCellReference addSaturated(final int column, final int row) {
        return this.addColumnSaturated(column)
            .addRowSaturated(row);
    }

    // row..............................................................................................................

    public SpreadsheetRowReference row() {
        return this.row;
    }

    public SpreadsheetCellReference setRow(final SpreadsheetRowReference row) {
        return this.row.equals(row) ?
            this :
            this.replace(
                this.column,
                checkRow(row)
            );
    }

    final SpreadsheetRowReference row;

    private static SpreadsheetRowReference checkRow(final SpreadsheetRowReference row) {
        return Objects.requireNonNull(row, "row");
    }

    /**
     * Adds a delta to the row, performing a would be updated if the row value is not zero.
     */
    public SpreadsheetCellReference addRow(final int row) {
        return this.setRow(this.row().add(row));
    }

    /**
     * Adds with saturation a delta to the row, performing a would be updated if the row value is not zero.
     */
    public SpreadsheetCellReference addRowSaturated(final int row) {
        return this.setRow(
            this.row()
                .addSaturated(row)
        );
    }

    /**
     * Adds the given deltas to the relative tokens of this {@link SpreadsheetCellReference}.
     */
    @Override
    public SpreadsheetCellReference addIfRelative(final int columnDelta,
                                                  final int rowDelta) {
        return this.setColumn(
            this.column()
                .addIfRelative(columnDelta)
        ).setRow(
            this.row()
                .addIfRelative(rowDelta)
        );
    }

    public SpreadsheetColumnReference column() {
        return this.column;
    }

    public SpreadsheetCellReference setColumn(final SpreadsheetColumnReference column) {
        return this.column.equals(column) ?
            this :
            this.replace(
                checkColumn(column),
                this.row
            );
    }

    final SpreadsheetColumnReference column;

    private static SpreadsheetColumnReference checkColumn(final SpreadsheetColumnReference column) {
        return Objects.requireNonNull(column, "column");
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
    @Override
    public SpreadsheetCellReference toRelative() {
        return this.setSpreadsheetReferenceKind(SpreadsheetReferenceKind.RELATIVE);
    }

    /**
     * Returns a {@link SpreadsheetCellReference} with both the column and row set to {@link SpreadsheetReferenceKind#RELATIVE}.
     */
    private SpreadsheetCellReference setSpreadsheetReferenceKind(final SpreadsheetReferenceKind kind) {
        return this.setColumn(
            this.column()
                .setReferenceKind(kind)
        ).setRow(
            this.row()
                .setReferenceKind(kind)
        );
    }

    /**
     * Adds a delta to the column, performing a would be updated if the column value is not zero.
     */
    public SpreadsheetCellReference addColumn(final int column) {
        return this.setColumn(
            this.column()
                .add(column)
        );
    }

    /**
     * Adds with saturation a delta to the column, performing a would be updated if the column value is not zero
     */
    public SpreadsheetCellReference addColumnSaturated(final int column) {
        return this.setColumn(
            this.column()
                .addSaturated(column)
        );
    }

    private SpreadsheetCellReference replace(final SpreadsheetColumnReference column,
                                             final SpreadsheetRowReference row) {
        return new SpreadsheetCellReference(
            column,
            row
        );
    }

    @Override
    public long count() {
        return 1;
    }

    @Override
    public boolean isFirst() {
        return this.column()
            .isFirst() &&
            this.row()
                .isFirst();
    }

    @Override
    public boolean isLast() {
        return this.column()
            .isLast() &&
            this.row().isLast();
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
    public SpreadsheetColumnRangeReference toColumnRange() {
        return this.toColumn()
            .toColumnRange();
    }

    @Override
    public SpreadsheetRowReference toRow() {
        return this.row();
    }

    @Override
    public SpreadsheetRowRangeReference toRowRange() {
        return this.row()
            .toRowRange();
    }

    @Override
    public SpreadsheetSelection toScalar() {
        return this;
    }

    // toRange..........................................................................................................

    @Override
    public SpreadsheetCellRangeReference toRange() {
        return this.toCellRange();
    }

    // anchors..........................................................................................................

    @Override
    public Set<SpreadsheetViewportAnchor> anchors() {
        return NONE_ANCHORS;
    }

    // replaceReferencesMapper..........................................................................................

    @Override
    Optional<Function<SpreadsheetCellReference, Optional<SpreadsheetCellReference>>> replaceReferencesMapper0(final SpreadsheetSelection moveTo) {
        final SpreadsheetSelection moveToScalar = moveTo.toScalar();

        int deltaX = 0;
        if (moveToScalar.isColumn() || moveToScalar.isCell()) {
            deltaX = moveTo.toColumn().value() - this.column().value();
        }

        int deltaY = 0;
        if (moveToScalar.isRow() || moveToScalar.isCell()) {
            deltaY = moveTo.toRow().value() - this.row().value();
        }

        return Optional.ofNullable(
            0 != deltaX || 0 != deltaY ?
                SpreadsheetSelectionReplaceReferencesMapperFunction.with(
                    deltaX,
                    deltaY
                ) :
                null
        );
    }

    // CanReplaceReferences.............................................................................................

    @Override
    public SpreadsheetCellReference replaceReferences(final Function<SpreadsheetCellReference, Optional<SpreadsheetCellReference>> mapper) {
        Objects.requireNonNull(mapper, "mapper");

        final SpreadsheetCellReference replaced = mapper.apply(this)
            .orElseThrow(() -> new IllegalArgumentException("Mapper must return a cell"));
        return this.equals(replaced) ?
            this :
            replaced;
    }

    // SpreadsheetViewportRectangle.....................................................................................

    /**
     * Creates a {@link SpreadsheetViewportRectangle} using this as the top/left.
     */
    public SpreadsheetViewportRectangle viewportRectangle(final double width,
                                                          final double height) {
        return SpreadsheetViewportRectangle.with(
            this,
            width,
            height
        );
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
    boolean testCellNonNull(final SpreadsheetCellReference cell) {
        return this.equalsIgnoreReferenceKind(cell);
    }

    @Override
    boolean testCellRangeNonNull(final SpreadsheetCellRangeReference range) {
        return range.testCellNonNull(this);
    }

    @Override
    boolean testColumnNonNull(final SpreadsheetColumnReference column) {
        return this.column()
            .equalsIgnoreReferenceKind(column);
    }

    @Override
    boolean testRowNonNull(final SpreadsheetRowReference row) {
        return this.row()
            .equalsIgnoreReferenceKind(row);
    }

    // range/cellRange.......................................................................................

    /**
     * Creates a {@link Range} using the given {@link SpreadsheetCellReference}.
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
     * Creates a {@link SpreadsheetCellRangeReference} with the given {@link SpreadsheetCellReference}.
     */
    public SpreadsheetCellRangeReference cellRange(final SpreadsheetCellReference other) {
        return SpreadsheetCellRangeReference.with(
            this.range(other)
        );
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

    // SpreadsheetViewportNavigation....................................................................................

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
        return this.column()
            .isHidden(
                hiddenColumnTester,
                hiddenRowTester
            ) ||
            this.row()
                .isHidden(
                    hiddenColumnTester,
                    hiddenRowTester
                );
    }

    @Override
    public Optional<SpreadsheetSelection> moveLeftColumn(final SpreadsheetViewportAnchor anchor,
                                                         final SpreadsheetViewportNavigationContext context) {
        return this.leftOrRightColumn(
            context,
            context::moveLeft
        );
    }

    @Override
    public Optional<SpreadsheetSelection> moveRightColumn(final SpreadsheetViewportAnchor anchor,
                                                          final SpreadsheetViewportNavigationContext context) {
        return this.leftOrRightColumn(
            context,
            context::moveRightColumn
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
    public Optional<SpreadsheetSelection> moveLeftPixels(final SpreadsheetViewportAnchor anchor,
                                                         final int count,
                                                         final SpreadsheetViewportNavigationContext context) {
        return this.leftOrRightPixels(
            context,
            count,
            context::leftPixels
        );
    }

    @Override
    public Optional<SpreadsheetSelection> moveRightPixels(final SpreadsheetViewportAnchor anchor,
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
    public Optional<SpreadsheetSelection> moveUpRow(final SpreadsheetViewportAnchor anchor,
                                                    final SpreadsheetViewportNavigationContext context) {
        return this.upOrDownRow(
            context,
            context::moveUpRow
        );
    }

    @Override
    public Optional<SpreadsheetSelection> moveDownRow(final SpreadsheetViewportAnchor anchor,
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
    public Optional<SpreadsheetSelection> moveUpPixels(final SpreadsheetViewportAnchor anchor,
                                                       final int count,
                                                       final SpreadsheetViewportNavigationContext context) {
        return this.upOrDownPixels(
            context,
            count,
            context::upPixels
        );
    }

    @Override
    public Optional<SpreadsheetSelection> downPixels(final SpreadsheetViewportAnchor anchor,
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
    public Optional<AnchoredSpreadsheetSelection> extendLeftColumn(final SpreadsheetViewportAnchor anchor,
                                                                   final SpreadsheetViewportNavigationContext context) {
        return this.extendRange(
            this.moveLeftColumn(
                anchor,
                context
            ),
            anchor
        ).map(this::setAnchorOrDefaultBottomRight);
    }

    @Override
    public Optional<AnchoredSpreadsheetSelection> extendLeftPixels(final SpreadsheetViewportAnchor anchor,
                                                                   final int count,
                                                                   final SpreadsheetViewportNavigationContext context) {
        return this.extendRange(
            this.moveLeftPixels(
                anchor,
                count,
                context
            ),
            anchor
        ).map(this::setAnchorOrDefaultBottomRight);
    }

    @Override
    public Optional<AnchoredSpreadsheetSelection> extendUpRow(final SpreadsheetViewportAnchor anchor,
                                                              final SpreadsheetViewportNavigationContext context) {
        return this.extendRange(
            this.moveUpRow(
                anchor,
                context
            ),
            anchor
        ).map(this::setAnchorOrDefaultBottomRight);
    }

    @Override
    public Optional<AnchoredSpreadsheetSelection> extendUpPixels(final SpreadsheetViewportAnchor anchor,
                                                                 final int count,
                                                                 final SpreadsheetViewportNavigationContext context) {
        return this.extendRange(
            this.moveUpPixels(
                anchor,
                count,
                context
            ),
            anchor
        ).map(this::setAnchorOrDefaultBottomRight);
    }

    private AnchoredSpreadsheetSelection setAnchorOrDefaultBottomRight(final SpreadsheetSelection selection) {
        return selection.setAnchorOrDefault(SpreadsheetViewportAnchor.BOTTOM_RIGHT);
    }

    @Override
    public Optional<AnchoredSpreadsheetSelection> extendRightColumn(final SpreadsheetViewportAnchor anchor,
                                                                    final SpreadsheetViewportNavigationContext context) {
        return this.extendRange(
            this.moveRightColumn(
                anchor,
                context
            ),
            anchor
        ).map(this::setAnchorOrDefaultTopLeft);
    }

    @Override
    public Optional<AnchoredSpreadsheetSelection> extendRightPixels(final SpreadsheetViewportAnchor anchor,
                                                                    final int count,
                                                                    final SpreadsheetViewportNavigationContext context) {
        return this.extendRange(
            this.moveRightPixels(
                anchor,
                count,
                context
            ),
            anchor
        ).map(this::setAnchorOrDefaultTopLeft);
    }

    @Override
    public Optional<AnchoredSpreadsheetSelection> extendDownRow(final SpreadsheetViewportAnchor anchor,
                                                                final SpreadsheetViewportNavigationContext context) {
        return this.extendRange(
            this.moveDownRow(
                anchor,
                context
            ),
            anchor
        ).map(this::setAnchorOrDefaultTopLeft);
    }

    @Override
    public Optional<AnchoredSpreadsheetSelection> extendDownPixels(final SpreadsheetViewportAnchor anchor,
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

    private AnchoredSpreadsheetSelection setAnchorOrDefaultTopLeft(final SpreadsheetSelection selection) {
        return selection.setAnchorOrDefault(SpreadsheetViewportAnchor.TOP_LEFT);
    }

    @Override
    Optional<SpreadsheetSelection> extendRange(final Optional<? extends SpreadsheetSelection> other,
                                               final SpreadsheetViewportAnchor anchor) {
        return other.map(
            o -> this.cellRange(o.toCell())
                .toScalarIfUnit()
        );
    }

    // focused...........................................................................................................

    @Override
    public SpreadsheetCellReference focused(final SpreadsheetViewportAnchor anchor) {
        this.checkAnchor(anchor);
        return this;
    }

    // HasParserToken...................................................................................................

    @Override
    public CellSpreadsheetFormulaParserToken toParserToken() {
        // GWTC fails if type parameter missing
        //
        // [INFO]       [ERROR] Errors in 'walkingkooka/spreadsheet/reference/SpreadsheetCellReference.java'
        // [INFO]          [ERROR] Line 644: The method of(T...) of type Lists is not applicable as the formal varargs element type T is not accessible here
        return SpreadsheetFormulaParserToken.cell(
            Lists.<ParserToken>of(
                this.column()
                    .toParserToken(),
                this.row()
                    .toParserToken()
            ),
            this.text()
        );
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(this.column, this.row);
    }

    @Override
    boolean equalsNotSameAndNotNull(final Object other,
                                    final boolean includeKind) {
        return this.equals1(
            (SpreadsheetCellReference) other,
            includeKind
        );
    }

    private boolean equals1(final SpreadsheetCellReference other,
                            final boolean includeKind) {
        return this.column.equals1(
            other.column,
            includeKind
        ) &&
            this.row.equals1(
                other.row,
                includeKind
            );
    }

    @Override
    public String toString() {
        return this.column.toString()
            .concat(this.row.toString());
    }

    // Comparable ......................................................................................................

    /**
     * Sort by rows then column, so B3 comes before C2
     */
    @Override
    public int compareTo(final SpreadsheetCellReference other) {
        final int result = this.row.value - other.row.value;
        return Comparators.EQUAL != result ?
            result :
            this.column.value - other.column.value;
    }

    // SpreadsheetSelectionIgnoresReferenceKindComparator...............................................................

    @Override
    int spreadsheetSelectionIgnoresReferenceKindComparatorPriority() {
        return SpreadsheetSelectionIgnoresReferenceKindComparator.CELL;
    }
}

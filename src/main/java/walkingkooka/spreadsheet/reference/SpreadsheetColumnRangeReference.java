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

import walkingkooka.collect.HasRange;
import walkingkooka.collect.HasRangeBounds;
import walkingkooka.collect.Range;
import walkingkooka.collect.RangeBound;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.viewport.AnchoredSpreadsheetSelection;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewportAnchor;
import walkingkooka.spreadsheet.viewport.SpreadsheetViewportNavigationContext;
import walkingkooka.text.CharSequences;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

/**
 * Holds a column range.
 */
@SuppressWarnings("lgtm[java/inconsistent-equals-and-hashcode]")
public final class SpreadsheetColumnRangeReference extends SpreadsheetColumnReferenceOrRange
    implements Comparable<SpreadsheetColumnRangeReference>,
    HasRange<SpreadsheetColumnReference>,
    HasRangeBounds<SpreadsheetColumnReference>,
    Iterable<SpreadsheetColumnReference> {

    /**
     * A {@link SpreadsheetColumnRangeReference} that includes all columns.
     */
    public static final SpreadsheetColumnRangeReference ALL = SpreadsheetReferenceKind.RELATIVE.firstColumn()
        .columnRange(
            SpreadsheetReferenceKind.RELATIVE.lastColumn()
        );

    /**
     * Factory that creates a {@link SpreadsheetColumnRangeReference}
     */
    static SpreadsheetColumnRangeReference with(final Range<SpreadsheetColumnReference> range) {
        SpreadsheetSelectionRangeRangeVisitor.check(range);

        return new SpreadsheetColumnRangeReference(range);
    }

    /**
     * Private ctor
     */
    private SpreadsheetColumnRangeReference(final Range<SpreadsheetColumnReference> range) {
        super();
        this.range = range;
    }

    /**
     * Returns the top left column/row reference.
     */
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public SpreadsheetColumnReference begin() {
        return this.range.lowerBound()
            .value()
            .get(); // must exist
    }

    /**
     * Returns the bottom right column/row reference.
     */
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public SpreadsheetColumnReference end() {
        return this.range.upperBound()
            .value()
            .get(); // must exist
    }

    @Override
    public Range<SpreadsheetColumnReference> range() {
        return this.range;
    }

    private final Range<SpreadsheetColumnReference> range;


    public SpreadsheetColumnRangeReference setRange(final Range<SpreadsheetColumnReference> range) {
        Objects.requireNonNull(range, "range");

        return this.range.equals(range) ?
            this :
            with(range);
    }

    /**
     * Creates a {@link SpreadsheetCellRangeReference} combining this column range and the given row range.
     */
    public SpreadsheetCellRangeReference setRowRange(final SpreadsheetRowRangeReference row) {
        Objects.requireNonNull(row, "row");

        final SpreadsheetColumnReference columnBegin = this.begin();
        final SpreadsheetRowReference rowBegin = row.begin();

        final SpreadsheetColumnReference columnEnd = this.end();
        final SpreadsheetRowReference rowEnd = row.end();

        return columnBegin.setRow(rowBegin)
            .cellRange(
                columnEnd.setRow(rowEnd)
            );
    }

    // add..............................................................................................................

    @Override
    public SpreadsheetColumnRangeReference add(final int value) {
        return this.setRange(
            Range.with(
                RangeBound.inclusive(
                    this.begin().add(value)
                ),
                RangeBound.inclusive(
                    this.end().add(value)
                )
            )
        );
    }

    // addSaturated.....................................................................................................

    @Override
    public SpreadsheetColumnRangeReference addSaturated(final int value) {
        return this.setRange(
            Range.with(
                RangeBound.inclusive(
                    this.begin()
                        .addSaturated(value)
                ),
                RangeBound.inclusive(
                    this.end()
                        .addSaturated(value)
                )
            )
        );
    }

    @Override
    public SpreadsheetColumnRangeReference add(final int column,
                                               final int row) {
        checkRowDeltaIsZero(row);
        return this.add(column);
    }

    @Override
    public SpreadsheetColumnRangeReference addSaturated(final int column,
                                                        final int row) {
        checkRowDeltaIsZero(row);
        return this.addSaturated(column);
    }

    @Override
    public SpreadsheetColumnRangeReference addIfRelative(final int delta) {
        return this.setRange(
            Range.with(
                RangeBound.inclusive(
                    this.begin()
                        .addIfRelative(delta)
                ),
                RangeBound.inclusive(
                    this.end()
                        .addIfRelative(delta)
                )
            )
        );
    }

    // testXXX.........................................................................................................

    @Override
    boolean testCellNonNull(final SpreadsheetCellReference cell) {
        return this.testColumnNonNull(cell.column());
    }

    @Override
    boolean testCellRangeNonNull(final SpreadsheetCellRangeReference range) {
        return this.end().compareTo(range.begin().column()) >= 0 &&
            this.begin().compareTo(range.end().column()) <= 0;
    }

    /**
     * Tests if the given {@link SpreadsheetColumnReference} is within this {@link SpreadsheetColumnRangeReference}.
     */
    @Override
    boolean testColumnNonNull(final SpreadsheetColumnReference column) {
        return this.range.test(column);
    }

    // count............................................................................................................

    /**
     * Returns the number of columns in this range.
     */
    @Override
    public long count() {
        return this.end()
            .value()
            - this.begin()
            .value()
            + 1;
    }

    // isXXX............................................................................................................

    @Override
    public boolean isFirst() {
        return this.begin().isFirst() && this.isUnit();
    }

    @Override
    public boolean isLast() {
        return this.begin().isLast() && this.isUnit();
    }

    // toXXX............................................................................................................

    @Override
    public SpreadsheetColumnReference toColumn() {
        return this.begin();
    }

    @Override
    public SpreadsheetColumnRangeReference toColumnRange() {
        return this;
    }

    // toScalar.........................................................................................................

    @Override
    public SpreadsheetColumnReference toScalar() {
        return this.begin();
    }

    // toRange..........................................................................................................

    @Override
    public SpreadsheetColumnRangeReference toRange() {
        return this;
    }

    // toRelative......................................................................................................

    @Override
    public SpreadsheetColumnRangeReference toRelative() {
        final SpreadsheetColumnRangeReference relative = this.begin()
            .toRelative()
            .columnRange(this.end()
                .toRelative());
        return this.equals(relative) ?
            this :
            relative;
    }

    @Override
    public Set<SpreadsheetViewportAnchor> anchors() {
        return ANCHORS;
    }

    private final static Set<SpreadsheetViewportAnchor> ANCHORS = Sets.readOnly(
        EnumSet.of(
            SpreadsheetViewportAnchor.LEFT,
            SpreadsheetViewportAnchor.RIGHT
        )
    );

    @Override
    public SpreadsheetViewportAnchor defaultAnchor() {
        return SpreadsheetViewportAnchor.COLUMN_RANGE;
    }

    /**
     * Complains if this column range is not a valid frozen columns range representation.
     * A frozen column range must begin with column A
     */
    public void frozenColumnsCheck() {
        if (this.begin().value() != 0) {
            throw new IllegalArgumentException("Range must begin at 'A' but was " + CharSequences.quoteAndEscape(this.toString()));
        }
    }

    /**
     * A {@link SpreadsheetCellReference} is hidden if either begin or end is hidden.
     */
    @Override
    public boolean isHidden(final Predicate<SpreadsheetColumnReference> hiddenColumnTester,
                            final Predicate<SpreadsheetRowReference> hiddenRowTester) {
        return isHiddenRange(
            this,
            hiddenColumnTester,
            hiddenRowTester
        );
    }

    // navigate.........................................................................................................

    @Override
    public Optional<SpreadsheetSelection> moveLeftColumn(final SpreadsheetViewportAnchor anchor,
                                                         final SpreadsheetViewportNavigationContext context) {
        return anchor.opposite()
            .column(this)
            .moveLeftColumn(
                anchor,
                context
            );
    }

    @Override
    public Optional<SpreadsheetSelection> moveLeftPixels(final SpreadsheetViewportAnchor anchor,
                                                         final int count,
                                                         final SpreadsheetViewportNavigationContext context) {
        return anchor.opposite()
            .column(this)
            .moveLeftPixels(
                anchor,
                count,
                context
            );
    }

    @Override
    public Optional<SpreadsheetSelection> moveRightColumn(final SpreadsheetViewportAnchor anchor,
                                                          final SpreadsheetViewportNavigationContext context) {
        return anchor.opposite()
            .column(this)
            .moveRightColumn(
                anchor,
                context
            );
    }

    @Override
    public Optional<SpreadsheetSelection> rightPixels(final SpreadsheetViewportAnchor anchor,
                                                      final int count,
                                                      final SpreadsheetViewportNavigationContext context) {
        return anchor.opposite()
            .column(this)
            .rightPixels(
                anchor,
                count,
                context
            );
    }

    @Override
    public Optional<SpreadsheetSelection> moveUpRow(final SpreadsheetViewportAnchor anchor,
                                                    final SpreadsheetViewportNavigationContext context) {
        return this.emptyIfHidden(context);
    }

    @Override
    public Optional<SpreadsheetSelection> moveUpPixels(final SpreadsheetViewportAnchor anchor,
                                                       final int count,
                                                       final SpreadsheetViewportNavigationContext context) {
        return this.emptyIfHidden(context);
    }

    @Override
    public Optional<SpreadsheetSelection> downRow(final SpreadsheetViewportAnchor anchor,
                                                  final SpreadsheetViewportNavigationContext context) {
        return this.emptyIfHidden(context);
    }

    @Override
    public Optional<SpreadsheetSelection> downPixels(final SpreadsheetViewportAnchor anchor,
                                                     final int count,
                                                     final SpreadsheetViewportNavigationContext context) {
        return this.emptyIfHidden(context);
    }

    @Override
    public Optional<AnchoredSpreadsheetSelection> extendLeftColumn(final SpreadsheetViewportAnchor anchor,
                                                                   final SpreadsheetViewportNavigationContext context) {
        return this.extendColumn(
            this.isUnit() ? SpreadsheetViewportAnchor.RIGHT : anchor,
            context::moveLeft
        );
    }

    @Override
    public Optional<AnchoredSpreadsheetSelection> extendLeftPixels(final SpreadsheetViewportAnchor anchor,
                                                                   final int count,
                                                                   final SpreadsheetViewportNavigationContext context) {
        return this.extendColumn(
            this.isUnit() ? SpreadsheetViewportAnchor.RIGHT : anchor,
            c -> context.leftPixels(
                c,
                count
            )
        );
    }

    @Override
    public Optional<AnchoredSpreadsheetSelection> extendRightColumn(final SpreadsheetViewportAnchor anchor,
                                                                    final SpreadsheetViewportNavigationContext context) {
        return this.extendColumn(
            this.isUnit() ? SpreadsheetViewportAnchor.LEFT : anchor,
            context::moveRightColumn
        );
    }

    @Override
    public Optional<AnchoredSpreadsheetSelection> extendRightPixels(final SpreadsheetViewportAnchor anchor,
                                                                    final int count,
                                                                    final SpreadsheetViewportNavigationContext context) {
        return this.extendColumn(
            this.isUnit() ? SpreadsheetViewportAnchor.LEFT : anchor,
            c -> context.rightPixels(
                c,
                count
            )
        );
    }

    private Optional<AnchoredSpreadsheetSelection> extendColumn(final SpreadsheetViewportAnchor anchor,
                                                                final Function<SpreadsheetColumnReference, Optional<SpreadsheetColumnReference>> move) {
        return this.extendRange(
            move.apply(
                anchor.opposite()
                    .column(this)
            ),
            anchor
        ).map(s -> s.setAnchorOrDefault(anchor));
    }

    @Override
    Optional<SpreadsheetSelection> extendRange(final Optional<? extends SpreadsheetSelection> other,
                                               final SpreadsheetViewportAnchor anchor) {
        return other.map(
            s -> anchor.column(this)
                .columnRange(s.toColumn())
                .toScalarIfUnit()
        );
    }

    @Override
    public Optional<AnchoredSpreadsheetSelection> extendUpRow(final SpreadsheetViewportAnchor anchor,
                                                              final SpreadsheetViewportNavigationContext context) {
        return this.setAnchorEmptyIfHidden(
            anchor,
            context
        );
    }

    @Override
    public Optional<AnchoredSpreadsheetSelection> extendUpPixels(final SpreadsheetViewportAnchor anchor,
                                                                 final int count,
                                                                 final SpreadsheetViewportNavigationContext context) {
        return this.setAnchorEmptyIfHidden(
            anchor,
            context
        );
    }

    @Override
    public Optional<AnchoredSpreadsheetSelection> extendDownRow(final SpreadsheetViewportAnchor anchor,
                                                                final SpreadsheetViewportNavigationContext context) {
        return this.setAnchorEmptyIfHidden(
            anchor,
            context
        );
    }


    @Override
    public Optional<AnchoredSpreadsheetSelection> extendDownPixels(final SpreadsheetViewportAnchor anchor,
                                                                   final int count,
                                                                   final SpreadsheetViewportNavigationContext context) {
        return this.setAnchorEmptyIfHidden(
            anchor,
            context
        );
    }

    // focused...........................................................................................................

    @Override
    public SpreadsheetColumnReference focused(final SpreadsheetViewportAnchor anchor) {
        this.checkAnchor(anchor);
        return anchor.opposite()
            .column(this);
    }

    // replaceReferencesMapper..........................................................................................

    @Override
    Optional<Function<SpreadsheetCellReference, Optional<SpreadsheetCellReference>>> replaceReferencesMapper0(final SpreadsheetSelection movedTo) {
        return this.toScalar()
            .replaceReferencesMapper0(movedTo);
    }

    // SpreadsheetSelectionVisitor......................................................................................

    @Override
    void accept(final SpreadsheetSelectionVisitor visitor) {
        visitor.visit(this);
    }

    // Iterable.........................................................................................................

    @Override
    public Iterator<SpreadsheetColumnReference> iterator() {
        return IntStream.rangeClosed(
                this.begin().value(),
                this.end().value()
            )
            .boxed()
            .map(SpreadsheetReferenceKind.RELATIVE::column)
            .iterator();
    }

    // HasParserToken...................................................................................................

    @Override
    public SpreadsheetFormulaParserToken toParserToken() {
        throw new UnsupportedOperationException();
    }

    // hashCode/equals..................................................................................................

    @Override
    public int hashCode() {
        return this.range.hashCode();
    }

    @Override
    boolean equalsNotSameAndNotNull(final Object other,
                                    final boolean includeKind) {
        return this.equals1(
            (SpreadsheetColumnRangeReference) other,
            includeKind
        );
    }

    private boolean equals1(final SpreadsheetColumnRangeReference other,
                            final boolean includeKind) {
        return this.begin().equalsNotSameAndNotNull(other.begin(), includeKind) &&
            this.end().equalsNotSameAndNotNull(other.end(), includeKind);
    }

    // toString.........................................................................................................

    @Override
    public String toString() {
        return this.isUnit() ?
            this.begin().toString() :
            this.begin() + SEPARATOR.string() + this.end();
    }

    // Comparable.......................................................................................................

    @Override
    public int compareTo(final SpreadsheetColumnRangeReference other) {
        int result = this.begin().compareTo(other.begin());
        if (0 == result) {
            result = this.end().compareTo(other.end());
        }
        return result;
    }

    // SpreadsheetSelectionIgnoresReferenceKindComparator...............................................................

    @Override
    int spreadsheetSelectionIgnoresReferenceKindComparatorPriority() {
        return SpreadsheetSelectionIgnoresReferenceKindComparator.COLUMN_RANGE;
    }
}

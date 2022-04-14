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
import walkingkooka.spreadsheet.store.SpreadsheetColumnStore;
import walkingkooka.spreadsheet.store.SpreadsheetRowStore;
import walkingkooka.text.CharSequences;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Holds a column range.
 */
@SuppressWarnings("lgtm[java/inconsistent-equals-and-hashcode]")
public final class SpreadsheetColumnReferenceRange extends SpreadsheetColumnOrRowReferenceRange<SpreadsheetColumnReference>
        implements Comparable<SpreadsheetColumnReferenceRange> {

    /**
     * Factory that creates a {@link SpreadsheetColumnReferenceRange}
     */
    static SpreadsheetColumnReferenceRange with(final Range<SpreadsheetColumnReference> range) {
        SpreadsheetRangeRangeVisitor.check(range);

        return new SpreadsheetColumnReferenceRange(range);
    }

    /**
     * Private ctor
     */
    private SpreadsheetColumnReferenceRange(final Range<SpreadsheetColumnReference> range) {
        super(range);
    }

    public SpreadsheetColumnReferenceRange setRange(final Range<SpreadsheetColumnReference> range) {
        return this.setRange0(range);
    }

    @Override
    SpreadsheetColumnReferenceRange replace(final Range<SpreadsheetColumnReference> range) {
        return with(range);
    }

    /**
     * Creates a {@link SpreadsheetCellRange} combining this column range and the given row range.
     */
    public SpreadsheetCellRange setRowReferenceRange(final SpreadsheetRowReferenceRange row) {
        checkRowReferenceRange(row);

        final SpreadsheetColumnReference columnBegin = this.begin();
        final SpreadsheetRowReference rowBegin = row.begin();

        final SpreadsheetColumnReference columnEnd = this.end();
        final SpreadsheetRowReference rowEnd = row.end();

        return columnBegin.setRow(rowBegin)
                .cellRange(
                        columnEnd.setRow(rowEnd)
                );
    }

    @Override
    public boolean testCellRange(final SpreadsheetCellRange range) {
        return this.end().compareTo(range.begin().column()) >= 0 &&
                this.begin().compareTo(range.end().column()) <= 0;
    }

    @Override
    public boolean test(final SpreadsheetCellReference reference) {
        return this.testColumn(reference.column());
    }

    /**
     * Tests if the given {@link SpreadsheetColumnReference} is within this {@link SpreadsheetColumnReferenceRange}.
     */
    public boolean testColumn(final SpreadsheetColumnReference column) {
        return this.range.test(column);
    }

    @Override
    public SpreadsheetColumnReferenceRange toRelative() {
        final SpreadsheetColumnReferenceRange relative = this.begin()
                .toRelative()
                .columnRange(this.end()
                        .toRelative());
        return this.equals(relative) ?
                this :
                relative;
    }

    @Override
    Set<SpreadsheetViewportSelectionAnchor> anchors() {
        return ANCHORS;
    }

    private final static Set<SpreadsheetViewportSelectionAnchor> ANCHORS = EnumSet.of(
            SpreadsheetViewportSelectionAnchor.LEFT,
            SpreadsheetViewportSelectionAnchor.RIGHT
    );

    @Override
    public SpreadsheetViewportSelectionAnchor defaultAnchor() {
        return SpreadsheetViewportSelectionAnchor.COLUMN_RANGE;
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

    @Override
    Optional<SpreadsheetSelection> left(final SpreadsheetViewportSelectionAnchor anchor,
                                        final SpreadsheetColumnStore columnStore,
                                        final SpreadsheetRowStore rowStore) {
        return anchor.column(this)
                .left(anchor, columnStore, rowStore);
    }

    @Override
    Optional<SpreadsheetSelection> right(final SpreadsheetViewportSelectionAnchor anchor,
                                         final SpreadsheetColumnStore columnStore,
                                         final SpreadsheetRowStore rowStore) {
        return anchor.column(this)
                .right(anchor, columnStore, rowStore);
    }

    @Override
    Optional<SpreadsheetSelection> up(final SpreadsheetViewportSelectionAnchor anchor,
                                      final SpreadsheetColumnStore columnStore,
                                      final SpreadsheetRowStore rowStore) {
        return this.emptyIfHidden(
                columnStore,
                rowStore
        );
    }

    @Override
    Optional<SpreadsheetSelection> down(final SpreadsheetViewportSelectionAnchor anchor,
                                        final SpreadsheetColumnStore columnStore,
                                        final SpreadsheetRowStore rowStore) {
        return this.emptyIfHidden(
                columnStore,
                rowStore
        );
    }

    @Override
    Optional<SpreadsheetViewportSelection> extendLeft(final SpreadsheetViewportSelectionAnchor anchor,
                                                      final SpreadsheetColumnStore columnStore,
                                                      final SpreadsheetRowStore rowStore) {
        return this.extendColumn(
                this.isSingle() ? SpreadsheetViewportSelectionAnchor.RIGHT : anchor,
                (c) -> columnStore.leftSkipHidden(c)
        );
    }

    @Override
    Optional<SpreadsheetViewportSelection> extendRight(final SpreadsheetViewportSelectionAnchor anchor,
                                                       final SpreadsheetColumnStore columnStore,
                                                       final SpreadsheetRowStore rowStore) {
        return this.extendColumn(
                this.isSingle() ? SpreadsheetViewportSelectionAnchor.LEFT : anchor,
                (c) -> columnStore.rightSkipHidden(c)
        );
    }

    private Optional<SpreadsheetViewportSelection> extendColumn(final SpreadsheetViewportSelectionAnchor anchor,
                                                                final Function<SpreadsheetColumnReference, Optional<SpreadsheetColumnReference>> move) {
        return this.extendRange(
                move.apply(anchor.column(this)),
                anchor
        ).map(s -> s.setAnchorOrDefault(anchor));
    }

    @Override
    Optional<SpreadsheetSelection> extendRange(final Optional<? extends SpreadsheetSelection> other,
                                               final SpreadsheetViewportSelectionAnchor anchor) {
        return other.map(
                s -> anchor.fixedColumn(this)
                        .columnRange((SpreadsheetColumnReference) s)
                        .simplify()
        );
    }

    @Override
    Optional<SpreadsheetViewportSelection> extendUp(final SpreadsheetViewportSelectionAnchor anchor,
                                                    final SpreadsheetColumnStore columnStore,
                                                    final SpreadsheetRowStore rowStore) {
        return this.setAnchorEmptyIfHidden(
                anchor,
                columnStore,
                rowStore
        );
    }

    @Override
    Optional<SpreadsheetViewportSelection> extendDown(final SpreadsheetViewportSelectionAnchor anchor,
                                                      final SpreadsheetColumnStore columnStore,
                                                      final SpreadsheetRowStore rowStore) {
        return this.setAnchorEmptyIfHidden(
                anchor,
                columnStore,
                rowStore
        );
    }

    // SpreadsheetSelectionVisitor......................................................................................

    @Override
    void accept(final SpreadsheetSelectionVisitor visitor) {
        visitor.visit(this);
    }

    // Iterable.........................................................................................................

    @Override
    SpreadsheetColumnReference iteratorIntToReference(final int value) {
        return SpreadsheetReferenceKind.RELATIVE.column(value);
    }

    // TreePrintable....................................................................................................

    @Override
    String printTreeLabel() {
        return "column-range";
    }

    // Object...........................................................................................................

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof SpreadsheetColumnReferenceRange;
    }

    @Override
    public int compareTo(final SpreadsheetColumnReferenceRange other) {
        throw new UnsupportedOperationException();
    }
}

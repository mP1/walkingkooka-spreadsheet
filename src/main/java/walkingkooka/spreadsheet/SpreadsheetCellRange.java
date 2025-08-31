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

import walkingkooka.Value;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.spreadsheet.compare.SpreadsheetColumnOrRowSpreadsheetComparatorNamesList;
import walkingkooka.spreadsheet.compare.SpreadsheetColumnOrRowSpreadsheetComparators;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorContext;
import walkingkooka.spreadsheet.reference.CanReplaceReferences;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnOrRowReferenceKind;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnOrRowReferenceOrRange;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A container for cells within a {@link SpreadsheetCellRangeReference}.
 */
public final class SpreadsheetCellRange implements Value<Set<SpreadsheetCell>>,
    TreePrintable {

    public static SpreadsheetCellRange with(final SpreadsheetCellRangeReference range,
                                            final Set<SpreadsheetCell> value) {
        checkRange(range);
        Objects.requireNonNull(value, "value");

        final Set<SpreadsheetCell> copy = SortedSets.tree(SpreadsheetCell.REFERENCE_COMPARATOR);
        copy.addAll(value);
        checkValues(range, copy);

        return new SpreadsheetCellRange(
            range,
            copy
        );
    }

    private SpreadsheetCellRange(final SpreadsheetCellRangeReference range,
                                 final Set<SpreadsheetCell> value) {
        this.range = range;
        this.value = value;
    }

    public SpreadsheetCellRangeReference range() {
        return this.range;
    }

    public SpreadsheetCellRange setRange(final SpreadsheetCellRangeReference range) {
        checkRange(range);

        return this.range.equals(range) ?
            this :
            this.setRange0(range);
    }

    private SpreadsheetCellRange setRange0(final SpreadsheetCellRangeReference range) {
        final Set<SpreadsheetCell> value = this.value;

        if (false == range.containsAll(this.range)) {
            checkValues(
                range,
                value
            );
        }

        return new SpreadsheetCellRange(
            range,
            value
        );
    }

    private final SpreadsheetCellRangeReference range;

    private static SpreadsheetCellRangeReference checkRange(final SpreadsheetCellRangeReference range) {
        return Objects.requireNonNull(range, "range");
    }

    @Override
    public Set<SpreadsheetCell> value() {
        return this.value;
    }

    public SpreadsheetCellRange setValue(final Set<SpreadsheetCell> value) {
        Objects.requireNonNull(value, "value");

        final SortedSet<SpreadsheetCell> treeSet = SortedSets.tree(SpreadsheetCell.REFERENCE_COMPARATOR);
        treeSet.addAll(value);

        final Set<SpreadsheetCell> copy = SortedSets.immutable(treeSet);
        checkValues(this.range, copy);

        return this.value.equals(copy) ?
            this :
            new SpreadsheetCellRange(
                range,
                value
            );
    }

    private final Set<SpreadsheetCell> value;

    private static void checkValues(final SpreadsheetCellRangeReference range,
                                    final Set<SpreadsheetCell> value) {
        final Set<SpreadsheetCellReference> outOfBounds = value.stream()
            .map(SpreadsheetCell::reference)
            .filter(c -> false == range.testCell(c))
            .collect(Collectors.toCollection(SortedSets::tree));

        if (false == outOfBounds.isEmpty()) {
            throw new IllegalArgumentException(
                "Found " +
                    outOfBounds.size() +
                    " cells out of range " +
                    range +
                    " got " +
                    outOfBounds.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(", "))
            );
        }
    }

    // move.............................................................................................................

    /**
     * Moves all the values from this range to the new given {@link SpreadsheetCellRangeReference}.
     * This will be used by a PASTE from the clipboard to a new range. The target could be smaller and the extra
     * cells will be clipped and lost.
     */
    public SpreadsheetCellRange move(final SpreadsheetCellRangeReference range) {
        checkRange(range);

        return this.range.equalsIgnoreReferenceKind(range) ?
            this.setRange(range) :
            this.move0(
                range,
                this.range.replaceReferencesMapper(range)
                    .orElse(CanReplaceReferences.NULL_REPLACE_REFERENCE_MAPPER)
            );
    }

    private SpreadsheetCellRange move0(final SpreadsheetCellRangeReference to,
                                       final Function<SpreadsheetCellReference, Optional<SpreadsheetCellReference>> mapper) {
        final Set<SpreadsheetCell> movedCells = SortedSets.tree(SpreadsheetCell.REFERENCE_COMPARATOR);

        for (final SpreadsheetCell cell : this.value) {
            final Optional<SpreadsheetCellReference> maybeMoved = mapper.apply(cell.reference());

            // destination could be out of bounds, ignore those.
            if (maybeMoved.isPresent()) {
                final SpreadsheetCellReference moved = maybeMoved.get();

                // destination range could be smaller, ignore values outside
                if (to.testCell(moved)) {
                    movedCells.add(
                        cell.replaceReferences(mapper)
                    );
                }
            }
        }

        return new SpreadsheetCellRange(
            to,
            movedCells
        );
    }

    // sort.............................................................................................................

    /**
     * Uses the provided {@link SpreadsheetColumnOrRowSpreadsheetComparators} to sort the selected columns or rows.
     * The {@link BiConsumer} will receive all cells that were moved by the sort. Moved cells will lose their value
     * and will need to be re-evaluated.
     */
    public SpreadsheetCellRange sort(final List<SpreadsheetColumnOrRowSpreadsheetComparators> comparators,
                                     final BiConsumer<SpreadsheetCell, SpreadsheetCell> movedCells,
                                     final SpreadsheetComparatorContext context) {
        Objects.requireNonNull(comparators, "comparators");
        Objects.requireNonNull(movedCells, "movedCells");
        Objects.requireNonNull(context, "context");

        return sort0(
            SpreadsheetColumnOrRowSpreadsheetComparators.list(comparators),
            movedCells,
            context
        );
    }

    public SpreadsheetCellRange sort0(final List<SpreadsheetColumnOrRowSpreadsheetComparators> comparators,
                                      final BiConsumer<SpreadsheetCell, SpreadsheetCell> movedCellsConsumer,
                                      final SpreadsheetComparatorContext context) {
        final SpreadsheetColumnOrRowReferenceKind widthKind = comparators.get(0)
            .columnOrRow()
            .columnOrRowReferenceKind();

        final SpreadsheetCellRangeReference cellRange = this.range();
        cellRange.comparatorNamesBoundsCheck(
            SpreadsheetColumnOrRowSpreadsheetComparatorNamesList.with(
                comparators.stream()
                    .map(SpreadsheetColumnOrRowSpreadsheetComparators::toSpreadsheetColumnOrRowSpreadsheetComparatorNames)
                    .collect(Collectors.toList())
            )
        );

        final SpreadsheetCellReference home = cellRange.toCell();
        final int base = widthKind.value(home);

        final int width = widthKind.length(cellRange);

        final SpreadsheetColumnOrRowReferenceKind heightKind = widthKind.flip();

        final Map<SpreadsheetSelection, SpreadsheetCellRangeSortList> yToCells = Maps.sorted();

        // when sorting columns this will hold rows of cells
        // when sorting rows this will hold columns of cells
        // this allows the comparator to loop thru the cells when the first column/row is equal
        final List<SpreadsheetCellRangeSortList> allCells = Lists.array(); // this will the list that is sorted.

        for (final SpreadsheetCell cell : this.value) {
            final SpreadsheetColumnOrRowReferenceOrRange y = heightKind.columnOrRow(
                cell.reference()
            );
            SpreadsheetCellRangeSortList cells = yToCells.get(y);
            if (null == cells) {
                cells = SpreadsheetCellRangeSortList.with(
                    y,
                    width
                );
                yToCells.put(
                    y,
                    cells
                );
                allCells.add(cells);
            }

            final SpreadsheetSelection x = widthKind.columnOrRow(
                cell.reference()
            );

            cells.set(
                widthKind.value(x) - base,
                cell
            );
        }

        // sort $rows
        allCells.sort(
            SpreadsheetCellRangeComparator.with(
                comparators,
                context
            )
        );

        final Set<SpreadsheetCell> newCells = SortedSets.tree(SpreadsheetCell.REFERENCE_COMPARATOR);
        SpreadsheetSelection actualY = heightKind.columnOrRow(home);

        for (final SpreadsheetCellRangeSortList yCells : allCells) {
            final SpreadsheetSelection y = yCells.columnOrRow;

            final Optional<Function<SpreadsheetCellReference, Optional<SpreadsheetCellReference>>> referenceMapper = y.replaceReferencesMapper(actualY);

            if (referenceMapper.isPresent()) {
                for (final SpreadsheetCell cell : yCells) {
                    if (null != cell) {
                        final SpreadsheetCell movedCell = cell.replaceReferences(
                            referenceMapper.get()
                        );
                        // did cell move ?
                        if (false == cell.equals(movedCell)) {
                            movedCellsConsumer.accept(
                                cell,
                                movedCell
                            );
                        }
                        // always copy movedCell
                        newCells.add(movedCell);
                    }
                }
            } else {
                // just copy any cells in the row.
                for (final SpreadsheetCell cell : yCells) {
                    if (null != cell) {
                        newCells.add(cell);
                    }
                }
            }

            actualY = actualY.add(1);
        }

        return this.setValue(newCells);
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.range.toStringMaybeStar());

        printer.indent();
        {
            for (final SpreadsheetCell cell : this.value) {
                cell.printTree(printer);
            }
        }
        printer.outdent();
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.range,
            this.value
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof SpreadsheetCellRange && this.equals0((SpreadsheetCellRange) other);
    }

    private boolean equals0(final SpreadsheetCellRange other) {
        return this.range.equals(other.range) &&
            this.value.equals(other.value);
    }

    @Override
    public String toString() {
        return this.range + " " + this.value;
    }
}

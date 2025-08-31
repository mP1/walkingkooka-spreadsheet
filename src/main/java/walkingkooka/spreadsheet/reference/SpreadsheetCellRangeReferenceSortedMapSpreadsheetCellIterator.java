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

import walkingkooka.spreadsheet.SpreadsheetCell;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.SortedMap;
import java.util.function.BiFunction;

/**
 * An {@link Iterator} that returns cells, row by row for a given {@link SpreadsheetCellRangeReference} from a large {@link SortedMap},
 * in an efficient manner, with the iterator skipping cells outside the {@link SpreadsheetCellRangeReference}.
 * <br>
 * Note for performance reasons the {@link Map} is NOT defensively copied.
 */
final class SpreadsheetCellRangeReferenceSortedMapSpreadsheetCellIterator implements Iterator<SpreadsheetCell> {

    static SpreadsheetCellRangeReferenceSortedMapSpreadsheetCellIterator with(final SpreadsheetCellRangeReference range,
                                                                              final SortedMap<SpreadsheetCellReference, SpreadsheetCell> cells) {
        Objects.requireNonNull(cells, "cells");

        return new SpreadsheetCellRangeReferenceSortedMapSpreadsheetCellIterator(
            range,
            cells
        );
    }

    private SpreadsheetCellRangeReferenceSortedMapSpreadsheetCellIterator(final SpreadsheetCellRangeReference range,
                                                                          final SortedMap<SpreadsheetCellReference, SpreadsheetCell> cells) {
        this.range = range;
        this.nextRowFirstColumn = range.begin();

        final SpreadsheetColumnReference lastColumn = range.end()
            .column();

        this.rowSubMap = lastColumn.isLast() ?
            (left, right) ->
                left.row().isLast() ?
                    cells.tailMap(
                        left // last row and last column
                    ) :
                    cells.subMap(
                        left,
                        left.addRow(1)
                            .setColumn(
                                SpreadsheetReferenceKind.RELATIVE.firstColumn()
                            ) // to exclusive
                    ) :
            (left, right) ->
                cells.subMap(
                    left,
                    right.addColumn(1) // to exclusive
                );
    }

    @Override
    public boolean hasNext() {
        return null != this.nextCell();
    }

    @Override
    public SpreadsheetCell next() {
        final SpreadsheetCell cell = this.nextCell();
        this.nextCell = null;

        if (null == cell) {
            throw new NoSuchElementException();
        }
        return cell;
    }

    /**
     * The range of cells that will be returned by this {@link Iterator}.
     */
    private final SpreadsheetCellRangeReference range;

    /**
     * The first cell in the current row. This is advanced each time a new {@link #rowIterator} is created.
     */
    private SpreadsheetCellReference nextRowFirstColumn;

    /**
     * Lazily fetches the next cell if the field is not set.
     */
    private SpreadsheetCell nextCell() {
        if (null == this.nextCell) {
            Iterator<SpreadsheetCell> iterator = this.rowIterator();
            while (null != iterator) {
                if (iterator.hasNext()) {
                    this.nextCell = iterator.next();
                    break;
                }

                // need another row
                this.rowIterator = null;
                iterator = this.nextRowIterator();
            }
        }

        return this.nextCell;
    }

    /**
     * Cached copy of the next cell in the {@link #rowIterator()}. It will be cleared by {@link #next()} after being read.
     */
    private SpreadsheetCell nextCell;

    private Iterator<SpreadsheetCell> rowIterator() {
        if (null == this.rowIterator) {
            this.nextRowIterator();
        }
        return this.rowIterator;
    }

    private Iterator<SpreadsheetCell> nextRowIterator() {
        SpreadsheetCellReference nextRowFirstColumn = this.nextRowFirstColumn;

        if (null != nextRowFirstColumn) {
            final SpreadsheetCellReference bottomRight = this.range.end();
            final SpreadsheetCellReference lastCell = nextRowFirstColumn.setColumn(bottomRight.column());

            this.rowIterator = this.rowSubMap.apply(
                    nextRowFirstColumn,
                    lastCell
                ).values()
                .iterator();

            if (nextRowFirstColumn.row().isLast()) {
                nextRowFirstColumn = null;
            } else {
                if (lastCell.row().compareTo(bottomRight.row()) >= 0) {
                    nextRowFirstColumn = null;
                } else {
                    nextRowFirstColumn = nextRowFirstColumn.addRow(1);
                }
            }

            this.nextRowFirstColumn = nextRowFirstColumn;
        }

        return this.rowIterator;
    }

    /**
     * An {@link Iterator} for the current row.
     */
    private Iterator<SpreadsheetCell> rowIterator;

    /**
     * A Function that produces the to key parameter a ({@link SpreadsheetCellReference} for {@link SortedMap#subMap(Object, Object)}.
     */
    private final BiFunction<SpreadsheetCellReference, SpreadsheetCellReference, Map<SpreadsheetCellReference, SpreadsheetCell>> rowSubMap;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.range + " " + this.rowIterator;
    }
}

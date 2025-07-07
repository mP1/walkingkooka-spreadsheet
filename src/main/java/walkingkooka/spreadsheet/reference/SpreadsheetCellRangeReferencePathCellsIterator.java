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

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * An {@link Iterator} that maybe used to iterate over a {@link SpreadsheetCellRangeReference} in the order given by the
 * {@link SpreadsheetCellRangeReferencePath}.
 */
final class SpreadsheetCellRangeReferencePathCellsIterator implements Iterator<SpreadsheetCellReference> {

    static SpreadsheetCellRangeReferencePathCellsIterator with(final SpreadsheetCellRangeReference cells,
                                                               final SpreadsheetCellRangeReferencePath path) {
        Objects.requireNonNull(cells, "cells");

        return new SpreadsheetCellRangeReferencePathCellsIterator(
            cells,
            path
        );
    }

    private SpreadsheetCellRangeReferencePathCellsIterator(final SpreadsheetCellRangeReference cells,
                                                           final SpreadsheetCellRangeReferencePath path) {
        this.cells = cells;
        this.path = path;

        final SpreadsheetCellRangeReferencePathComparator comparator = this.path.comparator;
        this.origin = (comparator.reverseX == 1 ?
            cells.begin() :
            cells.end()
        ).column().setRow(
            (comparator.reverseY == 1 ?
                cells.begin() :
                cells.end()
            ).row()
        );

        this.x = 0;
        this.y = 0;
    }

    @Override
    public boolean hasNext() {
        final SpreadsheetCellRangeReference cells = this.cells;

        return this.path.comparator.xFirst ?
            this.x < cells.width() :
            this.y < cells.height();
    }

    @Override
    public SpreadsheetCellReference next() {
        if (false == this.hasNext()) {
            throw new NoSuchElementException();
        }

        final int x = this.x;
        final int y = this.y;

        final SpreadsheetCellRangeReference cells = this.cells;
        final SpreadsheetCellRangeReferencePathComparator comparator = this.path.comparator;

        if (comparator.xFirst) {
            if (y + 1 >= cells.height()) {
                this.y = 0;
                this.x++;
            } else {
                this.y = 1 + y;
            }
        } else {
            if (x + 1 >= cells.width()) {
                this.x = 0;
                this.y++;
            } else {
                this.x = 1 + x;
            }
        }

        return this.origin.add(
            x * comparator.reverseX,
            y * comparator.reverseY
        );
    }

    private final SpreadsheetCellRangeReference cells;

    private final SpreadsheetCellRangeReferencePath path;

    final SpreadsheetCellReference origin;

    // these coordinates are absolute or the matrix of the #cells
    private int x;

    private int y;

    @Override
    public String toString() {
        return this.cells + " " + this.path;
    }
}

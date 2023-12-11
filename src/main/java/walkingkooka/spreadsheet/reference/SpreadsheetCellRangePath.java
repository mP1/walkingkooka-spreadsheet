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

import java.util.Comparator;
import java.util.Iterator;

/**
 * A {@link Comparator} provider that may be used to sort {@link SpreadsheetCellReference} in a variety of arrangements.
 */
public enum SpreadsheetCellRangePath {

    /**
     * <pre>
     * 1 2 3
     * 4 5 6
     * 7 8 9
     * </pre>
     */
    LRTD(
            false, // xFirst
            1, // reverseX
            1 //reverseY,
    ),

    /**
     * <pre>
     * 3 2 1
     * 6 5 4
     * 9 8 7
     * </pre>
     */
    RLTD(
            false, // xFirst
            -1, // reverseX
            1//reverseY
    ),

    /**
     * <pre>
     * 7 8 9
     * 4 5 6
     * 1 2 3
     * </pre>
     */
    LRBU(
            false, // xFirst
            1, // reverseX
            -1 //reverseY
    ),

    /**
     * <pre>
     * 9 8 7
     * 6 5 4
     * 3 2 1
     * </pre>
     */
    RLBU(
            false, // xFirst
            -1, // reverseX
            -1 //reverseY
    ),

    /**
     * <pre>
     * 1 4 7
     * 2 5 8
     * 3 6 9
     * </pre>
     */
    TDLR(
            true, // xFirst
            1, // reverseX
            1 //reverseY
    ),

    /**
     * <pre>
     * 3 4 1
     * 8 5 2
     * 9 6 3
     * </pre>
     */
    TDRL(
            true, // xFirst
            -1, // reverseX
            1//reverseY
    ),

    /**
     * <pre>
     * 3 6 9
     * 2 5 8
     * 1 4 7
     * </pre>
     */
    BULR(
            true, // xFirst
            1, // reverseX
            -1 //reverseY
    ),

    /**
     * <pre>
     * 9 6 3
     * 8 5 2
     * 7 4 1
     * </pre>
     */
    BURL(
            true, // xFirst
            -1, // reverseX
            -1 //reverseY
    );

    SpreadsheetCellRangePath(final boolean xFirst,
                             final int reverseX,
                             final int reverseY) {
        this.comparator = SpreadsheetCellRangePathComparator.with(
                xFirst,
                reverseX,
                reverseY,
                this.name()
        );
    }

    public Comparator<SpreadsheetCellReference> comparator() {
        return this.comparator;
    }

    // SpreadsheetCellRangePathCellsIterator
    final SpreadsheetCellRangePathComparator comparator;

    public Iterator<SpreadsheetCellReference> cells(final SpreadsheetCellRange cells) {
        return SpreadsheetCellRangePathCellsIterator.with(
                cells,
                this
        );
    }
}

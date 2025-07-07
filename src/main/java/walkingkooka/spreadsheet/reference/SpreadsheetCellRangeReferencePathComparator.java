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

import walkingkooka.compare.Comparators;

import java.util.Comparator;

final class SpreadsheetCellRangeReferencePathComparator implements Comparator<SpreadsheetCellReference> {

    static SpreadsheetCellRangeReferencePathComparator with(final boolean xFirst,
                                                            final int reverseX,
                                                            final int reverseY,
                                                            final String toString) {
        return new SpreadsheetCellRangeReferencePathComparator(
            xFirst,
            reverseX,
            reverseY,
            toString
        );
    }

    private SpreadsheetCellRangeReferencePathComparator(final boolean xFirst,
                                                        final int reverseX,
                                                        final int reverseY,
                                                        final String toString) {
        this.xFirst = xFirst;
        this.reverseX = reverseX;
        this.reverseY = reverseY;
        this.toString = toString;
    }

    @Override
    public int compare(final SpreadsheetCellReference left,
                       final SpreadsheetCellReference right) {
        int result;

        if (this.xFirst) {
            result = left.column()
                .compareTo(
                    right.column()
                ) * this.reverseX;
            if (Comparators.EQUAL == result) {
                result = left.row()
                    .compareTo(
                        right.row()
                    ) * this.reverseY;
            }
        } else {
            result = left.row()
                .compareTo(
                    right.row()
                ) * this.reverseY;
            if (Comparators.EQUAL == result) {
                result = left.column()
                    .compareTo(
                        right.column()
                    ) * this.reverseX;
            }
        }

        return result;
    }

    // required by SpreadsheetCellRangeReferencePathCellsIterator

    final boolean xFirst;

    final int reverseX;

    final int reverseY;

    @Override
    public String toString() {
        return this.toString;
    }

    private final String toString;
}

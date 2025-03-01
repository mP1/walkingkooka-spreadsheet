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

/**
 * A {@link Comparator} that may be used to compare all types of {@link SpreadsheetSelection} ignoring their {@link SpreadsheetReferenceKind}.
 */
final class SpreadsheetSelectionIgnoresReferenceKindComparator implements Comparator<SpreadsheetSelection> {

    /**
     * Singleton
     */
    final static SpreadsheetSelectionIgnoresReferenceKindComparator INSTANCE = new SpreadsheetSelectionIgnoresReferenceKindComparator();

    /**
     * Private ctor use singleton
     */
    private SpreadsheetSelectionIgnoresReferenceKindComparator() {
        super();
    }

    @Override
    public int compare(final SpreadsheetSelection left,
                       final SpreadsheetSelection right) {
        final int leftPriority = left.spreadsheetSelectionIgnoresReferenceKindComparatorPriority();
        final int rightPriority = right.spreadsheetSelectionIgnoresReferenceKindComparatorPriority();
        int compare = leftPriority - rightPriority;

        if (Comparators.EQUAL == compare) {
            switch (leftPriority) {
                case COLUMN:
                    compare = compareColumnOrRows(
                            left.toColumn(),
                            right.toColumn()
                    );
                    break;
                case ROW:
                    compare = compareColumnOrRows(
                            left.toRow(),
                            right.toRow()
                    );
                    break;
                case COLUMN_RANGE:
                    compare = compareColumnOrRowRanges(
                            left.toColumnRange(),
                            right.toColumnRange()
                    );
                    break;
                case ROW_RANGE:
                    compare = compareColumnOrRowRanges(
                            left.toRowRange(),
                            right.toRowRange()
                    );
                    break;
                case CELL:
                    compare = compareCells(
                            left.toCell(),
                            right.toCell()
                    );
                    break;
                case CELL_RANGE:
                    compare = compareCellRanges(
                            left.toCellRange(),
                            right.toCellRange()
                    );
                    break;
                case LABEL:
                    compare = SpreadsheetLabelName.CASE_SENSITIVITY.comparator()
                            .compare(
                                    left.toLabelName()
                                            .value(),
                                    right.toLabelName()
                                            .value()
                            );
                    break;
            }
        }

        return compare;
    }

    private static int compareColumnOrRows(final SpreadsheetColumnOrRowReference left,
                                           final SpreadsheetColumnOrRowReference right) {
        return left.value - right.value;
    }

    private static int compareColumnOrRowRanges(final SpreadsheetColumnOrRowRangeReference<?> left,
                                                final SpreadsheetColumnOrRowRangeReference<?> right) {
        int compare = left.begin().value - right.begin().value;

        if (Comparators.EQUAL == compare) {
            compare = left.end().value - right.end().value;
        }

        return compare;
    }

    private static int compareCells(final SpreadsheetCellReference left,
                                    final SpreadsheetCellReference right) {
        int compare = left.column.value - right.column.value;

        if (Comparators.EQUAL == compare) {
            compare = left.row.value - right.row.value;
        }

        return compare;
    }

    private static int compareCellRanges(final SpreadsheetCellRangeReference left,
                                         final SpreadsheetCellRangeReference right) {
        int compare = compareCells(
                left.begin(),
                right.begin()
        );

        if (Comparators.EQUAL == compare) {
            compare = compareCells(
                    left.end(),
                    right.end()
            );
        }

        return compare;
    }

    final static int COLUMN = 1;
    final static int ROW = 2;

    final static int COLUMN_RANGE = 3;
    final static int ROW_RANGE = 4;

    final static int CELL = 5;
    final static int CELL_RANGE = 6;
    final static int LABEL = 7;

    @Override
    public String toString() {
        return Comparator.class.getSimpleName() +
                "(" +
                SpreadsheetSelection.class.getSimpleName() +
                " ignoring " +
                SpreadsheetReferenceKind.class.getSimpleName() +
                ")";
    }
}

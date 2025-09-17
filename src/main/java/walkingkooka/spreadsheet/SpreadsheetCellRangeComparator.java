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

import walkingkooka.ToStringBuilder;
import walkingkooka.compare.Comparators;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorContext;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetColumnOrRowSpreadsheetComparators;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * A {@link Comparator} that may be used to compare all sorted columns or rows within a large range being sorted.
 * After the sorted columns or rows are sorted, the remaining columns and rows will need to be moved around to match the
 * sorted columns/rows and then have their references fixed.If a cell is missing the list should contain a NULL<br>
 * Note the given lists for left and right must match the number of {@link SpreadsheetColumnOrRowSpreadsheetComparators}.
 */
public final class SpreadsheetCellRangeComparator implements Comparator<List<SpreadsheetCell>> {

    static SpreadsheetCellRangeComparator with(final List<SpreadsheetColumnOrRowSpreadsheetComparators> comparators,
                                               final SpreadsheetComparatorContext context) {
        return new SpreadsheetCellRangeComparator(
            SpreadsheetColumnOrRowSpreadsheetComparators.list(comparators),
            Objects.requireNonNull(context, "context")
        );
    }

    private SpreadsheetCellRangeComparator(final List<SpreadsheetColumnOrRowSpreadsheetComparators> comparators,
                                           final SpreadsheetComparatorContext context) {
        this.comparators = comparators;
        this.context = context;
    }

    // Comparator.......................................................................................................

    @Override
    public int compare(final List<SpreadsheetCell> left,
                       final List<SpreadsheetCell> right) {
        Objects.requireNonNull(left, "left");
        Objects.requireNonNull(right, "right");

        final List<SpreadsheetColumnOrRowSpreadsheetComparators> comparators = this.comparators;

        final SpreadsheetComparatorContext context = this.context;

        final int leftCount = left.size();
        final int rightCount = right.size();

        int result = Comparators.EQUAL;

        int i = 0;
        for (final SpreadsheetColumnOrRowSpreadsheetComparators comparator : comparators) {
            if (leftCount == i || rightCount == i) {
                break;
            }
            result = comparator.compare(
                left.get(i),
                right.get(i),
                context
            );
            if (Comparators.EQUAL != result) {
                break;
            }
            i++;
        }

        return result;
    }

    private final List<SpreadsheetColumnOrRowSpreadsheetComparators> comparators;

    // TODO should cache converted values key=value+target-type value=converted-value
    private final SpreadsheetComparatorContext context;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return ToStringBuilder.empty()
            .value(this.comparators)
            .value(this.context)
            .build();
    }
}

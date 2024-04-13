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

package walkingkooka.spreadsheet.compare;

import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.compare.Comparators;
import walkingkooka.spreadsheet.SpreadsheetCell;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * A {@link Comparator} that internally uses a {@link List} of {@Link SpreadsheetComparator} to compare any {@link SpreadsheetCell cells},
 * given to it.<br>
 * This comparator will be part of implementing a sort of a range of cells.
 */
final class SpreadsheetCellComparator implements Comparator<SpreadsheetCell> {

    static SpreadsheetCellComparator with(final List<SpreadsheetComparator<?>> spreadsheetComparators,
                                          final SpreadsheetComparatorMissingValues missingValues,
                                          final SpreadsheetComparatorContext context) {

        return new SpreadsheetCellComparator(
                Lists.immutable(
                        Objects.requireNonNull(spreadsheetComparators, "spreadsheetComparators")
                ),
                Objects.requireNonNull(
                        missingValues, "missingValues"
                ),
                Objects.requireNonNull(context, "context")
        );
    }

    private SpreadsheetCellComparator(final List<SpreadsheetComparator<?>> spreadsheetComparators,
                                      final SpreadsheetComparatorMissingValues missingValues,
                                      final SpreadsheetComparatorContext context) {
        this.spreadsheetComparators = spreadsheetComparators;
        this.missingValues = missingValues;
        this.context = context;
    }

    // Comparator.......................................................................................................

    @Override
    public int compare(final SpreadsheetCell left,
                       final SpreadsheetCell right) {
        int result = Comparators.EQUAL;

        final Object leftValue = left.formula()
                .value()
                .orElse(null);
        final Object rightValue = right.formula()
                .value()
                .orElse(null);

        final SpreadsheetComparatorContext context = this.context;

        // try one by one, until a non equal match.
        for (final SpreadsheetComparator<?> spreadsheetComparator : this.spreadsheetComparators) {
            final Class<?> type = spreadsheetComparator.type();

            Object convertedLeftValue = null;
            if (null != leftValue) {
                convertedLeftValue = context.convert(
                        leftValue,
                        type
                ).orElseLeft(null);
            }

            Object convertedRightValue = null;
            if (null != rightValue) {
                convertedRightValue = context.convert(
                        rightValue,
                        type
                ).orElseLeft(null);
            }

            final boolean missingLeft = null == convertedLeftValue;
            final boolean missingRight = null == convertedRightValue;
            if (missingLeft || missingRight) {
                result = missingLeft && missingRight ?
                        Comparators.EQUAL :
                        missingLeft ?
                                SpreadsheetComparatorMissingValues.BEFORE == this.missingValues ?
                                        Comparators.LESS :
                                        Comparators.MORE :
                                SpreadsheetComparatorMissingValues.BEFORE == this.missingValues ?
                                        Comparators.MORE :
                                        Comparators.LESS;
            } else {
                result = spreadsheetComparator.compare(
                        Cast.to(convertedLeftValue),
                        Cast.to(convertedRightValue)
                );
            }

            if (Comparators.EQUAL != result) {
                break;
            }
        }

        return result;
    }

    private final List<SpreadsheetComparator<?>> spreadsheetComparators;

    /**
     * Null or unconvertable values should appear before if this is true.
     */
    private final SpreadsheetComparatorMissingValues missingValues;

    // TODO should cache converted values key=value+target-type value=converted-value
    private final SpreadsheetComparatorContext context;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.spreadsheetComparators + " " +
                this.missingValues + " " +
                this.context;
    }
}

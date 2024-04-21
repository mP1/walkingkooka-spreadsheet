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
import walkingkooka.ToStringBuilder;
import walkingkooka.collect.list.Lists;
import walkingkooka.compare.Comparators;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnOrRowReference;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * For a single column or row holds a list of {@link SpreadsheetComparator}. Note that values that cannot be converted ond empty cells
 * will be appended to the sorted values.
 */
public final class SpreadsheetCellSpreadsheetComparators {

    /**
     * Creates a list of compatible {@link SpreadsheetCellSpreadsheetComparators}, which means all {@link SpreadsheetCellSpreadsheetComparators}
     * are the same {@link walkingkooka.spreadsheet.reference.SpreadsheetColumnOrRowReferenceKind}.
     */
    public static List<SpreadsheetCellSpreadsheetComparators> list(final List<SpreadsheetCellSpreadsheetComparators> list) {
        return SpreadsheetCellSpreadsheetComparatorsList.with(list);
    }

    /**
     * Parses the text into a {@link List} of {@link SpreadsheetCellSpreadsheetComparators} using the function
     * as a factory to transform spreadsheet comparator names into {@link SpreadsheetComparator} instances.
     */
    public static List<SpreadsheetCellSpreadsheetComparators> parse(final String text,
                                                                    final SpreadsheetComparatorProvider spreadsheetComparatorProvider) {
        return list(
                SpreadsheetCellSpreadsheetComparatorNames.parseList(text)
                        .stream()
                        .map(n -> SpreadsheetCellSpreadsheetComparators.with(
                                n.columnOrRow(),
                                n.comparatorNameAndDirections()
                                        .stream()
                                        .map(nad -> nad.direction()
                                                .apply(
                                                        spreadsheetComparatorProvider.spreadsheetComparator(nad.name())
                                                )
                                        ).collect(Collectors.toList())
                        )).collect(Collectors.toList())
        );
    }

    public static SpreadsheetCellSpreadsheetComparators with(final SpreadsheetColumnOrRowReference columnOrRow,
                                                             final List<SpreadsheetComparator<?>> comparators) {

        return new SpreadsheetCellSpreadsheetComparators(
                Objects.requireNonNull(columnOrRow, "columnOrRows"),
                Lists.immutable(
                        Objects.requireNonNull(comparators, "comparators")
                )
        );
    }

    private SpreadsheetCellSpreadsheetComparators(final SpreadsheetColumnOrRowReference columnOrRow,
                                                  final List<SpreadsheetComparator<?>> comparators) {
        if (comparators.isEmpty()) {
            throw new IllegalArgumentException("Expected at least 1 comparator got none");
        }

        this.columnOrRow = columnOrRow;
        this.comparators = comparators;
    }

    public int compare(final SpreadsheetCell left,
                       final SpreadsheetCell right,
                       final SpreadsheetComparatorContext context) {
        int result = Comparators.EQUAL;

        final Object leftValue = valueOf(left);
        final Object rightValue = valueOf(right);

        // try one by one, until a non equal match.
        for (final SpreadsheetComparator<?> comparator : this.comparators) {
            final Class<?> type = comparator.type();

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
                                        Comparators.MORE :
                                Comparators.LESS; // missing | nulls etc come AFTER
            } else {
                result = comparator.compare(
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

    private static Object valueOf(final SpreadsheetCell cell) {
        return null == cell ?
                null :
                cell.formula().
                        value()
                        .orElse(null);
    }

    public SpreadsheetColumnOrRowReference columnOrRow() {
        return this.columnOrRow;
    }

    private final SpreadsheetColumnOrRowReference columnOrRow;

    public List<SpreadsheetComparator<?>> comparators() {
        return this.comparators;
    }

    private final List<SpreadsheetComparator<?>> comparators;

    /**
     * Converts this back to its simple form.
     */
    public SpreadsheetCellSpreadsheetComparatorNames toSpreadsheetCellSpreadsheetComparatorNames() {
        return SpreadsheetCellSpreadsheetComparatorNames.with(
                this.columnOrRow,
                this.comparators.stream()
                        .map(c -> c.name().setDirection(c.direction()))
                        .collect(Collectors.toList())
        );
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
                this.columnOrRow,
                this.comparators
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetCellSpreadsheetComparators && this.equals0((SpreadsheetCellSpreadsheetComparators) other);
    }

    private boolean equals0(final SpreadsheetCellSpreadsheetComparators other) {
        return this.columnOrRow.equals(other.columnOrRow) &&
                this.comparators.equals(other.comparators);
    }

    @Override
    public String toString() {
        return ToStringBuilder.empty()
                .value(this.columnOrRow)
                .value(this.comparators)
                .build();
    }
}

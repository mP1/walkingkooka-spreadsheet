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

package walkingkooka.spreadsheet.compare.provider;

import walkingkooka.Cast;
import walkingkooka.ToStringBuilder;
import walkingkooka.collect.list.Lists;
import walkingkooka.compare.Comparators;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.compare.SpreadsheetComparator;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorContext;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnOrRowReferenceOrRange;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * For a single column or row holds a list of {@link SpreadsheetComparator}. Note that values that cannot be converted ond empty cells
 * will be appended to the sorted values.
 */
public final class SpreadsheetColumnOrRowSpreadsheetComparators {

    /**
     * Creates a list of compatible {@link SpreadsheetColumnOrRowSpreadsheetComparators}, which means all {@link SpreadsheetColumnOrRowSpreadsheetComparators}
     * are for either {@link walkingkooka.spreadsheet.reference.SpreadsheetColumnReference} or {@link walkingkooka.spreadsheet.reference.SpreadsheetRowReference}.
     */
    public static List<SpreadsheetColumnOrRowSpreadsheetComparators> list(final List<SpreadsheetColumnOrRowSpreadsheetComparators> list) {
        return SpreadsheetColumnOrRowSpreadsheetComparatorsList.with(list);
    }

    /**
     * Parses the text into a {@link List} of {@link SpreadsheetColumnOrRowSpreadsheetComparators} using the function
     * as a factory to transform spreadsheet comparator names into {@link SpreadsheetComparator} instances.
     */
    public static List<SpreadsheetColumnOrRowSpreadsheetComparators> parse(final String text,
                                                                           final SpreadsheetComparatorProvider provider,
                                                                           final ProviderContext context) {
        return list(
            SpreadsheetColumnOrRowSpreadsheetComparatorNames.parseList(text)
                .stream()
                .map(n -> SpreadsheetColumnOrRowSpreadsheetComparators.with(
                    n.columnOrRow(),
                    n.comparatorNameAndDirections()
                        .stream()
                        .map(nad -> nad.direction()
                            .apply(
                                Cast.to(
                                    provider.spreadsheetComparator(
                                        nad.name(),
                                        Lists.empty(),
                                        context
                                    )
                                )
                            )
                        ).collect(Collectors.toList())
                )).collect(Collectors.toList())
        );
    }

    public static SpreadsheetColumnOrRowSpreadsheetComparators with(final SpreadsheetColumnOrRowReferenceOrRange columnOrRow,
                                                                    final List<SpreadsheetComparator<?>> comparators) {

        return new SpreadsheetColumnOrRowSpreadsheetComparators(
            Objects.requireNonNull(columnOrRow, "columnOrRows"),
            Lists.immutable(
                Objects.requireNonNull(comparators, "comparators")
            )
        );
    }

    private SpreadsheetColumnOrRowSpreadsheetComparators(final SpreadsheetColumnOrRowReferenceOrRange columnOrRow,
                                                         final List<SpreadsheetComparator<?>> comparators) {
        if (comparators.isEmpty()) {
            throw new IllegalArgumentException("Empty comparators");
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
                errorOrValue()
                .orElse(null);
    }

    public SpreadsheetColumnOrRowReferenceOrRange columnOrRow() {
        return this.columnOrRow;
    }

    private final SpreadsheetColumnOrRowReferenceOrRange columnOrRow;

    public List<SpreadsheetComparator<?>> comparators() {
        return this.comparators;
    }

    private final List<SpreadsheetComparator<?>> comparators;

    /**
     * Converts this back to its simple form.
     */
    public SpreadsheetColumnOrRowSpreadsheetComparatorNames toSpreadsheetColumnOrRowSpreadsheetComparatorNames() {
        return SpreadsheetColumnOrRowSpreadsheetComparatorNames.with(
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
            other instanceof SpreadsheetColumnOrRowSpreadsheetComparators && this.equals0((SpreadsheetColumnOrRowSpreadsheetComparators) other);
    }

    private boolean equals0(final SpreadsheetColumnOrRowSpreadsheetComparators other) {
        return this.columnOrRow.equals(other.columnOrRow) &&
            this.comparators.equals(other.comparators);
    }

    @Override
    public String toString() {
        return ToStringBuilder.empty()
            .labelSeparator(String.valueOf(SpreadsheetColumnOrRowSpreadsheetComparatorNames.COLUMN_ROW_AND_COMPARATOR_NAME_SEPARATOR))
            .valueSeparator(String.valueOf(SpreadsheetColumnOrRowSpreadsheetComparatorNames.COMPARATOR_NAME_SEPARATOR))
            .label(this.columnOrRow.text())
            .value(this.comparators)
            .build();
    }
}

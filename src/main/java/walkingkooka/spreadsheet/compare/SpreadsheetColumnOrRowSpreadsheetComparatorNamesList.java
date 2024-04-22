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

import walkingkooka.collect.list.Lists;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnOrRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnOrRowReferenceKind;

import java.util.AbstractList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * {@link List} that holds multiple {@link SpreadsheetColumnOrRowSpreadsheetComparatorNames} typically one for each sortable
 * column with a range sort.
 */
final class SpreadsheetColumnOrRowSpreadsheetComparatorNamesList extends AbstractList<SpreadsheetColumnOrRowSpreadsheetComparatorNames> {

    static {
        Lists.registerImmutableType(SpreadsheetColumnOrRowSpreadsheetComparatorNamesList.class);
    }

    static SpreadsheetColumnOrRowSpreadsheetComparatorNamesList with(final List<SpreadsheetColumnOrRowSpreadsheetComparatorNames> comparatorNames) {
        Objects.requireNonNull(comparatorNames, "comparatorNames");

        return comparatorNames instanceof SpreadsheetColumnOrRowSpreadsheetComparatorNamesList ?
                (SpreadsheetColumnOrRowSpreadsheetComparatorNamesList) comparatorNames :
                copyAndCreate(comparatorNames);
    }

    static SpreadsheetColumnOrRowSpreadsheetComparatorNamesList copyAndCreate(final List<SpreadsheetColumnOrRowSpreadsheetComparatorNames> comparatorNames) {
        final SpreadsheetColumnOrRowSpreadsheetComparatorNames[] copy = comparatorNames.toArray(
                new SpreadsheetColumnOrRowSpreadsheetComparatorNames[comparatorNames.size()]
        );

        if (copy.length == 0) {
            throw new IllegalArgumentException("Expected several sorted column/rows got 0");
        }

        SpreadsheetColumnOrRowReferenceKind first = null;
        int i = 0;

        for (final SpreadsheetColumnOrRowSpreadsheetComparatorNames columnOrRowComparators : comparatorNames) {
            final SpreadsheetColumnOrRowReference columnOrRow = columnOrRowComparators.columnOrRow();
            if (null == first) {
                first = columnOrRow.columnOrRowReferenceKind();
            } else {
                if (first != columnOrRow.columnOrRowReferenceKind()) {
                    throw new IllegalArgumentException(
                            "All sorted columns/rows must be " +
                                    first +
                                    " but " +
                                    i +
                                    " is " +
                                    first.flip()
                    );
                }
            }

            i++;
        }

        return new SpreadsheetColumnOrRowSpreadsheetComparatorNamesList(copy);
    }

    private SpreadsheetColumnOrRowSpreadsheetComparatorNamesList(final SpreadsheetColumnOrRowSpreadsheetComparatorNames[] comparators) {
        this.comparators = comparators;
    }

    @Override
    public SpreadsheetColumnOrRowSpreadsheetComparatorNames get(final int index) {
        return this.comparators[index];
    }

    @Override
    public int size() {
        return this.comparators.length;
    }

    String parseString() {
        return this.stream()
                .map(Object::toString)
                .collect(Collectors.joining("" + SpreadsheetColumnOrRowSpreadsheetComparatorNames.COLUMN_ROW_SEPARATOR));
    }

    private final SpreadsheetColumnOrRowSpreadsheetComparatorNames[] comparators;
}

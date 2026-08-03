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

import walkingkooka.compare.Comparators;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorName;
import walkingkooka.spreadsheet.value.SpreadsheetCell;

import java.util.Objects;
import java.util.Optional;

abstract class SpreadsheetComparatorShared<T> implements SpreadsheetComparator<T> {

    SpreadsheetComparatorShared(final SpreadsheetComparatorName name) {
        super();

        this.name = name;
    }

    @Override
    public final Optional<T> extractValue(final SpreadsheetCell cell,
                                          final SpreadsheetComparatorContext context) {
        Objects.requireNonNull(context, "context");

        return null != cell ?
            this.extractValueNonNull(cell, context) :
            Optional.empty();
    }

    abstract Optional<T> extractValueNonNull(final SpreadsheetCell cell,
                                             final SpreadsheetComparatorContext context);

    // Comparator.......................................................................................................

    @Override
    public final int compare(final T left,
                             final T right) {
        final boolean leftIsNull = null == left;
        final boolean rightIsNull = null == right;
        return leftIsNull && rightIsNull ?
            Comparators.EQUAL :
            leftIsNull ? Comparators.MORE :
                rightIsNull ? - Comparators.LESS :
                    this.compareNonNull(
                        left,
                        right
                    );
    }

    abstract int compareNonNull(final T left,
                                final T right);

    // HasName..........................................................................................................

    @Override
    public final SpreadsheetComparatorName name() {
        return name;
    }

    private final SpreadsheetComparatorName name;
}

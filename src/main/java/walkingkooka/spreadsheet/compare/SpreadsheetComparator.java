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

import walkingkooka.naming.HasName;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorName;

import java.util.Comparator;

/**
 * A {@link Comparator} that includes a type property. Sorting cells is a two phase operation, first all cell values are
 * converted to the {@link SpreadsheetComparator#type()} and then converted cell values are sorted using this {@link Comparator}.
 * Cells that could not be converted will be placed before or after the sorted list of cells.
 */
public interface SpreadsheetComparator<T> extends Comparator<T>,
    HasName<SpreadsheetComparatorName> {

    /**
     * The type handled by this {@link Comparator}. This is used to convert the left/right values before calling {@link Comparator#compare(Object, Object)}.
     */
    Class<T> type();

    /**
     * Returns a reversed {@link SpreadsheetComparator}
     */
    @Override
    default SpreadsheetComparator<T> reversed() {
        return SpreadsheetComparators.reverse(this);
    }
}

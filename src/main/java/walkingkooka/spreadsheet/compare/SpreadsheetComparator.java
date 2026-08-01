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
import walkingkooka.spreadsheet.value.SpreadsheetCell;

import java.util.Comparator;
import java.util.Optional;

/**
 * A {@link Comparator} that sorts value from a {@link SpreadsheetCell}
 */
public interface SpreadsheetComparator<T> extends Comparator<T>,
    HasName<SpreadsheetComparatorName> {

    /**
     * Extract a value from the given {@link SpreadsheetCell}.
     */
    Optional<T> extractValue(final SpreadsheetCell cell,
                             final SpreadsheetComparatorContext context);

    /**
     * Returns a reversed {@link SpreadsheetComparator}
     */
    @Override
    default SpreadsheetComparator<T> reversed() {
        return SpreadsheetComparators.reverse(this);
    }
}

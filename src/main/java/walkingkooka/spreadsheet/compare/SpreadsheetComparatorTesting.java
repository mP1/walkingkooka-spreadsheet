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

import walkingkooka.compare.ComparatorTesting;
import walkingkooka.naming.HasNameTesting;
import walkingkooka.spreadsheet.value.SpreadsheetCell;

import java.util.Optional;

public interface SpreadsheetComparatorTesting extends ComparatorTesting,
    HasNameTesting {


    default <T> void extractValueAndCheck(final SpreadsheetComparator<T> comparator,
                                          final SpreadsheetCell cell,
                                          final SpreadsheetComparatorContext context) {
        this.extractValueAndCheck(
            comparator,
            cell,
            context,
            Optional.empty()
        );
    }

    default <T> void extractValueAndCheck(final SpreadsheetComparator<T> comparator,
                                          final SpreadsheetCell cell,
                                          final SpreadsheetComparatorContext context,
                                          final T expected) {
        this.extractValueAndCheck(
            comparator,
            cell,
            context,
            Optional.of(expected)
        );
    }

    default <T> void extractValueAndCheck(final SpreadsheetComparator<T> comparator,
                                          final SpreadsheetCell cell,
                                          final SpreadsheetComparatorContext context,
                                          final Optional<T> expected) {
        this.checkEquals(
            expected,
            comparator.extractValue(
                cell,
                context
            )
        );
    }
}

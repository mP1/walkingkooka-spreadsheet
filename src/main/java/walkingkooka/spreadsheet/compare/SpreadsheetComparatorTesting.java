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

import walkingkooka.compare.ComparatorTesting2;

public interface SpreadsheetComparatorTesting<C extends SpreadsheetComparator<T>, T> extends ComparatorTesting2<C, T> {

    default <TT> void typeAndCheck(final SpreadsheetComparator<TT> comparator,
                                   final Class<TT> expected) {
        this.checkEquals(
                expected,
                comparator.type(),
                () -> comparator.toString()
        );
    }

    default void directionAndCheck(final SpreadsheetComparator<?> comparator,
                                   final SpreadsheetComparatorDirection direction) {
        this.checkEquals(
                direction,
                comparator.direction(),
                () -> "direction of " + comparator
        );
    }
}

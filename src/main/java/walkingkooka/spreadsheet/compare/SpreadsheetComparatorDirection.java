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
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorNameAndDirection;

import java.util.Comparator;
import java.util.Objects;

public enum SpreadsheetComparatorDirection {

    DEFAULT {
        @Override
        public <T> SpreadsheetComparator<T> apply(final SpreadsheetComparator<T> comparator) {
            return UP.apply(comparator);
        }

        @Override
        public SpreadsheetComparatorDirection flip() {
            return UP.flip();
        }

        @Override
        public int fixCompareResult(final int value) {
            return UP.fixCompareResult(value);
        }

        @Override
        public String toStringWithEmptyDefault() {
            return "";
        }
    },

    UP {
        @Override
        public <T> SpreadsheetComparator<T> apply(final SpreadsheetComparator<T> comparator) {
            return Objects.requireNonNull(comparator);
        }

        @Override
        public SpreadsheetComparatorDirection flip() {
            return DOWN;
        }

        @Override
        public int fixCompareResult(final int value) {
            return value;
        }

        @Override
        public String toStringWithEmptyDefault() {
            return SpreadsheetComparatorNameAndDirection.SEPARATOR + this.name();
        }
    },

    DOWN {
        @Override
        public <T> SpreadsheetComparator<T> apply(final SpreadsheetComparator<T> comparator) {
            return SpreadsheetComparators.reverse(comparator);
        }

        @Override
        public SpreadsheetComparatorDirection flip() {
            return UP;
        }

        @Override
        public int fixCompareResult(final int value) {
            return -Comparators.normalize(value);
        }

        @Override
        public String toStringWithEmptyDefault() {
            return SpreadsheetComparatorNameAndDirection.SEPARATOR + this.name();
        }
    };

    abstract public <T> SpreadsheetComparator<T> apply(final SpreadsheetComparator<T> comparator);


    /**
     * Turns a UP to DOWN and vice versa.
     */
    abstract public SpreadsheetComparatorDirection flip();

    /**
     * Flips the sign of a {@link Comparator#compare(Object, Object)} when this is DOWN.
     */
    abstract public int fixCompareResult(final int value);

    abstract public String toStringWithEmptyDefault();
}

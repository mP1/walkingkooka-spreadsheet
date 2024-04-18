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

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A function that maps {@link SpreadsheetComparatorName} to {@link SpreadsheetComparator}.
 */
final class NameToSpreadsheetComparatorFunction implements Function<SpreadsheetComparatorName, SpreadsheetComparator<?>> {

    final static NameToSpreadsheetComparatorFunction INSTANCE = new NameToSpreadsheetComparatorFunction();

    private NameToSpreadsheetComparatorFunction() {
        super();
    }

    @Override
    public SpreadsheetComparator<?> apply(final SpreadsheetComparatorName name) {
        Objects.requireNonNull(name, "name");

        final SpreadsheetComparator<?> comparator = nameToComparator.get(name);
        if (null == comparator) {
            throw new IllegalArgumentException("Unknown SpreadsheetComparator " + name);
        }

        return comparator;
    }

    private final Map<SpreadsheetComparatorName, SpreadsheetComparator<?>> nameToComparator = Lists.of(
                    SpreadsheetComparators.date(),
                    SpreadsheetComparators.dateTime(),
                    SpreadsheetComparators.dayOfMonth(),
                    SpreadsheetComparators.expressionNumber(),
                    SpreadsheetComparators.hourOfAmPm(),
                    SpreadsheetComparators.hourOfDay(),
                    SpreadsheetComparators.minuteOfHour(),
                    SpreadsheetComparators.monthOfYear(),
                    SpreadsheetComparators.nanoOfSecond(),
                    SpreadsheetComparators.secondsOfMinute(),
                    SpreadsheetComparators.string(),
                    SpreadsheetComparators.stringCaseInsensitive(),
                    SpreadsheetComparators.time(),
                    SpreadsheetComparators.year()
            ).stream()
            .collect(Collectors.toMap(n -> n.name(), n -> n));

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
